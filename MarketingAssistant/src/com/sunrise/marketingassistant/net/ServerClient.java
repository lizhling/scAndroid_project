package com.sunrise.marketingassistant.net;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.marketingassistant.exception.http.HttpException;
import com.sunrise.marketingassistant.exception.http.ResponseException;
import com.sunrise.marketingassistant.exception.logic.BusinessException;
import com.sunrise.marketingassistant.exception.logic.ServerInterfaceException;
import com.sunrise.marketingassistant.net.http.HttpClient;
import com.sunrise.marketingassistant.utils.ZipUtils;

import android.text.TextUtils;

import com.sunrise.javascript.utils.LogUtlis;

public class ServerClient {
	private static final String TAG = "ServerClient";
	private static ServerClient Instance;

	private static final String URL_SENDDYNAMICCODE = "scUnifiedAppManagePlatform/unifiedAppInterface/sendDynamicCode.action";// 4a获取验证码
	private static final String URL_LOGIN_4A = "scUnifiedAppManagePlatform/unifiedAppInterface/login.action";// 4a登录
	private static final String URL_GET_ACCOUNT_SUB_ACCOUNT_INFO = "scUnifiedAppManagePlatform/businessHandlingService/getAccountSubAccountInfo.action";// 获取从账号
	/** 获取GROUPID */
	private static final String URL_GET_REGION_ORG_ID_BY_LOGIN_NO = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getRegionOrgIdByLoginNo";

	private static final String URL_UPDATEINFOS = "scUnifiedAppManagePlatform/unifiedAppInterface/json/updateInfos.json";// 升级配置信息
	private static final String URL_UPDATEINFO = "http://218.205.252.26:18098/scUnifiedAppManagePlatform/resources/uploadtemp/updateInfo.json";
	private static final String GET_APPLICATIONS_URL = "scUnifiedAppManagePlatform/unifiedAppInterface/getApplicationList.action";// 获取应用信息

	/** 渠道搜索 */
	private static final String URL_CHANNEL_SEARCH = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/groupMsgSearch";
	/** 渠道网点查询 */
	private static final String URL_MODE_TERMINA_BY_GROUPID = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/sModsTerminaLByGroupId";
	/** 渠道信息查询 */
	private static final String URL_GROUP_MSG_INFO = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/groupMsgInfo";

	/** 竞争对手 */
	private static final String URL_COMPETITOR_QUERY_URL = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getCptListByRangenew";

	/** 指标参数 */
	private static final String URL_GROUP_INDEX_INDEXSORT = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getGroupIndexsort";
	/** 签到 ***/
	private static final String URL_DO_MOB_REGISTE = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/sMobRegiste";
	/** 根据imsi获取手机号 */
	private static final String URL_GET_PHONENUM_BY_IMSI = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/selAPPAgent";

	private static final String PORT = ":18098";
	public static final String SERVER_IP = "218.205.252.26";

	private static final String HTTP = "http://";
	private static final String HTTP_SPLIT = "/";
	private static final String URL_GET_MOB_MENBER_SCORE = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/sMobMemberScore";

	/** 获取竞争对手信息列表 */
	private static final String GET_CPT_LIST = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getCptListnew";
	/** 获取竞争对手网点基本信息 */
	private static final String GET_BASE_INFO = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getBaseInfonew";
	/** 保存竞争对手网点基本信息 */
	private static final String SAVE_BASE_INFO = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/saveOrUpdateBaseInfonew";
	/** 删除竞争对手网点基本信息 */
	private static final String DEL_BASE_INFO = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/delBaseInfonew";
	/** 获取竞争对手网点活动基本信息 */
	private static final String GET_ACTIVITY_INFO = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getActInfonew";
	/** 保存竞争对手网点活动基本信息 */
	private static final String SAVE_ACT_INFO = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/saveOrUpdateActivityInfonew";
	/** 删除竞争对手网点活动基本信息 */
	private static final String DEL_ACT_INFO = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/delActivityInfonew";
	/** 根据工号获取酬金信息 */
	private static final String GET_REWARD_DETAIL = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getRewardDetail";
	/** 查找渠道树 */
	private static final String GET_CHANNEL_TREE = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getChannelTree";
	/** 查找渠道列表 */
	private static final String GET_CHANNEL_LIST = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getChannelList";
	/** 查找渠道列表 */
	private static final String GET_REWARD_TREE = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/rewardSubjectTree";
	/** 查酬金反查询明细 */
	private static final String GET_REWARD_INVERSE_DETAIL = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/rewardInverseDetail";
	/** 上传图片 */
	private static final String UPLOAD_IMAGE = "http://218.205.252.26:18098/scUnifiedAppManagePlatform/unifiedAppInterface/uploadInfoImg";
	/** 上传图片信息 */
	private static final String SAVE_OR_UPDATE_IMAGE = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/saveOrUpdatePictureInfonew";
	/** 签到轨迹 **/
	private static final String URL_GET_MOB_REGISTE = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/sMobRegisteTrack";
	/** 获取图片 **/
	private static final String URL_GET_FILE = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getFile";
	/** 查询渠道指标排序 */
	private static final String URL_GET_INDEX_BROTHERSORT = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getIndexBrothersort";

