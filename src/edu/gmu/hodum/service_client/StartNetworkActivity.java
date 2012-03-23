package edu.gmu.hodum.service_client;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


import edu.gmu.hodum.service_client.receiver.MyNetworkInitializedReceiver;
import edu.gmu.hodum.service_client.util.Constants;
import edu.gmu.hodum.service_client.util.MyProgressDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class StartNetworkActivity extends Activity implements OnClickListener {

	private MyProgressDialog dialog;
	private static final int NETWORK_STARTED = -1;
	private MyNetworkInitializedReceiver receiver;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_wifi);
		findViewById(R.id.start_network).setOnClickListener(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}


	@Override
	public void onClick(View v) {
		dialog = new MyProgressDialog(this);
		dialog.show("Starting AdHoc Network..", null);
		String channel = ((EditText)findViewById(R.id.wifi_channel)).getText().toString();

		receiver = new MyNetworkInitializedReceiver();
		receiver.registerHandler(networkStartedHandler);
		IntentFilter filter = new IntentFilter(Constants.NETWORK_INITIALIZED);

		registerReceiver(receiver,filter);
		Intent broadcastIntent = new Intent(Constants.INITIALIZE_NETWORK);
		broadcastIntent.putExtra("channel", channel);
		this.sendBroadcast(broadcastIntent);
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
				unregisterReceiver(receiver);
				dialog.cancel();
				Intent sendActivity = new Intent(StartNetworkActivity.this,SendReceiveActivity.class);
				sendActivity.putExtra("ipAddress", msg.getData().getString("ipAddress"));
				StartNetworkActivity.this.startActivity(sendActivity);
				break;
			}
			super.handleMessage(msg); 

		}
	};

}
