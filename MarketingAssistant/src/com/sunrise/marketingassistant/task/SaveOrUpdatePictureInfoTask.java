package com.sunrise.marketingassistant.task;

import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;

public class SaveOrUpdatePictureInfoTask extends GenericTask implements ExtraKeyConstant {

	public final SaveOrUpdatePictureInfoTask execute(String jsonStr,TaskListener listener) {
		TaskParams params = new TaskParams();
		params.put(KEY_JSON_STR, jsonStr);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String jsonStr = params[0].getString(KEY_JSON_STR);
		String result = null;
		try {
			result = ServerClient.getInstance().saveOrUpdatePictureInfo(jsonStr);
			LogUtlis.e("竞争列表", "竞争列表结果：" + result);
			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;

	}
}
