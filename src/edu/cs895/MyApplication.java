package edu.cs895;



import edu.cs895.message.TransferQueue;
import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

public class MyApplication extends Application {

	private LocationManager locMgr;
	private ApplicationLocationListener locHolder;
	private TransferQueue transferQ;

	
	@Override
	public void onCreate()
	{
		super.onCreate();
		locMgr = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		//LocationManager.GPS_PROVIDER
		
		//locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L,10f, locHolder);
		
		
	}

	public void initInterestEngine()
	{
		transferQ = new TransferQueue();
		
		locHolder = new ApplicationLocationListener();
	}

	@Override
	public void onTerminate()
	{
		if(locHolder!= null)
		{
			locMgr.removeUpdates(locHolder);
		}
		locHolder = null;
		transferQ = null;
	}
	
	public LocationHolder getLocationHolder()
	{
		return locHolder;
	}
	
	public TransferQueue getTransferQ() {
		return transferQ;
	}
}
