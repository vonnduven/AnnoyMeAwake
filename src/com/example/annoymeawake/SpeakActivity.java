package com.example.annoymeawake;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edmund.Alarm;

public class SpeakActivity extends Activity implements AlarmActivity {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

	private TextView metTextHint;
	private ListView mlvTextMatches;
	private Spinner msTextMatches;
	private Button mbtSpeak;

	Alarm alarm;

	private String[] myString;
	private static final Random rgenerator = new Random();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speak);
		metTextHint = (TextView) findViewById(R.id.etTextHint);
		mlvTextMatches = (ListView) findViewById(R.id.lvTextMatches);
		msTextMatches = (Spinner) findViewById(R.id.sNoOfMatches);
		msTextMatches.setSelection(4);
		mbtSpeak = (Button) findViewById(R.id.btSpeak);
		checkVoiceRecognition();

		setRandomPhrase();

		// Alarm
		initAlarm();
		alarm.start();
	}

	public void setRandomPhrase() {
		Resources res = getResources();
		myString = res.getStringArray(R.array.speakTxt);

		String q = myString[rgenerator.nextInt(myString.length)];

		metTextHint.setText(q);
	}

	public void checkVoiceRecognition() {
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			mbtSpeak.setEnabled(false);
			mbtSpeak.setText("Voice recognizer not present");
			Toast.makeText(this, "Voice recognizer not present",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void speak(View view) {
		// first, pause the alarm
		alarm.pause();

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());

		// Display an hint to the user about what he should say.
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, metTextHint.getText()
				.toString());

		// Given an hint to the recognizer about what the user is going to say
		// There are two form of language model available
		// 1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
		// 2.LANGUAGE_MODEL_FREE_FORM : If not sure about the words or phrases
		// and its domain.
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

		// If number of Matches is not selected then return show toast message
		if (msTextMatches.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
			Toast.makeText(this, "Please select No. of Matches from spinner",
					Toast.LENGTH_SHORT).show();
			return;
		}

		int noOfMatches = Integer.parseInt(msTextMatches.getSelectedItem()
				.toString());
		// Specify how many results you want to receive. The results will be
		// sorted where the first result is the one with higher confidence.
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
		// Start the Voice recognizer activity for the result.
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {

				ArrayList<String> textMatchList = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (!textMatchList.isEmpty()) {
					// If first Match contains the 'search' word
					// Then start web search.
					if (textMatchList.get(0).contains("search")) {

						String searchQuery = textMatchList.get(0);
						searchQuery = searchQuery.replace("search", "");
						Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
						search.putExtra(SearchManager.QUERY, searchQuery);
						startActivity(search);
					} else {
						// populate the Matches
						mlvTextMatches.setAdapter(new ArrayAdapter<String>(
								this, android.R.layout.simple_list_item_1,
								textMatchList));
					}
					// now compare the results to the hint
					compareResults(metTextHint.getText().toString(),
							textMatchList);

				}
				// Result code for various error.
			} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
				showToastMessage("Audio Error");
			} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				showToastMessage("Client Error");
			} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				showToastMessage("Network Error");
			} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				showToastMessage("No Match");
			} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				showToastMessage("Server Error");
			}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Helper method to show the toast message
	 **/
	public void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	// helper method to compare the hint to the results
	// if the hint matches one of the results, display success
	void compareResults(String test, ArrayList<String> results) {
		for (String s : results) {
			if (s.toLowerCase(Locale.ENGLISH).equals(
					test.toLowerCase(Locale.ENGLISH))) {
				showToastMessage("Success!");
				success();
				return;
			}
		}
		showToastMessage("Failure!");
		fail();
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
		Log.d("SPEAK", "SpeakToMe Success!");
		finish();
	}

	@Override
	public void fail() {
		// pick a new phrase
		setRandomPhrase();
		// stop alarm
		alarm.resume();
	}
}