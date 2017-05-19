package com.sunrise.econan.task;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.sunrise.econan.ExtraKeyConstant;
import com.sunrise.econan.net.ServerClient;
import com.sunrise.javascript.utils.FileUtils;

public class CheckLoginTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param phoneNumber
	 *            登录名
	 * @param password
	 *            登录密码
	 * @param imsi
	 *            sim卡号
	 * @param imei
	 *            设备号
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public final CheckLoginTask execute(String account, String password, String imsi, String imei, TaskListener listener) {
		TaskParams params = new TaskParams(KEY_ACCOUNT, account);
		params.put(KEY_PASSWORD, password);
		params.put(KEY_IMEI, imei);
		params.put(KEY_IMSI, imsi);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String account = params[0].getString(KEY_ACCOUNT);
		String password = params[0].getString(KEY_PASSWORD);
		String imei = params[0].getString(KEY_IMEI);
		String imsi = params[0].getString(KEY_IMSI);

		String result = null;
		try {
			result = ServerClient.getInstance().checklogin(account, password, imei, imsi);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		
		try {// 此处检测是会有图片验证码返回。如果有，回复到界面
			JSONObject jobj = new JSONObject(result);
			String url = jobj.getString("dynamicType");

			HttpURLConnection httpURLconnection = (HttpURLConnection) new URL(url).openConnection();
			httpURLconnection.setRequestMethod("GET");
			httpURLconnection.setReadTimeout(6 * 1000);
			byte[] imageBytes = null;
			if (httpURLconnection.getResponseCode() == 200) {
				InputStream in = null;
				in = httpURLconnection.getInputStream();
				imageBytes = FileUtils.readFiletoByte(in);
				in.close();
			}

			Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
			BitmapDrawable drawable = new BitmapDrawable(bitmap);
			publishProgress(drawable);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return TaskResult.OK;
	}
}
