package ed.gmu.hodum;

import java.util.Vector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
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

		Cursor cur = cr.query(Uri.parse( baseURI+"/person/location"), null, null, new String[]{"1"}, null); 
		if(cur == null)
		{
			System.out.println("Damnit!");
		}
		else
		{
			if(cur.moveToFirst())
			{
				Double latitude = cur.getDouble(cur.getColumnIndex("latitude"));
				System.out.println("latitude: " + latitude);
			}
		}
		cur.close();
		Cursor cur2 = cr.query(Uri.parse( baseURI+"/people"), null, null, null, null); 
		if(cur2 == null)
		{
			System.out.println("Damnit!");
		}
		else
		{
			if(cur2.moveToFirst())
			{
				String description = cur2.getString(cur2.getColumnIndex("description"));
				System.out.println("descrition: " + description);
			}
		}
	}
	
	public Vector<Thing> getThings(double latLL, double longLL, double latUR, double longUR)
	{
		Vector<Thing> ret = new Vector<Thing>();
		ContentResolver cr = ctxt.getContentResolver();
		
		Cursor cur = cr.query(Uri.parse( baseURI+"/people"), null, null, null, null); 
		if(cur == null)
		{
			System.out.println("Damnit!");
		}
		else
		{
			while(cur.moveToNext())
			{
				Long id = cur.getLong(cur.getColumnIndex("id"));
				String description = cur.getString(cur.getColumnIndex("description"));
				System.out.println("descrition: " + description);
				Cursor cur2 = cr.query(Uri.parse( baseURI+"/person/location"), null, null, new String[]{String.valueOf(id.longValue())}, null);
				Thing thng = new Thing();
				thng.setFriendliness(cur.getDouble(cur.getColumnIndex("current_rating")));
				thng.setRelevance(java.lang.Math.random());
				thng.setLatitude(cur2.getDouble(cur2.getColumnIndex("latitude")));
				thng.setLongitude(cur2.getDouble(cur2.getColumnIndex("longitude")));
				thng.setElevation(cur2.getDouble(cur2.getColumnIndex("elevation")));
				ret.add(thng);
			}
		}
		
		return ret;
	}
}
