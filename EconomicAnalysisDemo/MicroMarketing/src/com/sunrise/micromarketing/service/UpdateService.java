package com.sunrise.micromarketing.service;

import java.io.File;
import java.io.IOException;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.micromarketing.App;
import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.cache.download.DownLoadThread;
import com.sunrise.micromarketing.entity.UpdateInfo;
import com.sunrise.micromarketing.utils.CommUtil;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateService extends Service {

	private final IBinder mBinder = new LocalBinder();

	private int sNotificationId = -1;
	public static boolean isDownApk = false;
	private NotificationManager sUpdateNotificationManager;
	private String sApkFilePath;
	private Notification sUpdateNotification;

	private UpdateInfo updateInfo;// 下载地址

	public class LocalBinder extends Binder {
		UpdateService getService() {
			return UpdateService.this;
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

		updateInfo = intent.getParcelableExtra(ExtraKeyConstant.KEY_BUNDLE);

		try {
			downloadApk();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void downloadApk() throws IOException {

		if (isDownApk || updateInfo == null || updateInfo.getDownloadUrl() == null)
			return;

		isDownApk = true;
		//sApkFilePath = ExtraKeyConstant.APP_FILE_APK_DIR + File.separator + ExtraKeyConstant.APP_FILE_APK_NAME;
		sApkFilePath = FileUtils.getAbsPath(ExtraKeyConstant.APP_FILE_APK_DIR, ExtraKeyConstant.APP_FILE_APK_NAME);  //检查apk文件路径是否正确

		String downLoadUrl = updateInfo.getDownloadUrl();
		if (downLoadUrl != null)
			App.sDownFileManager.downFile(this, updateInfo.getDownloadUrl(), ExtraKeyConstant.APP_FILE_APK_DIR, ExtraKeyConstant.APP_FILE_APK_NAME,
					dowloadApkHandler, true, false);
	}

	private Handler dowloadApkHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DownLoadThread.START_DOWN_FILE:
				initNotifaction(UpdateService.this);
				break;
			case DownLoadThread.DWONLOAD_FLIE_COMPLETE:
				isDownApk = false;
				installApk(UpdateService.this);
				stopSelf();
				break;
			case DownLoadThread.DWONLOADING_FILE:
				showNotifactionProgress(UpdateService.this, msg);
				break;
			case DownLoadThread.DWONLOAD_FLIE_ERROR:
				isDownApk = false;
				if (updateInfo.getUpdateType() == UpdateInfo.TYPE_FORCE_UPDATE) {
					sUpdateNotificationManager.cancel(sNotificationId);
					sNotificationId = -1;
					Toast.makeText(UpdateService.this, "apk下载失败", Toast.LENGTH_LONG).show();
//					CommUtil.exit(UpdateService.this);
					notifactionShowError(UpdateService.this);
					System.gc();
					System.exit(0);
				} else {

				}
				break;
			default:
				break;
			}
		}
	};

	private void initNotifaction(Context context) {
		sNotificationId = DownLoadThread.START_DOWN_FILE;
		// 初始化通知管理器
		sUpdateNotificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		sUpdateNotification = new Notification();
		sUpdateNotification.icon = R.drawable.ic_launcher;

		// 通知自定义视图
		sUpdateNotification.contentView = new RemoteViews(context.getPackageName(), R.layout.down_apk_notification_view);
		sUpdateNotification.contentView.setProgressBar(R.id.content_view_progress, 100, 0, false);
	}

	private void installApk(final Context context) {
		startInstallApkActivity(context);
		CommUtil.exit(context);
		sUpdateNotificationManager.cancel(sNotificationId);
		sNotificationId = -1;
	}

	private void startInstallApkActivity(final Context context) {
		Uri uri = Uri.fromFile(new File(sApkFilePath));
		System.err.println(uri);
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
		installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(installIntent);
	}

	private void notifactionShowError(Context context) {
		sUpdateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent reDownloadApkIntent = new Intent(context, UpdateService.class);
		reDownloadApkIntent.putExtra(ExtraKeyConstant.KEY_BUNDLE, updateInfo);
		PendingIntent pendingintent = PendingIntent.getBroadcast(context, 0, reDownloadApkIntent, 0);
		sUpdateNotification.contentView.setTextViewText(R.id.down_apk_status, context.getString(R.string.apk_download_fail));
		sUpdateNotification.setLatestEventInfo(context, context.getString(R.string.apk_download_fail), context.getString(R.string.apk_download_fail),
				pendingintent); // add
		sUpdateNotificationManager.notify(sNotificationId, sUpdateNotification);
	}

	private void showNotifactionProgress(Context context, android.os.Message msg) {
		sUpdateNotification.contentView.setProgressBar(R.id.content_view_progress, 100, msg.arg1, false);
		String info = context.getString(R.string.latest_apk_downloading);
		sUpdateNotification.contentView.setTextViewText(R.id.down_apk_status, info + msg.arg1 + "%");
		PendingIntent pendingintent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		sUpdateNotification.contentIntent = pendingintent;
		sUpdateNotificationManager.notify(sNotificationId, sUpdateNotification);
	};
}
