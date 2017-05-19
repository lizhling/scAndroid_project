package com.sunrise.scmbhc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.sunrise.scmbhc.App.AppActionConstant;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.PhoneCurInfo;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.LoadUserBaseInfoTask;
import com.sunrise.scmbhc.task.PhoneCurrMsgTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;

public class AppWidgetService extends Service {

	private GenericTask mTask;
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		AppWidgetService getService() {
			return AppWidgetService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null)
			return super.onStartCommand(intent, flags, startId);

		if (mTask != null)
			mTask.cancle();

		stateLoadUserInfo = 0;

		startLoadMainTraffic(intent.getStringExtra(ExtraKeyConstant.KEY_PHONE_NUMBER));
		startLoadUserBaseInfo(intent.getStringExtra(ExtraKeyConstant.KEY_PHONE_NUMBER));
		return super.onStartCommand(intent, flags, startId);
	}

	private void startLoadMainTraffic(final String phoneNumber) {
		mTask = new PhoneCurrMsgTask().execute(phoneNumber, new TaskListener() {
			@Override
			public String getName() {
				return null;
			}

			@Override
			public void onPreExecute(GenericTask task) {
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				stateLoadUserInfo |= STATE_COMPLETE_PHONE_CURRENT_MSG;

				if (result == TaskResult.OK) {

					checkLoadUserInfoTaskComplete();

				} else {
					if (task.isBusinessAuthenticationTimeOut())
						UserInfoControler.getInstance().loginOut();
					else
						Toast.makeText(AppWidgetService.this, task.getException().toString(), Toast.LENGTH_LONG).show();

					sendBroadcast(false);

					stopSelf();
				}
			}

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				if (param != null)
					UserInfoControler.getInstance().setCurrentPhoneInfo((PhoneCurInfo) param);

			}

			@Override
			public void onCancelled(GenericTask task) {

			}
		});
	}

	/**
	 * 获取用户其它信息
	 * 
	 * @param phoneNumber
	 */
	private void startLoadUserBaseInfo(String phoneNumber) {
		new LoadUserBaseInfoTask().execute(phoneNumber, new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {

				String temp = (String) param;

				if (temp != null)
					UserInfoControler.getInstance().setUserBaseInfo(temp);
			}

			@Override
			public void onPreExecute(GenericTask task) {

			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {

				stateLoadUserInfo |= STATE_COMPLETE_GET_USER_BASE_INFO;

				if (result == TaskResult.OK) {
					checkLoadUserInfoTaskComplete();
				} else {

					if (task.isBusinessAuthenticationTimeOut())
						UserInfoControler.getInstance().loginOut();
					else
						Toast.makeText(AppWidgetService.this, task.getException().toString(), Toast.LENGTH_LONG).show();
					sendBroadcast(false);
					stopSelf();
				}

			}

			@Override
			public void onCancelled(GenericTask task) {

			}

			@Override
			public String getName() {
				return null;
			}
		});
	}

	private void sendBroadcast(boolean is) {
		Intent intent = new Intent(AppActionConstant.ACTION_REFRESH);
		intent.putExtra(ExtraKeyConstant.KEY_SUCCESS, is);
		sendBroadcast(intent);
	}

	private byte stateLoadUserInfo = 0;
	private final byte STATE_COMPLETE_PHONE_CURRENT_MSG = 2;
	private final byte STATE_COMPLETE_GET_USER_BASE_INFO = 16;
	private final byte STATE_COMPLETE_WHOLE = STATE_COMPLETE_PHONE_CURRENT_MSG | STATE_COMPLETE_GET_USER_BASE_INFO;

	private final void checkLoadUserInfoTaskComplete() {
		// mDialog.setMessage(getResources().getString(R.string.loadingUserInfo)
		// + String.format("(%d / %d)",
		// stateLoadUserInfo,STATE_COMPLETE_WHOLE));
		if (stateLoadUserInfo == STATE_COMPLETE_WHOLE) {
			sendBroadcast(true);
			stopSelf();
		}
	}
}
