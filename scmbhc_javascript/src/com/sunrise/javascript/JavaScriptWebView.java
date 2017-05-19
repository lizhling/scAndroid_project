/**
 *@(#)JavaScriptWebView.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

package com.sunrise.javascript;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.function.ImageChooserForWeb;
import com.sunrise.javascript.function.Phone;
import com.sunrise.javascript.function.SccmccControlWebScreen;
import com.sunrise.javascript.function.SccmccInfo;
import com.sunrise.javascript.mode.UserInfo;

/**
 * 
 * @version 0.01 January 12 2012
 * @author LIU WEI
 */
@SuppressLint("SetJavaScriptEnabled")
public class JavaScriptWebView extends WebView implements JavaScriptInterface {

	private ArrayList<JavaScriptInterface> mArrayJavascriptInterface;

	public JavaScriptWebView(Context context) {
		super(context);

	}

	public JavaScriptWebView(Context context, AttributeSet attrs) {
		super(((Activity) context).getParent() != null ? ((Activity) context).getParent() : context, attrs);
	}

	public void setJavascriptHandler(Context context, JavascriptHandler javascriptHandler) {
		WebSettings webSettings = getSettings();
		webSettings.setAllowFileAccess(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		addJavascriptInterface(new SccmccInfo(context, javascriptHandler), JavaScriptConfig.JAVASCRIPT_INFO_OBJECT);
		addJavascriptInterface(new SccmccControlWebScreen(context, javascriptHandler), JavaScriptConfig.JAVASCRIPT_CONTROL_WEB_SCREEN_OBJECT);
		addJavascriptInterface(new ImageChooserForWeb(context, javascriptHandler), JavaScriptConfig.JAVASCRIPT_CHOOSE_SD_IMAGE);// 图片资源获取
		addJavascriptInterface(new Phone(context, javascriptHandler), JavaScriptConfig.JAVASCRIPT_PHONE_FUNCTION);// 手机功能
	}

	public void setUseInfo(UserInfo userInfo) {
		SccmccInfo.setUserInfo(userInfo);
	}

	public void addJavascriptInterface(JavaScriptInterface obj, String interfaceName) {
		if(mArrayJavascriptInterface==null)
			mArrayJavascriptInterface = new ArrayList<JavaScriptInterface>();
		mArrayJavascriptInterface.add(obj);
		super.addJavascriptInterface(obj, interfaceName);
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		for (JavaScriptInterface itfc : mArrayJavascriptInterface)
			if (itfc.onActivityResult(requestCode, resultCode, data))
				return true;
		return false;
	}
}
