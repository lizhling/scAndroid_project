package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import com.sunrise.scmbhc.App;

public class ChannelOperTask extends GenericTask {

	public static final byte METHOD_LOGIN = 1;
	public static final byte METHOD_LOGOUT = 2;
	public static final byte METHOD_UPDATE = 0;

	public static final String[] METHOD_NAMES = { "channelUpdateToken", "channelLogin", "channelLogout" };

	public ChannelOperTask excute(String phone_no, byte type, String token, TaskListener listener) {

		TaskParams params = new TaskParams("phoneNo", phone_no);
		params.put("token", token);

		if (type < 0 || type >= METHOD_NAMES.length)
			type = 0;

		params.put("oper", METHOD_NAMES[type]);
		setListener(listener);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String token = params[0].getString("token");
		String operMethod = params[0].getString("oper");
		String phone_no = params[0].getString("phoneNo");
		try {
			String result = App.sServerClient.channelUpdate(phone_no, operMethod, token);
			// {"RETURN":{"RETURN_CODE":"00","RETURN_MESSAGE":"登录获取token成功","RETURN_INFO":[{"asiaToken":"8117388f-4414-4116-a83f-87efa75d1bae"}]}}
			if (result.contains("asiaToken")) {
				String asiaInfoToken = new JSONObject(result).getJSONObject("RETURN").getJSONArray("RETURN_INFO").getJSONObject(0).getString("asiaToken");
				publishProgress(asiaInfoToken);
			}else{
				publishProgress("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
