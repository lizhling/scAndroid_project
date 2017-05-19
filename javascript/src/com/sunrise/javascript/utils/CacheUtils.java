package com.sunrise.javascript.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CacheUtils {

	public static final String CACHE_SETTING_FILE_NAME = "cache_file";
	/**
	 * 判断url是否在缓存时间内
	 * @param context
	 * @param url  url地址
	 * @return 如果在缓存期内返货true，否则返回false
	 */
	public static boolean isCacheEffective(Context context,String url){
		SharedPreferences preferences = context.getSharedPreferences(CACHE_SETTING_FILE_NAME, Context.MODE_PRIVATE);
		long effectiveDate = preferences.getLong(url, 0);
		if (effectiveDate>System.currentTimeMillis()) {
			return true;
		}
		Editor editor = preferences.edit();
		editor.remove(url);
		editor.commit();
		return false;
	}
	/**
	 * 设置url缓存属性
	 * @param context
	 * @param url  url地址
	 * @param effectiveTime  有效时间
	 */
	public static void addCacheUrl(Context context,String url,long effectiveTime){
		SharedPreferences preferences = context.getSharedPreferences(CACHE_SETTING_FILE_NAME, Context.MODE_PRIVATE);
		long eTime = 0;
		try {
			eTime = effectiveTime*1000;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		long effectiveDate = System.currentTimeMillis() + eTime;
		Editor editor = preferences.edit();
		editor.putLong(url, effectiveDate);
		editor.commit();
	}
}
