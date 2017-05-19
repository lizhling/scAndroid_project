package com.sunrise.scmbhc.ui.activity;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.ManualFragment;


public class ManualActivity extends BaseActivity {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		setTitle(getTitle(getIntent()));
	}

	@Override
	protected BaseFragment getFragment() {
		// TODO Auto-generated method stub
		return new ManualFragment();
	}


	

}
