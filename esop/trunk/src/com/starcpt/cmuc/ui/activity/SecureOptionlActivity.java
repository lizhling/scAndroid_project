package com.starcpt.cmuc.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;

public class SecureOptionlActivity extends Activity implements android.view.View.OnClickListener {
	private Button mButtonOK;
	private Button mButtonCancel;
	private Button mModifySecurePasswordView;;
	private TextView mTVTitle;
	private ImageView mHiddenLockView;
	private ImageView mScreenOffLockView;
	private ImageView mNoneLockView;
	private ImageView mAferStartLockView;
	private boolean mIsHiddenLock;
	private boolean mIsScreenOffLock;
	private boolean mIsNoLock;
	private boolean mAfterStartLock;
	private CmucApplication cmucApplication;

@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	cmucApplication=(CmucApplication) getApplicationContext();
	CommonActions.setScreenOrientation(this);
	initView(this);
	registerSkinChangedReceiver();
	CommonActions.addActivity(this);
	//setSkin();
}

private void setSkin(){
	SkinManager.setSkin(this,null, ViewEnum.SecureOptionlActivity);
}

	protected void initView(Context context) {
		Preferences preferences=cmucApplication.getSettingsPreferences();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.security_layout);
		mTVTitle = (TextView) findViewById(R.id.tvTitle);
		mTVTitle.setText(R.string.secure_optional);
		mButtonOK = (Button) findViewById(R.id.btnOK);
		mModifySecurePasswordView=(Button) findViewById(R.id.modify_secure_password_view);
		mModifySecurePasswordView.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SecureOptionlActivity.this, SecurePasswordActivity.class);
				intent.putExtra(SecurePasswordActivity.MODIFY_SECURE_PASSWORD_EXTRAL, true);
				startActivity(intent);
				finish();
			}
		});
		
		mHiddenLockView = (ImageView) findViewById(R.id.hidden_lock_view);
		mIsHiddenLock =preferences.getHiddenLock();
		mIsScreenOffLock = preferences.getScreenOffLock();
		mIsNoLock=preferences.getNoLock();
		if (mIsHiddenLock) {
			mHiddenLockView.setImageResource(R.drawable.checkbox1_selected);
		}
		mHiddenLockView.setOnClickListener(this);
		mScreenOffLockView = (ImageView) findViewById(R.id.screen_off_lock_view);
		if (mIsScreenOffLock) {
			mScreenOffLockView.setImageResource(R.drawable.checkbox1_selected);
		}
		mScreenOffLockView.setOnClickListener(this);
		mNoneLockView=(ImageView) findViewById(R.id.no_lock_view);
		if(mIsNoLock){
			mNoneLockView.setImageResource(R.drawable.checkbox1_selected);
			mModifySecurePasswordView.setVisibility(View.GONE);
		}
		mAfterStartLock=preferences.getAfterStartLock();
		mAferStartLockView=(ImageView) findViewById(R.id.after_start_lock_view);
		if(mAfterStartLock){
			mAferStartLockView.setImageResource(R.drawable.checkbox1_selected);
		}
		mAferStartLockView.setOnClickListener(this);
		mNoneLockView.setOnClickListener(this);
		mButtonOK.setOnClickListener(this);
		mButtonCancel = (Button) findViewById(R.id.btnCancel);
		mButtonCancel.setOnClickListener(this);
		Window dialogWindow = getWindow();
		ColorDrawable dw = new ColorDrawable(0x0000ff00);
		dialogWindow.setBackgroundDrawable(dw);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOK:
			onButtonOK();
			break;
		case R.id.btnCancel:
			onButtonCancel();
			break;
		case R.id.hidden_lock_view:
			mIsHiddenLock = (!mIsHiddenLock);
			if (mIsHiddenLock) {
				mHiddenLockView.setImageResource(R.drawable.checkbox1_selected);
				mNoneLockView.setImageResource(R.drawable.checkbox1_unselect);
				mIsNoLock=false;
			} else {
				mHiddenLockView.setImageResource(R.drawable.checkbox1_unselect);
				if(!mIsScreenOffLock&&!mAfterStartLock){
					mNoneLockView.setImageResource(R.drawable.checkbox1_selected);
					mIsNoLock=true;
				}
			}
			break;
		case R.id.screen_off_lock_view:
			mIsScreenOffLock = (!mIsScreenOffLock);
			if (mIsScreenOffLock) {
				mScreenOffLockView.setImageResource(R.drawable.checkbox1_selected);
				mNoneLockView.setImageResource(R.drawable.checkbox1_unselect);
				mIsNoLock=false;
			} else {
				mScreenOffLockView.setImageResource(R.drawable.checkbox1_unselect);
				if(!mIsHiddenLock&&!mAfterStartLock){
					mNoneLockView.setImageResource(R.drawable.checkbox1_selected);
					mIsNoLock=true;
				}
			}
			break;
		case R.id.after_start_lock_view:
			mAfterStartLock = (!mAfterStartLock);
			if (mAfterStartLock) {
				mAferStartLockView.setImageResource(R.drawable.checkbox1_selected);
				mNoneLockView.setImageResource(R.drawable.checkbox1_unselect);
				mIsNoLock=false;
			} else {
				mAferStartLockView.setImageResource(R.drawable.checkbox1_unselect);
				if(!mIsHiddenLock&&!mIsScreenOffLock){
					mNoneLockView.setImageResource(R.drawable.checkbox1_selected);
					mIsNoLock=true;
				}
			}
			break; 
		case R.id.no_lock_view:
			mIsNoLock = (!mIsNoLock);
			if (mIsNoLock) {
				mNoneLockView.setImageResource(R.drawable.checkbox1_selected);
				mHiddenLockView.setImageResource(R.drawable.checkbox1_unselect);
				mScreenOffLockView.setImageResource(R.drawable.checkbox1_unselect);
				mAferStartLockView.setImageResource(R.drawable.checkbox1_unselect);
				mIsHiddenLock=false;
				mIsScreenOffLock=false;
				mAfterStartLock=false;
			} else {
				mNoneLockView.setImageResource(R.drawable.checkbox1_unselect);
			}
			break;
		}
	}

	protected void onButtonOK() {
		Preferences preferences=cmucApplication.getSettingsPreferences();
		if(mIsNoLock){
			preferences.saveSecurePassword(null);
		}
		preferences.saveHiddenLock(mIsHiddenLock);
		preferences.saveScreenOffLock(mIsScreenOffLock);
		preferences.saveNoLock(mIsNoLock);
		preferences.saveAfterStartLock(mAfterStartLock);
		finish();
	}

	protected void onButtonCancel() {
		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(CmucApplication.sNeedShowLock){
			CommonActions.showLockScreen(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterSkinChangedReceiver();
	}

	private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			setSkin();
		}
	};
	
	private void registerSkinChangedReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
		registerReceiver(mSkinChangedReceiver, filter);
	}
	
	private void unRegisterSkinChangedReceiver(){
		unregisterReceiver(mSkinChangedReceiver);
	}
}
