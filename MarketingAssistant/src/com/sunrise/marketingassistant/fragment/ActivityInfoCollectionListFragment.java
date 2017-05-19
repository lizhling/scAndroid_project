package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.adapter.ActivityInfoAdapter;
import com.sunrise.marketingassistant.entity.ActInfo;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetActInfoTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.CommUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityInfoCollectionListFragment extends BaseFragment implements OnClickListener, TaskListener, OnItemClickListener {

	private TextView empty_text;
	private ListView activity_list;
	private ActivityInfoAdapter adapter;
	private ArrayList<ActInfo> data = new ArrayList<ActInfo>();
	private String chlId = null;
	private int type;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = chlId == null ? 0 : 1;
		chlId = getArguments().getString(Intent.EXTRA_DATA_REMOVED);
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_activity_info_list, container, false);

		empty_text = (TextView) view.findViewById(R.id.empty_text);
		activity_list = (ListView) view.findViewById(R.id.listView_compatitor);
		activity_list.setEmptyView(empty_text);
		adapter = new ActivityInfoAdapter(mBaseActivity, data);
		activity_list.setAdapter(adapter);
		activity_list.setOnItemClickListener(this);

		if (chlId != null) {
			if (mTask != null)
				mTask.cancle();
			mTask = new GetActInfoTask().execute(getPreferences().getAuthentication(), chlId, this);
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog(true, false, null);
		if (task instanceof GetActInfoTask) {
			showDialog("获取活动列表信息，请稍候……");
		}
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK) {
			CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), getString(R.string.Return), null);
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param != null)
			try {
				JSONObject localJSONObject = new JSONObject((String) param);
				if (localJSONObject.has("RETURN")) {
					localJSONObject = localJSONObject.getJSONObject("RETURN");
					if (localJSONObject.has("RETURN_CODE")) {
						if (CommUtil.parse2Integer(localJSONObject.getString("RETURN_CODE"), -1) != 0) {
							Toast.makeText(mBaseActivity, localJSONObject.getString("RETURN_MESSAGE"), Toast.LENGTH_SHORT).show();
							return;
						}
						List<ActInfo> localArrayList = new ArrayList<ActInfo>();
						if (localJSONObject.has("RETURN_INFO")) {
							JSONArray array = localJSONObject.getJSONArray("RETURN_INFO");
							if (array.length() > 0) {
								JSONObject info = array.getJSONObject(0);
								if (info.has("DETAIL_INFO")) {
									array = array.getJSONObject(0).getJSONArray("DETAIL_INFO");
									if (array.length() > 0) {
										for (int i = 0; i < array.length(); i++) {
											ActInfo dto = (ActInfo) JsonUtils.parseJsonStrToObject(array.getJSONObject(i).toString(), ActInfo.class);
											localArrayList.add(dto);
										}
									}
								} else {
									for (int i = 0; i < array.length(); i++) {
										ActInfo dto = (ActInfo) JsonUtils.parseJsonStrToObject(array.getJSONObject(i).toString(), ActInfo.class);
										localArrayList.add(dto);
									}
								}

							}

						}
						data.clear();
						data.addAll(localArrayList);
						adapter.notifyDataSetChanged();
					}

				}
			} catch (JSONException localJSONException) {
				localJSONException.printStackTrace();
			}
	}

	@Override
	public void onCancelled(GenericTask task) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}

	}

	private GenericTask mTask;

	@Override
	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ActInfo dto = data.get(arg2);
		ActivityInfoCollectionFragment.newInstance(dto, dto.getChlId());
		startActivity(SingleFragmentActivity.createIntent(mBaseActivity, ActivityInfoCollectionFragment.class, null, null, null, null));
	}
}
