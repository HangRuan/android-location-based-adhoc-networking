package edu.gmu.hodum.sei.common;


public class Thing {
	
	public enum Type {PERSON, VEHICLE, LANDMARK, RESOURCE};
	
	private Long id;
	private Type type;
	private String description;
	private double friendliness;
	private double relevance;
	private double latitude;
	private double longitude;
	private double elevation;
	
	public Thing (){
		
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setFriendliness(double friendliness) {
		this.friendliness = friendliness;
	}
	public double getFriendliness() {
		return friendliness;
	}
	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}
	public double getRelevance() {
		return relevance;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	public double getElevation() {
		return elevation;
	}
}
