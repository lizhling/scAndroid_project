package com.sunrise.micromarketing.task;

import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.entity.ResultBean;
import com.sunrise.micromarketing.net.ServerClient;
import com.sunrise.javascript.utils.JsonUtils;

/**
 * 验证授权码
 * 
 * @author 珩
 * 
 */
public class CheckValidateCodeTask extends GenericTask implements ExtraKeyConstant {

	public final CheckValidateCodeTask execute(String phone_no, String checkCode, String businessId, String authenticationID, TaskListener listener) {
		TaskParams params = new TaskParams("phone_no", phone_no);
		params.put("checkCode", checkCode);
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
		String checkCode = params[0].getString("checkCode");
		String result = null;
		try {
			result = ServerClient.getInstance().checkCustomerValidateCode(phone_no, checkCode, businessId, authenticationID);
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
