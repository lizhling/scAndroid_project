package com.sunrise.econan.ui.activity;

import java.util.ArrayList;

import com.sunrise.econan.EconomicAlaysisApp;
import com.sunrise.econan.ExtraKeyConstant;
import com.sunrise.econan.R;
import com.sunrise.econan.entity.UpdateInfo;
import com.sunrise.econan.model.AppMenuBean;
import com.sunrise.econan.service.UpdateService;
import com.sunrise.econan.task.CheckLoginTask;
import com.sunrise.econan.task.GenericTask;
import com.sunrise.econan.task.GetSSJFInfoTask;
import com.sunrise.econan.task.GetUpdateInfoTask;
import com.sunrise.econan.task.TaskListener;
import com.sunrise.econan.task.TaskResult;
import com.sunrise.econan.ui.view.TwoButtonDialog;
import com.sunrise.econan.utils.CommUtil;
import com.sunrise.econan.utils.HardwareUtils;
import com.sunrise.javascript.utils.ActivityUtils;
import com.sunrise.javascript.utils.JsonUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

public class StartActivity extends BaseActivity implements TaskListener {
	private GenericTask mTask;

	private int mState = 0;
	private final int STATE_SSJF = 1;
	private final int STATE_LOGIN = 2;
	private final int STATE_UPDATE = 4;
	private final int STATE_COMPLETE = STATE_LOGIN | STATE_SSJF | STATE_UPDATE;

