package com.sunrise.scmbhc.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.TopupHistoryItem;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.task.TopupHistoryTask;
import com.sunrise.scmbhc.utils.CommUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * 充值历史记录页面
 * 
 * @author 珩
 * @version 2014年10月14日17:07:05
 */
public class TopUpHistoryFragment extends BaseFragment implements TaskListener {

	private TableLayout mListViewHistory;

	private final int[] ARRAY_RESIDS = { R.id.tag1, R.id.tag2, R.id.tag3 };

	private GenericTask mTask;

	private String mData;

	private TextView mTextView;

	private AnimationDrawable ANIM_DRAWABLE;

	private ViewSwitcher mViewSwitcher;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isAdded()) {
			ANIM_DRAWABLE = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_loadingbar);
		}
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_top_up_history, container, false);

		mTextView = (TextView) view.findViewById(R.id.orderedBusinessesNoContent);

		mListViewHistory = (TableLayout) view.findViewById(R.id.listView_histroy);

		mViewSwitcher = (ViewSwitcher) view;

		if (savedInstanceState != null)
			mData = savedInstanceState.getString(ExtraKeyConstant.KEY_TOP_UP_AMOUNT);
		else
			mData = null;

		return view;
	}

	public void onSaveInstence(Bundle outState) {
		outState.putString(ExtraKeyConstant.KEY_TOP_UP_AMOUNT, mData);
		super.onSaveInstanceState(outState);
	}

	public void onStart() {
		super.onStart();

		if (!mBaseActivity.checkLoginIn(null)) {
			return;
		}

		mBaseActivity.setTitle(getResources().getString(R.string.topupHistory));
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);

		if (mData == null) {
			mViewSwitcher.setDisplayedChild(1);
			startLoadingTask();
		} else {
			initListData(mData);
		}
	}

	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	private void initListData(String mData2) {

		ArrayList<HashMap<String, String>> result = null;

		try {
			JSONArray jsarray = new JSONArray(mData2);
			result = new ArrayList<HashMap<String, String>>();
			ArrayList<TopupHistoryItem> tempArr = new ArrayList<TopupHistoryItem>();

			for (int i = 0; i < jsarray.length(); ++i) {
				TopupHistoryItem item = JsonUtils.parseJsonStrToObject(jsarray.getJSONObject(i).toString(), TopupHistoryItem.class);

				if (tempArr.isEmpty())
					tempArr.add(item);
				else {
					int j = 0;
					for (; j < tempArr.size(); ++j) {
						if (tempArr.get(j).getOP_TIME_long() < item.getOP_TIME_long()) {
							break;
						}
					}
					tempArr.add(j, item);
					j = 0;
				}
			}

			for (int i = 0; i < tempArr.size(); ++i) {
				result.add(tempArr.get(i).getHashMap());
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		if (result == null || result.isEmpty())
			mListViewHistory.removeViewsInLayout(1, mListViewHistory.getChildCount() - 1);
		else
			insertTableRow(result, TopupHistoryItem.FROM, ARRAY_RESIDS);
	}

	private void insertTableRow(ArrayList<HashMap<String, String>> data, String[] from, int[] arr_resids) {
		for (HashMap<String, String> hash : data) {

			View line = new View(mBaseActivity);
			line.setBackgroundColor(getResources().getColor(R.color.bg_color_gray));
			mListViewHistory.addView(line, LayoutParams.MATCH_PARENT, 1);

			View view = LayoutInflater.from(mBaseActivity).inflate(R.layout.item_top_up_history, null);
			for (int i = 0; i < arr_resids.length; ++i) {
				TextView text = (TextView) view.findViewById(arr_resids[i]);
				text.setText(Html.fromHtml(hash.get(from[i])));
			}

			mListViewHistory.addView(view);
		}
	}

	private void startLoadingTask() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		TopupHistoryTask task = new TopupHistoryTask();
		long time = System.currentTimeMillis();
		task.execute(UserInfoControler.getInstance().getUserName(), CommUtil.getMonthPast(6) + "01", sdf.format(new Date(time)), this);

		mTask = task;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		if (ANIM_DRAWABLE != null) {
			mTextView.setCompoundDrawablesWithIntrinsicBounds(ANIM_DRAWABLE, null, null, null);
			ANIM_DRAWABLE.start();
		}
		mTextView.setText(R.string.loading_status);
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		if (ANIM_DRAWABLE != null) {
			ANIM_DRAWABLE.stop();
			mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		mTextView.setText(R.string.noCotent);

		if (result != TaskResult.OK) {
			if (task.getException() != null && task.getException().getMessage() != null) {
				if (isAdded()) {
					CommUtil.showAlert(getActivity(), getResources().getString(R.string.warmNotice), task.getException().getMessage(), null);
				}
			}

			mViewSwitcher.setDisplayedChild(1);
		}

	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {

		if (param == null) {
			mViewSwitcher.setDisplayedChild(1);
			return;
		}
		mViewSwitcher.setDisplayedChild(0);
		initListData((String) param);
	}

	@Override
	public void onCancelled(GenericTask task) {
		if (ANIM_DRAWABLE != null) {
			ANIM_DRAWABLE.stop();
			mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		mTextView.setText(R.string.noCotent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	@Override
	int getClassNameTitleId() {
		return R.string.TopUpHistoryFragment;
	}

}
