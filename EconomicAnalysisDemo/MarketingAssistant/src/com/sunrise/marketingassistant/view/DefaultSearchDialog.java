package com.sunrise.marketingassistant.view;

import com.sunrise.marketingassistant.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DefaultSearchDialog extends DefaultDialog implements android.view.View.OnClickListener {
	private EditText mMessageView;
	private TextView mTitleView;

	private Button mSearchButton;

	private OnConfirmListener mOnSearchListener;

	public DefaultSearchDialog(Context context) {
		super(context, R.layout.default_search_dialog);

		mTitleView = (TextView) findViewById(R.id.dialog_title);

		mMessageView = (EditText) findViewById(R.id.dialog_input);

		mSearchButton = (Button) findViewById(R.id.dialog_button);
		mSearchButton.setOnClickListener(this);

	}

	public DefaultSearchDialog setDilaogTitle(CharSequence text) {
		mTitleView.setText(text);
		mTitleView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
		return this;
	}

	public DefaultSearchDialog setInputFieldHint(CharSequence text) {
		mMessageView.setHint(text);
		return this;
	}

	public DefaultSearchDialog setInputFieldSingleLine(boolean singleLine) {
		mMessageView.setSingleLine(singleLine);
		return this;
	}

	public DefaultSearchDialog setInputType(int type) {
		mMessageView.setInputType(type);
		return this;
	}

	public DefaultSearchDialog setSearchText(CharSequence text) {
		mSearchButton.setText(text);
		return this;
	}

	public DefaultSearchDialog setOnSearchListener(OnConfirmListener listener) {
		mOnSearchListener = listener;
		return this;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.dialog_button:
			if (mOnSearchListener != null) {
				mOnSearchListener.onClick(this, mMessageView.getText().toString(), R.id.dialog_button);
			} else
				dismiss();
			break;
		default:
			break;
		}
	}
}
