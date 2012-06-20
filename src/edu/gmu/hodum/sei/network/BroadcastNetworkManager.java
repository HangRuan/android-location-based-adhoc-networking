package edu.gmu.hodum.sei.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import edu.gmu.hodum.sei.ui.LocationHolder;
import edu.gmu.hodum.sei.ui.MyApplication;
import edu.gmu.hodum.sei.ui.StartNetworkActivity;
import edu.gmu.hodum.sei.util.Constants;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class BroadcastNetworkManager implements NetworkManager, Sender {

	private Vector<Receiver> receivers = new Vector<Receiver>();
	private DatagramSocket datagramSocket;
	private final static int senderPort = 8882;
	private final static int receiverPort = 8881;
	
	private WifiManager wifi;
	private Context context;
	
	private long counter = 0;
	private SelectorThread receiverThread = null;
	private static BroadcastNetworkManager pInstance = null;
	private MulticastLock lock = null;
	private boolean networkStarted = false;
	
	private Map<String,HashSet<Long>> uniqueIDs = new HashMap<String, HashSet<Long> >();
	public static String uniqueID;
	private String ipAddress = null;

	public static BroadcastNetworkManager instance(Context ctxt)
	{
		if(pInstance == null)
		{
			pInstance = new BroadcastNetworkManager(ctxt);
		}
		return pInstance;
	}

	private BroadcastNetworkManager(Context activity){
		this.context = activity;
		try{
			datagramSocket = new DatagramSocket(senderPort);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// Setup WiFi
		wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		lock = wifi.createMulticastLock("testing");
		lock.acquire();
		// Get WiFi status
		
	}


	public void shuttingDown()
	{
		this.receiverThread.shuttingDown();
	}
	
	@Override
	public void sendMessage(Location center, double radius, byte[] buff)
	{
		checkIfIDsent(this.getIPAddress(),counter);
		sendMessage(center, radius, this.getIPAddress(), counter, buff);
		counter++;
	}

	private Location getLocationFromPreferences()
	{
		String latitude = PreferenceManager.getDefaultSharedPreferences(context).getString("latitude", "-37.15");
		String longitude = PreferenceManager.getDefaultSharedPreferences(context).getString("longitude", "-70.15");
		Location loc = new Location("Other");
		loc.setLatitude(Double.parseDouble(latitude));
		loc.setLongitude(Double.parseDouble(longitude));
		return loc;
		
	}

	private void sendMessage(Location center, double radius,String uniqueId, long counter, byte[] buff)
	{

		
		Location myLoc = getLocationFromPreferences();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			dos.writeDouble(myLoc.getLatitude());
			dos.writeDouble(myLoc.getLongitude());

			encoodeUniqueID(dos, uniqueId, counter);
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

	private void encoodeUniqueID(DataOutputStream dos, String uniqueID, long count) throws IOException
	{
		uniqueID = (uniqueID ==null ? "foo":uniqueID);
		dos.writeInt(uniqueID.length());
		dos.writeChars(uniqueID);
		dos.writeLong(count);
	}

	private boolean checkIfIDsent(String uniqueID, long id)
	{
		boolean ret = false;
		HashSet<Long> ids = uniqueIDs.get(uniqueID);
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
			uniqueIDs.put(uniqueID, ids);
		}
		return ret;
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
	public void beginReceivingPackets() {
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

			
			InetAddress IPAddress = InetAddress.getByName(Constants.broadcastAddress);
			//do we have a packet to be broadcasted?
			DatagramPacket sendPacket;

			//datagramSocket.setBroadcast(true);
			sendPacket = new DatagramPacket(data, data.length, IPAddress, receiverPort);

			//Toast.makeText(ctxt, "Sent Packet",  Toast.LENGTH_LONG);
			datagramSocket.send(sendPacket);

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
			long packetUniqueID = dis.readLong();
			//Check if I hav seen this, if so, just return
			if(checkIfIDsent(receivedMacAddress,packetUniqueID))
			{
				return;
			}
			Location myLoc = getLocationFromPreferences();
			short type = dis.readShort();
			if(type == Constants.ROUND_REGION_ADDRESS)
			{
				Location center = new Location("Other");
				center.setLatitude(dis.readDouble());
				center.setLongitude(dis.readDouble());
				double radius = dis.readDouble();
				int lngth = dis.readInt();
				byte[] buff = new byte[lngth];
				dis.readFully(buff);
//				Changed this to move filtering out of the network piece and leave only routing
//				if(myLoc.distanceTo(center)<radius || radius == -1.0)
				{
					for(Receiver receiver:receivers)
					{
						receiver.receiveMessage(center, radius, sourceLoc, buff);
					}
				}
				//####DEBUG ONLY!!!
//				Changed this to move filtering out of the network piece and leave only routing
//				else
				{
					for(Receiver receiver:receivers)
					{
						receiver.debugMessage(center, radius, sourceLoc, buff);
					}
				}
				//resend message
				//need to keep track of the unique ID so I don;t keep resending message
				BroadcastNetworkManager.this.sendMessage(center, radius, receivedMacAddress, packetUniqueID, buff);
			}

		}
		catch(IOException e)
		{

		}
	}

	private class SelectorThread extends Thread {

		private DatagramChannel channel;
		private boolean notShuttingDown = true;

		SelectorThread() {

			try {
				
				channel = DatagramChannel.open();

				channel.socket().bind(new InetSocketAddress(8881));
				channel.socket().setSoTimeout(1000);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
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

	@Override
	public void initNetwork(String channel) {
		String val = null;
		
		String cmd1;
		String cmd1a = null;
		String networkName = "eth0 ";
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
		{
			cmd1 = "busybox insmod /system/lib/modules/bcm4329.ko firmware_path=/system/vendor/firmware/fw_bcm4329_apsta.bin nvram_path=/system/vendor/firmware/nvram_net.txt\n";
		}
		else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB || Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR1 || 
				Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			cmd1 = "busybox insmod /lib/modules/dhd.ko firmware_path=/system/etc/wifi/bcm4330_aps.bin nvram_path=/system/etc/wifi/nvram_net.txt_us\n";
		}
//		else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD_MR1)
//		{
//			cmd1 = "busybox insmod /system/lib/modules/tiwlan_drv.ko\n";
//			cmd1a = "wlan_loader -f /system/etc/wifi/fw_wlan1271.bin -i " + activity.getFilesDir() + "/tiwlan.ini\n";
//			networkName = "tiwlan0 ";
//		}
		else
		{
			cmd1 = "busybox insmod /system/modules/bcm4329.ko firmware_path=/system/vendor/firmware/fw_bcm4329.bin nvram_path=/system/vendor/firmware/nvram_net.txt\n";
		}
		ipAddress = Constants.networkPrefix +  String.valueOf((int)(100*Math.random()));
		{
			try {

				String su = "su";
				String cmd2 = "ifconfig " + networkName  + ipAddress + " netmask 255.255.255.0\n";// getFilesDir() + "/" + Constants.NEXUS_SCRIPT1 + " load \n";
				String cmd3 = context.getFilesDir() + "/" + "iwconfig " + networkName + "mode ad-hoc\n";
				String cmd4 = context.getFilesDir() + "/" + "iwconfig " + networkName +" channel " + channel + "\n";
				String cmd5 = context.getFilesDir() + "/" + "iwconfig " + networkName + " essid SEI_GMU_Test\n";
				String cmd6 = context.getFilesDir() + "/" + "iwconfig " + networkName + " key 6741744573\n";
				Process p = null; 
				p = Runtime.getRuntime().exec(su);
				DataOutputStream  output=new DataOutputStream(p.getOutputStream());
				InputStream inputStrm = p.getInputStream();
				InputStream errorStrm = p.getErrorStream();
				output.writeBytes(cmd1);
				if(cmd1a != null)
				{
					output.writeBytes(cmd1a);
				}
				output.writeBytes(cmd2);
				output.writeBytes(cmd3);
				output.writeBytes(cmd4);
				output.writeBytes(cmd5);
				
				output.writeBytes("exit \n");
				output.writeBytes("exit \n");
				int exit = p.waitFor();
				Log.e("StartNetwork","exit= " + exit);
				//					Process p = null; 
				//					p = Runtime.getRuntime().exec("cd /data/data/edu.cs898/files;./script_nexus adhoc;./script_nexus configure");
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public String getIPAddress() {
		
		return ipAddress;
	}

}
