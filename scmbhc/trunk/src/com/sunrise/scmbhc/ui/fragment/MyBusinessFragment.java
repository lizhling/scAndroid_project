package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.MyBusinessAdapter;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.AdditionalTariffInfo;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.OpenedBusinessList;
import com.sunrise.scmbhc.entity.PhoneFreeQuery;
import com.sunrise.scmbhc.entity.PhoneFreeQuery.EmumState;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetOpenedUpBusinnessListTask;
import com.sunrise.scmbhc.task.PhoneFreeQueryTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.utils.CommUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyBusinessFragment extends BaseFragment implements OnClickListener {

	private ListView mListViewOrderedBusiness;
	private GenericTask mTask;
	/**
	 * 主资费信息、附加资费及增值业务信息
	 */
	private OpenedBusinessList mOpenedBusinessList;
	/**
	 * 主业务信息
	 */
	private TextView mMainBusinessName;
	/**
	 * 推荐业务列表
	 */
	private ListView mListViewFreeCondition;
	private TextView mCostMonth;
	private TextView mOrderedBusinessesNoContent;
	/**
	 * 用以显示主资费。主资费为空的时候，隐藏
	 */
	private View mMainBusinessView;
	/**
	 * 刷新按钮
	 */
	private Button mBtnRefresh;

	private AnimationDrawable ANIM_DRAWABLE;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isAdded()) {
			ANIM_DRAWABLE = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_loadingbar);
		}
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_business, container, false);

		mCostMonth = (TextView) view.findViewById(R.id.cost_this_month);

		mListViewOrderedBusiness = (ListView) view.findViewById(R.id.listview_orderedBusinesses);
		mOrderedBusinessesNoContent = (TextView) view.findViewById(R.id.orderedBusinessesNoContent);
		mListViewOrderedBusiness.setEmptyView(mOrderedBusinessesNoContent);

		mListViewFreeCondition = (ListView) view.findViewById(R.id.listview_usedConditon);

		mMainBusinessName = (TextView) view.findViewById(R.id.main_business_name);

		view.findViewById(R.id.lookCostThisMonth).setOnClickListener(this);

		mMainBusinessView = view.findViewById(R.id.main_business);
		return view;
	}

	public void onStart() {
		super.onStart();
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		mBtnRefresh = (Button) mBaseActivity.findViewById(R.id.headbar_rightbutton);
		setBtnRefreshEnable(true);
		mBaseActivity.setRightButton("刷新", RefreshListener);
		mBaseActivity.setRightButtonVisibility(View.VISIBLE);
		mBaseActivity.setTitle(R.string.myBusiness);
	}

	public void onResume() {
		super.onResume();
		if (!mBaseActivity.checkLoginIn(null))
			return;

		initData();
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

	OnClickListener RefreshListener = new OnClickListener() {

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
		mOpenedBusinessList = UserInfoControler.getInstance().getOpenedBusinessList();
		if (mOpenedBusinessList == null || mOpenedBusinessList.getTariffInfos() == null || mOpenedBusinessList.getTariffInfos().size() == 0) {// 重新加载
			mTask = new GetOpenedUpBusinnessListTask();
			mTask.setListener(mfGetOpenedUpBusinessTaskListener);
			mTask.execute();
		} else {
			initMainTraffic();
			initListView();
		}
		// 初始化余量信息的列表
		// ArrayList<UseCondition> array =
		// UserInfoControler.getInstance().getPhoneFreeQuery().getOtherPackages();
		// showTimeCost("UserInfoControler.getInstance().getPhoneFreeQuery().getOtherPackages()");
		// if (!array.isEmpty()) {
		// mListViewFreeCondition.setVisibility(View.VISIBLE);
		// UseConditionAdapter adapter = new UseConditionAdapter(mBaseActivity,
		// array);
		// mListViewFreeCondition.setAdapter(adapter);
		// CommUtil.expandListView(adapter, mListViewFreeCondition);
		// }
	}

	/**
	 * 初始化主资费列表
	 */
	private void initMainTraffic() {
		mCostMonth.setText(UserInfoControler.getInstance().getConsumptionThisMonth());

		// 主资费可能为空的情况
		if (mOpenedBusinessList != null && mOpenedBusinessList.getMainTraffic() != null) {
			mMainBusinessName.setText(CommUtil.deleteProdPrcidFromBusinessName(mOpenedBusinessList.getMainTraffic().getPROD_PRC_NAME()));

			mMainBusinessView.setVisibility(View.VISIBLE);
		} else {
			mMainBusinessView.setVisibility(View.GONE);
		}
		// mMainBusiness.findViewById(R.id.button1).setOnClickListener(null);
	}

	/**
	 * 初始化附加资费列表
	 */
	private void initListView() {
		if (mOpenedBusinessList == null || mOpenedBusinessList.getTariffInfos() == null || mOpenedBusinessList.getTariffInfos().isEmpty())
			return;
		mTask = new LoadExitBusinessMenuItemTask();
		mTask.setListener(mLoadExitBusinessMenuItemTaskListener);
		mTask.execute();
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

	private final TaskListener mfGetOpenedUpBusinessTaskListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
		}

		@Override
		public void onPreExecute(GenericTask task) {
			setBtnRefreshEnable(false);
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			setBtnRefreshEnable(true);

			if (result == TaskResult.OK) {
				mOpenedBusinessList = UserInfoControler.getInstance().getOpenedBusinessList();
				initMainTraffic();
				initListView();
				mTask = new PhoneFreeQueryTask().execute(UserInfoControler.getInstance().getUserName(), freeQueryListener);
			} else {
				if (task.isBusinessAuthenticationTimeOut())
					mBaseActivity.showReLoginDialog();
				else if (task.getException() != null && task.getException().getMessage() != null)
					CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), null);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			setBtnRefreshEnable(true);
		}

		@Override
		public String getName() {
			return null;
		}
	};
	TaskListener freeQueryListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			if (param != null) {
//				PhoneFreeQuery phoneFreeQuery = PhoneFreeQuery.craeteByAnalysisMessage((String) param);
//				UserInfoControler.getInstance().setPhoneFreeQuery(phoneFreeQuery);
				initData();
			}
		}

		@Override
		public void onPreExecute(GenericTask task) {

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			if (result == TaskResult.OK) {

			} else if (task.getException() != null && task.getException().getMessage() != null) {
//				UserInfoControler.getInstance().setPhoneFreeQuery(new PhoneFreeQuery(EmumState.CONNECT_ERROR));
				Toast.makeText(mBaseActivity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub
			if (mTask != null) {
				mTask.cancle();
			}
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * @param isEnabled
	 *            刷新按钮是否可控
	 */
	private void setBtnRefreshEnable(boolean isEnabled) {
		if (ANIM_DRAWABLE == null && isAdded()) {
			ANIM_DRAWABLE = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_loadingbar);
		}
		if (isEnabled) {
			if (ANIM_DRAWABLE != null) {
				ANIM_DRAWABLE.stop();
			}
			mBtnRefresh.setBackgroundResource(R.drawable.selector_ic_refresh);
			mBtnRefresh.setEnabled(true);
		} else {
			if (ANIM_DRAWABLE != null) {
				mBtnRefresh.setBackgroundDrawable(ANIM_DRAWABLE);
				ANIM_DRAWABLE.start();
			}
			mBtnRefresh.setEnabled(false);
		}

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

	class LoadExitBusinessMenuItemTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {

			ArrayList<AdditionalTariffInfo> array = mOpenedBusinessList.getTariffInfos();
			ScmbhcDbManager sdm = ScmbhcDbManager.getInstance(mBaseActivity.getContentResolver());
			for (AdditionalTariffInfo tariff : array) {
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
			showLoading();
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			showNormalText();
			MyBusinessAdapter adapter = new MyBusinessAdapter(mBaseActivity, mOpenedBusinessList.getTariffInfos());
			adapter.setOnClickOpenUp(MyBusinessFragment.this);
			if (mListViewOrderedBusiness != null) {
				mListViewOrderedBusiness.setAdapter(adapter);
				// CommUtil.expandListView(adapter, mListViewOrderedBusiness);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			showNormalText();
		}

		@Override
		public String getName() {
			return null;
		}

		private void showLoading() {
			if (ANIM_DRAWABLE == null && isAdded()) {
				ANIM_DRAWABLE = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_loadingbar);
			}
			if (ANIM_DRAWABLE != null) {
				ANIM_DRAWABLE.stop();
			}
			mOrderedBusinessesNoContent.setText(R.string.loading_status);
			mOrderedBusinessesNoContent.setCompoundDrawablesWithIntrinsicBounds(ANIM_DRAWABLE, null, null, null);
			ANIM_DRAWABLE.start();
		}

		private void showNormalText() {
			mOrderedBusinessesNoContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mOrderedBusinessesNoContent.setText(R.string.noCotent);
		}
	};

}
