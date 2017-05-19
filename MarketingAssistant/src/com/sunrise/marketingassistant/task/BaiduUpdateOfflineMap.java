package com.sunrise.marketingassistant.task;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

public class BaiduUpdateOfflineMap extends GenericTask implements MKOfflineMapListener {
	public void execute(int cityId, TaskListener listener) {
		setListener(listener);
		this.execute(new TaskParams("cityId", String.valueOf(cityId)));
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		MKOfflineMap mOffline = new MKOfflineMap();
		// 传入接口事件，离线地图更新会触发该回调
		mOffline.init(this);

		int id = Integer.parseInt(params[0].getString("cityId"));
		boolean isStarted = mOffline.start(id);
		if (!isStarted)
			return TaskResult.FAILED;

		return TaskResult.OK;
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		// type - 事件类型: MKOfflineMap.TYPE_NEW_OFFLINE,
		// MKOfflineMap.TYPE_DOWNLOAD_UPDATE, MKOfflineMap.TYPE_VER_UPDATE.
		// state - 事件状态: 当type为TYPE_NEW_OFFLINE时，表示新安装的离线地图数目.
		// 当type为TYPE_DOWNLOAD_UPDATE时，表示更新的城市ID.
	}
}
