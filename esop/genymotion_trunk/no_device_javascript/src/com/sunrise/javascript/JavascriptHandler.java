/**
 *@(#)JavascriptHandler.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;

/**
 * 接收耗时操作返回的结果，调用javascript回调函数返回结果。
 * 
 * @version 0.01 13 January 2012
 * @author LIU WEI
 */

public class JavascriptHandler extends Handler{
private static String TAG="JavascriptHandler";
public final static int JAVASCRIPT_SEND_SMS=0;
public final static int JAVASCRIPT_CALL_LOG_COUNT=1;
public final static int JAVASCRIPT_LAST_CALL_LOG=2;
//public final static int JAVASCRIPT_GET_LOCATIONGPS=3;
public final static int JAVASCRIPT_SET_PREFERENCE_FOR_KEY=4;
public final static int JAVASCRIPT_GET_PREFERENCE_WITH_KEY=5;
/*public static final int JAVASCRIPT_GET_MANUFACTURER =6;
public static final int JAVASCRIPT_GET_MODEL =7;
public static final int JAVASCRIPT_GET_OS =8;
public static final int JAVASCRIPT_GET_IMSI =9;
public static final int JAVASCRIPT_GET_IMEI =10;
public static final int JAVASCRIPT_GET_CELLID =11;
public static final int JAVASCRIPT_GET_LAC =12;
public static final int JAVASCRIPT_GET_LATITUDE =13;
public static final int JAVASCRIPT_GET_LONGITUDE =14;*/
public static final int JAVASCRIPT_READ_UNION_REVERSED = 6;
public static final int JAVASCRIPT_READ_IC_CARD = 7;
public static final int JAVASCRIPT_READ_ID_CARD = 8;
public static final int JAVASCRIPT_READ_PRINT = 9;
public static final int JAVASCRIPT_READ_SIM_CARD = 10;
public static final int JAVASCRIPT_READ_UNION_PAY = 11;
public static final int JAVASCRIPT_GET_BUSINESS_INFORMATION=12;
public static final int JAVASCRIPT_API_CALL_BACK=13;
//public static final int JAVASCRIPT_API_CALL_IMGIN_BACK=16;

private JavaScriptWebView mJavaScriptWebView;
	public JavascriptHandler(JavaScriptWebView mJavaScriptWebView) {
	this.mJavaScriptWebView = mJavaScriptWebView;
}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		String[] params=(String[]) msg.obj;
		switch(msg.what){
		case JAVASCRIPT_SEND_SMS:
			sendSmsCallBack(params,mJavaScriptWebView);
			break;
		case JAVASCRIPT_CALL_LOG_COUNT:
			getCallLogCountBack(params,mJavaScriptWebView);
			break;
		case JAVASCRIPT_LAST_CALL_LOG:
			getCallLogBack(params,mJavaScriptWebView);
			break;
		case JAVASCRIPT_SET_PREFERENCE_FOR_KEY:
			setPreferenceForKeyCallBack(params,mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_PREFERENCE_WITH_KEY:
			getPreferenceWithKeyCallBack(params,mJavaScriptWebView);
			break;
		/*case JAVASCRIPT_GET_IMEI:
			getIMEIBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_IMSI:
			getIMSIBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_LAC:
			getLACBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_LOCATIONGPS:
			getLocationGPSBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_LATITUDE:
			getLatitudeBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_CELLID:
			getLocationCellIDBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_LONGITUDE:
			getLongitudeBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_MANUFACTURER:
			getManufacturerlocationBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_MODEL:
			getModelBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_OS:
			getOSBack(params, mJavaScriptWebView);
			break;*/
		case JAVASCRIPT_READ_IC_CARD:
			requestReadICCardCallBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_READ_ID_CARD:
			requestReadIDCardCallBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_READ_PRINT:
			requestReadPrintCallBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_READ_SIM_CARD:
			requestReadSIMCardCallBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_READ_UNION_PAY:
			requestReadUnionPayCallBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_READ_UNION_REVERSED:
			requestReadUnionReversedCallBack(params, mJavaScriptWebView);
			break;
		case JAVASCRIPT_GET_BUSINESS_INFORMATION:
			getBusinessInformationCallBack(params,mJavaScriptWebView);
			break;
		case JAVASCRIPT_API_CALL_BACK:
			javascriptApiCallBack(params,mJavaScriptWebView);
//		case JAVASCRIPT_API_CALL_IMGIN_BACK:
//			strTransferImagCallback(params,mJavaScriptWebView);
		}
	}
	
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void sendSmsCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.SEND_SMS_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params 
	  */
	 private static void getCallLogCountBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.GET_CALL_LOG_COUNT_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getCallLogBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.GET_CALL_LOG_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getOSBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_OS_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getIMEIBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_IMEI_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getIMSIBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_IMSI_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getLACBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_LOCATIONLAC_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getLongitudeBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_LONGITUDE_CALL_BACK);						
		 Log.d(TAG, "javascriptMethod:"+javascriptMethod);
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getLatitudeBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_LATITUDE_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getLocationGPSBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_LOCATIONGPS_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getLocationCellIDBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_LOCATIONCELLID_CALL_BACK);						
		 Log.d(TAG, "javascriptMethod:"+javascriptMethod);
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getManufacturerlocationBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_MANUFACTURER_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 调用回调函数返回结果
	  * @param params
	  */
	 private static void getModelBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.HARDWARE_NETWORK_MODEL_CALL_BACK);						
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 
	 /**
	  *调用回调函数返回结果
	  *@param params
	  */
	 private static void setPreferenceForKeyCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.SET_PREFERENCE_FOR_KEY_CALL_BACK);	
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  *调用回调函数返回结果
	  *@param params
	  */
	 private static void getPreferenceWithKeyCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.GET_PREFERENCE_WITH_KEY_CALL_BACK);	
		 webView.loadUrl(javascriptMethod);
	 }
	
	 /**
	  *调用回调函数返回结果
	  *@param params
	  */
	 private static void requestReadUnionPayCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.REQUEST_READ_UNION_PAY_REQUEST_CALL_BACK);	
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  *调用回调函数返回结果
	  *@param params
	  */
	 private static void requestReadUnionReversedCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.REQUEST_READ_UNION_REVERSED_REQUEST_CALL_BACK);	
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  *调用回调函数返回结果
	  *@param params
	  */
	 private static void requestReadICCardCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.REQUEST_READ_IC_CARD_REQUEST_CALL_BACK);	
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  *调用回调函数返回结果
	  *@param params
	  */
	 private static void requestReadIDCardCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.REQUEST_READ_ID_CARD_REQUEST_CALL_BACK);	
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  *调用回调函数返回结果
	  *@param params
	  */
	 private static void requestReadPrintCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.REQUEST_READ_PRINT_REQUEST_CALL_BACK);	
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  *调用回调函数返回结果
	  *@param params
	  */
	 private static void requestReadSIMCardCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.REQUEST_READ_SIM_CARD_REQUEST_CALL_BACK);	
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 业务信息回调函数
	  * */
	 private static void getBusinessInformationCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.GET_BUSINESS_INFORMATION_CALL_BACK);
		 webView.loadUrl(javascriptMethod);
	 }
	 
	 /**
	  * 字符串gzip解压缩和base64 解密回调函数
	  */
	 private static void javascriptApiCallBack(String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.JAVASCRIPT_API_CALL_BACK);
		 webView.loadUrl(javascriptMethod);
	 }
	 /**
	  * 字符串解密并保存为图片回调函数
	  */
	 private static void strTransferImagCallback (String[] params,WebView webView){
		 String javascriptMethod=JavaScriptWebView.appendParamTOjavaScript(params, JavaScriptConfig.JAVASCRIPT_API_CALL_IMGIN_BACK);
		 webView.loadUrl(javascriptMethod);
	 }
}
