package com.sunrise.marketingassistant.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.javascript.utils.DateUtils;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.HallDetailActivity;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.entity.TabContent;
import com.sunrise.marketingassistant.entity.TabContentManager;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetRegisteTrajectoryTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.view.DefaultTimeAndDatePickerDialog;

/**
 * 渠道轨迹列表
 * 
 * @author 珩
 * 
 */
public class RegisteTrajectoryFragment extends BaseFragment implements OnClickListener, TaskListener, ExtraKeyConstant, OnItemClickListener {

	private GenericTask mTask;
	private TextView mTVStartTime;
	private TextView mTVEndTime;
	private ListView mListView;
	private ArrayList<HashMap<String, String>> mArrayHashRegiste;
	private SimpleAdapter mAdapter;
	private ArrayList<MobileBusinessHall> mArrayRegisteTrajectory;

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_registe_trajectory, container, false);
		view.findViewById(R.id.btn_search).setOnClickListener(this);

		mTVStartTime = (TextView) view.findViewById(R.id.textView_startTime);
		mTVEndTime = (TextView) view.findViewById(R.id.textView_endTime);

		mTVStartTime.setOnClickListener(this);
		mTVEndTime.setOnClickListener(this);

		mListView = (ListView) view.findViewById(R.id.listView_registe);
		mListView.setEmptyView(view.findViewById(R.id.textView_noContent));
		mListView.setOnItemClickListener(this);

		initData();
		return view;
	}

	private void initData() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		mTVStartTime.setText(String.format(FORMAT_DATA_AND_TIME, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), 0, 0));
		mTVEndTime.setText(DateUtils.formatlong2Time(System.currentTimeMillis(), FORMAT_SHOWING_TIME));

		final String[] FROM_FOR_ADAPTER = { KEY_AUTH, KEY_TIME };
		final int[] TO_FOR_ADAPTER = { android.R.id.text1, android.R.id.text2 };
		mArrayHashRegiste = new ArrayList<HashMap<String, String>>();
		mAdapter = new SimpleAdapter(mBaseActivity, mArrayHashRegiste, R.layout.simple_list_item_2, FROM_FOR_ADAPTER, TO_FOR_ADAPTER);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void onDestroy() {
		if (mTask != null)
			mTask.cancle();
		super.onDestroy();
	}

	private void doLoadRegisteTrajectoryList() {
		try {
			String startTime = DateUtils.formatlong2Time(DateUtils.string2Long(mTVStartTime.getText().toString(), FORMAT_SHOWING_TIME), FORMAT_PARAM_TIME);
			String endTime = DateUtils.formatlong2Time(DateUtils.string2Long(mTVEndTime.getText().toString(), FORMAT_SHOWING_TIME), FORMAT_PARAM_TIME);
			mTask = new GetRegisteTrajectoryTask().execute(getPreferences().getSubAccount(), startTime, endTime, this);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			doLoadRegisteTrajectoryList();
			break;
		case R.id.textView_startTime:
		case R.id.textView_endTime:
			new DefaultTimeAndDatePickerDialog(mBaseActivity).setAssociatedTextView((TextView) v)
					.setTimeLimit(System.currentTimeMillis() - 91L * 24 * 3600 * 1000).show();
			break;
		}
	}

	private void refreshListRegiste(ArrayList<MobileBusinessHall> array) {
		if (array == null)
			return;
		mArrayRegisteTrajectory = (ArrayList<MobileBusinessHall>) array;

		mArrayHashRegiste.clear();
		for (MobileBusinessHall been : array) {
			HashMap<String, String> hash = new HashMap<String, String>();
			hash.put(KEY_AUTH, been.getGROUP_NAME());
			hash.put(KEY_TIME, been.getREGISTIME());
			mArrayHashRegiste.add(hash);
		}
		mAdapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		refreshListRegiste((ArrayList<MobileBusinessHall>) param);
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog();
		showDialog(getName());
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK)
			Toast.makeText(mBaseActivity, getString(R.string.latest_version), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	@Override
	public String getName() {
		return "搜索从" + mTVStartTime.getText().toString() + " \n到 " + mTVEndTime.getText().toString() + "\n之间的签到记录";
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		gotoMapPage(position);
	}

	private void gotoMapPage(int index) {
		Intent intent = null;
		intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putBoolean(Intent.EXTRA_CC, true);
		bundle.putParcelableArrayList(Intent.EXTRA_DATA_REMOVED, mArrayRegisteTrajectory);
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

	private void gotoHallDetail(MobileBusinessHall hall) {
		Intent intent = null;
		TabContent temp = TabContentManager.getInstance(mBaseActivity).getTabContent(ID_TAB_ITEM_CHANNEL_DETAIL);
		intent = HallDetailActivity.createIntent(mBaseActivity, WebViewFragment2.class, temp.getZipContent(), temp.getTabName(), temp.getLastModify(),
				hall.getGROUP_ID());
		startActivity(intent);
	}
}
