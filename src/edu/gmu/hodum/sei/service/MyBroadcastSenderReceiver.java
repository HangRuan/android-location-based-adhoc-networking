package edu.gmu.hodum.sei.service;

import edu.gmu.hodum.sei.network.Receiver;
import edu.gmu.hodum.sei.service.BroadcastNetworkService.LocalBinder;
import edu.gmu.hodum.sei.util.Constants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class MyBroadcastSenderReceiver extends BroadcastReceiver implements Receiver {

	private BroadcastNetworkService networkService;
	private boolean bound = false;
	String channel;
	Context ctxt;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		//		<action android:name="edu.gmu.hodum.START_RECEIVING" />
		//		<action android:name="edu.gmu.hodum.SEND_DATA" />
		//		<action android:name="edu.gmu.hodum.SHUTDOWN_NETWORK" />

		ctxt = arg0;
		Bundle dataBundle = arg1.getExtras();
		if(arg1.getAction().equals(Constants.INITIALIZE_NETWORK) )
		{
			
			channel = dataBundle.getString("Channel");
			if(networkService == null)
			{
				Intent intent = new Intent(arg0, BroadcastNetworkService.class);
				arg0.bindService(intent, connection, Context.BIND_AUTO_CREATE);
			}
		}
		else if(arg1.getAction().equals(Constants.SEND_DATA))
		{
			if(networkService != null)
			{
				double latitude = dataBundle.getDouble("latitude");
				double longitude = dataBundle.getDouble("longitude");
				double radius = dataBundle.getDouble("radius");
				byte[] buff = dataBundle.getByteArray("data");
				Location center = new Location("unknown");
				center.setLatitude(latitude);
				center.setLongitude(longitude);
				
				networkService.sendMessage(center, radius, buff);
			}
		}
	}


	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			networkService = binder.getService();
			bound = true;
			networkService.initNetwork(channel);
			Toast.makeText(ctxt, "Started the network on channel: " + channel, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bound = false;
		}
	};
	@Override
	public void receiveMessage(Location center, double radius,
			Location originatingLocation, byte[] buff) {
		
		Toast.makeText(ctxt, "Got a packet", Toast.LENGTH_LONG).show();
		//Create intent an send message
	}


	@Override
	public void debugMessage(Location center, double radius,
			Location originatingLocation, byte[] buff) {
		// TODO Auto-generated method stub
		Toast.makeText(ctxt, "Got a debug packet", Toast.LENGTH_LONG).show();
	}

}
