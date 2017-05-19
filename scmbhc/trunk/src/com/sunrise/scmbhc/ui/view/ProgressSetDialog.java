package com.sunrise.scmbhc.ui.view;

import com.sunrise.scmbhc.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ProgressSetDialog extends Dialog implements android.view.View.OnClickListener, OnSeekBarChangeListener {
	private TextView mMessageView;

	private DialogInterface.OnClickListener leftOnClickListener, rightOnClickListener;

	private SeekbarChangeListener mSeekbarChangeListener;

	private TextView mTitleView;

	private SeekBar seekBar;

	private TextView mValue;

	public ProgressSetDialog(Context context, DialogInterface.OnClickListener leftOnClickListener, DialogInterface.OnClickListener rightOnClickListener,
			SeekbarChangeListener seekbarChangeListener) {
		super(context, R.style.twoButtonDialog);

		initView();

		this.leftOnClickListener = leftOnClickListener;
		this.rightOnClickListener = rightOnClickListener;
		this.mSeekbarChangeListener = seekbarChangeListener;
	}

	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_set_traffic_notification);
		mTitleView = ((TextView) (findViewById(R.id.title)));
		mMessageView = (TextView) findViewById(R.id.message);

		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setMax(10);
		seekBar.setOnSeekBarChangeListener(this);

		mValue = (TextView) findViewById(R.id.value);

		findViewById(R.id.confirm).setOnClickListener(this);
		findViewById(R.id.cancle).setOnClickListener(this);
	}

	public void setMessage(CharSequence message) {
		mMessageView.setText(message);
	}

	public void setMessage(int resId) {
		mMessageView.setText(resId);
	}

	public void setTitle(CharSequence title) {
		mTitleView.setText(title);
	}

	public void setTitle(int titleId) {
		mTitleView.setText(titleId);
	}

	public void setMax(int max) {
		if (max > 0)
			seekBar.setMax(max);
		else
			Toast.makeText(getContext(), "the max cannot be <1", Toast.LENGTH_LONG).show();
	}

	public void setProgress(int progress) {
		seekBar.setProgress(progress);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm:
			if (leftOnClickListener != null)
				leftOnClickListener.onClick(this, seekBar.getProgress());
			else
				dismiss();
			break;
		case R.id.cancle:
			if (rightOnClickListener != null)
				rightOnClickListener.onClick(this, -1);
			else
				dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		if (mSeekbarChangeListener != null)
			mValue.setText(mSeekbarChangeListener.getSeekBarValue(progress, arg0.getMax()));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {

	}

	public interface SeekbarChangeListener {
		CharSequence getSeekBarValue(int progress, int max);
	}

}
