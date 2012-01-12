package edu.cs895.network;

import android.location.Location;

public interface NetworkManager {
	
	public boolean registerReceiver(Receiver receiver);
	
	public boolean removeReceiver(Receiver receiver);
	
	public void startNetwork();
	
	public Sender getSender();
	
}
