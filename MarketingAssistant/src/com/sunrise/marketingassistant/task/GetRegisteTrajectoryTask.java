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
import com.sunrise.marketingassistant.entity.RegisteTrajectoryBeen;
import com.sunrise.marketingassistant.net.ServerClient;

/**
 * 签到轨迹
 * 
 * @author 珩
 * 
 */
public class GetRegisteTrajectoryTask extends GenericTask implements ExtraKeyConstant {

	public final GetRegisteTrajectoryTask execute(String loginNo, String startTime, String endTime, TaskListener listener) {

		TaskParams params = new TaskParams("loginNo", loginNo);
		params.put("endTime", endTime);
		params.put("startTime", startTime);
		setListener(listener);
		execute(params);

		return this;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String loginNo = params[0].getString("loginNo");
		String startTime = params[0].getString("startTime");
		String endTime = params[0].getString("endTime");

		try {

			String result = ServerClient.getInstance().getMobRegeste(loginNo, startTime, endTime);
			ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
			JSONArray jarr = new JSONObject(result).getJSONArray("resultStr");
			boolean containSameId = false;
			SimpleDateFormat sdf = new SimpleDateFormat(ExtraKeyConstant.PATTERN_DATA_AND_TIME);

			for (int i = 0; i < jarr.length(); ++i) {
				RegisteTrajectoryBeen item = JsonUtils.parseJsonStrToObject(jarr.getString(i), RegisteTrajectoryBeen.class);
				containSameId = false;
				for (int j = 0; j < array.size(); ++j)
					if (item.getGROUP_ID().equals(array.get(j).getGROUP_ID())) {
						array.get(j).addREGISTIME(sdf.format(new Date(item.getREGISTIME())));
						containSameId = true;
						break;
					}
				if (!containSameId)
					array.add(item.toMobileBusinessHall(ExtraKeyConstant.PATTERN_DATA_AND_TIME));
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
