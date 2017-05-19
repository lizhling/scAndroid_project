package com.cache.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.util.ZipUtils;

public class Preferences {
	// main config file' name
		private final static String SETTING = "setting";
		private final static String UPDATE_INFO_VERSIONS = "update_info_versions";
		private final static String CACHE = "cache";

		private final static String KEY_USER_ID = "user_id";
		private final static String KEY_USER_PASSWORD = "user_password";
		private final static String KEY_IS_AUTO_LOGIN = "autoLogin";
		private static final String KEY_SUB_ACCOUNT = "subAccount";
		private static final String USER_NAME_LOGINS = "logined user numbers";// 登录过的用户名

		private final static String COMMON_RES_VERSION_KEY = "common_res_version";
		private final static String KEY_COMMON_RES_URL = "common_url";
		private final static String BUSINESS_GUIDE_RES_VERSION_KEY = "business_guide_res_version";
		private final static String BUSINESS_GUIDE_URL_KEY = "business_guide_common_url";
		private final static String AUTHENTICATION_KEY = "authentication";
		private final static String PHONENUMBER_KEY = "mobile";
		private final static String PUSH_MESSAGE_USERNAME_KEY = "push_message_username";
		private final static String PUSH_MESSAGE_PASSWORD_KEY = "push_message_word";
		private final static String PUSH_MESSAGE_SERVER_IP_KEY = "server_ip";
		private final static String PUSH_MESSAGE_SERVER_PORT_KEY = "server_port";
		private final static String SCREEN_DIRECTION_KEY = "screen_direction";
		private final static String HIDDEN_IS_LOCK_KEY = "hidden_lock";
		private final static String SCREEN_OFF_LOCK_KEY = "screen_off_lock";
		private final static String AFTER_START_LOCK_KEY = "after_start_lock";
		private final static String NONE_LOCK_KEY = "no_lock";
		private final static String UPLOAD_FILE_NAME_KEY = "upload_file";
		private final static String NEED_GUIDE = "need_guide";
		private static final String KEY_CHECKED_NUMBER = "checked number";
		private final static String NEW_STR = "new";
		/** 搜索记录 */
		private static final String KEY_SEARCH_RECORDES = "search recordes";

		private static final String KEY_PHONE_TIME = "phone time";

		private static SharedPreferences sSettingsPreferences;
		private static SharedPreferences sUdateInfosPreferences;
		private static SharedPreferences sCachePreferences;

		private static Preferences instance;
		private static final String ZIP_RES_VERSION_DEFAULT_VERSION = "2012120402";
		public static final String USER_SPLIT = "username";
		private static final String KEY_GROUP_ID = "group id";
		/** 最大签到距离 */
		private static final String KEY_MAX_SIGN_RANGE = "max_sign_range";

		private static final String KEY_IMAGE_NAME = "KEY_IMAGE_NAME";

		private Preferences(Context context) {
			sSettingsPreferences = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
			sUdateInfosPreferences = context.getSharedPreferences(UPDATE_INFO_VERSIONS, Context.MODE_PRIVATE);
			sCachePreferences = context.getSharedPreferences(CACHE, Context.MODE_PRIVATE);

			clearCache();
		}

		public static Preferences getInstance(Context context) {
			if (instance == null) {
				instance = new Preferences(context);
			}
			return instance;
		}

		// 保存消息推送服务器和端口号
		public void savePushServerInfo(String serverIp, String serverPort) {
			SharedPreferences.Editor editor = sSettingsPreferences.edit();
			editor.putString(PUSH_MESSAGE_SERVER_IP_KEY, serverIp);
			editor.putString(PUSH_MESSAGE_SERVER_PORT_KEY, serverPort);
			editor.commit();
		}

		// 获取消息推送服务器IP
		public String getServerIp() {
			return getString(PUSH_MESSAGE_SERVER_IP_KEY);
		}

		// 获取消息推送端口号
		public String getServerPort() {
			return getString(PUSH_MESSAGE_SERVER_PORT_KEY);
		}

		public void clearCache() {
			sCachePreferences.edit().clear().commit();
		}

		public String getString(String key) {
			return sSettingsPreferences.getString(key, null);
		}

		public String getString(String key, String defaultStr) {
			return sSettingsPreferences.getString(key, defaultStr);
		}

