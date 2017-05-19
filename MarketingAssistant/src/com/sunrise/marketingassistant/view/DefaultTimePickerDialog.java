package com.sunrise.marketingassistant.view;

import java.util.Calendar;

import com.sunrise.marketingassistant.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * 
 * @author fuheng
 * 
 */
public class DefaultTimePickerDialog extends DefaultDialog implements android.view.View.OnClickListener {
	private TextView mTitleView;

	private Button mCancelButton;
	private Button mConfirmButton;

	private OnConfirmListener mOnConfirmListener;
	private android.content.DialogInterface.OnClickListener mOnCancelListener;
	private TimePicker mTimePicker;
	private Calendar c;

	/** 关联的textview */
	private TextView mAssociatedTextView;

	public DefaultTimePickerDialog(Context context) {
		super(context, R.layout.default_timepicker_dialog);

		mTitleView = (TextView) findViewById(R.id.dialog_title);

		mTimePicker = (TimePicker) findViewById(R.id.time_picker);

		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(this);
		mConfirmButton = ((Button) (findViewById(R.id.confirm)));
		mConfirmButton.setOnClickListener(this);

		// 初始化时间
		c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentMinute(c.get(Calendar.MINUTE));
	}

	public final DefaultTimePickerDialog setAssociatedTextView(TextView tv) {
		mAssociatedTextView = tv;
		return this;
	}

	public DefaultTimePickerDialog setDilaogTitle(CharSequence text) {
		mTitleView.setText(text);
		mTitleView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
		return this;
	}

	public DefaultTimePickerDialog setIs24HourView(Boolean is24HourView) {
		mTimePicker.setIs24HourView(is24HourView);
		return this;
	}

	public DefaultTimePickerDialog setConfirmText(CharSequence text) {
		mConfirmButton.setText(text);
		return this;
	}

	public DefaultTimePickerDialog setCancelText(CharSequence text) {
		mCancelButton.setText(text);
		return this;
	}

	public DefaultTimePickerDialog setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
		return this;
	}

	public DefaultTimePickerDialog setOnCancelListener(android.content.DialogInterface.OnClickListener listener) {
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
			CharSequence result = turnTime2String();
			if (mAssociatedTextView != null)
				mAssociatedTextView.setText(result);

			if (mOnConfirmListener != null) {
				mOnConfirmListener.onClick(this, result, R.id.confirm);
			} else
				dismiss();
			break;
		default:
			break;
		}
	}

	@SuppressLint("DefaultLocale")
	private String turnTime2String() {
		int mHour = mTimePicker.getCurrentHour();
		int mMinute = mTimePicker.getCurrentMinute();
		return String.format("%02d点%02d分", mHour, mMinute);

	}
}
