package edu.gmu.hodum.sei.map_display_client;

import java.util.List;
import java.util.Vector;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.gmu.hodum.ContentDatabaseAPI;
import edu.gmu.hodum.sei.common.Thing;
import edu.gmu.hodum.sei_map_display_client.R;
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

				boundingOverlay = new BoundingOverlay(MapDisplay.this);
				List<Overlay> mapOverlays = mapView.getOverlays();
				mapOverlays.add(boundingOverlay);

			}

		});
	}

	public void displayThingsWithinBounds(double latLL, double longLL, double latUR, double longUR){
		System.out.println("Bounds: " +latLL+","+longLL+","+latUR+","+longUR);
		
		Vector<Thing> things = getThingsWithinBounds(latLL, longLL, latUR, longUR);

		System.out.println("The Vector of Things has "+things.size() +" Thing(s) in it.");

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawablePerson = this.getResources().getDrawable(R.drawable.androidmarker);
		Drawable drawableVehicle = this.getResources().getDrawable(R.drawable.ic_lock_airplane_mode);
		Drawable drawableResource = this.getResources().getDrawable(R.drawable.sym_def_app_icon);
		Drawable drawableLandmark = this.getResources().getDrawable(R.drawable.btn_radio_on_selected);
		
		
		SimpleItemizedOverlay personOverlay = new SimpleItemizedOverlay(drawablePerson, this);
		SimpleItemizedOverlay vehicleOverlay = new SimpleItemizedOverlay(drawableVehicle, this);
		SimpleItemizedOverlay resourceOverlay = new SimpleItemizedOverlay(drawableResource, this);
		SimpleItemizedOverlay landmarkOverlay = new SimpleItemizedOverlay(drawableLandmark, this);

		OverlayItem item;
		GeoPoint point;
		int lat;
		int lon;
		for(Thing thing : things){
			lat = (int)thing.getLatitude();
			lon = (int)thing.getLongitude();
			point = new GeoPoint( lat,  lon);
			switch(thing.getType()){

			case PERSON:
				item = new OverlayItem(point, "I'm a person!", "I'm at: "+lat+","+lon);
				personOverlay.addOverlay(item);
				break;
			case VEHICLE:
				item = new OverlayItem(point, "I'm a vehicle!", "I'm at: "+lat+","+lon);
				vehicleOverlay.addOverlay(item);
			case RESOURCE:
				item = new OverlayItem(point, "I'm a resource!", "I'm at: "+lat+","+lon);
				resourceOverlay.addOverlay(item);
			case LANDMARK:
				item = new OverlayItem(point, "I'm a landmark!", "I'm at: "+lat+","+lon);
				landmarkOverlay.addOverlay(item);
			}


		}
		mapOverlays.clear();

		//empty overlays will crash mapview, so check if empty

		if(personOverlay.size()>0){
			mapOverlays.add(personOverlay);
		}
		if(vehicleOverlay.size()>0){
			mapOverlays.add(vehicleOverlay);
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