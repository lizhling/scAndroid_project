package com.starcpt.cmuc.utils;
import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneInfoUtils {
	
	/** 手机IMEI */
	public static final String TELEPHONE_INFO_IMEI_KEY = "imei";
	/** 手机号码 */
	public static final String TELEPHONE_INFO_NUMBER_KEY = "number";
	/** 手机型号 */
	public static final String TELEPHONE_INFO_TYPE_KEY = "type";
	/** 手机IMSI */
	public static final String TELEPHONE_INFO_IMSI_KEY = "imsi";

/*	public static Map<String, String> getMyPhoneInfo(Context context) {
		Map<String, String> phoneInfo = new HashMap<String, String>();
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		phoneInfo.put(TELEPHONE_INFO_IMEI_KEY, tm.getDeviceId());
		phoneInfo.put(TELEPHONE_INFO_IMSI_KEY, tm.getSubscriberId());
		phoneInfo.put(TELEPHONE_INFO_NUMBER_KEY, tm.getLine1Number());
		phoneInfo.put(TELEPHONE_INFO_TYPE_KEY, android.os.Build.MODEL);
		return phoneInfo;
	}*/
	
	public static String getPhoneIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);		
		return tm.getDeviceId();
	}
	
	
	public static String getPhoneIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}
	
	public static String getPhoneNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
	
}
