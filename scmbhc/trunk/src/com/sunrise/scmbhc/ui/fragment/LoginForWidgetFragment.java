package com.sunrise.scmbhc.ui.fragment;

import android.content.Intent;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppActionConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.utils.CommUtil;

public class LoginForWidgetFragment extends LoginFragment {

	protected void initData() {
		// 初始化数据
		mAutoLogin.setEnabled(false);
		mSavaPassword.setEnabled(false);
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {

		TaskParams params = (TaskParams) param;
		String phoneNumber = params.getString(KEY_PHONE_NUMBER);
		UserInfoControler.getInstance().loginIn(phoneNumber, null,(String) params.get(KEY_TOKEN));
		UserInfoControler.getInstance().setWidgetPhoneNumber(phoneNumber);
	}

	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();

		if (TaskResult.OK == result) {
			App.sSettingsPreferences.setLoginOut(false);
			mBaseActivity.sendBroadcast(new Intent(AppActionConstant.ACTION_REFRESH_REQUEST));// 呼叫刷新
			
			onBack();
		}
		

		else if (TaskResult.FAILED == result)
			CommUtil.showAlert(mBaseActivity, getResources().getString(R.string.failed), task.getException().getMessage(), null);
	}

	protected void showForgetPassword(boolean show) {
		super.showForgetPassword(false);
	}
	
}
