package com.sunrise.javascript.utils.gps;



import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GpsTask extends AsyncTask<Object, Object, Object> {

	private GpsTaskCallBack callBk = null;
	private Context context = null;
	private LocationManager locationManager = null;
	private boolean timeOut = false;
	private boolean betterLocationFound = false;
	private long TIME_DURATION = 30*1000;
	private GpsHandler handler = null;
	private static String TAG="GpsTask";
	private Location currentLocation;
	private static final int CHECK_INTERVAL = 1000 * 30;
	private LocationListener gpsListener=null;
	private LocationListener networkListner=null;
	private class GpsHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(callBk == null)
				return;
			switch (msg.what) {
			case 0:
				callBk.gpsConnected((Location)msg.obj);
				break;
			case 1:
				callBk.gpsConnectedTimeOut();
				break;
			}
		}
		
	}

	private void registerLocationListener(){
		networkListner=new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5*1000, 0, networkListner);
		gpsListener=new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5*1000, 0, gpsListener);
	}
	
	private void unRegisterLocationListener(){
		if(gpsListener!=null){
			locationManager.removeUpdates(gpsListener);
			gpsListener=null;
		}	
		if(networkListner!=null){
			locationManager.removeUpdates(networkListner);
			networkListner=null;
		}
	}
	
	public GpsTask(Context mContext, GpsTaskCallBack callBk) {
		this.callBk = callBk;
		this.context = mContext;
		gpsInit();
	}

	public GpsTask(Activity context, GpsTaskCallBack callBk, long time_out) {
		this.callBk = callBk;
		this.context = context;
		this.TIME_DURATION = time_out;
		gpsInit();
	}

	private void gpsInit() {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		handler = new GpsHandler();
		registerLocationListener();
	}

	@Override
	protected Object doInBackground(Object... params) {
		while (!timeOut && !betterLocationFound) {
			currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Log.d(TAG, "get last location");
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				timeOut = true;
			}
		}, TIME_DURATION);
	}
	
	@Override
	protected void onPostExecute(Object result) {
			//locationManager.removeUpdates(locationListener);
			unRegisterLocationListener();
			// 获取超时
		if (timeOut && callBk != null)
			if(currentLocation!=null){
			getLocationOver();
			}else{
			handler.sendEmptyMessage(1);
			}
		super.onPostExecute(result);
	}
	
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
 
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
		boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
		boolean isNewer = timeDelta > 0;
 
		// If it's been more than two minutes since the current location,
		// use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must
			// be worse
		} else if (isSignificantlyOlder) {
			return false;
		}
 
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
 
		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());
 
		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}
 
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
	
	private void getLocationOver(){
		Message msg = handler.obtainMessage();
		msg.what = 0;
		msg.obj = currentLocation;
		handler.sendMessage(msg);
	}
	
	private class MyLocationListner implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the location provider.
			Log.d(TAG, "Got New Location of provider:"+location.getProvider());
			if(currentLocation!=null){
				if(isBetterLocation(location, currentLocation)){
					Log.d(TAG, "It's a better location");
					currentLocation=location;
					getLocationOver();
					betterLocationFound = true;
				}
				else{
					Log.d(TAG, "Not very good!");
				}
			}
			else{
				Log.d(TAG, "It's first location");
				currentLocation=location;
			}
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	 
		public void onProviderEnabled(String provider) {
		}
	 
		public void onProviderDisabled(String provider) {
		}
	};
	
}
