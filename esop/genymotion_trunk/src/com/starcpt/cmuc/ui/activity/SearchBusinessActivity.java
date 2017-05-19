package com.starcpt.cmuc.ui.activity;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

public class SearchBusinessActivity extends FragmentActivity {
	private ActivityGroupPage mActivityGroup;	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		CommonActions.setScreenOrientation(this);
		setContentView(R.layout.search_business_list);
		mActivityGroup=(ActivityGroupPage) getParent();
		registerSkinChangedReceiver();
		setSkin();
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

	public void exitActivity(){
		if(mActivityGroup!=null)
		mActivityGroup.doBack();
	}
	private void setSkin(){
		SkinManager.setSkin(this,null,ViewEnum.SearchBusinessActivity);
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
