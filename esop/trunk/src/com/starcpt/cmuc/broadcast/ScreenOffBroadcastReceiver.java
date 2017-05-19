package com.starcpt.cmuc.broadcast;
import com.starcpt.cmuc.CmucApplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOffBroadcastReceiver extends BroadcastReceiver {
	private static final String SYSTEM_REASON = "reason";  
	private static final String SYSTEM_HOME_KEY = "homekey";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		if(action.equals(Intent.ACTION_SCREEN_OFF)){
			CmucApplication.sNeedShowLock=true;
		}else if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
			String reason = intent.getStringExtra(SYSTEM_REASON); 
			if(reason!=null){
				if (reason.equals(SYSTEM_HOME_KEY)){
					CmucApplication.sNeedShowLock=true;
				}
			}
		}

	}

}
