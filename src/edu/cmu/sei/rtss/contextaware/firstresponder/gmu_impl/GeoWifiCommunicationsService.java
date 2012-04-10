package edu.cmu.sei.rtss.contextaware.firstresponder.gmu_impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import edu.cmu.sei.rtss.contextaware.firstresponder.CommunicationsManagerCallback;
import edu.cmu.sei.rtss.contextaware.firstresponder.CommunicationsService;
import edu.gmu.hodum.service_client.SendReceiveActivity;
import edu.gmu.hodum.service_client.StartNetworkActivity;
import edu.gmu.hodum.service_client.receiver.MyNetworkInitializedReceiver;
import edu.gmu.hodum.service_client.receiver.MyReceivedDataReceiver;
import edu.gmu.hodum.service_client.util.Constants;
import edu.gmu.hodum.service_client.util.Quad;

public class GeoWifiCommunicationsService implements CommunicationsService {

	private MyNetworkInitializedReceiver receiver;
	private MyReceivedDataReceiver dataReceiver;
	private Context ctxt;
	private static final int NETWORK_STARTED = -1;
	private int state = Constants.DISABLED;
	private long packetID = 0;
	private String ipAddress;
	//Quad params:time,packetID, ipAddress/externalUser, packetType
	private LinkedList<Quad <Long, Long, String, Integer> > unAckedPackets = new LinkedList<Quad<Long, Long, String, Integer>>();
	private TimeoutThread timeoutThread;
	private ExternalUserStateManager externalUserState = new ExternalUserStateManager();
	private static GeoWifiCommunicationsService instance = null;

