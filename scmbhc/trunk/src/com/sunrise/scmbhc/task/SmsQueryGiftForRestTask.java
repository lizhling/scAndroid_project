package com.sunrise.scmbhc.task;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.ECoupon;

/**
 * 获取积分礼品接口
 * 
 * @author fuheng
 * 
 */
public class SmsQueryGiftForRestTask extends GenericTask {

	public static SmsQueryGiftForRestTask execute(String code, TaskListener taskListener) {
		SmsQueryGiftForRestTask result = new SmsQueryGiftForRestTask();
		result.setListener(taskListener);
		result.execute(new TaskParams("code", code));
		return result;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		String phoneNumber = UserInfoControler.getInstance().getUserName();
		String code = params[0].getString("code");
		try {
			String result = App.sServerClient.SmsQueryGiftForRest(phoneNumber, code, UserInfoControler.getInstance().getAuthorKey());
			JSONObject json = new JSONObject(result);
			JSONArray jarray = json.getJSONObject("RETURN").getJSONArray("RETURN_INFO");
			final int length = jarray.length();
			if (length > 0) {
				ECoupon eCoupon = JsonUtils.parseJsonStrToObject(jarray.getString(0), ECoupon.class);
				publishProgress(eCoupon);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
