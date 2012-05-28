package edu.gmu.hodum.sei.gesture.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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

/*
 * This is the main activity that is started after the application is launched. 
 * 
 * It has links to other activities to train the gestures and to change the application settings,
 * which are stored in the android default shared preferences files. This activity loads the preferences
 * upon startup and using an appropriate onActivityResult
 * 
 * This activity is also where the gestures are recognized. The primary gesture recognizer uses the andgee library.
 * However, the primary gesture recognizer is not active all of the time. It was determined that this would 
 * drain the battery excessively. 
 * 
 * Instead, the primary gesture recognizer is activated after 3 motions are detected using the accelerometer which exceed a noise threshold. 
 * These motions using the device can be of any type, as long as they exceed the noise threshold.
 * This prevents unintended vibrations or incidental contact from activating the gesture recognition.
 * A sensor reading that exceeds the noise threshold is called a sensor event 
 * 
 * Most accelerometers will sample values very quickly. Without filtering, this would result in several sensor events to be registered 
 * over the course of a single motion. The intention is to have a single sensor event per motion. Thus, after a sensor event is registered,
 * all sensor values are ignored for a configurable time. This allows whatever real-life motion which registers a sensor event to complete
 * without triggering other sensor events
 * 
 * To review, initiating the gesture recognition requires 3 sensor events must occur. These events must occur within a reasonable timeframe. 
 * For example, having three separation incidental events on three separate day should not activate the gesture recognition. 
 * 
 * 
 * After the gesture recognition is activated, the mechanism to activate the primary gesture recognition 
 * should be disabled for the duration of the gesture plus the delay for the minimum sensor event. 
 */

public class MainActivity extends GestureActivity implements SensorEventListener, OnInitListener{

	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private float NOISE;
	private boolean enableSpeech;
	private TextToSpeech mTts;

	final private SensorEvtManager sensorEvtManager = new SensorEvtManager();
	final private Timer timer = new Timer(true);
	final private int SETTINGS = 1;
	final private int TRAINING = 2; 

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
		
