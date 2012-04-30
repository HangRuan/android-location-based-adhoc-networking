package edu.gmu.hodum.sei.gesture.activity;

import java.util.Map;
import java.util.Vector;

import android.app.Activity;
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
import event.GestureEvent;

public class TrainGestureListActivity extends GestureListActivity
{
	public static final String GESTURE_ID = "edu.gmu.swe632.fruit.GESTURE_ID";
	public static final String NOT_TRAINED = "Not trained";
	public static final String TRAINED = "Gesture trained";

	private static final String TAG = "traininglist";
	private static String[] details;
	private static final Class<?> nextIntentClasses[] = { TrainGestureActivity.class,
		TrainGestureActivity.class, TrainGestureActivity.class, TrainGestureActivity.class,
		TrainGestureActivity.class };

	private LayoutInflater mInflater;
	private Vector<RowData> data;
	private RowData rd;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.list);
		initialize();

		this.registerForContextMenu(this.getListView());
	}

	public void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume");

		GestureRecognizerService.loadGestures();
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
			GestureRecognizerService.loadGestures();
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
		String[] gestureNames = GestureRecognizerService.GESTURE_NAMES;
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

		for (int j = 0; j < GestureRecognizerService.GESTURE_NAMES.length; j++)
		{
			try
			{
				rd = new RowData(j, GestureRecognizerService.GESTURE_NAMES[j], details[j]);
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
			startActivity(intent);
		}
	}

	public void gestureReceived(GestureEvent event)
	{
		if (event.getProbability() > 0.9)
		{
			String gesture = GestureRecognizerService.GestureIdMapping.get(event.getId());

			if (gesture != null)
			{
				Log.d(TAG, "Gesture received " + gesture);


				if (gesture.equalsIgnoreCase(GestureRecognizerService.SOS_GESTURE))
					Toast.makeText(this, "SOS recognized", Toast.LENGTH_LONG).show();
				else if (gesture.equalsIgnoreCase(GestureRecognizerService.SUPPLIES_GESTURE))
					Toast.makeText(this, "Supplies recognized", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this, "Unrecognized gesture", Toast.LENGTH_LONG).show();
			}
		}
	}
}
