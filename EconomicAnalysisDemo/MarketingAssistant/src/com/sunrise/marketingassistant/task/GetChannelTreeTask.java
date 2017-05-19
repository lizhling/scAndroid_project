package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.ChannelInfo;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetChannelTreeTask extends GenericTask implements ExtraKeyConstant {

	public final GetChannelTreeTask execute(String jsonStr, TaskListener listener) {
		TaskParams params = new TaskParams();
		params.put(KEY_JSON_STR, jsonStr);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String jsonStr = params[0].getString(KEY_JSON_STR);
		String result = null;
		try {
			result = ServerClient.getInstance().getChannelTree(jsonStr);

			ArrayList<ChannelInfo> array = new ArrayList<ChannelInfo>();
			JSONObject json = new JSONObject(result);
			if (json.has("resultStr")) {
				json = json.getJSONObject("resultStr");

				int count = Integer.parseInt(json.getString("COUNT"));

				if (json.has("GROUPINFO")) {

					if (count == 1) {
						String info = json.getString("GROUPINFO");
						array.add(JsonUtils.parseJsonStrToObject(info, ChannelInfo.class));
					} else {
						JSONArray info = json.getJSONArray("GROUPINFO");
						if (info.length() > 0)
							for (int i = 0; i < info.length(); i++) {
								ChannelInfo dto = (ChannelInfo) JsonUtils.parseJsonStrToObject(info.getJSONObject(i).toString(), ChannelInfo.class);
								array.add(dto);
							}
					}
				}
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
