package com.sunrise.micromarketing.ui.activity;

import java.util.ArrayList;

import com.sunrise.javascript.function.Cache;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.cache.preferences.Preferences;
import com.sunrise.micromarketing.ui.view.DefaultProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author fuheng
 * 
 */
public abstract class BaseActivity extends Activity {
	private DefaultProgressDialog mDialog;
	private static final ArrayList<BaseActivity> sActivityList = new ArrayList<BaseActivity>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sActivityList.add(this);
		Log.i(getClass().getSimpleName(), "onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(getClass().getSimpleName(), "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(getClass().getSimpleName(), "onPause");
	}

	protected void onStop() {
		dismissDialog();
		Log.i(getClass().getSimpleName(), "onStop");
		super.onStop();
	}

	protected void onDestroy() {
		sActivityList.remove(this);
		Log.i(getClass().getSimpleName(), "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

	}

	public boolean onTouch(View v, MotionEvent event) {
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// // sendBroadcast(new
		// // Intent(MainActivity.TRIGGLE_SLIDINGMENU_ACTION));
		// MainActivity.getInstance().toggleSlidingMenu();
		// }
		return true;
	}

	public void setTitle(CharSequence title) {
		super.setTitle(title);
		TextView textTitle = (TextView) findViewById(R.id.title);
		if (textTitle != null)
			textTitle.setText(title);
	}

	protected void setTitleBarLeftClick(OnClickListener listener) {
		View view = findViewById(R.id.bt_left);
		if (view == null)
			return;
		if (listener == null)
			view.setVisibility(View.INVISIBLE);
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

	public static void exit(Context context) {
		try {
			for (int i = 0; i < sActivityList.size(); i++) {
				if (null != sActivityList.get(i)) {
					sActivityList.get(i).finish();
				}
			}
		} catch (Exception e) {
		}
		new Cache(context, null).clean();// 新增删除所有缓存
		android.os.Process.killProcess(android.os.Process.myPid());
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
