package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import com.sunrise.scmbhc.App;

/**
 * 获取短信登录密码
 * 
 * @author fuheng
 * 
 */
public class AskForSmsPasswordTask extends GenericTask {
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String phoneNumber = params[0].getString(LoginTask.KEY_PHONE_NUMBER);

		try {
			String result = App.sServerClient
					.askForSmsPasswordInLogin(phoneNumber);

			// {"RETURN":{"RETURN_CODE":"00","RETURN_MESSAGE":"生成随机码成功","IS_ARRAY":0,"RETURN_CHANNEL":"NEW_INTERFACE","RETURN_INFO":[]},"serialNumber":"201401200943814355"}
			JSONObject jsobj = new JSONObject(result);
			result = jsobj.getJSONObject("RETURN").getString("RETURN_MESSAGE");

			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
