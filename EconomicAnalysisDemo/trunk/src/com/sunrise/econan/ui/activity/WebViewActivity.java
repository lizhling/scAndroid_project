package com.sunrise.econan.ui.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sunrise.econan.EconomicAlaysisApp;
import com.sunrise.econan.ExtraKeyConstant;
import com.sunrise.econan.R;
import com.sunrise.econan.task.DownloadZipPageTask;
import com.sunrise.econan.task.GenericTask;
import com.sunrise.econan.task.TaskListener;
import com.sunrise.econan.task.TaskResult;
import com.sunrise.econan.utils.CommUtil;
import com.sunrise.econan.utils.HardwareUtils;
import com.sunrise.javascript.JavaScriptWebView;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.JavascriptWebViewClient;
import com.sunrise.javascript.mode.BusinessInformation;
import com.sunrise.javascript.utils.FileUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewActivity extends BaseActivity implements TaskListener, ExtraKeyConstant {
	JavaScriptWebView mWebView;
	private final static String TAG = "WebViewActivity";
	private static final String CACH_NAME = "EconAnalCache";
	private JavascriptHandler mHandler;
	private ProgressBar mProgressBar;
	private TextView mScanedAppView;

	private GenericTask mTask;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();
		initBisinessInformation();

		loadUrl(getUrl());
	}

	private void loadUrl(String url) {
		if (url.startsWith("file://")) {
			mWebView.loadUrl(url);
			return;
		}

		if (url.endsWith(".zip")) {
			String fileName = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
			if (FileUtils.fileIsExist(APP_SD_PATH_NAME + File.separator + APP_SD_PATH_NAME + File.separator + fileName, HTML_FILE_NAME))
				mWebView.loadUrl(FileUtils.getAbsPath(APP_SD_PATH_NAME + File.separator + APP_SD_PATH_NAME + File.separator + fileName, HTML_FILE_NAME));
			else
				mTask = new DownloadZipPageTask().execute(url, this);

			return;
		}

		mWebView.loadUrl(url);
	}

	public String getFromAssets(String fileName) {
		try {
			String url = Environment.getExternalStorageDirectory() + "/test/index.html";
			System.out.println("url = " + url);
			InputStreamReader inputReader = new InputStreamReader(new FileInputStream(url), "utf-8");
			// InputStreamReader inputReader = new
			// InputStreamReader(getResources().getAssets().open("test2.html"));
			BufferedReader bufReader = new BufferedReader(inputReader);

			String line = "";
			String Result = "";

			while ((line = bufReader.readLine()) != null)
				Result += line;
			if (bufReader != null)
				bufReader.close();
			if (inputReader != null)
				inputReader.close();
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
	private WebChromeClient mWebChromeClient = new WebChromeClient() {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			final JsResult resu = result;
			// 构建一个Builder来显示网页中的alert对话框
			CommUtil.showAlert(WebViewActivity.this, null, message, false, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					resu.confirm();
				}
			});
			return true;
		}
	};
	private BusinessInformation mBusinessInformation;

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

		if (getPreferences().getGuideVersion() < EconomicAlaysisApp.sAppCode)
			;
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public boolean setHostVisibility() {
		return false;
	}

	protected void init() {
		setContentView(R.layout.fargment_webview);
		mWebView = (JavaScriptWebView) findViewById(R.id.web_view);

		mProgressBar = (ProgressBar) findViewById(R.id.web_load_progress);
		mScanedAppView = (TextView) findViewById(R.id.scaned_app);
		// 调用回调函数
		mHandler = new JavascriptHandler(mWebView);
		mWebView.setJavascriptHandler(mHandler);
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
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
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
			mBusinessInformation = new BusinessInformation(getPreferences().getAuthentication(), "123", HardwareUtils.getPhoneIMEI(this),
					HardwareUtils.getPhoneIMSI(this), null);
		mBusinessInformation.setSubAccount(getPreferences().getSubAccount());
		mBusinessInformation.setPhoneNumber(getPreferences().getMobile());
		mBusinessInformation.setAppVersion(String.valueOf(EconomicAlaysisApp.sAppCode));
		mBusinessInformation.setLoadStartTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
		mBusinessInformation.setAppTag("SSJF");
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
		super.onDestroy();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == 1) {
			if (resultCode != RESULT_OK) {
				exit(this);
			} else {
				initBisinessInformation();
				loadUrl(getPreferences().getSSJFPageUrl());
			}
		} else
			super.onActivityResult(requestCode, resultCode, intent);
	}

	public void onBackPressed() {
		mWebView.loadUrl("javascript:aNative.goBack(" + false + ")");

		// if (mWebView.canGoBack()) {
		// mWebView.goBack();
		// } else {
		// super.onBackPressed();
		// }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			eixtApp();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private long firstExitingTime;

	private void eixtApp() {
		if ((System.currentTimeMillis() - firstExitingTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
			firstExitingTime = System.currentTimeMillis();
		} else {
			exit(this);
		}
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
		getPreferences().setSSJFPageUrl((String) param);
		loadUrl((String) param);
	}

	@Override
	public void onCancelled(GenericTask task) {
		mTask = null;
		mProgressBar.setVisibility(View.GONE);
		mScanedAppView.setVisibility(View.GONE);
	}

	private String getUrl() {
		try {
			PackageManager manager = getApplication().getApplicationContext().getPackageManager();
			Bundle meta = manager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
			if (meta != null) {
				String assetUrl = meta.getString("assetsFileName");
				if (!TextUtils.isEmpty(assetUrl))
					return assetUrl;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return getPreferences().getSSJFPageUrl();
	}
}
