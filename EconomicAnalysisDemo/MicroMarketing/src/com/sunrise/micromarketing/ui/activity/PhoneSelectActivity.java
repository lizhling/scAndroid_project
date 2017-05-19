package com.sunrise.micromarketing.ui.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.javascript.JavaScriptWebView;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.JavascriptWebViewClient;
import com.sunrise.javascript.AddedJavascriptObject;
import com.sunrise.javascript.mode.BusinessInformation;
import com.sunrise.javascript.utils.ActivityUtils;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.micromarketing.App;
import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.cache.preferences.Preferences;
import com.sunrise.micromarketing.task.DownloadZipPage2Task;
import com.sunrise.micromarketing.task.GenericTask;
import com.sunrise.micromarketing.task.TaskListener;
import com.sunrise.micromarketing.task.TaskResult;
import com.sunrise.micromarketing.utils.CommUtil;
import com.sunrise.micromarketing.utils.HardwareUtils;
import com.sunrise.micromarketing.utils.LogUtlis;
import com.sunrise.micromarketing.utils.PageTurnUtils;

public class PhoneSelectActivity extends BaseActivity implements TaskListener, ExtraKeyConstant {

	JavaScriptWebView mWebView;
	private AddedJavascriptObject mPageTurnUtils;// 页面跳转js对象
	private final static String TAG = "WebViewActivity";
	private static final String CACH_NAME = "EconAnalCache";
	private JavascriptHandler mHandler;
	private ProgressBar mProgressBar;
	private TextView mScanedAppView;

	private GenericTask mTask;

	private boolean isSinglePage;

	private long lastUpdateTime;

	private String param;

	private String url;

