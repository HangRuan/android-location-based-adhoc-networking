package edu.cs895;

import java.util.ArrayList;
import java.util.List;

import edu.cs895.network.BroadcastNetworkManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class ApplicationLocationListener implements LocationListener, LocationHolder {

	private  Location lastLocation = new Location("Other");
	private List<LocationListener> listeners = new ArrayList<LocationListener>();
	private Location[] locations = new Location[3];
	private LocationThread locThread;
	
	public ApplicationLocationListener()
	{
		locThread = new LocationThread();
		locThread.start();
	}
	
	@Override
	synchronized public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lastLocation = location;
		updateListeners(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	synchronized public Location getCurrentLocation() {
		// TODO Auto-generated method stub
		return lastLocation;
	}
	
	synchronized private void updateListeners(Location location)
	{
//		Log.d("APP LOC LISTENER", "Latest location is: " + location.getLatitude() + ", " + location.getLongitude());
		for (LocationListener listener: listeners)
		{
			listener.onLocationChanged(location);
		}
	}

	@Override
	synchronized public void requestUpdates(LocationListener req) {
		// TODO Auto-generated method stub
		listeners.add(req);
	}

	@Override
	synchronized public void stopUpdates(LocationListener req) {
		listeners.remove(req);
		
	}
	
	public void shuttingDown()
	{
		locThread.shuttingDown();
	}
	
	private class LocationThread extends Thread {
		private boolean notShuttingDown = true;
		int counter = 0;
		
		
		public LocationThread()
		{
			
			//Massive hack for locations
			Location loc0 = new Location("Other");
			loc0.setBearing(2f);
			loc0.setLatitude(38.843295);
			loc0.setLongitude(-77.288038);
			loc0.setSpeed(1.2f);
			locations[0] = loc0;
			
			Location loc1 = new Location("Other");
			loc1.setBearing(358.0f);
			loc1.setLatitude(38.841208);
			loc1.setLongitude(-77.271382);
			loc1.setSpeed(2.0f);
			locations[1] = loc1;
			
			Location loc2 = new Location("Other");
			loc2.setBearing(250f);
			loc2.setLatitude(38.843295);
			loc2.setLongitude(-77.289147);
			loc2.setSpeed(1.9f);
			locations[2] = loc2;
			
		}
		
		public void run() {
			while(notShuttingDown)
			{
				try {
					
					String test = BroadcastNetworkManager.macAddressSet;
					if(test != null && test.equals("evo"))
					{
						//123 and University Dr.
						lastLocation.setBearing(80.0f);
						lastLocation.setLatitude(38.8457435);
						lastLocation.setLongitude(-77.3054999);
						lastLocation.setSpeed(1.1f);

						//create movement to show change in values
//						lastLocation = locations[counter];
//						counter++;
//						if(counter > 2)
//						{
//							counter = 0;
//						}
					}
					else if(test != null && test.equals("nexus1"))
					{
						//123 and Braddock Rd.
						lastLocation.setBearing(180.0f);
						lastLocation.setLatitude(38.8327505);
						lastLocation.setLongitude(-77.339174);
						lastLocation.setSpeed(0.5f);						
					}
					else if(test != null && test.equals("hero"))
					{
						//123 and Zion Dr.
						lastLocation.setBearing(250.0f);
						lastLocation.setLatitude(38.8095662);
						lastLocation.setLongitude(-77.3095246);
						lastLocation.setSpeed(1.5f);
					}
					onLocationChanged(lastLocation);
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
		
		public void shuttingDown()
		{
			notShuttingDown = false;
		}
	}
}
