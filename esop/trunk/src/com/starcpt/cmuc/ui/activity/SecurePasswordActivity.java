package com.starcpt.cmuc.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;

public class SecurePasswordActivity extends Activity {
private final static int FIXED_SECURE_PASSWORD=0;
private final static int CHECK_SECURE_PASSWORD=1;
public final static String MODIFY_SECURE_PASSWORD_EXTRAL="modify";
private TextView mInputTextViewStatus;
private EditText mPasswordView;
private Button mOkButton;
private Button mCancleButton;
private int mInputPasswordState=FIXED_SECURE_PASSWORD;
private boolean isConfirmPassword=false;
private String mSecurePassword;
private String mConfirmSecurePassowrd;
private boolean mIsNoLock;
private String mSaveSecurePassword;
private boolean isModifySecurePassword=false;
private CmucApplication cmucApplication;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		cmucApplication=(CmucApplication) getApplicationContext();
		final Preferences preferences=cmucApplication.getSettingsPreferences();
		CommonActions.setScreenOrientation(this);
		setContentView(R.layout.secure_password);
		registerSkinChangedReceiver();
		mInputTextViewStatus=(TextView) findViewById(R.id.password_input_status);
		mPasswordView=(EditText) findViewById(R.id.secure_password);
		mOkButton=(Button) findViewById(R.id.btnOK);
		mIsNoLock=preferences.getNoLock();
		isModifySecurePassword=getIntent().getBooleanExtra(MODIFY_SECURE_PASSWORD_EXTRAL, false);
		disableOkButton();
		setSkin();
		mOkButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			if(mInputPasswordState==FIXED_SECURE_PASSWORD){
				if(!isConfirmPassword){
					disableOkButton();
					mOkButton.setText(R.string.confirm);
					mPasswordView.setText(null);
					isConfirmPassword=true;
					mInputTextViewStatus.setText(R.string.confirm_secure_password);
				}else{
					if(!mConfirmSecurePassowrd.equals(mSecurePassword)){
						mInputTextViewStatus.setText(R.string.secure_password_dont_match);
						mOkButton.setText(R.string.continu);
					}else{
						startSecureOptionalActivity();
						preferences.saveSecurePassword(mSecurePassword);
						finish();
						isConfirmPassword=false;
					}
				}
			}else{
				if(!mSecurePassword.equals(mSaveSecurePassword)){
					mInputTextViewStatus.setText(R.string.retry_password);
					mPasswordView.setText(null);
					disableOkButton();
					mOkButton.setText(R.string.next_step);
				}else{
					startSecureOptionalActivity();
					finish();
				}
			}
			}
		});
		
		mCancleButton=(Button) findViewById(R.id.btnCancel);
		mCancleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();			
			}
		});
		mPasswordView.addTextChangedListener(new InputTextWatcher());
		if(mIsNoLock){
			mInputPasswordState=FIXED_SECURE_PASSWORD;
		}else{
			if(isModifySecurePassword){
				mInputPasswordState=FIXED_SECURE_PASSWORD;
			}else{
				mInputPasswordState=CHECK_SECURE_PASSWORD;
				mSaveSecurePassword=preferences.getSecurePassword();
				mInputTextViewStatus.setText(R.string.confirm_secure_password);
				mOkButton.setText(R.string.next_step);
			}
			
		}
		CommonActions.addActivity(this);
	}
	
	private void setSkin(){
		SkinManager.setSkin(this,null, ViewEnum.SecurePasswordActivity);
	}
	
	private void startSecureOptionalActivity(){
		Intent intent=new Intent(this, SecureOptionlActivity.class);
		startActivity(intent);
	}
	
	private void disableOkButton() {
		mOkButton.setTextColor(Color.GRAY);
		mOkButton.setEnabled(false);
	}
	
	private void enabelOkButton() {
		mOkButton.setTextColor(Color.WHITE);
		mOkButton.setEnabled(true);
	}
	
	private class InputTextWatcher implements TextWatcher{
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			switch(mInputPasswordState){
			case FIXED_SECURE_PASSWORD:
				if(!isConfirmPassword){
					if(TextUtils.isEmpty(s)){
						mInputTextViewStatus.setText(R.string.select_your_password);
						disableOkButton();
						return;
						}
					
					if(s.length()<CmucApplication.PASSWORD_LENGTH){
						mInputTextViewStatus.setText(R.string.secure_password_length);
						disableOkButton();
						return;
					}
					
				/*	if(s.toString().matches("^\\d+$")){
						mInputTextViewStatus.setText(R.string.secure_password_contain_chart);
						disableOkButton();
						return;
					}
					
					if(s.toString().){
						mInputTextViewStatus.setText(R.string.secure_password_contain_empty);
						disableOkButton();
						return;
					}	*/
					
					mInputTextViewStatus.setText(R.string.complete_input_secure_password);
					mSecurePassword=s.toString();
					enabelOkButton();
				}else{
					if(TextUtils.isEmpty(s)){
						disableOkButton();
						mOkButton.setText(R.string.confirm);
						return;
						}
					enabelOkButton();
					mConfirmSecurePassowrd=s.toString();
				}
				break;
			case CHECK_SECURE_PASSWORD:
				if(TextUtils.isEmpty(s)){
					disableOkButton();
					mOkButton.setText(R.string.next_step);
					return;
					}
				enabelOkButton();
				mInputTextViewStatus.setText(R.string.confirm_secure_password);
				mSecurePassword=s.toString();
				break;
			}
		}
		
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
