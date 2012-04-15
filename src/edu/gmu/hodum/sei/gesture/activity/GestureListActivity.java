package edu.gmu.hodum.sei.gesture.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ListActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import event.GestureListener;
import event.StateEvent;

abstract public class GestureListActivity extends ListActivity implements GestureListener
{
	private static final String TAG = "gesturelist";
	private boolean isRecognizing = false;
	private boolean isAllowed = true;
	private Timer allowTimer = null;

	public void onResume()
	{
		super.onResume();
		GestureRecognizerService.addGestureListener(this);
	}

	public void onPause()
	{
		super.onPause();
		GestureRecognizerService.removeGestureListener(this);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
			if (isAllowed)
			{
				if (isRecognizing)
				{
					Toast.makeText(this, GestureRecognizerService.CAPTURED, Toast.LENGTH_SHORT)
							.show();
					GestureRecognizerService.stopRecognizer();
				}
				else
				{
					Toast.makeText(this, GestureRecognizerService.CAPTURE, Toast.LENGTH_SHORT)
							.show();
					GestureRecognizerService.startRecognizer();
				}

				isRecognizing = !isRecognizing;
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

	public void stateReceived(StateEvent event)
	{
	}

	private class AllowTask extends TimerTask
	{
		public void run()
		{
			GestureListActivity.this.isAllowed = true;
			Log.d(TAG, "allow");
		}
	}
}
