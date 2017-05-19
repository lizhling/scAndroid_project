package com.sunrise.micromarketing.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.aes.AES;
import com.sunrise.micromarketing.net.http.HttpClient;
import com.sunrise.micromarketing.utils.LogUtlis;
import com.sunrise.micromarketing.utils.ZipAndBaseUtils;
import com.sunrise.micromarketing.entity.BusinessMenu;
import com.sunrise.micromarketing.exception.http.HttpException;
import com.sunrise.micromarketing.exception.http.ResponseException;
import com.sunrise.micromarketing.exception.logic.BusinessException;
import com.sunrise.micromarketing.exception.logic.ServerInterfaceException;

public class ServerClient {
	private static final String TAG = "ServerClient";
	private static final boolean isDecode = true;
	private static final int TEST_MAX_TRAFFIC = 200;

	// public static final String SERVER_IP = "61.235.80.178";// 测试
	// private static final String PORT = ":8008";

	public static final String SERVER_IP = "218.205.252.27";// 生产
	private static final String BUSINESS_PORT = ":18097";
	private static final String PORT = ":18097";
	// private static final String BUSINESS_PORT = ":8094"; // 测试
	// private static final String PORT = ":8094"; // 测试
	private static final String HTTP = "https://";
	private static final String HTTP_SPLIT = "/";
	// private static final String RECOMMOND_DOWNLOAD_URL =
	// "http://183.221.33.188:8091/scmbhm/sv/ri.action?";
	// private static final String URL_LOGIN_JF =
	// "scUnifiedAppManagePlatform/unifiedAppInterface/login.action";//jf登录
	// private static final String URL_CHECKLOGIN =
	// "scUnifiedAppManagePlatform/unifiedAppInterface/sendDynamicCodeJF.action";//jf获取验证码
	private static final String URL_SENDDYNAMICCODE = "https://218.205.252.26:28081/scWyxAppManagePlatform/unifiedAppInterface/sendDynamicCode.action";// 4a获取验证码
	private static final String URL_LOGIN_4A = "https://218.205.252.26:28081/scWyxAppManagePlatform/unifiedAppInterface/login.action";// 4a登录
	private static final String URL_GET_ACCOUNT_SUB_ACCOUNT_INFO = "https://218.205.252.26:28081/scWyxAppManagePlatform/businessHandlingService/getAccountSubAccountInfo.action";// 获取从账号
	private static final String URL_UPDATEINFOS = "https://218.205.252.26:28081/scWyxAppManagePlatform/json/updateInfos.json";// 升级配置信息
	private static final String URL_BUSINESS_MENUS = "https://218.205.252.26:28081/scWyxAppManagePlatform/json/businessMenu.json";
	private static final String URL_UPDATEINFO = "https://218.205.252.26:28081/scWyxAppManagePlatform/json/updateInfo.json";
	private static final String URL_BUSINESS_DEAL = "http://218.205.252.27:18097/scUnifiedServicePlatform/services/";// 升级配置信息

	/**
	 * 发送短信验证码
	 */
	private static final String URL_BUSINESS_SEND_DYNAMIC_CODE = "https://218.205.252.26:28081/scWyxAppManagePlatform/businessHandlingService/sendCustomerValidateCode.action";
	/**
	 * 验证短信验证码
	 */
	private static final String URL_CHECD_DYNAMIC_CODE = "https://218.205.252.26:28081/scWyxAppManagePlatform/businessHandlingService/checkCustomerValidateCode.action";

	private static final String URL_GET_BUSINESS_MENU_DATA = "https://218.205.252.26:28081/scWyxAppManagePlatform/businessHandlingService/shareBusinessMenuData.action";

	private HttpClient mHttpClient;
	private static ServerClient Instance;

	private ServerClient() {
		mHttpClient = HttpClient.getInstance();
	}

	public static ServerClient getInstance() {
		if (Instance == null) {
			Instance = new ServerClient();
		}
		return Instance;
	}

