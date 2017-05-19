package com.sunrise.marketingassistant.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;

/**
 * 签到
 * 
 * @author 珩
 * 
 */
public class DoMobRegisteTask extends GenericTask implements ExtraKeyConstant {

	public final DoMobRegisteTask execute(String groupId, String loginNo, String phoneNo, double latiTude, double longiTude, String imsi, TaskListener listener) {

		TaskParams params = new TaskParams("loginNo", loginNo);
		params.put("groupId", groupId);
		params.put("phoneNo", String.valueOf(phoneNo));
		params.put("latiTude", String.valueOf(latiTude));
		params.put("longiTude", String.valueOf(longiTude));
		params.put("imsi", imsi);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String loginNo = params[0].getString("loginNo");
		String groupId = params[0].getString("groupId");
		String phoneNo = params[0].getString("phoneNo");
		String latiTude = params[0].getString("latiTude");
		String longiTude = params[0].getString("longiTude");
		String regisTime = new SimpleDateFormat("yyyy-M-d hh:mm:ss").format(new Date(System.currentTimeMillis()));
		String imsi = params[0].getString("imsi");

		try {

			String phone = ServerClient.getInstance().getPhoneNumByIMSI(imsi, phoneNo);
			if (!App.isTest && !phone.equals(phoneNo))
				throw new Exception("imsi对应的手机号(" + phone + ")与登录帐号所对应的手机号(" + phoneNo + ")不同，不允许签到……\n请使用注册号码手机，或者双卡手机用户请切换卡槽……");

			String result = ServerClient.getInstance().doMobRegiste(regisTime, phoneNo, groupId, latiTude, longiTude, loginNo);

			// {"RETCODE":"0","RETMSG":"签到成功","resultStr":[true],"serialNumber":"201512202015742044"}
			JSONObject jobj = new JSONObject(result);
			publishProgress(jobj.getString("RETMSG"));
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
