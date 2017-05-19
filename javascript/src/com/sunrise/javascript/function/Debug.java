package com.sunrise.javascript.function;

/**
 *@(#)ViewUtils.java        0.01 2013/3/7
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

import android.util.Log;

/**
 * 调试工具集
 * 
 * @version 0.01 13 March 2013
 * @author LIU WEI 
 */

public class Debug {
	private static final String TAG="HTML";
	private static final String UNDEFINED="undefined";
	
	public void log(String msg,String tag){
		if(tag.equalsIgnoreCase(UNDEFINED)){
			Log.d(TAG, msg);
		}else{
			Log.d(tag, msg);
		}
	}
	
	public void error(String msg,String tag){
		if(tag.equalsIgnoreCase(UNDEFINED)){
			Log.e(TAG, msg);
		}else{
			Log.e(tag, msg);
		}
	}
	
}
