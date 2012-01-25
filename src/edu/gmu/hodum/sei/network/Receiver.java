package edu.gmu.hodum.sei.network;

import android.location.Location;

public interface Receiver {

	/**
	 * 
	 * This method will be called asynchronously by the network thread whenever a packet is received
	 * from the network.  Do as little work as possible in this method since taking too long will cause the
	 * network to stop reading data.
	 * 
	 * @param loc location that the packet was addressed to
	 * @param radius radius of the region around the center location the packet is addressed to
	 * @param buff data received in the packet.
	 */
	public void receiveMessage(Location center, double radius, Location originatingLocation, byte[] buff);
	
}
