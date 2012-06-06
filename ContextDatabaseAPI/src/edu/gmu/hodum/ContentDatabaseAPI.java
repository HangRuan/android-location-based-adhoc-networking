package edu.gmu.hodum;

import java.util.Vector;

import edu.gmu.hodum.sei.common.*;
import edu.gmu.hodum.sei.geometry.utils.DistancePoint;
import edu.gmu.hodum.sei.geometry.utils.Point2D;





import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;

public class ContentDatabaseAPI {

	private static final String baseURI = "content://edu.gmu.provider.cursor.dir";

	
	private Context ctxt;

	public ContentDatabaseAPI(Context ctxt_)
	{
		ctxt = ctxt_;
	}

	public void callDB()
	{
		ContentResolver cr = ctxt.getContentResolver();

		Cursor cur = cr.query(Uri.parse( baseURI+"/objective/patrol"), null, null, null, null); 
		if(cur == null)
		{
			System.out.println("Damnit!");
		}
		else
		{
			if(cur.moveToFirst())
			{
				String description = cur.getString(cur.getColumnIndex("description"));
				System.out.println("description: " + description);
			}
		}
		cur.close();
	}

	public Vector<Thing> getThings(double latLL, double longLL, double latUR, double longUR)
	{
		Objective patrolObjective = getPatrolObjective();
		Vector<Thing> ret = new Vector<Thing>();
		ContentResolver cr = ctxt.getContentResolver();

		Cursor cur = cr.query(Uri.parse( baseURI+"/people"), null, null, null, null); 
		if(cur == null)
		{
			System.out.println("Failed to find a Content Resolver!");
		}
		else
		{
			while(cur.moveToNext())
			{

				Long id = cur.getLong(cur.getColumnIndex("id"));

				Cursor cur2 = cr.query(Uri.parse( baseURI+"/person/location"), null, null, new String[]{String.valueOf(id.longValue())}, null);
				if(cur2.moveToFirst())
				{

					Thing thng = new Thing();
					thng.setType(Thing.Type.PERSON);
					thng.setId(cur.getLong(cur.getColumnIndex("id")));
					thng.setFriendliness(cur.getDouble(cur.getColumnIndex("current_rating")));

					thng.setRelevance(java.lang.Math.random());
					thng.setDescription(cur.getString(cur.getColumnIndex("description")));
					
					
					thng.setLatitude(cur2.getDouble(cur2.getColumnIndex("latitude")));

					thng.setLongitude(cur2.getDouble(cur2.getColumnIndex("longitude")));

					thng.setElevation(cur2.getDouble(cur2.getColumnIndex("elevation")));
					if(thng.getLatitude()> latLL && thng.getLatitude() <latUR && thng.getLongitude()>longLL && thng.getLongitude()<longUR)
					{
						thng.setRelevance(calculateRelevance(thng,patrolObjective));
						ret.add(thng);
					}
				}
				cur2.close();
			}
		}
		cur.close();
		cur = cr.query(Uri.parse( baseURI+"/vehicles"), null, null, null, null); 

		if(cur == null)
		{
			System.out.println("Failed to find a Content Resolver!");
		}
		else
		{
			while(cur.moveToNext())
			{
				Long id = cur.getLong(cur.getColumnIndex("id"));

				Cursor cur2 = cr.query(Uri.parse( baseURI+"/vehicle/location"), null, null, new String[]{String.valueOf(id.longValue())}, null);
				if(cur2.moveToFirst())
				{
					Thing thng = new Thing();
					thng.setType(Thing.Type.VEHICLE);
					thng.setId(cur.getLong(cur.getColumnIndex("id")));
					thng.setFriendliness(cur.getDouble(cur.getColumnIndex("current_rating")));

					thng.setRelevance(java.lang.Math.random());
					thng.setDescription(cur.getString(cur.getColumnIndex("description")));
					
					
					thng.setLatitude(cur2.getDouble(cur2.getColumnIndex("latitude")));

					thng.setLongitude(cur2.getDouble(cur2.getColumnIndex("longitude")));

					thng.setElevation(cur2.getDouble(cur2.getColumnIndex("elevation")));
					if(thng.getLatitude()> latLL && thng.getLatitude() <latUR && thng.getLongitude()>longLL && thng.getLongitude()<longUR)
					{
						thng.setRelevance(calculateRelevance(thng,patrolObjective));
						ret.add(thng);
					}
				}
				cur2.close();
			}
		}
		cur.close();
		cur = cr.query(Uri.parse( baseURI+"/landmarks"), null, null, null, null); 

		if(cur == null)
		{
			System.out.println("Failed to find a Content Resolver!");
		}
		else
		{
			while(cur.moveToNext())
			{
				Long id = cur.getLong(cur.getColumnIndex("id"));

				Cursor cur2 = cr.query(Uri.parse( baseURI+"/landmark/location"), null, null, new String[]{String.valueOf(id.longValue())}, null);
				if(cur2.moveToFirst())
				{
					Thing thng = new Thing();
					thng.setType(Thing.Type.LANDMARK);
					thng.setId(cur.getLong(cur.getColumnIndex("id")));
					thng.setFriendliness(cur.getDouble(cur.getColumnIndex("current_rating")));

					thng.setRelevance(java.lang.Math.random());
					thng.setDescription(cur.getString(cur.getColumnIndex("description")));
					
					thng.setLatitude(cur2.getDouble(cur2.getColumnIndex("latitude")));

					thng.setLongitude(cur2.getDouble(cur2.getColumnIndex("longitude")));

					thng.setElevation(cur2.getDouble(cur2.getColumnIndex("elevation")));
					if(thng.getLatitude()> latLL && thng.getLatitude() <latUR && thng.getLongitude()>longLL && thng.getLongitude()<longUR)
					{
						thng.setRelevance(calculateRelevance(thng,patrolObjective));
						ret.add(thng);
					}
				}
				cur2.close();
			}
		}
		cur.close();
		cur = cr.query(Uri.parse( baseURI+"/resources"), null, null, null, null); 

		if(cur == null)
		{
			System.out.println("Failed to find a Content Resolver!");
		}
		else
		{
			while(cur.moveToNext())
			{
				Long id = cur.getLong(cur.getColumnIndex("id"));

				Cursor cur2 = cr.query(Uri.parse( baseURI+"/resource/location"), null, null, new String[]{String.valueOf(id.longValue())}, null);
				if(cur2.moveToFirst())
				{
					Thing thng = new Thing();
					thng.setType(Thing.Type.RESOURCE);
					thng.setId(cur.getLong(cur.getColumnIndex("id")));
					thng.setFriendliness(cur.getDouble(cur.getColumnIndex("current_rating")));

					
					thng.setDescription(cur.getString(cur.getColumnIndex("description")));
					
					thng.setLatitude(cur2.getDouble(cur2.getColumnIndex("latitude")));

					thng.setLongitude(cur2.getDouble(cur2.getColumnIndex("longitude")));

					thng.setElevation(cur2.getDouble(cur2.getColumnIndex("elevation")));
					if(thng.getLatitude()> latLL && thng.getLatitude() <latUR && thng.getLongitude()>longLL && thng.getLongitude()<longUR)
					{
						thng.setRelevance(calculateRelevance(thng,patrolObjective));
						ret.add(thng);
					}
				}
				cur2.close();
			}
		}
		cur.close();
		return ret;
	}
	
