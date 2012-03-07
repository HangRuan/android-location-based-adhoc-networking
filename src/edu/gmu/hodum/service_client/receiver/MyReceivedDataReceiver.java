package edu.gmu.hodum.service_client.receiver;

import edu.gmu.hodum.service_client.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MyReceivedDataReceiver extends android.content.BroadcastReceiver {

	private Handler callbackHandler;

	public void registerHandler(Handler handler)
	{
		callbackHandler = handler;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
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
		callbackHandler.sendMessage(msg);
	}

}
