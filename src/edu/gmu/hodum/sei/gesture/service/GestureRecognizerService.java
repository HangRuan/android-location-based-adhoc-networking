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

	public static final String LANDMARK_GESTURE = "Landmark";
	public static final String SUPPLIES_GESTURE = "Supplies";
	public static final String PERSON_GESTURE = "Person";
	public static final String VEHICLE_GESTURE = "Vehicle";

	public static final String GO_NEXT_GESTURE = "Go Forward";
	public static final String GO_BACK_GESTURE = "Go Back";
	public static final String CONFIRM_GESTURE = "Confirm";
	public static final String CANCEL_GESTURE = "Cancel";

	public static final Andgee mAndgee = Andgee.getInstance();
	public static final Map<Integer, String> GestureIdMapping = new HashMap<Integer, String>();

	public static final String[] GESTURE_NAMES_MAIN = new String[] {LANDMARK_GESTURE,SUPPLIES_GESTURE,PERSON_GESTURE,VEHICLE_GESTURE};
	public static final String[] GESTURE_NAMES_CHOICE = new String[] {GO_NEXT_GESTURE,GO_BACK_GESTURE,CONFIRM_GESTURE,CANCEL_GESTURE};

	//Sensors
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mCompass;

	private String mainGesture; //a temporary holder for the main gesture for use in choice mode and compass mode
	private float compassValHolder; //a temporary holder for the compass value for use in compass mode and choice mode
	private float compassVal;

	public static String mPackageName;
	private static Context mApplicationContext;

	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;

	private SensorEvtManager sensorEvtManager;
	Lock evtLock = new ReentrantLock();

	private float NOISE;
	private float START;

	private boolean enableSpeech;
	private boolean enableToast;
	private TextToSpeech mTts;

	private static RecognizerState STATE;

	private static int gestureCount;

	boolean mListenersRegistered; //are the sensor listeners registered

	//These are the file directory paths used to store the main and choice mode gestures
	public static String PATH_MAIN;
	public static String PATH_CHOICE;
	public static String currentPath; 

	public static String LEARNING_METHOD_ACTIVATED = "ACTIVATED";
	public static String LEARNING_METHOD_QUIET = "QUIET";

	private GestureChoice choice;

	public static final String INITIALIZE_NETWORK = "edu.gmu.hodum.INITIALIZE_NETWORK";

	public void onCreate(){
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
		mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorEvtManager = new SensorEvtManager(this);
		mListenersRegistered = false;

		setDefaultPrefs();
		loadPrefs();
	}

	static synchronized String getPath(){
		return currentPath;
	}
	public static synchronized void setPath(String path){
		currentPath = path;
	}

	private void registerListeners(){
		//register listeners to various components

		//register Andgee to fast accelerometer
		System.out.println("Andgee mSensorManager register listener: " + mSensorManager.registerListener(
				mAndgee.getDevice(),
				SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_DELAY_FASTEST));

		//register Gesture Recognizer Service to normal accelerometer
		System.out.println("mSensorManager.registerListener: "+  mSensorManager.registerListener(
				this, 
				mAccelerometer, 
				SensorManager.SENSOR_DELAY_NORMAL));

		//register Gesture Recognizer Service to compass
		mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_NORMAL);

		//register Gesture Recognizer Service to Andgee gestures
		mAndgee.addGestureListener(this);

		//enable the Andgee's acceleration sensors
		try{
			mAndgee.getDevice().enableAccelerationSensors();
		}
		catch (IOException e){
			Log.e(getClass().toString(), e.getMessage(), e);
		}

		System.out.println("SENSOR LISTENERS REGISTERED!");
	}

	public void onDestroy(){
		super.onDestroy();
		//turn off gesture recognizer functionality
		Log.d(TAG, "off");

		mAndgee.getDevice().fireButtonReleasedEvent();
		mAndgee.getDevice().getAccelerationStreamAnalyzer().reset();

		mAndgee.removeGestureListener(this);
		mSensorManager.unregisterListener(mAndgee.getDevice());

		mSensorManager.unregisterListener(this, mAccelerometer);
		mSensorManager.unregisterListener(this, mCompass);

		if (mTts !=null){
			mTts.shutdown();
		}

		try{
			mAndgee.getDevice().disableAccelerationSensors();
		}
		catch (Exception e){
			Log.e(getClass().toString(), e.getMessage(), e);
		}

		mListenersRegistered = false;

	}

	public int onStartCommand(Intent intent, int flags, int startId){
		String command = intent.getAction();

		System.out.println("Intent action = "+command);

		//button pressed on widget
		if(command.equals(this.getString(R.string.on))){

			//start the network, if needed
			Intent broadcastIntent = new Intent(INITIALIZE_NETWORK);
			broadcastIntent.putExtra("channel", "8");
			this.sendBroadcast(broadcastIntent);

			//toggle button state
			setState(RecognizerState.MAIN_DEACTIVATED);

			if(mListenersRegistered == false){
				registerListeners();
				mListenersRegistered = true;
			}
			resetGestures();
			setPath(GestureRecognizerService.PATH_MAIN);
			loadGestures();

			PendingIntent pendIntent = PendingIntent.getActivity(this, 0, new Intent (this, MainActivity.class), 0);

			Notification notification = new Notification (R.drawable.sei_logo, "SEI Gesture Service", System.currentTimeMillis());
			notification.setLatestEventInfo(this, "SEI Gesture Service", "Touch to configure", pendIntent);
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_NO_CLEAR;

			this.startForeground(1337, notification);

			//Now that the service is set, the next steps modify the toggle widget so that pressing the button again will turn off the service
			toggleWidget(this.getString(R.string.off));

		}
		else if(command.equals(this.getString(R.string.off))){
			//toggle button state

			//modify the toggle widget so that pressing the button again will turn on the service
			toggleWidget(this.getString(R.string.on));

			this.stopForeground(true);
			this.stopSelf();
		}

		return START_STICKY;
	}

	private void toggleWidget(String actionString){
		//create intent to toggle service
		Intent toggleIntent = new Intent(this, GestureRecognizerService.class);
		toggleIntent.setAction(actionString);
		PendingIntent offPendingIntent = PendingIntent.getService(this, 0, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		//set the widget to toggle the service
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

		ComponentName gestureWidget = new ComponentName(this, GestureWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(gestureWidget);

		for (int widgetId : allWidgetIds) {
			RemoteViews views = new RemoteViews(this.getApplicationContext().getPackageName(),R.layout.gesture_widget_layout);
			views.setOnClickPendingIntent(R.id.btn_on_off, offPendingIntent);
			appWidgetManager.updateAppWidget(widgetId, views);
		}
	}

	public IBinder onBind(Intent intent)
	{
		return null;
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

		//get compass values
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			compassVal = x;
		}
		//get accelerometer values
		else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
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
					sensorEvtManager.addEvt(System.currentTimeMillis(), SensorEvtType.ACTIVATE);
				}
				//if greater than noise
				else if(deltaX > NOISE	||
						deltaY > NOISE	|| 
						deltaZ > NOISE){
					sensorEvtManager.addEvt(System.currentTimeMillis(), SensorEvtType.NORMAL);
				}
				//less than noise level
				else{
					sensorEvtManager.addEvt(System.currentTimeMillis(), SensorEvtType.QUIET);
				}

			}
		}
	}

	public synchronized static void setState(RecognizerState state){
		System.out.println("Recognizer State is now: "+state.toString());
		STATE = state;
	}

	synchronized static RecognizerState getState(){
		return STATE;
	}

	public static synchronized void resetGestureCount(){
		gestureCount = 0;
	}

	public void gestureReceived(GestureEvent event){

		evtLock.lock();
		RecognizerState state = getState();
		Log.d(TAG, event.getId() + " " + GestureIdMapping.get(event.getId()) + " with prob. " + event.getProbability());

		if (event.getProbability() > 0.8){
			String gesture = GestureRecognizerService.GestureIdMapping.get(event.getId());

			//if a test state, just output the gesture recognized
			if(state == RecognizerState.TEST_CHOICE_ACTIVATED || 
					state == RecognizerState.TEST_CHOICE_DEACTIVATED || 
					state == RecognizerState.TEST_MAIN_ACTIVATED ||
					state == RecognizerState.TEST_MAIN_DEACTIVATED){
				updateUI("Test Mode "+gesture + " recognized");
			}
			else if (gesture != null){
				Log.d(TAG, "Gesture received " + gesture);

				//Choice Mode Gestures
				if(GestureRecognizerService.getState() == RecognizerState.CHOICE_ACTIVATED ||
						GestureRecognizerService.getState() == RecognizerState.CHOICE_DEACTIVATED){

					if(gesture.equalsIgnoreCase(GestureRecognizerService.GO_NEXT_GESTURE)){
						updateUI ("Go Forward");
						choice.goNext();
						this.updateUI(choice.getCurrentUIString());
					}
					else if(gesture.equalsIgnoreCase(GestureRecognizerService.GO_BACK_GESTURE)){
						updateUI ("Go Back");
						choice.goBack();
						this.updateUI(choice.getCurrentUIString());
					}
					else if(gesture.equalsIgnoreCase(GestureRecognizerService.CONFIRM_GESTURE)){
						choice.onConfirm();
						updateUI ("Confirmed a choice");
						updateUI (choice.getCurrentUIString());

						if(choice.isFinished()){

							if(mainGesture.equals(GestureRecognizerService.PERSON_GESTURE)){
								
								Location start = getCurrentLocation();

								System.out.println("CompassVal: "+compassValHolder);
								Location end = GeoMath.getLocationFromStartBearingAndDistance2(
										start,
										compassValHolder,
										Float.parseFloat(choice.getCurrentVal()));
								System.out.println("Person Location: "+end.getLatitude()+","+end.getLongitude());

								resetGestures();
								setPath(GestureRecognizerService.PATH_MAIN);
								loadGestures();

								updateUI ("Sending Person Broadcast");
								Broadcaster.sendBroadcastPerson(end, this);

								GestureRecognizerService.setState(RecognizerState.MAIN_DEACTIVATED);	
							}
							else if(mainGesture.equals(GestureRecognizerService.VEHICLE_GESTURE)){
								Location start = getCurrentLocation();

								System.out.println("CompassVal: "+compassValHolder);
								Location end = GeoMath.getLocationFromStartBearingAndDistance2(
										start,
										compassValHolder,
										Float.parseFloat(choice.getCurrentVal()));
								System.out.println("Vehicle Location: "+end.getLatitude()+","+end.getLongitude());

								resetGestures();
								setPath(GestureRecognizerService.PATH_MAIN);
								loadGestures();

								updateUI ("Sending Vehicle Broadcast");
								Broadcaster.sendBroadcastVehicle(end, this);

								GestureRecognizerService.setState(RecognizerState.MAIN_DEACTIVATED);
							}
							choice = null;
						}
					}
					else if(gesture.equalsIgnoreCase(GestureRecognizerService.CANCEL_GESTURE)){
						updateUI ("Canceled");
						resetGestures();
						setPath(GestureRecognizerService.PATH_MAIN);
						loadGestures();
						GestureRecognizerService.setState(RecognizerState.MAIN_DEACTIVATED);	
					}

				}
				else{
					//TODO: send Broadcasts for actions

					if (gesture.equalsIgnoreCase(GestureRecognizerService.SUPPLIES_GESTURE)){
						updateUI(this.getResources().getString(R.string.supplies));

						Broadcaster.sendBroadcastResource(getCurrentLocation(), this);
						GestureRecognizerService.setState(RecognizerState.MAIN_DEACTIVATED);
					}
					else if (gesture.equalsIgnoreCase(GestureRecognizerService.PERSON_GESTURE)){
						mainGesture = GestureRecognizerService.PERSON_GESTURE;
						updateUI("Person Recognized");

						updateUI("Point in the direction and shake");
						resetGestures();
						GestureRecognizerService.setState(RecognizerState.COMPASS_MODE);
					}
					else if(gesture.equalsIgnoreCase(GestureRecognizerService.VEHICLE_GESTURE)){
						mainGesture = GestureRecognizerService.VEHICLE_GESTURE;
						updateUI("Vehicle Recognized");

						updateUI("Point in the direction and shake");
						resetGestures();
						GestureRecognizerService.setState(RecognizerState.COMPASS_MODE);						
					}
					else if(gesture.equalsIgnoreCase(GestureRecognizerService.LANDMARK_GESTURE)){
						mainGesture = GestureRecognizerService.LANDMARK_GESTURE;
						updateUI("Landmark Recognized");

						updateUI("Point in the direction and shake");
						resetGestures();
						GestureRecognizerService.setState(RecognizerState.COMPASS_MODE);						
					}
					
				}
			}
		}
		else{
			updateUI(this.getResources().getString(R.string.gesture_not_recognized));
			if(GestureRecognizerService.getState() == RecognizerState.MAIN_ACTIVATED){
				GestureRecognizerService.setState(RecognizerState.MAIN_DEACTIVATED);
			}
		}
		evtLock.unlock();
	}

	public void triggerCompassRead(){
		compassValHolder = compassVal;

		int direction = (int)compassValHolder;

		if(direction<0||direction>360){
			System.err.println("Invalid compass direction");
		}
		else if (direction == 0 || direction == 360){
			updateUI("Directly north");
		}
		else if(direction<90){
			updateUI(direction+" degrees east of north");
		}
		else if (direction == 90){
			updateUI("Directly east");
		}
		else if(direction<180){
			updateUI((180-direction)+" degrees east of south");
		}
		else if (direction ==180){
			updateUI("Directly south");
		}
		else if(direction<270){
			updateUI((direction-180)+" degrees west of south");
		}
		else if (direction == 270){
			updateUI("Directly west");
		}
		else {
			updateUI((360-direction)+" degrees west of north");
		}
		
		resetGestures();
		if(mainGesture == PERSON_GESTURE){
			GestureRecognizerService.setState(RecognizerState.CHOICE_DEACTIVATED);
			setPath(GestureRecognizerService.PATH_CHOICE);
		
			choice = new MetricDistanceChoice();

			updateUI("How far away?");
			updateUI(choice.getCurrentUIString());
			loadGestures();
			
		}
	}

	public void triggerRecognizer(){

		RecognizerState state = GestureRecognizerService.getState();

		//Learning the main gestures
		if (state == RecognizerState.LEARNING_MAIN_DEACTIVATED){
			GestureRecognizerService.setState(RecognizerState.LEARNING_MAIN_ACTIVATED);
			GestureRecognizerService.startLearning();
		}
		else if (state == RecognizerState.LEARNING_MAIN_ACTIVATED){
			GestureRecognizerService.setState(RecognizerState.LEARNING_MAIN_DEACTIVATED);
			Toast.makeText(this, GestureRecognizerService.CAPTURED + " "+ Integer.toString(++gestureCount), Toast.LENGTH_SHORT).show();
			GestureRecognizerService.stopLearning();
			updateUI("Gesture Captured");
		}

		//Learning the choice gestures
		else if (state == RecognizerState.LEARNING_CHOICE_DEACTIVATED){
			GestureRecognizerService.setState(RecognizerState.LEARNING_CHOICE_ACTIVATED);
			GestureRecognizerService.startLearning();
		}
		else if(state == RecognizerState.LEARNING_CHOICE_ACTIVATED){
			GestureRecognizerService.setState(RecognizerState.LEARNING_CHOICE_DEACTIVATED);
			Toast.makeText(this, GestureRecognizerService.CAPTURED + " "+ Integer.toString(++gestureCount), Toast.LENGTH_SHORT).show();
			updateUI("Gesture Captured");
			GestureRecognizerService.stopLearning();
		}

		//Recognizing the main gestures
		if (state == RecognizerState.MAIN_DEACTIVATED || 
				state == RecognizerState.TEST_MAIN_DEACTIVATED){
			if(state == RecognizerState.MAIN_DEACTIVATED){
				GestureRecognizerService.setState(RecognizerState.MAIN_ACTIVATED);
			}
			else {
				GestureRecognizerService.setState(RecognizerState.TEST_MAIN_ACTIVATED);
			}
			GestureRecognizerService.startRecognizer();
		}
		else if (state == RecognizerState.MAIN_ACTIVATED ||
				state == RecognizerState.TEST_MAIN_ACTIVATED){
			if (state == RecognizerState.MAIN_ACTIVATED){
				GestureRecognizerService.setState(RecognizerState.MAIN_DEACTIVATED);
			}
			else{
				GestureRecognizerService.setState(RecognizerState.TEST_MAIN_DEACTIVATED);
			}
			Toast.makeText(this, GestureRecognizerService.CAPTURED, Toast.LENGTH_SHORT).show();
			GestureRecognizerService.stopRecognizer();
		}

		//Recognizing the choice gestures
		if (state == RecognizerState.CHOICE_DEACTIVATED ||
				state == RecognizerState.TEST_CHOICE_DEACTIVATED){
			if(state == RecognizerState.CHOICE_DEACTIVATED){
				GestureRecognizerService.setState(RecognizerState.CHOICE_ACTIVATED);
			}
			else{
				GestureRecognizerService.setState(RecognizerState.TEST_CHOICE_ACTIVATED);
			}
			GestureRecognizerService.startRecognizer();
		}
		else if (state == RecognizerState.CHOICE_ACTIVATED ||
				state == RecognizerState.TEST_CHOICE_ACTIVATED){
			
			if(state == RecognizerState.CHOICE_ACTIVATED){
				GestureRecognizerService.setState(RecognizerState.CHOICE_DEACTIVATED);	
			}
			else{
				GestureRecognizerService.setState(RecognizerState.TEST_CHOICE_DEACTIVATED);
			}
			Toast.makeText(this, GestureRecognizerService.CAPTURED, Toast.LENGTH_SHORT).show();
			GestureRecognizerService.stopRecognizer();
		}

	}

	public void stateReceived(StateEvent event){
		if (event.getState() == event.STATE_RECOGNIZING)
			Log.d(TAG, "State is RECOGNIZING");
		else if (event.getState() == event.STATE_LEARNING)
			Log.d(TAG, "State is LEARNING");
	}

	public static void addGestureListener(GestureListener listener){
		Log.d(TAG, "add gesture listener");
		mAndgee.addGestureListener(listener);
	}

	public static void removeGestureListener(GestureListener listener){
		Log.d(TAG, "remove gesture listener");
		mAndgee.removeGestureListener(listener);
	}

	public static void startRecognizer(){
		Log.d(TAG, "start recognizer");
		mAndgee.getDevice().fireButtonPressedEvent(RECOGNIZE_KEY);
	}

	public static void stopRecognizer(){
		Log.d(TAG, "stop recognizer");
		mAndgee.getDevice().fireButtonReleasedEvent();
	}

	public static void startLearning(){
		Log.d(TAG, "start learning");
		mAndgee.getDevice().fireButtonPressedEvent(LEARN_KEY);
	}

	public static void stopLearning(){
		Log.d(TAG, "stop learning");
		mAndgee.getDevice().fireButtonReleasedEvent();
	}

	public static void finalizeLearning(){
		Log.d(TAG, "finalize learning");
		mAndgee.getDevice().fireButtonPressedEvent(STOP_KEY);
		mAndgee.getDevice().fireButtonReleasedEvent();
	}

	public static void loadGestures(){
		Log.d(TAG, "load gestures");
		String path = GestureRecognizerService.getPath();
		resetGestures();
		try{
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
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void saveGesture(String name){
		String path = GestureRecognizerService.getPath();
		Log.d(TAG, "save gesture " + path+name);

		ProcessingUnitWrapper punitWrapper = new ProcessingUnitWrapper(mAndgee.getDevice().getAccelerationStreamAnalyzer());

		Vector<GestureModel> models = punitWrapper.getGestureModels();

		for (GestureModel model : models){
			try{
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
			catch (FileNotFoundException e){
				Log.d(TAG, name + " not found");
				e.printStackTrace();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	public static void deleteGesture(String name){
		//delete individual gesture from the file system and gesture Id map by name
		String path = GestureRecognizerService.getPath();
		File file = new File(path+name+".txt");

		if (file.isFile()){
			file.delete();
			GestureIdMapping.remove(name);

			Log.d(TAG, "Deleting "+file.getAbsolutePath());	
		}
		else{
			Log.d(TAG, "Tried to delete non-existent file: "+file.getAbsolutePath());
		}

		resetGestures();
	}

	public static void deleteGestures(){
		//delete all gestures from the file system and the Gesture Id Map
		String path = GestureRecognizerService.getPath();
		File file = new File(path);

		if (file.list() != null){
			for (String item : file.list()){
				new File(path, item).delete();
				String gesture = item.substring(0, item.lastIndexOf("."));
				GestureIdMapping.remove(gesture);

				Log.d(TAG, "Deleting " + GestureRecognizerService.getPath() + item);
			}
		}

		resetGestures();
	}

	public static void resetGestures(){
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
	public void onUtteranceCompleted(String arg0) {
	}
	
	private Location getCurrentLocation(){
		LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location start = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		System.out.println("Start Location: "+start.getLatitude()+","+start.getLongitude());
		return start;
	}
}
