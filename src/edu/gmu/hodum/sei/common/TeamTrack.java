package edu.gmu.hodum.sei.common;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class TeamTrack {

	@Element (required = false)
	private Observation observation;

	public TeamTrack(){
		
	}
	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	public Observation getObservation() {
		return observation;
	}
	
	
}
