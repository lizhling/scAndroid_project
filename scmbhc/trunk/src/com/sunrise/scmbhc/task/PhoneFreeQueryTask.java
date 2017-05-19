package com.sunrise.scmbhc.task;

import android.text.TextUtils;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.entity.PhoneFreeQuery;
import com.sunrise.scmbhc.entity.PhoneFreeQuery.EmumState;
import com.sunrise.scmbhc.utils.CommUtil;

/**
 * 用户余量查询 。 用于流量服务
 * 
 * @author fuheng
 * 
 */
public class PhoneFreeQueryTask extends GenericTask implements ExtraKeyConstant {

	public PhoneFreeQueryTask execute(String phoneNumber, TaskListener taskListener) {
		setListener(taskListener);
		execute(new TaskParams(KEY_PHONE_NUMBER, phoneNumber));
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		String phoneNumber = params[0].getString(KEY_PHONE_NUMBER);
		if (TextUtils.isEmpty(phoneNumber)) {
			setException(new NullPointerException("error:no user Phone Number!!!"));
			return TaskResult.AUTH_ERROR;
		}

		try {
			String string = App.sServerClient.sPhoneFreeQuery(phoneNumber, CommUtil.getMonthPast(0), UserInfoControler.getInstance().getAuthorKey());
			publishProgress(string, true);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			publishProgress(e.getMessage(), false);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

	protected void onProgressUpdate(Object... values) {
		if (values != null && values.length > 0) {

			boolean isSuccess = (Boolean) values[1];
			if (isSuccess) {
				PhoneFreeQuery phoneFreeQuery = PhoneFreeQuery.craeteByAnalysisMessage((String) values[0]);
				UserInfoControler.getInstance().setPhoneFreeQuery(phoneFreeQuery);
			} else {
				UserInfoControler.getInstance().setPhoneFreeQuery(new PhoneFreeQuery(EmumState.CONNECT_ERROR));
			}

			super.onProgressUpdate((String) values[0]);
		}
	}

	protected void onPreExecute(){
		TaskManager.getInstance().addTask(this);
		super.onPreExecute();
	}
	
	protected void onPostExecute(TaskResult result) {
		TaskManager.getInstance().deleteObserver(this);
		super.onPostExecute(result);
	}
}
