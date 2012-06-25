package edu.gmu.hodum.sei.gesture.service;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import edu.gmu.hodum.sei.common.SimpleXMLSerializer;
import edu.gmu.hodum.sei.common.Thing;
import edu.gmu.hodum.sei.common.Thing.Type;

public class Broadcaster {
	
	static String SEND_DATA = "edu.gmu.hodum.SEND_DATA";

	public static void sendBroadcast(int counter, Context context){

		Thing thing = new Thing();
		thing.setDescription("Testing!");
		thing.setElevation(230.0);
		thing.setLatitude(38.88255 + (.02*counter));
		thing.setLongitude(-77.049897 + (.02*counter));
		thing.setFriendliness(55.0);
		thing.setRelevance(67.0);
		thing.setType(Type.PERSON);
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
		Intent broadcastIntent = new Intent("edu.gmu.hodum.SEND_DATA");
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		context.sendBroadcast(broadcastIntent);
	}
	public static void sendBroadcastPerson(Location loc, Context context){
		System.out.println("SendBroadcast Person");

		Thing thing = new Thing();
		thing.setDescription("Testing with Location!");
		thing.setElevation(230.0);
		thing.setLatitude(loc.getLatitude());
		thing.setLongitude(loc.getLongitude());
		thing.setFriendliness(55.0);
		thing.setRelevance(67.0);
		thing.setType(Type.PERSON);

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
		broadcastIntent.putExtra("latitude", loc.getLatitude());
		broadcastIntent.putExtra("longitude", loc.getLongitude());
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		context.sendBroadcast(broadcastIntent);
	}
	public static void sendBroadcastLandmark(Location loc, Context context){

            Thing thing = new Thing();
            thing.setDescription("Building");
            thing.setElevation(230.0);
            thing.setLatitude(loc.getLatitude());
            thing.setLongitude(loc.getLongitude());
            thing.setFriendliness(55.0);
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
            broadcastIntent.putExtra("latitude", loc.getLatitude());
            broadcastIntent.putExtra("longitude", loc.getLongitude());
            broadcastIntent.putExtra("radius",200.0d);
            broadcastIntent.putExtra("data",buff);
            context.sendBroadcast(broadcastIntent);
    }
	public static void sendBroadcastVehicle(Location loc, Context context){

            Thing thing = new Thing();
            thing.setDescription("Tank!");
            thing.setElevation(230.0);
            thing.setLatitude(loc.getLatitude());
            thing.setLongitude(loc.getLongitude());
            thing.setFriendliness(55.0);
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
            broadcastIntent.putExtra("latitude", loc.getLatitude());
            broadcastIntent.putExtra("longitude", loc.getLongitude());
            broadcastIntent.putExtra("radius",200.0d);
            broadcastIntent.putExtra("data",buff);
            context.sendBroadcast(broadcastIntent);
    }
	public static void sendBroadcastResource(Location loc, Context context){

            Thing thing = new Thing();
            thing.setDescription("Testing!");
            thing.setElevation(230.0);
            thing.setLatitude(loc.getLatitude());
            thing.setLongitude(loc.getLongitude());
            thing.setFriendliness(55.0);
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
            broadcastIntent.putExtra("latitude", loc.getLatitude());
            broadcastIntent.putExtra("longitude", loc.getLongitude());
            broadcastIntent.putExtra("radius",200.0d);
            broadcastIntent.putExtra("data",buff);
            context.sendBroadcast(broadcastIntent);
    }
	
}
