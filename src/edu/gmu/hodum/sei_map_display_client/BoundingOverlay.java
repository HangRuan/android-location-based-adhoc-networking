package edu.gmu.hodum.sei_map_display_client;

import android.graphics.Paint;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class BoundingOverlay extends Overlay{

	final int MAX_POINT_CNT = 2;

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	float[] x = new float[MAX_POINT_CNT];
	float[] y = new float[MAX_POINT_CNT];
	boolean[] isTouch = new boolean[MAX_POINT_CNT];

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent, MapView mapView) {
		// TODO Auto-generated method stub

		int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) 
				>> MotionEvent.ACTION_POINTER_ID_SHIFT);
		int pointerId = motionEvent.getPointerId(pointerIndex);
		int action = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
		int pointCnt = motionEvent.getPointerCount();

		if (pointCnt <= MAX_POINT_CNT){
			if (pointerIndex <= MAX_POINT_CNT - 1){

				for (int i = 0; i < pointCnt; i++) {
					int id = motionEvent.getPointerId(i);
					x[id] = (int)motionEvent.getX(i);
					y[id] = (int)motionEvent.getY(i);
				}

				switch (action){
				case MotionEvent.ACTION_DOWN:
					isTouch[pointerId] = true;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					isTouch[pointerId] = true;
					break;
				case MotionEvent.ACTION_MOVE:
					isTouch[pointerId] = true;
					break;
				case MotionEvent.ACTION_UP:
					isTouch[pointerId] = false;
					break;
				case MotionEvent.ACTION_POINTER_UP:
					isTouch[pointerId] = false;
					break;
				case MotionEvent.ACTION_CANCEL:
					isTouch[pointerId] = false;
					break;
				default:
					isTouch[pointerId] = false;
				}
			}
		}


		//TODO: Code for getting GeoPoints

		//TODO: Code for drawing

		return true;
	}

}
