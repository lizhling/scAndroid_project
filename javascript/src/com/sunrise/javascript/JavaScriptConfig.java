/**
 *@(#)JavaScriptConfig.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

package com.sunrise.javascript;

import java.util.HashMap;

/**
 * javascript相关常量
 * 
 * @version 0.01 12 January 2012
 * @author LIU WEI
 */
public class JavaScriptConfig {
	/** 是否debug */
	public static boolean sDebug = true;
	public static boolean sIsPad=false;
	public static String TAG = "JavaScriptConfig";
	public static String sCaptureImageDir;
	
	/** 在javascript定义的发送短信回调函数的名字 */
	public static String SEND_SMS_CALL_BACK = "javascript:sendMessageCallBack";
	
	/**javascript定义的获得特定类型通话记录个数回调函数的名字*/
	public static final  String GET_CALL_LOG_COUNT_CALL_BACK="javascript:getCallLogCountCallBack";
	
	/**javascript定义的获得特定类型通话记录回调函数的名字*/
	public static final String GET_CALL_LOG_CALL_BACK="javascript:getCallLogCallBack";
	
	/**javascript定义的HARDWARE_NETWORK相关回调函数的名字*/
	public final static String HARDWARE_NETWORK_MANUFACTURER_CALL_BACK = "javascript:getPhoneManufacturerCallBack";
	public final static String HARDWARE_NETWORK_OS_CALL_BACK = "javascript:getPhoneOSCallBack";
	public final static String HARDWARE_NETWORK_MODEL_CALL_BACK = "javascript:getPhoneModelCallBack";
	public final static String HARDWARE_NETWORK_IMSI_CALL_BACK = "javascript:getPhoneIMSICallBack";
	public final static String HARDWARE_NETWORK_IMEI_CALL_BACK = "javascript:getPhoneIMEICallBack";
	public final static String HARDWARE_NETWORK_LOCATIONCELLID_CALL_BACK = "javascript:getLocationCellIDCallBack";
	public final static String HARDWARE_NETWORK_LOCATIONLAC_CALL_BACK = "javascript:getLocationLACCallBack";
	public final static String HARDWARE_NETWORK_LONGITUDE_CALL_BACK = "javascript:getLongitudeCallBack";
	public final static String HARDWARE_NETWORK_LATITUDE_CALL_BACK = "javascript:getLatitudeCallBack";
	public final static String HARDWARE_NETWORK_LOCATIONGPS_CALL_BACK = "javascript:getLatitudeAndLongitudeCallBack";
	
	/**在javascript定义的根据键key值，存储preference的值，到指定的文件回调函数的名字*/
	public final static String SET_PREFERENCE_FOR_KEY_CALL_BACK="javascript:setPreferenceForKeyCallBack";
	/**在javascript定义的根据文件名，取出key值对应的preference的值的回调函数的名字*/
	public final static String GET_PREFERENCE_WITH_KEY_CALL_BACK="javascript:getPreferenceWithKeyCallBack";
	
	/**第三方设备回调函数*/
	public static final String REQUEST_READ_UNION_PAY_REQUEST_CALL_BACK = "javascript:requestReadUnionPayCallBack";
	public static final String REQUEST_READ_UNION_REVERSED_REQUEST_CALL_BACK = "javascript:requestReadUnionReversedCallBack";
	public static final String REQUEST_READ_IC_CARD_REQUEST_CALL_BACK = "javascript:requestReadICCardCallBack";
	public static final String REQUEST_READ_ID_CARD_REQUEST_CALL_BACK = "javascript:requestReadIDCardCallBack";
	public static final String REQUEST_READ_PRINT_REQUEST_CALL_BACK = "javascript:requestPrintCallBack";
	public static final String REQUEST_READ_SIM_CARD_REQUEST_CALL_BACK = "javascript:requestReadSIMCardCallBack";
	public static final String REQUEST_WRITE_SIM_CARD_REQUEST_CALL_BACK = "javascript:requestWriteSIMCardCallBack";
	public static final String DISCONNECT_TO_DEVICE_CALL_BACK="javascript:disconnectCallBack";
	public static final String REQUEST_10085_IDCARD_CALL_BACK="javascript:getSmrznewCallBack";
	
	/**GZIP压缩和base64回调函数名字*/
	public static final String JAVASCRIPT_API_CALL_BACK="javascript:javaScriptApiCallBack";
	public static final String JAVASCRIPT_API_CALL_IMGIN_BACK="javascript:strTransferImagCallback";
	
