package edu.gmu.hodum.sei.ui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.Locale;

import edu.cs895.R;
import edu.gmu.hodum.sei.heartbeatprotocol.HeartbeatSender;
import edu.gmu.hodum.sei.network.BroadcastNetworkManager;
import edu.gmu.hodum.sei.network.NetworkManager;
import edu.gmu.hodum.sei.network.Receiver;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;



public class SendReceiveActivity extends Activity implements OnInitListener, OnClickListener, Receiver {

	private long counter = 0;
	NetworkManager networkManager;
	private TextToSpeech tts = null;
	private SenderThread sender;
	HeartbeatSender heartbeatSender;


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
			networkManager.beginReceivingPackets();
		}
		
		
		findViewById(R.id.geo_message).setOnClickListener(this);
		findViewById(R.id.broadcast_message).setOnClickListener(this);

		//###HACK commented out so I don't have to redo all of this to send the user packets in SEI
		//networkManager.registerReceiver(this);
		heartbeatSender = new HeartbeatSender(networkManager.getSender(), (MyApplication)getApplication(),this);
		networkManager.registerReceiver(heartbeatSender);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.geo_message)
		{
			sendGeoMessage();

		}
		else if(arg0.getId() == R.id.broadcast_message)
		{
//			counter++;
//
//			ByteBuffer b = ByteBuffer.allocate(100);
//			b.putLong(counter);
//			byte[] nm = BroadcastNetworkManager.uniqueID.getBytes();
//			b.putLong(nm.length);
//			b.put(nm);
//
//			
//			byte[] buff = b.array();
//		
//			Location loc = new Location("Other");
//			loc.setLatitude(360);
//			loc.setLongitude(360);
//			networkManager.getSender().sendMessage(loc, -1.0, buff);
			heartbeatSender.startDiscovery();

		}
	}

	private void sendGeoMessage()
	{
		counter++;

		ByteBuffer b = ByteBuffer.allocate(100);
		b.putLong(counter);
		byte[] nm = BroadcastNetworkManager.uniqueID.getBytes();
		b.putLong(nm.length);
		b.put(nm);

		
		byte[] buff = b.array();
	
		Location loc = new Location("Other");
		loc.setLatitude(37.5);
		loc.setLongitude(-73.25);
		networkManager.getSender().sendMessage(loc, 200.0, buff);
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
				EditText txt = (EditText)SendReceiveActivity.this.findViewById(R.id.editText1);
				String foo = new String ("Received Packet number: " + counter);
				txt.setText(foo + " lat: " + center.getLatitude() + " lon: "+ center.getLongitude() + " radius: " + radius);
				String foo2 = new String ("Packet: " + counter);
				tts.speak(foo2, TextToSpeech.QUEUE_ADD, null);
			}});

	}
	
	//############DEBUG ONLY method!
	@Override
	public void debugMessage(Location center_in, double radius_in,
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
				EditText txt = (EditText)SendReceiveActivity.this.findViewById(R.id.debugMessages);
				String foo = new String ("Received Packet number: " + counter);
				txt.setText(foo + " lat: " + center.getLatitude() + " lon: "+ center.getLongitude() + " radius: " + radius);
			}});

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
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
	}
	
	
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