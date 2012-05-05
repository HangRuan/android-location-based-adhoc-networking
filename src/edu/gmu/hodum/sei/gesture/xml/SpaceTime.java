package edu.gmu.hodum.sei.gesture.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class SpaceTime {

		@Element (required = false)
		private Long latitude;
		@Element (required = false)
		private Long longtitude;
		@Element (required = false)
		private Long elevation;

	
}
