package com.sunrise.marketingassistant.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * 
 * @author fuheng
 * 
 */
public class DefaultTimeAndDatePickerDialog extends DefaultDialog implements android.view.View.OnClickListener, ExtraKeyConstant {
	private TextView mTitleView;

	private Button mCancelButton;
	private Button mConfirmButton;

	private OnConfirmListener mOnConfirmListener;
	private android.content.DialogInterface.OnClickListener mOnCancelListener;
	private TimePicker mTimePicker;
	private DatePicker mDatePicker;

	/** 关联的textview */
	private TextView mAssociatedTextView;

	public DefaultTimeAndDatePickerDialog(Context context) {
		super(context, R.layout.default_time_and_date_picker_dialog);

		mTitleView = (TextView) findViewById(R.id.dialog_title);

		mTimePicker = (TimePicker) findViewById(R.id.time_picker);
		mDatePicker = (DatePicker) findViewById(R.id.date_picker);

		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(this);
		mConfirmButton = ((Button) (findViewById(R.id.confirm)));
		mConfirmButton.setOnClickListener(this);

		initTime(System.currentTimeMillis());
	}

	private void initTime(long time) {
		// 初始化时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentMinute(c.get(Calendar.MINUTE));
		mDatePicker.setCalendarViewShown(false);
		mDatePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
		mDatePicker.setMaxDate(time);
	}

	@SuppressLint("SimpleDateFormat")
	public final DefaultTimeAndDatePickerDialog setAssociatedTextView(TextView tv) {
		mAssociatedTextView = tv;

		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_DATA_AND_TIME);
		try {
			long time = sdf.parse(tv.getText().toString()).getTime();
			initTime(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return this;
	}

	public DefaultTimeAndDatePickerDialog setDilaogTitle(CharSequence text) {
		mTitleView.setText(text);
		mTitleView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
		return this;
	}

	public DefaultTimeAndDatePickerDialog setIs24HourView(Boolean is24HourView) {
		mTimePicker.setIs24HourView(is24HourView);
		return this;
	}

	public DefaultTimeAndDatePickerDialog setConfirmText(CharSequence text) {
		mConfirmButton.setText(text);
		return this;
	}

	public DefaultTimeAndDatePickerDialog setCancelText(CharSequence text) {
		mCancelButton.setText(text);
		return this;
	}

	public DefaultTimeAndDatePickerDialog setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
		return this;
	}

	public DefaultTimeAndDatePickerDialog setOnCancelListener(android.content.DialogInterface.OnClickListener listener) {
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

		return String.format(FORMAT_DATA_AND_TIME, mDatePicker.getYear(), mDatePicker.getMonth() + 1, mDatePicker.getDayOfMonth(), mHour, mMinute);
	}

	public DefaultTimeAndDatePickerDialog setTimeLimit(long minDate) {
		mDatePicker.setMinDate(minDate);
		return this;
	}

}
