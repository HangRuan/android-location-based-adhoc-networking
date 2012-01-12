package edu.cs895.network;

import android.location.Location;

public interface Sender {

	public void sendMessage(Location loc, byte[] buff);
	
	public void sendMessage(Location bottomLeft, Location topRight,  byte[] buff);
	
	public void sendMessage(Location center, double radius,  byte[] buff);
}
