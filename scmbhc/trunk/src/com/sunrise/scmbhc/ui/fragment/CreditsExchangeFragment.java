package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.AppDirConstant;
import com.sunrise.scmbhc.adapter.CreditsExpandableAdapter;
import com.sunrise.scmbhc.adapter.ViewPagerAdapter;
import com.sunrise.scmbhc.entity.ECoupon;
import com.sunrise.scmbhc.entity.bean.CreditsSmsList.CreditsExchangeItem;
import com.sunrise.scmbhc.entity.bean.CreditsSmsList.CreditsSmsObject;
import com.sunrise.scmbhc.task.ExchangeCreditsTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetPhoneNumberByImsiTask;
import com.sunrise.scmbhc.task.LoadMCreditsTask;
import com.sunrise.scmbhc.task.SmsQueryGiftForRestTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.view.TwoButtonDialog;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.HardwareUtils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.TextView;

/**
 * 积分兑换
 * 
 * @author fuheng
 * 
 */
public class CreditsExchangeFragment extends BaseFragment implements  OnChildClickListener, OnCheckedChangeListener, OnPageChangeListener {

	private static final String EXCHANGE_AIM_NUMBER = "10658999";

	private static final String ACTION_SEND_SMS_FOR_EXCHANGE = CreditsExchangeFragment.class.getName() + ".exchangeBySms";

	private static final String FORMAT_NOTICE_SMS_EXCHANGE = "您选择了兑换<font color=\"%d\">%s</font>，需要<font color=\"%s\">%s</font>积分，确认兑换？";
	private static final String FORMAT_NOTICE_E_COUPEN = "您选择了兑换<font color=\"%d\">%s</font>，需要<font color=\"%d\">%s</font>积分，确认兑换？<br><font color=\"%d\">(温馨提示：积分电子券兑换规则，请前往<a href=\"http://www.sc.10086.cn/service/promotion/index.jsp?type=7005&pageId=61#\">sc.10086.cn</a>查询)</font>";
	// "您选择了兑换" + ecoupon.getName() + "，需要" + ecoupon.getCredits() + "积分，确认兑换？";

	private static final String KEY_CREDITS = "credits";

	private TextView mViewCredits;// 积分数值

	// private View mContentView;

	private String mCredits;// 积分数

	private ExpandableListView mEexpandableListView_creditsExchange;

	private CreditsExpandableAdapter mExpandableAdapter;

	private ViewPager mViewPager;

	private RadioGroup mTabWidget;

	private int BLUE;
	private int GRAY;

	/**
	 * 用户是否符合当前imsi帐号
	 */
	private boolean isLoginUserFixImsi = false;

	private boolean isSimCardEnable = true;

	/**
	 * 联网检查imsi是否失败
	 */
	private boolean isCheckImsiFailed;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BLUE = getResources().getColor(R.color.text_color_blue);
		GRAY = getResources().getColor(R.color.text_color_gray);

