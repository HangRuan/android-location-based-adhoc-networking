package edu.gmu.service;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.gmu.ContextDataProvider;
import edu.gmu.contextdb.utils.Constants;
import edu.gmu.contextdb.utils.Observation;
import edu.gmu.contextdb.utils.SimpleXMLSerializer;

import edu.gmu.contextdb.utils.TeamTrack;
import edu.gmu.hodum.sei.common.Thing;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

public class ContextDatabaseService extends Service {
	static Lock lock = new ReentrantLock();
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getAction();
		if(action != null && (action.equals(Constants.SEND_DATA) || action.equals(Constants.RECEIVE_DATA)))
		{
			new ProcessDataAsyncTask().execute(intent.getExtras(),null);
		}
		return Service.START_STICKY;
	}

	private class ProcessDataAsyncTask extends AsyncTask<Bundle, Object, Object>
	{
		
		@Override
		protected Object doInBackground(Bundle... params) {

			Thing payload = null;
			params[0].getDouble("latitude");
			params[0].getDouble("longitude");
			params[0].getDouble("originatingLatitude");
			params[0].getDouble("originatingLongitude");
			params[0].getDouble("radius");
			byte[] data = params[0].getByteArray("data");
			ByteBuffer b = ByteBuffer.wrap(data);

			long packetType = b.getLong();

			if(packetType == 1000)
			{
				byte[] xml = new byte[b.capacity()-8];
				b.get(xml);
				SimpleXMLSerializer<Thing> decoder = new SimpleXMLSerializer<Thing>();

				try {
					payload = decoder.deserialize(Thing.class, xml);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(payload != null)
				{
						lock.lock();
						ContextDataProvider db = new ContextDataProvider(ContextDatabaseService.this);
						db.insertThing(payload);
						lock.unlock();
				}
			}
			
			return payload;
		}
	}

}
