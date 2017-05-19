package com.sunrise.marketingassistant.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * 对输入框文字监控，控制指定按钮显现或消失
 * 
 * @author 珩
 * @version 2014年9月26日 16:33:38
 */
public class EditTextWacher implements TextWatcher {
	private View view;

	public EditTextWacher(View view) {
		this.view = view;
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		view.setVisibility((arg1 + arg3 == 0) ? View.GONE : View.VISIBLE);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void afterTextChanged(Editable arg0) {
	}
};