	/**获取业务相关信息回调函数名字*/
	public static final String GET_BUSINESS_INFORMATION_CALL_BACK="javascript:getBusinessInformationCallBack";
	
	/**返回上页网页函数名字*/
	public static final String GO_BACK="javascript:aNative.goBack("+sIsPad+")";

	
	/**javascript 请求设备功能时url包含的标志*/
	public static final String STARCPT_WEB_API_FLAG="StarCPTWebAPI";
	public static final String STARCPT_WEP_API_URL_SPLIT_STRING="param";
	
	public static final String SEND_SMS_REQUEST_NAME="sendMessage";
	public static final String VOICE_CALL_REQUEST_NAME="voiceCall";
	public static final String GET_CALL_LOG_COUNT_BY_CALLTYPE_REQUEST_NAME="getCallLogCount";
	public static final String GET_LAST_CALL_LOG_BY_CALLTYPE_REQUEST_NAME="getCallLog";
	public static final String SET_PREFERENCE_FOR_KEY_REQUEST_NAME="setPreferenceForKey";
	public static final String GET_PREFERENCE_WITH_KEY_REQUEST_NAME="getPreferenceWithKey";
	
	public static final String GET_HARDWARE_NETWORK_MANUFACTURER_REQUEST_NAME="getPhoneManufacturer";
	public static final String GET_HARDWARE_NETWORK_MODEL_REQUEST_NAME="getPhoneModel";
	public static final String GET_HARDWARE_NETWORK_OS_REQUEST_NAME="getPhoneOS";
	public static final String GET_HARDWARE_NETWORK_IMSI_REQUEST_NAME="getPhoneIMSI";
	public static final String GET_HARDWARE_NETWORK_IMEI_REQUEST_NAME="getPhoneIMEI";
	public static final String GET_HARDWARE_NETWORK_LOCATIONCELLID_REQUEST_NAME="getLocationCellID";
	public static final String GET_HARDWARE_NETWORK_LAC_REQUEST_NAME="getLocationLAC";
	public static final String GET_HARDWARE_NETWORK_LATITUDE_REQUEST_NAME="getLatitude";
	public static final String GET_HARDWARE_NETWORK_LONGITUDE_REQUEST_NAME="getLongitude";
	public static final String GET_HARDWARE_NETWORK_LOCATION_REQUEST_NAME="getLatitudeAndLongitude";
	
	public static final String REQUEST_READ_UNION_PAY_REQUEST_NAME = "requestReadUnionPay";
	public static final String REQUEST_READ_UNION_REVERSED_REQUEST_NAME = "requestReadUnionReversed";
	public static final String REQUEST_READ_IC_CARD_REQUEST_NAME = "requestReadICCard";
	public static final String REQUEST_READ_ID_CARD_REQUEST_NAME = "requestReadIDCard";
	public static final String REQUEST_READ_PRINT_REQUEST_NAME = "requestPrint";
	public static final String REQUEST_READ_SIM_CARD_REQUEST_NAME = "requestReadSIMCard";
	public static final String DISCONNECT_TO_DEVICE_NAME="disconnect";
	
	public static final String COMPRESS_AND_ENCODEBASE64_NAME="compressAndEncodeBase64";
	public static final String DE_COMPRESS_AND_DECODE_BASE64_NAME="decompressAndDecodeBase64";
	public static final String GET_BUSINESS_INFORMATION_NAME="getBusinessInformation";
	public static final String START_ACTIVITY_NAME="startActivity";
	public static final String FINISH_WEBVIEW_NAME="finish";
	public static final String REFRESH_WEBVIEW_NAME="refresh";
	public static final String GET_DATE_NAME="showDatePickerDialog";
	public static final String GET_TIME_NAME="showTimePickerDialog";
	public static final String DEBUG_LOG="log";
	public static final String DEBUG_LOG_ERROR="error";
	public static final String GET_STR_FROM_IMAGE="getStringFromImage";
	public static final String DELETE_IDCARDS_IAMGES="deleteIDCardImages";
	public static final String TAEK_PICTURE="takePicture";
	public static final String TAEK_PICTURE_NEW="takePicturenew";
	public static final String TXT_TO_IMG ="strTransferImg";
	public static final String DELETE_FILE ="deleteFile";
	
	
	// 东信和平写卡组件接口
	public static final String CONFIG_READER ="ConfigReader";
	public static final String GETOPS_VERSION ="GetOPSVersion";
	public static final String GETCARD_SN ="GetCardSN";
	public static final String READSIMCARD_INFO ="requestReadSIMCard ";
	public static final String WRITE_CARD ="requestWriteSimCard";
	public static final String GETOPS_ERRORMSG ="GetOPSErrorMsg";
	
