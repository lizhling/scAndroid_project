package com.sunrise.scmbhc.ui.activity;

import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.BusinessListFragment1;

public class BusinessActivity extends BaseActivity {

	public void init() {
		setTitle(getTitle(getIntent()));
	}

	@Override
	protected BaseFragment getFragment() {
		BusinessMenu businessMenu = new BusinessMenu();
		businessMenu.setId(BusinessMenu.ROOT_BUSINESSMEUN_ID);
		BusinessListFragment1 fragment = new BusinessListFragment1(businessMenu);
		return fragment;
	}
}
