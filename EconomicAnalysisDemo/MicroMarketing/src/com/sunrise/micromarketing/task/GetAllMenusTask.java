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

public class GetAllMenusTask extends GenericTask {
	private Context mContext;
	private UpdateInfo mUpdateInfo;

	public GetAllMenusTask(Context mContext, UpdateInfo mUpdateInfo) {
		super();
		this.mContext = mContext;
		this.mUpdateInfo = mUpdateInfo;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String url = mUpdateInfo.getDownloadUrl();
		try {
			ArrayList<BusinessMenu> allMenus = ServerClient.getInstance().getAllMenus(url);
			ContentResolver contentResolver = mContext.getContentResolver();
			ScmbhcDbManager scmbhcDbManager = ScmbhcDbManager.getInstance(contentResolver);
			scmbhcDbManager.deleteAllBusinessMenu();
			scmbhcDbManager.recordBusinessMenuList(allMenus);
			contentResolver.notifyChange(ScmbhcStore.BusinessMenu.CONTENT_URI, null);
			Preferences.getInstance(App.sContext).putResVersion(String.valueOf(UpdateInfo.TYPE_MENUS), mUpdateInfo.getNewVersionCode());
			publishProgress();
		} catch (Exception e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}
	
	protected void onProgressUpdate(Object... values){
		App.sContext.sendBroadcast(new Intent(ExtraKeyConstant.ACTION_REFRESH_USER_DATA));
		super.onProgressUpdate(values);
	}

}
