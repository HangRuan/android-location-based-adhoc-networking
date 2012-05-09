package edu.gmu.hodum.sei.gesture.widget;

import edu.gmu.hodum.sei.gesture.service.GestureRecognizerService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

	public static boolean wasScreenOn = true;

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			// DO WHATEVER YOU NEED TO DO HERE
			wasScreenOn = false;
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			// AND DO WHATEVER YOU NEED TO DO HERE
			wasScreenOn = true;
		}
		Intent i = new Intent(context, GestureRecognizerService.class);
		i.putExtra("screen_state", wasScreenOn);
		context.startService(i);

	}
}

