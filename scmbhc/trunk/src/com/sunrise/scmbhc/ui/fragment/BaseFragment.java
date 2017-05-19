package com.sunrise.scmbhc.ui.fragment;

import com.starcpt.analytics.PhoneClickAgent;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppActionConstant;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.activity.LoginActivity;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.ui.view.DefaultProgressDialog;
import com.sunrise.scmbhc.utils.CommUtil;

import android.content.DialogInterface.OnCancelListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	private DefaultProgressDialog mDialog;
	protected BaseActivity mBaseActivity;
	private UpdateReceiver updateReceiver;

	protected final OnClickListener mLoginClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivityForResult(new Intent(mBaseActivity, LoginActivity.class), 0);
		}
	};

	protected final OnClickListener mQRCodeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 防快速点击双击判断
			if (CommUtil.isFastDoubleClick()) {
				return;
			}
			// Intent intent = new Intent(mBaseActivity,
			// MipcaActivityCapture.class);
			// startActivity(intent);
			Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
			intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, FriendsShareFragment.class);
			mBaseActivity.startActivity(intent);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBaseActivity = (BaseActivity) getActivity();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return _onCreateView(inflater, container, savedInstanceState);
	}

	protected abstract View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	public void startFragment(FragmentActivity context, int fragmentContainer) {
		FragmentManager fragmentManager = context.getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.replace(fragmentContainer, this);
		ft.addToBackStack(null);
		ft.commit();
	}

	public int finish(FragmentActivity context) {
		FragmentManager fragmentManager = context.getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 1) {
			fragmentManager.popBackStack();
		}

		return fragmentManager.getBackStackEntryCount();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	public void onStart() {
		super.onStart();
		mBaseActivity.setCurrentFragment(this);
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	abstract int getClassNameTitleId();

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		PhoneClickAgent.onPageStart(getString(getClassNameTitleId()));
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		PhoneClickAgent.onPageEnd(mBaseActivity, getString(getClassNameTitleId()));
	}

	public void onStop() {
		super.onStop();
		dismissDialog();
	}

	protected void initDialog(boolean cancelAble, boolean cancelOnTouchOutside, OnCancelListener cancellistener) {
		if (mDialog == null) {
			mDialog = new DefaultProgressDialog(mBaseActivity);
		}
		mDialog.setCancelable(cancelAble);
		mDialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
		mDialog.setOnCancelListener(cancellistener);
	}

	protected void initDialog() {
		if (mDialog == null) {
			mDialog = new DefaultProgressDialog(mBaseActivity);
		}
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

	protected void registerUpdateReciever() {

		if (updateReceiver == null)
			updateReceiver = new UpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppActionConstant.ACTION_REFRESH);
		mBaseActivity.registerReceiver(updateReceiver, filter);
	}

	protected void unregisterUpdateReciever() {
		if (updateReceiver != null)
			mBaseActivity.unregisterReceiver(updateReceiver);
	}

	private class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AppActionConstant.ACTION_REFRESH)) {

				byte Case = intent.getByteExtra(ExtraKeyConstant.KEY_CASE, (byte) 0);
				switch (Case) {
				case AppActionConstant.STATE_COMPLETE_PHONE_FREE_QUERY:
					refreshFreeCondition(true);
					break;
				case AppActionConstant.STATE_COMPLETE_PHONE_CURRENT_MSG:
					refreshRemanderView(true);
					break;
				case AppActionConstant.STATE_COMPLETE_ADDITIONAL_TRAFFIC_INFO:
					refreshAddtionalTrafficView(true);
					break;
				case AppActionConstant.STATE_COMPLETE_GET_CREDITS:
					refreshScoreInfo(true);
					break;
				case AppActionConstant.STATE_COMPLETE_GET_USER_BASE_INFO:
					refreshUserBaseInfo(true);
					break;
				default:
					refreshUserInfo();
					break;
				}
			}

		}

	};

	protected void refreshAddtionalTrafficView(boolean b) {

	}

	protected void refreshUserBaseInfo(boolean b) {

	}

	protected void refreshUserInfo() {

	}

	protected void refreshScoreInfo(boolean b) {

	}

	protected void refreshRemanderView(boolean b) {

	}

	protected void refreshFreeCondition(boolean b) {

	}
}
