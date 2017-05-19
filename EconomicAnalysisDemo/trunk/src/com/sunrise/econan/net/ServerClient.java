package com.sunrise.econan.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sunrise.econan.net.http.HttpClient;
import com.sunrise.econan.utils.LogUtlis;
import com.sunrise.econan.utils.ZipAndBaseUtils;
import com.sunrise.econan.exception.http.HttpException;
import com.sunrise.econan.exception.http.ResponseException;
import com.sunrise.econan.exception.logic.BusinessException;
import com.sunrise.econan.exception.logic.ServerInterfaceException;

public class ServerClient {
	private static final String TAG = "ServerClient";
	private static final boolean isDecode = true;
	private static final int TEST_MAX_TRAFFIC = 200;

	// public static final String SERVER_IP = "61.235.80.178";// 测试
	// private static final String PORT = ":8008";

	public static final String SERVER_IP = "218.205.252.26";// 生产
	private static final String BUSINESS_PORT = ":18098";
	private static final String PORT = ":8097";
	// private static final String BUSINESS_PORT = ":8094"; // 测试
	// private static final String PORT = ":8094"; // 测试
	private static final String HTTP = "http://";
	private static final String HTTP_SPLIT = "/";
	private static final String RECOMMOND_DOWNLOAD_URL = "http://183.221.33.188:8091/scmbhm/sv/ri.action?";
	private static final String URL_LOGIN_JF = "scUnifiedAppManagePlatform/unifiedAppInterface/login.action";//jf登录
	private static final String URL_CHECKLOGIN = "scUnifiedAppManagePlatform/unifiedAppInterface/sendDynamicCodeJF.action";//jf获取验证码
	private static final String URL_SENDDYNAMICCODE = "scUnifiedAppManagePlatform/unifiedAppInterface/sendDynamicCode.action";//4a获取验证码
	private static final String URL_LOGIN_4A = "scUnifiedAppManagePlatform/unifiedAppInterface/login.action";//4a登录
	private static final String GET_ACCOUNT_SUB_ACCOUNT_INFO_URL = "scUnifiedAppManagePlatform/businessHandlingService/getAccountSubAccountInfo.action";//获取从账号
	private static final String URL_UPDATE_INFO = "scUnifiedAppManagePlatform/ssjf/updateInfo.json";//配置文件路径

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
		pairs.add(new BasicNameValuePair("appTag", "SSJF"));

		// String result = visitBusiness(getBusinessUrl(URL_CHECKLOGIN), pairs);
		String result = visitBusiness(getBusinessUrl(URL_SENDDYNAMICCODE), pairs);

		return result;
	}

	public String login(String phone_no, String checkCode) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("userName", phone_no));
		pairs.add(new BasicNameValuePair("dynamicCode", checkCode));
		pairs.add(new BasicNameValuePair("appTag", "SSJF"));
		String result = visitBusiness(getBusinessUrl(URL_LOGIN_4A), pairs);
		return result;
	}

	public String getUpdateInfos() throws HttpException, ServerInterfaceException, BusinessException {
		String url = getUrl(URL_UPDATE_INFO);
		String jsonStr = requestJson(url, null);
		// TODO: 获取升级信息
		LogUtlis.i("updateinfos", "updateinfos:" + jsonStr);

		return jsonStr;
	}
	
	public String getSubAccount(String account,String appTag) throws HttpException, BusinessException, ServerInterfaceException{
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account",account));
		pairs.add(new BasicNameValuePair("appTag",appTag));
		String jsonStr=visitBusiness(getBusinessUrl(GET_ACCOUNT_SUB_ACCOUNT_INFO_URL), pairs);
		Log.d(TAG, "jsonStr:"+jsonStr);
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
			decodeJsonStr = ZipAndBaseUtils.decodeBase64(str);
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
		String decodeJsonStr = ZipAndBaseUtils.decodeBase64(str);// DesCrypUtil.decompressAndDecodeBase64(str,
																	// Base64.PREFERRED_ENCODING);
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
		String jsonStr = visitBusiness("http://218.205.252.26:18098/scUnifiedAppManagePlatform/unifiedAppInterface/getMenuDetail.action", pairs);
		Log.d(TAG, "jsonStr:" + jsonStr);
		return jsonStr;
	}
}
