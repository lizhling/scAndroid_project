package com.starcpt.cmuc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.model.bean.SkinBean;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.utils.FileUtils;
/**
 * @author LUOYUN
 */
public class DownSkinService extends Service {
	private final static int DOWNLOAD_COMPLETE = 1;
	private final static int DOWNLOAD_FALL = 2;
	private final static int DOWNLOAD_SUCCESS = 3;
	private NotificationManager updateNotificationMgr;
//	private PendingIntent updatePendingIntent;

	private LinkedList<String> downSkinList;
	private int notificationIdIndex = 1;
	
	public void onCreate() {
		this.updateNotificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.downSkinList = new LinkedList<String>();
	};	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		if (intent == null) {// fh add
			stopSelf();
			return;
		}
		
		SkinBean skinBean = (SkinBean) intent.getParcelableExtra(SkinManager.SKIN_BEAN);
		downloadSkin(skinBean,notificationIdIndex++);
	}
	
	@Override
	public void onDestroy() { // fh add
		updateNotificationMgr.cancel(notificationIdIndex);
		super.onDestroy();
	}

	private void stopService(){
		if (downSkinList.size()==0) {
			stopSelf();
		}
	}
	
	private void sendDownloadCompleteBroadcast(SkinBean skinBean){
		Intent intent = new Intent();
		intent.setAction(SkinManager.SKIN_DOWNLOAD_COMPLETED_RECEIVER);
		intent.putExtra(SkinManager.SKIN_BEAN, skinBean);
		sendBroadcast(intent);
	}
	
	@SuppressLint("HandlerLeak")
	private void downloadSkin(final SkinBean skinBean,final int notificationId){
		final Notification updateNotification = new Notification();
		String downSkinUrl = skinBean.getDownUrl();
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String skinName = skinBean.getSkinName();
				switch (msg.what) {
				case DOWNLOAD_SUCCESS:
					//下载完成点击通知进入安装
					updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					updateNotification.defaults = Notification.DEFAULT_SOUND;// 设置铃声
					
					// 更新通知视图值
					updateNotification.contentView.setTextViewText(
							R.id.content_view_text1, skinName+"下载成功。");
					updateNotification.contentView.setProgressBar(
							R.id.content_view_progress, 100, 100, false);
					updateNotificationMgr
							.notify(notificationId, updateNotification);
					downloadEnd(skinBean,SkinBean.SKIN_STATE_DOWNLOAD_OK);
					updateNotificationMgr.cancel(notificationId);
					break;
				case DOWNLOAD_COMPLETE:// 下载中状态
					updateNotification.contentView.setProgressBar(
							R.id.content_view_progress, 100, msg.arg1, false);
					updateNotification.contentView.setTextViewText(
							R.id.content_view_text1,skinName+getString(R.string.downloadling) + msg.arg1 + "%");
					updateNotificationMgr.notify(notificationId, updateNotification);
					break;
				case DOWNLOAD_FALL:// 失败状态
					updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					Intent intent = new Intent();    
					intent.setClass(DownSkinService.this, DownSkinService.class);   
					PendingIntent downEndingIntent = PendingIntent.getService(DownSkinService.this, 0, intent, 0);
					updateNotification.contentIntent = downEndingIntent;
					updateNotification.contentView.setTextViewText(
							R.id.content_view_text1, skinName+getString(R.string.download_failed));
					updateNotificationMgr.notify(notificationId, updateNotification);
					downloadEnd(skinBean,SkinBean.SKIN_STATE_DOWNLOAD_FAILED);
					break;
				default:
					downSkinList.remove(skinName);
					stopService();
				}
			}
		};
		boolean isExist = FileUtils.fileIsExist(CmucApplication.APP_SKIN_CACHE_DIR, SkinManager.getSkinName(skinBean.getDownUrl()));
		if (isExist) {
			downloadEnd(skinBean,SkinBean.SKIN_STATE_DOWNLOAD_OK);
			return;
		}
		File downloadFile = FileUtils.createSDFile(CmucApplication.APP_SKIN_CACHE_DIR,SkinManager.getSkinName(skinBean.getDownUrl()));
		
		// 初始化通知管理器
		updateNotification.icon = R.drawable.icon;
		// 通知自定义视图
		updateNotification.contentView = new RemoteViews(getPackageName(),
				R.layout.down_apk_notification_view);
		updateNotification.contentView.setProgressBar(
				R.id.content_view_progress, 100, 0, false);
		updateNotification.contentIntent = PendingIntent.getActivity(this, 0, new Intent(),
				Intent.FLAG_ACTIVITY_NEW_TASK);

		// 开启线程进行下载
		new Thread(new DownloadThread(handler, downloadFile, downSkinUrl)).start();
	}
	
	private void downloadEnd(SkinBean skinBean,int state){
		skinBean.setSkinState(state);
		sendDownloadCompleteBroadcast(skinBean);
		downSkinList.remove(skinBean.getSkinName());
		stopService();
	}
	
	
	/**
	 * 下载文件
	 * 
	 * @param downloadUrl
	 *            下载路径
	 * @param saveFile
	 *            保存文件名
	 */
	public long downloadFile(Handler handler,String downloadUrl, File saveFile)
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
				msg.what = DOWNLOAD_COMPLETE;
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

	class DownloadThread implements Runnable {
		
		private Handler handler;
		private File downloadFile;
		private String downSkinUrl;
		Message msg;		
	
		public DownloadThread(Handler handler, File downloadFile,
				String downSkinUrl) {
			super();
			this.handler = handler;
			this.downloadFile = downloadFile;
			this.downSkinUrl = downSkinUrl;
			this.msg = handler.obtainMessage();
		}
		
		@Override
		public void run() {
			try {
				if (!downloadFile.exists()) {
					downloadFile.createNewFile();
				}
				long downSize = downloadFile(handler,downSkinUrl, downloadFile);
				if (downSize > 0) {
					// 下载成功！
					msg.what = DOWNLOAD_SUCCESS;
					handler.sendMessage(msg);
				}
			} catch (Exception ex) {
				ex.printStackTrace();// 下载失败
				msg.what = DOWNLOAD_FALL;
				//如果下载失败，删除缓存文件
				if (downloadFile.exists()) {
					downloadFile.delete();
				}
				handler.sendMessage(msg);
			}
		}
	}

}
