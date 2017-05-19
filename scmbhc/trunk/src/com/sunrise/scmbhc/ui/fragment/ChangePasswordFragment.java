package com.sunrise.scmbhc.ui.fragment;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.task.ChangePasswordTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.view.EditTextWacher;
import com.sunrise.scmbhc.utils.CommUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 积分兑换
 * 
 * @author fuheng
 * 
 */
public class ChangePasswordFragment extends BaseFragment implements TaskListener, OnClickListener {

	private TextView mEditOldPassword, mEditNewPassword, mEditRepeatPassword;
	private GenericTask mTask;

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_change_password, container, false);

		mEditOldPassword = (TextView) view.findViewById(R.id.editText_oldpassword);
		mEditNewPassword = (TextView) view.findViewById(R.id.editText_newPassword);
		mEditRepeatPassword = (TextView) view.findViewById(R.id.editText_repeatPassword);

		View mbtn1 = view.findViewById(R.id.btn_delete1);
		mbtn1.setOnClickListener(this);
		mEditOldPassword.addTextChangedListener(new EditTextWacher(mbtn1));

		View mbtn2 = view.findViewById(R.id.btn_delete2);
		mbtn2.setOnClickListener(this);
		mEditNewPassword.addTextChangedListener(new EditTextWacher(mbtn2));

		View mbtn3 = view.findViewById(R.id.btn_delete3);
		view.findViewById(R.id.submit).setOnClickListener(this);
		mbtn3.setOnClickListener(this);

		// 提交按钮
		mEditRepeatPassword.addTextChangedListener(new EditTextWacher(mbtn3));

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!((BaseActivity) getActivity()).checkLoginIn(null))
			return;
	}

	public void onStart() {
		super.onStart();

		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		baseActivity.setTitle(getResources().getString(R.string.changePassword));

	}

	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != BaseActivity.RESULT_OK) {
			mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog(false, false, null);
		showDialog(getActivity().getResources().getString(R.string.onDealing));
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK) {
			if (task.isBusinessAuthenticationTimeOut())
				mBaseActivity.showReLoginDialog();
			else
				CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), null);
		} else {
			CommUtil.showAlert(mBaseActivity, null, getString(R.string.passwordChangeSuccess), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
				}
			});
		}

	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// 清除该账号登录密码
		UserInfoControler.getInstance().loginIn(UserInfoControler.getInstance().getUserName(), null, null);
		UserInfoControler.getInstance().setAutoLogin(false);
		UserInfoControler.getInstance().loginOut();
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_delete1:
			mEditOldPassword.setText(null);
			break;
		case R.id.btn_delete2:
			mEditNewPassword.setText(null);
			break;
		case R.id.btn_delete3:
			mEditRepeatPassword.setText(null);
			break;
		case R.id.submit:
			doChange();
			break;
		default:
			break;
		}
	}

	private void doChange() {
		String oldpassword = mEditOldPassword.getText().toString();
		if (TextUtils.isEmpty(oldpassword)) {// 判断旧密码是否为空
			Toast.makeText(mBaseActivity, R.string.hintInputServicePassword, Toast.LENGTH_SHORT).show();
			mEditOldPassword.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			mEditOldPassword.requestFocus();
			return;
		}

		String password = UserInfoControler.getInstance().getPassword();
		if (!TextUtils.isEmpty(password) && !password.equals(oldpassword)) {// 当本地存储的登录密码不为空的时候，判断与当前输入的旧密码是否相同
			Toast.makeText(mBaseActivity, R.string.passwordInputWrong, Toast.LENGTH_SHORT).show();
			mEditOldPassword.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			mEditOldPassword.requestFocus();
			return;
		}

		String newpassword = mEditNewPassword.getText().toString();
		if (TextUtils.isEmpty(newpassword) || newpassword.length() < 6) {// 判断新密码是否为空或者小于6位
			Toast.makeText(mBaseActivity, R.string.hintNewServicePassword, Toast.LENGTH_SHORT).show();
			mEditNewPassword.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			mEditNewPassword.requestFocus();
			return;
		}

		String repeatpassword = mEditRepeatPassword.getText().toString();
		if (TextUtils.isEmpty(repeatpassword)) {// // 判断再次新密码是否为空或者小于6位
			Toast.makeText(mBaseActivity, R.string.hintRepeatNewPassword, Toast.LENGTH_SHORT).show();
			mEditRepeatPassword.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			mEditRepeatPassword.requestFocus();
			return;
		}

		if (!newpassword.equals(repeatpassword)) {// 判断两次输入新密码是否相同
			CommUtil.showAlert(mBaseActivity, null, getString(R.string.noticeSameCheckPassword), null);
			mEditRepeatPassword.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.vibrate));
			mEditRepeatPassword.requestFocus();
			return;
		}

		ChangePasswordTask task = new ChangePasswordTask();
		task.execute(UserInfoControler.getInstance().getUserName(), oldpassword, newpassword, this);
		mTask = task;
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.ChangePasswordFragment;
	}

}
