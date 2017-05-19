package com.sunrise.econan.cache.preferences;

import java.util.Date;

import com.sunrise.javascript.utils.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class Preferences {

	// main config file' name
	private final static String SETTING = "setting";
	private final static String UPDATE_INFO_VERSIONS = "update_info_versions";
	private final static String USER_ID_KEY = "user_id";
	private final static String USER_PASSWORD_KEY = "user_password";

	public final static String PHONE_FREE_QUERY = "phone_free_query";// 套餐余量查询

	// public final static String QUERY_BILL_HOME = "query_bill_home";// 账单查询

	private final static String NEW_STR = "new";
	private static final String AUTO_LOGIN = "auto_login";// 自动登录
	public static final String KEY_LOGOUT = "logout_code";
	private final static String TRAFFIC_THRESHOLD_KEY = "traffic_threshold";// 流量提醒的值
	private final static String TRAFFIC_NOTIFICATION_MODE_KEY = "traffic_notification_mode";// 流量提醒模式
	private final static String TRAFFIC_OVER_HANDLE_MODE_KEY = "traffic_over_handle_mode";// 流量用完执行模式
	private final static String TRAFFIC_NOTIFICATION_FUNCTION_KEY = "traffic_notification";// 是否提醒
	public static final String M_CREDITS = "mCredits";// "m_credits";
	// private static final String IS_LOGININ = "is_login";
	private static final String WIDGET_NUNBER = "widget phone number";
	private static final String PHONE_CUR_MSG = "phone_cur_msg";// 用户总帐单余额信息查询
	private static final String USER_BASE_INFO = "user base info";// 用户基本信息 -
	private static final String ISNEEDGUIDE = "isneedguide"; // 姓名、品牌、归属地、主资费

	public static final String ORDER_TIME = "order time";// 预约时间
	public static final String HALL_NUMBER = "hall_numbers";// 已预约营业厅号
	public static final String HALL_ADDRESS_POP = "HALL_ADDRESS_POP";// 已预约营业厅地址
	public static final String HALL_NAME_POP = "HALL_NAME_POP";// 已预约营业厅名称

	private static final String USER_NAME_LOGINS = "logined user numbers";// 登录过的用户名
	private final static String PUSH_MESSAGE_USERNAME_KEY = "push_message_username";
	private final static String PUSH_MESSAGE_PASSWORD_KEY = "push_message_word";
	private final static String PUSH_MESSAGE_SERVER_IP_KEY = "server_ip";
	private final static String PUSH_MESSAGE_SERVER_PORT_KEY = "server_port";
	private final static String AUTHENTICATION_KEY = "authentication";// 密匙

	private final static String KEY_USER_AGREEMENT = "user_agreement";// 用户协议

	private static final String KEY_HOME_PAGE_GUIDE_VERSION = "homepage_guide_version";// 首页引导图片
	private static final String KEY_AUTHENTICATION = "Authentication";// 令牌
	private static final String KEY_MOBILE = "mobile";// 手机号
	private static final String KEY_NOTIFICATION_PASSWORD = "NotificationPassword";
	private static final String KEY_SSJF_PAGE_VER_CODE = "ssjf page vercode";// 页面版本号
	private static final String KEY_SSJF_PAGE_URL = "ssjf page url";
	private static final String KEY_CHECKED_NUMBER = "checked number";
	private static final String KEY_SUBACCOUNT = "sub account";// 从账号

	private static SharedPreferences sSettingsPreferences;
	private static SharedPreferences sUdateInfosPreferences;
	private static Preferences instance;

	private Preferences(Context context) {
		sSettingsPreferences = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		sUdateInfosPreferences = context.getSharedPreferences(UPDATE_INFO_VERSIONS, Context.MODE_PRIVATE);

	}

	public static Preferences getInstance(Context context) {
		if (instance == null) {
			instance = new Preferences(context);
		}
		return instance;
	}

	public String getString(String key) {
		return sSettingsPreferences.getString(key, null);
	}

	public String getString(String key, String defaultStr) {
		return sSettingsPreferences.getString(key, defaultStr);
	}

	public boolean getBoolean(String key, boolean defaultValue) {

		String realValue = "120";
		if (defaultValue) {
			realValue = "110"; // true
		} else {
			realValue = "120"; // false
		}

		String retValue = sSettingsPreferences.getString(key, encode(realValue));
		if ("110".equals(decode(retValue))) {
			return true;
		} else {
			return false;
		}
		// return sSettingsPreferences.getBoolean(key, defaultValue);
	}

	public void putString(String key, String value) {
		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void putBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		String realValue = "120";
		if (value) {
			realValue = "110"; // true
		} else {
			realValue = "120"; // false
		}
		// editor.putBoolean(key, value);
		editor.putString(key, encode(realValue));
		editor.commit();
	}

	public void putInt(String key, int value) {
		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void putLong(String key, long value) {
		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public int getInt(String key, int defaultValue) {
		return sSettingsPreferences.getInt(key, defaultValue);
	}

	public long getLong(String key, long defaultValue) {
		return sSettingsPreferences.getLong(key, defaultValue);
	}

	public void saveUserInfo(String userName, String password) {
		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		editor.putString(USER_ID_KEY, encode(userName));
		editor.putString(USER_PASSWORD_KEY, encode(password));
		editor.commit();
	}

	public void clearUserInfo() {

		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		editor.remove(USER_ID_KEY);
		editor.remove(USER_PASSWORD_KEY);
		editor.remove(PHONE_FREE_QUERY);
		editor.remove(PHONE_CUR_MSG);
		editor.putString(AUTO_LOGIN, encode("114"));
		// editor.putString(IS_LOGININ, encode("119"));
		// editor.putBoolean(AUTO_LOGIN, false);
		// editor.putBoolean(IS_LOGININ, false);
		editor.remove(USER_BASE_INFO);
		editor.remove(M_CREDITS);
		editor.remove("m_credits");
		editor.remove(AUTHENTICATION_KEY);
		editor.remove(KEY_LOGOUT);
		editor.commit();
	}

	public void cleanUserOtherData() {
		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		editor.remove(PHONE_FREE_QUERY);
		editor.remove(PHONE_CUR_MSG);
		// editor.putString(IS_LOGININ, encode("119"));
		// editor.putBoolean(IS_LOGININ, false);
		editor.remove(USER_BASE_INFO);
		editor.remove(M_CREDITS);
		editor.remove(AUTHENTICATION_KEY);

		editor.remove(KEY_LOGOUT);

		editor.commit();
	}

	public String getUserName() {
		return decode(getString(USER_ID_KEY));
	}

	public String getPassword() {
		return decode(getString(USER_PASSWORD_KEY));
	}

	public void putResVersion(String key, long value) {
		SharedPreferences.Editor editor = sUdateInfosPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public void putResNewVersion(String key, long value) {
		SharedPreferences.Editor editor = sUdateInfosPreferences.edit();
		editor.putLong(NEW_STR + key, value);
		editor.commit();
	}

	public long getResOldVersion(String key, long defaultVersion) {
		return sUdateInfosPreferences.getLong(key, defaultVersion);
	}

	public long getResNewVersion(String key) {
		return sUdateInfosPreferences.getLong(NEW_STR + key, -1);
	}

	public void savePhoneFreeQuery(String info) {
		putString(PHONE_FREE_QUERY, info);
	}

	/**
	 * @return 套餐余额
	 */
	public String getPhoneFreeQuery() {
		return getString(PHONE_FREE_QUERY);
	}

	public void saveAutoLogin(boolean is) {
		putBoolean(AUTO_LOGIN, is);
	}

	/**
	 * @return 自动登录
	 */
	public boolean isAutoLogin() {

		return getBoolean(AUTO_LOGIN, false);
	}

	/**
	 * @return 返回是否需要指引页面
	 */
	public boolean isNeedGuide() {
		return getBoolean(ISNEEDGUIDE, true);
	}

	/**
	 * @param is
	 *            设置isNeedGuide 属性
	 */
	public void setNeedGuide(boolean is) {
		putBoolean(ISNEEDGUIDE, is);
	}

	/**
	 * @param info
	 *            账单查询
	 */
	// public void saveQueryBillHome(String info) {
	// putString(QUERY_BILL_HOME, info);
	// }

	/**
	 * @return 账单查询
	 */
	// public String getQueryBillHome() {
	// return getString(QUERY_BILL_HOME);
	// }

	/**
	 * @param m_credits
	 *            积分/M值
	 */
	public void saveMCredits(String m_credits) {
		putString(M_CREDITS, m_credits);
	}

	/**
	 * @return 积分/M值
	 */
	public String getMCredits() {
		return getString(M_CREDITS);
	}

	/**
	 * @return 判断用户登录
	 */
	// public boolean isLoginIn() {
	// return getBoolean(IS_LOGININ, false);
	// }

	/**
	 * @param is
	 *            判断用户登录
	 */
	// public void setLoginIn(boolean is) {
	// putBoolean(IS_LOGININ, is);
	// }

	/**
	 * @return 判断是否是LOGOUT_CODE
	 */
	public boolean getLoginOutCode() {
		return getBoolean(KEY_LOGOUT, true);
	}

	/**
	 * @param is
	 *            设置LOGOUT_CODE
	 */
	public void setLoginOut(boolean is) {
		putBoolean(KEY_LOGOUT, is);
	}

	/**
	 * @param number
	 *            widget使用的号码
	 */
	public void setWidgetPhoneNumber(String number) {
		putString(WIDGET_NUNBER, number);
	}

	/**
	 * @return widget使用的号码
	 */
	public String getWidgetPhoneNumber() {
		return getString(WIDGET_NUNBER);
	}

	public void savePhoneCurrMsg(String string) {
		putString(PHONE_CUR_MSG, string);
	}

	public String getPhoneCurrMsg() {
		return getString(PHONE_CUR_MSG);
	}

	public void saveUserBaseInfo(String info) {
		putString(USER_BASE_INFO, encode(info));
	}

	public String getUserBaseInfo() {
		return decode(getString(USER_BASE_INFO));
	}

	public String getLoginNames() {
		return decode(getString(USER_NAME_LOGINS));
	}

	public void setLoginNames(String names) {
		putString(USER_NAME_LOGINS, encode(names));
	}

	public void setToken(String token) {
		putString(AUTHENTICATION_KEY, encode(token));
	}

	public String getToken() {
		return decode(getString(AUTHENTICATION_KEY));
	}

	/**
	 * 保存预约信息
	 * 
	 * @param id
	 *            营业厅ID
	 * @param name
	 *            营业厅名称
	 * @param address
	 *            营业厅地址
	 */
	public void saveReservationInfo(String id, String name, String address) {
		putString(Preferences.HALL_NUMBER, id);
		putString(Preferences.HALL_NAME_POP, name);
		putString(Preferences.HALL_ADDRESS_POP, address);
		putLong(Preferences.ORDER_TIME, new Date().getTime());
	}

	/**
	 * 清除预约信息
	 */
	public void clearReservationInfo() {
		putString(Preferences.HALL_NUMBER, "");
		putLong(Preferences.ORDER_TIME, 0);
		putString(Preferences.HALL_NAME_POP, "");
		putString(Preferences.HALL_ADDRESS_POP, "");
	}

	/**
	 * 判断是否已预约营业厅（营业厅ID存在并且长度不为0）并且是否有效（预约时间在今天早上6点到晚上8点之间）
	 * 
	 * @return true-有效预约，false-无效预约
	 */
	public boolean isValidReservation() {
		Date date = new Date();
		Date morningOfToday = new Date(date.getYear(), date.getMonth(), date.getDate(), 6, 0, 0);
		Date eveningOfToday = new Date(date.getYear(), date.getMonth(), date.getDate(), 20, 0, 0);
		long orderTime = getLong(Preferences.ORDER_TIME, 0);
		// orderTime>morningOfToday&&orderTime<eveningOfToday&&
		String hallnum = getString(Preferences.HALL_NUMBER, "");
		if (orderTime > morningOfToday.getTime() && orderTime < eveningOfToday.getTime() && hallnum.length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param str
	 * @return 加密
	 */
	private String encode(String str) {
		if (TextUtils.isEmpty(str))
			return str;
		return Base64.encodeBytes(str.getBytes(), Base64.GZIP, Base64.PREFERRED_ENCODING);
	}

	/**
	 * @param str
	 * @return 解密
	 */
	private String decode(String str) {
		if (TextUtils.isEmpty(str))
			return str;
		byte[] data = Base64.decode(str, Base64.GZIP, Base64.PREFERRED_ENCODING);
		if (data == null)
			return "error";
		return new String(data);
	}

	public void savePushServerInfo(String serverIp, String serverPort) {
		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		editor.putString(PUSH_MESSAGE_SERVER_IP_KEY, serverIp);
		editor.putString(PUSH_MESSAGE_SERVER_PORT_KEY, serverPort);
		editor.commit();
	}

	public String getServerIp() {
		return getString(PUSH_MESSAGE_SERVER_IP_KEY);
	}

	public String getServerPort() {
		return getString(PUSH_MESSAGE_SERVER_PORT_KEY);
	}

	public void savePushMessageUserName(String userName) {
		putString(PUSH_MESSAGE_USERNAME_KEY, userName);
	}

	public void savePushMessagePassword(String password) {
		putString(PUSH_MESSAGE_PASSWORD_KEY, password);
	}

	public String getPushMessageUserName() {
		return sSettingsPreferences.getString(PUSH_MESSAGE_USERNAME_KEY, "testpush2");
	}

	public String getPushMessagePassword() {
		return sSettingsPreferences.getString(PUSH_MESSAGE_PASSWORD_KEY, "testpushpassword2");
	}

	/**
	 * @param defaultValue
	 * @return 流量超标处理模式
	 */
	public int getTrafficOverHandleMode(int defaultValue) {

		return getInt(Preferences.TRAFFIC_OVER_HANDLE_MODE_KEY, defaultValue);
	}

	/**
	 * 流量超标处理模式
	 * 
	 * @param value
	 */
	public void setTrafficOverHandleMode(int value) {
		putInt(Preferences.TRAFFIC_OVER_HANDLE_MODE_KEY, value);
	}

	/**
	 * @param defaultValue
	 * @return 流量超限是否提醒
	 */
	public boolean isTrafficNotificationFunction(boolean defaultValue) {
		return getBoolean(TRAFFIC_NOTIFICATION_FUNCTION_KEY, defaultValue);
	}

	/**
	 * @param value
	 *            流量超限是否提醒
	 */
	public void setTrafficNotificationFunction(boolean value) {
		putBoolean(TRAFFIC_NOTIFICATION_FUNCTION_KEY, value);
	}

	/**
	 * @param defaultValue
	 * @return 获取流量提醒模式
	 */
	public int getTrafficNotifictionMode(int defaultValue) {
		return getInt(TRAFFIC_NOTIFICATION_MODE_KEY, defaultValue);
	}

	/**
	 * @param value
	 *            设置流量提醒模式
	 */
	public void setTrafficNotifictionMode(int value) {
		putInt(TRAFFIC_NOTIFICATION_MODE_KEY, value);
	}

	/**
	 * @param defaultValue
	 * @return 获取流量提醒的值
	 */
	public int getTrafficThreshold(int defaultValue) {
		return getInt(TRAFFIC_THRESHOLD_KEY, defaultValue);
	}

	/**
	 * @param value
	 *            设置流量提醒的值
	 */
	public void setTrafficThreshold(int value) {
		putInt(TRAFFIC_THRESHOLD_KEY, value);
	}

	/**
	 * @return 用户协议是否签订
	 */
	public boolean isUserAgreementSigned() {
		return getBoolean(KEY_USER_AGREEMENT, false);
	}

	/**
	 * @param value
	 *            用户协议是否签订
	 */
	public void setUserAgreementSigned(boolean value) {
		putBoolean(KEY_USER_AGREEMENT, value);
	}

	public void clearAllInfo() {
		SharedPreferences.Editor editor = sSettingsPreferences.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 重置首页引导版本
	 * 
	 * @param versionCode
	 */
	public int getGuideVersion() {
		return getInt(KEY_HOME_PAGE_GUIDE_VERSION, 0);
	}

	/**
	 * 重置首页引导版本
	 * 
	 * @param versionCode
	 */
	public void setGuideVersion(int versionCode) {
		putInt(KEY_HOME_PAGE_GUIDE_VERSION, versionCode);
	}

	public void saveMenuVersion(String mMenuVersionKey, long mChildVersion) {
		// TODO Auto-generated method stub

	}

	public String getAuthentication() {
		return getString(KEY_AUTHENTICATION);
	}

	public void setAuthentication(String authentication) {
		putString(KEY_AUTHENTICATION, authentication);
	}

	public String getMobile() {
		return getString(KEY_MOBILE);
	}

	public void setMobile(String string) {
		putString(KEY_MOBILE, string);
	}

	public String getNotificationPassword() {
		return getString(KEY_NOTIFICATION_PASSWORD);
	}

	public void setNotificationPassword(String string) {
		putString(KEY_NOTIFICATION_PASSWORD, string);
	}

	public long getSSJFPageVerCode() {
		return getLong(KEY_SSJF_PAGE_VER_CODE, 0);
	}

	public void setSSJFPageVerCode(long code) {
		putLong(KEY_SSJF_PAGE_VER_CODE, code);
	}

	public String getSSJFPageUrl() {
		return getString(KEY_SSJF_PAGE_URL);
	}

	public void setSSJFPageUrl(String url) {
		putString(KEY_SSJF_PAGE_URL, url);
	}

	public String getCheckedNumber() {
		return getString(KEY_CHECKED_NUMBER);
	}

	public void setCheckedNumber(String number) {
		putString(KEY_CHECKED_NUMBER, number);
	}

	public void setSubAccount(String subAccount) {
		putString(KEY_SUBACCOUNT, subAccount);

	}

	public String getSubAccount() {
		return getString(KEY_SUBACCOUNT);
	}
}