	// 缓存
	public static final String CACHE_GET = "getCacheValue";
	public static final String CACHE_SET = "setCacheValue";
	
	//public static final int SEND_SMS_REQUEST=0;
	public static final int VOICE_CALL_REQUEST=1;
	public static final int GET_CALL_LOG_COUNT_BY_CALLTYPE_REQUEST=2;
	public static final int GET_LAST_CALL_LOG_BY_CALLTYPE_REQUEST=3;
	public static final int SET_PREFERENCE_FOR_KEY_REQUEST=4;
	public static final int GET_PREFERENCE_WITH_KEY_REQUEST=5;
/*	public static final int GET_HARDWARE_NETWORK_MANUFACTURER_REQUEST=6;
	public static final int GET_HARDWARE_NETWORK_MODEL_REQUEST=7;
	public static final int GET_HARDWARE_NETWORK_OS_REQUEST=8;
	public static final int GET_HARDWARE_NETWORK_IMSI_REQUEST=9;
	public static final int GET_HARDWARE_NETWORK_IMEI_REQUEST=10;
	public static final int GET_HARDWARE_NETWORK_LOCATIONCELLID_REQUEST=11;
	public static final int GET_HARDWARE_NETWORK_LAC_REQUEST=12;
	public static final int GET_HARDWARE_NETWORK_LATITUDE_REQUEST=13;
	public static final int GET_HARDWARE_NETWORK_LONGITUDE_REQUEST=14;
	public static final int GET_HARDWARE_NETWORK_LOCATION_REQUEST=15;*/
	
/*	public static final int REQUEST_READ_UNION_PAY_REQUEST = 17;
	public static final int REQUEST_READ_UNION_REVERSED_REQUEST = 18;
	public static final int REQUEST_READ_IC_CARD_REQUEST = 19;
	public static final int REQUEST_READ_ID_CARD_REQUEST = 20;
    public static final int REQUEST_READ_ID_CARD_BY_CAMERA_REQUEST=21;
	public static final int REQUEST_READ_PRINT_REQUEST = 22;
	public static final int REQUEST_READ_SIM_CARD_REQUEST = 23;*/
	
	//public static final int DISCONNECT_TO_DEVICE_REQUEST =24;
	
	//public static final int COMPRESS_AND_ENCODEBASE64_REQUEST = 25;
	//public static final int DE_COMPRESS_AND_DECODE_BASE64_REQUEST = 26;
    //public static final int GET_BUSINESS_INFORMATION_REQUEST=27;
	
	public static HashMap<String, Integer> mWebApiRequest=new  HashMap<String, Integer>();
	
