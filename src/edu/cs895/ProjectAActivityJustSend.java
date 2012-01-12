package edu.cs895;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import edu.cs895.message.Coder;
import edu.cs895.message.Event;
import edu.cs895.message.EventType;

import edu.cs895.message.MessageBuffer;
import edu.cs895.message.TransferQueue;
import edu.cs895.network.BroadcastNetworkManager;
import edu.cs895.network.NetworkManager;
import edu.cs895.network.Receiver;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProjectAActivityJustSend extends Activity implements OnClickListener, Receiver {

	private long counter = 0;
	private LocationHolder locHolder;
	private Notification notification;
	private NotificationManager mNotificationManager;
	NetworkManager networkManager;

	//eventually queues will be received in the interest engine only
	private TransferQueue inBoundQ;
	private TransferQueue outBoundQ;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if(networkManager == null)
		{
			//eventually queues will be received in the interest engine only
//			inBoundQ = ((MyApplication)this.getApplication()).getInBoundQ();
//			outBoundQ = ((MyApplication)this.getApplication()).getOutBoundQ();

			networkManager = BroadcastNetworkManager.instance(this, ((MyApplication)this.getApplication()).getLocationHolder());
			networkManager.startNetwork();
		}
		//Once you have this locHolder, simply call getCurrentLocation everytime you
		//need a new location.
		locHolder = ((MyApplication)this.getApplication()).getLocationHolder();
		findViewById(R.id.fire).setOnClickListener(this);

		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
		networkManager.registerReceiver(this);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.fire)
		{
			counter++;

			CharSequence tickerText = "Clicked me!";
			long when = System.currentTimeMillis();

//			int icon = R.drawable.ic_menu_info_details;
//
//			notification = new Notification(icon, tickerText, when);
//			Intent notificationIntent = new Intent(this, ProjectAActivity.class);
//			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,0);
//			notification.flags |= Notification.FLAG_AUTO_CANCEL;
//			notification.setLatestEventInfo(getApplicationContext(), "Clicked button " + counter + " times",
//					"Click this notification to open the app", contentIntent);
//			mNotificationManager.notify(1, notification);
			ByteBuffer b = ByteBuffer.allocate(100);
			b.putLong(counter);
			byte[] nm = BroadcastNetworkManager.macAddressSet.getBytes();
			b.putLong(nm.length);
			b.put(nm);

			
			byte[] buff = b.array();
		
			Location loc = new Location("Other");
			loc.setLatitude(37.5);
			loc.setLongitude(-73.25);
			networkManager.getSender().sendMessage(loc, buff);
			
//			LocationArea origLoc = new LocationArea(loc);
//			Event event = Event.getInstance(EventType.FIRE, new Timestamp(Calendar.getInstance().getTimeInMillis()), 
//					"Temp01", 3, origLoc, origLoc, origLoc, "Adam", "Testing creation of event");
//			MessageBuffer msgBuff = Coder.encodeEvent(event);
//			Coder.decodeEvent(msgBuff);
//			outBoundQ.toAppMsg(msgBuff);
		}
	}


	@Override
	public void receiveMessage(Location targetLocation,
			Location originatingLocation, byte[] buff) {
		final byte[] bytes =buff;
		runOnUiThread(new Runnable() {
			public void run() {
				ByteBuffer b = ByteBuffer.wrap(bytes);
				long counter = b.getLong();
				long length = b.getLong();
				byte[] dst = new byte[(int)length];
				b.get(dst, 0, (int)length);
				String srce = new String(dst);
				//(Toast.makeText(ProjectAActivity.this, "Received Packet: " + counter,  Toast.LENGTH_SHORT)).show();
				EditText txt = (EditText)ProjectAActivityJustSend.this.findViewById(R.id.editText1);
				String foo = new String ("Received Packet from: " + srce + " number: " + counter);
				txt.setText(foo);
			}});
	}


	@Override
	public void receiveMessage(Location targetUpperLeft,
			Location targetLowerRight, Location originatingLocation, byte[] buff) {
		// TODO Auto-generated method stub

	}


	@Override
	public void receiveMessage(Location center, double radius,
			Location originatingLocation, byte[] buff) {
		// TODO Auto-generated method stub

	}
	
	

}