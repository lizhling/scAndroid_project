package com.sunrise.micromarketing.ui.view;

import com.sunrise.micromarketing.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author fuheng
 * 
 */
public class DefaultInputDialog extends DefaultDialog implements android.view.View.OnClickListener {
	private EditText mMessageView;
	private TextView mTitleView;

	private Button mCancelButton;
	private Button mConfirmButton;

	private OnConfirmListener mOnConfirmListener;
	private android.content.DialogInterface.OnClickListener mOnCancelListener;
	private ImageView imageView1;

	public DefaultInputDialog(Context context) {
		super(context, R.layout.default_input_dialog);

		mTitleView = (TextView) findViewById(R.id.dialog_title);

		mMessageView = (EditText) findViewById(R.id.dialog_message);

		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(this);
		mConfirmButton = ((Button) (findViewById(R.id.confirm)));
		mConfirmButton.setOnClickListener(this);

		imageView1 = (ImageView) findViewById(R.id.imageView1);
	}

	public DefaultInputDialog setDilaogTitle(CharSequence text) {
		mTitleView.setText(text);
		mTitleView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
		return this;
	}

	public DefaultInputDialog setInputFieldHint(CharSequence text) {
		mMessageView.setHint(text);
		return this;
	}

	public DefaultInputDialog setInputFieldSingleLine(boolean singleLine) {
		mMessageView.setSingleLine(singleLine);
		return this;
	}

	public DefaultInputDialog setInputType(int type) {
		mMessageView.setInputType(type);
		return this;
	}

	public DefaultInputDialog setConfirmText(CharSequence text) {
		mConfirmButton.setText(text);
		return this;
	}

	public DefaultInputDialog setCancelText(CharSequence text) {
		mCancelButton.setText(text);
		return this;
	}

	public DefaultInputDialog setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
		return this;
	}

	public DefaultInputDialog setMessageIcon(int resId) {
		return setMessageIcon(getContext().getResources().getDrawable(resId));
	}

	public DefaultInputDialog setMessageIcon(Bitmap bitmap) {
		return setMessageIcon(new BitmapDrawable(bitmap));
	}
	
	public DefaultInputDialog setMessageIcon(Drawable drawable) {
		if (drawable == null)
			imageView1.setVisibility(View.GONE);
		else {
			imageView1.setImageDrawable(drawable);
			imageView1.setVisibility(View.VISIBLE);
		}
		return this;
	}


	public DefaultInputDialog setOnCancelListener(android.content.DialogInterface.OnClickListener listener) {
		mOnCancelListener = listener;
		return this;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.cancel:
			if (mOnCancelListener != null)
				mOnCancelListener.onClick(this, R.id.confirm);
			dismiss();
			break;
		case R.id.confirm:
			if (mOnConfirmListener != null) {
				mOnConfirmListener.onClick(this, mMessageView.getText().toString(), R.id.confirm);
			} else
				dismiss();
			break;
		default:
			break;
		}
	}
}
