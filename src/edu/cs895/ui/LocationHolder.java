package edu.cs895.ui;

import android.location.Location;
import android.location.LocationListener;

public interface LocationHolder {
	
	public Location getCurrentLocation();
	
	public void requestUpdates(LocationListener req);
	
	public void stopUpdates(LocationListener req);

}
