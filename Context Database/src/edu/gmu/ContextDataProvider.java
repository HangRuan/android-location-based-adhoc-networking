package edu.gmu;

import java.util.ArrayList;
import java.util.List;

import edu.gmu.contextdb.utils.Location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContextDataProvider {

	private static final String DATABASE_NAME = "gmu_context.db";
	private static final int DATABASE_VERSION = 1;


	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private Context myContext;


		private static final String PERSON_TABLE = "person";
		private static final String VEHICLE_TABLE = "vehicle";
		private static final String LANDMARK_TABLE = "landmark";
		private static final String RESOURCE_TABLE = "resource";
		private static final String PERSON_FRIENDLINESS = "person_friendliness";
		private static final String LOCATION = "location";
		private static final String LOCATION_TYPE = "location_type";
		private static final String SPACE_TIME = "space_time";
		private static final String FRIENDLINESS = "friendliness";
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			myContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE location_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");

			db.execSQL("CREATE TABLE resource_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");

			db.execSQL("CREATE TABLE vehicle_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");

			db.execSQL("CREATE TABLE landmark_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");

			db.execSQL("CREATE TABLE space_time (id INTEGER PRIMARY KEY  NOT NULL , timestamp DATETIME NOT NULL )");

			db.execSQL("CREATE TABLE friendliness (id INTEGER PRIMARY KEY  NOT NULL , timestamp DATETIME NOT NULL, " +
			"rating DOUBLE NOT NULL )");

			db.execSQL("CREATE TABLE location (id INTEGER PRIMARY KEY  NOT NULL , latitude DOUBLE, " +
					"longitude DOUBLE, elevation DOUBLE, time DATETIME, " +
					"space_time_id INTEGER, " +
					"type_id INTEGER, " +
					"FOREIGN KEY(space_time_id) REFERENCES space_time(id), " + 
			"FOREIGN KEY(type_id) REFERENCES location_type(id))");

			db.execSQL("CREATE TABLE vehicle (id INTEGER PRIMARY KEY  NOT NULL  UNIQUE , " +
					"color text, description text, " +
					"space_time_id INTEGER, " +
					"vehicle_type_id INTEGER, " +
					"FOREIGN KEY(space_time_id) REFERENCES space_time(id), " +
			"FOREIGN KEY(vehicle_type_id) REFERENCES vehicle_type(id))");

			db.execSQL("CREATE TABLE resource (id INTEGER PRIMARY KEY  NOT NULL , " +
					"value REAL NOT NULL , description TEXT, " +
					"space_time_id INTEGER, " +
					"resource_type_id INTEGER, " +
					"vehicle_id INTEGER, " +
					"FOREIGN KEY(space_time_id) REFERENCES space_time(id)," +
					"FOREIGN KEY(resource_type_id) REFERENCES resource_type(id)," +
			"FOREIGN KEY(vehicle_id) REFERENCES vehicle (id) )");

			db.execSQL("CREATE TABLE person (id INTEGER PRIMARY KEY  NOT NULL  UNIQUE , description TEXT, current_rating DOUBLE NOT NULL, " +
					"space_time_id INTEGER, " +
					"vehicle_id INTEGER, " +
					"FOREIGN KEY(space_time_id) REFERENCES space_time(id)," +
			"FOREIGN KEY(vehicle_id) REFERENCES vehicle(id) )");

			db.execSQL("CREATE TABLE person_friendliness (id INTEGER PRIMARY KEY  NOT NULL  UNIQUE, " +
					"person_id INTEGER, " +
					"friendliness_id INTEGER, " +
					"FOREIGN KEY(person_id) REFERENCES person(id)," +
			"FOREIGN KEY(friendliness_id) REFERENCES friendliness(id) )");

			db.execSQL("CREATE TABLE vehicle_friendliness (id INTEGER PRIMARY KEY  NOT NULL  UNIQUE, " +
					"vehicle_id INTEGER, " +
					"friendliness_id INTEGER, " +
					"FOREIGN KEY(vehicle_id) REFERENCES vehicle(id)," +
			"FOREIGN KEY(friendliness_id) REFERENCES friendliness(id) )");


			db.execSQL("CREATE TABLE objective (id INTEGER PRIMARY KEY  NOT NULL , description TEXT, " +
					"space_time_id INTEGER, " +
			"FOREIGN KEY(space_time_id) REFERENCES space_time(id) )");

			db.execSQL("CREATE TABLE landmark (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL , description TEXT, " +
					"space_time_id INTEGER, " +
					"landmark_type_id INTEGER, " +
					"FOREIGN KEY(landmark_type_id) REFERENCES landmark_type(id)," +
			"FOREIGN KEY(space_time_id) REFERENCES space_time(id) )");
			
			loadTestData(db);
		}

		private void loadTestData(SQLiteDatabase db)
		{
			long row = -1;
			ContentValues location_type = new ContentValues();
			location_type.put("type","GPS");
			row = db.insert(LOCATION_TYPE, null, location_type);
			
			ContentValues space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			row = db.insert(SPACE_TIME, null, space_time);
			
			ContentValues location = new ContentValues();
			location.put("latitude", -37.5);
			location.put("longitude", 73.25);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", 1);
			location.put("type_id", 1);
			row = db.insert(LOCATION, null, location);
			
			ContentValues friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",30.0);
			row = db.insert(FRIENDLINESS, null,friendliness);
			
			
			ContentValues person1 = new ContentValues();
			person1.put("description", "Tall Man");
			person1.put("current_rating",30.0);
			person1.put("space_time_id", 1);
    		row = db.insert(PERSON_TABLE, null, person1);
			
			ContentValues person_friendliness = new ContentValues();
			person_friendliness.put("person_id", 1);
			person_friendliness.put("friendliness_id", 1);
			db.insert(PERSON_FRIENDLINESS, null, person_friendliness);
			
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub


		}

		public Cursor getPeople()
		{
			return       	
			getReadableDatabase().query(true, "person", null,
					null, null,
					null, null, null, null);
		}

		private long insertPerson(ContentValues values)
		{
			SQLiteDatabase db = getWritableDatabase();
			long row = db.insert(PERSON_TABLE, null, values);
			db.close();
			return row;
		}
		
		private Cursor getSpaceTimeForPerson(long id)
		{
			SQLiteDatabase db = getReadableDatabase();
			Cursor cur = db.query("person", new String[] {"space_time_id"}, "id=?", new String[] {String.valueOf(id)}, null, null, null);
			if(cur.moveToFirst())
			{
				long space_time_id = cur.getLong(cur.getColumnIndex("space_time_id"));
				
				return db.query("space_time", null, "id=?", new String[] {String.valueOf(space_time_id)}, null, null, null);
			}
			return null;
		}
		
		private long insertLocation(double latitude, double longitude, double elevation, long timestamp,
				long space_time_id, long type_id)
		{
			ContentValues location = new ContentValues();
			location.put("latitude", latitude);
			location.put("longitude", longitude);
			location.put("elevation", 125.5);
			location.put("time", timestamp);
			location.put("space_time_id", space_time_id);
			location.put("type_id", type_id);
			SQLiteDatabase db = getReadableDatabase();
			return db.insert(LOCATION, null, location);
			
		}
		
		private Cursor getCurrentLocationForPerson(Long id)
		{
			String sql = "SELECT loc.* from location loc, space_time st, person p where p.id=" + id.longValue() +
			" AND p.space_time_id = st.id AND loc.space_time_id = st.id ORDER BY loc.time";
			return getReadableDatabase().rawQuery(sql,	null);
		}

	};



	private DatabaseHelper mOpenHelper;

	public ContextDataProvider(Context ctxt)
	{
		mOpenHelper = new DatabaseHelper(ctxt);

		mOpenHelper.getWritableDatabase();
		mOpenHelper.close();

	}

	public Cursor getPeople()
	{
		return mOpenHelper.getPeople();
	}
	
	public long insertPerson(ContentValues values)
	{
		return mOpenHelper.insertPerson(values);
	}

	public Cursor getSpaceTimeForPerson(long id)
	{
		Cursor cur = mOpenHelper.getSpaceTimeForPerson(id);
		return cur;
	}
	
	public Cursor getCurrentLocationForPerson(Long id)
	{
		Cursor cur = mOpenHelper.getCurrentLocationForPerson( id);
		int rows = cur.getCount();
		if(rows >0)
		{
			cur.moveToFirst();
			Double lat = cur.getDouble(cur.getColumnIndex("latitude"));
			System.out.println("Lat:" + lat);
		}
		
		return cur;
	}
	
	public long insertLocation(double latitude, double longitude, double elevation, long timestamp, long space_time_id)
	{
		long ret = -1;
		ret = mOpenHelper.insertLocation(latitude, longitude, elevation, timestamp, space_time_id, 1);
		return ret;
	}
}
