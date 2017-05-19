package com.starcpt.cmuc.ui.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.sunrise.javascript.utils.ActivityUtils;

public class LockActivity extends Activity {
	private Button mConfirmButton;
	private TextView mInputViewStaus;
	private EditText mSecurePasswordEditText;
	public final static String AFTER_SPALSH_START_EXTRAL="after_splash_start";
	//private boolean mIsAferSpash;
	private String mSaveSecurePassword;
	private static  int errorCount=0;
	private static final int ERRCOR_COUNT=5;
	private CmucApplication cmucApplication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		CmucApplication.sLockScreenShowing=true;
		cmucApplication=(CmucApplication) getApplicationContext();
		mSaveSecurePassword=cmucApplication.getSettingsPreferences().getSecurePassword();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock_screen);
		TextView title=(TextView) findViewById(R.id.tvTitle);
		title.setText(cmucApplication.getAppName());
		setSkin();
		mConfirmButton=(Button) findViewById(R.id.confirm);
		mInputViewStaus=(TextView) findViewById(R.id.password_input_status);
		mSecurePasswordEditText=(EditText) findViewById(R.id.secure_password);
		mConfirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String inputSecurePassword=mSecurePasswordEditText.getText().toString();
				if(TextUtils.isEmpty(inputSecurePassword)){
					mInputViewStaus.setText(R.string.sorry_retry);
					errorCount++;
					if(errorCount>=ERRCOR_COUNT){
						errorCount=0;
						clearLock();
						goLogin();
					}else{
						mSecurePasswordEditText.setText(null);
						mSecurePasswordEditText.requestFocus();
						mSecurePasswordEditText.setHint(getResources().getString(R.string.last_input_time)+(ERRCOR_COUNT-errorCount));
					}
					return;
				}
				
				if(!inputSecurePassword.equals(mSaveSecurePassword)){
					mInputViewStaus.setText(R.string.sorry_retry);				
					errorCount++;
					if(errorCount>=ERRCOR_COUNT){
						errorCount=0;
						clearLock();
						goLogin();
					}else{
						mSecurePasswordEditText.setText(null);
						mSecurePasswordEditText.requestFocus();
						mSecurePasswordEditText.setHint(getResources().getString(R.string.last_input_time)+(ERRCOR_COUNT-errorCount));
					}
					return;
				}
				CmucApplication.sLockScreenShowing=false;
				errorCount=0;
				finish();
			}
		});
	}
	
	private void clearLock(){
		Preferences preferences=cmucApplication.getSettingsPreferences();
		preferences.saveSecurePassword(null);
		preferences.saveHiddenLock(false);
		preferences.saveScreenOffLock(false);
		preferences.saveNoLock(true);
		preferences.saveAfterStartLock(false);
	}
	
	private void goLogin() {
		Intent intent=new Intent(LockActivity.this, LoginActivity.class);
		intent.setAction(ActivityUtils.CMUC_LOGIN_ACTION);
		startActivity(intent);
		finish();
		CmucApplication.sLockScreenShowing=false;
	}
	
	private void setSkin(){
		SkinManager.setSkin(this,null, ViewEnum.LockActivity);
	}
	
//	private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			setSkin();
//		}
//	};
//	
//	private void registerSkinChangedReceiver(){
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(SkinManager.SKIN_CHANGED_BROADCAST);
//		registerReceiver(mSkinChangedReceiver, filter);
//	}
//	
//	private void unRegisterSkinChangedReceiver(){
//		unregisterReceiver(mSkinChangedReceiver);
//	}
	
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		unRegisterSkinChangedReceiver();
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
