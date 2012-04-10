package edu.gmu.hodum.service_client;


import java.nio.ByteBuffer;

import edu.gmu.hodum.service_client.receiver.MyNetworkInitializedReceiver;
import edu.gmu.hodum.service_client.receiver.MyReceivedDataReceiver;
import edu.gmu.hodum.service_client.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;



public class SendReceiveActivity extends Activity implements  OnClickListener{

	private long counter = 0;


	private SenderThread sender;

	private String ipAddress;
	MyReceivedDataReceiver receiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ipAddress = this.getIntent().getStringExtra("ipAddress");

		findViewById(R.id.geo_message).setOnClickListener(this);
		findViewById(R.id.sendBroadcast).setOnClickListener(this);

		findViewById(R.id.sendSingle).setOnClickListener(this);
		
		receiver = new MyReceivedDataReceiver();
		receiver.registerHandler(dataReceivedHandler);
		IntentFilter filter1 = new IntentFilter(Constants.RECEIVE_DATA);
		registerReceiver(receiver,filter1);
		IntentFilter filter2 = new IntentFilter(Constants.DEBUG_RECEIVE_DATA);
		registerReceiver(receiver,filter2);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.sendBroadcast)
		{
			counter++;

			ByteBuffer b = ByteBuffer.allocate(100);
			b.putLong(counter);
			byte[] nm = ipAddress.getBytes();
			b.putLong(nm.length);
			b.put(nm);


			byte[] buff = b.array();

			Intent broadcastIntent = new Intent(Constants.SEND_DATA);
			broadcastIntent.putExtra("latitude", 360.0d);
			broadcastIntent.putExtra("longitude", 360.0d);
			broadcastIntent.putExtra("radius",-1.0d);
			broadcastIntent.putExtra("data",buff);
			sendBroadcast(broadcastIntent);

			//			Location loc = new Location("Other");
			//			loc.setLatitude(360);
			//			loc.setLongitude(360);
			//			networkManager.getSender().sendMessage(loc, -1.0, buff);


		}
	}

	private void sendGeoMessage()
	{
		counter++;

		ByteBuffer b = ByteBuffer.allocate(100);
		b.putLong(counter);
		byte[] nm = ipAddress.getBytes();
		b.putLong(nm.length);
		b.put(nm);


		byte[] buff = b.array();

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent(Constants.SEND_DATA);
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		sendBroadcast(broadcastIntent);

		//		loc.setLatitude(37.5);
		//		loc.setLongitude(-73.25);
		//		networkManager.getSender().sendMessage(loc, 200.0, buff);
	}


	public void receiveMessage(Location center, double radius,
			Location originatingLocation, byte[] bytes) {

		ByteBuffer b = ByteBuffer.wrap(bytes);
		long counter = b.getLong();
		long length = b.getLong();
		byte[] dst = new byte[(int)length];
		b.get(dst, 0, (int)length);
		String srce = new String(dst);
		//(Toast.makeText(ProjectAActivity.this, "Received Packet: " + counter,  Toast.LENGTH_SHORT)).show();
		EditText txt = (EditText)SendReceiveActivity.this.findViewById(R.id.editText1);
		String foo = new String ("Received Packet number: " + counter);
		txt.setText(foo + " lat: " + center.getLatitude() + " lon: "+ center.getLongitude() + " radius: " + radius);
		String foo2 = new String ("Packet: " + counter);


	}

	//############DEBUG ONLY method!

	public void debugMessage(Location center, double radius,
			Location originatingLocation, byte[] bytes) {

		ByteBuffer b = ByteBuffer.wrap(bytes);
		long counter = b.getLong();
		long length = b.getLong();
		byte[] dst = new byte[(int)length];
		b.get(dst, 0, (int)length);
		String srce = new String(dst);
		//(Toast.makeText(ProjectAActivity.this, "Received Packet: " + counter,  Toast.LENGTH_SHORT)).show();
		EditText txt = (EditText)SendReceiveActivity.this.findViewById(R.id.debugMessages);
		String foo = new String ("Received Packet number: " + counter);
		txt.setText(foo + " lat: " + center.getLatitude() + " lon: "+ center.getLongitude() + " radius: " + radius);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		boolean ret = false;
		if(item.getItemId() == R.id.startAutoSend)
		{
			if(sender == null)
			{
				sender = new SenderThread();
				sender.start();
			}
		}
		else if(item.getItemId() == R.id.stopAutoSend)
		{
			if(sender != null)
			{
				sender.stopSending();
				sender = null;
			}

		}
		return ret;

	}

	private Handler dataReceivedHandler = new Handler() { 
		/* (non-Javadoc) 
		 * @see android.os.Handler#handleMessage(android.os.Message) 
		 */ 
		@Override 
		public void handleMessage(Message msg) { 
			Bundle bundle = msg.getData();
			Location center = new Location("Other");
			center.setLatitude(bundle.getDouble("latitude"));
			center.setLongitude(bundle.getDouble("longitude"));
			double radius = bundle.getDouble("radius");
			byte[] data = bundle.getByteArray("data");
			Location originatingLocation = new Location("Other");
			originatingLocation.setLatitude(bundle.getDouble("originatingLatitude"));
			originatingLocation.setLongitude(bundle.getDouble("originatingLongitude"));
			switch(msg.arg1)
			{
			case Constants.RECEIVE_DATA_MSG:
				SendReceiveActivity.this.receiveMessage(center, radius, originatingLocation, data);
				break;
			case Constants.DEBUG_RECEIVE_DATA_MSG:
				SendReceiveActivity.this.debugMessage(center, radius, originatingLocation, data);
				break;
			}
			super.handleMessage(msg); 

		}
	};

	private class SenderThread extends Thread {

		private boolean sending = true;;

		public void run() {
			while(sending)
			{
				sendGeoMessage();
				try{
					sleep(5000);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}

		}

		public void stopSending()
		{
			sending = false;
		}
	}


	public void debugMessage(String msg) {
		// TODO Auto-generated method stub
		final String message = msg;
		runOnUiThread(new Runnable() {
			public void run() {
				EditText txt = (EditText)SendReceiveActivity.this.findViewById(R.id.debugMessages);
				txt.setText("Got: " + message);
			}
		});
	}


}