	private String getBusinessUrl(String relUrl) {
		String absUrl = HTTP + SERVER_IP + BUSINESS_PORT + HTTP_SPLIT + relUrl;
		return absUrl;
	}

	private String getUrl(String relUrl) {
		String absUrl = HTTP + SERVER_IP + PORT + HTTP_SPLIT + relUrl;
		return absUrl;
	}

	/**
	 * 登录
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param password
	 *            密码
	 * @param imsi
	 * @param imei
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String checklogin(String phone_no, String password, String imei, String imsi) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account", phone_no));
		pairs.add(new BasicNameValuePair("password", password));
		pairs.add(new BasicNameValuePair("imei", imei));
		pairs.add(new BasicNameValuePair("imsi", imsi));
		// pairs.add(new BasicNameValuePair("appTag", "WYX"));

		// String result = visitBusiness(getBusinessUrl(URL_CHECKLOGIN), pairs);
		String result = visitBusiness(URL_SENDDYNAMICCODE, pairs);
		System.out.println("testcode:"+result);

		return result;
	}

	public String login(String phone_no, String checkCode) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("userName", phone_no));
		pairs.add(new BasicNameValuePair("dynamicCode", checkCode));
		pairs.add(new BasicNameValuePair("appTag", "WYX"));
		String result = visitBusiness(URL_LOGIN_4A, pairs);
		return result;
	}

	/**
	 * 发送短信验证码
	 * 
	 * @param phone_no
	 * @param businessId
	 * @param authenticationID
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String sendCustomerValidateCode(String phone_no, String businessId, String authenticationID) throws HttpException, ServerInterfaceException,
			BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("phoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("businessId", businessId));
		pairs.add(new BasicNameValuePair("authenticationID", authenticationID));
		String result = visitBusiness(URL_BUSINESS_SEND_DYNAMIC_CODE, pairs);
		return result;
	}

	/**
	 * 验证短信验证码
	 * 
	 * @param phone_no
	 * @param checkCode
	 * @param businessId
	 * @param authenticationID
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String checkCustomerValidateCode(String phone_no, String checkCode, String businessId, String authenticationID) throws HttpException,
			ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("phoneNumber", phone_no));
		// pairs.add(new BasicNameValuePair("validationCode", businessId));
		// pairs.add(new BasicNameValuePair("businessId", authenticationID));
		pairs.add(new BasicNameValuePair("validationCode", checkCode));
		// phoneNumber=15208394361&validationCode=0531
		String result = visitBusiness(URL_CHECD_DYNAMIC_CODE, pairs);
		return result;
	}

	public String getBusinessMenuData(String businessId, String subAccount) throws HttpException, ServerInterfaceException,
			BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("businessId", businessId));
		pairs.add(new BasicNameValuePair("bossNo", subAccount));
		String result = visitBusiness(URL_GET_BUSINESS_MENU_DATA, pairs);
		return result;
	}

	public String getUpdateInfos() throws HttpException, ServerInterfaceException, BusinessException {
		String jsonStr = mHttpClient.httpRequestStr(URL_UPDATEINFOS, null);
		// TODO: 获取升级信息
		LogUtlis.i("updateinfos", "updateinfos:" + jsonStr);
		return jsonStr;
	}

	public String getAllBusinessMenus() throws HttpException, ServerInterfaceException, BusinessException {
		String jsonStr = mHttpClient.httpRequestStr(URL_BUSINESS_MENUS, null);
		// TODO: 全量菜单信息
		LogUtlis.i("updateinfos", "updateinfos:" + jsonStr);
		return jsonStr;
	}

	public String getUpdateInfo() throws HttpException, ServerInterfaceException, BusinessException {
		String jsonStr = mHttpClient.httpRequestStr(URL_UPDATEINFO, null);
		// TODO: 获取配置信息
		LogUtlis.i("updateinfos", "updateinfos:" + jsonStr);

		return jsonStr;
	}

	/**
	 * 从服务器获取新的菜单列表
	 * 
	 * @param url
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public ArrayList<BusinessMenu> getAllMenus(String url) throws HttpException, ServerInterfaceException, BusinessException {
		ArrayList<BusinessMenu> allMenus = new ArrayList<BusinessMenu>();
		String jsonStr = requestJson(url, null);
		try {
			JSONArray jsonarray = new JSONObject(jsonStr).getJSONArray("datas");
			for (int i = 0; i < jsonarray.length(); ++i)
				allMenus.add(JsonUtils.parseJsonStrToObject(jsonarray.getString(i), BusinessMenu.class));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return allMenus;
	}

	public String getUrlContent(String url) throws HttpException, ServerInterfaceException, BusinessException {
		String jsonStr = requestJson(url, null);
		return jsonStr;
	}

	public String getSubAccount(String account, String appTag) throws HttpException, BusinessException, ServerInterfaceException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account", account));
		pairs.add(new BasicNameValuePair("appTag", appTag));
		String jsonStr = visitBusiness(URL_GET_ACCOUNT_SUB_ACCOUNT_INFO, pairs);
		Log.d(TAG, "jsonStr:" + jsonStr);
		return jsonStr;
	}

	public String handleBusiness(String service, String data, String phone_no, long id, String token) throws HttpException, ServerInterfaceException,
			BusinessException {
		List<NameValuePair> pairs = analysisParamsData(data);
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("businessid", String.valueOf(id)));
		pairs.add(new BasicNameValuePair("token", token));
		if (service.indexOf('/') != -1)
			service = service.substring(service.lastIndexOf('/'));
		if (!service.endsWith("_wyx"))
			service += "_wyx";
		String jsonStr = visitBusinessWithoutKey(URL_BUSINESS_DEAL + service, pairs);
		LogUtlis.d(TAG, "handleBusiness:" + jsonStr);
		return jsonStr;
	}

	/**
	 * Handle business result code
	 * 
	 * @throws BusinessException
	 */
	private void handleBusinessResultCode(String jsonStr) throws HttpException, ServerInterfaceException, BusinessException {
		JSONObject jsonObject = null;
		int businessResultCode = 0;
		/*
		 * if (App.debug != 1) { jsonStr =
		 * "{\"RETURN\":{\"RETURN_CODE\":\"1\",\"RETURN_MESSAGE\":\"登录令牌验证失败，请重新登录！\",\"RETURN_INFO\":[{\"DETAIL_MESSAGE\":\"登录令牌验证失败，请重新登录！\"}]}}"
		 * ; }
		 */
		try {
			jsonObject = new JSONObject(jsonStr);
			if (jsonObject.has("resultCode")) {
				businessResultCode = jsonObject.getInt("resultCode");

				if (businessResultCode != ServerInterfaceException.BUSINESS_OK) {
					String businessResultMesage = jsonObject.getString("resultMessage");
					if (businessResultCode == 1) {// &&
													// businessResultMesage.contains("令牌"))
													// {
						throw new ServerInterfaceException(businessResultMesage, ServerInterfaceException.BUSINESS_AUTHENTICATION_TIME_OUT);
					} else {
						throw new BusinessException(businessResultMesage, businessResultCode);
					}
				} else if (jsonObject.has("CODE")) {
					businessResultCode = parse2Integer(jsonObject.getString("CODE"), -1);
					if (businessResultCode != ServerInterfaceException.BUSINESS_OK) {
						String businessResultMesage = jsonObject.getString("MSG");
						throw new BusinessException(businessResultMesage, businessResultCode);
					}
				} else if (jsonObject.has("resultCode")) {
					businessResultCode = parse2Integer(jsonObject.getString("resultCode"), -1);
					LogUtlis.d(TAG, "" + jsonObject.toString());
					if (businessResultCode != ServerInterfaceException.BUSINESS_OK) {
						String businessResultMesage = jsonObject.getString("resultMessage");
						throw new ServerInterfaceException(businessResultMesage, businessResultCode);
					}

				}
			}
		} catch (JSONException e) {
			throw new ResponseException("抱歉，服务器压力过大 稍后请重试……", businessResultCode);
		}
	}

