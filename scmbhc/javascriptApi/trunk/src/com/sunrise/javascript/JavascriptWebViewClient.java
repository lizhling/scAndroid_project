/**
 *@(#)JavascriptWebViewClient.java  0.01 2012/02/2
 * Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 
 * 主要帮助WebView处理各种通知、请求事件,能够处理javascript 对设备的功能请求。 代码片段： JavascriptWebViewClient
 * javascriptWebViewClient=new JavascriptWebViewClient();
 * webView.setWebViewClient(javascriptWebViewClient);
 * 
 * @version 0.01 February 2 2012
 * @author LIU WEI
 */
public class JavascriptWebViewClient extends WebViewClient {
	public JavascriptWebViewClient(Context context, JavascriptHandler mJavascriptHandler) {
		super();
	}

	/**
	 * 如果请求时设备功请求，调用设备功能
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		{
			return super.shouldOverrideUrlLoading(view, url);
		}
	}

}
