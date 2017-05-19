package com.sunrise.scmbhc.broadcast;

import java.io.IOException;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.utils.CommUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownLoadApkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String downloadUrl=intent.getStringExtra(App.ExtraKeyConstant.KEY_DOWNLOAD_APK_URL);
		try {
			CommUtil.downloadApk(context,downloadUrl,false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
