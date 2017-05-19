package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;


/**
 * 积分兑换礼品接口
 * 
 * @author fuheng
 * 
 */
public class ExchangeCreditsTask extends GenericTask {

	public static ExchangeCreditsTask execute(String code, TaskListener taskListener) {
		ExchangeCreditsTask result = new ExchangeCreditsTask();
		result.setListener(taskListener);
		result.execute(new TaskParams("code", code));
		return result;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		String code = params[0].getString("code");

		String phoneNumber = UserInfoControler.getInstance().getUserName();

		try {
			String result = App.sServerClient.exchangeCredits(phoneNumber, code, UserInfoControler.getInstance().getAuthorKey());
			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			publishProgress(e.getMessage());
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
