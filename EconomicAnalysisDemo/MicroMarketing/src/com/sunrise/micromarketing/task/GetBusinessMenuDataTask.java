package com.sunrise.micromarketing.task;

import org.json.JSONObject;

import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.entity.ResultBean;
import com.sunrise.micromarketing.net.ServerClient;
import com.sunrise.micromarketing.utils.ZipAndBaseUtils;
import com.sunrise.javascript.utils.JsonUtils;

public class GetBusinessMenuDataTask extends GenericTask implements ExtraKeyConstant {

	public final GetBusinessMenuDataTask execute(String businessId, String subAccount, TaskListener listener) {
		TaskParams params = new TaskParams("businessId", businessId);
		params.put("subAccount", subAccount);
		setListener(listener);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String businessId = params[0].getString("businessId");
		String subAccount = params[0].getString("subAccount");

		String result = null;
		try {
			 result = ServerClient.getInstance().getBusinessMenuData(businessId, subAccount);
			//result =ZipAndBaseUtils.decodeBase64("eyJhY2Nlc3NVcmwiOiJodHRwOi8vd3d3LmhhbzEyMy5jb20/YnVzaW5lc3NJZD0xODAmYm9zc05v PWFhYVg3NCIsIm5hbWUiOiLAtLXnzOHQ0SIsInJlc3VsdENvZGUiOjAsInJlc3VsdE1lc3NhZ2Ui OiIifQ==");
			publishProgress(new JSONObject(result).getString("accessUrl"));
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}
}
