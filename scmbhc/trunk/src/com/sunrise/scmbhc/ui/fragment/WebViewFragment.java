package com.sunrise.scmbhc.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavaScriptWebView;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.JavascriptWebViewClient;
import com.sunrise.javascript.mode.UserInfo;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.fuction.AsiaTokenOperation;
import com.sunrise.scmbhc.fuction.DesCrypCoding;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.activity.MainActivity;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

@SuppressLint("ValidFragment")
public class WebViewFragment extends BaseFragment {
	private final static String TAG = "WebViewFragment";
	private JavaScriptWebView mWebView;
	private BusinessMenu mBusinessMenu;
	private String mUrl;
	private JavascriptHandler mHandler;
	private UserInfo mUserInfo;
	private UserInfoControler mUserInfoControler;
	private ProgressBar mProgressBar;
	private TextView mScanedAppView;

	private String mFilePath;

	// 主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
	private WebChromeClient mWebChromeClient = new WebChromeClient() {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			final JsResult resu = result;
			// 构建一个Builder来显示网页中的alert对话框
			if (isAdded()) {
				CommUtil.showAlert(mBaseActivity, null, message, false, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						resu.confirm();
					}
				});
			}
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mBusinessMenu = bundle.getParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO);
		mUrl = mBusinessMenu.getServiceUrl();
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if (mBusinessMenu != null) {
			if (mBusinessMenu.getName() == getString(R.string.discount_phone)) {
				// ((BaseActivity) getActivity()).sendBroadcast(new
				// Intent(MainActivity.CLOSE_TABHOST_ACTION));
				// getActivity().sendBroadcast(new
				// Intent(MainActivity.OPEN_TABHOST_ACTION));
			} else {
				((BaseActivity) getActivity()).setTitle(mBusinessMenu.getName());
			}
		}
		super.onResume();
	}

	public void onStart() {
		super.onStart();
		if (mBusinessMenu != null) {
			if (mBusinessMenu.getName() == getString(R.string.discount_phone)) {
				((BaseActivity) getActivity()).setTitle(mBusinessMenu.getName());
				((BaseActivity) getActivity()).setTitleLayoutVisibility(false);
				// ((BaseActivity) getActivity()).sendBroadcast(new
				// Intent(MainActivity.CLOSE_TABHOST_ACTION));
				// getActivity().sendBroadcast(new
				// Intent(MainActivity.OPEN_TABHOST_ACTION));
			} else {
				((BaseActivity) getActivity()).setTitle(mBusinessMenu.getName());
			}
		}
		((BaseActivity) getActivity()).setLeftButtonVisibility(View.VISIBLE);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public boolean setHostVisibility() {
		return false;
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fargment_webview, container, false);
		mWebView = (JavaScriptWebView) view.findViewById(R.id.web_view);

		mProgressBar = (ProgressBar) view.findViewById(R.id.web_load_progress);
		mScanedAppView = (TextView) view.findViewById(R.id.scaned_app);
		// 调用回调函数
		mHandler = new JavascriptHandler(mWebView);
		mWebView.setJavascriptHandler(mBaseActivity, mHandler);
		mWebView.addJavascriptInterface(new AsiaTokenOperation(mBaseActivity, mHandler), AsiaTokenOperation.TAG);
		mWebView.addJavascriptInterface(new DesCrypCoding(), JavaScriptConfig.JAVASCRIPT_ENCRYPTION_AND_DECRYPITON);
		mWebView.setDownloadListener(new MyDownLoadListener());
		// WebSettings webSettings =mWebView.getSettings();
		// mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// webSettings.setBuiltInZoomControls(true);
		mWebView.getSettings().setBuiltInZoomControls(false);
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
		
		initAndStartUrl();

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == SccmccInfo.GO_LOGIN_REQUEST_CODE) {
//			switch (resultCode) {
//			case Activity.RESULT_OK:
//				if (mUserInfoControler.checkUserLoginIn()) {
//					String loginPhoneNumber = mUserInfoControler.getUserName();
//					mUserInfo.setLoginPhoneNumber(loginPhoneNumber);
//					mUserInfo.setResultCode(UserInfo.LOGIN_CODE);
//					mUserInfo.setResultMessage(UserInfo.LOGIN_STR);
//					mUserInfo.setToken(mUserInfoControler.getAuthorKey());
//				} else {
//					mUserInfo.setLoginPhoneNumber(null);
//					mUserInfo.setResultCode(UserInfo.UN_LOGIN_CODE);
//					mUserInfo.setResultMessage(UserInfo.UN_LOGIN_STR);
//				}
//				mHandler.sendObject(JavaScriptConfig.GET_USER_INFO_FUNCTION_CALL_BACK_NAME, mUserInfo);
//				break;
//			}
//		}
		
		mWebView.onActivityResult(requestCode, resultCode, data);
	}

	public String js_getFilePath() {
		return mFilePath;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mWebView.canGoBack()) {

			mWebView.goBack();

			return true;
		}
		if (mBusinessMenu != null && mBusinessMenu.getName() == getString(R.string.discount_phone)) {
			LogUtlis.showLogI("webview", "excute Closetabhost_action");
			// ((BaseActivity) getActivity()).sendBroadcast(new
			// Intent(MainActivity.OPEN_TABHOST_ACTION));
			MainActivity.getInstance().setTabHostVisibility(true);
		}
		return false;
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public void onDestroy() {
		mWebView.setWebViewClient(null);
		mWebView.setWebChromeClient(null);
		mWebView.stopLoading();
		super.onDestroy();
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.WebViewFragment;
	}

	private void initAndStartUrl() {
		mUserInfo = new UserInfo();
		mUserInfoControler = UserInfoControler.getInstance();
		if (mUserInfoControler.checkUserLoginIn()) {
			String loginPhoneNumber = mUserInfoControler.getUserName();
			mUserInfo.setLoginPhoneNumber(loginPhoneNumber);
			mUserInfo.setResultCode(UserInfo.LOGIN_CODE);
			mUserInfo.setResultMessage(UserInfo.LOGIN_STR);
			mUserInfo.setToken(mUserInfoControler.getAuthorKey());
			mUserInfo.setCriditsClassInfo(mUserInfoControler.getUserBaseInfo().getSMS_CONTENT());
		} else {
			mUserInfo.setLoginPhoneNumber(null);
			mUserInfo.setResultCode(UserInfo.UN_LOGIN_CODE);
			mUserInfo.setResultMessage(UserInfo.UN_LOGIN_STR);
			mUserInfo.setCriditsClassInfo(null);
		}
		mWebView.setUseInfo(mUserInfo);
		LogUtlis.showLogD(TAG, "mUrl:" + mUrl);
		mWebView.loadUrl(mUrl);		
	}
}
