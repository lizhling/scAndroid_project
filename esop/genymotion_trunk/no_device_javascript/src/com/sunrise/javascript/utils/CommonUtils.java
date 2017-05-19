package com.sunrise.javascript.utils;

import android.os.Handler;
import android.os.Message;

import com.sunrise.javascript.JavascriptHandler;

public class CommonUtils {
	public static void sendResult(String result,String key,Handler handler){
		String params[]={result,key};
		Message message = handler.obtainMessage(JavascriptHandler.JAVASCRIPT_API_CALL_BACK,params);  
		message.sendToTarget();
	}
	
}
