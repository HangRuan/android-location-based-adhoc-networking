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

public class MapDisplay extends MapActivity {

	ContentDatabaseAPI api;
	MapView mapView;
	
	final int MAX_POINT_CNT = 3;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		api = new ContentDatabaseAPI(this);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		displayThingsWithinBounds(-37.2, 77.55, -35.5, 79.2);

	}


	public void displayThingsWithinBounds(double latLL, double longLL, double latUR, double longUR){
		Vector<Thing> things = getThingsWithinBounds(-37.2, 77.55, -35.5, 79.2);
		
		/*
		 * location.put("latitude", -37.5);
			location.put("longitude", 73.25);
		 */
		
		/*
		Thing temp = new Thing();
		temp.setType(Thing.Type.PERSON);
		temp.setLatitude(36.0);
		temp.setLongitude(78.0);
		@SuppressWarnings("serial")
		Vector<Thing> things = new Vector<Thing>();
		things.add(temp);
		*/
		
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

		//TODO: Used to have user select the "displayThingsWithinBounds" bounding box
		//mapOverlays.add(new BoundingOverlay());
		
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