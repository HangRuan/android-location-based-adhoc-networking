package edu.gmu.hodum.sei.service;

import edu.gmu.hodum.sei.network.BroadcastNetworkManager;
import edu.gmu.hodum.sei.network.Receiver;
import edu.gmu.hodum.sei.network.Sender;
import edu.gmu.hodum.sei.ui.MyApplication;
import edu.gmu.hodum.sei.util.Constants;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BroadcastNetworkService extends Service implements Sender, Receiver {

	private BroadcastNetworkManager networkManager;
	private String channel;
	@Override
	public IBinder onBind(Intent arg0) {


		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		
		String action = intent.getAction();
		Log.d("BroadcastNetworkService", action);
		if(action != null && action.equals(Constants.INITIALIZE_NETWORK))
		{
			channel = intent.getStringExtra("channel");
			Log.d("BroadcastNetworkService", "Got onStartCommand with: " + channel);
			if(networkManager == null)
			{
				new StartNetworkAsyncTask().execute(null,null);
			}
			else
			{
				Intent broadcastIntent = new Intent(Constants.NETWORK_INITIALIZED);
				broadcastIntent.putExtra("ipAddress", networkManager.getIPAddress());
				BroadcastNetworkService.this.sendBroadcast(broadcastIntent);
			}
		}
		else if(action.equals(Constants.SEND_DATA))
		{
			if(networkManager!= null)
			{
				
				new SendDataAsyncTask().execute(intent.getExtras(),null);
			}
		}
		return START_NOT_STICKY;
	}

	@Override
	public void sendMessage(Location center, double radius, byte[] buff) {
		if(networkManager != null)
		{
			networkManager.sendMessage(center, radius, buff);
		}
	}

	public void initNetwork(String channel)
	{
		networkManager = BroadcastNetworkManager.instance(this);
		networkManager.initNetwork(channel);
		networkManager.beginReceivingPackets();
		networkManager.registerReceiver(this);
	}

	
	private class StartNetworkAsyncTask extends AsyncTask<Object, Object, Object>
	{

		@Override
		protected Object doInBackground(Object... params) {
			initNetwork(channel);
			Intent broadcastIntent = new Intent(Constants.NETWORK_INITIALIZED);
			broadcastIntent.putExtra("ipAddress", networkManager.getIPAddress());
			BroadcastNetworkService.this.sendBroadcast(broadcastIntent);
			return null;
		}
	};
	
	private class SendDataAsyncTask extends AsyncTask<Bundle, Object, Object>
	{

		@Override
		protected Object doInBackground(Bundle... params) {
			// TODO Auto-generated method stub
			double latitude = params[0].getDouble("latitude");
			double longitude = params[0].getDouble("longitude");
			double radius = params[0].getDouble("radius");
			byte[] buff = params[0].getByteArray("data");
			Location loc = new Location("Other");
			loc.setLatitude(latitude);
			loc.setLongitude(longitude);
			sendMessage(loc,radius,buff);
			return null;
		}
	}

	@Override
	public void receiveMessage(Location center, double radius,
			Location originatingLocation, byte[] buff) {
		Intent broadcastIntent = new Intent(Constants.RECEIVE_DATA);
		broadcastIntent.putExtra("latitude", center.getLatitude());
		broadcastIntent.putExtra("longitude", center.getLongitude());
		broadcastIntent.putExtra("radius", radius);
		broadcastIntent.putExtra("originatingLatitude", originatingLocation.getLatitude());
		broadcastIntent.putExtra("originatingLongitude", originatingLocation.getLongitude());
		broadcastIntent.putExtra("data", buff);
		BroadcastNetworkService.this.sendBroadcast(broadcastIntent);
		
	}

	
	@Override
	public void debugMessage(Location center, double radius,
			Location originatingLocation, byte[] buff) {
		Intent broadcastIntent = new Intent(Constants.DEBUG_RECEIVE_DATA);
		broadcastIntent.putExtra("latitude", center.getLatitude());
		broadcastIntent.putExtra("longitude", center.getLongitude());
		broadcastIntent.putExtra("radius", radius);
		broadcastIntent.putExtra("originatingLatitude", originatingLocation.getLatitude());
		broadcastIntent.putExtra("originatingLongitude", originatingLocation.getLongitude());
		broadcastIntent.putExtra("data", buff);
		BroadcastNetworkService.this.sendBroadcast(broadcastIntent);
		
	};

}
