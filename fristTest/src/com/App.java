package com;


import java.util.HashMap;

import com.baidu.mapapi.SDKInitializer;
import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.util.AsyncImageLoader;

//import cn.jpush.android.api.JPushInterface;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class App extends Application {

	public static Context sContext;
	public static int sAppCode;
	public static String sAppVersionName;

	public static final String PREFS_NAME = "JPUSH_EXAMPLE";
	public static final String PREFS_DAYS = "JPUSH_EXAMPLE_DAYS";
	public static final String PREFS_START_TIME = "PREFS_START_TIME";
	public static final String PREFS_END_TIME = "PREFS_END_TIME";
	public static final String KEY_APP_KEY = "JPUSH_APPKEY";

	private static App mInstance = null;
	public static boolean isTest;
	private static AsyncImageLoader imageLoader2;
	/**
	 * 百度地图api KEY
	 */
	public static String sBaiduApiKey;

	public void onCreate() {
		super.onCreate();
		sContext = getApplicationContext();
		mInstance = this;
		imageLoader2 = new AsyncImageLoader(this);
		init();

		FileUtils.createSDDir(ExtraKeyConstant.APP_FILE_HTML_DATA_DIR);

		JavaScriptConfig.setCaptureImageDir(ExtraKeyConstant.APP_CHENNEL_IMAGE_DIR);
	}

	public static App getInstance() {
		return mInstance;
	}

	private void init() {

		/** 创建外部存储文件夹 */
		FileUtils.createSDDir(ExtraKeyConstant.APP_FILE_APK_NAME);

		/** 极光推送初始化 */
		// JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		// JPushInterface.init(this); // 初始化 JPush

		/** 百度地图初始化 */
		SDKInitializer.initialize(getApplicationContext());

		/** 初始化应用信息 */
		initApplicationName();

		// LogUtlis.showLog = isTest;
	}

	private void initApplicationName() {

		try {
			sAppCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			sAppVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		try {
			isTest = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.getBoolean("isTest");
			sBaiduApiKey = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData
					.getString("com.baidu.lbsapi.API_KEY");

			boolean logShow = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.getBoolean("logShow");
			LogUtlis.showLog = logShow;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String[] keys = { "isTest" };
		// HashMap<String, String> hash = getAppMetaData(sContext, keys);
		// if (hash != null)
		// if (hash.containsKey("isTest"))
		// isTest = Boolean.valueOf(hash.get(keys[0]));

	}

	private HashMap<String, String> getAppMetaData(Context context, String[] keys) {
		if (keys == null || keys.length == 0)
			return null;

		Bundle metaData = null;
		HashMap<String, String> appMetaDatasHash = new HashMap<String, String>();
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai)
				metaData = ai.metaData;
			if (null != metaData)
				for (String key : keys) {
					String value = metaData.getString(key);
					appMetaDatasHash.put(key, value);
				}
		} catch (NameNotFoundException e) {
			return null;
		}
		return appMetaDatasHash;
	}

	public static AsyncImageLoader getImageLoader() {
		return imageLoader2;
	}

}
