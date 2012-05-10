package edu.gmu.contextdb.utils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

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