	private String requestJson(String url, List<NameValuePair> pairs) throws HttpException, ServerInterfaceException, BusinessException {
		String str = mHttpClient.httpRequestStr(url, pairs);
		String decodeJsonStr = str;
		LogUtlis.i(TAG, str);
		if (isDecode)
			decodeJsonStr = ZipAndBaseUtils.decodeDes(str);
		handleBusinessResultCode(decodeJsonStr);
		LogUtlis.d(TAG, "decodeJsonStr:" + decodeJsonStr);
		return decodeJsonStr;
	}

	private String requestStr(String url, List<NameValuePair> pairs) throws HttpException, ServerInterfaceException, BusinessException {
		String str = mHttpClient.httpRequestStr(url, pairs);
		LogUtlis.i(TAG, str);
		if (isDecode)
			str = ZipAndBaseUtils.decodeBase64(str);
		LogUtlis.d(TAG, "decodeJsonStr:" + str);
		return str;
	}

	private String visitBusiness(String url, List<NameValuePair> pairs) throws HttpException, ServerInterfaceException, BusinessException {
		String str = mHttpClient.httpRequestStr(url, pairs);
		LogUtlis.i("login", "loginurl" + str);
		String decodeJsonStr = ZipAndBaseUtils.decodeBase64(str);
		LogUtlis.d(TAG, "visitBusiness:" + decodeJsonStr);
		handleBusinessResultCode(decodeJsonStr);
		return decodeJsonStr;
	}
	
