package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.DateUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetMobPicture extends GenericTask implements ExtraKeyConstant {

	public final GetMobPicture execute(String subContent, String filsaccaptId, TaskListener listener) {
		TaskParams params = new TaskParams("filsaccaptId", filsaccaptId);
		params.put("subContent", String.valueOf(subContent));
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String subContent = params[0].getString("subContent");
		String filsaccaptId = params[0].getString("filsaccaptId");

		try {
			String result = ServerClient.getInstance().getMobPicture(DateUtils.formatlong2Time(System.currentTimeMillis(), "yyyyMMdd"),
					DateUtils.formatlong2Time(System.currentTimeMillis(), "yyyyMMdd HH:mm:ss"), subContent, filsaccaptId);

			JSONArray jarr = new JSONObject(result).getJSONArray("RETURN_INFO");
			if (jarr.length() == 0)
				throw new Exception("服务器没有数据返回……");

			jarr = jarr.getJSONObject(0).getJSONArray("DETAIL_INFO");
			if (jarr.length() == 0)
				throw new Exception("周边没有竞争对手渠道网点……");

			ArrayList<ChlBaseInfo> array = new ArrayList<ChlBaseInfo>();

			for (int i = 0; i < jarr.length(); ++i)
				array.add(JsonUtils.parseJsonStrToObject(jarr.getString(i), ChlBaseInfo.class));

			publishProgress(array);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;

	}
}
