package com.sunrise.scmbhc.ui.activity;

import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.TrafficServeFragement;

public class TrafficPackageActivity extends BaseActivity {

	@Override
	public void init() {
		setTitle(getTitle(getIntent()));
	}

	@Override
	protected BaseFragment getFragment() {
		return new TrafficServeFragement();
	}

}
