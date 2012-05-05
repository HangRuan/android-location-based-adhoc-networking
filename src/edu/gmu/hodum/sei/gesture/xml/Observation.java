package edu.gmu.hodum.sei.gesture.xml;

import java.util.Collection;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Observation {

	@Element
	private String dataType;
	
	//Need a time here and in the space time
	//The one here is the time when I made the onservation or update to the plan
	//The time in the Location is either when I saw them there or when I plan to be there
	
	@ElementList
	private Collection<SpaceTime> spaceTimes;
	
	@ElementList (required = false, name = "times")
	private Collection<Long> times;

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
	
	public Collection<Long> getTimes() {
		return times;
	}

	public void setTimes(Collection<Long> times) {
		this.times = times;
	}


	public int getFriendlyness() {
		return friendlyness;
	}


	public void setFriendlyness(int friendlyness) {
		this.friendlyness = friendlyness;
	}


	public void setSpacetime(Collection<SpaceTime> spacetimes) {
		this.spaceTimes = spacetimes;
	}


	public Collection<SpaceTime> getSpacetime() {
		return spaceTimes;
	}
	
}
