package edu.gmu.hodum.sei.ui;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import edu.gmu.hodum.sei.R;
import edu.gmu.hodum.sei.network.BroadcastNetworkManager;
import edu.gmu.hodum.sei.util.Constants;
import edu.gmu.hodum.sei.util.MyProgressDialog;
import edu.gmu.hodum.sei.util.Util;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class StartNetworkActivity extends Activity implements OnClickListener {



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_wifi);
		
		String latitude = PreferenceManager.getDefaultSharedPreferences(this).getString("latitude", "-37.15");
		String longitude = PreferenceManager.getDefaultSharedPreferences(this).getString("longitude", "-70.15");
		
		((EditText)findViewById(R.id.latitude)).setText(latitude);
		((EditText)findViewById(R.id.longitude)).setText(longitude);
		
		
		findViewById(R.id.save_location).setOnClickListener(this);

		
		Util.copyScripts(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}


	@Override
	public void onClick(View v) {
		
		String latitude = ((EditText)findViewById(R.id.latitude)).getText().toString();
		String longitude = ((EditText)findViewById(R.id.longitude)).getText().toString();
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString("latitude", latitude);
		editor.putString("longitude", longitude);
		editor.commit();
		
		Toast.makeText(this, "Latitude and Longitude saved.",Toast.LENGTH_LONG).show();
		this.finish();
	}
}
