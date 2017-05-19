//package com.sunrise.micromarketing.ui.fragment;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.zip.ZipException;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.text.Spanned;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.webkit.JsResult;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import com.sunrise.javascript.JavaScriptConfig;
//import com.sunrise.javascript.JavaScriptWebView;
//import com.sunrise.javascript.JavascriptHandler;
//import com.sunrise.javascript.JavascriptWebViewClient;
//import com.sunrise.javascript.mode.BusinessInformation;
//import com.sunrise.javascript.utils.ActivityUtils;
//import com.sunrise.javascript.utils.DateUtils;
//import com.sunrise.javascript.utils.FileUtils;
//import com.sunrise.micromarketing.App;
//import com.sunrise.micromarketing.ExtraKeyConstant;
//import com.sunrise.micromarketing.R;
//import com.sunrise.micromarketing.cache.download.DownLoadThread;
//import com.sunrise.micromarketing.cache.preferences.Preferences;
//import com.sunrise.micromarketing.utils.HardwareUtils;
//
//@SuppressLint({ "HandlerLeak", "ValidFragment" })
//public class WebViewFragment extends Fragment {
//	@SuppressLint("UseSparseArrays")
//	private JavaScriptWebView mWebView;
//	private LinearLayout mWebWindowPanel;
//	private String mPagePositions;
//	private String mParentPositions;
//	private String mTitle;
//	private String mUrl;
//	private String mAppTag;
//	private String mMenuId;
//	private int mBusinessId;
//	private boolean mIsUpdate;
//	private boolean mIsBusinessWeb;
//	private String mMenuVersionKey;
//	private long mChildVersion;
//	private boolean mIsManualStart = false;
//	private String mFileName;
//	private RelativeLayout mLoadProgressPanel;
//	private TextView mWebLoadStatus;
//	private RelativeLayout mWebViewTitleBar;
//	private RelativeLayout mLoadWebFailedPanel;
//	private Button mRetryLoadWebButton;
//	private ImageView mWebButtonBack;
//	private TextView mTitleView;
//	private ImageView mMinimizeImageView;
//	private TextView mSubAccountView;
//	private TextView mWebWindowNumbers;
//	private TextView mMenuTrackView;
//	private String mUnZipTargetFolder;
//	private boolean mTargetFolderIsExist;
//	private String mZipFilePath;
//	private String mVisitAddress;
//	private String mWebLoadStartTime;
//	private String mWebOverTime;
//	private String mHelpId;
//	private BusinessInformation mBusinessInformation;
//	private static final String HTML_FILE_NAME = "index.html";
//
//	private Activity mActivity;
//	private boolean isFirstPage = true;
//	private boolean isBackgroundStatus;
//	private RelativeLayout mWebViewToolBar;
//	private LinearLayout mWebViewToolContent;
//	private Button mOpenWebViewButton;
//	private boolean isContainWebActivity = false;
//	final Handler mHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case DownLoadThread.DWONLOAD_FLIE_COMPLETE:
//				mWebLoadStatus.setText(R.string.unzip_web);
//				unZipHtmlFile();
//				Preferences preferences = Preferences.getInstance(App.sContext);
//				if (mIsUpdate) {
//					preferences.saveMenuVersion(mMenuVersionKey, mChildVersion);
//				}
//				if (isBackgroundStatus) {
//					mWebLoadStatus.setText(R.string.load_web);
//					mWebView.loadUrl(mVisitAddress);
//				}
//				break;
//			case DownLoadThread.DWONLOADING_FILE:
//				mWebLoadStatus.setText("页面下载中" + msg.arg1 + "%");
//				mWebLoadStatus.invalidate();
//				break;
//			case DownLoadThread.DWONLOAD_FLIE_ERROR:
//				clearWebDatas();
//				break;
//			default:
//				break;
//			}
//		};
//	};
//
//	private BroadcastReceiver mWebReciever = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			String action = intent.getAction();
//			if (action.equals(ActivityUtils.WEB_GO_BROADCAST)) {
//				boolean currentFirstPage = intent.getBooleanExtra(ActivityUtils.FIRST_PAGE_EXTRAL, false);
//				// TODO
//			}
//		}
//
//	};
//
//	public WebViewFragment() {
//		super();
//	}
//
//	public WebViewFragment(Item item, boolean isBusinessWeb, String helpId) {
//		this();
//		this.mUrl = item.getContent();
//		this.mAppTag = item.getAppTag();
//		this.mMenuId = item.getMenuId() + "";
//		this.mBusinessId = item.getBusinessId();
//		this.mIsUpdate = isChildUpdated(mMenuId, mAppTag);
//		this.mIsBusinessWeb = isBusinessWeb;
//		this.mHelpId = helpId;
//	}
//
//	public boolean isContainWebActivity() {
//		return isContainWebActivity;
//	}
//
//	public void setContainWebActivity(boolean isContainWebActivity) {
//		this.isContainWebActivity = isContainWebActivity;
//	}
//
//	public void update(Item item, boolean isBusinessWeb, String helpId) {
//		this.mUrl = item.getContent();
//		this.mAppTag = item.getAppTag();
//		this.mMenuId = item.getMenuId() + "";
//		this.mBusinessId = item.getBusinessId();
//		this.mIsUpdate = isChildUpdated(mMenuId, mAppTag);
//		this.mIsBusinessWeb = isBusinessWeb;
//		this.mChildVersion = item.getChildVersion();
//		this.mHelpId = helpId;
//	}
//
//	private boolean isChildUpdated(String menuID, String appTag) {
//		App cmucApplication = (App) App.sContext;
//		Preferences preferences = Preferences.getInstance(mActivity);
//		mMenuVersionKey = preferences.getUserName() + appTag + menuID;
//		if (Integer.valueOf(menuID) == ExtraKeyConstant.IDENTITY_VERIFICATION_MENU_ID) {
//			mMenuVersionKey = menuID;
//		}
//		long oldChildVersion = preferences.getMenuVersion(mMenuVersionKey);
//		return oldChildVersion < mChildVersion;
//	}
//
//	private void updateWebButtonBack() {
//		if (mWebButtonBack != null) {
//			if (isFirstPage)
//				mWebButtonBack.setVisibility(View.GONE);
//			else {
//				mWebButtonBack.setVisibility(View.VISIBLE);
//			}
//		}
//	}
//
//	public void setTitle(String mTitle) {
//		this.mTitle = mTitle;
//	}
//
//	public void setParentPositions(String mParentPositions) {
//		this.mParentPositions = mParentPositions;
//	}
//
//	private void removeWebview() {
//		if (mWebWindowPanel != null) {
//			mWebWindowPanel.removeAllViews();
//		}
//	}
//
//	private void setSkin() {
//		SkinManager.setSkin(mActivity, mCurrView, ViewEnum.WebViewFragment);
//	}
//
//	private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			setSkin();
//		}
//	};
//
//	private void registerSkinChangedReceiver() {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
//		mActivity.registerReceiver(mSkinChangedReceiver, filter);
//	}
//
//	private void unRegisterSkinChangedReceiver() {
//		mActivity.unregisterReceiver(mSkinChangedReceiver);
//	}
//
//	public void exitWebview(Activity activity) {
//		removeWebview();
//		mActivity.finish();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		refreshWebView();
//		if (mWebView != null)
//			mWebView.requestFocusFromTouch();
//	}
//
//	public void refreshWebView() {
//		if (mIsManualStart) {
//			refreshView();
//			if (!isOpened(mBusinessId)) {
//				isFirstPage = true;
//				initUnZipPaths();
//				initWebView();
//				mWebWindowPanel.addView(mWebView);
//				loadWeb();
//			}
//			mIsManualStart = false;
//			updateWebButtonBack();
//			addWebWindow();
//			updateSubAccount();
//			refrehSubAccount();
//		}
//		refreshWebNumbers();
//		displayWebTool();
//		isBackgroundStatus = true;
//	}
//
//	public void setManualStart(boolean mIsStartedByIntent) {
//		this.mIsManualStart = mIsStartedByIntent;
//	}
//
//	private void refreshView() {
//		if (mParentPositions == null)
//			mPagePositions = mTitle;
//		else
//			mPagePositions = mParentPositions + ">>" + mTitle;
//		try {
//			Spanned tracksSpanned = CommonActions.getMenuTracks(mPagePositions);
//			mMenuTrackView.setText(tracksSpanned);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		mWebWindowPanel.removeAllViews();
//		if (cmucApplication.isOpenWebViewInFragment()) {
//			mTitleView.setText(mTitle);
//		}
//
//	}
//
//	private void refreshWebNumbers() {
//		int size = cmucApplication.getWebViewWindows().size();
//		if (size > 0) {
//			mWebWindowNumbers.setText("" + size);
//		} else {
//			mWebWindowNumbers.setText(null);
//		}
//	}
//
//	@Override
//	public void onStop() {
//		// TODO Auto-generated method stub
//		super.onStop();
//		isBackgroundStatus = false;
//	}
//
//	private View mCurrView;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		mActivity = getActivity();
//		cmucApplication = (CmucApplication) mActivity.getApplicationContext();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(ActivityUtils.WEB_GO_BROADCAST);
//		mActivity.registerReceiver(mWebReciever, filter);
//		return initFragmentView(inflater, container);
//	}
//
//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		if (mActivity != null) {
//			mActivity.unregisterReceiver(mWebReciever);
//			unRegisterSkinChangedReceiver();
//		}
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		setSkin();
//	}
//
//	private void clearWebDatas() {
//		FileUtils.deleteDirByRelativePath(mUnZipTargetFolder);
//		FileUtils.deleteFileByRelativePath(CmucApplication.APP_FILE_HTML_DATA_DIR, mFileName);
//	}
//
//	private void addWebWindow() {
//		ArrayList<WebViewWindow> webViewWindows = cmucApplication.getWebViewWindows();
//		int size = webViewWindows.size();
//		for (int i = 0; i < size; i++) {
//			WebViewWindow window = webViewWindows.get(i);
//			if (window.getBusinessId() == mBusinessId) {
//				deleteWebWindow(i);
//				addWebWindow(webViewWindows, i);
//				return;
//			}
//		}
//		if (webViewWindows.size() + 1 > 10) {
//			deleteWebWindow(webViewWindows.size() - 1);
//		}
//		addWebWindow(webViewWindows);
//	}
//
//	private void addWebWindow(ArrayList<WebViewWindow> webViewWindows) {
//		webViewWindows.add(createWebWindow());
//	}
//
//	private void addWebWindow(ArrayList<WebViewWindow> webViewWindows, int i) {
//		webViewWindows.add(i, createWebWindow());
//	}
//
//	private void deleteWebWindow(int i) {
//		cmucApplication.getWebViewWindows().remove(i);
//	}
//
//	private void deleteWebWindow(WebViewWindow webViewWindow) {
//		cmucApplication.getWebViewWindows().remove(webViewWindow);
//	}
//
//	private WebViewWindow createWebWindow() {
//		mWebView.setDrawingCacheEnabled(true);
//		mWebView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
//		Bitmap bitmap = mWebView.getDrawingCache();
//		byte[] resultBitmap = null;
//		if (bitmap != null) {
//			resultBitmap = BitmapUtil.getBytesFromComBitmap(bitmap);
//			mWebView.setDrawingCacheEnabled(false);
//		}
//		WebViewWindow webViewWindow = new WebViewWindow(resultBitmap, mTitle, mWebView);
//		webViewWindow.setBusinessId(mBusinessId);
//		webViewWindow.setUrl(mUrl);
//		webViewWindow.setAppTag(mAppTag);
//		webViewWindow.setBusinessWeb(mIsBusinessWeb);
//		webViewWindow.setMenuId(mMenuId);
//		webViewWindow.setChildVersion(mChildVersion);
//		webViewWindow.setFirstPage(isFirstPage);
//		/* add by xie xiuyan * */
//		// 设置浏览器允许缓存 缓存大小8M
//		mWebView.getSettings().setDomStorageEnabled(true);// 设置可以使用localStorage
//		mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);// 默认使用缓存
//		mWebView.getSettings().setAppCacheMaxSize(8 * 1024 * 1024);// 缓存最多可以有8M
//		mWebView.getSettings().setAllowFileAccess(true);// 可以读取文件缓存(manifest生效)
//		mWebView.getSettings().setPluginsEnabled(true);
//		mWebView.getSettings().setAppCacheEnabled(true);// 应用可以有缓存
//
//		return webViewWindow;
//	}
//
//	private void removeWebWindow(int businessId) {
//		ArrayList<WebViewWindow> webViewWindows = cmucApplication.getWebViewWindows();
//		int size = webViewWindows.size();
//		for (int i = 0; i < size; i++) {
//			WebViewWindow webViewWindow = webViewWindows.get(i);
//			if (webViewWindow.getBusinessId() == businessId) {
//				deleteWebWindow(webViewWindow);
//				return;
//			}
//		}
//	}
//
//	public boolean isOpened(int businessId) {
//		boolean isExist = false;
//		int size = cmucApplication.getWebViewWindows().size();
//		for (int i = 0; i < size; i++) {
//			WebViewWindow webViewWindow = cmucApplication.getWebViewWindows().get(i);
//			if (webViewWindow.getBusinessId() == businessId) {
//				isExist = true;
//				break;
//			}
//		}
//		return isExist;
//	}
//
//	private WebViewWindow getWebView(int businessId) {
//		int size = cmucApplication.getWebViewWindows().size();
//		for (int i = 0; i < size; i++) {
//			WebViewWindow webViewWindow = cmucApplication.getWebViewWindows().get(i);
//			if (webViewWindow.getBusinessId() == businessId) {
//				return webViewWindow;
//			}
//		}
//		return null;
//	}
//
//	private void displayWebTool() {
//		mWebViewToolContent.setVisibility(View.VISIBLE);
//		mOpenWebViewButton.setVisibility(View.GONE);
//	}
//
//	private View initFragmentView(LayoutInflater inflater, ViewGroup container) {
//		mCurrView = inflater.inflate(R.layout.webview_fragment, container, false);
//		mOpenWebViewButton = (Button) mCurrView.findViewById(R.id.open_web_tool_bt);
//		mOpenWebViewButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				displayWebTool();
//			}
//
//		});
//		mWebViewToolBar = (RelativeLayout) mCurrView.findViewById(R.id.webView_toolbar);
//		mWebViewToolContent = (LinearLayout) mCurrView.findViewById(R.id.webView_toolbar_content);
//		mMenuTrackView = (TextView) mCurrView.findViewById(R.id.menu_track);
//		if (cmucApplication.isOpenWebViewInFragment()) {
//			mWebViewTitleBar = (RelativeLayout) mCurrView.findViewById(R.id.webview_title_bar);
//			mWebButtonBack = (ImageView) mCurrView.findViewById(R.id.webview_back);
//			mWebButtonBack.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					doback();
//				}
//			});
//			mTitleView = (TextView) mCurrView.findViewById(R.id.webview_title);
//			mTitleView.setText(mTitle);
//			mWebViewTitleBar.setVisibility(View.VISIBLE);
//			mWebViewToolBar.setVisibility(View.GONE);
//			mMenuTrackView.setVisibility(View.GONE);
//		}
//		mMinimizeImageView = (ImageView) mCurrView.findViewById(R.id.webview_minimize);
//		mMinimizeImageView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (!cmucApplication.isOpenWebViewInFragment()) {
//					exitWebview(mActivity);
//					addWebWindow();
//				}
//			}
//		});
//		mSubAccountView = (TextView) mCurrView.findViewById(R.id.current_sub_account_tv);
//		mWebWindowNumbers = (TextView) mCurrView.findViewById(R.id.web_window_numbers);
//		mWebWindowNumbers.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (!cmucApplication.isOpenWebViewInFragment()) {
//					addWebWindow();
//				}
//				Intent intent = new Intent(mActivity, MultiwindowActivity.class);
//				mActivity.startActivity(intent);
//			}
//		});
//		mLoadProgressPanel = (RelativeLayout) mCurrView.findViewById(R.id.load_progress_panel);
//		mWebLoadStatus = (TextView) mCurrView.findViewById(R.id.web_load_staus);
//		// (JavaScriptWebView) view.findViewById(R.id.webView);
//		mWebWindowPanel = (LinearLayout) mCurrView.findViewById(R.id.web_window_panel);
//		mLoadWebFailedPanel = (RelativeLayout) mCurrView.findViewById(R.id.load_webview_failed_panel);
//		mRetryLoadWebButton = (Button) mCurrView.findViewById(R.id.re_load_web);
//		mRetryLoadWebButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				mLoadWebFailedPanel.setVisibility(View.GONE);
//				mLoadProgressPanel.setVisibility(View.VISIBLE);
//				if (mUrl != null) {
//					if (mIsBusinessWeb) {
//						clearWebDatas();
//						try {
//							CmucApplication.sDownFileManager
//									.downFile(mActivity, mUrl, CmucApplication.APP_FILE_HTML_DATA_DIR, mFileName, mHandler, true, false);
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					} else {
//						mWebView.loadUrl(mUrl);
//					}
//				}
//			}
//		});
//		registerSkinChangedReceiver();
//		return mCurrView;
//	}
//
//	private AlertDialog mAlertDialog;
//
//	private void initWebView() {
//		mWebView = new JavaScriptWebView(mActivity);
//		mWebView.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				hiddenWebTool();
//				return false;
//			}
//		});
//		mWebView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//		iniWebSetting();
//		// 调用回调函数
//		JavascriptHandler handler = new JavascriptHandler(mWebView);
//		mWebView.setJavascriptHandler(handler);
//		JavascriptWebViewClient javascriptWebViewClient = new JavascriptWebViewClient(mActivity, handler) {
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				mWebOverTime = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_1);
//				if (mBusinessInformation != null)
//					mBusinessInformation.setLoadOvertTime(mWebOverTime);
//				super.onPageFinished(view, url);
//				mLoadProgressPanel.setVisibility(View.GONE);
//				if (cmucApplication.isOpenWebViewInFragment()) {
//					addWebWindow();
//				}
//			}
//
//			@Override
//			public void onPageStarted(WebView view, String url, Bitmap favicon) {
//				super.onPageStarted(view, url, favicon);
//			}
//
//			@Override
//			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//				mLoadWebFailedPanel.setVisibility(View.VISIBLE);
//			}
//		};
//
//		mWebView.setWebViewClient(javascriptWebViewClient);
//		mWebView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
//		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//		mWebView.setWebChromeClient(new WebChromeClient() {
//			@Override
//			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//				final JsResult resu = result;
//				if (mAlertDialog != null) {
//					mAlertDialog.dismiss();
//					mAlertDialog = null;
//				}
//				// 构建一个Builder来显示网页中的alert对话框
//				Builder builder = new Builder(mActivity.getParent() != null ? mActivity.getParent() : mActivity);
//				builder.setMessage(message);
//				builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						resu.confirm();
//					}
//				});
//				builder.setCancelable(false);
//				mAlertDialog = builder.create();
//				builder.show();
//				return true;
//			}
//
//			// update the progress bar
//			public void onProgressChanged(WebView view, int progress) {
//				if (progress == 100) {
//					mLoadProgressPanel.setVisibility(View.GONE);
//					mWebLoadStatus.setVisibility(View.GONE);
//				}
//			}
//		});
//		setBusinessInformation();
//	}
//
//	private void refrehSubAccount() {
//		String subAccount = cmucApplication.getCurrentSubAccount(mAppTag);
//		if (subAccount != null && !subAccount.equals("")) {
//			String msg = mActivity.getString(R.string.current_sub_account) + subAccount;
//			mSubAccountView.setText(msg);
//			mSubAccountView.setVisibility(View.VISIBLE);
//		} else {
//			mSubAccountView.setVisibility(View.INVISIBLE);
//		}
//	}
//
//	private void setBusinessInformation() {
//		if (mBusinessId != -1) {
//			mBusinessInformation = new BusinessInformation(Preferences.getInstance(mActivity).getAuthentication(), mBusinessId + "",
//					HardwareUtils.getPhoneIMEI(mActivity), HardwareUtils.getPhoneIMSI(mActivity), mAppTag);
//			mBusinessInformation.setPhoneNumber(Preferences.getInstance(mActivity).getMobile());
//			mBusinessInformation.setPhoneSimCardNumber(cmucApplication.getPhoneNumber());
//			mBusinessInformation.setHelpId(mHelpId);
//			mBusinessInformation.setAppVersion(CmucApplication.getApkVersion());
//			mBusinessInformation.setOs("android");
//			updateSubAccount();
//		}
//	}
//
//	private void updateSubAccount() {
//		String subAccount = Preferences.getInstance(mActivity).getSubAccount();
//		if (mBusinessInformation != null) {
//			if (subAccount == null)
//				mBusinessInformation.setSubAccount("");
//			else
//				mBusinessInformation.setSubAccount(subAccount);
//			mWebView.setBusinessInformation(mBusinessInformation);
//		}
//	}
//
//	private void loadWeb() {
//		if (mLoadProgressPanel.getVisibility() == View.GONE)
//			mLoadProgressPanel.setVisibility(View.VISIBLE);
//		if (mUrl != null) {
//			if (mIsBusinessWeb) {
//				loadHtml(mUrl);
//			} else {
//				mWebView.loadUrl(mUrl);
//			}
//		}
//	}
//
//	private void initUnZipPaths() {
//		if (!mIsBusinessWeb)
//			return;
//		String htmlPath;
//		try {
//			mFileName = FileUtils.getFileNameByUrl(mUrl);
//			mZipFilePath = FileUtils.getAbsPath(ExtraKeyConstant.APP_FILE_HTML_DATA_DIR, mFileName);
//			String unTargetFolderName = mFileName.substring(0, mFileName.lastIndexOf("."));
//			String unZipTargetFolderRelativePath = ExtraKeyConstant.APP_FILE_HTML_DATA_DIR + File.separator + unTargetFolderName;
//			mUnZipTargetFolder = FileUtils.getAbsPath(unZipTargetFolderRelativePath, null);
//			mTargetFolderIsExist = FileUtils.fileIsExist(unZipTargetFolderRelativePath);
//			if (mFileName.contains(".zip")) {
//				htmlPath = mUnZipTargetFolder + HTML_FILE_NAME;
//			} else {
//				htmlPath = mZipFilePath;
//			}
//			mVisitAddress = "file:///" + htmlPath;
//			// mVisitAddress= "file:///android_asset/esop/yxzs-test/index.html";
//		} catch (IOException e) {
//			e.printStackTrace();
//			mFileName = mAppTag + "" + mMenuId + ".html";
//		}
//	}
//
//	private void iniWebSetting() {
//		WebSettings webSettings = mWebView.getSettings();
//		webSettings.setAllowFileAccess(true);
//		int screenDensity = getResources().getDisplayMetrics().densityDpi;
//		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
//		switch (screenDensity) {
//		case DisplayMetrics.DENSITY_LOW:
//			zoomDensity = WebSettings.ZoomDensity.CLOSE;
//			break;
//		case DisplayMetrics.DENSITY_MEDIUM:
//			zoomDensity = WebSettings.ZoomDensity.MEDIUM;
//			break;
//		case DisplayMetrics.DENSITY_HIGH:
//			zoomDensity = WebSettings.ZoomDensity.FAR;
//			break;
//		}
//		if (HardwareUtils.getLCDwidth(mActivity) > 700)
//			zoomDensity = WebSettings.ZoomDensity.FAR;
//		webSettings.setDefaultZoom(zoomDensity);
//	}
//
//	private void unZipHtmlFile() {
//		try {
//			FileUtils.upZip(mZipFilePath, mUnZipTargetFolder);
//			FileUtils.deleteFileByRelativePath(ExtraKeyConstant.APP_FILE_HTML_DATA_DIR, mFileName);
//		} catch (ZipException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public void loadHtml(final String url) {
//		mWebLoadStartTime = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_1);
//		if (mBusinessInformation != null)
//			mBusinessInformation.setLoadStartTime(mWebLoadStartTime);
//		try {
//			getHtml(url, mHandler);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	private void getHtml(final String url, final Handler handler) throws IOException {
//		if (mTargetFolderIsExist) {
//			if (!mIsUpdate) {
//				mWebView.loadUrl(mVisitAddress);
//			} else {
//				FileUtils.deleteFileAbsolutePath(mUnZipTargetFolder);
//				mWebLoadStatus.setText("页面下载中0%");
//				App.sDownFileManager.downFile(mActivity, url, ExtraKeyConstant.APP_FILE_HTML_DATA_DIR, mFileName, handler, true, false);
//			}
//		} else {
//			mWebLoadStatus.setText("页面下载中0%");
//			App.sDownFileManager.downFile(mActivity, url, ExtraKeyConstant.APP_FILE_HTML_DATA_DIR, mFileName, handler, true, false);
//		}
//	}
//
//	public void setBusinessInformation(String authentication) {
//		if (mBusinessId != -1) {
//			if (mBusinessInformation != null) {
//				mBusinessInformation.setPhoneNumber(Preferences.getInstance(App.sContext).getMobile());
//				mBusinessInformation.setOauthInforamtion(authentication);
//			}
//		}
//	}
//
//	public void doback() {
//		back();
//	}
//
//	private void back() {
//		if (mIsBusinessWeb) {
//			mWebView.loadUrl(JavaScriptConfig.goBack());
//		} else {
//			if (mWebView.canGoBack())
//				mWebView.goBack();
//		}
//	}
//
//	public void hiddenWebTool() {
//		if (mWebViewToolBar != null) {
//			mWebViewToolContent.setVisibility(View.GONE);
//			mOpenWebViewButton.setVisibility(View.VISIBLE);
//		}
//	}
//}
