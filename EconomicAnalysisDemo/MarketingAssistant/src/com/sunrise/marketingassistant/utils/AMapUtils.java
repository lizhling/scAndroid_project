package com.sunrise.marketingassistant.utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class AMapUtils {
	private Context mContext;

	public AMapUtils(Context context) {
		mContext = context;
	}

	public void getAMapLocation() {
		final LocationManagerProxy aMapLocManager = LocationManagerProxy.getInstance(mContext);
		aMapLocManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, new AMapLocationListener() {
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onProviderDisabled(String arg0) {
			}

			public void onLocationChanged(Location arg0) {
			}

			public void onLocationChanged(AMapLocation arg0) {
				Log.e("高德地图坐标", "高德坐标：" + arg0.getLongitude() + " , " + arg0.getLatitude());
				if (aMapLocManager != null) {
					aMapLocManager.setGpsEnable(false);
					aMapLocManager.removeUpdates(this);
					aMapLocManager.destory();
				}
			}
		});
		aMapLocManager.setGpsEnable(true);
	}
}
