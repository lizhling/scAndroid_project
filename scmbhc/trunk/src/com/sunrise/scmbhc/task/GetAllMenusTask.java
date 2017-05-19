package com.sunrise.scmbhc.task;

import android.content.ContentResolver;
import android.content.Context;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.database.ScmbhcStore;
import com.sunrise.scmbhc.entity.AllMenus;
import com.sunrise.scmbhc.entity.UpdateInfo;

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
			AllMenus allMenus = App.sServerClient.getAllMenus(url);
			ContentResolver contentResolver = mContext.getContentResolver();
			ScmbhcDbManager scmbhcDbManager = ScmbhcDbManager.getInstance(contentResolver);
			scmbhcDbManager.deleteAllBusinessMenu();
			scmbhcDbManager.recordBusinessMenuList(allMenus.getDatas());
			contentResolver.notifyChange(ScmbhcStore.BusinessMenu.CONTENT_URI, null);
			App.sSettingsPreferences.putResVersion(String.valueOf(UpdateInfo.TYPE_MENUS), mUpdateInfo.getNewVersionCode());
		} catch (Exception e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
