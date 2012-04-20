package edu.gmu.hodum.sei.gesture.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;


public class TrainGestureActivity extends Activity implements SensorEventListener, OnInitListener
{
	private int gestureId;
	private static final String TAG = "traingesture";
	private boolean isLearning = false;
	private int gestureCount = 0;
	private boolean isAllowed = true;
	private Timer allowTimer = null;
	
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
	
	//delays
		private long eventDelay;
		private long gestureRecognizeTime; //in milliseconds 1/1000th of a second
		private long startRecognizerTime; //in milliseconds 1/1000th of a second
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.train);

		Bundle bundle = getIntent().getExtras();
		gestureId = bundle.getInt(TrainGestureListActivity.GESTURE_ID);

		Button done = (Button) findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
				String gestureName = GestureRecognizerService.GESTURE_NAMES[gestureId];
				GestureRecognizerService.finalizeLearning();
				GestureRecognizerService.saveGesture(gestureName);
				GestureRecognizerService.resetGestures();
				finish();
			}
		});
		
		mInitialized = false;
		loadPrefs();
		
		//setting up the accelerometer and sensor
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);      
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
	
	public void triggerRecognizer(){
		if (isAllowed)
		{
			if (isLearning)
			{
				Toast.makeText(this, GestureRecognizerService.CAPTURED + " "+ Integer.toString(++gestureCount), Toast.LENGTH_SHORT)
						.show();
				GestureRecognizerService.stopLearning();
			}
			else
			{
				Toast.makeText(this, GestureRecognizerService.CAPTURE, Toast.LENGTH_SHORT)
						.show();
				GestureRecognizerService.startLearning();
			}

			isLearning = !isLearning;
			isAllowed = false;
			Log.d(TAG, "disallow");
		}

		if (allowTimer != null)
			allowTimer.cancel();

		allowTimer = new Timer();
		allowTimer.schedule(new AllowTask(), 400);
	}
	
	

	private class AllowTask extends TimerTask
	{
		public void run()
		{
			TrainGestureActivity.this.isAllowed = true;
			Log.d(TAG, "allow");
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
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
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
			TrainGestureActivity.this.gatekeeper = true;
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
			TrainGestureActivity.this.runOnUiThread(new StopRecognizerRunnable());
		}

		private class StopRecognizerRunnable implements Runnable{

			public void run() {
				TrainGestureActivity.this.triggerRecognizer();

			}

		}
	}
	

	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void toast(String text){
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	public void sayText(String text){
		mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
	}
	
	public void speakNToast(String text){
		sayText(text);
		toast(text);
	}
}
