package com.starcpt.cmuc.ui.activity;


import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.broadcast.CheckUpdateApkBroadcastReceiver;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.model.CachItem;
import com.starcpt.cmuc.model.bean.UpdateInfoBean;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.SkinSwitchActivity;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.starcpt.cmuc.ui.view.MultiChoicDialog;
import com.starcpt.cmuc.ui.view.SingleChoiceDialog;
import com.starcpt.cmuc.utils.FileUtils;
import com.sunrise.javascript.JavaScriptConfig;

public class SettingActivity extends Activity {	
//private static final String TAG="SettingActivity";
public final static String CHANGE_SCERRN_DIRECTION_ACTION="com.starcpt.cmuc.ui.activity.SCREEN_DIRECTION";
public final static String SCREEN_DIRECTION_EXTRAL="screen_direction";
private TextView mTitleView;
private Button mBackView;
private LinearLayout mSkinSettingView;
private LinearLayout mClearCachView;
private LinearLayout mReadFeedBackView;
private LinearLayout mScreenDirectionPanelView;
private LinearLayout mSecurePasswordPanelView;
private LinearLayout mVersionUpdatePanelView;
private LinearLayout mAddSupportDevicePanelView;
private LinearLayout mAddSupportDeviceSplitView;
private LinearLayout mFunctionGuidePanelView;
private LinearLayout mBusinessGuidePanelView;
private LinearLayout mOpreGuidePanelView;
private LinearLayout mSharePanelView;
private ArrayList<String> mScreenDirections=new ArrayList<String>();
private  CheckUpdateApkBroadcastReceiver checkApkUpdateBroadcastReceiver=new CheckUpdateApkBroadcastReceiver(SettingActivity.this);
private CmucApplication cmucApplication;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	cmucApplication=(CmucApplication) getApplicationContext();
	CommonActions.setScreenOrientation(this);
	setContentView(R.layout.setting);
	registerSkinChangedReceiver();
	registerCheckUpdateApkBrocastReceiver();
	initView();
	CommonActions.addActivity(this);
	String[] screenDirections=getResources().getStringArray(R.array.screen_directions); 
	for(String screenDirection:screenDirections){
		mScreenDirections.add(screenDirection);
	}
	//setSkin();
}

private void setSkin(){
	SkinManager.setSkin(this,null, ViewEnum.SettingActivity);
}

private void initView(){
	mTitleView=(TextView) findViewById(R.id.top_title_text);
	initTitleBar(getIntent());
	mBackView=(Button) findViewById(R.id.top_back_btn);
	mBackView.setOnClickListener(new OnClickListener() {		
		@Override
		public void onClick(View v) {
			sendCancleCheckBroadcast();
			finish();
		}
	});
	
	mSkinSettingView=(LinearLayout) findViewById(R.id.skin_setting);
	mSkinSettingView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			skinSetting();
		}
	});

	mClearCachView=(LinearLayout) findViewById(R.id.clear_cach);
	mClearCachView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			createClearCachDialog();
			
		}
	});
	
	mReadFeedBackView=(LinearLayout) findViewById(R.id.read_feedback);
	mReadFeedBackView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(SettingActivity.this, FeedBacksActivity.class);
			startActivity(intent);
		}
	});
	
	mScreenDirectionPanelView=(LinearLayout) findViewById(R.id.screen_direction);

	/*if(!CmucApplication.sIsPad){
		mScreenDirectionPanelView.setVisibility(View.GONE);
		mScreenDirectionSplit.setVisibility(View.GONE);
	}*/
	mScreenDirectionPanelView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			createSetScreenDirectionDialog();
		}
		
	});
	
	mSecurePasswordPanelView=(LinearLayout) findViewById(R.id.secure_password);
	mSecurePasswordPanelView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startSecurePasswordActivity();
		}
	});
	
	mVersionUpdatePanelView=(LinearLayout) findViewById(R.id.version_update);
	mVersionUpdatePanelView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String checkType=null;
			if(JavaScriptConfig.sIsSupportThirdParthDevice)
				checkType=UpdateInfoBean.HAVE_DEVICE_CHECK_TYPE;
			else
				checkType=UpdateInfoBean.NO_DEVICE_CHECK_TYPE;
			sendCheckApkBroadcast(checkType);
		}
	});
	
	mAddSupportDevicePanelView=(LinearLayout) findViewById(R.id.add_support_device);
	mAddSupportDeviceSplitView=(LinearLayout) findViewById(R.id.support_device_split);
	if(!JavaScriptConfig.sIsSupportThirdParthDevice){
		mAddSupportDevicePanelView.setVisibility(View.VISIBLE);
		mAddSupportDeviceSplitView.setVisibility(View.VISIBLE);
	}
	
	mAddSupportDevicePanelView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			sendCheckApkBroadcast(UpdateInfoBean.NO_DEVICE_TO_SUPPORT_DEVICE_CHECK_TYPE);
		}
	});
	
	mFunctionGuidePanelView=(LinearLayout) findViewById(R.id.function_guide_panel);
	mFunctionGuidePanelView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(SettingActivity.this, GuideActivity.class);
			intent.putExtra(CmucApplication.DISPLAY_GUIDE_TYPE, GuideActivity.DISPLAY_FUNCTION_GUIDE_TYPE);
			startActivity(intent);
		}
	});
	
	mBusinessGuidePanelView=(LinearLayout) findViewById(R.id.business_guide_panel);
	mBusinessGuidePanelView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(SettingActivity.this, GuideActivity.class);
			intent.putExtra(CmucApplication.DISPLAY_GUIDE_TYPE, GuideActivity.DISPLAY_BUSINESS_GUIDE_TYPE);
			startActivity(intent);
		}
	});
	
	mOpreGuidePanelView=(LinearLayout) findViewById(R.id.opre_guide_panel);
	mOpreGuidePanelView.setOnClickListener(new OnClickListener() {		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(SettingActivity.this, GuideActivity.class);
			intent.putExtra(CmucApplication.DISPLAY_GUIDE_TYPE, GuideActivity.DISPLAY_OPER_GUIDE_TYPE);
			startActivity(intent);
		}
	});
	
	mSharePanelView=(LinearLayout) findViewById(R.id.share_panel);
	mSharePanelView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(SettingActivity.this, ShareActivity.class);
			startActivity(intent);
		}
	});
	
}

