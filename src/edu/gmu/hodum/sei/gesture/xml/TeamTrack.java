package edu.gmu.hodum.sei.gesture.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class TeamTrack {

	@Element (required = false)
	private Observation observation;

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	public Observation getObservation() {
		return observation;
	}
	
	
}
