package com.sunrise.javascript.utils.gps;

import android.location.Location;


public interface GpsTaskCallBack {
	public void gpsConnected(Location location);	
	public void gpsConnectedTimeOut();
}
