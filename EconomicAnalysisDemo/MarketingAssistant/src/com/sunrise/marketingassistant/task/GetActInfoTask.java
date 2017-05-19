package com.sunrise.marketingassistant.task;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetActInfoTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param  auth
	 * 
	 * @param  login_no
	 *            从帐号
	 * @param chlName
	 *           测试
	 * @param currentPage
	 * @param pageSize
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public final GetActInfoTask execute(String auth,String chlId, TaskListener listener) {
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

		String result = null;
		try {
			result = ServerClient.getInstance().getActInfo(auth,chlId);
			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;

	}
}