		//TODO: Fix for when training new gestures
		//GestureRecognizerService.loadGestures();
		//startService(new Intent(this, GestureRecognizerService.class));

	}

	public void onResume() {
		super.onResume();
		//boolean bool = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		//System.out.println("onResume mSensorManager.registerListener: "+ Boolean.toString(bool));

		//TODO: Fix for when training new gestures
		//GestureRecognizerService.loadGestures();
		if(enableSpeech){
			mTts = new TextToSpeech(this,this);
		}
	}

	public void onPause() {
		super.onPause();
		//mSensorManager.unregisterListener(this);
		//System.out.println("onPause mSensorManager.unregisterListener");

		if(enableSpeech){
			mTts.shutdown();
		}
	}

	public void gestureReceived(GestureEvent event)
	{
		if (event.getProbability() > 0.4)
		{
			String gesture = GestureRecognizerService.GestureIdMapping.get(event.getId());

			if (gesture != null)
			{
				Log.d(TAG, "Gesture received " + gesture);
				
				//TODO: send Broadcasts for actions
				Intent intent = new Intent(this.getString(R.string.send_data));
				if (gesture.equalsIgnoreCase(GestureRecognizerService.SOS_GESTURE)){
					speakNToast(this.getResources().getString(R.string.sos));
					//TODO: create intent for SOS
					//
					//this.sendBroadcast(intent);
				}
				else if (gesture.equalsIgnoreCase(GestureRecognizerService.SUPPLIES_GESTURE)){
					speakNToast(this.getResources().getString(R.string.supplies));
					//TODO: create intent for Supplies
					//
					//this.sendBroadcast(intent);
				}

			}
		}
		else{
			speakNToast(this.getResources().getString(R.string.gesture_not_recognized));
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

				if(sensorEvtManager.addEvtStartRecognizer(System.currentTimeMillis())){
					//activates the recognizer
					System.out.println("Recognizer triggered");
					sayText("Gesture Start");
					triggerRecognizer();
					timer.schedule(new StopRecognizerTask(), gestureRecognizeTime);
				}

			}
		}

	}

	private class SensorEvtManager {
		final private Vector<Long> evts = new Vector<Long>();
		private long nextSampleTime = 0;

		SensorEvtManager(){
		}

		public synchronized boolean addEvtStartRecognizer(long time){

			if(time > nextSampleTime){
				System.out.println("Sensor event triggered, evts.size = "+evts.size());
				//there are not two events yet
				if(evts.size()<2){
					evts.add(time);
					nextSampleTime = time + eventDelay;
				}
				//this is the third event
				else{

					//removes values which may be too old
					for (long evt : evts){
						if((time - evt) > startRecognizerTime){
							System.out.println("remove");
							evts.remove(evt);
						}
					}

					evts.add(time);

					//checks to make sure that there are still enough sensor events to activate the recognizer
					if(evts.size() >= 3){
						nextSampleTime = time + gestureRecognizeTime + eventDelay;
						evts.clear();
						return true;
					}
					else {
						nextSampleTime = time + eventDelay;
					}
				}
			}
			return false;
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
				speakNToast("Recognizer Stopped");
			}

		}
	}

	public void btnTrainGestures_onClick(View view){
		this.startActivityForResult(new Intent(this, TrainGestureListActivity.class), TRAINING);
	}
	public void btnSettings_onClick(View view){
		this.startActivityForResult(new Intent(this, SetPreferenceActivity.class), SETTINGS);
	}

	public void onActivityResult(int requestCode, int resultcode, Intent data){
		if(resultcode ==  Activity.RESULT_OK){
			if(requestCode == SETTINGS){
				toast("Saved");
				loadPrefs();
			}
			else if(requestCode == TRAINING){
				GestureRecognizerService.loadGestures();
			}
		}
		/*
		else{
			toast("Canceled from list");
		}
		*/
	}

	private void loadPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		//get the noise level from the preferences
		NOISE = prefs.getFloat(this.getString(R.string.prefname_noise_level_filter), Float.parseFloat(this.getString(R.string.prefval_noise_level_filter)));

		//get the delay between accelerometer events
		eventDelay = prefs.getLong(this.getString(R.string.prefname_event_delay), Long.parseLong(this.getString(R.string.prefval_event_delay)));

		//get the window of time between the first and the last senor event to start the recognizer
		startRecognizerTime = prefs.getLong(this.getString(R.string.prefname_recognizer_start_window), Long.parseLong(this.getString(R.string.prefval_recognizer_start_window)));

		//get the time that the full gesture recognizer is active
		gestureRecognizeTime = prefs.getLong(this.getString(R.string.prefname_gesture_recognize_time), Long.parseLong(this.getString(R.string.prefval_gesture_recognize_time)));

		//get the text-to-speech setting
		enableSpeech = prefs.getBoolean(this.getString(R.string.prefname_enable_speech), Boolean.parseBoolean(this.getString(R.string.prefval_enable_speech)));
		if(mTts !=null){
			mTts.shutdown();
		}

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

			//gesture start
			//three accelerometer motions above this level indicate that the user wishes to start the gesture recognition
			//The values vary from device to device
			editor.putFloat(this.getString(R.string.prefname_gesture_start), Float.parseFloat(this.getString(R.string.prefval_gesture_start)));
			
			//event delay
			//After an event is recognized, this setting sets the delay before the next event can be recognized 
			//The delay is measured in milliseconds; 1/1000th of a second
			editor.putLong(this.getString(R.string.prefname_event_delay), Long.parseLong(this.getString(R.string.prefval_event_delay)));

			//start recognizer time
			//After the first event is detected, this setting indicates the window of time for the full command to start the gesture recognizer must be inputed  
			//The delay is measured in milliseconds; 1/1000th of a second
			editor.putLong(this.getString(R.string.prefname_recognizer_start_window), Long.parseLong(this.getString(R.string.prefval_recognizer_start_window)));

			
			//gesture recognize time 
			//When the full gesture recognition is activated, this value measure the length of a "quiet period"
			//Thus, gestures must not take longer to execute than this time 
			//The time is measured in milliseconds; 1/1000th of a second
			editor.putLong(this.getString(R.string.prefname_gesture_recognize_time), Long.parseLong(this.getString(R.string.prefval_gesture_recognize_time)));

			//enable speech
			editor.putBoolean(this.getString(R.string.prefname_enable_speech), Boolean.parseBoolean(this.getString(R.string.prefname_enable_speech)));

		}
	}

}


