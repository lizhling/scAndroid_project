package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.adapter.MyPagerAdapter;
import com.sunrise.marketingassistant.entity.TabContent;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyChannelFragment extends BaseFragment implements ExtraKeyConstant, OnPageChangeListener, OnCheckedChangeListener {

	private ViewPager mViewPager;
	private ArrayList<View> mListViews;
	private TabContent mTabContentOfChannelTree;
	private RadioGroup mRadioGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTabContentOfChannelTree = JsonUtils.parseJsonStrToObject(getArguments().getString(KEY_URL), TabContent.class);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_my_channel, null);
		mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
		mListViews = new ArrayList<View>();

		for (int i = 0; i < mViewPager.getChildCount(); i++) {
			mListViews.add(mViewPager.getChildAt(i));
		}

		// FragmentManager fragmentManager =
		// mBaseActivity.getSupportFragmentManager();
		// FragmentTransaction ft = fragmentManager.beginTransaction();
		// {
		// int id = 0x343;
		// View v = new View(view.getContext());
		// view.setId(id);
		// mListViews.add(view);
		// BaseFragment fragment = new ChannelListFragment();
		// ft.replace(id, fragment);
		// }
		// {
		// int id = 0x342;
		// View v = new View(view.getContext());
		// view.setId(id);
		// mListViews.add(view);
		// BaseFragment fragment = new WebViewFragment2();
		// Bundle args = new Bundle();
		// args.putString(KEY_CONTENT, null);
		// args.putString(KEY_LAST_MODIFY,
		// mTabContentOfChannelTree.getLastModify());
		// args.putString(Intent.EXTRA_TEXT,
		// mTabContentOfChannelTree.getZipContent());
		// fragment.setArguments(args);
		//
		// ft.replace(id, fragment);
		// }
		// {
		// int id = 0x344;
		// View v = new View(view.getContext());
		// view.setId(id);
		//
		// mListViews.add(view);
		// BaseFragment fragment = new FavoriteChannelFragment();
		// ft.replace(id, fragment);
		// ft.commit();
		// }
		mViewPager.setAdapter(new MyPagerAdapter(mListViews));
		mViewPager.setOnPageChangeListener(this);

		mRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup1);
		mRadioGroup.setOnCheckedChangeListener(this);

		return view;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		mRadioGroup.setOnCheckedChangeListener(null);
		mRadioGroup.check(arg0);
		mRadioGroup.setOnCheckedChangeListener(this);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		int index = arg0.indexOfChild(arg0.findViewById(arg1));
		mViewPager.setCurrentItem(index);
	}

}
