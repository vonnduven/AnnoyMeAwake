package com.example.annoymeawake;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.example.util.TimePreference;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {


	SharedPreferences.Editor editor;
	SharedPreferences sp;
	String filePath;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_settings);
		addPreferencesFromResource(R.xml.pref_alarm);

		sp = getPreferenceScreen().getSharedPreferences();

		EditTextPreference editTextPref = (EditTextPreference) findPreference("namePreference");
		editTextPref.setSummary(sp.getString("namePreference",
				"Some Default Text"));

		TimePreference timePref = (TimePreference) findPreference("timePreference");
		timePref.setSummary(sp.getString("timePreference", "Some Default Text"));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference pref = findPreference(key);
		if (pref instanceof EditTextPreference) {
			EditTextPreference etp = (EditTextPreference) pref;
			pref.setSummary(etp.getText());
		}
		if (pref instanceof TimePreference) {
			TimePreference tp = (TimePreference) pref;
			pref.setSummary(tp.getTime());
		}
	}
	/*
	 * @Override public void onStop() { // do your stuff here super.onStop();
	 * setResult(RESULT_CANCELED); }
	 * 
	 * void cancel(View view) { setResult(RESULT_CANCELED); }
	 * 
	 * void confirm(View view) { editor = sp.edit();
	 * editor.putString("timePreference", sp.getString("timePreference", null));
	 * editor.putString("namePreference", sp.getString("namePreference", null));
	 * editor.putBoolean("prefVibrate", sp.getBoolean("prefVibrate", false));
	 * editor.putBoolean("alarmToggle", sp.getBoolean("alarmToggle", false));
	 * editor.commit(); setResult(RESULT_OK); }
	 */
}
