package edu.gmu.hodum.sei.network;


public interface NetworkManager {
	
	public boolean registerReceiver(Receiver receiver);
	
	public boolean removeReceiver(Receiver receiver);
	
	public void beginReceivingPackets();
	
	public Sender getSender();
	
	public void initNetwork(String channel);
	
	public String getIPAddress();
	
}
