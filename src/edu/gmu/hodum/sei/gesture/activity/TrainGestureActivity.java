package edu.gmu.hodum.sei.gesture.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService.LocalBinder;

public class TrainGestureActivity extends Activity
{
	private int gestureId;
	private String gesturePath;
	private static final String TAG = "traingesture";
	private boolean mIsBound = false; 
	
	private ServiceConnection mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			binder.getService();
			mIsBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIsBound = false;
		}
		
	};
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.train);

		Bundle bundle = getIntent().getExtras();
		gestureId = bundle.getInt(TrainGestureListActivity.GESTURE_ID);
		gesturePath = bundle.getString(TrainGestureListActivity.GESTURE_PATH);
		
		//binds to the service
		Intent intent = new Intent(this, GestureRecognizerService.class);
		intent.setAction(this.getString(R.string.train));
		this.bindService(intent, mConnection, 0);
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
		GestureRecognizerService.saveGesture(gestureName, gesturePath);
		GestureRecognizerService.resetGestures();
		this.setResult(Activity.RESULT_OK);
		
		cleanup();
		finish();
	}
	public void onBackPressed(){
		cleanup();
		this.finish();
	}
	public void cleanup(){
		if(mIsBound){
			this.unbindService(mConnection);
			mIsBound = false;
		}
	}
	
	
	
}
