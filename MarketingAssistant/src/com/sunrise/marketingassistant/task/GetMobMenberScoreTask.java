package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.MobMenberScore;
import com.sunrise.marketingassistant.net.ServerClient;

/**
 * 获取店员积分
 * 
 * @author 珩
 */
public class GetMobMenberScoreTask extends GenericTask implements ExtraKeyConstant {

	public GetMobMenberScoreTask execute(String groupId, TaskListener listener) {
		setListener(listener);
		TaskParams params = new TaskParams("groupId", groupId);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String groupId = params[0].getString("groupId");
		try {
			String result = null;

			result = ServerClient.getInstance().getMobMenberScore(groupId);
			JSONArray jsonarr = new JSONObject(result).getJSONArray("resultStr");
			ArrayList<MobMenberScore> array = new ArrayList<MobMenberScore>();
			for (int i = 0; i < jsonarr.length(); ++i) {
				array.add(JsonUtils.parseJsonStrToObject(jsonarr.getString(i), MobMenberScore.class));
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
