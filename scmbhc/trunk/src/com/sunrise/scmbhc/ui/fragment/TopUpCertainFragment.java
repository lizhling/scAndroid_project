package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.adapter.PayWayAdapter;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.PaymentContainer;
import com.sunrise.scmbhc.entity.PaymentContainer.Payment;
import com.sunrise.scmbhc.task.CountDownTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.RequestForPayActionTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.HardwareUtils;

import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 充值服务
 * 
 * @author fuheng
 * 
 */
public class TopUpCertainFragment extends BaseFragment implements ExtraKeyConstant, OnClickListener, OnItemClickListener, TaskListener {

	private ListView mListViewPayWay;
	private PayWayAdapter mAdapter;
	private Bundle mBundle;
	private ArrayList<Payment> mArrayPayway;
	private TextView mBtnSubmit;
	private OnCancelListener mCancellistener = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface arg0) {
			if (mTask != null)
				mTask.cancle();
		}
	};

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			savedInstanceState = getArguments();
		}

		mBundle = savedInstanceState;

		View view = inflater.inflate(R.layout.fragment_top_up_certain, container, false);

		TextView phoneNum = (TextView) view.findViewById(R.id.phoneNum);
		TextView topupSum = (TextView) view.findViewById(R.id.topupSum);
		TextView city = (TextView) view.findViewById(R.id.city);
		TextView payAmount = (TextView) view.findViewById(R.id.textView_payAmount);

		if (mBundle != null) {
			phoneNum.setText(mBundle.getString(KEY_PHONE_NUMBER));
			topupSum.setText(mBundle.getString(KEY_TOP_UP_AMOUNT));
			payAmount.setText(mBundle.getString(KEY_PAY_AMOUNT));
		}

		mListViewPayWay = (ListView) view.findViewById(R.id.payWay);
		mListViewPayWay.setOnItemClickListener(this);
		mArrayPayway = new ArrayList<PaymentContainer.Payment>();
		Payment object = new Payment("{\"@account\":\"false\",\"@icon\":\"\",\"@id\":\"100100\",\"@name\":\"支付宝WAP\"}");
		mArrayPayway.add(object);
		mAdapter = new PayWayAdapter(mBaseActivity, mArrayPayway);
		mListViewPayWay.setAdapter(mAdapter);

		mBtnSubmit = (TextView) view.findViewById(R.id.submit);
		mBtnSubmit.setOnClickListener(this);

		return view;
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(mBundle);
	}

	public void onStart() {
		super.onStart();
		mBaseActivity.setTitle(getResources().getString(R.string.topUpCertain));
	}

	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.submit:
			int selectIndex = mListViewPayWay.getSelectedItemPosition();
			if (selectIndex < 0) {
				mListViewPayWay.setSelection(0);
				selectIndex = 0;
			}
			//
			String user = UserInfoControler.getInstance().checkUserLoginIn() ? UserInfoControler.getInstance().getUserName() : "00000000000";
			String uuid = HardwareUtils.getPhoneIMSI(mBaseActivity) + CommUtil.getUniqueID(mBaseActivity);
			mTask = RequestForPayActionTask.execute(user, mBundle.getString(KEY_PHONE_NUMBER),
					(int) CommUtil.parse2Float(mBundle.getString(KEY_TOP_UP_AMOUNT)), CommUtil.parse2Float(mBundle.getString(KEY_PAY_AMOUNT)), mArrayPayway
							.get(selectIndex).getId(), HardwareUtils.getPhoneIMEI(mBaseActivity), uuid, HardwareUtils.getLocalIpAddress(), this);
			// startPayPage();
			break;

		default:
			break;
		}
	}

	/**
	 * 前往支付页面
	 */
	private void startPayPage(String url) {
		Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		String IndicatorStr = "支付平台";
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// if (mAdapter != null)
		// mAdapter.setSelectPosition(arg2);
	}

	private void initPayWay(PaymentContainer param) {
		PaymentContainer paymentcontainer = (PaymentContainer) param;
		mAdapter = new PayWayAdapter(mBaseActivity, paymentcontainer.getPayment());
		mListViewPayWay.setAdapter(mAdapter);

		// CommUtil.expandListView(mAdapter, mListViewPayWay);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog(true, false, mCancellistener);
		showDialog(getActivity().getResources().getString(R.string.waitForRequestPay));

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish(mBaseActivity);
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();

		if (result == TaskResult.OK) {
			App.sSettingsPreferences.setNonameToken("abce&*3s");
		} else if (task.getException() != null && !TextUtils.isEmpty(task.getException().getMessage())) {
			CommUtil.showAlert(getActivity(), getString(R.string.failed), task.getException().getMessage(), null);
			mInterval = System.currentTimeMillis() - 1;
			checkInterval();
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {

		if (param == null)
			return;

		startPayPage((String) param);
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	private long mInterval;// 间隔时间

	private static final long DURATION_INTERVAL = 15000;// 间隔时间 15秒，防止反复刷

	private static final long REFRESH_PERIOD = 1000;// 刷新时间间隔
	private GenericTask mTask;

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
					mBtnSubmit.setEnabled(false);
				}

				@Override
				public void onPostExecute(GenericTask task, TaskResult result) {
					// TODO Auto-generated method stub
					mBtnSubmit.setEnabled(true);
					mBtnSubmit.setText(R.string.clickPay);
				}

				@Override
				public void onProgressUpdate(GenericTask task, Object param) {
					// TODO Auto-generated method stub
					long time = Long.parseLong(param.toString());
					mBtnSubmit.setText(getString(R.string.clickPay) + '(' + time + ')');
				}

				@Override
				public void onCancelled(GenericTask task) {
					if (mBtnSubmit != null) {
						mBtnSubmit.setEnabled(true);
						mBtnSubmit.setText(R.string.clickPay);
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
		return R.string.TopUpCertainFragment;
	}

}
