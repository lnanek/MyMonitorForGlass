package com.neatocode.mymonitor;

import org.hermit.android.R;

import android.os.SystemClock;
import android.util.Log;

/**
 * Checks the delay between speech in the conversation.
 * 
 */
public class RapidVoiceOrSilenceAnalyzer implements Analyzer {
	// TODO check if consistent no gap speech?

	private static final String TAG = "VoiceAnalyzer";

	private static final float SPEAKING_INCREASE_PERCENT = 0.1f;

	private static final RapidVoiceOrSilenceAnalyzer instance = new RapidVoiceOrSilenceAnalyzer();

	private Float lastSum;

	private Float currentSum;

	private Long lastSpeakingTimestamp = null;

	private Long currentTimestamp = null;

	private Long currentTimeElapsed = null;

	private Boolean isSpeaking;

	private Long averageDelay = null;

	public synchronized static RapidVoiceOrSilenceAnalyzer getInstance() {
		return instance;
	}

	public synchronized void setData(float[] data) {
		Log.i(TAG, "setData");

		if (null == data) {
			return;
		}

		float sum = 0;
		for (int i = 1; i < data.length; i++) {
			float point = data[i];

			float dataLog = (float) Math.log10(point);
			if (Float.isNaN(dataLog)) {
				dataLog = 0f;
			}

			sum += dataLog;
		}

		currentSum = sum;
		currentTimestamp = SystemClock.uptimeMillis();
		if (null == lastSum) {
			lastSum = currentSum;
			return;
		}

		final float delta = lastSum * SPEAKING_INCREASE_PERCENT;
		if (isSpeaking) {
			// Check if speaking stopped.
			if (currentSum < lastSum - delta) {
				isSpeaking = false;
				lastSum = currentSum;
				currentTimeElapsed = currentTimestamp - lastSpeakingTimestamp;

				if (null == averageDelay) {
					averageDelay = currentTimeElapsed;
				} else {
					averageDelay = (averageDelay + currentTimeElapsed) / 2;
				}
			}
		} else {
			// Check if speaking started.
			if (currentSum > lastSum + delta) {
				isSpeaking = true;
				lastSum = currentSum;
			}
		}
	}

	@Override
	public synchronized String getLabel() {

		if (null != averageDelay && averageDelay < 500) {
			return "SPEAKING RAPIDLY (" + averageDelay + "ms)";
		}

		final Boolean isSpeakingReguarly = isNominal();
		return null == isSpeakingReguarly ? "Measuring speaking rate..."
				: currentTimeElapsed > 4000 ? ("LONG SILENCE ("
						+ currentTimeElapsed + "ms)") : ("SPEAKING OK ("
						+ currentTimeElapsed + "ms)");
	}

	@Override
	public synchronized Boolean isNominal() {
		if (null == currentTimeElapsed) {
			return null;
		}
		return currentTimeElapsed < 4000 || averageDelay > 500;
	}

	@Override
	public synchronized Integer getDrawable() {
		return R.drawable.take_turns_card;
	}

	public synchronized void didDisplay() {
	}

	public synchronized boolean clearBackground() {
		return true;
	}

}