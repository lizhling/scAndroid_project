package com.sunrise.marketingassistant.task;

import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;

public class DelActInfoTask extends GenericTask implements ExtraKeyConstant {

	public final DelActInfoTask execute(String auth,String login_no,String actId,TaskListener listener) {
		TaskParams params = new TaskParams();
		params.put(KEY_AUTH, auth);
		params.put(KEY_ACCOUNT, login_no);
		params.put(KEY_CHL_ID, actId);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String auth = params[0].getString(KEY_AUTH);
		String login_no = params[0].getString(KEY_ACCOUNT);
		String actId = params[0].getString(KEY_CHL_ID);
		
		String result = null;
		try {
			result = ServerClient.getInstance().deleteActInfo(auth, login_no, actId);
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

