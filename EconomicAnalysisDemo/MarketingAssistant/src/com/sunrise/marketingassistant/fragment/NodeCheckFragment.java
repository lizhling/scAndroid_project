package com.sunrise.marketingassistant.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.adapter.ChannelAdapter;
import com.sunrise.marketingassistant.entity.ChannelInfo;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetChannelListTask;
import com.sunrise.marketingassistant.task.GetChannelTreeTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.HardwareUtils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class NodeCheckFragment extends BaseFragment implements OnClickListener, TaskListener, OnItemClickListener {

	private Button check_btn; // 工号查询
	private EditText channel_name;
	private TextView nav_text;
	private String nav = "";
	private ListView tree_list;

	private GenericTask mTask;

	// private List<FileBean> mDatas2 = new ArrayList<FileBean>();
	protected ArrayList<ChannelInfo> channelDataList = new ArrayList<ChannelInfo>();
	private ChannelAdapter mAdapter;

	private Stack<ArrayList<ChannelInfo>> mHistoryListChannel;

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_node_check, container, false);

		check_btn = (Button) view.findViewById(R.id.check_btn);
		nav_text = (TextView) view.findViewById(R.id.nav_text);
		check_btn.setOnClickListener(this);
		channel_name = (EditText) view.findViewById(R.id.channel_name);
		tree_list = (ListView) view.findViewById(R.id.tree_list);
		tree_list.setEmptyView(view.findViewById(R.id.textView_noContent));
		mAdapter = new ChannelAdapter(mBaseActivity, channelDataList);
		tree_list.setAdapter(mAdapter);
		tree_list.setOnItemClickListener(this);

		startTask("2");
		return view;
	}

	private void startTask(String groupId) {
		// mDatas2 = new ArrayList<FileBean>();
		// channelDataList.clear();
		String jsonStr = getData(groupId);
		if (jsonStr != null) {
			if (mTask != null) {
				mTask.cancle();
			}
			mTask = new GetChannelTreeTask().execute(jsonStr, this);
		}
	}

	private String getData(String groupId) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String curStr = formatter.format(curDate);

		JSONObject json = new JSONObject();
		try {
			json.put("OPERATE_NO", getPreferences().getSubAccount());
			json.put("LOGIN_NO", getPreferences().getSubAccount()); // getPreferences().getSubAccount()
			json.put("START_NUM", "0");
			json.put("END_NUM", "100");
			json.put("REGION_GROUP", groupId);
			json.put("authenticationID", getPreferences().getAuthentication());
			json.put("authentication", getPreferences().getAuthentication());
			json.put("businessID", "123");
			json.put("businessId", "123");
			json.put("appTag", "XDB");
			json.put("loadOvertTime", "undefined");
			json.put("loadStartTime", curStr);
			json.put("session_phone", getPreferences().getMobile()); // getPreferences().getMobile()
			json.put("deviceImei", HardwareUtils.getPhoneIMEI(mBaseActivity));
			json.put("submitTime", curStr);
			json.put("subAccount", getPreferences().getSubAccount());
			return json.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.check_btn:
			if (!TextUtils.isEmpty(channel_name.getText().toString())) {
				// 模糊查询
				// channelDataList.clear();
				if (mTask != null) {
					mTask.cancle();
				}
				mTask = new GetChannelListTask().execute(getData2(channel_name.getText().toString().trim()), this);
			} else {
				Toast.makeText(mBaseActivity, "请输入网点数据!", Toast.LENGTH_SHORT).show();
			}
			break;
		}

	}

	private String getData2(String groupName) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String curStr = formatter.format(curDate);

		JSONObject json = new JSONObject();
		try {
			json.put("groupName", groupName);
			json.put("PhoneNo", getPreferences().getMobile());
			json.put("authenticationID", getPreferences().getAuthentication());
			json.put("authentication", getPreferences().getAuthentication());
			json.put("businessID", "123");
			json.put("businessId", "123");
			json.put("appTag", "XDB");
			json.put("loadOvertTime", "undefined");
			json.put("loadStartTime", curStr);
			json.put("session_phone", getPreferences().getMobile()); // getPreferences().getMobile()
			json.put("deviceImei", HardwareUtils.getPhoneIMEI(mBaseActivity));
			json.put("submitTime", curStr);
			json.put("subAccount", getPreferences().getSubAccount());
			return json.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param != null) {
			if (task instanceof GetChannelTreeTask) {
				ArrayList<ChannelInfo> array = (ArrayList<ChannelInfo>) param;
				addNewList(channelDataList);

				channelDataList.clear();
				channelDataList.addAll(array);
				mAdapter.notifyDataSetChanged();

			} else if (task instanceof GetChannelListTask) {
				ArrayList<ChannelInfo> array = (ArrayList<ChannelInfo>) param;
				addNewList(channelDataList);

				channelDataList.clear();
				channelDataList.addAll(array);
				mAdapter.notifyDataSetChanged();
			}

		}
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog();
		showDialog("获取数据...");
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK)
			Toast.makeText(mBaseActivity, getString(R.string.latest_version), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCancelled(GenericTask task) {

	}

	@Override
	public String getName() {
		return null;
	}

	/**
	 * 将旧列表存入堆栈
	 * 
	 * @param newList
	 */
	private void addNewList(ArrayList<ChannelInfo> previousList) {

		if (mHistoryListChannel == null)
			mHistoryListChannel = new Stack<ArrayList<ChannelInfo>>();

		if (previousList == null || previousList.isEmpty())
			return;

		ArrayList<ChannelInfo> array = new ArrayList<ChannelInfo>();
		array.addAll(previousList);
		mHistoryListChannel.push(array);
	}

	private ArrayList<ChannelInfo> getPreviousList() {
		if (mHistoryListChannel == null || mHistoryListChannel.isEmpty())
			return null;

		return mHistoryListChannel.pop();

	}

	public boolean onBackPressed() {

		ArrayList<ChannelInfo> array = getPreviousList();
		if (array != null) {// 返回上一级菜单
			channelDataList.clear();
			channelDataList.addAll(array);
			mAdapter.notifyDataSetChanged();
			return true;
		}

		return super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (channelDataList.get(arg2).getHAS_CHILD().equals("1")) { // 是叶子
			// Toast.makeText(mBaseActivity,
			// channelDataList.get(arg2).getGROUP_NAME(),
			// Toast.LENGTH_SHORT).show();
			goNextPage(arg2);
		} else {
			if (TextUtils.isEmpty(nav)) {
				nav += channelDataList.get(arg2).getGROUP_NAME();
			} else {
				nav += "<<" + channelDataList.get(arg2).getGROUP_NAME();
			}
			nav_text.setText(nav);
			startTask(channelDataList.get(arg2).getGROUP_ID());
		}
	}

	protected void goNextPage(int index) {
		startActivity(SingleFragmentActivity.createIntent(mBaseActivity, CardNoCheckFragment.class, channelDataList.get(index).getGROUP_ID(), null, null, null));
	}
}
