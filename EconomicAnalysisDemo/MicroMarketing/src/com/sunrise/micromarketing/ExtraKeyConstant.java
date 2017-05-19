package com.sunrise.micromarketing;

import java.io.File;

public interface ExtraKeyConstant {

	// directory
	public static final String APP_SD_PATH_NAME = "micro_marketing";
	public static final String APP_FILE_APK_DIR = APP_SD_PATH_NAME + File.separator + "apk";
	public static final String APP_FILE_APK_NAME = "update.apk";
	public static final String APP_FILE_JSON_DATA_DIR = APP_SD_PATH_NAME + File.separator + "json";
	public static final String APP_FILE_HTML_DATA_DIR = APP_SD_PATH_NAME + File.separator + "html";
	public static final String APP_FILE_COMMON_RES_DATA_DIR = APP_FILE_HTML_DATA_DIR + File.separator + "common_res";
	public static final String APP_FILE_IMAGE_DATA_DIR = APP_SD_PATH_NAME + File.separator + "image";
	public static final String APP_CAPTURE_IMAGE_DIR = APP_SD_PATH_NAME + File.separator + "capture";
	public static final String APP_CRASH_LOG_DIR = APP_SD_PATH_NAME + File.separator + "crash";
	public static final String APP_FILE_COMMON_RES_IMAGES_OTHER_DATA_DIR = APP_FILE_COMMON_RES_DATA_DIR + File.separator + "images" + File.separator + "other";
	public static final String APP_SKIN_BASE_PATH_DIR = APP_SD_PATH_NAME + File.separator + "skin";
	public static final String APP_SKIN_CACHE_DIR = APP_SKIN_BASE_PATH_DIR + File.separator + "cache";
	public static final String APP_SKIN_INSTALLED_DIR = APP_SKIN_BASE_PATH_DIR + File.separator + "installed";
	public static final String APP_BUSINESS_GUIDE_TOP_DIR = APP_SD_PATH_NAME + File.separator + "business_guide";
	public static final String APP_BUSINESS_GUIDE_DIR = APP_BUSINESS_GUIDE_TOP_DIR + File.separator + "guide";

	public static final String HTML_FILE_NAME = "index.html";

	public static final String KEY_BUNDLE = "bundle";
	public static final String KEY_URL = "url";

	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_IMSI = "imsi";
	public static final String KEY_IMEI = "imei";

	public static final String KEY_LAST_MODIFY = "last modify";

	public static final String KEY_HTM_ZIP_VERSION_INFOS = "zipHtmlVersions";
	/**
	 * 文件下载信息，用于preference中查询
	 */
	public static final String APP_DOWNLOAD_INFO = "app download info";

	public static final String ACTION_REFRESH_USER_DATA = "com.sunrise.micromarketing.refreshUserData";
	
	public static final String KEY_CONTENT = "content";
	public static final String KEY_TIME = "time";

}