	private TwoButtonDialog mDialogCertain;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_start);
		{
			ImageView imageview = (ImageView) findViewById(R.id.image_splash);
			AnimationDrawable drawable = (AnimationDrawable) imageview.getDrawable();
			drawable.start();
		}

		if (!CommUtil.isOpenNetwork(this)) {
			CommUtil.showAlert(this, null, getString(R.string.frame_loading_fail), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					exit(StartActivity.this);
				}
			});
			return;
		}
		doGetUpdateInfo();

		// checkAutoLogin();
	}

	public void onStart() {
		super.onStart();
	}

	public void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		if (mTask != null)
			mTask.cancle();
		super.onDestroy();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 1) {
			if (resultCode != RESULT_OK) {
				finish();
			} else {
				mState |= STATE_LOGIN;
				if (isComplete())
					goNextPage();
			}
		} else
			super.onActivityResult(requestCode, resultCode, intent);
	}

	private boolean isComplete() {
		if (mState == STATE_COMPLETE)
			return true;
		return false;
	}

	/**
	 * 启动加载升级配置信息
	 */
	private void doGetUpdateInfo() {
		mTask = new GetUpdateInfoTask();
		mTask.setListener(this);
		mTask.execute();
	}

	private void doInitSSJF(UpdateInfo updateinfo) {
		String appTag = "SSJF";
		String menuId = "453201";

		if (updateinfo != null) {
			if (!TextUtils.isEmpty(updateinfo.getUpdateDescription()))
				appTag = updateinfo.getUpdateDescription();
			if (!TextUtils.isEmpty(updateinfo.getNewVersionName()))
				menuId = updateinfo.getNewVersionName();
		}

		mTask = new GetSSJFInfoTask().execute(appTag, menuId, String.valueOf(HardwareUtils.getLCDwidth(this)),
				String.valueOf(HardwareUtils.getLCDHeight(this)), this);
	}

	private void checkAutoLogin() {
		String account = getPreferences().getUserName();
		String password = getPreferences().getPassword();
		if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) || !getPreferences().isAutoLogin()) {
			goLoginPage();
		} else
			mTask = new CheckLoginTask().execute(account, password, HardwareUtils.getPhoneIMSI(this), HardwareUtils.getPhoneIMEI(this), this);

	}

	private void goNextPage() {
		startActivity(new Intent(this, WebViewActivity.class));
		finish();
	}

	private void goLoginPage() {
		getPreferences().setCheckedNumber(null);
		startActivityForResult(new Intent(this, LoginActivity.class), ActivityUtils.GO_LOGIN_REQUEST_CODE);
	}

	/**
	 * 处理升级信息
	 * 
	 * @param param
	 */
	private void dealUpdateInfos(ArrayList<UpdateInfo> datas) {
		if (datas == null || datas.size() == 0)
			return;
		boolean hasSSJF = false;
		for (UpdateInfo updateInfo : datas) {
			int type = updateInfo.getType();
			switch (type) {
			case UpdateInfo.TYPE_APK:
				dealUpdateApkInfo(datas, updateInfo, type);
				break;
			case UpdateInfo.TYPE_SSJF_INFO:
				doInitSSJF(updateInfo);
				hasSSJF = true;
				break;
			default:
				break;
			}
		}
		if (!hasSSJF)
			doInitSSJF(null);
	}

	/**
	 * 处理升级信息
	 * 
	 * @param datas
	 * @param updateInfo
	 * @param type
	 */
	private void dealUpdateApkInfo(ArrayList<UpdateInfo> datas, UpdateInfo updateInfo, int type) {
		long newVersion = updateInfo.getNewVersionCode();
		int updateType = updateInfo.getUpdateType();
		getPreferences().putResNewVersion(String.valueOf(type), newVersion);
		getPreferences().putString(ExtraKeyConstant.APP_DOWNLOAD_INFO, JsonUtils.writeObjectToJsonStr(updateInfo));

		// 更新APK信息
		if (newVersion > EconomicAlaysisApp.sAppCode) {
			dismissDialog();
			if (updateType == UpdateInfo.TYPE_FORCE_UPDATE) { // 强制更新
				showForceUpdateApkDialog(updateInfo);
			} else {
				showUpdateApkDialog(updateInfo);
			}
		} else
			checkAutoLogin();
	}

	/**
	 * 强制升级功能
	 * 
	 * @param updateInfo
	 * @param startActivity
	 */
	private void showForceUpdateApkDialog(final UpdateInfo updateInfo) {

		CharSequence message = CommUtil.sGetDownloadDescriptionWords(this, updateInfo);

		TwoButtonDialog dialog = new TwoButtonDialog(this, message, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				finish();
			}
		}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				startDownLoadApk(updateInfo);
				finish();
			}
		}, getString(R.string.exitApp), getString(R.string.update_now));
		dialog.setCancelable(false);
		dialog.show();
	}

	/**
	 * 显示升级信息对话框
	 * 
	 * @param updateInfo
	 */
	private void showUpdateApkDialog(final UpdateInfo updateInfo) {
		CharSequence message = CommUtil.sGetDownloadDescriptionWords(this, updateInfo);

		TwoButtonDialog dialog = new TwoButtonDialog(this, message, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				checkAutoLogin();
			}
		}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				startDownLoadApk(updateInfo);
				showTipDialog();
			}
		}, getString(R.string.later_say), getString(R.string.update_now));
		dialog.setCancelable(false);
		dialog.show();
	}

	/**
	 * 启动下载客户端
	 * 
	 * @param downLoadUrl
	 */
	private void startDownLoadApk(UpdateInfo updateInfo) {

		Intent intent = new Intent(this, UpdateService.class);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, updateInfo);
		startService(intent);
	}

	/**
	 * 询问更新是否继续
	 */
	private void showTipDialog() {
		if (mDialogCertain == null) {
			mDialogCertain = new TwoButtonDialog(this, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					checkAutoLogin();
				}
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					finish();
				}
			});
			mDialogCertain.setMessage("正在程序更新中, 是否继续？");
		}
		mDialogCertain.show();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {

	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		if (task instanceof GetUpdateInfoTask) {
			mState |= STATE_UPDATE;
			if (result != TaskResult.OK) {
				checkAutoLogin();
				doInitSSJF(null);
			}
		} else if (task instanceof GetSSJFInfoTask) {
			mState |= STATE_SSJF;
		} else if (task instanceof CheckLoginTask) {
			if (result == TaskResult.OK) {
				mState |= STATE_LOGIN;
			} else {
				goLoginPage();
			}
		}

		if (isComplete())
			goNextPage();
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (task instanceof GetSSJFInfoTask) {
			AppMenuBean bean = (AppMenuBean) param;

			if (bean.getChildVersion() > getPreferences().getSSJFPageVerCode()) {
				getPreferences().setSSJFPageVerCode(bean.getChildVersion());
				getPreferences().setSSJFPageUrl(bean.getContent());
			}

		} else if (task instanceof GetUpdateInfoTask) {
			dealUpdateInfos((ArrayList<UpdateInfo>) param);
		} else if (task instanceof CheckLoginTask) {

		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		exit(this);
	}
}
