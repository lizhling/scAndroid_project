package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.starcpt.analytics.PhoneClickAgent;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppActionConstant;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.PhoneCurInfo;
import com.sunrise.scmbhc.entity.PhoneFreeQuery;
import com.sunrise.scmbhc.entity.PhoneFreeQuery.EmumState;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.task.AskForSmsPasswordTask;
import com.sunrise.scmbhc.task.ChannelOperTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetOpenedUpBusinnessListTask;
import com.sunrise.scmbhc.task.LoadMCreditsTask;
import com.sunrise.scmbhc.task.LoadUserBaseInfoTask;
import com.sunrise.scmbhc.task.LoadUserTrafficTask;
import com.sunrise.scmbhc.task.LoginTask;
import com.sunrise.scmbhc.task.PhoneCurrMsgTask;
import com.sunrise.scmbhc.task.PhoneFreeQueryTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.view.EditTextWacher;
import com.sunrise.scmbhc.ui.view.TwoButtonDialog;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.HardwareUtils;
import com.sunrise.scmbhc.utils.LogUtlis;

public class LoginFragment extends BaseFragment implements ExtraKeyConstant, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener,
		TaskListener, OnClickListener {

	private AutoCompleteTextView mPhoneNumber;
	private EditText mPassword;
	protected CheckBox mAutoLogin;
	protected CheckBox mSavaPassword;
	private MyCount mc;
	private GenericTask mTask;
	private Button mBtSendSmsPassword;
	public static Handler loginTaskThreadHandler;
	private TextView mForgetPassword;
	private SmsPasswordReceiver mSMSReceiver;
	private RadioGroup mWidget;
	private TextView mTagPasswordType;
	// public static final int LOGIN_SUCCESS = 1;
	// public static final int LOGIN_FAILED = 2;
	// public static final int LOGIN_RETRAY = 3;
	private ArrayList<HashMap<String, String>> mUserNames;
	// Thread thread = null;
	/**
	 * 信号强度
	 */
	private String mSignalStrength;

	private View mBtnDeleteUserName;
	private View mBtnDeletePassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSMSReceiver = new SmsPasswordReceiver();
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = initView(inflater, container);
		initData();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mBaseActivity.setTitle(getString(R.string.app_name));
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		mBaseActivity.registerReceiver(mSMSReceiver, new IntentFilter(SMS_RECEIVED_ACTION));

		HardwareUtils.loadSignalStrength(mBaseActivity, new Observer() {

			@Override
			public void update(Observable arg0, Object arg1) {
				mSignalStrength = (String) arg1;
			}
		});
	}

	@Override
	public void onStop() {
		// 销毁注册的广播
		if (mSMSReceiver != null) {
			mBaseActivity.unregisterReceiver(mSMSReceiver);
		}
		// 取消倒计时
		if (mc != null) {
			mc.cancel();
		}
		// 取消任务
		cancelTask();

		super.onStop();
	}

	private void cancelTask() {
		if (mTask != null) {
			mTask.cancle();
		}
	}

	protected View initView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.login_layout, container, false);
		{
			mWidget = (RadioGroup) view.findViewById(R.id.tabs);
			mWidget.setOnCheckedChangeListener(this);
		}
		{
			mPhoneNumber = (AutoCompleteTextView) view.findViewById(R.id.editText_phoneNumber);
			mPassword = (EditText) view.findViewById(R.id.editText_password);
		}
		{
			mAutoLogin = (CheckBox) view.findViewById(R.id.checkBox_autoLogin);
			mAutoLogin.setOnCheckedChangeListener(this);
			mSavaPassword = (CheckBox) view.findViewById(R.id.checkBox_savePassword);
			mSavaPassword.setOnCheckedChangeListener(this);

			mBtSendSmsPassword = (Button) view.findViewById(R.id.sendSmsPassword);
			mBtSendSmsPassword.setOnClickListener(this);

			mForgetPassword = (TextView) view.findViewById(R.id.forgetPassword);
			mForgetPassword.getPaint().setUnderlineText(true);

			view.findViewById(R.id.login).setOnClickListener(this);
		}
		{
			mBtnDeleteUserName = view.findViewById(R.id.btn_delete1);
			mBtnDeletePassword = view.findViewById(R.id.btn_delete2);

			mBtnDeleteUserName.setOnClickListener(this);
			mBtnDeletePassword.setOnClickListener(this);

			mPhoneNumber.addTextChangedListener(new EditTextWacher(mBtnDeleteUserName));
			mPassword.addTextChangedListener(new EditTextWacher(mBtnDeletePassword));

			mPhoneNumber.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					TextView editText = (TextView) arg0;
					if (!arg1) {
						mBtnDeleteUserName.setVisibility(View.INVISIBLE);
						return;
					}

					if (!TextUtils.isEmpty(editText.getText().toString()))
						mBtnDeleteUserName.setVisibility(View.VISIBLE);

				}
			});

			mPassword.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					TextView editText = (TextView) arg0;
					if (!arg1) {
						mBtnDeletePassword.setVisibility(View.INVISIBLE);
						return;
					}

					if (!TextUtils.isEmpty(editText.getText().toString()))
						mBtnDeletePassword.setVisibility(View.VISIBLE);

				}
			});
		}

		mTagPasswordType = (TextView) view.findViewById(R.id.tag_password_type);

		return view;
	}

	protected void initData() {
		// 初始化数据
		String phoneNum = UserInfoControler.getInstance().getUserName();
		if (!TextUtils.isEmpty(phoneNum))
			mPhoneNumber.setText(phoneNum);

		mUserNames = UserInfoControler.getInstance().getUserNames();
		mPhoneNumber.setAdapter(new SimpleAdapter(mBaseActivity, mUserNames, android.R.layout.simple_dropdown_item_1line, new String[] { KEY_PHONE_NUMBER },
				new int[] { android.R.id.text1 }));
		mPhoneNumber.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				HashMap<String, String> hash = (HashMap<String, String>) arg0.getAdapter().getItem(position);
				mPhoneNumber.setText(hash.get(KEY_PHONE_NUMBER));
				CharSequence password = hash.get(KEY_PASSWORD);

				boolean isSmsPasswordType = mBtSendSmsPassword.getVisibility() == View.VISIBLE;// 短信登录状态

				if (isSmsPasswordType) {
					mPassword.setTag(password);
				} else {
					if (!TextUtils.isEmpty(password)) {
						mPassword.setText(password);
						mSavaPassword.setChecked(true);
						mPassword.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.left_spread));
					} else {
						mPassword.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
						mPassword.setText(null);
						mSavaPassword.setChecked(false);
					}
				}
			}
		});

		String password = UserInfoControler.getInstance().getPassword();
		if (!TextUtils.isEmpty(password)) {
			mPassword.setText(password);
			mSavaPassword.setChecked(true);
		}

		mAutoLogin.setChecked(App.sSettingsPreferences.isAutoLogin());
	}

	/**
	 * called by button(R.id.button_sendSmsPassword) in R.layout.login_layout
	 * 
	 * @param view
	 */
	public void askForSmsPassword(View view) {

		String phoneNumber = mPhoneNumber.getText().toString();
		if (TextUtils.isEmpty(phoneNumber)) {
			mPhoneNumber.requestFocus();
			Toast.makeText(mBaseActivity, R.string.hintInputMobileNumber, Toast.LENGTH_SHORT).show();
			return;
		}

		// if (!CommUtil.isMobilePhone(phoneNumber)) {
		if (mPhoneNumber.length() != 11) {
			mPhoneNumber.requestFocus();
			Toast.makeText(mBaseActivity, R.string.inputCorrectPhoneNumber_s, Toast.LENGTH_SHORT).show();
			return;
		}

		mTask = new AskForSmsPasswordTask();
		mTask.setListener(new TaskListener() {
			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				Toast.makeText(mBaseActivity, (String) param, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onPreExecute(GenericTask task) {
				initProgressDialog();
				showDialog(getResources().getString(R.string.requestSmsPassword));
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				dismissDialog();

				if (TaskResult.FAILED == result) {
					// 恢复发送信息可点击
					if (mc != null) {
						mc.cancel();
						mBtSendSmsPassword.setText(getString(R.string.sendSmsPassword));
						mBtSendSmsPassword.setBackgroundResource(R.drawable.selector_bg_azure);
						mBtSendSmsPassword.setEnabled(true);
						mBtSendSmsPassword.setClickable(true);
					}
					CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), null);
				}
			}

			@Override
			public void onCancelled(GenericTask task) {
				dismissDialog();
			}

			@Override
			public String getName() {
				return null;
			}
		});
		TaskParams params = new TaskParams(LoginTask.KEY_PHONE_NUMBER, phoneNumber);
		mTask.execute(params);
	}

	/**
	 * called by button(button1\button2) in R.layout.login_layout
	 * 
	 * @param view
	 */
	public void login(View view) {
		String phoneNumber = mPhoneNumber.getText().toString();
		boolean isServicePasswordType = mBtSendSmsPassword.getVisibility() != View.VISIBLE;// 服务密码状态

		if (TextUtils.isEmpty(phoneNumber)) {
			mPhoneNumber.requestFocus();
			Toast.makeText(mBaseActivity, R.string.hintInputMobileNumber, Toast.LENGTH_SHORT).show();
			return;
		}

		// if (!CommUtil.isMobilePhone(phoneNumber)) {
		if (mPhoneNumber.length() != 11) {
			mPhoneNumber.requestFocus();
			Toast.makeText(mBaseActivity, R.string.inputCorrectPhoneNumber_s, Toast.LENGTH_SHORT).show();
			return;
		}

		String servicepassword = mPassword.getText().toString();
		if (TextUtils.isEmpty(servicepassword) || (isServicePasswordType && servicepassword.length() != 6)) {
			mPassword.requestFocus();
			Toast.makeText(mBaseActivity, isServicePasswordType ? R.string.hintInputServicePassword : R.string.hintNoticeClickSmsPassword, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// if (thread == null) {
		// thread = new LoginTaskThread(phoneNumber, servicepassword,
		// mAutoLogin.isChecked(), isServicePasswordType);
		// thread.start();
		// } else {
		// if (thread.isAlive()) {
		// thread.stop();
		// }
		// thread.start();
		// }

		mTask = LoginTask.execute(phoneNumber, servicepassword, isServicePasswordType, mSavaPassword.isChecked(), mAutoLogin.isChecked(), this);
	}

	protected void onBack() {
		mBaseActivity.finish();

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.loginByServecePassword:
			mBtSendSmsPassword.setVisibility(View.GONE);
			mAutoLogin.setVisibility(View.VISIBLE);
			mSavaPassword.setVisibility(View.VISIBLE);
			// showForgetPassword(true);
			mPassword.setHint(R.string.hintInputServicePassword);
			mTagPasswordType.setText(R.string.tag_service_password);
			break;

		case R.id.loginBySmsPassword:
			mBtSendSmsPassword.setVisibility(View.VISIBLE);
			mAutoLogin.setVisibility(View.GONE);
			mSavaPassword.setVisibility(View.GONE);
			showForgetPassword(false);
			mPassword.setHint(R.string.hintNoticeClickSmsPassword);
			mTagPasswordType.setText(R.string.tag_sms_password);
			break;
		}

		String temp = (String) mPassword.getTag();
		mPassword.setTag(mPassword.getText().toString());
		mPassword.setText(temp);
	}

	protected void showForgetPassword(boolean show) {
		mForgetPassword.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.checkBox_autoLogin:
			if (isChecked)
				mSavaPassword.setChecked(true);
			break;
		case R.id.checkBox_savePassword:
			if (!isChecked)
				mAutoLogin.setChecked(false);
			break;
		}
	}

	/**
	 * 加载用户信息
	 */
	private void doInit() {
		cancelTask();
		new LoadUserTrafficTask().execute();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initProgressDialog();
		showDialog(getResources().getString(R.string.logining));
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {

		if (TaskResult.OK == result) {
			App.sSettingsPreferences.setLoginOut(false);
			getActivity().sendBroadcast(new Intent(AppActionConstant.ACTION_REFRESH_REQUEST));// 请求刷新
			mBaseActivity.setResult(Activity.RESULT_OK, mBaseActivity.getIntent());
			if (mDialogCertain != null) {
				mDialogCertain.dismiss();
			}
			// if (App.getServiceManager() != null) {
			// App.getServiceManager().stopService();
			// App.setServiceManager(null);
			// }
			// App.setServiceManager(CommUtil.startPushMessageService(getActivity()));

			doInit();
			doAsiaInfoChannelLogin();
			dismissDialog();
			onBack();
		} else {
			dismissDialog();
			if (task.getException() instanceof HttpException) {
				showRetryDialog(task.getException().getMessage()); // 显示重试对话框
			} else {
				CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), null); // 显示
																									// 异常信息提示框
			}
		}

		HashMap<String, String> map = CommUtil.stringToMap("result&" + (TaskResult.OK == result), "phoneNumber&" + mPhoneNumber.getText().toString(),
				"isSmsPassword&" + String.valueOf(mBtSendSmsPassword.getVisibility() == View.VISIBLE), "ip&" + HardwareUtils.getLocalIpAddress(), "macAddress&"
						+ HardwareUtils.getMacAddress(getActivity()), "networkType&" + HardwareUtils.getNetWorkType(getActivity()), "signalStrength&"
						+ mSignalStrength);
		HardwareUtils.getLocation(getActivity(), map);
		LogUtlis.showLogD(getClass().getSimpleName(), map.toString());
		PhoneClickAgent.onEvent(getActivity(), "login", map);
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {

		TaskParams params = (TaskParams) param;
		String phoneNumber = params.getString(KEY_PHONE_NUMBER);
		String password = params.getString(KEY_PASSWORD);
		boolean savePassword = (Boolean) params.get(KEY_SAVE_PASSWORD);
		boolean autoLogin = (Boolean) params.get(KEY_AUTO_LOGIN);
		String pushMsgPassword = (String) params.get(PUSH_MESSAGE_PASSWORD);
		String token = (String) params.get(KEY_TOKEN);

		App.setPwd(password);
		UserInfoControler.getInstance().clean();
		UserInfoControler.getInstance().loginIn(phoneNumber, savePassword ? password : null, token);
		UserInfoControler.getInstance().setAutoLogin(autoLogin);
		mBaseActivity.getIntent().putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
		mBaseActivity.getIntent().putExtra(Intent.EXTRA_REMOTE_INTENT_TOKEN, token);
		// App.sSettingsPreferences.savePushMessageUserName(phoneNumber);
		// if (pushMsgPassword != null) {
		// App.sSettingsPreferences.savePushMessagePassword(pushMsgPassword);
		// }

	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	private void initProgressDialog() {
		initDialog(true, false, new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				cancelTask();
			}
		});
	}

	// public class LoginTaskThread extends Thread {
	// String phoneNumber;
	// String passWord;
	// boolean isAuto;
	// boolean isServePassword;
	// String result = null;
	//
	// public LoginTaskThread(String phoneNumber, String passWord) {
	// this.phoneNumber = phoneNumber;
	// this.passWord = passWord;
	// this.isAuto = false;
	// this.isServePassword = false;
	// }
	//
	// public LoginTaskThread(String phoneNumber, String passWord, boolean
	// isAuto, boolean isServePassword) {
	// this.phoneNumber = phoneNumber;
	// this.passWord = passWord;
	// this.isAuto = isAuto;
	// this.isServePassword = isServePassword;
	// }
	//
	// public void run() {
	// if (loginTaskThreadHandler == null) {
	// // 其它线程中新建一个handler
	// Looper.prepare();//
	// 创建该线程的Looper对象，用于接收消息,在非主线程中是没有looper的所以在创建handler前一定要使用prepare()创建一个Looper
	// loginTaskThreadHandler = new Handler() {
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case LOGIN_SUCCESS:
	// // 更新显示
	// if (mDialogCertain == null) {
	// mDialogCertain = new TwoButtonDialog(getActivity(),
	// mOnClickListenerForConfirm,
	// mOnClickListenerForCancle);
	// mDialogCertain.setMessage("登录成功，重新登录？");
	// }
	// mDialogCertain.show();
	// App.sSettingsPreferences.setLoginOut(false);
	// getActivity().sendBroadcast(new
	// Intent(AppWidgetProviderBill.ACTION_REFRESH_REQUEST));// 请求刷新
	// mBaseActivity.setResult(Activity.RESULT_OK, mBaseActivity.getIntent());
	// if (App.getServiceManager() != null) {
	// App.getServiceManager().stopService();
	// App.setServiceManager(null);
	// }
	// App.setServiceManager(CommUtil.startPushMessageService(getActivity()));
	// doInit();
	// break;
	// case LOGIN_FAILED:
	// // 显示提示
	// // showRetryDialog();
	// break;
	// case LOGIN_RETRAY:
	// // 重新发送请求
	// doLogin();
	// break;
	// }
	// }
	// };
	// Looper.myLooper().loop();// 建立一个消息循环，该线程不会退出
	// }
	// doLogin();
	// }
	//
	// private void doLogin() {
	// try {
	// LogUtlis.showLogI("密码登录", "密码登录");
	// if (isServePassword) {// 系统密码登录
	// result = App.sServerClient.login(phoneNumber, passWord);
	// } else {// 随机短信密码登录
	// result = App.sServerClient.loginBySmsRamd(phoneNumber, passWord);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// LogUtlis.showLogI("Login Task", "in LoginTask FAILED");
	// // 发送消息失败消息
	// loginTaskThreadHandler.sendEmptyMessage(LOGIN_FAILED);
	// }
	//
	// App.sSettingsPreferences.savePushMessagePassword("122553");
	//
	// try {
	// JSONObject obj = new JSONObject(result);
	// if (obj.has("RETURN_INFO")) {
	// String returnMsg = "";
	// }
	// Message msg = new Message();
	// msg.obj = "{\"token\":"
	// +
	// obj.getJSONObject("RETURN").getJSONArray("RETURN_INFO").getJSONObject(0).getString("token")
	// + "}";
	// msg.what = LOGIN_SUCCESS;
	// loginTaskThreadHandler.sendMessage(msg);
	// // put(KEY_TOKEN,
	// //
	// obj.getJSONObject("RETURN").getJSONArray("RETURN_INFO").getJSONObject(0).getString("token"));
	// } catch (Exception e) {
	// e.printStackTrace();
	// loginTaskThreadHandler.sendEmptyMessage(LOGIN_FAILED);
	// }
	// }
	//
	// public String getPhoneNumber() {
	// return phoneNumber;
	// }
	//
	// public void setPhoneNumber(String phoneNumber) {
	// this.phoneNumber = phoneNumber;
	// }
	//
	// public String getPassWord() {
	// return passWord;
	// }
	//
	// public void setPassWord(String passWord) {
	// this.passWord = passWord;
	// }
	//
	// public boolean isAuto() {
	// return isAuto;
	// }
	//
	// public void setAuto(boolean isAuto) {
	// this.isAuto = isAuto;
	// }
	//
	// }

	private TwoButtonDialog mDialogCertain = null;

	private View.OnClickListener mOnClickListenerForConfirm = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// 重新发起请求
			login(null);
			mDialogCertain.dismiss();
		}
	};
	private View.OnClickListener mOnClickListenerForCancle = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mDialogCertain.dismiss();
		}
	};

	private void showRetryDialog(CharSequence message) {
		if (mDialogCertain == null) {
			mDialogCertain = new TwoButtonDialog(getActivity(), mOnClickListenerForConfirm, mOnClickListenerForCancle);
			mDialogCertain.setLeftButtonText(getString(R.string.re_try));
		}
		mDialogCertain.setMessage(message.toString());
		mDialogCertain.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			// PhoneClickAgent.onEvent(mBaseActivity, "login");
			login(v);
			break;
		case R.id.sendSmsPassword:
			PhoneClickAgent.onEvent(mBaseActivity, "loginSendSmsPassword", "");
			String phoneNumber = mPhoneNumber.getText().toString();
			if (TextUtils.isEmpty(phoneNumber)) {
				mPhoneNumber.requestFocus();
				Toast.makeText(mBaseActivity, R.string.hintInputMobileNumber, Toast.LENGTH_LONG).show();
			} else {
				askForSmsPassword(v);
				// 点击后在60s内不能重新获取
				mBtSendSmsPassword.setBackgroundResource(R.drawable.sendsms_grey);
				mBtSendSmsPassword.setEnabled(false);
				mBtSendSmsPassword.setClickable(false);
				// 启动倒计时
				mc = new MyCount(60000, 1000);
				mc.start();
				Toast.makeText(mBaseActivity, "60s后可以重新获取", Toast.LENGTH_LONG).show(); // 显示提醒消息
			}
			break;
		case R.id.btn_delete1:
			mPhoneNumber.setText(null);
			break;
		case R.id.btn_delete2:
			mPassword.setText(null);
			break;
		default:
			break;
		}
	}

	/* 定义一个倒计时的内部类 来倒计时 */
	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mBtSendSmsPassword.setText(getString(R.string.sendSmsPassword));
			mBtSendSmsPassword.setBackgroundResource(R.drawable.selector_bg_azure);
			mBtSendSmsPassword.setEnabled(true);
			mBtSendSmsPassword.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mBtSendSmsPassword.setText("重新获取(" + millisUntilFinished / 1000 + "s)");
		}
	}

	private void dealMsgForSMSPassword(String msgBody) {
		if (TextUtils.isEmpty(msgBody))
			return;
		int index = msgBody.indexOf("短信随机码");
		if (index == -1)
			return;

		index = msgBody.indexOf("验证码", index + 5) + 3;
		// 10086 : 10086 :
		// 尊敬的客户，您于2014年06月19日16时13分40秒,发起短信随机码验证。您的验证码为058423。请注意查收保管，验证码有效时间 :
		// 1403165620000
		while (index < msgBody.length() && !Character.isDigit(msgBody.charAt(index)))
			++index;

		int end = index + 1;
		while (end < msgBody.length() && Character.isDigit(msgBody.charAt(end)))
			++end;

		if (end == -1)
			end = index + 6;

		String smspassword = msgBody.substring(index, end);

		if (!TextUtils.isDigitsOnly(smspassword))// 分数字返回
			return;

		Toast.makeText(mBaseActivity, "已自动提取短信随机码：" + smspassword, Toast.LENGTH_LONG).show();

		mWidget.check(R.id.loginBySmsPassword);
		mPassword.setText(smspassword);
		login(null);
	}

	private void dealMsgForSMSPassword2(String msgBody) {
		if (TextUtils.isEmpty(msgBody))
			return;

		final int length = 6;
		String smspassword = null;
		for (int i = 0, j = 0; i < msgBody.length();)
			if (Character.isDigit(msgBody.charAt(i + j))) {
				if (++j == length - 1) {
					smspassword = msgBody.substring(i, i + length);
					break;
				} else
					++j;
			} else {
				i += Math.max(j, 1);
				j = 0;
			}

		if (!TextUtils.isEmpty(smspassword))// 分数字返回
			return;

		mWidget.check(R.id.loginBySmsPassword);
		mPassword.setText(smspassword);
		login(null);
	}

	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	class SmsPasswordReceiver extends BroadcastReceiver {

		public static final String TAG = "ImiChatSMSReceiver";

		// android.provider.Telephony.Sms.Intents

		private static final String AIMS_ORIGINATING_ADDRESS = "10086";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {

				SmsMessage[] messages = getMessagesFromIntent(intent);
				if (messages != null)
					for (SmsMessage message : messages) {
						LogUtlis.showLogI(TAG,
								message.getOriginatingAddress() + " : " + message.getDisplayOriginatingAddress() + " : " + message.getDisplayMessageBody()
										+ " : " + message.getTimestampMillis());
						String displayOriginationAdrress = message.getDisplayOriginatingAddress();
						if (displayOriginationAdrress != null && displayOriginationAdrress.startsWith(AIMS_ORIGINATING_ADDRESS)) {
							dealMsgForSMSPassword2(message.getDisplayMessageBody());
						}
					}
			}
		}

		public final SmsMessage[] getMessagesFromIntent(Intent intent) {

			Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

			byte[][] pduObjs = new byte[messages.length][];

			for (int i = 0; i < messages.length; i++) {
				pduObjs[i] = (byte[]) messages[i];
			}

			byte[][] pdus = new byte[pduObjs.length][];

			int pduCount = pdus.length;

			SmsMessage[] msgs = new SmsMessage[pduCount];

			for (int i = 0; i < pduCount; i++) {
				pdus[i] = pduObjs[i];
				msgs[i] = SmsMessage.createFromPdu(pdus[i]);
			}
			return msgs;
		}
	}

	@Override
	public void onDestroy() {
		cancelTask();
		super.onDestroy();
	}

	/**
	 * 激活亚信后台，获取asiaInfoToken
	 */
	private void doAsiaInfoChannelLogin() {
		new ChannelOperTask().excute(UserInfoControler.getInstance().getUserName(), ChannelOperTask.METHOD_LOGIN, null, new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				LogUtlis.showLogE("login", "asiaTokenOperation login " + param);
				App.sSettingsPreferences.setAsiaInfoToken((String) param);
			}

			@Override
			public void onPreExecute(GenericTask task) {

			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {

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

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.LoginFragment;
	}
}
