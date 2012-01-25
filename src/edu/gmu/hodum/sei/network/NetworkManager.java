package edu.gmu.hodum.sei.network;


public interface NetworkManager {
	
	public boolean registerReceiver(Receiver receiver);
	
	public boolean removeReceiver(Receiver receiver);
	
	public void startNetwork();
	
	public Sender getSender();
	
}
