package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.BillConditionExpandableAdapter;
import com.sunrise.scmbhc.adapter.BillExpandableAdapter;
import com.sunrise.scmbhc.adapter.BillStringExpandableAdapter;
import com.sunrise.scmbhc.entity.BillApxPageSecoundLevelItem;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.QueryBillApxPageTask;
import com.sunrise.scmbhc.task.QueryCurMonthBillDetailTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.DateUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * 我的帐单
 * 
 * @author fuheng
 * @since 2014年9月29日17:20:48
 */
public class BillApxPageQueryFragment extends BaseFragment implements OnSeekBarChangeListener, OnCheckedChangeListener {

	private static final String FORMAAT_MONTH = "%2d/%d";

	private final String KEY_CACHE = "cache";

	private static final int[] RES_ID_MONTHS = { R.id.month_1, R.id.month_2, R.id.month_3, R.id.month_4, R.id.month_5, R.id.month_6 };

	private GenericTask mTask;

	private String mQueryTime;

	private SeekBar mSeekBarMonth;

	private int mCurIndex = -1;

	private JSONObject mCachBillInfo;

	/**
	 * 月份刻度可点击
	 */
	private OnClickListener mListenerMonth = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int index = (Integer) v.getTag();
			mSeekBarMonth.setProgress(2 * index + 1);
			onStopTrackingTouch(mSeekBarMonth);
		}
	};

	private ViewFlipper mTabHost;
	private RadioGroup mTabWidget;

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_query_bill_apx_page, container, false);

		mTabHost = (ViewFlipper) view.findViewById(R.id.viewFlipper1);

		mTabWidget = (RadioGroup) view.findViewById(R.id.tabs);
		mTabWidget.setOnCheckedChangeListener(this);

		mSeekBarMonth = (SeekBar) view.findViewById(R.id.seekBar_month);
		mSeekBarMonth.setMax(RES_ID_MONTHS.length << 1);
		mSeekBarMonth.setProgress(mSeekBarMonth.getMax() - 1);
		mSeekBarMonth.setOnSeekBarChangeListener(this);

		for (int i = 0; i < RES_ID_MONTHS.length; ++i) {
			TextView month = (TextView) view.findViewById(RES_ID_MONTHS[i]);
			month.setText(getMonthPast(mSeekBarMonth.getMax() / 2 - i - 1));
			month.setTag(i);
			month.setOnClickListener(mListenerMonth);
		}

		if (savedInstanceState == null)
			mCachBillInfo = new JSONObject();
		else
			try {
				mCachBillInfo = new JSONObject(savedInstanceState.getString(KEY_CACHE));
			} catch (Exception e) {
				e.printStackTrace();
				mCachBillInfo = new JSONObject();
			}

		return view;
	}

	private void startLoad(int past) {
		if (mTask != null)
			mTask.cancle();

		mQueryTime = CommUtil.getMonthPast(FORMAAT_MONTH, past);
		System.err.println(past + ", " + mQueryTime);
		if (mCachBillInfo.has(mQueryTime)) {
			try {
				initData(mCachBillInfo.getString(mQueryTime));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}

		if (past == 0)
			mTask = startQueryCurMonthBillDetailTask();
		else
			mTask = startQueryBillApxPageTask(past);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!mBaseActivity.checkLoginIn(null)) {
			return;
		}
		// 成功后初始化数据
		mBaseActivity.setTitle(getResources().getString(R.string.myBill));
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		startLoad(mSeekBarMonth.getMax() / 2 - (mSeekBarMonth.getProgress() - 1) / 2 - 1);
	}

	public void onStart() {
		super.onStart();
		CommUtil.dismissAlert();

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	public void onSaveInstence(Bundle outState) {
		outState.putString(KEY_CACHE, mCachBillInfo.toString());
		super.onSaveInstanceState(outState);
	}

	private void initData(String str) {
		try {
			ArrayList<String> arrayTitles = new ArrayList<String>();
			ArrayList<Byte> arrayType = new ArrayList<Byte>();
			ArrayList<ArrayList<BillApxPageSecoundLevelItem>> billInfoArray = BillApxPageSecoundLevelItem.analysisJson(mBaseActivity, new JSONObject(str),
					arrayTitles, arrayType);

			for (int i = 0; i < mTabWidget.getChildCount(); ++i) {
				RadioButton radio = (RadioButton) mTabWidget.getChildAt(i);
				if (i < arrayTitles.size()) {
					radio.setText(arrayTitles.get(i));
					radio.setVisibility(View.VISIBLE);
				} else {
					radio.setText(R.string.none_bound);
					radio.setChecked(false);
					radio.setVisibility(View.GONE);
				}

				ExpandableListView listview = (ExpandableListView) mTabHost.getChildAt(i);
				if (i < billInfoArray.size()) {
					BillExpandableAdapter adapter = null;

					switch (arrayType.get(i)) {
					case 2:
						adapter = new BillConditionExpandableAdapter(listview.getContext(), billInfoArray.get(i));
						break;
					case 0:
						adapter = new BillStringExpandableAdapter(listview.getContext(), billInfoArray.get(i));
						break;
					default:
						adapter = new BillExpandableAdapter(listview.getContext(), billInfoArray.get(i));
						break;
					}
					listview.setAdapter(adapter);
					for (int index = 0; index < adapter.getGroupCount(); ++index)
						listview.expandGroup(index);
				}
			}

			mTabWidget.check(mTabWidget.getChildAt(0).getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		if (mCurIndex != seekBar.getProgress()) {
			startLoad(seekBar.getMax() / 2 - (seekBar.getProgress() - 1) / 2 - 1);
			mCurIndex = seekBar.getProgress();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		int index = arg0.indexOfChild(arg0.findViewById(id));
		int lastIndex = mTabHost.getDisplayedChild();

		if (index > lastIndex) {
			mTabHost.setInAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.slide_right_in));
			mTabHost.setOutAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.slide_left_out));
		} else {
			mTabHost.setInAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.slide_left_in));
			mTabHost.setOutAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.slide_right_out));
		}
		mTabHost.setDisplayedChild(index);
	}

	private String getMonthPast(int pastNum) {

		if (pastNum == 0)
			return "本月";

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);

		month -= pastNum;
		if (month < 0) {
			year--;
			month += 12;
		}

		if (month == 0)
			return String.format(FORMAAT_MONTH, (year % 100), month + 1);
		return String.format("%d月", month + 1);
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.BillHomeQueryFragment;
	}


	private QueryBillApxPageTask startQueryBillApxPageTask(int past) {
		QueryBillApxPageTask task = new QueryBillApxPageTask();
		task.execute(UserInfoControler.getInstance().getUserName(), CommUtil.getMonthPast(past), new TaskListener() {

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				dismissDialog();

				if (result != TaskResult.OK)

					if (task.isBusinessAuthenticationTimeOut())
						mBaseActivity.showReLoginDialog();
					else if (task.getException() != null && task.getException().getMessage() != null) {
						CommUtil.showAlert(getActivity(), null, task.getException().getMessage(), null);
					}
			}

			@Override
			public void onPreExecute(GenericTask task) {
				initDialog(true, false, null);
				showDialog(getActivity().getResources().getString(R.string.tab_query_bill) + mQueryTime);
			}

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				initData((String) param);
				try {// 缓存账单信息
					mCachBillInfo.put(mQueryTime, (String) param);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onCancelled(GenericTask task) {
				dismissDialog();
			}
		});
		return task;
	}

	private GenericTask startQueryCurMonthBillDetailTask() {
		QueryCurMonthBillDetailTask task = new QueryCurMonthBillDetailTask();
		task.execute(UserInfoControler.getInstance().getUserName(), DateUtil.getDateTime("yyyyMM"), new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				if (param != null) {

					initData((String) param);

				}
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
		return task;
	}
}
