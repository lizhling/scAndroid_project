package com.sunrise.marketingassistant.task;

import org.json.JSONObject;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;
import com.sunrise.javascript.utils.LogUtlis;

public class LoginTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param phoneNumber
	 *            登录名
	 * @param password
	 *            登录密码
	 * @param imsi
	 *            sim卡号
	 * @param imei
	 *            设备号
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public final LoginTask execute(String account, String password, TaskListener listener) {

		TaskParams params = new TaskParams(KEY_ACCOUNT, account);
		params.put(KEY_PASSWORD, password);
		setListener(listener);

		execute(params);
		// new GetSubAccountTask(account).start();
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String account = params[0].getString(KEY_ACCOUNT);
		String password = params[0].getString(KEY_PASSWORD);

		try {
			String result = ServerClient.getInstance().login(account, password);
			LogUtlis.e("登录", "登录结果：" + result);
			JSONObject json = new JSONObject(result);
			StringBuilder sb = new StringBuilder();
			if (!json.has("mobile"))
				throw new Exception("未获取此4A帐号对应的手机号码……");

			if (!json.has("authentication"))
				throw new Exception("未获取token……");

			sb.append(json.getString("mobile"));
			sb.append('&');
			sb.append(json.getString("authentication"));

			publishProgress(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
	// class GetSubAccountTask extends Thread {
	// public GetSubAccountTask(String account) {
	// super(account);
	// isGetSubAccountComplete = false;
	// }
	//
	// public void run() {
	// try {
	// String temp = ServerClient.getInstance().getSubAccount(getName(),
	// "YXZS");
	// SubAccountBeen subAccountBeen = JsonUtils.parseJsonStrToObject(temp,
	// SubAccountBeen.class);
	// subAccount = subAccountBeen.getSubAccount();
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// isGetSubAccountComplete = true;
	// }
	// }
	// }

}
