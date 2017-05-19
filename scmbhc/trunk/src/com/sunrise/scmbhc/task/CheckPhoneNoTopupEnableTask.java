package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;

/**
 * 检测号码充值许可
 * 
 * @author fuheng
 * 
 */
public class CheckPhoneNoTopupEnableTask extends GenericTask implements ExtraKeyConstant {

	/*
	 * @param listener 回调对象
	 * 
	 * @return 异步对象
	 */
	public final CheckPhoneNoTopupEnableTask execute(String phoneNum, String user, String price, String token, TaskListener listener) {
		setListener(listener);
		TaskParams params = new TaskParams(KEY_PHONE_NUMBER, phoneNum);
		params.put("user", user);
		params.put("token", token);
		params.put("price", price);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String phoneNumber = params[0].getString(KEY_PHONE_NUMBER);
		String token = params[0].getString("token");
		String user = params[0].getString("user");
		String price = params[0].getString("price");
		
		//将元转化为分
		price = String.valueOf(Math.round(Float.parseFloat(price)*100));
		
		
		try {
			String result = App.sServerClient.checkPhoneNumberTopupEnable(phoneNumber, user,price, token);

			if (!result.contains("DETAIL_MESSAGE"))
				publishProgress("");
			else {
				// {"RETURN":{"RETURN_CODE":"00","RETURN_MESSAGE":"验证通过","RETURN_INFO":[{"DETAIL_MESSAGE":"http://www.sc.10086.cn/bankpaymobile.do?dispatch=index_app&type=H4sIAAAAAAAAAAEoAdf+JJmL62kwF/7Fo8k3v/tenJk9bLn7feOx2TvX/G5HI14jvxQVbGvpUAFizYt9sEqFSbIxoDZRhReeZnsuFGTvRDyX4/gUgoq4YDAGDPDP3uevQDyUGjS6vop9hOdJZ/m8aXtSbR0AlnljoTAe+YGMlvv/EUwes1EK0Qy9xz0hhSiVckynDSs1WFeSKB3Wd8yGDGNT+1epzP5EFh6Ch7XDkDcZXKEWVwdLZtbY3xThIf0XfQSl/DLCHvPV1JYSHTdTd2pidsy6erXHmMs2h0HqOqxFudgq1NCXAJPg3DNC4eVyD1T8COA0wufAEzV72WGNr0YPTX+w6KhUIE74lTwXVr6TBTR7xjk3RDiqwWChM0PH2J6cIressD5AXrcIh6xAKQG2/ym7iwAVANLtKAEAAA=="}]}}
				String asiaInfoUrl = new JSONObject(result).getJSONObject("RETURN").getJSONArray("RETURN_INFO").getJSONObject(0).getString("DETAIL_MESSAGE");
				publishProgress(asiaInfoUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
