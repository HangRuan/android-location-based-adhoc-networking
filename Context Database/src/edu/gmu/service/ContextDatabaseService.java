package edu.gmu.service;

import java.nio.ByteBuffer;

import edu.gmu.ContextDataProvider;
import edu.gmu.contextdb.utils.Constants;
import edu.gmu.contextdb.utils.Observation;
import edu.gmu.contextdb.utils.SimpleXMLSerializer;

import edu.gmu.contextdb.utils.TeamTrack;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

public class ContextDatabaseService extends Service {

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

			TeamTrack payload = null;
			params[0].getDouble("latitude");
			params[0].getDouble("longitude");
			params[0].getDouble("originatingLatitude");
			params[0].getDouble("originatingLongitude");
			params[0].getDouble("radius");
			byte[] data = params[0].getByteArray("data");
			ByteBuffer b = ByteBuffer.wrap(data);

			long packetType = b.getLong();

			if(packetType != 1000)
			{
				byte[] xml = new byte[b.capacity()-8];
				b.get(xml);
				SimpleXMLSerializer<TeamTrack> decoder = new SimpleXMLSerializer<TeamTrack>();

				try {
					payload = decoder.deserialize(TeamTrack.class, xml);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(payload != null)
				{
					Observation observation = payload.getObservation();
					if(observation != null)
					{
						ContextDataProvider db = new ContextDataProvider(ContextDatabaseService.this);
						if(observation.getTime().equals(Constants.PERSON_OBSERVATION))
						{
							Cursor cur = db.getSpaceTimeForPerson(observation.getId());
							db.insertLocation(observation.getLocation().getLatitude(), observation.getLocation().getLatitude(),
									observation.getLocation().getElevation(), observation.getTime(), 
									cur.getLong(cur.getColumnIndex("id")));
						}
					}
				}
			}
			return payload;
		}
	}

}