public  void registerCheckUpdateApkBrocastReceiver(){
	registerReceiver(checkApkUpdateBroadcastReceiver, new IntentFilter(CheckUpdateApkBroadcastReceiver.CHECK_APK_UPDATE_ACTION));
}

public  void unregisterCheckUpdateApkBrocastReceiver(){
	unregisterReceiver(checkApkUpdateBroadcastReceiver);
}

private void sendCheckApkBroadcast(String checkType){
	Intent intent=new Intent(CheckUpdateApkBroadcastReceiver.CHECK_APK_UPDATE_ACTION);
	intent.putExtra(CmucApplication.CHECK_TYPE_EXTRAL, checkType);
	sendBroadcast(intent);
}

private void sendCancleCheckBroadcast(){
	Intent intent=new Intent(CheckUpdateApkBroadcastReceiver.CANCLE_CHECK);
	sendBroadcast(intent);
}

private void startSecurePasswordActivity(){
	Intent intent=new Intent(this, SecurePasswordActivity.class);
	startActivity(intent);
}

protected void createSetScreenDirectionDialog() {
	final Preferences preferences=cmucApplication.getSettingsPreferences();
	final int screenOrientation=preferences.getScreenDirection();
	final int screenDirectionIndex=screenOrientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?0:1;
	final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(this, mScreenDirections);
	singleChoiceDialog.setTitle(getString(R.string.screen_direction));
	singleChoiceDialog.setSelectItem(screenDirectionIndex);
	singleChoiceDialog.setOnOKButtonListener(new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			int selItem = singleChoiceDialog.getSelectItem();
			if(screenDirectionIndex!=selItem){
				int screeDirection=	(screenOrientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				preferences.saveScreenDirection(screeDirection);
				CommonActions.setScreenOrientation(SettingActivity.this);
				CommonActions.setApplicationScreenDirection();
				cmucApplication.setScreenDirection(screeDirection);
				boolean screenD=cmucApplication.getScreenDirection()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE?true:false;
				boolean openWebViewInFragment=cmucApplication.isIsPad()&&cmucApplication.isDeviceSupportHtlm5Pool()&&screenD;
				cmucApplication.setOpenWebViewInFragment(openWebViewInFragment);
				Intent intent=new Intent(CHANGE_SCERRN_DIRECTION_ACTION);
				intent.putExtra(SCREEN_DIRECTION_EXTRAL, screeDirection);
				cmucApplication.getWebViewWindows().clear();
				sendBroadcast(intent);
			}
		}
		
	});
	singleChoiceDialog.show();
}

private void initTitleBar(Intent intent) {
	String title=intent.getStringExtra(CmucApplication.TITLE_EXTRAL);
	mTitleView.setText(title);
}

private ArrayList<String> getAllDirSize(){
	
 ArrayList<String> sizes=new ArrayList<String>();
 for(CachItem cachItemBean:CmucApplication.sCachItemBeans){
	 String rpath=cachItemBean.getDirName();
	 if(rpath==null)
		 rpath=cmucApplication.getUserJsonDir();
      String path=FileUtils.getAbsPath(rpath, null);
      double size=FileUtils.getSizeOfFile(path)/1024;
      sizes.add(size+"K");
 }
 return sizes;
}

private void skinSetting(){
	 Intent intent = new Intent(this,SkinSwitchActivity.class);
	 startActivity(intent);
}

private void createClearCachDialog(){
	ArrayList<String> sizes=getAllDirSize();
	ArrayList<String> names=new ArrayList<String>();
	for(int i=0;i<CmucApplication.sCachItemBeans.size();i++){
		String cachName=CmucApplication.sCachItemBeans.get(i).getName();
		String size=sizes.get(i);
		String name=cachName+"<font color=#808080>"+"("+size+")"+"</font>";
		names.add(name);
	}
	final MultiChoicDialog clearCachDialog=new MultiChoicDialog(this, names);
	clearCachDialog.setTitle(getString(R.string.clear_cache));
	clearCachDialog.setOnOKButtonListener(new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			boolean []selItems = clearCachDialog.getSelectItem();
			for(int i=0;i<selItems.length;i++){
				if(selItems[i]){
					CachItem cachItemBean=CmucApplication.sCachItemBeans.get(i);
					 String rpath=cachItemBean.getDirName();
					 if(rpath==null)
						 rpath=cmucApplication.getUserJsonDir();
					String path=FileUtils.getAbsPath(rpath, null);
					FileUtils.deleteFileAbsolutePath(path);
				}
			}
			Toast.makeText(SettingActivity.this, getString(R.string.clear_cach_success), Toast.LENGTH_SHORT).show();
			clearCachDialog.dismiss();
		}
		
	
	});
	clearCachDialog.show();
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
public boolean onKeyDown(int keyCode, KeyEvent event) {
	if(keyCode==KeyEvent.KEYCODE_BACK){
		sendCancleCheckBroadcast();
		finish();
		}
	return super.onKeyDown(keyCode, event);
}

@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	unRegisterSkinChangedReceiver();
	unregisterCheckUpdateApkBrocastReceiver();
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
