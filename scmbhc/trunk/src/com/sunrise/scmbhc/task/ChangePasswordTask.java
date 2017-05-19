package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;

/**
 * 密码修改
 * 
 * @author 珩
 * @version 2014年9月26日12:09:28
 */
public class ChangePasswordTask extends GenericTask {

	public void execute(String number, String oldpassword, String newpassowrd, TaskListener listener) {

		setListener(listener);
		TaskParams params = new TaskParams(App.ExtraKeyConstant.KEY_PHONE_NUMBER, number);
		params.put(App.ExtraKeyConstant.KEY_PASSWORD, oldpassword);
		params.put("newPassword", newpassowrd);
		execute(params);

	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String number = params[0].getString(App.ExtraKeyConstant.KEY_PHONE_NUMBER);
		String oldpassword = params[0].getString(App.ExtraKeyConstant.KEY_PASSWORD);
		String newpassowrd = params[0].getString("newPassword");
		try {
			String str = App.sServerClient.changePassword(number, oldpassword, newpassowrd, UserInfoControler.getInstance().getAuthorKey());
			publishProgress(str);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
