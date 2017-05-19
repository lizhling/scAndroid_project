package com.sunrise.scmbhc.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppDirConstant;
import com.sunrise.scmbhc.entity.AllMenus;
import com.sunrise.scmbhc.entity.CommonlySearchs;
import com.sunrise.scmbhc.entity.CommonlyUsedBusinessMenus;
import com.sunrise.scmbhc.entity.ContentInfo;
import com.sunrise.scmbhc.entity.ContentInfos;
import com.sunrise.scmbhc.entity.MobileBusinessHall;
import com.sunrise.scmbhc.entity.PreferentialInfos;
import com.sunrise.scmbhc.entity.ReservationNumberReslut;
import com.sunrise.scmbhc.entity.bean.MessageFeedbackBean;
import com.sunrise.scmbhc.entity.bean.ReadedBean;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.http.ResponseException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;
import com.sunrise.scmbhc.net.http.HttpClient;
import com.sunrise.scmbhc.net.http.utils.UrlUtils;
import com.sunrise.scmbhc.utils.Base64;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.utils.LogUtlis;
import com.sunrise.scmbhc.utils.ZipAndBaseUtils;

public class ServerClient {
	private static final String TAG = "ServerClient";
	private static final boolean isDecode = true;
	private static final int TEST_MAX_TRAFFIC = 200;

	// public static final String SERVER_IP = "61.235.80.178";// 测试
	// private static final String PORT = ":8008";

	public static final String SERVER_IP = "183.221.33.188";// 生产
	private static final String BUSINESS_PORT = ":8092";
	private static final String PORT = ":8093";
	// private static final String BUSINESS_PORT = ":8094"; // 测试
	// private static final String PORT = ":8094"; // 测试
	private static final String HTTP = "http://";
	private static final String HTTP_SPLIT = "/";
	private static final String UPDATEINFOBYOSINFO_URL = "json/updateInfo.json";// "json/updateInfos.json";
	private static final String PREFERENTIALINFOS_URL = "json/preferentialInfo.json";
	private static final String OFFEN_USESEARCH_URL = "json/searchTags.json";
	private static final String SUBMIT_SEARCH_TAGS_URL = "scmbhm/MbhAppInterfaceAction/submitSearchRecord.action";

	private static final String LOGIN_BY_SYS_PASSWORD_URL = "scmbhi/services/swwwLogin";// 登录
	private static final String LOGIN_BY_RAMDSMS_PASSWORD_URL = "scmbhi/services/sRandSmsPwd";// 短信密码登录

	private static final String QUERY_ADD_MODE_URL = "scmbhi/services/sAddModeQuery3";// 增值业务查询
	private static final String QUERY_PHONE_FREE_URL = "scmbhi/services/sfreeqry";// 套餐余量查询
	private static final String QUERY_QUERY_BILL_HOME_URL = "scmbhi/services/s1350Print";// 我的帐单

	private static final String QUERY_M_SCORE_URL = "scmbhi/services/sSmsQueryM";// 积分M值查询
	private static final String YXGETREVERSEINFO_URL = "scmbhi/services/yxGetReverseInfo";
	private static final String YXGETQUEUENUM_URL = "scmbhi/services/yxGetQueueNum";
	private static final String GET_FAMILY_USER_LIST_URL = "scmbhi/services/getFamilyUserList";// 查询合家欢成员列表
	private static final String ADD_OR_DELETE_HJH_MEMBER_URL = "scmbhi/services/sFamilyUser1";// 增删合家欢成员
	private static final String OPEN_WHOLE_FAMILY_URL = "scmbhi/services/sPubOpenUser";// 合家欢开户
	private static final String CLOSE_WHOLE_FAMILY_URL = "scmbhi/services/sCancelUser";// "scmbhi/services/sCancelUser";//
																						// 合家欢销户
	private static final String CALL_TRANSFER_HANDLE_URL = "scmbhi/services/s1219Cfm";// 办理呼叫转移
	private static final String CALL_TRANSFER_QUERY_URL = "scmbhi/services/s1219Init";// 查询呼叫转移

	private static final String CHECK_PHONE_NO_TOPUP_ENABLE_URL = "scmbhi/services/payValidate";// 验证号码充值可否
	private static final String SMS_QUREY_GIFT_FOR_REST_URL = "scmbhi/services/sSmsQueryGift";// 获取积分礼品接口
	private static final String EXCHANGE_CREDITS_URL = "scmbhi/services/s1250Cfirm";// 积分兑换接口
	private static final String PHONE_CUR_MSG_URL = "scmbhi/services/sPhoneCurrMsg1";// 余量查询

	public static final String USER_INFO_URL = "scmbhi/services/getLoginUserInfo";// 获取用户信息
	private static final String READED_URL = "scmbhi/services/unifiedAppInterface/readed"; // 消息推送已读消息
	private static final String FEEDBACK_URL = "scmbhi/services/unifiedAppInterface/feedback"; // 消息推送反馈
	private static final String CHECK_IMSI_URL = "scmbhi/services/selAPPAgent";// 检测imsi

	private static final String CHANGE_PASSWORD_URL = "scmbhi/services/s1114CfmL";// 修改密码
	private static final String GET_TOPUP_HISTORY_URL = "scmbhi/services/sHandFeeQry";// 充值记录
	private static final String GET_BILL_APX_PAGE_URL = "scmbhi/services/sQryBillApxPage";// 账单详情记录

	private static final String GET_MY_BUSINESS_URL = "scmbhi/services/sMobileCityQry";// 获取我的业务详细信息

	private static final String GPHONE_FEE_MSG_URL = "scmbhi/services/sPhoneFeeMsg";// 1008611查询话费
	private static final String STAR_LEVEL_QUERY_URL = "scmbhi/services/sSmsQueryStarLevel";// 星级会员查询
	private static final String RECOMMOND_DOWNLOAD_URL = "http://183.221.33.188:8091/scmbhm/sv/ri.action?";

	private static final String URL_GET_RECOMMEND_BUSINESS = "scmbhi/services/getPersonInfo";// 推荐业务

	private static final String URL_CHANNEL_OPER = "scmbhi/services/sOperChannel";// 第三方渠道使用的url

	private static final String URL_CUR_BILL_DETAIL_QRY = "scmbhi/services/sBillDetailQry";// 本月账单详情

