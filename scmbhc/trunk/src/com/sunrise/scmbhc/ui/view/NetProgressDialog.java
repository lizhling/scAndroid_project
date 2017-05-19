package com.sunrise.scmbhc.ui.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sunrise.scmbhc.R;

public class NetProgressDialog extends ProgressDialog {
	
	public NetProgressDialog(Context context) {
		super(context, R.style.twoButtonDialog);
		initView();
	}

	private void initView(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.net_progress_dialog);
	}
	
}
