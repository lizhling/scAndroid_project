package com.view;

import com.example.fristtest.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class DefaultDialog extends Dialog {

	public DefaultDialog(Context context,View view) {
		super(context, R.style.dialog);
		setContentView(view);
	}

	public DefaultDialog(Context context, int layoutResID) {
		super(context, R.style.dialog);
		setContentView(layoutResID);
	}

	public DefaultDialog(Context context,View view, int featureId) {
		super(context);
		requestWindowFeature(featureId);
		setContentView(view);
		// TODO Auto-generated constructor stub
	}

	public interface OnConfirmListener{
		
		void onClick(DialogInterface dialog,Object resultObj,int which);
	}

}
