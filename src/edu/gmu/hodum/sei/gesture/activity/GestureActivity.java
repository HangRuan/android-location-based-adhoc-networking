package edu.gmu.hodum.sei.gesture.activity;

import android.app.Activity;
import android.widget.Toast;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import event.GestureListener;
import event.StateEvent;

abstract public class GestureActivity extends Activity implements GestureListener
{
	protected static final String TAG = "gesturelist";
	private boolean isRecognizing = false;

	
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
	
	public void triggerRecognizer(){

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
	}
	
	public void stateReceived(StateEvent event)
	{
	}
	
	public void toast(String text){
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
}