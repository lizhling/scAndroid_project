package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;

public class LoginTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param phoneNumber
	 *            登录名
	 * @param password
	 *            登录密码
	 * @param isServePassword
	 *            是否服务密码
	 * @param savePassword
	 *            是否保存密码
	 * @param autoLogin
	 *            自动登录
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public static final LoginTask execute(String phoneNumber, String password, boolean isServePassword, boolean savePassword,
			boolean autoLogin, TaskListener listener) {

		LoginTask task = new LoginTask();
		task.setListener(listener);
		TaskParams params = new TaskParams(KEY_PHONE_NUMBER, phoneNumber);
		params.put(KEY_PASSWORD, password);
		params.put(KEY_LOGIN_TYPE, isServePassword);
		params.put(KEY_SAVE_PASSWORD, savePassword && isServePassword);
		params.put(KEY_AUTO_LOGIN, autoLogin);
		task.execute(params);

		return task;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		// TODO loginTask
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String phoneNumber = params[0].getString(KEY_PHONE_NUMBER);
		String password = params[0].getString(KEY_PASSWORD);
		boolean isServePassword = (Boolean) params[0].get(KEY_LOGIN_TYPE);
		boolean savePassword = (Boolean) params[0].get(KEY_SAVE_PASSWORD);
		boolean autoLogin = (Boolean) params[0].get(KEY_AUTO_LOGIN);

		String result = null;
		try {
			if (isServePassword) {// 系统密码登录
				result = App.sServerClient.login(phoneNumber, password);
			} else {// 随机短信密码登录
				result = App.sServerClient.loginBySmsRamd(phoneNumber, password);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		params[0].put(PUSH_MESSAGE_PASSWORD, "122553");
		App.sSettingsPreferences.savePushMessagePassword("122553");

		try {
			JSONObject obj = new JSONObject(result);
			if (obj.has("RETURN_INFO")) {
				String returnMsg = "";
			}
			params[0].put(KEY_TOKEN, obj.getJSONObject("RETURN").getJSONArray("RETURN_INFO").getJSONObject(0).getString("token"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		publishProgress(params[0]);

		return TaskResult.OK;
	}
}
