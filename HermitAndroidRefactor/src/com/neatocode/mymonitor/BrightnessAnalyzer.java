package com.neatocode.mymonitor;

import org.hermit.android.R;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Tracks the users gaze to see if they are looking at their partner.
 */
public class BrightnessAnalyzer implements SensorListener, Analyzer {

	private static final String TAG = "BrightnessAnalyzer";

	private static final float TOO_HIGH = 50f;

	private static BrightnessAnalyzer instance;

	private SensorManager mManager;

	private Float brightness;

	public BrightnessAnalyzer(Context context) {
		mManager = (SensorManager) context
				.getSystemService(Activity.SENSOR_SERVICE);
		mManager.registerListener(this, SensorManager.SENSOR_LIGHT);
	}

	public static synchronized BrightnessAnalyzer getOrCreateInstance(
			final Context context) {
		if (null == instance) {
			instance = new BrightnessAnalyzer(context);
		}
		return instance;
	}

	public static synchronized BrightnessAnalyzer getInstance() {
		return instance;
	}

	@Override
	public synchronized void onAccuracyChanged(int sensor, int accuracy) {
	}

	@Override
	public synchronized void onSensorChanged(int sensor, float[] values) {
		Log.i(TAG, "Brightness: " + values[0]);
		brightness = values[1];
	}

	@Override
	public synchronized String getLabel() {
		final Boolean isBright = isConditionGood();
		return null == isBright ? "Checking brightness...\n(coming soon)"
				: isBright ? ("Good Job Lance!\n(Brightness "
						+ Math.round(brightness) + " lux)")
						: ("It might be bright outside.\n(Brightness "
								+ Math.round(brightness) + "lux)");
	}

	@Override
	public synchronized Boolean isConditionGood() {
		if (null == brightness) {
			return null;
		}
		return brightness > TOO_HIGH;
	}

	@Override
	public synchronized Integer getDrawable() {
		return R.drawable.bright_card;
	}

	public synchronized void didDisplay() {
	}

	public synchronized boolean clearBackground() {
		return true;
	}
}
