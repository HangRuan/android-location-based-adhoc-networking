package edu.gmu.hodum.sei.gesture.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import edu.gmu.hodum.sei.gesture.service.RecognizerState;

public class TrainGestureActivity extends Activity{
	private int gestureId;
	private String gesturePath;
	private static final String TAG = "traingesture";

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.train);

		Bundle bundle = getIntent().getExtras();
		gestureId = bundle.getInt(TrainGestureListActivity.GESTURE_ID);
		gesturePath = bundle.getString(TrainGestureListActivity.GESTURE_PATH);
		
		//GestureRecognizerService.setLearningMode(true);
		GestureRecognizerService.resetGestureCount();
		if(gesturePath.equals(GestureRecognizerService.PATH_MAIN)){
			GestureRecognizerService.setState(RecognizerState.LEARNING_MAIN_DEACTIVATED);
		}
		else{
			GestureRecognizerService.setState(RecognizerState.LEARNING_CHOICE_DEACTIVATED);
		}
	}

	public void btnDone_onClick(View view){
		
		//saves the gesture to file, and closes the activity
		String gestureName;
		
		if(gesturePath.equals(GestureRecognizerService.PATH_MAIN)){
			gestureName = GestureRecognizerService.GESTURE_NAMES_MAIN[gestureId];
		}
		else{
			gestureName = GestureRecognizerService.GESTURE_NAMES_CHOICE[gestureId];	
		}
		GestureRecognizerService.finalizeLearning();
		GestureRecognizerService.setPath(gesturePath);
		GestureRecognizerService.saveGesture(gestureName);
		GestureRecognizerService.resetGestures();
		this.setResult(Activity.RESULT_OK);
		
		finish();
	}
	
	
	
}
