package com.sunrise.marketingassistant.view;

import com.sunrise.marketingassistant.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TwoButtonDialog extends Dialog {
	private Button mRightButton;
	private Button mLeftButton;
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

	public TwoButtonDialog(Context context, View.OnClickListener leftOnClickListener, View.OnClickListener rightOnClickListener) {
		super(context, R.style.twoButtonDialog);
		initView(null, leftOnClickListener, rightOnClickListener, null, null);
	}

	public TwoButtonDialog(Context context, CharSequence string, DialogInterface.OnClickListener leftOnClickListener,
			DialogInterface.OnClickListener rightOnClickListener) {
		this(context, string, leftOnClickListener, rightOnClickListener, null, null);
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
	public TwoButtonDialog(Context context, CharSequence string, DialogInterface.OnClickListener leftOnClickListener,
			DialogInterface.OnClickListener rightOnClickListener, String cancel, String confirm) {
		super(context, R.style.twoButtonDialog);

		android.view.View.OnClickListener rightListner = null;
		if (rightOnClickListener != null)
			rightListner = new DefaultDilaogClickListener(this, rightOnClickListener);

		android.view.View.OnClickListener leftListner = null;
		if (leftOnClickListener != null)
			leftListner = new DefaultDilaogClickListener(this, leftOnClickListener);

		initView(string, leftListner, rightListner, cancel, confirm);
	}

	private void initView(CharSequence charSequence, View.OnClickListener leftOnClickListener, View.OnClickListener rightOnClickListener, String cancel,
			String confirm) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_two_button);
		// mTitleView=((TextView)(findViewById(R.id.dialog_title)));
		mMessageView = (TextView) findViewById(R.id.dialog_message);

		mLeftButton = (Button) findViewById(R.id.cancle);
		mLeftButton.setOnClickListener(leftOnClickListener);
		mRightButton = ((Button) (findViewById(R.id.confirm)));
		mRightButton.setOnClickListener(rightOnClickListener == null ? DEFAULT_CLICK_LISTNER : rightOnClickListener);

		if (!TextUtils.isEmpty(charSequence))
			setMessage(charSequence);

		if (!TextUtils.isEmpty(cancel))
			mLeftButton.setText(cancel);

		if (!TextUtils.isEmpty(confirm))
			mRightButton.setText(confirm);

		mSupportVersionAlertView = (TextView) findViewById(R.id.dialog_message);
	}

	public void setMessage(CharSequence message) {
		mMessageView.setText(message);
	}

	public void setTitle(String title) {
		// mTitleView.setText(title);
	}

	public void setRightButton(int visibility) {
		mRightButton.setVisibility(visibility);
	}

	public void setSupportVersionAlert(int visibility) {
		mSupportVersionAlertView.setVisibility(visibility);
	}

	public void setLeftButtonText(CharSequence text) {
		mLeftButton.setText(text);
	}

	public void setRightButtonText(CharSequence text) {
		mRightButton.setText(text);
	}

	private final android.view.View.OnClickListener DEFAULT_CLICK_LISTNER = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
		}
	};
}
