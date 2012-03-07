package edu.gmu.hodum.sei.service;

import edu.gmu.hodum.sei.network.Receiver;
import edu.gmu.hodum.sei.util.Constants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastSenderReceiver extends BroadcastReceiver implements Receiver {

	private BroadcastNetworkService networkService;
	private boolean bound = false;
	String channel;
	Context ctxt;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		//		<action android:name="edu.gmu.hodum.START_RECEIVING" />
		//		<action android:name="edu.gmu.hodum.SEND_DATA" />
		//		<action android:name="edu.gmu.hodum.SHUTDOWN_NETWORK" />
		Log.d("BROADCAST_RECEIVER", "Got the broadcast receiver");
		Intent startServiceIntent = new Intent(arg0,BroadcastNetworkService.class);
		startServiceIntent.setAction(arg1.getAction());
		startServiceIntent.putExtras(arg1);
		Log.d("BROADCAST_RECEIVER","Starting.....");
		arg0.startService(startServiceIntent);
		Log.d("BROADCAST_RECEIVER","Started???");

	}

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
