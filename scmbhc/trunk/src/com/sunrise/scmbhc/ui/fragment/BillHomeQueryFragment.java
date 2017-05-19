package com.sunrise.scmbhc.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.QueryBillHome;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetBillHomeQueryTask;
import com.sunrise.scmbhc.task.QueryBillApxPageTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.task.TopupHistoryTask;
import com.sunrise.scmbhc.utils.CommUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 我的帐单主页
 * 
 * @author fuheng
 * 
 */
public class BillHomeQueryFragment extends BaseFragment implements OnSeekBarChangeListener {

	private static final String[] from = { QueryBillHome.KEY_NAME, QueryBillHome.KEY_VALUE };

	private static final int[] to = { R.id.text1, R.id.text2 };

	private static final String FORMAAT_MONTH = "%2d/%d";

	private final String KEY_CACHE = "cache";

	private static final int[] RES_ID_MONTHS = { R.id.month_1, R.id.month_2, R.id.month_3, R.id.month_4, R.id.month_5, R.id.month_6 };

	private ListView mListViewBillDetail;
	private TextView mBillTotle;

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

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bill_query, container, false);

		mListViewBillDetail = (ListView) view.findViewById(R.id.listView_billDetail);

		mBillTotle = (TextView) view.findViewById(R.id.billTotle);

		mSeekBarMonth = (SeekBar) view.findViewById(R.id.seekBar_month);
		mSeekBarMonth.setMax(RES_ID_MONTHS.length << 1);
		mSeekBarMonth.setProgress(mSeekBarMonth.getMax() - 1);
		mSeekBarMonth.setOnSeekBarChangeListener(this);

		for (int i = 0; i < 6; ++i) {
			TextView month = (TextView) view.findViewById(RES_ID_MONTHS[i]);
			month.setText(getMonthPast(mSeekBarMonth.getMax() / 2 - i));
			month.setTag(i);
			month.setOnClickListener(mListenerMonth);
		}

		initData(new QueryBillHome());

		if (savedInstanceState == null)
			mCachBillInfo = new JSONObject();
		else
			try {
				mCachBillInfo = new JSONObject(savedInstanceState.getString(KEY_CACHE));
			} catch (JSONException e) {
				e.printStackTrace();
				mCachBillInfo = new JSONObject();
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

		if (mCachBillInfo.has(mQueryTime)) {
			try {
				initData(mCachBillInfo.getString(mQueryTime));
			} catch (JSONException e) {
				e.printStackTrace();
				initData(new QueryBillHome());
			}
			return;
		}

		GetBillHomeQueryTask task = new GetBillHomeQueryTask();
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
		mTask = task;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!mBaseActivity.checkLoginIn(null)) {
			return;
		}
		// 成功后初始化数据
		mBaseActivity.setTitle(getResources().getString(R.string.myBill));
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		startLoad((mSeekBarMonth.getMax() / 2 - 1) - (mSeekBarMonth.getProgress() - 1) / 2 + 1);

		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			TopupHistoryTask task = new TopupHistoryTask();
			long time = System.currentTimeMillis();
			task.execute(UserInfoControler.getInstance().getUserName(), CommUtil.getMonthPast(3) + "01", sdf.format(new Date(time)), null);
		}
		{
			QueryBillApxPageTask task = new QueryBillApxPageTask();
			task.execute(UserInfoControler.getInstance().getUserName(), new SimpleDateFormat("yyyyMM").format(new Date(System.currentTimeMillis())), null);
		}
	}

	public void onStart() {
		super.onStart();
		/*
		 * if (!mBaseActivity.checkLoginIn(null)) return;
		 * 
		 * mBaseActivity.setTitle(getResources().getString(R.string.myBill));
		 * mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		 * startLoad(mSeekBarMonth.getMax() - mSeekBarMonth.getProgress() + 1);
		 */
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

	private void initData(QueryBillHome queryBillHome) {
		// 详情
		mBillTotle.setText(queryBillHome.getTotleCost());// 自我统计消费
		// mBillTotle.setText(queryBillHome.getPCAS_07_07());
		List<? extends Map<String, ?>> data = queryBillHome.getDetailOfBill(mBaseActivity);
		SimpleAdapter adapter = new SimpleAdapter(mBaseActivity, data, R.layout.item_bill_query_list, from, to);
		mListViewBillDetail.setAdapter(adapter);
		CommUtil.expandListView(adapter, mListViewBillDetail);
	}

	private void initData(String str) {
		initData(JsonUtils.parseJsonStrToObject(str, QueryBillHome.class));
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
			startLoad((seekBar.getMax() / 2 - 1) - (seekBar.getProgress() - 1) / 2 + 1);
			mCurIndex = seekBar.getProgress();
		}
	}

	private String getMonthPast(int pastNum) {
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

}
