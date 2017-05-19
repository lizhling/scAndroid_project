package com.sunrise.marketingassistant.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sunrise.marketingassistant.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

/**
 * 
 * @author fuheng
 * 
 */
public class DefaultDatePickerDialog extends DefaultDialog implements android.view.View.OnClickListener {
	private TextView mTitleView;

	private Button mCancelButton;
	private Button mConfirmButton;

	private OnConfirmListener mOnConfirmListener;
	private android.content.DialogInterface.OnClickListener mOnCancelListener;
	private Calendar c;

	/** 关联的textview */
	private TextView mAssociatedTextView;

	private DatePicker mDatePicker;

	public DefaultDatePickerDialog(Context context) {
		super(context, R.layout.default_datepicker_dialog);

		mTitleView = (TextView) findViewById(R.id.dialog_title);

		mDatePicker = (DatePicker) findViewById(R.id.date_picker);

		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(this);
		mConfirmButton = ((Button) (findViewById(R.id.confirm)));
		mConfirmButton.setOnClickListener(this);

		initTime(System.currentTimeMillis());
	}

	private void initTime(long time) {
		// 初始化时间
		c = Calendar.getInstance();
		c.setTimeInMillis(time);
		mDatePicker.setCalendarViewShown(false);
		mDatePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
	}

	@SuppressLint("SimpleDateFormat")
	public final DefaultDatePickerDialog setAssociatedTextView(TextView tv) {
		mAssociatedTextView = tv;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long time = sdf.parse(tv.getText().toString()).getTime();
			initTime(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return this;
	}

	public DefaultDatePickerDialog setDilaogTitle(CharSequence text) {
		mTitleView.setText(text);
		mTitleView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
		return this;
	}

	public DefaultDatePickerDialog setShowDetail(boolean shown) {
		mDatePicker.setCalendarViewShown(shown);

		// 设置日期简略显示 否则详细显示 包括:星期周
		return this;
	}

	public DefaultDatePickerDialog setConfirmText(CharSequence text) {
		mConfirmButton.setText(text);
		return this;
	}

	public DefaultDatePickerDialog setCancelText(CharSequence text) {
		mCancelButton.setText(text);
		return this;
	}

	public DefaultDatePickerDialog setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
		return this;
	}

	public DefaultDatePickerDialog setOnCancelListener(android.content.DialogInterface.OnClickListener listener) {
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
			CharSequence result = turnData2String();
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
	private String turnData2String() {
		return String.format("%d-%02d-%02d", mDatePicker.getYear(), mDatePicker.getMonth() + 1, mDatePicker.getDayOfMonth());

	}
}
