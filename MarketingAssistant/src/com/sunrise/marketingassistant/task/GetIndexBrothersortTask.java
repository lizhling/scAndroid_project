package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetIndexBrothersortTask extends GenericTask implements ExtraKeyConstant {

	public boolean isMonth;

	public final GetIndexBrothersortTask execute(String groupId, String date, String sortName, TaskListener listener) {

		if (date.length() < 7)
			isMonth = true;

		TaskParams params = new TaskParams("sortName", sortName);
		params.put("groupId", groupId);
		params.put("date", date);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String groupId = params[0].getString("groupId");
		String date = params[0].getString("date");
		String sortName = params[0].getString("sortName");

		try {
			String result = ServerClient.getInstance().getIndexBrothersort(groupId, date, sortName);
			JSONArray jarr = new JSONObject(result).getJSONArray("RETURN_INFO");
			if (jarr.length() == 0) {
				LogUtlis.e("获取数据", result);
				throw new Exception(new JSONObject(result).getString("RETURN_MESSAGE"));
			}
			if (result.contains("DETAIL_INFO")) {
				jarr = jarr.getJSONObject(0).getJSONArray("DETAIL_INFO");
			}

			ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
			for (int i = 0; i < jarr.length(); ++i) {
				array.add(JsonUtils.parseJsonStrToObject(jarr.getString(i), MobileBusinessHall.class));
			}
			publishProgress(array);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;

	}
}
