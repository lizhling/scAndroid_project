/**
 *@(#)WebViewActivity.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package test.javascript;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavaScriptWebView;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.JavascriptWebViewClient;
import com.sunrise.javascript.R;
import com.sunrise.javascript.mode.UserInfo;
import com.sunrise.javascript.utils.SccmccInfo;

/**
 *呈现html页面
 *
 *@version 0.01 January 12 2012
 *@author LIU WEI
 */
public class WebViewActivity extends Activity implements OnClickListener{	
	private String mUrl=null;
	private JavaScriptWebView mWebView;
	private EditText mWebAddressView;
	private Button mVisitWebButton;
	private UserInfo mUserInfo;
	private JavascriptHandler mHandler;
	
	//主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
	private WebChromeClient mWebChromeClient=new WebChromeClient(){
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			final JsResult resu=result;
			// 构建一个Builder来显示网页中的alert对话框
			Builder builder = new Builder(WebViewActivity.this);
			builder.setMessage(message);
			builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					resu.confirm();
				}
			});
			builder.setCancelable(false);
			builder.create();
			builder.show();
			return true;
		}
		
	};	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.webview);
		getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON); 
		mWebView=(JavaScriptWebView) findViewById(R.id.web_view);
		mVisitWebButton=(Button) findViewById(R.id.visit_button);
		mVisitWebButton.setOnClickListener(this);
		mWebAddressView=(EditText) findViewById(R.id.web_address);
		String url=getIntent().getStringExtra("url");
		if(url==null){
			//mUrl="file:///android_asset/index.html";
			mUrl=mWebAddressView.getText().toString();
		}	
		//调用回调函数
		mHandler=new JavascriptHandler(mWebView);
		mWebView.setJavascriptHandler(this,mHandler);
		
		//主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
		mWebView.setWebChromeClient(mWebChromeClient);
		
		//主要帮助WebView处理各种通知、请求事件,能够处理javascript对设备的功能请求。
		JavascriptWebViewClient javascriptWebViewClient=new JavascriptWebViewClient(this,mHandler);
		mWebView.setWebViewClient(javascriptWebViewClient);
		mUserInfo=new UserInfo();
		if(JavaScriptConfig.isLogin()){
			String loginPhoneNumber="13548261301";
			mUserInfo.setLoginPhoneNumber(loginPhoneNumber);
			mUserInfo.setResultCode(UserInfo.LOGIN_CODE);
			mUserInfo.setResultMessage(UserInfo.LOGIN_STR);
		}else{
			mUserInfo.setLoginPhoneNumber(null);
			mUserInfo.setResultCode(UserInfo.UN_LOGIN_CODE);
			mUserInfo.setResultMessage(UserInfo.UN_LOGIN_STR);
		}
		mWebView.setUseInfo(mUserInfo);
		mWebView.loadUrl(mUrl);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==SccmccInfo.GO_LOGIN_REQUEST_CODE){
			switch(resultCode){
			case RESULT_OK:				
				if(JavaScriptConfig.isLogin()){
					String loginPhoneNumber="13548261301";
					mUserInfo.setLoginPhoneNumber(loginPhoneNumber);
					mUserInfo.setResultCode(UserInfo.LOGIN_CODE);
					mUserInfo.setResultMessage(UserInfo.LOGIN_STR);
				}else{
					mUserInfo.setLoginPhoneNumber(null);
					mUserInfo.setResultCode(UserInfo.UN_LOGIN_CODE);
					mUserInfo.setResultMessage(UserInfo.UN_LOGIN_STR);
				}
				mHandler.sendObject(JavaScriptConfig.GET_USER_INFO_FUNCTION_CALL_BACK_NAME,mUserInfo);
				break;
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		mUrl=mWebAddressView.getText().toString();
		if(TextUtils.isEmpty(mUrl)){
			Toast.makeText(this, "请输入网址", Toast.LENGTH_SHORT).show();
		}else{
			mWebView.loadUrl(mUrl);
		}
	}
	
}