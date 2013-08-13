package com.neatocode.mymonitor;

import org.hermit.android.R;

import android.util.Log;

/**
 * Checks if the tone of voice in the conversation is higher than average.
 * 
 */
public class CalmVoiceAnalyzer implements Analyzer {

	// TODO cut off low freq on glass?
	// not preset during calibration, but always the same during voice

	private static final String TAG = "VoiceAnalyzer";

	private static final float MIN_FREQ = 0;

	private static final float MAX_FREQ = 4000;

	private static final float SILENCE_THRESHOLD = -40f;
	
	private static final float FREQ_RANGE = MAX_FREQ - MIN_FREQ;

	private static final CalmVoiceAnalyzer instance = new CalmVoiceAnalyzer();

	private float[] calibrationData;

	private Float averageLoudestTone = null;
	
	private long averageLoudestToneSamples;

	private Float currentLoudestTone = null;
	
	private Float currentVolume = null;

	public synchronized static CalmVoiceAnalyzer getInstance() {
		return instance;
	}
	
	public synchronized void setPower(float power) {
		if (Float.isInfinite(power) || Float.isNaN(power)) {
			return;
		}

		Log.i(TAG, "setPower: " + power);
		currentVolume = power;
	}

	public synchronized void setData(float[] data) {
		Log.i(TAG, "setData");

		if (null == data || null == currentVolume || currentVolume < SILENCE_THRESHOLD) {
			return;
		}

		if (null == calibrationData) {
			calibrationData = new float[data.length];
			System.arraycopy(data, 0, calibrationData, 0, data.length);
			return;
		}

		final float freqPerBucket = FREQ_RANGE / (data.length - 1);

		Float max = null;
		Integer loudestFreqIndex = null;
		Float loudestFreqLog = null;
		for (int i = 1; i < data.length; i++) {

			// Try to ignore anything outside human voice range.
			final float freq = freqPerBucket * i;
			if ( freq < 70 || freq > 1500) {
				continue;
			}
			
			float point = data[i];
			float calibrationPoint = calibrationData[i];

			float dataLog = (float) Math.log10(point);
			if (Float.isNaN(dataLog)) {
				dataLog = 0f;
			}

			float calibrationLog = (float) Math.log10(calibrationPoint);
			if (Float.isNaN(calibrationLog)) {
				calibrationLog = 0f;
			}

			float log = dataLog - calibrationLog;
			if (Float.isNaN(log)) {
				log = 0f;
			}

			if (null == loudestFreqLog && null == max) {
				max = log;
				loudestFreqIndex = i;
				loudestFreqLog = dataLog;
			} else {
				if (log >= max) {
					max = log;
					loudestFreqIndex = i;
					loudestFreqLog = dataLog;
				}
			}
		}

		final float loudestFreq = (loudestFreqIndex * freqPerBucket);
		currentLoudestTone = loudestFreq;
		if (null == averageLoudestTone || averageLoudestToneSamples <= 0) {
			averageLoudestTone = loudestFreq;
			averageLoudestToneSamples = 1;
		} else {
			averageLoudestTone = (loudestFreq + (averageLoudestTone * averageLoudestToneSamples)) / (averageLoudestToneSamples + 1);
			averageLoudestToneSamples++;
		}

		Log.i(TAG, "Loudest freq.: " + loudestFreq);
		Log.i(TAG, "Average loudest freq.: " + averageLoudestTone);
	}

	@Override
	public synchronized String getLabel() {
		final Boolean isCalm = isConditionGood();
		return null == isCalm ? "Measuring tone..."
				: isCalm ? ("Good Job Lance!\n("
						+ Math.round(currentLoudestTone) + " vs "
						+ Math.round(averageLoudestTone) + ")")
						: ("Please keep your voice calm.\n("
								+ Math.round(currentLoudestTone) + " vs "
								+ Math.round(averageLoudestTone) + ")");
	}

	@Override
	public synchronized Boolean isConditionGood() {
		if (null == currentLoudestTone || null == averageLoudestTone) {
			return null;
		}
		float delta = averageLoudestTone * 0.2f;

		return currentLoudestTone <= (averageLoudestTone + delta);
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
