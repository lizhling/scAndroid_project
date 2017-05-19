package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetCompetitorInfoTask extends GenericTask implements ExtraKeyConstant {

	public final GetCompetitorInfoTask execute(double latiTude, double longitude, String chl_type, TaskListener listener) {
		TaskParams params = new TaskParams("chl_type", chl_type);
		params.put("latiTude", String.valueOf(latiTude));
		params.put("longiTude", String.valueOf(longitude));
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String latiTude = params[0].getString("latiTude");
		String longiTude = params[0].getString("longiTude");
		String chl_type = params[0].getString("chl_type");

		try {
			String result = ServerClient.getInstance().getCompetitorInfo(latiTude, longiTude, chl_type);

			JSONArray jarr = new JSONObject(result).getJSONArray("RETURN_INFO");
			if (jarr.length() == 0)
				throw new Exception("服务器没有数据返回……");

			jarr = jarr.getJSONObject(0).getJSONArray("DETAIL_INFO");
			if (jarr.length() == 0)
				throw new Exception( "周边没有竞争对手渠道网点……");

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
