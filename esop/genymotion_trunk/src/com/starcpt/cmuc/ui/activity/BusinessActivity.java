package com.starcpt.cmuc.ui.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;

public class BusinessActivity extends FragmentActivity{
//private static final String TAG="BusinessActivity";
private ActivityGroupPage mActivityGroup;
private boolean mIsCollectionMenu=false;
private CmucApplication cmucApplication;
public static final String MENU_ROOT_ACTION="com.starcpt.cmuc.MENU_ROOT";
 @Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	cmucApplication=(CmucApplication) getApplicationContext();
	CommonActions.setScreenOrientation(this);
	setContentView(R.layout.business);
	Intent intent=getIntent();
	mIsCollectionMenu=intent.getBooleanExtra(CmucApplication.COLLECTION_MENU_EXTRAL, false);
	if(!mIsCollectionMenu)
	mActivityGroup=(ActivityGroupPage) getParent();
	CommonActions.addActivity(this);
	getStatusBarHeight();  
	registerSkinChangedReceiver();
	registerBackRootOfMenuReceiver();
	setSkin();
}

 	private void setSkin(){
		SkinManager.setSkin(this,null, ViewEnum.BusinessActivity);
	}

 
	private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			setSkin();
		}
	};
	
	private BroadcastReceiver mBackRootOfMenuReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(mActivityGroup!=null){
				if(mActivityGroup.getActivityIds().size()>2){
					mActivityGroup.doBack(1);
				}
			}
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
	
	private void registerBackRootOfMenuReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(MENU_ROOT_ACTION);
		registerReceiver(mBackRootOfMenuReceiver, filter);
	}
	
	private void unRegisterBackRootOfMenuReceiver(){
		unregisterReceiver(mBackRootOfMenuReceiver);
	}
	
@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	unRegisterSkinChangedReceiver();
	unRegisterBackRootOfMenuReceiver();
}

private void getStatusBarHeight() {
	if(cmucApplication.isIsPad()){
		Rect frame = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		cmucApplication.setStatusBarHeight(frame.top);
	}
}

@Override
	protected void onResume() {	   
		super.onResume();
	}

@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

public void exitActivity(){
	if(!mIsCollectionMenu){
		if(mActivityGroup!=null)
		mActivityGroup.doBack();
	}
	else{
		CommonActions.exitAppDialog(this);
	}
}
	
public ActivityGroupPage getActivityGroup() {
	return mActivityGroup;
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode==KeyEvent.KEYCODE_BACK){
			exitActivity();
			return true;
	}		
	return super.onKeyDown(keyCode, event);
}


}
