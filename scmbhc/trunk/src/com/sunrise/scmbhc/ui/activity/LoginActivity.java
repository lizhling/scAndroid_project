package com.sunrise.scmbhc.ui.activity;

import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.LoginFragment;

import android.os.Bundle;

public class LoginActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public void init() {
		setFinishActivity(true);
		//PhoneClickAgent.onEvent(this, "login");
	}

	@Override
	protected BaseFragment getFragment() {
		return new LoginFragment();
	}
}
