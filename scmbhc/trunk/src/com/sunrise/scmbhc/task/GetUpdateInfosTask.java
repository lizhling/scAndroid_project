package com.sunrise.scmbhc.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.entity.UpdateInfo;

public class GetUpdateInfosTask extends GenericTask {
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		try {
			String result = App.sServerClient.getUpdateInfos();
			ArrayList<UpdateInfo> updateInfos = new ArrayList<UpdateInfo>();

			JSONArray jsarray = new JSONObject(result).getJSONArray("datas");
			for (int i = 0; i < jsarray.length(); ++i) {
				updateInfos.add(com.sunrise.javascript.utils.JsonUtils.parseJsonStrToObject(jsarray.getString(i), UpdateInfo.class));
			}
			publishProgress(updateInfos);
		} catch (Exception e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}
}
