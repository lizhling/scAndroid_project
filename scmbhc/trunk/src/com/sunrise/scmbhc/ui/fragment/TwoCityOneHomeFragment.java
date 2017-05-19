package com.sunrise.scmbhc.ui.fragment;

import com.sunrise.scmbhc.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewSwitcher;

@SuppressLint("ValidFragment")
public class TwoCityOneHomeFragment extends DefaultBusinessDetailFragment implements OnClickListener {
	private ViewSwitcher mSwitcher_2city1home;

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_business_two_city_one_home, container, false);

		initView(view);

		init2City1Home(view);

		initAddressData(view, inflater);

		return view;
	}

	private void initAddressData(View view, LayoutInflater inflater) {
		ViewGroup group1 = (ViewGroup) view.findViewById(R.id.container1);
		for (int i = 0; i < 3; ++i) {
			View item = inflater.inflate(R.layout.item_business_roam_set, null);
			TextView state = (TextView) item.findViewById(R.id.state);
			Spinner spinner1 = (Spinner) item.findViewById(R.id.spinner1);
			TextView duration = (TextView) item.findViewById(R.id.duration);
			group1.addView(item, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}

		ViewGroup group2 = (ViewGroup) view.findViewById(R.id.container2);
		for (int i = 0; i < 3; ++i) {
			View item = inflater.inflate(R.layout.item_business_roam_set, null);
			TextView state = (TextView) item.findViewById(R.id.state);
			Spinner spinner1 = (Spinner) item.findViewById(R.id.spinner1);
			ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.tC1H_provinces, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner1.setAdapter(adapter);
			TextView duration = (TextView) item.findViewById(R.id.duration);
			group2.addView(item, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}

	private void init2City1Home(View view) {
		mSwitcher_2city1home = (ViewSwitcher) view.findViewById(R.id.viewSwitcher_2city1home);

		RadioGroup mRadioGroupTypeOfProvince = (RadioGroup) view.findViewById(R.id.radioGroup_2city1home);
		mRadioGroupTypeOfProvince.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int index = group.indexOfChild(group.findViewById(checkedId));
				if (mSwitcher_2city1home.getDisplayedChild() != index)
					if (index == 0)
						mSwitcher_2city1home.showNext();
					else
						mSwitcher_2city1home.showPrevious();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if(mBaseActivity.checkLoginIn(null))
			return;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	@Override
	public void onClick(View view) {
	}
	
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.TwoCityOneHomeFragment;
	}
}
