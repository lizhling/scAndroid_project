package com.sunrise.micromarketing.ui.activity;

import java.util.ArrayList;

import com.sunrise.micromarketing.App;
import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.entity.UpdateInfo;
import com.sunrise.micromarketing.service.UpdateService;
import com.sunrise.micromarketing.task.CheckLoginTask;
import com.sunrise.micromarketing.task.GenericTask;
import com.sunrise.micromarketing.task.GetAllMenusTask;
import com.sunrise.micromarketing.task.GetTabInfosTask;
import com.sunrise.micromarketing.task.GetUpdateInfoTask;
import com.sunrise.micromarketing.task.TaskListener;
import com.sunrise.micromarketing.task.TaskResult;
import com.sunrise.micromarketing.ui.view.TwoButtonDialog;
import com.sunrise.micromarketing.utils.CommUtil;
import com.sunrise.micromarketing.utils.HardwareUtils;
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
	private final int STATE_INIT = 1;
	private final int STATE_LOGIN = 2;
	private final int STATE_UPDATE_TABS = 4;
	private int STATE_COMPLETE = STATE_LOGIN | STATE_INIT;

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

	/**
	 * 启动加载升级配置信息
	 */
	private void doGetUpdateInfo() {
		mTask = new GetUpdateInfoTask();
		mTask.setListener(this);
		mTask.execute();
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

	public void onBackPressed() {

		super.onBackPressed();
		exit(App.sContext);
	}

	private boolean isComplete() {
		if (mState == STATE_COMPLETE)
			return true;
		return false;
	}

	private void checkAutoLogin() {
		final String account = getPreferences().getUserName();
		final String password = getPreferences().getPassword();
		if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) || !getPreferences().isAutoLogin()) {
			goLoginPage();
		} else {
			TwoButtonDialog autoDialog = new TwoButtonDialog(this, "是否自动登录？", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					startAutoLogin();
				}
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					// 消除密码和自动登录
					getPreferences().saveAutoLogin(false);
					getPreferences().saveUserInfo(getPreferences().getUserName(), null);
					goLoginPage();
				}
			}, "是", "否");
			autoDialog.setCancelable(false);
			autoDialog.show();
		}
	}

	/**
	 * 启动自动登录
	 */
	protected void startAutoLogin() {
		mTask = new CheckLoginTask().execute(getPreferences().getUserName(), getPreferences().getPassword(), HardwareUtils.getPhoneIMSI(this),
				HardwareUtils.getPhoneIMEI(getThis()), this);
	}

	private void goNextPage() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	private void goLoginPage() {
		getPreferences().setCheckedNumber(null);
		startActivityForResult(new Intent(this, LoginActivity.class), ActivityUtils.GO_LOGIN_REQUEST_CODE);
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
			mState |= STATE_INIT;
			if (result != TaskResult.OK) {
				checkAutoLogin();
			}
		} else if (task instanceof CheckLoginTask) {
			if (result == TaskResult.OK) {
				mState |= STATE_LOGIN;
			} else {
				getPreferences().saveAutoLogin(false);
				getPreferences().saveUserInfo(getPreferences().getUserName(), null);
				getPreferences().setSubAccount(null);
				goLoginPage();
			}
		} else if (task instanceof GetTabInfosTask) {
			mState |= STATE_UPDATE_TABS;
		}

		if (isComplete())
			goNextPage();
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (task instanceof GetUpdateInfoTask) {
			dealUpdateInfos((ArrayList<UpdateInfo>) param);
		} else if (task instanceof CheckLoginTask) {

		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		exit(this);
	}

	/**
	 * 处理升级信息
	 * 
	 * @param param
	 */
	private void dealUpdateInfos(ArrayList<UpdateInfo> datas) {
		if (datas == null || datas.size() == 0)
			return;

		for (final UpdateInfo updateInfo : datas) {
			int type = updateInfo.getType();
			switch (type) {
			case UpdateInfo.TYPE_APK:
				dealUpdateApkInfo(datas, updateInfo, type);
				break;
			case UpdateInfo.TYPE_MENUS:
				updateAllBusinessMenu(updateInfo);
				break;
			case UpdateInfo.TYPE_TAB_INFOS:
				updateTabInfos(updateInfo);
				break;
			default:
				break;
			}

		}
	}

	private void updateTabInfos(UpdateInfo updateInfo) {
		long oldVersion = getPreferences().getResOldVersion(String.valueOf(updateInfo.getType()), 0);

		if (oldVersion >= updateInfo.getNewVersionCode())
			return;

		STATE_COMPLETE |= STATE_UPDATE_TABS;

		GenericTask task = new GetTabInfosTask(this, updateInfo);
		task.setListener(this);
		task.execute();
	}

	private void updateAllBusinessMenu(UpdateInfo updateInfo) {
		int type = updateInfo.getType();
		long newVersion = updateInfo.getNewVersionCode();
		long oldVersion = getPreferences().getResOldVersion(String.valueOf(type), 0);
		if (newVersion > oldVersion) {
			doGetAllMenus(updateInfo);
		}
	}

	private void doGetAllMenus(UpdateInfo updateInfo) {
		String url = updateInfo.getDownloadUrl();
		if (url != null) {
			GenericTask getAllMenusTask = new GetAllMenusTask(this, updateInfo);
			getAllMenusTask.execute();
		}
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
		if (newVersion > App.sAppCode) {
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
}
