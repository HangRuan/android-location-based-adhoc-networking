package edu.gmu.hodum.service_client;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


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

public class StartNetworkActivity extends Activity implements OnClickListener {

	private GeoWifiCommunicationsService pComms;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_sei_network);
		findViewById(R.id.initialize).setOnClickListener(this);
		findViewById(R.id.start).setOnClickListener(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.initialize)
		{
			pComms = GeoWifiCommunicationsService.getInstance(this.getApplicationContext());
			pComms.initialize(CommunicationsManagerCallbackImpl.getInstance());
		}
		else
		{
			pComms.start();
			
			Intent sendActivity = new Intent(this,ScriptRunner.class);
			this.startActivity(sendActivity);
			this.finish();
		}
	}
}
