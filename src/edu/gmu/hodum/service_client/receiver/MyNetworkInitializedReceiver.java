package edu.gmu.hodum.service_client.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MyNetworkInitializedReceiver extends android.content.BroadcastReceiver {

	private Handler callbackHandler;

	public void registerHandler(Handler handler)
	{
		callbackHandler = handler;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Message msg = Message.obtain();
		msg.arg1 = -1;
		Bundle data = new Bundle();
		data.putString("ipAddress",intent.getStringExtra("ipAddress"));
		msg.setData(data);
		callbackHandler.sendMessage(msg);
	}

}
