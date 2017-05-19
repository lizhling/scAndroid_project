package com.sunrise.javascript.function;

/**
 *@(#)JavasscriptUtils.java        0.01 2012/11/17
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavaScriptInterface;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.mode.DeviceInfo;
import com.sunrise.javascript.mode.UserInfo;
import com.sunrise.javascript.utils.LogUtlis;

/**
 * @version 0.01 September 21 2012
 * @author LIU WEI
 */

public class SccmccInfo implements JavaScriptInterface {

	private final static String TAG = "Info";
	private Context mContext;
	private JavascriptHandler mJavascriptHandler;
	private static UserInfo sUserInfo;
	public static final int GO_LOGIN_REQUEST_CODE = 0x7fff;
	public static final String SCMBNC_LOGIN_ACTION = "com.starcpt.LOGIN";
	public static final String TEST_LOGIN_ACTION = "com.starcpt.scmbhc.LOGIN";

	public SccmccInfo(Context mContext, JavascriptHandler mJavascriptHandler) {
		super();
		this.mContext = mContext;
		this.mJavascriptHandler = mJavascriptHandler;
	}

	public void getUserInfo() {
		LogUtlis.d(TAG, "getUserInfo is called");
		if (sUserInfo.getLoginPhoneNumber() != null) {
			mJavascriptHandler.sendObject(JavaScriptConfig.GET_USER_INFO_FUNCTION_CALL_BACK_NAME, sUserInfo);
		} else {
			try {
				Intent intent = new Intent();
				intent.setAction(TEST_LOGIN_ACTION);
				((Activity) mContext).startActivityForResult(intent, GO_LOGIN_REQUEST_CODE);
			} catch (ActivityNotFoundException e) {
				throw new IllegalArgumentException("指定的操作不存在");
			}
		}

	}

	public void getDeviceInfo() {
		DisplayMetrics displaymetrics = mContext.getResources().getDisplayMetrics();
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setScreenWidth(displaymetrics.widthPixels);
		deviceInfo.setScreenHeight(displaymetrics.heightPixels);
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		deviceInfo.setIMEI(tm.getDeviceId());
		mJavascriptHandler.sendObject(JavaScriptConfig.GET_DEVICE_INFO_FUNCTION_CALL_BACK_NAME, deviceInfo);
	}

	public static void setUserInfo(UserInfo sUserInfo) {
		SccmccInfo.sUserInfo = sUserInfo;
	}

	public void setGuideBarVisiblity(boolean isvisibility) {
		if (isvisibility) {
			mContext.sendBroadcast(new Intent("com.scmbhc.OPEN_TABHOST_ACTION"));
		} else {
			mContext.sendBroadcast(new Intent("com.scmbhc.CLOSE_TABHOST_ACTION"));
		}
	}

	public int getAppVersionCode() {
		int verCode = 0;
		try {
			verCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	public void startActivity(String action) {
		try {
			Intent intent = new Intent();
			intent.setAction(action);
			((Activity) mContext).startActivityForResult(intent, GO_LOGIN_REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			throw new IllegalArgumentException("指定的操作不存在");
		}
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == GO_LOGIN_REQUEST_CODE) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				sUserInfo.setLoginPhoneNumber(data.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
				sUserInfo.setToken(data.getStringExtra(Intent.EXTRA_REMOTE_INTENT_TOKEN));
				sUserInfo.setResultCode(UserInfo.LOGIN_CODE);
				sUserInfo.setResultMessage(UserInfo.LOGIN_STR);
				break;
			default:
				sUserInfo.setLoginPhoneNumber(null);
				sUserInfo.setResultCode(UserInfo.UN_LOGIN_CODE);
				sUserInfo.setResultMessage(UserInfo.UN_LOGIN_STR);
				break;
			}
			mJavascriptHandler.sendObject(JavaScriptConfig.GET_USER_INFO_FUNCTION_CALL_BACK_NAME, sUserInfo);
			return true;
		}

		return false;
	}
}
