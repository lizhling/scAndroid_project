package com.sunrise.scmbhc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.androidpn.client.ServiceManager;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;

import com.baidu.mapapi.SDKInitializer;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.cache.download.DownFileManager;
import com.sunrise.scmbhc.cache.preferences.Preferences;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.AllMenus;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.CommonlySearchs;
import com.sunrise.scmbhc.entity.CommonlyUsedBusinessMenus;
import com.sunrise.scmbhc.entity.ContentInfo;
import com.sunrise.scmbhc.entity.ContentInfos;
import com.sunrise.scmbhc.entity.MobileBusinessHall;
import com.sunrise.scmbhc.entity.PreferentialInfo;
import com.sunrise.scmbhc.entity.PreferentialInfos;
import com.sunrise.scmbhc.entity.SearchTag;
import com.sunrise.scmbhc.net.ServerClient;
import com.sunrise.scmbhc.task.TaskManager;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.DatasUpdateObservable;
import com.sunrise.scmbhc.utils.FileUtils;
import com.sunrise.scmbhc.utils.LogUtlis;
import com.sunrise.scmbhc.utils.coding.DesCrypCoding;

public class App extends Application {
	private static final String TAG = "App";
	public static final boolean test = false;
	public static final boolean prepareTestData = false;
	public static final String sChinaMobileNetFlag = "10086.cn";
	public static final String sHttpFlag = "http://";
	public static final String sHttpsFlag = "https://";
	public static int SCREEN_WIDTH;
	public static int sAPKVersionCode = 0;
	// public static int debug = 1;
	public static String sAPKVersionName = "0.0";
	// public static final String sStrKey = "GzfagN4PydclRikzLIrodmub";// fuheng
	// public static final String sStrKey = "zNgx857rDjoaXefje0RNCxEh";//
	// qinhubao
	// public static final String sStrKey = "Mw4vSUtRFpBy2eGj9ZqUghd8";// guiban
	public static final String sStrKey = "PDYFNVaGtMm3aGFfqN8d2iAf";// 签名打包时候用
	private static String pwd = null;
	public static ServerClient sServerClient = null;
	public static TaskManager sTaskManager = null;
	public static DownFileManager sDownFileManager = null;
	public static ArrayList<PreferentialInfo> sScrollPreferentialInfos = null;
	public static ArrayList<PreferentialInfo> sPreferentialListInfos = null;
	public static ArrayList<PreferentialInfo> sAllPreferentialInfos = null;
	public static ArrayList<BusinessMenu> sCommonlyUsedBusiness = null;
	public static ArrayList<ContentInfo> sContentInfoList = null;
	public static ArrayList<SearchTag> sCommonlySearchs = null;
	public static ArrayList<BusinessMenu> sSearchBusinesses = null;
	public static ArrayList<PreferentialInfo> sSearchPreferentialInfos = null;
	public static ArrayList<MobileBusinessHall> sMobileBusinessHalls = null;
	public static DatasUpdateObservable sScrollsPreferentialInfosObservable = null;
	public static DatasUpdateObservable sCommonlyBusinessObservable = null;
	public static DatasUpdateObservable sContenInfoObservable = null;
	public static DatasUpdateObservable sCommonlySearchsObservable = null;
	public static List<Activity> sActivityList = null;
	public static Preferences sSettingsPreferences;
	public static boolean sAppRunning = false;
	public static App sContext;
	private static ServiceManager mServiceManager;

	public void onCreate() {
		super.onCreate();
		sContext = this;
		initApkVersionInfo();
		init();

		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
	}

