package edu.gmu.hodum.service_client;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


import edu.cmu.sei.rtss.contextaware.firstresponder.CommunicationsManagerCallback;
import edu.cmu.sei.rtss.contextaware.firstresponder.gmu_impl.CommunicationsManagerCallbackImpl;
import edu.cmu.sei.rtss.contextaware.firstresponder.gmu_impl.GeoWifiCommunicationsService;
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
import android.widget.TextView;

public class ScriptRunner extends Activity implements OnClickListener, CommunicationsManagerCallback {

	private GeoWifiCommunicationsService pComms;
	EditText log;
	GeoWifiCommunicationsService pInstance;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sending);
		findViewById(R.id.sendBroadcast).setOnClickListener(this);
		findViewById(R.id.sendSingle).setOnClickListener(this);
		findViewById(R.id.sendConnect).setOnClickListener(this);
		log = (EditText)findViewById(R.id.log);
		CommunicationsManagerCallbackImpl.getInstance().registerInterest(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}


	@Override
	public void onClick(View v) {
		String external = ((EditText)findViewById(R.id.singleAddress)).getText().toString();
		String connect = ((EditText)findViewById(R.id.connectAddress)).getText().toString();
		if(v.getId() == R.id.sendBroadcast)
		{
			pInstance = GeoWifiCommunicationsService.getInstance(this.getApplicationContext());
			pInstance.sendDataToAll(("hi").getBytes());
		}
		else if(v.getId() == R.id.sendSingle && external.length()>0)
		{
			pInstance = GeoWifiCommunicationsService.getInstance(this.getApplicationContext());
			
			pInstance.sendData("192.168.42." + external, ("Hello").getBytes());
		}
		else if(v.getId() == R.id.sendConnect && connect.length()>0)
		{
			pInstance = GeoWifiCommunicationsService.getInstance(this.getApplicationContext());
			
			pInstance.connect("192.168.42." + connect);
		}
	}

	@Override
	public void reportNewUser(String externalUserID, String userName) {
		// TODO Auto-generated method stub
		log.append("Found new user: " + externalUserID + "\n");
		log.refreshDrawableState();
	}

	@Override
	public void reportConnectionEnabled(String externalUserID) {
		final String msg = externalUserID;
		runOnUiThread(new Runnable()
		{
			public void run() {
				log.append("Connection enabled: " + msg + "\n");
			}
		});
		
	}

	@Override
	public void reportConnectionDisabled(String externalUserID) {
		final String msg = externalUserID;
		runOnUiThread(new Runnable()
		{
			public void run() {
				log.append("Connection disabled: " + msg + "\n");
			}
		});
		
	}

	@Override
	public void reportConnectionAttemptFailed(String externalUserID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportMessageReceived(String externalUserID,
			byte[] messagePayload, int payloadSize) {
		// TODO Auto-generated method stub
		final String msg = externalUserID;
		runOnUiThread(new Runnable()
		{
			public void run() {
				log.append("Received data from: " + msg + "\n");
			}
		});
		
	}
}
