package edu.gmu.hodum.sei.ui;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import edu.cs895.R;
import edu.gmu.hodum.sei.network.BroadcastNetworkManager;
import edu.gmu.hodum.sei.util.Constants;
import edu.gmu.hodum.sei.util.MyProgressDialog;
import edu.gmu.hodum.sei.util.Util;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class StartNetworkActivity extends Activity implements OnClickListener {

	private MyProgressDialog dialog;
	private String channel = "8";
	private String ipAddress;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_wifi);
		findViewById(R.id.start_network).setOnClickListener(this);



		Util.copyScripts(this);
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
		if(v.getId() == R.id.start_network)
		{
			BroadcastNetworkManager.uniqueID = ((MyApplication) getApplication()).getUniqueID();

		}
		channel = ((EditText)findViewById(R.id.wifi_channel)).getText().toString();
		new MyAsyncTask().execute(BroadcastNetworkManager.uniqueID);

	}


	private class MyAsyncTask extends AsyncTask
	{

		@Override
		protected Object doInBackground(Object... params) {

			String val = null;
			if(params != null)
			{
				val = (String)params[0];
			}
			String cmd1;
			if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
			{
				cmd1 = "busybox insmod /system/modules/bcm4329.ko firmware_path=/system/vendor/firmware/fw_bcm4329_apsta.bin nvram_path=/system/vendor/firmware/nvram_net.txt\n";
			}
			else
			{
				cmd1 = "busybox insmod /system/modules/bcm4329.ko firmware_path=/system/vendor/firmware/fw_bcm4329.bin nvram_path=/system/vendor/firmware/nvram_net.txt\n";
			}
			ipAddress = Constants.networkPrefix +  String.valueOf((int)(100*Math.random()));
			((MyApplication)StartNetworkActivity.this.getApplication()).setIPAddress(ipAddress);
			if(val != null )
			{
				try {

					String su = "su";
					String cmd2 = "ifconfig eth0 " + ipAddress + " netmask 255.255.255.0\n";// getFilesDir() + "/" + Constants.NEXUS_SCRIPT1 + " load \n";
					String cmd3 = getFilesDir() + "/" + "iwconfig eth0 mode ad-hoc\n";
					String cmd4 = getFilesDir() + "/" + "iwconfig eth0 channel " + StartNetworkActivity.this.channel + "\n";
					String cmd5 = getFilesDir() + "/" + "iwconfig eth0 essid SEI_GMU_Test\n";
					String cmd6 = getFilesDir() + "/" + "iwconfig eth0 key 6741744573\n";
					Process p = null; 
					p = Runtime.getRuntime().exec(su);
					DataOutputStream  output=new DataOutputStream(p.getOutputStream());
					InputStream inputStrm = p.getInputStream();
					InputStream errorStrm = p.getErrorStream();
					output.writeBytes(cmd1);
					output.writeBytes(cmd2);
					output.writeBytes(cmd3);
					output.writeBytes(cmd4);
					output.writeBytes(cmd5);
					
					output.writeBytes("exit \n");
					output.writeBytes("exit \n");
					int exit = p.waitFor();
					Log.e("StartNetwork","exit= " + exit);
					//					Process p = null; 
					//					p = Runtime.getRuntime().exec("cd /data/data/edu.cs898/files;./script_nexus adhoc;./script_nexus configure");
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}



			dialog.cancel();;
			Intent newIntent = new Intent(StartNetworkActivity.this, SendReceiveActivity.class);
			startActivity(newIntent);
			StartNetworkActivity.this.finish();
			return null;
		}
	}
}
