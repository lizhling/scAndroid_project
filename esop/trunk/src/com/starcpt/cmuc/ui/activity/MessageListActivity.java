package com.starcpt.cmuc.ui.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;

public class MessageListActivity extends FragmentActivity{
private boolean isStartByNoti=false;
public final static String NOTIFACTION_EXTRAL="notifaction";

@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
    CommonActions.setScreenOrientation(this);
	setContentView(R.layout.message_list);
	registerSkinChangedReceiver();
	isStartByNoti=getIntent().getBooleanExtra(NOTIFACTION_EXTRAL, false);
    if(!isStartByNoti)
	CommonActions.addActivity(this);
    setSkin();
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

private void setSkin(){
	SkinManager.setSkin(this,null,ViewEnum.MessageListActivity);
}

private void registerSkinChangedReceiver(){
	IntentFilter filter = new IntentFilter();
	filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
	registerReceiver(mSkinChangedReceiver, filter);
}

private void unRegisterSkinChangedReceiver(){
	unregisterReceiver(mSkinChangedReceiver);
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
    if(keyCode==KeyEvent.KEYCODE_BACK){
    	if(!isStartByNoti)
    	CommonActions.exitAppDialog(this);
    	else{
    		finish();
    	}
		return true;
	}
	return super.onKeyDown(keyCode, event);
}


}
