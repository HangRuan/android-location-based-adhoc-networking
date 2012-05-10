package edu.gmu.service;


import edu.gmu.contextdb.utils.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;


public class MyBroadcastSenderReceiver extends BroadcastReceiver {

	Context ctxt;
	@Override
	public void onReceive(Context arg0, Intent intent) {
		//		<action android:name="edu.gmu.hodum.START_RECEIVING" />
		//		<action android:name="edu.gmu.hodum.SEND_DATA" />
		//		<action android:name="edu.gmu.hodum.SHUTDOWN_NETWORK" />

		Log.d("BROADCAST_RECEIVER","Starting.....");
		Message msg = Message.obtain();
		if(intent.getAction().equals(Constants.RECEIVE_DATA))
		{
			msg.arg1 = Constants.RECEIVE_DATA_MSG;
		}
		else
		{
			msg.arg1 = Constants.DEBUG_RECEIVE_DATA_MSG;
		}
		Bundle data = new Bundle();
		data.putDouble("latitude",intent.getDoubleExtra("latitude",-1));
		data.putDouble("longitude",intent.getDoubleExtra("longitude",-1));
		data.putDouble("originatingLatitude",intent.getDoubleExtra("originatingLatitude",-1));
		data.putDouble("originatingLongitude",intent.getDoubleExtra("originatingLongitude",-1));
		data.putDouble("radius",intent.getDoubleExtra("radius",-1));
		data.putByteArray("data", intent.getByteArrayExtra("data"));
		msg.setData(data);
		Intent startServiceIntent = new Intent(arg0,ContextDatabaseService.class);
		startServiceIntent.setAction(intent.getAction());
		startServiceIntent.putExtras(intent);
		Log.d("BROADCAST_RECEIVER","Starting.....");
		arg0.startService(startServiceIntent);
		Log.d("BROADCAST_RECEIVER","Started???");

	}


}
