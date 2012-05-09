package edu.gmu.hodum.sei.gesture.widget;

import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class GestureWidgetProvider extends AppWidgetProvider{

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }
	
	public static PendingIntent makePendingIntent(Context context, String command, int appWidgetId) {
        Intent active = new Intent(context, GestureRecognizerService.class);
        active.setAction(command);
        active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //this Uri data is to make the PendingIntent unique, so it wont be updated by FLAG_UPDATE_CURRENT
        //so if there are multiple widget instances they wont override each other
        return(PendingIntent.getService(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