	private void init() {
		initProperties();
		// 消息推送初始化用户名和密码 及服务器地址和端口
		String namepwd = CommUtil.getAppUUID(sContext);
		sSettingsPreferences = Preferences.getInstance(sContext);
		App.sSettingsPreferences.savePushMessageUserName(namepwd);
		App.sSettingsPreferences.savePushMessagePassword(namepwd);
		
		/*
		 * 消息摇头服务器 172.20.58.48 测试 61.235.80.178 正式 :5222
		 */
//		sSettingsPreferences.savePushServerInfo("172.20.58.36", "5222");
		sSettingsPreferences.savePushServerInfo("183.221.33.188", "8094");

		createFileStoreDir();
		getScreenSize();
		initDataFromLocal();
		if (App.getServiceManager() != null) {
			App.getServiceManager().stopService();
			App.setServiceManager(null);
		}
		App.setServiceManager(CommUtil.startPushMessageService(sContext));
	}

	private void initProperties() {
		sServerClient = ServerClient.getInstance();
		sTaskManager = TaskManager.getInstance();
		sDownFileManager = DownFileManager.getInstance();
		sScrollPreferentialInfos = new ArrayList<PreferentialInfo>();
		sPreferentialListInfos = new ArrayList<PreferentialInfo>();
		sAllPreferentialInfos = new ArrayList<PreferentialInfo>();
		sCommonlyUsedBusiness = new ArrayList<BusinessMenu>();
		sContentInfoList = new ArrayList<ContentInfo>();
		sCommonlySearchs = new ArrayList<SearchTag>();
		sSearchBusinesses = new ArrayList<BusinessMenu>();
		sSearchPreferentialInfos = new ArrayList<PreferentialInfo>();
		sMobileBusinessHalls = new ArrayList<MobileBusinessHall>();

		sScrollsPreferentialInfosObservable = new DatasUpdateObservable();
		sCommonlyBusinessObservable = new DatasUpdateObservable();
		sContenInfoObservable = new DatasUpdateObservable();
		sCommonlySearchsObservable = new DatasUpdateObservable();
		sActivityList = new ArrayList<Activity>();
	}

