package edu.gmu.hodum.sei.common;

import java.util.Vector;

import android.location.Location;

public class Objective {
	
	public enum ObjectiveType {HUMANITARAN, PATROL};
	
	private Long id;
	
	private String description;
	
	private ObjectiveType type;
	
	Vector<Location> locations = new Vector<Location>();

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setType(ObjectiveType type) {
		this.type = type;
	}

	public ObjectiveType getType() {
		return type;
	}

	public Vector<Location> getLocations()
	{
		return locations;
	}
	
	public void addLocation(Location loc)
	{
		locations.add(loc);
	}
	
	
}
