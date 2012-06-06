package edu.gmu.hodum.sei_map_display_client;

import java.util.Vector;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;

public class RouteOverlay extends Overlay {

	private Vector<Location> nodes;

	public RouteOverlay (Vector<Location> points)
	{
		nodes = (Vector<Location>)points.clone();

	}

	public void draw(Canvas canvas, MapView mapv, boolean shadow){
		super.draw(canvas, mapv, shadow);

		Paint mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(5);
		GeoPoint geoPoint1;
		Point p1;
		GeoPoint geoPoint2;
		Point p2;

		Path path;

		for(int i =0; i<nodes.size()-1; i++)
		{
			Location loc = nodes.elementAt(i);
			Location loc2 = nodes.elementAt(i+1);
			Double lat = new Double(loc.getLatitude());
			Double lng = new Double(loc.getLongitude());

			geoPoint1 = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			geoPoint2 = new GeoPoint((int)(loc2.getLatitude() *1E6), (int)(loc2.getLongitude() *1E6));

			p1 = new Point();
			p2 = new Point();

			path = new Path();

			Projection projection = mapv.getProjection();
			projection.toPixels(geoPoint1, p1);
			projection.toPixels(geoPoint2, p2);

			path.moveTo(p2.x, p2.y);
			path.lineTo(p1.x,p1.y);

			canvas.drawPath(path, mPaint);
		}

	}

}


/**
class WalkOverlay extends Overlay{  


 **/