package edu.gmu.contextdb.utils;

import org.simpleframework.xml.Element;

public class Location {

		@Element (required = false)
		private Long latitude;
		@Element (required = false)
		private Long longtitude;
		@Element (required = false)
		private Long elevation;
		public void setLatitude(Long latitude) {
			this.latitude = latitude;
		}
		public Long getLatitude() {
			return latitude;
		}
		public void setLongtitude(Long longtitude) {
			this.longtitude = longtitude;
		}
		public Long getLongtitude() {
			return longtitude;
		}
		public void setElevation(Long elevation) {
			this.elevation = elevation;
		}
		public Long getElevation() {
			return elevation;
		}

	
}
