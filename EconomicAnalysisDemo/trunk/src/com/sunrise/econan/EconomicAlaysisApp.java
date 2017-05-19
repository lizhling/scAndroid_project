package com.sunrise.econan;

import com.sunrise.econan.cache.download.DownFileManager;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;

public class EconomicAlaysisApp extends Application {

	public static int sAppCode;
	public static String sAppName;
	public static DownFileManager sDownFileManager;
	
	public void onCreate() {
		super.onCreate();
		
		sDownFileManager = DownFileManager.getInstance();
		
		init();
	}

	private void init() {
		try {
			sAppCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			sAppName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

}