	static{
		//mWebApiRequest.put(SEND_SMS_REQUEST_NAME, SEND_SMS_REQUEST);
		mWebApiRequest.put(VOICE_CALL_REQUEST_NAME, VOICE_CALL_REQUEST);
		mWebApiRequest.put(GET_CALL_LOG_COUNT_BY_CALLTYPE_REQUEST_NAME, GET_CALL_LOG_COUNT_BY_CALLTYPE_REQUEST);
		mWebApiRequest.put(GET_LAST_CALL_LOG_BY_CALLTYPE_REQUEST_NAME, GET_LAST_CALL_LOG_BY_CALLTYPE_REQUEST);

		/*mWebApiRequest.put(GET_HARDWARE_NETWORK_MANUFACTURER_REQUEST_NAME, GET_HARDWARE_NETWORK_MANUFACTURER_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_MODEL_REQUEST_NAME, GET_HARDWARE_NETWORK_MODEL_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_OS_REQUEST_NAME, GET_HARDWARE_NETWORK_OS_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_IMSI_REQUEST_NAME, GET_HARDWARE_NETWORK_IMSI_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_IMEI_REQUEST_NAME, GET_HARDWARE_NETWORK_IMEI_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_LOCATIONCELLID_REQUEST_NAME, GET_HARDWARE_NETWORK_LOCATIONCELLID_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_LAC_REQUEST_NAME, GET_HARDWARE_NETWORK_LAC_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_LATITUDE_REQUEST_NAME, GET_HARDWARE_NETWORK_LATITUDE_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_LONGITUDE_REQUEST_NAME, GET_HARDWARE_NETWORK_LONGITUDE_REQUEST);
		mWebApiRequest.put(GET_HARDWARE_NETWORK_LOCATION_REQUEST_NAME, GET_HARDWARE_NETWORK_LOCATION_REQUEST);*/

		mWebApiRequest.put(SET_PREFERENCE_FOR_KEY_REQUEST_NAME, SET_PREFERENCE_FOR_KEY_REQUEST);
		mWebApiRequest.put(GET_PREFERENCE_WITH_KEY_REQUEST_NAME, GET_PREFERENCE_WITH_KEY_REQUEST);

/*		mWebApiRequest.put(REQUEST_READ_IC_CARD_REQUEST_NAME, REQUEST_READ_IC_CARD_REQUEST);
		mWebApiRequest.put(REQUEST_READ_ID_CARD_REQUEST_NAME, REQUEST_READ_ID_CARD_REQUEST);
		mWebApiRequest.put(REQUEST_READ_ID_CARD_BY_CAMERA_REQUEST_NAME, REQUEST_READ_ID_CARD_BY_CAMERA_REQUEST);
		mWebApiRequest.put(REQUEST_READ_PRINT_REQUEST_NAME, REQUEST_READ_PRINT_REQUEST);
		mWebApiRequest.put(REQUEST_READ_SIM_CARD_REQUEST_NAME, REQUEST_READ_SIM_CARD_REQUEST);
		mWebApiRequest.put(REQUEST_READ_UNION_PAY_REQUEST_NAME, REQUEST_READ_UNION_PAY_REQUEST);
		mWebApiRequest.put(REQUEST_READ_UNION_REVERSED_REQUEST_NAME, REQUEST_READ_UNION_REVERSED_REQUEST);*/
		//mWebApiRequest.put(DISCONNECT_TO_DEVICE_NAME,DISCONNECT_TO_DEVICE_REQUEST);
		
		//mWebApiRequest.put(COMPRESS_AND_ENCODEBASE64_NAME, COMPRESS_AND_ENCODEBASE64_REQUEST);
		//mWebApiRequest.put(DE_COMPRESS_AND_DECODE_BASE64_NAME, DE_COMPRESS_AND_DECODE_BASE64_REQUEST);
		
		//mWebApiRequest.put(GET_BUSINESS_INFORMATION_NAME, GET_BUSINESS_INFORMATION_REQUEST);
	}

	public static final String EXTRAL_DEVICE_ADDRESS="bluetooth_address";
	public static final String EXTRAL_GESTURE="gesture";
	public static final String EXTRAL_ID_CARD_INFO="id_info";
	public static final String ERROR_STR="error";
	public static final String EXTRAL_BITMAP="bitmap";
	public static final String EXTRAL_BITMAP_RESULT="bitmap_result";
	public static final String EXTRAL_CAPTURE_IMAGE_RESULT="reslut";
	public static final String EXTRAL_KEY="key";
	
	public static final String ZIPANDBASE64_JAVASCRIPT_OBJECT="ZipAndBase64";
	public static final String ACTIVITYUTILS_JAVASCRIPT_OBJECT="ActivityUtils";
	public static final String START_ACTIVITY_JAVASCRIPT_OBJECT="startActivity";
	public static final String BUSINESS_JAVASCRIPT_OBJECT="Business";
	public static final String VIEW_JAVASCRIPT_OBJECT="ViewUtils";
	public static final String DEBUG_JAVASCRIPT_OBJECT="Debug";
	public static final String HARDWAREANDNETWORK_OBJECT="HardwareAndNetwork";
	public static final String SMS_OBJECT="Sms";
	public static final String PHONE_OBJECT="Phone";
	public static final String CACHE="cache";
	
	public static final String mStrKey = "AADE0AC4938C3CE71D416CCD3CD75B5B8BCD3E28";
	public static final String SCAN_QR_CODE = "scanQrCode";
	
	public static boolean m_bKeyRight = true; // 授权Key正确，验证通过
	public static boolean sIsSupportThirdParthDevice=true;
	
	public static void setIsPad(boolean isPad){
		sIsPad=isPad;
	}

	public static void setCaptureImageDir(String dir){
		sCaptureImageDir=dir;
	}


	public static String goBack(){
		return "javascript:aNative.goBack("+sIsPad+")";
	}


	public static void setsIsSupportThirdParthDevice(
			boolean sIsSupportThirdParthDevice) {
		JavaScriptConfig.sIsSupportThirdParthDevice = sIsSupportThirdParthDevice;
	}
	
	
	
}
