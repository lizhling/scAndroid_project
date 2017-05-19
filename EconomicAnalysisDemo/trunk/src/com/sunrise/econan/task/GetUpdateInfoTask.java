package com.sunrise.econan.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.econan.ExtraKeyConstant;
import com.sunrise.econan.entity.UpdateInfo;
import com.sunrise.econan.net.ServerClient;


public class GetUpdateInfoTask extends GenericTask implements ExtraKeyConstant {

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		try {
			String result = ServerClient.getInstance().getUpdateInfos();

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
