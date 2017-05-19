/**
 *@(#)Sms.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript.function;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.utils.SmsUtils;
import com.sunrise.javascript.utils.StringUtils;

/**
 * 处理短信,实例被绑定到javascript上。
 * @version 0.01 January 12 2012
 * @author LIU WEI
 */
public final class Sms {
private Context mContext;
private JavascriptHandler mJavascriptHandler;

public Sms(Context mContext,JavascriptHandler mJavascriptHandler) {
	this.mContext = mContext;
	this.mJavascriptHandler = mJavascriptHandler;
}




/**
 * 发送短信 
 * @param phoneNumber 电话号码
 * @param content 短信内容
 * @throws IllegalArgumentException phoneNumber电话号码为空，不是数字
 */
public void sendMessage(final String phoneNumber,final String content){
	if(TextUtils.isEmpty(phoneNumber))
		throw new IllegalArgumentException("phoneNumber 为空");
	if(!StringUtils.isNumber(phoneNumber))
		throw new IllegalArgumentException("phoneNumber 不是数字");
	if(TextUtils.isEmpty(content))
		throw new IllegalArgumentException("content 短信内容为空");		
	new Thread(new Runnable() {		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(JavaScriptConfig.sDebug){
				Log.d(JavaScriptConfig.TAG, "发送短信");
			    Log.d(JavaScriptConfig.TAG, "phoneNumber:+"+phoneNumber);
			    Log.d(JavaScriptConfig.TAG, "content:+"+content);
			}
			SmsUtils.sendSMS(mContext,mJavascriptHandler,phoneNumber, content);
		}
	}).start();
}

/**
 * 调用系统界面发送短信 
 * @param phoneNumber 电话号码
 * @throws IllegalArgumentException phoneNumber电话号码为空，不是数字
 */
public void sendMessage(String phoneNumber){
	if(TextUtils.isEmpty(phoneNumber))
		throw new IllegalArgumentException("phoneNumber 为空");
	if(!StringUtils.isNumber(phoneNumber))
		throw new IllegalArgumentException("phoneNumber 不是数字");
	Uri smsToUri = Uri.parse("smsto:"+phoneNumber);
	Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
	mContext.startActivity(intent);
}
}
