package com.sunrise.marketingassistant.task;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.entity.SubAccountBeen;
import com.sunrise.marketingassistant.exception.logic.BusinessException;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;
import com.sunrise.javascript.utils.LogUtlis;

public class GetSubAccountTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param phoneNumber
	 *            登录名
	 * @param imsi
	 *            sim卡号
	 * @param imei
	 *            设备号
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public final GetSubAccountTask execute(String account, TaskListener listener) {

		TaskParams params = new TaskParams(KEY_ACCOUNT, account);
		setListener(listener);

		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String account = params[0].getString(KEY_ACCOUNT);

		try {
			String result = ServerClient.getInstance().getSubAccount(account, "YXZS");
			LogUtlis.e("获取从账号", "获取从账号结果：" + result);
			SubAccountBeen subAccountBeen = JsonUtils.parseJsonStrToObject(result, SubAccountBeen.class);
			if (subAccountBeen.getSubAccount() == null)
				throw new BusinessException("此4A帐号不含有巡店宝从账号……");
			publishProgress(subAccountBeen.getSubAccount());
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
