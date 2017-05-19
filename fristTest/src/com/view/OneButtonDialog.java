package com.view;


import com.example.fristtest.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class OneButtonDialog extends Dialog {
	private Button mConfirmButton;
	private TextView mMessageView;
	private TextView mSupportVersionAlertView;

	class DefaultDilaogClickListener implements View.OnClickListener {
		DialogInterface.OnClickListener onClickListener;
		Dialog dialog;

		public DefaultDilaogClickListener(Dialog dialog, DialogInterface.OnClickListener onClickListener) {
			this.onClickListener = onClickListener;
			this.dialog = dialog;
		}

		@Override
		public void onClick(View arg0) {
			if (onClickListener != null)
				onClickListener.onClick(dialog, arg0.getId());
		}
	}

	public OneButtonDialog(Context context, View.OnClickListener onClickListener) {
		super(context, R.style.twoButtonDialog);
		initView(null, onClickListener, null);
	}

	public OneButtonDialog(Context context, DialogInterface.OnClickListener onClickListener) {
		super(context, R.style.twoButtonDialog);
		initView(null, onClickListener == null ? null : new DefaultDilaogClickListener(this, onClickListener), null);
	}

	public OneButtonDialog(Context context, CharSequence string, DialogInterface.OnClickListener onClickListener) {
		this(context, string, onClickListener, null);
	}

	/**
	 * @param context
	 *            上下文
	 * @param updateInfo
	 *            更新信息类
	 * @param leftOnClickListener
	 *            取消点击事件逻辑
	 * @param rightOnClickListener
	 *            确认点击事件逻辑
	 * @param confirm
	 *            rightBuntton显示文本
	 * @param cancel
	 *            leftBuntton显示文本
	 */
	public OneButtonDialog(Context context, CharSequence string, DialogInterface.OnClickListener onClickListener, String confirm) {
		super(context, R.style.twoButtonDialog);
		initView(string, onClickListener == null ? null : new DefaultDilaogClickListener(this, onClickListener), confirm);
	}

	private void initView(CharSequence charSequence, View.OnClickListener onClickListener, String confirm) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_one_button);
		// mTitleView=((TextView)(findViewById(R.id.dialog_title)));
		mMessageView = (TextView) findViewById(R.id.dialog_message);

		mConfirmButton = ((Button) (findViewById(R.id.confirm)));
		mConfirmButton.setOnClickListener(onClickListener == null ? DEFAULT_CLICK_LISTNER : onClickListener);

		if (!TextUtils.isEmpty(charSequence))
			setMessage(charSequence);

		if (!TextUtils.isEmpty(confirm))
			mConfirmButton.setText(confirm);

		mSupportVersionAlertView = (TextView) findViewById(R.id.dialog_message);
	}

	public void setMessage(CharSequence message) {
		mMessageView.setText(message);
	}

	public void setTitle(String title) {
		// mTitleView.setText(title);
	}

	public void setButton(int visibility) {
		mConfirmButton.setVisibility(visibility);
	}

	public void setSupportVersionAlert(int visibility) {
		mSupportVersionAlertView.setVisibility(visibility);
	}

	public void setButtonText(CharSequence text) {
		mConfirmButton.setText(text);
	}

	private final android.view.View.OnClickListener DEFAULT_CLICK_LISTNER = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
		}
	};
}
