package com.sunrise.javascript.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.sunrise.javascript.JavascriptHandler;

public class CommonUtils {
	public static void sendResult(String result, String key, Handler handler) {
		String params[] = { result, key };
		Message message = handler.obtainMessage(JavascriptHandler.JAVASCRIPT_API_CALL_BACK, params);
		message.sendToTarget();
	}

	public static boolean isApkInstalled(Context context, String string) {
		PackageManager pm = context.getPackageManager();// 通过这个application得到PackageManager
		// 找在launch中注册了的应用 pm.getInstalledApplications 可以返回所有的列表
		Intent intent = pm.getLaunchIntentForPackage(string);
		if (intent == null) {// 这个intent如果为空，那就是代表没有找到指定包名的程序。
			return false;
		}
		return true;
	}
	public static boolean isMobilePhone(String number) {
		return number.matches("^((13[4-9])|(147)|(15[^4,^5,\\D])|(178)|(18[2-4,7-8]))\\d{8}$");
	}
	
	public static boolean isPhoneNumber(String number) {
		return number.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
	}
	/**
	 * 判断是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		Pattern p = Pattern.compile("^[0-9]*$");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 去除电话号码中不必要的字符
	 * 
	 * @param number
	 * @return
	 */
	public static String removeNoNecessaryWordsFromPhoneNumber(String str){
		Pattern p = Pattern.compile("[^0-9]");
		Matcher m = p.matcher(str.replace("+86", ""));
		return m.replaceAll("");
	}
}
