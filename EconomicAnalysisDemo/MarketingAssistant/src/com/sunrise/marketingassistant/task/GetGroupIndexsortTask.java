package com.sunrise.marketingassistant.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.net.ServerClient;
import com.sunrise.javascript.utils.LogUtlis;

public class GetGroupIndexsortTask extends GenericTask implements ExtraKeyConstant {

	public final GetGroupIndexsortTask execute(String sortType, String sortName, ArrayList<MobileBusinessHall> array, TaskListener listener) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.size(); ++i) {
			sb.append('\'').append(array.get(i).getGROUP_ID()).append('\'');
			if (i < array.size() - 1)
				sb.append(',');
		}

		TaskParams params = new TaskParams("sortType", sortType);
		params.put("sortName", sortName);
		params.put("groupIds", sb.toString());
		setListener(listener);
		execute(params);
		return this;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String sortType = params[0].getString("sortType");
		String sortName = params[0].getString("sortName");
		String groupIds = params[0].getString("groupIds");

		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));

		try {
			String result = ServerClient.getInstance().getGroupIndexsort(groupIds, date, sortName, sortType);

			JSONArray jsona = new JSONObject(result).getJSONArray("RETURN_INFO");
			if (jsona.length() == 0)
				throw new Exception("服务器返回结果中没包含任何数据……");

			jsona = jsona.getJSONObject(0).getJSONArray("DETAIL_INFO");
			if (jsona.length() == 0)
				throw new Exception("服务器返回结果中没包含任何数据……");

			ArrayList<MobileBusinessHall> arrayIndexsort = new ArrayList<MobileBusinessHall>();
			for (int i = 0; i < jsona.length(); ++i)
				arrayIndexsort.add(JsonUtils.parseJsonStrToObject(jsona.getString(i), MobileBusinessHall.class));

			LogUtlis.i("渠道查询", "结果:" + result);
			publishProgress(arrayIndexsort);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
