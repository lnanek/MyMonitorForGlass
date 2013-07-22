package com.neatocode.mymonitor;

import org.hermit.android.R;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Track how close to crowded areas where might have trouble dealing with people
 * and other dangerous, stressful, or difficult locations.
 */
public class NearCrowdedLocationAnalyzer implements LocationListener, Analyzer {

	private static final String TAG = "NearCrowdedLocationAnalyzer";

	// For this demo we use the embarcadero
	// https://maps.google.com/?ll=37.792982,-122.392159&spn=0.016193,0.028603&t=m&z=16
	private static final float CROWDED_LAT = 37.792982f;
	private static final float CROWDED_LON = -122.392159f;

	private static final int DISTANCE = 400;

	private static NearCrowdedLocationAnalyzer instance;

	private LocationManager mManager;

	private Float currentDistance;

	public NearCrowdedLocationAnalyzer(Context context) {
		mManager = (LocationManager) context
				.getSystemService(Activity.LOCATION_SERVICE);
		mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
				this);
	}

	public static synchronized NearCrowdedLocationAnalyzer getOrCreateInstance(
			final Context context) {
		if (null == instance) {
			instance = new NearCrowdedLocationAnalyzer(context);
		}
		return instance;
	}

	public static synchronized NearCrowdedLocationAnalyzer getInstance() {
		return instance;
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		Location crowdedLocation = new Location("");
		crowdedLocation.setLatitude(CROWDED_LAT);
		crowdedLocation.setLongitude(CROWDED_LON);
		currentDistance = location.distanceTo(crowdedLocation);
	}

	@Override
	public synchronized void onProviderDisabled(String provider) {
	}

	@Override
	public synchronized void onProviderEnabled(String provider) {
	}

	@Override
	public synchronized void onStatusChanged(String provider, int status,
			Bundle extras) {
	}

	@Override
	public synchronized String getLabel() {
		final Boolean isDistant = isNominal();
		return null == isDistant ? "Crowd check..."
				: isDistant ? ("Good job Lance!\n("
						+ Math.round(currentDistance) + "m)")
						: ("The are ahead is crowded. Make a left here to take a detour.\n("
								+ Math.round(currentDistance) + "m)");
	}

	@Override
	public synchronized Boolean isNominal() {
		if (null == currentDistance) {
			return null;
		}
		Log.i(TAG, "Current distance from crowds: " + currentDistance);
		return currentDistance > DISTANCE;
	}

	@Override
	public synchronized Integer getDrawable() {
		return R.drawable.crowd_card;
	}

	public synchronized void didDisplay() {
	}

	public synchronized boolean clearBackground() {
		return true;
	}

}
