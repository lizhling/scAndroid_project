package com.sunrise.scmbhc.ui.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.task.CheckPhoneNoTopupEnableTask;
import com.sunrise.scmbhc.task.CountDownTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetNoNameTokenTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.activity.GetContactsActivity;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.ui.view.ClickAnimationListner;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.HardwareUtils;

/**
 * 充值服务
 * 
 * @author fuheng
 * 
 */
public class TopUpServeFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {

	private TextView mPhoneNum;
	/**
	 * 输入金额
	 */
	private TextView mEditTopupAmount;
	/**
	 * 支付金额
	 */
	private TextView mPayAmount;

	private GenericTask mTask;

	private final int[] RESID_RADIOBUTTON_RMB = { R.id.rmb30, R.id.rmb50, R.id.rmb100, R.id.rmb200 };

	private CompoundButton[] mRadioGroupRMB;

	private static final String FORMAT_MONEY = "%.2f元";

	private TextView mNotice10Times;// 金额10倍数提醒

	private long mInterval;// 间隔时间

	private float mRateOfDiscount = 1f;

	private static final long DURATION_INTERVAL = 15000;// 间隔时间 15秒，防止反复刷

	private static final long REFRESH_PERIOD = 1000;// 刷新时间间隔
	private static final int MAX_TOP_UP_AMOUNT = 500;// 最大充值金额。
	private static final int MIN_TOP_UP_AMOUNT = 10;// 最小充值金额。
	private static final String FORMAT_PUBLIC_BALACE = "<font color=\"%d\">%s</font><br><font color=\"%d\">%s</font>";

