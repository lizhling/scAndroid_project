package com.starcpt.cmuc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.utils.FileUtils;

public class DownApkService extends Service {
	public final static int UPDATE_APK_NOTIFICATION_ID=1001;
	public final static int ADD_SUPPORT_DEVICE_APK_NOTIFICATION_ID=1002;
	public final static int DOWNLOADING = 1;
    public final static int DOWNLOAD_FAIL = 2;
	public final static int DOWNLOAD_SUCCESS = 3;
	public static int sNotificationId = -1;
	private NotificationManager updateNotificationMgr;
	private Notification updateNotification;
	//private PendingIntent updatePendingIntent;
	private File updateFile;
	private String downApkUrl;
	public static boolean sDownApkIng=false;
	private int state=-1;
	private int downProgress=0;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DOWNLOAD_SUCCESS:
				// 下载完成点击通知进入安装
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,"application/vnd.android.package-archive");
				installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pendingInstallIntent = PendingIntent.getActivity(DownApkService.this, 0, installIntent, 0);
				updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				updateNotification.defaults = Notification.DEFAULT_SOUND;// 设置铃声
				
				// 更新通知视图值
				int successResId=R.string.apk_download_success;
				if(sNotificationId==ADD_SUPPORT_DEVICE_APK_NOTIFICATION_ID)
					successResId=R.string.support_device_apk_download_success;
				updateNotification.contentView.setTextViewText(
						R.id.content_view_text1,getString(successResId));
				updateNotification.contentView.setProgressBar(
						R.id.content_view_progress, 100, 100, false);
				updateNotification.setLatestEventInfo(DownApkService.this,
								getString(successResId), 100 + "%", pendingInstallIntent);
				updateNotificationMgr.notify(sNotificationId, updateNotification);
				startActivity(installIntent);
				updateNotificationMgr.cancel(sNotificationId);
				stopSelf();
				reset();
				state=DOWNLOAD_SUCCESS;
				break;
			case DOWNLOADING:// 下载中状态
				updateNotification.contentView.setProgressBar(
						R.id.content_view_progress, 100, msg.arg1, false);
				downProgress=msg.arg1;
				String info=getString(R.string.latest_apk_downloading);
				if(sNotificationId==ADD_SUPPORT_DEVICE_APK_NOTIFICATION_ID)
					info=getString(R.string.support_third_device_apk_downloading);
				updateNotification.contentView.setTextViewText(
						R.id.content_view_text1, info + msg.arg1 + "%");
				PendingIntent pendingintent = PendingIntent.getActivity(DownApkService.this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
				updateNotification.setLatestEventInfo(DownApkService.this,
						info, msg.arg1 + "%", pendingintent);
				updateNotificationMgr.notify(sNotificationId, updateNotification);
				state=DOWNLOADING;
				break;
			case DOWNLOAD_FAIL:// 失败状态
				updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				Intent intent = new Intent();    
				intent.setClass(DownApkService.this, DownApkService.class);   
				intent.putExtra(CmucApplication.DOWNLOAD_APK_URL_EXTRAL,downApkUrl);
				PendingIntent downEndingIntent = PendingIntent.getService(DownApkService.this, 0, intent, 0);
				updateNotification.contentIntent = downEndingIntent;
				int failResId=R.string.apk_download_fail;
				if(sNotificationId==ADD_SUPPORT_DEVICE_APK_NOTIFICATION_ID)
					failResId=R.string.support_device_apk_download_fail;
				updateNotification.contentView.setTextViewText(
						R.id.content_view_text1, getString(failResId));
				updateNotification.setLatestEventInfo(DownApkService.this,
						getString(failResId), getString(failResId),	downEndingIntent);// fh add
				updateNotificationMgr.notify(sNotificationId, updateNotification);
				stopSelf();
				reset();
				state=DOWNLOAD_FAIL;
				break;
			default:
				stopSelf();
			}
		}

		private void reset() {
			sDownApkIng=false;
			sNotificationId=-1;
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return myBinder;
	}
	
	
	public int getState() {
		return state;
	}


	
	public int getDownProgress() {
		return downProgress;
	}


	@Override
	public void onDestroy() { // fh add
		if (sNotificationId != -1)
			updateNotificationMgr.cancel(sNotificationId);
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(!sDownApkIng){
			state=-1;
			downProgress=0;
			sNotificationId=intent.getIntExtra(CmucApplication.NOTIFICATION_ID_EXTRAL, UPDATE_APK_NOTIFICATION_ID);;
			sDownApkIng=true;
			downApkUrl = intent.getStringExtra(CmucApplication.DOWNLOAD_APK_URL_EXTRAL);

			updateFile = FileUtils.createSDFile(CmucApplication.APP_FILE_APK_DIR,
					CmucApplication.sApkFileName+sNotificationId);

			// 初始化通知管理器
			this.updateNotificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			this.updateNotification = new Notification();
			updateNotification.icon = R.drawable.icon;

			// 通知自定义视图
			updateNotification.contentView = new RemoteViews(getPackageName(),
					R.layout.down_apk_notification_view);
			updateNotification.contentView.setProgressBar(
					R.id.content_view_progress, 100, 0, false);

			// 开启线程进行下载
			new Thread(new updateThread()).start();
		}
		
		return START_STICKY_COMPATIBILITY;
	}
	
	/**
	 * 下载文件
	 * 
	 * @param downloadUrl
	 *            下载路径
	 * @param saveFile
	 *            保存文件名
	 */
	public long downloadFile(String downloadUrl, File saveFile)
			throws Exception {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;
		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		URL url = new URL(downloadUrl);
		httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
		if (currentSize > 0) {
			httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize
					+ "-");
		}
		httpConnection.setConnectTimeout(10*1000);
		httpConnection.setReadTimeout(20000);
		updateTotalSize = httpConnection.getContentLength();// 总大小
		if (httpConnection.getResponseCode() == 404) {
			throw new Exception("conection net 404！");
		}
		is = httpConnection.getInputStream();
		fos = new FileOutputStream(saveFile);
		byte[] buf = new byte[1024];
		int readSize = -1;

		while ((readSize = is.read(buf)) != -1) {
			fos.write(buf, 0, readSize);
			// 通知更新进度
			totalSize += readSize;
			int tmp = (int) (totalSize * 100 / updateTotalSize);
			// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
			if (downloadCount == 0 || tmp - 1 > downloadCount) {
				downloadCount += 1;
				Message msg = handler.obtainMessage();
				msg.what = DOWNLOADING;
				msg.arg1 = downloadCount;
				handler.sendMessage(msg);
			}
		}

		if (httpConnection != null) {
			httpConnection.disconnect();
		}
		if (is != null) {
			is.close();
		}
		if (fos != null) {
			fos.close();
		}
		return totalSize;
	}

	class updateThread implements Runnable {
		Message msg = handler.obtainMessage();

		@Override
		public void run() {
			try {
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				long downSize = downloadFile(downApkUrl, updateFile);
				if (downSize > 0) {
					// 下载成功！
					msg.what = DOWNLOAD_SUCCESS;
					handler.sendMessage(msg);
				}
			} catch (Exception ex) {
				ex.printStackTrace();// 下载失败
				msg.what = DOWNLOAD_FAIL;
				handler.sendMessage(msg);
			}
		}
	}
	
	 public class MyBinder extends Binder{
	        
	        public DownApkService getService(){
	            return DownApkService.this;
	        }
	    }
	    
	    private MyBinder myBinder = new MyBinder();

}
