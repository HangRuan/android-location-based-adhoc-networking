package edu.cs895;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import edu.cs895.network.BroadcastNetworkManager;
import edu.cs895.util.Constants;
import edu.cs895.util.MyProgressDialog;
import edu.cs895.util.Util;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class StartNetworkActivity extends Activity implements OnClickListener {

	private MyProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_wifi);
		findViewById(R.id.start_hero).setOnClickListener(this);
		findViewById(R.id.start_nexus).setOnClickListener(this);
		findViewById(R.id.start_nexus2).setOnClickListener(this);
		findViewById(R.id.start_evo).setOnClickListener(this);
		findViewById(R.id.skip).setOnClickListener(this);
		Util.copyScripts(this);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub


		if (v.getId() == R.id.skip)

		{
			Intent newIntent = new Intent(this, ProjectAActivity.class);
			startActivity(newIntent);
			this.finish();
		}
		else 
		{
			dialog = new MyProgressDialog(this);
			dialog.show("Starting AdHoc Network..", null);
			if(v.getId() == R.id.start_hero)
			{
				BroadcastNetworkManager.macAddressSet = "hero";
				new MyAsyncTask().execute("hero");
			}
			else if (v.getId() == R.id.start_nexus)
			{
				BroadcastNetworkManager.macAddressSet = "nexus1";
				new MyAsyncTask().execute("nexus1");	
			}
			else if (v.getId() == R.id.start_nexus2)
			{
				BroadcastNetworkManager.macAddressSet = "nexus2";
				new MyAsyncTask().execute("nexus2");	
			}
			else if (v.getId() == R.id.start_evo)
			{
				BroadcastNetworkManager.macAddressSet = "evo";
				new MyAsyncTask().execute("evo");	
			}
		}
	}


	private class MyAsyncTask extends AsyncTask
	{

		@Override
		protected Object doInBackground(Object... params) {
			/*
			 * Process p = null; 
			p = Runtime.getRuntime().exec("su sqlite3 your_query");
			 */
			String val = null;
			if(params != null)
			{
				val = (String)params[0];
			}
			if(val != null && val.equals("hero"))
			{
				try {
					String su = "su";
					String cmd = getFilesDir() + "/" + Constants.HERO_SCRIPT + " load \n";
					Process p = null; 
					p = Runtime.getRuntime().exec(su);
					DataOutputStream  output=new DataOutputStream(p.getOutputStream());
					InputStream inputStrm = p.getInputStream();
					InputStream errorStrm = p.getErrorStream();
					output.writeBytes(cmd);

					output.writeBytes("exit \n");
				

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}


			}
			else if(val != null && val.equals("nexus1"))
			{
				try {

					String su = "su";
					String cmd = getFilesDir() + "/" + Constants.NEXUS_SCRIPT1 + " load \n";
					Process p = null; 
					p = Runtime.getRuntime().exec(su);
					DataOutputStream  output=new DataOutputStream(p.getOutputStream());
					InputStream inputStrm = p.getInputStream();
					InputStream errorStrm = p.getErrorStream();
					output.writeBytes(cmd);

					output.writeBytes("exit \n");
					output.writeBytes("exit \n");



					//					pb.command(cmd).directory(getFilesDir());
					//					Process p = pb.start();

					int exit = p.waitFor();
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStrm));
					BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStrm));
					String foo = new String("");;
					String bar = new String("");;
					int i=0;
					while(i<1000)
					{
						foo += inputReader.readLine();
						bar += errorReader.readLine();
						i++;
					}
					System.out.println(exit);

					p = Runtime.getRuntime().exec(su);
					output=new DataOutputStream(p.getOutputStream());
					String cmd2 = getFilesDir() + "/" +  Constants.NEXUS_SCRIPT2 + " up \n";
					output.writeBytes(cmd2);
					output.writeBytes("exit \n");

					p = Runtime.getRuntime().exec(su);
					output=new DataOutputStream(p.getOutputStream());
					String cmd3 = getFilesDir() + "ifconfig eth0 192.168.13.12 netmask 255.255.255.0\n";
					output.writeBytes(cmd3);
					output.writeBytes("exit \n");
					
					exit = p.waitFor();
					//					Process p = null; 
					//					p = Runtime.getRuntime().exec("cd /data/data/edu.cs898/files;./script_nexus adhoc;./script_nexus configure");
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			else if(val != null && val.equals("nexus2"))
			{
				try {

					String su = "su";
					String cmd = getFilesDir() + "/" + Constants.NEXUS_SCRIPT1 + " load \n";
					Process p = null; 
					p = Runtime.getRuntime().exec(su);
					DataOutputStream  output=new DataOutputStream(p.getOutputStream());
					InputStream inputStrm = p.getInputStream();
					InputStream errorStrm = p.getErrorStream();
					output.writeBytes(cmd);

					output.writeBytes("exit \n");
					output.writeBytes("exit \n");



					//					pb.command(cmd).directory(getFilesDir());
					//					Process p = pb.start();

					int exit = p.waitFor();
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStrm));
					BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStrm));
					String foo = new String("");;
					String bar = new String("");;
					int i=0;
					while(i<1000)
					{
						foo += inputReader.readLine();
						bar += errorReader.readLine();
						i++;
					}
					System.out.println(exit);

					p = Runtime.getRuntime().exec(su);
					output=new DataOutputStream(p.getOutputStream());
					String cmd2 = getFilesDir() + "/" +  Constants.NEXUS_SCRIPT2 + " up \n";
					output.writeBytes(cmd2);
					output.writeBytes("exit \n");

					p = Runtime.getRuntime().exec(su);
					output=new DataOutputStream(p.getOutputStream());
					String cmd3 = "ifconfig eth0 192.168.13.13 netmask 255.255.255.0\n";
					output.writeBytes(cmd3);
					output.writeBytes("exit \n");
					
					exit = p.waitFor();
					//					Process p = null; 
					//					p = Runtime.getRuntime().exec("cd /data/data/edu.cs898/files;./script_nexus adhoc;./script_nexus configure");
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			else if(val != null && val.equals("evo"))
			{
				try {

					String su = "su";
					String cmd = getFilesDir() + "/" + Constants.EVO_SCRIPT + " load \n";
					Process p = null; 
					p = Runtime.getRuntime().exec(su);
					DataOutputStream  output=new DataOutputStream(p.getOutputStream());
					InputStream inputStrm = p.getInputStream();
					InputStream errorStrm = p.getErrorStream();
					output.writeBytes(cmd);

					output.writeBytes("exit \n");
					output.writeBytes("exit \n");



					//					pb.command(cmd).directory(getFilesDir());
					//					Process p = pb.start();

					int exit = p.waitFor();
					
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			dialog.cancel();
			((MyApplication) getApplication()).initInterestEngine();
			Intent newIntent = new Intent(StartNetworkActivity.this, ProjectAActivity.class);
			startActivity(newIntent);
			StartNetworkActivity.this.finish();
			return null;
		}
	}
}
