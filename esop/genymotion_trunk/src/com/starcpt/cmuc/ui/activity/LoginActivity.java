package com.starcpt.cmuc.ui.activity;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.Item;
import com.starcpt.cmuc.model.bean.DynamicCodeBean;
import com.starcpt.cmuc.model.bean.LoginBean;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.GetTopDataTask;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.starcpt.cmuc.ui.view.BindIMEIDialog;
import com.starcpt.cmuc.ui.view.BubbleEditText;
import com.starcpt.cmuc.ui.view.SetSesurePasswordDialog;
import com.starcpt.cmuc.utils.AsyncImageLoader;
import com.starcpt.cmuc.utils.AsyncImageLoader.ImageCallBack;
import com.starcpt.cmuc.utils.FileUtils;
import com.sunrise.javascript.utils.ActivityUtils;

public class LoginActivity extends Activity implements OnClickListener {
	// private static final String TAG = "LoginActivity";

	private final String LOGIN_SUCCESS = "success";
	private static final int LOGIN_TASK_INDEX = 0;
	private static final int GET_TOP_LIST_DATA_TASK_INDEX = 1;
	private static final int GET_GETDYNAMICCODE_TASK_INDE = 2;
	private static final long GETDYNAMICCODE_DURATION = 15 * 1000;

	private String mLoginId;
	private String mLoginPassword;
	private String mDynamicCodeStr;
	private String mResultMsg;

	private BubbleEditText mLoginIdView;
	private BubbleEditText mLoginPasswordView;
	private Button mLoginButtonView;
	private BubbleEditText mDynamicCodeView;
	private Button mGetDynamicCodeButtoniew;
	private ImageView mAnimImageView;
	private AnimationDrawable mAnimDrawable;
	private TextView mTitleView;
	private Button mBackView;
	private TextView mLoginStatusView;
	private TextView mImeiView;
	private TextView mIdentityVerificationView;

	private DynamicCodeBean mDynamicCodeBean;
	private long getDynamicCodeStartTime = -1;

	private LoginTask mLoginTask;
	private GetDynamicCodeTask mGetDynamicCodeTask;
	private GetTopDataTask mGetTopListDataTask;
	private int mTaskIndex = -1;

	private String mAuthentication;
	private String mNotificationPassword;
	private String mMobile;
	private boolean isStartByJavascript = false;

	private PopupWindow pupupwindow;
	// private boolean testMessagePush=true;
	private ArrayAdapter<String> mAllUserNamesAdapter;
	
	//private BusinessException businessException;/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian*/

