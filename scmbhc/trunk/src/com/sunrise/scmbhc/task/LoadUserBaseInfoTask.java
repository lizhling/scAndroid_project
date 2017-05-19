package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;

/**
 * 获取用户归属地、名字的接口
 * 
 * @author fuheng
 * 
 */
public class LoadUserBaseInfoTask extends GenericTask implements ExtraKeyConstant {

	public LoadUserBaseInfoTask execute(String phoneNumber, TaskListener taskListener) {
		setListener(taskListener);
		execute(new TaskParams(KEY_PHONE_NUMBER, phoneNumber));
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		String phoneNumber = params[0].getString(KEY_PHONE_NUMBER);
		try {
			String result = App.sServerClient.getUserInfos(phoneNumber, UserInfoControler.getInstance().getAuthorKey());
			result = new JSONObject(result).getJSONObject("RETURN").getString("RETURN_INFO");
			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

	protected void onProgressUpdate(Object... values) {
		if (values != null && values.length > 0) {
			UserInfoControler.getInstance().setUserBaseInfo((String) values[0]);
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
