package com.sunrise.scmbhc.task;

import java.io.IOException;

import android.content.Context;

import com.starcpt.analytics.common.FileUtils;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppDirConstant;
import com.sunrise.scmbhc.entity.UpdateInfo;
import com.sunrise.scmbhc.utils.DesCrypUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

public class GetCreditsExchangeInfoTask extends GenericTask {
	private final String TAG = "GetCreditsExchangeInfoTask";
	private Context mContext;
	private final String KEY_TYPE = "type";

	private final String KEY_URL = "url";
	private final String KEY_NEW_VERSIONCODE = "new version code";

	public GetCreditsExchangeInfoTask execute(Context context, UpdateInfo mUpdateInfo, TaskListener taskListener) {
		mContext = context;

		setListener(taskListener);
		TaskParams params = new TaskParams(KEY_URL, mUpdateInfo.getDownloadUrl());
		params.put(KEY_TYPE, mUpdateInfo.getType());
		params.put(KEY_NEW_VERSIONCODE, mUpdateInfo.getNewVersionCode());
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		String url = params[0].getString(KEY_URL);
		LogUtlis.showLogI(TAG, "url请求" + url);
		String type = params[0].get(KEY_TYPE).toString();
		LogUtlis.showLogI(TAG, "type:" + type);
		String filename = AppDirConstant.APP_CREDITS_EXCHANGE_JSON_NAME;

		long newVersionCode = (Long) params[0].get(KEY_NEW_VERSIONCODE);

		String data = null;
		try {
			data = App.sServerClient.getHttpContentData(mContext, url, filename);
			// 保存新版本号
			App.sSettingsPreferences.putResVersion(type, newVersionCode);
		} catch (Exception e) {
			data = FileUtils.getTextFromAssets(mContext, filename, "utf-8");
			try {
				data = DesCrypUtil.DESDecrypt(data);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return TaskResult.FAILED;
		} finally {
			try {
				com.sunrise.scmbhc.utils.FileUtils.saveToDataFile(mContext, data, filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// CreditsExchangeData ceData = JsonUtils.parseJsonStrToObject(data,
		// CreditsExchangeData.class);
		// // 保存公告信息到文件

		return TaskResult.OK;
	}
}
