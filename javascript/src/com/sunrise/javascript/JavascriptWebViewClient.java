/**
 *@(#)JavascriptWebViewClient.java  0.01 2012/02/2
 * Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript;



import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.sunrise.javascript.function.CallLog;
import com.sunrise.javascript.function.Preference;
import com.sunrise.javascript.utils.HtmlUtils;


/**
 * 
 * 主要帮助WebView处理各种通知、请求事件,能够处理javascript
 * 对设备的功能请求。
 * 代码片段：
 * JavascriptWebViewClient javascriptWebViewClient=new JavascriptWebViewClient();
 * webView.setWebViewClient(javascriptWebViewClient);
 * @version 0.01 February 2 2012
 * @author LIU WEI
 */
public class JavascriptWebViewClient extends WebViewClient {
	//private static final String TAG="JavascriptWebViewClient";
	private static final int PARAM_MAX_NUM=5; 
    private static final String mParams[]=new String[PARAM_MAX_NUM];
    static{
    	for(int i=0;i<mParams.length;i++){
    		mParams[i]="";
    	}
    }
    private Context mContext;
    private JavascriptHandler mJavascriptHandler;
	public JavascriptWebViewClient(Context context,
			JavascriptHandler mJavascriptHandler) {
		super();
		this.mContext = context;
		this.mJavascriptHandler = mJavascriptHandler;	
		
	}

