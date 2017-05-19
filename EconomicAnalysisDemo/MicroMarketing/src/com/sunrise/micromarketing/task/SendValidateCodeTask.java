package com.sunrise.micromarketing.task;

import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.entity.ResultBean;
import com.sunrise.micromarketing.net.ServerClient;
import com.sunrise.javascript.utils.JsonUtils;

public class SendValidateCodeTask extends GenericTask implements ExtraKeyConstant {

	public final SendValidateCodeTask execute(String phone_no, String businessId, String authenticationID, TaskListener listener) {
		TaskParams params = new TaskParams("phone_no", phone_no);
		params.put("businessId", businessId);
		params.put("authenticationID", authenticationID);
		setListener(listener);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String phone_no = params[0].getString("phone_no");
		String businessId = params[0].getString("businessId");
		String authenticationID = params[0].getString("authenticationID");

		String result = null;
		try {
			result = ServerClient.getInstance().sendCustomerValidateCode(phone_no, businessId, authenticationID);
			ResultBean been = JsonUtils.parseJsonStrToObject(result, ResultBean.class);
			publishProgress(been.getResultMesage());
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}
}
