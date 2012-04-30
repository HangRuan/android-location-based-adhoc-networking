package edu.gmu.hodum.sei.gesture.util;

import java.util.Collection;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class DataPayload {

	@Element
	private String dataType;
	
	@ElementList (required = false, name = "locations")
	private Collection<Long> locations;
	
	@ElementList (required = false, name = "times")
	private Collection<Long> times;

	@Element (required = false, name = "friendlyness")
	private int friendlyness;
	
	DataPayload(){
	}
	
	
	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public Collection<Long> getLocations() {
		return locations;
	}


	public void setLocations(Collection<Long> locations) {
		this.locations = locations;
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






	
	
	
}
