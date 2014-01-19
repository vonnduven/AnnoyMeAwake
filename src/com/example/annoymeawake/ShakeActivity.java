package com.example.annoymeawake;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.edmund.Alarm;
import com.example.edmund.OnShakeListener;
import com.example.edmund.ShakeListener;

public class ShakeActivity extends Activity implements OnShakeListener,
		AlarmActivity {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private ShakeListener shakeDetector;
	private Alarm alarm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake);

		// Set up shake handling
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		shakeDetector = new ShakeListener(this);
		shakeDetector.setOnShakeListener(this);
		shakeDetector.setGsThreshold(2.0f);
		initAlarm();
		alarm.start();
	}

	// HARDWARE KEY OVERRIDES
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean result;
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			result = true;
			break;

		default:
			result = super.dispatchKeyEvent(event);
			break;
		}

		return result;
	}

	// HARDWARE KEY OVERRIDES
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// dont do shit
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME
				&& event.getRepeatCount() == 0) {
			// dont do shit
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_CAMERA
				&& event.getRepeatCount() == 0) {
			// dont do shit
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shake, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		sensorManager.registerListener(shakeDetector, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void onPause() {
		super.onPause();
		sensorManager.unregisterListener(shakeDetector);
	}

	@Override
	public void onShake() {
		ProgressBar p = (ProgressBar) findViewById(R.id.progressBar1);
		if (p.getProgress() < 100) {
			p.incrementProgressBy(2);
			if (p.getProgress() == 100) {
				TextView t = (TextView) findViewById(R.id.header);
				t.setText("BOOM!");
				success();
				// vibrate();
				// playMusic(null);
			}
		}
	}

	public void vibrate() {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(2000);
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		alarm.stop();
		alarm.getVibrator().cancel();
		Log.d("SHAKER", "Shaker Success!");
		finish();
	}

	@Override
	public void fail() {
		// TODO Auto-generated method stub
		// tbh shaker shouldn't fail
		Log.d("SHAKER", "Shaker Fail!");
	}

	@Override
	public void initAlarm() {
		// TODO Auto-generated method stub
		SharedPreferences mPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		Log.d("SHAKER", "Init Alarm");
		String filePath = mPrefs.getString("prefFilePath", null);
		Log.d("INITALARM", filePath);
		alarm = new Alarm(this, true, filePath);
	}
}
