package edu.gmu.hodum.sei.gesture.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;


public class TrainGestureActivity extends Activity
{
	private int gestureId;
	private static final String TAG = "traingesture";
	private boolean isLearning = false;
	private int gestureCount = 0;
	private boolean isAllowed = true;
	private Timer allowTimer = null;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.train);

		Bundle bundle = getIntent().getExtras();
		gestureId = bundle.getInt(TrainGestureListActivity.GESTURE_ID);

		Button done = (Button) findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
				String gestureName = GestureRecognizerService.GESTURE_NAMES[gestureId];
				GestureRecognizerService.finalizeLearning();
				GestureRecognizerService.saveGesture(gestureName);
				GestureRecognizerService.resetGestures();
				finish();
			}
		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
			if (isAllowed)
			{
				if (isLearning)
				{
					Toast.makeText(this, GestureRecognizerService.CAPTURED + " "+ Integer.toString(++gestureCount), Toast.LENGTH_SHORT)
							.show();
					GestureRecognizerService.stopLearning();
				}
				else
				{
					Toast.makeText(this, GestureRecognizerService.CAPTURE, Toast.LENGTH_SHORT)
							.show();
					GestureRecognizerService.startLearning();
				}

				isLearning = !isLearning;
				isAllowed = false;
				Log.d(TAG, "disallow");
			}

			if (allowTimer != null)
				allowTimer.cancel();

			allowTimer = new Timer();
			allowTimer.schedule(new AllowTask(), 400);

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private class AllowTask extends TimerTask
	{
		public void run()
		{
			TrainGestureActivity.this.isAllowed = true;
			Log.d(TAG, "allow");
		}
	}
}
