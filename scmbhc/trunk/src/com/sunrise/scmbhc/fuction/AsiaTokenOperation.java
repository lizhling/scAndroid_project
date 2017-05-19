package com.sunrise.scmbhc.fuction;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import com.starcpt.analytics.common.MD5Utility;
import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.task.ChannelOperTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.utils.LogUtlis;

public class AsiaTokenOperation {

	public static final String TAG = "AsiaInfoOperation";
	private Activity activity;
	private JavascriptHandler mHandler;

	public AsiaTokenOperation(Activity activity, JavascriptHandler handler) {
		this.activity = activity;
		mHandler = handler;
	}

	public String getToken() {

		JSONObject json = new JSONObject();
		try {
			json.put("digest", MD5Utility.md5Appkey(App.sSettingsPreferences.getUserName() + "yyzcpt_123"));
			json.put("appid", "2013091400029967");
			json.put("token", App.sSettingsPreferences.getAsiaInfoToken());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	LogUtlis.showLogE("asiaTokenOperation","asiaTokenOperation getToken "+json.toString());
		return json.toString();
	}

	public void updateToken() {
		// Intent intent = new Intent(activity, BackgroundService.class);
		// intent.putExtra(ExtraKeyConstant.KEY_CASE,
		// ChannelOperTask.METHOD_NAMES[ChannelOperTask.METHOD_UPDATE]);
		// Bundle bundle = new Bundle();
		// bundle.putString(ExtraKeyConstant.KEY_PHONE_NUMBER,
		// UserInfoControler.getInstance().getUserName());
		// bundle.putString(ExtraKeyConstant.KEY_TOKEN,
		// App.sSettingsPreferences.getAsiaInfoToken());
		// intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		// activity.startService(intent);
		LogUtlis.showLogE("asiaTokenOperation","asiaTokenOperation updateToken ");
		new ChannelOperTask().excute(UserInfoControler.getInstance().getUserName(), ChannelOperTask.METHOD_UPDATE, App.sSettingsPreferences.getAsiaInfoToken(),
				new TaskListener() {

					@Override
					public void onProgressUpdate(GenericTask task, Object param) {
						App.sSettingsPreferences.setAsiaInfoToken((String) param);
					}

					@Override
					public void onPreExecute(GenericTask task) {

					}

					@Override
					public void onPostExecute(GenericTask task, TaskResult result) {
						callback("updateToken", String.valueOf(result == TaskResult.OK ? true : false));
					}

					@Override
					public void onCancelled(GenericTask task) {
						callback("updateToken", "cancelled");
					}

					@Override
					public String getName() {
						return null;
					}
				});

	}

	public void relogin() {
		LogUtlis.showLogE("asiaTokenOperation","asiaTokenOperation relogin ");
		new ChannelOperTask().excute(UserInfoControler.getInstance().getUserName(), ChannelOperTask.METHOD_LOGIN, null, new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				App.sSettingsPreferences.setAsiaInfoToken((String) param);
			}

			@Override
			public void onPreExecute(GenericTask task) {

			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				callback("relogin", String.valueOf(result == TaskResult.OK ? true : false));
			}

			@Override
			public void onCancelled(GenericTask task) {
				callback("relogin", "cancelled");
			}

			@Override
			public String getName() {
				return null;
			}
		});

	}

	private void callback(String key, String result) {
		ArrayList<Object> objs = new ArrayList<Object>();
		objs.add(JavaScriptConfig.JAVASCRIPT_API_CALLBACK_NAME);

		if (TextUtils.isEmpty(key)) {
			objs.add(new String[] { result, "key" });
		} else {
			objs.add(new String[] { result, key });
		}
		System.out.println("callBack：" + objs.toString());
		mHandler.obtainMessage(0, objs).sendToTarget();
	}
}
