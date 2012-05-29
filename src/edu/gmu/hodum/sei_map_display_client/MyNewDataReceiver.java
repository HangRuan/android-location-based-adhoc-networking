package edu.gmu.hodum.sei_map_display_client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class MyNewDataReceiver extends BroadcastReceiver {

	
	private Handler callbackHandler;

	public void registerHandler(Handler handler)
	{
		callbackHandler = handler;
	}
	
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		callbackHandler.sendMessage(msg);
	}

}
