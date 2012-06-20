package com.jwetherell.augmented_reality.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.jwetherell.augmented_reality.R;
import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;

import edu.gmu.hodum.ContentDatabaseAPI;
import edu.gmu.hodum.sei.common.Objective;
import edu.gmu.hodum.sei.common.Thing;



public class ContextDataSource extends DataSource {

	private Drawable drawableGreenPerson;
	private Drawable drawableRedPerson;
	private Drawable drawableBluePerson;
	private Drawable drawableGreenVehicle;
	private Drawable drawableBlueVehicle;
	private Drawable drawableRedVehicle;
	private Drawable drawableResource;
	private Drawable drawableLandmark;
	
	private Drawable halfFadedDrawableGreenPerson;
	private Drawable halfFadedDrawableRedPerson;
	private Drawable halfFadedDrawableBluePerson;
	private Drawable halfFadedDrawableGreenVehicle;
	private Drawable halfFadedDrawableBlueVehicle;
	private Drawable halfFadedDrawableRedVehicle;
	private Drawable halfFadedDrawableResource;
	private Drawable halfFadedDrawableLandmark;
	
	private Drawable fullFadedDrawableGreenPerson;
	private Drawable fullFadedDrawableRedPerson;
	private Drawable fullFadedDrawableBluePerson;
	private Drawable fullFadedDrawableGreenVehicle;
	private Drawable fullFadedDrawableBlueVehicle;
	private Drawable fullFadedDrawableRedVehicle;
	private Drawable fullFadedDrawableResource;
	private Drawable fullFadedDrawableLandmark;
	
	private Drawable waypoint;
	
	private Context mCtxt;
	private ContentDatabaseAPI api;
	
	
	public ContextDataSource(Context ctxt)
	{
		mCtxt = ctxt;
		api = new ContentDatabaseAPI(mCtxt);
		loadMarkerIcons();
		
	}
	
