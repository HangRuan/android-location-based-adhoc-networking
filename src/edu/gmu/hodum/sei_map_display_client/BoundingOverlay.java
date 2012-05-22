package edu.gmu.hodum.sei_map_display_client;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class BoundingOverlay extends Overlay{

	final int MAX_POINT_CNT = 2;

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	//float[] x = new float[MAX_POINT_CNT];
	//float[] y = new float[MAX_POINT_CNT];
	boolean[] isTouch = new boolean[MAX_POINT_CNT];
	boolean stateDrawingBox = false; //indicates whether the box is being drawn
	GeoPoint[] points;
	Rect r;
	private MapDisplay mapDisplay;

	BoundingOverlay(MapDisplay mapDisplay){
		super();
		this.mapDisplay = mapDisplay;
		points = new GeoPoint[MAX_POINT_CNT];
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent, MapView mapView) {

		int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) 
				>> MotionEvent.ACTION_POINTER_ID_SHIFT);
		int pointerId = motionEvent.getPointerId(pointerIndex);
		int action = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
		int pointCnt = motionEvent.getPointerCount();

		if (pointCnt <= MAX_POINT_CNT){
			if (pointerIndex <= MAX_POINT_CNT - 1){

				for (int i = 0; i < pointCnt; i++) {
					int id = motionEvent.getPointerId(i);
					//x[id] = (int)motionEvent.getX(i);
					//y[id] = (int)motionEvent.getY(i);
					points[id] = mapView.getProjection().fromPixels( (int) motionEvent.getX(i), (int) motionEvent.getY(i));
				}

				
				//System.out.println("onTouchEvent, action: "+action+" GeoPoint: "+ p.getLatitudeE6()+","+p.getLongitudeE6());
				switch (action){
				case MotionEvent.ACTION_DOWN:
					System.out.println("ACTION_DOWN");
					isTouch[pointerId] = true;
					//point1 = p;

					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					System.out.println("ACTION_POINTER_DOWN");
					isTouch[pointerId] = true;

					//point2 = p;

					//TODO: upon the second touch, begin drawing the rectangle between the points
					stateDrawingBox = true;

					break;
				case MotionEvent.ACTION_MOVE:
					System.out.println("ACTION_MOVE; Pointer: "+pointerId);
					isTouch[pointerId] = true;

					/*update the appropriate GeoPoint
					if(pointerId == 0 ){
						point1 = p;
					}
					else if (pointerId == 1){
						point2 = p;
					}
					*/

					break;
				case MotionEvent.ACTION_UP:
					System.out.println("ACTION_UP");
					isTouch[pointerId] = false;
					stateDrawingBox = false;
					break;
				case MotionEvent.ACTION_POINTER_UP:
					System.out.println("ACTION_POINTER_UP");
					//This is the action for when a user lifts their finger

					//calculate the bounding box/ end the gesture
					GeoPoint LL = mapView.getProjection().fromPixels(r.left, r.bottom);
					GeoPoint UR =  mapView.getProjection().fromPixels(r.right, r.top);
					mapDisplay.displayThingsWithinBounds(LL.getLatitudeE6(), LL.getLongitudeE6(), UR.getLatitudeE6(), UR.getLongitudeE6());

					stateDrawingBox = false;
					isTouch[pointerId] = false;
					break;
				case MotionEvent.ACTION_CANCEL:
					isTouch[pointerId] = false;
					break;
				default:
					isTouch[pointerId] = false;
				}
				
				mapView.invalidate();
			}
		}
		return true;
	}

	public void draw (Canvas canvas, MapView mapView, boolean shadow){
		super.draw(canvas, mapView, shadow);

		if(stateDrawingBox){


			//convert saved GeoPoints to pixel points in order to draw the bounding box 
			Point drawPoint1 = mapView.getProjection().toPixels(points[0], null);
			Point drawPoint2 = mapView.getProjection().toPixels(points[1], null);

			System.out.println("Point1: "+drawPoint1.x+","+drawPoint1.y);
			System.out.println("Point2: "+drawPoint2.x+","+drawPoint2.y);
			//create Rectangle
			int left, top, right, bottom;

			if(drawPoint1.x<drawPoint2.x){
				left = drawPoint1.x;
				right = drawPoint2.x;
			}
			else{
				left = drawPoint2.x;
				right = drawPoint1.x;
			}
			if(drawPoint1.y < drawPoint2.y){
				top = drawPoint1.y;
				bottom = drawPoint2.y;
			}
			else{
				top = drawPoint2.y;
				bottom = drawPoint1.y;
			}
			r = new Rect(left,top,right,bottom);

			//setup Paint
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(5);
			paint.setColor(Color.RED);

			canvas.drawRect(r, paint);

		}
	}




}
