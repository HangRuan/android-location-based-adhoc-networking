package edu.cs895;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;

import edu.cs895.context.CurrentContext;
import edu.cs895.interest.InterestEngine;
import edu.cs895.interest.InterestReceiver;
import edu.cs895.message.Coder;
import edu.cs895.message.EventType;
import edu.cs895.message.MessageBuffer;
import edu.cs895.message.TransferQueue;
import edu.cs895.message.Event;
import edu.cs895.network.BroadcastNetworkManager;
import edu.cs895.network.NetworkManager;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class ProjectAActivity extends Activity implements OnClickListener, UIDisplay, InterestReceiver {

	private long counter = 0;
	private LocationHolder locHolder;
	private Notification notification;
	private NotificationManager mNotificationManager;
	private InterestEngine interestEngine;
	NetworkManager networkManager;
	CurrentContext myContext;

	//eventually queues will be received in the interest engine only
	private TransferQueue transferQ;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if(networkManager == null)
		{
			networkManager = BroadcastNetworkManager.instance(this, ((MyApplication)this.getApplication()).getLocationHolder());
			networkManager.startNetwork();

			//eventually queues will be received in the interest engine only
			transferQ = ((MyApplication)this.getApplication()).getTransferQ();
			transferQ.setNetworkManager(networkManager);
			networkManager.registerReceiver(transferQ);

			interestEngine = InterestEngine.getInstance();
			interestEngine.setUIDisplay(this);
			interestEngine.registerInterest(this);

			myContext = new CurrentContext();

		}
		//Once you have this locHolder, simply call getCurrentLocation everytime you
		//need a new location.
		locHolder = ((MyApplication)this.getApplication()).getLocationHolder();
		findViewById(R.id.fire).setOnClickListener(this);
		findViewById(R.id.evacPoint).setOnClickListener(this);
		findViewById(R.id.evacAll).setOnClickListener(this);

		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		((BroadcastNetworkManager)networkManager).shuttingDown();
		((ApplicationLocationListener)locHolder).shuttingDown();
		interestEngine.shuttingDown();
	}

	@Override
	public void onClick(View arg0) {
		Log.d("A_ACTIVITY", "Received a click event!");
		Event event = null;
		counter++;
		if(arg0.getId() == R.id.fire)
		{
			Location loc = new Location("Other");
			//George Mason University
			loc.setLatitude(38.8312049);
			loc.setLongitude(-77.3121336);

			event = Event.getInstance(EventType.FIRE, new Timestamp(Calendar.getInstance().getTimeInMillis()), 
					"Fire Report #" + counter, loc, locHolder.getCurrentLocation());
		}
		else if(arg0.getId() == R.id.evacPoint)
		{
			Location loc = new Location("Other");
			//Sideburn Rd north of campus
//			loc.setLatitude(38.837611);
//			loc.setLongitude(-77.30006);
			loc.setLatitude(38.84796);
			loc.setLongitude(-77.28718);

			event = Event.getInstance(EventType.EVACUATE, new Timestamp(Calendar.getInstance().getTimeInMillis()), 
					"Evacuate Report #" + counter, loc, locHolder.getCurrentLocation());
		}
		else if(arg0.getId() == R.id.evacAll)
		{
			Location loc = new Location("Other");
			//location does not matter for an evacuate all
			loc.setLatitude(38.8186449);
			loc.setLongitude(-77.31966);

			event = Event.getInstance(EventType.EVACUATE_ALL, new Timestamp(Calendar.getInstance().getTimeInMillis()), 
					"All Evacuate #" + counter, loc, locHolder.getCurrentLocation());
		}
		if (event != null) {
			Log.d("A_ACTIVITY", "Sending Event: " + event.getEventType().getText());
			interestEngine.sendEvent(event);
		}
		else {
			Log.d("A_ACTIVITY", "Sending Event: is null");
			counter--; //undo the increment because a message was not sent
		}
	}

	@Override
	public void displayMessage(String msg, double relVal) {
		final String dispMsg = msg;
		final double relevance = relVal;
		runOnUiThread(new Runnable() {
			public void run() {
				if(relevance > 80.0)
				{
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

					// 1. Vibrate for 1000 milliseconds
//					long milliseconds = 1000;
//					v.vibrate(milliseconds);

					// 2. Vibrate in a Pattern with 500ms on, 500ms off for 5 times
					long[] pattern = { 500, 300 , 400, 200 , 300 , 100};
					v.vibrate(pattern, -1);
				}
				//(Toast.makeText(ProjectAActivity.this, dispMsg + " (" + counter + ")",  Toast.LENGTH_SHORT)).show();
				EditText txt = (EditText)ProjectAActivity.this.findViewById(R.id.editText1);
				String displayText = new String (dispMsg);
				txt.setText(displayText);
			}});
	}


	@Override
	public void receivePacket(Event evt) {
		final Event evnt = evt;
		runOnUiThread(new Runnable() {
			public void run() {
				if(myContext.shouldNotify(evnt))
				{
					int icon = R.drawable.ic_menu_info_details;

					CharSequence tickerText = "Clicked me!";
					long when = System.currentTimeMillis();

					notification = new Notification(icon, tickerText, when);
					Intent notificationIntent = new Intent(ProjectAActivity.this, ProjectAActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(ProjectAActivity.this, 0, notificationIntent,0);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					notification.setLatestEventInfo(getApplicationContext(), "Clicked button " + counter + " times",
							"Click this notification to open the app", contentIntent);
					mNotificationManager.notify(1, notification);

				}
			}});
	}
}