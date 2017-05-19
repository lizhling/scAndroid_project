package com.sunrise.marketingassistant.fragment;

import com.sunrise.marketingassistant.activity.BaseActivity;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.view.DefaultProgressDialog;

import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	private DefaultProgressDialog mDialog;
	protected BaseActivity mBaseActivity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBaseActivity = (BaseActivity) getActivity();
		LogUtlis.d("测试", getClass().getSimpleName() + " . " + new Throwable().getStackTrace()[1].getMethodName());
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtlis.d("测试", getClass().getSimpleName() + " . " + new Throwable().getStackTrace()[1].getMethodName());
		return _onCreateView(inflater, container, savedInstanceState);
	}

	protected abstract View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	public void startFragment(FragmentActivity context, int fragmentContainer) {
		FragmentManager fragmentManager = context.getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();

		for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i)
			fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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

	/**
	 * @return true将仅作用在本层。
	 */
	public boolean onBackPressed() {
		return false;
	}

	public void onStart() {
		super.onStart();
		LogUtlis.d("测试", getClass().getSimpleName() + " . " + new Throwable().getStackTrace()[1].getMethodName());
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtlis.d("测试", getClass().getSimpleName() + " . " + new Throwable().getStackTrace()[1].getMethodName());
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtlis.d("测试", getClass().getSimpleName() + " . " + new Throwable().getStackTrace()[1].getMethodName());
	}

	public void onStop() {
		super.onStop();
		LogUtlis.d("测试", getClass().getSimpleName() + " . " + new Throwable().getStackTrace()[1].getMethodName());
		dismissDialog();
	}

	public void onDestroy() {
		LogUtlis.d("测试", getClass().getSimpleName() + " . " + new Throwable().getStackTrace()[1].getMethodName());
		super.onDestroy();
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

	protected Preferences getPreferences() {
		return Preferences.getInstance(mBaseActivity);
	}
}
