package com.starcpt.cmuc.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.net.ServerClient;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;

public class PushMessageSettingActivity extends Activity {
//private final String TAG="PushMessageSettingActivity";
private EditText mServerIp;	
private EditText mServerPort;
String serverIpContent;
String serverPortContent;
private CmucApplication cmucApplication;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cmucApplication=(CmucApplication) getApplicationContext();
		setContentView(R.layout.message_push_setting_dialog);
		registerSkinChangedReceiver();
		final Preferences preferences=cmucApplication.getSettingsPreferences();
		Button confirm = (Button) findViewById(R.id.confirm);
	    Button cancel = (Button) findViewById(R.id.cancel);    
	    mServerIp=(EditText) findViewById(R.id.server_ip);
	    mServerPort=(EditText) findViewById(R.id.server_port);
	    serverIpContent=preferences.getServerIp();
	    if(serverIpContent!=null)
	    	mServerIp.setText(serverIpContent);
	    else
	    	mServerIp.setText(ServerClient.SERVER_IP);
	    confirm.setOnClickListener(new OnClickListener() {	    
			@Override
			public void onClick(View v) {
				serverIpContent=mServerIp.getText().toString().trim();
				serverPortContent=mServerPort.getText().toString().trim();
				if(TextUtils.isEmpty(serverPortContent)||TextUtils.isEmpty(serverIpContent)){
					Toast.makeText(PushMessageSettingActivity.this, "内容为空", Toast.LENGTH_LONG).show();
					return;
				}
				preferences.savePushServerInfo(serverIpContent, serverPortContent);
				startActivity(new Intent(PushMessageSettingActivity.this,StartActivity.class));
				finish();
			}
		});
	    
	    cancel.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
		SkinManager.setSkin(this,null, ViewEnum.PushMessageSettingActivity);
	}
	
	private void registerSkinChangedReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
		registerReceiver(mSkinChangedReceiver, filter);
	}
	
	private void unRegisterSkinChangedReceiver(){
		unregisterReceiver(mSkinChangedReceiver);
	}

}
