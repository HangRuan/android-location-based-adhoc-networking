package edu.cs895.network;

import android.location.Location;

public interface Sender {
	
	public void sendMessage(Location center, double radius,  byte[] buff);
}
