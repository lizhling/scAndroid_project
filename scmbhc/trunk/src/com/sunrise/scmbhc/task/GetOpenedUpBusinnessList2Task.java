package com.sunrise.scmbhc.task;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;

/**
 * 获得已开通业务列表
 * 
 * @author fuheng
 * 
 */
public class GetOpenedUpBusinnessList2Task extends GenericTask {

	public GetOpenedUpBusinnessList2Task execute(TaskListener taskListener) {
		setListener(taskListener);
		execute();
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String phoneNumber = UserInfoControler.getInstance().getUserName();

		if (TextUtils.isEmpty(phoneNumber)) {
			setException(new NullPointerException("您尚未登录，请先登录。"));
			return TaskResult.NOT_FOLLOWED_ERROR;
		}

		try {// 获取分类的其他业务信息。
			String result = App.sServerClient.getMyBusinessInfo(phoneNumber, UserInfoControler.getInstance().getAuthorKey());

			JSONObject json = new JSONObject(result).getJSONObject("RETURN");
			boolean isArray = json.getInt("IS_ARRAY") == 1;
			JSONArray array = json.getJSONArray("RETURN_INFO");
			if (isArray)
				array = array.getJSONObject(0).getJSONArray("DETAIL_INFO");

			publishProgress(array.toString());
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}
