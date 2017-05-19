package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.ShareOfChannels;
import com.sunrise.marketingassistant.net.ServerClient;
import com.sunrise.javascript.utils.LogUtlis;

public class GetAppQuotaByGroupid extends GenericTask implements ExtraKeyConstant {

	public final GetAppQuotaByGroupid execute(String groupId, String startMonth, String endMonth, TaskListener listener) {

		TaskParams params = new TaskParams("endMonth", endMonth);
		params.put("groupId", groupId);
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
		String groupId = params[0].getString("groupId");
		String startMonth = params[0].getString("startMonth");

		try {
			String result = ServerClient.getInstance().getAppQuotaByGroupid(groupId, startMonth, endMonth);
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsona = jsonObject.getJSONArray("RETURN_INFO");
			if (jsona.length() == 0) {
				throw new Exception(jsonObject.getString("RETURN_MESSAGE"));
			}

			if (result.contains("DETAIL_INFO"))
				jsona = jsona.getJSONObject(0).getJSONArray("DETAIL_INFO");
			if (jsona.length() == 0)
				throw new Exception(jsonObject.getString("RETURN_MESSAGE"));

			ArrayList<ShareOfChannels> array = new ArrayList<ShareOfChannels>();
			for (int i = 0; i < jsona.length(); ++i) {
				ShareOfChannels mbh = JsonUtils.parseJsonStrToObject(jsona.getString(i), ShareOfChannels.class);
				array.add(mbh);
			}

			LogUtlis.e("渠道查询", "结果:" + result);

			publishProgress(array);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
