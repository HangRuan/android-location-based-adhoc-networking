package edu.cs895.interest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.util.Log;

public class Grid {
	private static Grid INSTANCE = null;
	private static final String TAG = "GRID";
    DecimalFormat df = new DecimalFormat("###.####");

	private List<Row> grid = null;
	private static final int NUM_ROWS = 6;
	private Location origin = null;
	
	private Double oneDegreeLatInMeters = null; //basically a constant with variance negligible for this project
	private Double oneDegreeLongInMeters = null;
	private static boolean isRunning;
	private Stat calcStat;
	
	private final double piDiv180 = Math.PI / 180.0; 
	
	private Grid(){
		grid = new ArrayList<Row>(NUM_ROWS);
		for (int count=0; count<NUM_ROWS; count++) {
			grid.add(count, new Row(count));
		}
		calcStat = new Stat();
	}
	
	public static Grid getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Grid();
			isRunning = false;
		}
		return INSTANCE;
	}

	private void setDistancePerDegree(double currLatitude) {
		double ratio = 1000000.0;
		
		oneDegreeLatInMeters = (11100.0) / ratio;
		if (oneDegreeLongInMeters == null) {
			this.oneDegreeLongInMeters = ((piDiv180) * Math.cos(piDiv180*currLatitude)* 6378137.0) / ratio;
			Log.d(TAG, "One Degree of Longitude at " + currLatitude + " is " + this.oneDegreeLongInMeters);
		}
		isRunning = true;
	}
	
	//Start of Cartesian conversions-------------------------------------------
	private double convertLatToY(double inLat) throws Exception {
		if (isRunning == false) {
			throw new Exception();
		}
		return (inLat - origin.getLatitude()) / oneDegreeLatInMeters;		
	}
	
	private double convertLongToX(double inLong) throws Exception {
		if (isRunning == false) {
			throw new Exception();
		}
		return (inLong - origin.getLongitude()) / oneDegreeLongInMeters;
	}
	
	public static double getDistanceFromOrigin(double x, double y) {
		return Math.sqrt((x*x) + (y*y));
	}
	
	public static double getDistanceFromPoint(double x1, double y1, double x2, double y2) {
		return Math.sqrt(((x1-x2)*(x1-x2)) + ((y1-y2)*(y1-y2)));
	}
	//End of Cartesian conversions----------------------------------------------
	
	//determine which row the latitude is in
	private Row getRowForLat(double yCoord) {
		int row = (int) Math.floor(yCoord);
		row +=3;
		if (row < 0 || row > 5) {
			return null;
		}
		Log.d(TAG, "Selected Row: " + row);
		return grid.get(row);
	}
	
	public void update(Location point) {
		//set the new lat and long for the origin (0,0)
		this.origin = point;
		
		//if this is the first location call then set the constants based on the first location received
		if (oneDegreeLongInMeters == null) {
			setDistancePerDegree(point.getLatitude());
		}
		
		//update the relevance values for each triangle based on our new speed and bearing
		calcStat.calcRelVal(origin.getSpeed(), origin.getBearing());
	}
	
	public double getRelevance(Location point) {
		Triangle t = isWhere(point);
		
		if (t == null) {
			Log.d(TAG, "Triangle is null!  What the hell...?!?!");
			return 0.0;
		}
		Double relVal = t.getRelevance();
		if (relVal == null) {
			Log.d(TAG, "Relevance is null..  What the hell...?!?!");
			return 0.0;
		}
		if (relVal > 100.0){
			return 100.0; //for values greater than 100%
		}
		return relVal;
	}
	
	private Triangle isWhere(Location point) {
		if (origin == null) {
			Log.d(TAG, "Origin from isWhere is null");
			return null;
		}
		
		double x=0, y=0; //dummy initialization
		try {
			x = (convertLongToX(point.getLongitude()));
			y = (convertLatToY(point.getLatitude()));
		} catch (Exception e) {
			Log.d(TAG, "SEVERE ERROR: Grid is not correctly initialized with an initial location");
		}

		Log.d(TAG, "Origin is at : (" + origin.getLatitude() + ", " + origin.getLongitude() + ")");
		Log.d(TAG, "Point is at : (" + point.getLatitude() + ", " + point.getLongitude() + ")");
		Log.d(TAG, "Looking for coordinate: (" + df.format(x) + ", " + df.format(y) + ")");
		
		Row r = getRowForLat(y);
		if (r == null) {
			Log.d(TAG, "Row is null!");
			return null; //does not exist in our grid so drop
		}
		
		for (Triangle t : r.getTriangles()) {
			if (t.isInside(x, y)) {
				calcStat.printValues();
				return t;
			}
		}
		return null;
	}
	
	private class Stat {
		void calcRelVal(double speed, double bearing) {
//			double speed = currLoc.getSpeed();
//			double bearing = currLoc.getBearing();
//			double bearingSlope = Math.tan(currLoc.getBearing());
//			double perpendicularSlope = Math.pow(bearingSlope, -1.0) * -1.0;
			
			//multiply speed by 10 to see how many meters they will travel in 10 seconds
			double futureDist = speed*10; //distance in meteres that will be traveled in next 10 seconds
			Point futurePt=null, tCenter;

//			Log.d(TAG, "=== Bearing: " + bearing + " ====");
//			Log.d(TAG, "=== Speed: " + speed + " ====");

			//get predicted distance from 10 seconds in future based on the currSpeed and currBearing
			futurePt = new Point( futureDist*Math.sin(bearing*piDiv180), futureDist*Math.cos(bearing*piDiv180) );
//			Log.d(TAG, "FuturePt: (" + futurePt.getX() + ", " + futurePt.getY() + ")");

			//calculate differences to use for relevance factor
			double x2 = futurePt.getX();
			double y2 = futurePt.getY();
			
			for (Row r : grid) {
				for (Triangle t : r.getTriangles()) {
					tCenter = t.getCenter();

					//write out numbers to the logs to determine if they reasonable
					//Log.d(TAG, "TriPt: (" + tCenter.getX() + ", " + tCenter.getY() + ") " + tDist + " meters from Origin");
					
					//get closest point of approach
					double x = ((tCenter.getY() * x2 * y2) + (x2 * x2 * tCenter.getX())) / ((y2*y2) + (x2*x2));
					double distVal = Triangle.calcDistributionVal(t.getDistToCenter()); //inflate to be more visible for demo
//					Log.d(TAG, "CPA is " + x + " and x2, y2 is (" + x2 + ", " + y2 + ")");
					
					if (x2==0 && y2==0) {
						x = 0;
					}					
					if ((x >= 0) && (x <= x2)){
						//Closest point of approach is on line, but then we move away again
						t.updateRelevance(distVal*2.0);
					}
					else if (Math.abs(x-x2) <= Math.abs(x)) {
						//getting closer to the triangle
						t.updateRelevance(distVal*1.5);
					}
					else {
						//moving further away from triangle
						t.updateRelevance(distVal);
					}
//					Log.d(TAG, "Relevance for this triangle is: " + t.getRelevance() + "%");
				}
			}
//			printValues();
		}
		
		public void printValues() {
			
			String buildString = "";
			for (int triRow=0; triRow<6; triRow++) {
				Row r = grid.get(triRow);
				buildString += "Row " + triRow + ": ";
				for (Triangle t : r.getTriangles()) {
					buildString += df.format(t.getRelevance()) + "  ";
				}
				Log.d(TAG, buildString);
				buildString = "";
			}				
		}
	}
}
