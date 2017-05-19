package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.HallDetailActivity;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.adapter.BusinessHallsAdapter;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.entity.TabContent;
import com.sunrise.marketingassistant.entity.TabContentManager;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.fragment.WebViewFragment2;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetChannelSearchTaskByGroupId;
import com.sunrise.marketingassistant.task.GetMobPictureTask;
import com.sunrise.marketingassistant.task.MobInfoByGroupIdTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.CommUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;

public class ChannelListFragment extends BaseFragment implements ExtraKeyConstant, OnClickListener, TaskListener, OnItemSelectedListener, OnItemClickListener,
		OnRefreshListener<ListView> {

	private static final int NUM_OF_PAGE = 20;

	private BusinessHallsAdapter adapter;
	private ArrayList<MobileBusinessHall> mArrMobileBusinessHalls;
	private GenericTask mTask;
	private AutoCompleteTextView mSearchBar;

	private Spinner spinner_channelType;
	private Spinner spinner_starlevel;

	/** 是否刷新 */
	private boolean isRefresh;

	/** 是否搜索模式 */
	private boolean mIsSearchMode;
	private PullToRefreshListView refreshListView;

	private int mIndexTemp;

	private GetMobPictureTask mGetMobPicture;

	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		if (getArguments() != null)
			mIsSearchMode = getArguments().getBoolean(Intent.EXTRA_CC);

		mArrMobileBusinessHalls = new ArrayList<MobileBusinessHall>();
	}

	@SuppressLint("InflateParams")
	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_search, null, false);

		initViews(view);

		if (mIsSearchMode)
			return view;

		String jsonHall = getPreferences().getCacheString(getClass().getSimpleName() + "_Hall");
		if (jsonHall != null)
			try {
				JSONArray jsonArray = new JSONArray(jsonHall);
				for (int i = 0; i < jsonArray.length(); i++)
					mArrMobileBusinessHalls.add(JsonUtils.parseJsonStrToObject(jsonArray.getString(i), MobileBusinessHall.class));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		String temp = getPreferences().getCacheString(getClass().getSimpleName() + "_SEARCH_TEXT");
		if (temp != null)
			mSearchBar.setText(temp);

		temp = getPreferences().getCacheString(getClass().getSimpleName() + "_CHANNEL_TYPE");
		if (temp != null)
			spinner_channelType.setSelection(Integer.parseInt(temp));

		temp = getPreferences().getCacheString(getClass().getSimpleName() + "_STAR_LEVEL");
		if (temp != null)
			spinner_starlevel.setSelection(Integer.parseInt(temp));

		if (mArrMobileBusinessHalls.isEmpty())
			doSearch();

		return view;
	}

	private void initViews(View view) {
		refreshListView = (PullToRefreshListView) view.findViewById(R.id.listView_hotSearch);
		refreshListView.setOnItemClickListener(this);
		adapter = new BusinessHallsAdapter(mArrMobileBusinessHalls, mBaseActivity);
		refreshListView.setAdapter(adapter);
		adapter.setViewMapClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (arg0.getTag() == null)
					return;
				int index = (Integer) arg0.getTag();
				gotoMapPage(index);
			}
		});
		refreshListView.setMode(Mode.BOTH);
		refreshListView.setOnRefreshListener(this);

		mSearchBar = (AutoCompleteTextView) view.findViewById(R.id.editText_search);
		mSearchBar.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg1 == KeyEvent.KEYCODE_ENTER) {
					isRefresh = true;
					doSearch();
				}
				return false;
			}
		});
		view.findViewById(R.id.btn_search).setOnClickListener(this);
		{
			spinner_channelType = (Spinner) view.findViewById(R.id.spinner_channelType);
			spinner_channelType.setOnItemSelectedListener(this);
		}
		{
			spinner_starlevel = (Spinner) view.findViewById(R.id.spinner_starlevel);
			spinner_starlevel.setOnItemSelectedListener(this);
		}
	}

	public void onDestroy() {
		if (mTask != null)
			mTask.cancle();

		for (GenericTask task : mArrayTask) {
			task.cancle();
		}

		if (!mIsSearchMode)
			doCatheState();

		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.btn_search:
			isRefresh = true;
			doSearch();
			break;
		default:
			break;
		}
	}

	private void doSearch() {
		String searchStr = mSearchBar.getText().toString();
		Preferences p = Preferences.getInstance(mBaseActivity);
		int start = isRefresh ? 0 : mArrMobileBusinessHalls.size();

		if (mTask != null)
			mTask.cancle();

		int index = spinner_starlevel.getSelectedItemPosition();
		String starLevel = null;
		if (index != 0)
			starLevel = getResources().getStringArray(R.array.starLevelNames)[index];

		index = spinner_channelType.getSelectedItemPosition();
		String mobClass = null;
		if (index != 0)
			mobClass = getResources().getStringArray(R.array.channelTypeNames)[index];
		mTask = new GetChannelSearchTaskByGroupId().execute(p.getMobile(), searchStr, p.getSubAccount(), starLevel, mobClass, start, start + NUM_OF_PAGE, this);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		if (refreshListView.isRefreshing())
			return;
		initDialog(true, false, null);

		String notice = null;
		if (!isResumed())
			notice = getString(R.string.onListLoading);
		else if (task instanceof GetChannelSearchTaskByGroupId) {
			notice = String.format("%s \"%s\"", getString(R.string.search), mSearchBar.getText().toString());
		} else if (task instanceof MobInfoByGroupIdTask) {
			notice = "获取详情中……";
		} else
			notice = "加载中……";

		showDialog(notice);
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (refreshListView.isRefreshing()) {
			refreshListView.onRefreshComplete();
		}
		if (result != TaskResult.OK) {
			CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), null, null);
			return;
		}

		if (task instanceof MobInfoByGroupIdTask) {
			gotoHallDetail(mArrMobileBusinessHalls.get(mIndexTemp));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (task instanceof GetChannelSearchTaskByGroupId) {
			if (isRefresh)
				mArrMobileBusinessHalls.clear();
			mArrMobileBusinessHalls.addAll((ArrayList<MobileBusinessHall>) param);
			adapter.notifyDataSetChanged();
		}

		else if (task instanceof MobInfoByGroupIdTask) {
			if (mIndexTemp >= 0) {
				MobileBusinessHall tempHall = mArrMobileBusinessHalls.get(mIndexTemp);
				MobileBusinessHall hall = (MobileBusinessHall) param;
				tempHall.setACTIVE_TIME(hall.getACTIVE_TIME());
				tempHall.setCLASS_CODE(hall.getCLASS_CODE());
				tempHall.setCLASS_NAME(hall.getCLASS_NAME());
				tempHall.setCONTACT_PERSON(hall.getCONTACT_PERSON());
				tempHall.setCONTACT_PHONE(hall.getCONTACT_PHONE());
				tempHall.setGRADE_CODE(hall.getGRADE_CODE());
				tempHall.setGRADE_NAME(hall.getGRADE_NAME());
				tempHall.setGROUP_ADDRESS(hall.getGROUP_ADDRESS());
				tempHall.setGROUP_AREA(hall.getGROUP_AREA());
				// tempHall.setGROUP_ID(hall.getGROUP_ID());
				// tempHall.setGROUP_NAME(hall.getGROUP_NAME());
				tempHall.setIMG_INFO(hall.getIMG_INFO());
				tempHall.setLATITUDE(hall.getLATITUDE());
				tempHall.setLONGITUDE(hall.getLONGITUDE());
				tempHall.setRWD_TOTAL(hall.getRWD_TOTAL());

				// final LatLng pt = new LatLng(tempHall.getLATITUDE(),
				// tempHall.getLONGITUDE());
				adapter.notifyDataSetChanged();
			}

		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
		if (refreshListView.isRefreshing()) {
			refreshListView.onRefreshComplete();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.spinner_starlevel:
			break;
		case R.id.spinner_channelType:
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mIndexTemp = arg2 - 1;

		MobileBusinessHall hall = mArrMobileBusinessHalls.get(arg2 - 1);
		// if (TextUtils.isEmpty(hall.getGROUP_ADDRESS()))
		// doGetGroupInfo(hall.getGROUP_ID());
		// else
		gotoHallDetail(hall);
	}

	private void gotoHallDetail(MobileBusinessHall hall) {
		Intent intent = null;
		TabContent temp = TabContentManager.getInstance(mBaseActivity).getTabContent(ID_TAB_ITEM_CHANNEL_DETAIL);
		intent = HallDetailActivity.createIntent(mBaseActivity, WebViewFragment2.class, temp.getZipContent(), temp.getTabName(), temp.getLastModify(),
				hall.getGROUP_ID());
		startActivity(intent);
	}

	private TabContent getTabContent(int id) {
		String jsonStr = FileUtils.getTextFromAssets(mBaseActivity, "tabInfo.txt", "utf-8");
		jsonStr = Preferences.getInstance(mBaseActivity).getString(UpdateInfo.KEY_TAB_INFOS, jsonStr);
		try {
			JSONArray jsarray = new JSONObject(jsonStr).getJSONArray("datas");

			for (int i = 0; i < jsarray.length(); ++i) {
				TabContent temp = JsonUtils.parseJsonStrToObject(jsarray.getString(i), TabContent.class);
				if (temp.getId() == id)
					return temp;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.isHeaderShown()) {
			isRefresh = true;
		} else if (refreshView.isFooterShown()) {
			isRefresh = false;
		}
		doSearch();
	}

	protected void gotoMapPage(int index) {
		Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(Intent.EXTRA_DATA_REMOVED, mArrMobileBusinessHalls);
		bundle.putInt(Intent.EXTRA_UID, index);

		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		intent.putExtra(KEY_FRAGMENT, MapLocationShowFragment.class);
		startActivityForResult(intent, REQUEST_GO_MAP_SHOW);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		if (requestCode == REQUEST_GO_MAP_SHOW) {
			MobileBusinessHall item = data.getParcelableExtra(Intent.EXTRA_DATA_REMOVED);
			gotoHallDetail(item);
		}
	}

	private void doCatheState() {
		if (mArrMobileBusinessHalls == null)
			return;

		getPreferences().putCacheString(getClass().getSimpleName() + "_Hall", JsonUtils.writeObjectToJsonStr(mArrMobileBusinessHalls));
		getPreferences().putCacheString(getClass().getSimpleName() + "_SEARCH_TEXT", mSearchBar.getText().toString());
		getPreferences().putCacheString(getClass().getSimpleName() + "_CHANNEL_TYPE", String.valueOf(spinner_channelType.getSelectedItemPosition()));
		getPreferences().putCacheString(getClass().getSimpleName() + "_STAR_LEVEL", String.valueOf(spinner_starlevel.getSelectedItemPosition()));
	}

	private void doGetGroupInfo(String groupId) {

		if (mTask != null)
			mTask.cancle();
		mTask = new MobInfoByGroupIdTask().execute(getPreferences().getMobile(), groupId, getPreferences().getSubAccount(), this);
	}

	private ArrayList<GenericTask> mArrayTask = new ArrayList<GenericTask>();

	private class GetGroupInfoListener implements TaskListener {
		protected MobileBusinessHall hall;

		public GetGroupInfoListener(MobileBusinessHall hall) {
			this.hall = hall;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public void onPreExecute(GenericTask task) {
			mArrayTask.add(task);
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			mArrayTask.remove(task);
			if (result != TaskResult.OK) {
				LogUtlis.e(getClass().getSimpleName(), task.getException().toString());
				return;
			}
		}

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			MobileBusinessHall temp = (MobileBusinessHall) param;
			hall.merge(temp);
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onCancelled(GenericTask task) {
			mArrayTask.remove(task);
		}

	}

}
