package com.starcpt.cmuc.ui.view;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.utils.FileUtils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SetSesurePasswordDialog extends Dialog implements
		android.view.View.OnClickListener {
	private EditText mPasswordView;
	private EditText mCertainView;
	private Button mBtnOK;
	
	private boolean mIsPassowrdCorrect;

	private android.content.DialogInterface.OnClickListener mBtnOkListener;
	private TextView mInputTextViewStatus;
	private CmucApplication cmucApplication;
	private PopupWindow pupupwindow;

	public SetSesurePasswordDialog(Context context) {
		super(context, R.style.dialog);
		cmucApplication = (CmucApplication) context.getApplicationContext();
		this.setContentView(R.layout.set_secure_password);
		TextView title=(TextView) findViewById(R.id.tvTitle);
		title.setText(R.string.set_secure_password);

		mInputTextViewStatus = (TextView) findViewById(R.id.password_input_status);

		mBtnOK = (Button) findViewById(R.id.btnOK);
		mBtnOK.setOnClickListener(this);

		mPasswordView = (EditText) findViewById(R.id.secure_password);
		mPasswordView.addTextChangedListener(new InputTextWatcher());

		mCertainView = (EditText) findViewById(R.id.certain_password);
		mCertainView.addTextChangedListener(new InputTextWatcherCertain());

		{
			TextView tv = (TextView) findViewById(R.id.help);
			tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			tv.setOnClickListener(this);
		}

		setCancelable(false);
		disableOkButton();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOK:
			Preferences preferences = cmucApplication.getSettingsPreferences();
			preferences.saveSecurePassword(mPasswordView.getText().toString());
			preferences.saveScreenOffLock(true);
			preferences.saveNoLock(false);
			preferences.saveScreenOffLock(true);
			preferences.saveNoLock(false);
			preferences.saveHiddenLock(true);
			preferences.saveAfterStartLock(false);
			mBtnOkListener.onClick(this, 0);
			break;
		case R.id.help:
			showHelpInfo(v);
			break;
		default:
			break;
		}

	}

	private void showHelpInfo(View v) {
		if (pupupwindow == null) {
			String content = FileUtils.getTextFromAssets(v.getContext(),
					"answer_secure.txt", "utf-8");

			if (content != null) {
				TextView view = new TextView(v.getContext());
				view.setText(content);
				view.setBackgroundResource(R.drawable.op_bookmark_bg);
				view.setTextColor(Color.WHITE);
				pupupwindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				pupupwindow.setBackgroundDrawable(new ColorDrawable(0));
				pupupwindow.setOutsideTouchable(true);
				pupupwindow.setFocusable(true);
			}
		}
		if (pupupwindow != null)
			pupupwindow.showAsDropDown(v);
	}

	private void disableOkButton() {
		mBtnOK.setTextColor(Color.DKGRAY);
		mBtnOK.setBackgroundResource(R.drawable.grey_button_bg_normal);
		mBtnOK.setEnabled(false);
	}

	private void enabelOkButton() {
		mBtnOK.setTextColor(Color.WHITE);
		mBtnOK.setBackgroundResource(R.drawable.green_button_bg);
		mBtnOK.setEnabled(true);
	}

	public void setBtnOkListener(
			android.content.DialogInterface.OnClickListener mBtnOkListener) {
		this.mBtnOkListener = mBtnOkListener;
	}

	private class InputTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			if (TextUtils.isEmpty(s)) {
				mInputTextViewStatus.setText(R.string.first_set_password);
				disableOkButton();
				mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				mCertainView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				return;
			}

			if (s.length() < CmucApplication.PASSWORD_LENGTH) {
				mInputTextViewStatus.setText(R.string.secure_password_length);
				disableOkButton();
				mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				mCertainView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				return;
			}

		/*	if (s.toString().matches("^\\d+$")) {
				mInputTextViewStatus
						.setText(R.string.secure_password_contain_chart);
				disableOkButton();
				mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				mCertainView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				return;
			}

			if (!s.toString().contains(" ")) {
				mInputTextViewStatus
						.setText(R.string.secure_password_contain_empty);
				disableOkButton();
				mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				mCertainView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				return;
			}*/
			mInputTextViewStatus.setText(R.string.confirm_secure_password);

			mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_correct, 0);
			mIsPassowrdCorrect = true;

			// 判断字符串是否相同
			boolean isSameAsPassword = s.toString().equals(
					mCertainView.getText().toString());
			if (isSameAsPassword) {
				mCertainView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_correct, 0);
				enabelOkButton();
			} else {
				mCertainView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				disableOkButton();
			}
		}
	}

	private class InputTextWatcherCertain extends InputTextWatcher {

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			boolean isSameAsPassword = s.toString().equals(
					mPasswordView.getText().toString());
			if (!mIsPassowrdCorrect || !isSameAsPassword) {
				disableOkButton();
				mCertainView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
				return;
			}

			mCertainView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_correct, 0);
			enabelOkButton();
		}
	}

	/*private boolean isPasswordCorrect(CharSequence s) {
		if (TextUtils.isEmpty(s)) {
			disableOkButton();
			return false;
		}

		if (s.length() < CmucApplication.PASSWORD_LENGTH) {
			return false;
		}

		if (s.toString().matches("^\\d+$")) {
			return false;
		}

		if (!s.toString().matches("^[A-Za-z0-9]+$")) {
			return false;
		}

		return true;
	}*/
}
