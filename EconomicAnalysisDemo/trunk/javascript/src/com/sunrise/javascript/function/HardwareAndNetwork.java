/**
 *@(#)HardwareAndNetwork.java       0.01 2012/01/16
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript.function;

import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
//import com.baidu.lbsapi.BMapManager;
//import com.baidu.lbsapi.MKGeneralListener;
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.mode.LocationBean;
import com.sunrise.javascript.utils.CommonUtils;
import com.sunrise.javascript.utils.HardwareAndNetworkUtils;
import com.sunrise.javascript.utils.JsonUtils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
/**
 * 获取硬件网络相关信息,实例被绑定到javascript上。
 * 
 * @version January 12 2012
 * @author luoyun
 */
public class HardwareAndNetwork {
	private TelephonyManager mTelephonyManager;
	private Context mContext;
	private Handler mHandler;
	private String key;
	// private static BMapManager mBMapMan = null;
	private final static long GPS_TIME_DURATION = 60 * 1000;
	private final static String GET_LOCATION_TIMEOUT_ERROR = "request location time out";
	private final static String NETWORK_ERROR = "please open network";
	private final static String GPS_CLOSED_ERROR = "gps is closed";
	private boolean timeOut = false;

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	// class MyGeneralListener implements MKGeneralListener {
	//
	// @Override
	// public void onGetPermissionState(int iError) {
	// if (iError != 0) {
	// // 授权Key错误：
	// Toast.makeText(mContext,
	// "请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError,
	// Toast.LENGTH_LONG).show();
	//
	// LocationBean locationBean = new LocationBean();
	// locationBean.setMessage(NETWORK_ERROR);
	// String result = JsonUtils.writeObjectToJsonStr(locationBean);
	// CommonUtils.sendResult(result, key, mHandler);
	// timeOut = true;
	// } else {
	// Toast.makeText(mContext, "key认证成功", Toast.LENGTH_LONG).show();
	// }
	// }
	//
	// }

	public HardwareAndNetwork(Context context, Handler handler) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mHandler = handler;
		mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		// if (mBMapMan == null) {
		// mBMapMan = new BMapManager(context.getApplicationContext());
		// mBMapMan.init(new MyGeneralListener());
		// }
	}

	private void registLocationListener() {
		final LocationManagerProxy aMapLocManager = LocationManagerProxy.getInstance(mContext);
		aMapLocManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, new AMapLocationListener() {
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
			public void onProviderEnabled(String arg0) {}
			public void onProviderDisabled(String arg0) {}
			public void onLocationChanged(Location arg0) {}
			public void onLocationChanged(AMapLocation arg0) {
				String result = JsonUtils.writeObjectToJsonStr(new LocationBean(arg0.getLongitude(), arg0.getLatitude()));
				CommonUtils.sendResult(result, key, mHandler);
				if (aMapLocManager != null) {
					aMapLocManager.removeUpdates(this);
					aMapLocManager.destory();
				}
				timeOut = true;
			}
		});
		aMapLocManager.setGpsEnable(false);
	}

	// private LocationClient mLocClient;
	// private void registLocationListener() {
	// mLocClient = new LocationClient(mContext);
	// mLocClient.registerLocationListener(new BDLocationListener() {
	//
	// @Override
	// public void onReceiveLocation(BDLocation location) {
	// if (location == null)
	// return;
	//
	// String result = JsonUtils.writeObjectToJsonStr(new
	// LocationBean(location.getLongitude(), location.getLatitude()));
	// CommonUtils.sendResult(result, key, mHandler);
	// mLocClient.unRegisterLocationListener(this);
	// mLocClient.registerNotifyLocationListener(null);// fh add
	// mLocClient = null;
	// timeOut = true;
	// }
	// });
	// }

	/**
	 * 获取手机制造商
	 */
	public void getPhoneManufacturer() {
		String phoneManufacturer = Build.MANUFACTURER;
		Log.d(JavaScriptConfig.TAG, "获取手机制造商：" + phoneManufacturer);
		String params[] = { phoneManufacturer };
	}

	/**
	 * 获取手机机型信息
	 */
	public void getPhoneModel() {
		String phoneModel = Build.MODEL;
		// Log.d(JavaScriptConfig.TAG, "获取手机型号为："+phoneModel);
		String params[] = { phoneModel };
	}

	/**
	 * 获取手机操作系统
	 */
	public void getPhoneOS() {
		String phoneOs = Build.VERSION.RELEASE;
		// Log.d(JavaScriptConfig.TAG, "获取手机操作系统版本信息:"+phoneOs);
		String params[] = { phoneOs };
	}

	/**
	 * 获取手机里面SIM卡的imsi号
	 */
	public void getPhoneIMS() {
		String phoneIMS = mTelephonyManager.getSubscriberId();
		// Log.d(JavaScriptConfig.TAG, "获取SIM卡IMSI号："+phoneIMS);
		String params[] = { phoneIMS };
	}

	/**
	 * 获取手机的唯一标识IMEI号
	 */
	public void getPhoneIMEI() {
		String phoneIMEI = mTelephonyManager.getDeviceId();
		// Log.d(JavaScriptConfig.TAG, "获取手机的IMEI号："+phoneIMEI);
		String params[] = { phoneIMEI };
	}

	/**
	 * 获取蜂窝小区ID
	 */
	public void getLocationCellID() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HardwareAndNetworkUtils.getCellID(mTelephonyManager, mHandler);
			}
		}).start();
	}

	/**
	 * 获取位置参数LAC
	 */
	public void getLocationLAC() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HardwareAndNetworkUtils.getLAC(mTelephonyManager, mHandler);
			}
		}).start();
	}

	/**
	 * 获取GPS定位的WGS84纬度数据
	 */
	public void getLatitude() {
		HardwareAndNetworkUtils.getLocationLatitude(mContext, mHandler);
	}

	/**
	 * 获取GPS定位的WGS84经度数据
	 */
	public void getLongitude() {
		HardwareAndNetworkUtils.getLocationLongitude(mContext, mHandler);
	}

	/**
	 * 获取GPS定位的WGS84经度和纬度数据
	 */
	public void getLatitudeAndLongitude(final String javascriptKey) {
		final LocationBean locationBean = new LocationBean();
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationBean.setMessage(GPS_CLOSED_ERROR);
			String result = JsonUtils.writeObjectToJsonStr(locationBean);
			CommonUtils.sendResult(result, javascriptKey, mHandler);
			timeOut = true;
			return;
		}
		timeOut = false;
		this.key = javascriptKey;
		registLocationListener();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!timeOut) {
					locationBean.setMessage(GET_LOCATION_TIMEOUT_ERROR);
					String result = JsonUtils.writeObjectToJsonStr(locationBean);
					CommonUtils.sendResult(result, javascriptKey, mHandler);
					timeOut = true;
				}
			}
		}, GPS_TIME_DURATION);
		// HardwareAndNetworkUtils.getLatitudeAndLongitude(mContext,mHandler,key);
	}

}
