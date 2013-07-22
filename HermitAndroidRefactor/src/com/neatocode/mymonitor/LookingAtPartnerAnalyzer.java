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
public class LookingAtPartnerAnalyzer implements SensorListener, Analyzer {

	private static final float TOO_HIGH = -105f;

	private static final float TOO_LOW = -75f;

	private static final String TAG = "GazeAnalyzer";

	private static LookingAtPartnerAnalyzer instance;

	private SensorManager mManager;

	private Float verticalOrientation;

	public LookingAtPartnerAnalyzer(Context context) {
		mManager = (SensorManager) context
				.getSystemService(Activity.SENSOR_SERVICE);
		mManager.registerListener(this, SensorManager.SENSOR_ORIENTATION);
	}

	public static synchronized LookingAtPartnerAnalyzer getOrCreateInstance(
			final Context context) {
		if (null == instance) {
			instance = new LookingAtPartnerAnalyzer(context);
		}
		return instance;
	}

	public static synchronized LookingAtPartnerAnalyzer getInstance() {
		return instance;
	}

	@Override
	public synchronized void onAccuracyChanged(int sensor, int accuracy) {
	}

	@Override
	public synchronized void onSensorChanged(int sensor, float[] values) {
		Log.i(TAG, "Orientation: " + values[0] + ", " + values[1] + ", "
				+ values[2]);
		verticalOrientation = values[1];
	}

	@Override
	public synchronized String getLabel() {
		final Boolean isLookingAhead = isNominal();
		return null == isLookingAhead ? "Checking eye level..."
				: isLookingAhead ? ("Good Job Lance!\n(Gaze level: "
						+ Math.round(verticalOrientation) + ")")
						: ("Try to make eye contact!\n(Gaze level: "
								+ Math.round(verticalOrientation) + ")");
	}

	@Override
	public synchronized Boolean isNominal() {
		if (null == verticalOrientation) {
			return null;
		}
		return verticalOrientation < TOO_LOW && verticalOrientation > TOO_HIGH;
	}

	@Override
	public synchronized Integer getDrawable() {
		return R.drawable.gaze_card;
	}

	public synchronized void didDisplay() {
	}

	public synchronized boolean clearBackground() {
		return true;
	}

}
