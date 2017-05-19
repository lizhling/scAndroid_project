package com.sunrise.micromarketing.task;

import org.json.JSONObject;

import android.text.TextUtils;

import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.exception.logic.BusinessException;
import com.sunrise.micromarketing.net.ServerClient;
import com.sunrise.javascript.mode.BusinessInformation;

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

			JSONObject json = new JSONObject(result);
			BusinessInformation bi = new BusinessInformation(null, null, null, null, null);
			if (json.has("mobile"))
				bi.setPhoneNumber(json.getString("mobile"));
			if (json.has("authentication"))
				bi.setOauthInforamtion(json.getString("authentication"));

			if (json.has("subAccounts")) {
				String subAccount = json.getString("subAccounts");
				if (TextUtils.isEmpty(subAccount))
					throw new BusinessException("此4A帐号不含有经分从账号……");
				bi.setSubAccount(subAccount);
			}
			
			
			publishProgress(bi);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
