package com.starcpt.cmuc.service;

import com.starcpt.cmuc.broadcast.ScreenOffBroadcastReceiver;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service{
	private ScreenOffBroadcastReceiver mScreenOffBroadcastReceiver=new ScreenOffBroadcastReceiver();
	private  void registerScreenOffReceiver(Context context){
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		context.registerReceiver(mScreenOffBroadcastReceiver, intentFilter);
	}
	
	private  void unregisterScreenOffReceiver(Context context){
		context.unregisterReceiver(mScreenOffBroadcastReceiver);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		registerScreenOffReceiver(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterScreenOffReceiver(this);
	}
}
