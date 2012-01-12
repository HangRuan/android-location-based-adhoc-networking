package edu.cs895.message;

import java.sql.Timestamp;
import java.util.Calendar;

import android.location.Location;

public class Event {
	private Location targetLoc;
	private Location origLoc;
	
	private EventType eventType;
	private Timestamp timestamp;
	private String msgId;		

	
	//IN: A MessageBuffer received from the network
	//OUT: returns a non-modifiable Event object that contains all information from the MessageBuffer
	//SIDE EFFECTS: Creation of the message buffer
	private Event() {
	}
	
	//IN: All values needed to create a new event
	//OUT: A new event with all values requested by the caller
	//SIDE EFFECTS: Creation of a new event
	public static Event getInstance(EventType eventType, Timestamp timestamp, String msgId, 
			Location targetLoc, Location origLoc) {

		Event event = new Event();
		event.eventType = eventType;
		event.timestamp = timestamp;
		event.msgId = msgId;
		event.targetLoc = targetLoc;
		event.origLoc = origLoc;
		
		return event;
	}
		
	//IN: this
	//OUT: If the object is populated correctly and completely
	//SIDE EFFECTS: none
	public boolean isValid() {
		//add code in here to check for nulls and other problems with the field values
		return true;
	}
	
	//getters
	public EventType getEventType() { return this.eventType; 	}
	public Timestamp getTimestamp() { return this.timestamp; 	}
	public String getMsgId() 		{ return this.msgId; 		}
	public Location getTargetLoc()	{ return this.targetLoc; 	}
	public Location getOrigLoc()	{ return this.origLoc; 		}
	
	@Override
	public String toString() {
		String buildString = "Msg Id is: " + this.msgId + "\n";
		buildString += "Event Type is: " + this.eventType.getText() + "\n";
		buildString += "Timestamp is: " + this.timestamp.toString() + "\n";
		buildString += "Target Loc is: " + this.targetLoc.getLatitude() + ", " + this.targetLoc.getLongitude() + "\n";	
		buildString += "Origniating Loc is: " + this.origLoc.getLatitude() + ", " + this.origLoc.getLongitude() + "\n";			
		return buildString;
	}
}
