package edu.cs895.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.cs895.LocationHolder;
import edu.cs895.MyApplication;
import edu.cs895.util.Constants;

import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;
import android.widget.Toast;

public class BroadcastNetworkManager implements NetworkManager, Sender {

	private Vector<Receiver> receivers = new Vector<Receiver>();
	private DatagramSocket datagramSocket;
	private int receiverPort = 8881;
	private String subNetPart = "192.168.13.";
	private WifiManager wifi;
	private Context ctxt;
	private String macAddress;
	private long counter = 0;
	private SelectorThread receiverThread = null;
	private static BroadcastNetworkManager pInstance = null;
	private MulticastLock lock = null;
	boolean networkStarted = false;
	private LocationHolder locationHolder;
	private Map<String,HashSet<Long>> uniqueIDs = new HashMap<String, HashSet<Long> >();
	public static String macAddressSet;

	public static BroadcastNetworkManager instance(Context ctxt, LocationHolder locationHolder)
	{
		if(pInstance == null)
		{
			pInstance = new BroadcastNetworkManager(ctxt, locationHolder);
		}
		return pInstance;
	}
	
	private BroadcastNetworkManager(Context ctxt, LocationHolder locationHolder){
		this.ctxt = ctxt;
		try{
			datagramSocket = new DatagramSocket(8882);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.locationHolder = locationHolder;
		// Setup WiFi
		wifi = (WifiManager) ctxt.getSystemService(Context.WIFI_SERVICE);
		lock = wifi.createMulticastLock("testing");
		lock.acquire();
		// Get WiFi status
		macAddress = wifi.getConnectionInfo().getMacAddress();
		if(macAddress == null)
		{
			macAddress = macAddressSet;
		}
	}

	private void sendMessage(Location loc, String macAddr, long count, byte[] buff)
	{
		if(checkIfIDsent(macAddress, counter))
		{
			return;
		}
		Location myLoc = locationHolder.getCurrentLocation();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			dos.writeDouble(myLoc.getLatitude());
			dos.writeDouble(myLoc.getLongitude());
			
			encodeMACAddress(dos, macAddr, count);
			dos.writeShort(Constants.POINT_ADDRESS);
			dos.writeDouble(loc.getLatitude());
			dos.writeDouble(loc.getLongitude());
			dos.writeInt(buff.length);
			dos.write(buff);
			dos.flush();
			dos.close();
		}
		catch(IOException ex)
		{

		}

		try {
			sendPacket(baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataExceedsMaxSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void shuttingDown()
	{
		this.receiverThread.shuttingDown();
	}
	
	@Override
	public void sendMessage(Location loc, byte[] buff) {
		
		sendMessage(loc, macAddress, counter, buff);
		counter++;
	}

	private void sendMessage(Location bottomLeft, Location topRight, String macAddr, long count, byte[] buff)
	{
		if(checkIfIDsent(macAddress, counter))
		{
			return;
		}
		Location myLoc = locationHolder.getCurrentLocation();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			dos.writeDouble(myLoc.getLatitude());
			dos.writeDouble(myLoc.getLongitude());
			
			encodeMACAddress(dos, macAddr, count);
			dos.writeShort(Constants.SQUARE_REGION_ADDRESS);
			dos.writeDouble(bottomLeft.getLatitude());
			dos.writeDouble(bottomLeft.getLongitude());
			dos.writeDouble(topRight.getLatitude());
			dos.writeDouble(topRight.getLongitude());
			dos.writeInt(buff.length);
			dos.write(buff);
			dos.flush();
			dos.close();
		}
		catch(IOException ex)
		{

		}

		try {
			sendPacket(baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataExceedsMaxSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean checkIfIDsent(String macAddr, long id)
	{
		boolean ret = false;
		HashSet<Long> ids = uniqueIDs.get(macAddr);
		if(ids != null)
		{
			if(!ids.add(new Long(id)))
			{
				ret = true;
			}
		}
		else
		{
			ids = new HashSet<Long>();
			ids.add(new Long(id));
			uniqueIDs.put(macAddr, ids);
		}
		return ret;
	}
	
	@Override
	public void sendMessage(Location bottomLeft, Location topRight,  byte[] buff)
	{
		if(checkIfIDsent(macAddress, counter))
		{
			return;
		}
		sendMessage(bottomLeft, topRight, macAddress, counter, buff);
		counter++;
		
	}

	public void sendMessage(Location center, double radius,String macAddress, long counter, byte[] buff)
	{
		
		
		Location myLoc = locationHolder.getCurrentLocation();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			dos.writeDouble(myLoc.getLatitude());
			dos.writeDouble(myLoc.getLongitude());
			
			encodeMACAddress(dos, macAddress, counter);
			dos.writeShort(Constants.ROUND_REGION_ADDRESS);
			dos.writeDouble(center.getLatitude());
			dos.writeDouble(center.getLongitude());
			dos.writeDouble(radius);
			
			dos.writeInt(buff.length);
			dos.write(buff);
			dos.flush();
			dos.close();
		}
		catch(IOException ex)
		{

		}

		try {
			sendPacket(baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataExceedsMaxSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendMessage(Location center, double radius, byte[] buff)
	{
		if(checkIfIDsent(macAddress, counter))
		{
			return;
		}
		sendMessage(center, radius, macAddress, counter, buff);
		counter++;
	}
	
	
//	private  void resendMessage(Location center, double radius,  byte[] buff)
//	{
//		Location myLoc = locationHolder.getCurrentLocation();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		DataOutputStream dos = new DataOutputStream(baos);
//		try{
//			dos.writeDouble(myLoc.getLatitude());
//			dos.writeDouble(myLoc.getLongitude());
//			
//			//encodeMACAddress(dos);
//			
//			dos.writeShort(Constants.ROUND_REGION_ADDRESS);
//			dos.writeDouble(center.getLatitude());
//			dos.writeDouble(center.getLongitude());
//			dos.writeDouble(radius);
//			
//			dos.writeInt(buff.length);
//			dos.write(buff);
//			dos.flush();
//			dos.close();
//		}
//		catch(IOException ex)
//		{
//
//		}
//
//		try {
//			sendPacket(baos.toByteArray());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DataExceedsMaxSizeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private void encodeMACAddress(DataOutputStream dos, String macAddr, long count) throws IOException
	{
		macAddr = (macAddr ==null ? "foo":macAddr);
		dos.writeInt(macAddr.length());
		dos.writeChars(macAddr);
		dos.writeLong(count);
	}

	@Override
	public boolean registerReceiver(Receiver receiver) {
		return receivers.add(receiver);
	}

	@Override
	public boolean removeReceiver(Receiver receiver) {
		return receivers.remove(receiver);

	}

	@Override
	public void startNetwork() {
		if(!networkStarted)
		{
			// TODO Auto-generated method stub
			// In this method, spawn thread and begin processing the network. 
			receiverThread = new SelectorThread();
			receiverThread.start();
			networkStarted = true;
		}
	}


	/**
	 * Sends data using the UDP protocol to a specific receiver
	 * @param destinationNodeID indicates the ID of the receiving node. Should be a positive integer.
	 * @param data is the message which is to be sent. 
	 * @throws IOException 
	 * @throws SizeLimitExceededException is thrown if the length of the data to be sent exceeds the limit
	 */
	private boolean sendPacket(byte[] data) throws IOException, DataExceedsMaxSizeException{
		if(data.length <= Constants.MAX_PACKAGE_SIZE){
//			for(int i=1;i<20;i++)
//			{
			int i = 255;
				InetAddress IPAddress = InetAddress.getByName(subNetPart + String.valueOf(i));
				//do we have a packet to be broadcasted?
				DatagramPacket sendPacket;

				//datagramSocket.setBroadcast(true);
				sendPacket = new DatagramPacket(data, data.length, IPAddress, receiverPort);

				//Toast.makeText(ctxt, "Sent Packet",  Toast.LENGTH_LONG);
				datagramSocket.send(sendPacket);
//			}
			return true;
		} else {
			throw new DataExceedsMaxSizeException();
		}
	}

	public void closeSoket(){
		datagramSocket.close();
	}

	protected void processPacket(ByteBuffer buf)
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(buf.array());
		DataInputStream dis = new DataInputStream(bais);
		try{
			Location sourceLoc = new Location ("Other");
			sourceLoc.setLatitude(dis.readDouble());
			sourceLoc.setLongitude(dis.readDouble());
			
			int macAddressLength = dis.readInt();
			String receivedMacAddress = new String();
			for(int i=0;i<macAddressLength;i++)
			{
				receivedMacAddress+= dis.readChar();
			}
			long uniqueID = dis.readLong();
			//Check if I hav seen this, if so, just return
			if(checkIfIDsent(receivedMacAddress,uniqueID))
			{
				return;
			}
			short type = dis.readShort();
			if(type == Constants.POINT_ADDRESS)
			{
				Location loc = new Location("Other");
				loc.setLatitude(dis.readDouble());
				loc.setLongitude(dis.readDouble());
				
				int lngth = dis.readInt();
				byte[] buff = new byte[lngth];
				dis.readFully(buff);
				for(Receiver receiver:receivers)
				{
					receiver.receiveMessage(loc, sourceLoc, buff);
				}
				//resend message
				//need to keep track of the unique ID so I don;t keep resending message
				
				BroadcastNetworkManager.this.sendMessage(loc, receivedMacAddress, uniqueID, buff);
			}
			else if(type == Constants.SQUARE_REGION_ADDRESS)
			{
				Location bottomLeft = new Location("Other");
				bottomLeft.setLatitude(dis.readDouble());
				bottomLeft.setLongitude(dis.readDouble());
				Location topRight = new Location("Other");
				topRight.setLatitude(dis.readDouble());
				topRight.setLongitude(dis.readDouble());
				
				int lngth = dis.readInt();
				byte[] buff = new byte[lngth];
				dis.readFully(buff);
				for(Receiver receiver:receivers)
				{
					receiver.receiveMessage(bottomLeft, topRight, sourceLoc, buff);
				}	
				//resend message
				//need to keep track of the unique ID so I don;t keep resending message
				BroadcastNetworkManager.this.sendMessage(bottomLeft, topRight, receivedMacAddress, uniqueID, buff);
			}
			else if(type == Constants.ROUND_REGION_ADDRESS)
			{
				Location center = new Location("Other");
				center.setLatitude(dis.readDouble());
				center.setLongitude(dis.readDouble());
				double radius = dis.readDouble();
				int lngth = dis.readInt();
				byte[] buff = new byte[lngth];
				dis.readFully(buff);
				for(Receiver receiver:receivers)
				{
					receiver.receiveMessage(center, radius, sourceLoc, buff);
				}
				//resend message
				//need to keep track of the unique ID so I don;t keep resending message
				BroadcastNetworkManager.this.sendMessage(center, radius, receivedMacAddress, uniqueID, buff);
			}

		}
		catch(IOException e)
		{

		}
	}

	private class SelectorThread extends Thread {
		private Selector selector;
		private DatagramChannel channel;
		private boolean notShuttingDown = true;

		SelectorThread() {

			try {
				selector = Selector.open();
				channel = DatagramChannel.open();
				//channel.configureBlocking(false);
				channel.socket().bind(new InetSocketAddress(8881));
				channel.socket().setSoTimeout(1000);
				//channel.register(selector, SelectionKey.OP_ACCEPT&SelectionKey.OP_READ&SelectionKey.OP_CONNECT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				String clazz = e.getClass().getCanonicalName();
				e.printStackTrace();
				String cause = e.getLocalizedMessage();
				String message = e.getMessage();
			}
		}

		public void run() {
			while(notShuttingDown)
			{
				ByteBuffer buf = ByteBuffer.allocate(5000);
				buf.clear();
				try {
					SocketAddress retAddr = channel.receive(buf);
					if(retAddr != null)
					{
						BroadcastNetworkManager.this.processPacket(buf);
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//				while(notShuttingDown)
			//				{
			//					try {
			//				        // Wait for an event
			//				        selector.select();
			//				    } catch (IOException e) {
			//				        // Handle error with selector
			//				        break;
			//				    }
			//				}
			//				// Get list of selection keys with pending events
			//			    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			//
			//			    // Process each key at a time
			//			    while (it.hasNext()) {
			//			        // Get the selection key
			//			        SelectionKey selKey = (SelectionKey)it.next();
			//
			//			        // Remove it from the list to indicate that it is being processed
			//			        it.remove();
			//			        if (selKey.isValid() && selKey.isReadable()) {
			//			            // Get channel with bytes to read
			//			        	ByteBuffer buf = ByteBuffer.allocate(5000);
			//						buf.clear();
			//						try {
			//							channel.receive(buf);
			//							BroadcastNetworkManager.this.processPacket(buf);
			//						} catch (IOException e) {
			//							// TODO Auto-generated catch block
			//							e.printStackTrace();
			//						}
			//			        }
			//			    }
		}

		public void shuttingDown()
		{
			notShuttingDown = false;
		}
	}

	@Override
	public Sender getSender() {

		return this;
	}

}
