package com.sunrise.marketingassistant.service;

import java.util.ArrayList;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class BaiduMapOfflineMapService extends Service implements MKOfflineMapListener, ExtraKeyConstant {

	private MKOfflineMap mOffline;

	// private boolean isOpened;
	// private Thread mThreadRefresh = new Thread() {
	// public void run() {
	// while (isOpened) {
	// try {
	// Thread.sleep(3000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// if (isNeedRefresh())
	// sendBroadcast(new Intent(ACTION_REFRESH_OFFLINE_MAP_STATE));
	// else
	// break;
	// }
	//
	// stopSelf();
	// }
	// };

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		super.onCreate();

		mOffline = new MKOfflineMap();
		// 传入接口事件，离线地图更新会触发该回调
		mOffline.init(this);

	}

	public void onDestroy() {
		mOffline.destroy();
		super.onDestroy();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent == null)
			return super.onStartCommand(intent, flags, startId);

		int cityID = intent.getIntExtra(Intent.EXTRA_SUBJECT, -1);

		int command = intent.getIntExtra(Intent.EXTRA_KEY_EVENT, COMMAND_START);

		if (cityID == -1)
			return super.onStartCommand(intent, flags, startId);

		switch (command) {
		case COMMAND_START:
			doStart(cityID);
			break;
		case COMMAND_REMOVE:
			if (mOffline.remove(cityID))
				sendBroadcast(new Intent(ACTION_REFRESH_OFFLINE_MAP_STATE));
			else
				Toast.makeText(this, "删除失败，请重试……", Toast.LENGTH_SHORT).show();
			break;
		case COMMAND_PAUSE:
			if (mOffline.pause(cityID))
				sendBroadcast(new Intent(ACTION_REFRESH_OFFLINE_MAP_STATE));
			else
				Toast.makeText(this, "无法暂停,请重试……", Toast.LENGTH_SHORT).show();
			break;
		case COMMAND_UPDATE:
			doUpdate(cityID);
			break;
		default:
			break;
		}

		// if (!isNeedRefresh())
		// stopSelf();

		return super.onStartCommand(intent, flags, startId);
	}

	private void doUpdate(int cityID) {
		MKOLUpdateElement info = mOffline.getUpdateInfo(cityID);
		if (info != null && (info.status == MKOLUpdateElement.DOWNLOADING || info.status == MKOLUpdateElement.WAITING)) {
			Toast.makeText(this, "已在下载队列中不用重复下载……", Toast.LENGTH_SHORT).show();
			return;
		}

		if (mOffline.update(cityID)) {
			sendBroadcast(new Intent(ACTION_REFRESH_OFFLINE_MAP_STATE));
		} else
			Toast.makeText(this, "无法更新,请重试……", Toast.LENGTH_SHORT).show();
	}

	private void doStart(int cityID) {

		MKOLUpdateElement info = mOffline.getUpdateInfo(cityID);

		if (info != null && (info.status == MKOLUpdateElement.DOWNLOADING || info.status == MKOLUpdateElement.WAITING)) {
			Toast.makeText(this, "已在下载队列中不用重复下载……", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mOffline.start(cityID)) {
			sendBroadcast(new Intent(ACTION_REFRESH_OFFLINE_MAP_STATE));
		} else
			Toast.makeText(this, "启动下载失败，请重试……", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		String s = "事件状态: ";
		switch (state) {
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			s += "表示新安装的离线地图数目";
			break;
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
			s += "表示更新的城市ID";
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			s += "离线地图数据版本更新事件类型";
			break;
		}
		String t = "事件类型:";
		switch (type) {
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			t += "新安装离线地图事件类型";
			break;
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
			t += "离线地图下载更新事件类型";
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			t += "离线地图数据版本更新事件类型";
			break;
		default:
			break;
		}
		LogUtlis.i("离线地图状态", t + "\n" + s);

		sendBroadcast(new Intent(ACTION_REFRESH_OFFLINE_MAP_STATE));
		ArrayList<MKOLUpdateElement> array = mOffline.getAllUpdateInfo();
		for (int i = 0; i < array.size(); i++) {
			MKOLUpdateElement item = array.get(i);
			if (item.status == MKOLUpdateElement.DOWNLOADING || item.status == MKOLUpdateElement.WAITING)
				return;
		}
		stopSelf();
	}

	// private void startThreadRefreshList() {
	// if (!mThreadRefresh.isAlive())
	// mThreadRefresh.start();
	// }
	//
	// private boolean isNeedRefresh() {
	// boolean isNeedRefresh = false;
	//
	// ArrayList<MKOLUpdateElement> array = mOffline.getAllUpdateInfo();
	// for (int i = 0; i < array.size(); i++) {
	// MKOLUpdateElement item = array.get(i);
	// if (item.status == MKOLUpdateElement.DOWNLOADING || item.status ==
	// MKOLUpdateElement.WAITING) {
	// isNeedRefresh = true;
	// break;
	// }
	// }
	//
	// return isNeedRefresh;
	// }

}
