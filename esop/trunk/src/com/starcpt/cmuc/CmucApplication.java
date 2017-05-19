package com.starcpt.cmuc;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.androidpn.client.ServiceManager;

import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import com.starcpt.cmuc.cache.download.DownFileManager;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.AppMenu;
import com.starcpt.cmuc.model.CachItem;
import com.starcpt.cmuc.model.Channel;
import com.starcpt.cmuc.model.DataPackage;
import com.starcpt.cmuc.model.WebViewWindow;
import com.starcpt.cmuc.model.bean.FeedbackBean;
import com.starcpt.cmuc.model.bean.SubAccountBeen;
import com.starcpt.cmuc.net.ServerClient;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.utils.CrashHandler;
import com.starcpt.cmuc.utils.DeviceUuidFactory;
import com.starcpt.cmuc.utils.FileUtils;
import com.starcpt.cmuc.utils.PhoneInfoUtils;
import com.sunrise.javascript.JavaScriptConfig;

public class CmucApplication extends Application {
	//private static final String TAG="CmucApplication";
	public static final boolean sTestData=false;
	public static final String TITLE_EXTRAL="title";
	public static final String MENU_ID="menuParentId";
	public static final String APP_TAG_EXTRAL="appTag";
	public static final String PAGE_ID_EXTRAL="pageId";
	public static final String CHILDSTYLENAME_EXTRAL="listDisplayStyle";
	//public static final String ISCHILDUPDATED_EXTRAL="isChildUpdated";
	public static final String CONTENT_EXTRAL="content";
	public static final String COLLECTION_MENU_EXTRAL="collection_menu";
	public static final String SEARCH_MENU_EXTRAL="search_menu";
	public static final String BUSINESS_ID_EXTRAL="businessId";
	public static final String HELP_ID_EXTRAL="helpId";
	public static final String POSITIONS_EXTRAL="positions";
	public static final String CHECK_TYPE_EXTRAL="check_type";
	public static final String NOTIFICATION_ID_EXTRAL="notification_id";
	public static final String CHILD_VERSION_EXTRAL="child_version";
	//public static final String SUB_ACCOUNT_EXTRAL="sub_account";
	
	public static final String MESSAGE_NOTIFICATION_ID_EXTRAL="notificationId";
	public static final String MESSAGE_OPTION_TYPE_EXTRAL="optionType";
	public static final String MESSAGE_OPTION_CONTENT_EXTRAL="optionContent";
	public static final String MESSAGE_TEXT_CONTENT_EXTRAL="textContent";
	public static final String MESSAGE_OPERATION_COMPLETE_EXTRAL="operationComplete";
	public static final String MESSAGE_TITLE_EXTRAL="messageTitle";
	public static final String MESSAGE_TIME_EXTRAL="messageTime";
	public static final String MESSAGE_ID_EXTRAL="id";
	public static final String MESSAGE_FEEDBACK_CONTENT_EXTRAL="feedback_content";
	public static final String MESSAGE_FEEDBACK_TIME_EXTRAL="feedbackTime";
	public static final String MESSAGE_READED_EXTRAL="readed";
	public static final String DOWNLOAD_APK_URL_EXTRAL = "download_url";
	public static final String DISPLAY_GUIDE_TYPE="display_guide_type";
	public static final String IS_BUSINESS_GUIDES_UPDATED="is_business_guies_updated";
	
