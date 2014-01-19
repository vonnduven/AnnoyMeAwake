package com.example.annoymeawake;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserActivity;

import com.example.annoymeawake.Utils.Activities;

public class MainActivity extends Activity {

	private static final int FILE_SELECT_RESULT = 9001;
	private static final int SETTINGS_RESULT = 1;
	
	private String filePath = "test";

	private SharedPreferences mPrefs;
	private AlarmManagerBroadcastReceiver alarm;

	private boolean alarmOn = false;
	private boolean vibrate = false;
	private String time = "";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		alarm = new AlarmManagerBroadcastReceiver(this);

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		updateViews();
		((TextView) findViewById(R.id.alarmName))
				.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	protected void onPause() {
		super.onPause();
	}

	// Menu Stuff
	// Initiating Menu XML file (menu.xml)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.main_menu, menu);
		return true;
	}

	public void updateViews() {
		time = mPrefs.getString("timePreference", null);
		((TextView) findViewById(R.id.alarmTime)).setText("Time: " + time);

		String name = mPrefs.getString("namePreference", null);
		((TextView) findViewById(R.id.alarmName)).setText(name);

		alarmOn = mPrefs.getBoolean("alarmToggle", false);
		String onOff = "Alarm: ";
		if (alarmOn)
			onOff += "On";
		else
			onOff += "Off";
		((TextView) findViewById(R.id.alarmStatus)).setText(onOff);

		vibrate = mPrefs.getBoolean("prefVibrate", false);
		
		filePath = mPrefs.getString("prefFilePath", null);
		((TextView) findViewById(R.id.txtPath)).setText("Play Sound: " + filePath);
	}

	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_speak:
			// Starts the VERBATIM minigame
			Utils.toActivity(this, Activities.Speak);
			return true;

		case R.id.menu_shake:
			// Starts the SHAKEME minigame
			Utils.toActivity(this, Activities.Shake);
			return true;

		case R.id.menu_whistle:
			// Starts the SHAKEME minigame
			Utils.toActivity(this, Activities.Whistle);
			return true;

		case R.id.menu_settings:
			// switched to settings activity
			Intent intent = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivityForResult(intent, SETTINGS_RESULT);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SETTINGS_RESULT) {
			if (alarmOn) {
				setAlarm();
				Toast.makeText(this, "Alarm set!", Toast.LENGTH_SHORT).show();
			}
		}
		else if (requestCode == FILE_SELECT_RESULT
				&& resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				File file = (File) bundle
						.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
				filePath = file.getAbsolutePath();
				
				mPrefs.edit().putString("prefFilePath", filePath).commit();
				
			}
		}
		updateViews();
	}

	public void editAlarm(View view) {
		Intent intent = new Intent(getApplicationContext(),
				SettingsActivity.class);
		startActivityForResult(intent, SETTINGS_RESULT);
	}

	@SuppressWarnings("deprecation")
	public void setAlarm() {
		Context context = this.getApplicationContext();

		// get the Date obj from the Time string
		String format = "HH:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		try {
			// fuck all of this, for real
			// why is it so difficult to get a goddamn time difference?
			// you fkn w0t m8
			long currentTime = System.currentTimeMillis();
			Date alarmDate = sdf.parse(time);
			Date now = new Date();
			alarmDate.setYear(now.getYear());
			alarmDate.setMonth(now.getMonth());
			alarmDate.setDate(now.getDate());

			Calendar alarmCal = DateToCalendar(alarmDate);

			long timeFromNow = Math.abs(alarmCal.getTimeInMillis()
					- now.getTime());

			// alarmCal.set = Calendar.YEAR;

			Log.d("SETALARM", "Time: " + time);
			Log.d("SETALARM",
					"AlarmTime in milli: " + alarmCal.getTimeInMillis());
			Log.d("SETALARM",
					"Current Time: " + new Date(currentTime).toString());
			Log.d("SETALARM",
					"Alarm set for: "
							+ new Date(alarmCal.getTimeInMillis()).toString());

			if (alarm != null) {
				alarm.setOnetimeTimer(context, timeFromNow);
			} else {
				Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT)
						.show();
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Calendar DateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public void chooseFile(View view) {
		Intent intent = new Intent(this, FileChooserActivity.class);
		intent.putExtra(FileChooserActivity.INPUT_REGEX_FILTER,
				".*mp3|.*midi|.*ogg|.*wav");
		this.startActivityForResult(intent, FILE_SELECT_RESULT);
	}

	public String getFilePath() {
		return filePath;
	}
}
