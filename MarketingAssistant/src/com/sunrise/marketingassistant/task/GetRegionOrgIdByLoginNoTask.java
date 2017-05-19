package com.sunrise.marketingassistant.task;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;
import com.sunrise.javascript.utils.LogUtlis;

public class GetRegionOrgIdByLoginNoTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param phoneNumber
	 *            登录名
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public final GetRegionOrgIdByLoginNoTask execute(String account, TaskListener listener) {

		TaskParams params = new TaskParams(KEY_ACCOUNT, account);
		setListener(listener);

		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String account = params[0].getString(KEY_ACCOUNT);
		// {"RETURN":{"RETURN_CODE":"00","RETURN_MESSAGE":"业务查询成功!","IS_ARRAY":0,"RETURN_CHANNEL":"SERVER_INTERFACE","RETURN_COUNT":1,"RETURN_INFO":[{"LOGIN_NO":"chna5055","GROUP_ID":"2","REGION_ID":"01","GROUP_NAME":"成都"}]},"serialNumber":"201512111619510014"}

		try {
			String result = ServerClient.getInstance().getRegionOrgIdByLoginNo(account);
			LogUtlis.e("获取groupId", "获取groupid：" + result);
			int index = result.indexOf("GROUP_ID") + 11;
			result = result.substring(index, result.indexOf('"', index));
			// JsonUtils.parseJsonStrToObject(jsonStr,
			// SearchGroupIdResult.class);
			LogUtlis.e("获取groupId", "获取groupid：" + result);
			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