	/** 查询渠道份额 */
	private static final String URL_GET_APP_QUOTA_BY_GROUPID = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/getAppQuotabyGroupid";

	private HttpClient mHttpClient;

	private ServerClient() {
		mHttpClient = HttpClient.getInstance();
	}

	public static ServerClient getInstance() {
		if (Instance == null) {
			Instance = new ServerClient();
		}
		return Instance;
	}

	private String getUrl(String relUrl) {
		String absUrl = HTTP + SERVER_IP + PORT + HTTP_SPLIT + relUrl;
		return absUrl;
	}

	public String login(String userName, String dynamicCode) throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("userName", userName));
		pairs.add(new BasicNameValuePair("dynamicCode", dynamicCode));
		String jsonStr = mHttpClient.httpRequest(getUrl(URL_LOGIN_4A), pairs);
		LogUtlis.d(TAG, "jsonStr:" + jsonStr);
		return jsonStr;
	}

	public String checklogin(String phone_no, String password, String imei, String imsi) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account", phone_no));
		pairs.add(new BasicNameValuePair("password", password));
		pairs.add(new BasicNameValuePair("imei", imei));
		pairs.add(new BasicNameValuePair("imsi", imsi));
		pairs.add(new BasicNameValuePair("appTag", "WYX"));

		String result = mHttpClient.httpRequest(getUrl(URL_SENDDYNAMICCODE), pairs);
		System.out.println("testcode:" + result);
		return result;
	}

	/**
	 * 获取从账号的groupId
	 * 
	 * @param account
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 */
	public String getRegionOrgIdByLoginNo(String account) throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("LOGIN_NO", account));

		// String jsonStr =
		// mHttpClient.httpRequestForInterfacePlatform(URL_GET_REGION_ORG_ID_BY_LOGIN_NO,
		// pairs);
		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(URL_GET_REGION_ORG_ID_BY_LOGIN_NO, pairs);
		try {
			jsonStr = new JSONObject(jsonStr).getString("RETURN");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ResponseException("服务器繁忙，请稍后再试");
		}
		mHttpClient.handleResultCode(jsonStr, "RETURN_CODE", "RETURN_MASSAGE");
		LogUtlis.e(TAG, "jsonStr:" + jsonStr);
		return jsonStr;
	}

	public String getSubAccount(String account, String appTag) throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account", account));
		pairs.add(new BasicNameValuePair("appTag", appTag));
		String jsonStr = mHttpClient.httpRequest(getUrl(URL_GET_ACCOUNT_SUB_ACCOUNT_INFO), pairs);
		LogUtlis.d(TAG, "jsonStr:" + jsonStr);
		return jsonStr;
	}

	/** 渠道搜索 ,根据groupId */
	public String getChannelSearchByGroupId(String routePhoneNumber, String searchStr, String loginNo, String mobGrade, String mobClass, String startNum,
			String endNum) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", routePhoneNumber));
		pairs.add(new BasicNameValuePair("groupName", searchStr));
		pairs.add(new BasicNameValuePair("loginNo", loginNo));
		pairs.add(new BasicNameValuePair("isTude", "false"));
		if (mobGrade != null)
			pairs.add(new BasicNameValuePair("mobGrade", mobGrade));
		if (mobClass != null)
			pairs.add(new BasicNameValuePair("mobClass", mobClass));
		if (startNum != null)
			pairs.add(new BasicNameValuePair("startNum", startNum));
		if (endNum != null)
			pairs.add(new BasicNameValuePair("endNum", endNum));
		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(URL_CHANNEL_SEARCH, pairs);
		mHttpClient.handleResultCode(jsonStr, "retCode", "retMsg");
		// String result = mHttpClient.httpRequest(URL_CHANNEL_SEARCH, pairs);
		return jsonStr;
	}

	/** 渠道搜索 */
	public String getChannelSearchByLatLng(String routePhoneNumber, String groupId, String loginNo, String longiTude, String latiTude, String mobClass)
			throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", routePhoneNumber));
		pairs.add(new BasicNameValuePair("groupId", groupId));
		pairs.add(new BasicNameValuePair("loginNo", loginNo));
		pairs.add(new BasicNameValuePair("isTude", "true"));
		if (longiTude != null)
			pairs.add(new BasicNameValuePair("longiTude", longiTude));
		if (latiTude != null)
			pairs.add(new BasicNameValuePair("latiTude", latiTude));
		if (mobClass != null)
			pairs.add(new BasicNameValuePair("mobClass", mobClass));
		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(URL_CHANNEL_SEARCH, pairs);
		mHttpClient.handleResultCode(jsonStr, "RETCODE", "RETMSG");
		// String result = mHttpClient.httpRequest(URL_CHANNEL_SEARCH, pairs);
		return jsonStr;
	}

	/** 渠道网点查询 */
	public String getModsTerminaLByGroupId(String routePhoneNumber, String groupId, String boss, String startNum, String endNum) throws HttpException,
			ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", routePhoneNumber));
		pairs.add(new BasicNameValuePair("groupId", groupId));
		pairs.add(new BasicNameValuePair("boss", boss));
		pairs.add(new BasicNameValuePair("startNum", startNum));
		pairs.add(new BasicNameValuePair("endNum", endNum));
		String result = mHttpClient.httpRequest(URL_MODE_TERMINA_BY_GROUPID, pairs);

		return result;
	}

	/** 渠道信息查询 */
	public String getGroupMsgInfo(String routePhoneNumber, String groupId, String boss) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", routePhoneNumber));
		pairs.add(new BasicNameValuePair("groupId", groupId));
		pairs.add(new BasicNameValuePair("boss", boss));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_GROUP_MSG_INFO, pairs);
		mHttpClient.handleResultCode(result, "RETCODE", "RETMSG");
		return result;
	}

	/**
	 * 获取指标信息
	 * 
	 * @param groupIds
	 *            要排序的网点，注意单引号和逗号分隔
	 * @param data
	 *            查询日期
	 * @param sortName
	 *            ADD 排序字段 [GROUP_ID 渠道id,G4_TARIFF_ADD 4g资费新增,G4_TERM_SALES
	 *            4g终端销量,BROADBAND_NUMS 宽带发展量,DATA_CYCLE 数据日期]
	 * @param sortType
	 *            1升序， 2降序 其他不排序
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getGroupIndexsort(String groupIds, String date, String sortName, String sortType) throws HttpException, ServerInterfaceException,
			BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("groupIds", groupIds));
		pairs.add(new BasicNameValuePair("Date", date));
		pairs.add(new BasicNameValuePair("sortName", sortName));
		pairs.add(new BasicNameValuePair("sortType", sortType));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_GROUP_INDEX_INDEXSORT, pairs);
		try {
			JSONObject jsonObj = new JSONObject(result);
			result = jsonObj.getString("RETURN");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ResponseException("服务器繁忙，请稍后再试");
		}

		mHttpClient.handleResultCode(result, "RETURN_CODE", "RETURN_MESSAGE");
		return result;
	}

	/**
	 * 签到
	 * 
	 * @param regisTime
	 *            yyyy-M-d h:m:s
	 * @param phoneNo
	 * @param groupId
	 * @param latiTude
	 * @param longiTude
	 * @param longinNo
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String doMobRegiste(String regisTime, String phoneNo, String groupId, String latiTude, String longiTude, String longinNo) throws HttpException,
			ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("regisTime", regisTime));
		pairs.add(new BasicNameValuePair("phoneNo", phoneNo));
		pairs.add(new BasicNameValuePair("groupId", groupId));
		pairs.add(new BasicNameValuePair("longitude", longiTude));
		pairs.add(new BasicNameValuePair("latitude", latiTude));
		pairs.add(new BasicNameValuePair("longinNo", longinNo));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_DO_MOB_REGISTE, pairs);
		mHttpClient.handleResultCode(result, "RETCODE", "RETMSG");
		return result;
	}

	/**
	 * @param groupId
	 * @return 店员积分查询
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getMobMenberScore(String groupId) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("groupId", groupId));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_GET_MOB_MENBER_SCORE, pairs);
		mHttpClient.handleResultCode(result, "RETCODE", "RETMSG");
		return result;
	}

	public String getPhoneNumByIMSI(String imsi, String phoneNo) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phoneNo));
		pairs.add(new BasicNameValuePair("IMSI_NO", imsi));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_GET_PHONENUM_BY_IMSI, pairs);
		String phone = null;
		try {
			JSONObject jsonObj = new JSONObject(result).getJSONObject("RETURN");
			result = jsonObj.toString();
			mHttpClient.handleResultCode(result, "RETURN_CODE", "RETURN_MASSAGE");
			JSONArray jarr = jsonObj.getJSONArray("RETURN_INFO");
			if (jarr.length() > 0)
				phone = jarr.getJSONObject(0).getString("PHONE_NO");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ResponseException("服务器繁忙，请稍后再试");
		}
		return phone;
	}

	/**
	 * 签到轨迹
	 * 
	 * @param phoneNo
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getMobRegeste(String phoneNo, String startTime, String endTime) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("longinNo", phoneNo));
		pairs.add(new BasicNameValuePair("startTime", startTime));
		pairs.add(new BasicNameValuePair("endTime", endTime));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_GET_MOB_REGISTE, pairs);
		mHttpClient.handleResultCode(result, "RETCODE", "RETMSG");
		return result;
	}

	public String getMobPicture(String total_date, String operate_time, String operate_no, String filsaccaptId) throws HttpException, ServerInterfaceException,
			BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("total_date", total_date));
		pairs.add(new BasicNameValuePair("operate_time", operate_time));
		pairs.add(new BasicNameValuePair("operate_no", operate_no));
		pairs.add(new BasicNameValuePair("fileAcceptId", filsaccaptId));

		String result = new String(mHttpClient.httpRequestForBytes(mHttpClient.convertRequestUrlWithoutParam(URL_GET_FILE, pairs, false)));
		mHttpClient.handleResultCode(result, "RETCODE", "RETMSG");
		return result;
	}

	public String getIndexBrothersort(String groupId, String date, String sortName) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("groupId", groupId));
		pairs.add(new BasicNameValuePair("Date", date));
		pairs.add(new BasicNameValuePair("sortName", sortName));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_GET_INDEX_BROTHERSORT, pairs);
		LogUtlis.d(TAG, "jsonStr:" + result);
		try {
			result = new JSONObject(result).getString("RETURN");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ResponseException("服务器繁忙，请稍后再试");
		}
		mHttpClient.handleResultCode(result, "RETURN_CODE", "RETURN_MESSAGE");
		return result;
	}

	/** 根据groupid查询渠道份额接口 */
	public String getAppQuotaByGroupid(String groupId, String startMonth, String endMonth) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("groupId", groupId));
		pairs.add(new BasicNameValuePair("startMonth", startMonth));
		pairs.add(new BasicNameValuePair("endMonth", endMonth));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_GET_APP_QUOTA_BY_GROUPID, pairs);
		LogUtlis.d(TAG, "jsonStr:" + result);
		try {
			result = new JSONObject(result).getString("RETURN");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ResponseException("服务器繁忙，请稍后再试");
		}
		mHttpClient.handleResultCode(result, "RETURN_CODE", "RETURN_MESSAGE");
		return result;
	}

	/** 查询全部地市份额接口 */
	public String getAppQuotaByRegionId(String startMonth, String endMonth) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("cityId", "10008"));
		pairs.add(new BasicNameValuePair("countryId", ""));
		pairs.add(new BasicNameValuePair("startMonth", startMonth));
		pairs.add(new BasicNameValuePair("endMonth", endMonth));
		String result = mHttpClient.httpRequestForInterfacePlatform("http://218.205.252.27:18097/scUnifiedServicePlatform/services/getAppQuotabyRegionid",
				pairs);
		LogUtlis.d(TAG, "jsonStr:" + result);
		try {
			result = new JSONObject(result).getString("RETURN");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ResponseException("服务器繁忙，请稍后再试");
		}
		mHttpClient.handleResultCode(result, "RETURN_CODE", "RETURN_MESSAGE");
		return result;
	}

	/**
	 * 根据经纬度获取周边竞争对手网点
	 * 
	 * @param latiTude
	 * @param longTitude
	 * @param chl_type
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getCompetitorInfo(String latiTude, String longiTude, String chl_type) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("latiTude", latiTude));
		pairs.add(new BasicNameValuePair("longiTude", longiTude));
		if (chl_type != null)
			pairs.add(new BasicNameValuePair("chl_type", chl_type));

		String result = mHttpClient.httpRequestForInterfacePlatform(URL_COMPETITOR_QUERY_URL, pairs);
		try {
			JSONObject jsonObj = new JSONObject(result);
			result = jsonObj.getString("RETURN");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ResponseException("服务器繁忙，请稍后再试");
		}

		mHttpClient.handleResultCode(result, "RETURN_CODE", "RETURN_MESSAGE");
		return result;
	}

	public String getBitmapOfChannel(String imginfo) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("imginfo", imginfo));
		String result = mHttpClient.httpRequestForInterfacePlatform(URL_GET_FILE, pairs);
		mHttpClient.handleResultCode(result, "RETCODE", "RETMSG");
		return result;
	}

	public String getUrlContent(String url) throws HttpException, ServerInterfaceException, BusinessException {
		String jsonStr = new String(mHttpClient.httpRequestForBytes(mHttpClient.convertRequestUrlWithoutParam(url, null, false)));
		return jsonStr;
	}

	public String getUpdateInfos() throws HttpException, ServerInterfaceException, BusinessException {
		String jsonStr = new String(mHttpClient.httpRequestForBytes(mHttpClient.convertRequestUrlWithoutParam(URL_UPDATEINFOS, null, false)));
		// TODO: 获取升级信息
		LogUtlis.i("updateinfos", "updateinfos:" + jsonStr);
		return jsonStr;
	}

	public String getUpdateInfo() throws HttpException, ServerInterfaceException, BusinessException {
		String jsonStr = new String(mHttpClient.httpRequestForBytes(mHttpClient.convertRequestUrlWithoutParam(URL_UPDATEINFO, null, false)));
		// String temp1 = ZipUtils.decodeBase64(jsonStr);
		// String temp2 = ZipUtils.decodeAES(jsonStr);
		String temp3 = ZipUtils.decodeDes(jsonStr);
		// TODO: 获取升级信息
 		LogUtlis.i("updateinfos", "updateinfos:" + temp3);
		return temp3;
	}

	public String getApplicationList(String authentication, String screenw, String screenh, String pageNumber, String pageSize, String appTag)
			throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authentication", authentication));
		pairs.add(new BasicNameValuePair("screenw", screenw));
		pairs.add(new BasicNameValuePair("screenh", screenh));
		pairs.add(new BasicNameValuePair("pageNumber", pageNumber));
		pairs.add(new BasicNameValuePair("pageSize", pageSize));
		if (appTag != null)
			pairs.add(new BasicNameValuePair("appTag", appTag));
		String jsonStr = mHttpClient.httpRequest(getUrl(GET_APPLICATIONS_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:" + jsonStr);
		return jsonStr;
	}

	public String getCptList(String auth, String login_no, String chlName, String pageNumber, String pageSize) throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authenticationID", auth));
		pairs.add(new BasicNameValuePair("login_no", login_no));
		if (!TextUtils.isEmpty(chlName)) {
			pairs.add(new BasicNameValuePair("chlName", chlName));
		}
		pairs.add(new BasicNameValuePair("currentPage", pageNumber));
		pairs.add(new BasicNameValuePair("pageSize", pageSize));
		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(GET_CPT_LIST, pairs);
		LogUtlis.d(TAG, "竞争对手列表:" + jsonStr);
		return jsonStr;
	}

	public String getBaseInfo(String auth, String chlId) throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authenticationID", auth));
		pairs.add(new BasicNameValuePair("chlId", chlId));
		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(GET_BASE_INFO, pairs);
		try {
			jsonStr = new JSONObject(jsonStr).getString("RETURN");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ResponseException("服务器繁忙，请稍后再试");
		}
		mHttpClient.handleResultCode(jsonStr, "RETURN_CODE", "RETURN_MASSAGE");
		return jsonStr;
	}

	public String saveOrUpdateBaseInfo(String json) throws HttpException, BusinessException, JSONException {
		JSONObject jsonObject = new JSONObject(json);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authenticationID", jsonObject.getString("authenticationID")));
		pairs.add(new BasicNameValuePair("chlId", jsonObject.getString("chlId")));
		pairs.add(new BasicNameValuePair("chlName", jsonObject.getString("chlName")));
		pairs.add(new BasicNameValuePair("chlLvl", jsonObject.getString("chlLvl")));
		pairs.add(new BasicNameValuePair("busareaId", jsonObject.getString("busareaId")));
		pairs.add(new BasicNameValuePair("arId", jsonObject.getString("arId")));
		pairs.add(new BasicNameValuePair("localNature", jsonObject.getString("localNature")));
		pairs.add(new BasicNameValuePair("longitude", jsonObject.getString("longitude")));
		pairs.add(new BasicNameValuePair("latitude", jsonObject.getString("latitude")));
		pairs.add(new BasicNameValuePair("radius", jsonObject.getString("radius")));
		pairs.add(new BasicNameValuePair("personNum", jsonObject.getString("personNum")));
		pairs.add(new BasicNameValuePair("personDensity", jsonObject.getString("personDensity")));
		pairs.add(new BasicNameValuePair("rentFee", jsonObject.getString("rentFee")));
		pairs.add(new BasicNameValuePair("empNum", jsonObject.getString("empNum")));
		pairs.add(new BasicNameValuePair("salerNum", jsonObject.getString("salerNum")));
		pairs.add(new BasicNameValuePair("address", jsonObject.getString("address")));
		pairs.add(new BasicNameValuePair("isNew", jsonObject.getString("isNew")));
		pairs.add(new BasicNameValuePair("isLost", jsonObject.getString("isLost")));
		pairs.add(new BasicNameValuePair("mktCenterType", jsonObject.getString("mktCenterType")));
		pairs.add(new BasicNameValuePair("mktCenterId", jsonObject.getString("mktCenterId")));
		pairs.add(new BasicNameValuePair("login_no", jsonObject.getString("login_no")));
		pairs.add(new BasicNameValuePair("chlType", jsonObject.getString("chlType")));

		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(SAVE_BASE_INFO, pairs);
		LogUtlis.d(TAG, "竞争对手基本信息:" + jsonStr);
		return jsonStr;
	}

	public String deleteBaseInfo(String auth, String login_no, String chlId) throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authenticationID", auth));
		pairs.add(new BasicNameValuePair("login_no", login_no));
		pairs.add(new BasicNameValuePair("chlId", chlId));
		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(DEL_BASE_INFO, pairs);
		LogUtlis.d(TAG, "竞争对手基本信息:" + jsonStr);
		return jsonStr;
	}

	public String getActInfo(String auth, String chlId) throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authenticationID", auth));
		pairs.add(new BasicNameValuePair("chlId", chlId));
		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(GET_ACTIVITY_INFO, pairs);
		LogUtlis.d(TAG, "竞争对手活动基本信息:" + jsonStr);
		return jsonStr;
	}

	public String saveOrUpdateActInfo(String json) throws HttpException, BusinessException, JSONException {
		JSONObject jsonObject = new JSONObject(json);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authenticationID", jsonObject.getString("authenticationID")));
		pairs.add(new BasicNameValuePair("chlId", jsonObject.getString("chlId")));
		pairs.add(new BasicNameValuePair("actId", jsonObject.getString("actId")));
		pairs.add(new BasicNameValuePair("opTime", jsonObject.getString("opTime")));
		pairs.add(new BasicNameValuePair("actionName", jsonObject.getString("actionName")));
		pairs.add(new BasicNameValuePair("actionDesc", jsonObject.getString("actionDesc")));
		pairs.add(new BasicNameValuePair("actionLvl", jsonObject.getString("actionLvl")));
		pairs.add(new BasicNameValuePair("busarea", jsonObject.getString("busarea")));
		pairs.add(new BasicNameValuePair("effDate", jsonObject.getString("effDate")));
		pairs.add(new BasicNameValuePair("expDate", jsonObject.getString("expDate")));
		pairs.add(new BasicNameValuePair("actionType", jsonObject.getString("actionType")));
		pairs.add(new BasicNameValuePair("subsidyFee", jsonObject.getString("subsidyFee")));
		pairs.add(new BasicNameValuePair("login_no", jsonObject.getString("login_no")));

		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(SAVE_ACT_INFO, pairs);
		LogUtlis.d(TAG, "竞争对手基本信息:" + jsonStr);
		return jsonStr;
	}

	public String deleteActInfo(String auth, String login_no, String actId) throws HttpException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authenticationID", auth));
		pairs.add(new BasicNameValuePair("login_no", login_no));
		pairs.add(new BasicNameValuePair("actId", actId));
		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(DEL_ACT_INFO, pairs);
		LogUtlis.d(TAG, "竞争对手基本信息:" + jsonStr);
		return jsonStr;
	}

	/**
	 * 根据工号查询酬金
	 * 
	 * @param auth
	 * @param login_no
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String getRewardDetail(String json) throws HttpException, BusinessException, JSONException {
		JSONObject jsonObject = new JSONObject(json);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("yearMonth", jsonObject.getString("yearMonth")));
		pairs.add(new BasicNameValuePair("OPERATE_NO", jsonObject.getString("OPERATE_NO")));
		pairs.add(new BasicNameValuePair("loginNo", jsonObject.getString("loginNo")));
		pairs.add(new BasicNameValuePair("groupId", jsonObject.optString("groupId")));
		pairs.add(new BasicNameValuePair("rwdCode", jsonObject.getString("rwdCode")));
		pairs.add(new BasicNameValuePair("authenticationID", jsonObject.getString("authenticationID")));
		pairs.add(new BasicNameValuePair("authentication", jsonObject.getString("authentication")));
		pairs.add(new BasicNameValuePair("businessID", jsonObject.getString("businessID")));
		pairs.add(new BasicNameValuePair("businessId", jsonObject.getString("businessId")));
		pairs.add(new BasicNameValuePair("appTag", jsonObject.getString("appTag")));
		pairs.add(new BasicNameValuePair("loadOvertTime", jsonObject.getString("loadOvertTime")));
		pairs.add(new BasicNameValuePair("loadStartTime", jsonObject.getString("loadStartTime")));
		pairs.add(new BasicNameValuePair("session_phone", jsonObject.getString("session_phone")));
		pairs.add(new BasicNameValuePair("deviceImei", jsonObject.getString("deviceImei")));
		pairs.add(new BasicNameValuePair("submitTime", jsonObject.getString("submitTime")));
		pairs.add(new BasicNameValuePair("subAccount", jsonObject.getString("subAccount")));

		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(GET_REWARD_DETAIL, pairs);
		LogUtlis.d(TAG, "获取工号信息:" + jsonStr);
		return jsonStr;
	}

	/**
	 * 获取渠道树
	 * 
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String getChannelTree(String json) throws HttpException, BusinessException, JSONException {
		JSONObject jsonObject = new JSONObject(json);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("OPERATE_NO", jsonObject.getString("OPERATE_NO")));
		pairs.add(new BasicNameValuePair("LOGIN_NO", jsonObject.getString("LOGIN_NO")));
		pairs.add(new BasicNameValuePair("START_NUM", jsonObject.getString("START_NUM")));
		pairs.add(new BasicNameValuePair("END_NUM", jsonObject.getString("END_NUM")));
		pairs.add(new BasicNameValuePair("REGION_GROUP", jsonObject.getString("REGION_GROUP")));
		pairs.add(new BasicNameValuePair("authenticationID", jsonObject.getString("authenticationID")));
		pairs.add(new BasicNameValuePair("authentication", jsonObject.getString("authentication")));
		pairs.add(new BasicNameValuePair("businessID", jsonObject.getString("businessID")));
		pairs.add(new BasicNameValuePair("businessId", jsonObject.getString("businessId")));
		pairs.add(new BasicNameValuePair("appTag", jsonObject.getString("appTag")));
		pairs.add(new BasicNameValuePair("loadOvertTime", jsonObject.getString("loadOvertTime")));
		pairs.add(new BasicNameValuePair("loadStartTime", jsonObject.getString("loadStartTime")));
		pairs.add(new BasicNameValuePair("session_phone", jsonObject.getString("session_phone")));
		pairs.add(new BasicNameValuePair("deviceImei", jsonObject.getString("deviceImei")));
		pairs.add(new BasicNameValuePair("submitTime", jsonObject.getString("submitTime")));
		pairs.add(new BasicNameValuePair("subAccount", jsonObject.getString("subAccount")));

		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(GET_CHANNEL_TREE, pairs);
		LogUtlis.d(TAG, "获取渠道信息:" + jsonStr);
		return jsonStr;
	}

	/**
	 * 获取渠道列表
	 * 
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String getChannelList(String json) throws HttpException, BusinessException, JSONException {
		JSONObject jsonObject = new JSONObject(json);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("groupName", jsonObject.getString("groupName")));
		pairs.add(new BasicNameValuePair("PhoneNo", jsonObject.getString("PhoneNo")));
		pairs.add(new BasicNameValuePair("authenticationID", jsonObject.getString("authenticationID")));
		pairs.add(new BasicNameValuePair("authentication", jsonObject.getString("authentication")));
		pairs.add(new BasicNameValuePair("businessID", jsonObject.getString("businessID")));
		pairs.add(new BasicNameValuePair("businessId", jsonObject.getString("businessId")));
		pairs.add(new BasicNameValuePair("appTag", jsonObject.getString("appTag")));
		pairs.add(new BasicNameValuePair("loadOvertTime", jsonObject.getString("loadOvertTime")));
		pairs.add(new BasicNameValuePair("loadStartTime", jsonObject.getString("loadStartTime")));
		pairs.add(new BasicNameValuePair("session_phone", jsonObject.getString("session_phone")));
		pairs.add(new BasicNameValuePair("deviceImei", jsonObject.getString("deviceImei")));
		pairs.add(new BasicNameValuePair("submitTime", jsonObject.getString("submitTime")));
		pairs.add(new BasicNameValuePair("subAccount", jsonObject.getString("subAccount")));

		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(GET_CHANNEL_LIST, pairs);
		LogUtlis.d(TAG, "获取渠道信息:" + jsonStr);
		return jsonStr;
	}

	/**
	 * 获取酬金树
	 * 
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String getRewardSubjectTree(String json) throws HttpException, BusinessException, JSONException {
		JSONObject jsonObject = new JSONObject(json);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("TOTAL_DATE", jsonObject.getString("TOTAL_DATE")));
		pairs.add(new BasicNameValuePair("OPERATE_NO", jsonObject.getString("OPERATE_NO")));
		pairs.add(new BasicNameValuePair("OPERATE_TIME", jsonObject.getString("OPERATE_TIME")));
		pairs.add(new BasicNameValuePair("MOB_PARENT_CLASS_CODE", jsonObject.getString("MOB_PARENT_CLASS_CODE")));
		pairs.add(new BasicNameValuePair("authenticationID", jsonObject.getString("authenticationID")));
		pairs.add(new BasicNameValuePair("authentication", jsonObject.getString("authentication")));
		pairs.add(new BasicNameValuePair("businessID", jsonObject.getString("businessID")));
		pairs.add(new BasicNameValuePair("businessId", jsonObject.getString("businessId")));
		pairs.add(new BasicNameValuePair("appTag", jsonObject.getString("appTag")));
		pairs.add(new BasicNameValuePair("loadOvertTime", jsonObject.getString("loadOvertTime")));
		pairs.add(new BasicNameValuePair("loadStartTime", jsonObject.getString("loadStartTime")));
		pairs.add(new BasicNameValuePair("session_phone", jsonObject.getString("session_phone")));
		pairs.add(new BasicNameValuePair("deviceImei", jsonObject.getString("deviceImei")));
		pairs.add(new BasicNameValuePair("submitTime", jsonObject.getString("submitTime")));
		pairs.add(new BasicNameValuePair("subAccount", jsonObject.getString("subAccount")));

		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(GET_REWARD_TREE, pairs);
		LogUtlis.d(TAG, "获取渠道信息:" + jsonStr);
		return jsonStr;
	}

	/**
	 * 酬金反查明细
	 * 
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String getRewardInverseDetail(String json) throws HttpException, BusinessException, JSONException {
		JSONObject jsonObject = new JSONObject(json);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("TOTAL_DATE", jsonObject.getString("TOTAL_DATE")));
		pairs.add(new BasicNameValuePair("FUNCTION_CODE", jsonObject.getString("FUNCTION_CODE")));
		pairs.add(new BasicNameValuePair("SYS_FLAG", jsonObject.getString("SYS_FLAG")));
		pairs.add(new BasicNameValuePair("OPERATE_NO", jsonObject.getString("OPERATE_NO")));
		pairs.add(new BasicNameValuePair("OPERATE_TIME", jsonObject.getString("OPERATE_TIME")));
		pairs.add(new BasicNameValuePair("OPERATE_NOTE", jsonObject.getString("OPERATE_NOTE")));
		pairs.add(new BasicNameValuePair("rewardQryType", jsonObject.getString("rewardQryType")));
		pairs.add(new BasicNameValuePair("MOB_FIRST_CLASS_CODE", jsonObject.getString("MOB_FIRST_CLASS_CODE")));
		pairs.add(new BasicNameValuePair("MOB_CLASS_CODE", jsonObject.getString("MOB_CLASS_CODE")));
		pairs.add(new BasicNameValuePair("GROUP_ID", jsonObject.getString("GROUP_ID")));
		pairs.add(new BasicNameValuePair("YEAR_MONTH", jsonObject.getString("YEAR_MONTH")));
		pairs.add(new BasicNameValuePair("LOGIN_NO", jsonObject.getString("LOGIN_NO")));
		pairs.add(new BasicNameValuePair("SERVICE_NO", jsonObject.getString("SERVICE_NO")));
		pairs.add(new BasicNameValuePair("SERVICE_TYPE", jsonObject.getString("SERVICE_TYPE")));
		pairs.add(new BasicNameValuePair("START_NUM", jsonObject.getString("START_NUM")));
		pairs.add(new BasicNameValuePair("END_NUM", jsonObject.getString("END_NUM")));
		pairs.add(new BasicNameValuePair("authenticationID", jsonObject.getString("authenticationID")));
		pairs.add(new BasicNameValuePair("authentication", jsonObject.getString("authentication")));
		pairs.add(new BasicNameValuePair("businessID", jsonObject.getString("businessID")));
		pairs.add(new BasicNameValuePair("businessId", jsonObject.getString("businessId")));
		pairs.add(new BasicNameValuePair("appTag", jsonObject.getString("appTag")));
		pairs.add(new BasicNameValuePair("loadOvertTime", jsonObject.getString("loadOvertTime")));
		pairs.add(new BasicNameValuePair("loadStartTime", jsonObject.getString("loadStartTime")));
		pairs.add(new BasicNameValuePair("session_phone", jsonObject.getString("session_phone")));
		pairs.add(new BasicNameValuePair("deviceImei", jsonObject.getString("deviceImei")));
		pairs.add(new BasicNameValuePair("submitTime", jsonObject.getString("submitTime")));
		pairs.add(new BasicNameValuePair("subAccount", jsonObject.getString("subAccount")));

		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(GET_REWARD_INVERSE_DETAIL, pairs);
		LogUtlis.d(TAG, "获取渠道信息:" + jsonStr);
		return jsonStr;
	}

	/**
	 * 上传图片
	 * 
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 * @throws ServerInterfaceException
	 */
	public String uploadInfoImg(String infoFileName) throws HttpException, BusinessException, ServerInterfaceException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("infoFileName", infoFileName));
		String jsonStr = mHttpClient.httpRequestStrPost(UPLOAD_IMAGE, pairs); // httpRequestForInterfacePlatform(UPLOAD_IMAGE,
																				// pairs);
		LogUtlis.d(TAG, "竞争对手基本信息:" + jsonStr);
		return jsonStr;
	}

	/**
	 * 上传图片信息
	 * 
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 * @throws ServerInterfaceException
	 * @throws JSONException
	 */
	public String saveOrUpdatePictureInfo(String json) throws HttpException, BusinessException, ServerInterfaceException, JSONException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		JSONObject jsonObject = new JSONObject(json);
		pairs.add(new BasicNameValuePair("chlId", jsonObject.getString("chlId")));
		pairs.add(new BasicNameValuePair("imgId", jsonObject.getString("imgId")));
		pairs.add(new BasicNameValuePair("outdoorPic", jsonObject.getString("outdoorPic")));
		pairs.add(new BasicNameValuePair("indoorPic", jsonObject.getString("indoorPic")));
		pairs.add(new BasicNameValuePair("houseNumPic", jsonObject.getString("houseNumPic")));

		String jsonStr = mHttpClient.httpRequestForInterfacePlatform(SAVE_OR_UPDATE_IMAGE, pairs); // httpRequestForInterfacePlatform(UPLOAD_IMAGE,
																									// pairs);
		LogUtlis.d(TAG, "竞争对手基本信息:" + jsonStr);
		return jsonStr;
	}

}
