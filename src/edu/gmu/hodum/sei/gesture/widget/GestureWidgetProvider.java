package edu.gmu.hodum.sei.gesture.widget;

import edu.gmu.hodum.sei.gesture.R;
import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
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
			intent.setAction(context.getString(R.string.on));
			PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gesture_widget_layout);
			views.setOnClickPendingIntent(R.id.btn_on_off, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

}
