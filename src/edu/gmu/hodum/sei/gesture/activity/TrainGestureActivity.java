package edu.gmu.hodum.sei.gesture.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import edu.gmu.hodum.sei.gesture.service.RecognizerState;

public class TrainGestureActivity extends Activity{
	private int gestureId;
	private String gesturePath;
	private String gestureName;
	private static final String TAG = "traingesture";

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.train);

		TextView label = (TextView) this.findViewById(R.id.label);

		TextView instructions = (TextView) this.findViewById(R.id.instructions);

		Bundle bundle = getIntent().getExtras();
		gestureId = bundle.getInt(TrainGestureListActivity.GESTURE_ID);
		gesturePath = bundle.getString(TrainGestureListActivity.GESTURE_PATH);

		GestureRecognizerService.resetGestureCount();
		if(gesturePath.equals(GestureRecognizerService.PATH_MAIN)){
			GestureRecognizerService.setState(RecognizerState.LEARNING_MAIN_DEACTIVATED);
			gestureName = GestureRecognizerService.GESTURE_NAMES_MAIN[gestureId];
			instructions.setText("Shake three times to start training. Hold still to finish gesture.");
		}
		else{
			GestureRecognizerService.setState(RecognizerState.LEARNING_CHOICE_DEACTIVATED);
			gestureName = GestureRecognizerService.GESTURE_NAMES_CHOICE[gestureId];
			instructions.setText("Shake once to start training. Hold still to finish gesture.");
		}
		label.setText(gestureName);
	}

	public void btnDone_onClick(View view){

		//saves the gesture to file, and closes the activity

		GestureRecognizerService.finalizeLearning();
		GestureRecognizerService.setPath(gesturePath);
		GestureRecognizerService.saveGesture(gestureName);
		GestureRecognizerService.resetGestures();
		this.setResult(Activity.RESULT_OK);

		finish();
	}
	public void btnCancel_onClick(View view){

		//closes the activity

		finish();
	}

	public void onBackPressed(){
		//saves the gesture to file, and closes the activity

		GestureRecognizerService.finalizeLearning();
		GestureRecognizerService.setPath(gesturePath);
		GestureRecognizerService.saveGesture(gestureName);
		GestureRecognizerService.resetGestures();
		this.setResult(Activity.RESULT_OK);

		finish();
	}



}
