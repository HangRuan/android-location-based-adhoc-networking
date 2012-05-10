package edu.gmu.contextdb.utils;

import java.util.Collection;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


public class Observation {

	@Element
	private String dataType;
	
	//Need a time here and in the space time
	//The one here is the time when I made the onservation or update to the plan
	//The time in the Location is either when I saw them there or when I plan to be there
	
	@Element (required = true, name= "observation_type")
	private String observation_type;
	
	@Element (required=true, name="id")
	private Long id;
	
	@Element
	private Location spaceTime;
	
	@Element (required = false, name = "times")
	private Long time;

	@Element (required = false, name = "friendlyness")
	private int friendlyness;
	
	Observation(){
	}
	
	
	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}


	public int getFriendlyness() {
		return friendlyness;
	}


	public void setFriendlyness(int friendlyness) {
		this.friendlyness = friendlyness;
	}


	public void setLocatio(Location spacetime) {
		this.spaceTime = spacetime;
	}


	public Location getLocation() {
		return spaceTime;
	}


	public void setObservation_type(String observation_type) {
		this.observation_type = observation_type;
	}


	public String getObservation_type() {
		return observation_type;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getId() {
		return id;
	}
	
}
