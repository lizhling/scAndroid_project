package com.sunrise.scmbhc.ui.view;

import java.io.IOException;
import java.io.InputStream;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.utils.FileUtils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 协议对话框
 * 
 * @author fuheng
 * 
 */
public class AgreementDialog extends Dialog implements OnCheckedChangeListener {
	private android.view.View.OnClickListener mCancelListener;
	private android.view.View.OnClickListener mConfirmListener;
	private Button confirmButton;

	public AgreementDialog(Context context, String fileName, android.view.View.OnClickListener confirmListner, android.view.View.OnClickListener cancelListener) {
		super(context, R.style.twoButtonDialog);

		mCancelListener = cancelListener;
		mConfirmListener = confirmListner;

		initView(context, fileName);

		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}

	private void initView(Context context, String fileName) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_user_agreement);
		confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setTextColor(Color.DKGRAY);
		confirmButton.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
				if (mConfirmListener != null)
					mConfirmListener.onClick(arg0);
			}
		});

		TextView mMessageView = (TextView) findViewById(R.id.content);

		CharSequence text = null;
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			text = new String(FileUtils.readFiletoByte(in), "utf-8");
//			text = FileUtils.readToStringFormInputStreamUTF_8(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mMessageView.setText(text);

		Button cancleButton = ((Button) (findViewById(R.id.cancle)));
		cancleButton.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
				if (mCancelListener != null)
					mCancelListener.onClick(arg0);
			}
		});

		CheckBox checobox = (CheckBox) findViewById(R.id.checkBox_agree);
		checobox.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		arg0.setTextColor(arg1 ? Color.CYAN : Color.DKGRAY);

		if (arg1)
			confirmButton.setTextColor(getContext().getResources().getColor(R.color.dialog_button_text));
		else
			confirmButton.setTextColor(Color.DKGRAY);

		confirmButton.setEnabled(arg1);
	}

}