	private TaskListener mLoginListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			mLoginStatusView.setText((String) param);
		}

		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub
			startAnimation();
			updateLoginAllViewState(false);
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			stopAnimation();
			if (result == TaskResult.OK) {
				createLastLoginTimeDialog(mResultMsg);
			} else {
				onLoginFailed(task);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			mLoginStatusView.setText("");
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private TaskListener mGetDynamicCodeListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreExecute(GenericTask task) {
			updateLoginAllViewState(false);
			mGetDynamicCodeButtoniew.setText(R.string.getting_dynamiccode);
			startAnimation();
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// TODO Auto-generated method stub
			updateLoginAllViewState(true);
			if (result == TaskResult.OK) {
				mTaskIndex = -1;
				onGetDynamicCodeSuccess();
			} else {
				onGetDynamicCodeFaild(task);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			mLoginStatusView.setText("");
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private TaskListener mGetTopListDataListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			if (mLoginStatusView != null)
				mLoginStatusView.setText((String) param);
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
				mTaskIndex = -1;
				onGetTopListDataSuccess();
			} else {
				onGetTopListDataFailed(task);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			mLoginStatusView.setText("");
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private CmucApplication cmucApplication;
	private BindIMEIDialog mBingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cmucApplication = (CmucApplication) getApplicationContext();
		CommonActions.setScreenOrientation(this);
		setContentView(R.layout.login);
		checkStartAction();
		initViews();
		initTitleBar();
		CommonActions.addActivity(this);
		setSkin();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(CmucApplication.sNeedShowLock){
			CommonActions.showLockScreen(this);
		}
	}
	/*
	 * public boolean isTabletDevice() { TelephonyManager telephony =
	 * (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); int type
	 * = telephony.getP honeType(); return type ==
	 * TelephonyManager.PHONE_TYPE_NONE; }
	 */

	private void setSkin() {
		SkinManager.setSkin(this, null, ViewEnum.LoginActivity);
	}

	private void initTitleBar() {
		mTitleView = (TextView) findViewById(R.id.top_title_text);
		mBackView = (Button) findViewById(R.id.top_back_btn);
		mTitleView.setText(R.string.user_login);
		mBackView.setVisibility(View.INVISIBLE);
	}

	private void checkStartAction() {
		String action = getIntent().getAction();
		if (action != null) {
			if (action.equals(ActivityUtils.CMUC_LOGIN_ACTION)) {
				isStartByJavascript = true;
			} else
				isStartByJavascript = false;
		} else
			isStartByJavascript = false;
		if (!isStartByJavascript) {
			cmucApplication.clearSubAccounts();
		}
	}

	private void initViews() {
		mLoginIdView = (BubbleEditText) findViewById(R.id.login_id);
		initAllUserNames(null);
		mLoginPasswordView = (BubbleEditText) findViewById(R.id.login_password);
		mLoginButtonView = (Button) findViewById(R.id.btn_login);
		mLoginButtonView.setOnClickListener(this);
		mAnimImageView = (ImageView) findViewById(R.id.login_anima);
		mAnimDrawable = (AnimationDrawable) mAnimImageView.getDrawable();
		mDynamicCodeView = (BubbleEditText) findViewById(R.id.oper_auth_code);
		mGetDynamicCodeButtoniew = (Button) findViewById(R.id.get_auth_code);
		mLoginStatusView = (TextView) findViewById(R.id.login_status);
		mImeiView = (TextView) findViewById(R.id.imie);
		mImeiView.setText("本机标识：" + cmucApplication.getPhoneIMEI());
		mGetDynamicCodeButtoniew.setOnClickListener(this);
		ImageView mAppNameView = (ImageView) findViewById(R.id.splash_app_name);
		mAppNameView.setImageResource(cmucApplication.getAppSplashNameId());
		
//		TextView helpView = (TextView) findViewById(R.id.help);
//		helpView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//		helpView.setOnClickListener(this);
		
		TextView bindView = (TextView) findViewById(R.id.binding_imie);
		bindView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bindView.setOnClickListener(this);
		
		mIdentityVerificationView=(TextView) findViewById(R.id.identity_verification);
		mIdentityVerificationView.setOnClickListener(this);
		String appTag=cmucApplication.getAppTag();
		if(appTag.equals(CmucApplication.ESOP_APP_TAG)||appTag.equals(CmucApplication.YXZS_APP_TAG)){
			mIdentityVerificationView.setVisibility(View.GONE);
		}
		((TextView)findViewById(R.id.version_name)).setText("V "+CmucApplication.getApkVersionName(this));
		
		findViewById(R.id.friends_share).setOnClickListener(this);
	}

	private void initAllUserNames(String userName) {
		String userNames = cmucApplication.getSettingsPreferences()
				.getAllUserNames();
		if (userNames != null) {
			String[] mAllUserNames = userNames.split(Preferences.USER_SPLIT);
			if (mAllUserNames != null && mAllUserNames.length > 0) {
				mAllUserNamesAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line,
						mAllUserNames);
				mLoginIdView.setAdapter(mAllUserNamesAdapter);
			}
		}
	}

	private boolean checkLoginInfoValid() {
		BubbleEditText view = null;
		String warnMessage = LOGIN_SUCCESS;
		mLoginId = mLoginIdView.getText().toString().trim().replaceAll(" ", "");
		mLoginPassword = mLoginPasswordView.getText().toString().trim();
		if (TextUtils.isEmpty(mLoginId)) {
			warnMessage = getString(R.string.login_id_is_null);
			view = mLoginIdView;
			view.showBubbleTxtInfo(warnMessage);
			view.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(mLoginPassword)) {
			warnMessage = getString(R.string.login_password_is_null);
			view = mLoginPasswordView;
			view.requestFocus();
			view.showBubbleTxtInfo(warnMessage);
			return false;
		}
		mDynamicCodeStr = mDynamicCodeView.getText().toString().trim();
		if (TextUtils.isEmpty(mDynamicCodeStr)) {
			warnMessage = getString(R.string.login_auth_code_is_null);
			view = mDynamicCodeView;
			view.requestFocus();
			view.showBubbleTxtInfo(warnMessage);
			return false;
		}
		return true;
	}

	private void saveUserInfo() {
		Preferences preferences = cmucApplication.getSettingsPreferences();
		preferences.saveUserInfo(mLoginId, mLoginPassword);
		preferences.saveAuthentication(mAuthentication);
		preferences.saveMobile(mMobile);
		preferences.savePushMessageUserName(mLoginId);
		preferences.savePushMessagePassword(mNotificationPassword);
		preferences.saveUserNameToAllUserNames(mLoginId);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_login:
			doLogin();
			break;
		case R.id.get_auth_code:
			doGetDynamicCode();
			break;
		case R.id.top_back_btn:
			finish();
			break;
		case R.id.binding_imie:
			createBindingDialog(false);/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian*/
			break;
//		case R.id.help:
//			showHelpInfo(v);
//			break;
		case R.id.identity_verification:
			visit(cmucApplication.getIdentityVerificationMenu());
			break;
		case R.id.friends_share:
			goShare();
			break;
		}
	}

	private void goShare() {
		Intent intent=new Intent(this, ShareActivity.class);
		startActivity(intent);
	}

	private void visit(Item item){
		if (item == null) {
			doAlertExist();
			return;
		}
		
		String content=item.getContent();
		String name=item.getName();
		String appTag=item.getAppTag();
		String menuId=item.getMenuId()+"";
	    long childVersion=item.getChildVersion();
	    int businessId=item.getBusinessId();
		Intent intent = new Intent(LoginActivity.this,WebViewActivity.class);
		intent.putExtra(CmucApplication.CONTENT_EXTRAL, content);
		intent.putExtra(CmucApplication.MENU_ID, menuId);
		intent.putExtra(CmucApplication.APP_TAG_EXTRAL,appTag);
		intent.putExtra(CmucApplication.BUSINESS_ID_EXTRAL, businessId);
		intent.putExtra(CmucApplication.IS_BUSINESS_WEB_EXTRAL,true);
		intent.putExtra(CmucApplication.TITLE_EXTRAL, name);
		intent.putExtra(CmucApplication.POSITIONS_EXTRAL, "登录");
	    intent.putExtra(CmucApplication.CHILD_VERSION_EXTRAL, childVersion);
		if(content!=null){
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}	
	}
	
	private void doAlertExist() {
		CommonActions.createSingleBtnMsgDialog(this, null, "应用内存被清理，请重新启动",
				getString(R.string.exit),
				new CommonActions.OnTwoBtnDialogHandler() {

					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						cancleAll();
						dialog.dismiss();
						CommonActions.exitClient(LoginActivity.this);
					}

					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
					}
				});
	}
	
	private void createBindingDialog(boolean isFromGetDynamicCode) {/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian*/
		String account = null;
		if (mLoginIdView.getText().length() != 0)
			account = mLoginIdView.getText().toString();

		if (mBingDialog == null){
			mBingDialog = new BindIMEIDialog(this,
					cmucApplication.getPhoneIMEI(), account,isFromGetDynamicCode);/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian*/
		}else{
			mBingDialog.setAccount(account);
			mBingDialog.setTitle(isFromGetDynamicCode);/*add for request id RA-IR-0018: 优化imei号绑定流程 	by liuyitian*/
		}
			
		mBingDialog.show();
	}
	
	private void doLogin() {
		boolean isVaild = checkLoginInfoValid();
		if (isVaild) {
			login();
		}
	}

	private void doGetTopListData() {
		if (mGetTopListDataTask != null)
			mGetTopListDataTask.cancle();
		mGetTopListDataTask = new GetTopDataTask(this);
		mGetTopListDataTask.setListener(mGetTopListDataListener);
		mTaskIndex = GET_TOP_LIST_DATA_TASK_INDEX;
		mGetTopListDataTask.execute();
	}

	private void onGetTopListDataSuccess() {
		goMainPage();
		if (cmucApplication.getServiceManager() != null) {
			cmucApplication.getServiceManager().stopService();
			cmucApplication.setServiceManager(null);
		}
		cmucApplication.setServiceManager(CommonActions
				.startPushMessageService(this));
	}

	private void handleException(GenericTask task, Context context) {
		Exception exception = task.getException();
		if (task.getException() != null) {
			if (exception instanceof BusinessException) {
				/*add for request id RA-IR-0018: 优化imei号绑定流程 Start	by liuyitian*/
//				if (exception.getMessage().equals("帐号或者IMEI码验证失败!")) {
				if (((BusinessException)exception).getStatusCode() == 2) {//等彪哥后台搞好就用这个判断
					createBindingDialog(true);
				} else {
					Toast.makeText(context, exception.getMessage(),
							Toast.LENGTH_LONG).show();
				}
				/*add for request id RA-IR-0018: 优化imei号绑定流程 End	by liuyitian*/
			} else {
				createServerErrorDialog(task);
			}
		}
	}
	
	private void handleGetTopDataException(GenericTask task) {
		Exception exception = task.getException();
		if (exception != null) {
			createServerErrorDialog(task);
		}
	}

	private void onGetTopListDataFailed(GenericTask task) {
		mLoginStatusView.setText("");
		handleGetTopDataException(task);
	}

	public void createServerErrorDialog(GenericTask task) {
		CommonActions.createTwoBtnMsgDialog(this, null, task.getException()
				.getMessage(), getString(R.string.re_try),
				getString(R.string.exit),
				new CommonActions.OnTwoBtnDialogHandler() {

					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						dialog.dismiss();
						retryTask();
					}

					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						cancleAll();
						dialog.dismiss();
						CommonActions.exitClient(LoginActivity.this);
					}
				}, false);
	}

	private void login() {
		// TODO Auto-generated method stub
		if (mLoginTask != null)
			mLoginTask.cancle();
		mLoginTask = new LoginTask();
		mLoginTask.setListener(mLoginListener);
		TaskParams params = new TaskParams();
		mTaskIndex = LOGIN_TASK_INDEX;
		mLoginTask.execute(params);
	}

	private void retryTask() {
		switch (mTaskIndex) {
		case LOGIN_TASK_INDEX:
			doLogin();
			break;
		case GET_TOP_LIST_DATA_TASK_INDEX:
			doGetTopListData();
			break;
		case GET_GETDYNAMICCODE_TASK_INDE:
			doGetDynamicCode();
		}
	}

	private void cancleAll() {
		cancleTask(mLoginTask);
		cancleTask(mGetTopListDataTask);
		cancleTask(mGetDynamicCodeTask);
	}

	private void onLoginSuccess() {
		if (cmucApplication.isApplicationRunning()) {
			saveUserInfo();
			createUserCachDir();
			if (!isStartByJavascript)
				doGetTopListData();
			else {
				returnToStartActivity();
			}
		} else {
			returnToStartActivity();
		}
	}

	private void returnToStartActivity() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}

	private void cancleTask(GenericTask task) {
		if (task != null && task.getStatus() == GenericTask.Status.RUNNING) {
			task.cancle();
		}
	}

	private void createUserCachDir() {
		Preferences preferences = cmucApplication.getSettingsPreferences();
		String userName = preferences.getUserName();
		String userJsonDir = CmucApplication.APP_FILE_ROOT_DIR + File.separator
				+ userName + File.separator + "json";
		cmucApplication.setUserJsonDir(userJsonDir);
		FileUtils.createSDDir(userJsonDir);
		preferences.createMenusVersionOfUserConfig(userName);
	}

	private void onLoginFailed(GenericTask task) {
		mLoginStatusView.setText("");
		handleException(task, LoginActivity.this);
		updateLoginAllViewState(true);
	}

	private void goMainPage() {
		finish();
		Intent intent = new Intent(this, MainTabActivity.class);
		startActivity(intent);
	}

	private void setDynamicCodeViewState(boolean gotAuthCode) {
		mDynamicCodeView.setEditable(gotAuthCode);
		mGetDynamicCodeButtoniew.setEnabled(gotAuthCode);
	}

	private void getDynamicCode() {
		if (getDynamicCodeStartTime != -1) {
			long duration = System.currentTimeMillis()
					- getDynamicCodeStartTime;
			if (duration < GETDYNAMICCODE_DURATION) {
				Toast.makeText(LoginActivity.this,
						R.string.busy_click_dyaniccode_button,
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		if (mGetDynamicCodeTask != null)
			mGetDynamicCodeTask.cancle();
		mGetDynamicCodeTask = new GetDynamicCodeTask();
		mGetDynamicCodeTask.setListener(mGetDynamicCodeListener);
		mTaskIndex = GET_GETDYNAMICCODE_TASK_INDE;
		mGetDynamicCodeTask.execute();
	}

	private void doGetDynamicCode() {
		boolean isVaild = checkDynamicCodeInfoValid();
		if (isVaild) {
			getDynamicCode();
		}
	}

	private boolean checkDynamicCodeInfoValid() {
		String warnMessage = LOGIN_SUCCESS;
		mLoginId = mLoginIdView.getText().toString().trim();
		mLoginPassword = mLoginPasswordView.getText().toString().trim();
		if (TextUtils.isEmpty(mLoginId)) {
			warnMessage = getString(R.string.login_id_is_null);
			mLoginIdView.showBubbleTxtInfo(warnMessage);
			mLoginIdView.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(mLoginPassword)) {
			warnMessage = getString(R.string.login_password_is_null);
			mLoginPasswordView.showBubbleTxtInfo(warnMessage);
			mLoginPasswordView.requestFocus();
			return false;
		}

		return true;
	}

	private void updateLoginAllViewState(boolean enable) {
		if (!enable)
			mAnimImageView.setVisibility(View.VISIBLE);
		else
			mAnimImageView.setVisibility(View.INVISIBLE);
		mLoginIdView.setEnabled(enable);
		mLoginPasswordView.setEnabled(enable);
		mLoginButtonView.setEnabled(enable);
		setDynamicCodeViewState(enable);
	}

	private void startAnimation() {
		if (!mAnimDrawable.isRunning())
			mAnimDrawable.start();
	}

	private void stopAnimation() {
		if (mAnimDrawable.isRunning())
			mAnimDrawable.stop();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		dismissBubble();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	private void dismissBubble() {
		mLoginIdView.dismissBubble();
		mLoginPasswordView.dismissBubble();
		if (mDynamicCodeView != null)
			mDynamicCodeView.dismissBubble();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stopAnimation();
			cancleAll();
			createExitAppDialog();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void createExitAppDialog() {
		CommonActions.createTwoBtnMsgDialog(this, getString(R.string.userexit),
				getString(R.string.confirmexit),
				getString(R.string.user_exit_yes),
				getString(R.string.user_exit_no),
				new CommonActions.OnTwoBtnDialogHandler() {

					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						cancleAll();
						dialog.dismiss();
						CommonActions.exitClient(LoginActivity.this);
					}

					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						retryTask();
						dialog.dismiss();
					}
				}, false);
	}

	class LoginTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			// TODO Auto-generated method stub
			LoginBean loginBean;
			try {
				publishProgress(getString(R.string.logining));
				loginBean = CmucApplication.sServerClient.login(mLoginId,
						mDynamicCodeStr);
				mAuthentication = loginBean.getAuthentication();
				mNotificationPassword = loginBean.getNotificationPassword();
				mMobile = loginBean.getMobile();
				mResultMsg = loginBean.getResultMesage();
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
			}

			return TaskResult.OK;
		}

	}

	class GetDynamicCodeTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				mDynamicCodeBean = CmucApplication.sServerClient
						.sendDynamicCode(mLoginId, mLoginPassword,
								cmucApplication.getPhoneIMEI(),
								cmucApplication.getPhoneIMSI());
			} catch (HttpException e) {
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			} catch (BusinessException e) {
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	private void onGetDynamicCodeSuccess() {
		if (mDynamicCodeBean != null) {
			String dynamicType = mDynamicCodeBean.getDynamicType();
			if (dynamicType.equals(DynamicCodeBean.DYNAMIC_MESSAGE_TYPE)) {
				stopAnimation();
				Toast.makeText(LoginActivity.this,
						R.string.send_dynamiccode_request, Toast.LENGTH_SHORT)
						.show();
				mGetDynamicCodeButtoniew.setText(R.string.get_auth_code);
				getDynamicCodeStartTime = System.currentTimeMillis();
			} else {
				asyncdynamicCodeImage(dynamicType);
				getDynamicCodeStartTime = -1;
			}
		}
	}

	private void asyncdynamicCodeImage(String dynamicCodeUrl) {
		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(this);
		asyncImageLoader.setIsAlwaysFormNet(true);
		ImageCallBack imageCallBack = new ImageCallBack() {
			@Override
			public void loadImage(Bitmap d) {
				stopAnimation();
				if (d != null) {
					mGetDynamicCodeButtoniew
							.setBackgroundDrawable(new BitmapDrawable(d));
					mGetDynamicCodeButtoniew.setText("      ");
				} else {
					Toast.makeText(LoginActivity.this,
							R.string.get_dynamiccode_faild, Toast.LENGTH_LONG)
							.show();
					mGetDynamicCodeButtoniew.setText(R.string.get_auth_code);
				}
			}

		};
		asyncImageLoader.loadDrawable(dynamicCodeUrl, imageCallBack);
	}

	private void onGetDynamicCodeFaild(GenericTask task) {
		mLoginStatusView.setText("");
		handleException(task, LoginActivity.this);
		mGetDynamicCodeButtoniew.setText(R.string.get_auth_code);
	}

	/* fuheng add 2013-07-03 for 登录显示上次登录时间 */
	/**
	 * 登录成功后显示上次登录时间
	 * 
	 * @param lastLogininInfo
	 */
	private void createLastLoginTimeDialog(String lastLogininInfo) {
		CommonActions.createSingleBtnMsgDialog(this, "登录成功", lastLogininInfo,
				getString(R.string.continu),
				new CommonActions.OnTwoBtnDialogHandler() {

					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						mTaskIndex = -1;
						dialog.dismiss();
						checkSecurePassword();
					}

					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						dialog.dismiss();
					}
				});
	}

	/**
	 * 安全密码判断
	 */
	private void checkSecurePassword() {
		if (cmucApplication.getSettingsPreferences().getSecurePassword() == null) {
			SetSesurePasswordDialog dialog = new SetSesurePasswordDialog(this);
			dialog.setBtnOkListener(new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					onLoginSuccess();
					dialog.dismiss();
				}
			});

			dialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {

					if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						createExitAppDialog();
					}

					return false;
				}
			});

			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					cancleAll();
					dialog.dismiss();
					CommonActions.exitClient(LoginActivity.this);
				}
			});
			dialog.show();
		} else {
			onLoginSuccess();
		}
	}

	private void showHelpInfo(View v) {
		if (pupupwindow == null) {
			String content = v.getContext().getString(R.string.answer_imei);/* add for request id RA-IR-0018: 优化imei号绑定流程 by liuyitian Start */
			if (content != null) {
				TextView view = new TextView(v.getContext());
				view.setText(content);
				view.setBackgroundResource(R.drawable.op_bookmark_bg);
				view.setTextColor(Color.WHITE);
				pupupwindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				pupupwindow.setBackgroundDrawable(new ColorDrawable(0));
				pupupwindow.setOutsideTouchable(true);
				pupupwindow.setFocusable(true);
			}
		}
		if (pupupwindow != null)
			pupupwindow.showAtLocation(
					(ViewGroup) (findViewById(R.id.login_status).getParent()),
					Gravity.BOTTOM, 0, 0);
	}
}
