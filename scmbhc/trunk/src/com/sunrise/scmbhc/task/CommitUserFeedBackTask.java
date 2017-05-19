package com.sunrise.scmbhc.task;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.entity.FeedBackInfo;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;
import com.sunrise.scmbhc.utils.LogUtlis;

public class CommitUserFeedBackTask extends GenericTask {
	public String TAG = "CommitUserFeedBackTask";
	private Context mContext;
	private FeedBackInfo mFeedBackInfo;
	public static String PHONE_NUM = "phonenum";
	public String PHONE_IMEI = "phoneimei";
	public String FEEDBACK_TYPE = "feedbacktype";
	public String FEEDBACK_CONTENT = "feedbackcontent";
	private String USER_FEEDBACK_URL = "scmbhm/MbhAppInterfaceAction/submitFeekback.action"; 
	
	public CommitUserFeedBackTask(Context mContext) {
		super();
		this.mContext = mContext;
	}
	public CommitUserFeedBackTask(Context mContext, FeedBackInfo mFeedBackInfo) {
		super();
		this.mContext = mContext;
		this.mFeedBackInfo = mFeedBackInfo;
	}
	
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String url = "http://" + "192.168.4.39" + ":8000" + "/" ;
		Integer retCode = -1;
		String retMsg = "";
		String imei  = "";
        String tel = "";
		try {
			// 获取IMEI
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
	        if (params[0].get(PHONE_NUM) != null && params[0].get(PHONE_NUM).toString().length() > 0) {
	        	tel = params[0].get(PHONE_NUM).toString();
	        } else {
	        	return TaskResult.FAILED;
	        }
	        imei = tm.getDeviceId();
	        if (imei == null || imei.equals("")) {
	        	imei = "3558842054339828";
	        }
	        String content = mFeedBackInfo.getContent();
	        String myurl = USER_FEEDBACK_URL+"?phone_no="+tel+"&imei="+imei+"&content="+URLEncoder.encode(content)+"&feekbackType="+mFeedBackInfo.getFeekbackType();
			// 发送URL请求
	        LogUtlis.showLogI(TAG, "url:"+myurl);
	        String jsonstr = null;
	       /* if (App.test) {
	        	jsonstr = "{'resultCode':0, 'resultMesage':'success'}";
	        } else*/ {
	        	try {
					jsonstr = App.sServerClient.commitFeedBack(mContext, myurl);   //jsonstr  = App.sServerClient.requestJson(url+myurl, null);
				} catch (IOException e) {
					e.printStackTrace();
					return TaskResult.FAILED;
				}
	        	
	        }
	        if(null == jsonstr || 0 == jsonstr.trim().length() ) {
	        	return TaskResult.FAILED;
	        }
			// 获取并解析返回值
	        LogUtlis.showLogI(TAG, "return json:"+jsonstr);
	        try {
				JSONObject obj = new JSONObject(jsonstr);
				if (obj != null) {
					retCode = Integer.valueOf(obj.getString("resultCode"));
					retMsg = obj.getString("resultMessage");
				}
			} catch (JSONException e) {
				retCode = -1;
				e.printStackTrace();
				return TaskResult.FAILED;
			} catch (Exception e) {
				retCode = -1;
				e.printStackTrace();
				return TaskResult.FAILED;
			} 
		
		} catch (HttpException e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (ServerInterfaceException e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (BusinessException e){
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		if(retCode != 0) {
			return TaskResult.CANCELLED;
		} else {
			return TaskResult.OK;
		}
	}
	/**
	 * 功能:  获取手机IMEI值
	 * @return 获取失败返回null，获取成功返回imei字符串 (GSM 返回IMEI值 ESN或CDMA返回 MEID值)
	 */
	String getPhoneIMEI() {
		// 获取IMEI
		String imei = null;
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        return imei;
	}
}
