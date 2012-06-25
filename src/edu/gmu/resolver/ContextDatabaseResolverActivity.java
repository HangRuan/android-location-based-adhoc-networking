package edu.gmu.resolver;


import java.nio.ByteBuffer;

import edu.gmu.hodum.ContentDatabaseAPI;
import edu.gmu.hodum.sei.common.Objective;
import edu.gmu.hodum.sei.common.SimpleXMLSerializer;
import edu.gmu.hodum.sei.common.Thing;
import edu.gmu.hodum.sei.common.Thing.Type;

import edu.gmu.resolver.R;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;


public class ContextDatabaseResolverActivity extends Activity {
	public static final String SEND_DATA = "edu.gmu.hodum.SEND_DATA";
	private static final String baseURI = "content://edu.gmu.provider.cursor.dir";//"content://edu.gmu.provider.dir/people/location";
	public static final String INITIALIZE_NETWORK = "edu.gmu.hodum.INITIALIZE_NETWORK";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent broadcastIntent = new Intent(INITIALIZE_NETWORK);
		broadcastIntent.putExtra("channel", "8");
		this.sendBroadcast(broadcastIntent);
		
//		ContentDatabaseAPI api = new ContentDatabaseAPI(this);
//		Objective obj = api.getPatrolObjective();
//		System.out.println(obj.getDescription());
		
		Thread thread = new Thread(){
			public void run(){
				int counter = 0;
				try {
					sleep(7000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.d("Sender", "Starting to send data");
				
				while (counter < 100)
				{
					counter++;
					Log.d("Sender", "Sending data");
					sendBroadcastPerson(counter);
					try {
						sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					sendBroadcastLandmark(counter);
//					try {
//						sleep(5000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					sendBroadcastResource(counter);
//					try {
//						sleep(5000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					sendBroadcastVehicle(counter);
					try {
						sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		thread.start();
		
	}

	private void sendBroadcastPerson(int counter)
	{

		Thing thing = new Thing();
		thing.setDescription("Testing!");
		thing.setElevation(230.0);
		thing.setLatitude(38.88255 + (.002*counter));
		thing.setLongitude(-77.049897 - .002 );
		thing.setFriendliness(100*Math.random());
		thing.setRelevance(67.0);
		thing.setType(Type.PERSON);
		thing.setSubType("Military");
		SimpleXMLSerializer<Thing> serializer = new SimpleXMLSerializer<Thing>();
		byte[] data;
		try {
			data = serializer.serialize(thing);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		ByteBuffer b = ByteBuffer.allocate(data.length + 8);
		b.putLong(1000);
		b.put(data);

		byte[] buff = b.array();

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent(SEND_DATA);
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		sendBroadcast(broadcastIntent);
	}
	
	private void sendBroadcastLandmark(int counter)
	{

		Thing thing = new Thing();
		thing.setDescription("Building");
		thing.setElevation(230.0);
		thing.setLatitude(38.88255 + (.002*counter));
		thing.setLongitude(-77.049897  );
		thing.setFriendliness(100*Math.random());
		thing.setRelevance(67.0);
		thing.setType(Type.LANDMARK);
		thing.setSubType("Building");
		SimpleXMLSerializer<Thing> serializer = new SimpleXMLSerializer<Thing>();
		byte[] data;
		try {
			data = serializer.serialize(thing);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		ByteBuffer b = ByteBuffer.allocate(data.length + 8);
		b.putLong(1000);
		b.put(data);

		byte[] buff = b.array();

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent(SEND_DATA);
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		sendBroadcast(broadcastIntent);
	}
	
	private void sendBroadcastVehicle(int counter)
	{

		Thing thing = new Thing();
		thing.setDescription("Tank!");
		thing.setElevation(230.0);
		thing.setLatitude(38.88255 + (.002*counter));
		thing.setLongitude(-77.049897 );
		thing.setFriendliness(100*Math.random());
		thing.setRelevance(67.0);
		thing.setType(Type.VEHICLE);
		thing.setSubType("Tank");
		SimpleXMLSerializer<Thing> serializer = new SimpleXMLSerializer<Thing>();
		byte[] data;
		try {
			data = serializer.serialize(thing);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		ByteBuffer b = ByteBuffer.allocate(data.length + 8);
		b.putLong(1000);
		b.put(data);

		byte[] buff = b.array();

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent(SEND_DATA);
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		sendBroadcast(broadcastIntent);
	}
	
	private void sendBroadcastResource(int counter)
	{

		Thing thing = new Thing();
		thing.setDescription("Testing!");
		thing.setElevation(230.0);
		thing.setLatitude(38.88255 + (.002*counter));
		thing.setLongitude(-77.049897 );
		thing.setFriendliness(100*Math.random());
		thing.setRelevance(67.0);
		thing.setType(Type.RESOURCE);
		thing.setSubType("Fuel");
		SimpleXMLSerializer<Thing> serializer = new SimpleXMLSerializer<Thing>();
		byte[] data;
		try {
			data = serializer.serialize(thing);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		ByteBuffer b = ByteBuffer.allocate(data.length + 8);
		b.putLong(1000);
		b.put(data);

		byte[] buff = b.array();

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent(SEND_DATA);
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		sendBroadcast(broadcastIntent);
	}
}