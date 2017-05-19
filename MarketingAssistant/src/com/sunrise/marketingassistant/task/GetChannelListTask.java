package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.ChannelInfo;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetChannelListTask extends GenericTask implements ExtraKeyConstant {

	public final GetChannelListTask execute(String jsonStr, TaskListener listener) {
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
			result = ServerClient.getInstance().getChannelList(jsonStr);

			ArrayList<ChannelInfo> array = new ArrayList<ChannelInfo>();
			JSONObject json = new JSONObject((String) result);
			if (json.has("resultStr")) {
				json = json.getJSONObject("resultStr");
				if (json.has("groupInfo")) {
					JSONArray info = json.getJSONArray("groupInfo");
					if (info.length() > 0) {
						for (int i = 0; i < info.length(); i++) {
							ChannelInfo dto = (ChannelInfo) JsonUtils.parseJsonStrToObject(info.getJSONObject(i).toString(), ChannelInfo.class);
							dto.setGROUP_NAME(dto.getGroupName());
							dto.setGROUP_ID(dto.getGroupId());
							dto.setHAS_CHILD("1");
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
