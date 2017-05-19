/**
 *@(#)JavascriptHandler.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript;


import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;

import com.sunrise.javascript.utils.CommonUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;

/**
 * 调用javascript回调函数返回结果。
 * @version 0.01 13 January 2012
 * @author LIU WEI
 */

public class JavascriptHandler extends Handler{
private static final String TAG="JavascriptHandler";

private JavaScriptWebView mJavaScriptWebView;
	public JavascriptHandler(JavaScriptWebView mJavaScriptWebView) {
	this.mJavaScriptWebView = mJavaScriptWebView;
}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		ArrayList<Object> objs=(ArrayList<Object>) msg.obj;
		String callBackName=(String) objs.get(0);
		String[] params=(String[]) objs.get(1);
		callBackJavaScript(params,callBackName,mJavaScriptWebView);
	}
	
	 private static void callBackJavaScript(String[] params,String callBackName,WebView webView){
		 String javascriptMethod=CommonUtils.appendParamTOjavaScript(params, callBackName);
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 private void sendResult(String callBackName,String... results){
		String params[]=results;
		ArrayList<Object> obj=new ArrayList<Object>();
		obj.add(callBackName);
		obj.add(params);
		Message message = obtainMessage();
		message.obj=obj;
		message.sendToTarget();
    }		
	 public void sendObject(String callBackName,Object object) {
			String result = "object is null";
			if (object != null) {
				result = JsonUtils.writeObjectToJsonStr(object);
				LogUtlis.d(TAG, "sendObject:" + result);
			}
		 sendResult(callBackName,result);
	}
}