	@Override
	public List<Marker> getMarkers() {
		Location myPos = ARData.getCurrentLocation();
		Vector<Thing> things= api.getThings(myPos.getLatitude()-10, myPos.getLongitude()-10, myPos.getLatitude()+10, myPos.getLongitude()+10);
		List<Marker> markers = new ArrayList<Marker>(things.size());
	int counter = 0;
		for(Thing thing: things)
		{
			Marker newMarker = null;
			switch(thing.getType()){

			case PERSON:
				
				if(thing.getRelevance()<200.0)
				{
					if(thing.getFriendliness()>66)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.GREEN, drawableToBitmap(drawableGreenPerson));
					}
					else if(thing.getFriendliness()>33)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.BLUE, drawableToBitmap(drawableBluePerson));
					}
					else
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.RED, drawableToBitmap(drawableRedPerson));
					}						
				}
				else if(thing.getRelevance() <1000.0)
				{
					if(thing.getFriendliness()>66)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.GREEN, drawableToBitmap(halfFadedDrawableGreenPerson));
						
					}
					else if(thing.getFriendliness()>33)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.BLUE, drawableToBitmap(halfFadedDrawableBluePerson));
						
					}
					else
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.RED, drawableToBitmap(halfFadedDrawableRedPerson));
						
					}
					
				}
				else
				{
					if(thing.getFriendliness()>66)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.GREEN, drawableToBitmap(fullFadedDrawableGreenPerson));
						
					}
					else if(thing.getFriendliness()>33)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.BLUE, drawableToBitmap(fullFadedDrawableBluePerson));
					
					}
					else
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.RED, drawableToBitmap(fullFadedDrawableRedPerson));
					
					}

				}
				break;
			case VEHICLE:
				
				if(thing.getRelevance()<200.0)
				{
					
					if(thing.getFriendliness()>66)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.GREEN, drawableToBitmap(drawableGreenVehicle));
						
					}
					else if(thing.getFriendliness()>33)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.BLUE, drawableToBitmap(drawableBlueVehicle));
						
					}
					else
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.RED, drawableToBitmap(drawableRedVehicle));
						
					}
					
				}
				else if(thing.getRelevance() <1000.0)
				{
					if(thing.getFriendliness()>66)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.GREEN, drawableToBitmap(halfFadedDrawableGreenVehicle));
						
					}
					else if(thing.getFriendliness()>33)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.BLUE, drawableToBitmap(halfFadedDrawableBlueVehicle));
						
					}
					else
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.RED, drawableToBitmap(halfFadedDrawableRedVehicle));
						
					}
				}
				else
				{
					if(thing.getFriendliness()>66)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.GREEN, drawableToBitmap(fullFadedDrawableGreenVehicle));
						
					}
					else if(thing.getFriendliness()>33)
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.BLUE, drawableToBitmap(fullFadedDrawableBlueVehicle));
						
					}
					else
					{
						newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
								-10.0, 
								Color.RED, drawableToBitmap(fullFadedDrawableRedVehicle));
						
					}
				}

				break;
			case RESOURCE:
				
				newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
						-10.0, 
						Color.GREEN, drawableToBitmap(drawableResource));
				
				break;
			case LANDMARK:
				newMarker = new IconMarker(thing.getDescription() + counter++,thing.getLatitude(),thing.getLongitude(),
						-10.0, 
						Color.BLUE, drawableToBitmap(drawableLandmark));
				break;
			}
			
			if(newMarker != null)
			{
				markers.add(newMarker);
			}
		}
		counter = 0;
		Objective objective = api.getPatrolObjective();
		if(objective != null && objective.getLocations() != null)
		{
			Marker newMarker = null;
			Vector<Location> waypoints = objective.getLocations();
			for(Location loc:waypoints)
			{
				newMarker = new IconMarker(objective.getDescription() + counter++,loc.getLatitude(),loc.getLongitude(),
						-10.0, 
						Color.RED, drawableToBitmap(waypoint)); 
				markers.add(newMarker);
			}
		}
		return markers;
	}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), 
	    		Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	private void loadMarkerIcons()
	{
		drawableGreenPerson = mCtxt.getResources().getDrawable(R.drawable.androidmarker);
		drawableBluePerson = mCtxt.getResources().getDrawable(R.drawable.blue_androidmarker);
		drawableRedPerson = mCtxt.getResources().getDrawable(R.drawable.red_androidmarker);
		drawableGreenVehicle = mCtxt.getResources().getDrawable(R.drawable.green_auto_icon);
		drawableBlueVehicle = mCtxt.getResources().getDrawable(R.drawable.blue_auto_icon);
		drawableRedVehicle = mCtxt.getResources().getDrawable(R.drawable.red_auto_icon);
		drawableResource = mCtxt.getResources().getDrawable(R.drawable.sym_def_app_icon);
		drawableLandmark = mCtxt.getResources().getDrawable(R.drawable.btn_radio_on_selected);
		
		
		halfFadedDrawableGreenPerson = mCtxt.getResources().getDrawable(R.drawable.androidmarker_partial_faded);

		
		halfFadedDrawableBluePerson = mCtxt.getResources().getDrawable(R.drawable.blue_androidmarker_partial_faded);

		
		halfFadedDrawableRedPerson = mCtxt.getResources().getDrawable(R.drawable.red_androidmarker_partial_faded);

		
		halfFadedDrawableGreenVehicle = mCtxt.getResources().getDrawable(R.drawable.green_auto_icon_partial_faded);

		
		halfFadedDrawableBlueVehicle = mCtxt.getResources().getDrawable(R.drawable.blue_auto_icon_partial_faded);

		
		halfFadedDrawableRedVehicle = mCtxt.getResources().getDrawable(R.drawable.red_auto_icon_partial_faded);

		
		halfFadedDrawableResource = mCtxt.getResources().getDrawable(R.drawable.sym_def_app_icon);

		
		halfFadedDrawableLandmark = mCtxt.getResources().getDrawable(R.drawable.btn_radio_on_selected);

		
		
		
		fullFadedDrawableGreenPerson = mCtxt.getResources().getDrawable(R.drawable.androidmarker_fully_faded);

		fullFadedDrawableBluePerson = mCtxt.getResources().getDrawable(R.drawable.blue_androidmarker_fully_faded);

		fullFadedDrawableRedPerson = mCtxt.getResources().getDrawable(R.drawable.red_androidmarker_fully_faded);

		
		fullFadedDrawableGreenVehicle = mCtxt.getResources().getDrawable(R.drawable.green_auto_icon_fully_faded);
		
		fullFadedDrawableBlueVehicle = mCtxt.getResources().getDrawable(R.drawable.blue_auto_icon_fully_faded);
		
		fullFadedDrawableRedVehicle = mCtxt.getResources().getDrawable(R.drawable.red_auto_icon_fully_faded);
		
		fullFadedDrawableResource = mCtxt.getResources().getDrawable(R.drawable.sym_def_app_icon);
		
		fullFadedDrawableLandmark = mCtxt.getResources().getDrawable(R.drawable.btn_radio_on_selected);
		
		waypoint = mCtxt.getResources().getDrawable(R.drawable.marker_waypoint);
		
//		receiver = new MyNewDataReceiver();
//		receiver.registerHandler(newDataHandler);
	}

}
