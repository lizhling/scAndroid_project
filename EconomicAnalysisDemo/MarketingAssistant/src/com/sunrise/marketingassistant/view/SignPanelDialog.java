package com.sunrise.marketingassistant.view;

import com.sunrise.marketingassistant.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class SignPanelDialog extends Dialog {
	private Button mRightButton;
	private Button mLeftButton;
	private SignPanel mSignPanel;
	private View mRecycle;

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

	public SignPanelDialog(Context context, DialogInterface.OnClickListener leftOnClickListener, DialogInterface.OnClickListener rightOnClickListener) {
		super(context, R.style.twoButtonDialog);
		initView(new DefaultDilaogClickListener(this, leftOnClickListener), new DefaultDilaogClickListener(this, rightOnClickListener), null, null);
	}

	/**
	 * @param context
	 *            上下文
	 * @param leftOnClickListener
	 *            取消点击事件逻辑
	 * @param rightOnClickListener
	 *            确认点击事件逻辑
	 * @param confirm
	 *            rightBuntton显示文本
	 * @param cancel
	 *            leftBuntton显示文本
	 */
	public SignPanelDialog(Context context, DialogInterface.OnClickListener leftOnClickListener, DialogInterface.OnClickListener rightOnClickListener,
			String cancel, String confirm) {
		super(context, R.style.twoButtonDialog);
		initView(new DefaultDilaogClickListener(this, leftOnClickListener), new DefaultDilaogClickListener(this, rightOnClickListener), cancel, confirm);
	}

	private void initView(View.OnClickListener leftOnClickListener, View.OnClickListener rightOnClickListener, String cancel, String confirm) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_sign_panel);

		mSignPanel = (SignPanel) findViewById(R.id.signPanel1);
		mSignPanel.setOnTouchListener(new SignPanel.OnTouchListener() {

			@Override
			public void onTouched(boolean isTouch) {
				mSignPanel.setVisibility(isTouch ? View.GONE : View.INVISIBLE);
			}
		});

		mLeftButton = (Button) findViewById(R.id.cancle);
		mLeftButton.setOnClickListener(leftOnClickListener);
		mRightButton = ((Button) (findViewById(R.id.confirm)));
		mRightButton.setOnClickListener(rightOnClickListener == null ? DEFAULT_CLICK_LISTNER : rightOnClickListener);

		if (!TextUtils.isEmpty(cancel))
			mLeftButton.setText(cancel);

		if (!TextUtils.isEmpty(confirm))
			mRightButton.setText(confirm);

		mRecycle = findViewById(R.id.bt_recycle);
		mRecycle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mSignPanel.clean();
			}
		});
	}

	public void setRightButton(int visibility) {
		mRightButton.setVisibility(visibility);
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
