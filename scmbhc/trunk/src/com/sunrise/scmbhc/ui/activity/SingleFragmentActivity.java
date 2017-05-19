package com.sunrise.scmbhc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;

public class SingleFragmentActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected BaseFragment getFragment() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		Class<? extends BaseFragment> cls = (Class<? extends BaseFragment>) intent.getSerializableExtra(App.ExtraKeyConstant.KEY_FRAGMENT);
		boolean isFinishAcitivity = intent.getBooleanExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, true);
		setFinishActivity(isFinishAcitivity);
		if (cls == null)
			return null;

		BaseFragment fragment = null;
		try {
			fragment = cls.newInstance();

			Bundle bundle = getIntent().getBundleExtra(App.ExtraKeyConstant.KEY_BUNDLE);
			if (bundle != null)
				fragment.setArguments(bundle);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return fragment;
		// return new AboutFragment();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (mCurrentFragment != null) {
			if (mCurrentFragment.onKeyDown(keyCode, event)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mCurrentFragment != null) 
			mCurrentFragment.onActivityResult(requestCode, resultCode, data);
	}
}
