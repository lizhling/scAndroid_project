package com.sunrise.econan.ui.view;

import com.sunrise.econan.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * 
 * @author fuheng
 * 
 */
public class DefaultDialog extends Dialog {

	public DefaultDialog(Context context, View view) {
		super(context, R.style.dialog);
		setContentView(view);
	}

	public DefaultDialog(Context context, View view, int featureId) {
		super(context, R.style.dialog);
		requestWindowFeature(featureId);
		setContentView(view);
	}

	public DefaultDialog(Context context, int layoutResID) {
		super(context, R.style.dialog);

		setContentView(layoutResID);
	}

}
