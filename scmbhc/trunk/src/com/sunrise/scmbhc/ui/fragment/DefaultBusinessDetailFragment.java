package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.adapter.SubBusinessAdapter;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetOpenedUpBusinnessListTask;
import com.sunrise.scmbhc.task.HandleBusinessTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

public abstract class DefaultBusinessDetailFragment extends BaseFragment {
	private static final int QUERY_SUB_LIST_TOKEN = 1703;
	protected static final String FORMAT_SHOW_DESCRIPTION = "<p align=\"center\"><b>业务详情</b></p><p align=\"center\">%s</p><p></p><p align=\"center\"><b>资费信息</b></p><p align=\"center\"><i><font color=\"#ff0000\">%s</font></i></p><p></p><p align=\"center\"><b>温馨提示</b></p><p align=\"center\">%s</p>";
	protected ArrayList<BusinessMenu> mArraySubBusinessinfos;
	protected SubBusinessAdapter mSubBusinessAdapter;
	protected BusinessMenu mBusinessMenu;
	private GenericTask mTask;
	private ScmbhcDbManager mScmbhcDbManager;
	private ContentResolver mContentResolver;
	private AsyncQueryHandler mQueryHandler;
	protected RadioGroup mTabWedget;

	protected ViewSwitcher mSwitcher;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null)
			this.mBusinessMenu = (BusinessMenu) savedInstanceState.get(App.ExtraKeyConstant.KEY_BUSINESS_INFO);
		else if (getArguments() != null)
			this.mBusinessMenu = (BusinessMenu) getArguments().get(App.ExtraKeyConstant.KEY_BUSINESS_INFO);

		initDatabase();
		LogUtlis.showLogE(getClass().getSimpleName(), "onCreate");
		mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler, QUERY_SUB_LIST_TOKEN, mBusinessMenu.getId());
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(App.ExtraKeyConstant.KEY_BUSINESS_INFO, mBusinessMenu);
		super.onSaveInstanceState(outState);
	}

	private void initDatabase() {
		mContentResolver = mBaseActivity.getContentResolver();
		mScmbhcDbManager = ScmbhcDbManager.getInstance(mContentResolver);
		mQueryHandler = new QueryHandler(mContentResolver);
	}

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_business_default, container, false);
		initView(view);
		initSubBusinessList(view);
		return view;
	}

	protected void initView(View view) {
		mTabWedget = (RadioGroup) view.findViewById(R.id.radioGroup1);

		mTabWedget.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int index = arg0.indexOfChild(arg0.findViewById(arg1));
				if (mSwitcher.getDisplayedChild() != index)
					if (index == 0)
						mSwitcher.showNext();
					else
						mSwitcher.showPrevious();

				onTabChangeed(index);
			}
		});
		mSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcher01);

		if (mBusinessMenu != null) {
			TextView textview_showHtml = (TextView) view.findViewById(R.id.textView_showHtml);
			String description = mBusinessMenu.getDescription();
			String charge = mBusinessMenu.getCharges();
			String warmNotice = mBusinessMenu.getWarmPrompt();
			if (description != null)
				textview_showHtml.setText(Html.fromHtml(String.format(FORMAT_SHOW_DESCRIPTION, description, charge, warmNotice)));
		}

	}

	protected void initSubBusinessList(View view) {
		if (mBusinessMenu != null) {
			if (mArraySubBusinessinfos == null)
				mArraySubBusinessinfos = new ArrayList<BusinessMenu>();
			if (mArraySubBusinessinfos.isEmpty())
				mArraySubBusinessinfos.add(mBusinessMenu);

			ListView subBusinessView = (ListView) view.findViewById(R.id.listview_subbusiness);
			mSubBusinessAdapter = new SubBusinessAdapter(getActivity(), mArraySubBusinessinfos);
			mSubBusinessAdapter.setOnClickOpenUp(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					doBusinessHandle((BusinessMenu) arg0.getTag());
				}
			});
			subBusinessView.setAdapter(mSubBusinessAdapter);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onStart() {
		super.onStart();
		if (mBusinessMenu != null) {
			mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
			mBaseActivity.setTitle(mBusinessMenu.getName());
		}

		if (UserInfoControler.getInstance().checkUserLoginIn()) {
			mBaseActivity.setRightButtonVisibility(View.GONE);
		} else {
			mBaseActivity.setRightButton(getResources().getString(R.string.login), new OnClickListener() {
				@Override
				public void onClick(View v) {
					mBaseActivity.checkLoginIn(null);
				}
			});
		}
	}

	public void onStop() {
		super.onStop();
		doCancelTask();
	}

	private void doCancelTask() {
		if (mTask != null && mTask.getStatus() != HandleBusinessTask.Status.FINISHED)
			mTask.cancle();
	}

	/**
	 * 处理业务
	 * 
	 * @param item
	 */
	private void doBusinessHandle(BusinessMenu item) {
		if (item == null)
			return;

		if (!mBaseActivity.checkLoginIn(null))// 检测是否登录。未登录，启动登录页面
		{
			Toast.makeText(mBaseActivity, getResources().getString(R.string.unlogin_notice), Toast.LENGTH_LONG).show();
			return;
		}

		doCancelTask();

		mTask = new HandleBusinessTask();
		mTask.setListener(new TaskListener() {
			@Override
			public String getName() {
				return "业务办理";
			}

			@Override
			public void onPreExecute(GenericTask task) {
				initDialog(false, false, null);
				showDialog(getActivity().getResources().getString(R.string.onDealing));
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				dismissDialog();

				if (result == TaskResult.OK) {
					// 刷新
					doRefresh();
				} else {
					if (task.isBusinessAuthenticationTimeOut())
						mBaseActivity.showReLoginDialog();
					else if (task.getException() != null && task.getException().getMessage() != null) {
						CommUtil.showAlert(getActivity(), getResources().getString(R.string.businessDealFaild), task.getException().getMessage(), null);
					}
				}
			}

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				// 启动刷新用户已办业务的线程
				HandleBusinessTask tTask = (HandleBusinessTask) task;
				String title = tTask.getBusinessItem().getName() + getResources().getString(R.string.businessDealSuccess);
				String message = title;
				if (CommUtil.isContainChinese((String) param))
					message = (String) param;
				else
					title = null;

				CommUtil.showAlert(getActivity(), title, message, null);
			}

			/*
			 * if (mArraySubBusinessinfos.size() < 2) { if
			 * (getActivity().getSupportFragmentManager
			 * ().getBackStackEntryCount() < 1) getActivity().finish(); } else {
			 * finish(getActivity()); }
			 */

			@Override
			public void onCancelled(GenericTask task) {
				dismissDialog();
			}
		});
		mTask.execute(new TaskParams(ExtraKeyConstant.KEY_BUSINESS_INFO, item));
	}

	/**
	 * 办理成功，刷新业务逻辑
	 */
	private void doRefresh() {
		mTask = new GetOpenedUpBusinnessListTask();
		mTask.setListener(new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
			}

			@Override
			public void onPreExecute(GenericTask task) {
				initDialog(false, false, null);
				showDialog(getActivity().getResources().getString(R.string.onListLoading));
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				dismissDialog();

				if (result == TaskResult.OK) {
					mSubBusinessAdapter.notifyDataSetChanged();
				} else {
					if (task.isBusinessAuthenticationTimeOut())
						mBaseActivity.showReLoginDialog();
					else if (task.getException() != null && task.getException().getMessage() != null)
						CommUtil.showAlert(getActivity(), getResources().getString(R.string.error), task.getException().getMessage(), null);
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
		});
		mTask.execute();
	}

	protected void onTabChangeed(int index) {

	}

	public void showIndexPage(int index) {
		final int length = mTabWedget.getChildCount();
		mTabWedget.check(mTabWedget.getChildAt(index).getId());
	}

	protected void showNextPage() {
		final int length = mTabWedget.getChildCount();
		int index = mTabWedget.indexOfChild(mTabWedget.findViewById(mTabWedget.getCheckedRadioButtonId()));
		mTabWedget.check(mTabWedget.getChildAt(++index % length).getId());
	}

	class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			switch (token) {
			case QUERY_SUB_LIST_TOKEN:
				ArrayList<BusinessMenu> subBusinessMenus = mScmbhcDbManager.cursorToBusinessBusinessMenus(cursor);
				if (subBusinessMenus.size() > 0) {
					if (mArraySubBusinessinfos != null) {
						mArraySubBusinessinfos.clear();
						mArraySubBusinessinfos.addAll(subBusinessMenus);
						mSubBusinessAdapter.notifyDataSetChanged();
					}
				}
				break;
			}
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
		}
	}

}
