package com.sunrise.scmbhc.service;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.task.ChannelOperTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

public class BackgroundService extends Service implements ExtraKeyConstant, TaskListener {

	private final IBinder mBinder = new LocalBinder();
	private Handler handler = new Handler();

	private GenericTask mTask;

	public class LocalBinder extends Binder {
		BackgroundService getService() {
			return BackgroundService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public void onCreate() {
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent == null)
			return super.onStartCommand(intent, flags, startId);

		String event_case = intent.getStringExtra(KEY_CASE);
		if (event_case != null && ChannelOperTask.METHOD_NAMES[ChannelOperTask.METHOD_LOGOUT].equals(event_case)) {
			doLogout(intent.getBundleExtra(KEY_BUNDLE));
		} else if (event_case != null && ChannelOperTask.METHOD_NAMES[ChannelOperTask.METHOD_UPDATE].equals(event_case)) {
			doUpdateToken(intent.getBundleExtra(KEY_BUNDLE));
		} else if (event_case != null && ChannelOperTask.METHOD_NAMES[ChannelOperTask.METHOD_LOGIN].equals(event_case)) {
			doLogin(intent.getBundleExtra(KEY_BUNDLE));
		} else
			handler.postAtTime(new Runnable() {

				@Override
				public void run() {
					stopSelf();
				}
			}, 2000);

		return super.onStartCommand(intent, flags, startId);
	}

	private void doLogin(Bundle bundle) {
		if (mTask != null && !mTask.isCancelled())
			mTask.cancle();
		mTask = new ChannelOperTask().excute(bundle.getString(KEY_PHONE_NUMBER), ChannelOperTask.METHOD_LOGIN, bundle.getString(KEY_TOKEN), this);
	}

	private void doUpdateToken(Bundle bundle) {
		if (mTask != null && !mTask.isCancelled())
			mTask.cancle();
		mTask = new ChannelOperTask().excute(bundle.getString(KEY_PHONE_NUMBER), ChannelOperTask.METHOD_UPDATE, bundle.getString(KEY_TOKEN), this);
	}

	private void doLogout(Bundle bundle) {
		if (mTask != null && !mTask.isCancelled())
			mTask.cancle();
		mTask = new ChannelOperTask().excute(bundle.getString(KEY_PHONE_NUMBER), ChannelOperTask.METHOD_LOGOUT, bundle.getString(KEY_TOKEN), this);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {

	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		if (task instanceof ChannelOperTask)// asiaInfo logout action
			stopSelf();
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param != null) {
			String str = (String) param;
			if (TextUtils.isEmpty(str))
				App.sSettingsPreferences.setAsiaInfoToken(null);
			else
				App.sSettingsPreferences.setAsiaInfoToken(str);
		}
	}

	@Override
	public void onCancelled(GenericTask task) {

	}

}
