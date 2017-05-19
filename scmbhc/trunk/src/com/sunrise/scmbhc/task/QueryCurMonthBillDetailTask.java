package com.sunrise.scmbhc.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;

/**
 * 查询本月实时账单
 * 
 * @author 珩
 * @version 2015年6月29日 15:28:25
 */
public class QueryCurMonthBillDetailTask extends GenericTask {

	public void execute(String number, String month, TaskListener listener) {
		setListener(listener);
		TaskParams params = new TaskParams(App.ExtraKeyConstant.KEY_PHONE_NUMBER, number);
		params.put(App.ExtraKeyConstant.KEY_TIME_START, month);
		execute(params);

	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String number = params[0].getString(App.ExtraKeyConstant.KEY_PHONE_NUMBER);
		String month = params[0].getString(App.ExtraKeyConstant.KEY_TIME_START);
		try {
			String str = App.sServerClient.queryCurMonthBillDetail(number, month, UserInfoControler.getInstance().getAuthorKey());

			JSONObject jsonobj = new JSONObject(str);
			JSONArray jsarr = jsonobj.getJSONObject("RETURN").getJSONArray("RETURN_INFO");
			if (jsarr.length() > 0)
				publishProgress(jsarr.getString(0));

		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
