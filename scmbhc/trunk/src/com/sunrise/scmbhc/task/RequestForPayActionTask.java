package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import android.text.TextUtils;

import com.starcpt.analytics.common.DesCrypUtil;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.utils.LogUtlis;

/**
 * 支付请求
 * 
 * @author fuheng
 * 
 */
public class RequestForPayActionTask extends GenericTask implements ExtraKeyConstant {
	private static final String KEY_PRICE = "price";
	private static final String KEY_AMOUNT = "amount";
	private static final String KEY_BANKTYPE = "bank_type";
	private static final String KEY_DEVICE_ID = "deviceId";
	private static final String KEY_SIM_ID = "UDNBDAS";
	private static final String KEY_UD_ID = "FHDSKJ";
	private static final String KEY_IP = "IP";
	private static final String KEY_VERSION = "clientversion";
	private static final String KEY_USER = "user";

	/**
	 * @param phoneNumber
	 * @param price
	 *            价格，单位元
	 * @param f
	 * @param bankType
	 * @param taskListener
	 * @return
	 */
	public static RequestForPayActionTask execute(String user, String phoneNumber, int amount, float price, String bankType, String imei, String imsi,
			String ip, TaskListener taskListener) {
		RequestForPayActionTask result = new RequestForPayActionTask();
		TaskParams params = new TaskParams(KEY_PHONE_NUMBER, phoneNumber);
		params.put(KEY_PRICE, String.valueOf((int) (price * 100)));
		params.put(KEY_BANKTYPE, bankType);
		params.put(KEY_AMOUNT, amount * 100);
		params.put(KEY_DEVICE_ID, imei);
		params.put(KEY_SIM_ID, imsi);
		params.put(KEY_IP, ip);
		params.put(KEY_USER, user);
		result.setListener(taskListener);
		result.execute(params);
		return result;
	}

	private RequestForPayActionTask() {
		super();
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		TaskParams param = params[0];

		String token = App.sSettingsPreferences.getNonameToken();
		String srctoken = token;
		LogUtlis.showLogD("token", "token = " + token);
		if (!TextUtils.isEmpty(token) && token.length() > 0xf) {
			token = dealToken2Key(token);
		} else {
			setException(new BusinessException("请重新打开应用再充值……"));
			return TaskResult.FAILED;
		}
		LogUtlis.showLogD("token", "key = " + token);

		String phoneNumber = param.getString(KEY_PHONE_NUMBER);
		String price = param.getString(KEY_PRICE);
		String amount = param.getString(KEY_AMOUNT);
		String bankType = param.getString(KEY_BANKTYPE);
		String imei = param.getString(KEY_DEVICE_ID);
		String imsi = param.getString(KEY_SIM_ID);
		String IP = param.getString(KEY_IP);
		String user = param.getString(KEY_USER);

		StringBuilder otherInfo = new StringBuilder();
		otherInfo.append("name=&billing=&quantity=");
		otherInfo.append('&').append(KEY_USER).append('=').append(user);

		otherInfo.append('&').append("versionName").append('=').append(App.sAPKVersionName);
		otherInfo.append('&').append("versionCode").append('=').append(App.sAPKVersionCode);
		otherInfo.append('&').append("timestep").append('=').append(System.currentTimeMillis());
		if (imsi != null){
			LogUtlis.showLogI("imsi", "imsi:"+imsi);
			String UDIA = imsi.substring(3, 9);
			StringBuilder sb = new StringBuilder(UDIA);
		    sb.reverse();
		    UDIA = sb.toString() + imsi.substring(imsi.length() - 15);
			otherInfo.append('&').append(KEY_SIM_ID).append('=').append(UDIA);
			sb = new StringBuilder(imsi.substring(9, 15));
			sb.reverse();
			sb.insert(3, imsi.substring(imsi.length() - 15));
			String UDIB = sb.toString();
			otherInfo.append('&').append(KEY_UD_ID).append('=').append(UDIB);
			
		}
		if (IP != null) {
			otherInfo.append('&').append(KEY_IP).append('=').append(IP);
		}
		
		if (UserInfoControler.getInstance().checkUserLoginIn()) {
			otherInfo.append('&').append("token").append('=').append(UserInfoControler.getInstance().getAuthorKey());
		} else {
			otherInfo.append('&').append("token").append('=').append(srctoken);
		}

		otherInfo.append('&').append("timestep").append('=').append(System.currentTimeMillis());

		if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(phoneNumber)) {// 增加自定义验证信息(根据时间后四位、imei前四位、手机号码后四位、充值金额来生成验证信息)
			int amountN = Integer.parseInt(amount);
			int phoneN4 = Integer.parseInt(phoneNumber.substring(phoneNumber.length() - 4));
			long time = System.currentTimeMillis();
			long d = time % 10000 ;
			d ^= amountN;
			d ^= phoneN4;
			otherInfo.append('&').append("x").append('=').append(Long.toOctalString(d));
		}

		try {
			// 从so中获取token
			String temp = DesCrypUtil.GetDynamicKey(token);
			if (temp != null && temp.length() == 8) {
				token = temp;
			}
			String result = App.sServerClient.requestForPayAction(otherInfo.toString(), bankType, phoneNumber, amount, price, token, imei);
			publishProgress(new JSONObject(result).getJSONObject("data").getString("redirecturl"));
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

	private String dealToken2Key(String token) {
		int start = token.charAt(0) - 90;
		final int length = 8;
		start = Math.min(start, token.length() - length);
		start = Math.max(0, start);
		return token.substring(start, start + length);
	}
}
