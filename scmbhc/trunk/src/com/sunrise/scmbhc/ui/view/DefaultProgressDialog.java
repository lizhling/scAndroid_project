package com.sunrise.scmbhc.ui.view;

import com.sunrise.scmbhc.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.TextView;

/**
 * 
 * @author fuheng
 * 
 */
public class DefaultProgressDialog extends DefaultDialog {
	private TextView textview;
	private AnimationDrawable animation;

	public DefaultProgressDialog(Context context) {
		super(context, R.layout.default_progress_dialog);
		textview = (TextView) findViewById(android.R.id.text1);

		animation = (AnimationDrawable) textview.getCompoundDrawables()[0];
		getWindow().getAttributes().y = Math.round(context.getResources().getDisplayMetrics().heightPixels * 0.118f);
	}

	public void setMessage(CharSequence text) {
		textview.setText(text);
	}

	public void show() {

		if (animation != null)
			animation.start();

		super.show();
	}

	public void dismiss() {
		if (animation != null)
			animation.stop();

		super.dismiss();
	}
}
