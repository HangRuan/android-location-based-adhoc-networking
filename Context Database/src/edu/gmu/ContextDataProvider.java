package edu.gmu;

import edu.gmu.hodum.sei.common.Thing;
import edu.gmu.hodum.sei.common.Thing.Type;



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

		private static final String VEHICLE_TYPE = "vehicle_type";
		private static final String RESOURCE_TYPE = "resource_type";
		private static final String LANDMARK_TYPE = "landmark_type";
		private static final String PERSON_TYPE = "person_type";
		private static final String OBJECTIVE_TYPE = "objective_type";
		private static final String PERSON_TABLE = "person";
		private static final String VEHICLE_TABLE = "vehicle";
		private static final String LANDMARK_TABLE = "landmark";
		private static final String RESOURCE_TABLE = "resource";
		private static final String OBJECTIVE_TABLE = "objective";
		private static final String PERSON_FRIENDLINESS = "person_friendliness";
		private static final String VEHICLE_FRIENDLINESS = "vehicle_friendliness";
		private static final String LANDMARK_FRIENDLINESS = "landmark_friendliness";
		private static final String RESOURCE_FRIENDLINESS = "resource_friendliness";
		private static final String OBJECTIVE_VEHICLE_RELEVANCE = "objective_type_vehicle_type_relevance";
		private static final String OBJECTIVE_LANDMARK_RELEVANCE = "objective_type_landmark_type_relevance";
		private static final String OBJECTIVE_RESOURCE_RELEVANCE = "objective_type_resource_type_relevance";
		private static final String OBJECTIVE_PERSON_RELEVANCE = "objective_type_person_type_relevance";
		private static final String LOCATION = "location";
		private static final String LOCATION_TYPE = "location_type";
		private static final String SPACE_TIME = "space_time";
		private static final String FRIENDLINESS = "friendliness";
		private static final int HIGH_VALUE = 2;
		private static final int LOW_VALUE = 1;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			myContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE location_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");

			db.execSQL("CREATE TABLE resource_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");

			db.execSQL("CREATE TABLE vehicle_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");
			
			db.execSQL("CREATE TABLE person_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");
			
			db.execSQL("CREATE TABLE objective_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");

			db.execSQL("CREATE TABLE landmark_type (id INTEGER PRIMARY KEY  NOT NULL , type TEXT NOT NULL )");

			db.execSQL("CREATE TABLE space_time (id INTEGER PRIMARY KEY  NOT NULL , timestamp DATETIME NOT NULL )");

			db.execSQL("CREATE TABLE friendliness (id INTEGER PRIMARY KEY  NOT NULL , timestamp DATETIME NOT NULL, " +
			"rating DOUBLE NOT NULL )");

			db.execSQL("CREATE TABLE objective_type_vehicle_type_relevance (id INTEGER PRIMARY KEY NOT NULL, " +
					"relevance INTEGER, " +
					"objective_type_id INTEGER, " +
					"vehicle_type_id INTEGER, " +
					"FOREIGN KEY(objective_type_id) REFERENCES objective_type(id), " +
					"FOREIGN KEY(vehicle_type_id) REFERENCES vehicle_type(id)) ");
			
			db.execSQL("CREATE TABLE objective_type_resource_type_relevance (id INTEGER PRIMARY KEY NOT NULL, " +
					"relevance INTEGER, " +
					"objective_type_id INTEGER, " +
					"resource_type_id INTEGER, " +
					"FOREIGN KEY(objective_type_id) REFERENCES objective_type(id), " +
					"FOREIGN KEY(resource_type_id) REFERENCES resource_type(id)) ");
			
			db.execSQL("CREATE TABLE objective_type_landmark_type_relevance (id INTEGER PRIMARY KEY NOT NULL, " +
					"relevance INTEGER, " +
					"objective_type_id INTEGER, " +
					"landmark_type_id INTEGER, " +
					"FOREIGN KEY(objective_type_id) REFERENCES objective_type(id), " +
					"FOREIGN KEY(landmark_type_id) REFERENCES landmark_type(id)) ");
			
			db.execSQL("CREATE TABLE objective_type_person_type_relevance (id INTEGER PRIMARY KEY NOT NULL, " +
					"relevance INTEGER, " +
					"objective_type_id INTEGER, " +
					"person_type_id INTEGER, " +
					"FOREIGN KEY(objective_type_id) REFERENCES objective_type(id), " +
					"FOREIGN KEY(person_type_id) REFERENCES person_type(id)) ");
					
			
			db.execSQL("CREATE TABLE location (id INTEGER PRIMARY KEY  NOT NULL , latitude DOUBLE, " +
					"longitude DOUBLE, elevation DOUBLE, time DATETIME, " +
					"space_time_id INTEGER, " +
					"type_id INTEGER, " +
					"FOREIGN KEY(space_time_id) REFERENCES space_time(id), " + 
			"FOREIGN KEY(type_id) REFERENCES location_type(id))");

			db.execSQL("CREATE TABLE vehicle (id INTEGER PRIMARY KEY  NOT NULL  UNIQUE , " +
					"color text, description text, " +
					"current_rating DOUBLE NOT NULL," +
					"space_time_id INTEGER, " +
					"vehicle_type_id INTEGER, " +
					"FOREIGN KEY(space_time_id) REFERENCES space_time(id), " +
			"FOREIGN KEY(vehicle_type_id) REFERENCES vehicle_type(id))");

			db.execSQL("CREATE TABLE resource (id INTEGER PRIMARY KEY  NOT NULL , current_rating DOUBLE NOT NULL, " +
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
					"person_type_id INTEGER, " +
					"FOREIGN KEY(space_time_id) REFERENCES space_time(id)," +
					"FOREIGN KEY(person_type_id) REFERENCES person_type(id), " +
			"FOREIGN KEY(vehicle_id) REFERENCES vehicle(id) )");

			db.execSQL("CREATE TABLE landmark (id INTEGER PRIMARY KEY  NOT NULL , description TEXT,  current_rating DOUBLE NOT NULL, " +
					"space_time_id INTEGER, " +
					"landmark_type_id INTEGER, " +
					"FOREIGN KEY(landmark_type_id) REFERENCES landmark_type(id)," +
			"FOREIGN KEY(space_time_id) REFERENCES space_time(id) )");

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

			db.execSQL("CREATE TABLE landmark_friendliness (id INTEGER PRIMARY KEY  NOT NULL  UNIQUE, " +
					"landmark_id INTEGER, " +
					"friendliness_id INTEGER, " +
					"FOREIGN KEY(landmark_id) REFERENCES landmark(id)," +
			"FOREIGN KEY(friendliness_id) REFERENCES friendliness(id) )");

			db.execSQL("CREATE TABLE resource_friendliness (id INTEGER PRIMARY KEY  NOT NULL  UNIQUE, " +
					"resource_id INTEGER, " +
					"friendliness_id INTEGER, " +
					"FOREIGN KEY(resource_id) REFERENCES resource(id)," +
			"FOREIGN KEY(friendliness_id) REFERENCES friendliness(id) )");


			db.execSQL("CREATE TABLE objective (id INTEGER PRIMARY KEY  NOT NULL , description TEXT, " +
					"space_time_id INTEGER, " +
					"objective_type_id INTEGER, " +
					"FOREIGN KEY(objective_type_id) REFERENCES objective_type(id), "+
			"FOREIGN KEY(space_time_id) REFERENCES space_time(id) )");

			


			loadTestData(db);
		}

		private void loadTestData(SQLiteDatabase db)
		{

			//---------------------Begin TYPES

			long row = -1;
			ContentValues location_type = new ContentValues();
			location_type.put("type","GPS");
			row = db.insert(LOCATION_TYPE, null, location_type);

			location_type = new ContentValues();
			location_type.put("type","observation");
			row = db.insert(LOCATION_TYPE, null, location_type);

			ContentValues vehicle_type = new ContentValues();
			vehicle_type.put("type", "Sedan");
			db.insert(VEHICLE_TYPE, null, vehicle_type);

			vehicle_type = new ContentValues();
			vehicle_type.put("type", "Pickup Truck");
			db.insert(VEHICLE_TYPE, null, vehicle_type);

			vehicle_type = new ContentValues();
			vehicle_type.put("type", "Tank");
			db.insert(VEHICLE_TYPE, null, vehicle_type);

			vehicle_type = new ContentValues();
			vehicle_type.put("type", "SUV");
			db.insert(VEHICLE_TYPE, null, vehicle_type);

			ContentValues landmark_type = new ContentValues();
			landmark_type.put("type", "Arena");
			db.insert(LANDMARK_TYPE, null, landmark_type);

			landmark_type = new ContentValues();
			landmark_type.put("type", "Religious Site");
			db.insert(LANDMARK_TYPE, null, landmark_type);

			landmark_type = new ContentValues();
			landmark_type.put("type", "Monument");
			db.insert(LANDMARK_TYPE, null, landmark_type);

			landmark_type = new ContentValues();
			landmark_type.put("type", "Building");
			db.insert(LANDMARK_TYPE, null, landmark_type);

			landmark_type = new ContentValues();
			landmark_type.put("type", "Military Base");
			db.insert(LANDMARK_TYPE, null, landmark_type);
			
			
			ContentValues resource_type = new ContentValues();
			resource_type.put("type", "Food");
			db.insert(RESOURCE_TYPE, null, resource_type);
			
			resource_type = new ContentValues();
			resource_type.put("type", "Munition");
			db.insert(RESOURCE_TYPE, null, resource_type);
			
			resource_type = new ContentValues();
			resource_type.put("type", "Fuel");
			db.insert(RESOURCE_TYPE, null, resource_type);
			
			resource_type = new ContentValues();
			resource_type.put("type", "Water");
			db.insert(RESOURCE_TYPE, null, resource_type);
			
			
			ContentValues person_type = new ContentValues();
			person_type.put("type", "Military");
			db.insert(PERSON_TYPE, null, person_type);
			
			person_type = new ContentValues();
			person_type.put("type", "Civilian");
			db.insert(PERSON_TYPE, null, person_type);
			
			person_type = new ContentValues();
			person_type.put("type", "Insurgent");
			db.insert(PERSON_TYPE, null, person_type);
			
			person_type = new ContentValues();
			person_type.put("type", "Law Enforcement");
			db.insert(PERSON_TYPE, null, person_type);
			
			
			ContentValues objective_type = new ContentValues();
			objective_type.put("type", "Patrol");
			db.insert(OBJECTIVE_TYPE, null, objective_type);
			
			objective_type = new ContentValues();
			objective_type.put("type", "Humanitarian");
			db.insert(OBJECTIVE_TYPE, null, objective_type);

			//---------------------End TYPES

			
			//---------------OBJECTIVE TO TYPES
			//Humanitarian - vehicle
			ContentValues objective_vehicle_relevance = new ContentValues();
			objective_vehicle_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_vehicle_relevance.put("vehicle_type_id", getVehicleTypeId(db,"Sedan"));
			objective_vehicle_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_VEHICLE_RELEVANCE, null, objective_vehicle_relevance);
			
			objective_vehicle_relevance = new ContentValues();
			objective_vehicle_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_vehicle_relevance.put("vehicle_type_id", getVehicleTypeId(db,"Pickup Truck"));
			objective_vehicle_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_VEHICLE_RELEVANCE, null, objective_vehicle_relevance);
			
			objective_vehicle_relevance = new ContentValues();
			objective_vehicle_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_vehicle_relevance.put("vehicle_type_id", getVehicleTypeId(db,"Tank"));
			objective_vehicle_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_VEHICLE_RELEVANCE, null, objective_vehicle_relevance);
			
			objective_vehicle_relevance = new ContentValues();
			objective_vehicle_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_vehicle_relevance.put("vehicle_type_id", getVehicleTypeId(db,"SUV"));
			objective_vehicle_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_VEHICLE_RELEVANCE, null, objective_vehicle_relevance);
			
			//Patrol - vehicle
			objective_vehicle_relevance = new ContentValues();
			objective_vehicle_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_vehicle_relevance.put("vehicle_type_id", getVehicleTypeId(db,"Sedan"));
			objective_vehicle_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_VEHICLE_RELEVANCE, null, objective_vehicle_relevance);
			
			objective_vehicle_relevance = new ContentValues();
			objective_vehicle_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_vehicle_relevance.put("vehicle_type_id", getVehicleTypeId(db,"Pickup Truck"));
			objective_vehicle_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_VEHICLE_RELEVANCE, null, objective_vehicle_relevance);
			
			objective_vehicle_relevance = new ContentValues();
			objective_vehicle_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_vehicle_relevance.put("vehicle_type_id", getVehicleTypeId(db,"Tank"));
			objective_vehicle_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_VEHICLE_RELEVANCE, null, objective_vehicle_relevance);
			
			objective_vehicle_relevance = new ContentValues();
			objective_vehicle_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_vehicle_relevance.put("vehicle_type_id", getVehicleTypeId(db,"SUV"));
			objective_vehicle_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_VEHICLE_RELEVANCE, null, objective_vehicle_relevance);

			//Humanitarian - landmark
			
			ContentValues objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Arena"));
			objective_landmark_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Religious Site"));
			objective_landmark_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Monument"));
			objective_landmark_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Building"));
			objective_landmark_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Military Base"));
			objective_landmark_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			//Patrol - landmark
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Arena"));
			objective_landmark_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Religious Site"));
			objective_landmark_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Monument"));
			objective_landmark_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Building"));
			objective_landmark_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			objective_landmark_relevance = new ContentValues();
			objective_landmark_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_landmark_relevance.put("landmark_type_id", getLandmarkTypeId(db,"Military Base"));
			objective_landmark_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_LANDMARK_RELEVANCE, null, objective_landmark_relevance);
			
			
			//Humanitarian - Resource
			
			ContentValues objective_resource_relevance = new ContentValues();
			objective_resource_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_resource_relevance.put("resource_type_id", getResourceTypeId(db,"Food"));
			objective_resource_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_RESOURCE_RELEVANCE, null, objective_resource_relevance);
			
			objective_resource_relevance = new ContentValues();
			objective_resource_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_resource_relevance.put("resource_type_id", getResourceTypeId(db,"Munition"));
			objective_resource_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_RESOURCE_RELEVANCE, null, objective_resource_relevance);
			
			objective_resource_relevance = new ContentValues();
			objective_resource_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_resource_relevance.put("resource_type_id", getResourceTypeId(db,"Fuel"));
			objective_resource_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_RESOURCE_RELEVANCE, null, objective_resource_relevance);
			
			objective_resource_relevance = new ContentValues();
			objective_resource_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_resource_relevance.put("resource_type_id", getResourceTypeId(db,"Water"));
			objective_resource_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_RESOURCE_RELEVANCE, null, objective_resource_relevance);
			
			//Patrol - Resource
			
			objective_resource_relevance = new ContentValues();
			objective_resource_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_resource_relevance.put("resource_type_id", getResourceTypeId(db,"Food"));
			objective_resource_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_RESOURCE_RELEVANCE, null, objective_resource_relevance);
			
			objective_resource_relevance = new ContentValues();
			objective_resource_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_resource_relevance.put("resource_type_id", getResourceTypeId(db,"Munition"));
			objective_resource_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_RESOURCE_RELEVANCE, null, objective_resource_relevance);
			
			objective_resource_relevance = new ContentValues();
			objective_resource_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_resource_relevance.put("resource_type_id", getResourceTypeId(db,"Fuel"));
			objective_resource_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_RESOURCE_RELEVANCE, null, objective_resource_relevance);
			
			objective_resource_relevance = new ContentValues();
			objective_resource_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_resource_relevance.put("resource_type_id", getResourceTypeId(db,"Water"));
			objective_resource_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_RESOURCE_RELEVANCE, null, objective_resource_relevance);
			
			
			//Humanitarian - Person
			
			ContentValues objective_person_relevance = new ContentValues();
			objective_person_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_person_relevance.put("person_type_id", getPersonTypeId(db,"Military"));
			objective_person_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_PERSON_RELEVANCE, null, objective_person_relevance);
			
			objective_person_relevance = new ContentValues();
			objective_person_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_person_relevance.put("person_type_id", getPersonTypeId(db,"Civilian"));
			objective_person_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_PERSON_RELEVANCE, null, objective_person_relevance);
			
			objective_person_relevance = new ContentValues();
			objective_person_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_person_relevance.put("person_type_id", getPersonTypeId(db,"Insurgent"));
			objective_person_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_PERSON_RELEVANCE, null, objective_person_relevance);
			
			objective_person_relevance = new ContentValues();
			objective_person_relevance.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective_person_relevance.put("person_type_id", getPersonTypeId(db,"Law Enforcement"));
			objective_person_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_PERSON_RELEVANCE, null, objective_person_relevance);
			
			//Patrol - Person
			
			objective_person_relevance = new ContentValues();
			objective_person_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_person_relevance.put("person_type_id", getPersonTypeId(db,"Military"));
			objective_person_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_PERSON_RELEVANCE, null, objective_person_relevance);
			
			objective_person_relevance = new ContentValues();
			objective_person_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_person_relevance.put("person_type_id", getPersonTypeId(db,"Civilian"));
			objective_person_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_PERSON_RELEVANCE, null, objective_person_relevance);
			
			objective_person_relevance = new ContentValues();
			objective_person_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_person_relevance.put("person_type_id", getPersonTypeId(db,"Insurgent"));
			objective_person_relevance.put("relevance", HIGH_VALUE);
			db.insert(OBJECTIVE_PERSON_RELEVANCE, null, objective_person_relevance);
			
			objective_person_relevance = new ContentValues();
			objective_person_relevance.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective_person_relevance.put("person_type_id", getPersonTypeId(db,"Law Enforcement"));
			objective_person_relevance.put("relevance", LOW_VALUE);
			db.insert(OBJECTIVE_PERSON_RELEVANCE, null, objective_person_relevance);
			
			//Person
			
			ContentValues space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			long space_time_row = db.insert(SPACE_TIME, null, space_time);

			ContentValues location = new ContentValues();
			location.put("latitude", 35.349931);
			location.put("longitude", -116.594151);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);

			ContentValues friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",3.0);
			long friendliness_row = db.insert(FRIENDLINESS, null,friendliness);


			ContentValues person1 = new ContentValues();
			person1.put("description", "Suspicious Looking Man");
			person1.put("current_rating",30.0);
			person1.put("space_time_id", space_time_row);
			long person_row = db.insert(PERSON_TABLE, null, person1);

			ContentValues person_friendliness = new ContentValues();
			person_friendliness.put("person_id", person_row);
			person_friendliness.put("friendliness_id", friendliness_row);
			db.insert(PERSON_FRIENDLINESS, null, person_friendliness);
			
			
			//Person
			
			space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			space_time_row = db.insert(SPACE_TIME, null, space_time);

			location = new ContentValues();
			location.put("latitude", 35.339616);
			location.put("longitude", -116.592351);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);

			friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",3.0);
			friendliness_row = db.insert(FRIENDLINESS, null,friendliness);


			person1 = new ContentValues();
			person1.put("description", "Civilian");
			person1.put("current_rating",3.0);
			person1.put("space_time_id", space_time_row);
			person_row = db.insert(PERSON_TABLE, null, person1);

			person_friendliness = new ContentValues();
			person_friendliness.put("person_id", person_row);
			person_friendliness.put("friendliness_id", friendliness_row);
			db.insert(PERSON_FRIENDLINESS, null, person_friendliness);
			
			//Person
			
			space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			space_time_row = db.insert(SPACE_TIME, null, space_time);

			location = new ContentValues();
			location.put("latitude", 35.349379);
			location.put("longitude", -116.593825);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);

			friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",70.0);
			friendliness_row = db.insert(FRIENDLINESS, null,friendliness);


			person1 = new ContentValues();
			person1.put("description", "Police Officer");
			person1.put("current_rating",70.0);
			person1.put("space_time_id", space_time_row);
			person_row = db.insert(PERSON_TABLE, null, person1);

			person_friendliness = new ContentValues();
			person_friendliness.put("person_id", person_row);
			person_friendliness.put("friendliness_id", friendliness_row);
			db.insert(PERSON_FRIENDLINESS, null, person_friendliness);

			//Now add a vehicle

			space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			space_time_row = db.insert(SPACE_TIME, null, space_time);

			location = new ContentValues();
			location.put("latitude", 35.349379);
			location.put("longitude", -116.593825);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);

			friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",-30.0);
			friendliness_row = db.insert(FRIENDLINESS, null,friendliness);


			ContentValues vehicle1 = new ContentValues();
			vehicle1.put("description", "Toyota Pickup");
			vehicle1.put("color", "Red");
			vehicle1.put("current_rating",30.0);
			vehicle1.put("vehicle_type_id", getVehicleTypeId(db,"Pickup Truck"));
			vehicle1.put("space_time_id", space_time_row);
			long vehicle_row = db.insert(VEHICLE_TABLE, null, vehicle1);

			ContentValues vehicle_friendliness = new ContentValues();
			vehicle_friendliness.put("vehicle_id", vehicle_row);
			vehicle_friendliness.put("friendliness_id", friendliness_row);
			db.insert(VEHICLE_FRIENDLINESS, null, vehicle_friendliness);

			//Now add a landmark

			space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			space_time_row = db.insert(SPACE_TIME, null, space_time);

			location = new ContentValues();
			location.put("latitude", 35.348881);
			location.put("longitude", -116.593948);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);



			ContentValues landmark1 = new ContentValues();
			landmark1.put("description", "Building");
			landmark1.put("current_rating",67.0);
			landmark1.put("landmark_type_id", getLandmarkTypeId(db,"Monument"));
			landmark1.put("space_time_id", space_time_row);
			row = db.insert(LANDMARK_TABLE, null, landmark1);

			friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",-30.0);
			row = db.insert(FRIENDLINESS, null,friendliness);

			ContentValues landmark_friendliness = new ContentValues();
			landmark_friendliness.put("landmark_id", 1);
			landmark_friendliness.put("friendliness_id", row);
			db.insert(LANDMARK_FRIENDLINESS, null, landmark_friendliness);


			//Now add a resource




			space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			space_time_row = db.insert(SPACE_TIME, null, space_time);

			location = new ContentValues();
			location.put("latitude", 38.889118);
			location.put("longitude", -77.04693);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);



			ContentValues resource1 = new ContentValues();
			resource1.put("description", "Gas Station");
			resource1.put("value", 12);
			resource1.put("current_rating",-45.0);
			resource1.put("resource_type_id", getResourceTypeId(db,"Fuel"));
			resource1.put("space_time_id", space_time_row);
			row = db.insert(RESOURCE_TABLE, null, resource1);

			friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",-30.0);
			row = db.insert(FRIENDLINESS, null,friendliness);

			ContentValues resource_friendliness = new ContentValues();
			resource_friendliness.put("resource_id", 1);
			resource_friendliness.put("friendliness_id", row);
			db.insert(RESOURCE_FRIENDLINESS, null, resource_friendliness);


			//Now add an objective
			
			space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			space_time_row = db.insert(SPACE_TIME, null, space_time);

			location = new ContentValues();
			location.put("latitude", 35.349157);
			location.put("longitude", -116.597363);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.349352);
			location.put("longitude", -116.595315);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.349464);
			location.put("longitude", -116.594274);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.350360);
			location.put("longitude", -116.594896);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.350390);
			location.put("longitude", -116.593678);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.350612);
			location.put("longitude", -116.593081);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.349893);
			location.put("longitude", -116.591055);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			ContentValues objective1 = new ContentValues();
			objective1.put("description", "Patrol");
			objective1.put("objective_type_id", getObjectiveTypeId(db,"Patrol"));
			objective1.put("space_time_id", space_time_row);
			row = db.insert(OBJECTIVE_TABLE, null, objective1);
			
			
			//Add a Humanitarian Objective
			space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			space_time_row = db.insert(SPACE_TIME, null, space_time);

			location = new ContentValues();
			location.put("latitude", 35.349157);
			location.put("longitude", -116.597363);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.349352);
			location.put("longitude", -116.595315);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.349464);
			location.put("longitude", -116.594274);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.350360);
			location.put("longitude", -116.594896);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.350390);
			location.put("longitude", -116.593678);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.350612);
			location.put("longitude", -116.593081);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			location = new ContentValues();
			location.put("latitude", 35.349893);
			location.put("longitude", -116.591055);
			location.put("elevation", 125.5);
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			row = db.insert(LOCATION, null, location);
			
			ContentValues objective2 = new ContentValues();
			objective1.put("description", "Patrol");
			objective1.put("objective_type_id", getObjectiveTypeId(db,"Humanitarian"));
			objective1.put("space_time_id", space_time_row);
			row = db.insert(OBJECTIVE_TABLE, null, objective1);
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

		private Long getVehicleTypeId(String name)
		{
			SQLiteDatabase db = getReadableDatabase();

			Long ret = getVehicleTypeId(db,name);
			db.close();
			return ret;
		}

		private long getVehicleTypeId(SQLiteDatabase db, String name)
		{
			long ret = -1;

			Cursor cur = db.query(VEHICLE_TYPE, new String[] {"id"}, "type=?", new String[] {name}, null, null, null);
			if(cur.moveToFirst())
			{
				ret = cur.getLong(cur.getColumnIndex("id"));
			}
			
			return ret;
		}

		private Long getLocationTypeId(String name)
		{
			SQLiteDatabase db = getReadableDatabase();

			Long ret = getLocationTypeId(db,name);
			db.close();
			return ret;
		}

		private long getLocationTypeId(SQLiteDatabase db, String name)
		{
			long ret = -1;

			Cursor cur = db.query(LOCATION_TYPE, new String[] {"id"}, "type=?", new String[] {name}, null, null, null);
			if(cur.moveToFirst())
			{
				ret = cur.getLong(cur.getColumnIndex("id"));
			}

			return ret;
		}

		private Long getResourceTypeId(String name)
		{
			SQLiteDatabase db = getReadableDatabase();

			Long ret = getResourceTypeId(db,name);
			db.close();
			return ret;
		}

		private long getResourceTypeId(SQLiteDatabase db , String name)
		{
			long ret = -1;

			Cursor cur = db.query(RESOURCE_TYPE, new String[] {"id"}, "type=?", new String[] {name}, null, null, null);
			if(cur.moveToFirst())
			{
				ret = cur.getLong(cur.getColumnIndex("id"));
			}
			
			return ret;
		}

		private Long getLandmarkTypeId(String name)
		{
			SQLiteDatabase db = getReadableDatabase();

			Long ret = getLandmarkTypeId(db,name);
			db.close();
			return ret;
		}

		private long getLandmarkTypeId(SQLiteDatabase db, String name)
		{
			long ret = -1;

			Cursor cur = db.query(LANDMARK_TYPE, new String[] {"id"}, "type=?", new String[] {name}, null, null, null);
			if(cur.moveToFirst())
			{
				ret = cur.getLong(cur.getColumnIndex("id"));
			}
			
			return ret;
		}
		
		private long getObjectiveTypeId(SQLiteDatabase db , String name)
		{
			long ret = -1;

			Cursor cur = db.query(OBJECTIVE_TYPE, new String[] {"id"}, "type=?", new String[] {name}, null, null, null);
			if(cur.moveToFirst())
			{
				ret = cur.getLong(cur.getColumnIndex("id"));
			}
			
			return ret;
		}
		
		private long getPersonTypeId(SQLiteDatabase db , String name)
		{
			long ret = -1;

			Cursor cur = db.query(PERSON_TYPE, new String[] {"id"}, "type=?", new String[] {name}, null, null, null);
			if(cur.moveToFirst())
			{
				ret = cur.getLong(cur.getColumnIndex("id"));
			}
			
			return ret;
		}
		
		private Cursor getPersonTypeForPerson(Long id)
		{
			String sql = "SELECT PersonType.type from person_type PersonType, person p where p.id=" + id.longValue() +
			" AND p.person_type_id = VehicleType.id";
			return getReadableDatabase().rawQuery(sql,	null);
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

		public Cursor getVehicles()
		{
			return       	
			getReadableDatabase().query(true, "vehicle", null,
					null, null,
					null, null, null, null);
		}

		private Cursor getCurrentLocationForVehicle(Long id)
		{
			String sql = "SELECT loc.* from location loc, space_time st, vehicle v where v.id=" + id.longValue() +
			" AND v.space_time_id = st.id AND loc.space_time_id = st.id ORDER BY loc.time";
			return getReadableDatabase().rawQuery(sql,	null);
		}

		private Cursor getVehicleTypeForVehicle(Long id)
		{
			String sql = "SELECT VehicleType.type from vehicle_type VehicleType, vehicle v where v.id=" + id.longValue() +
			" AND v.vehicle_type_id = VehicleType.id";
			return getReadableDatabase().rawQuery(sql,	null);
		}

		public Cursor getLandmarks()
		{
			return       	
			getReadableDatabase().query(true, "landmark", null,
					null, null,
					null, null, null, null);
		}

		private Cursor getCurrentLocationForLandmark(Long id)
		{
			String sql = "SELECT loc.* from location loc, space_time st, landmark l where l.id=" + id.longValue() +
			" AND l.space_time_id = st.id AND loc.space_time_id = st.id ORDER BY loc.time";
			return getReadableDatabase().rawQuery(sql,	null);
		}

		private Cursor getLandmarkTypeForLandmark(Long id)
		{
			String sql = "SELECT LandmarkType.type from landmark_type LandmarkType, resource r where r.id=" + id.longValue() +
			" AND r.landmark_type_id = LandmarkType.id";
			return getReadableDatabase().rawQuery(sql,	null);
		}

		public Cursor getResources()
		{
			return       	
			getReadableDatabase().query(true, "resource", null,
					null, null,
					null, null, null, null);
		}

		private Cursor getCurrentLocationForResource(Long id)
		{
			String sql = "SELECT loc.* from location loc, space_time st, resource r where r.id=" + id.longValue() +
			" AND r.space_time_id = st.id AND loc.space_time_id = st.id ORDER BY loc.time";
			return getReadableDatabase().rawQuery(sql,	null);
		}

		private Cursor getResourceTypeForResource(Long id)
		{
			String sql = "SELECT ResourceType.type from resource_type ResourceType, resource r where r.id=" + id.longValue() +
			" AND r.resource_type_id = ResourceType.id";
			return getReadableDatabase().rawQuery(sql,	null);
		}

		private boolean insertPerson(Thing thing)
		{
			SQLiteDatabase db = this.getWritableDatabase();


			ContentValues space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			long space_time_row = db.insert(SPACE_TIME, null, space_time);

			ContentValues location = new ContentValues();
			location.put("latitude", thing.getLatitude());
			location.put("longitude", thing.getLongitude());
			location.put("elevation", thing.getElevation());
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db,"GPS"));
			db.insert(LOCATION, null, location);

			ContentValues friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",thing.getFriendliness());
			long friendliness_row = db.insert(FRIENDLINESS, null,friendliness);


			ContentValues person1 = new ContentValues();
			person1.put("description", thing.getDescription());
			person1.put("current_rating",thing.getFriendliness());
			person1.put("space_time_id", space_time_row);
			long person_row = db.insert(PERSON_TABLE, null, person1);

			ContentValues person_friendliness = new ContentValues();
			person_friendliness.put("person_id", person_row);
			person_friendliness.put("friendliness_id", friendliness_row);
			long person_friendliness_row = db.insert(PERSON_FRIENDLINESS, null, person_friendliness);
			db.close();
			return true;
		}

		private boolean insertLandmark(Thing thing)
		{
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			long space_time_row = db.insert(SPACE_TIME, null, space_time);

			ContentValues location = new ContentValues();
			location.put("latitude", thing.getLatitude());
			location.put("longitude", thing.getLongitude());
			location.put("elevation", thing.getElevation());
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db, "GPS"));
			long location_row = db.insert(LOCATION, null, location);

			ContentValues friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",thing.getFriendliness());
			long friendliness_row = db.insert(FRIENDLINESS, null,friendliness);

			ContentValues landmark1 = new ContentValues();
			landmark1.put("description", thing.getDescription());
			landmark1.put("current_rating",thing.getFriendliness());
