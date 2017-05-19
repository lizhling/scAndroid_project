package com.sunrise.scmbhc.task;

import java.io.IOException;

import android.content.Context;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppDirConstant;
import com.sunrise.scmbhc.entity.ContentInfos;
import com.sunrise.scmbhc.entity.UpdateInfo;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.utils.LogUtlis;

public class GetContentInfoTask extends GenericTask {
	private final String TAG = "GetContentInfoTask";
	private Context mContext;
	private UpdateInfo mUpdateInfo;
	private final String TYPE = "type";
	private final String NOTICE_TYPE = "NOTICE_TYPE";
	private final String HELP_TYPE = "HELP_TYPE";
	private static String type = "";

	public GetContentInfoTask(Context mContext, UpdateInfo mUpdateInfo) {
		super();
		this.mContext = mContext;
		this.mUpdateInfo = mUpdateInfo;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String url = mUpdateInfo.getDownloadUrl();
		LogUtlis.showLogI(TAG, "url请求" + url);
		type = params[0].get(TYPE).toString();
		LogUtlis.showLogI(TAG, "type:" + type);
		String filename = "";
		if (type.trim().equals(UpdateInfo.TYPE_SYSTEM_NOTICE + "")) {
			filename = AppDirConstant.APP_SYSTEM_NOTICE_JSON_NAME;
			LogUtlis.showLogI(TAG, "UpdateInfo.SYSTEM_NOTICE_TYPE:" + type);
		} else if (type.trim().equals(UpdateInfo.TYPE_USER_GUIDE + "")) {
			filename = AppDirConstant.APP_USER_GUIDE_JSON_NAME;
			LogUtlis.showLogI(TAG, "UpdateInfo.USER_GUIDE_TYPE:" + type);
		} else if (type.trim().equals(UpdateInfo.TYPE_COMMONLY_PROBLEM + "")) {
			filename = AppDirConstant.APP_COMMONLY_PROBLEM_JSON_NAME;
			LogUtlis.showLogI(TAG, "UpdateInfo.APP_COMMONLY_PROBLEM_JSON_NAME:" + type);
		}else if (type.trim().equals(UpdateInfo.TYPE_CREDITS_EXCHANGE + "")) {
			filename = AppDirConstant.APP_CREDITS_EXCHANGE_JSON_NAME;
			LogUtlis.showLogI(TAG, "UpdateInfo.APP_CREDITS_EXCHANGE_JSON_NAME:" + type);
		} else {
			LogUtlis.showLogI(TAG, "请求失败" + type);
			return TaskResult.FAILED;
		}
		try {
			// 获取公告信息
			ContentInfos contentlist = App.sServerClient.getContentInfo(mContext, url, filename);// new
			// 保存新版本号
			App.sSettingsPreferences.putResVersion(type, mUpdateInfo.getNewVersionCode());
			App.sContentInfoList.clear();
			LogUtlis.showLogI(TAG, "type:"+type+"\ncontent list:" + JsonUtils.writeObjectToJsonStr(contentlist));
			// 保存公告信息到文件
			com.sunrise.scmbhc.utils.FileUtils.saveToDataFile(mContext,JsonUtils.writeObjectToJsonStr(contentlist), filename);
			/* App.sContentInfoList.addAll(contentlist.getDatas()); */
		} catch (HttpException e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (ServerInterfaceException e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (IOException e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (BusinessException e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

	// String APP_CONTENTINFO_LIST = "contentInfolist.json";

	/*
	 * private Context mContext; private ContentInfo mContentInfo; public String
	 * PHONE_NUM = ""; public String PHONE_IMEI = ""; public String
	 * FEEDBACK_TYPE = ""; public String FEEDBACK_CONTENT = "";
	 * 
	 * public GetContentInfoTask(Context mContext) { super(); this.mContext =
	 * mContext; } public GetContentInfoTask(Context mContext, ContentInfo
	 * mContentInfo) { super(); this.mContext = mContext; this.mContentInfo =
	 * mContentInfo; }
	 * 
	 * @Override protected TaskResult _doInBackground(TaskParams... params) {
	 * String url = ""; Integer retCode = -1; String retMsg = null; String datas
	 * = null; try { // llMenus allMenus = App.sServerClient.getAllMenus(url);
	 * // 获取参数 // 获取本机号码 TelephonyManager tm = (TelephonyManager)
	 * mContext.getSystemService(Context.TELEPHONY_SERVICE); String deviceid =
	 * tm.getDeviceId(); String tel = tm.getLine1Number(); String contentType =
	 * ""; String imei = tm.getSimSerialNumber();
	 * 
	 * String myurl = url +
	 * "getContentTypeList.action?phone_no="+tel+"&cntentType="+contentType; //
	 * 发送url请求 String jsonstr = App.sServerClient.requestJson(url, null); //
	 * 获取返回值 // 判断返回值
	 * 
	 * try { JSONObject obj = new JSONObject(jsonstr); if (obj != null) {
	 * retCode = Integer.valueOf(obj.getString("resultCode")); retMsg =
	 * obj.getString("resultMesage"); datas = obj.getString("datas"); JSONArray
	 * arrayObj = new JSONArray(datas);
	 * 
	 * List<ContentInfo> content = new
	 * ArrayList<ContentInfo>();//JsonUtils.parseJsonStrToObject(datas,
	 * ContentInfo.class) for (int i = 0; arrayObj != null && i <
	 * arrayObj.length(); i++) { ContentInfo contentinfo = new ContentInfo();
	 * JSONObject jsonObject = arrayObj.getJSONObject(i); int type =
	 * Integer.valueOf(jsonObject.getString("contentType"));
	 * contentinfo.setContentType(type);
	 * contentinfo.setTitle(jsonObject.getString("title"));
	 * contentinfo.setContentText(jsonObject.getString("contentText"));
	 * content.add(contentinfo); }
	 * 
	 * } } catch (JSONException e) { 
	 * = -1; e.printStackTrace(); } catch (Exception e) {
	 * catch block retCode = -1; e.printStackTrace(); }
	 * 
	 * } catch (HttpException e) { 
	 * e.printStackTrace(); return TaskResult.FAILED; } catch
	 * (ServerInterfaceException e) { 
	 * e.printStackTrace(); return TaskResult.FAILED; } if(retCode != 0) {
	 * return TaskResult.FAILED; } else {
	 * 
	 * return TaskResult.OK; }
	 * 
	 * 
	 * }
	 */

}
