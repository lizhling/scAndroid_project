package com.sunrise.marketingassistant.task;

import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.net.ServerClient;

import android.content.Context;
import android.content.Intent;

public class GetTabInfosTask extends GenericTask {
	private UpdateInfo mUpdateInfo;

	public GetTabInfosTask(Context mContext, UpdateInfo mUpdateInfo) {
		super();
		this.mUpdateInfo = mUpdateInfo;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		try {
			String jsonStr = ServerClient.getInstance().getUrlContent(mUpdateInfo.getDownloadUrl());
			Preferences.getInstance(App.sContext).putResNewVersion(String.valueOf(UpdateInfo.TYPE_TAB_INFOS), mUpdateInfo.getNewVersionCode());
			Preferences.getInstance(App.sContext).putString(UpdateInfo.KEY_TAB_INFOS, jsonStr); // 存储最新的数据
			publishProgress(mUpdateInfo.getType());
		} catch (Exception e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

	protected void onProgressUpdate(Object... values) {
		Intent intent = new Intent(ExtraKeyConstant.ACTION_REFRESH_USER_DATA);
		intent.putExtra(Intent.EXTRA_UID, (Integer) values[0]);
		App.sContext.sendBroadcast(intent);
		super.onProgressUpdate(values);
	}

}
