package com.sunrise.marketingassistant.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.adapter.ForeWetherAdapter;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.task.CheckLoginTask;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetRegionOrgIdByLoginNoTask;
import com.sunrise.marketingassistant.task.GetSubAccountTask;
import com.sunrise.marketingassistant.task.LoginTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.utils.HardwareUtils;
import com.sunrise.marketingassistant.view.DefaultDialog.OnConfirmListener;
import com.sunrise.marketingassistant.view.DefaultInputDialog;
import com.sunrise.marketingassistant.view.DefaultListDialog;
import com.sunrise.marketingassistant.view.EditTextWacher;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements ExtraKeyConstant, OnClickListener, TaskListener, OnCheckedChangeListener, AMapLocalWeatherListener {

	public static final String KEY_IS_START_FOR_RESULT = "is start for result";

	private AutoCompleteTextView mPhoneNumber;
	private EditText mPassword;
	protected CheckBox mAutoLogin;
	private GenericTask mTask;
	private View mBtnDeleteUserName;
	private View mBtnDeletePassword;
	private ArrayList<String> mArrayAccount;
	private TextView mEditCheckCode;

	/** 高德天气管理器 */
	private LocationManagerProxy aMapLocManager;
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
			mAutoLogin.setChecked(com.sunrise.marketingassistant.cache.preference.Preferences.getInstance(this).isAutoLogin());
		}
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.textCheckCode).setOnClickListener(this);
		{// 显示版本号
			TextView text = (TextView) findViewById(R.id.versionName);
			text.setText("V" + App.sAppVersionName);
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
		// if (App.isTest) {
		// mPhoneNumber.setText("luoxingui");
		// mPassword.setText("Lxga#123");
		// } else {
		mPhoneNumber.setText(Preferences.getInstance(this).getUserName());
		mPassword.setText(Preferences.getInstance(this).getPassword());
		// }

		// { // 更新二维码
		// ImageView imageview = (ImageView)
		// findViewById(R.id.imageView_twodimensioncode);
		// imageview.setImageBitmap(CommUtil.createQRCode(getShareContent(),
		// BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher)));
		// }
		setTitle(getString(R.string.login));

		initWether();
	}

	protected void onStart() {
		super.onStart();
		if (Preferences.getInstance(this).isAutoLogin())
			checkCode();
	}

	protected void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	@Override
	protected void onDestroy() {
		aMapLocManager.destroy();
		aMapLocManager = null;
		super.onDestroy();
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

	private void initWether() {
		aMapLocManager = LocationManagerProxy.getInstance(this);
		aMapLocManager.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);
		aMapLocManager.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_FORECAST, this);
	}

	/**
	 * 分享通过短信的对话框
	 */
	private void shareBySmsDialog() {
		DefaultInputDialog dialog = new DefaultInputDialog(this).setDilaogTitle("分享内容给好友");
		dialog.setInputType(InputType.TYPE_CLASS_PHONE).setInputFieldSingleLine(true);
		dialog.setInputFieldHint("请输入手机号");
		dialog.setMessageIcon(R.drawable.ic_launcher);// TODO 分享图片
		dialog.setMessageIcon(CommUtil.createQRCode(getString(R.string.app_copyright), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)));
		dialog.setOnConfirmListener(new OnConfirmListener() {

			@Override
			public void onClick(DialogInterface dialog, Object resultObj, int which) {
				String inputText = (String) resultObj;
				if (!CommUtil.isMobilePhone(inputText)) {
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
		if (TextUtils.isEmpty(account)) {
			CommUtil.showAlert(this, null, getString(R.string.askInputAccount), getString(R.string.confirm), null);
			mPhoneNumber.requestFocus();
			return;
		}
		String password = mPassword.getText().toString();
		if (TextUtils.isEmpty(password)) {
			CommUtil.showAlert(this, null, getString(R.string.askInputPssword), getString(R.string.confirm), null);
			mPhoneNumber.requestFocus();
			return;
		}
		if (mTask != null)
			mTask.cancle();
		mTask = new CheckLoginTask().execute(account, password, HardwareUtils.getPhoneIMSI(this), HardwareUtils.getPhoneIMEI(this), this);
	}

	public void login() {
		String account = mPhoneNumber.getText().toString();

		if (TextUtils.isEmpty(account)) {
			CommUtil.showAlert(this, null, getString(R.string.askInputAccount), getString(R.string.confirm), null);
			mPhoneNumber.requestFocus();
			return;
		}

		// TextView viewCheckCode = (TextView) findViewById(R.id.textCheckCode);
		// if (!TextUtils.isEmpty(viewCheckCode.getText())) {
		// CommUtil.showAlert(this, null, "请先获取校验码……", null);
		// return;
		// }

		String checkcode = mEditCheckCode.getText().toString();
		if (TextUtils.isEmpty(checkcode)) {
			CommUtil.showAlert(this, null, getString(R.string.askInputPssword), getString(R.string.confirm), null);
			mEditCheckCode.requestFocus();
			return;
		}

		mTask = new LoginTask().execute(account, checkcode, this);
	}

	private void doGetSubAccount() {
		String account = mPhoneNumber.getText().toString();
		mTask = new GetSubAccountTask().execute(account, this);
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
				public void onClick(DialogInterface dialog, Object resultObj, int which) {
					String inputText = (String) resultObj;
					dialog.dismiss();
					getPreferences().saveSubAccount(inputText);
					doGetGroupId(inputText);
				}
			});
			mDefaultListdialog.show();
			return;
		}

		if (TextUtils.isEmpty(subAccount))
			CommUtil.showAlert(this, null, "没有获取到从账号，请联系管理员……", "退出", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					finish();
				}
			});
		else {
			getPreferences().saveSubAccount(subAccount);
			doGetGroupId(subAccount);
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

	private void doGetGroupId(String account) {
		mTask = new GetRegionOrgIdByLoginNoTask().execute(account, this);
	}

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
			showDialog("申请校验码，请稍候……");
		else if (task instanceof GetSubAccountTask) {
			showDialog("获取用户信息，请稍候……");
		} else if (task instanceof GetRegionOrgIdByLoginNoTask) {
			showDialog("获取用户信息，请稍候……");
		}
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result == TaskResult.OK) {
			if (task instanceof LoginTask) {
				getPreferences().saveUserName(mPhoneNumber.getText().toString());
				if (mAutoLogin.isChecked())
					getPreferences().savePassword(mPassword.getText().toString());
				doGetSubAccount();
			}

			else if (task instanceof CheckLoginTask) {
				showToast("验证码发送成功……");
				saveLoginAccount(mPhoneNumber.getText().toString());

				getPreferences().saveUserName(mPhoneNumber.getText().toString());
				if (mAutoLogin.isChecked())
					getPreferences().savePassword(mPassword.getText().toString());

				mEditCheckCode.requestFocus();
			}

			else if (task instanceof GetSubAccountTask) {
				onCompleteLogin();
			}

			else if (task instanceof GetRegionOrgIdByLoginNoTask) {
				setResult(RESULT_OK);
				finish();
			}
		} else {
			CommUtil.showAlert(this, null, task.getException().getMessage(), getString(R.string.Return), null);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param == null)
			return;

		if (task instanceof LoginTask) {
			String[] sb = ((String) param).split("&");
			getPreferences().saveMobile(sb[0]);
			getPreferences().saveAuthentication(sb[1]);
		} else if (task instanceof CheckLoginTask) {
			TextView viewCheckCode = (TextView) findViewById(R.id.textCheckCode);
			viewCheckCode.setText(null);
			viewCheckCode.setBackgroundDrawable((Drawable) param);
		} else if (task instanceof GetSubAccountTask) {
			getPreferences().saveSubAccount((String) param);
		} else if (task instanceof GetRegionOrgIdByLoginNoTask) {
			getPreferences().saveGroupId((String) param);
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
	private void sendSmsToFriend(String inputText) {
		PendingIntent mSentIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);

		SmsManager smsManager = SmsManager.getDefault();

		// TODO
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
		String url = getPreferences().getString(KEY_APP_DOWNLOAD_INFO);
		if (url == null)
			url = getString(R.string.defaultDownloadUrl);
		else
			url = JsonUtils.parseJsonStrToObject(url, UpdateInfo.class).getDownloadUrl();
		return url;
	}

	@Override
	public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {
		GridView listview = (GridView) findViewById(R.id.list_weatherForecase);
		ArrayList<AMapLocalDayWeatherForecast> wetherInfo = new ArrayList<AMapLocalDayWeatherForecast>();
		wetherInfo.addAll(arg0.getWeatherForecast());
		{
			AMapLocalDayWeatherForecast obj = wetherInfo.remove(0);
			TextView tempRange = (TextView) findViewById(R.id.textView_tempRange);
			float[] hsv = { 1, 1f, 1f };
			int color1 = ForeWetherAdapter.getTemperatureColor(CommUtil.parse2Float(obj.getNightTemp()), hsv);
			int color2 = ForeWetherAdapter.getTemperatureColor(CommUtil.parse2Float(obj.getDayTemp()), hsv);
			tempRange.setText(Html.fromHtml(String.format("<font color='%d'>%s°</font>~<font color='%d'>%s°</font>", color1, obj.getNightTemp(), color2,
					obj.getDayTemp())));
		}
		listview.setAdapter(new ForeWetherAdapter(getThis(), wetherInfo));
	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive arg0) {
		TextView city = (TextView) findViewById(R.id.textView_city);
		ImageView wether = (ImageView) findViewById(R.id.textView_wether);
		TextView temperature = (TextView) findViewById(R.id.textView_temperature);
		TextView tempRange = (TextView) findViewById(R.id.textView_tempRange);

		String strcity = arg0.getCity();
		if (strcity.contains("攀")) {
			city.setText(strcity.subSequence(0, 3));
			city.setTextSize(33);
		} else
			city.setText(strcity.subSequence(0, 2));

		wether.setImageResource(ForeWetherAdapter.getWetherResId(arg0.getWeather()));
		temperature.setText(arg0.getTemperature() + '°');
		{
			float[] hsv = { 1, 1f, 1f };
			temperature.setTextColor(ForeWetherAdapter.getTemperatureColor(CommUtil.parse2Float(arg0.getTemperature()), hsv));
		}

		((View) city.getParent()).setVisibility(View.VISIBLE);

		{
			CircleAniamtion ca = new CircleAniamtion(wether, temperature, tempRange);
			Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
			anim.setDuration(2000);
			ca.setAnimation(anim);
			ca.start();
		}
	}

	class CircleAniamtion implements AnimationListener {

		private View[] mViews;
		private Animation mAnim;
		private int index;

		public CircleAniamtion(View... views) {
			mViews = views;
		}

		public void setAnimation(Animation anim) {
			mAnim = anim;
			mAnim.setAnimationListener(this);
		}

		public void start() {
			if (mViews == null || mViews.length == 0)
				return;

			if (mAnim == null) {
				mAnim = new AlphaAnimation(0.2f, 1);
				mAnim.setDuration(1000);
				mAnim.setRepeatCount(1);
				mAnim.setRepeatMode(Animation.RESTART);
			}

			if (index != 0) {
				mViews[index].setAnimation(null);
				index = 0;
			}

			mViews[index].setAnimation(mAnim);

			mAnim.start();
		}

		@SuppressLint("NewApi")
		public void stop() {
			if (mViews == null || mViews.length == 0)
				return;

			mAnim.cancel();
			mViews[index].setAnimation(null);
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			mViews[index++].setAnimation(null);
			index %= mViews.length;
			mViews[index].setAnimation(arg0);
			// mAnim.reset();
			arg0.start();
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {

		}

		@Override
		public void onAnimationStart(Animation arg0) {

		}
	}

}
