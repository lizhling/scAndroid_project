package com.sunrise.scmbhc.ui.activity;

import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.SearchFragment;

public class SearchActivity extends BaseActivity {

	@Override
	public void init() {
		setTitle(getTitle(getIntent()));
		setFinishActivity(true);
	}

	@Override
	protected BaseFragment getFragment() {
		// TODO Auto-generated method stub
		BaseFragment searchFragment=new SearchFragment();
		return searchFragment;
	}

}
