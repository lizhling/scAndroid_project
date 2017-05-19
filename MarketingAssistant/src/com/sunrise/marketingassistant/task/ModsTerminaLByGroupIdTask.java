package com.sunrise.marketingassistant.task;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;

/**
 * 渠道网点查询
 * 
 * @author 珩
 * 
 */
public class ModsTerminaLByGroupIdTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param routePhoneNumber
	 *            手机号
	 * @param groupId
	 * @param boss
	 *            BOSS工号（boss） 可选 boss暂时无用
	 * @param startNum
	 * @param endNum
	 * @param listener
	 * @return
	 */
	public final ModsTerminaLByGroupIdTask execute(String routePhoneNumber, String groupId, String boss, int startNum, int endNum, TaskListener listener) {

		TaskParams params = new TaskParams("routePhoneNumber", routePhoneNumber);
		params.put("groupId", groupId);
		params.put("boss", boss);
		params.put("startNum", String.valueOf(startNum));
		params.put("endNum", String.valueOf(endNum));
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String routePhoneNumber = params[0].getString("routePhoneNumber");
		String groupId = params[0].getString("groupId");
		String boss = params[0].getString("boss");
		String startNum = params[0].getString("startNum");
		String endNum = params[0].getString("startNum");

		String result = null;
		try {
			result = ServerClient.getInstance().getModsTerminaLByGroupId(routePhoneNumber, groupId, boss, startNum, endNum);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		publishProgress(result);

		return TaskResult.OK;
	}
}
