package com.sunrise.marketingassistant.task;

import java.net.URI;

import org.json.JSONObject;

import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;
import com.sunrise.marketingassistant.net.http.HttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class GetCptListTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param  login_no
	 *            从帐号
	 * @param chlName
	 *           测试
	 * @param currentPage
	 * @param pageSize
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public final GetCptListTask execute(String auth,String login_no, String chlName, String currentPage, String pageSize, TaskListener listener) {
		TaskParams params = new TaskParams();
		params.put(KEY_ACCOUNT, login_no);
		params.put(KEY_CHL_NAME, chlName);
		params.put(KEY_PAGE, currentPage);
		params.put(KEY_PAGE_SIZE, pageSize);
		params.put(KEY_AUTH, auth);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String login_no = params[0].getString(KEY_ACCOUNT);
		String chlName = params[0].getString(KEY_CHL_NAME);
		String currentPage = params[0].getString(KEY_PAGE);
		String pageSize = params[0].getString(KEY_PAGE_SIZE);
		String auth = params[0].getString(KEY_AUTH);

		String result = null;
		try {
			result = ServerClient.getInstance().getCptList(auth,login_no, chlName, currentPage, pageSize);
			LogUtlis.e("竞争列表", "竞争列表结果：" + result);
			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;

	}
}
