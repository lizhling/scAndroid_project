/**
 *@(#)JavaScriptWebView.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

package com.sunrise.javascript;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.function.Business;
import com.sunrise.javascript.function.Cache;
import com.sunrise.javascript.function.HardwareAndNetwork;
import com.sunrise.javascript.function.Phone;
import com.sunrise.javascript.function.Sms;
import com.sunrise.javascript.function.ZipAndBase64;
import com.sunrise.javascript.mode.BusinessInformation;
import com.sunrise.javascript.utils.ActivityUtils;
import com.sunrise.javascript.utils.CacheUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.javascript.utils.ViewUtils;

/**
 * 绑定对象到javascript上，javascript能够调用用设备上的功能，例如发送短信，卫星定位，拨打语音电话等功能。
 * 
 * @version 0.01 January 12 2012
 * @author LIU WEI
 */
public class JavaScriptWebView extends WebView {
	private long mCachEffectiveTime = -1;
	private Context mContext;
	private JavascriptHandler mJavascriptHandler;
	private Business mBusiness = Business.getInstance();

	public JavaScriptWebView(Context context) {
		super(context);
		mContext = context;
		WebSettings webSettings = getSettings();
		webSettings.setJavaScriptEnabled(true);
	}

	public JavaScriptWebView(Context context, AttributeSet attrs) {
		super(((Activity) context).getParent() != null ? ((Activity) context).getParent() : context, attrs);
		mContext = context;
		WebSettings webSettings = getSettings();
		webSettings.setJavaScriptEnabled(true);
	}

	public JavascriptHandler getJavascriptHandler() {
		return mJavascriptHandler;
	}

	@SuppressLint("NewApi")
	public void setJavascriptHandler(JavascriptHandler mJavascriptHandler) {
		this.mJavascriptHandler = mJavascriptHandler;
		addJavascriptInterface(new ZipAndBase64(mJavascriptHandler), JavaScriptConfig.ZIPANDBASE64_JAVASCRIPT_OBJECT);
		addJavascriptInterface(new ActivityUtils(mContext, mJavascriptHandler), JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT);
		mBusiness.setJavascriptHandler(mJavascriptHandler);
		addJavascriptInterface(mBusiness, JavaScriptConfig.BUSINESS_JAVASCRIPT_OBJECT);
		addJavascriptInterface(new ViewUtils(mContext, mJavascriptHandler), JavaScriptConfig.VIEW_JAVASCRIPT_OBJECT);
		addJavascriptInterface(new HardwareAndNetwork(mContext, mJavascriptHandler), JavaScriptConfig.HARDWAREANDNETWORK_OBJECT);
		addJavascriptInterface(new com.sunrise.javascript.function.Debug(), JavaScriptConfig.DEBUG_JAVASCRIPT_OBJECT);
		addJavascriptInterface(new Sms(mContext, mJavascriptHandler), JavaScriptConfig.SMS_OBJECT);
		addJavascriptInterface(new Phone(mContext), JavaScriptConfig.PHONE_OBJECT);
		addJavascriptInterface(new Cache(mContext, mJavascriptHandler), JavaScriptConfig.CACHE);
		// removeJavascriptInterface("accessibility");
		// removeJavascriptInterface("ccessibilityaversal");
		// removeJavascriptInterface("searchBoxJavaBridge_");
	}

	public void addJavascriptEvent(AddedJavascriptObject obj) {
		addJavascriptInterface(obj, obj.getName());
	}

	/**
	 * 设置缓存模式，缓存模式有效时间
	 * 
	 * @param <b>model</b> 缓存模式
	 * @param effectiveTime
	 *            缓存模式有效时间，以秒为单位
	 */
	private void setCacheMode(int mode) {
		getSettings().setCacheMode(mode);
	}

	public int getCachMode() {
		return getSettings().getCacheMode();
	}

	public long getCachModeEffectiveTime() {
		return mCachEffectiveTime;
	}

	/**
	 * 将参javascript回调函数接收的参数和和它的方法名转化为字符串
	 * 
	 * @param params
	 *            参数数组
	 * @param methodName
	 *            javascript 函数名字
	 * @return 转化后的字符串
	 */
	public static String appendParamTOjavaScript(String[] params, String methodName) {
		if (TextUtils.isEmpty(methodName)) {
			throw new IllegalArgumentException("方法名不能为空");
		}
		String pas = "";
		String result = "";
		if (params != null) {
			int size = params.length;
			int lastIndex = size - 1;
			String temp = "", temp1;
			for (int i = 0; i < size; i++) {
				temp = params[i];
				if (temp != null && temp.startsWith("{") && temp.endsWith("}") && temp.contains(":")) {
					temp1 = temp;
				} else {
					temp1 = "'" + temp + "'";
				}
				pas += temp1 + (i == lastIndex ? "" : ",");

			}
		}
		result = methodName + "(" + pas + ")";
		LogUtlis.w("js调用", "返回：" + result);
		return result;
	}

	public void loadUrlCache(String url) {
		if (CacheUtils.isCacheEffective(mContext, url)) {
			setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		} else {
			setCacheMode(WebSettings.LOAD_DEFAULT);
		}
		super.loadUrl(url);
	}

	public void setBusinessInformation(BusinessInformation businessInformation) {
		mBusiness.setBusinessInformation(businessInformation);
	}

}
