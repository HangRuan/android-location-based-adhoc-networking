package edu.cs895.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import android.util.Log;

import edu.cs895.message.Event;
import edu.cs895.message.MessageBuffer;

public class Coder {
	private static final String TAG = "CODER: "; 

	public static Event decodeEvent(MessageBuffer msgBuff) {
		EventType eventType = null;
		Timestamp timestamp = null;
		String msgId = null;
		
		//read in buffer into the values needed to enter the event into the interest engine
		ByteArrayInputStream bais = new ByteArrayInputStream(msgBuff.getBuffer());
		DataInputStream dis = new DataInputStream(bais);
		try {
			Log.d(TAG + "DECODE", msgBuff.getBuffer().toString());
			
			//get message id to store in database
			int len = dis.readInt();

			StringBuilder sb = new StringBuilder();
			for (int count=0; count < len; count++){
				sb.append(dis.readChar());
			}
			msgId = sb.toString();

			//get eventType and Timestamp
			eventType = EventType.getFromValue(dis.readInt()); 
			Log.d(TAG + "DECODE", "The EventType for is: " + eventType);			
			timestamp = new Timestamp(dis.readLong()); //check for possible exceptions and handle
			Log.d(TAG + "DECODE", "The timestamp is: " + timestamp);

			//done -- cleanup
			dis.close();
		}
		catch(IOException ex)
		{
			Log.d(TAG + "DECODE", "IO Exception in decodeEvent");
		}

		Event event = Event.getInstance(eventType, timestamp, msgId, msgBuff.getTargetLoc(), msgBuff.getOrigLoc());
		Log.d(TAG + "DECODE", "decoded message: " + event.toString());
		return event;
	}

	public static byte[] encodeEvent(Event event) {
		
		//need to write out to a message buffer for use by the network
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			//write out data to byte buffer
			dos.writeInt(event.getMsgId().length());
			dos.writeChars(event.getMsgId());
			dos.writeInt(event.getEventType().getValueOf());
			dos.writeLong(event.getTimestamp().getTime());

			//close streams
			dos.flush();
			dos.close();
			final byte[] buff = baos.toByteArray();
			return buff;
		}
		catch(IOException ex)
		{
			Log.d(TAG + "ENCODE", "IO Exception in encodeEvent");
		}		
		return null;
	}
}
