package com.example.edmund;

/**
 * onShake() will probably be called multiple times per 
 * second of shaking, since ShakeIt.java is implemented
 * so that for any moment at which acceleration is greater
 * than a threshold, onShake will be called. Do code
 * accordingly.
 * 
 * @author Edmund Qiu
 *
 */
public interface OnShakeListener {
	public void onShake();
}
