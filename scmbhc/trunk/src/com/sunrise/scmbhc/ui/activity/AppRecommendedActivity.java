package com.sunrise.scmbhc.ui.activity;

import android.os.Bundle;

import com.sunrise.scmbhc.ui.fragment.AppRecommendFragment;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;
public class AppRecommendedActivity extends BaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	public void init() {
		setFinishActivity(true);
		setTitle(getTitle(getIntent()));
	}

	@Override
	protected BaseFragment getFragment() {
		AppRecommendFragment fragment = new AppRecommendFragment();
		return fragment;
	}
}
