package com.sunrise.econan.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class HardwareUtils {

	/**
	 * @param context
	 * @return 获取mac地址
	 */
	public static String getMacAddress(Context context) {
		String result = "";
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		result = wifiInfo.getMacAddress();
		LogUtlis.d("getMacAddress", "macAdd:" + result);
		return result;

	}

	public static String getAllApp(Context context) {
		String result = "";
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
		for (PackageInfo i : packages) {
			if ((i.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				result += i.applicationInfo.loadLabel(context.getPackageManager()).toString() + ",";
			}
		}
		return result.substring(0, result.length() - 1);
	}

	/**
	 * @param context
	 * @return 获取经纬度
	 */
	public static String getLocation(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location location = null;
		for (String provider : locationManager.getAllProviders()) {
			location = locationManager.getLastKnownLocation(provider);
			if (location != null)
				break;

		}

		if (location == null)
			return null;

		double latitude = location.getLatitude(); // 经度
		double longitude = location.getLongitude(); // 纬度
		double altitude = location.getAltitude(); // 海拔
		LogUtlis.d("tag", "latitude " + latitude + "  longitude:" + longitude + " altitude:" + altitude);
		return "latitude=" + latitude + "&longitude=" + longitude + "&altitude=" + altitude;
	}

	/**
	 * @param context
	 * @param map
	 * @return 获取经纬度
	 */
	public static HashMap<String, String> getLocation(Context context, HashMap<String, String> map) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (location == null)
			return map;

		double latitude = location.getLatitude(); // 经度
		double longitude = location.getLongitude(); // 纬度
		double altitude = location.getAltitude(); // 海拔

		if (map == null)
			map = new HashMap<String, String>();

		map.put("latitude", String.valueOf(latitude));
		map.put("longitude", String.valueOf(longitude));
		map.put("altitude", String.valueOf(altitude));
		return map;
	}

	/**
	 * 获取位置参数LAC
	 * 
	 * @param telephonyManager
	 *            TelephoneManager实例
	 * @param context
	 * @param handler
	 *            Handler 实例
	 */
	public static String getLAC(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation mGsmCellLocation = (GsmCellLocation) tm.getCellLocation();
		if (mGsmCellLocation == null) {
			return null;
		}
		return String.valueOf(mGsmCellLocation.getLac());
	}

	public static String getModle() {
		return "MODEL=" + android.os.Build.MODEL;
	}

	/**
	 * @return androidSdk版本
	 */
	public static String getAndroidSDKVersionRelease() {
		return "SDK_VERSION_RELEASE=" + android.os.Build.VERSION.RELEASE;
	}

	/**
	 * @return ip地址
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {

						byte[] b = inetAddress.getAddress();
						StringBuilder sb = new StringBuilder(b.length);
						for (byte a : b) {
							sb.append(String.valueOf(a));
						}

						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 功能: 获取手机IMEI值
	 * 
	 * @param mContext
	 *            上下文
	 * @return 获取失败返回null，获取成功返回imei字符串 (GSM 返回IMEI值 ESN或CDMA返回 MEID值)
	 */
	public static String getPhoneIMEI(Context mContext) {
		// 获取IMEI
		String imei = null;
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		imei = tm.getDeviceId();
		return imei;
	}

	public static String getPhoneIMSI(Context mContext) {
		// 获取IMSI
		String imsi = null;
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		imsi = tm.getSubscriberId();
		return imsi;
	}

	/**
	 * 功能: 获取手机卡的ICCID (sim卡集成电路序列号)
	 * 
	 * @param mContext
	 *            上下文
	 * @return 获取失败返回null，获取成功返回sim卡的ICCID字符串
	 */
	public static String getICCIDNO(Context mContext) {
		String ICCID = null;
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		ICCID = tm.getSimSerialNumber(); // sim卡集成电路标识ICCID
		return ICCID;
	}

	/**
	 * 获取手机网络名称
	 * 
	 * @param context
	 * @return
	 */
	/**
	 * @param context
	 * @return 获取网络类型
	 */
	public static String getNetWorkType(Context context) {

		ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
		// System.out.println("获取详细状态:" + networkInfo.getDetailedState());
		// System.out.println("获取附加信息" + networkInfo.getExtraInfo());
		// System.out.println("获取连接失败的原因:" + networkInfo.getReason());
		// System.out.println("获取网络类型(一般为移动或Wi-Fi):" + networkInfo.getType());
		// System.out.println("获取网络类型名称(一般取值“WIFI”或“MOBILE”):" +
		// networkInfo.getTypeName());
		// System.out.println("判断该网络是否可用:" + networkInfo.isAvailable());
		// System.out.println("判断是否已经连接:" + networkInfo.isConnected());
		// System.out.println("判断是否已经连接或正在连接:" +
		// networkInfo.isConnectedOrConnecting());
		// System.out.println("判断是否连接失败:" + networkInfo.isFailover());
		// System.out.println("判断是否漫游:" + networkInfo.isRoaming());

		if (networkInfo != null && networkInfo.getType() == 1)
			return networkInfo.getTypeName();
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		int netType = telephonyManager.getNetworkType();
		netType = Math.max(0, netType);

		if (netType < NETWORK_TYPE.length)
			return NETWORK_TYPE[telephonyManager.getNetworkType()];
		else
			return NETWORK_TYPE[0];
	}

	private static final String[] NETWORK_TYPE = { "UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO_0", "EVDO_A", "1xRTT", "HSDPA", "HSUPA", "HSPA", "IDEN",
			"EVDO_B", "LTE", "EHRPD", "HSPAP" };

	public static void loadSignalStrength(final Context context, final Observer observer) {

		final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		PhoneStateListener sPhoneStateListener = new PhoneStateListener() {
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {

				int strength = 0;
				if (signalStrength.isGsm())
					strength = -113 + 2 * signalStrength.getGsmSignalStrength();
				else {
					strength = -113 + 2 * signalStrength.getCdmaDbm();
				}

				if (observer != null)
					observer.update(null, strength + "dBm");

				telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
			}
		};

		telephonyManager.listen(sPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	public static String getPhoneIMSI2(Context context) {
		String imsi2 = null;
		boolean isException = false;
		try {
			imsi2 = getPhoneIMSI2_QualcommDouble(context);
		} catch (Exception e) {
			isException = true;
		}

		if (isException)
			try {
				isException = false;
				imsi2 = getPhoneIMSI2_MtkDouble(context);
			} catch (Exception e2) {
				isException = true;
			}

		if (isException)
			try {
				isException = false;
				imsi2 = getPhoneIMSI2_MtkSecondDouble(context);
			} catch (Exception e3) {
				e3.printStackTrace();
				isException = true;
			}

		if (isException)
			try {
				isException = false;
				imsi2 = getPhoneIMSI2_SpreadDouble(context);
			} catch (Exception e) {
				e.printStackTrace();
				isException = true;
			}

		return imsi2;
	}

	/**
	 * @param context
	 * @return 获取高通手机的第二张卡信息
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static String getPhoneIMSI2_QualcommDouble(Context context) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		Class<?> cx = Class.forName("android.telephony.MSimTelephonyManager");
		Object obj = context.getSystemService("phone_msim");
		Integer simId_2 = 1;
		Method ms = cx.getMethod("getSubscriberId", int.class);
		String imsi_2 = (String) ms.invoke(obj, simId_2);
		return imsi_2;
	}

	private static String getPhoneIMSI2_MtkDouble(Context context) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		Class<?> c = Class.forName("com.android.internal.telephony.Phone");
		Field fields2 = c.getField("GEMINI_SIM_2");
		fields2.setAccessible(true);
		Integer simId_2 = (Integer) fields2.get(null);
		Method m = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", int.class);
		String imsi_2 = (String) m.invoke(tm, simId_2);
		return imsi_2;
	}

	private static String getPhoneIMSI2_MtkSecondDouble(Context context) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		Class<?> c = Class.forName("com.android.internal.telephony.Phone");
		Field fields2 = c.getField("GEMINI_SIM_2");
		fields2.setAccessible(true);
		Integer simId_2 = (Integer) fields2.get(null);

		Method mx = TelephonyManager.class.getMethod("getDefault", int.class);
		TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);
		String imsi2 = tm2.getSubscriberId();
		return imsi2;
	}

	private static String getPhoneIMSI2_SpreadDouble(Context context) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Class<?> c = Class.forName("com.android.internal.telephony.PhoneFactory");
		Method m = c.getMethod("getServiceName", String.class, int.class);
		String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
		TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);
		String imsi2 = tm1.getSubscriberId();
		return imsi2;
	}

	public static boolean isOpenNetwork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	public static int getLCDwidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getLCDHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}
}
