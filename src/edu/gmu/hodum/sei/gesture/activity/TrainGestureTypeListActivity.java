package edu.gmu.hodum.sei.gesture.activity;

import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TrainGestureTypeListActivity extends ListActivity{
	private static final String TAG = "trainingtypelist";
	public static final String GESTURE_PATH = "edu.gmu.hodum.sei.gesture.GESTURE_PATH";
	public static final String GESTURE_LEARNING_METHOD = "edu.gmu.hodum.sei.gesture.GESTURE_LEARNING_METHOD";
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.list);
		
		//create array of choices
		String[] gestureTypes = new String[]{"Normal Gestures", "Choice Mode Gestures"};
		ListAdapter adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				gestureTypes);
        setListAdapter(adapter);
	}
	
	public void onListItemClick(ListView parent, View v, int position, long id)
	{
		
		Bundle bundle = new Bundle();
		if(position == 0){
			bundle.putString(GESTURE_PATH, GestureRecognizerService.PATH_MAIN);
			bundle.putString(GESTURE_LEARNING_METHOD, GestureRecognizerService.LEARNING_METHOD_ACTIVATED);
		}
		else{
			bundle.putString(GESTURE_PATH, GestureRecognizerService.PATH_CHOICE);
			bundle.putString(GESTURE_LEARNING_METHOD, GestureRecognizerService.LEARNING_METHOD_QUIET);
		}

		Intent intent = new Intent(this, TrainGestureListActivity.class);
		intent.putExtras(bundle);
		this.startActivityForResult(intent,1);
	}

	public void onActivityResult(int requestCode, int resultcode, Intent data){
		if(resultcode ==  Activity.RESULT_OK){
			this.setResult(Activity.RESULT_OK);
		}
	}
	public void toast(String text){
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
}
