package com.sunrise.micromarketing;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.micromarketing.cache.download.DownFileManager;
import com.sunrise.micromarketing.cache.preferences.Preferences;
import com.sunrise.micromarketing.database.ScmbhcDbManager;
import com.sunrise.micromarketing.entity.BusinessMenu;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class App extends Application {

	public static int sAppCode;
	public static String sAppName;

	/**
	 * 测试状态
	 */
	public static boolean isTest;

	public static DownFileManager sDownFileManager;
	public static Context sContext;

	public void onCreate() {
		super.onCreate();

		sContext = getApplicationContext();
		Preferences.getInstance(sContext);

		init();

		sDownFileManager = DownFileManager.getInstance();

		if (isTest) {
			initAllMenusFromLocal();
		}

	}

	private void init() {
		try {
			sAppCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			sAppName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

			// String appName = (String) getPackageManager().getPackageInfo(
			// getPackageName(),
			// PackageManager.GET_CONFIGURATIONS).applicationInfo
			// .loadLabel(getPackageManager());// 应用名称
			Bundle appMetaData = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;// 是否测试
			if (appMetaData != null) {
				isTest = appMetaData.getBoolean("isTest");
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initAllMenusFromLocal() {
		ScmbhcDbManager scmbhcDbManager = ScmbhcDbManager.getInstance(getContentResolver());
		int count = scmbhcDbManager.getBusinessMenuCount();
		if (count > 0 )
			return;

		String jsonmenu = FileUtils.getTextFromAssets(this, "menus.json", "utf-8");
		ArrayList<BusinessMenu> arrBusiness = new ArrayList<BusinessMenu>();
		try {
			JSONArray jsonarray = new JSONObject(jsonmenu).getJSONArray("datas");
			for (int i = 0; i < jsonarray.length(); ++i)
				arrBusiness.add(JsonUtils.parseJsonStrToObject(jsonarray.getString(i), BusinessMenu.class));
			scmbhcDbManager.deleteAllBusinessMenu();
			scmbhcDbManager.recordBusinessMenuList(arrBusiness);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
