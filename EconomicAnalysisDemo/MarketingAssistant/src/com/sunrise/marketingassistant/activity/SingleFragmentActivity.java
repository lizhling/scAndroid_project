package com.sunrise.marketingassistant.activity;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.fragment.BaseFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

public class SingleFragmentActivity extends BaseActivity implements ExtraKeyConstant {
	protected BaseFragment mCurrentFragment;

	public static Intent createIntent(Context context, Class<? extends BaseFragment> cls, String Durl, String Dname, String DlastModify, String outPageInfo) {

		Bundle bundle = new Bundle();
		if (!TextUtils.isEmpty(Durl))
			bundle.putString(Intent.EXTRA_TEXT, Durl);
		if (!TextUtils.isEmpty(Dname))
			bundle.putString(Intent.EXTRA_TITLE, Dname);
		if (!TextUtils.isEmpty(outPageInfo))
			bundle.putString(ExtraKeyConstant.KEY_CONTENT, outPageInfo);
		if (!TextUtils.isEmpty(DlastModify))
			bundle.putString(ExtraKeyConstant.KEY_LAST_MODIFY, DlastModify);

		Intent intent = new Intent(context, SingleFragmentActivity.class);
		intent.putExtra(KEY_FRAGMENT, cls);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);

		return intent;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	protected void initView() {
		setContentView(R.layout.layout_base_fragment);
		mCurrentFragment = getFragment();
		mCurrentFragment.startFragment(this, R.id.fragmentContainer);
	}

	protected BaseFragment getFragment() {
		Intent intent = getIntent();
		Class<? extends BaseFragment> cls = (Class<? extends BaseFragment>) intent.getSerializableExtra(KEY_FRAGMENT);
		if (cls == null)
			return null;

		BaseFragment fragment = null;
		try {
			fragment = cls.newInstance();

			Bundle bundle = intent.getBundleExtra(ExtraKeyConstant.KEY_BUNDLE);
			if (bundle != null)
				fragment.setArguments(bundle);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return fragment;
	}

	@Override
	public void onBackPressed() {
		switch (onBack()) {
		case 1:
			finish();
			return;
		case 0:
			return;
		default:
			super.onBackPressed();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mCurrentFragment != null)
			mCurrentFragment.onActivityResult(requestCode, resultCode, data);
	}

	protected int onBack() {
		int count = 1;

		if (mCurrentFragment != null) {
			if (mCurrentFragment.onBackPressed())
				count = 0;
			else
				count = mCurrentFragment.finish(this);
		}

		return count;
	}

	public void onExit(Fragment fragment) {
		finish();
	}
}
