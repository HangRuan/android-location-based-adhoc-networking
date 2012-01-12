package edu.cs895.message;

import android.location.Location;

public class MessageBuffer {
	private byte[] buff;
	private Location origLoc, targetLoc;
	
	public byte[] getBuffer() { return this.buff; }
	public Location getOrigLoc() { return this.origLoc; }
	public Location getTargetLoc() { return this.targetLoc; }
	
	public void setBuffer(byte[] buff) { this.buff = buff; }
	public void setOrigLoc(Location origLoc){ this.origLoc = origLoc;}
	public void setTargetLoc(Location targetLoc){ this.targetLoc = targetLoc;}
}
