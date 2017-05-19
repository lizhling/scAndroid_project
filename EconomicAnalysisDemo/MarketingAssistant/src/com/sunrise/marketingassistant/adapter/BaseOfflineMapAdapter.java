package com.sunrise.marketingassistant.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.widget.BaseAdapter;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.service.BaiduMapOfflineMapService;
import com.sunrise.marketingassistant.utils.DataTrafficUtils;
import com.sunrise.marketingassistant.view.TwoButtonDialog;

public abstract class BaseOfflineMapAdapter extends BaseAdapter {

	private static final String FORMAT_NOTICE_DELETE = "是否&nbsp;<b><font color=\"#fe1c3f\">删除 </font></b>&nbsp;%s&nbsp;(<i>%s</i>)&nbsp;的离线地图？";
	/*** 列表离线城市地图下载状态 */
	final String FORMAT_DOWNLOAD_STATE = "%s：%d %%(%s/%s)";
	final static String FORMAT_COMPLETE_STATE = "%s&nbsp;&nbsp;&nbsp;<i><font color=\"#679800\">%s</font></i>";

	final static String FORMAT_NEW_UPDATE_STATE = FORMAT_COMPLETE_STATE + "&nbsp;&nbsp;&nbsp;<font color=\"#ea4444\">有更新 <i>%s</i></font>";

	/*** 列表离线城市地图下载状态 */
	protected String[] NAMES_STATUS_MKOLUP;
	protected Context mContext;

	public BaseOfflineMapAdapter(Context context) {
		mContext = context;
		NAMES_STATUS_MKOLUP = context.getResources().getStringArray(R.array.statesOfOfflineMapElement);
	}

	protected final CharSequence getOperateBtnTxt(MKOLUpdateElement item) {
		if (item == null)
			return "下载";

		switch (item.status) {
		case MKOLUpdateElement.DOWNLOADING:
		case MKOLUpdateElement.WAITING:
			return "暂停";
		case MKOLUpdateElement.SUSPENDED:
			return "继续";
		case MKOLUpdateElement.FINISHED:
			if (item.update)
				return "升级";
			else
				return "删除";
		default:

			return "下载";
		}
	}

	protected final void doClickOperateBtn(MKOLUpdateElement item, int cityID) {
		if (item == null) {
			doOperateOfflineMap(cityID, ExtraKeyConstant.COMMAND_START);
			return;
		}
		switch (item.status) {
		case MKOLUpdateElement.DOWNLOADING:
		case MKOLUpdateElement.WAITING:
			doOperateOfflineMap(item.cityID, ExtraKeyConstant.COMMAND_PAUSE);
			break;
		case MKOLUpdateElement.SUSPENDED:
			doOperateOfflineMap(item.cityID, ExtraKeyConstant.COMMAND_START);
			break;
		case MKOLUpdateElement.FINISHED:
			if (item.update)
				doOperateOfflineMap(item.cityID, ExtraKeyConstant.COMMAND_UPDATE);
			else
				doAlertDeleteOfflineMap(item);
			break;
		default:
			doOperateOfflineMap(item.cityID, ExtraKeyConstant.COMMAND_START);
		}
	}

	protected void doAlertDeleteOfflineMap(MKOLUpdateElement item) {
		new TwoButtonDialog(mContext, Html.fromHtml(String.format(FORMAT_NOTICE_DELETE, item.cityName, DataTrafficUtils.getFlowBitString(item.size))),
				new OnDeleteAlertDialogListener(item.cityID), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				}, "是", "否").show();
	}

	/**
	 * 启动下载离线地图
	 * 
	 * @param tag
	 */
	protected final void doOperateOfflineMap(int cityID, int command) {
		Intent service = new Intent(mContext, BaiduMapOfflineMapService.class);
		service.putExtra(Intent.EXTRA_SUBJECT, cityID);
		service.putExtra(Intent.EXTRA_KEY_EVENT, command);
		mContext.startService(service);
	}

	protected class OnDeleteAlertDialogListener implements DialogInterface.OnClickListener {
		private int cityID;

		public OnDeleteAlertDialogListener(int cityId) {
			cityID = cityId;
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			doOperateOfflineMap(cityID, ExtraKeyConstant.COMMAND_REMOVE);
			arg0.dismiss();
		}
	}
}
