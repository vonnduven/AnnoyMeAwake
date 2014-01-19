package com.example.annoymeawake;

import android.content.Context;
import android.content.Intent;

public class Utils {
	
	public enum Results
	{
		SHAKE_SUCCESS
	}
	
	public enum Activities
	{
		Main,
		Settings,
		Speak,
		Shake,
		Whistle
	}
	
	public static void toActivity(Context u, Activities a)
	{
		Intent intent;
		switch (a) {
		case Main:
			break;
		case Speak:
			intent = new Intent(u, SpeakActivity.class);
			u.startActivity(intent);
			break;
		case Shake:
			intent = new Intent(u, ShakeActivity.class);
			u.startActivity(intent);
			break;
		case Whistle:
			intent = new Intent(u, WhistleActivity.class);
			u.startActivity(intent);
			break;
		default:
			break;
		}
	}
}
