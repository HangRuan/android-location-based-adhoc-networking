package edu.gmu.hodum.sei.gesture.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import logic.GestureModel;
import logic.ProcessingUnitWrapper;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import android.widget.Toast;
import control.Andgee;
import edu.gmu.hodum.sei.common.SimpleXMLSerializer;
import edu.gmu.hodum.sei.common.Thing;
import edu.gmu.hodum.sei.common.Thing.Type;
import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.activity.MainActivity;
import edu.gmu.hodum.sei.gesture.util.GeoMath;
import edu.gmu.hodum.sei.gesture.widget.GestureWidgetProvider;
import event.GestureEvent;
import event.GestureListener;
import event.StateEvent;

public class GestureRecognizerService extends Service implements GestureListener, SensorEventListener, OnInitListener, OnUtteranceCompletedListener
{
	private static final String TAG = "gestureSvc";
	private static final int LEARN_KEY = KeyEvent.KEYCODE_T;
	private static final int RECOGNIZE_KEY = KeyEvent.KEYCODE_SPACE;
	private static final int STOP_KEY = KeyEvent.KEYCODE_ENTER;

	public static final String CAPTURE = "Capturing gesture";
	public static final String CAPTURED = "Gesture captured";

	public static final String SOS_GESTURE = "SOS";
	public static final String SUPPLIES_GESTURE = "Supplies";
	public static final String PERSON_GESTURE = "Person";

	public static final String GO_NEXT_GESTURE = "Go Forward";
	public static final String GO_BACK_GESTURE = "Go Back";
	public static final String CONFIRM_GESTURE = "Confirm";
	public static final String CANCEL_GESTURE = "Cancel";

	public static final Andgee mAndgee = Andgee.getInstance();
	public static final Map<Integer, String> GestureIdMapping = new HashMap<Integer, String>();

	public static final String[] GESTURE_NAMES_MAIN = new String[] {SOS_GESTURE,SUPPLIES_GESTURE,PERSON_GESTURE};
	public static final String[] GESTURE_NAMES_CHOICE = new String[] {GO_NEXT_GESTURE,GO_BACK_GESTURE,CONFIRM_GESTURE,CANCEL_GESTURE};

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

	private final IBinder mBinder = new LocalBinder();

	public static String mPackageName;
	private static Context mApplicationContext;

	private boolean isRecognizing = false;
	private boolean isLearning = false;

	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;

	private Lock allowLock = new ReentrantLock();
	private boolean isAllowed = true;
	private Timer allowTimer = null;

	private SensorEvtManager sensorEvtManager;
	private Lock evtLock = new ReentrantLock();

	private float NOISE;
	private float START;

	private boolean enableSpeech;
	private boolean enableToast;
	private TextToSpeech mTts;

	private RecognizerState STATE;

	private static int gestureCount;
	private static boolean learningMode;

	private String recognizedGesture;

	private float compassVal; //controls the compass value for pointing to objects at a distance

	boolean mIsRegistered; //are the sensors registered

	//private Handler handler = new Handler();

	//These are the file directory paths used to store the main and choice mode gestures
	public static String PATH_MAIN;
	public static String PATH_CHOICE;

