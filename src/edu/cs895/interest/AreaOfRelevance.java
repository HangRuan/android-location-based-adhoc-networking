package edu.cs895.interest;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import edu.cs895.LocationHolder;


public class AreaOfRelevance implements LocationListener {
	private static AreaOfRelevance INSTANCE = null;
	
	private static Grid grid = null;
	private static LocationHolder locHolder = null;
	
	public static AreaOfRelevance getInstance(LocationHolder locHolder) {
		if (INSTANCE == null) {
			INSTANCE = new AreaOfRelevance(locHolder);
			locHolder.requestUpdates(INSTANCE);
		}
		return INSTANCE;
	}
	
	private AreaOfRelevance(LocationHolder locHolder) {
		AreaOfRelevance.locHolder = locHolder;
		this.grid = Grid.getInstance();		
	}
	
	public double getRelevance(Location loc) {
		return grid.getRelevance(loc);
	}
	
	public double getQuickRelevance(Location loc) {
		float dist = loc.distanceTo(locHolder.getCurrentLocation());
		double relevance = (dist/200);
		double ret = 0.0;
		if(relevance < 10)
		{
			ret = (10-relevance) * 10.0;
		}
		int convert = (int)(ret*100);
		ret = ((double)convert/100.0);
		return ret;
	}
	
	@Override
	public void onLocationChanged(Location currPt) {
		grid.update(currPt);
	}
	
	@Override
	public void onProviderDisabled(String provider) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }
}
