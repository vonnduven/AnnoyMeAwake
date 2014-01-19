package com.example.annoymeawake;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RandomActivity extends Activity {
	// random classes
	public Class<?>[] classes = new Class<?>[] { ShakeActivity.class,
			SpeakActivity.class };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		Log.d("RA", "OnCreate RandAct");
		startRandomActivity();
	}

	public void startRandomActivity() {
		Random rng = new Random(classes.length);
		Intent intent = new Intent(getApplicationContext(),
				classes[rng.nextInt()]);

		Log.d("RA", "Start RandAct");
		startActivity(intent);
	}
}
