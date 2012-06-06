package edu.gmu.hodum.sei.geometry.utils;

public class Point2D {
	
	private double x;
	private double y;
	
	
	public Point2D(double _x, double _y)
	{
		setX(_x);
		setY(_y);
	}


	public void setX(double x) {
		this.x = x;
	}


	public double getX() {
		return x;
	}


	public void setY(double y) {
		this.y = y;
	}


	public double getY() {
		return y;
	}

	public double distance(Point2D p2)
	{
		return Math.sqrt((p2.x-x)*(p2.x-x) + (p2.y-y)*(p2.y-y));
	}
}
