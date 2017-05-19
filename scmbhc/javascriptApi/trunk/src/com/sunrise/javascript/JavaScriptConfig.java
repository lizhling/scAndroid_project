/**
 *@(#)JavaScriptConfig.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

package com.sunrise.javascript;

/**
 * javascript相关常量
 * 
 * @version 0.01 12 January 2012
 * @author LIU WEI
 */
public class JavaScriptConfig {
	public static String TAG = "JavaScriptConfig";
	private static final String JAVASCRIPT_STR = "javascript:";
	private static final String PACKAGE_NAME = "Sccmcc";
	private static final String CALL_BACK_STR = "CallBack";
	public static final String JAVASCRIPT_INFO_OBJECT = PACKAGE_NAME + "Info";
	public static final String JAVASCRIPT_CONTROL_WEB_SCREEN_OBJECT = PACKAGE_NAME + "ControlWebScreen";
	public static final String JAVASCRIPT_CHOOSE_SD_IMAGE = "imageChooser";
	public static final String GET_USER_INFO_FUNCTION_NAME = "getUserInfo";
	public static final String GET_DEVICE_INFO_FUNCTION_NAME = "getDeviceInfo";
	public static final String JAVASCRIPT_PHONE_FUNCTION = "phone";
	public static final String JAVASCRIPT_ENCRYPTION_AND_DECRYPITON = "EncryAndDecry";
	public static final String GET_USER_INFO_FUNCTION_CALL_BACK_NAME = JAVASCRIPT_STR + GET_USER_INFO_FUNCTION_NAME + CALL_BACK_STR;
	public static final String GET_DEVICE_INFO_FUNCTION_CALL_BACK_NAME = JAVASCRIPT_STR + GET_DEVICE_INFO_FUNCTION_NAME + CALL_BACK_STR;
	public static final String JAVASCRIPT_API_CALLBACK_NAME = JAVASCRIPT_STR + "javaScriptApi" + CALL_BACK_STR;
	public static final boolean sDebug = false;
	private static boolean isLogin;

	/*
	 * private static final String FUNCTION="function "; private static final
	 * String NEWLINE="\n"; private static final String BRACES_LEFT="{"+NEWLINE;
	 * private static final String BRACES_RIGHT="}"+NEWLINE; private static
	 * final String
	 * getBusinessInformation=getAndroidJavascriptMethod(JAVASCRIPT_UTILS_OBJECT
	 * ,GET_USER_INFO_FUNCTION_NAME); private static final String
	 * javaScriptMethods=getBusinessInformation;
	 * 
	 * private static void addJavaScriptApiToApp(WebView view){
	 * view.loadUrl("javascript:"+javaScriptMethods); }
	 * 
	 * private static String getAndroidJavascriptMethod(String
	 * javascriptObjectName,String methodName,String...params){ String
	 * javaScripParams="("; int length=params.length; if(length>0){ for(int
	 * i=0;i<params.length;i++){ if(i<length-1)
	 * javaScripParams+=(params[i]+","); else javaScripParams+=(params[i]+")");
	 * } }else{ javaScripParams="()"; } String
	 * method=FUNCTION+methodName+javaScripParams+ BRACES_LEFT
	 * +"window."+javascriptObjectName
	 * +"."+methodName+javaScripParams+";"+NEWLINE+ BRACES_RIGHT; return method;
	 * }
	 */

	public static boolean isLogin() {
		return isLogin;
	}

	public static void setLogin(boolean isLogin) {
		JavaScriptConfig.isLogin = isLogin;
	}

}
