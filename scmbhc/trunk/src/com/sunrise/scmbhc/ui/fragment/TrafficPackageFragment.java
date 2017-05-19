package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.SubBusinessAdapter;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.utils.CommUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 流量包
 * 
 * @author fuheng
 * 
 */
public class TrafficPackageFragment extends TrafficBaseFragment implements OnClickListener {

	private ListView mListViewBase, mListViewOverlay, mListViewIdle;

	private OnClickListener mDoTaskListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			goBusinessDetailPage((BusinessMenu) v.getTag());
		}
	};

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_traffic_package, container, false);

		mListViewBase = (ListView) view.findViewById(R.id.listView_base);
		mListViewOverlay = (ListView) view.findViewById(R.id.listView_superposition);
		mListViewIdle = (ListView) view.findViewById(R.id.listView_leisure);

		if (!TextUtils.isEmpty(mNameBaseInfo)) {
			TextView textview = (TextView) view.findViewById(R.id.tab_baseTrafficPackage);
			textview.setText(mNameBaseInfo);
		}

		if (!TextUtils.isEmpty(mNameOverlay)) {
			TextView textview = (TextView) view.findViewById(R.id.tab_overlayTrafficPackage);
			textview.setText(mNameOverlay);
		}

		if (!TextUtils.isEmpty(mNameIdle)) {
			TextView textview = (TextView) view.findViewById(R.id.tab_idleTrafficPackage);
			textview.setText(mNameIdle);
		}

		view.findViewById(R.id.button_share).setOnClickListener(this);

		initData();
		return view;
	}

	public void onStart() {
		super.onStart();

		if (!UserInfoControler.getInstance().checkUserLoginIn())
			mBaseActivity.setRightButton(getResources().getString(R.string.login), new OnClickListener() {
				@Override
				public void onClick(View v) {
					mBaseActivity.checkLoginIn(null);
				}
			});
		else
			mBaseActivity.setRightButtonVisibility(View.GONE);

		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);

		getActivity().setTitle(getString(R.string.trafficPackage));
	}

	/**
	 * 初始化数据，先从外部获取数据，外部无数据，再从本地获取数据。
	 */
	private void initData() {

		// 基础
		SubBusinessAdapter adapter = new SubBusinessAdapter(getActivity(), mArrayBaseInfo);
		adapter.setOnClickOpenUp(mDoTaskListener);
		mListViewBase.setAdapter(adapter);

		// 叠加
		adapter = new SubBusinessAdapter(getActivity(), mArrayOverlay);
		adapter.setOnClickOpenUp(mDoTaskListener);
		mListViewOverlay.setAdapter(adapter);

		// 闲时
		adapter = new SubBusinessAdapter(getActivity(), mArrayIdle);
		adapter.setOnClickOpenUp(mDoTaskListener);
		mListViewIdle.setAdapter(adapter);

		// 查询数据库获取菜单列表(当无外部数据传来的时候，才进行数据库查找)
		if (mArrayBaseInfo.isEmpty() && mArrayOverlay.isEmpty() && mArrayIdle.isEmpty()) {
			startQueryBusinessMenus();
		} else {
			CommUtil.expandListView((BaseAdapter) mListViewBase.getAdapter(), mListViewBase);
			CommUtil.expandListView((BaseAdapter) mListViewOverlay.getAdapter(), mListViewOverlay);
			CommUtil.expandListView((BaseAdapter) mListViewIdle.getAdapter(), mListViewIdle);
		}
	}

	@Override
	public void onClick(View arg0) {
		BaseFragment fragment = new GiveTrafficAsGiftFragment();
		Bundle args = new Bundle();
		ArrayList<BusinessMenu> array = new ArrayList<BusinessMenu>();
		array.addAll(mArrayBaseInfo);
		array.addAll(mArrayOverlay);
		array.addAll(mArrayIdle);
		args.putParcelableArrayList(KEY_BUSINESS_INFO, array);
		fragment.setArguments(args);
		fragment.startFragment(mBaseActivity, R.id.fragmentContainer);
	}

	protected void onCompleteBaseNetList() {
		if (mListViewBase == null)
			return;

		mListViewBase.postInvalidate();
		CommUtil.expandListView((BaseAdapter) mListViewBase.getAdapter(), mListViewBase);
	}

	protected void onCompleteOverlayNetList() {
		if (mListViewOverlay == null)
			return;
		mListViewOverlay.postInvalidate();
		CommUtil.expandListView((BaseAdapter) mListViewOverlay.getAdapter(), mListViewOverlay);
	}

	protected void onCompleteIdleNetList() {
		if (mListViewIdle == null)
			return;

		mListViewIdle.postInvalidate();
		CommUtil.expandListView((BaseAdapter) mListViewIdle.getAdapter(), mListViewIdle);
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.TrafficPackageFragment;
	}

}
