package com.sunrise.scmbhc.task;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;

public class GetNoNameTokenTask extends GenericTask implements App.ExtraKeyConstant {

	public static GetNoNameTokenTask excuet(String imei, String imsi, String mac, String netType, TaskListener listener) {

		GetNoNameTokenTask task = new GetNoNameTokenTask();
		if (listener != null)
			task.setListener(listener);
		
		TaskParams params = new TaskParams(KEY_IMEI, imei);
		params.put(KEY_IMSI, imsi);
		params.put("ip", mac);
		params.put("net_type", netType);
		task.execute(params);

		return task;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params.length < 1)
			return TaskResult.AUTH_ERROR;
		String imei = params[0].getString(KEY_IMEI);
		String imsi = params[0].getString(KEY_IMSI);
		String mac = params[0].getString("ip");
		String netType = params[0].getString("net_type");

		try {
			String result = App.sServerClient.getNoNameToken(imei, imsi, mac, netType);
			publishProgress(result);
		} catch (HttpException e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		} catch (ServerInterfaceException e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		} catch (BusinessException e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

	protected void onProgressUpdate(Object... values) {
		String param = null;
		try {
			param = new JSONObject((String) values[0]).getString("token");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		App.sSettingsPreferences.setNonameToken(param);
		super.onProgressUpdate(param);

	}

}
