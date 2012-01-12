package edu.cs895.interest;

import edu.cs895.message.Event;

public interface InterestReceiver {
	
	public void receivePacket(Event evt);

}
