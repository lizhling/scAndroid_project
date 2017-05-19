package com.sunrise.javascript.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
//import android.widget.Toast;

import com.sunrise.javascript.utils.CommonUtils;

public class Cache {

	private Context mContext;

	private Handler mHandler;

	private static final String CACHE_SETTING_FILE_NAME = "_cache";

	public Cache(Context context, Handler javascriptHandler) {
		mContext = context;
		mHandler = javascriptHandler;
	}

	public void setCacheValue(String tab, String value, String key) {
		// Toast.makeText(mContext, "存储信息为（tab=" + tab + ",value=" + value +
		// "） ", Toast.LENGTH_SHORT).show();
		SharedPreferences preferences = mContext.getSharedPreferences(CACHE_SETTING_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(tab, value);
		editor.commit();
	}

	public void getCacheValue(String tab, String key) {
		SharedPreferences preferences = mContext.getSharedPreferences(CACHE_SETTING_FILE_NAME, Context.MODE_PRIVATE);
		String value = preferences.getString(tab, "");
		// Toast.makeText(mContext, "读取信息为（tab=" + tab + ",value=" + value +
		// "） ", Toast.LENGTH_SHORT).show();
		CommonUtils.sendResult(value, key, mHandler);
	}
	
	public void clean(){
		SharedPreferences preferences = mContext.getSharedPreferences(CACHE_SETTING_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
}
