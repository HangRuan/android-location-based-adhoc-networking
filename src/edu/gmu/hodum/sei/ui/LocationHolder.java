package edu.gmu.hodum.sei.ui;

import android.location.Location;
import android.location.LocationListener;

public interface LocationHolder {
	
	public Location getCurrentLocation();
	
	public void requestUpdates(LocationListener req);
	
	public void stopUpdates(LocationListener req);

}
