package com.sunrise.marketingassistant.activity;

import java.util.ArrayList;

import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.view.DefaultProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author fuheng
 * 
 */
public abstract class BaseActivity extends FragmentActivity {
	private DefaultProgressDialog mDialog;
	private static final ArrayList<BaseActivity> sActivityList = new ArrayList<BaseActivity>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sActivityList.add(this);
		LogUtlis.i(getClass().getSimpleName(), "onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtlis.i(getClass().getSimpleName(), "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtlis.i(getClass().getSimpleName(), "onPause");
	}

	protected void onStop() {
		dismissDialog();
		LogUtlis.i(getClass().getSimpleName(), "onStop");
		super.onStop();
	}

	protected void onDestroy() {
		sActivityList.remove(this);
		LogUtlis.i(getClass().getSimpleName(), "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

	}

	public void setTitle(CharSequence title) {
		super.setTitle(title);
		TextView textTitle = (TextView) findViewById(R.id.title);
		if (textTitle != null)
			textTitle.setText(title);
	}

	public void setTitleBarLeftClick(OnClickListener listener) {
		View view = findViewById(R.id.bt_left);
		if (view == null)
			return;
		if (listener == null)
			view.setVisibility(View.INVISIBLE);
		else
			view.setVisibility(View.VISIBLE);
		view.setOnClickListener(listener);
	}

	public void setAddActivityClick(OnClickListener listener) {
		View view = findViewById(R.id.add_activity);
		if (view == null)
			return;
		if (listener == null)
			view.setVisibility(View.GONE);
		else
			view.setVisibility(View.VISIBLE);
		view.setOnClickListener(listener);
	}

	protected void setTitleBarRightClick(OnClickListener listener) {
		View view = findViewById(R.id.bt_right);
		if (view == null)
			return;
		if (listener == null)
			view.setVisibility(View.INVISIBLE);
		else
			view.setVisibility(View.VISIBLE);
		view.setOnClickListener(listener);
	}

	protected void setTitleBarRightIcon(int resId) {
		ImageView view = (ImageView) findViewById(R.id.bt_right);
		if (view == null)
			return;
		view.setImageResource(resId);
	}

	public void onExit(Fragment fragment) {

	}

	public static void exit(Context context) {
		try {
			for (int i = 0; i < sActivityList.size(); i++) {
				if (null != sActivityList.get(i)) {
					sActivityList.get(i).finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommUtil.exit(context);
	}

	protected void initDialog(boolean cancelAble, boolean cancelOnTouchOutside, OnCancelListener cancellistener) {
		if (mDialog == null) {
			mDialog = new DefaultProgressDialog(this);
		}
		mDialog.setCancelable(cancelAble);
		mDialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
		mDialog.setOnCancelListener(cancellistener);
	}

	protected void initDialog() {
		if (mDialog == null) {
			mDialog = new DefaultProgressDialog(this);
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
		return Preferences.getInstance(this);
	}

	protected BaseActivity getThis() {
		return this;
	}
}
