package edu.gmu.hodum.sei.ui;



import java.util.HashMap;
import java.util.Map;


import edu.gmu.hodum.sei.network.BroadcastNetworkManager;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

public class MyApplication extends Application {


	public static  Location location;
        	
	private  String ipAddress;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			Location loc2 = new Location("Other");
			loc2.setBearing(250f);
			loc2.setLatitude(37.5);
			loc2.setLongitude(-73.25);
			loc2.setSpeed(1.9f);
			location = loc2;
		}
		else
		{
			Location loc0 = new Location("Other");
			loc0.setBearing(2f);
			loc0.setLatitude(38.843295+ Math.random());
			loc0.setLongitude(-77.288038 + Math.random());
			loc0.setSpeed(1.2f);
			location = loc0;
			
		}
		
	}

	public Location getCurrentLocation()
	{
		return location;
	}
	
	
	
	@Override
	public void onTerminate()
	{

	}

	
}
