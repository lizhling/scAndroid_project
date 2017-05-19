package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.adapter.OfflineMapCityAdapter;
import com.sunrise.marketingassistant.adapter.OfflineMapUploadAdapter;
import com.sunrise.marketingassistant.utils.BaiduMapUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ViewSwitcher;

public class OfflineMapFragment extends BaseFragment implements OnCheckedChangeListener, MKOfflineMapListener {

	private BaiduMapUtils baiduMapUtils;
	private ViewSwitcher mViewSwitcher;
	private MKOfflineMap mOffline = new MKOfflineMap();
	private ListView mListCity;
	private ListView mListUpload;
	private OfflineMapCityAdapter mCityAdapter;
	private OfflineMapUploadAdapter mOfflineMapUploadAdapter;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			doResetData();
		}
	};
	private ArrayList<MKOLUpdateElement> mArrayUpeateInfo;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baiduMapUtils = new BaiduMapUtils(mBaseActivity);
		mOffline.init(this);
	}

	public void onStart() {
		doResetData();
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ExtraKeyConstant.ACTION_REFRESH_OFFLINE_MAP_STATE);
		mBaseActivity.registerReceiver(mReceiver, filter);
	}

	public void onStop() {
		super.onStop();
		mBaseActivity.unregisterReceiver(mReceiver);
	}

	public void onDestroy() {
		mOffline.destroy();
		super.onDestroy();
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_offline_operation, null, false);

		mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcher01);
		{
			RadioButton checkBox = (RadioButton) view.findViewById(R.id.radio0);
			checkBox.setOnCheckedChangeListener(this);
		}
		{
			RadioButton checkBox = (RadioButton) view.findViewById(R.id.radio1);
			checkBox.setOnCheckedChangeListener(this);
		}

		mListUpload = (ListView) view.findViewById(R.id.listView_upload);
		mListUpload.setEmptyView(view.findViewById(R.id.none_gridview1));

		mListCity = (ListView) view.findViewById(R.id.listView_citys);

		initData();

		return view;
	}

	private void initData() {
		mArrayUpeateInfo = mOffline.getAllUpdateInfo();
		if (mArrayUpeateInfo == null)
			mArrayUpeateInfo = new ArrayList<MKOLUpdateElement>();
		mOfflineMapUploadAdapter = new OfflineMapUploadAdapter(mBaseActivity, mArrayUpeateInfo);
		mListUpload.setAdapter(mOfflineMapUploadAdapter);

		ArrayList<MKOLSearchRecord> mMKOSearchRecords = mOffline.searchCity("四川");
		if (mMKOSearchRecords != null) {
			mCityAdapter = new OfflineMapCityAdapter(mBaseActivity, mMKOSearchRecords.get(0), mOffline);
			mListCity.setAdapter(mCityAdapter);
		}
	}

	private void doResetData() {
		mArrayUpeateInfo.clear();
		ArrayList<MKOLUpdateElement> temp = mOffline.getAllUpdateInfo();
		if (temp != null)
			mArrayUpeateInfo.addAll(temp);
		mOfflineMapUploadAdapter.notifyDataSetChanged();

		mCityAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (!arg1)
			return;

		switch (arg0.getId()) {
		case R.id.radio0:
			mViewSwitcher.setInAnimation(mBaseActivity, R.anim.push_left_in);
			mViewSwitcher.setOutAnimation(mBaseActivity, R.anim.push_right_out);
			mViewSwitcher.setDisplayedChild(0);
			break;
		case R.id.radio1:
			mViewSwitcher.setInAnimation(mBaseActivity, R.anim.push_right_in);
			mViewSwitcher.setOutAnimation(mBaseActivity, R.anim.push_left_out);
			mViewSwitcher.setDisplayedChild(1);
			break;

		default:
			break;
		}
	}

	@Override
	public void onGetOfflineMapState(int arg0, int arg1) {
		String status = "事件状态: ";
		switch (arg1) {
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			status += "表示新安装的离线地图数目";
			break;
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
			status += "表示更新下载";
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			status += "表示版本升级";
			break;
		}
		String type = "事件类型:";
		switch (arg0) {
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			type += "新安装离线地图事件类型";
			break;
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
			type += "离线地图下载更新事件类型";
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			type += "离线地图数据版本更新事件类型";
			break;
		default:
			break;
		}
		LogUtlis.w("离线地图状态", type + "\n" + status);
	}

}
