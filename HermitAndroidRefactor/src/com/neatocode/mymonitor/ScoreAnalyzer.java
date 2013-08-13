package com.neatocode.mymonitor;

import org.hermit.android.R;

import android.os.SystemClock;

/**
 * Tracks score.
 */
public class ScoreAnalyzer implements Analyzer {

	private static final String TAG = "ScoreAnalyzer";

	private static final int SCORING_DELAY_MS = 10 * 1000;

	private static ScoreAnalyzer instance = new ScoreAnalyzer();

	private int score = 0;

	private Long lastDisplayedTimestamp = null;

	private Long lastScoringTimestamp = null;

	public static synchronized ScoreAnalyzer getInstance() {
		return instance;
	}

	public synchronized void reset() {
		score = 0;
	}

	@Override
	public synchronized String getLabel() {
		return "Good Job Lance!\n(score " + score + ")";
	}

	@Override
	public synchronized Boolean isConditionGood() {

		if (null != lastDisplayedTimestamp) {
			final long timeElapsedSinceLastDisplay = SystemClock.uptimeMillis()
					- lastDisplayedTimestamp;
			if (timeElapsedSinceLastDisplay > 3000) {
				return null;
			}
		}

		if (0 == score) {
			return null;
		}
		return false;
	}

	public synchronized void updateScore(Analyzer[] analyzers) {
		if (null == lastScoringTimestamp) {
			lastScoringTimestamp = SystemClock.uptimeMillis();
			return;
		}

		long currentTimestamp = SystemClock.uptimeMillis();
		long timeElapsedMs = currentTimestamp - lastScoringTimestamp;
		if (timeElapsedMs > SCORING_DELAY_MS) {
			doScore(analyzers);
			lastScoringTimestamp = SystemClock.uptimeMillis();
		}
	}

	private synchronized void doScore(Analyzer[] analyzers) {
		for (int i = 0; i < analyzers.length; i++) {
			final Analyzer analyzer = analyzers[i];
			final Boolean result = analyzer.isConditionGood();
			if (null != result && result) {
				score++;
				lastDisplayedTimestamp = null;
			}
		}
	}

	public synchronized void didDisplay() {
		if (null == lastDisplayedTimestamp) {
			lastDisplayedTimestamp = SystemClock.uptimeMillis();
		}
	}

	@Override
	public synchronized Integer getDrawable() {
		return R.drawable.good_job_card;
	}

	public synchronized boolean clearBackground() {
		return true;
	}

}
