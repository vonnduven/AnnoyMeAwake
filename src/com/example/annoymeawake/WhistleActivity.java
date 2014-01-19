/*
 * Copyright (C) 2012 Jacquet Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * musicg api in Google Code: http://code.google.com/p/musicg/
 * Android Application in Google Play: https://play.google.com/store/apps/details?id=com.whistleapp
 * 
 */

package com.example.annoymeawake;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.edmund.Alarm;
import com.musicg.DetectorThread;
import com.musicg.OnSignalsDetectedListener;
import com.musicg.RecorderThread;

public class WhistleActivity extends Activity implements
		OnSignalsDetectedListener, AlarmActivity {

	// self reference
	static WhistleActivity mainApp;

	public static final int DETECT_NONE = 0;
	public static final int DETECT_WHISTLE = 1;
	public static int selectedDetection = DETECT_NONE;

	// detection parameters
	private DetectorThread detectorThread;
	private RecorderThread recorderThread;
	private int numWhistleDetected = 0;

	public final int MAX_WHISTLE = 20;

	// views
	private ProgressBar p;
	private Button whistleButton;

	// alarm
	Alarm alarm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainApp = this;

		// set views
		// LayoutInflater inflater = LayoutInflater.from(this);
		// mainView = inflater.inflate(R.layout.activity_whistle, null);
		setContentView(R.layout.activity_whistle);

		whistleButton = (Button) this.findViewById(R.id.btWhistle);
		whistleButton.setOnClickListener(new ClickEvent());

		p = (ProgressBar) findViewById(R.id.whistleProgress);
		p.setMax(MAX_WHISTLE);

		initAlarm();
		alarm.start();

		whistleButton.performClick();
	}

	class ClickEvent implements OnClickListener {
		public void onClick(View view) {
			if (view == whistleButton) {
				selectedDetection = DETECT_WHISTLE;
				recorderThread = new RecorderThread();
				recorderThread.start();
				detectorThread = new DetectorThread(recorderThread);
				detectorThread.setOnSignalsDetectedListener(mainApp);
				detectorThread.start();
				view.setVisibility(View.INVISIBLE);
				view.setEnabled(false);
				// goListeningView();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Quit demo");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			finish();
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
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

	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onWhistleDetected() {
		/*
		 * TextView textView = (TextView) WhistleActivity.mainApp
		 * .findViewById(R.id.detectedNumberText);
		 * textView.setText(String.valueOf(numWhistleDetected++));
		 */
		runOnUiThread(new Runnable() {
			public void run() {
				numWhistleDetected++;
				Log.d("WHISTLE_DETECT", "Test");
				if (p.getProgress() < MAX_WHISTLE) {
					p.setProgress(numWhistleDetected);
					if (p.getProgress() >= MAX_WHISTLE) {
						TextView t = (TextView) findViewById(R.id.textView1);
						t.setText("BOOM!");
						success();
						// vibrate();
						// playMusic(null);
					}
				}
			}
		});
	}

	@Override
	public void initAlarm() {
		SharedPreferences mPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		Log.d("SHAKER", "Init Alarm");
		String filePath = mPrefs.getString("prefFilePath", null);
		Log.d("INITALARM", filePath);
		alarm = new Alarm(this, true, filePath);
	}

	@Override
	public void success() {
		alarm.stop();
		alarm.getVibrator().cancel();
		Log.d("WHISTLER", "Whistle Success!");
		finish();
	}

	@Override
	public void fail() {
	}
}