	public static final String IS_BUSINESS_WEB_EXTRAL="isBusinessWeb";
	public static final String AUTHENTICATION_EXTRAL="authentication";
	public static final int PASSWORD_LENGTH = 4;
	private static final String METADATA_APP_TAG_NAME="appTag";
	private static final String METADATA_NO_DEVICE_NAME="support_third_party_device";
	
	
	private static final String CRM_APP_TAG="CRM";
	public static final String ESOP_APP_TAG="ESOP";
	public static final String YXZS_APP_TAG="YXZS";
	private static final String CMUC_APP_TAG="ALL";
	//directory
	public static final String APP_FILE_ROOT_DIR = "cmuc";
	public static final String APP_FILE_APK_DIR = APP_FILE_ROOT_DIR + File.separator + "apk";
	public static final String APP_FILE_JSON_DATA_DIR=APP_FILE_ROOT_DIR+File.separator+"json";
	public static final String APP_FILE_HTML_DATA_DIR=APP_FILE_ROOT_DIR+File.separator+"html";
    public static final String APP_FILE_COMMON_RES_DATA_DIR=APP_FILE_HTML_DATA_DIR+File.separator+"common_res";
	public static final String APP_FILE_IMAGE_DATA_DIR=APP_FILE_ROOT_DIR+File.separator+"image";
	public static final String APP_CAPTURE_IMAGE_DIR=APP_FILE_ROOT_DIR+File.separator+"capture";
	public static final String APP_CRASH_LOG_DIR=APP_FILE_ROOT_DIR+File.separator+"crash";
	public static final String APP_FILE_COMMON_RES_IMAGES_OTHER_DATA_DIR=APP_FILE_COMMON_RES_DATA_DIR+File.separator+"images"+File.separator+"other";
	public static final String APP_SKIN_BASE_PATH_DIR = APP_FILE_ROOT_DIR+File.separator+"skin";
	public static final String APP_SKIN_CACHE_DIR = APP_SKIN_BASE_PATH_DIR+File.separator+"cache";
	public static final String APP_SKIN_INSTALLED_DIR = APP_SKIN_BASE_PATH_DIR+File.separator+"installed";
	public static final String APP_BUSINESS_GUIDE_TOP_DIR=APP_FILE_ROOT_DIR + File.separator + "business_guide";
	public static final String APP_BUSINESS_GUIDE_DIR=APP_BUSINESS_GUIDE_TOP_DIR + File.separator + "guide";
	
	public final static ArrayList<CachItem> sCachItemBeans=new ArrayList<CachItem>();
	static{
		sCachItemBeans.add(new CachItem("缓存图片",CmucApplication.APP_FILE_IMAGE_DATA_DIR));
		sCachItemBeans.add(new CachItem("缓存菜单",null));
	}
	
	public final static HashMap<String, Integer> sAppSplashNames=new HashMap<String, Integer>();
	static{
		sAppSplashNames.put(CRM_APP_TAG, R.drawable.crm_start_name);
		sAppSplashNames.put(ESOP_APP_TAG, R.drawable.esop_start_name);
		sAppSplashNames.put(CMUC_APP_TAG, R.drawable.cmuc_start_name);
		sAppSplashNames.put(YXZS_APP_TAG, R.drawable.yxzs_start_name);
	}
	public final static HashMap<String, Integer> sAppSplashNamesText=new HashMap<String, Integer>();
	static{
		sAppSplashNamesText.put(CRM_APP_TAG, R.string.splash_crm_app_name);
		sAppSplashNamesText.put(ESOP_APP_TAG, R.string.splash_esop_app_name);
		sAppSplashNamesText.put(CMUC_APP_TAG, R.string.splash_cmuc_app_name);
		sAppSplashNamesText.put(YXZS_APP_TAG, R.string.splash_yxzs_app_name);
	}
	
	public final static int[] sFunctionGuideDrawableIds={R.drawable.function_guide_1,R.drawable.function_guide_2};
	public final static int[] sOpreGuideDrawableIds={R.drawable.opre_guide_1,R.drawable.opre_guide_2,R.drawable.opre_guide_3};
	
	public static final String OS = "android";
	public static final String APP_MENU_FIRST_PAGE_ID="first";
	
	public static final ServerClient sServerClient = ServerClient.getInstance();
	public static final DownFileManager sDownFileManager= DownFileManager.getInstance();
	
    public static final String COMMON_RES_ZIP_NAME="common_res.zip";
	public static final String sApkFileName = "update.apk";
	public static final String BUSINESS_GUIDE_ZIP_NAME="business_guide.zip";
	
	public static final int UPDATE_APK_TASK_INDEX=0;
	public static final int UPDATE_COMMOM_RES_TASK_INDEX=1;
	public static final int GET_TOP_LIST_DATA_TASK_INDEX=2;
	public static final int  IDENTITY_VERIFICATION_MENU_ID=205600;
	
