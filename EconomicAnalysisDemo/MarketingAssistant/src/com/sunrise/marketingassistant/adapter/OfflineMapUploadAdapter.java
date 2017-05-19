package com.sunrise.marketingassistant.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.utils.DataTrafficUtils;

public class OfflineMapUploadAdapter extends BaseOfflineMapAdapter implements OnClickListener {
	private ArrayList<MKOLUpdateElement> mData;

	// <font
	// dir=\"ltr\" style=\"margin-left: 0px; margin-right: 10px\"><span style=\"font-size:12px;\">&nbsp;&nbsp;&nbsp;</span>总量：%s";

	public OfflineMapUploadAdapter(Context context, ArrayList<MKOLUpdateElement> allUpdateInfo) {
		super(context);
		mData = allUpdateInfo;
	}

	@Override
	public int getCount() {
		if (mData == null)
			return 0;
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		if (mData == null)
			return null;
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null)
			arg1 = LayoutInflater.from(mContext).inflate(R.layout.item_offline_map_update, null);
		MKOLUpdateElement item = mData.get(arg0);
		TextView tv1 = (TextView) arg1.findViewById(android.R.id.text1);
		tv1.setText(item.cityName);

		TextView tv2 = (TextView) arg1.findViewById(android.R.id.text2);

		TextView btn = (TextView) arg1.findViewById(R.id.btn_download);
		btn.setOnClickListener(this);
		btn.setTag(arg0);
		btn.setText(getOperateBtnTxt(item));

		ProgressBar progressBar01 = (ProgressBar) arg1.findViewById(R.id.progressBar01);
		switch (item.status) {
		case MKOLUpdateElement.DOWNLOADING:
		case MKOLUpdateElement.WAITING:
		case MKOLUpdateElement.SUSPENDED:
			progressBar01.setVisibility(View.VISIBLE);
			progressBar01.setMax(item.serversize);
			progressBar01.setProgress(item.size);
			tv2.setText(String.format(FORMAT_DOWNLOAD_STATE, NAMES_STATUS_MKOLUP[item.status], item.ratio, DataTrafficUtils.getFlowBitString(item.size),
					DataTrafficUtils.getFlowBitString(item.serversize)));
			tv2.setGravity(Gravity.CENTER);
			break;
		case MKOLUpdateElement.FINISHED:
			if (item.update) {
				progressBar01.setVisibility(View.GONE);
				tv2.setText(Html.fromHtml(String.format(FORMAT_NEW_UPDATE_STATE, NAMES_STATUS_MKOLUP[item.status],
						DataTrafficUtils.getFlowBitString(item.size), DataTrafficUtils.getFlowBitString(item.serversize))));
				tv2.setGravity(Gravity.LEFT);
				break;
			}
		default:
			progressBar01.setVisibility(View.GONE);
			tv2.setText(Html.fromHtml(String.format(FORMAT_COMPLETE_STATE, NAMES_STATUS_MKOLUP[item.status], DataTrafficUtils.getFlowBitString(item.size))));
			tv2.setGravity(Gravity.LEFT);
		}

		return arg1;
	}

	@Override
	public void onClick(View arg0) {
		MKOLUpdateElement item = mData.get((Integer) arg0.getTag());
		switch (arg0.getId()) {
		case R.id.btn_download:
			doClickOperateBtn(item, item.cityID);
			break;
		default:
			break;
		}
	}

}