	public static GeoWifiCommunicationsService getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new GeoWifiCommunicationsService(context);
		}
		return instance;
	}

	private  GeoWifiCommunicationsService(Context context)
	{
		this.ctxt = context;
	}

	private CommunicationsManagerCallback callbackObj;
	@Override
	public void initialize(CommunicationsManagerCallback callbackObject) {
		receiver = new MyNetworkInitializedReceiver();
		receiver.registerHandler(networkStartedHandler);
		IntentFilter filter = new IntentFilter(Constants.NETWORK_INITIALIZED);

		ctxt.registerReceiver(receiver,filter);
		Intent broadcastIntent = new Intent(Constants.INITIALIZE_NETWORK);
		broadcastIntent.putExtra("channel", "8");
		ctxt.sendBroadcast(broadcastIntent);
		callbackObj = callbackObject;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return state;
	}

	@Override
	public void start() {
		//Register Broadcast receivers for the incoming messages
		//Set state to ACTIVE
		dataReceiver = new MyReceivedDataReceiver();
		dataReceiver.registerHandler(dataReceivedHandler);
		IntentFilter filter1 = new IntentFilter(Constants.RECEIVE_DATA);
		ctxt.registerReceiver(dataReceiver,filter1);
		state = Constants.ACTIVE;
		if(timeoutThread !=null)
		{
			timeoutThread.shuttingDown = true;
			timeoutThread = null;
		}
		timeoutThread = new TimeoutThread();
		timeoutThread.start();
		sendQuePasa();
	}

	private void sendQuePasa()
	{
		packetID++;

		byte[] out = ("What's up?").getBytes();
		byte[] nm = ipAddress.getBytes();

		//Allocate buffer large enough to hold the packetType + ipAddress, its length and the packetID
		ByteBuffer b = ByteBuffer.allocate(8 + 8 + nm.length + 8 + 1 + out.length);
		b.putLong(Constants.QUE_PASA);
		b.putLong(packetID);
		b.putLong(nm.length);
		b.put(nm);

		b.put(out);
		byte[] buff = b.array();
		sendBroadcast(buff);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		timeoutThread.shuttingDown = true;
		ctxt.unregisterReceiver(dataReceiver);
	}

	@Override
	public void connect(String externalUserID) {
		packetID++;

		byte[] nm = ipAddress.getBytes();
		byte[] out = ("Connect").getBytes();
		//Allocate buffer large enough to hold the packetType + ipAddress, its length and the packetID
		ByteBuffer b = ByteBuffer.allocate(8 + 8 + 8 + nm.length + out.length);
		b.putLong(Constants.CONNECT_MESSAGE);
		b.putLong(packetID);
		b.putLong(nm.length);
		b.put(nm);

		b.put(out);
		byte[] buff = b.array();

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent(Constants.SEND_DATA);
		broadcastIntent.putExtra("latitude", 360.0d);
		broadcastIntent.putExtra("longitude", 360.0d);
		broadcastIntent.putExtra("radius",-1.0d);
		broadcastIntent.putExtra("data",buff);

		Quad <Long, Long, String, Integer> unAcked = 
			new Quad <Long, Long, String, Integer>((System.currentTimeMillis()+10000),packetID,externalUserID,
					Constants.CONNECT_MESSAGE);
		addUnAckedPacket(unAcked);

		ctxt.sendBroadcast(broadcastIntent);

	}

	@Override
	public void disconnect(String externalUserID) {
		// TODO Auto-generated method stub
		this.removeUnAckedPacketsForDisconnectedNode(externalUserID);

	}

	@Override
	public void sendDataToAll(byte[] out) {
		packetID++;

		byte[] nm = ipAddress.getBytes();

		//Allocate buffer large enough to hold the packetType + ipAddress, its length and the packetID
		ByteBuffer b = ByteBuffer.allocate(8 + 8 + nm.length + 8 + 1 + out.length);
		b.putLong(Constants.SEND_TO_ALL);
		b.putLong(packetID);
		b.putLong(nm.length);
		b.put(nm);

		b.put(out);
		byte[] buff = b.array();
		sendBroadcast(buff);
	}

	private void sendBroadcast(byte[] buff)
	{
		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent(Constants.SEND_DATA);
		broadcastIntent.putExtra("latitude", 360.0d);
		broadcastIntent.putExtra("longitude", 360.0d);
		broadcastIntent.putExtra("radius",-1.0d);
		broadcastIntent.putExtra("data",buff);
		ctxt.sendBroadcast(broadcastIntent);
	}

	@Override
	public void sendData(String externalUserID, byte[] out) {
		packetID++;

		byte[] nm = ipAddress.getBytes();
		byte[] externalAddress = externalUserID.getBytes();

		//Allocate buffer large enough to hold the packetType , packetID, ipAddressLength, 
		//ipAddress + externalAddress length + externalAddress + data
		ByteBuffer b = ByteBuffer.allocate(8 + 8 + 8 + nm.length +8 + externalAddress.length + out.length);
		b.putLong(Constants.SEND_TO_SPECIFIC);
		b.putLong(packetID);
		b.putLong(nm.length);
		b.put(nm);
		b.putLong(externalAddress.length);
		b.put(externalAddress);
		b.put(out);
		byte[] buff = b.array();

		//Quad params:time,packetID, ipAddress/externalUser, packetType
		Quad <Long, Long, String, Integer> unAcked = 
			new Quad <Long, Long, String, Integer>((System.currentTimeMillis()+10000),packetID,externalUserID,
					Constants.SEND_TO_SPECIFIC);
		addUnAckedPacket(unAcked);

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent(Constants.SEND_DATA);
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		ctxt.sendBroadcast(broadcastIntent);

	}

	private Handler networkStartedHandler = new Handler() { 
		/* (non-Javadoc) 
		 * @see android.os.Handler#handleMessage(android.os.Message) 
		 */ 
		@Override 
		public void handleMessage(Message msg) { 
			switch(msg.arg1)
			{
			case NETWORK_STARTED:
				GeoWifiCommunicationsService.this.ctxt.unregisterReceiver(receiver);
				state = Constants.ENABLED;
				GeoWifiCommunicationsService.this.ipAddress = msg.getData().getString("ipAddress");
				Toast.makeText(ctxt, "Initialized", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg); 

		}
	};

	private Handler dataReceivedHandler = new Handler() { 
		/* (non-Javadoc) 
		 * @see android.os.Handler#handleMessage(android.os.Message) 
		 */ 
		@Override 
		public void handleMessage(Message msg) { 
			Bundle bundle = msg.getData();
			//			Location center = new Location("Other");
			//			center.setLatitude(bundle.getDouble("latitude"));
			//			center.setLongitude(bundle.getDouble("longitude"));
			//			double radius = bundle.getDouble("radius");
			byte[] data = bundle.getByteArray("data");
			//			Location originatingLocation = new Location("Other");
			//			originatingLocation.setLatitude(bundle.getDouble("originatingLatitude"));
			//			originatingLocation.setLongitude(bundle.getDouble("originatingLongitude"));
			//			
			//	SendReceiveActivity.this.receiveMessage(center, radius, originatingLocation, data);
			GeoWifiCommunicationsService.this.processPacket(data);
			super.handleMessage(msg); 

		}
	};

	private void processPacket(byte[] data)
	{
		ByteBuffer b = ByteBuffer.wrap(data);

		long packetType = b.getLong();
		long receivedPacketID = b.getLong();
		long ipAddressLength = b.getLong();
		byte[] src = new byte[(int)ipAddressLength];
		b.get(src, 0, (int)ipAddressLength);
		String srce = new String(src);

		if(packetType == Constants.ACK_MESSAGE)
		{
			long originalPacketID = b.getLong();
			long originalIpAddressLength = b.getLong();
			byte[] ipAdressBytes = new byte[(int)originalIpAddressLength];
			b.get(ipAdressBytes, 0, (int)ipAddressLength);
			String originalIPAddress = new String(ipAdressBytes); 
			//Check if it is an ack from one of our messages
			if(originalIPAddress.equalsIgnoreCase(this.ipAddress))
			{
				//Fetch the type of message
				Quad<Long,Long,String,Integer> ackedMessageData = ackedPacket(originalPacketID);
				//if it is a connect, report connection enabled and remove from unAcked
				if(ackedMessageData != null && ackedMessageData.getFourth().equals(Constants.CONNECT_MESSAGE))
				{
					int lastValue = externalUserState.newUser(srce) ;
					if(lastValue != ExternalUserStateManager.CONNECTED)
					{
						callbackObj.reportConnectionEnabled(srce);
					}
				}
				//if it was a send, simply remove packet from queue of Unacked
				//and re-enable comms if they are disconnected
				else if(externalUserState.newUser(srce) == ExternalUserStateManager.DISCONNECTED)
				{
					callbackObj.reportConnectionEnabled(srce);
				}
			}

		}
		else if(packetType == Constants.QUE_PASA_RESPONSE)
		{
			int previousState = externalUserState.newUser(srce);
			if(previousState == ExternalUserStateManager.NEVER_DISCOVERED)
			{
				callbackObj.reportNewUser(srce, null);
			}
		}
		else if(packetType == Constants.SEND_TO_SPECIFIC )
		{
			int previousState = externalUserState.newUser(srce);
			long destinationLength = b.getLong();
			byte[] destination = new byte[(int)destinationLength];
			b.get(destination, 0, (int)destinationLength);
			String destinationAddress = new String(destination);
			if(previousState == ExternalUserStateManager.NEVER_DISCOVERED)
			{
				callbackObj.reportNewUser(srce, null);
				if(destinationAddress.equals(this.ipAddress))
				{
					callbackObj.reportConnectionEnabled(srce);
				}
			}
			else if(previousState == ExternalUserStateManager.DISCONNECTED)
			{
				callbackObj.reportConnectionEnabled(srce);
			}


			if(destinationAddress.equals(this.ipAddress))
			{
				byte[] payload = new byte[b.remaining()];
				b.get(payload);
				callbackObj.reportMessageReceived(srce, payload, payload.length);
				sendAck(receivedPacketID,srce);
			}
		}
		else if (packetType == Constants.SEND_TO_ALL)
		{
			int previousState = externalUserState.newUser(srce);

			if(previousState == ExternalUserStateManager.NEVER_DISCOVERED)
			{
				callbackObj.reportNewUser(srce, null);
				callbackObj.reportConnectionEnabled(srce);
			}
			else if(previousState == ExternalUserStateManager.DISCONNECTED)
			{
				callbackObj.reportConnectionEnabled(srce);
			}

			byte[] payload = new byte[b.remaining()];
			b.get(payload);			
			callbackObj.reportMessageReceived(srce, payload, payload.length);
			sendAck(receivedPacketID,srce);
		}
		else if (packetType == Constants.QUE_PASA)
		{
			int previousState = externalUserState.newUser(srce);
			if(previousState == ExternalUserStateManager.NEVER_DISCOVERED)
			{
				callbackObj.reportNewUser(srce, null);
			}
			sendQuePasaAck(receivedPacketID,srce);
		}
		else //packetType == connect
		{
			int previousState = externalUserState.newUser(srce);
			if(previousState == ExternalUserStateManager.NEVER_DISCOVERED)
			{
				callbackObj.reportNewUser(srce, null);
				callbackObj.reportConnectionEnabled(srce);
			}
			else if(previousState == ExternalUserStateManager.DISCONNECTED)
			{
				callbackObj.reportConnectionEnabled(srce);
			}
			sendAck(receivedPacketID,srce);
		}
	}

	private void sendAck(Long originalPacketID,String originalipAddress)
	{
		packetID++;

		byte[] nm = ipAddress.getBytes();
		byte[] originalIpAddressBytes = originalipAddress.getBytes();
		byte[] out = ( "Got it").getBytes(); 
		//Allocate buffer large enough to hold the packetType + ipAddress, its length and the packetID
		ByteBuffer b = ByteBuffer.allocate(8 + 8 + 8 + nm.length + 8 + 8 + originalIpAddressBytes.length + out.length);
		b.putLong(Constants.ACK_MESSAGE);
		b.putLong(packetID);
		b.putLong(nm.length);
		b.put(nm);
		b.putLong(originalPacketID);
		b.putLong(originalIpAddressBytes.length);
		b.put(originalIpAddressBytes);
		b.put(out);
		byte[] buff = b.array();

		sendBroadcast(buff);
	}

	private void sendQuePasaAck(Long originalPacketID, String originalipAddress)
	{
		packetID++;

		byte[] nm = ipAddress.getBytes();
		byte[] out = ("Nice to meet you").getBytes(); 
		//Allocate buffer large enough to hold the packetType + ipAddress, its length and the packetID
		ByteBuffer b = ByteBuffer.allocate(8 + 8 + nm.length + 8 + 1 + out.length);
		b.putLong(Constants.QUE_PASA_RESPONSE);
		b.putLong(packetID);
		b.putLong(nm.length);
		b.put(nm);

		b.put(out);
		byte[] buff = b.array();

		sendBroadcast(buff);

	}

	private class TimeoutThread extends Thread {

		private boolean shuttingDown = false;;

		public void run() {
			while(!shuttingDown)
			{

				try{

					sleep(10000);

					GeoWifiCommunicationsService.this.timeoutUnAckedPackets();
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}

		}

		public void shuttingDown()
		{
			shuttingDown = true;
		}
	};

	private synchronized boolean addUnAckedPacket(Quad<Long,Long,String,Integer> packet)
	{
		boolean ret = true;
		ret = unAckedPackets.add(packet);
		return ret;
	}

	private synchronized boolean removeUnAckedPacket(Long id)
	{
		boolean ret = true;
		for(Quad<Long,Long,String,Integer> element:unAckedPackets)
		{
			if(element.getSecond() == id)
			{
				unAckedPackets.removeFirstOccurrence(element);
				ret = true;
				break;
			}
		}
		return ret;
	}

	private synchronized boolean timeoutUnAckedPackets()
	{
		boolean ret = true;
		Long current  = System.currentTimeMillis();
		Quad<Long,Long,String,Integer> next = unAckedPackets.peek();
		while(next != null && next.getFirst()<current)
		{
			Quad<Long,Long,String,Integer> expired = unAckedPackets.poll();
			if(expired !=null)
			{
				if(expired.getFourth().equals(Constants.SEND_TO_SPECIFIC))
				{
					callbackObj.reportConnectionDisabled(expired.getThird());
				}
				else if(expired.getFourth().equals(Constants.CONNECT_MESSAGE))
				{
					callbackObj.reportConnectionAttemptFailed(expired.getThird());
					//Don't need to do anything with user?
				}
				externalUserState.disconnectUser(expired.getThird());
			}
			next = unAckedPackets.peek();
		}
		return ret;
	}

	private synchronized boolean removeUnAckedPacketsForDisconnectedNode(String node)
	{
		List<Quad<Long,Long,String,Integer>> toBeRemoved = new ArrayList<Quad<Long,Long,String,Integer>>();
		for(Quad<Long,Long,String,Integer> element:unAckedPackets)
		{
			if(element.getThird().equals(node))
			{
				toBeRemoved.add(element);
			}
		}
		return unAckedPackets.removeAll(toBeRemoved);
	}

	private synchronized Quad<Long,Long,String,Integer> ackedPacket(Long id)
	{
		for(Quad<Long,Long,String,Integer> element:unAckedPackets)
		{
			if(element.getSecond().equals(id))
			{
				unAckedPackets.removeFirstOccurrence(element);
				return element;
			}
		}
		return null;
	}

}
