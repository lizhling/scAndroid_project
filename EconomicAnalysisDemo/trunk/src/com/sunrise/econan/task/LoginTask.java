package com.sunrise.econan.task;

import org.json.JSONObject;

import android.text.TextUtils;

import com.sunrise.econan.ExtraKeyConstant;
import com.sunrise.econan.exception.logic.BusinessException;
import com.sunrise.econan.net.ServerClient;
import com.sunrise.javascript.mode.BusinessInformation;

public class LoginTask extends GenericTask implements ExtraKeyConstant {

	// private GenericTask mTask;

	// private BusinessInformation bi;

	// private boolean isCompleteGetSubAccountTask;

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

		// bi = new BusinessInformation(null, null, null, null, null);

		TaskParams params = new TaskParams(KEY_ACCOUNT, account);
		params.put(KEY_PASSWORD, password);
		setListener(listener);

		// mTask = new GetSubAccountTask();
		// mTask.setListener(this);
		// mTask.execute(params);

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

			// Thread.sleep(3 * 1000);
			//
			// result = ServerClient.getInstance().getSubAccount(account,
			// "SSJF");
			// json = new JSONObject(result);
			// if (json.has("subAccount")) {
			//
			// if (TextUtils.isEmpty(subAccount))
			// throw new BusinessException("未获取到相应经分从账号……");
			//
			// bi.setSubAccount(subAccount);
			// } else {
			// throw new BusinessException("未获取到相应经分从账号……");
			// }
			publishProgress(bi);
			// while (!isCompleteGetSubAccountTask)
			// Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			// if (mTask != null)
			// mTask.cancle();
			return TaskResult.FAILED;
		}

		// if (getException() != null) {
		// return TaskResult.FAILED;
		// }

		return TaskResult.OK;
	}

	// private class GetSubAccountTask extends GenericTask {
	//
	// @Override
	// protected TaskResult _doInBackground(TaskParams... params) {
	// String account = params[0].getString(KEY_ACCOUNT);
	// try {
	// //
	// {"belongSystemName":"","orgCode":"2","regionCode":"10008","resultCode":0,"resultMessage":"success","roleId":"2","subAccount":"aclc01,aagh74,aaaX74,aaam99"}
	//
	// String result = ServerClient.getInstance().getSubAccount(account,
	// "SSJF");
	// JSONObject json = new JSONObject(result);
	// if (json.has("subAccount")) {
	// publishProgress(json.getString("subAccount"));
	// } else {
	// publishProgress(json.getString("subAccount"));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// setException(e);
	// return TaskResult.FAILED;
	// }
	//
	// return TaskResult.OK;
	// }
	// }

	// public void onCancelled() {
	// if (mTask != null)
	// mTask.cancle();
	// super.onCancelled();
	// }

	// @Override
	// public String getName() {
	// return null;
	// }
	//
	// @Override
	// public void onPreExecute(GenericTask task) {
	//
	// }
	//
	// @Override
	// public void onPostExecute(GenericTask task, TaskResult result) {
	// if (result != TaskResult.OK) {
	// setException(task.getException());
	// }
	// isCompleteGetSubAccountTask = true;
	// }
	//
	// @Override
	// public void onProgressUpdate(GenericTask task, Object param) {
	// String result = (String) param;
	// bi.setSubAccount(result);
	// }
	//
	// @Override
	// public void onCancelled(GenericTask task) {
	// mTask = null;
	// }
}
