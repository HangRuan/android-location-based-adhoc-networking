package edu.gmu.hodum.sei_map_display_client;

import java.util.List;
import java.util.Vector;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import ed.gmu.hodum.ContentDatabaseAPI;
import ed.gmu.hodum.Thing;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MapDisplay extends MapActivity {

	ContentDatabaseAPI api;
	MapView mapView;
	BoundingOverlay boundingOverlay;
	
	
	final int MAX_POINT_CNT = 3;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		api = new ContentDatabaseAPI(this);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		Button btnDisplayItems = (Button)findViewById(R.id.btn_display_items);
		btnDisplayItems.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				
				//boundingOverlay = new BoundingOverlay(MapDisplay.this);
				//List<Overlay> mapOverlays = mapView.getOverlays();
				//mapOverlays.add(boundingOverlay);

				//TODO: Content Database API displays Person no matter the coordinates 
				//displayThingsWithinBounds(0.0, 0.0, 1.0, 1.0);
				displayThingsWithinBounds(0.0, 0.0, 0.0, 0.0);
				//displayThingsWithinBounds(-37.2, 77.55, -35.5, 79.2);
				
			}
			
		});
	}

	public void displayThingsWithinBounds(double latLL, double longLL, double latUR, double longUR){
		Vector<Thing> things = getThingsWithinBounds(latLL, longLL, latUR, longUR);
		
		System.out.println("The Vector of Things has "+things.size() +" Thing(s) in it.");
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		SimpleItemizedOverlay itemizedoverlay = new SimpleItemizedOverlay(drawable, this);

		OverlayItem item;
		GeoPoint point;
		int lat;
		int lon;
		for(Thing thing : things){
			lat = (int)thing.getLatitude();
			lon = (int)thing.getLongitude();
			point = new GeoPoint( lat,  lon);
			if(thing.getType().equals(Thing.Type.PERSON)){
				item = new OverlayItem(point, "I'm a person!", "I'm at: "+lat+","+lon);
				itemizedoverlay.addOverlay(item);
			}
		}

		mapOverlays.add(itemizedoverlay);
		
		if(boundingOverlay != null){
			mapOverlays.remove(boundingOverlay);
			boundingOverlay = null;
		}
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
}