		public boolean getBoolean(String key) {
			return sSettingsPreferences.getBoolean(key, false);
		}

		public void putString(String key, String value) {
			SharedPreferences.Editor editor = sSettingsPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		}

		public void putBoolean(String key, boolean value) {
			SharedPreferences.Editor editor = sSettingsPreferences.edit();
			editor.putBoolean(key, value);
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

		public void saveCommonResVersion(String commonResVersion) {
			putString(COMMON_RES_VERSION_KEY, commonResVersion);
		}

		public String getCommonResVersion() {
			return sSettingsPreferences.getString(COMMON_RES_VERSION_KEY, ZIP_RES_VERSION_DEFAULT_VERSION);
		}

		public void saveCommonResUrl(String commonUrl) {
			putString(KEY_COMMON_RES_URL, commonUrl);
		}

		public String getCommonResUrl() {
			return sSettingsPreferences.getString(KEY_COMMON_RES_URL, null);
		}

		public void saveBusinessGuideResVersion(String commonResVersion) {
			putString(BUSINESS_GUIDE_RES_VERSION_KEY, commonResVersion);
		}

		public String getBusinessGuideResVersion() {
			return sSettingsPreferences.getString(BUSINESS_GUIDE_RES_VERSION_KEY, ZIP_RES_VERSION_DEFAULT_VERSION);
		}

		public void saveBusinessGuideResUrl(String commonUrl) {
			putString(BUSINESS_GUIDE_URL_KEY, commonUrl);
		}

		public String getBusinessResUrl() {
			return sSettingsPreferences.getString(BUSINESS_GUIDE_URL_KEY, null);
		}

		public int getScreenDirection() {
			return sSettingsPreferences.getInt(SCREEN_DIRECTION_KEY, -1);
		}

		public void saveScreenDirection(int screenDirection) {
			putInt(SCREEN_DIRECTION_KEY, screenDirection);
		}

		public String getUpLoadFileName() {
			return sSettingsPreferences.getString(UPLOAD_FILE_NAME_KEY, null);
		}

		public void saveUpLoadFileName(String uploadFileName) {
			putString(UPLOAD_FILE_NAME_KEY, uploadFileName);
		}

		public boolean getHiddenLock() {
			return sSettingsPreferences.getBoolean(HIDDEN_IS_LOCK_KEY, false);
		}

		public void saveHiddenLock(boolean hiddenLock) {
			putBoolean(HIDDEN_IS_LOCK_KEY, hiddenLock);
		}

		public boolean getScreenOffLock() {
			return sSettingsPreferences.getBoolean(SCREEN_OFF_LOCK_KEY, false);
		}

		public void saveScreenOffLock(boolean screenOffLock) {
			putBoolean(SCREEN_OFF_LOCK_KEY, screenOffLock);
		}

		public boolean getNoLock() {
			return sSettingsPreferences.getBoolean(NONE_LOCK_KEY, true);
		}

		public void saveNoLock(boolean noLock) {
			putBoolean(NONE_LOCK_KEY, noLock);
		}

		public boolean getAfterStartLock() {
			return sSettingsPreferences.getBoolean(AFTER_START_LOCK_KEY, false);
		}

		public void saveAfterStartLock(boolean noLock) {
			putBoolean(AFTER_START_LOCK_KEY, noLock);
		}

		public void saveAuthentication(String authentication) {
			putString(AUTHENTICATION_KEY, authentication);
		}

		public String getAuthentication() {
			return sSettingsPreferences.getString(AUTHENTICATION_KEY, "");
		}

		public void saveMobile(String mobile) {
			putString(PHONENUMBER_KEY, mobile);
		}

		public String getMobile() {
			return sSettingsPreferences.getString(PHONENUMBER_KEY, "");
		}

		public void saveNeedGuide(boolean needGuide) {
			putBoolean(NEED_GUIDE, needGuide);
		}

		public boolean isNeedGuide() {
			return sSettingsPreferences.getBoolean(NEED_GUIDE, true);
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

		public String getUserName() {
			return decode(getString(KEY_USER_ID));
		}

		public void saveUserName(String value) {
			putString(KEY_USER_ID, encode(value));
		}

		public boolean isAutoLogin() {
			return getBoolean(KEY_IS_AUTO_LOGIN);
		}

		public void saveAutoLogin(boolean is) {
			putBoolean(KEY_IS_AUTO_LOGIN, is);
		}

		public String getPassword() {
			return decode(getString(KEY_USER_PASSWORD));
		}

		public void savePassword(String value) {
			putString(KEY_USER_PASSWORD, encode(value));
		}

		public String getSubAccount() {
			return decode(getString(KEY_SUB_ACCOUNT));
		}

		public void saveSubAccount(String value) {
			putString(KEY_SUB_ACCOUNT, encode(value));
		}

		public void setLoginNames(String names) {
			putString(USER_NAME_LOGINS, encode(names));
		}

		public String getLoginNames() {
			return decode(getString(USER_NAME_LOGINS));
		}

		/**
		 * @param str
		 * @return 加密
		 */
		private String encode(String str) {
			if (TextUtils.isEmpty(str))
				return str;
			return ZipUtils.encodeDes(str);
		}

		/**
		 * @param str
		 * @return 解密
		 */
		private String decode(String str) {
			if (TextUtils.isEmpty(str))
				return str;
			return ZipUtils.decodeDes(str);
		}

		public void putResVersion(String key, String value) {
			SharedPreferences.Editor editor = sUdateInfosPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		}

		public void saveUserInfo(String userName, String password) {
			SharedPreferences.Editor editor = sSettingsPreferences.edit();
			editor.putString(KEY_USER_ID, encode(userName));
			editor.putString(KEY_USER_PASSWORD, encode(password));
			editor.commit();
		}

		public String getCheckedNumber() {
			return getString(KEY_CHECKED_NUMBER);
		}

		public void setCheckedNumber(String number) {
			putString(KEY_CHECKED_NUMBER, number);
		}

		public void putResNewVersion(String key, long value) {
			SharedPreferences.Editor editor = sUdateInfosPreferences.edit();
			editor.putLong(NEW_STR + key, value);
			editor.commit();
		}

		public long getResNewVersion(String key, long defaultVersion) {
			return sUdateInfosPreferences.getLong(NEW_STR + key, defaultVersion);
		}

		public long getResNewVersion(String key) {
			return sUdateInfosPreferences.getLong(NEW_STR + key, -1);
		}

		public void setPhoneTime(String time) {
			putString(KEY_PHONE_TIME, time);

		}

		public String getPhoneTime() {
			return getString(KEY_PHONE_TIME);
		}

		public String getGroupId() {
			return getString(KEY_GROUP_ID);
		}

		public void saveGroupId(String groupID) {
			putString(KEY_GROUP_ID, groupID);

		}

		public String getImageName() {
			return getString(KEY_IMAGE_NAME);
		}

		public void saveImageName(String name) {
			putString(KEY_IMAGE_NAME, name);

		}

		/**
		 * @param str
		 *            需要保存的搜索关键字
		 */
		public void saveSearchKeyword(String str) {
			if (TextUtils.isEmpty(str))
				return;
			String[] keywords = getSearchKeyword();
			if (keywords != null)
				for (int i = 0; i < keywords.length; ++i) {
					if (str.contains(keywords[i])) {
						keywords[i] = str;
						break;
					}
					if (keywords[i].contains(str)) {
						return;
					}
				}

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < keywords.length; ++i) {
				if (i != 0)
					sb.append(',');
				sb.append(keywords[i]);
			}
			putString(KEY_SEARCH_RECORDES, sb.toString());
		}

		/** @return 获取搜索关键字列表 */
		public String[] getSearchKeyword() {
			String temp = getString(KEY_SEARCH_RECORDES);
			if (temp == null)
				return null;

			if (temp.indexOf(',') == -1)
				return new String[] { temp };

			return temp.split(",");
		}

		public void saveMaxSignRange(long newVersionCode) {
			putLong(KEY_MAX_SIGN_RANGE, newVersionCode);
		}

		public long getMaxSignRange() {
			return sSettingsPreferences.getLong(KEY_MAX_SIGN_RANGE, 10);
		}

		public void putCacheString(String key, String value) {
			Editor edit = sCachePreferences.edit();
			edit.putString(key, value);
			edit.commit();
		}

		public String getCacheString(String key) {
			return gutCacheString(key, null);
		}

		public String gutCacheString(String key, String defalut) {
			return sCachePreferences.getString(key, defalut);
		}
}
