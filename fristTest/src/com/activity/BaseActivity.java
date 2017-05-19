package com.activity;

import java.util.ArrayList;

import com.cache.preference.Preferences;
import com.example.fristtest.R;
import com.util.CommonUtil;
import com.view.DefaultProgressDialog;

import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseActivity extends FragmentActivity {
	private DefaultProgressDialog  mDialog;
	private static final ArrayList<BaseActivity> sActivityList = new ArrayList<BaseActivity>();
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
	}
	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		sActivityList.add(this);
	}
	@Override
	protected void onDestroy() {
		sActivityList.remove(this);
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onStop() {
		dismissDialog();
		super.onStop();
	}
	
	
	
	public void setTitle(CharSequence title){
		super.setTitle(title);
		TextView textTitle = (TextView)findViewById(R.id.title);
		if(textTitle!=null)
			textTitle.setText(title);
	}
	
	public void setTitleBarLeftClick(OnClickListener listener){
		View view  = findViewById(R.id.bt_left);
		if(view == null)
			return;
		if(listener ==null)
			view.setVisibility(View.INVISIBLE);
		else
			view.setVisibility(View.VISIBLE);
		view.setOnClickListener(listener);
	}
	
	public void setAddActivityClick(OnClickListener listener){
		View view  = findViewById(R.id.add_activity);
		if(view ==null)return;
		if(listener ==null)view.setVisibility(View.GONE);
		else view.setVisibility(View.VISIBLE);
		view.setOnClickListener(listener);
	}
	
	protected void setTitleBarRightClick(OnClickListener listener){
		View view  = findViewById(R.id.bt_right);
		if(view == null)
			return;
		if(listener ==null)
			view.setVisibility(View.INVISIBLE);
		else
			view.setVisibility(View.VISIBLE);
		view.setOnClickListener(listener);
	}
	
	protected void setTitleBarRightIcon(int resId){
		ImageView view = (ImageView)findViewById(R.id.bt_right);
		if(view == null)
			return;
		view.setImageResource(resId);
	}
	
	public static void exit(Context context){
		try {
			for(int i=0;i<sActivityList.size();i++){
				if(null != sActivityList.get(i)){
					sActivityList.get(i).finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommonUtil.exit(context);
	}
	protected void initDialog(boolean cancelAble,boolean cancelOnTouchOutside,OnCancelListener cancellistener){
		if(mDialog == null){
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
