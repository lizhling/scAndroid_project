package com;

import java.io.File;

public interface ExtraKeyConstant {

	// directory
	public static final String APP_SD_PATH_NAME = "fristDemo_test";
	public static final String APP_FILE_APK_DIR = APP_SD_PATH_NAME + File.separator + "apk";
	public static final String APP_FILE_APK_NAME = "update.apk";
	public static final String APP_FILE_JSON_DATA_DIR = APP_SD_PATH_NAME + File.separator + "json";
	public static final String APP_FILE_HTML_DATA_DIR = APP_SD_PATH_NAME + File.separator + "html";
	public static final String APP_FILE_COMMON_RES_DATA_DIR = APP_FILE_HTML_DATA_DIR + File.separator + "common_res";
	public static final String APP_FILE_IMAGE_DATA_DIR = APP_FILE_HTML_DATA_DIR + File.separator + "image";
	public static final String APP_CAPTURE_IMAGE_DIR = APP_FILE_HTML_DATA_DIR + File.separator + "capture";
	public static final String APP_CHENNEL_IMAGE_DIR = APP_FILE_HTML_DATA_DIR + File.separator + "channelImage";
	public static final String APP_CRASH_LOG_DIR = APP_FILE_HTML_DATA_DIR + File.separator + "crash";
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
	public static final String KEY_TOKEN = "token";
	public static final String KEY_IMSI = "imsi";
	public static final String KEY_IMEI = "imei";
	public static final String KEY_CHL_NAME = "chlName";
	public static final String KEY_PAGE = "page";
	public static final String KEY_PAGE_SIZE = "pageSize";
	public static final String KEY_AUTH = "KEY_AUTH";
	public static final String KEY_CHL_ID = "chlId";
	public static final String KEY_JSON_STR = "KEY_JSON_STR";

	public static final String KEY_LAST_MODIFY = "last modify";

	public static final String KEY_HTM_ZIP_VERSION_INFOS = "zipHtmlVersions";
	/**
	 * 文件下载信息，用于preference中查询
	 */
	public static final String KEY_APP_DOWNLOAD_INFO = "app download info";

	public static final String ACTION_REFRESH_USER_DATA = "com.sunrise.micromarketing.refreshUserData";

	public static final String ACTION_REFRESH_OFFLINE_MAP_STATE = "com.sunrise.micromarketing.refreshOfflineMapState";

	public static final int COMMAND_START = 0;
	public static final int COMMAND_REMOVE = 1;
	public static final int COMMAND_PAUSE = 2;
	public static final int COMMAND_UPDATE = 3;

	public static final String KEY_CONTENT = "content";
	public static final String KEY_TIME = "time";

	public static final String KEY_FRAGMENT = "fragment";

	public static final String KEY_FINISH_ACTIVITY = "isFinishActivity";

	/** js返回格式 */
	public static final String FORMAT_JS_RETURN = "{\"resultCode\":%d,\"resultMsg\":\"%s\"}";

	/** 指标展示方式 ****/
	public static final String FORMAT_INDEXSORT_SHOW_G4_TARIFF_ADD = "%s\n4G新增业务：%s\n最近更新：%s";
	public static final String FORMAT_INDEXSORT_SHOW_G4_TEAM_SALE = "%s\n4G终端销售：%s\n最近更新：%s";
	public static final String FORMAT_INDEXSORT_SHOW_BROADBAND_NUMS = "%s\n新增宽带：%s\n最近更新：%s";
	public static final String FORMAT_NOTICE_SIGN_UNABLE = "您当前距离为%.0f米，超出允许签到范围%d米，请靠近再签到……";

	public static final String FORMAT_DATA_AND_TIME = "%d-%02d-%02d %02d点%02d分";
	public static final String PATTERN_DATA_AND_TIME = "yyyy-MM-dd HH点mm分";
	public static final String FORMAT_DATA = "yyyy-MM-dd";
	public static final String PATTERN_NEW_VERSION = "yyyy-MM-dd HH点mm分";

	public static final String FORMAT_SHOWING_TIME = "yyyy-M-d HH点mm分";
	public static final String FORMAT_PARAM_TIME = "yyyyMMddHHmmss";

	String FORMAT_SHARE_OF_CHANNELS = "渠道份额[%s%s](%%)";

	/** 渠道的颜色（移动联通电信） */
	int[] COLORS_OF_CHANNEL_TYPE = { 0xff0085d0, 0xffffa239, 0xff00ff42 };

	/** 二维码拍照请求 **/
	public static final int REQUEST_CODE_MUPCA_CAPTURE = 0xa2;

	/** 前往地图显示 */
	public static final int REQUEST_GO_MAP_SHOW = 0x430;

	/** 首页tabIndex */
	public static final int ID_TAB_ITEM_CHANNEL_MAP = 20;
	public static final int ID_TAB_ITEM_CHANNEL_LIST = 21;
	public static final int ID_TAB_ITEM_CHANNEL_TREE = 22;
	public static final int ID_TAB_ITEM_CHANNEL_COLLECT = 23;
	public static final int ID_TAB_ITEM_CHANNEL_SCAN = 24;
	public static final int ID_TAB_ITEM_CHANNEL_REMUNERATION = 25;
	public static final int ID_TAB_ITEM_CHANNEL_DETAIL = 420;

	public static final int[] RES_ID_MARKER_TOP_10 = { 
//		R.drawable.icon_marka, R.drawable.icon_markb, R.drawable.icon_markc, R.drawable.icon_markd,
//			R.drawable.icon_marke, R.drawable.icon_markf, R.drawable.icon_markg, R.drawable.icon_markh, R.drawable.icon_marki, R.drawable.icon_markj 
			};
	public static final int[] RES_ID_MARKER_INDEX = { 
//		R.drawable.ic_lacation_3, R.drawable.ic_lacation_6, R.drawable.ic_lacation_4, R.drawable.ic_lacation_5,
//			R.drawable.ic_lacation_2, R.drawable.ic_lacation_1 
			};

	/***** 缓存KEY *****/
	String KEY_CACHE_HALL_NEARBY_LATLNG = "key_halls_nearby_latlng";
	String KEY_MY_LOCATION_LATITUDE = "key_my_location_latitude";
	String KEY_MY_LOCATION_LONGITUDE = "key_my_location_longitude";
	String KEY_CITY = "key_city";
	String KEY_SHARE_OF_CHANNEL_ALL_CITYS = "key_share_of_channel_all_citys";
	String KEY_SHARE_OF_CHANNEL_ALL_CITYS1 = "key_share_of_channel_all_citys1";
}

