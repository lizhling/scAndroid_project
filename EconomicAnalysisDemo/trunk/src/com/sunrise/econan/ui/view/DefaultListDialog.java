package com.sunrise.econan.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.sunrise.econan.R;
import com.sunrise.econan.ui.view.DefaultInputDialog.OnConfirmListener;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author fuheng
 * 
 */
public class DefaultListDialog extends DefaultDialog implements android.view.View.OnClickListener, OnItemClickListener {
	private ListView mListview;
	private TextView mTitleView;

	private Button mCancelButton;
	private Button mConfirmButton;

	private OnConfirmListener mOnConfirmListener;
	private android.content.DialogInterface.OnClickListener mOnCancelListener;
	private List<String> mList;

	private String mSelectItem;

	public DefaultListDialog(Context context) {
		super(context, R.layout.default_list_dialog);

		mTitleView = (TextView) findViewById(R.id.dialog_title);

		mListview = (ListView) findViewById(R.id.dialog_list);
		mListview.setOnItemClickListener(this);

		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(this);
		mConfirmButton = ((Button) (findViewById(R.id.confirm)));
		mConfirmButton.setOnClickListener(this);
		mConfirmButton.setEnabled(false);
	}

	public DefaultListDialog setDilaogTitle(CharSequence text) {
		mTitleView.setText(text);
		mTitleView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
		return this;
	}

	public DefaultListDialog setListChoice(List<String> list) {
		mList = list;
		mListview.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_single_choice, mList));
		return this;
	}

	public DefaultListDialog setListChoice(String[] args) {
		mList = new ArrayList<String>();
		for (String s : args) {
			mList.add(s);
		}
		mListview.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_single_choice, mList));
		return this;
	}

	public DefaultListDialog setConfirmText(CharSequence text) {
		mConfirmButton.setText(text);
		return this;
	}

	public DefaultListDialog setCancelText(CharSequence text) {
		mCancelButton.setText(text);
		return this;
	}

	public DefaultListDialog setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
		return this;
	}

	public DefaultListDialog setOnCancelListener(android.content.DialogInterface.OnClickListener listener) {
		mOnCancelListener = listener;
		return this;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.cancel:
			dismiss();
			if (mOnCancelListener != null)
				mOnCancelListener.onClick(this, R.id.confirm);
			break;
		case R.id.confirm:
			if (mOnConfirmListener != null) {
				mOnConfirmListener.onClick(this, mSelectItem, R.id.confirm);
			} else
				dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mSelectItem = mList.get(arg2);
		if (!mConfirmButton.isEnabled())
			mConfirmButton.setEnabled(true);
	}

}
