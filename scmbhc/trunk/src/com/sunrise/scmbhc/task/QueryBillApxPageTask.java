package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;

/**
 * 账单详情查询
 * 
 * @author 珩
 * @since 2014年9月29日10:41:43
 */
public class QueryBillApxPageTask extends GenericTask implements ExtraKeyConstant {

	public final void execute(String phone_no, String time, TaskListener listener) {
		setListener(listener);

		TaskParams param = new TaskParams();
		param.put(KEY_PHONE_NUMBER, phone_no);
		param.put(KEY_TIME, time);
		execute(param);
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String phoneNumber = params[0].getString(KEY_PHONE_NUMBER);
		String time = params[0].getString(KEY_TIME);
		// 用户帐单信息
		try {
			String string = App.sServerClient.getBillApxPage(phoneNumber, time, UserInfoControler.getInstance().getAuthorKey());
			publishProgress(new JSONObject(string).getJSONObject("ROOT").getJSONObject("OUT_DATA").toString());
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
