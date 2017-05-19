package com.sunrise.marketingassistant.task;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;
import com.sunrise.javascript.utils.LogUtlis;

/**
 * 所有城市渠道份额查询
 * 
 * @author 珩
 * 
 */
public class GetAppQuotaAllCity extends GenericTask implements ExtraKeyConstant {

	public final GetAppQuotaAllCity execute(String startMonth, String endMonth, TaskListener listener) {

		TaskParams params = new TaskParams("endMonth", endMonth);
		params.put("startMonth", startMonth);
		setListener(listener);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String endMonth = params[0].getString("endMonth");
		String startMonth = params[0].getString("startMonth");

		try {
			String result = ServerClient.getInstance().getAppQuotaByRegionId(startMonth, endMonth);
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsona = jsonObject.getJSONArray("RETURN_INFO");
			if (jsona.length() == 0) {
				throw new Exception(jsonObject.getString("RETURN_MESSAGE"));
			}
			jsona = jsona.getJSONObject(0).getJSONArray("DETAIL_INFO");
			if (jsona.length() == 0) {
				throw new Exception(jsonObject.getString("RETURN_MESSAGE"));
			}
			LogUtlis.e("渠道份额查询", "结果:" + result);
			publishProgress(jsona.toString());
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