//			landmark1.put("landmark_type_id", getLandmarkTypeId(db, thing.getSubType()));
			landmark1.put("space_time_id", space_time_row);
			long landmark = db.insert(LANDMARK_TABLE, null, landmark1);



			ContentValues landmark_friendliness = new ContentValues();
			landmark_friendliness.put("landmark_id", landmark);
			landmark_friendliness.put("friendliness_id", friendliness_row);
			db.insert(LANDMARK_FRIENDLINESS, null, landmark_friendliness);

			db.close();
			return true;
		}

		private boolean insertVehicle(Thing thing)
		{
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			long space_time_row = db.insert(SPACE_TIME, null, space_time);

			ContentValues location = new ContentValues();
			location.put("latitude", thing.getLatitude());
			location.put("longitude", thing.getLongitude());
			location.put("elevation", thing.getElevation());
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db, "GPS"));
			long location_row = db.insert(LOCATION, null, location);

			ContentValues friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",thing.getFriendliness());
			long friendliness_row = db.insert(FRIENDLINESS, null,friendliness);

			ContentValues vehicle1 = new ContentValues();
			vehicle1.put("description", thing.getDescription());
			vehicle1.put("color", "");
			vehicle1.put("current_rating",thing.getFriendliness());
//			vehicle1.put("vehicle_type_id", getVehicleTypeId(db, thing.getSubType()));
			vehicle1.put("space_time_id", space_time_row);
			long vehicle_row = db.insert(VEHICLE_TABLE, null, vehicle1);



			ContentValues vehicle_friendliness = new ContentValues();
			vehicle_friendliness.put("vehicle_id", vehicle_row);
			vehicle_friendliness.put("friendliness_id", friendliness_row);
			db.insert(VEHICLE_FRIENDLINESS, null, vehicle_friendliness);

			db.close();
			return true;
		}

		private boolean insertResource(Thing thing)
		{
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues space_time = new ContentValues();
			space_time.put("timestamp",System.currentTimeMillis());
			long space_time_row = db.insert(SPACE_TIME, null, space_time);

			ContentValues location = new ContentValues();
			location.put("latitude", thing.getLatitude());
			location.put("longitude", thing.getLongitude());
			location.put("elevation", thing.getElevation());
			location.put("time", System.currentTimeMillis());
			location.put("space_time_id", space_time_row);
			location.put("type_id", getLocationTypeId(db, "GPS"));
			long location_row = db.insert(LOCATION, null, location);

			ContentValues friendliness = new ContentValues();
			friendliness.put("timestamp",System.currentTimeMillis());
			friendliness.put("rating",thing.getFriendliness());
			long friendliness_row = db.insert(FRIENDLINESS, null,friendliness);

			ContentValues resource1 = new ContentValues();
			resource1.put("description", thing.getDescription());
			resource1.put("value", -1);
			resource1.put("current_rating",thing.getFriendliness());
			resource1.put("resource_type_id", getResourceTypeId(db, thing.getSubType()));
			resource1.put("space_time_id", space_time_row);
			long resource_row = db.insert(RESOURCE_TABLE, null, resource1);   		

			ContentValues vehicle_friendliness = new ContentValues();
			vehicle_friendliness.put("vehicle_id", resource_row);
			vehicle_friendliness.put("friendliness_id", friendliness_row);
			db.insert(VEHICLE_FRIENDLINESS, null, vehicle_friendliness);

			db.close();
			return true;
		}
		
		private Cursor getPatrolObjective()
		{
			SQLiteDatabase db = getReadableDatabase();
			Long objTypeId = getObjectiveTypeId(db,"Patrol");
			return db.query(true, "objective", null,
					"objective_type_id=?", new String[] {Long.toString(objTypeId)},
					null, null, null, null);
		}
		
		private Cursor getHumanitarianObjective()
		{
			SQLiteDatabase db = getReadableDatabase();
			Long objTypeId = getObjectiveTypeId(db,"Humanitarian");
			return db.query(true, "objective", null,
					"objective_type_id=?", new String[] {Long.toString(objTypeId)},
					null, null, null, null);
		}
		
		private Cursor getCurrentLocationForObjective(Long id)
		{
			String sql = "SELECT loc.* from location loc, space_time st, objective obj where obj.id=" + id.longValue() +
			" AND obj.space_time_id = st.id AND loc.space_time_id = st.id ORDER BY loc.time";
			return getReadableDatabase().rawQuery(sql,	null);
		}
		
		private Cursor getVehicleRelevanceFactor(Long thingID, Long objectiveID)
		{
			
			String sql = "SELECT rel.* from " + OBJECTIVE_VEHICLE_RELEVANCE + " rel, objective obj, vehicle veh " +
			"where veh.id=" + thingID.longValue() + " AND veh.vehicle_type_id = rel.vehicle_type_id AND " +
			"obj.objective_type_id = rel.objective_type_id AND obj.id = " + objectiveID.longValue();
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
//		int rows = cur.getCount();
//		if(rows >0)
//		{
//			cur.moveToFirst();
//			Double lat = cur.getDouble(cur.getColumnIndex("latitude"));
//			System.out.println("Lat:" + lat);
//		}

		return cur;
	}

	public Cursor getPersonTypeForPerson(Long id)
	{
		return mOpenHelper.getPersonTypeForPerson(id);
	}
	
	public Cursor getVehicles()
	{
		return mOpenHelper.getVehicles();
	}

	public Cursor getCurrentLocationForVehicle(Long id)
	{
		Cursor cur = mOpenHelper.getCurrentLocationForVehicle( id);
//		int rows = cur.getCount();
//		if(rows >0)
//		{
//			cur.moveToFirst();
//			Double lat = cur.getDouble(cur.getColumnIndex("latitude"));
//			System.out.println("Lat:" + lat);
//		}

		return cur;
	}
	
	public Cursor getVehicleTypeForVehicle(Long id)
	{
		return mOpenHelper.getVehicleTypeForVehicle(id);
	}

	public Cursor getLandmarks()
	{
		return mOpenHelper.getLandmarks();
	}

	public Cursor getCurrentLocationForLandmark(Long id)
	{
		Cursor cur = mOpenHelper.getCurrentLocationForLandmark( id);
//		int rows = cur.getCount();
//		if(rows >0)
//		{
//			cur.moveToFirst();
//			Double lat = cur.getDouble(cur.getColumnIndex("latitude"));
//			System.out.println("Lat:" + lat);
//		}

		return cur;
	}

	public Cursor getResources()
	{
		return mOpenHelper.getResources();
	}
	
	public Cursor getResourceTypeForResource(Long id)
	{
		return mOpenHelper.getResourceTypeForResource(id);
	}

	public Cursor getCurrentLocationForResource(Long id)
	{
		Cursor cur = mOpenHelper.getCurrentLocationForResource( id);
//		int rows = cur.getCount();
//		if(rows >0)
//		{
//			cur.moveToFirst();
//			Double lat = cur.getDouble(cur.getColumnIndex("latitude"));
//			System.out.println("Lat:" + lat);
//		}

		return cur;
	}

	public long insertLocation(double latitude, double longitude, double elevation, long timestamp, long space_time_id)
	{
		long ret = -1;
		ret = mOpenHelper.insertLocation(latitude, longitude, elevation, timestamp, space_time_id, 1);
		return ret;
	}

	public boolean insertThing(Thing thing)
	{
		boolean ret = false;

		if(thing.getType() == Type.PERSON)
		{
			ret = mOpenHelper.insertPerson(thing);
		}
		else if (thing.getType() == Type.LANDMARK)
		{
			ret = mOpenHelper.insertLandmark(thing);
		}
		else if(thing.getType() == Type.RESOURCE)
		{
			ret = mOpenHelper.insertResource(thing);
		}
		else if(thing.getType() == Type.VEHICLE)
		{
			ret = mOpenHelper.insertVehicle(thing);
		}

		return ret;

	}


	public Cursor getPatrolObjective()
	{
		return mOpenHelper.getPatrolObjective();
	}
	
	public Cursor getHumanitarianObjective()
	{
		return mOpenHelper.getHumanitarianObjective();
	}
	
	public Cursor getVehicleRelevanceFactor(Long thingID, Long objectiveID)
	{
		return mOpenHelper.getVehicleRelevanceFactor(thingID, objectiveID);
	}
	
	public Cursor getCurrentLocationForObjective(Long id)
	{
		Cursor cur = mOpenHelper.getCurrentLocationForObjective( id);
		
		while(cur.moveToNext())
		{
			Double lat = cur.getDouble(cur.getColumnIndex("latitude"));
			System.out.println("Lat:" + lat);
		}
		cur.moveToFirst();
//		int rows = cur.getCount();
//		if(rows >0)
//		{
//			cur.moveToFirst();
//			Double lat = cur.getDouble(cur.getColumnIndex("latitude"));
//			System.out.println("Lat:" + lat);
//		}

		return cur;
	}
}
