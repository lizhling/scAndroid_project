package com.sunrise.micromarketing.ui.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.sunrise.micromarketing.App;
import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.entity.UpdateInfo;
import com.sunrise.micromarketing.task.CheckLoginTask;
import com.sunrise.micromarketing.task.GenericTask;
import com.sunrise.micromarketing.task.LoginTask;
import com.sunrise.micromarketing.task.TaskListener;
import com.sunrise.micromarketing.task.TaskResult;
import com.sunrise.micromarketing.ui.view.DefaultInputDialog;
import com.sunrise.micromarketing.ui.view.DefaultListDialog;
import com.sunrise.micromarketing.ui.view.DefaultDialog.OnConfirmListener;
import com.sunrise.micromarketing.ui.view.EditTextWacher;
import com.sunrise.micromarketing.utils.CommUtil;
import com.sunrise.micromarketing.utils.HardwareUtils;
import com.sunrise.javascript.mode.BusinessInformation;
import com.sunrise.javascript.utils.CommonUtils;
import com.sunrise.javascript.utils.JsonUtils;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener, TaskListener, OnCheckedChangeListener {

	public static final String KEY_IS_START_FOR_RESULT = "is start for result";

	private AutoCompleteTextView mPhoneNumber;
	private EditText mPassword;
	protected CheckBox mAutoLogin;
	private GenericTask mTask;
	private View mBtnDeleteUserName;
	private View mBtnDeletePassword;
	private ArrayList<String> mArrayAccount;
	private TextView mEditCheckCode;
	private String sCheckNumber;
	
	/**
	 * 从账号对话框
	 */
	private DefaultListDialog mDefaultListdialog;

	private Toast mToast;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		mArrayAccount = loadLoginAccount();
		{
			mPhoneNumber = (AutoCompleteTextView) findViewById(R.id.editText_phoneNumber);
			mPhoneNumber.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mArrayAccount));
			mPassword = (EditText) findViewById(R.id.editText_password);

			mEditCheckCode = (TextView) findViewById(R.id.editText_checkcode);
		}
		{
			mAutoLogin = (CheckBox) findViewById(R.id.checkBox_autoLogin);
			mAutoLogin.setOnCheckedChangeListener(this);
			mAutoLogin.setChecked(getPreferences().isAutoLogin());
		}
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.textCheckCode).setOnClickListener(this);
		{// 显示版本号
			TextView text = (TextView) findViewById(R.id.versionName);
			text.setText("V" + App.sAppName);
		}
		{
			mBtnDeleteUserName = findViewById(R.id.btn_delete1);
			mBtnDeletePassword = findViewById(R.id.btn_delete2);
			mBtnDeleteUserName.setOnClickListener(this);
			mBtnDeletePassword.setOnClickListener(this);

			mPhoneNumber.addTextChangedListener(new EditTextWacher(mBtnDeleteUserName));
			mPassword.addTextChangedListener(new EditTextWacher(mBtnDeletePassword));
		}
		{
			TextView btnShare = (TextView) findViewById(R.id.btn_share);
			btnShare.getPaint().setUnderlineText(true);
			btnShare.setOnClickListener(this);
		}
		// mTagPasswordType = (TextView) findViewById(R.id.tag_password_type);

		mPhoneNumber.setText(getPreferences().getUserName());
		mPassword.setText(getPreferences().getPassword());
		sCheckNumber = getPreferences().getCheckedNumber();

		{ // 更新二维码
			ImageView imageview = (ImageView) findViewById(R.id.imageView_twodimensioncode);
			imageview.setImageBitmap(CommUtil.createQRCode(getShareContent(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)));
		}

	}

	protected void onStart() {
		super.onStart();
		if (getPreferences().isAutoLogin())
			checkCode();
	}

	protected void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			login();
			break;
		case R.id.btn_delete1:
			mPhoneNumber.setText(null);
			break;
		case R.id.btn_delete2:
			mPassword.setText(null);
			break;
		case R.id.textCheckCode:
			checkCode();
			break;
		case R.id.btn_share:
			// shareBySmsDialog();
			startShareActivity();
			break;
		default:
			break;
		}
	}

	private void startShareActivity() {
		Intent intent = new Intent(this, FriendsShareActivity.class);
		intent.putExtra(Intent.EXTRA_TEXT, getShareContent());
		startActivity(intent);
	}

	/**
	 * 分享通过短信的对话框
	 */
	private void shareBySmsDialog() {
		DefaultInputDialog dialog = new DefaultInputDialog(this).setDilaogTitle("分享内容给好友");
		dialog.setInputType(InputType.TYPE_CLASS_PHONE).setInputFieldSingleLine(true);
		dialog.setInputFieldHint("请输入手机号");
		dialog.setMessageIcon(R.drawable.icon_ssjf_share);
		dialog.setMessageIcon(CommUtil.createQRCode(getString(R.string.defaultDownloadUrl),
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)));
		dialog.setOnConfirmListener(new OnConfirmListener() {

			@Override
			public void onClick(DialogInterface dialog, String inputText, int which) {
				if (!CommonUtils.isMobilePhone(inputText)) {
					showToast("您输入的号码非标准11位移动手机号，请检查后重新输入……");
					return;
				}
				sendSmsToFriend(inputText);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void checkCode() {
		String account = mPhoneNumber.getText().toString();
		String password = mPassword.getText().toString();
		if (TextUtils.isEmpty(account)) {
			CommUtil.showAlert(this, null, "请输入帐号……", null);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			CommUtil.showAlert(this, null, "请输入密码……", null);
			return;
		}
		if (mTask != null)
			mTask.cancle();
		mTask = new CheckLoginTask().execute(account, password, HardwareUtils.getPhoneIMSI(this), HardwareUtils.getPhoneIMEI(this), this);
	}

	public void login() {
		String account = mPhoneNumber.getText().toString();

		if (TextUtils.isEmpty(account)) {
			CommUtil.showAlert(this, null, "请输入帐号……", null);
			return;
		}

		// TextView viewCheckCode = (TextView) findViewById(R.id.textCheckCode);

		// if (!TextUtils.isEmpty(viewCheckCode.getText())) {
		// CommUtil.showAlert(this, null, "请先获取校验码……", null);
		// return;
		// }

		String checkcode = mEditCheckCode.getText().toString();
		if (TextUtils.isEmpty(checkcode)) {
			CommUtil.showAlert(this, null, "请输入校验码……", null);
			return;
		}

		mTask = new LoginTask().execute(account, checkcode, this);
	}

	private void onCompleteLogin() {
		String subAccount = getPreferences().getSubAccount();
		if (subAccount.indexOf(',') != -1) {
			if (mDefaultListdialog == null)
				mDefaultListdialog = new DefaultListDialog(this);
			mDefaultListdialog.setDilaogTitle("请选择从账号……");
			mDefaultListdialog.setListChoice(subAccount.split(","));
			mDefaultListdialog.setOnConfirmListener(new OnConfirmListener() {
				@Override
				public void onClick(DialogInterface dialog, String inputText, int which) {
					dialog.dismiss();
					getPreferences().setSubAccount(inputText);
					finish();
				}
			});
			mDefaultListdialog.setOnCancelListener(new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mDefaultListdialog.show();
				}
			});
			mDefaultListdialog.show();
		} else {
			finish();
		}
	}

	// private void goNextPage() {
	// setResult(RESULT_OK);
	// Intent intent = new Intent(this, WebViewActivity.class);
	// String url = getNextPageUrl();
	// LogUtlis.d(getClass().getSimpleName(), "url = " + url);
	// getPreferences().setCheckedNumber(mPhoneNumber.getText().toString());
	// intent.putExtra(ExtraKeyConstant.KEY_URL, url);
	// overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	// startActivity(intent);
	// finish();
	// }

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog(true, false, null);
		if (task instanceof LoginTask)
			showDialog("登录中，请稍候……");
		else if (task instanceof CheckLoginTask)
			showDialog("申请校验码，请稍后……");
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result == TaskResult.OK) {
			if (task instanceof LoginTask) {
				setResult(RESULT_OK);
				onCompleteLogin();
				getPreferences().saveUserInfo(mPhoneNumber.getText().toString(), mAutoLogin.isChecked() ? mPassword.getText().toString() : null);
			}

			else if (task instanceof CheckLoginTask) {
				showToast("验证码发送成功……");
				saveLoginAccount(mPhoneNumber.getText().toString());
				getPreferences().saveUserInfo(mPhoneNumber.getText().toString(), mAutoLogin.isChecked() ? mPassword.getText().toString() : null);
			}
		} else {
			CommUtil.showAlert(this, null, task.getException().getMessage(), null);
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param == null)
			return;

		if (task instanceof LoginTask) {
			BusinessInformation bi = (BusinessInformation) param;
			getPreferences().setMobile(bi.getPhoneNumber());
			getPreferences().setSubAccount(bi.getSubAccount());
			getPreferences().setAuthentication(bi.getOauthInforamtion());
		} else if (task instanceof CheckLoginTask) {
			TextView viewCheckCode = (TextView) findViewById(R.id.textCheckCode);
			viewCheckCode.setText(null);
			viewCheckCode.setBackgroundDrawable((Drawable) param);
		}

	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.checkBox_autoLogin:
			// if (isChecked)
			// mSavaPassword.setChecked(true);
			getPreferences().saveAutoLogin(isChecked);
			break;
		// case R.id.checkBox_savePassword:
		// if (!isChecked)
		// mAutoLogin.setChecked(false);
		// break;
		}
	}

	private void saveLoginAccount(String account) {

		if (mArrayAccount == null || mArrayAccount.contains(account))
			return;

		mArrayAccount.add(account);

		JSONArray jsonarray = new JSONArray();
		for (String a : mArrayAccount) {
			jsonarray.put(a);
		}

		getPreferences().setLoginNames(jsonarray.toString());
	}

	private ArrayList<String> loadLoginAccount() {
		ArrayList<String> result = new ArrayList<String>();
		String strJson = getPreferences().getLoginNames();
		if (strJson != null)
			try {
				JSONArray jsonarray = new JSONArray(strJson);
				for (int i = 0; i < jsonarray.length(); ++i)
					result.add(jsonarray.getString(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return result;
	}

	/**
	 * 发送分享给好友
	 * 
	 * @param inputText
	 */
	protected void sendSmsToFriend(String inputText) {
		PendingIntent mSentIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(inputText, null, "向您推荐 实时经分系统客户端安卓版：http://183.221.33.188:8093/analysis_download.html", mSentIntent, null);
	}

	private void showToast(CharSequence chsq) {
		if (mToast == null)
			mToast = Toast.makeText(this, chsq, Toast.LENGTH_SHORT);
		else
			mToast.setText(chsq);
		mToast.show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private String getShareContent() {
		String url = getPreferences().getString(ExtraKeyConstant.APP_DOWNLOAD_INFO);
		if (url == null)
			url = getString(R.string.defaultDownloadUrl);
		else
			url = JsonUtils.parseJsonStrToObject(url, UpdateInfo.class).getDownloadUrl();
		return url;
	}
}
