/**
 *@(#)SMSUtils.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import com.sunrise.javascript.JavascriptHandler;

/**
 * 处理短信工具
 * 
 * @version 0.01 January 12 2012
 * @author LIU WEI
 */
public class SmsUtils {
	
	/*用于监听短信发送结果的广播*/
	static String SEND_SMS_ACTION = "SENT_SMS_ACTION";
	
	/**
	 * 发送短信
	 * @param context 上下文
	 * @param phoneNumber 电话号码
	 * @param content 短信内容
	 * @param webView 显示当前HTML页面的view
	 */
	 public static void sendSMS(Context context,final Handler handler,String phoneNumber,String content){
	    	SmsManager smsManager = SmsManager.getDefault();
	    	ArrayList<PendingIntent> sendIntents = new ArrayList<PendingIntent>();
	        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(SEND_SMS_ACTION), 0);
	        sendIntents.add(sentIntent);
	        context.registerReceiver(new BroadcastReceiver() {				
				@Override
				public void onReceive(Context context, Intent intent) {
					boolean result=false;
					switch (getResultCode()) {
	    			case Activity.RESULT_OK:
	    				result=true;
	    				break;
	    			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	    				break;
	    			case SmsManager.RESULT_ERROR_RADIO_OFF:
	    				break;
	    			case SmsManager.RESULT_ERROR_NULL_PDU:
	    				break;
	    			}
					String[] params={result+""};
					Message msg=handler.obtainMessage(JavascriptHandler.JAVASCRIPT_SEND_SMS, params);
					handler.sendMessage(msg);
    				context.unregisterReceiver(this);
				}
				
			},  new IntentFilter(SEND_SMS_ACTION));
	        ArrayList<String> msgs = smsManager.divideMessage(content);
	        smsManager.sendMultipartTextMessage(phoneNumber, null, msgs, sendIntents, null);
	 }
}
