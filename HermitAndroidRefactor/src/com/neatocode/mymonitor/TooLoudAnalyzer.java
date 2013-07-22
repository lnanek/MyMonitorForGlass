package com.neatocode.mymonitor;

import org.hermit.android.R;

import android.util.Log;

/**
 * Tracks if people are speaking too loudly.
 */
public class TooLoudAnalyzer implements Analyzer {

	private static final String TAG = "TooLoudAnalyzer";

	private static final float TOO_LOUD = -20f;

	private static TooLoudAnalyzer instance = new TooLoudAnalyzer();

	private Float currentVolume;

	public static synchronized TooLoudAnalyzer getInstance() {
		return instance;
	}

	@Override
	public synchronized String getLabel() {
		final Boolean okLoudness = isNominal();
		return null == okLoudness ? "Measuring loudness..."
				: okLoudness ? ("Good Job Lance!\n("
						+ Math.round(currentVolume) + "dB vs "
						+ Math.round(TOO_LOUD) + "dB)")
						: ("Please keep your voice down.\n("
								+ Math.round(currentVolume) + "dB vs "
								+ Math.round(TOO_LOUD) + "dB)");
	}

	@Override
	public synchronized Boolean isNominal() {
		if (null == currentVolume) {
			return null;
		}
		return currentVolume < TOO_LOUD;
	}

	public synchronized void setPower(float power) {
		if (Float.isInfinite(power) || Float.isNaN(power)) {
			return;
		}

		Log.i(TAG, "setPower: " + power);
		currentVolume = power;
	}

	@Override
	public synchronized Integer getDrawable() {
		return R.drawable.loud_card;
	}

	public synchronized void didDisplay() {
	}

	public synchronized boolean clearBackground() {
		return true;
	}

}
