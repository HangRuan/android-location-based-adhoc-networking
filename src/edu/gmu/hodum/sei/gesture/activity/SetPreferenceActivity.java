package edu.gmu.hodum.sei.gesture.activity;

import java.util.Map;
import java.util.Set;

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
		
		
		Map<String, ?> map = prefs.getAll();
		Set<String> keyset = map.keySet();
		for (String key: keyset){
			System.out.println(key);
		}
		
		
		//set spinners
		spinnerNoise = (Spinner) this.findViewById(R.id.spinner_noise);
		String stringNoise = Float.toString(prefs.getFloat(this.getString(R.string.prefname_noise_level_filter), Float.parseFloat(this.getString(R.string.prefval_noise_level_filter))));
		setupSpinner(spinnerNoise, R.array.prefarray_noise_level_filter, stringNoise);

		spinnerEventDelay = (Spinner) this.findViewById(R.id.spinner_event_delay);
		String stringEventDelay = Long.toString(prefs.getLong(this.getString(R.string.prefname_event_delay), Long.parseLong(this.getString(R.string.prefval_event_delay))));
		setupSpinner(spinnerEventDelay, R.array.prefarray_event_delay, stringEventDelay);

		spinnerStartRecognizerTime = (Spinner) this.findViewById(R.id.spinner_start_recognizer_time);
		String stringRecognizerTime = Long.toString(prefs.getLong(this.getString(R.string.prefname_start_recognizer_time), Long.parseLong(this.getString(R.string.prefval_start_recognizer_time))));
		setupSpinner(spinnerStartRecognizerTime, R.array.prefarray_start_recognizer_time, stringRecognizerTime);

		spinnerGestureRecognizeTime = (Spinner) this.findViewById(R.id.spinner_gesture_recognize_time);
		String stringGestureRecognizeTime = Long.toString(prefs.getLong(this.getString(R.string.prefval_gesture_recognize_time), Long.parseLong(this.getString(R.string.prefval_gesture_recognize_time))));
		setupSpinner(spinnerGestureRecognizeTime, R.array.prefarray_gesture_recognize_time, stringGestureRecognizeTime);

	}

	private void setupSpinner(Spinner spinner, int arrayVals, String prefVal){

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
		SharedPreferences.Editor editor = prefs.edit();
		
		String spinnerVal = (String) spinnerNoise.getSelectedItem();
		Float floatVal = Float.parseFloat(spinnerVal);
		editor.putFloat(this.getString(R.string.prefname_noise_level_filter), floatVal);
		
		spinnerVal = (String) spinnerEventDelay.getSelectedItem();
		Long longVal = Long.parseLong(spinnerVal);
		editor.putLong(this.getString(R.string.prefname_event_delay), longVal);
		
		spinnerVal = (String) spinnerStartRecognizerTime.getSelectedItem();
		longVal = Long.parseLong(spinnerVal);
		editor.putLong(this.getString(R.string.prefname_start_recognizer_time), longVal);
		
		spinnerVal = (String) spinnerGestureRecognizeTime.getSelectedItem();
		longVal = Long.parseLong(spinnerVal);
		editor.putLong(this.getString(R.string.prefname_gesture_recognize_time), longVal);
		
		editor.commit();

		this.setResult(Activity.RESULT_OK);
		this.finish();
	}
	public void btnCancel_onClick(View view){
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
	}
}
