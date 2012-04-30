package edu.gmu.hodum.sei.gesture.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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
	private boolean enableSpeech;
	
	final private SensorEvtManager sensorEvtManager = new SensorEvtManager();
	final private Timer timer = new Timer(true);
	
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
		
		mInitialized = false;
		loadPrefs();
		
		//setting up the accelerometer and sensor
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);      
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}
	
	public void btnDone_onClick(View view){
		String gestureName = GestureRecognizerService.GESTURE_NAMES[gestureId];
		GestureRecognizerService.finalizeLearning();
		GestureRecognizerService.saveGesture(gestureName);
		GestureRecognizerService.resetGestures();
		this.setResult(Activity.RESULT_OK);
		finish();
	}

	public void onResume() {
		super.onResume();
		boolean bool = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		System.out.println("onResume mSensorManager.registerListener: "+ Boolean.toString(bool));

		if(enableSpeech){
			mTts = new TextToSpeech(this,this);
		}
	}
	
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		System.out.println("onPause mSensorManager.unregisterListener");

		if(enableSpeech){
			mTts.shutdown();
		}
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
