/**
  *@(#)HardwareAndNetworkUtils.java       0.01 2012/01/16
  *Copyright (c) 2012-3000 Sunrise, Inc.
  */
package com.sunrise.javascript.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.sunrise.javascript.mode.LocationBean;
import com.sunrise.javascript.utils.gps.GpsTask;
import com.sunrise.javascript.utils.gps.GpsTaskCallBack;

/**
 * 获取网络与硬件相关信息
 * @version January 12 2012
 * @author luoyun
 */
public class HardwareAndNetworkUtils {
	/*错误常量*/
	private final static String GET_LOCATION_GPS_NOT_OPEN_ERROR = "GPS is closed";
	private final static String GET_LOCATION_TIMEOUT_ERROR = "request location time out";
	private final static String GET_LOCATION_LAC_CELLID_ERROR = "No network or SIM card does not exist";
	private static GsmCellLocation mGsmCellLocation;
	/**
	 * 获取纬度信息
	 * @param handler    Handler实例
	 */
	public static void getLocationLatitude(final Context context,final Handler handler){
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			//sendLatitudeToHandler(GET_LOCATION_GPS_NOT_OPEN_ERROR,handler);
			return;
		}
	   GpsTask gpsTask=new GpsTask(context,new GpsTaskCallBack() {
		
		@Override
		public void gpsConnectedTimeOut() {
			// TODO Auto-generated method stub
			//sendLatitudeToHandler(GET_LOCATION_TIMEOUT_ERROR,handler);
		}
		
		@Override
		public void gpsConnected(Location location) {
			// TODO Auto-generated method stub
			//sendLatitudeToHandler(location.getLatitude()+"",handler);
		}
	});
	   gpsTask.execute();
	}
	
	/**
	 * 获取经度信息
	 * @param handler  Handler实例
	 */
	public static void getLocationLongitude(final Context context,final Handler handler){
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			//sendLongitudeToHandler(GET_LOCATION_GPS_NOT_OPEN_ERROR,handler);
			return;
		}
		GpsTask gpsTask=new GpsTask(context,new GpsTaskCallBack() {
			
			@Override
			public void gpsConnectedTimeOut() {
				// TODO Auto-generated method stub
				//sendLongitudeToHandler(GET_LOCATION_TIMEOUT_ERROR,handler);
			}
			
			@Override
			public void gpsConnected(Location location ) {
				// TODO Auto-generated method stub
				//sendLongitudeToHandler(location.getLongitude()+"", handler);
			}
		});
		gpsTask.execute();
	}
	
	/**
	 * 获取经度和纬度信息
	 * @param handler  Handler实例
	 */
	public static void getLatitudeAndLongitude(final Context context,final Handler handler,final String key){
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			CommonUtils.sendResult(GET_LOCATION_GPS_NOT_OPEN_ERROR,key,handler);
			return;
		}
		GpsTask gpsTask=new GpsTask(context,new GpsTaskCallBack() {
				
		@Override
		public void gpsConnectedTimeOut() {
			// TODO Auto-generated method stub
			CommonUtils.sendResult(GET_LOCATION_TIMEOUT_ERROR,key,handler);
		}
		
		@Override
		public void gpsConnected(Location location) {
			// TODO Auto-generated method stub
			String result=JsonUtils.writeObjectToJsonStr(new LocationBean(location.getLatitude(), location.getLongitude()));
			CommonUtils.sendResult(result,key,handler);
		}
	    });
		gpsTask.execute();
	}
	
	/**
	 * 获取蜂窝小区ID
	 * @param telephonyManager  TelephoneManager实例
	 * @param handler	Handler 实例
	 */
	public static void getCellID(TelephonyManager telephonyManager,Handler handler){
		if (mGsmCellLocation==null) {
			mGsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
			if (mGsmCellLocation==null) {
				//sendCellIDToHandler(GET_LOCATION_LAC_CELLID_ERROR, handler);
				return;
			}
		}
		String cellID = String.valueOf(mGsmCellLocation.getCid());
		//sendCellIDToHandler(cellID, handler);
	}
	
	/**
	 * 获取位置参数LAC
	 * @param telephonyManager  TelephoneManager实例
	 * @param handler  Handler 实例
	 */
	public static void getLAC(TelephonyManager telephonyManager,Handler handler){
		if (mGsmCellLocation==null) {
			mGsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
			if (mGsmCellLocation==null) {
				//sendLACToHandler(GET_LOCATION_LAC_CELLID_ERROR, handler);
				return;
			}
		}
		String locationLAC = String.valueOf(mGsmCellLocation.getLac());
		//sendLACToHandler(locationLAC, handler);
	}
}
