package edu.cs895.message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.location.Location;

import edu.cs895.network.NetworkManager;
import edu.cs895.network.Receiver;


public class TransferQueue implements Receiver {
	private Queue<MessageBuffer> queue;
	private NetworkManager networkManager = null;
	
	public TransferQueue(){
		queue = new ConcurrentLinkedQueue<MessageBuffer>();
	}
	
	public boolean setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
		return true;
	}
	
	@Override
	public void receiveMessage(Location targetLocation,	Location originatingLocation, byte[] buff) {
		MessageBuffer msgBuff = new MessageBuffer();
		msgBuff.setOrigLoc(originatingLocation);
		msgBuff.setTargetLoc(targetLocation);
		msgBuff.setBuffer(buff);
		toAppMsg(msgBuff);
	}

	
	public boolean toAppMsg(MessageBuffer msgBuff) {
		return queue.offer(msgBuff);
	}
	
	public void toNetworkMsg(MessageBuffer msgBuff) {
		networkManager.getSender().sendMessage(msgBuff.getTargetLoc(), msgBuff.getBuffer());
	}
	
	public boolean hasNextEvent() {
		if (queue.size() > 0)
			return true;
		return false;
	}

	//returns null if no message found
	public MessageBuffer getNextEvent(){
		return queue.poll();
	}
	
	@Override
	public void receiveMessage(Location targetUpperLeft, Location targetLowerRight, Location originatingLocation, byte[] buff) {
		// NOT USED
	}

	@Override
	public void receiveMessage(Location center, double radius, Location originatingLocation, byte[] buff) {
		// NOT USED		
	}

}
