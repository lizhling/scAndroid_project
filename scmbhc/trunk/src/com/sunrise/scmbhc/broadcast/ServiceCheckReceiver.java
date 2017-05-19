package com.sunrise.scmbhc.broadcast;

import org.androidpn.client.PushMessageService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sunrise.scmbhc.utils.CommUtil;

public class ServiceCheckReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) { 
	        //检查Service状态 
	    	checkServiceRunning(context);
	     } else {
	    	 if (CommUtil.isOpenNetwork(context)) {
	    		 checkServiceRunning(context); 
	    	 }
	     }  
	    
	}
	
	private void checkServiceRunning(Context context){
	    boolean isServiceRunning = false; 
	    ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
	    for (RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) { 
	    if("org.androidpn.client.PushMessageService".equals(service.service.getClassName())) 
	           //Service的类名 
	    { 
	    isServiceRunning = true; 
	    } 
	      
	     } 
	    if (!isServiceRunning) { 
	    Intent i = new Intent(context, PushMessageService.class); 
	           context.startService(i); 
	    } 
	}
}
