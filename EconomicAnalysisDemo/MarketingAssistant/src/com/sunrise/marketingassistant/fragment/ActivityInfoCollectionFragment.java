package com.sunrise.marketingassistant.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.impl.client.DefaultRequestDirector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.ActInfo;
import com.sunrise.marketingassistant.task.DelActInfoTask;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetActInfoTask;
import com.sunrise.marketingassistant.task.SaveOrUpdateActivityInfoTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.view.DefaultDatePickerDialog;
import com.sunrise.marketingassistant.view.DefaultTimePickerDialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 渠道信息采集
 */
public class ActivityInfoCollectionFragment extends BaseFragment implements TaskListener, OnClickListener {

	private TextView date_et;

	private EditText activity_name_et; // 活动名称
	private EditText activity_desc_et; // 活动详情
	private EditText activity_levl_et; // 活动等级
	private TextView code_text; // 归属竞争

	private EditText bus_area_et; // 归属商圈
	private EditText activity_type_et; // 活动类型
	private EditText activity_money_et; // 活动补贴金额

	private TextView start_date_text;
	private TextView mTvStarTime;

	private TextView end_date_text;
	private TextView mTvEndTime;

	private Button delete_btn; // 删除网点
	private Button clear_btn; // 清除数据
	private Button save_btn; // 保存

	private static String chlId = null;
	private GenericTask mTask;
	
	private static ActInfo dto =null;
	private static ActivityInfoCollectionFragment instance;
	
	public static ActivityInfoCollectionFragment newInstance(ActInfo dto,String chlId){
		instance =new ActivityInfoCollectionFragment();
		ActivityInfoCollectionFragment.chlId = chlId;
		ActivityInfoCollectionFragment.dto = dto;
		return instance;
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_activity_info_collection, null, false);
		init(view);

