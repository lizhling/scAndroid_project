package com.sunrise.marketingassistant.task;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetRewardSubjectTreeTask extends GenericTask implements ExtraKeyConstant {
	public final GetRewardSubjectTreeTask execute(String json, TaskListener listener) {
		TaskParams params = new TaskParams();
		params.put(KEY_JSON_STR, json);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String json = params[0].getString(KEY_JSON_STR);

		String result = null;
		try {
			result = ServerClient.getInstance().getRewardSubjectTree(json);
			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;

	}
}
