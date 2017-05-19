package com.starcpt.cmuc.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.broadcast.CheckUpdateApkBroadcastReceiver;
import com.starcpt.cmuc.model.AppMenu;
import com.starcpt.cmuc.ui.fragment.WebViewFragment;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.sunrise.javascript.utils.ActivityUtils;

public class WebViewActivity extends FragmentActivity {
	// private String TAG="WebViewActivity";
	private TextView mTitleView;
	private Button mTopBackView;
	private WebViewFragment mWebViewFragment;
	private FragmentManager mFragmentManager = getSupportFragmentManager();
	private int mBusinessId;
	private CheckUpdateApkBroadcastReceiver installSupportDeviceBroadcastReceiver = new CheckUpdateApkBroadcastReceiver(
			WebViewActivity.this);
	private CmucApplication cmucApplication;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cmucApplication=(CmucApplication) getApplicationContext();
		CommonActions.setScreenOrientation(this);
		setContentView(R.layout.webview);
		initTitleBarView();
		updateWebViewFragment(getIntent());
		CommonActions.addActivity(this);
		registerSkinChangedReceiver();
		registerSupportDeviceReceiver();
		setSkin();
	}

	private void setTitle(String title) {
		mTitleView.setText(title);
		mWebViewFragment.setTitle(title);
	}

	private void setSkin() {
		SkinManager.setSkin(this, null, ViewEnum.WebViewActivity);
	}

	public void registerSupportDeviceReceiver() {
		registerReceiver(installSupportDeviceBroadcastReceiver,
				new IntentFilter(CheckUpdateApkBroadcastReceiver.CHECK_APK_UPDATE_ACTION));
	}

	public void unregisterSupportDeviceReceiver() {
		unregisterReceiver(installSupportDeviceBroadcastReceiver);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		updateWebViewFragment(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(CmucApplication.sNeedShowLock){
			CommonActions.showLockScreen(this);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	private void initTitleBarView() {
		mTitleView = (TextView) findViewById(R.id.top_title_text);
		mTopBackView = (Button) findViewById(R.id.top_back_btn);
		mTopBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mWebViewFragment.doback();
			}
		});
	}

	private void updateWebViewFragment(Intent intent) {
		boolean isBusinessWeb = intent.getBooleanExtra(
				CmucApplication.IS_BUSINESS_WEB_EXTRAL, false);
		String url = intent.getStringExtra(CmucApplication.CONTENT_EXTRAL);
		String appTag = intent.getStringExtra(CmucApplication.APP_TAG_EXTRAL);
		String menuId = intent.getStringExtra(CmucApplication.MENU_ID);
		String helpId=intent.getStringExtra(CmucApplication.HELP_ID_EXTRAL);
		mBusinessId = intent.getIntExtra(CmucApplication.BUSINESS_ID_EXTRAL, -1);
		String title = intent.getStringExtra(CmucApplication.TITLE_EXTRAL);
		String parentPositions = intent.getStringExtra(CmucApplication.POSITIONS_EXTRAL);
		long childVersion=intent.getLongExtra(CmucApplication.CHILD_VERSION_EXTRAL, -1);
		AppMenu appMenu=new AppMenu(null, url, mBusinessId, null, appTag);
		appMenu.setMenuId(Integer.valueOf(menuId));
		appMenu.setChildVersion(childVersion);
		if (mWebViewFragment == null) {
			mWebViewFragment = new WebViewFragment(appMenu,isBusinessWeb,helpId);
			mWebViewFragment.setContainWebActivity(true);
			mWebViewFragment.setManualStart(true);
			FragmentTransaction fragmentTransaction = mFragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.webview_fragment_panel, mWebViewFragment);
			fragmentTransaction.commitAllowingStateLoss();
		} else {
			mWebViewFragment.update(appMenu,isBusinessWeb,helpId);
			mWebViewFragment.setManualStart(true);
		}
		setTitle(title);
		mWebViewFragment.setParentPositions(parentPositions);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityUtils.GO_LOGIN_REQUEST_CODE) {
			switch (resultCode) {
			case RESULT_OK:
				String authentication =cmucApplication.getSettingsPreferences().getAuthentication();
				mWebViewFragment.setBusinessInformation(authentication);
				break;
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mWebViewFragment.doback();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterSkinChangedReceiver();
		unregisterSupportDeviceReceiver();
	}

	private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			setSkin();
		}
	};

	private void registerSkinChangedReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
		registerReceiver(mSkinChangedReceiver, filter);
	}

	private void unRegisterSkinChangedReceiver() {
		unregisterReceiver(mSkinChangedReceiver);
	}
	
}
