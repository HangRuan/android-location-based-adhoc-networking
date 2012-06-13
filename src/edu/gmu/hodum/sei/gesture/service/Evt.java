package edu.gmu.hodum.sei.gesture.service;

public class Evt {
	private final long time;
	private final SensorEvtType type;
	Evt(long time, SensorEvtType type){
		this.time = time;
		this.type = type;
	}
	public long getTime() {
		return time;
	}
	public SensorEvtType getType() {
		return type;
	}

}
