package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.DefaultImageViewAdapter;
import com.sunrise.scmbhc.adapter.DefaultImageViewAdapter.RecommodPackage;
import com.sunrise.scmbhc.adapter.UseConditionAdapter;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.PhoneFreeQuery;
import com.sunrise.scmbhc.entity.UseCondition;
import com.sunrise.scmbhc.entity.PhoneFreeQuery.EmumState;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetRecommendBusinessTask;
import com.sunrise.scmbhc.task.PhoneFreeQueryTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.view.InstrumentPanel;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 流量服务
 * 
 * @author fuheng
 * 
 */
public class TrafficServeFragement extends TrafficBaseFragment implements OnClickListener, OnItemClickListener, TaskListener {

	private GridView mGridView;
	private TextView mBtRecoomend;
	private GenericTask mTask;
	private TextView mSurplus;
	private TextView mTotle;
	private TextView mUsed;
	private InstrumentPanel mInstrumentPanel1;

	private ArrayList<DefaultImageViewAdapter.RecommodPackage> mArrayRecommedPackage;

	private View mBtnTrafficDetails;// 流量详情按钮

	private PopupWindow mPopupTrafficDetails;

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_query_traffic, container, false);

		// 套餐内设置
		mSurplus = (TextView) view.findViewById(R.id.package_surplus);
		mTotle = (TextView) view.findViewById(R.id.package_totle);
		mUsed = (TextView) view.findViewById(R.id.package_used);

		mBtnTrafficDetails = view.findViewById(R.id.button_traffic_details);

		mBtRecoomend = (TextView) view.findViewById(R.id.button_recommend);
		mBtRecoomend.setOnClickListener(this);

		mGridView = (GridView) view.findViewById(R.id.gridView_recommend_network_package);
		mGridView.setOnItemClickListener(this);

		mInstrumentPanel1 = (InstrumentPanel) view.findViewById(R.id.instrumentPanel1);

		return view;
	}

	// public void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// }

	private void initBtRecommend(int i) {
		if (i < 0 || mArrayBaseInfo == null || i >= mArrayBaseInfo.size())
			return;
		mBtRecoomend.setClickable(true);
		BusinessMenu item = mArrayBaseInfo.get(i);
		BusinessMenu itemobj = (BusinessMenu) mBtRecoomend.getTag();
		if (itemobj != null) {
			item.setName(itemobj.getName());
			item.setProdPrcid(itemobj.getProdPrcid());
			item.setDescription(itemobj.getDescription());
			item.setCharges(itemobj.getCharges());
			item.setWarmPrompt(itemobj.getWarmPrompt());
		}
		mBtRecoomend.setText(item.getName());
		mBtRecoomend.setTag(item);
	}

	private void initGridview() {
		mArrayRecommedPackage = new ArrayList<DefaultImageViewAdapter.RecommodPackage>();

		for (int resid : mArrayBusinessDrawableRes) {
			BusinessMenu item = getBusinessItemByTag(mArrBusinessTag[0], mArrayBaseInfo);
			if (item == null)
				continue;

			RecommodPackage object = new RecommodPackage();
			object.setIconRes(resid);
			object.setBusiness(item);
			mArrayRecommedPackage.add(object);
		}
		{
			RecommodPackage object = new RecommodPackage();
			object.setIconRes(R.drawable.icon_traffic_recomond_package_bg_more);
			mArrayRecommedPackage.add(object);
		}
		{
			RecommodPackage object = new RecommodPackage();
			object.setIconRes(R.drawable.icon_traffic_recomond_package_bg_operation);
			mArrayRecommedPackage.add(object);
		}
		mGridView.setAdapter(new DefaultImageViewAdapter(mGridView.getContext(), mArrayRecommedPackage));
	}

	private void initPopupWindow(Context context) {
		if (mPopupTrafficDetails == null) {
			float density = getResources().getDisplayMetrics().density;
			LinearLayout linearLayout = new LinearLayout(context);
			ListView contentView = new ListView(context);
			contentView.setAdapter(new UseConditionAdapter(context, UserInfoControler.getInstance().getPhoneFreeQuery().getFlowPackages()));
			int padding = Math.round(10 * density);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			contentView.setCacheColorHint(Color.TRANSPARENT);
			contentView.setDivider(new ColorDrawable());
			contentView.setDividerHeight(Math.round(3 * density));
			contentView.setPadding(padding, padding, padding, padding);
			contentView.setLayoutParams(params);
			contentView.setSelector(new ColorDrawable());
			contentView.setBackgroundResource(R.drawable.shape_white_bg);
			linearLayout.setPadding(padding, 0, padding, 0);
			linearLayout.addView(contentView);

			mPopupTrafficDetails = new PopupWindow(linearLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
			// Math.round(240 * density)
			mPopupTrafficDetails.setBackgroundDrawable(new ColorDrawable());
			mPopupTrafficDetails.setOutsideTouchable(false);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_traffic_details:
			initPopupWindow(view.getContext());
			mPopupTrafficDetails.showAsDropDown(mInstrumentPanel1, view.getWidth() - mPopupTrafficDetails.getWidth(),
					Math.round(getResources().getDisplayMetrics().density));
			break;
		case R.id.button_recommend:
			BusinessMenu item = (BusinessMenu) view.getTag();
			if (item != null)
				goBusinessDetailPage(item);
			break;
		default:
			break;
		}
	}

	public void onStart() {
		super.onStart();

		if (!mBaseActivity.checkLoginIn(null)) {
			return;
		}
		initRecommondBusiness();
		if (mArrayBaseInfo != null && !mArrayBaseInfo.isEmpty()) {
			onCompleteBaseNetList();
		} else {
			startQueryBusinessMenus(new TaskListener() {

				@Override
				public void onProgressUpdate(GenericTask task, Object param) {

				}

				@Override
				public void onPreExecute(GenericTask task) {
					mInstrumentPanel1.randomWave(true);
				}

				@Override
				public void onPostExecute(GenericTask task, TaskResult result) {
					onCompleteBaseNetList();
					onCompleteOverlayNetList();
					onCompleteIdleNetList();
					mInstrumentPanel1.randomWave(false);

					// 再判断登录后是否获取到用户套餐余量信息
					if (UserInfoControler.getInstance().getPhoneFreeQuery().isNormalState())
						initBaseTrafficInfo();
					else {// 都不行，再加载一次
						mTask = new PhoneFreeQueryTask().execute(UserInfoControler.getInstance().getUserName(), this);
					}
				}

				@Override
				public void onCancelled(GenericTask task) {
					// TODO Auto-generated method stub
					mInstrumentPanel1.randomWave(false);
				}

				@Override
				public String getName() {
					return null;
				}
			});
		}

		// 再判断登录后是否获取到用户套餐余量信息
		if (UserInfoControler.getInstance().getPhoneFreeQuery().isNormalState())
			initBaseTrafficInfo();
//		else {// 都不行，再加载一次
//			mTask = PhoneFreeQueryTask.execute(UserInfoControler.getInstance().getUserName(), this);
//		}

		getActivity().setTitle(getString(R.string.trafficServe));
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
	}

	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		if (position + 2 == mArrayRecommedPackage.size()) {
			TrafficPackageFragment fragment = new TrafficPackageFragment();
			Bundle bundle = new Bundle();
			onSaveInstanceState(bundle);
			fragment.setArguments(bundle);
			fragment.startFragment(mBaseActivity, R.id.fragmentContainer);
			return;
		}

		else if (position + 1 == mArrayRecommedPackage.size()) {
			new TrafficNotificationFragment().startFragment(mBaseActivity, R.id.fragmentContainer);
			return;
		}

		else if (mArrayBaseInfo != null && mArrayBaseInfo.size() > position) {
			goBusinessDetailPage(mArrayBaseInfo.get(position));
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog(true, false, new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				mTask.cancle();
			}
		});

		showDialog(getString(R.string.refreshing));
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();

		if (task.isBusinessAuthenticationTimeOut()) {
			mBaseActivity.showReLoginDialog();
			return;
		}

		if (result == TaskResult.OK) {
			initBaseTrafficInfo();
		} else if (task.getException() != null && task.getException().getMessage() != null) {
			UserInfoControler.getInstance().setPhoneFreeQuery(new PhoneFreeQuery(EmumState.CONNECT_ERROR));
			CommUtil.showAlert(getActivity(), getResources().getString(R.string.businessQueryFaild), task.getException().getMessage(), null);
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param != null) {
			PhoneFreeQuery phoneFreeQuery = PhoneFreeQuery.craeteByAnalysisMessage((String) param);
			UserInfoControler.getInstance().setPhoneFreeQuery(phoneFreeQuery);
		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	/**
	 * 初始化流量详情
	 */
	private void initBaseTrafficInfo() {

		UseCondition flowCondition = UserInfoControler.getInstance().getConditionFlow(null);
		if (flowCondition.getTotle() > 0) {// 详情初始化
			mBtnTrafficDetails.setOnClickListener(this);
			mBtnTrafficDetails.setVisibility(View.VISIBLE);
		}

		mSurplus.setText(flowCondition.getSurplusString());
		mUsed.setText(flowCondition.getUsedString());
		mTotle.setText(flowCondition.getTotleString());

		// mInstrumentPanel1.setMax((int) flowCondition.getTotle());
		// mInstrumentPanel1.setProgress((int) flowCondition.getSurplus());

		mInstrumentPanel1.setProgressAndMax((int) flowCondition.getSurplus(), (int) flowCondition.getTotle());

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	/**
	 * 基础流量列表
	 */
	protected void onCompleteBaseNetList() {
		initGridview();
		initBtRecommend(1);
	}

	@Override
	protected void onCompleteOverlayNetList() {

	}

	@Override
	protected void onCompleteIdleNetList() {

	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.TrafficServeFragement;
	}

	private final String[] mArrBusinessTag = { "sjll5y", "sjll10ytc", "sjll20ytc" };

	private final int[] mArrayBusinessDrawableRes = { R.drawable.icon_traffic_recomond_package_bg_5, R.drawable.icon_traffic_recomond_package_bg_10,
			R.drawable.icon_traffic_recomond_package_bg_20 };

	/**
	 * @param tag
	 * @param array
	 * @return 通过tag来从array中找到对应businessmenu所在
	 */
	private BusinessMenu getBusinessItemByTag(String tag, ArrayList<BusinessMenu> array) {
		if (tag != null && array != null && !array.isEmpty())
			for (BusinessMenu item : array)
				if (tag.equals(item.getBusTag()))
					return item;
		return null;
	}

	/**
	 * 初始化加载推荐信息
	 */
	private void initRecommondBusiness() {
		new GetRecommendBusinessTask().excute(UserInfoControler.getInstance().getUserName(), UserInfoControler.getInstance().getAuthorKey(),
				new TaskListener() {

					@Override
					public void onProgressUpdate(GenericTask task, Object param) {
						BusinessMenu item = (BusinessMenu) param;
						BusinessMenu itemobj = (BusinessMenu) mBtRecoomend.getTag();

						if (TextUtils.isEmpty(item.getName()) || TextUtils.isEmpty(item.getProdPrcid()) || TextUtils.isEmpty(item.getDescription())
								|| TextUtils.isEmpty(item.getCharges())) {// 返回内容为空，不继续
							LogUtlis.showLogE(TrafficServeFragement.class.getSimpleName(), "businessMenuItem has null info -> " + item.toString());
							return;
						}

						if (itemobj != null) {
							itemobj.setName(item.getName());
							itemobj.setProdPrcid(item.getProdPrcid());
							itemobj.setDescription(item.getDescription());
							itemobj.setCharges(item.getCharges());
							itemobj.setWarmPrompt(item.getWarmPrompt());
						} else {
							mBtRecoomend.setTag(item);
						}
						mBtRecoomend.setText(item.getName());
					}

					@Override
					public void onPreExecute(GenericTask task) {
					}

					@Override
					public void onPostExecute(GenericTask task, TaskResult result) {
					}

					@Override
					public void onCancelled(GenericTask task) {
					}

					@Override
					public String getName() {
						return null;
					}
				});
	}
}
