package com.sunrise.marketingassistant.view;

import com.sunrise.marketingassistant.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

	public interface OnConfirmListener {
		/**
		 * @param dialog
		 *            The dialog that received the click.
		 * @param inputText
		 *            The input txt.
		 * @param which
		 *            The button that was clicked (e.g. confirm) or the position
		 *            of the item clicked.
		 */
		void onClick(DialogInterface dialog, Object resultObj, int which);
	}
}
