package edu.gmu.hodum.sei.gesture.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;


public class TrainGestureActivity extends Activity
{
	private int gestureId;
	private static final String TAG = "traingesture";

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.train);

		Bundle bundle = getIntent().getExtras();
		gestureId = bundle.getInt(TrainGestureListActivity.GESTURE_ID);

	}

	public void btnDone_onClick(View view){
		String gestureName = GestureRecognizerService.GESTURE_NAMES[gestureId]; 
		GestureRecognizerService.finalizeLearning();
		GestureRecognizerService.saveGesture(gestureName);
		GestureRecognizerService.resetGestures();
		this.setResult(Activity.RESULT_OK);
		finish();
	}
	
	public void onResume(){
		super.onResume();
		Intent intent = new Intent(this, GestureRecognizerService.class);
		intent.setAction(this.getString(R.string.train));
		startService(intent);
	}
	public void onPause(){
		super.onPause();
		Intent intent = new Intent(this, GestureRecognizerService.class);
		intent.setAction(this.getString(R.string.on));
		startService(intent);
	}
	
}
