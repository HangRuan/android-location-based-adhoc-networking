package edu.gmu.hodum.sei.common;

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
		
		public SpaceTime(){
			
		}
		public Long getLatitude() {
			return latitude;
		}
		public void setLatitude(Long latitude) {
			this.latitude = latitude;
		}
		public Long getLongtitude() {
			return longtitude;
		}
		public void setLongtitude(Long longtitude) {
			this.longtitude = longtitude;
		}
		public Long getElevation() {
			return elevation;
		}
		public void setElevation(Long elevation) {
			this.elevation = elevation;
		}

	
}