	private void initScrollPreferentialInfoFromLocal() {
		try {
			String jsonStr = FileUtils.readDataFile(sContext, App.AppDirConstant.APP_PREFERENTIALINFOS_JSON_NAME);
			PreferentialInfos preferentialInfos;
			if (jsonStr != null) {
				preferentialInfos = JsonUtils.parseJsonStrToObject(jsonStr, PreferentialInfos.class);
				if (preferentialInfos != null) {
					sScrollPreferentialInfos.clear();
					sPreferentialListInfos.clear();
					for (PreferentialInfo preferentialInfo : preferentialInfos.getDatas()) {
						if (preferentialInfo.getType() == PreferentialInfo.SCROLL_TYPE) {
							sScrollPreferentialInfos.add(preferentialInfo);
						} else {
							sPreferentialListInfos.add(preferentialInfo);
						}
						App.sAllPreferentialInfos.add(preferentialInfo);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initCommonlySearchFromLocal() {
		try {
			String jsonStr = FileUtils.readDataFile(sContext, App.AppDirConstant.APP_COMNONLY_SEARCCH_JSON_NAME);
			CommonlySearchs commonlySearchs;
			if (jsonStr != null) {
				commonlySearchs = JsonUtils.parseJsonStrToObject(jsonStr, CommonlySearchs.class);
				if (commonlySearchs != null) {
					sCommonlySearchs.clear();
					sCommonlySearchs.addAll(commonlySearchs.getDatas());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initMobileBusinessHallFromLocal() {
		String mobileBusinessHallsStr = null;
		try {
			mobileBusinessHallsStr = FileUtils.readDataFile(sContext, AppDirConstant.APP_BUSINESS_HALL_INFOS_JSON_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mobileBusinessHallsStr == null) {
			mobileBusinessHallsStr = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_BUSINESS_HALL_INFOS_JSON_NAME);
		}
		ArrayList<MobileBusinessHall> halls = CommUtil.getMobileBusinessHalls(mobileBusinessHallsStr);
		sMobileBusinessHalls.clear();
		sMobileBusinessHalls.addAll(halls);
	}

	private void initCommonlyBusinessMenusFromLocal() {
		String jsonStr = null;
		try {
			jsonStr = FileUtils.readDataFile(sContext, AppDirConstant.APP_COMMONLY_BUSINESS_JSON_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (jsonStr == null) {
			jsonStr = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_COMMONLY_BUSINESS_JSON_NAME);
		}
		CommonlyUsedBusinessMenus commonlyUsedBusinessMenus = JsonUtils.parseJsonStrToObject(jsonStr, CommonlyUsedBusinessMenus.class);
		App.sCommonlyUsedBusiness.clear();
		App.sCommonlyUsedBusiness.addAll(commonlyUsedBusinessMenus.getDatas());
	}

	private void initContentInfoMsgFromLocal() {
		if (prepareTestData) {
			String jsonStrnotice = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_SYSTEM_NOTICE_JSON_NAME);
			String jsonStrguide = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_USER_GUIDE_JSON_NAME);
			String jsonStrproblem = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_COMMONLY_PROBLEM_JSON_NAME);
			ContentInfos contentInfosnotice = JsonUtils.parseJsonStrToObject(jsonStrnotice, ContentInfos.class);
			ContentInfos contentInfosguide = JsonUtils.parseJsonStrToObject(jsonStrguide, ContentInfos.class);
			ContentInfos contentInfosproblem = JsonUtils.parseJsonStrToObject(jsonStrproblem, ContentInfos.class);
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
			LogUtlis.showLogD(TAG, "contentinfos:" + jsonStrnotice + jsonStrguide + jsonStrproblem);
		} else {
			initContentInfoMsg();
		}
	}

	private void initContentInfoMsg() {
		String jsonStrnotice = null;
		String jsonStrguide = null;
		// String jsonStrproblem = null;
		try {
			jsonStrnotice = FileUtils.readDataFile(sContext, AppDirConstant.APP_SYSTEM_NOTICE_JSON_NAME);
			LogUtlis.showLogI(TAG, "from net jsonStrnotice:" + jsonStrnotice);
			jsonStrguide = FileUtils.readDataFile(sContext, AppDirConstant.APP_USER_GUIDE_JSON_NAME);
			LogUtlis.showLogI(TAG, "from net  jsonStrguide:" + jsonStrguide);
			// jsonStrproblem = FileUtils.readDataFile(sContext,
			// AppDirConstant.APP_COMMONLY_PROBLEM_JSON_NAME);
			// LogUtlis.showLogI(TAG, "from net jsonStrproblem:" +
			// jsonStrproblem);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (jsonStrnotice == null) {
			jsonStrnotice = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_SYSTEM_NOTICE_JSON_NAME);
			LogUtlis.showLogI(TAG, "from local jsonStrnotice:" + jsonStrnotice);
			jsonStrguide = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_USER_GUIDE_JSON_NAME);
			LogUtlis.showLogI(TAG, "from local  jsonStrguide:" + jsonStrguide);
			// jsonStrproblem = FileUtils.readFileFromAssets(sContext,
			// AppDirConstant.APP_COMMONLY_PROBLEM_JSON_NAME);
			// LogUtlis.showLogI(TAG, "from local jsonStrproblem:" +
			// jsonStrproblem);
		}
		ContentInfos contentInfosnotice = null;
		ContentInfos contentInfosguide = null;
		// ContentInfos contentInfosproblem;
		if (jsonStrnotice != null) {
			contentInfosnotice = JsonUtils.parseJsonStrToObject(jsonStrnotice, ContentInfos.class);
			contentInfosguide = JsonUtils.parseJsonStrToObject(jsonStrguide, ContentInfos.class);
			// contentInfosproblem =
			// JsonUtils.parseJsonStrToObject(jsonStrproblem,
			// ContentInfos.class);
		}
		if (contentInfosnotice != null && contentInfosguide != null) {
			LogUtlis.showLogI(TAG, "contentInfosnotice:" + contentInfosnotice.getDatas().size() + "contentInfosguide:" + contentInfosguide.getDatas().size());
			App.sContentInfoList.clear();
			App.sContentInfoList.addAll(contentInfosnotice.getDatas());
			int length = contentInfosguide.getDatas().size();
			int i = 0;
			for (i = 0; i < length; i++) {
				App.sContentInfoList.add(contentInfosguide.getDatas().get(i));
			}
			// length = contentInfosproblem.getDatas().size();
			// for (i = 0; i < length; i++) {
			// App.sContentInfoList.add(contentInfosproblem.getDatas().get(i));
			// }
		}

	}

	private void initAllMenusFromLocal() {
		ContentResolver contentResolver = getContentResolver();
		ScmbhcDbManager scmbhcDbManager = ScmbhcDbManager.getInstance(contentResolver);
		int count = scmbhcDbManager.getBusinessMenuCount();
		if (count <= 0) {
			String jsonStr = FileUtils.readFileFromAssets(sContext, AppDirConstant.APP_MENU_JSON_NAME);
			jsonStr = new DesCrypCoding().decode(jsonStr);
			AllMenus allMenus = JsonUtils.parseJsonStrToObject(jsonStr, AllMenus.class);
			scmbhcDbManager.recordBusinessMenuList(allMenus.getDatas());
		}
	}

	private void initDataFromLocal() {
		initAllMenusFromLocal();
		initScrollPreferentialInfoFromLocal();
		initCommonlyBusinessMenusFromLocal();
		// initCommonlySearchFromLocal();
		initMobileBusinessHallFromLocal();
		initContentInfoMsgFromLocal();
	}

	private void initApkVersionInfo() {
		try {
			String packageName = sContext.getPackageName();
			sAPKVersionCode = sContext.getPackageManager().getPackageInfo(packageName, 0).versionCode;
			sAPKVersionName = sContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void getScreenSize() {
		DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
		SCREEN_WIDTH = displaymetrics.widthPixels;
	}

	private void createFileStoreDir() {
		FileUtils.createSDDir(App.AppDirConstant.APP_ROOT_DIR);
		FileUtils.createSDDir(App.AppDirConstant.APP_APK_DIR);
		FileUtils.createSDDir(App.AppDirConstant.APP_IMAGE_DATA_DIR);
	}

	public void onLowMemory() {
		super.onLowMemory();
	}

	public static String getPwd() {
		return pwd;
	}

	public static void setPwd(String pwd) {
		App.pwd = pwd;
	}

	public void onDestroy() {
		// 清除用户登出
		sSettingsPreferences.cleanUserOtherData();
		sSettingsPreferences.setNonameToken(null);
		setPwd(null);
		this.sendBroadcast(new Intent(AppActionConstant.ACTION_REFRESH));// 呼叫刷新
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public interface ExtraKeyConstant {
		public static final String KEY_TITLE = "title";
		public static final String KEY_HEADBAR_RB_NAME = "headbar_right_name";
		public static final String KEY_FINISH_ACTIVITY = "finish_activity";

		/**
		 * boolean for opened?
		 */
		public static final String KEY_HAVE_OPENED = "have_opened_up";

		public static final String KEY_BUSINESS_INFO = "business_info";

		public static final String KEY_SUB_BUSINESS_INFOS = "sub_business_infos";

		public static final String KEY_MEMBER_NUMBER = "whole_family_happy_menber";

		public static final String KEY_DOWNLOAD_APK_URL = "download_apk_url";

		public static final String KEY_PHONE_NUMBER = "phoneNum";

		public static final String KEY_TOP_UP_AMOUNT = "topUpAmount";

		public static final String KEY_PAY_AMOUNT = "payAmount";

		public static final String KEY_PASSWORD = "user_password";
		public static final String KEY_LOGIN_TYPE = "loginType";
		public static final String KEY_AUTO_LOGIN = "autoLogin";
		public static final String KEY_SAVE_PASSWORD = "savePassword";
		public static final String KEY_TIME = "time";

		public static final String KEY_TIME_START = "start_time";
		public static final String KEY_TIME_END = "end_time";
		// 消息推送用户名和密码
		public static final String PUSH_MESSAGE_USERNAME = "push_message_username";
		public static final String PUSH_MESSAGE_PASSWORD = "push_message_password";

		/**
		 * 密匙
		 */
		public static final String KEY_TOKEN = "token";

		public static final String KEY_IS_TRAFFIC_OVER = "traffic_over";

		public static final String KEY_FRAGMENT = "fragment";
		public static final String KEY_BUNDLE = "bundle";
		public static final String KEY_QR_CODE = "qr_code";

		public static final String KEY_SUCCESS = "is success";
		public static final String KEY_DISPLAY_GUIDE_TYPE = "display_guide_type";
		public static final String KEY_IS_BUSINESS_GUIDES_UPDATED = "is_business_guies_updated";

		public static final String KEY_IMEI = "imei";
		public static final String KEY_IMSI = "imsi";

		public static final String KEY_CASE = "case";
	}

	public interface AppDirConstant {
		public static final String APP_ROOT_DIR = "scmbhc";
		public static final String APP_APK_DIR = APP_ROOT_DIR + File.separator + "apk";
		public static final String APP_IMAGE_DATA_DIR = APP_ROOT_DIR + File.separator + "image";
		public static final String APP_MENU_JSON_NAME = "menus.json";
		public static final String APP_BUSINESS_HALL_INFOS_JSON_NAME = "business_hall_infos.json";
		public static final String APP_PREFERENTIALINFOS_JSON_NAME = "preferential_infos.json";
		public static final String APP_SEARCH_PREFERENTIALINFOS_JSON_NAME = "search_preferential_infos.json";
		public static final String APP_COMMONLY_BUSINESS_JSON_NAME = "commonlyBusiness.json";
		public static final String APP_SYSTEM_NOTICE_JSON_NAME = "systemnotice.json";
		public static final String APP_USER_GUIDE_JSON_NAME = "userguide.json";
		public static final String APP_COMMONLY_PROBLEM_JSON_NAME = "commonlyproblem.json";
		public static final String APP_CREDITS_EXCHANGE_JSON_NAME = "creditsExchange.json";
		public static final String APP_COMNONLY_SEARCCH_JSON_NAME = "commonly_searchs.json";
		public static final String APP_APK_FILE_NAME = "update.apk";
		public static final String APP_SEARCH_FILE_NAME = "searchs.json";
		public static final String APP_BUSINESS_GUIDE_ZIP_NAME = "businessimg.zip";
		public static final String APP_BUSINESS_GUIDE_DIR = APP_ROOT_DIR + File.separator + "businessimage";

	}

	public interface AppActionConstant {
		public static final String ACTION_UPDATA_APK = "com.sunrise.scmbhc.action.updata_apk";

		public static final String ACTION_UPDATE_DISCOUNT_RATES = "com.sunrise.scmbhc.action.updata_discount_rates";
		
		// for appWidget
		public static final String ACTION_LOGIN = "com.sunrise.scmbhc.broadcast.action.login";
		public static final String ACTION_REFRESH = "com.sunrise.scmbhc.broadcast.action.refresh";
		public static final String ACTION_REFRESH_REQUEST = "com.sunrise.scmbhc.broadcast.action.refresh_quest";
		
		public static final byte STATE_COMPLETE_PHONE_FREE_QUERY = 1;
		public static final byte STATE_COMPLETE_PHONE_CURRENT_MSG = 2;
		public static final byte STATE_COMPLETE_ADDITIONAL_TRAFFIC_INFO = 4;
		public static final byte STATE_COMPLETE_GET_CREDITS = 8;
		public static final byte STATE_COMPLETE_GET_USER_BASE_INFO = 16;
	}

	public static ServiceManager getServiceManager() {
		return mServiceManager;
	}

	public static void setServiceManager(ServiceManager ServiceManager) {
		mServiceManager = ServiceManager;
	}

	public boolean isIsPad() {
		// TODO Auto-generated method stub
		return false;
	}
}
