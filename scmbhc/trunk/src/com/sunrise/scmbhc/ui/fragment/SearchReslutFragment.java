package com.sunrise.scmbhc.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.adapter.SearchBusinessAdapter;
import com.sunrise.scmbhc.adapter.SearchPreferntialInfoAdapter;
import com.sunrise.scmbhc.adapter.SpinnerAdapter;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.PreferentialInfo;
import com.sunrise.scmbhc.entity.SearchKeyWord;
import com.sunrise.scmbhc.entity.SearchTag;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetSearchsByKeyTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.view.MySpinner;

@SuppressLint("ValidFragment")
public class SearchReslutFragment extends BaseFragment {
	private MySpinner mSpinner;
	private EditText mEditText;
	private SpinnerAdapter mSpinnerAdapter;
	private String[] mSearchTypes;
	private ListView mSearchBusinessListView;
	private ListView mSearchPreferntialInfoListView;
	private SearchBusinessAdapter mSearchBusinessAdapter;
	private SearchPreferntialInfoAdapter mSearchPreferntialInfoAdapter;
	private LinearLayout mSearchBusinessPanel;
	private LinearLayout mSearchPreferntialInfoPanel;
	private LinearLayout mSearchReslutSplit;
	private TaskListener mSearchsListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreExecute(GenericTask task) {
			initDialog(false, false, null);
			showDialog(mBaseActivity.getText(R.string.searching));
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			dismissDialog();
			if (result == TaskResult.OK) {
				refreshSearchReslut();
			} else {

			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doSearchByKey();
	};

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_reslut, container, false);
		mEditText = (EditText) view.findViewById(R.id.searck_key);
		mSearchTypes = mBaseActivity.getResources().getStringArray(R.array.searchType);
		mSpinnerAdapter = new SpinnerAdapter(mBaseActivity, mSearchTypes);
		mSpinner = (MySpinner) view.findViewById(R.id.search_type);
		mSpinner.setAdapter(mSpinnerAdapter);
		if (SearchFragment.sSearchKeyWord != null) {
			mSpinner.setSelectIndex(SearchFragment.sSearchKeyWord.getSearchType());
			mEditText.setText(SearchFragment.sSearchKeyWord.getKeyWord());
		} else {
			mSpinner.setSelectIndex(0);
		}
		view.findViewById(R.id.search_bt).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String key = mEditText.getText().toString();
				int type = mSpinner.getSelectIndex();
				if (TextUtils.isEmpty(key)) {
					Toast.makeText(mBaseActivity, R.string.search_hint, Toast.LENGTH_SHORT).show();
					return;
				} else {
					SearchFragment.sSearchKeyWord = new SearchKeyWord(type, key, SearchTag.USER_TYPE);
					doSearchByKey();
				}

			}
		});
		mSearchBusinessListView = (ListView) view.findViewById(R.id.business_search_result);
		mSearchPreferntialInfoListView = (ListView) view.findViewById(R.id.preferntilainfo_search_result);
		mSearchBusinessAdapter = new SearchBusinessAdapter(App.sSearchBusinesses, mBaseActivity);
		mSearchBusinessListView.setAdapter(mSearchBusinessAdapter);
		mSearchBusinessListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				BusinessMenu businessMenu = App.sSearchBusinesses.get(position);
				businessMenu.visitByNewActivity(mBaseActivity);
			}
		});
		mSearchPreferntialInfoAdapter = new SearchPreferntialInfoAdapter(App.sSearchPreferentialInfos, mBaseActivity);
		mSearchPreferntialInfoListView.setAdapter(mSearchPreferntialInfoAdapter);
		mSearchPreferntialInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				PreferentialInfo preferentialInfo = App.sSearchPreferentialInfos.get(position);
				preferentialInfo.visit(mBaseActivity);
			}
		});
		mSearchBusinessPanel = (LinearLayout) view.findViewById(R.id.search_business_panel);
		mSearchPreferntialInfoPanel = (LinearLayout) view.findViewById(R.id.search_discount_panel);
		mSearchReslutSplit = (LinearLayout) view.findViewById(R.id.search_reslut_split);
		refreshSearchReslut();
		return view;
	}

	private void refreshSearchReslut() {
		boolean isShowSplit = true;
		if (App.sSearchBusinesses.size() != 0) {
			mSearchBusinessPanel.setVisibility(View.VISIBLE);
		} else {
			mSearchBusinessPanel.setVisibility(View.GONE);
			isShowSplit = false;
		}
		mSearchBusinessAdapter.notifyDataSetChanged();
		if (App.sSearchPreferentialInfos.size() != 0) {
			mSearchPreferntialInfoPanel.setVisibility(View.VISIBLE);
		} else {
			mSearchPreferntialInfoPanel.setVisibility(View.GONE);
			isShowSplit = false;
		}
		mSearchPreferntialInfoAdapter.notifyDataSetChanged();
		if (isShowSplit)
			mSearchReslutSplit.setVisibility(View.VISIBLE);
		else
			mSearchReslutSplit.setVisibility(View.GONE);
	}

	private void doSearchByKey() {
		GenericTask getSearchsByKeyTask = new GetSearchsByKeyTask(mBaseActivity, SearchFragment.sSearchKeyWord);
		getSearchsByKeyTask.setListener(mSearchsListener);
		getSearchsByKeyTask.execute();
		App.sTaskManager.addObserver(getSearchsByKeyTask);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		mBaseActivity.setTitle(R.string.search_result);
	}
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.SearchReslutFragment;
	}

}
