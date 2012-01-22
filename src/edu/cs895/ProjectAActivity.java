package edu.cs895;

import java.nio.ByteBuffer;
import java.util.Locale;



import edu.cs895.network.BroadcastNetworkManager;
import edu.cs895.network.NetworkManager;
import edu.cs895.network.Receiver;
import android.app.Activity;

import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;


public class ProjectAActivity extends Activity implements OnInitListener, OnClickListener, Receiver {

	private long counter = 0;
	NetworkManager networkManager;
	private TextToSpeech tts = null;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tts = new TextToSpeech(this, this);
		tts.setLanguage(Locale.US);
		tts.setSpeechRate(2.0f);
		setTitle(((MyApplication) getApplication()).getIPAddress());
		if(networkManager == null)
		{
			networkManager = BroadcastNetworkManager.instance(this, ((MyApplication)this.getApplication()));
			networkManager.startNetwork();
		}
		
		
		findViewById(R.id.geo_message).setOnClickListener(this);
		findViewById(R.id.broadcast_message).setOnClickListener(this);

		
		networkManager.registerReceiver(this);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.geo_message)
		{
			counter++;

			ByteBuffer b = ByteBuffer.allocate(100);
			b.putLong(counter);
			byte[] nm = BroadcastNetworkManager.macAddressSet.getBytes();
			b.putLong(nm.length);
			b.put(nm);

			
			byte[] buff = b.array();
		
			Location loc = new Location("Other");
			loc.setLatitude(37.5);
			loc.setLongitude(-73.25);
			networkManager.getSender().sendMessage(loc, 200.0, buff);

		}
		else if(arg0.getId() == R.id.broadcast_message)
		{
			counter++;

			ByteBuffer b = ByteBuffer.allocate(100);
			b.putLong(counter);
			byte[] nm = BroadcastNetworkManager.macAddressSet.getBytes();
			b.putLong(nm.length);
			b.put(nm);

			
			byte[] buff = b.array();
		
			Location loc = new Location("Other");
			loc.setLatitude(360);
			loc.setLongitude(360);
			networkManager.getSender().sendMessage(loc, -1.0, buff);

		}
	}


	@Override
	public void receiveMessage(Location center_in, double radius_in,
			Location originatingLocation, byte[] buff) {
		final byte[] bytes =buff;
		final Location  center = center_in;
		final double radius = radius_in;
		runOnUiThread(new Runnable() {
			public void run() {
				ByteBuffer b = ByteBuffer.wrap(bytes);
				long counter = b.getLong();
				long length = b.getLong();
				byte[] dst = new byte[(int)length];
				b.get(dst, 0, (int)length);
				String srce = new String(dst);
				//(Toast.makeText(ProjectAActivity.this, "Received Packet: " + counter,  Toast.LENGTH_SHORT)).show();
				EditText txt = (EditText)ProjectAActivity.this.findViewById(R.id.editText1);
				String foo = new String ("Received Packet number: " + counter);
				txt.setText(foo + " lat: " + center.getLatitude() + " lon: "+ center.getLongitude() + " radius: " + radius);
				tts.speak(foo, TextToSpeech.QUEUE_ADD, null);
			}});

	}


	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
	}
	
	

}