	/**
	 * 如果请求时设备功请求，调用设备功能
	 */
	@Override  
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if(url.contains(JavaScriptConfig.STARCPT_WEB_API_FLAG)){
			String params[]=getParamsFormRequest(url);
            System.arraycopy(params, 0, mParams, 0, params.length);
            int request=JavaScriptConfig.mWebApiRequest.get(mParams[0]);
            switch(request){
           /* case JavaScriptConfig.SEND_SMS_REQUEST:
            	String sendMessagePhoneNumber=mParams[1];
            	String smsContent=URLDecoder.decode(mParams[2]);           	
            	Sms sendMessage=new Sms(mContext, mJavascriptHandler);
            	sendMessage.sendSms(sendMessagePhoneNumber, smsContent);
            	break;*/
           /* case JavaScriptConfig.VOICE_CALL_REQUEST:
            	String voiceCallphoneNumber=mParams[1];
            	Phone voiceCall=new Phone(mContext);
            	voiceCall.callVoice(voiceCallphoneNumber);
            	break;*/
            case JavaScriptConfig.GET_CALL_LOG_COUNT_BY_CALLTYPE_REQUEST:
            	String getCallLogCountCallType=mParams[1];
            	CallLog getCallLogCount=new CallLog(mContext, mJavascriptHandler);
            	getCallLogCount.getCallLogCount(getCallLogCountCallType);
            	break;
            case JavaScriptConfig.GET_LAST_CALL_LOG_BY_CALLTYPE_REQUEST:
            	String getCallLogCallType=mParams[1];
            	String getCallLogPhoneNumber=mParams[2];
            	CallLog getCallLog=new CallLog(mContext, mJavascriptHandler);
            	getCallLog.getCallLog(getCallLogCallType, getCallLogPhoneNumber);
            	break;
            case JavaScriptConfig.SET_PREFERENCE_FOR_KEY_REQUEST:
            	String setFileName=mParams[1];
            	String setKey=mParams[2];
            	String setValue=mParams[3];
            	Preference setPreferenceForKey=new Preference(mContext,mJavascriptHandler);
            	setPreferenceForKey.setPreferenceForKey(setFileName, setKey, setValue);
            	break;
            case JavaScriptConfig.GET_PREFERENCE_WITH_KEY_REQUEST:
            	String getFileName=mParams[1];
            	String getKey=mParams[2];
            	Preference getPreferenceWithKey=new Preference(mContext,mJavascriptHandler);
            	getPreferenceWithKey.getPreferenceForKey(getFileName, getKey);
            	break;
          /*  case JavaScriptConfig.GET_HARDWARE_NETWORK_IMEI_REQUEST:
            	HardwareAndNetwork phoneIMEI = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	phoneIMEI.getPhoneIMEI();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_IMSI_REQUEST:
            	HardwareAndNetwork phoneIMSI = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	phoneIMSI.getPhoneIMS();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_LAC_REQUEST:
            	HardwareAndNetwork networkLAC = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	networkLAC.getLocationLAC();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_LATITUDE_REQUEST:
            	HardwareAndNetwork latitude = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	latitude.getLatitude();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_LOCATIONCELLID_REQUEST:
            	HardwareAndNetwork locationCellID = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	locationCellID.getLocationCellID();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_LONGITUDE_REQUEST:
            	HardwareAndNetwork longitude = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	longitude.getLongitude();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_LOCATION_REQUEST:
            	HardwareAndNetwork location= new HardwareAndNetwork(mContext, mJavascriptHandler);
            	location.getLatitudeAndLongitude();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_MANUFACTURER_REQUEST:
            	HardwareAndNetwork phoneManufacturer = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	phoneManufacturer.getPhoneManufacturer();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_MODEL_REQUEST:
            	HardwareAndNetwork phoneModel = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	phoneModel.getPhoneModel();
            	break;
            case JavaScriptConfig.GET_HARDWARE_NETWORK_OS_REQUEST:
            	HardwareAndNetwork phoneOS = new HardwareAndNetwork(mContext, mJavascriptHandler);
            	phoneOS.getPhoneOS();
            	break;*/
          /*  case JavaScriptConfig.REQUEST_READ_IC_CARD_REQUEST:
            	mThirdPartyDevice.requestReadICCard();
            	break;
            case JavaScriptConfig.REQUEST_READ_ID_CARD_REQUEST:
            	mThirdPartyDevice.requestReadIDCard();
            	break;
            case JavaScriptConfig.REQUEST_READ_ID_CARD_BY_CAMERA_REQUEST:
            	mThirdPartyDevice.requestReadIDCardByCamera();
            	break;
            case JavaScriptConfig.REQUEST_READ_PRINT_REQUEST:
            	String print = mParams[1];
            	String charset = mParams[2];
            	Boolean isSign = Boolean.valueOf(mParams[3]);
            	mThirdPartyDevice.requestPrint(print,charset,isSign);
            	break;  
            case JavaScriptConfig.REQUEST_READ_SIM_CARD_REQUEST:
            	mThirdPartyDevice.requestReadSIMCard();
            	break;
            case JavaScriptConfig.REQUEST_READ_UNION_PAY_REQUEST:
            	String payValue = mParams[1];
            	mThirdPartyDevice.requestReadUnionPay(payValue);
            	break;
            case JavaScriptConfig.REQUEST_READ_UNION_REVERSED_REQUEST:
            	String no = mParams[1];
            	mThirdPartyDevice.requestReadUnionReversed(no);
            	break;*/
        /*    case JavaScriptConfig.COMPRESS_AND_ENCODEBASE64_REQUEST:
            	String source =mParams[1];
            	String chartSetName=mParams[2];
            	ZipAndBase64 zipAndBase64=new ZipAndBase64(mJavascriptHandler);
            	zipAndBase64.compressAndEncodeBase64(source, chartSetName);
            	break;
            case JavaScriptConfig.DE_COMPRESS_AND_DECODE_BASE64_REQUEST:
            	String deSource =mParams[1];
            	String deChartSetName=mParams[2];
            	ZipAndBase64 deZipAndBase64=new ZipAndBase64(mJavascriptHandler);
            	deZipAndBase64.decompressAndDecodeBase64(deSource, deChartSetName);
            	break;*/
           /* case JavaScriptConfig.GET_BUSINESS_INFORMATION_REQUEST:
            	mBusiness.getBusinessInformation();
            	break;*/
            }
			return true;
		}else{
			JavaScriptWebView javaScriptWebView=(JavaScriptWebView) view;
			javaScriptWebView.loadUrlCache(url);
			return true;
		}
	}
	
    /**
     * 从功能请求的url中得到参数数组
     * @param url 功能请求url
     */
	private String[] getParamsFormRequest(String url){
		int requestParamsStartIndex=url.indexOf(JavaScriptConfig.STARCPT_WEB_API_FLAG)+JavaScriptConfig.STARCPT_WEB_API_FLAG.length()+
				JavaScriptConfig.STARCPT_WEP_API_URL_SPLIT_STRING.length();
		String paramsStr=url.substring(requestParamsStartIndex);
		String params[]=paramsStr.split(JavaScriptConfig.STARCPT_WEP_API_URL_SPLIT_STRING);
		return params;
	}
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		// TODO Auto-generated method stub
		super.onPageStarted(view, url, favicon);
		HtmlUtils.addJavaScriptApiToApp(view);
	}
}
