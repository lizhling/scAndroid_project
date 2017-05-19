package com.sunrise.scmbhc.task;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;

/**
 * 充值记录
 * 
 * @author 珩
 * @version 2014年9月28日 12:17:33
 */
public class TopupHistoryTask extends GenericTask {

	public void execute(String number, String startTime, String endTime, TaskListener listener) {

		setListener(listener);
		TaskParams params = new TaskParams(App.ExtraKeyConstant.KEY_PHONE_NUMBER, number);
		params.put(App.ExtraKeyConstant.KEY_TIME_START, startTime);
		params.put(App.ExtraKeyConstant.KEY_TIME_END, endTime);
		execute(params);

	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String number = params[0].getString(App.ExtraKeyConstant.KEY_PHONE_NUMBER);
		String startTime = params[0].getString(App.ExtraKeyConstant.KEY_TIME_START);
		String endTime = params[0].getString(App.ExtraKeyConstant.KEY_TIME_END);
		try {
			String str = App.sServerClient.getTopupHistory(number, startTime, endTime, UserInfoControler.getInstance().getAuthorKey());
			JSONArray jsarray = new JSONObject(str).getJSONObject("RETURN").getJSONArray("RETURN_INFO");
			if (jsarray.length() > 0)
				publishProgress(jsarray.getJSONObject(0).getJSONArray("DETAIL_INFO").toString());
			else
				publishProgress();
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
