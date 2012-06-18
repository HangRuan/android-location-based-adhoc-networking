package edu.gmu.hodum.sei.gesture.activity;

import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import edu.gmu.hodum.sei.gesture.util.CustomAdapter;
import edu.gmu.hodum.sei.gesture.util.RowData;
import edu.gmu.hodum.sei.gesture.R;

public class TrainGestureListActivity extends ListActivity
{
	public static final String GESTURE_ID = "edu.gmu.swe632.fruit.GESTURE_ID";
	public static final String GESTURE_PATH = "edu.gmu.swe632.fruit.GESTURE_PATH";
	public static final String NOT_TRAINED = "Not trained";
	public static final String TRAINED = "Gesture trained";
	private String gesturePath;
	String[] gestureNames;

	private static final String TAG = "traininglist";
	private static String[] details;
	private static final Class<?> nextIntentClasses[] = { 
		TrainGestureActivity.class,
		TrainGestureActivity.class, 
		TrainGestureActivity.class, 
		TrainGestureActivity.class,
		TrainGestureActivity.class,
		TrainGestureActivity.class,
		TrainGestureActivity.class,
		};

	private LayoutInflater mInflater;
	private Vector<RowData> data;
	private RowData rd;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.list);
		
		//get the path from the intent
		Bundle bundle = getIntent().getExtras();
		
		gesturePath = bundle.getString(TrainGestureListActivity.GESTURE_PATH);
		
		initialize();

		this.registerForContextMenu(this.getListView());
	}

	public void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume");

		GestureRecognizerService.loadGestures(gesturePath);
		initialize();
	}

	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause");

		GestureRecognizerService.resetGestures();
	}

	public void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_context_menu, menu);
	}
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		long id = this.getListAdapter().getItemId(info.position);
		
		switch (item.getItemId()) {
		case R.id.select:
			startNextActivity(info.position);	
			return true;
		case R.id.delete:
			CustomAdapter adapter = (CustomAdapter) this.getListAdapter();
			RowData rowData = adapter.getItem((int) id); 
			String name = rowData.getTitle();
			GestureRecognizerService.deleteGesture(name);
			GestureRecognizerService.loadGestures(gesturePath);
			initialize();
			return true;
		case R.id.cancel:
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void onListItemClick(ListView parent, View v, int position, long id)
	{
		startNextActivity(position);
	}

	private void initialize()
	{
		Map<Integer, String> mGestureIdMapping = GestureRecognizerService.GestureIdMapping;
		if(gesturePath.equals(GestureRecognizerService.PATH_MAIN)){
			gestureNames = GestureRecognizerService.GESTURE_NAMES_MAIN;
		}
		else {
			gestureNames = GestureRecognizerService.GESTURE_NAMES_CHOICE;
		}
		
		details = new String[gestureNames.length];

		for (String value : mGestureIdMapping.values())
			Log.d(TAG, "Map has " + value);

		int i = 0;
		for (String gesture : gestureNames)
		{
			Log.d(TAG, "Checking for gesture " + gesture);

			if (mGestureIdMapping.containsValue(gesture))
				details[i] = TRAINED;
			else
				details[i] = NOT_TRAINED;
			i++;
		}

		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		data = new Vector<RowData>();

		for (int j = 0; j < gestureNames.length; j++)
		{
			try
			{
				rd = new RowData(j, gestureNames[j], details[j]);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}

			data.add(rd);
		}

		CustomAdapter adapter = new CustomAdapter(
				this,
				mInflater, 
				R.layout.list_item,
				R.id.title,
				data
				);

		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
	}

	private void startNextActivity(int index)
	{
		if (0 <= index && index < nextIntentClasses.length)
		{
			Bundle bundle = new Bundle();
			bundle.putInt(GESTURE_ID, index);

			Intent intent = new Intent(this, nextIntentClasses[index]);
			intent.putExtras(bundle);
			startActivityForResult(intent, 1);
		}
	}
	
	public void onActivityResult(int requestCode, int resultcode, Intent data){
		if(resultcode ==  Activity.RESULT_OK){
			toast("Gesture trained");
			this.setResult(Activity.RESULT_OK);
		}
	}
	
	public void toast(String text){
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
}
