package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetUpdateInfoTask extends GenericTask implements ExtraKeyConstant {

	private final String FILE_NAME_UPDATE = "update.json";

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		try {
			String result = null;
			if (App.isTest) {
				// if (FileUtils.fileIsExist(ExtraKeyConstant.APP_SD_PATH_NAME,
				// FILE_NAME_UPDATE)) {
				// String path = FileUtils.getAbsPath(
				// ExtraKeyConstant.APP_SD_PATH_NAME, FILE_NAME_UPDATE);
				// result = FileUtils.readToStringFormFile(path);// 先加载本地文件
				// }
				// if (result == null)// 如果为空，加载assets里的文件
				result = FileUtils.readToStringFormInputStream(App.sContext.getAssets().open(FILE_NAME_UPDATE));
			}

			else
				result = ServerClient.getInstance().getUpdateInfo();

			JSONArray jsarray = new JSONObject(result).getJSONArray("datas");
			ArrayList<UpdateInfo> updateInfos = new ArrayList<UpdateInfo>();
			for (int i = 0; i < jsarray.length(); ++i) {
				updateInfos.add(com.sunrise.javascript.utils.JsonUtils.parseJsonStrToObject(jsarray.getString(i), UpdateInfo.class));
			}

			publishProgress(updateInfos);

		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
