package edu.cs895.interest;

import android.util.Log;

public class Triangle {
	private Point a,b,c, center;
	private double distToCenter= 0.0;
	private double distVal = 0.0; //normal distribution value based on distance from center
	private double relVal = 0.0;
	private static String TAG = "TRIANGLE";
	
	public Triangle(Point a, Point b, Point c) {
		this.a = a;
		this.b = b;
		this.c = c;
		
		//get center of triangle for calculations
		double x = (a.getX() + b.getX() + c.getX()) / 3.0;
		double y = (a.getY() + b.getY() + c.getY()) / 3.0;		
		center = new Point(x, y);
//		Log.d(TAG, "Center point for triangle is: " + x + ", " + y);
		distToCenter = Math.sqrt((center.getX()*center.getX()) + (center.getY()*center.getY()));
//		Log.d(TAG, "Distance to Origin is: " + distToCenter);
		distVal= Triangle.calcDistributionVal(distToCenter);
//		Log.d(TAG, "Distribution Value = " + distVal);
	}
	
	public Point getPointA() { return this.a; }
	public Point getPointB() { return this.b; }
	public Point getPointC() { return this.c; }
	public Point getCenter() { return this.center; }
	public double getDistVal() { return this.distVal; }
	public double getDistToCenter() { return this.distToCenter; }
	
	public String toString(){
		return "={" + a.toString() + ", " + b.toString() + ", " + c.toString() + "}=\n";
	}
	
	//basic calculation for cumulative relevance
	public double getRelevance() {
		return (this.relVal + this.distVal) * 100.0; 
	}

	//should only be called by the grid
	public void updateRelevance(double relevance) {
		if (relevance > 1.0){
			relevance = 1.0;
		}
//		Log.d(TAG, "Relevance updated to: " + relevance);
		this.relVal = relevance;
	}
	
	public boolean isInside(double x, double y) {
		Point p = new Point(x,y);
		double areaDiff = getArea(a,b,c) - (getArea(a, b, p) + getArea(a,p,c) + getArea(p, b, c));
		if (Math.abs(areaDiff) < .0000000001) { //account for floating point error
			Log.d(TAG, "INSIDE TRIANGLE: " + a.toString() + ", " + b.toString() + ", "+ c.toString());
			Log.d(TAG, "x, y is: " + p.toString());
			return true;
		}
		Log.d(TAG, "NOT in " + a.toString() + ", " + b.toString() + ", "+ c.toString());
		Log.d(TAG, "x, y is: " + p.toString());		
		return false;
	}
	
	private double getArea(Point d, Point e, Point f) {
		double area = Math.abs(d.getX()*e.getY() + e.getX()*f.getY() + f.getX()*d.getY() - d.getX()*f.getY() - f.getX()*e.getY() - e.getX()*d.getY())/2.0;
		Log.d(TAG, "Area is: " + area);
		return area;
	}
	
	public static double calcDistributionVal(double distFromOrigin) {		
		double phi = Math.exp(-distFromOrigin*distFromOrigin / 2) / Math.sqrt(2 * Math.PI);
//		Log.d(TAG, "Gaussian is; " + phi);
		
		return phi;
	}

}
