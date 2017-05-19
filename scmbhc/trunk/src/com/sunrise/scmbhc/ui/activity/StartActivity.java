package com.sunrise.scmbhc.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.UpdateInfo;
import com.sunrise.scmbhc.service.UpdateService;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetNoNameTokenTask;
import com.sunrise.scmbhc.task.GetUpdateInfosTask;
import com.sunrise.scmbhc.task.LoadUserTrafficTask;
import com.sunrise.scmbhc.task.LoginTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.view.DefaultProgressDialog;
import com.sunrise.scmbhc.ui.view.TwoButtonDialog;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.HardwareUtils;

public class StartActivity extends Activity implements ExtraKeyConstant {

	private ArrayList<UpdateInfo> strupdateInfo;
	private TwoButtonDialog mDialogCertain;
	private TwoButtonDialog mDialogNet;
	private boolean isBusinessGuidesUpdate;

	public static final String ON_APP_INIT_FINISH = "com.scmbhc.ON_APP_INIT_FINISH";

	private DefaultProgressDialog mDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_layout);
		((TextView) findViewById(R.id.version)).setText("v" + App.sAPKVersionName);
		CommUtil.addActivity(this);

		App.sSettingsPreferences.cleanUserOtherData();

		initDialog(false, false, null);
	}

	protected void onStart() {
		super.onStart();
		if (CommUtil.isOpenNetwork(this)) {// 判断是否网络通畅，不畅前往设置
			getUpdateInfo();
			onGetNonameToken();
		} else
			showCheckNetDialog();

	}

	public void OnResume() {
		super.onResume();
	}

	private void getUpdateInfo() {

		showDialog("获取新版本信息……");

		GenericTask getUpdateInfos = new GetUpdateInfosTask();
		getUpdateInfos.setListener(new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				dealUpateInfo((ArrayList<UpdateInfo>) param);
			}

			@Override
			public void onPreExecute(GenericTask task) {
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				if (result != TaskResult.OK)
					autoLogin();
			}

			@Override
			public void onCancelled(GenericTask task) {
			}

			@Override
			public String getName() {
				return null;
			}
		});
		getUpdateInfos.execute();
		App.sTaskManager.addObserver(getUpdateInfos);
	}

	private void dealUpateInfo(ArrayList<UpdateInfo> datas) {
		for (final UpdateInfo updateInfo : datas) {
			int type = updateInfo.getType();
			if (type != UpdateInfo.TYPE_APK)
				continue;

			switch (type) {
			case UpdateInfo.TYPE_APK:
				dealUpdateApkInfo(datas, updateInfo, type);
				break;
			default:
				break;
			}

			return;
		}

		autoLogin();

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
		App.sSettingsPreferences.putResNewVersion(String.valueOf(type), newVersion);

		datas.remove(updateInfo);
		strupdateInfo = datas;

		// 更新APK信息
		if (newVersion > App.sAPKVersionCode) {
			dismissDialog();
			if (updateType == UpdateInfo.TYPE_FORCE_UPDATE) { // 强制更新
				showForceUpdateApkDialog(updateInfo);
			} else {
				showUpdateApkDialog(updateInfo);
			}
		} else
			autoLogin();
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
				App.sContext.onDestroy();
			}
		}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				startDownLoadApk(updateInfo);
			}
		}, getString(R.string.exitApp), getString(R.string.update_now));
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
				autoLogin();
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

	private boolean checkNeedGuide() {
		// Preferences preferences=cmucApplication.getSettingsPreferences();
		showDialog("检查导航信息……");
		boolean needGuide = UserInfoControler.getInstance().isNeedGuide();
		Intent intent;
		if (needGuide || isBusinessGuidesUpdate) {
			intent = new Intent(this, GuideActivity.class);
			Bundle extras = new Bundle();
			extras.putParcelableArrayList("updateinfos", strupdateInfo);
			if (needGuide) {
				extras.putInt(App.ExtraKeyConstant.KEY_DISPLAY_GUIDE_TYPE, GuideActivity.AFTER_START_DISPLAY_GUIDE_TYPE);
			} else {
				extras.putInt(App.ExtraKeyConstant.KEY_DISPLAY_GUIDE_TYPE, GuideActivity.DISPLAY_BUSINESS_GUIDE_TYPE);
				extras.putBoolean(App.ExtraKeyConstant.KEY_IS_BUSINESS_GUIDES_UPDATED, isBusinessGuidesUpdate);
			}
			intent.putExtras(extras);
			startActivity(intent);
			finish();
		} else {
			goMain();
		}

		return needGuide;
	}

	private void autoLogin() {
		showDialog("检查自动更新……");
		String pwd = App.getPwd();
		boolean isAutoLogin = UserInfoControler.getInstance().isAutoLogin();
		if (isAutoLogin && TextUtils.isEmpty(pwd)) {
			login();
			// onStartPushMessage();
		} else
			checkNeedGuide();
	}

	/**
	 * 询问更新是否继续
	 */
	private void showTipDialog() {

		if (mDialogCertain == null) {
			mDialogCertain = new TwoButtonDialog(this, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Intent i = new Intent(Intent.ACTION_MAIN);
					// android123提示如果是服务里调用，必须加入new task标识
					// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// i.addCategory(Intent.CATEGORY_HOME);
					// startActivity(i);
					// arg0.dismiss();
					autoLogin();
				}
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					finish();
					App.sContext.onDestroy();
				}
			});
			mDialogCertain.setMessage("正在程序更新中, 是否继续？");
		}
		mDialogCertain.show();
	}

	/**
	 * 设置网络对话框
	 */
	private void showCheckNetDialog() {

		if (mDialogNet == null) {
			mDialogNet = new TwoButtonDialog(this, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent("/");
					ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.Settings");
					intent.setComponent(cm);
					intent.setAction("android.intent.action.VIEW");
					try {
						startActivityForResult(intent, 0);
					} catch (SecurityException e) {
						intent = new Intent("android.settings.WIRELESS_SETTINGS");
						startActivityForResult(intent, 0);
					}
					arg0.dismiss();
				}
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					finish();
					App.sContext.onDestroy();
				}
			});
			mDialogNet.setMessage("设置网络后再使用,是否设置？");
		}

		if (!mDialogNet.isShowing()) {
			mDialogNet.show();
		}

	}

	public void onBackPressed() {
	}

	private void login() {
		showDialog("自动登录中……");
		String phoneNumber = UserInfoControler.getInstance().getUserName();
		if (!TextUtils.isEmpty(phoneNumber)) {
			String servicepassword = UserInfoControler.getInstance().getPassword();
			String pwd = App.getPwd();
			boolean isout = App.sSettingsPreferences.getLoginOutCode();
			boolean isAutoLogin = UserInfoControler.getInstance().isAutoLogin();

			if (servicepassword == null && (!isout)) {
				servicepassword = pwd;
			}

			LoginTask.execute(phoneNumber, servicepassword, true, isAutoLogin, isAutoLogin, new TaskListener() {

				@Override
				public void onProgressUpdate(GenericTask task, Object param) {
					TaskParams params = (TaskParams) param;
					String phoneNumber = params.getString(KEY_PHONE_NUMBER);
					String password = params.getString(KEY_PASSWORD);

					boolean savePassword = (Boolean) params.get(KEY_SAVE_PASSWORD);
					boolean autoLogin = (Boolean) params.get(KEY_AUTO_LOGIN);

					UserInfoControler.getInstance().clean();
					UserInfoControler.getInstance().loginIn(phoneNumber, savePassword ? password : null, (String) params.get(KEY_TOKEN));
					UserInfoControler.getInstance().setAutoLogin(autoLogin);
				}

				@Override
				public void onPreExecute(GenericTask task) {

				}

				@Override
				public void onPostExecute(GenericTask task, TaskResult result) {
					if (TaskResult.OK != result) {
						checkNeedGuide();
						onStartPushMessage();
						Toast.makeText(StartActivity.this, R.string.autoLoginFailed, Toast.LENGTH_LONG).show();
					} else {
						doInit();
					}

				}

				@Override
				public void onCancelled(GenericTask task) {

				}

				@Override
				public String getName() {
					return null;
				}
			});
		}
	}

	/**
	 * 加载用户信息
	 */
	private void doInit() {
		showDialog("获取用户基本信息……");
		checkNeedGuide();
		new LoadUserTrafficTask().execute();
	}

	private void goMain() {
		showDialog("完成……");
		Intent intent = new Intent(this, MainActivity.class);
		Bundle extras = new Bundle();
		extras.putParcelableArrayList("updateinfos", strupdateInfo);
		intent.putExtras(extras);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		dismissDialog();
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

		// try {
		// CommUtil.downloadApk(this, downLoadUrl, true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	protected void initDialog(boolean cancelAble, boolean cancelOnTouchOutside, OnCancelListener cancellistener) {
		// if (mDialog == null) {
		// mDialog = new DefaultProgressDialog(this);
		// }
		// mDialog.setCancelable(cancelAble);
		// mDialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
		// mDialog.setOnCancelListener(cancellistener);
	}

	protected void showDialog(CharSequence text) {
		if (mDialog != null) {
			mDialog.setMessage(text);
			if (!mDialog.isShowing())
				mDialog.show();
		}
	}

	protected void dismissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	/**
	 * 启动消息推送
	 */
	private void onStartPushMessage() {
		if (App.getServiceManager() != null) {
			App.getServiceManager().stopService();
			App.setServiceManager(null);
		}
		App.setServiceManager(CommUtil.startPushMessageService(this));
	}

	/**
	 * 获取匿名token方法
	 */
	private void onGetNonameToken() {
		String mac = HardwareUtils.getMacAddress(this);
		String netType = HardwareUtils.getNetWorkType(this);
		String location = HardwareUtils.getLocation(this);
		String imei = HardwareUtils.getPhoneIMEI(this);
		String imsi = HardwareUtils.getPhoneIMSI(this);
		GetNoNameTokenTask.excuet(imei, imsi, mac, netType, null);
	}
}