		if (savedInstanceState != null) {
			mCredits = savedInstanceState.getString(KEY_CREDITS);
		} else {
			mCredits = UserInfoControler.getInstance().getMCredits();
		}
		checkSMSExchangeEnable();
	}

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_credits_exchange, container, false);

		mViewCredits = (TextView) view.findViewById(R.id.credits);
		mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

		mTabWidget = (RadioGroup) view.findViewById(R.id.tabs);
		mTabWidget.setOnCheckedChangeListener(this);

		ArrayList<View> arrayViews = new ArrayList<View>();
		// 增加说明
		WebView webview = new WebView(mBaseActivity);
		webview.setBackgroundResource(R.drawable.bg_businesslist);
		webview.loadUrl("file:///android_asset/introduceOfCreditsExchange.html");
		arrayViews.add(webview);
		// 增加兑换列表
		mEexpandableListView_creditsExchange = new ExpandableListView(mBaseActivity);
		// mExpandableAdapter = new CreditsExpandableAdapter(mBaseActivity,
		// "creditsExchange.json", mCredits);
		mExpandableAdapter = new CreditsExpandableAdapter(mBaseActivity, AppDirConstant.APP_CREDITS_EXCHANGE_JSON_NAME, mCredits);

		mEexpandableListView_creditsExchange.setAdapter(mExpandableAdapter);
		mEexpandableListView_creditsExchange.expandGroup(0);
		mEexpandableListView_creditsExchange.setOnChildClickListener(this);
		mEexpandableListView_creditsExchange.setGroupIndicator(new ColorDrawable());
		arrayViews.add(mEexpandableListView_creditsExchange);

		mViewPager.setAdapter(new ViewPagerAdapter(arrayViews));
		mViewPager.setOnPageChangeListener(this);

		return view;

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onStart() {
		super.onStart();

		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		baseActivity.setTitle(getResources().getString(R.string.mScroreTitle));

		mBaseActivity.registerReceiver(mSendReceiver, new IntentFilter(ACTION_SEND_SMS_FOR_EXCHANGE));

		if (!((BaseActivity) getActivity()).checkLoginIn(null))
			return;

		if (mCredits != null)
			mViewCredits.setText(mCredits);
		else
			registerUpdateReciever();
		
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_CREDITS, mViewCredits.getText().toString());
		super.onSaveInstanceState(outState);

	}

	public void onStop() {
		super.onStop();
		try {
			mBaseActivity.unregisterReceiver(mSendReceiver);
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
		}

		unregisterUpdateReciever();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != BaseActivity.RESULT_OK) {
			mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	private TwoButtonDialog mExitAppDialog;

	/**
	 * 兑换接口的监听
	 */
	private TaskListener mExchangeTaskListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {

		}

		@Override
		public void onPreExecute(GenericTask task) {
			initDialog(false, false, null);
			showDialog(getActivity().getResources().getString(R.string.exchangeHandling));
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			dismissDialog();
			if (result == TaskResult.OK) {
				// 处理成功，刷新积分信息
				new LoadMCreditsTask().execute();
			} else {
				if (task.isBusinessAuthenticationTimeOut())
					mBaseActivity.showReLoginDialog();
				else if (task.getException() != null && task.getException().getMessage() != null) {
					CommUtil.showAlert(getActivity(), getResources().getString(R.string.businessDealFaild), task.getException().getMessage(), null);
				}
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
	};

	/**
	 * 积分兑换电子卡预处理
	 */
	private TaskListener mInitExchangeTaskListener = new InitExchangeTaskListener();

	private class InitExchangeTaskListener implements TaskListener {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			ECoupon ecoupon = (ECoupon) param;
			CharSequence charSequence = Html.fromHtml(String.format(FORMAT_NOTICE_E_COUPEN, BLUE, ecoupon.getName(), BLUE, ecoupon.getScore(), GRAY));
			showCertainDialog(charSequence, false, ecoupon.getScore());
		}

		@Override
		public void onPreExecute(GenericTask task) {
			initDialog(false, false, null);
			showDialog(getActivity().getResources().getString(R.string.exchangeHandling));
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			dismissDialog();
			if (result != TaskResult.OK) {
				if (task.isBusinessAuthenticationTimeOut())
					mBaseActivity.showReLoginDialog();
				else if (task.getException() != null && task.getException().getMessage() != null) {
					CommUtil.showAlert(getActivity(), getResources().getString(R.string.businessDealFaild), task.getException().getMessage(), null);
				}
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
	};

	private TaskListener mGetPhoneNoByIMSIListener = new InitExchangeTaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			String imsiPhoneNo = (String) param;
			isLoginUserFixImsi = UserInfoControler.getInstance().getUserName().equals(imsiPhoneNo);
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			dismissDialog();
			if (result != TaskResult.OK) {
				isCheckImsiFailed = true;
				isLoginUserFixImsi = false;
			} else {
				isCheckImsiFailed = false;
			}
		}
	};

	/**
	 * 执行兑换
	 * 
	 * @param value
	 */
	private void doExchange(String value) {
		ExchangeCreditsTask.execute(value, mExchangeTaskListener);
	}

	private PendingIntent mSentIntent;

	private void sendMsg(String msg) {

		if (mSentIntent == null) {
			mSentIntent = PendingIntent.getBroadcast(mBaseActivity, 0, new Intent(ACTION_SEND_SMS_FOR_EXCHANGE), 0);
		}

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(EXCHANGE_AIM_NUMBER, null, msg, mSentIntent, null);
	}

	private BroadcastReceiver mSendReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (ACTION_SEND_SMS_FOR_EXCHANGE.equals(intent.getAction()))//
			{
				int resultCode = getResultCode();
				if (resultCode == BaseActivity.RESULT_OK) {
					Toast.makeText(mBaseActivity, "发送成功", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mBaseActivity, "发送失败", Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		CreditsSmsObject groupObject = (CreditsSmsObject) parent.getExpandableListAdapter().getGroup(groupPosition);
		CreditsExchangeItem ecoupon = (CreditsExchangeItem) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);

		boolean isSendSms = groupObject.getOwenAttr().getExchangeType() == 1;

		if (isSendSms) { // 短信兑换
			if (!isSimCardEnable) {// 判断是否插卡
				CommUtil.showAlert(mBaseActivity, getString(R.string.smsExchangeUnalbeTitle) + ecoupon.getTitle(), getString(R.string.smsExchangeUnalbeNotice),
						null);
				return false;
			}

			if (!isLoginUserFixImsi) {
				CommUtil.showAlert(mBaseActivity, getString(R.string.warmNotice), getString(R.string.imsiNotFixLoginUserNotice), null);
				return false;
			}

			String htmlBody = String.format(FORMAT_NOTICE_SMS_EXCHANGE, BLUE, ecoupon.getTitle(), BLUE, String.valueOf(ecoupon.getCredits()));
			if (isCheckImsiFailed)// 当检查imsi失败的时候，不确定imsi的时候，增加温馨提示
				htmlBody += getString(R.string.noticeSMSExchange);
			CharSequence charSequence = Html.fromHtml(htmlBody);

			showCertainDialog(charSequence, isSendSms, ecoupon.getEqualScor());
		}

		else
			checkEcopenExchangeEnable(ecoupon.getEqualScor());// 电子券兑换

		return false;
	}

	private void showCertainDialog(CharSequence charSequence, final boolean isSendSms, final String message) {
		mExitAppDialog = new TwoButtonDialog(mBaseActivity, new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isSendSms)// 通过短信兑换
					sendMsg(message);

				// 通过http兑换
				else
					doExchange(message);

				mExitAppDialog.dismiss();
			}

		}, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mExitAppDialog.dismiss();
			}
		});
		mExitAppDialog.setMessage(charSequence);
		mExitAppDialog.show();
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.CreditsExchangeFragment;
	}

	private void checkEcopenExchangeEnable(String exchangeCode) {
		SmsQueryGiftForRestTask.execute(exchangeCode, mInitExchangeTaskListener);
	}

	private void checkSMSExchangeEnable() {
		String imsi = HardwareUtils.getPhoneIMSI(mBaseActivity);
		isSimCardEnable = !TextUtils.isEmpty(imsi);
		if (isSimCardEnable)
			new GetPhoneNumberByImsiTask().execute(imsi, mGetPhoneNoByIMSIListener);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		int index = arg0.indexOfChild(arg0.findViewById(id));
		mViewPager.setCurrentItem(index);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int index) {
		setTabWidgetCheckIndex(index);
	}

	/**
	 * 设置tabwidget的当前序列，不启动radioGroup的监控。
	 * 
	 * @param index
	 */
	private void setTabWidgetCheckIndex(int index) {
		mTabWidget.setOnCheckedChangeListener(null);
		mTabWidget.check(mTabWidget.getChildAt(index).getId());
		mTabWidget.setOnCheckedChangeListener(this);
	}
	
	protected void refreshScoreInfo(boolean b) {
		mCredits = UserInfoControler.getInstance().getMCredits();
		mViewCredits.setText(mCredits);
		mExpandableAdapter.setUserScore(mCredits);
	}
}
