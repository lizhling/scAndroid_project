package com.sunrise.marketingassistant.activity;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.fragment.ActivityInfoCollectionFragment;
import com.sunrise.marketingassistant.fragment.ActivityInfoCollectionListFragment;
import com.sunrise.marketingassistant.fragment.BaseFragment;
import com.sunrise.marketingassistant.fragment.CompatitorDetailFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.ViewSwitcher;

public class CompatitorDetailActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, ExtraKeyConstant {

	private ViewSwitcher mSwitcher;

	private BaseFragment mCurrentFragment;

	private BaseFragment mDetailFragment;

	private BaseFragment mListFragment;

	private ChlBaseInfo mChlBaseInfo;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_compatitor_info);
		initView();

	}

	protected void onStart() {
		super.onStart();
		setTitle(getString(R.string.CompetitorsInformationCollection));
	}

	public void onBackPressed() {
		if (mCurrentFragment == null || !mCurrentFragment.onBackPressed())
			finish();
	}

	private void initView() {

		((RadioButton) findViewById(R.id.check_activity_info)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.check_base_info)).setOnCheckedChangeListener(this);

		mSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher01);

		mChlBaseInfo = getIntent().getParcelableExtra(Intent.EXTRA_DATA_REMOVED);

		{
			BaseFragment fragment = new CompatitorDetailFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(Intent.EXTRA_DATA_REMOVED, mChlBaseInfo);
			fragment.setArguments(bundle);
			fragment.startFragment(getThis(), R.id.content1);
			mCurrentFragment = mDetailFragment = fragment;
		}

		if (mChlBaseInfo != null) {
			BaseFragment fragment = new ActivityInfoCollectionListFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Intent.EXTRA_DATA_REMOVED, String.valueOf(mChlBaseInfo.getChlId()));
			fragment.setArguments(bundle);
			fragment.startFragment(getThis(), R.id.content2);
			mListFragment = fragment;
		} else {
			findViewById(R.id.radioGroup1).setVisibility(View.GONE);
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (arg1)
			switch (arg0.getId()) {
			case R.id.check_activity_info:
				mSwitcher.setInAnimation(this, R.anim.push_right_in);
				mSwitcher.setOutAnimation(this, R.anim.push_left_out);
				mSwitcher.setDisplayedChild(1);
				mCurrentFragment = mListFragment;
				setTitleBarRightIcon(android.R.drawable.ic_menu_add);
				setTitleBarRightClick(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						ActivityInfoCollectionFragment.newInstance(null, String.valueOf(mChlBaseInfo.getChlId()));
						startActivity(SingleFragmentActivity.createIntent(CompatitorDetailActivity.this, ActivityInfoCollectionFragment.class, null, null,
								null, null));
					}
				});
				break;
			case R.id.check_base_info:
				mSwitcher.setInAnimation(this, R.anim.push_left_in);
				mSwitcher.setOutAnimation(this, R.anim.push_right_out);
				mSwitcher.setDisplayedChild(0);
				mCurrentFragment = mDetailFragment;
				setTitleBarRightClick(null);
				break;

			default:
				break;
			}
	}

}
