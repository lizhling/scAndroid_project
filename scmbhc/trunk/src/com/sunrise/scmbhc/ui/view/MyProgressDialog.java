package com.sunrise.scmbhc.ui.view;


import com.sunrise.scmbhc.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MyProgressDialog extends Dialog {
	private TextView mStatusView;
	public MyProgressDialog(Context context) {
		super(context);
		initView();		
	}

	private void initView(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.progress_dialog);
		mStatusView=(TextView) findViewById(R.id.load_progress);
		findViewById(R.id.close_progress_dialog).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
	}
	
	public void setProgressStatus(String text){
		mStatusView.setText(text);
	}
	
}
