package com.sunrise.scmbhc.ui.activity;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;

public class PhoneMarketActivity extends BaseActivity {

	@Override
	public void init() {
		setTitle(getTitle(getIntent()));
	}

	@Override
	protected BaseFragment getFragment() {
		// TODO Auto-generated method stub
		return null;
	}

}
