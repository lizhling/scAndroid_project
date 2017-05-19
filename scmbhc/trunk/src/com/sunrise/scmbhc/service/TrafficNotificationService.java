package com.sunrise.scmbhc.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.UseCondition;
import com.sunrise.scmbhc.ui.activity.TrafficNotificationActivity;
import com.sunrise.scmbhc.ui.fragment.TrafficNotificationFragment;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

@SuppressLint("HandlerLeak")
public class TrafficNotificationService extends Service {
	private final static int NOTIFICATION_ID = 10012;
	private final static long ONE_HOUR_PERIOD = 1000 * 60 * 60;
	private final static long ONE_DAY_PERIOD = ONE_HOUR_PERIOD * 24;
	private final static long ONE_WEEK_PERIOD = ONE_DAY_PERIOD * 7;
	private final static int TRAFFIC_OVER = 5120;
	private AlarmManager mAlarmManager;
	private UserInfoControler mUserInfoControler;
	private int mTrafficThreshold;// 流量剩余百分比提醒
	private double mRemindTraffic;
	private long mRefreshTrafficPeriod = ONE_HOUR_PERIOD;

	public static final String ACTION_TRAFFIC_NOTIFICATION_SERVICE = "com.sunrise.scmbhc.service.traffic_notification";// 流量提醒广播
	private PendingIntent mPanddingIntent;
	private TrafficNotificationReceiver mTNReceiver;// 广播接收器对象

	private void showNotification(boolean isOverTraffic) {
		int notificationMode = App.sSettingsPreferences.getTrafficNotifictionMode(TrafficNotificationFragment.NOTIFICATION_MODE);
		String tickerText = String.format(getString(R.string.traffic_low_threadshold), mTrafficThreshold + "%");
		String remindTrafficStr = String.format(getString(R.string.remiand_traffic), UseCondition.getFlowString(mRemindTraffic));
		if (isOverTraffic) {
			tickerText = getString(R.string.traffic_over);
		}
		switch (notificationMode) {
		case TrafficNotificationFragment.NOTIFICATION_MODE:
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification n = new Notification(R.drawable.icon_launcher, tickerText, System.currentTimeMillis());
			n.flags |= Notification.FLAG_AUTO_CANCEL;
			PendingIntent pendingintent = PendingIntent.getActivity(TrafficNotificationService.this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
			n.setLatestEventInfo(TrafficNotificationService.this, tickerText, remindTrafficStr, pendingintent);
			nm.notify(NOTIFICATION_ID, n);
			break;
		case TrafficNotificationFragment.NOTIFICATION_DIALOG_MODE:
			if (!TrafficNotificationActivity.isShowing) {
				Intent intent = new Intent(TrafficNotificationService.this, TrafficNotificationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(App.ExtraKeyConstant.KEY_IS_TRAFFIC_OVER, isOverTraffic);
				startActivity(intent);
			}
			break;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		mUserInfoControler = UserInfoControler.getInstance();
		
		if (mAlarmManager == null)
			mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// 设置广播接收
		if (mTNReceiver == null) {
			mTNReceiver = new TrafficNotificationReceiver();
			registerReceiver(mTNReceiver, new IntentFilter(ACTION_TRAFFIC_NOTIFICATION_SERVICE));
		}

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent arg0, int flags, int startId) {
		if (mAlarmManager != null && mPanddingIntent != null) {
			mAlarmManager.cancel(mPanddingIntent);
		}

		Intent intent = new Intent(ACTION_TRAFFIC_NOTIFICATION_SERVICE);
		mPanddingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mRefreshTrafficPeriod, mPanddingIntent);

		return START_STICKY_COMPATIBILITY;
	}

	@Override
	public void onDestroy() {

		// 注销广播接收器
		if (mTNReceiver != null)
			unregisterReceiver(mTNReceiver);

		// 取消循环广播
		if (mAlarmManager != null && mPanddingIntent != null) {
			mAlarmManager.cancel(mPanddingIntent);
		}

		super.onDestroy();
	}

	private void alertManager() {
		if (mUserInfoControler.checkUserLoginIn()) {

			//如果无流量，或者流量未能获取，取消提醒管理。
			UseCondition flowCondition = mUserInfoControler.getConditionFlow(null);
			if (flowCondition.getTotle() <= 0)
				return;
			
			mRemindTraffic = flowCondition.getSurplus();

			mTrafficThreshold = App.sSettingsPreferences.getTrafficThreshold(TrafficNotificationFragment.DEFAULT_TRAFFIC_THRESHOLD);

			if (mRemindTraffic < TRAFFIC_OVER) {
				int trafficOverHandleMode = App.sSettingsPreferences.getTrafficOverHandleMode(TrafficNotificationFragment.TRAFFIC_OVER_HANDLE_MODE_0);
				switch (trafficOverHandleMode) {
				case TrafficNotificationFragment.TRAFFIC_OVER_HANDLE_MODE_0:
					LogUtlis.showLogE("=======", "============= close network data");
					CommUtil.setMobileDataStatus(TrafficNotificationService.this, false);
					break;
				case TrafficNotificationFragment.TRAFFIC_OVER_HANDLE_MODE_2:
					Toast.makeText(TrafficNotificationService.this, R.string.jumpToAutoHandleTrafficBusiness, Toast.LENGTH_SHORT).show();
					break;
				case TrafficNotificationFragment.TRAFFIC_OVER_HANDLE_MODE_1:
					showNotification(true);
					break;
				}
				return;
			}

			if (flowCondition.getSurplusRate() < mTrafficThreshold / 100f) {
				// 如果用户剩余量小于百分比
				showNotification(false);
			}
		}
	}

	/**
	 * 流量提醒广播接收器
	 * 
	 * @author fuheng
	 * 
	 */
	class TrafficNotificationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_TRAFFIC_NOTIFICATION_SERVICE)) {
				alertManager();
			}
		}
	}
}
