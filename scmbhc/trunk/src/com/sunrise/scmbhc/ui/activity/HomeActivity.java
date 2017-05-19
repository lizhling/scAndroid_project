package com.sunrise.scmbhc.ui.activity;

import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.HomeFragment;

public class HomeActivity extends BaseActivity {

	@Override
	public void init() {
		setTitle(getTitle(getIntent()));
		setHomeActivity(true);
	}

	@Override
	protected BaseFragment getFragment() {		
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}
	
}
