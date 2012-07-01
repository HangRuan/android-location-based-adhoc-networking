package edu.gmu.hodum.sei.gesture.activity;

import java.util.Collections;
import java.util.Vector;

import edu.gmu.hodum.sei.gesture.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.FloatMath;
import android.view.View;
import android.widget.TextView;

public class CalibrateActivity extends Activity implements SensorEventListener, OnInitListener{
	private TextView txtCalibrate;
	private Vector<Float> activityLevel;

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

	private ProgressDialog pd;

	private long duration = 5000; //seconds of calibration 
	private long updateFrequency;
	private Handler handler;
	private String mode;

	private SharedPreferences prefs;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.calibrate_activity);
		txtCalibrate = (TextView) this.findViewById(R.id.txtCalibrate);
		txtCalibrate.setText("Please press the button to calibrate");

		this.mode = this.getIntent().getAction();

		updateFrequency = duration/100;

		handler = new Handler();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public void btnCalibrate_onClick (View view){
		activityLevel = new Vector<Float>();


		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);      
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setIndeterminate(false);
		pd.setMax(100);
		pd.setCancelable(true);

		if(mode.equals(this.getString(R.string.calibrate_noise))){
			pd.setMessage("Hold still...");
		}
		else if(mode.equals(this.getString(R.string.calibrate_start))){
			pd.setMessage("Calibrating shake to start. SHAKE!");
		}
		pd.setTitle("Calibrating");
		pd.show();


		handler.post(new UpdateDialog());
	}

	private class UpdateDialog implements Runnable{

		public void run() {
			pd.incrementProgressBy(1);

			//done calibration
			if (pd.getProgress() == pd.getMax()){
				//data gathering finished
				mSensorManager.unregisterListener(CalibrateActivity.this);

				//records the avg as the noise level
				SharedPreferences.Editor editor = prefs.edit();
				
				//compute avg noise level
				float avg = 0f;
				if(activityLevel.size()>0){
					for (float val : activityLevel){
						avg += val;
					}
					avg = avg / activityLevel.size();
				}
				
				if(mode.equals(CalibrateActivity.this.getString(R.string.calibrate_noise))){
	
					System.out.println("The noise level is: "+(avg));
					editor.putFloat(CalibrateActivity.this.getString(R.string.prefname_noise_level_filter), (avg * 1.2f) );
					editor.commit();
				}
				else if(mode.equals(CalibrateActivity.this.getString(R.string.calibrate_start))){
					System.out.println("The noise level is: "+(avg));
					float twoStdDeviation =  computeStdDeviation(avg) * 2;
					
					Collections.sort(activityLevel);
					
					//get the highest value within two standard deviations of the mean
					float val = 0f;
					int i = activityLevel.size()-1;
					boolean flag = true;
					while(flag){
						if (activityLevel.get(i) < (avg+twoStdDeviation)){
							val = activityLevel.get(i);
							flag = false;
						}
						i--;
					}
					
					System.out.println("The start signal level is: "+(val));
					editor.putFloat(CalibrateActivity.this.getString(R.string.prefname_gesture_start), (val) );
					editor.commit();
				}
				
				pd.dismiss();
			}
			//not done
			else{
				handler.postDelayed(new UpdateDialog(), updateFrequency);
			}
		}
		
		private float computeStdDeviation(float avg){
			float sum = 0f;
			for(float val : activityLevel){
				sum += (avg-val)*(avg-val);
			}
			
			//FloatMath is an Android-only class to help with Float calculations
			return FloatMath.sqrt(sum / activityLevel.size());
		}

	}

	public void onInit(int arg0) {
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {		
	}

	public void onSensorChanged(SensorEvent event) {
		//gets the 3 values from the accelerometer, computes and stores the average
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		float avg = (Math.abs(x) + Math.abs(y) + Math.abs(z))/3f;
		activityLevel.add(avg);

	}

}