	public Objective getPatrolObjective()
	{
		ContentResolver cr = ctxt.getContentResolver();
		Objective ret = null;
		Cursor cur = cr.query(Uri.parse( baseURI+"/objective/patrol"), null, null, null, null); 
		if(cur == null)
		{
			System.out.println("Damnit!");
		}
		else
		{
			if(cur.moveToFirst())
			{
				Long id = cur.getLong(cur.getColumnIndex("id"));
				ret = new Objective();
				ret.setId(id);
				ret.setDescription(cur.getString(cur.getColumnIndex("description")));
				ret.setType(Objective.ObjectiveType.PATROL);
				Cursor cur2 = cr.query(Uri.parse( baseURI+"/objective/patrol/route"), null, null, new String[]{String.valueOf(id.longValue())}, null);
				cur2.moveToFirst();
				for(int i=0; i<cur2.getCount(); i++)
				{
					Location loc = new Location("Other");
				
					loc.setLatitude(cur2.getDouble(cur2.getColumnIndex("latitude")));

					loc.setLongitude(cur2.getDouble(cur2.getColumnIndex("longitude")));

					loc.setAltitude(cur2.getDouble(cur2.getColumnIndex("elevation")));
					
					ret.addLocation(loc);
					cur2.moveToNext();
				}
			}
		}
		return ret;
	}
	
	private double calculateRelevance(Thing thing, Objective objective)
	{
		double ret = Double.POSITIVE_INFINITY;
		
		Vector<Location> locations = objective.getLocations();
		
		for(int i=0; i<locations.size()-1;i++)
		{
			Location a = locations.get(i);
			Location b = locations.get(i+1);
			Point2D pa = new Point2D(a.getLatitude(),a.getLongitude());
			Point2D pb = new Point2D(b.getLatitude(),b.getLongitude());
			Point2D pc = new Point2D(thing.getLatitude(), thing.getLongitude());
			double distance = DistancePoint.distanceToSegment(pa, pb, pc);
			if (distance < ret)
			{
				ret = distance;
			}
		}
		ret *= 100000;
		
		return ret;
	}
}
