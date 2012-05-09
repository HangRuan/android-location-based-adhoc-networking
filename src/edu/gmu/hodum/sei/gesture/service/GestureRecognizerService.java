package edu.gmu.hodum.sei.gesture.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import logic.GestureModel;
import logic.ProcessingUnitWrapper;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import android.widget.Toast;
import control.Andgee;
import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.widget.GestureWidgetProvider;
import event.GestureEvent;
import event.GestureListener;
import event.StateEvent;

public class GestureRecognizerService extends Service implements GestureListener
{
	private static final String TAG = "gesture";
	private static final int LEARN_KEY = KeyEvent.KEYCODE_T;
	private static final int RECOGNIZE_KEY = KeyEvent.KEYCODE_SPACE;
	private static final int STOP_KEY = KeyEvent.KEYCODE_ENTER;

	public static final String CAPTURE = "Capturing gesture";
	public static final String CAPTURED = "Gesture captured";

	public static final String SOS_GESTURE = "SOS";
	public static final String SUPPLIES_GESTURE = "Supplies";

	public static final Andgee mAndgee = Andgee.getInstance();
	public static final Map<Integer, String> GestureIdMapping = new HashMap<Integer, String>();

	public static final String[] GESTURE_NAMES = new String[] {SOS_GESTURE,SUPPLIES_GESTURE };

	private SensorManager mSensorManager;
	private final IBinder mBinder = new LocalBinder();

	public static String mPackageName;
	private static Context mApplicationContext;

	private String widgetState = this.getString(R.string.off); 

	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "onCreate");

		mAndgee.setTrainButton(LEARN_KEY);
		mAndgee.setRecognitionButton(RECOGNIZE_KEY);
		mAndgee.setCloseGestureButton(STOP_KEY);
		

		mApplicationContext = getApplicationContext();
	}

	public int onStartCommand(Intent intent, int flags, int startId){
		String command = intent.getAction();
		RemoteViews remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.gesture_widget_layout);
		int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

		//button pressed on widget
		if(command.equals(this.getString(R.string.on))){
			//toggle button state
			
			//turn on gesture recognizer functionality
			mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
			System.out.println("Andgee mSensorManager register listener: " + mSensorManager.registerListener(
					mAndgee.getDevice(), 
					SensorManager.SENSOR_ACCELEROMETER,
					SensorManager.SENSOR_DELAY_GAME));
			
			try
			{
				mAndgee.getDevice().enableAccelerationSensors();
			}
			catch (IOException e)
			{
				Log.e(getClass().toString(), e.getMessage(), e);
			}
			
			mAndgee.addGestureListener(this);
		}
		else if(command.equals(this.getString(R.string.off))){
			//toggle button state
			
			//turn off gesture recognizer functionality
			Log.d(TAG, "off");

			mAndgee.getDevice().fireButtonReleasedEvent();
			mAndgee.getDevice().getAccelerationStreamAnalyzer().reset();
			mSensorManager.unregisterListener(mAndgee.getDevice());

			try
			{
				mAndgee.getDevice().disableAccelerationSensors();
			}
			catch (Exception e)
			{
				Log.e(getClass().toString(), e.getMessage(), e);
			}
		}

		//set buttons for widget
		if(widgetState.equals(this.getString(R.string.off))){
			remoteView.setPendingIntentTemplate(R.id.btn_on_off, GestureWidgetProvider.makePendingIntent(getApplicationContext(),this.getString(R.string.on),appWidgetId));
		}
		else {
			remoteView.setPendingIntentTemplate(R.id.btn_on_off, GestureWidgetProvider.makePendingIntent(getApplicationContext(),this.getString(R.string.off),appWidgetId));

		}
		return START_STICKY;
	}

	public void onDestroy()
	{
		super.onDestroy();

		Log.d(TAG, "onDestroy");

		mAndgee.getDevice().fireButtonReleasedEvent();
		mAndgee.getDevice().getAccelerationStreamAnalyzer().reset();
		mSensorManager.unregisterListener(mAndgee.getDevice());

		try
		{
			mAndgee.getDevice().disableAccelerationSensors();
		}
		catch (Exception e)
		{
			Log.e(getClass().toString(), e.getMessage(), e);
		}
	}

	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	public class LocalBinder extends Binder
	{
		public GestureRecognizerService getService()
		{
			return GestureRecognizerService.this;
		}
	}

	public void gestureReceived(GestureEvent event)
	{
		Log.d(TAG, event.getId() + " " + GestureIdMapping.get(event.getId()) + " with prob. "
				+ event.getProbability());
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

	public static void loadGestures()
	{
		Log.d(TAG, "load gestures");

		try
		{
			File root = Environment.getExternalStorageDirectory();

			String path = root + "/Android/data/" + mPackageName + "/gestures/";
			File file = new File(path);
			file.getParentFile().mkdirs();

			if (file.list() != null)
			{
				for (String item : file.list())
				{
					Log.d(TAG, "A");
					BufferedReader reader = new BufferedReader(new FileReader(new File(path, item)));
					Log.d(TAG, "B");

					int id = mAndgee.getDevice().getAccelerationStreamAnalyzer()
							.loadGesture(reader);
					Log.d(TAG, "C");
					String gesture = item.substring(0, item.lastIndexOf("."));
					Log.d(TAG, "D");
					GestureIdMapping.put(id, gesture);
					Log.d(TAG, "E");

					Log.d(TAG, "Loading " + path + item);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void saveGesture(String name)
	{
		Log.d(TAG, "save gesture " + name);

		ProcessingUnitWrapper punitWrapper = new ProcessingUnitWrapper(mAndgee.getDevice()
				.getAccelerationStreamAnalyzer());

		Vector<GestureModel> models = punitWrapper.getGestureModels();

		for (GestureModel model : models)
		{
			try
			{
				File root = Environment.getExternalStorageDirectory();
				String filename = name + ".txt";
				String path = root + "/Android/data/" + mPackageName + "/gestures/";
				File file = new File(path, filename);
				file.getParentFile().mkdirs();
				file.createNewFile();

				FileOutputStream out = new FileOutputStream(file);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

				mAndgee.getDevice().getAccelerationStreamAnalyzer()
				.saveGesture(writer, model.getId());

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

	public static void deleteGesture(String name){
		//delete individual gesture from the file system and gesture Id map by name
		File root = Environment.getExternalStorageDirectory();

		String path = root + "/Android/data/" + mPackageName + "/gestures/";
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

	public static void deleteGestures(){
		//delete all gestures from the file system and the Gesture Id Map
		File root = Environment.getExternalStorageDirectory();

		String path = root + "/Android/data/" + mPackageName + "/gestures/";
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
}