	public static boolean sLockScreenShowing=false;
	public static boolean sNeedShowLock=false;
    
	private DataPackage mCollectionAppmenus = null;
	private HashMap<String, DataPackage> mDatapakages;
	private HashMap<String, SubAccountBeen> mSubAccountMap;
	private ArrayList<FeedbackBean> mFeedbackBeans;
	private ArrayList<Channel> mTapChannels;
	private ArrayList<WebViewWindow> mWebViewWindows;

	private String mOsInfo;
	private String mAppName;
	private String mAppTag = null;
	private int mAppSplashNameId, mAppSplashNameTextId;
	public static Context sContext;
	private Preferences mSettingsPreferences;

	private String mPhoneIMEI;
	private String mPhoneIMSI;
	private String mPhoneNumber;
	private String mModeNumber;
	private String mReleaseVersion;

	// screen size
	private int mScreenWidth;
	private int mScreenHeight;
	private int mStatusBarHeight = 0;
	private int mSCREEN_WIDTH;

	private String mUserJsonDir;
	private boolean mApplicationRunning = false;
	private ServiceManager mServiceManager;
	private boolean mIsPad = false;
	private boolean mDeviceSupportHtlm5Pool = true;
	private int mScreenDirection = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	private Bitmap mGridViewItemDefaultBitmap;
	private boolean mOpenWebViewInFragment;
	private HashMap<String, String> mCurrentSubAccountHashMap;
	private ArrayList<Drawable> mFunctionGuideDrawables;
	private ArrayList<Drawable> mBusinessGuideDrawables;
	private ArrayList<Drawable> mOpreGuideDrawables;
	private AppMenu mIdentityVerificationMenu;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sContext=getApplicationContext();
		init();
	}

	private void init() {
		initDataContainer();
		checkIsPad();
		mGridViewItemDefaultBitmap = FileUtils.getBitmapFromAssets(sContext, "display_style_1_default_icon.png");
		mOsInfo = CmucApplication.OS + "~" + mScreenWidth + "*" + mScreenHeight;
		getPhoneInFo();
		mSettingsPreferences = Preferences.getInstance(this);
		mSettingsPreferences.savePushServerInfo("218.205.252.26", "18099");// 消息摇头服务器
		initApplicationName();
		intTabChannels();
		setScreenOrientation();
		initCrashHandler();
		startUpThread();
		SkinManager.initCurSkin(this);
	}

	private void initDataContainer() {
		mDatapakages = new HashMap<String, DataPackage>();
		mFeedbackBeans = new ArrayList<FeedbackBean>();
		mTapChannels = new ArrayList<Channel>();
		mWebViewWindows = new ArrayList<WebViewWindow>();
		mSubAccountMap = new HashMap<String, SubAccountBeen>();
		mCurrentSubAccountHashMap = new HashMap<String, String>();
		mFunctionGuideDrawables = new ArrayList<Drawable>();
		mBusinessGuideDrawables = new ArrayList<Drawable>();
		mOpreGuideDrawables = new ArrayList<Drawable>();
	}

	public DataPackage getCollectionAppmenus() {
		return mCollectionAppmenus;
	}

	public void setCollectionAppmenus(DataPackage mCollectionAppmenus) {
		this.mCollectionAppmenus = mCollectionAppmenus;
	}

	public void insertDataPakage(String key, DataPackage dataPackage) {
		mDatapakages.put(key, dataPackage);
	}

	public DataPackage getDataPackage(String key) {
		return mDatapakages.get(key);
	}

	public void clearDataPackages() {
		mDatapakages.clear();
	}

	public ArrayList<FeedbackBean> getFeedbackBeans() {
		return mFeedbackBeans;
	}

	public void setFeedbackBeans(ArrayList<FeedbackBean> mFeedbackBeans) {
		this.mFeedbackBeans = mFeedbackBeans;
	}

	public ArrayList<Channel> getTapChannels() {
		return mTapChannels;
	}

	public void setTapChannels(ArrayList<Channel> mTapChannels) {
		this.mTapChannels = mTapChannels;
	}

	public String getAppTag() {
		return mAppTag;
	}

	public String getOsInfo() {
		return mOsInfo;
	}

	public void setOsInfo(String mOsInfo) {
		this.mOsInfo = mOsInfo;
	}

	public String getAppName() {
		return mAppName;
	}

	public int getAppSplashNameId() {
		return mAppSplashNameId;
	}

	public void setAppSplashNameId(int mAppSplashNameId) {
		this.mAppSplashNameId = mAppSplashNameId;
	}

	public int getAppSplashNameTextId() {
		return mAppSplashNameTextId;
	}

	public void setAppSplashNameTextId(int mAppSplashNameTextId) {
		this.mAppSplashNameTextId = mAppSplashNameTextId;
	}

	/*
	 * public boolean isSingleApp() { return isSingleApp; }
	 */

	public void setSingleApp(boolean isSingleApp) {
	}

	public Preferences getSettingsPreferences() {
		return mSettingsPreferences;
	}

	public void setSettingsPreferences(Preferences mSettingsPreferences) {
		this.mSettingsPreferences = mSettingsPreferences;
	}

	public String getPhoneIMEI() {
		return mPhoneIMEI;
	}

	public void setPhoneIMEI(String mPhoneIMEI) {
		this.mPhoneIMEI = mPhoneIMEI;
	}

	public String getPhoneIMSI() {
		return mPhoneIMSI;
	}

	public void setPhoneIMSI(String mPhoneIMSI) {
		this.mPhoneIMSI = mPhoneIMSI;
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}

	public String getModeNumber() {
		return mModeNumber;
	}

	public void setModeNumber(String mModeNumber) {
		this.mModeNumber = mModeNumber;
	}

	public String getReleaseVersion() {
		return mReleaseVersion;
	}

	public void setReleaseVersion(String mReleaseVersion) {
		this.mReleaseVersion = mReleaseVersion;
	}

	public HashMap<String, SubAccountBeen> getSubAccountMap() {
		return mSubAccountMap;
	}

	public void setSubAccountMap(HashMap<String, SubAccountBeen> mSubAccountMap) {
		this.mSubAccountMap = mSubAccountMap;
	}

	private void startUpThread() {
		new Thread() {
			public void run() {
				upLoadCrashFiles();
			};
		}.start();
	}

	private void upLoadCrashFiles() {
		ArrayList<File> files = FileUtils.getFileListOfDir(CmucApplication.APP_CRASH_LOG_DIR);
		for (File file : files) {
			String name = file.getName();
			if (name.contains("noup")) {
				try {
					String absPath = file.getAbsolutePath();
					String errorLogJson = FileUtils.readToStringFormFile(absPath);
					CmucApplication.sServerClient.saveDeviceErrorLog(errorLogJson);
					String newPath = file.getAbsolutePath().replace("noup", "uped");
					file.renameTo(new File(newPath));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void initCrashHandler() {
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}

	private void setScreenOrientation() {
		mScreenDirection = mSettingsPreferences.getScreenDirection();
		if (mScreenDirection == -1) {
			if (mIsPad) {
				mScreenDirection = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			} else {
				mScreenDirection = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			mSettingsPreferences.saveScreenDirection(mScreenDirection);
		}
		boolean screenD = mScreenDirection == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? true : false;
		mOpenWebViewInFragment = mIsPad && mDeviceSupportHtlm5Pool && screenD;
	}

	private void checkIsPad() {
		DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
		int widthPixels = displaymetrics.widthPixels;
		int heightPixels = displaymetrics.heightPixels;
		float densityDpi = displaymetrics.densityDpi;
		double diagonalPixels = Math.sqrt(Math.pow(widthPixels, 2) + Math.pow(heightPixels, 2));
		double screenSize = diagonalPixels / (densityDpi);
		if (screenSize >= 5.0f) {
			mIsPad = true;
		}

		mModeNumber = android.os.Build.MODEL;
		mReleaseVersion = android.os.Build.VERSION.RELEASE;
		mDeviceSupportHtlm5Pool = !((mModeNumber.equalsIgnoreCase("A10t(5DM3)") && mReleaseVersion.equalsIgnoreCase("4.0.3")));
		JavaScriptConfig.setIsPad(mOpenWebViewInFragment);
		mScreenWidth = 480;// displaymetrics.widthPixels;
		mScreenHeight = 800;// displaymetrics.heightPixels;
		mSCREEN_WIDTH = displaymetrics.widthPixels > displaymetrics.heightPixels ? displaymetrics.heightPixels : displaymetrics.widthPixels;
	}

	public void intTabChannels() {
		mTapChannels.clear();
		mTapChannels.add(new Channel(0, getBitmapFromResId(R.drawable.home), null, getString(R.string.home)));
		mTapChannels.add(new Channel(1, getBitmapFromResId(R.drawable.collection), null, getString(R.string.collection)));
		mTapChannels.add(new Channel(2, getBitmapFromResId(R.drawable.search), null, getString(R.string.search)));
		mTapChannels.add(new Channel(3, getBitmapFromResId(R.drawable.message), null, getString(R.string.message)));
		mTapChannels.add(new Channel(4, getBitmapFromResId(R.drawable.more), null, getString(R.string.more)));
	}

	private Bitmap getBitmapFromResId(int resId) {
		Resources res = getResources();
		Bitmap bitmap = ((BitmapDrawable) (res.getDrawable(resId))).getBitmap();
		return bitmap;
	}

	public String getUserJsonDir() {
		return mUserJsonDir;
	}

	public void setUserJsonDir(String userJsonDir) {
		mUserJsonDir = userJsonDir;
	}

	public int getStatusBarHeight() {
		return mStatusBarHeight;
	}

	public void setStatusBarHeight(int mStatusBarHeight) {
		this.mStatusBarHeight = mStatusBarHeight;
	}

	public int getRealScreenWidth() {
		return mSCREEN_WIDTH;
	}

	public int getScreenWidth() {
		return mScreenWidth;
	}

	public int getScreenHeight() {
		return mScreenHeight;
	}

	public boolean isApplicationRunning() {
		return mApplicationRunning;
	}

	public void setApplicationRunning(boolean mApplicationRunning) {
		this.mApplicationRunning = mApplicationRunning;
	}

	public ServiceManager getServiceManager() {
		return mServiceManager;
	}

	public void setServiceManager(ServiceManager mServiceManager) {
		this.mServiceManager = mServiceManager;
	}

	public boolean isIsPad() {
		return mIsPad;
	}

	public void setIsPad(boolean mIsPad) {
		this.mIsPad = mIsPad;
	}

	public boolean isDeviceSupportHtlm5Pool() {
		return mDeviceSupportHtlm5Pool;
	}

	public void setDeviceSupportHtlm5Pool(boolean mDeviceSupportHtlm5Pool) {
		this.mDeviceSupportHtlm5Pool = mDeviceSupportHtlm5Pool;
	}

	public int getScreenDirection() {
		return mScreenDirection;
	}

	public void setScreenDirection(int mScreenDirection) {
		this.mScreenDirection = mScreenDirection;
	}

	public Bitmap getGridViewItemDefaultBitmap() {
		return mGridViewItemDefaultBitmap;
	}

	public void setGridViewItemDefaultBitmap(Bitmap mGridViewItemDefaultBitmap) {
		this.mGridViewItemDefaultBitmap = mGridViewItemDefaultBitmap;
	}

	public boolean isOpenWebViewInFragment() {
		return mOpenWebViewInFragment;
	}

	public void setOpenWebViewInFragment(boolean mOpenWebViewInFragment) {
		this.mOpenWebViewInFragment = mOpenWebViewInFragment;
	}

	public ArrayList<WebViewWindow> getWebViewWindows() {
		return mWebViewWindows;
	}

	public void setWebViewWindows(ArrayList<WebViewWindow> mWebViewWindows) {
		this.mWebViewWindows = mWebViewWindows;
	}

	/*
	 * public boolean isDisplayBusinessGuide() { return isDisplayBusinessGuide;
	 * }
	 * 
	 * public void setDisplayBusinessGuide(boolean isDisplayBusinessGuide) {
	 * this.isDisplayBusinessGuide = isDisplayBusinessGuide; }
	 */

	public HashMap<String, String> getCurrentSubAccountHashMap() {
		return mCurrentSubAccountHashMap;
	}

	public String getCurrentSubAccount(String appTag) {
		return mCurrentSubAccountHashMap.get(appTag);
	}

	public void clearSubAccounts() {
		mCurrentSubAccountHashMap.clear();
	}

	public ArrayList<Drawable> getFunctionGuideDrawables() {
		return mFunctionGuideDrawables;
	}

	public void setFunctionGuideDrawables(ArrayList<Drawable> mFunctionGuideDrawables) {
		this.mFunctionGuideDrawables = mFunctionGuideDrawables;
	}

	public ArrayList<Drawable> getBusinessGuideDrawables() {
		return mBusinessGuideDrawables;
	}

	public void setBusinessGuideDrawables(ArrayList<Drawable> mBusinessGuideDrawables) {
		this.mBusinessGuideDrawables = mBusinessGuideDrawables;
	}

	public ArrayList<Drawable> getOpreGuideDrawables() {
		return mOpreGuideDrawables;
	}

	public void setOpreGuideDrawables(ArrayList<Drawable> mOpreGuideDrawables) {
		this.mOpreGuideDrawables = mOpreGuideDrawables;
	}

	private void getPhoneInFo() {
		mPhoneIMEI = PhoneInfoUtils.getPhoneIMEI(this);
		if (mPhoneIMEI == null) {
			DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(sContext);
			mPhoneIMEI = deviceUuidFactory.getDeviceUuid().toString();
		}
		// "357242042544384";
		mPhoneIMSI = PhoneInfoUtils.getPhoneIMSI(this);
		// "460002381300854";
		mPhoneNumber = PhoneInfoUtils.getPhoneNumber(this);
	}

	public static String getApkVersion() {
		try {
			return sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0).versionCode + "";
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0";
	}

	public static String getApkVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0.0";
	}

	private void initApplicationName() {
		try {
			PackageManager pm = getPackageManager();
			String packname = getPackageName();

			PackageInfo appConfigurations = pm.getPackageInfo(packname, PackageManager.GET_CONFIGURATIONS);
			mAppName = (String) appConfigurations.applicationInfo.loadLabel(pm);
			ApplicationInfo appInfo = pm.getApplicationInfo(packname, PackageManager.GET_META_DATA);
			Bundle appMetaData = appInfo.metaData;
			if (appMetaData != null) {
				mAppTag = appMetaData.getString(METADATA_APP_TAG_NAME);
				String isSupportDevice = appMetaData.getString(METADATA_NO_DEVICE_NAME);
				JavaScriptConfig.setsIsSupportThirdParthDevice(isSupportDevice.equals("support"));
				String appTags[] = mAppTag.split(",");
				if (appTags.length > 1) {
					setCmucApp();
				} else {
					setAppSplashNameId(sAppSplashNames.get(mAppTag));
					setAppSplashNameTextId(sAppSplashNamesText.get(mAppTag));
					if (!mAppTag.equalsIgnoreCase(CMUC_APP_TAG)) {
					}
				}

				JavaScriptConfig.setsSupportThirdParthDeviceType(appMetaData.getString("third_parth_device_type"));
				// 固定第三方外设屏幕方向
				if (appMetaData.containsKey("third_parth_device_screen_direction"))
					setIsPad(!"portrait".equalsIgnoreCase(appMetaData.getString("third_parth_device_screen_direction")));

			} else {
				setCmucApp();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			setCmucApp();
		}

	}

	public AppMenu getIdentityVerificationMenu() {
		return mIdentityVerificationMenu;
	}

	public void setIdentityVerificationMenu(AppMenu mIdentityVerificationMenu) {
		this.mIdentityVerificationMenu = mIdentityVerificationMenu;
	}

	private void setCmucApp() {
		mAppSplashNameId = sAppSplashNames.get(CMUC_APP_TAG);
	}

}
