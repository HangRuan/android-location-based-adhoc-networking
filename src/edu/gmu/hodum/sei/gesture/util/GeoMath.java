package edu.gmu.hodum.sei.gesture.util;

import android.location.Location;

//used to perform "geography math"

public class GeoMath {

	/*
	public static Location getLocationFromStartAndDistance(Location start, float distance){


		Float floatBearing = new Float(start.getBearing());

		double radianBearing = Math.toRadians(floatBearing.doubleValue());
		double lat1 = Math.toRadians(start.getLatitude());
		double lon1 = Math.toRadians(start.getLongitude());

		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(distance) + Math.cos(lat1)*Math.sin(distance)*Math.cos(radianBearing) );
		double a = Math.atan2(Math.sin(radianBearing)*Math.sin(distance)*Math.cos(lat1), Math.cos(distance)-Math.sin(lat1)*Math.sin(lat2));
		System.out.println("a = " +  a);
		double lon2 = lon1 + a;

		lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;

		System.out.println("New Latitude = "+Math.toDegrees(lat2)+", Longitude = "+Math.toDegrees(lon2));

		Location dest = new Location (start);
		dest.setLatitude(lat2);
		dest.setLongitude(lon2);
		return dest;

	}
	 */
	
	/*
	public static Location getLocationFromStartBearingAndDistance(Location start, float bearing, float distance){

		Float objectFloat = new Float(bearing);
		double radianBearing = Math.toRadians(objectFloat.doubleValue());
		double lat1 = Math.toRadians(start.getLatitude());
		double lon1 = Math.toRadians(start.getLongitude());

		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(distance) + Math.cos(lat1)*Math.sin(distance)*Math.cos(radianBearing) );
		double a = Math.atan2(Math.sin(radianBearing)*Math.sin(distance)*Math.cos(lat1), Math.cos(distance)-Math.sin(lat1)*Math.sin(lat2));
		System.out.println("a = " +  a);
		double lon2 = lon1 + a;

		lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;

		System.out.println("Latitude = "+Math.toDegrees(lat2)+"\nLongitude = "+Math.toDegrees(lon2));

		Location dest = new Location (start);
		dest.setLatitude(lat2);
		dest.setLongitude(lon2);
		return dest;

	}
	*/
	
	public static Location getLocationFromStartBearingAndDistance2(Location location, float angle, float distance){

		double lat1 = location.getLatitude() * Math.PI / 180;
		double lon1 = location.getLongitude()* Math.PI / 180;
		double bearing = angle *Math.PI / 180;
		double R = 6371;
		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / R) + Math.cos(lat1) *Math.sin(distance / R) * Math.cos(bearing));
		double lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(distance / R) *Math.cos(lat1), Math.cos(distance / R) - Math.sin(lat1) *Math.sin(lat2));
		lat2 = lat2 / Math.PI * 180;
		lon2 = lon2 / Math.PI * 180;
		
		Location loc = new Location (location);
		loc.setLatitude(lat2); 
		loc.setLongitude(lon2);
		return loc;

	}
	
	

}


