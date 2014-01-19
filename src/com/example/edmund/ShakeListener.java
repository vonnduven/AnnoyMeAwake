package com.example.edmund;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Activity;
import android.util.FloatMath;


/**
 * Detects when the acceleration acting on the device is
 * greater than a specified threshold. When it does, it
 * will call the method onShake() in whatever implements
 * the OnShakeListener interface. So..
 * 1. Set up ShakeIt in an Activity so it listens
 * 2. Provide it using setOnShakeListener() the instance
 * which reacts to shaking. 
 * 3. Use setGsThreshold() for a difficulty setting
 * 		1.0 fills up by itself, 1.1 doesn't, 1.5 is easy,
 * 		2.0 is alright, 2.5 requires decent exertion
 * 		3.0 is difficult, 3.1-2 are hard, 3.5 is impossible
 * 
 * Untested for anything besides my Galaxy S4 so who knows.
 * 
 * @author Edmund Qiu
 *
 */
public class ShakeListener implements SensorEventListener {
	
	private OnShakeListener listener;

	public static final float gravity = SensorManager.GRAVITY_EARTH;
	public static final float gravitySq = gravity * gravity;
	private float shakeThresholdInGs;
	
	public Activity m;
	
	/**
	 * 
	 * @param m
	 */
	public ShakeListener(Activity m)
	{
		this.m = m;
		shakeThresholdInGs = 1.0f;
	}
	
	public void setGsThreshold(float g)
	{
		this.shakeThresholdInGs = g;
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void setOnShakeListener(OnShakeListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//We don't actually use this...
	}

	@Override
	public void onSensorChanged(SensorEvent s) {
		
		if (listener != null)
		{
			float x = s.values[0];
			float y = s.values[1];
			float z = s.values[2];
			
			float underRoot = (x*x + y*y + z*z)/gravitySq;
			float gForce = FloatMath.sqrt(underRoot);
			
			if (gForce > shakeThresholdInGs)
			{
				listener.onShake();
			}	
		}
		
	}
	
}
