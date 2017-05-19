package com.sunrise.marketingassistant.fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.ZipException;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.task.DownloadZipPageTask;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.utils.HardwareUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.utils.PageTurnUtils;
import com.sunrise.javascript.JavaScriptWebView;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.JavascriptWebViewClient;
import com.sunrise.javascript.AddedJavascriptObject;
import com.sunrise.javascript.mode.BusinessInformation;
import com.sunrise.javascript.utils.ActivityUtils;
import com.sunrise.javascript.utils.FileUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewFragment2 extends BaseFragment implements TaskListener, ExtraKeyConstant {
	JavaScriptWebView mWebView;
	private AddedJavascriptObject mPageTurnUtils;// 页面跳转js对象
	private final static String TAG = "WebViewActivity";
	private static final String CACH_NAME = "EconAnalCache";
	private static final String KEY_INFO_TO_WEB = "info for web";
	private JavascriptHandler mHandler;
	private ProgressBar mProgressBar;
	private TextView mScanedAppView;

	private GenericTask mTask;

	private String mPageInfo;

	private String mUrl;

	private String ver;

	private String mtitle;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			savedInstanceState = getArguments();
		}
		mPageInfo = savedInstanceState.getString(ExtraKeyConstant.KEY_CONTENT);
		mUrl = savedInstanceState.getString(Intent.EXTRA_TEXT);
		ver = savedInstanceState.getString(ExtraKeyConstant.KEY_LAST_MODIFY);
		mtitle = savedInstanceState.getString(Intent.EXTRA_TITLE);
	}

	public void addBundleInfo(String url, String outPageInfo, String lastModify) {
		Bundle args = new Bundle();
		args.putString(ExtraKeyConstant.KEY_CONTENT, outPageInfo);
		args.putString(KEY_LAST_MODIFY, lastModify);
		args.putString(Intent.EXTRA_TEXT, url);
		setArguments(args);
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fargment_webview, null);

		mWebView = (JavaScriptWebView) view.findViewById(R.id.web_view);
		mWebView.removeJavascriptInterface("accessibility");
		mWebView.removeJavascriptInterface("ccessibilityaversal");
		mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

		mProgressBar = (ProgressBar) view.findViewById(R.id.web_load_progress);
		mScanedAppView = (TextView) view.findViewById(R.id.scaned_app);
		initWebview();

		initBisinessInformation();

		IntentFilter filter = new IntentFilter();
		filter.addAction(ActivityUtils.WEB_GO_BROADCAST);
		mBaseActivity.registerReceiver(mWebReciever, filter);

		loadUrl(getUrl());

		return view;
	}

	private void loadUrl(String url) {
		if (url == null) {
			CommUtil.showAlert(mBaseActivity, "异常", "url = " + url, getString(R.string.exit), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					doBack();
				}
			});
			return;
		}
		// Toast.makeText(WebViewActivity.this, "url:"+url,
		// Toast.LENGTH_LONG).show();
		if (url.startsWith("file://")) {
			if (url.endsWith(".html")) {
				mWebView.loadUrl(url);
			} else if (url.endsWith("zip")) {
				String filename = url.substring(url.lastIndexOf('/') + 1);
				String zipFolder = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR + File.separator + filename.substring(0, filename.indexOf('.')), null);
				try {
					if (FileUtils.upZip(FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR, filename), zipFolder) == 0) {
						loadUrl("file://" + FileUtils.searchFileFormDir(zipFolder, HTML_FILE_NAME));
					}
				} catch (ZipException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return;
		}

		if (App.isTest && url.startsWith("local://")) {
			String temp = url.replace("local://", "");
			mWebView.loadUrl("file://" + FileUtils.getAbsPath(ExtraKeyConstant.APP_SD_PATH_NAME, temp));
			return;
		}

		if (url.startsWith("http")) {
			if (url.endsWith(".zip")) {
				String fileName = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
				if (isNeedUpdateZip(fileName, ver)) {
					mTask = new DownloadZipPageTask().execute(url, this);
					if (App.isTest)
						Toast.makeText(mBaseActivity, url + "\n需要重新下载，ver=" + ver, Toast.LENGTH_LONG).show();
				} else {
					String dir = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR + File.separator + fileName, null);
					String file = FileUtils.searchFileFormDir(dir, HTML_FILE_NAME);
					mWebView.loadUrl("file://" + file);

					if (App.isTest)
						Toast.makeText(mBaseActivity, url + "\n无须重新下载，直接启动：" + file, Toast.LENGTH_LONG).show();
				}
				return;
			}
		}
		mWebView.loadUrl(url);
	}

	// private void initUnZipPaths(String url) {
	// String htmlPath;
	// try {
	// String filename = FileUtils.getFileNameByUrl(url);
	// String zipFilePath = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR,
	// filename);
	// String unTargetFolderName = filename.substring(0,
	// filename.lastIndexOf("."));
	// String unZipTargetFolderRelativePath = APP_FILE_HTML_DATA_DIR +
	// File.separator + unTargetFolderName;
	// String unZipTargetFolder =
	// FileUtils.getAbsPath(unZipTargetFolderRelativePath, null);
	// boolean targetFolderIsExist =
	// FileUtils.fileIsExist(unZipTargetFolderRelativePath);
	// if (filename.contains(".zip")) {
	// htmlPath = unZipTargetFolder + HTML_FILE_NAME;
	// } else {
	// htmlPath = zipFilePath;
	// }
	// mVisitAddress = "file:///" + htmlPath;
	// // mVisitAddress= "file:///android_asset/esop/yxzs-test/index.html";
	// } catch (IOException e) {
	// e.printStackTrace();
	// mFileName = mAppTag + "" + mMenuId + ".html";
	// }
	// }

	// 主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
	private WebChromeClient mWebChromeClient = new WebChromeClient() {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			final JsResult resu = result;
			// 构建一个Builder来显示网页中的alert对话框
			CommUtil.showAlert(mBaseActivity, null, message, getString(R.string.confirm), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					resu.confirm();
				}
			});
			return true;
		}
	};
	private BusinessInformation mBusinessInformation;
	protected OnClickListener mRefreshListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// mWebView.reload();
			// mWebView.loadUrl("javascript:aNative.reload4Client()");
			mWebView.clearHistory();
			mWebView.clearMatches();
			mWebView.clearView();
			// Toast.makeText(getApplication(), "url:"+url,
			// Toast.LENGTH_LONG).show();
			initWebview();
			loadUrl(getUrl());
		}
	};

	/*
	 * private void setZoomControlGone(View view) { Class classType; Field
	 * field; try { classType = WebView.class; field =
	 * classType.getDeclaredField("mZoomButtonsController");
	 * field.setAccessible(true); ZoomButtonsController mZoomButtonsController =
	 * new ZoomButtonsController(view);
	 * mZoomButtonsController.getZoomControls().setVisibility(View.GONE); try {
	 * field.set(view, mZoomButtonsController); } catch
	 * (IllegalArgumentException e) { e.printStackTrace(); } catch
	 * (IllegalAccessException e) { e.printStackTrace(); } } catch
	 * (SecurityException e) { e.printStackTrace(); } catch
	 * (NoSuchFieldException e) { e.printStackTrace(); } }
	 */
	class MyDownLoadListener implements DownloadListener {
		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
			mScanedAppView.setVisibility(View.VISIBLE);
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void onStart() {
		super.onStart();

		// String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
		// if (TextUtils.isEmpty(title))
		// title = "我的记录";
		// setTitle(title);
		//
		// setTitleBarLeftClick(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// onBackPressed();
		// }
		// });
		// setTitleBarRightClick(null);

	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putString(ExtraKeyConstant.KEY_CONTENT, mPageInfo);
		outState.putString(ExtraKeyConstant.KEY_LAST_MODIFY, ver);
		outState.putString(Intent.EXTRA_TEXT, mUrl);
		outState.putString(Intent.EXTRA_TITLE, mtitle);
		super.onSaveInstanceState(outState);
	}

	private void initWebview() {
		// 调用回调函数
		mHandler = new JavascriptHandler(mWebView);
		mWebView.setJavascriptHandler(mHandler);
		mWebView.setFocusable(true);
		{
			mPageTurnUtils = new PageTurnUtils(mWebView, mPageInfo, mHandler);
			mWebView.addJavascriptEvent(mPageTurnUtils);// 增加业务推荐
		}

		mWebView.addJavascriptInterface(this, CACH_NAME);
		mWebView.setDownloadListener(new MyDownLoadListener());
		WebSettings webSettings = mWebView.getSettings();
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webSettings.setBuiltInZoomControls(true);
		// mWebView.getSettings().setBuiltInZoomControls(false);
		// mWebView.getSettings().setAppCacheEnabled(true);
		// mWebView.getSettings().setAllowFileAccess(true);
		// mWebView.getSettings().setDomStorageEnabled(true);
		// mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// mWebView.getSettings().setSupportZoom(true);
		// mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setJavaScriptEnabled(true);

		// setZoomControlGone(mWebView);
		// 主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
		mWebView.setWebChromeClient(mWebChromeClient);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		// 主要帮助WebView处理各种通知、请求事件,能够处理javascript对设备的功能请求。
		JavascriptWebViewClient javascriptWebViewClient = new JavascriptWebViewClient(mBaseActivity, mHandler) {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mProgressBar.setVisibility(View.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (mPageTurnUtils != null)
					view.loadUrl("javascript:" + mPageTurnUtils.getHtmlRegist());// TODO
				mProgressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				// failingUrl = "file:///android_asset/error.html";
				// super.onReceivedError(view, errorCode, description,
				// failingUrl);
				// Log.i("webView：",description+"\n"+failingUrl);
				mProgressBar.setVisibility(View.GONE);
				// mWebView.stopLoading();
				// mWebView.clearView();
				// mWebView.loadUrl("file:///android_asset/error.html");
				String data = "<div><div><div style='font-size:30px;display: inline-block;vertical-align:middle;'>"
						+ "</div>"
						+ "<div style='font-size:30px;display: inline-block;vertical-align:middle;color: #CC6633;text-shadow: -1px -1px 0 #fff,1px 1px 0 #333,1px 1px 0 #444;padding-top:20px;'>找不到网页</div> "
						+ "</div>	<div style='margin-top:20px;font-size:20px;'>		此处网页可能暂时出现故障，也可能已永久移至某个新的网络地址。	</div>	"
						+ "<div style='margin:20px 15px;font-size:20px;color:blue;width:300px;'><a style='text-decoration: underline;width:250px;' href='javascript:window.location.reload();'>重新加载</a></div>"
						+ "<div style='margin-top:10px;color:#333;'><label style='font-weight: bolder;font-size:20px;'>以下是几点建议：</label><ul style='font-size:20px;'><li>进行检查以确保您的设备具有信号和数据连接</li><li>稍后重新载入该网页</li></ul></div></div>";
				// String data =
				// "<div style='text-align: center;	font-size:28px;	color:red;	padding-bottom:40px;'>网络异常，请稍后再试！</div><br/>	</div>";
				// view.loadUrl("javascript:document.body.innerHTML=\"" +
				// "<img width='160' height='124'  src='file:///android_asset/error.jpg'>"
				// + "\"");
				view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
				return;
			}

		};
		mWebView.setWebViewClient(javascriptWebViewClient);
	}

	private void initBisinessInformation() {
		if (mBusinessInformation == null)
			mBusinessInformation = new BusinessInformation(getPreferences().getAuthentication(), "123", HardwareUtils.getPhoneIMEI(mBaseActivity),
					HardwareUtils.getPhoneIMSI(mBaseActivity), null);
		mBusinessInformation.setSubAccount(getPreferences().getSubAccount());
		mBusinessInformation.setPhoneNumber(getPreferences().getMobile());
		mBusinessInformation.setAppVersion(String.valueOf(App.sAppCode));
		mBusinessInformation.setLoadStartTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
		mBusinessInformation.setAppTag("XDB");
		mBusinessInformation.setOauthInforamtion(getPreferences().getAuthentication());
		mWebView.setBusinessInformation(mBusinessInformation);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		if (mTask != null)
			mTask.cancle();

		mWebView.setWebViewClient(null);
		mWebView.setWebChromeClient(null);
		mWebView.stopLoading();

		mBaseActivity.unregisterReceiver(mWebReciever);

		super.onDestroy();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == 1) {
			if (resultCode != mBaseActivity.RESULT_OK) {
				doBack();
			} else {
				initBisinessInformation();
			}
		} else
			super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public boolean onBackPressed() {
		mWebView.loadUrl("javascript:aNative.goBack()");
		return true;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		mProgressBar.setVisibility(View.VISIBLE);
		mScanedAppView.setVisibility(View.VISIBLE);
		mScanedAppView.setText("加载页面压缩包……");
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		if (result == TaskResult.OK) {

		} else {
			CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), getString(R.string.exit), false, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					doBack();
				}

			});
		}

		mProgressBar.setVisibility(View.GONE);
		mScanedAppView.setVisibility(View.GONE);
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		String url = getUrl();
		String fileName = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
		if (!TextUtils.isEmpty(ver)) {
			try {
				long newVer = Long.valueOf(ver);
				saveZipHtmlVersion(fileName, newVer);
			} catch (NumberFormatException e) {

			}
		}
		loadUrl((String) param);
	}

	@Override
	public void onCancelled(GenericTask task) {
		mTask = null;
		mProgressBar.setVisibility(View.GONE);
		mScanedAppView.setVisibility(View.GONE);
	}

	/**
	 * 目前是否是首页
	 */
	private boolean mIsFirstPage = true;
	private BroadcastReceiver mWebReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			System.err.println("receive Action: " + action);
			if (action.equals(ActivityUtils.WEB_GO_BROADCAST)) {// 返回按钮
				boolean isFirstPage = intent.getBooleanExtra(ActivityUtils.FIRST_PAGE_EXTRAL, false);
				if (isFirstPage)
					doBack();
				mIsFirstPage = isFirstPage;
			}

			else if (action.equals(ActivityUtils.WEB_REFRESH_BROADCAST)) {// 刷新
				boolean showBt = intent.getBooleanExtra(Intent.EXTRA_TEXT, false);
			}
		}

	};

	private String getUrl() {
		// return "file:///android_asset/test2.html";
		// if (App.isTest) {
		// String fileDir = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR +
		// File.separator + "test", null);
		// LogUtlis.e(TAG, "fileDir = " + fileDir);
		// String filePath = FileUtils.searchFileFormDir(fileDir,
		// HTML_FILE_NAME);
		// LogUtlis.e(TAG, "filePath = " + filePath);
		// if (filePath != null)
		// return "file:///" + filePath;
		// }
		return mUrl;
	}

	private boolean isNeedUpdateZip(String fileName, String verStr) {

		if (TextUtils.isEmpty(verStr))
			return true;

		long newver = 0;
		try {
			newver = Long.valueOf(verStr);
		} catch (NumberFormatException e) {
			return true;
		}

		String filePath = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR + File.separator + fileName, null);
		boolean isFileExist = FileUtils.searchFileFormDir(filePath, HTML_FILE_NAME) != null;
		if (!isFileExist)
			return true;

		LogUtlis.d(TAG, filePath + " IS EXIST!!");

		String infos = getPreferences().getString(KEY_HTM_ZIP_VERSION_INFOS);
		if (TextUtils.isEmpty(infos))
			return true;
		LogUtlis.d(TAG, infos);

		try {
			JSONObject json = new JSONObject(infos);
			if (json.isNull(fileName))
				return true;

			long oldver = json.getLong(fileName);
			LogUtlis.d(TAG, "oldver = " + oldver + ", newver = " + newver);
			return newver > oldver;
		} catch (JSONException e) {
			e.printStackTrace();
			getPreferences().putString(ExtraKeyConstant.KEY_HTM_ZIP_VERSION_INFOS, null);
			return true;
		}

	}

	private void saveZipHtmlVersion(String fileName, long newver) {
		try {
			String infos = getPreferences().getString(ExtraKeyConstant.KEY_HTM_ZIP_VERSION_INFOS);
			JSONObject json = new JSONObject();
			json.put(fileName, newver);
			if (!TextUtils.isEmpty(infos)) {

				JSONObject jsonOld = new JSONObject(infos);
				Iterator<String> iterator = jsonOld.keys();
				do {
					String key = iterator.next();
					String value = jsonOld.getString(key);
					if (!key.equals(fileName))
						json.put(key, value);
					LogUtlis.d(TAG, "key = " + key + ",value = " + value);
				} while (iterator.hasNext());
			}

			LogUtlis.d(TAG, json.toString());

			getPreferences().putString(ExtraKeyConstant.KEY_HTM_ZIP_VERSION_INFOS, json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void doBack() {
		if (mIsFirstPage) {
			// if (isSinglePage)
			LogUtlis.d("doBack", "^^^^^^^^");
			for (StackTraceElement array : new Throwable().getStackTrace()) {
				LogUtlis.e("doBack", "^^^^^^^^" + array.getClassName() + '.' + array.getMethodName());
			}
			mBaseActivity.onExit(this);
			// else
			// BaseActivity.exit(mBaseActivity);
		}
	}
}
