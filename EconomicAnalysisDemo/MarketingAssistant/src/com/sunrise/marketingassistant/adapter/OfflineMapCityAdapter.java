package com.sunrise.marketingassistant.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.utils.DataTrafficUtils;

public class OfflineMapCityAdapter extends BaseOfflineMapAdapter implements OnClickListener {

	/*** 列表离线城市地图下载状态 */
	private MKOLSearchRecord mData;
	private MKOfflineMap mOffline;

	public OfflineMapCityAdapter(Context context, MKOLSearchRecord records, MKOfflineMap offlineMap) {
		super(context);
		mData = records;
		mOffline = offlineMap;
	}

	@Override
	public int getCount() {
		if (mData == null)
			return 0;

		if (mData.childCities == null || mData.childCities.isEmpty())
			return 1;

		return mData.childCities.size() + 1;
	}

	@Override
	public Object getItem(int arg0) {
		return getDataItem(arg0);

	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = LayoutInflater.from(mContext).inflate(R.layout.item_offline_map, null);
			arg1.setFocusable(false);
		}
		MKOLSearchRecord item = getDataItem(arg0);
		TextView tv1 = (TextView) arg1.findViewById(android.R.id.text1);

		String title = item.cityName;
		if (item.cityType == 1)
			title += "(全省)";
		tv1.setText(title);

		CharSequence status = "地图" + DataTrafficUtils.getFlowBitString(item.size);
		MKOLUpdateElement updateElement = mOffline.getUpdateInfo(item.cityID);
		if (updateElement != null) {// 存在更新目录中的时候
			if (updateElement.status == MKOLUpdateElement.FINISHED && updateElement.update)// 当完成状态和含有升级的时候
				status = Html.fromHtml(String.format(FORMAT_NEW_UPDATE_STATE, NAMES_STATUS_MKOLUP[updateElement.status],
						DataTrafficUtils.getFlowBitString(item.size), DataTrafficUtils.getFlowBitString(updateElement.serversize)));
			else
				status = Html.fromHtml(String.format(FORMAT_COMPLETE_STATE, status, NAMES_STATUS_MKOLUP[updateElement.status]));
		}

		TextView tv2 = (TextView) arg1.findViewById(android.R.id.text2);
		tv2.setText(status);

		TextView btn = (TextView) arg1.findViewById(R.id.btn_download);
		btn.setOnClickListener(this);
		btn.setTag(item.cityID);

		if (updateElement != null)
			btn.setText(getOperateBtnTxt(updateElement));
		else
			btn.setText(R.string.download);

		return arg1;
	}

	private MKOLSearchRecord getDataItem(int arg0) {

		if (arg0 == 0)
			return mData;

		return mData.childCities.get(arg0 - 1);
	}

	@Override
	public void onClick(View arg0) {
		int cityID = (Integer) arg0.getTag();
		switch (arg0.getId()) {
		case R.id.btn_download:
			doClickOperateBtn(mOffline.getUpdateInfo(cityID), cityID);
			break;
		default:
			break;
		}
	}
}
