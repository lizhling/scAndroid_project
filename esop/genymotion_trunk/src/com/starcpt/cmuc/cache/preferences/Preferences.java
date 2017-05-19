package com.starcpt.cmuc.cache.preferences;

import com.starcpt.cmuc.CmucApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	//main config file' name 
	private final static String SETTING = "setting";
		
	private final static String USER_ID_KEY="user_id";
	private final static String USER_PASSWORD_KEY="user_password";
	private final static String COMMON_RES_VERSION_KEY="common_res_version";
	private final static String COMMON_RES_URL_KEY="common_url";
	private final static String BUSINESS_GUIDE_RES_VERSION_KEY="business_guide_res_version";
	private final static String BUSINESS_GUIDE_URL_KEY="business_guide_common_url";
	private final static String AUTHENTICATION_KEY="authentication";
	private final static String PHONENUMBER_KEY="mobile";
	private final static String PUSH_MESSAGE_USERNAME_KEY="push_message_username";
	private final static String PUSH_MESSAGE_PASSWORD_KEY="push_message_word";
	private final static String PUSH_MESSAGE_SERVER_IP_KEY="server_ip";
	private final static String PUSH_MESSAGE_SERVER_PORT_KEY="server_port";
	private final static String ALL_USER_NAMES_KEY="all_user_names";
	private final static String SCREEN_DIRECTION_KEY="screen_direction";
	private final static String SECURE_PASSWORD_KEY="secure_passweord";
	private final static String HIDDEN_IS_LOCK_KEY="hidden_lock";
	private final static String SCREEN_OFF_LOCK_KEY="screen_off_lock";
	private final static String AFTER_START_LOCK_KEY="after_start_lock";
	private final static String NONE_LOCK_KEY="no_lock";
	private final static String UPLOAD_FILE_NAME_KEY="upload_file";
	private final static String NEED_GUIDE="need_guide";
	private final static String PASSWORD_KEY="password_key";
	
	private static SharedPreferences sSettingsPreferences;
	private static SharedPreferences sMenusVersionOfUserConfig;
	private static Preferences instance;
	private Context mContext;
    private static final String ZIP_RES_VERSION_DEFAULT_VERSION="2012120402";
	public static final String USER_SPLIT="username";
	
	private Preferences(Context context) {
		// TODO Auto-generated constructor stub
		mContext=context;
		sSettingsPreferences = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
	}	
 
	public static Preferences getInstance(Context context){
		if (instance==null) {
			instance = new Preferences(context);
		}
		return instance;
	}
	
	public void savePushServerInfo(String serverIp,String serverPort){
		SharedPreferences.Editor editor =  sSettingsPreferences.edit();
		editor.putString(PUSH_MESSAGE_SERVER_IP_KEY,serverIp);
		editor.putString(PUSH_MESSAGE_SERVER_PORT_KEY,serverPort);
		editor.commit();
	}
	
	public String getServerIp(){
		return getString(PUSH_MESSAGE_SERVER_IP_KEY);
	}
	
	public String getServerPort(){
		return getString(PUSH_MESSAGE_SERVER_PORT_KEY);
	}
	
	public void saveUserInfo(String userName,String password){
		SharedPreferences.Editor editor =  sSettingsPreferences.edit();
		editor.putString(USER_ID_KEY, userName);
		editor.putString(USER_PASSWORD_KEY, password);
		editor.commit();
	}
	
	public void clearUserInfo(){
		SharedPreferences.Editor editor =  sSettingsPreferences.edit();
		editor.putString(USER_ID_KEY, null);
		editor.putString(USER_PASSWORD_KEY, null);
		editor.putString(AUTHENTICATION_KEY, null);
		editor.commit();
	}
	
	public String getUserName(){
		return getString(USER_ID_KEY);
	}
	
	public String getPassword(){
		return getString(USER_PASSWORD_KEY);
	}
	
	
	public String getString(String key){
		return sSettingsPreferences.getString(key, null);
	}

	public String getString(String key,String defaultStr){
		return sSettingsPreferences.getString(key, defaultStr);
	}

	public boolean getBoolean(String key){
		return sSettingsPreferences.getBoolean(key, false);
	}

	public void putString(String key,String value){
		SharedPreferences.Editor editor =  sSettingsPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public void putBoolean(String key, boolean value){
		SharedPreferences.Editor editor =  sSettingsPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
 
	public void putInt(String key, int value){
		SharedPreferences.Editor editor =  sSettingsPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void putLong(String key, long value){
		SharedPreferences.Editor editor =  sSettingsPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public void saveCommonResVersion(String commonResVersion){
		putString(COMMON_RES_VERSION_KEY,commonResVersion);
	}
	
	public String getCommonResVersion(){
		return sSettingsPreferences.getString(COMMON_RES_VERSION_KEY, ZIP_RES_VERSION_DEFAULT_VERSION);
	}
	public void saveCommonResUrl(String commonUrl){
		putString(COMMON_RES_URL_KEY,commonUrl);
	}
	
	public String getCommonResUrl(){
		return sSettingsPreferences.getString(COMMON_RES_URL_KEY, null);
	}
	
	public void saveBusinessGuideResVersion(String commonResVersion){
		putString(BUSINESS_GUIDE_RES_VERSION_KEY,commonResVersion);
	}
	
	public String getBusinessGuideResVersion(){
		return sSettingsPreferences.getString(BUSINESS_GUIDE_RES_VERSION_KEY, ZIP_RES_VERSION_DEFAULT_VERSION);
	}
	public void saveBusinessGuideResUrl(String commonUrl){
		putString(BUSINESS_GUIDE_URL_KEY,commonUrl);
	}
	
	public String getBusinessResUrl(){
		return sSettingsPreferences.getString(BUSINESS_GUIDE_URL_KEY, null);
	}
	
	public int getScreenDirection(){
		return sSettingsPreferences.getInt(SCREEN_DIRECTION_KEY, -1);
	}
	
	public void saveScreenDirection(int screenDirection){
		putInt(SCREEN_DIRECTION_KEY,screenDirection);
	}
	
	public String getUpLoadFileName(){
		return sSettingsPreferences.getString(UPLOAD_FILE_NAME_KEY, null);
	}
	
	public void saveUpLoadFileName(String uploadFileName){
		putString(UPLOAD_FILE_NAME_KEY,uploadFileName);
	}
	
	public void saveUserNameToAllUserNames(String userName){
		String allUserNames=getAllUserNames();
		if(allUserNames==null){
			allUserNames=userName;
		}else{
			if(!allUserNames.contains(userName))
			allUserNames+=(USER_SPLIT+userName);
		}
		putString(ALL_USER_NAMES_KEY,allUserNames);
	}
	
	public String getAllUserNames(){
		return sSettingsPreferences.getString(ALL_USER_NAMES_KEY, null);
	}
	
	
	public String getSecurePassword(){
		return sSettingsPreferences.getString(SECURE_PASSWORD_KEY, null);
	}
	
	public void saveSecurePassword(String securePassword){
		putString(SECURE_PASSWORD_KEY,securePassword);
	}
	
	public boolean getHiddenLock(){
		return sSettingsPreferences.getBoolean(HIDDEN_IS_LOCK_KEY, false);
	}
	
	public void saveHiddenLock(boolean hiddenLock){
		putBoolean(HIDDEN_IS_LOCK_KEY, hiddenLock);
	}
	
	public boolean getScreenOffLock(){
		return sSettingsPreferences.getBoolean(SCREEN_OFF_LOCK_KEY, false);
	}
	
	public void saveScreenOffLock(boolean screenOffLock){
		putBoolean(SCREEN_OFF_LOCK_KEY, screenOffLock);
	}
	
	public boolean getNoLock(){
		return sSettingsPreferences.getBoolean(NONE_LOCK_KEY, true);
	}
	
	public void saveNoLock(boolean noLock){
		putBoolean(NONE_LOCK_KEY, noLock);
	}
	
	public boolean getAfterStartLock(){
		return sSettingsPreferences.getBoolean(AFTER_START_LOCK_KEY, false);
	}
	
	public void saveAfterStartLock(boolean noLock){
		putBoolean(AFTER_START_LOCK_KEY, noLock);
	}
	
	public void saveAuthentication(String authentication){
		putString(AUTHENTICATION_KEY, authentication);
	}
	
	public String getAuthentication(){
		return sSettingsPreferences.getString(AUTHENTICATION_KEY, "");
	}
	
	public void saveMobile(String mobile){
		putString(PHONENUMBER_KEY, mobile);
	}
	
	public String getMobile(){
		return sSettingsPreferences.getString(PHONENUMBER_KEY, "");
	}
	
	public void saveNeedGuide(boolean needGuide){
		putBoolean(NEED_GUIDE, needGuide);
	}
	
	public boolean isNeedGuide(){
		return sSettingsPreferences.getBoolean(NEED_GUIDE, true);
	}
	
	
	public void savePushMessageUserName(String userName){
		putString(PUSH_MESSAGE_USERNAME_KEY,userName);
	}
	
	public void savePushMessagePassword(String password){
		putString(PUSH_MESSAGE_PASSWORD_KEY,password);
	}
	
	public String getPushMessageUserName(){
		return sSettingsPreferences.getString(PUSH_MESSAGE_USERNAME_KEY, "testpush2");
	}
	
	public String getPushMessagePassword(){
		return sSettingsPreferences.getString(PUSH_MESSAGE_PASSWORD_KEY, "testpushpassword2");
	}
	
	public void createMenusVersionOfUserConfig(String userName){
		sMenusVersionOfUserConfig=mContext.getSharedPreferences(userName, Context.MODE_PRIVATE);
	}
	
	public void saveMenuVersion(String menuVersionKey,long menuVersion){
		if(menuVersionKey.equals(CmucApplication.IDENTITY_VERIFICATION_MENU_ID+"")){
			putLong(menuVersionKey, menuVersion);
			return;
		}
		if(sMenusVersionOfUserConfig!=null){
				SharedPreferences.Editor editor =  sMenusVersionOfUserConfig.edit();
				editor.putLong(menuVersionKey, menuVersion);
				editor.commit();
		}
	}
	
	public long getMenuVersion(String menuVersionKey){
		if(menuVersionKey.equals(CmucApplication.IDENTITY_VERIFICATION_MENU_ID+"")){
			return sSettingsPreferences.getLong(menuVersionKey, -1);
		}
		if(sMenusVersionOfUserConfig!=null){
			return sMenusVersionOfUserConfig.getLong(menuVersionKey, -1);
		}else{
			return -1;
		}
		
	}
}
