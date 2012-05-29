package edu.gmu.hodum.sei.gesture.activity;

import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import event.GestureEvent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends GestureActivity {

	final private int SETTINGS = 1;
	final private int TRAINING = 2; 

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		System.out.println("SensorEventListener onCreate");
		//mInitialized = false;

		//setting up the accelerometer and sensor
		//mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);      
		//mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		//TODO: Fix for when training new gestures
		Intent intent = new Intent(this, GestureRecognizerService.class);
		intent.setAction(this.getString(R.string.on));
		startService(intent);

	}

	public void btnTrainGestures_onClick(View view){
		this.startActivityForResult(new Intent(this, TrainGestureListActivity.class), TRAINING);
	}
	public void btnSettings_onClick(View view){
		Intent intent = new Intent(this, GestureRecognizerService.class);
		intent.setAction(this.getString(R.string.configure));
		startService(intent);
		
		this.startActivityForResult(new Intent(this, SetPreferenceActivity.class), SETTINGS);
	}

	public void onActivityResult(int requestCode, int resultcode, Intent data){
		if(resultcode ==  Activity.RESULT_OK){
			if(requestCode == SETTINGS){
				toast("Saved");
				Intent intent = new Intent(this, GestureRecognizerService.class);
				intent.setAction(this.getString(R.string.update_prefs));
				this.startService(intent);
			}
			else if(requestCode == TRAINING){
				Intent intent = new Intent(this, GestureRecognizerService.class);
				intent.setAction(this.getString(R.string.load_gestures));
				this.startService(intent);
			}
		}

	}

	@Override
	public void gestureReceived(GestureEvent event) {
		// TODO Auto-generated method stub
		
	}



}