	/**
	 * 目前是否是首页
	 */
	private boolean mIsFirstPage = true;
	private BroadcastReceiver mWebReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("com.sunrise.javascript.test")) {// 返回按钮
				boolean isFirstPage = intent.getBooleanExtra(ActivityUtils.FIRST_PAGE_EXTRAL, false);
				// updateWebButtonBack(isFirstPage);
				if (mIsFirstPage && isFirstPage) {
					if (isSinglePage)
						finish();
					else
						eixtApp();
				}

				mIsFirstPage = isFirstPage;
			}

			else if (action.equals(ActivityUtils.WEB_REFRESH_BROADCAST)) {// 刷新
				boolean showBt = intent.getBooleanExtra(Intent.EXTRA_TEXT, false);
				updateWebButtonRefresh(showBt);
			}
		}

	};
	private long firstExitingTime;

	private void eixtApp() {
		if ((System.currentTimeMillis() - firstExitingTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
			firstExitingTime = System.currentTimeMillis();
		} else {
			exit(this);
		}
	}

	private void updateWebButtonBack(boolean isFirstPage) {
		if (isFirstPage)
			setTitleBarLeftClick(null);
		else {
			setTitleBarLeftClick(new OnClickListener() {

				public void onClick(View arg0) {
					onBackPressed();
				}
			});
		}
	}

	// public void onBackPressed() {
	// mWebView.loadUrl("javascript:aNative.goBack(" + false + ")");
	//
	// // if (mWebView.canGoBack()) {
	// // mWebView.goBack();
	// // } else {
	// // super.onBackPressed();
	// // }
	// }
	private void updateWebButtonRefresh(boolean isShow) {
		setTitleBarRightClick(isShow ? mRefreshListener : null);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null)
			isSinglePage = getIntent().getBooleanExtra(Intent.EXTRA_LOCAL_ONLY, true);
		else
			isSinglePage = savedInstanceState.getBoolean(Intent.EXTRA_LOCAL_ONLY, true);

		init();
		initBisinessInformation();

		// IntentFilter filter = new IntentFilter();
		// filter.addAction("com.sunrise.javascript.test");
		// registerReceiver(mWebReciever, filter);
		// MainActivity.getInstance().setCurrentTab(1);
		load(getUrl());

	}

	private void load(String url) {
		// Toast.makeText(WebViewActivity.this, "url:"+url,
		// Toast.LENGTH_LONG).show();
		if (url.startsWith("file://")) {
			mWebView.loadUrl(url + param);
			return;
		}
		if (url.endsWith(".zip")) {
			String fileName = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
			String ver = Preferences.getInstance(this).getPhoneTime();
			if (isNeedUpdateZip(fileName, Long.valueOf(ver)))
				mTask = new DownloadZipPage2Task().execute(url, ver, this);
			else {
				String dir = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR + File.separator + ver + "_" + fileName, null);
				String file = FileUtils.searchFileFormDir(dir, HTML_FILE_NAME);
				mWebView.loadUrl("file://" + file + param);
			}

			return;
		}

		// if (url.endsWith(".zip")) {
		// String fileName = url.substring(url.lastIndexOf('/') + 1,
		// url.lastIndexOf('.'));
		// String ver = getIntent().getStringExtra(
		// ExtraKeyConstant.KEY_LAST_MODIFY);
		// String infos = getPreferences().getString(KEY_HTM_ZIP_VERSION_INFOS);
		// if (TextUtils.isEmpty(infos)){
		// mTask = new DownloadZipPage2Task().execute(url, null,this);
		// }else{
		// try {
		// JSONObject json = new JSONObject(infos);
		// if (json.isNull(fileName)){
		// mTask = new DownloadZipPage2Task().execute(url, null,this);
		// }else{
		// long oldver = json.getLong(fileName);
		// lastUpdateTime = oldver;
		// if(isNeedUpdateZip(fileName, Long.valueOf(oldver))){
		// mTask = new DownloadZipPage2Task().execute(url, oldver+"",this);
		// }else {
		// String dir = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR
		// + File.separator + fileName, null);
		// String file = FileUtils.searchFileFormDir(dir,
		// HTML_FILE_NAME);
		// mWebView.loadUrl("file://" + file);
		// }
		// }
		//
		//
		// } catch (JSONException e) {
		// e.printStackTrace();
		// getPreferences().putString(
		// ExtraKeyConstant.KEY_HTM_ZIP_VERSION_INFOS, null);
		// }
		// }
		// return;
		// }

		else if (url.endsWith(".html")) {
			if (FileUtils.fileIsExist(url)) {
				mWebView.loadUrl("file:///" + FileUtils.getAbsPathOfDir(url) + param);
				// mWebView.loadUrl("http://www.baidu.com");
				return;
			} else
				Toast.makeText(this, url + "文件不存在", Toast.LENGTH_SHORT).show();
		}

		mWebView.loadUrl(url + param);
	}

	private boolean isNeedUpdateZip(String fileName, long newver) {
		String filePath = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR + File.separator + newver + "_" + fileName, null);
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

	// private boolean isNeedUpdateZip(String fileName, long newver) {
	// String filePath = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR
	// + File.separator + newver+"_"+fileName, null);
	// boolean isFileExist = FileUtils.searchFileFormDir(filePath,
	// HTML_FILE_NAME) != null;
	//
	// if(isFileExist){
	// //存在不用下载
	// return false;
	// }else{
	// //不存在就下载
	// return true;
	// }
	// //if (!isFileExist)
	// // return true;
	// //
	// //LogUtlis.d(TAG, filePath + " IS EXIST!!");
	// //
	// //String infos = getPreferences().getString(KEY_HTM_ZIP_VERSION_INFOS);
	// //if (TextUtils.isEmpty(infos))
	// // return true;
	// //LogUtlis.d(TAG, infos);
	// //
	// //try {
	// // JSONObject json = new JSONObject(infos);
	// // if (json.isNull(fileName))
	// // return true;
	// //
	// // long oldver = json.getLong(fileName);
	// // LogUtlis.d(TAG, "oldver = " + oldver + ", newver = " + newver);
	// // return newver > oldver;
	// //} catch (JSONException e) {
	// // e.printStackTrace();
	// // getPreferences().putString(
	// // ExtraKeyConstant.KEY_HTM_ZIP_VERSION_INFOS, null);
	// // return true;
	// //}
	//
	// }

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
		url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		// Toast.makeText(getApplication(), url, Toast.LENGTH_LONG).show();
		if (url.contains("?")) {
			param = url.substring(url.lastIndexOf('?'));
			url = url.substring(0, url.lastIndexOf('?'));
		}

		return url;
	}

	private void initBisinessInformation() {
		if (mBusinessInformation == null)
			mBusinessInformation = new BusinessInformation(getPreferences().getAuthentication(), "123", HardwareUtils.getPhoneIMEI(this),
					HardwareUtils.getPhoneIMSI(this), null);
		mBusinessInformation.setSubAccount(getPreferences().getSubAccount());
		mBusinessInformation.setPhoneNumber(getPreferences().getMobile());
		mBusinessInformation.setAppVersion(String.valueOf(App.sAppCode));
		mBusinessInformation.setLoadStartTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
		mBusinessInformation.setAppTag("SSJF");
		mBusinessInformation.setOauthInforamtion(getPreferences().getAuthentication());
		mWebView.setBusinessInformation(mBusinessInformation);
	}

	private BusinessInformation mBusinessInformation;
	protected OnClickListener mRefreshListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// mWebView.loadUrl("javascript:aNative.reload4Client()");
			mWebView.clearHistory();
			mWebView.clearMatches();
			mWebView.clearView();
			// Toast.makeText(getApplication(), "url:"+url,
			// Toast.LENGTH_LONG).show();
			load(url);
			initWebView();
		}
	};

	private void initWebView() {
		// 调用回调函数
		mHandler = new JavascriptHandler(mWebView);
		mWebView.setJavascriptHandler(mHandler);
		mWebView.setFocusable(true);
		{
			mPageTurnUtils = new PageTurnUtils(mWebView,getString(R.string.pageTurnUtils), mHandler);
			mWebView.addJavascriptEvent(mPageTurnUtils);// TODO 增加业务推荐
		}
		mWebView.addJavascriptInterface(this, CACH_NAME);
		mWebView.setDownloadListener(new MyDownLoadListener());
		// WebSettings webSettings =mWebView.getSettings();
		// mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// webSettings.setBuiltInZoomControls(true);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setJavaScriptEnabled(true);

		// setZoomControlGone(mWebView);
		// 主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
		mWebView.setWebChromeClient(mWebChromeClient);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		// 主要帮助WebView处理各种通知、请求事件,能够处理javascript对设备的功能请求。
		JavascriptWebViewClient javascriptWebViewClient = new JavascriptWebViewClient(this, mHandler) {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mProgressBar.setVisibility(View.GONE);
				updateWebButtonRefresh(true);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				view.loadUrl("javascript:" + mPageTurnUtils.getHtmlRegist());// TODO
																// 加载注册本地功能
																// LogUtlis.d(TAG,
																// JavascriptObjcets.javaScriptMethods);
				updateWebButtonRefresh(false);
				// 增加分享业务
				// view.loadUrl("javascript:function callBusinessPage(phone,msg){alert(phone +',' + msg);}");//TODO
				// 增加分享业务

				mProgressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				// failingUrl = "file:///android_asset/error.html";
				// super.onReceivedError(view, errorCode, description,
				// failingUrl);
				// Log.i("webView：",description+"\n"+failingUrl);
				mProgressBar.setVisibility(View.GONE);
				updateWebButtonRefresh(true);
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

	protected void init() {
		setContentView(R.layout.fargment_webview);

		initViewTitleBar();

		mWebView = (JavaScriptWebView) findViewById(R.id.web_view);
		mWebView.removeJavascriptInterface("accessibility");
		mWebView.removeJavascriptInterface("ccessibilityaversal");
		mWebView.removeJavascriptInterface("searchBoxJavaBridge");

		mProgressBar = (ProgressBar) findViewById(R.id.web_load_progress);
		mScanedAppView = (TextView) findViewById(R.id.scaned_app);
		mWebView = (JavaScriptWebView) findViewById(R.id.web_view);

		mProgressBar = (ProgressBar) findViewById(R.id.web_load_progress);
		mScanedAppView = (TextView) findViewById(R.id.scaned_app);
		initWebView();
	}

	private void initViewTitleBar() {
		setTitleBarLeftClick(new OnClickListener() {
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		// setTitleBarRightClick(null);
		setTitle(getIntent().getStringExtra(Intent.EXTRA_TITLE));

		// updateWebButtonBack(true);
		updateWebButtonRefresh(false);
	}

	// @Override
	// // 设置回退
	// // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.FLAG_CANCELED) {
	// finish(); // 返回键的物理代码
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	// 主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
	private WebChromeClient mWebChromeClient = new WebChromeClient() {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			final JsResult resu = result;
			// 构建一个Builder来显示网页中的alert对话框
			CommUtil.showAlert(PhoneSelectActivity.this, null, message, false, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					resu.confirm();
				}
			});
			return true;
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

		if (getPreferences().getGuideVersion() < App.sAppCode)
			;
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Intent.EXTRA_LOCAL_ONLY, isSinglePage);
		super.onSaveInstanceState(outState);
	}

	public boolean setHostVisibility() {
		return false;
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

		// unregisterReceiver(mWebReciever);
		super.onDestroy();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == 1) {
			if (resultCode != RESULT_OK) {
				exit(this);
			} else {
				initBisinessInformation();
			}
		} else
			super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
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
			CommUtil.showAlert(this, null, task.getException().getMessage(), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					finish();
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
		saveZipHtmlVersion(fileName, Long.parseLong(Preferences.getInstance(this).getPhoneTime()));
		load((String) param);
	}

	@Override
	public void onCancelled(GenericTask task) {
		mTask = null;
		mProgressBar.setVisibility(View.GONE);
		mScanedAppView.setVisibility(View.GONE);
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

}