	private String visitBusinessWithoutKey(String url, List<NameValuePair> pairs) throws HttpException, ServerInterfaceException, BusinessException {
		String str = mHttpClient.httpRequestStrWithoutKey(url, pairs);
		LogUtlis.i("login", "loginurl" + str);
		String decodeJsonStr = ZipAndBaseUtils.decodeBase64(str);
		LogUtlis.d(TAG, "visitBusiness:" + decodeJsonStr);
		handleBusinessResultCode(decodeJsonStr);
		return decodeJsonStr;
	}

	/**
	 * 转换为数字，转换失败，为默认数
	 * 
	 * @param str
	 * @param defaultInt
	 * @return
	 */
	public static int parse2Integer(String str, int defaultInt) {
		try {
			defaultInt = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return defaultInt;
	}

	public String getMenuDetail(String appTag, String menuId, String screenw, String screenh) throws HttpException, BusinessException, ServerInterfaceException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("appTag", appTag));
		pairs.add(new BasicNameValuePair("menuId", menuId));
		pairs.add(new BasicNameValuePair("screenw", screenw));
		pairs.add(new BasicNameValuePair("screenh", screenh));
		String jsonStr = visitBusiness("https://218.205.252.26:18098/scUnifiedAppManagePlatform/unifiedAppInterface/getMenuDetail.action", pairs);
		Log.d(TAG, "jsonStr:" + jsonStr);
		return jsonStr;
	}

	private List<NameValuePair> analysisParamsData(String data) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (TextUtils.isEmpty(data))
			return pairs;

		final String temp = data.trim();
		if (TextUtils.isEmpty(temp))
			return pairs;

		for (int start = 0, end = 0; start != -1; start = end + 1) {

			end = temp.indexOf('=', start);
			if (end == -1)
				break;

			String key = temp.substring(start, end);
			start = end + 1;
			end = temp.indexOf('&', start);
			if (end == -1) {
				String value = temp.substring(start);
				pairs.add(new BasicNameValuePair(key, value));
				break;
			} else {
				String value = temp.substring(start, end);
				pairs.add(new BasicNameValuePair(key, value));
			}
		}

		return pairs;
	}
}
