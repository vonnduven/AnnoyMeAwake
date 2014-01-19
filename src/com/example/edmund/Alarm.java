package com.example.edmund;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

/**
 * Declare Alarm with an Activity, which will probably be the Main activity, and
 * provide a path to the music to be played.
 * 
 * start(), pause(), resume(), stop() do what you'd expect them to
 * setSoundPath(String newFileName) will change the music loaded the next time
 * the Alarm is stopped, then started.
 * 
 * @author Edmund Qiu
 */
public class Alarm implements Runnable{

	private enum AlarmState {
		ON, PAUSED, OFF
	}

	private AlarmState currentState;
	private boolean vibrates;
	private String soundPath;
	private MediaPlayer mediaPlayer;
	private Vibrator vibrator;
	private Activity activity;

	private final int VIBE_LONG = 10000;

	private final long[] PATTERN = { 1500, 2000, 1500, 2000, 1500 };
	private final int REPEAT = 0;
	private boolean songWillChange = false;

	public Alarm(Activity activity, boolean vibrates, String soundPath) {
		currentState = AlarmState.OFF;

		this.activity = activity;
		this.vibrates = vibrates;
		this.soundPath = soundPath;

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setVolume(1.0f, 1.0f);
		mediaPlayer.setLooping(true);

		if (vibrates) {
			vibrator = (Vibrator) activity
					.getSystemService(Context.VIBRATOR_SERVICE);
		}

		readyMusic();
	}

	public Vibrator getVibrator() {
		return vibrator;
	}

	private void readyMusic() {
		try {
			mediaPlayer.setDataSource(soundPath);
		} catch (IOException e) {

		}
	}

	public void setSoundPath(String newSoundPath) {
		// If newSoundPath is different, then
		// update later.
		this.soundPath = newSoundPath;
		songWillChange = true;
	}

	public void start() {
		switch (currentState) {
		case OFF:
			// If new song, please change
			if (songWillChange) {
				mediaPlayer.reset();
				readyMusic();
				songWillChange = false;
			}

			// Start the music
			try {
				mediaPlayer.prepare();
			} catch (IOException e) {
			}
			mediaPlayer.seekTo(0);
			mediaPlayer.start();

			// Start the vibrations
			if (vibrates) {
				vibrator.vibrate(PATTERN, REPEAT);
				//vibrator.vibrate(VIBE_LONG);
			}
			currentState = AlarmState.ON;
			break;

		case PAUSED:
			resume();
			break;

		case ON:
			break;

		}
	}

	public void pause() {
		switch (currentState) {
		case ON:
			if (vibrates) {
				vibrator.cancel();
			}
			mediaPlayer.pause();
			currentState = AlarmState.PAUSED;
			//Handler handler = new Handler(); 
		    //handler.postDelayed(this, 10000); 
			break;

		case OFF:
		case PAUSED:
			break;

		}
	}

	public void resume() {
		switch (currentState) {
		case PAUSED:
			if (vibrates) {
				vibrator.vibrate(PATTERN, REPEAT);
				//vibrator.vibrate(VIBE_LONG);
			}
			mediaPlayer.start();
			currentState = AlarmState.ON;
			break;

		case OFF:
		case ON:
			break;
		}
	}

	public void stop() {
		/*
		 * switch (currentState) { case ON: mediaPlayer.stop(); if (vibrates) {
		 * vibrator.cancel(); } case PAUSED: mediaPlayer.stop(); if (vibrates) {
		 * vibrator.cancel(); } break; case OFF: break; }
		 */
		mediaPlayer.stop();
		vibrator.cancel();
		currentState = AlarmState.OFF;
	}

	@Override
	public void run() {
		// called when pause is up
		resume();
	}
}