	private GestureChoice choice;

	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "onCreate");

		mAndgee.setTrainButton(LEARN_KEY);
		mAndgee.setRecognitionButton(RECOGNIZE_KEY);
		mAndgee.setCloseGestureButton(STOP_KEY);

		mApplicationContext = getApplicationContext();
		mPackageName = mApplicationContext.getPackageName();
		PATH_MAIN = Environment.getExternalStorageDirectory() + "/Android/data/" + mPackageName + "/gestures/";
		PATH_CHOICE = Environment.getExternalStorageDirectory() + "/Android/data/" + mPackageName + "/gestures/choice/";

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);      
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorEvtManager = new SensorEvtManager(this);
		mIsRegistered = false; 

		setDefaultPrefs();
		loadPrefs();

		setState(RecognizerState.DEACTIVATED);
	}

	private void registerListeners(){
		//register listeners to various components
		System.out.println("SENSOR LISTENERS REGISTERED!");

		System.out.println("Andgee mSensorManager register listener: " + mSensorManager.registerListener(
				mAndgee.getDevice(), 
				SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_DELAY_GAME));

		System.out.println("mSensorManager.registerListener: "+  mSensorManager.registerListener(
				this, 
				mAccelerometer, 
				SensorManager.SENSOR_DELAY_NORMAL));

		mAndgee.addGestureListener(this);

		try{
			mAndgee.getDevice().enableAccelerationSensors();
		}
		catch (IOException e){
			Log.e(getClass().toString(), e.getMessage(), e);
		}
	}
	public int onStartCommand(Intent intent, int flags, int startId){
		String command = intent.getAction();

		System.out.println("Intent action = "+command);

		//button pressed on widget
		if(command.equals(this.getString(R.string.on))){
			//toggle button state

			setState(RecognizerState.DEACTIVATED);

			if(mIsRegistered == false){
				registerListeners();
				mIsRegistered = true;
			}
			loadGestures(GestureRecognizerService.PATH_MAIN);

			PendingIntent pendIntent = PendingIntent.getActivity(this, 0, new Intent (this, MainActivity.class), 0);

			Notification notification = new Notification (R.drawable.sei_logo, "SEI Gesture Service", System.currentTimeMillis());
			notification.setLatestEventInfo(this, "SEI Gesture Service", "Touch to configure", pendIntent);
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_NO_CLEAR;

			this.startForeground(1337, notification);

			//Now that the service is set, the next steps modify the toggle widget so that pressing the button again will turn off the service

			//create intent to turn off service
			Intent offIntent = new Intent(this, GestureRecognizerService.class);
			offIntent.setAction(this.getString(R.string.off));
			PendingIntent offPendingIntent = PendingIntent.getService(this, 0, offIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			//set the widget to turn off the service
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

			ComponentName gestureWidget = new ComponentName(this, GestureWidgetProvider.class);
			int[] allWidgetIds = appWidgetManager.getAppWidgetIds(gestureWidget);

			for (int widgetId : allWidgetIds) {
				RemoteViews views = new RemoteViews(this.getApplicationContext().getPackageName(),R.layout.gesture_widget_layout);
				views.setOnClickPendingIntent(R.id.btn_on_off, offPendingIntent);
				appWidgetManager.updateAppWidget(widgetId, views);
			}
		}
		else if(command.equals(this.getString(R.string.off))){
			//toggle button state

			//turn off gesture recognizer functionality
			Log.d(TAG, "off");

			mAndgee.getDevice().fireButtonReleasedEvent();
			mAndgee.getDevice().getAccelerationStreamAnalyzer().reset();

			mAndgee.removeGestureListener(this);
			mSensorManager.unregisterListener(mAndgee.getDevice());

			mSensorManager.unregisterListener(this);

			if (mTts !=null){
				mTts.shutdown();
			}

			try{
				mAndgee.getDevice().disableAccelerationSensors();
			}
			catch (Exception e){
				Log.e(getClass().toString(), e.getMessage(), e);
			}

			mIsRegistered = false;

			//Now that the service is shutting down, the next steps modify the toggle widget so that pressing the button again will turn on the service

			//create intent to turn on service
			Intent offIntent = new Intent(this, GestureRecognizerService.class);
			offIntent.setAction(this.getString(R.string.on));
			PendingIntent offPendingIntent = PendingIntent.getService(this, 0, offIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			//set the widget to turn on the service
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

			ComponentName gestureWidget = new ComponentName(this, GestureWidgetProvider.class);
			int[] allWidgetIds = appWidgetManager.getAppWidgetIds(gestureWidget);

			for (int widgetId : allWidgetIds) {
				RemoteViews views = new RemoteViews(this.getApplicationContext().getPackageName(),R.layout.gesture_widget_layout);
				views.setOnClickPendingIntent(R.id.btn_on_off, offPendingIntent);
				appWidgetManager.updateAppWidget(widgetId, views);
			}

			this.stopForeground(true);
			this.stopSelf();
		}

		return START_STICKY;
	}

	public IBinder onBind(Intent intent)
	{
		//sets the learning mode when training gestures
		/*
		System.out.println("onBind");
		String action = intent.getAction();
		if(action.equals(this.getString(R.string.train))){
			setLearningMode(true);
		}
		*/
		return mBinder;
	}
	public boolean onUnbind(Intent intent){
		//System.out.println("onUnbind");
		//setLearningMode(false);

		return false;
	}

	public class LocalBinder extends Binder
	{
		public GestureRecognizerService getService()
		{
			return GestureRecognizerService.this;
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
			//When the full gesture recognition is activated, this value measure the length of a "quiet period" to end the gesture
			//The time is measured in milliseconds; 1/1000th of a second
			editor.putLong(this.getString(R.string.prefname_gesture_recognize_time), Long.parseLong(this.getString(R.string.prefval_gesture_recognize_time)));

			//enable speech
			editor.putBoolean(this.getString(R.string.prefname_enable_speech), Boolean.parseBoolean(this.getString(R.string.prefname_enable_speech)));

		}
	}

	private void loadPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		//get the noise level from the preferences
		NOISE = prefs.getFloat(this.getString(R.string.prefname_noise_level_filter), Float.parseFloat(this.getString(R.string.prefval_noise_level_filter)));

		//get the start level from the preferences
		START = prefs.getFloat(this.getString(R.string.prefname_gesture_start), Float.parseFloat(this.getString(R.string.prefval_gesture_start)));

		//get the delay between accelerometer events
		//eventDelay = prefs.getLong(this.getString(R.string.prefname_event_delay), Long.parseLong(this.getString(R.string.prefval_event_delay)));
		sensorEvtManager.setEventDelay(prefs.getLong(this.getString(R.string.prefname_event_delay), Long.parseLong(this.getString(R.string.prefval_event_delay))));

		//get the window of time between the first and the last sensor event to start the recognizer
		//startRecognizerTime = prefs.getLong(this.getString(R.string.prefname_recognizer_start_window), Long.parseLong(this.getString(R.string.prefval_recognizer_start_window)));
		sensorEvtManager.setStartRecognizerTime(prefs.getLong(this.getString(R.string.prefname_recognizer_start_window), Long.parseLong(this.getString(R.string.prefval_recognizer_start_window))));

		//get the time that the full gesture recognizer is active
		//gestureRecognizeTime = prefs.getLong(this.getString(R.string.prefname_gesture_recognize_time), Long.parseLong(this.getString(R.string.prefval_gesture_recognize_time)));
		sensorEvtManager.setGestureRecognizeTime(prefs.getLong(this.getString(R.string.prefname_gesture_recognize_time), Long.parseLong(this.getString(R.string.prefval_gesture_recognize_time))));

		//get the text-to-speech setting
		enableSpeech = prefs.getBoolean(this.getString(R.string.prefname_enable_speech), Boolean.parseBoolean(this.getString(R.string.prefval_enable_speech)));
		if(enableSpeech){
			mTts = new TextToSpeech(this,this);
		}
		else if (mTts !=null){
			mTts.shutdown();
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		if(STATE == RecognizerState.COMPASS_MODE){
			//x is the compass value
			compassVal = x;

		}
		else{
			if (!mInitialized) {
				mLastX = x;
				mLastY = y;
				mLastZ = z;
				mInitialized = true;
			} else {
				float deltaX = Math.abs(mLastX - x);
				float deltaY = Math.abs(mLastY - y);
				float deltaZ = Math.abs(mLastZ - z);
				mLastX = x;
				mLastY = y;
				mLastZ = z;


				if(		deltaX > START	||
						deltaY > START	|| 
						deltaZ > START){
					if(evtLock.tryLock()){
						sensorEvtManager.addEvt(System.currentTimeMillis(), SensorEvtType.ACTIVATE);
						evtLock.unlock();
					}

				}
				//if greater than noise
				else if(deltaX > NOISE	||
						deltaY > NOISE	|| 
						deltaZ > NOISE){
					if(evtLock.tryLock()){
						sensorEvtManager.addEvt(System.currentTimeMillis(), SensorEvtType.NORMAL);
						evtLock.unlock();
					}
				}
				//less than noise level
				else{
					if(evtLock.tryLock()){
						sensorEvtManager.addEvt(System.currentTimeMillis(), SensorEvtType.QUIET);
						evtLock.unlock();
					}
				}
			}
		}
	}


	static enum RecognizerState{
		DEACTIVATED,
		ACTIVATED,
		CONFIGURING,
		CHOICE_MODE,
		COMPASS_MODE
	}

	synchronized void setState(RecognizerState state){
		//System.out.println("Recognizer State is now: "+state.toString());
		this.STATE = state;

		/*
		if(state  == RecognizerState.DEACTIVATED){
			StackTraceElement[] elements = Thread.currentThread().getStackTrace();
			for (StackTraceElement element : elements){
				System.out.println(element.toString());
			}
		}
		 */
	}
	synchronized RecognizerState getState(){
		return this.STATE;
	}
	public static synchronized void setLearningMode(boolean val){
		System.out.println("Set LearningMode: "+val);
		gestureCount = 0;
		learningMode = val;
	}
	synchronized boolean getLearningMode(){
		return learningMode;
	}

	public void gestureReceived(GestureEvent event)
	{
		Log.d(TAG, event.getId() + " " + GestureIdMapping.get(event.getId()) + " with prob. "
				+ event.getProbability());

		if (event.getProbability() > 0.9)
		{
			String gesture = GestureRecognizerService.GestureIdMapping.get(event.getId());

			if (gesture != null)
			{
				Log.d(TAG, "Gesture received " + gesture);

				//Choice Mode Gestures
				if(this.getState() == RecognizerState.CHOICE_MODE){

					if(gesture.equalsIgnoreCase(GestureRecognizerService.GO_NEXT_GESTURE)){
						choice.goNext();
						this.updateUI(choice.getCurrentUIString());
					}
					else if(gesture.equalsIgnoreCase(GestureRecognizerService.GO_BACK_GESTURE)){
						choice.goBack();
						this.updateUI(choice.getCurrentUIString());
					}
					else if(gesture.equalsIgnoreCase(GestureRecognizerService.CONFIRM_GESTURE)){
						choice.onConfirm();
						updateUI (choice.getCurrentUIString());

						if(choice.isFinished()){
							this.setState(RecognizerState.DEACTIVATED);

							if(recognizedGesture == GestureRecognizerService.PERSON_GESTURE){
								updateUI("Deactivated mode with finished person gesture");

								LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
								Location start = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								System.out.println(start.getLatitude()+","+start.getLongitude());
								Location person = GeoMath.getLocationFromStartBearingAndDistance(
										start,
										compassVal,
										Float.parseFloat(choice.getCurrentVal()));
								System.out.println(person.getLatitude()+","+person.getLongitude());

								loadGestures(GestureRecognizerService.PATH_MAIN);

								sendBroadcastPerson(person);

							}
							choice = null;
						}
						else{

						}
					}
					else if(gesture.equalsIgnoreCase(GestureRecognizerService.CANCEL_GESTURE)){
						this.setState(RecognizerState.DEACTIVATED);	
					}

				}
				else{
					//TODO: send Broadcasts for actions
					//Intent intent = new Intent(this.getString(R.string.send_data));
					if (gesture.equalsIgnoreCase(GestureRecognizerService.SOS_GESTURE)){
						updateUI(this.getResources().getString(R.string.sos));
						//TODO: create intent for SOS
						//
						//this.sendBroadcast(intent);

						//Test code
						sendBroadcast(1);
					}
					else if (gesture.equalsIgnoreCase(GestureRecognizerService.SUPPLIES_GESTURE)){
						updateUI(this.getResources().getString(R.string.supplies));
						//TODO: create intent for Supplies
						//
						//this.sendBroadcast(intent);

						//Test code
						sendBroadcast(2);
					}
					else if (gesture.equalsIgnoreCase(GestureRecognizerService.PERSON_GESTURE)){
						updateUI("Person Recognized");

						this.setState(RecognizerState.COMPASS_MODE);
						loadGestures(GestureRecognizerService.PATH_CHOICE);
						updateUI("Point device to person");
						updateUI("3");
						updateUI("2");
						updateUI("1");

						choice = new MetricDistanceChoice();
						updateUI(choice.getCurrentUIString());
						evtLock.lock();
						this.setState(RecognizerState.CHOICE_MODE);
						evtLock.unlock();
					}
				}


			}
		}
		else{
			updateUI(this.getResources().getString(R.string.gesture_not_recognized));
			this.setState(RecognizerState.DEACTIVATED);
		}
	}


	public void triggerRecognizer(){

		if(allowLock.tryLock()){
			if(isAllowed){
				if( getLearningMode() ){
					if (isLearning)
					{
						Toast.makeText(this, GestureRecognizerService.CAPTURED + " "+ Integer.toString(++gestureCount), Toast.LENGTH_SHORT).show();
						GestureRecognizerService.stopLearning();
					}
					else
					{
						Toast.makeText(this, GestureRecognizerService.CAPTURE, Toast.LENGTH_SHORT).show();
						GestureRecognizerService.startLearning();
					}

					isLearning = !isLearning;
					isAllowed = false;
					Log.d(TAG, "disallow");
				}
				else{
					if (isRecognizing)
					{
						Toast.makeText(this, GestureRecognizerService.CAPTURED, Toast.LENGTH_SHORT).show();
						GestureRecognizerService.stopRecognizer();
					}
					else
					{
						Toast.makeText(this, GestureRecognizerService.CAPTURE, Toast.LENGTH_SHORT).show();
						GestureRecognizerService.startRecognizer();
					}

					isRecognizing = !isRecognizing;
					isAllowed = false;
					Log.d(TAG, "disallow");
				}

				if (allowTimer != null)
					allowTimer.cancel();

				allowTimer = new Timer();
				allowTimer.schedule(new AllowTask(), 1000);
			}
			allowLock.unlock();
		}

	}

	private class AllowTask extends TimerTask
	{

		public void run()
		{
			allowLock.lock();
			GestureRecognizerService.this.isAllowed = true;
			Log.d(TAG, "allow");
			allowLock.unlock();
		}
	}

	public void stateReceived(StateEvent event)
	{
		if (event.getState() == event.STATE_RECOGNIZING)
			Log.d(TAG, "State is RECOGNIZING");
		else if (event.getState() == event.STATE_LEARNING)
			Log.d(TAG, "State is LEARNING");
	}

	public static void addGestureListener(GestureListener listener)
	{
		Log.d(TAG, "add gesture listener");
		mAndgee.addGestureListener(listener);
	}

	public static void removeGestureListener(GestureListener listener)
	{
		Log.d(TAG, "remove gesture listener");
		mAndgee.removeGestureListener(listener);
	}

	public static void startRecognizer()
	{
		Log.d(TAG, "start recognizer");
		mAndgee.getDevice().fireButtonPressedEvent(RECOGNIZE_KEY);
	}

	public static void stopRecognizer()
	{
		Log.d(TAG, "stop recognizer");
		mAndgee.getDevice().fireButtonReleasedEvent();
	}

	public static void startLearning()
	{
		Log.d(TAG, "start learning");
		mAndgee.getDevice().fireButtonPressedEvent(LEARN_KEY);
	}

	public static void stopLearning()
	{
		Log.d(TAG, "stop learning");
		mAndgee.getDevice().fireButtonReleasedEvent();
	}

	public static void finalizeLearning()
	{
		Log.d(TAG, "finalize learning");
		mAndgee.getDevice().fireButtonPressedEvent(STOP_KEY);
		mAndgee.getDevice().fireButtonReleasedEvent();
	}

	public static void loadGestures(String path)
	{
		Log.d(TAG, "load gestures");

		try
		{
			File file = new File(path);
			file.getParentFile().mkdirs();

			File subFile;
			if (file.list() != null){
				for (String item : file.list()){
					subFile = new File(path, item);
					if(!subFile.isDirectory()){
						BufferedReader reader = new BufferedReader(new FileReader(subFile));

						int id = mAndgee.getDevice().getAccelerationStreamAnalyzer().loadGesture(reader);
						String gesture = item.substring(0, item.lastIndexOf("."));
						GestureIdMapping.put(id, gesture);
						Log.d(TAG, "Loading " + path + item);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void saveGesture(String name, String path)
	{
		Log.d(TAG, "save gesture " + name);

		ProcessingUnitWrapper punitWrapper = new ProcessingUnitWrapper(mAndgee.getDevice()
				.getAccelerationStreamAnalyzer());

		Vector<GestureModel> models = punitWrapper.getGestureModels();

		for (GestureModel model : models)
		{
			try
			{
				String filename = name + ".txt";

				File file = new File(path, filename);
				file.getParentFile().mkdirs();
				file.createNewFile();

				FileOutputStream out = new FileOutputStream(file);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

				mAndgee.getDevice().getAccelerationStreamAnalyzer().saveGesture(writer, model.getId());

				String text = "Saving gesture model";

				Log.d(TAG, text);
				Log.d(TAG, "Wrote to " + file.getPath());

				Toast toast = Toast.makeText(mApplicationContext, text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
			}
			catch (FileNotFoundException e)
			{
				Log.d(TAG, name + " not found");
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void deleteGesture(String name, String path){
		//delete individual gesture from the file system and gesture Id map by name
		File file = new File(path+name+".txt");

		if (file.isFile())
		{
			file.delete();
			GestureIdMapping.remove(name);

			Log.d(TAG, "Deleting "+file.getAbsolutePath());	
		}
		else{
			Log.d(TAG, "Tried to delete non-existent file: "+file.getAbsolutePath());
		}

		resetGestures();
	}

	public static void deleteGestures(String path){
		//delete all gestures from the file system and the Gesture Id Map
		File file = new File(path);

		if (file.list() != null)
		{
			for (String item : file.list())
			{
				new File(path, item).delete();
				String gesture = item.substring(0, item.lastIndexOf("."));
				GestureIdMapping.remove(gesture);

				Log.d(TAG, "Deleting " + path + item);
			}
		}

		resetGestures();
	}

	public static void resetGestures()
	{
		mAndgee.getDevice().getAccelerationStreamAnalyzer().reset();
		GestureIdMapping.clear();

		Log.d(TAG, "reset gestures");
		Log.d(TAG, "Gesture id map has " + GestureIdMapping.size());
	}

	public void updateUI(String text){
		if(enableSpeech){
			mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
		}
		if(enableToast){
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}
	}

	private void sendBroadcast(int counter){

		Thing thing = new Thing();
		thing.setDescription("Testing!");
		thing.setElevation(230.0);
		thing.setLatitude(38.88255 + (.02*counter));
		thing.setLongitude(-77.049897 + (.02*counter));
		thing.setFriendliness(55.0);
		thing.setRelevance(67.0);
		thing.setType(Type.PERSON);
		SimpleXMLSerializer<Thing> serializer = new SimpleXMLSerializer<Thing>();
		byte[] data;
		try {
			data = serializer.serialize(thing);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		ByteBuffer b = ByteBuffer.allocate(data.length + 8);
		b.putLong(1000);
		b.put(data);

		byte[] buff = b.array();

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent("edu.gmu.hodum.SEND_DATA");
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		sendBroadcast(broadcastIntent);
	}
	private void sendBroadcastPerson(Location location)
	{

		Thing thing = new Thing();
		thing.setDescription("Testing with Location!");
		thing.setElevation(230.0);
		thing.setLatitude(location.getLatitude());
		thing.setLongitude(location.getLongitude());
		thing.setFriendliness(55.0);
		thing.setRelevance(67.0);
		thing.setType(Type.PERSON);

		SimpleXMLSerializer<Thing> serializer = new SimpleXMLSerializer<Thing>();
		byte[] data;
		try {
			data = serializer.serialize(thing);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		ByteBuffer b = ByteBuffer.allocate(data.length + 8);
		b.putLong(1000);
		b.put(data);

		byte[] buff = b.array();

		//Location loc = new Location("Other");
		Intent broadcastIntent = new Intent("edu.gmu.hodum.SEND_DATA");
		broadcastIntent.putExtra("latitude", 37.5d);
		broadcastIntent.putExtra("longitude", -73.25d);
		broadcastIntent.putExtra("radius",200.0d);
		broadcastIntent.putExtra("data",buff);
		sendBroadcast(broadcastIntent);
	}

	@Override
	public void onInit(int status) {
		if(status == TextToSpeech.SUCCESS){
			mTts.setOnUtteranceCompletedListener(this);
		}
		else if (status == TextToSpeech.ERROR){
			System.out.println();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onUtteranceCompleted(String text) {
		System.out.println("onUtteranceCompleted: "+text);

		/* Inactive b/c onUtteranceCompleted does not trigger properly
		 * 
		 * if(STATE == RecognizerState.COMPASS_MODE){
			System.out.println("Compass recording");
			if(text.equals("1")){
				choice = new MetricDistanceChoice();
				updateUI(choice.getCurrentUIString());
				this.setState(RecognizerState.CHOICE_MODE);

			}
		}
		 */
	}


}
