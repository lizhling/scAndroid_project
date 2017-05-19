package com.sunrise.micromarketing.task;

import java.util.ArrayList;

import com.sunrise.micromarketing.App;
import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.cache.preferences.Preferences;
import com.sunrise.micromarketing.database.ScmbhcDbManager;
import com.sunrise.micromarketing.database.ScmbhcStore;
import com.sunrise.micromarketing.entity.BusinessMenu;
import com.sunrise.micromarketing.entity.UpdateInfo;
import com.sunrise.micromarketing.net.ServerClient;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

public class GetTabInfosTask extends GenericTask {
	private Context mContext;
	private UpdateInfo mUpdateInfo;

	public GetTabInfosTask(Context mContext, UpdateInfo mUpdateInfo) {
		super();
		this.mContext = mContext;
		this.mUpdateInfo = mUpdateInfo;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		try {
			String jsonStr =  ServerClient.getInstance().getUrlContent(mUpdateInfo.getDownloadUrl());
			Preferences.getInstance(App.sContext).putResVersion(String.valueOf(UpdateInfo.TYPE_TAB_INFOS), mUpdateInfo.getNewVersionCode());
			Preferences.getInstance(App.sContext).putString(UpdateInfo.KEY_TAB_INFOS, jsonStr); //存储最新的数据
			publishProgress(mUpdateInfo.getType());
		} catch (Exception e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}
	
	protected void onProgressUpdate(Object... values){
		Intent intent = new Intent(ExtraKeyConstant.ACTION_REFRESH_USER_DATA);
		intent.putExtra(Intent.EXTRA_UID, (Integer)values[0]);
		App.sContext.sendBroadcast(intent);
		super.onProgressUpdate(values);
	}

}
