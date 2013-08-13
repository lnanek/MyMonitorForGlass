package com.neatocode.mymonitor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AnalyzerDisplay {

	private static final String TAG = "AnalyzerDisplay";

	private static AnalyzerDisplay instance;

	private Context context;

	private boolean isTouchDown;

	private Integer displayIndex = null;

	public AnalyzerDisplay(Context context) {
		this.context = context;
	}

	public static synchronized AnalyzerDisplay getOrCreateInstance(
			final Context context) {
		if (null == instance) {
			instance = new AnalyzerDisplay(context);
		}
		return instance;
	}

	public static AnalyzerDisplay getInstance() {
		return instance;
	}

	final Paint redPaint = new Paint();
	{
		redPaint.setColor(Color.RED);
		redPaint.setAlpha(128);
		redPaint.setStyle(Paint.Style.FILL);
	}

	final Paint greenPaint = new Paint();
	{
		greenPaint.setColor(Color.GREEN);
		greenPaint.setAlpha(128);
		greenPaint.setStyle(Paint.Style.FILL);
	}

	final Paint blackPaint = new Paint();
	{
		greenPaint.setColor(Color.BLACK);
		// greenPaint.setAlpha(128);
		greenPaint.setStyle(Paint.Style.FILL);
	}

	public synchronized Analyzer[] getAnalyzers() {
		return new Analyzer[] {
				//LogoAnalyzer.getInstance(),
				LookingAtPartnerAnalyzer.getOrCreateInstance(null),
				CalmVoiceAnalyzer.getInstance(),
				RapidVoiceOrSilenceAnalyzer.getInstance(),
				TooLoudAnalyzer.getInstance(),
				NearCrowdedLocationAnalyzer.getOrCreateInstance(null),
				ScoreAnalyzer.getInstance(), BrightnessAnalyzer.getInstance(),
				BlankAnalyzer.getInstance(), 
				};
	}

	public synchronized void onTouchUp() {
		Log.i(TAG, "onTouchUp");

		ScoreAnalyzer.getInstance().reset();

		if (null == displayIndex) {
			displayIndex = 0;
		} else {
			displayIndex++;
			if (displayIndex == getAnalyzers().length) {
				displayIndex = null;
			}
		}
		isTouchDown = false;
	}

	public synchronized void onTouchDown() {
		Log.i(TAG, "onTouchDown");
		isTouchDown = true;
	}

	public synchronized void doDraw(Canvas canvas) {
		Log.i(TAG, "doDraw");

		final Analyzer[] analyzers = getAnalyzers();
		ScoreAnalyzer.getInstance().updateScore(analyzers);

		final Rect rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());

		// Display a single analyzer.
		if (null != displayIndex) {
			Log.i(TAG, "doDraw showing analyzer: " + displayIndex);
			final Analyzer analyzer = analyzers[displayIndex];
			drawAnalyzer(canvas, rect, analyzer, false);
			return;
		}

		// Display first analyzer with a warning.
		for (int i = 0; i < analyzers.length; i++) {
			Log.i(TAG, "doDraw checking for analyzer to show");
			final Analyzer analyzer = analyzers[i];
			final Boolean isConditionGood = analyzer.isConditionGood();
			if (null != isConditionGood && !isConditionGood) {
				Log.i(TAG, "showing analyzer: "
						+ analyzer.getClass().getSimpleName());
				drawAnalyzer(canvas, rect, analyzer, true);
				return;
			}
		}

		// Otherwise display good job!
		canvas.drawRect(rect, blackPaint);
		drawStringInRect(canvas, rect, "(monitoring...)");
	}

	private synchronized void drawAnalyzer(Canvas canvas, final Rect rect,
			final Analyzer analyzer, boolean skipColor) {

		if (analyzer.clearBackground()) {
			canvas.drawRect(rect, blackPaint);
		}

		final String label = analyzer.getLabel();
		final Boolean result = analyzer.isConditionGood();
		if (!skipColor && null != result) {
			Paint color = result ? greenPaint : redPaint;
			canvas.drawRect(rect, color);
		}
		final Integer drawableId = analyzer.getDrawable();
		if (null != drawableId && drawableId > 0) {
			Drawable drawable = context.getResources().getDrawable(drawableId);
			drawable.setBounds(rect);
			drawable.draw(canvas);
		}
		analyzer.didDisplay();
		drawStringInRect(canvas, rect, label);
	}

	private synchronized void drawStringInRect(Canvas c, Rect r, String s) {
		// TODO get bounds and center vertically? wrap text?
		Paint paint = new Paint();
		paint.setTextSize(40);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		// c.drawText(s, r.centerX(), r.centerY(), paint);
		drawMultiLineText("\n\n" + s, r.centerX(), r.centerY(), paint, c);
	}

	private synchronized void drawMultiLineText(String str, float x, float y,
			Paint paint, Canvas canvas) {
		String[] lines = str.split("\n");
		float txtSize = -paint.ascent() + paint.descent();

		if (paint.getStyle() == Style.FILL_AND_STROKE
				|| paint.getStyle() == Style.STROKE) {
			txtSize += paint.getStrokeWidth(); // add stroke width to the text
												// size
		}
		float lineSpace = txtSize * 0.2f; // default line spacing

		for (int i = 0; i < lines.length; ++i) {
			canvas.drawText(lines[i], x, y + (txtSize + lineSpace) * i, paint);
		}
	}
}
