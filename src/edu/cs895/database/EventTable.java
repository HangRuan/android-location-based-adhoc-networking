package edu.cs895.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EventTable {
	private static final String TAG = "EVENT TABLE";
	private static final String DB_TABLE = "events";
	
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_TABLE + " ("
                + EventDBAdapter.KEY_ROWID + " INTEGER PRIMARY KEY,"
                + EventDBAdapter.KEY_MESSAGE_ID + " TEXT,"
                + EventDBAdapter.KEY_EVENT_TYPE + " INTEGER,"
                + EventDBAdapter.KEY_TIMESTAMP + " REAL,"
                + EventDBAdapter.KEY_TARGET_LAT + " REAL,"
                + EventDBAdapter.KEY_TARGET_LONG + " REAL,"
                + EventDBAdapter.KEY_ORIG_LAT + " REAL,"                    
                + EventDBAdapter.KEY_ORIG_LONG + " REAL"
                + ");");
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS events");
        onCreate(db);
    }

}
