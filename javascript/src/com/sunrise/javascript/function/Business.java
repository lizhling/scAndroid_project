/**
 *@(#)Business.java        0.01 2012/12/10
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript.function;
import android.os.Message;
import android.util.Log;

import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.mode.BusinessInformation;
import com.sunrise.javascript.utils.JsonUtils;

/**
 * 处理业务所需信息
 * 
 * @version 0.01 13 January 2013
 * @author LIU WEI 
 */
public class Business {
private final static String TAG="Business";
private BusinessInformation mBusinessInformation;
private JavascriptHandler mJavascriptHandler;
private static Business sBusiness;

private Business() {
	super();
}

public static Business getInstance(){
	if(sBusiness==null){
		sBusiness=new Business();
	}
	return sBusiness;
}

public void setBusinessInformation(BusinessInformation businessInformation) {
	mBusinessInformation = businessInformation;
}


public JavascriptHandler getJavascriptHandler() {
	return mJavascriptHandler;
}

public void setJavascriptHandler(JavascriptHandler mJavascriptHandler) {
	this.mJavascriptHandler = mJavascriptHandler;
}

/**
 * 获取业务页面所需信息
 */
public  void getBusinessInformation(){
	String result="buniess is null";
	if(mBusinessInformation!=null){
		result= JsonUtils.writeObjectToJsonStr(mBusinessInformation);
		Log.d(TAG, "mBusinessInformation:"+result);
	}
	sendJsonStrResult(result,mJavascriptHandler);
}

/**
 * 发送json字符串到handler
 * @param result	json字符串
 * @param handler   Handler实例
 */
private static void sendJsonStrResult(String result,JavascriptHandler handler){
	Log.d(TAG, result);
	String params[]={result};
	Message message = handler.obtainMessage(JavascriptHandler.JAVASCRIPT_GET_BUSINESS_INFORMATION,params);  
	message.sendToTarget();
}
}
