package edu.gmu.hodum.sei.gesture.widget;

import java.util.List;

import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class GestureWidgetProvider extends AppWidgetProvider{

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		for (int i=0; i<N; i++) {
			int appWidgetId = appWidgetIds[i];
			Intent intent = new Intent(context, GestureRecognizerService.class);


			//check if the service is running
			ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);

			boolean flag = true;
			int j = 0;
			while(flag && j<services.size()){
				String serviceClassName = services.get(j).service.getClassName();
				//if the service is running, the flag is set to false
				if(serviceClassName.equals("edu.gmu.hodum.sei.gesture.service.GestureRecognizerService")){
					flag = false;
				}
				j++;
			}
			System.out.println("Flag = "+flag);
			//if the service is not running
			if(flag){
				intent.setAction(context.getString(R.string.on));
			}
			//if the service is running
			else{
				intent.setAction(context.getString(R.string.off));
			}
			PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gesture_widget_layout);
			views.setOnClickPendingIntent(R.id.btn_on_off, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

}
