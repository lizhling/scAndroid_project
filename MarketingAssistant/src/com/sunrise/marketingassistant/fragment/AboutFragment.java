package com.sunrise.marketingassistant.fragment;

import java.text.ParseException;
import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.javascript.utils.DateUtils;
import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.service.UpdateService;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetUpdateInfoTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.view.TwoButtonDialog;
import com.sunrise.marketingassistant.utils.CommUtil;

public class AboutFragment extends BaseFragment implements OnClickListener, TaskListener {

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_about, container, false);
		view.findViewById(R.id.check_version_update).setOnClickListener(this);
		view.findViewById(R.id.china_mobile_url).setOnClickListener(this);
		String version = String.format(getString(R.string.text_about_version), App.sAppVersionName);
		((TextView) (view.findViewById(R.id.apk_version))).setText(version);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void doGetUpdateInfos() {
		GenericTask getUpdateInfos = new GetUpdateInfoTask();
		getUpdateInfos.setListener(this);
		getUpdateInfos.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.check_version_update:
			if (!UpdateService.isDownApk)
				doGetUpdateInfos();
			else {
				Toast.makeText(mBaseActivity, getString(R.string.latest_apk_downloading), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.china_mobile_url:
			visitChinaMobile();
			break;
		}

	}

	private void visitChinaMobile() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(getString(R.string.url_sc10086));
		intent.setData(content_url);
		startActivity(intent);
	}

	/**
	 * 强制升级功能
	 * 
	 * @param updateInfo
	 * @param startActivity
	 */
	private void showForceUpdateApkDialog(final UpdateInfo updateInfo) {
		CharSequence message = CommUtil.sGetDownloadDescriptionWords(mBaseActivity, updateInfo);

		TwoButtonDialog dialog = new TwoButtonDialog(mBaseActivity, message, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				mBaseActivity.finish();
				CommUtil.exit(mBaseActivity);
			}
		}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				startDownLoadApk(updateInfo);
			}
		}, getString(R.string.exit), getString(R.string.update_now));

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				CommUtil.exit(mBaseActivity);
			}
		});
		dialog.show();
	}

	/**
	 * 显示升级信息对话框
	 * 
	 * @param updateInfo
	 * @param startActivity
	 */
	private void showUpdateApkDialog(final UpdateInfo updateInfo) {

		CharSequence message = CommUtil.sGetDownloadDescriptionWords(mBaseActivity, updateInfo);

		TwoButtonDialog dialog = new TwoButtonDialog(mBaseActivity, message, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				startDownLoadApk(updateInfo);
			}
		}, getString(R.string.later_say), getString(R.string.update_now));
		dialog.setCancelable(false);
		dialog.show();
	}

	private void startDownLoadApk(UpdateInfo updateInfo) {
		Intent intent = new Intent(mBaseActivity, UpdateService.class);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, updateInfo);
		mBaseActivity.startService(intent);
		// try {
		// CommUtil.downloadApk(mBaseActivity, downLoadUrl, true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param != null) {
			ArrayList<UpdateInfo> datas = (ArrayList<UpdateInfo>) param;
			for (UpdateInfo updateInfo : datas) {
				int type = updateInfo.getType();
				if (type != UpdateInfo.TYPE_APK)
					continue;

				int updateType = updateInfo.getUpdateType();
				long newVersion = updateInfo.getNewVersionCode();
//				try {
//					newVersion = DateUtils.string2Long(updateInfo.getNewVersionCode(), ExtraKeyConstant.FORMAT_PARAM_TIME);
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
				getPreferences().putResNewVersion(String.valueOf(type), newVersion);

				if (newVersion > App.sAppCode) {
					if (updateType == UpdateInfo.TYPE_FORCE_UPDATE) { // 判断是否强制更新
						showForceUpdateApkDialog(updateInfo);
					} else {
						showUpdateApkDialog(updateInfo);
					}
				} else {
					Toast.makeText(mBaseActivity, getString(R.string.latest_version_name), Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog();
		showDialog(getString(R.string.check_apk_version_progress));
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK)
			Toast.makeText(mBaseActivity, getString(R.string.latest_version), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCancelled(GenericTask task) {

	}

	@Override
	public String getName() {
		return null;
	}
}
