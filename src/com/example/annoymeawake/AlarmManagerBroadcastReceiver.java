package com.example.annoymeawake;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;


public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
	@SuppressWarnings("unused")
	private String filePath = "";
	final public int MAX_ACTIVITIES = 3;

	final public static String ONE_TIME = "onetime";
	private Activity activity;

	public AlarmManagerBroadcastReceiver() {
		activity = null;
	}

	public AlarmManagerBroadcastReceiver(MainActivity a) {
		activity = a;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
		// Acquire the lock
		wl.acquire();

		// You can do the processing here.
		Bundle extras = intent.getExtras();
		StringBuilder msgStr = new StringBuilder();

		if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
			// Make sure this intent has been sent by the one-time timer button.
			msgStr.append("One time Timer : ");
			Intent i = new Intent(context, RandomActivity.class);
			activity.startActivity(i);
			Log.d("ALARMFIRE", "Random Activity Should Start");
		}
		Format formatter = new SimpleDateFormat("hh:mm:ss a");
		msgStr.append(formatter.format(new Date()));

		Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

		// Release the lock
		wl.release();
	}

	public void SetAlarm(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.FALSE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// After after 5 seconds
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 5, pi);
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public void setOnetimeTimer(Context context, long timeFromNow) {

		filePath = ((MainActivity) activity).getFilePath();

		// Determine random activity
		Random rng = new Random();
		int num = rng.nextInt(MAX_ACTIVITIES);
		Intent intent = null;
		switch (num)
		{
		case 0:	// Shake!
			intent = new Intent(context, ShakeActivity.class);
			break;
		case 1: // Speak!
			intent = new Intent(context, SpeakActivity.class);
			break;
		case 2: // Whistle!
			intent = new Intent(context, WhistleActivity.class);
			break;
		}

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 101,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager am = (AlarmManager) context
				.getSystemService(Activity.ALARM_SERVICE);

		// fires in 5 seconds
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (1000 * 5), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
		
		// fires in timefromnow @VOLATILE
		//am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
			//		+ timeFromNow, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
	}
}