	private TextWatcher mWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (TextUtils.isEmpty(s))
				mPayAmount.setText("0.00");
			else {
				float sum = parse2Float(s.toString().trim()) * mRateOfDiscount;
				mPayAmount.setText(String.format(FORMAT_MONEY, sum));
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	private TextView mBtnPayCertain;// 确认按钮

	private AnimationDrawable mProgressDialog;
	private TextView mTag1;
	private TextView mTag2;
	private TextView mTag3;
	private TextView mBtHistory;

	/**
	 * 点击充值历史
	 */
	private OnClickListener mClickListenerForShowHistory = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			BaseFragment fragment = new TopUpHistoryFragment();
			fragment.startFragment(mBaseActivity, R.id.fragmentContainer);
		}
	};
	private View mHistoryView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mProgressDialog = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_loadingbar);

		if (!TextUtils.isEmpty(App.sSettingsPreferences.getTopupRates()))
			mRateOfDiscount = Float.parseFloat(App.sSettingsPreferences.getTopupRates());
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_top_up_serve, container, false);

		mPhoneNum = (TextView) view.findViewById(R.id.editText_phoneNumber);
		mEditTopupAmount = (TextView) view.findViewById(R.id.editText_topupAmount);
		mEditTopupAmount.addTextChangedListener(mWatcher);
		mPayAmount = (TextView) view.findViewById(R.id.textView_payAmount);

		mNotice10Times = (TextView) view.findViewById(R.id.notice_10_times);

		view.findViewById(R.id.button_addressList).setOnClickListener(this);
		mBtnPayCertain = (TextView) view.findViewById(R.id.button_payCertain);
		mBtnPayCertain.setOnClickListener(this);

		mRadioGroupRMB = new CompoundButton[RESID_RADIOBUTTON_RMB.length];
		for (int i = 0; i < RESID_RADIOBUTTON_RMB.length; ++i) {
			mRadioGroupRMB[i] = (CompoundButton) view.findViewById(RESID_RADIOBUTTON_RMB[i]);
			mRadioGroupRMB[i].setOnCheckedChangeListener(this);
		}

		mTag1 = (TextView) view.findViewById(R.id.tag1);
		mTag2 = (TextView) view.findViewById(R.id.tag2);
		mTag3 = (TextView) view.findViewById(R.id.tag3);

		if (savedInstanceState != null)
			mInterval = savedInstanceState.getLong("mInterval");

		mHistoryView = view.findViewById(R.id.linearLayout_History);

		mBtHistory = (TextView) view.findViewById(R.id.history);
		mBtHistory.getPaint().setUnderlineText(true);
		mBtHistory.setOnClickListener(this);

		// 利率显示
		TextView textview_rates = (TextView) view.findViewById(R.id.textView_rates);
		if (mRateOfDiscount == 1) {
			textview_rates.setVisibility(View.GONE);
		} else {
			textview_rates.setVisibility(View.VISIBLE);
			textview_rates.setText(CommUtil.subZeroAndDot(String.format("%.2f", mRateOfDiscount * 10)) + "折");
		}

		return view;
	}

	public void onStart() {
		super.onStart();
		mBaseActivity.setTitle(getResources().getString(R.string.topUpServe));
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		if (UserInfoControler.getInstance().checkUserLoginIn()) {// 如果用户登录了
			mPhoneNum.setText(UserInfoControler.getInstance().getUserName());// 默认充值手机号为本机

			String pBalance = UserInfoControler.getInstance().getPhoneCurMsg().getPublicBalance();
			String balace = UserInfoControler.getInstance().getCurBalance();
			if (TextUtils.isEmpty(pBalance)) {
				mTag2.setText(balace);
				mTag1.setText(R.string.tag_balace);
			} else {
				mTag1.setText(Html.fromHtml(String.format(FORMAT_PUBLIC_BALACE, Color.DKGRAY, getString(R.string.tag_balace), mTag1.getTextColors()
						.getDefaultColor(), getString(R.string.tag_pbalace))));
				mTag2.setText(Html.fromHtml(String.format(FORMAT_PUBLIC_BALACE, mTag2.getTextColors().getDefaultColor(), balace, Color.GRAY, pBalance
						+ getString(R.string.yuan))));
			}

			mTag2.setOnClickListener(null);
			mTag2.getPaint().setUnderlineText(false);
			mTag2.setTextColor(getResources().getColor(R.color.dark_red));

			mTag3.setVisibility(View.GONE);

			mHistoryView.setVisibility(View.VISIBLE);

		} else {
			mTag2.setText(R.string.login);
			mTag2.setOnClickListener(this);
			mTag2.getPaint().setUnderlineText(true);
			mTag2.setTextColor(getResources().getColor(R.color.bg_color_light_blue));

			mTag1.setText(R.string.please);

			mTag3.setVisibility(View.VISIBLE);

			mHistoryView.setVisibility(View.GONE);
		}

		checkInterval();

		registerUpdateReciever();
	}

	public void onResume() {
		super.onResume();
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putLong("mInterval", mInterval);// 保留上次使用此事件的时间

		super.onSaveInstanceState(outState);
	}

	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();

		unregisterUpdateReciever();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button_addressList:
			startActivityForResult(new Intent(mBaseActivity, GetContactsActivity.class), GetContactsActivity.REQUEST_CONTACT);
			break;
		case R.id.button_payCertain:
			goCertain();
			break;
		case R.id.tag2:
			new ClickAnimationListner(arg0, mLoginClickListener, R.anim.click_scale).startAnim();
			break;
		case R.id.history:
			new ClickAnimationListner(arg0, mClickListenerForShowHistory, R.anim.click_scale).startAnim();
			break;
		default:
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == BaseActivity.RESULT_OK) {
			String str = data.getStringExtra(ExtraKeyConstant.KEY_PHONE_NUMBER);
			if (!TextUtils.isEmpty(str))
				mPhoneNum.setText(str);
		}
	}

	private void goCertain() {

		String phoneNum = mPhoneNum.getText().toString();
		// 检测号码是否为空
		if (TextUtils.isEmpty(phoneNum)) {
			Toast.makeText(mBaseActivity, R.string.hintInputMobileNumber, Toast.LENGTH_SHORT).show();
			mPhoneNum.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			mPhoneNum.requestFocus();
			return;
		}

		// 检测号码是否正确
		if (!CommUtil.isMobilePhone(phoneNum)) {
			Toast.makeText(mBaseActivity, R.string.inputCorrectPhoneNumber, Toast.LENGTH_SHORT).show();
			mPhoneNum.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			mPhoneNum.requestFocus();
			return;
		}

		// 检测是否输入金额
		String payAmount = mPayAmount.getText().toString();
		if (TextUtils.isEmpty(payAmount) || payAmount.startsWith("0.00")) {
			Toast.makeText(mBaseActivity, R.string.noticeInputPayAmount, Toast.LENGTH_SHORT).show();
			mPayAmount.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			mEditTopupAmount.requestFocus();
			return;
		}

		// 检测10的倍数
		if (!check10TimsOfPayAmount(mEditTopupAmount.getText().toString())) {
			mEditTopupAmount.requestFocus();
			mNotice10Times.setText(R.string.topupNotice10times);
			mNotice10Times.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			return;
		}

		String price = mEditTopupAmount.getText().toString();
		// 检测充值范围
		if (!check10To500Range(price)) {
			mEditTopupAmount.requestFocus();
			mNotice10Times.setText(R.string.topupNoticeRange);
			mNotice10Times.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			return;
		}

		String token = App.sSettingsPreferences.getNonameToken();
		String user = UserInfoControler.getInstance().checkUserLoginIn() ? UserInfoControler.getInstance().getUserName() : null;
		mTask = new CheckPhoneNoTopupEnableTask().execute(phoneNum, user, price, token, new TaskListener() {
			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				String url = (String) param;
				if (TextUtils.isEmpty(url))
					goNextPage();
				else {
					String phoneNum = mPhoneNum.getText().toString();
					String price = mEditTopupAmount.getText().toString();
					price = String.valueOf(Integer.parseInt(price) * 100);
					startWebPage(getString(R.string.topUpCertain), url + "&phoneNum=" + phoneNum + "&price=" + price);
				}
			}

			@Override
			public void onPreExecute(GenericTask task) {
				// if (mDialog == null) {
				// mDialog = new ProgressDialog(getActivity());
				// mDialog.setCanceledOnTouchOutside(false);
				// mDialog.setCancelable(true);
				// mDialog.setOnCancelListener(mCancellistener);
				// }
				// mDialog.setMessage(getActivity().getResources().getString(R.string.checkPhoneTopupEnable));
				// mDialog.show();

				mBtnPayCertain.setCompoundDrawablesWithIntrinsicBounds(mProgressDialog, null, null, null);
				mProgressDialog.start();
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				dismissDialog();

				if (mProgressDialog.isRunning()) {
					mBtnPayCertain.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
					mProgressDialog.stop();
				}

				if (result == TaskResult.OK) {

				} else {
					if (task.getException() != null && task.getException().getMessage() != null) {
						if (isAdded()) {
							CommUtil.showAlert(getActivity(), getResources().getString(R.string.warmNotice), task.getException().getMessage(), null);
						}
					}
					// mPhoneNum.setText(null);
					mPhoneNum.requestFocus();
				}
			}

			@Override
			public void onCancelled(GenericTask task) {
				dismissDialog();

				if (mProgressDialog.isRunning()) {
					mBtnPayCertain.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
					mProgressDialog.stop();
				}
			}

			@Override
			public String getName() {
				return getResources().getString(R.string.checkPhoneTopupEnable);
			}
		});
	}

	/**
	 * 前往下一页
	 */
	private void goNextPage() {
		onGetNonameToken();
		mInterval = System.currentTimeMillis() - 1;

		BaseFragment fragment = new TopUpCertainFragment();
		Bundle bundle = new Bundle();
		bundle.putString(App.ExtraKeyConstant.KEY_PHONE_NUMBER, mPhoneNum.getText().toString());
		bundle.putString(App.ExtraKeyConstant.KEY_TOP_UP_AMOUNT, mEditTopupAmount.getText().toString());
		bundle.putString(App.ExtraKeyConstant.KEY_PAY_AMOUNT, mPayAmount.getText().toString());
		fragment.setArguments(bundle);
		fragment.startFragment(mBaseActivity, R.id.fragmentContainer);
	}

	private void startWebPage(String IndicatorStr, String url) {
		Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		intent.putExtra(ExtraKeyConstant.KEY_TITLE, IndicatorStr);
		intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT, WebViewFragment.class);
		BusinessMenu businessMenu = new BusinessMenu();
		businessMenu.setServiceUrl(url);
		businessMenu.setName(IndicatorStr);
		Bundle bundle = new Bundle();
		bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, true);
		startActivityForResult(intent, 10);
	}

	/**
	 * 获取匿名token方法
	 */
	private void onGetNonameToken() {
		String imei = HardwareUtils.getPhoneIMEI(mBaseActivity);
		GetNoNameTokenTask.excuet(imei, null, null, null, null);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (!arg1)
			return;

		for (CompoundButton button : mRadioGroupRMB) {
			if (button != arg0) {
				button.setChecked(false);
				continue;
			}

			String temp = arg0.getText().toString().trim();
			if (temp.contains("元"))
				temp = temp.substring(0, temp.indexOf("元"));
			mEditTopupAmount.setText(temp);
		}
	}

	private OnCancelListener mCancellistener = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface arg0) {
			if (mTask != null)
				mTask.cancle();
		}
	};

	private float parse2Float(String temp) {
		if (temp.contains("元"))
			temp = temp.substring(0, temp.indexOf("元"));
		return Float.parseFloat(temp);
	}

	/**
	 * 10的倍数检测
	 * 
	 * @param string
	 * @return
	 */
	private boolean check10TimsOfPayAmount(String string) {
		if (TextUtils.isEmpty(string))
			return false;

		int a = Integer.parseInt(string);
		if (a % 10 == 0)
			return true;
		else
			return false;
	}

	private boolean check10To500Range(String string) {
		if (TextUtils.isEmpty(string))
			return false;

		int a = Integer.parseInt(string);
		if (a > MAX_TOP_UP_AMOUNT || a < MIN_TOP_UP_AMOUNT)
			return false;
		return true;
	}

	/**
	 * 整理间隔时间，防止反复点击
	 */
	private void checkInterval() {
		if (System.currentTimeMillis() - mInterval < DURATION_INTERVAL) {
			mTask = CountDownTask.execute((int) (DURATION_INTERVAL / (System.currentTimeMillis() - mInterval)), REFRESH_PERIOD, new TaskListener() {
				@Override
				public String getName() {
					return null;
				}

				@Override
				public void onPreExecute(GenericTask task) {
					// TODO 确认按钮不可点击
					mBtnPayCertain.setEnabled(false);
				}

				@Override
				public void onPostExecute(GenericTask task, TaskResult result) {
					// TODO Auto-generated method stub
					mBtnPayCertain.setEnabled(true);
					mBtnPayCertain.setText(R.string.clickPay);
				}

				@Override
				public void onProgressUpdate(GenericTask task, Object param) {
					// TODO Auto-generated method stub
					long time = Long.parseLong(param.toString());
					mBtnPayCertain.setText(getString(R.string.clickPay) + '(' + time + ')');
				}

				@Override
				public void onCancelled(GenericTask task) {
					if (mBtnPayCertain != null) {
						mBtnPayCertain.setEnabled(true);
						mBtnPayCertain.setText(R.string.clickPay);
					}
				}
			});
		}
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.TopUpServeFragment;
	}

	protected void refreshRemanderView(boolean b) {
		if (!b)
			return;
		
		String pBalance = UserInfoControler.getInstance().getPhoneCurMsg().getPublicBalance();
		String balace = UserInfoControler.getInstance().getCurBalance();
		if (TextUtils.isEmpty(pBalance)) {
			mTag2.setText(balace);
			mTag1.setText(R.string.tag_balace);
		} else {
			mTag1.setText(Html.fromHtml(String.format(FORMAT_PUBLIC_BALACE, Color.DKGRAY, getString(R.string.tag_balace), mTag1.getTextColors()
					.getDefaultColor(), getString(R.string.tag_pbalace))));
			mTag2.setText(Html.fromHtml(String.format(FORMAT_PUBLIC_BALACE, mTag2.getTextColors().getDefaultColor(), balace, Color.GRAY, pBalance
					+ getString(R.string.yuan))));
		}
	}
}
