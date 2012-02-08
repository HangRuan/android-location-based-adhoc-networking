package edu.gmu.hodum.sei.heartbeatprotocol;

import java.nio.ByteBuffer;

import android.location.Location;
import edu.gmu.hodum.sei.network.Receiver;
import edu.gmu.hodum.sei.network.Sender;
import edu.gmu.hodum.sei.ui.MyApplication;
import edu.gmu.hodum.sei.ui.SendReceiveActivity;

public class HeartbeatSender implements Receiver, DiscoverUsersService {

	private Sender sender;
	MyApplication application;
	SendReceiveActivity userInterface;
	
	public HeartbeatSender(Sender snder, MyApplication appl, SendReceiveActivity ui)
	{
		application = appl;
		sender=snder;
		userInterface = ui;
	}
	
	
	
	@Override
	public void receiveMessage(Location center, double radius,
			Location originatingLocation, byte[] buff) {
		final ByteBuffer b = ByteBuffer.wrap(buff);
		new Thread(){
			public void run()
			{
				long length = b.getLong();
				byte[] dst = new byte[(int)length];
				b.get(dst, 0, (int)length);
				String srce = new String(dst);
				if("DiscoverUsers".equals(srce))
				{
					userInterface.debugMessage("Got a user Discovery");
					sendUserResponse();
				}	
				else if("UserName".equals(srce))
				{
					length = b.getLong();
					byte[] nameBytes = new byte[(int)length];
					b.get(nameBytes, 0, (int)length);
					String name = new String(nameBytes);
					userInterface.debugMessage("Got a user response:" + name);
				}
				
			};
		}.start();

	}
	
	private void sendUserResponse()
	{
		ByteBuffer b = ByteBuffer.allocate(300);
		byte[] type = "UserName".getBytes();
		b.putLong(type.length);
		b.put(type);
		byte[] name = application.getIPAddress().getBytes();
		b.putLong(name.length);
		b.put(name);
		Location loc = new Location("Other");
		loc.setLatitude(360);
		loc.setLongitude(360);
		//###SEND Message broadcast hack!
		sender.sendMessage(loc, -1.0, b.array());
	}

	@Override
	public void debugMessage(Location center, double radius,
			Location originatingLocation, byte[] buff) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDiscovery() {
		
		ByteBuffer b = ByteBuffer.allocate(300);
		byte[] type = "DiscoverUsers".getBytes();
		b.putLong(type.length);
		b.put(type);
		b.putDouble(application.getCurrentLocation().getLatitude());
		b.putDouble(application.getCurrentLocation().getLongitude());
		b.putDouble(100.0);
		Location loc = new Location("Other");
		loc.setLatitude(360);
		loc.setLongitude(360);
		//###SEND Message broadcast hack!
		sender.sendMessage(loc, -1.0, b.array());
	}

	@Override
	public void endDiscovery() {
		// TODO Auto-generated method stub
		
	}


	public void debugMessage(String msg) {
		// TODO Auto-generated method stub
		
	}

}
