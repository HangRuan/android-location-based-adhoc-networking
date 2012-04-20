package edu.gmu.hodum.sei.gesture.activity;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import event.GestureEvent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;

public class MainActivity extends GestureActivity implements SensorEventListener, OnInitListener{

	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private float NOISE;
	private TextToSpeech mTts;

	private boolean gatekeeper = true;
	private int gestureStart = 0;

	final private Lock lock = new ReentrantLock();
	final private Timer timer = new Timer(true);
	private StartRecognizerTask reset;

	final private int settingsResultCode = 1; 

	//delays
	private long eventDelay;
	private long gestureRecognizeTime; //in milliseconds 1/1000th of a second
	private long startRecognizerTime; //in milliseconds 1/1000th of a second

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		System.out.println("SensorEventListener onCreate");
		mInitialized = false;

		setDefaultPrefs();
		loadPrefs();


		//setting up the accelerometer and sensor
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);      
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		GestureRecognizerService.mPackageName = getApplicationContext().getPackageName();
		startService(new Intent(this, GestureRecognizerService.class));


	}

	public void onResume() {
		super.onResume();
		boolean bool = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		System.out.println("onResume mSensorManager.registerListener: "+ Boolean.toString(bool));

		mTts = new TextToSpeech(this,this);
	}

	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		System.out.println("onPause mSensorManager.unregisterListener");

		mTts.shutdown();
	}

	public void gestureReceived(GestureEvent event)
	{
		if (event.getProbability() > 0.9)
		{
			String gesture = GestureRecognizerService.GestureIdMapping.get(event.getId());

			if (gesture != null)
			{
				Log.d(TAG, "Gesture received " + gesture);

				if (gesture.equalsIgnoreCase(GestureRecognizerService.SOS_GESTURE)){
					speakNToast(this.getResources().getString(R.string.sos));
				}
				else if (gesture.equalsIgnoreCase(GestureRecognizerService.SUPPLIES_GESTURE)){
					speakNToast(this.getResources().getString(R.string.sos));
				}
				else{
					speakNToast(this.getResources().getString(R.string.gesture_not_recognized));
				}
			}
		}
	}
	public void speakNToast(String text){
		sayText(text);
		toast(text);
	}

	public void sayText(String text){
		mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
	}

	public void onInit(int status) {
		// TODO Auto-generated method stub

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {

		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mInitialized = true;
		} else {
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			//if greater than noise
			if(
					deltaX > 0.0	||
					deltaY > 0.0	|| 
					deltaZ > 0.0){

				//check if lock is available, else do nothing
				if (lock.tryLock()){
					if (gatekeeper == true){
						//sayText("Event");

						//sets the gatekeeper to false, so the device ignores the sensor events
						//a timer is set for eventDelay milliseconds to change the gatekeeper back 
						//to allow the device to respond to sensor events again 
						gatekeeper = false;
						timer.schedule(new EventDelayTask(), eventDelay);

						//increments the gestureStart counter
						gestureStart++;

						//if there is no reset timer set, set the reset timer 
						if (reset == null){
							reset = new StartRecognizerTask();
							timer.schedule(reset, startRecognizerTime);
						}

						//if the gestureStart counter has reached 3, then reset the counter, and remove the reset timer, and activate the gesture recognizer
						if(gestureStart>3){
							gestureStart = 0;
							reset.cancel();
							reset = null;
							sayText("Gesture Start");

							//activates the recognizer
							triggerRecognizer();
							timer.schedule(new StopRecognizerTask(), gestureRecognizeTime);

						}
					}
					lock.unlock();
				}
				//do nothing in response to the sensor event if the lock is acquired
			}
		}

	}

	private class EventDelayTask extends TimerTask
	{
		public void run()
		{
			lock.lock();
			MainActivity.this.gatekeeper = true;
			lock.unlock();
		}
	}
	private class StartRecognizerTask extends TimerTask
	{
		public void run()
		{
			lock.lock();
			gestureStart = 0;
			lock.unlock();
		}
	}

	private class StopRecognizerTask extends TimerTask{
		public void run()
		{
			MainActivity.this.runOnUiThread(new StopRecognizerRunnable());
		}

		private class StopRecognizerRunnable implements Runnable{

			public void run() {
				MainActivity.this.triggerRecognizer();

			}

		}
	}

	public void btnTrainGestures_onClick(View view){
		this.startActivity(new Intent(this, TrainGestureListActivity.class));
	}
	public void btnSettings_onClick(View view){
		this.startActivityForResult(new Intent(this, SetPreferenceActivity.class), settingsResultCode);
	}

	public void onActivityResult(int requestCode, int resultcode, Intent data){
		if(resultcode ==  Activity.RESULT_OK){
			toast("Saved");
			loadPrefs();
		}
		else{
			toast("Canceled");
		}
	}

	private void loadPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		//get the noise level from the preferences
		NOISE = prefs.getFloat(this.getString(R.string.prefname_noise_level_filter), Float.parseFloat(this.getString(R.string.prefval_noise_level_filter)));

		//get the delay between accelerometer events
		eventDelay = prefs.getLong(this.getString(R.string.prefname_event_delay), Long.parseLong(this.getString(R.string.prefval_event_delay)));

		//get the time that the full gesture recognizer is active
		gestureRecognizeTime = prefs.getLong(this.getString(R.string.prefname_gesture_recognize_time), Long.parseLong(this.getString(R.string.prefval_gesture_recognize_time)));

	}

	private void setDefaultPrefs(){
		//sets the default preferences if not already set

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getAll().isEmpty()){
			SharedPreferences.Editor editor = prefs.edit();

			//noise level
			//accelerometer motions below this noise level are ignored completely
			//The values vary from device to device
			editor.putFloat(this.getString(R.string.prefname_noise_level_filter), Float.parseFloat(this.getString(R.string.prefval_noise_level_filter)));

			//event delay
			//After an event is recognized, this setting sets the delay before the next event can be recognized 
			//The delay is measured in milliseconds; 1/1000th of a second
			editor.putLong(this.getString(R.string.prefname_event_delay), Long.parseLong(this.getString(R.string.prefval_event_delay)));

			//start recognizer time
			//After an event is detected, this setting indicates the time for the full command to start the gesture recognizer must be inputted  
			//The delay is measured in milliseconds; 1/1000th of a second
			editor.putLong(this.getString(R.string.prefname_start_recognizer_time), Long.parseLong(this.getString(R.string.prefval_start_recognizer_time)));

			//gesture recognize time 
			//When the full gesture recognition is activated, this value measure how long the gesture recognizer is active
			//Thus, gestures must not take longer to execute than this time 
			//The time is measured in milliseconds; 1/1000th of a second
			editor.putLong(this.getString(R.string.prefname_gesture_recognize_time), Long.parseLong(this.getString(R.string.prefval_gesture_recognize_time)));


		}
	}

}


