package edu.cs895.interest;

import java.text.DecimalFormat;

public class Point {
	private double x,y;
    DecimalFormat df = new DecimalFormat("##.########");

	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {return this.x;}
	public double getY() {return this.y;}
	
	@Override
	public String toString() {
		return "(" + df.format(x) + ", " + df.format(y) + ") ";
	}
}