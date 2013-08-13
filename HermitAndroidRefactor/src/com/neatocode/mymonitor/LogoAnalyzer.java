package com.neatocode.mymonitor;

import org.hermit.android.R;

import android.os.SystemClock;

/**
 * Shows logo for a few seconds on startup.
 */
public class LogoAnalyzer implements Analyzer {

	private static final String TAG = "LogoAnalyzer";

	private static final int DISPLAY_LENGTH_MS = 2 * 1000;

	private static LogoAnalyzer instance = new LogoAnalyzer();

	private Long firstDisplayedTimestamp = null;
	
	public static synchronized LogoAnalyzer getInstance() {
		return instance;
	}

	@Override
	public synchronized String getLabel() {
		return "";
	}

	@Override
	public synchronized Boolean isConditionGood() {
		if ( null == firstDisplayedTimestamp ) {
			return false;
		}
		final long timeShowedMs = SystemClock.uptimeMillis() - firstDisplayedTimestamp;
		final boolean withinDisplayTime = timeShowedMs < DISPLAY_LENGTH_MS;
		return !withinDisplayTime;
	}

	@Override
	public synchronized Integer getDrawable() {
		return R.drawable.spectrum_icon;
	}

	public synchronized boolean clearBackground() {
		return true;
	}

	@Override
	public void didDisplay() {
		if ( null == firstDisplayedTimestamp ) {
			firstDisplayedTimestamp = SystemClock.uptimeMillis();
		}
	}

}
