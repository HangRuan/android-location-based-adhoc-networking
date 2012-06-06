package edu.gmu.hodum.sei_map_display_client;

import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.gmu.hodum.ContentDatabaseAPI;
import edu.gmu.hodum.sei.common.Objective;
import edu.gmu.hodum.sei.common.Thing;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;

import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class MapDisplay extends MapActivity {

	public static final String NEW_DATA = "edu.gmu.hodum.NEW_DATA_IN_DATABASE";
	public static final String INITIALIZE_NETWORK = "edu.gmu.hodum.INITIALIZE_NETWORK";
	
	private static int E6 = 10000000;
	ContentDatabaseAPI api;
	MapView mapView;
	BoundingOverlay boundingOverlay;
	private MyNewDataReceiver receiver;
	private double lastLatLL=-1; 
	private double lastLongLL=-1;
	private double lastLatUR=-1;
	private double lastLongUR=-1;
	private SensorManager mSensorManager;
    private RotateView mRotateView;
    private Objective objective;
    private Drawable drawableGreenPerson;
    private Drawable drawableRedPerson;
    private Drawable drawableBluePerson;
    private Drawable drawableGreenVehicle;
    private Drawable drawableBlueVehicle;
    private Drawable drawableRedVehicle;
    private Drawable drawableResource;
    private Drawable drawableLandmark;
    
	final int MAX_POINT_CNT = 3;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Bitmap immutableBitmap = BitmapFactory.decodeResource( getResources(), R.drawable.androidmarker );

		drawableGreenPerson = this.getResources().getDrawable(R.drawable.androidmarker);
		drawableBluePerson = this.getResources().getDrawable(R.drawable.blue_androidmarker);
		drawableRedPerson = this.getResources().getDrawable(R.drawable.red_androidmarker);
		Bitmap mutableBitmap = immutableBitmap.copy( Bitmap.Config.ARGB_8888, true );
		immutableBitmap.recycle();
		immutableBitmap = null;
		Drawable d1 = new BitmapDrawable( mutableBitmap );
		d1.setColorFilter( 0xff00ff00, PorterDuff.Mode.SRC_ATOP );
		
		drawableGreenVehicle = this.getResources().getDrawable(R.drawable.green_auto_icon);
		drawableBlueVehicle = this.getResources().getDrawable(R.drawable.blue_auto_icon);
		drawableRedVehicle = this.getResources().getDrawable(R.drawable.red_auto_icon);
		drawableResource = this.getResources().getDrawable(R.drawable.sym_def_app_icon);
		drawableLandmark = d1;
		
		Intent broadcastIntent = new Intent(INITIALIZE_NETWORK);
		broadcastIntent.putExtra("channel", "8");
		this.sendBroadcast(broadcastIntent);
		
//		drawableLandmark = this.getResources().getDrawable(R.drawable.btn_radio_on_selected);
//		 mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//	        mRotateView = new RotateView(this);
//	        mapView = new MapView(this, "05g0RFzyrsupQODNRFXrkv6QdxU2QGFnXYpAN8w");
//	        mRotateView.addView(mapView);
//	      LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
//	      mainLayout.addView(mRotateView);  
		api = new ContentDatabaseAPI(this);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		//mapView.setClickable(true);

		Button btnDisplayItems = (Button)findViewById(R.id.btn_display_items);
		btnDisplayItems.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				boundingOverlay = new BoundingOverlay(MapDisplay.this);
				List<Overlay> mapOverlays = mapView.getOverlays();
				mapOverlays.add(boundingOverlay);

			}

		});

		objective = api.getPatrolObjective();

		addObjectiveOverlay();
		
		
		receiver = new MyNewDataReceiver();
		receiver.registerHandler(newDataHandler);
		IntentFilter filter1 = new IntentFilter(NEW_DATA);
		registerReceiver(receiver,filter1);

	}

	@Override
    protected void onResume() {
        super.onResume();
//        mSensorManager.registerListener(mRotateView,
//                SensorManager.SENSOR_ORIENTATION,
//                SensorManager.SENSOR_DELAY_UI);
       /// mMyLocationOverlay.enableMyLocation();
    }
	
	private void addObjectiveOverlay()
	{
		RouteOverlay overlay = new RouteOverlay(objective.getLocations());
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add(overlay);
	}

    @Override
    protected void onStop() {
//        mSensorManager.unregisterListener(mRotateView);
      //  mMyLocationOverlay.disableMyLocation();
        super.onStop();
    }
    
	synchronized public void displayThingsWithinBounds(double latLL, double longLL, double latUR, double longUR){
//		System.out.println("Bounds: " +latLL+","+longLL+","+latUR+","+longUR);
		lastLatLL = latLL;
		lastLongLL = longLL;
		lastLatUR = latUR;
		lastLongUR = longUR;
		Vector<Thing> things = getThingsWithinBounds(latLL /1E6, longLL/1E6, latUR/1E6, longUR/1E6);

		System.out.println("The Vector of Things has "+things.size() +" Thing(s) in it.");

		List<Overlay> mapOverlays = mapView.getOverlays();
		
		
		float[] array = 
			new float[] 
			          {1.0f,0,0,0,0,
				0,0,0,0,0,
				0,0,0,0,0,
				0,0,0,0,0};
				
		
		ColorMatrix matrix=new ColorMatrix(array);
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		
		int red = Color.parseColor("#FF0000");
		Mode mMode = Mode.SRC_ATOP;
		
		//drawablePerson.setColorFilter(0xffff0000, Mode.SCREEN);
		//drawablePerson.setAlpha(255);
	
		

		SimpleItemizedOverlay personGreenOverlay = new SimpleItemizedOverlay(drawableGreenPerson, this);
		SimpleItemizedOverlay personBlueOverlay = new SimpleItemizedOverlay(drawableBluePerson, this);
		SimpleItemizedOverlay personRedOverlay = new SimpleItemizedOverlay(drawableRedPerson, this);
		SimpleItemizedOverlay vehicleGreenOverlay = new SimpleItemizedOverlay(drawableGreenVehicle, this);
		SimpleItemizedOverlay vehicleBlueOverlay = new SimpleItemizedOverlay(drawableBlueVehicle, this);
		SimpleItemizedOverlay vehicleRedOverlay = new SimpleItemizedOverlay(drawableRedVehicle, this);
		SimpleItemizedOverlay resourceOverlay = new SimpleItemizedOverlay(drawableResource, this);
		SimpleItemizedOverlay landmarkOverlay = new SimpleItemizedOverlay(drawableLandmark, this);

		OverlayItem item;
		GeoPoint point;
		double lat;
		double lon;
		for(Thing thing : things){
			lat = thing.getLatitude();
			lon = thing.getLongitude();			
			point = new GeoPoint( (int)(thing.getLatitude() * 1E6),  (int)(thing.getLongitude() * 1E6));
			switch(thing.getType()){

			case PERSON:
				item = new OverlayItem(point, thing.getDescription(), "I'm at: "+lat+","+lon);
				if(thing.getRelevance()<200.0)
				{
					personRedOverlay.addOverlay(item);
				}
				else if(thing.getRelevance() <1000.0)
				{
					personBlueOverlay.addOverlay(item);
				}
				else
				{
					personGreenOverlay.addOverlay(item);
				}
				break;
			case VEHICLE:
				item = new OverlayItem(point, thing.getDescription(), "I'm at: "+lat+","+lon);
				if(thing.getRelevance()<200.0)
				{
					vehicleRedOverlay.addOverlay(item);
				}
				else if(thing.getRelevance() <1000.0)
				{
					vehicleBlueOverlay.addOverlay(item);
				}
				else
				{
					vehicleGreenOverlay.addOverlay(item);
				}
				
				break;
			case RESOURCE:
				item = new OverlayItem(point, thing.getDescription(), "I'm at: "+lat+","+lon);
				resourceOverlay.addOverlay(item);
				break;
			case LANDMARK:
				item = new OverlayItem(point, thing.getDescription(), "I'm at: "+lat+","+lon);
				landmarkOverlay.addOverlay(item);
				break;
			}


		}
		mapOverlays.clear();

		//empty overlays will crash mapview, so check if empty

		if(personGreenOverlay.size()>0){
			mapOverlays.add(personGreenOverlay);
		}
		if(personBlueOverlay.size()>0){
			mapOverlays.add(personBlueOverlay);
		}
		if(personRedOverlay.size()>0){
			mapOverlays.add(personRedOverlay);
		}
		if(vehicleGreenOverlay.size()>0){
			mapOverlays.add(vehicleGreenOverlay);
		}
		if(vehicleBlueOverlay.size()>0){
			mapOverlays.add(vehicleBlueOverlay);
		}
		if(vehicleRedOverlay.size()>0){
			mapOverlays.add(vehicleRedOverlay);
		}
		if(resourceOverlay.size()>0){
			mapOverlays.add(resourceOverlay);
		}
		if(landmarkOverlay.size()>0){
			mapOverlays.add(landmarkOverlay);
		}

		if(boundingOverlay != null){
			mapOverlays.remove(boundingOverlay);
			boundingOverlay = null;
		}
		addObjectiveOverlay();
		mapView.invalidate();
	}

	public Vector<Thing> getThingsWithinBounds(double latLL, double longLL, double latUR, double longUR){
		return api.getThings(latLL, longLL, latUR, longUR);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private Handler newDataHandler = new Handler() { 
		/* (non-Javadoc) 
		 * @see android.os.Handler#handleMessage(android.os.Message) 
		 */ 
		@Override 
		public void handleMessage(Message msg) { 
			if(lastLatLL !=-1)
			{
				runOnUiThread(new Runnable()
				{
					public void run() {
						MapDisplay.this.displayThingsWithinBounds(MapDisplay.this.lastLatLL,
								MapDisplay.this.lastLongLL, MapDisplay.this.lastLatUR, 
								MapDisplay.this.lastLongUR);
					}
				});
				
			}
			super.handleMessage(msg); 

		}
	};
	
	
	
	private class RotateView extends ViewGroup implements SensorListener {
        private static final float SQ2 = 1.414213562373095f;
        private final SmoothCanvas mCanvas = new SmoothCanvas();
        private float mHeading = 0;

        public RotateView(Context context) {
            super(context);
        }

        public void onSensorChanged(int sensor, float[] values) {
            //Log.d(TAG, "x: " + values[0] + "y: " + values[1] + "z: " + values[2]);
            synchronized (this) {
                mHeading = values[0];
                invalidate();
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(-mHeading, getWidth() * 0.5f, getHeight() * 0.5f);
            mCanvas.delegate = canvas;
            super.dispatchDraw(mCanvas);
            canvas.restore();
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            int sizeSpec;
            if (w > h) {
                sizeSpec = MeasureSpec.makeMeasureSpec((int) (w * SQ2), MeasureSpec.EXACTLY);
            } else {
                sizeSpec = MeasureSpec.makeMeasureSpec((int) (h * SQ2), MeasureSpec.EXACTLY);
            }
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).measure(sizeSpec, sizeSpec);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            // TODO: rotate events too
            return super.dispatchTouchEvent(ev);
        }

        public void onAccuracyChanged(int sensor, int accuracy) {
            // TODO Auto-generated method stub
            
        }
    }
	
	
	
	static final class SmoothCanvas extends Canvas {
        Canvas delegate;

        private final Paint mSmooth = new Paint(Paint.FILTER_BITMAP_FLAG);

        public void setBitmap(Bitmap bitmap) {
            delegate.setBitmap(bitmap);
        }

        public void setViewport(int width, int height) {
            delegate.setViewport(width, height);
        }

        public boolean isOpaque() {
            return delegate.isOpaque();
        }

        public int getWidth() {
            return delegate.getWidth();
        }

        public int getHeight() {
            return delegate.getHeight();
        }

        public int save() {
            return delegate.save();
        }

        public int save(int saveFlags) {
            return delegate.save(saveFlags);
        }

        public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
            return delegate.saveLayer(bounds, paint, saveFlags);
        }

        public int saveLayer(float left, float top, float right, float
                bottom, Paint paint,
                int saveFlags) {
            return delegate.saveLayer(left, top, right, bottom, paint,
                    saveFlags);
        }

        public int saveLayerAlpha(RectF bounds, int alpha, int saveFlags) {
            return delegate.saveLayerAlpha(bounds, alpha, saveFlags);
        }

        public int saveLayerAlpha(float left, float top, float right,
                float bottom, int alpha,
                int saveFlags) {
            return delegate.saveLayerAlpha(left, top, right, bottom,
                    alpha, saveFlags);
        }

        public void restore() {
            delegate.restore();
        }

        public int getSaveCount() {
            return delegate.getSaveCount();
        }

        public void restoreToCount(int saveCount) {
            delegate.restoreToCount(saveCount);
        }

        public void translate(float dx, float dy) {
            delegate.translate(dx, dy);
        }

        public void scale(float sx, float sy) {
            delegate.scale(sx, sy);
        }

        public void rotate(float degrees) {
            delegate.rotate(degrees);
        }

        public void skew(float sx, float sy) {
            delegate.skew(sx, sy);
        }

        public void concat(Matrix matrix) {
            delegate.concat(matrix);
        }

        public void setMatrix(Matrix matrix) {
            delegate.setMatrix(matrix);
        }

        public void getMatrix(Matrix ctm) {
            delegate.getMatrix(ctm);
        }

        public boolean clipRect(RectF rect, Region.Op op) {
            return delegate.clipRect(rect, op);
        }

        public boolean clipRect(Rect rect, Region.Op op) {
            return delegate.clipRect(rect, op);
        }

        public boolean clipRect(RectF rect) {
            return delegate.clipRect(rect);
        }

        public boolean clipRect(Rect rect) {
            return delegate.clipRect(rect);
        }

        public boolean clipRect(float left, float top, float right,
                float bottom, Region.Op op) {
            return delegate.clipRect(left, top, right, bottom, op);
        }

        public boolean clipRect(float left, float top, float right,
                float bottom) {
            return delegate.clipRect(left, top, right, bottom);
        }

        public boolean clipRect(int left, int top, int right, int bottom) {
            return delegate.clipRect(left, top, right, bottom);
        }

        public boolean clipPath(Path path, Region.Op op) {
            return delegate.clipPath(path, op);
        }

        public boolean clipPath(Path path) {
            return delegate.clipPath(path);
        }

        public boolean clipRegion(Region region, Region.Op op) {
            return delegate.clipRegion(region, op);
        }

        public boolean clipRegion(Region region) {
            return delegate.clipRegion(region);
        }

        public DrawFilter getDrawFilter() {
            return delegate.getDrawFilter();
        }

        public void setDrawFilter(DrawFilter filter) {
            delegate.setDrawFilter(filter);
        }

        public GL getGL() {
            return delegate.getGL();
        }

        public boolean quickReject(RectF rect, EdgeType type) {
            return delegate.quickReject(rect, type);
        }

        public boolean quickReject(Path path, EdgeType type) {
            return delegate.quickReject(path, type);
        }

        public boolean quickReject(float left, float top, float right,
                float bottom,
                EdgeType type) {
            return delegate.quickReject(left, top, right, bottom, type);
        }

        public boolean getClipBounds(Rect bounds) {
            return delegate.getClipBounds(bounds);
        }

        public void drawRGB(int r, int g, int b) {
            delegate.drawRGB(r, g, b);
        }

        public void drawARGB(int a, int r, int g, int b) {
            delegate.drawARGB(a, r, g, b);
        }

        public void drawColor(int color) {
            delegate.drawColor(color);
        }

        public void drawColor(int color, PorterDuff.Mode mode) {
            delegate.drawColor(color, mode);
        }

        public void drawPaint(Paint paint) {
            delegate.drawPaint(paint);
        }

        public void drawPoints(float[] pts, int offset, int count,
                Paint paint) {
            delegate.drawPoints(pts, offset, count, paint);
        }

        public void drawPoints(float[] pts, Paint paint) {
            delegate.drawPoints(pts, paint);
        }

        public void drawPoint(float x, float y, Paint paint) {
            delegate.drawPoint(x, y, paint);
        }

        public void drawLine(float startX, float startY, float stopX,
                float stopY, Paint paint) {
            delegate.drawLine(startX, startY, stopX, stopY, paint);
        }

        public void drawLines(float[] pts, int offset, int count, Paint paint) {
            delegate.drawLines(pts, offset, count, paint);
        }

        public void drawLines(float[] pts, Paint paint) {
            delegate.drawLines(pts, paint);
        }

        public void drawRect(RectF rect, Paint paint) {
            delegate.drawRect(rect, paint);
        }

        public void drawRect(Rect r, Paint paint) {
            delegate.drawRect(r, paint);
        }

        public void drawRect(float left, float top, float right, float
                bottom, Paint paint) {
            delegate.drawRect(left, top, right, bottom, paint);
        }

        public void drawOval(RectF oval, Paint paint) {
            delegate.drawOval(oval, paint);
        }

        public void drawCircle(float cx, float cy, float radius, Paint paint) {
            delegate.drawCircle(cx, cy, radius, paint);
        }

        public void drawArc(RectF oval, float startAngle, float
                sweepAngle, boolean useCenter,
                Paint paint) {
            delegate.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
        }

        public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
            delegate.drawRoundRect(rect, rx, ry, paint);
        }

        public void drawPath(Path path, Paint paint) {
            delegate.drawPath(path, paint);
        }

        public void drawBitmap(Bitmap bitmap, float left, float top,
                Paint paint) {
            if (paint == null) {
                paint = mSmooth;
            } else {
                paint.setFilterBitmap(true);
            }
            delegate.drawBitmap(bitmap, left, top, paint);
        }

        public void drawBitmap(Bitmap bitmap, Rect src, RectF dst,
                Paint paint) {
            if (paint == null) {
                paint = mSmooth;
            } else {
                paint.setFilterBitmap(true);
            }
            delegate.drawBitmap(bitmap, src, dst, paint);
        }

        public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
            if (paint == null) {
                paint = mSmooth;
            } else {
                paint.setFilterBitmap(true);
            }
            delegate.drawBitmap(bitmap, src, dst, paint);
        }

        public void drawBitmap(int[] colors, int offset, int stride,
                int x, int y, int width,
                int height, boolean hasAlpha, Paint paint) {
            if (paint == null) {
                paint = mSmooth;
            } else {
                paint.setFilterBitmap(true);
            }
            delegate.drawBitmap(colors, offset, stride, x, y, width,
                    height, hasAlpha, paint);
        }

        public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
            if (paint == null) {
                paint = mSmooth;
            } else {
                paint.setFilterBitmap(true);
            }
            delegate.drawBitmap(bitmap, matrix, paint);
        }

        public void drawBitmapMesh(Bitmap bitmap, int meshWidth, int
                meshHeight, float[] verts,
                int vertOffset, int[] colors, int colorOffset, Paint paint) {
            delegate.drawBitmapMesh(bitmap, meshWidth, meshHeight,
                    verts, vertOffset, colors,
                    colorOffset, paint);
        }

        public void drawVertices(VertexMode mode, int vertexCount,
                float[] verts, int vertOffset,
                float[] texs, int texOffset, int[] colors, int
                colorOffset, short[] indices,
                int indexOffset, int indexCount, Paint paint) {
            delegate.drawVertices(mode, vertexCount, verts,
                    vertOffset, texs, texOffset, colors,
                    colorOffset, indices, indexOffset, indexCount, paint);
        }

        public void drawText(char[] text, int index, int count, float
                x, float y, Paint paint) {
            delegate.drawText(text, index, count, x, y, paint);
        }

        public void drawText(String text, float x, float y, Paint paint) {
            delegate.drawText(text, x, y, paint);
        }

        public void drawText(String text, int start, int end, float x,
                float y, Paint paint) {
            delegate.drawText(text, start, end, x, y, paint);
        }

        public void drawText(CharSequence text, int start, int end,
                float x, float y, Paint paint) {
            delegate.drawText(text, start, end, x, y, paint);
        }

        public void drawPosText(char[] text, int index, int count,
                float[] pos, Paint paint) {
            delegate.drawPosText(text, index, count, pos, paint);
        }

        public void drawPosText(String text, float[] pos, Paint paint) {
            delegate.drawPosText(text, pos, paint);
        }

        public void drawTextOnPath(char[] text, int index, int count,
                Path path, float hOffset,
                float vOffset, Paint paint) {
            delegate.drawTextOnPath(text, index, count, path, hOffset,
                    vOffset, paint);
        }

        public void drawTextOnPath(String text, Path path, float
                hOffset, float vOffset,
                Paint paint) {
            delegate.drawTextOnPath(text, path, hOffset, vOffset, paint);
        }

        public void drawPicture(Picture picture) {
            delegate.drawPicture(picture);
        }

        public void drawPicture(Picture picture, RectF dst) {
            delegate.drawPicture(picture, dst);
        }

        public void drawPicture(Picture picture, Rect dst) {
            delegate.drawPicture(picture, dst);
        }
    }
}