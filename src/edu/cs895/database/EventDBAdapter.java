package edu.cs895.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class EventDBAdapter {
	
    public static final String KEY_ROWID = "_id";
    public static final String KEY_MESSAGE_ID = "msg_id";
    public static final String KEY_EVENT_TYPE = "event_type";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_TARGET_LAT = "target_lat";
    public static final String KEY_TARGET_LONG = "target_long";
    public static final String KEY_ORIG_LAT = "orig_lat";
    public static final String KEY_ORIG_LONG = "orig_long";
    private static final String DB_TABLE = "events";
    private Context context;
    private SQLiteDatabase db;
    private EventDatabaseHelper dbHelper;

    public EventDBAdapter(Context context) {
    	this.context = context;
    }
    
    public EventDBAdapter open() throws SQLException {
    	dbHelper = new EventDatabaseHelper(context);
    	db = dbHelper.getWritableDatabase();
    	return this;
    }
    
    public void close(){
    	dbHelper.close();
    }
    
    public long createEvent(String msg_id, int eventType, long timestamp, double target_lat, double target_long,
    		double orig_lat, double orig_long) {
    	ContentValues values = createContentValues(msg_id, eventType, timestamp, target_lat, target_long, orig_lat, orig_long);
    	return db.insert(DB_TABLE, null, values);
    }
    
    public boolean deleteEvent(long rowId) {
    	return db.delete(DB_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public Cursor fetchAllEvents() {
    	return db.query(DB_TABLE, new String[] { KEY_ROWID, KEY_MESSAGE_ID, KEY_EVENT_TYPE, KEY_TIMESTAMP,
    			KEY_TARGET_LAT, KEY_TARGET_LONG, KEY_ORIG_LAT, KEY_ORIG_LONG}, null, null, null, null, null);
    }
    
    public Cursor fetchEvent(long rowId) throws SQLException {
    	Cursor mCursor = db.query(true, DB_TABLE, new String[] {KEY_ROWID, KEY_MESSAGE_ID, KEY_EVENT_TYPE, KEY_TIMESTAMP,
    			KEY_TARGET_LAT, KEY_TARGET_LONG, KEY_ORIG_LAT, KEY_ORIG_LONG}, KEY_ROWID + "=" + rowId, 
    			null, null, null, null,null);
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    	}
    	return mCursor;
    }
    
    private ContentValues createContentValues(String msg_id, int eventType, long timestamp, double target_lat, double target_long,
    		double orig_lat, double orig_lon) {
    	ContentValues values = new ContentValues();
    	values.put(KEY_MESSAGE_ID, msg_id);
    	values.put(KEY_EVENT_TYPE, eventType);
    	values.put(KEY_TIMESTAMP, timestamp);
    	values.put(KEY_TARGET_LAT, target_lat);
    	values.put(KEY_TARGET_LONG, target_long);
    	values.put(KEY_ORIG_LAT, target_lat);
    	values.put(KEY_ORIG_LONG, target_long);
    	return values;
    }
}
