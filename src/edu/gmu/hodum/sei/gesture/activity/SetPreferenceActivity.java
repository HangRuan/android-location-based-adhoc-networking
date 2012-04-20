package edu.gmu.hodum.sei.gesture.activity;

import edu.gmu.hodum.sei.gesture.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SetPreferenceActivity extends Activity{

	private SharedPreferences prefs;
	private Spinner spinnerNoise;
	private Spinner spinnerEventDelay;
	private Spinner spinnerStartRecognizerTime;
	private Spinner spinnerGestureRecognizeTime;

	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		this.setContentView(R.layout.set_prefs);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//set spinners
		String stringNoise = Float.toString(prefs.getFloat(Integer.toString(R.string.prefname_noise_level_filter), Float.parseFloat(this.getString(R.string.prefval_noise_level_filter))));
		//spinnerNoise = (Spinner) this.findViewById(R.id.spinner_noise);
		//if (spinnerNoise!=null){
			//System.out.println("Test");
		//}
		setupSpinner(spinnerNoise, R.id.spinner_noise, R.array.prefarray_noise_level_filter, stringNoise);

		String stringEventDelay = Float.toString(prefs.getFloat(Integer.toString(R.string.prefname_event_delay), Float.parseFloat(this.getString(R.string.prefval_event_delay))));
		setupSpinner(spinnerEventDelay, R.id.spinner_event_delay, R.array.prefarray_event_delay, stringEventDelay);

		String stringRecognizerTime = Float.toString(prefs.getFloat(Integer.toString(R.string.prefname_start_recognizer_time), Float.parseFloat(this.getString(R.string.prefval_start_recognizer_time))));
		setupSpinner(spinnerStartRecognizerTime, R.id.spinner_start_recognizer_time, R.array.prefarray_start_recognizer_time, stringRecognizerTime);

		String stringGestureRecognizeTime = Float.toString(prefs.getFloat(Integer.toString(R.string.prefval_gesture_recognize_time), Float.parseFloat(this.getString(R.string.prefval_gesture_recognize_time))));
		setupSpinner(spinnerGestureRecognizeTime, R.id.spinner_gesture_recognize_time, R.array.prefarray_gesture_recognize_time, stringGestureRecognizeTime);

	}

	private void setupSpinner(Spinner spinner, int spinnerID, int arrayVals, String prefVal){

		//set Spinner
		spinner = (Spinner) this.findViewById(spinnerID);

		if(spinner!=null){
			//populate spinner
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
					this, arrayVals, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);

			//sets the spinner to the currently saved setting
			int i = 0;
			while ( i<adapter.getCount()){
				if(adapter.getItem(i).equals(prefVal)){
					break;
				}
				i++;
			}
			if(i<adapter.getCount()){
				spinner.setSelection(i);
			}
		}
	}


	public void btnSave_onClick(View view){
		//TODO: Get the values of the spinners and save the preferences


		this.setResult(Activity.RESULT_OK);
		this.finish();
	}
	public void btnCancel_onClick(View view){
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
	}
}
