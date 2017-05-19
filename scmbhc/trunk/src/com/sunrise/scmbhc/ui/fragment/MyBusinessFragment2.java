package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.adapter.MyBusinessAdapter;
import com.sunrise.scmbhc.adapter.ViewPagerAdapter;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.AdditionalTariffInfo;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.OpenedBusinessList;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetOpenedUpBusinnessListTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.utils.CommUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyBusinessFragment2 extends BaseFragment implements OnClickListener, OnCheckedChangeListener, OnPageChangeListener {

	private GenericTask mTask;

	private ViewPager mViewPager;

	private RadioGroup mTabWidget;

	private ArrayList<AdditionalTariffInfo> mArrayTariffInof;

	private View mViewGroup_MainBusiness;// 主业务信息的容器。

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(ExtraKeyConstant.KEY_SUB_BUSINESS_INFOS)) {
			String tempStr = savedInstanceState.getString(ExtraKeyConstant.KEY_SUB_BUSINESS_INFOS);
			if (tempStr != null)
				mArrayTariffInof = analysisAdditionalTariffInfo(tempStr);
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		String tempStr = parseArrayTariffInfosToJsonString(mArrayTariffInof);
		outState.putString(ExtraKeyConstant.KEY_SUB_BUSINESS_INFOS, tempStr);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_business2, container, false);

		mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

		mTabWidget = (RadioGroup) view.findViewById(R.id.tabs);
		mTabWidget.setOnCheckedChangeListener(this);

		mViewGroup_MainBusiness = view.findViewById(R.id.main_business);

		return view;
	}

	public void onStart() {
		super.onStart();
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		mBaseActivity.setTitle(R.string.myBusiness);
	}

	public void onResume() {
		super.onResume();
		if (!mBaseActivity.checkLoginIn(null))
			return;

		if (mArrayTariffInof == null || mArrayTariffInof.isEmpty()) {

			OpenedBusinessList mOpenedBusiList = UserInfoControler.getInstance().getOpenedBusinessList();
			if (mOpenedBusiList == null) {
				GetOpenedUpBusinnessListTask task = new GetOpenedUpBusinnessListTask();
				task.execute(this.mfGetOpenedUpBusinessTaskListener);
				mTask = task;
			} else {
				mArrayTariffInof = mOpenedBusiList.getTariffInfos();
				initMainBusinessInfoView(mOpenedBusiList);
				startLoadExitTariffInfos();
			}
		}

	}

	public void onStop() {
		super.onStop();

		if (mTask != null)
			mTask.cancle();

		mBaseActivity.setRightButton(null, null);
		mBaseActivity.setRightButtonVisibility(View.GONE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	private OnClickListener RefreshListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mTask != null)
				mTask.cancle();

			mTask = new GetOpenedUpBusinnessListTask();
			mTask.setListener(mfGetOpenedUpBusinessTaskListener);
			mTask.execute();
		}
	};

	private void initData() {

		ArrayList<AdditionalTariffInfo> array1, array2, array3;
		array1 = new ArrayList<AdditionalTariffInfo>();
		array2 = new ArrayList<AdditionalTariffInfo>();
		array3 = new ArrayList<AdditionalTariffInfo>();

		for (int i = 0; i < mArrayTariffInof.size(); ++i) {
			AdditionalTariffInfo item = mArrayTariffInof.get(i);

			// if ("0".equals(item.getPROD_TYPE())) {
			// initMainBusinessInfoView(item);
			// continue;
			// }

			int type = 0;
			if (item.getPRODUCT_TYPE() != null)
				type = Integer.parseInt(item.getPRODUCT_TYPE());
			switch (type) {
			case 1:
				array1.add(item);
				break;
			case 2:
				array2.add(item);
				break;
			case 3:
				array3.add(item);
				break;
			default:
				Log.d("initData()", "defualt :" + item.toString());
				array2.add(item);
				break;
			}
		}
		ArrayList<View> arrayViews = new ArrayList<View>();
		for (int i = 0; i < mTabWidget.getChildCount(); ++i) {
			ListView view = new ListView(mViewPager.getContext());
			view.setDivider(getResources().getDrawable(R.drawable.icon_divider));
			view.setFadingEdgeLength(0);
			view.setSelector(new ColorDrawable());
			view.setCacheColorHint(0);
			switch (i) {
			case 0:
				view.setAdapter(new MyBusinessAdapter(mBaseActivity, array1));
				break;
			case 1:
				view.setAdapter(new MyBusinessAdapter(mBaseActivity, array2));
				break;
			case 2:
				view.setAdapter(new MyBusinessAdapter(mBaseActivity, array3));
				break;
			default:
				break;
			}

			arrayViews.add(view);
		}
		mViewPager.setAdapter(new ViewPagerAdapter(arrayViews));
		mViewPager.setOnPageChangeListener(this);
		mTabWidget.check(mTabWidget.getChildAt(0).getId());
	}

	private void initMainBusinessInfoView(OpenedBusinessList openedBusiList) {
		AdditionalTariffInfo item = openedBusiList.getMainTraffic();

		if (item == null) {
			mViewGroup_MainBusiness.setVisibility(View.GONE);
			return;
		}

		mViewGroup_MainBusiness.setVisibility(View.VISIBLE);
		// 当月消费
		TextView costMonth = (TextView) mViewGroup_MainBusiness.findViewById(R.id.cost_this_month);
		TextView tag = (TextView) mViewGroup_MainBusiness.findViewById(R.id.tag1);

		String pCost = UserInfoControler.getInstance().getPhoneCurMsg().getPublicCost();
		String cost = UserInfoControler.getInstance().getConsumptionThisMonth();
		if (TextUtils.isEmpty(pCost)) {
			costMonth.setText(cost);
			tag.setText(R.string.cost_in_this_month);
		} else {
			costMonth.setText(UserInfoControler.getInstance().getConsumptionThisMonth() + "\n" + pCost + getString(R.string.yuan));
			tag.setText(getString(R.string.cost_in_this_month) + "\n" + getString(R.string.cost_public_account_this_month));
		}

		// 主资费
		TextView mainBusinessName = (TextView) mViewGroup_MainBusiness.findViewById(R.id.main_business_name);
		mainBusinessName.setText(CommUtil.deleteProdPrcidFromBusinessName(item.getPROD_PRC_NAME()));
		// 次月主资费
		AdditionalTariffInfo item2 = openedBusiList.getMainTrafficNext();
		if (item2 != null) {
			TextView nextMainBusinessName = (TextView) mViewGroup_MainBusiness.findViewById(R.id.main_business_next_name);
			nextMainBusinessName.setText("(下月套餐：" + CommUtil.deleteProdPrcidFromBusinessName(item2.getPROD_PRC_NAME()) + ")");
			nextMainBusinessName.setVisibility(View.VISIBLE);
		} else
			mViewGroup_MainBusiness.findViewById(R.id.main_business_next_name).setVisibility(View.GONE);
	}

	private ArrayList<AdditionalTariffInfo> analysisAdditionalTariffInfo(String str) {
		ArrayList<AdditionalTariffInfo> arrayAdditional = new ArrayList<AdditionalTariffInfo>();
		try {
			JSONArray jarray = new JSONArray(str);
			for (int i = 0; i < jarray.length(); ++i) {
				AdditionalTariffInfo item = JsonUtils.parseJsonStrToObject(jarray.get(i).toString(), AdditionalTariffInfo.class);
				arrayAdditional.add(item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrayAdditional;
	}

	private String parseArrayTariffInfosToJsonString(ArrayList<AdditionalTariffInfo> arrayAdditional) {
		if (arrayAdditional != null && arrayAdditional.size() != 0)
			try {
				JSONArray jarray = new JSONArray();
				for (AdditionalTariffInfo item : arrayAdditional)
					jarray.put(item.toJson());
				return jarray.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		return null;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.lookCostThisMonth:
			new BillApxPageQueryFragment().startFragment(mBaseActivity, R.id.fragmentContainer);
			break;
		default:// 已定业务
			// showDialog(view);
			goBusinessDetailPage((BusinessMenu) view.getTag());
			break;
		}
	}

	/**
	 * 启动业务办理
	 * 
	 * @param item
	 */
	public void goBusinessDetailPage(BusinessMenu item) {
		Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, DefaultBusinessDetailFragment1.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(App.ExtraKeyConstant.KEY_BUSINESS_INFO, item);
		bundle.putBoolean(App.ExtraKeyConstant.KEY_HAVE_OPENED, true);
		intent.putExtra(App.ExtraKeyConstant.KEY_BUNDLE, bundle);
		mBaseActivity.startActivity(intent);
	}

	/**
	 * 启动读取业务办理的退出信息
	 */
	private void startLoadExitTariffInfos() {
		mTask = new LoadExitBusinessMenuItemTask();
		mTask.setListener(mLoadExitBusinessMenuItemTaskListener);
		mTask.execute();
	}

	private final TaskListener mfGetOpenedUpBusinessTaskListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// mArrayTariffInof = analysisAdditionalTariffInfo((String) param);
			OpenedBusinessList mOpenedBusiList = (OpenedBusinessList) param;
			mArrayTariffInof = mOpenedBusiList.getTariffInfos();
			initMainBusinessInfoView(mOpenedBusiList);
			startLoadExitTariffInfos();
		}

		@Override
		public void onPreExecute(GenericTask task) {
			initDialog(true, false, new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
				}

			});
			showDialog(getResources().getString(R.string.loading_status));
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			dismissDialog();

			if (result == TaskResult.OK) {
				startLoadExitTariffInfos();
			} else {
				if (task.isBusinessAuthenticationTimeOut())
					mBaseActivity.showReLoginDialog();
				else if (task.getException() != null && task.getException().getMessage() != null)
					CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), null);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			dismissDialog();
		}

		@Override
		public String getName() {
			return null;
		}
	};

	private class LoadExitBusinessMenuItemTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {

			ScmbhcDbManager sdm = ScmbhcDbManager.getInstance(mBaseActivity.getContentResolver());
			for (AdditionalTariffInfo tariff : mArrayTariffInof) {
				String efftype = tariff.getEFF_TYPE();
				if (efftype == null || efftype.equals("2") || efftype.equals("4"))
					continue;
				BusinessMenu item = sdm.queryBusinessMenuByPrcID(tariff.getPROD_PRCID());
				if (item != null)
					tariff.setBusinessItem(item.getQuietItem(tariff.getPROD_PRCID()));
			}
			return TaskResult.OK;
		}
	}

	private TaskListener mLoadExitBusinessMenuItemTaskListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {

		}

		@Override
		public void onPreExecute(GenericTask task) {
			initDialog();
			showDialog(getString(R.string.loading_status));
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// MyBusinessAdapter adapter = new MyBusinessAdapter(mBaseActivity,
			// mOpenedBusinessList.getTariffInfos());
			// adapter.setOnClickOpenUp(MyBusinessFragment2.this);
			//
			// if (mListViewOrderedBusiness != null) {
			// mListViewOrderedBusiness.setAdapter(adapter);
			// }
			initData();
			dismissDialog();
		}

		@Override
		public void onCancelled(GenericTask task) {
			dismissDialog();
		}

		@Override
		public String getName() {
			return null;
		}

	};

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		int index = arg0.indexOfChild(arg0.findViewById(id));
		mViewPager.setCurrentItem(index);
	}

	/**
	 * 设置tabwidget的当前序列，不启动radioGroup的监控。
	 * 
	 * @param index
	 */
	private void setTabWidgetCheckIndex(int index) {
		mTabWidget.setOnCheckedChangeListener(null);
		mTabWidget.check(mTabWidget.getChildAt(index).getId());
		mTabWidget.setOnCheckedChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int index) {
		setTabWidgetCheckIndex(index);
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.MyBusinessFragment;
	}
}
