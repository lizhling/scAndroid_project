package com.starcpt.cmuc.ui.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.AppMenu;
import com.starcpt.cmuc.model.bean.UpdateInfoBean;
import com.starcpt.cmuc.service.DownApkService;
import com.starcpt.cmuc.service.LockScreenService;
import com.starcpt.cmuc.service.DownApkService.MyBinder;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.GetTopDataTask;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.task.UpdateApkTask;
import com.starcpt.cmuc.ui.activity.CommonActions.OnTwoBtnDialogHandler;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.starcpt.cmuc.utils.FileUtils;
import com.starcpt.cmuc.utils.HttpDownLoader;
import com.sunrise.javascript.JavaScriptConfig;

public class StartActivity extends Activity {
	// private static final String TAG = "StartActivity";
	private static final int HANDLER_NETWORK_SETTING_MSG = 0;
	private static final int HANDLER_DOWN_APK_SUCCESS = 1;
	private static final int HANDLER_DOWN_APK_FAIL = 2;
	private ImageView mAnimImageView;
	private TextView mStatusView;
	private AnimationDrawable mAnimDrawable;
	private ImageView mAppNameView;
	// private LinearLayout mSplashBgView;
	// private ImageView mLogoView;
	private UpdateApkTask mCheckApkUpdateTask;
	private UpdateCommonResTask mUpdateCommonResTask;
	private int runningTaskIndex = CmucApplication.UPDATE_APK_TASK_INDEX;
	private boolean finishCheckNet;
	public Bitmap sSplashImage;
	public Bitmap sLogoImage;
	private ProgressDialog mDownApkProgressDialog;
	private boolean pauseUpdateDownPd = false;
	private DownApkService mDownApkService;
	private boolean isBusinessGuidesUpdate = false;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NETWORK_SETTING_MSG:
				checkNetSetting();
				break;
			case HANDLER_DOWN_APK_SUCCESS:
				doUpdateCommonResVersion();
				break;
			case HANDLER_DOWN_APK_FAIL:
				mDownApkProgressDialog.setMessage(getString(R.string.down_apk_failed));
				mDownApkProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(R.string.re_try);
				mDownApkProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startDownApkService();
						updateDownApkProgress();
						mDownApkProgressDialog.setMessage(getString(R.string.down_apk_progress));
						mDownApkProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(R.string.background_donwn_apk);
						v.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dismissDownApkProgress();

							}
						});
					}
				});
				mDownApkProgressDialog.setProgress(0);
			}
		};
	};

	private TaskListener mUpdateApkListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			mStatusView.setText((String) param);
		}

		@Override
		public void onPreExecute(GenericTask task) {

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// TODO Auto-generated method stub
			if (result == TaskResult.OK) {
				runningTaskIndex = -1;
				checkApkUpdaeSuccess();
			} else {
				checkApkUpdateFailed(task);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "Update";
		}
	};

	private TaskListener mUpdateCommonResListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub
			mStatusView.setText((String) param);
		}

		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// TODO Auto-generated method stub
			if (result == TaskResult.OK) {
				runningTaskIndex = -1;
				updateCommonResSucess();
			} else {
				updateCommonResFailed(task);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "UpdateCommonRes";
		}
	};

	private TaskListener mGetTopListDataListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			mStatusView.setText((String) param);
		}

		@Override
		public void onPreExecute(GenericTask task) {
			startAnimation();
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// TODO Auto-generated method stub
			stopAnimation();
			if (result == TaskResult.OK) {
				runningTaskIndex = -1;
				onGetTopListDataSuccess();
			} else {
				onGetTopListDataFailed(task);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private CmucApplication cmucApplication;
	private GetTopDataTask mGetTopListDataTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cmucApplication = (CmucApplication) getApplicationContext();
		CommonActions.setScreenOrientation(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		registerSkinChangedReceiver();
		initSplash();
		cmucApplication.setApplicationRunning(true);
		mAnimImageView = (ImageView) findViewById(R.id.start_anima);
		mStatusView = (TextView) findViewById(R.id.start_status);
		mAnimDrawable = (AnimationDrawable) mAnimImageView.getDrawable();
		mAppNameView = (ImageView) findViewById(R.id.splash_app_name);
		mAppNameView.setImageResource(cmucApplication.getAppSplashNameId());

		TextView mAppNameText = (TextView) findViewById(R.id.splash_app_name_t);
		mAppNameText.setText(cmucApplication.getAppSplashNameTextId());

		((TextView) findViewById(R.id.version_name)).setText("V " + CmucApplication.getApkVersionName(this));
		CommonActions.addActivity(this);
		if (FileUtils.sdCardIsExist()) {
			createFileStoreDir();
			FileUtils.updateSdPath();
		}
		setSkin();
		CmucApplication.sContext.startService(new Intent(CmucApplication.sContext, LockScreenService.class));
		clearGuideDrawables();
	}

	protected void onGetTopListDataFailed(GenericTask task) {

		// 当营销助手的时候，重试就是重新进入登录操作
		if (cmucApplication.getAppTag().equals(CmucApplication.YXZS_APP_TAG)) {
			CommonActions.createTwoBtnMsgDialog(this, null, task.getException().getMessage(), getString(R.string.re_login), getString(R.string.exit),
					new CommonActions.OnTwoBtnDialogHandler() {

						@Override
						public void onPositiveHandle(Dialog dialog, View v) {
							dialog.dismiss();
							cmucApplication.getSettingsPreferences().saveAuthentication(null);
							goNextActivity();
						}

						@Override
						public void onNegativeHandle(Dialog dialog, View v) {
							dialog.dismiss();
							CommonActions.exitClient(StartActivity.this);
						}
					}, false);
		} else
			handleTaskFailed(task);

	}

	private void onGetTopListDataSuccess() {
		goMainPage();
		if (cmucApplication.getServiceManager() != null) {
			cmucApplication.getServiceManager().stopService();
			cmucApplication.setServiceManager(null);
		}
		cmucApplication.setServiceManager(CommonActions.startPushMessageService(this));
	}

	private void goMainPage() {
		Intent intent = new Intent(this, MainTabActivity.class);
		startActivity(intent);
		finish();
	}

	private void clearGuideDrawables() {
		cmucApplication.getFunctionGuideDrawables().clear();
		cmucApplication.getBusinessGuideDrawables().clear();
		cmucApplication.getOpreGuideDrawables().clear();
	}

	private void dismissDownApkProgress() {
		mDownApkProgressDialog.dismiss();
		doUpdateCommonResVersion();
		unBindDownApkService();
	}

	private void setSkin() {
		SkinManager.setSkin(this, null, ViewEnum.StartActivity);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finishCheckNet = true;
		cancleAllTask();
	}

	private void initSplash() {
		if (FileUtils.fileIsExist(CmucApplication.APP_FILE_COMMON_RES_IMAGES_OTHER_DATA_DIR, "splash.jpg")) {
			sSplashImage = FileUtils.readBitmapFormFile(FileUtils.getAbsPath(CmucApplication.APP_FILE_COMMON_RES_IMAGES_OTHER_DATA_DIR, "splash.jpg"));

		}
		if (FileUtils.fileIsExist(CmucApplication.APP_FILE_COMMON_RES_IMAGES_OTHER_DATA_DIR, "logo.png")) {
			sLogoImage = FileUtils.readBitmapFormFile(FileUtils.getAbsPath(CmucApplication.APP_FILE_COMMON_RES_IMAGES_OTHER_DATA_DIR, "logo.png"));
		}
	}

	private void checkNetSetting() {
		if (isOpenNetwork()) {
			retryTask();
		} else {
			stopAnimation();
			CommonActions.createNetSettingDialog(this);
		}
	}

	private void startWaitThread() {
		finishCheckNet = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long startTime = System.currentTimeMillis();
				long running = startTime + 8 * 1000;
				while (System.currentTimeMillis() <= running) {
					if (isOpenNetwork()) {
						mHandler.sendEmptyMessage(HANDLER_NETWORK_SETTING_MSG);
						finishCheckNet = true;
						break;
					}
					if (finishCheckNet)
						break;
				}
				if (!finishCheckNet) {
					mHandler.sendEmptyMessage(HANDLER_NETWORK_SETTING_MSG);
					finishCheckNet = true;
				}
			}

		}).start();
		startAnimation();
	}

	private void createSdCardWarnDialog() {
		CommonActions.createSingleBtnMsgDialog(this, null, getString(R.string.sd_card_error), getString(R.string.confirm), new OnTwoBtnDialogHandler() {

			@Override
			public void onPositiveHandle(Dialog dialog, View v) {
				CommonActions.exitClient(StartActivity.this);
			}

			@Override
			public void onNegativeHandle(Dialog dialog, View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void createFileStoreDir() {
		FileUtils.createSDDir(CmucApplication.APP_FILE_ROOT_DIR);
		FileUtils.createSDDir(CmucApplication.APP_FILE_APK_DIR);
		FileUtils.createSDDir(CmucApplication.APP_FILE_IMAGE_DATA_DIR);
		FileUtils.createSDDir(CmucApplication.APP_FILE_HTML_DATA_DIR);
		FileUtils.createSDDir(CmucApplication.APP_CAPTURE_IMAGE_DIR);
		FileUtils.createSDDir(CmucApplication.APP_CRASH_LOG_DIR);
		FileUtils.createSDDir(CmucApplication.APP_SKIN_CACHE_DIR);
		FileUtils.createSDDir(CmucApplication.APP_SKIN_INSTALLED_DIR);
		FileUtils.createSDDir(CmucApplication.APP_BUSINESS_GUIDE_DIR);
		JavaScriptConfig.setCaptureImageDir(FileUtils.getAbsPathOfDir(CmucApplication.APP_CAPTURE_IMAGE_DIR));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean sdStaus = FileUtils.sdCardIsExist();
		if (!sdStaus) {
			createSdCardWarnDialog();
		} else {
			startWaitThread();
		}
		if (CmucApplication.sNeedShowLock) {
			CommonActions.showLockScreen(this);
		}
	}

	private boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	private void startAnimation() {
		if (!mAnimDrawable.isRunning())
			mAnimDrawable.start();
	}

	private void stopAnimation() {
		if (mAnimDrawable.isRunning())
			mAnimDrawable.stop();
	}

	private void doCheckApkUpdate() {
		if (mCheckApkUpdateTask != null)
			mCheckApkUpdateTask.cancle();
		mCheckApkUpdateTask = new UpdateApkTask(this);
		mCheckApkUpdateTask.setListener(mUpdateApkListener);
		String curVersion = CmucApplication.getApkVersion() + "";
		TaskParams params = new TaskParams();
		params.put(UpdateApkTask.CURRENT_VERSION_KEY, curVersion);
		String checkType = UpdateInfoBean.HAVE_DEVICE_CHECK_TYPE;
		if (!JavaScriptConfig.sIsSupportThirdParthDevice)
			checkType = UpdateInfoBean.NO_DEVICE_CHECK_TYPE;
		params.put(UpdateApkTask.CHECK_TYPE_KEY, checkType);
		runningTaskIndex = CmucApplication.UPDATE_APK_TASK_INDEX;
		mCheckApkUpdateTask.execute(params);
	}

	private void checkApkUpdaeSuccess() {
		stopAnimation();
		startDownloadApk();
	}

	private void checkApkUpdateFailed(GenericTask task) {
		handleTaskFailed(task);
	}

	private void startDownloadApk() {
		if (mCheckApkUpdateTask.getUpdateApkInfo().getUpdateType() == UpdateInfoBean.FORCE_UPDATE) {
			doDownloadApk();
		} else if (mCheckApkUpdateTask.getUpdateApkInfo().getUpdateType() == UpdateInfoBean.SELECT_UPDATE)
			createUpdateApkVersionDialog();
		else {
			doUpdateCommonResVersion();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterSkinChangedReceiver();
		unBindDownApkService();
	}

	private void createUpdateApkVersionDialog() {
		CommonActions.createTwoBtnMsgDialog(this, getString(R.string.update_version_title), getString(R.string.update_version_info), null, null,
				new CommonActions.OnTwoBtnDialogHandler() {

					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						doDownloadApk();
						dialog.dismiss();
					}

					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						doUpdateCommonResVersion();
						dialog.dismiss();
					}
				}, false);
	}

	private void doUpdateCommonResVersion() {
		if (mUpdateCommonResTask != null)
			mUpdateCommonResTask.cancle();
		mUpdateCommonResTask = new UpdateCommonResTask();
		mUpdateCommonResTask.setListener(mUpdateCommonResListener);
		runningTaskIndex = CmucApplication.UPDATE_COMMOM_RES_TASK_INDEX;
		startAnimation();
		mUpdateCommonResTask.execute();
	}

	private void updateCommonResSucess() {
		stopAnimation();
		loadGuideDrawables();
		goNextActivity();
	}

	private void loadGuideDrawables() {
		for (int i = 0; i < CmucApplication.sFunctionGuideDrawableIds.length; i++) {
			Drawable drawable = getResources().getDrawable(CmucApplication.sFunctionGuideDrawableIds[i]);
			cmucApplication.getFunctionGuideDrawables().add(drawable);
		}

		for (int i = 0; i < CmucApplication.sOpreGuideDrawableIds.length; i++) {
			Drawable drawable = getResources().getDrawable(CmucApplication.sOpreGuideDrawableIds[i]);
			cmucApplication.getOpreGuideDrawables().add(drawable);
		}

		ArrayList<File> files = FileUtils.getFileListOfDir(CmucApplication.APP_BUSINESS_GUIDE_DIR);

		for (int i = 0; i < files.size(); i++) {
			Bitmap bitmap = BitmapFactory.decodeFile(files.get(i).getAbsolutePath());
			BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
			cmucApplication.getBusinessGuideDrawables().add(bitmapDrawable);
		}

	}

	private void updateCommonResFailed(GenericTask task) {
		handleTaskFailed(task);
	}

	private void handleTaskFailed(GenericTask task) {
		// TODO Auto-generated method stub
		CommonActions.createTwoBtnMsgDialog(this, null, task.getException().getMessage(), getString(R.string.re_try), getString(R.string.exit),
				new CommonActions.OnTwoBtnDialogHandler() {

					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						retryTask();
						startAnimation();
						dialog.dismiss();
					}

					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						dialog.dismiss();
						CommonActions.exitClient(StartActivity.this);
					}
				}, false);
	}

	private void goNextActivity() {
		Preferences preferences = cmucApplication.getSettingsPreferences();
		boolean needGuide = preferences.isNeedGuide();
		Intent intent;
		if (needGuide || isBusinessGuidesUpdate) {
			intent = new Intent(this, GuideActivity.class);
			if (needGuide) {
				intent.putExtra(CmucApplication.DISPLAY_GUIDE_TYPE, GuideActivity.AFTER_START_DISPLAY_GUIDE_TYPE);
			} else {
				intent.putExtra(CmucApplication.DISPLAY_GUIDE_TYPE, GuideActivity.DISPLAY_BUSINESS_GUIDE_TYPE);
				intent.putExtra(CmucApplication.IS_BUSINESS_GUIDES_UPDATED, isBusinessGuidesUpdate);
			}
			if (needGuide)
				preferences.saveNeedGuide(false);
		} else {
			if (cmucApplication.getAppTag().equals(CmucApplication.YXZS_APP_TAG)) {
				if (!TextUtils.isEmpty(preferences.getAuthentication())) {
					doGetTopList();
					return;
				}
			}
			intent = new Intent(this, LoginActivity.class);
		}
		startActivity(intent);
		finish();
	}

	private void doGetTopList() {
		if (mGetTopListDataTask != null)
			mGetTopListDataTask.cancle();
		mGetTopListDataTask = new GetTopDataTask(this);
		mGetTopListDataTask.setListener(mGetTopListDataListener);
		runningTaskIndex = CmucApplication.GET_TOP_LIST_DATA_TASK_INDEX;
		mGetTopListDataTask.execute();
	}

	private boolean mBindDownApkService = false;

	private void doDownloadApk() {
		startDownApkService();
		if (!mBindDownApkService) {
			bindDownApkServiceService();
			mBindDownApkService = true;
		}
		Toast.makeText(StartActivity.this, R.string.down_apk_toast, Toast.LENGTH_LONG).show();
		createDownApkProgress();
	}

	private void startDownApkService() {
		Intent intent = new Intent();
		intent.setClass(this, DownApkService.class);
		intent.putExtra(CmucApplication.DOWNLOAD_APK_URL_EXTRAL, mCheckApkUpdateTask.getUpdateApkInfo().getDownloadUrl());
		intent.putExtra(CmucApplication.NOTIFICATION_ID_EXTRAL, DownApkService.UPDATE_APK_NOTIFICATION_ID);
		startService(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stopAnimation();
			cancleAllTask();
			createExitAppDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void cancleAllTask() {
		CommonActions.cancleTask(mCheckApkUpdateTask);
		CommonActions.cancleTask(mUpdateCommonResTask);
	}

	private void createExitAppDialog() {
		CommonActions.createTwoBtnMsgDialog(this, getString(R.string.userexit), getString(R.string.confirmexit), getString(R.string.user_exit_yes),
				getString(R.string.user_exit_no), new CommonActions.OnTwoBtnDialogHandler() {

					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						CommonActions.exitClient(StartActivity.this);
					}

					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						retryTask();
						startAnimation();
						dialog.dismiss();
					}
				}, false);
	}

	private void retryTask() {
		if (runningTaskIndex != -1) {
			switch (runningTaskIndex) {
			case CmucApplication.UPDATE_APK_TASK_INDEX:
				doCheckApkUpdate();
				break;
			case CmucApplication.UPDATE_COMMOM_RES_TASK_INDEX:
				doUpdateCommonResVersion();
				break;
			case CmucApplication.GET_TOP_LIST_DATA_TASK_INDEX:
				doGetTopList();
				break;
			}
		}
	}

	class UpdateCommonResTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			Preferences preferences = cmucApplication.getSettingsPreferences();
			String commonResVersion = preferences.getCommonResVersion();
			String businessGuideResVersion = preferences.getBusinessGuideResVersion();
			try {
				publishProgress(getString(R.string.checking_common_res_update));
				updateCommonRes(commonResVersion);
				updateBusinessGuide(businessGuideResVersion);
				updateIdentityVerification();
				return TaskResult.OK;
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			}
		}

		private void updateCommonRes(String commonResVersion) throws HttpException, BusinessException, ClientProtocolException, IOException {
			publishProgress(getString(R.string.downing_common_res));
			UpdateInfoBean updateInfoBean = CmucApplication.sServerClient.getUpdateInfoByOsInfo(commonResVersion, cmucApplication.getOsInfo(),
					UpdateInfoBean.COMMON_RES_CHECK_TYPE, cmucApplication.getAppTag());
			boolean isExistCommonResDir = FileUtils.fileIsExist(CmucApplication.APP_FILE_COMMON_RES_DATA_DIR);
			publishProgress(getString(R.string.importing_common_res));
			if (updateInfoBean.getUpdateType() == UpdateInfoBean.NO_NEED_UPDATE) {
				String downUrl = cmucApplication.getSettingsPreferences().getCommonResUrl();
				if (!isExistCommonResDir) {
					downZipAndUnZip(downUrl, CmucApplication.APP_FILE_HTML_DATA_DIR, CmucApplication.COMMON_RES_ZIP_NAME,
							CmucApplication.APP_FILE_COMMON_RES_DATA_DIR);
				}
			} else {
				if (isExistCommonResDir)
					FileUtils.deleteDirByRelativePath(CmucApplication.APP_FILE_COMMON_RES_DATA_DIR);
				downZipAndUnZip(updateInfoBean.getDownloadUrl(), CmucApplication.APP_FILE_HTML_DATA_DIR, CmucApplication.COMMON_RES_ZIP_NAME,
						CmucApplication.APP_FILE_COMMON_RES_DATA_DIR);
				saveCommonInformation(updateInfoBean);
			}
		}

		private void updateBusinessGuide(String businessGuideResVersion) throws HttpException, BusinessException, ClientProtocolException, IOException {
			UpdateInfoBean updateInfoBean = CmucApplication.sServerClient.getUpdateInfoByOsInfo(businessGuideResVersion, cmucApplication.getOsInfo(),
					UpdateInfoBean.BUSINESSGUIDE_CHECK_TYPE, cmucApplication.getAppTag());
			boolean isExistBusinessGuideResDir = FileUtils.fileIsExist(CmucApplication.APP_BUSINESS_GUIDE_DIR);
			if (updateInfoBean.getUpdateType() == UpdateInfoBean.NO_NEED_UPDATE) {
				String downUrl = cmucApplication.getSettingsPreferences().getBusinessResUrl();
				if (!isExistBusinessGuideResDir) {
					downZipAndUnZip(downUrl, CmucApplication.APP_BUSINESS_GUIDE_TOP_DIR, CmucApplication.BUSINESS_GUIDE_ZIP_NAME,
							CmucApplication.APP_BUSINESS_GUIDE_DIR);
				}
				isBusinessGuidesUpdate = false;
			} else {
				if (isExistBusinessGuideResDir)
					FileUtils.deleteDirByRelativePath(CmucApplication.APP_BUSINESS_GUIDE_DIR);
				downZipAndUnZip(updateInfoBean.getDownloadUrl(), CmucApplication.APP_BUSINESS_GUIDE_TOP_DIR, CmucApplication.BUSINESS_GUIDE_ZIP_NAME,
						CmucApplication.APP_BUSINESS_GUIDE_DIR);
				saveBusinessInformation(updateInfoBean);
				isBusinessGuidesUpdate = true;
			}
		}

		private void updateIdentityVerification() throws HttpException, BusinessException {
			AppMenu appMenu = CmucApplication.sServerClient.getMenuDetail("SMRZ", "" + CmucApplication.IDENTITY_VERIFICATION_MENU_ID,
					cmucApplication.getScreenWidth() + "", cmucApplication.getScreenHeight() + "");
			// AppMenu
			// appMenu=CmucApplication.sServerClient.getMenuDetail("SMRZ10085",
			// "453200", cmucApplication.getScreenWidth()+"",
			// cmucApplication.getScreenHeight()+"");
			cmucApplication.setIdentityVerificationMenu(appMenu);
		}

		private void downZipAndUnZip(String downUrl, String destDir, String destFileName, String unzipDir) throws ClientProtocolException, IOException,
				HttpException {
			HttpDownLoader.downLoadToFile(downUrl, destDir, destFileName);
			FileUtils.unZipCommonRes(unzipDir, destDir, destFileName);
		}

		private void saveCommonInformation(UpdateInfoBean mUpdateInfoBean) {
			Preferences preferences = cmucApplication.getSettingsPreferences();
			preferences.saveCommonResVersion(mUpdateInfoBean.getUpdatedVersion());
			preferences.saveCommonResUrl(mUpdateInfoBean.getDownloadUrl());
		}

		private void saveBusinessInformation(UpdateInfoBean mUpdateInfoBean) {
			Preferences preferences = cmucApplication.getSettingsPreferences();
			preferences.saveBusinessGuideResVersion(mUpdateInfoBean.getUpdatedVersion());
			preferences.saveBusinessGuideResUrl(mUpdateInfoBean.getDownloadUrl());
		}

	}

	private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

		}
	};

	private void registerSkinChangedReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
		registerReceiver(mSkinChangedReceiver, filter);
	}

	private void unRegisterSkinChangedReceiver() {
		unregisterReceiver(mSkinChangedReceiver);
	}

	private void createDownApkProgress() {
		mDownApkProgressDialog = new ProgressDialog(this);
		mDownApkProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDownApkProgressDialog.setMessage(getString(R.string.down_apk_progress));
		mDownApkProgressDialog.setMax(100);
		mDownApkProgressDialog.setIndeterminate(false);
		mDownApkProgressDialog.setCancelable(false);
		mDownApkProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.background_donwn_apk), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismissDownApkProgress();
			}

		});
		mDownApkProgressDialog.show();
		updateDownApkProgress();
	}

	private void updateDownApkProgress() {
		pauseUpdateDownPd = false;
		new Thread() {
			public void run() {
				while (!pauseUpdateDownPd) {
					if (mDownApkService != null) {
						try {
							switch (mDownApkService.getState()) {
							case DownApkService.DOWNLOADING:
								mDownApkProgressDialog.setProgress(mDownApkService.getDownProgress());
								break;
							case DownApkService.DOWNLOAD_SUCCESS:
								mDownApkProgressDialog.dismiss();
								mHandler.sendEmptyMessage(HANDLER_DOWN_APK_SUCCESS);
								pauseUpdateDownPd = true;
								break;
							case DownApkService.DOWNLOAD_FAIL:
								mHandler.sendEmptyMessage(HANDLER_DOWN_APK_FAIL);
								pauseUpdateDownPd = true;
								break;
							default:
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
		}.start();
	}

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder binder = (MyBinder) service;
			mDownApkService = binder.getService();
		}
	};

	private void bindDownApkServiceService() {
		Intent intent = new Intent(this, DownApkService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	private void unBindDownApkService() {
		if (mBindDownApkService) {
			unbindService(conn);
			mBindDownApkService = false;
			mDownApkService = null;
		}
	}
}