	private HttpClient mHttpClient;
	private static int TEST_REMIND_CURRENT_TRAFFIC = TEST_MAX_TRAFFIC;
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
	 * 获取短信密码
	 * 
	 * @param phoneNumber
	 *            手机号码
	 * @throws BusinessException
	 * @throws ServerInterfaceException
	 * @throws HttpException
	 */
	public String askForSmsPasswordInLogin(String phone_no) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("op_type", "0"));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("PASS_MODE", "0"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));

		String result = visitBusiness(getBusinessUrl(LOGIN_BY_RAMDSMS_PASSWORD_URL), pairs);
		return result;
	}

	/**
	 * 登录
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param password
	 *            密码
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String login(String phone_no, String password) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));

		pairs.add(new BasicNameValuePair("USER_PASSWORD", password));

		pairs.add(new BasicNameValuePair("SERVICE_NO", phone_no));
		pairs.add(new BasicNameValuePair("WORK_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));

		pairs.add(new BasicNameValuePair("query_ph", phone_no));
		pairs.add(new BasicNameValuePair("param1", CommUtil.getMonthPast(0)));

		String result = visitBusiness(getBusinessUrl(LOGIN_BY_SYS_PASSWORD_URL), pairs);

		return result;
	}

	/**
	 * 通过短信密码登录
	 * 
	 * @param phone_no
	 *            手机号
	 * @param rand_pwd
	 *            短信密码
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String loginBySmsRamd(String phone_no, String rand_pwd) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("rand_pwd", rand_pwd));
		pairs.add(new BasicNameValuePair("SMS_PWD", rand_pwd));

		pairs.add(new BasicNameValuePair("op_type", "1"));

		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PASS_MODE", "1"));

		pairs.add(new BasicNameValuePair("query_ph", phone_no));
		pairs.add(new BasicNameValuePair("param1", CommUtil.getMonthPast(0)));

		String result = visitBusiness(getBusinessUrl(LOGIN_BY_RAMDSMS_PASSWORD_URL), pairs);
		return result;
	}

	/**
	 * 处理通用业务
	 * 
	 * @param service
	 *            全量业务菜单里面的参数 serviceUrl
	 * @param data
	 *            全量业务菜单里面的参数 businessData
	 * @param phone_no
	 *            手机号码
	 * @param id
	 *            业务id
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String handleBusiness(String service, String data, String phone_no, long id, String token) throws HttpException, ServerInterfaceException,
			BusinessException {
		List<NameValuePair> pairs = analysisParamsData(data);
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("businessid", String.valueOf(id)));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(service), pairs);
		LogUtlis.showLogD(TAG, "handleBusiness:" + jsonStr);
		return jsonStr;
	}

	/**
	 * 密码修改
	 * 
	 * @param phone_no
	 * @param oldpassword
	 * @param newpassowrd
	 * @return 
	 *         {"RETURN":{"RETURN_CODE":"00","RETURN_MESSAGE":"用户密码修改成功!","IS_ARRAY"
	 *         :0,"RETURN_CHANNEL":"NEW_INTERFACE","RETURN_INFO":[]}}
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String changePassword(String phone_no, String oldpassword, String newpassowrd, String token) throws HttpException, ServerInterfaceException,
			BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("SERVICE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));

		pairs.add(new BasicNameValuePair("NEW_PWD", newpassowrd));

		pairs.add(new BasicNameValuePair("OLD_PWD", oldpassword));
		pairs.add(new BasicNameValuePair("token", token));

		String jsonStr = visitBusiness(getBusinessUrl(CHANGE_PASSWORD_URL), pairs);

		return jsonStr;
	}

	/**
	 * @param phone_no
	 * @param beginTime
	 *            yyyyMMdd
	 * @param endTime
	 *            yyyyMMdd
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getTopupHistory(String phone_no, String beginTime, String endTime, String token) throws HttpException, ServerInterfaceException,
			BusinessException {
		// sManPayScL

		// 返回一条数据：routePhoneNumber=15208394361&PHONE_NO=15208394361&BEGIN_TIME=20140901&END_TIME=20140926&PAY_SN=10000366630754
		// 返回多条数据：routePhoneNumber=15208394361&PHONE_NO=15208394361&BEGIN_TIME=20140901&END_TIME=20140926
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("BEGIN_TIME", beginTime));
		pairs.add(new BasicNameValuePair("END_TIME", endTime));

		pairs.add(new BasicNameValuePair("token", token));

		String jsonStr = visitBusiness(getBusinessUrl(GET_TOPUP_HISTORY_URL), pairs);

		return jsonStr;
	}

	/**
	 * 获取订单详情
	 * 
	 * @param phone_no
	 * @param beginTime
	 * @param queryTime
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getBillApxPage(String phone_no, String queryTime, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("QRY_START_DATE", queryTime));

		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("token", token));

		String jsonStr = visitBusiness(getBusinessUrl(GET_BILL_APX_PAGE_URL), pairs);

		return jsonStr;
	}

	/**
	 * @param phone_no
	 * @param token
	 * @return 获取我的业务详情
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getMyBusinessInfo(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("SERVICE_NO", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));

		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("token", token));

		String jsonStr = visitBusiness(getBusinessUrl(GET_MY_BUSINESS_URL), pairs);

		return jsonStr;
	}

	public String getPhoneFeeMsg(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));

		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("token", token));

		String jsonStr = visitBusiness(getBusinessUrl(GPHONE_FEE_MSG_URL), pairs);

		return jsonStr;
	}

	/**
	 * @param phone_no
	 * @param token
	 * @return 星级查询接口
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String queryStarLevel(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("SERVICE_NO", phone_no));

		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("token", token));

		String jsonStr = visitBusiness(getBusinessUrl(STAR_LEVEL_QUERY_URL), pairs);

		return jsonStr;
	}

	public String queryCurMonthBillDetail(String phone_no, String month, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("YEAR_MONTH", month));

		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("token", token));

		String jsonStr = visitBusiness(getBusinessUrl(URL_CUR_BILL_DETAIL_QRY), pairs);

		return jsonStr;
	}

	public String channelUpdate(String phone_no, String operMethod, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));

		if (operMethod != null)
			pairs.add(new BasicNameValuePair("OPER_METHOD", operMethod));

		if (token != null)
			pairs.add(new BasicNameValuePair("asiaToken", token));

		String jsonStr = visitBusiness(getBusinessUrl(URL_CHANNEL_OPER), pairs);

		return jsonStr;
	}

	/**
	 * 付款申请
	 * 
	 * @param data
	 * @param phone_no
	 * @param price
	 * @param amount
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String requestForPayAction(String data, String bankType, String phone_no, String amount, String price, String token, String imei)
			throws HttpException, ServerInterfaceException, BusinessException {

		List<NameValuePair> pairs = analysisParamsData(data);
		String uuid = getKeyValue(pairs, null);
		pairs.add(new BasicNameValuePair("bank_type", bankType));
		pairs.add(new BasicNameValuePair("id", phone_no));
		pairs.add(new BasicNameValuePair("value", amount));
		pairs.add(new BasicNameValuePair("sum_value", amount));
		pairs.add(new BasicNameValuePair("price", price));
		pairs.add(new BasicNameValuePair("amount", price));

		String requestParamsStr = UrlUtils.buildQueryString(pairs);
		LogUtlis.showLogI("充值", "充值参数:" + requestParamsStr + "&deviceId" + imei + "&clientversion" + App.sAPKVersionName);
		requestParamsStr = ZipAndBaseUtils.compressAndEncodeDesCryp(requestParamsStr, token);
		pairs.clear();

		requestParamsStr += "&deviceId=" + imei;
		// 参数添加clientversion
		if (!TextUtils.isEmpty(App.sAPKVersionName)) {
			requestParamsStr += "&clientversion=" + App.sAPKVersionName;
		}

		if (uuid != null) {
			requestParamsStr += "&deviceuuid=" + uuid;
		}

		// 对参数加解密
		String url = getBusinessUrl("scmbhi/services/initDefray") + "?" + requestParamsStr;
		LogUtlis.showLogD(TAG, "requestParamsStr:" + url);

		// 生成uri
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			LogUtlis.showLogE(TAG, e.getMessage(), e);
			throw new HttpException("Invalid URL.");
		}
		// 访问网络
		String str = mHttpClient.httpRequestStr(uri);
		String jsonStr = ZipAndBaseUtils.decompressAndDecodeBase64(str, Base64.PREFERRED_ENCODING);
		LogUtlis.showLogD(TAG, "visitBusiness:" + jsonStr);
		handleBusinessResultCode(jsonStr);

		return jsonStr;
	}

	private String getKeyValue(List<NameValuePair> pairs, String key) {
		if (TextUtils.isEmpty(key)) {
			key = "UDNBDAS";
		}
		String queryStr = "";
		int size = pairs.size();
		for (int i = 0; i < size; i++) {
			NameValuePair pair = pairs.get(i);
			String name = pair.getName();
			String value = pair.getValue();
			if (key.trim().equals(name.trim())) {
				queryStr = value;
				break;
			}
		}
		if (queryStr.length() > 0) {
			queryStr = queryStr.substring(queryStr.length() - 15) + System.currentTimeMillis();
		}
		return queryStr;
	}

	/**
	 * @return 支付商户信息
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getPayTypeAction() throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();

		String str = mHttpClient.httpRequestStr(getBusinessUrl("scmbhi/services/getPayList"), pairs);
		String decodeJsonStr = ZipAndBaseUtils.decompressAndDecodeBase64(str, Base64.PREFERRED_ENCODING);
		return decodeJsonStr;
	}

	/**
	 * 合家欢成员列表获取
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String loadWholeFamilyMembers(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = analysisParamsData("op_type=9010&OP_CODE=9010");
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(GET_FAMILY_USER_LIST_URL), pairs);
		return jsonStr;
	}

	public String getWholeFamilyType(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = analysisParamsData("op_type=9009&OP_CODE=9009");
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(GET_FAMILY_USER_LIST_URL), pairs);
		return jsonStr;
	}

	/**
	 * 开通合家欢
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String openWholeFamily(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		// analysisParamsData("GROUP_ID=64901&REGION_ID=11&OP_CODE=1017&CUST_FLAG=1&OP_NOTE=aa&VERIFY_FLAG=0&CHN_CODE=21&TYPE_CODE=1&USERGROUP_FLAG=5&PROD_ID=APBZ02897&MEMBER_ROLE_ID=40016&op_type=1&LOGIN_NO=ob0014");
		// pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("OBJECT_PHONE", phone_no));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		// pairs.add(new BasicNameValuePair("family_no", phone_no));
		// pairs.add(new BasicNameValuePair("member_no", member_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(OPEN_WHOLE_FAMILY_URL), pairs);
		return jsonStr;
	}

	/**
	 * 关闭合家欢
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String closeWholeFamily(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		// analysisParamsData("OP_CODE=1018&OP_NOTE=aa&CUST_ID_TYPE=9&BUSI_TYPE=9&GROUP_TYPE=AFMO&op_type=9&mode_flag=0");
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("SERVICE_NO", phone_no));
		// pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		// pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		// pairs.add(new BasicNameValuePair("family_no", phone_no));
		// pairs.add(new BasicNameValuePair("member_no", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(CLOSE_WHOLE_FAMILY_URL), pairs);
		return jsonStr;
	}

	/**
	 * 获取增值业务菜单信息
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String loadAdditionalTariffInfo(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		// pairs.add(new BasicNameValuePair("qry_type", "0"));
		// pairs.add(new BasicNameValuePair("type", "0"));
		pairs.add(new BasicNameValuePair("param1", CommUtil.getMonthPast(0)));
		pairs.add(new BasicNameValuePair("subAccount", "ob0014"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("QRY_TIME", CommUtil.getMonthPast(0)));
		pairs.add(new BasicNameValuePair("token", token));

		String jsonStr = visitBusiness(getBusinessUrl(QUERY_ADD_MODE_URL), pairs);

		LogUtlis.showLogD(TAG, "handleBusiness:" + jsonStr);
		return jsonStr;
	}

	static int flag = 0;

	/**
	 * 增加或删除合家欢成员
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param member_no
	 *            成员号码
	 * @param isDelete
	 *            true 删除；false 增加
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String deleteOrAddWholeFamilyMember(String phone_no, String member_no, boolean isDelete, String token) throws HttpException,
			ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = analysisParamsData("MODE_FLAG=0&mode_flag=0");
		pairs.add(new BasicNameValuePair("OPERATE_TYPE", isDelete ? "3" : "2"));
		pairs.add(new BasicNameValuePair("family_no", phone_no));
		pairs.add(new BasicNameValuePair("member_no", member_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_CHILD", member_no));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("op_type", isDelete ? "4" : "3"));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(ADD_OR_DELETE_HJH_MEMBER_URL), pairs);
		return jsonStr;
	}

	/**
	 * 套餐余量查询
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param time
	 *            日期 yyyyMM格式
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String sPhoneFreeQuery(String phone_no, String time, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("type", "0"));
		pairs.add(new BasicNameValuePair("YEAR_MONTH", time));
		// pairs.add(new BasicNameValuePair("month", time));
		pairs.add(new BasicNameValuePair("BUSI_CODE", "0"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(QUERY_PHONE_FREE_URL), pairs);
		return jsonStr;
	}

	/**
	 * 余量查询
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getPhoneCurrMsg(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(PHONE_CUR_MSG_URL), pairs);
		return jsonStr;
	}

	/**
	 * 帐单查询
	 * 
	 * @param phone_no
	 *            手机号码
	 * @param query_date
	 *            日期 yyyyMM格式
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String queryBillHome(String phone_no, String query_date, String token) throws HttpException, ServerInterfaceException, BusinessException,
			JSONException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("query_date", query_date));
		pairs.add(new BasicNameValuePair("QRY_START_DATE", query_date));
		// pairs.add(new BasicNameValuePair("QRY_END_DATE", "201405"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(QUERY_QUERY_BILL_HOME_URL), pairs);
		jsonStr = new JSONObject(jsonStr).getJSONObject("RETURN").getJSONArray("RETURN_INFO").getJSONObject(0).toString();
		return jsonStr;
	}

	/**
	 * 积分查询
	 * 
	 * @param phone_no
	 * @param token
	 * @return 积分/M值
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String getMScore(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException, JSONException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(QUERY_M_SCORE_URL), pairs);
		return jsonStr;
	}

	/**
	 * 办理呼叫转移
	 * 
	 * @param phone_no
	 * @param opType
	 * @param cf_phone
	 *            转移号码
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String handleBusinessCallTransfer(String phone_no, String opType, String cf_phone, String token) throws HttpException, ServerInterfaceException,
			BusinessException, JSONException {
		// 呼叫转移
		List<NameValuePair> pairs = analysisParamsData(null);
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("ORG_CODE", "ob0014"));// 组织代码
		pairs.add(new BasicNameValuePair("op_type", opType));
		pairs.add(new BasicNameValuePair("cf_phone", cf_phone));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(CALL_TRANSFER_HANDLE_URL), pairs);
		return jsonStr;
	}

	/**
	 * 查询呼叫转移
	 * 
	 * @param phone_no
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String queryCallTransfer(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException, JSONException {
		List<NameValuePair> pairs = analysisParamsData(null);
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("ORG_CODE", "ob0014"));// 组织代码
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(CALL_TRANSFER_QUERY_URL), pairs);
		return jsonStr;
	}

	/**
	 * @param phone_no
	 * @param user
	 * @param price
	 *            分
	 * @return 验证号码可否充值
	 */
	public String checkPhoneNumberTopupEnable(String phone_no, String user, String price, String token) throws HttpException, ServerInterfaceException,
			BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("query_ph", phone_no));
		pairs.add(new BasicNameValuePair("clie_phone_no", user));
		pairs.add(new BasicNameValuePair("price", price));
		pairs.add(new BasicNameValuePair("param1", CommUtil.getMonthPast(0)));
		if (token != null)
			pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(CHECK_PHONE_NO_TOPUP_ENABLE_URL), pairs);
		return jsonStr;
	}

	/**
	 * 获取积分礼品接口
	 * 
	 * @param token
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public String SmsQueryGiftForRest(String phone_no, String smsInfo, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = analysisParamsData("WORK_NO=ob0014&OP_CODE=2008&PROVINCE_GROUP=10008");
		pairs.add(new BasicNameValuePair("SMS_INFO", smsInfo));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("SERVICE_NO", phone_no));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(SMS_QUREY_GIFT_FOR_REST_URL), pairs);
		return jsonStr;
	}

	/**
	 * 
	 * 积分兑换
	 * 
	 * @param phone_no
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String exchangeCredits(String phone_no, String code, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = analysisParamsData("WORK_NO=ob0014&");
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("favour_code", code));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonStr = visitBusiness(getBusinessUrl(EXCHANGE_CREDITS_URL), pairs);
		return jsonStr;
	}

	/**
	 * 获取用户的基本信息
	 * 
	 * @param phone_no
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getUserInfos(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("PHONE_NO", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));

		pairs.add(new BasicNameValuePair("SERVICE_NO", phone_no));
		pairs.add(new BasicNameValuePair("WORK_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));

		pairs.add(new BasicNameValuePair("query_ph", phone_no));
		pairs.add(new BasicNameValuePair("param1", CommUtil.getMonthPast(0)));
		pairs.add(new BasicNameValuePair("token", token));

		String result = visitBusiness(getBusinessUrl(USER_INFO_URL), pairs);

		return result;
	}

	/**
	 * @param phone_no
	 * @param token
	 * @return 获取推荐业务
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getRecommendBusiness(String phone_no, String token) throws HttpException, ServerInterfaceException, BusinessException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("phoneNo", phone_no));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));

		pairs.add(new BasicNameValuePair("SERVICE_NO", phone_no));
		pairs.add(new BasicNameValuePair("WORK_NO", "ob0014"));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phone_no));

		pairs.add(new BasicNameValuePair("token", token));

		String result = visitBusiness(getBusinessUrl(URL_GET_RECOMMEND_BUSINESS), pairs);

		return result;
	}

	/**
	 * @param imei
	 * @return 获取匿名token
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 */
	public String getNoNameToken(String imei, String imsi, String ip, String netType) throws HttpException, ServerInterfaceException, BusinessException {

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("imei", imei));
		if (imsi != null)
			pairs.add(new BasicNameValuePair("imsi", imsi));
		if (ip != null)
			pairs.add(new BasicNameValuePair("phone_mac", ip));
		if (netType != null)
			pairs.add(new BasicNameValuePair("net_type", netType));
		pairs.add(new BasicNameValuePair("LOGIN_NO", "ob0014"));

		String result = visitBusiness(getBusinessUrl("scmbhi/services/generateTokey"), pairs);

		return result;
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

	public String getUpdateInfos() throws HttpException, ServerInterfaceException, BusinessException {
		/*
		 * if (App.test) { ArrayList<UpdateInfo> datas = new
		 * ArrayList<UpdateInfo>();
		 * 
		 * UpdateInfo apkUpdateInfo = new UpdateInfo();
		 * apkUpdateInfo.setDownloadUrl(
		 * "http://gdown.baidu.com/data/wisegame/b79e84ad373bb53c/kuwoyinle_5750.apk"
		 * ); apkUpdateInfo.setNewVersionCode(1);
		 * apkUpdateInfo.setSuportVersion(1);
		 * apkUpdateInfo.setType(UpdateInfo.APK_TYPE);
		 * apkUpdateInfo.setNewVersionName("1.1"); String des1 =
		 * "优化大部分页面，以及修复了一些bug"; String des2 = "修改更多界面，跳转程序错误问题"; String des =
		 * des1 + ":" + des2; apkUpdateInfo.setUpdateDescription(des);
		 * apkUpdateInfo.setSuportVersionName("0.2");
		 * 
		 * UpdateInfo menusUpdateInfo = new UpdateInfo();
		 * menusUpdateInfo.setDownloadUrl
		 * (AppDirConstant.APP_FILE_MENU_JSON_NAME);
		 * menusUpdateInfo.setNewVersionCode(20140113);
		 * menusUpdateInfo.setType(UpdateInfo.MENUS_TYPE);
		 * 
		 * UpdateInfo commonlyBusinessesUpdateInfo = new UpdateInfo();
		 * commonlyBusinessesUpdateInfo
		 * .setDownloadUrl(AppDirConstant.APP_COMMONLY_BUSINESS_JSON_NAME);
		 * commonlyBusinessesUpdateInfo.setNewVersionCode(20140113);
		 * commonlyBusinessesUpdateInfo
		 * .setType(UpdateInfo.COMMONLY_BUSINESS_TYPE);
		 * 
		 * datas.add(apkUpdateInfo); datas.add(menusUpdateInfo);
		 * datas.add(commonlyBusinessesUpdateInfo);
		 * 
		 * updateInfos = new UpdateInfos(); updateInfos.setDatas(datas); String
		 * jsonStr = JsonUtils.ObjectToJson(updateInfos); try {
		 * FileUtils.saveToFile(jsonStr, "test", "updateInfos.json"); } catch
		 * (IOException e) { e.printStackTrace(); } } else
		 */
		String url = getUrl(UPDATEINFOBYOSINFO_URL);
		String jsonStr = requestJson(url, null);
		// TODO: 获取升级信息
		LogUtlis.showLogI("updateinfos", "updateinfos:" + jsonStr);

		return jsonStr;
	}

	public AllMenus getAllMenus(String url) throws HttpException, ServerInterfaceException, BusinessException {
		AllMenus allMenus = null;
		String jsonStr;
		/*
		 * if (App.test) { jsonStr = FileUtils.readFileFromAssets(App.sContext,
		 * url); } else
		 */
		{
			jsonStr = requestJson(url, null);
			if (App.test) {
				try {
					FileUtils.saveToFile(jsonStr, "test", AppDirConstant.APP_MENU_JSON_NAME);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		allMenus = JsonUtils.parseJsonStrToObject(jsonStr, AllMenus.class);
		return allMenus;
	}

	public CommonlyUsedBusinessMenus getCommonlyUsedBusinessMenus(Context context, String url) throws HttpException, ServerInterfaceException, IOException,
			BusinessException {
		String jsonStr;
		jsonStr = requestJson(url, null);
		CommonlyUsedBusinessMenus commonlyUsedBusinessMenus = JsonUtils.parseJsonStrToObject(jsonStr, CommonlyUsedBusinessMenus.class);
		FileUtils.saveToDataFile(context, jsonStr, AppDirConstant.APP_COMMONLY_BUSINESS_JSON_NAME);
		return commonlyUsedBusinessMenus;
	}

	public ArrayList<MobileBusinessHall> getBusinesHallInfo(Context context, String url) throws HttpException, ServerInterfaceException, BusinessException,
			IOException {
		String jsonStr = requestStr(url, null);
		if (App.test) {
			try {
				FileUtils.saveToFile(jsonStr, "test", AppDirConstant.APP_BUSINESS_HALL_INFOS_JSON_NAME);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ArrayList<MobileBusinessHall> halls = CommUtil.getMobileBusinessHalls(jsonStr);
		if (halls != null && halls.size() > 0) {
			FileUtils.saveToDataFile(context, jsonStr, AppDirConstant.APP_BUSINESS_HALL_INFOS_JSON_NAME);
		}
		return halls;
	}

	/**
	 * 功能： 获取公告，帮助等内容信息并保证到文件中
	 * 
	 * @param context
	 *            上下午
	 * @param url
	 *            请求的链接地址
	 * @param sFileName
	 *            保存的文件地址
	 * @return 请求的返回的结果列表
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws IOException
	 * @throws BusinessException
	 */
	public ContentInfos getContentInfo(Context context, String url, String sFileName) throws HttpException, ServerInterfaceException, IOException,
			BusinessException {
		String jsonStr = "";
		jsonStr = requestJson(url, null);
		ContentInfos sContentList = JsonUtils.parseJsonStrToObject(jsonStr, ContentInfos.class);
		FileUtils.saveToDataFile(context, jsonStr, sFileName);
		return sContentList;
	}

	/**
	 * @param context
	 * @param url
	 * @param sFileName
	 * @return 直接获取网路上加密文档的内容并解密返回
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws IOException
	 * @throws BusinessException
	 */
	public String getHttpContentData(Context context, String url, String sFileName) throws HttpException, ServerInterfaceException, IOException,
			BusinessException {
		String jsonStr = "";
		jsonStr = requestJson(url, null);
		FileUtils.saveToDataFile(context, jsonStr, sFileName);
		return jsonStr;
	}

	/**
	 * 获取使用帮助，常见问题，体统公告信息
	 * 
	 * @param context
	 *            上下文
	 * @param type
	 *            获取信息类型 0 系统公告 1 使用帮助 2 常见问题
	 * @return ContentInfo 列表
	 */
	public List<ContentInfo> getContentList(Context context, int type) {
		/*
		 * TelephonyManager tm = (TelephonyManager)
		 * context.getSystemService(Context.TELEPHONY_SERVICE); String tel =
		 * tm.getLine1Number(); String myurl =
		 * "getContentTypeList.action?phone_no=" + tel + "&cntentType=" + type;
		 * String jsonstr = null;
		 */

		List<ContentInfo> content = new ArrayList<ContentInfo>();
		int length = App.sContentInfoList.size();
		if (length == 0) {
			initContentInfoMsg(context);
			length = App.sContentInfoList.size();
		}
		LogUtlis.showLogI(TAG, "contentlist size:" + length);

		for (int i = 0; i < length; i++) {
			ContentInfo info = App.sContentInfoList.get(i);
			if (info.getContentType() == type) {
				content.add(info);
			}
		}

		return content;
	}

	private void initContentInfoMsg(Context sContext) {
		String jsonStrnotice = null;
		String jsonStrguide = null;
		String jsonStrproblem = null;
		try {
			jsonStrnotice = FileUtils.readDataFile(sContext, AppDirConstant.APP_SYSTEM_NOTICE_JSON_NAME);
			LogUtlis.showLogI(TAG, "from net jsonStrnotice:" + jsonStrnotice);
			jsonStrguide = FileUtils.readDataFile(sContext, AppDirConstant.APP_USER_GUIDE_JSON_NAME);
			LogUtlis.showLogI(TAG, "from net  jsonStrguide:" + jsonStrguide);
			jsonStrproblem = FileUtils.readDataFile(sContext, AppDirConstant.APP_COMMONLY_PROBLEM_JSON_NAME);
			LogUtlis.showLogI(TAG, "from net jsonStrproblem:" + jsonStrproblem);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (jsonStrnotice == null) {
			jsonStrnotice = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_SYSTEM_NOTICE_JSON_NAME);
			LogUtlis.showLogI(TAG, "from local jsonStrnotice:" + jsonStrnotice);
			jsonStrguide = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_USER_GUIDE_JSON_NAME);
			LogUtlis.showLogI(TAG, "from local  jsonStrguide:" + jsonStrguide);
			jsonStrproblem = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_COMMONLY_PROBLEM_JSON_NAME);
			LogUtlis.showLogI(TAG, "from local jsonStrproblem:" + jsonStrproblem);
		}
		ContentInfos contentInfosnotice = JsonUtils.parseJsonStrToObject(jsonStrnotice, ContentInfos.class);
		ContentInfos contentInfosguide = JsonUtils.parseJsonStrToObject(jsonStrguide, ContentInfos.class);
		ContentInfos contentInfosproblem = JsonUtils.parseJsonStrToObject(jsonStrproblem, ContentInfos.class);
		LogUtlis.showLogI(TAG, "contentInfosnotice:" + contentInfosnotice.getDatas().size() + "contentInfosguide:" + contentInfosguide.getDatas().size()
				+ "contentInfosproblem:" + contentInfosproblem.getDatas().size());
		App.sContentInfoList.clear();
		App.sContentInfoList.addAll(contentInfosnotice.getDatas());
		int length = contentInfosguide.getDatas().size();
		int i = 0;
		for (i = 0; i < length; i++) {
			App.sContentInfoList.add(contentInfosguide.getDatas().get(i));
		}
		length = contentInfosproblem.getDatas().size();
		for (i = 0; i < length; i++) {
			App.sContentInfoList.add(contentInfosproblem.getDatas().get(i));
		}
	}

	public CommonlySearchs getCommonlySearch(Context context) throws HttpException, ServerInterfaceException, IOException, BusinessException {
		CommonlySearchs commonlySearchs = null;
		String jsonStr;
		{
			String url = getUrl(OFFEN_USESEARCH_URL);
			jsonStr = requestJson(url, null);
			commonlySearchs = JsonUtils.parseJsonStrToObject(jsonStr, CommonlySearchs.class);
		}
		FileUtils.saveToDataFile(context, jsonStr, AppDirConstant.APP_COMNONLY_SEARCCH_JSON_NAME);
		return commonlySearchs;
	}

	public PreferentialInfos getPreferentialInfos(Context context) throws HttpException, ServerInterfaceException, IOException, BusinessException {
		PreferentialInfos preferentialInfos = null;
		String jsonStr = null;
		/*
		 * if (App.test) { PreferentialInfo preferentialInfo1 = new
		 * PreferentialInfo(); preferentialInfo1.setBigIcon("scroll_1.png");
		 * preferentialInfo1.setType(PreferentialInfo.SCROLL_TYPE);
		 * preferentialInfo1.setDetailsUrl("http://www.baidu.com/");
		 * PreferentialInfo preferentialInfo2 = new PreferentialInfo();
		 * preferentialInfo2.setBigIcon("scroll_2.png");
		 * preferentialInfo2.setDetailsUrl("http://www.baidu.com/");
		 * preferentialInfo2.setType(PreferentialInfo.SCROLL_TYPE);
		 * PreferentialInfo preferentialInfo3 = new PreferentialInfo();
		 * preferentialInfo3.setBigIcon("scroll_1.png");
		 * preferentialInfo3.setDetailsUrl("http://www.baidu.com/");
		 * preferentialInfo3.setType(PreferentialInfo.SCROLL_TYPE);
		 * PreferentialInfo preferentialInfo4 = new PreferentialInfo();
		 * preferentialInfo4.setBigIcon("scroll_2.png");
		 * preferentialInfo4.setDetailsUrl("http://www.baidu.com/");
		 * preferentialInfo4.setType(PreferentialInfo.SCROLL_TYPE);
		 * PreferentialInfo preferentialInfo5 = new PreferentialInfo();
		 * preferentialInfo5.setBigIcon("scroll_1.png");
		 * preferentialInfo5.setDetailsUrl("http://www.baidu.com/");
		 * preferentialInfo5.setType(PreferentialInfo.SCROLL_TYPE);
		 * ArrayList<PreferentialInfo> datas = new
		 * ArrayList<PreferentialInfo>(); datas.add(preferentialInfo1);
		 * datas.add(preferentialInfo2); datas.add(preferentialInfo3);
		 * datas.add(preferentialInfo4); datas.add(preferentialInfo5);
		 * preferentialInfos = new PreferentialInfos();
		 * preferentialInfos.setDatas(datas); jsonStr =
		 * JsonUtils.ObjectToJson(preferentialInfos); } else
		 */
		{
			String url = getUrl(PREFERENTIALINFOS_URL);
			jsonStr = requestJson(url, null);
		}
		preferentialInfos = JsonUtils.parseJsonStrToObject(jsonStr, PreferentialInfos.class);
		FileUtils.saveToDataFile(context, jsonStr, AppDirConstant.APP_PREFERENTIALINFOS_JSON_NAME);
		return preferentialInfos;
	}

	public String commitFeedBack(Context context, String url) throws HttpException, ServerInterfaceException, IOException, BusinessException {
		Integer retCode = -1;
		String retMsg = null;
		String jsonstr = null;
		/*
		 * if (App.test) { jsonstr =
		 * "{'resultCode':0, 'resultMesage':'success'}"; } else
		 */{
			jsonstr = requestJson(getBusinessUrl(url), null);
		}
		return jsonstr;
	}

	public void commitSearchTags(String data) throws ClientProtocolException, IOException, HttpException {
		mHttpClient.postData(getUrl(SUBMIT_SEARCH_TAGS_URL), data);
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
			if (jsonObject.has("RETURN")) {
				jsonObject = jsonObject.getJSONObject("RETURN");

				businessResultCode = CommUtil.parse2Integer(jsonObject.getString("RETURN_CODE"), -1);
				if (businessResultCode != ServerInterfaceException.BUSINESS_OK) {
					String businessResultMesage = jsonObject.getString("RETURN_MESSAGE");
					if (businessResultCode == 1) {// &&
													// businessResultMesage.contains("令牌"))
													// {
						throw new ServerInterfaceException(businessResultMesage, ServerInterfaceException.BUSINESS_AUTHENTICATION_TIME_OUT);
					} else {
						throw new BusinessException(businessResultMesage, businessResultCode);
					}
				} else if (jsonObject.has("CODE")) {
					businessResultCode = CommUtil.parse2Integer(jsonObject.getString("CODE"), -1);
					if (businessResultCode != ServerInterfaceException.BUSINESS_OK) {
						String businessResultMesage = jsonObject.getString("MSG");
						throw new BusinessException(businessResultMesage, businessResultCode);
					}
				} else if (jsonObject.has("resultCode")) {
					businessResultCode = CommUtil.parse2Integer(jsonObject.getString("resultCode"), -1);
					LogUtlis.showLogD(TAG, "" + jsonObject.toString());
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

	/**
	 * Handle business result code
	 * 
	 * @throws JSONException
	 */
	/*
	 * private void handleLogicResultCode(String jsonStr) throws HttpException,
	 * ServerInterfaceException { JSONObject jsonObject = null; int
	 * businessResultCode = 10; try { jsonObject = (JSONObject) new
	 * JSONObject(jsonStr); if (jsonObject.has("resultCode")) {
	 * businessResultCode =
	 * Integer.parseInt(jsonObject.getString("resultCode"));
	 * LogUtlis.showLogD(TAG, "" + jsonObject.toString()); if
	 * (businessResultCode != ServerInterfaceException.BUSINESS_OK) { String
	 * businessResultMesage = jsonObject.getString("resultMessage"); throw new
	 * ServerInterfaceException(businessResultMesage, businessResultCode); } } }
	 * catch (JSONException e) { throw new ResponseException("json error",
	 * businessResultCode); } }
	 */

	/**
	 * @param groupIds
	 * @return 获取等待人数
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public String getWaitPeople(String groupIds) throws HttpException, ServerInterfaceException, BusinessException, JSONException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("groupIds", groupIds));
		String jsonReslut = visitBusiness(getBusinessUrl(YXGETQUEUENUM_URL), pairs);
		JSONArray jsonObject = new JSONObject(jsonReslut).getJSONArray("DATALIST");
		String number = "";
		if (jsonObject.length() > 0) {
			JSONObject reslutObject = jsonObject.getJSONObject(0);
			number = reslutObject.getString("NUM");
		}
		return number;
	}

	/**
	 * @param mobile
	 * @param groupId
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public ReservationNumberReslut reservationNumber(String mobile, String groupId, String token) throws HttpException, ServerInterfaceException,
			BusinessException, JSONException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("mobile", mobile));
		pairs.add(new BasicNameValuePair("groupId", groupId));
		pairs.add(new BasicNameValuePair("token", token));
		String jsonReslut = visitBusiness(getBusinessUrl(YXGETREVERSEINFO_URL), pairs);
		JSONObject jsonObject = new JSONObject(jsonReslut);
		String number = jsonObject.getString("NUM");
		String result = jsonObject.getString("MSG");
		int resultCode = Integer.valueOf(jsonObject.getString("CODE"));
		ReservationNumberReslut reservationNumberReslut = new ReservationNumberReslut(result, number);
		reservationNumberReslut.setResultCode(resultCode);
		return reservationNumberReslut;
	}

	/**
	 * @param mobile
	 * @param groupId
	 * @param token
	 * @return
	 * @throws HttpException
	 * @throws ServerInterfaceException
	 * @throws BusinessException
	 * @throws JSONException
	 */
	public ReservationNumberReslut unreservationNumber(String mobile, String groupId, String token) throws HttpException, ServerInterfaceException,
			BusinessException, JSONException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("mobile", mobile));
		pairs.add(new BasicNameValuePair("groupId", groupId));
		String jsonReslut = visitBusiness(getBusinessUrl(YXGETREVERSEINFO_URL), pairs);
		JSONObject jsonObject = new JSONObject(jsonReslut);
		String number = jsonObject.getString("NUM");
		String result = jsonObject.getString("MSG");
		pairs.add(new BasicNameValuePair("token", token));
		int resultCode = Integer.valueOf(jsonObject.getString("CODE"));
		ReservationNumberReslut reservationNumberReslut = new ReservationNumberReslut(result, number);
		reservationNumberReslut.setResultCode(resultCode);
		return reservationNumberReslut;
	}

	private String requestJson(String url, List<NameValuePair> pairs) throws HttpException, ServerInterfaceException, BusinessException {
		String str = mHttpClient.httpRequestStr(url, pairs);
		String decodeJsonStr = str;
		LogUtlis.showLogI(TAG, str);
		if (isDecode)
			decodeJsonStr = ZipAndBaseUtils.decompressAndDecodeBase64(str, Base64.PREFERRED_ENCODING);
		handleBusinessResultCode(decodeJsonStr);
		LogUtlis.showLogD(TAG, "decodeJsonStr:" + decodeJsonStr);
		return decodeJsonStr;
	}

	private String requestStr(String url, List<NameValuePair> pairs) throws HttpException, ServerInterfaceException, BusinessException {
		String str = mHttpClient.httpRequestStr(url, pairs);
		LogUtlis.showLogI(TAG, str);
		if (isDecode)
			str = ZipAndBaseUtils.decompressAndDecodeBase64(str, Base64.PREFERRED_ENCODING);
		LogUtlis.showLogD(TAG, "decodeJsonStr:" + str);
		return str;
	}

	private String visitBusiness(String url, List<NameValuePair> pairs) throws HttpException, ServerInterfaceException, BusinessException {
		String str = mHttpClient.httpRequestStr(url, pairs);
		LogUtlis.showLogI("login", "loginurl" + str);
		String decodeJsonStr = ZipAndBaseUtils.decompressAndDecodeBase64(str, Base64.PREFERRED_ENCODING);
		LogUtlis.showLogD(TAG, "visitBusiness:" + decodeJsonStr);
		handleBusinessResultCode(decodeJsonStr);
		return decodeJsonStr;
	}

	// 消息推送
	/**
	 * 功能： 向后台发送消息已读的通知。后台把该消息的状态设置为已读
	 * 
	 * @param notificationId
	 *            信息ID
	 * @return 返回的消息处理结果
	 * @throws HttpException
	 *             连接异常
	 * @throws BusinessException
	 *             业务异常
	 */
	public ReadedBean readed(String notificationId) throws HttpException, BusinessException {
		ReadedBean readedBean = null;
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("notificationId", notificationId));
		String jsonStr = mHttpClient.httpRequestStr(getUrl(READED_URL), pairs);
		readedBean = JsonUtils.parseJsonStrToObject(jsonStr, ReadedBean.class);
		return readedBean;
	}

	/**
	 * 功能： 用户在客户端收到推送的消息后，如果消息的配置消息反馈要求，可在消息的详情接口的文本输入框输入反馈，调用本接口向后台发送消息的反馈。
	 * 后台把该反馈保存到消息记录中，同时把消息的状态设置为已读。
	 * 
	 * @param notificationId
	 *            信息ID
	 * @param feedback
	 *            反馈内容
	 * @return 返回的消息处理结果(处理返回码和返回信息)
	 * @throws HttpException
	 *             连接异常
	 * @throws BusinessException
	 *             业务异常
	 */
	public MessageFeedbackBean feedback(String notificationId, String feedback) throws HttpException, BusinessException {
		MessageFeedbackBean feedbackBean = null;
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("notificationId", notificationId));
		pairs.add(new BasicNameValuePair("feedback", feedback));
		String jsonStr = mHttpClient.httpRequestStr(getUrl(FEEDBACK_URL), pairs);
		feedbackBean = JsonUtils.parseJsonStrToObject(jsonStr, MessageFeedbackBean.class);
		return feedbackBean;
	}

	public String getPhoneNumberByIMSI(String imsi, String token, String phoneNumber) throws HttpException, BusinessException, ServerInterfaceException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("IMSI_NO", imsi));
		pairs.add(new BasicNameValuePair("token", token));
		pairs.add(new BasicNameValuePair("routePhoneNumber", phoneNumber));
		pairs.add(new BasicNameValuePair("PHONE_NO", phoneNumber));
		String jsonStr = visitBusiness(getBusinessUrl(CHECK_IMSI_URL), pairs);
		return jsonStr;
	}

	public String getRecommondDownloadUrl() {
		return RECOMMOND_DOWNLOAD_URL;
	}

	public String requestPayPageHtml(String imei, String imsi, String VER, String LCD_WIDTH, String clie_phone_no, String token) throws HttpException,
			ServerInterfaceException, BusinessException {

		// IMEI=5432495024342&IMSI=9539793249789348900&VER=1000001&LCD_WIDTH=480&clie_phone_no=13213&clie_time=4354534
		// genHtUrl？上面参数的加密&deviceId=5432495024342

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();

		pairs.add(new BasicNameValuePair("IMEI", imei));
		pairs.add(new BasicNameValuePair("IMSI", imsi));
		pairs.add(new BasicNameValuePair("VER", VER));
		pairs.add(new BasicNameValuePair("LCD_WIDTH", LCD_WIDTH));
		pairs.add(new BasicNameValuePair("clie_phone_no", clie_phone_no));
		pairs.add(new BasicNameValuePair("clie_time", String.valueOf(System.currentTimeMillis())));

		String requestParamsStr = UrlUtils.buildQueryString(pairs);
		LogUtlis.showLogD(TAG, "加密前:" + requestParamsStr);
		requestParamsStr = ZipAndBaseUtils.compressAndEncodeDesCryp(requestParamsStr, token);
		pairs.clear();

		requestParamsStr += "&deviceId=" + imei;

		// 对参数加解密
		String url = getBusinessUrl("scmbhi/services/genHtUrl") + "?" + requestParamsStr;
		LogUtlis.showLogD(TAG, "加密后:" + url);

		// 生成uri
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			LogUtlis.showLogE(TAG, e.getMessage(), e);
			throw new HttpException("Invalid URL.");
		}
		LogUtlis.showLogD(TAG, "uri = " + uri.getPath());
		// 访问网络
		String str = mHttpClient.httpRequestStr(uri);
		String jsonStr = ZipAndBaseUtils.decompressAndDecodeBase64(str, Base64.PREFERRED_ENCODING);
		LogUtlis.showLogD(TAG, "visitBusiness:" + jsonStr);
		handleBusinessResultCode(jsonStr);

		return jsonStr;
	}
}
