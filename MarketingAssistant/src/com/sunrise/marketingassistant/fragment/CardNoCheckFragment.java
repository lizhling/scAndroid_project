package com.sunrise.marketingassistant.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.adapter.RemunerationAdapter;
import com.sunrise.marketingassistant.entity.Remuneration;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetRewordDetailTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.HardwareUtils;
import com.sunrise.marketingassistant.view.DefaultDatePickerDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class CardNoCheckFragment extends BaseFragment implements OnClickListener, TaskListener {

	private Button check_btn; // 工号查询
	private TextView date_et;
	private ListView remuneration_list;
	private GenericTask mTask;
	private RemunerationAdapter adapter;
	private List<Remuneration> data = new ArrayList<Remuneration>();
	private String groupId = "";

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_card_no_check, container, false);
		if (this.getArguments() != null) {
			// Toast.makeText(mBaseActivity, groupId,
			// Toast.LENGTH_SHORT).show();
			groupId = this.getArguments().getString(Intent.EXTRA_TEXT);
		}

		check_btn = (Button) view.findViewById(R.id.check_btn);
		check_btn.setOnClickListener(this);
		date_et = (TextView) view.findViewById(R.id.date_et);
		date_et.setOnClickListener(this);
		remuneration_list = (ListView) view.findViewById(R.id.remuneration_list);
		remuneration_list.setEmptyView(view.findViewById(R.id.empty_text));

		{// 设置时间
			Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
			ca.setTimeInMillis(System.currentTimeMillis()); // 设置时间为当前时间
			ca.add(Calendar.MONTH, -2); // 月份减2
			date_et.setText(new SimpleDateFormat(ExtraKeyConstant.FORMAT_DATA).format(ca.getTime()));
		}

		startTask();
		return view;
	}

	private void startTask() {
		String jsonStr = getData();
		if (jsonStr != null) {
			if (mTask != null) {
				mTask.cancle();
			}
			mTask = new GetRewordDetailTask().execute(jsonStr, this);
		}
	}

	private String getData() {
		SimpleDateFormat formatter = new SimpleDateFormat(ExtraKeyConstant.FORMAT_PARAM_TIME);
		SimpleDateFormat formatter2 = new SimpleDateFormat(ExtraKeyConstant.FORMAT_DATA);
		String curStr;
		try {
			curStr = formatter.format(formatter2.parse(date_et.getText().toString()));
		} catch (ParseException e1) {
			e1.printStackTrace();
			curStr = date_et.getText().toString().replace("-", "") + "000000";
		}
		// date_et.setText(formatter2.format(lastTwoMonth));

		JSONObject json = new JSONObject();
		try {
			json.put("yearMonth", date_et.getText().toString().replaceAll("-", "").substring(0, 6));
			json.put("OPERATE_NO", getPreferences().getSubAccount());
			json.put("loginNo", getPreferences().getSubAccount());
			json.put("groupId", groupId);
			json.put("rwdCode", "0");
			json.put("authenticationID", getPreferences().getAuthentication());
			json.put("authentication", getPreferences().getAuthentication());
			json.put("businessID", "123");
			json.put("businessId", "123");
			json.put("appTag", "XDB");
			json.put("loadOvertTime", "undefined");
			json.put("loadStartTime", curStr);
			json.put("session_phone", getPreferences().getMobile());
			json.put("deviceImei", HardwareUtils.getPhoneIMEI(mBaseActivity));
			json.put("submitTime", curStr);
			json.put("subAccount", getPreferences().getSubAccount());
			return json.toString();
		} catch (JSONException e) {
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
			startTask();
			break;
		case R.id.date_et:
			new DefaultDatePickerDialog(mBaseActivity).setAssociatedTextView((TextView) v).setDilaogTitle("设置日期信息").show();
			break;
		}

	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param != null) {
			try {
				JSONObject json = new JSONObject((String) param);
				if (json.has("resultStr")) {
					data.clear();
					JSONArray array = json.getJSONArray("resultStr");
					if (array.length() > 0) {
						for (int i = 0; i < array.length(); i++) {
							Remuneration info = JsonUtils.parseJsonStrToObject(array.getJSONObject(i).toString(), Remuneration.class);
							data.add(info);
						}
					}
					// else{
					// SimpleDateFormat formatter = new
					// SimpleDateFormat("yyyyMMddHHmmss");
					// Date curDate = new Date(System.currentTimeMillis());//
					// 获取当前时间
					// String curStr =formatter.format(curDate);
					// for(int i=0;i<5;i++){
					// Remuneration temp = new Remuneration();
					// temp.setRWD_CODE(i+"");
					// temp.setRWD_NAME("酬金"+i);
					// temp.setPAYMENT_AMOUNT(curStr);
					// data.add(temp);
					// }
					// }
					adapter = new RemunerationAdapter(mBaseActivity, data);
					remuneration_list.setAdapter(adapter);
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
}