		if (dto != null) {
			type =1;
			chlId = dto.getChlId();
			actId = dto.getActId();
			initData();
		} else {
			type = 0;
			delete_btn.setClickable(false);
		}
		return view;
	}
	private void initData(){
		delete_btn.setClickable(true);

		actId = dto.getActId();
		if(!TextUtils.isEmpty(dto.getOpTime())){
			if(dto.getOpTime().length()>=8){
				date_et.setText(dto.getOpTime().substring(0,4)+"-"+dto.getOpTime().substring(4,6)+"-"+
						dto.getOpTime().substring(6));
			}else{
				date_et.setText(dto.getOpTime());
			}
		}else{
			date_et.setText(dto.getOpTime());
		}
		
		activity_name_et.setText(dto.getActionName());
		activity_desc_et.setText(dto.getActionDesc());
		activity_levl_et.setText(dto.getActionLvl() + "");
		code_text.setText(dto.getChlId());
		bus_area_et.setText(dto.getBusarea());
		activity_type_et.setText(dto.getActionType());
		activity_money_et.setText(dto.getSubsidyFee() + "");

		String effDate = dto.getEffDate(); // 开始时间
		if (!TextUtils.isEmpty(effDate)) {
			start_date_text.setText(effDate.split(" ")[0]);
			String time = effDate.split(" ")[1];
			mTvStarTime.setText(time.split(":")[0] + "点" + time.split(":")[1] + "分");
		}
		String expDate = dto.getExpDate(); // 结束时间
		if (!TextUtils.isEmpty(expDate)) {
			end_date_text.setText(expDate.split(" ")[0]);
			String time = expDate.split(" ")[1];
			mTvEndTime.setText(time.split(":")[0] + "点" + time.split(":")[1] + "分");
		}
	}

	private void init(View view) {
		code_text = (TextView) view.findViewById(R.id.code_text);
		activity_name_et = (EditText) view.findViewById(R.id.activity_name_et);
		activity_desc_et = (EditText) view.findViewById(R.id.activity_desc_et);
		activity_levl_et = (EditText) view.findViewById(R.id.activity_levl_et);
		bus_area_et = (EditText) view.findViewById(R.id.bus_area_et);
		activity_type_et = (EditText) view.findViewById(R.id.activity_type_et);
		activity_money_et = (EditText) view.findViewById(R.id.activity_money_et);

		date_et = (TextView) view.findViewById(R.id.date_et);
		date_et.setOnClickListener(this);

		start_date_text = (TextView) view.findViewById(R.id.start_date_text);
		start_date_text.setOnClickListener(this);

		end_date_text = (TextView) view.findViewById(R.id.end_date_text);
		end_date_text.setOnClickListener(this);

		mTvStarTime = (TextView) view.findViewById(R.id.start_time_text);
		mTvStarTime.setOnClickListener(this);
		mTvEndTime = (TextView) view.findViewById(R.id.end_time_text);
		mTvEndTime.setOnClickListener(this);

		delete_btn = (Button) view.findViewById(R.id.delete_btn);
		delete_btn.setOnClickListener(this);
		clear_btn = (Button) view.findViewById(R.id.clear_btn);
		clear_btn.setOnClickListener(this);
		save_btn = (Button) view.findViewById(R.id.save_btn);
		save_btn.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mBaseActivity.setTitle("渠道网点信息");
		mBaseActivity.setTitleBarLeftClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBaseActivity.finish();
			}
		});
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog(true, false, null);
		if (task instanceof GetActInfoTask) {
			showDialog("获取基本信息，请稍候……");
		} else if (task instanceof SaveOrUpdateActivityInfoTask) {
			showDialog("正在保存信息，请稍候……");
		} else if (task instanceof DelActInfoTask) {
			showDialog("正在删除信息，请稍候……");
		}
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK) {
			CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), getString(R.string.Return), null);
		}
	}

	private int type; // 0:保存 1:修改

	private void saveData() {
		if (TextUtils.isEmpty(activity_name_et.getText().toString())) {
			Toast.makeText(mBaseActivity, "活动名称不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}

		JSONObject json = new JSONObject();
		int money = 0;
		if (!TextUtils.isEmpty(activity_money_et.getText().toString())) {
			money = Integer.parseInt(activity_money_et.getText().toString());
		}
		String optTime ="";
		if(!TextUtils.isEmpty(date_et.getText().toString())){
			optTime = date_et.getText().toString().replaceAll("-","");
		}
		String effDate = "";
		String start_time = mTvStarTime.getText().toString();
		if (!TextUtils.isEmpty(start_time)) {
			effDate = start_date_text.getText().toString() + " " + start_time.replace("点", ":").replace("分", ":") + "00";
		} else {
			Toast.makeText(mBaseActivity, "请选择开始时间!", Toast.LENGTH_SHORT).show();
			return;
		}
		String expDate = "";
		String end_time = mTvEndTime.getText().toString();
		if (!TextUtils.isEmpty(end_time)) {
			expDate = end_date_text.getText().toString() + " " + end_time.replace("点", ":").replace("分", ":") + "00";
		} else {
			Toast.makeText(mBaseActivity, "请选择结束时间!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (expDate.compareTo(effDate) <= 0) {
			Toast.makeText(mBaseActivity, "结束时间不能小于开始时间!", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			json.put("authenticationID", getPreferences().getAuthentication());
			if (type == 0) {
				json.put("actId", "");
				json.put("chlId", chlId);
			} else {
				json.put("actId", actId);
				json.put("chlId", chlId);
			}

			json.put("opTime", optTime);
			json.put("actionName", activity_name_et.getText().toString());
			json.put("actionDesc", activity_desc_et.getText().toString());
			json.put("actionLvl", activity_levl_et.getText().toString());
			json.put("busarea", bus_area_et.getText().toString());
			json.put("effDate", effDate);
			json.put("expDate", expDate);
			json.put("actionType", activity_type_et.getText().toString());
			json.put("subsidyFee", activity_money_et.getText().toString());
			json.put("login_no", getPreferences().getSubAccount());

			if (mTask != null) {
				mTask.cancle();
			}
			mTask = new SaveOrUpdateActivityInfoTask().execute(json.toString(), this);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void clearData() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		date_et.setText(str);
		activity_name_et.setText("");
		activity_desc_et.setText("");
		activity_levl_et.setText("");
		bus_area_et.setText("");
		activity_type_et.setText("");
		activity_money_et.setText("");
		start_date_text.setText(str);
		mTvStarTime.setText("01点00分");
		end_date_text.setText(str);
		mTvEndTime.setText("23点59分");
	}

	private int actId = -999;

	private void initData(String param) {
		JSONObject jsonObject = null;
		if (param != null)
			try {
				jsonObject = new JSONObject((String) param);
				if (jsonObject.has("RETURN")) {
					jsonObject = jsonObject.getJSONObject("RETURN");
					if (jsonObject.has("RETURN_CODE")) {
						if (CommUtil.parse2Integer(jsonObject.getString("RETURN_CODE"), -1) != 0) {
							Toast.makeText(mBaseActivity, jsonObject.getString("RETURN_MESSAGE"), Toast.LENGTH_SHORT).show();
							return;
						}
						if (jsonObject.has("RETURN_INFO")) {
							JSONArray array = jsonObject.getJSONArray("RETURN_INFO");
							if (array.length() > 0) {
								delete_btn.setClickable(true);
								ActInfo info = JsonUtils.parseJsonStrToObject(array.getJSONObject(0).toString(), ActInfo.class);

								actId = info.getActId();
								if(!TextUtils.isEmpty(info.getOpTime())){
									if(info.getOpTime().length()>=8){
										date_et.setText(info.getOpTime().substring(0,4)+"-"+info.getOpTime().substring(4,6)+"-"+
												info.getOpTime().substring(6));
									}else{
										date_et.setText(info.getOpTime());
									}
								}else{
									date_et.setText(info.getOpTime());
								}
								
								activity_name_et.setText(info.getActionName());
								activity_desc_et.setText(info.getActionDesc());
								activity_levl_et.setText(info.getActionLvl() + "");
								code_text.setText(info.getChlId());
								bus_area_et.setText(info.getBusarea());
								activity_type_et.setText(info.getActionType());
								activity_money_et.setText(info.getSubsidyFee() + "");

								String effDate = info.getEffDate(); // 开始时间
								if (!TextUtils.isEmpty(effDate)) {
									start_date_text.setText(effDate.split(" ")[0]);
									String time = effDate.split(" ")[1];
									mTvStarTime.setText(time.split(":")[0] + "点" + time.split(":")[1] + "分");
								}
								String expDate = info.getExpDate(); // 结束时间
								if (!TextUtils.isEmpty(expDate)) {
									end_date_text.setText(expDate.split(" ")[0]);
									String time = expDate.split(" ")[1];
									mTvEndTime.setText(time.split(":")[0] + "点" + time.split(":")[1] + "分");
								}
							}

						} else {
							delete_btn.setClickable(false);
						}
					}

				}
			} catch (JSONException localJSONException) {
				localJSONException.printStackTrace();
			}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// TODO Auto-generated method stub
		if (param == null)
			return;
		if (task instanceof GetActInfoTask) {
			initData((String) param);
		} else if (task instanceof SaveOrUpdateActivityInfoTask) {
			JSONObject jsonObject = null;
			if (param != null)
				try {
					jsonObject = new JSONObject((String) param);
					if (jsonObject.has("RETURN")) {
						jsonObject = jsonObject.getJSONObject("RETURN");
						if (jsonObject.has("RETURN_MESSAGE")) {
							Toast.makeText(mBaseActivity, jsonObject.getString("RETURN_MESSAGE"), Toast.LENGTH_SHORT).show();
							mBaseActivity.finish();
						}
					}
				} catch (JSONException localJSONException) {
					localJSONException.printStackTrace();
				}
		} else if (task instanceof DelActInfoTask) {
			JSONObject jsonObject = null;
			if (param != null)
				try {
					jsonObject = new JSONObject((String) param);
					if (jsonObject.has("RETURN")) {
						jsonObject = jsonObject.getJSONObject("RETURN");
						if (jsonObject.has("RETURN_MESSAGE")) {
							Toast.makeText(mBaseActivity, jsonObject.getString("RETURN_MESSAGE"), Toast.LENGTH_SHORT).show();
							mBaseActivity.finish();
						}
					}
				} catch (JSONException localJSONException) {
					localJSONException.printStackTrace();
				}
		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mTask != null) {
			mTask.cancle();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.date_et:
		case R.id.start_date_text:
		case R.id.end_date_text:
			new DefaultDatePickerDialog(mBaseActivity).setAssociatedTextView((TextView) v).setDilaogTitle("设置日期信息").show();
			break;
		case R.id.start_time_text:
			new DefaultTimePickerDialog(mBaseActivity).setDilaogTitle("请选择开始时间").setAssociatedTextView((TextView) v).show();
			break;
		case R.id.end_time_text:
			new DefaultTimePickerDialog(mBaseActivity).setDilaogTitle("请选择结束时间").setAssociatedTextView((TextView) v).show();
			break;
		case R.id.clear_btn:
			clearData();
			break;
		case R.id.save_btn:
			saveData();
			break;
		case R.id.delete_btn:
			deleteData();
			break;
		default:
			break;
		}
	}

	private void deleteData() {
		if (mTask != null) {
			mTask.cancle();
		}
		mTask = new DelActInfoTask().execute(getPreferences().getAuthentication(), getPreferences().getSubAccount(), actId + "", this);
	}

}
