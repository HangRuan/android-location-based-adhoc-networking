package edu.cs895.interest;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Row {
	private List<Triangle> triList = null;
	private static final int NUM_TRIANGLES = 11;
	private int rowNum;
	
	Row(int rowNumInInt) {
		triList = new ArrayList<Triangle>(NUM_TRIANGLES);
		this.rowNum = rowNumInInt;
		
		float halfStep = (float)0.5, fullStep = (float)1.0;
		Point a,b,c;
		
		float xCoord = (float) -3.0, yCoord = (float) ((float)(rowNum) - 3.0);
		float lowerY = yCoord;
		float upperY = (float)(yCoord + fullStep);

		if (rowNumInInt % 2 == 0) {
			float leftX = xCoord;
			float rightX = xCoord + halfStep;
			
			for (int count=0; count < NUM_TRIANGLES-1; count+=2) {
				a = new Point(leftX, upperY);
				b = new Point(leftX + fullStep, upperY);
				c = new Point(rightX, lowerY);
				triList.add(count, new Triangle(a, b, c));
				leftX += fullStep;
				rightX += fullStep;
				a = c;
				//b = b; remains the same
				c = new Point(rightX, lowerY);
				triList.add(count + 1, new Triangle(a, b, c));
			}

			a = new Point(leftX, upperY);
			b = new Point(leftX + fullStep, upperY);
			c = new Point(rightX, lowerY);
			triList.add(new Triangle(a, b, c));
		}
		else {
			float leftX = xCoord;
			float rightX = xCoord + halfStep;
			
			a = new Point(leftX, lowerY);
			b = new Point(rightX, upperY);
			c = new Point(leftX + fullStep, lowerY);
			triList.add(new Triangle(a, b, c));
			
			for (int count=0; count < NUM_TRIANGLES-1; count+=2) {
				leftX += halfStep;
				rightX += halfStep;
				a = new Point(leftX, upperY);
				b = new Point(leftX + fullStep, upperY);
				c = new Point(rightX, lowerY);
				triList.add(count, new Triangle(a, b, c));
				leftX += halfStep;
				rightX += halfStep;
				a = c;
				//b = b; remains the same
				c = new Point(rightX + halfStep, lowerY);
				triList.add(count + 1, new Triangle(a, b, c));
			}
		}
	}

	List<Triangle> getTriangles() {
		return triList;
	}

	@Override
	public String toString(){
		if (triList == null) {
			Log.d("ROW:", "SEVERE ERROR - No triangles in row " + this.rowNum);
			return "";
		}
		if (triList.size() != NUM_TRIANGLES) {
			Log.d("ROW:", "SEVERE ERROR - Number of triangles in row in " + triList.size());
			return ""; //
		}
		
		String myString = "";
		for (int count=0; count < NUM_TRIANGLES; count++) {
			myString += triList.get(count).toString();
		}
		myString += "\n";
		return myString;
	}
}
