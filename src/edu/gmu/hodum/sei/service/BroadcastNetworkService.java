package edu.gmu.hodum.sei.service;

import edu.gmu.hodum.sei.network.BroadcastNetworkManager;
import edu.gmu.hodum.sei.network.Sender;
import edu.gmu.hodum.sei.ui.MyApplication;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

public class BroadcastNetworkService extends Service implements Sender {

	private BroadcastNetworkManager networkManager;

	@Override
	public IBinder onBind(Intent arg0) {


		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{

		return START_STICKY;
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
		networkManager = BroadcastNetworkManager.instance(this, ((MyApplication)this.getApplication()));
		networkManager.initNetwork(channel);
	}
	
	public class LocalBinder extends Binder {
        BroadcastNetworkService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BroadcastNetworkService.this;
        }
    }

}
