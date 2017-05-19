package com.sunrise.marketingassistant.activity;

import java.util.ArrayList;
import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.service.UpdateService;
import com.sunrise.marketingassistant.task.CheckLoginTask;
import com.sunrise.marketingassistant.task.GetAppQuotaAllCity;
import com.sunrise.marketingassistant.task.GetCommResTask;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetTabInfosTask;
import com.sunrise.marketingassistant.task.GetUpdateInfoTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.utils.HardwareUtils;
import com.sunrise.marketingassistant.view.TwoButtonDialog;
import com.sunrise.javascript.utils.ActivityUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;

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
	private final int STATE_UPDATE_COMMRES = 8;
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
			CommUtil.showAlert(this, null, getString(R.string.frame_loading_fail), getString(R.string.exit), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					exit(StartActivity.this);
				}
			});
			return;
		}

		// if (doTestWeb())
		// return;

		doGetUpdateInfo();

		// checkAutoLogin();
	}

	// private boolean doTestWeb() {
	//
	// if (!App.isTest)
	// return false;
	//
	// try {
	// final String FILE_NAME_TABINFO = "tabInfo.txt";
	// if (FileUtils.fileIsExist(ExtraKeyConstant.APP_SD_PATH_NAME,
	// FILE_NAME_TABINFO)) {
	// String jsonStr =
	// FileUtils.readToStringFormFile(FileUtils.getAbsPath(ExtraKeyConstant.APP_SD_PATH_NAME,
	// FILE_NAME_TABINFO));
	// JSONArray jsarray = new JSONObject(jsonStr).getJSONArray("datas");
	// TabContent temp = JsonUtils.parseJsonStrToObject(jsarray.getString(0),
	// TabContent.class);
	// Intent intent = SingleFragmentActivity.createIntent(this,
	// WebViewFragment2.class, temp.getZipContent(), temp.getTabName(),
	// temp.getLastModify(), null);
	// startActivity(intent);
	// finish();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	//
	// return true;
	// }

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
		if (requestCode == ActivityUtils.GO_LOGIN_REQUEST_CODE) {
			if (resultCode != RESULT_OK) {
				exit(this);
			} else {
				doCompleteOneStep(STATE_LOGIN);
			}
		} else
			super.onActivityResult(requestCode, resultCode, intent);
	}

	public void onBackPressed() {

		super.onBackPressed();
		exit(App.sContext);
	}

	private void doCompleteOneStep(int step) {
		mState |= step;

		LogUtlis.i(StartActivity.class.getSimpleName(), "doCompleteStep(" + step + " ====> " + mState + ":" + STATE_COMPLETE);
		if (mState == STATE_COMPLETE)
			goNextPage();
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
			doCompleteOneStep(STATE_INIT);
			if (result != TaskResult.OK) {
				// checkAutoLogin();
				doAlertOperationInfoError(task.getException().getMessage());
			}
		} else if (task instanceof CheckLoginTask) {
			if (result == TaskResult.OK) {
				doCompleteOneStep(STATE_LOGIN);
			} else {
				getPreferences().saveAutoLogin(false);
				getPreferences().saveUserInfo(getPreferences().getUserName(), null);
				getPreferences().saveSubAccount(null);
				goLoginPage();
			}
		} else if (task instanceof GetTabInfosTask) {
			doCompleteOneStep(STATE_UPDATE_TABS);
		} else if (task instanceof GetCommResTask) {
			doCompleteOneStep(STATE_UPDATE_COMMRES);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (task instanceof GetUpdateInfoTask) {
			dealUpdateInfos((ArrayList<UpdateInfo>) param);
		} else if (task instanceof CheckLoginTask) {

		} else if (task instanceof GetTabInfosTask) {
		} else if (task instanceof GetCommResTask) {
			GetCommResTask t = (GetCommResTask) task;
			Preferences.getInstance(App.sContext).putResNewVersion(String.valueOf(UpdateInfo.TYPE_COMM_RES), t.updateInfo.getNewVersionCode());
		} else if (task instanceof GetAppQuotaAllCity) {
			getPreferences().putCacheString(ExtraKeyConstant.KEY_SHARE_OF_CHANNEL_ALL_CITYS, param.toString());
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
			case UpdateInfo.TYPE_TAB_INFOS:
				updateTabInfos(updateInfo);
				break;
			case UpdateInfo.TYPE_MAX_SIGN_RANGE:
				getPreferences().saveMaxSignRange(updateInfo.getNewVersionCode());
				break;
			case UpdateInfo.TYPE_COMM_RES:
				updateCommRes(updateInfo);
				break;
			default:
				break;
			}
		}
	}

	private void updateCommRes(UpdateInfo updateInfo) {
		long oldVersion = getPreferences().getResNewVersion(String.valueOf(updateInfo.getType()), 0);

		long newVersion = updateInfo.getNewVersionCode();
		// try {
		// newVersion = DateUtils.string2Long(updateInfo.getNewVersionCode(),
		// ExtraKeyConstant.FORMAT_PARAM_TIME);
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		if (oldVersion >= newVersion)
			return;

		STATE_COMPLETE |= STATE_UPDATE_COMMRES;

		new GetCommResTask().execute(updateInfo, this);
	}

	private void updateTabInfos(UpdateInfo updateInfo) {
		long oldVersion = getPreferences().getResNewVersion(String.valueOf(updateInfo.getType()), 0);

		long newVersion = updateInfo.getNewVersionCode();
		// try {
		// newVersion = DateUtils.string2Long(updateInfo.getNewVersionCode(),
		// ExtraKeyConstant.FORMAT_PARAM_TIME);
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		if (oldVersion >= newVersion) {
			return;
		}
		STATE_COMPLETE |= STATE_UPDATE_TABS;
		GenericTask task = new GetTabInfosTask(this, updateInfo);
		task.setListener(this);
		task.execute();
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
		// try {
		// newVersion = DateUtils.string2Long(updateInfo.getNewVersionCode(),
		// ExtraKeyConstant.FORMAT_PARAM_TIME);
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		int updateType = updateInfo.getUpdateType();
		getPreferences().putResNewVersion(String.valueOf(type), newVersion);
		getPreferences().putString(ExtraKeyConstant.KEY_APP_DOWNLOAD_INFO, JsonUtils.writeObjectToJsonStr(updateInfo));

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
		}, getString(R.string.exit), getString(R.string.update_now));
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
			mDialogCertain = new TwoButtonDialog(this, "正在程序更新中, 是否继续？", new DialogInterface.OnClickListener() {

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
		}
		mDialogCertain.show();
	}

	private void doAlertOperationInfoError(String errorInfo) {
		// CommUtil.showAlert(this, null, "配置信息异常:" + errorInfo,
		// getString(R.string.exit), false, new
		// DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface arg0, int arg1) {
		// arg0.dismiss();
		// CommUtil.exit(StartActivity.this);
		// }
		// });
		new TwoButtonDialog(this, "配置信息异常:" + errorInfo, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				checkAutoLogin();
			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				CommUtil.exit(StartActivity.this);
			}
		}, "忽略异常", getString(R.string.exit)).show();
	}

}
