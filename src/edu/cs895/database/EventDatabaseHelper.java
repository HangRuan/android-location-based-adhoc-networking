package edu.cs895.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "eventsdata";
	private static final int DATABASE_VERSION = 1;

	public EventDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		EventTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		EventTable.onUpgrade(db, oldVersion, newVersion);
	}	
}
