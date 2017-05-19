package com.sunrise.econan.task;

import org.json.JSONObject;

import com.sunrise.econan.ExtraKeyConstant;
import com.sunrise.econan.model.AppMenuBean;
import com.sunrise.econan.net.ServerClient;
import com.sunrise.javascript.utils.JsonUtils;

public class GetSSJFInfoTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param phoneNumber
	 *            登录名
	 * @param password
	 *            登录密码
	 * @param imsi
	 *            sim卡号
	 * @param imei
	 *            设备号
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public final GetSSJFInfoTask execute(String appTag, String menuId, String screenw, String screenh, TaskListener listener) {
		setListener(listener);
		TaskParams params = new TaskParams("apptag", appTag);
		params.put("menuid", menuId);
		params.put("screemw", screenw);
		params.put("screenh", screenh);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;
		String appTag = params[0].getString("apptag");
		String menuId = params[0].getString("menuid");
		String screenw = params[0].getString("screemw");
		String screenh = params[0].getString("screenh");
		try {
			String result = ServerClient.getInstance().getMenuDetail(appTag, menuId, screenw, screenh);
			JSONObject json = new JSONObject(result).getJSONObject("appMenuBean");
			AppMenuBean bean = JsonUtils.parseJsonStrToObject(json.toString(), AppMenuBean.class);
			publishProgress(bean);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
