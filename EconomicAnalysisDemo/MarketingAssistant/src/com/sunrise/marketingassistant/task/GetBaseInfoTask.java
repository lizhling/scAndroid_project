package com.sunrise.marketingassistant.task;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetBaseInfoTask extends GenericTask implements ExtraKeyConstant {

	public final GetBaseInfoTask execute(String auth, String chlId, TaskListener listener) {
		TaskParams params = new TaskParams();
		params.put(KEY_CHL_ID, chlId);
		params.put(KEY_AUTH, auth);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String chlId = params[0].getString(KEY_CHL_ID);
		String auth = params[0].getString(KEY_AUTH);

		try {
			String result = ServerClient.getInstance().getBaseInfo(auth, chlId);
			JSONArray jarr = new JSONObject(result).getJSONArray("RETURN_INFO");
			if (jarr.length() == 0)
				throw new Exception("服务器未返回数据……");

			publishProgress(JsonUtils.parseJsonStrToObject(jarr.getString(0), ChlBaseInfo.class));
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;

	}
}
