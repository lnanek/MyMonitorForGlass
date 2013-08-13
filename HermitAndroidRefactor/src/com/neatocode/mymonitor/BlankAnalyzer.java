package com.neatocode.mymonitor;


/**
 * Show Audalyzer screen.
 */
public class BlankAnalyzer implements Analyzer {

	private static final String TAG = "BlankAnalyzer";

	private static BlankAnalyzer instance = new BlankAnalyzer();

	public BlankAnalyzer() {
	}

	public static synchronized BlankAnalyzer getInstance() {
		return instance;
	}

	@Override
	public synchronized String getLabel() {
		return "";
	}

	@Override
	public synchronized Boolean isConditionGood() {
		return null;
	}

	@Override
	public synchronized Integer getDrawable() {
		return null;
	}

	public synchronized void didDisplay() {
	}

	public synchronized boolean clearBackground() {
		return false;
	}
}
