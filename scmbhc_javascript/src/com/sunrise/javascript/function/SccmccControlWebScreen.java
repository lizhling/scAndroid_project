package com.sunrise.javascript.function;

/**
 *@(#)JavasscriptUtils.java        0.01 2012/11/17
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */


import android.app.Activity;
import android.content.Context;
import com.sunrise.javascript.JavascriptHandler;

/**
 * @version 0.01 September 21 2012
 * @author LIU WEI
 */

public class SccmccControlWebScreen {
	private final static String TAG="Info";
	private Context mContext;
	private JavascriptHandler mJavascriptHandler;
	public SccmccControlWebScreen(Context mContext, JavascriptHandler mJavascriptHandler) {
		super();
		this.mContext = mContext;
		this.mJavascriptHandler = mJavascriptHandler;
	}
	
	public void closeWebScreen(){
		((Activity)mContext).finish();
	}
	
	
 }
