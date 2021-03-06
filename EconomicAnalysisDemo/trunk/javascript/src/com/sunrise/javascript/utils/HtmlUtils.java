package com.sunrise.javascript.utils;
import com.sunrise.javascript.JavaScriptConfig;

import android.util.Log;
import android.webkit.WebView;

public class HtmlUtils {
	private static final String TAG="HtmlUtils";
	/**javascript 请求设备功能时url包含的标志*/
	public static final String STARCPT_WEB_API_FLAG="'StarCPTWebAPI'";
	public static final String PLUS_SYMBOL="+";
	public static final String STARCPT_WEP_API_URL_SPLIT_STRING="param";
	public static final String STARCPT_WEP_API_URL_SPLIT="'param'";
	private static final String FUNCTION="function ";
	private static final String VAR_URL_JAVASCRIPT_WEB_API_FLAG="var url="+STARCPT_WEB_API_FLAG+PLUS_SYMBOL+STARCPT_WEP_API_URL_SPLIT+PLUS_SYMBOL;
	private static final String NEWLINE="\n";
	private static final String BRACES_LEFT="{"+NEWLINE;
	private static final String BRACES_RIGHT="}"+NEWLINE;
	private static final String DOCUMENT_LOCATION= "document.location = url;"+NEWLINE;
	private static final String COMMON_START=NEWLINE+BRACES_LEFT+VAR_URL_JAVASCRIPT_WEB_API_FLAG;
	private static final String COMMON_END=NEWLINE+DOCUMENT_LOCATION+BRACES_RIGHT;				
	private static final String getCallLogCount=getJavascripMethod(JavaScriptConfig.GET_CALL_LOG_COUNT_BY_CALLTYPE_REQUEST_NAME,"callLogType");	
	private static final String getCallLog=getJavascripMethod(JavaScriptConfig.GET_LAST_CALL_LOG_BY_CALLTYPE_REQUEST_NAME,"callLogType","phoneNumber");	
	private static final String setPreferenceForKey=getJavascripMethod(JavaScriptConfig.SET_PREFERENCE_FOR_KEY_REQUEST_NAME,"fileName","key","value");	
	private static final String getPreferenceWithKey=getJavascripMethod(JavaScriptConfig.GET_PREFERENCE_WITH_KEY_REQUEST_NAME,"fileName","key");
	private static final String getPhoneManufacturer=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_MANUFACTURER_REQUEST_NAME);
	private static final String getPhoneModel=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_MODEL_REQUEST_NAME);
	private static final String getPhoneOS=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_OS_REQUEST_NAME);	
	private static final String getPhoneIMSI=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_IMSI_REQUEST_NAME);
	private static final String getPhoneIMEI=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_IMEI_REQUEST_NAME);
	private static final String getLocationCellID=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_LOCATIONCELLID_REQUEST_NAME);
	private static final String getLocationLAC=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_LAC_REQUEST_NAME);
	private static final String getLatitude=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_LATITUDE_REQUEST_NAME);
	private static final String getLongitude=getJavascripMethod(JavaScriptConfig.GET_HARDWARE_NETWORK_LONGITUDE_REQUEST_NAME);
	
	private static final String getLatitudeAndLongitude=getAndroidJavascriptMethod(JavaScriptConfig.HARDWAREANDNETWORK_OBJECT,JavaScriptConfig.GET_HARDWARE_NETWORK_LOCATION_REQUEST_NAME,"key");
	
	private static final String compressAndEncodeBase64= getAndroidJavascriptMethod(JavaScriptConfig.ZIPANDBASE64_JAVASCRIPT_OBJECT,JavaScriptConfig.COMPRESS_AND_ENCODEBASE64_NAME,"source","chartSetName","key");
	private static final String decompressAndDecodeBase64=getAndroidJavascriptMethod(JavaScriptConfig.ZIPANDBASE64_JAVASCRIPT_OBJECT,JavaScriptConfig.DE_COMPRESS_AND_DECODE_BASE64_NAME,"source","chartSetName","key");
	private static final String compressAndEncodeDes= getAndroidJavascriptMethod(JavaScriptConfig.ZIPANDBASE64_JAVASCRIPT_OBJECT,"compressAndEncodeDes","source","key");
	private static final String decompressAndDecodeDes=getAndroidJavascriptMethod(JavaScriptConfig.ZIPANDBASE64_JAVASCRIPT_OBJECT,"compressAndEncodeDes","source","key");
	
	private static final String startActivity=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.START_ACTIVITY_NAME, "action");
	private static final String finish=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.FINISH_WEBVIEW_NAME,"isFirstPage");
	private static final String refresh=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.REFRESH_WEBVIEW_NAME,"isShowBt");
	private static final String getBusinessInformation=getAndroidJavascriptMethod(JavaScriptConfig.BUSINESS_JAVASCRIPT_OBJECT,JavaScriptConfig.GET_BUSINESS_INFORMATION_NAME); 
	
	private static final String getDate=getAndroidJavascriptMethod(JavaScriptConfig.VIEW_JAVASCRIPT_OBJECT,JavaScriptConfig.GET_DATE_NAME, "key");
	private static final String getTime=getAndroidJavascriptMethod(JavaScriptConfig.VIEW_JAVASCRIPT_OBJECT,JavaScriptConfig.GET_TIME_NAME, "key");
	
	private static final String scanQrCode=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.SCAN_QR_CODE, "key");
	private static final String getStringFromImage=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.GET_STR_FROM_IMAGE,"path","key");
	private static final String deleteIDCardImages=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.DELETE_IDCARDS_IAMGES);
	private static final String takePicture=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.TAEK_PICTURE,"watermark", "key");
	private static final String takePicturenew=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.TAEK_PICTURE,"watermark","type","key");
	private static final String strTransferImg=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.TXT_TO_IMG,"FileStream","tag","key");
	private static final String getPhoneContracts=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT,"getPhoneContracts", "key");
	private static final String deleteFile=getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT, JavaScriptConfig.DELETE_FILE,"path","fileName","key");
	private static final String log=getAndroidJavascriptMethod(JavaScriptConfig.DEBUG_JAVASCRIPT_OBJECT,JavaScriptConfig.DEBUG_LOG, "msg","tag");
	private static final String error=getAndroidJavascriptMethod(JavaScriptConfig.DEBUG_JAVASCRIPT_OBJECT,JavaScriptConfig.DEBUG_LOG_ERROR, "msg","tag");

	private static final String sendMessage=getAndroidJavascriptMethod(JavaScriptConfig.SMS_OBJECT,JavaScriptConfig.SEND_SMS_REQUEST_NAME,"phoneNumber");
	private static final String voiceCall=getAndroidJavascriptMethod(JavaScriptConfig.PHONE_OBJECT,JavaScriptConfig.VOICE_CALL_REQUEST_NAME,"phoneNumber");		
	
	// 缓存
	private static final String getCacheValue = getAndroidJavascriptMethod(JavaScriptConfig.CACHE, JavaScriptConfig.CACHE_GET, "tab", "key");
	private static final String setCacheValue = getAndroidJavascriptMethod(JavaScriptConfig.CACHE, JavaScriptConfig.CACHE_SET, "tab", "value", "key");
	
	// 添加写卡组件接口
	public static final String ConfigReader =getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT,JavaScriptConfig.CONFIG_READER,"key");
	public static final String GetOPSVersion =getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT,JavaScriptConfig.GETOPS_VERSION,"key");
	public static final String GetCardSN =getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT,JavaScriptConfig.GETCARD_SN,"key");
	public static final String ReadCard =getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT,JavaScriptConfig.READSIMCARD_INFO,"key");
	public static final String WriteCard =getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT,JavaScriptConfig.WRITE_CARD,"info","key");
	public static final String GetOPSErrorMsg =getAndroidJavascriptMethod(JavaScriptConfig.ACTIVITYUTILS_JAVASCRIPT_OBJECT,JavaScriptConfig.GETOPS_ERRORMSG,"key");
		private static final String javaScriptMethods=
	    	getCallLogCount+
	    	getCallLog+
	    	setPreferenceForKey+
	    	getPreferenceWithKey+
	    	getPhoneManufacturer+
	    	getPhoneModel+
	    	getPhoneOS+
	    	getPhoneIMSI+
	    	getPhoneIMEI+
	    	getLocationCellID+
	    	getLocationLAC+
	    	getLatitude+
	    	getLongitude+
	        voiceCall+
	        sendMessage+
	    	getLatitudeAndLongitude+
	    	compressAndEncodeBase64+
	    	decompressAndDecodeBase64+
	    	compressAndEncodeDes+
	    	decompressAndDecodeDes+
	    	startActivity+
	    	finish+
	    	refresh+
	    	getBusinessInformation+
	    	getDate+
	    	getTime+
	    	getPhoneContracts+
	    	log+
	    	error+
	    	scanQrCode+
	    	getStringFromImage+
	    	deleteIDCardImages+
	    	takePicture+
	    	takePicturenew+
	    	strTransferImg+
	    	deleteFile+
	    	ConfigReader  + 
	    	GetOPSVersion + 
	    	GetCardSN + 
	    	ReadCard + 
	    	WriteCard +
	    	GetOPSErrorMsg+
	    	getCacheValue+
	    	setCacheValue;
    public static void addJavaScriptApiToApp(WebView view){
    	Log.d(TAG, "javaScriptMethods:"+javaScriptMethods);
    	
    	Log.d(TAG, "getCacheValue:"+getCacheValue);
    	Log.d(TAG, "setCacheValue:"+setCacheValue);
    	
//    	view.removeJavascriptInterface("accessibility");
//    	view.removeJavascriptInterface("ccessibilityaversal");
//    	view.removeJavascriptInterface("searchBoxJavaBridge_");
    	view.loadUrl("javascript:"+javaScriptMethods);
	}
    
    public static String getJavascripMethod(String methodName,String...params){
    	String javaScripParams="(";
    	int length=params.length;
    	if(length>0){
    	for(int i=0;i<params.length;i++){
    		if(i<length-1)
    		javaScripParams+=(params[i]+",");
    		else
    		javaScripParams+=(params[i]+")");
    	}
    	}else{
    		javaScripParams="()";
    	}
    	
    	String javaScripParamsSend=PLUS_SYMBOL;
    	if(length>0){
    		for(int j=0;j<params.length;j++){
        		if(j<length-1)
        			javaScripParamsSend+=STARCPT_WEP_API_URL_SPLIT+PLUS_SYMBOL+params[j]+PLUS_SYMBOL;
        		else
        			javaScripParamsSend+=STARCPT_WEP_API_URL_SPLIT+PLUS_SYMBOL+params[j]+";";
        	}
    	}else{
    	javaScripParamsSend=";";
    	}
    	String method=FUNCTION+methodName+javaScripParams+
    		    COMMON_START
    	        +"'"+methodName+"'"+javaScripParamsSend+
    		    COMMON_END;
    	return method;
    }

    public static String getAndroidJavascriptMethod(String javascriptObjectName,String methodName,String...params){
    	String javaScripParams="(";
    	int length=params.length;
    	if(length>0){
    	for(int i=0;i<params.length;i++){
    		if(i<length-1)
    		javaScripParams+=(params[i]+",");
    		else
    		javaScripParams+=(params[i]+")");
    	}
    	}else{
    		javaScripParams="()";
    	}
    	String method=FUNCTION+methodName+javaScripParams+
    	        BRACES_LEFT
    	        +"window."+javascriptObjectName+"."+methodName+javaScripParams+";"+NEWLINE+
    		    BRACES_RIGHT;
    	return method;
    }
}
