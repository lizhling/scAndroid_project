package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import android.text.TextUtils;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;

/**
 * 检测imsi号码
 * 
 * @author 珩
 * 
 */
public class GetPhoneNumberByImsiTask extends GenericTask {

	public final GetPhoneNumberByImsiTask execute(String imsi, TaskListener taskListener) {
		setListener(taskListener);
		execute(new TaskParams("IMSI_NO", imsi));
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String imsi = params[0].getString("IMSI_NO");
		try {
			String result = App.sServerClient.getPhoneNumberByIMSI(imsi, UserInfoControler.getInstance().getAuthorKey(), UserInfoControler.getInstance()
					.getUserName());
			String phoneNumber = new JSONObject(result).getJSONObject("RETURN").getJSONArray("RETURN_INFO").getJSONObject(0).getString("PHONE_NO");

			publishProgress(phoneNumber);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
