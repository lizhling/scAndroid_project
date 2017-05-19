package com.sunrise.marketingassistant.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.adapter.RewardSubjectAdapter;
import com.sunrise.marketingassistant.entity.RewardSubject;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetRewardSubjectTreeTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.view.DefaultDatePickerDialog;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DetailCheckFragment extends BaseFragment implements OnClickListener, TaskListener {

	private Spinner spin_1;
	private Spinner spin_2;
	private Spinner spin_3;

	private RewardSubjectAdapter adapter1 = null;
	private RewardSubjectAdapter adapter2 = null;
	private RewardSubjectAdapter adapter3 = null;

	private List<RewardSubject> spinData1 = new ArrayList<RewardSubject>();
	private List<RewardSubject> spinData2 = new ArrayList<RewardSubject>();
	private List<RewardSubject> spinData3 = new ArrayList<RewardSubject>();

	static int provincePosition = 3;
	private String Imei;
	private int index = 1;

	private TextView date_et;
	private EditText imei_edt;
	private EditText phone_edt;
	private Button check_btn;

	private String MOB_FIRST_CLASS_CODE ="";  //保存一级数据
	private String MOB_SECOND_CLASS_CODE="";   //保存二级数据
	private String MOB_THIRD_CLASS_CODE="";   //保存三级数据

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_detail_check, container, false);
		TelephonyManager tm = (TelephonyManager) mBaseActivity.getSystemService(mBaseActivity.TELEPHONY_SERVICE);
		Imei = tm.getDeviceId();

		date_et = (TextView) view.findViewById(R.id.date_et);
		date_et.setOnClickListener(this);
		
		Calendar ca = Calendar.getInstance();//得到一个Calendar的实例 
		ca.setTime(new Date()); 
		ca.add(Calendar.MONTH, -2); //月份减2
		
		Date lastTwoMonth = ca.getTime(); //结果 
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		date_et.setText(sf.format(lastTwoMonth));
		
		imei_edt = (EditText) view.findViewById(R.id.imei_edt);
		phone_edt = (EditText) view.findViewById(R.id.phone_edt);
		check_btn = (Button) view.findViewById(R.id.check_btn);
		check_btn.setOnClickListener(this);

		setSpinner(view);
		spin_2.setVisibility(View.GONE);
		spin_3.setVisibility(View.GONE);
		
		index = 2;
		// 初始化第一级数据
		initData();
		//startTask("01");
		return view;
	}
	private void initData(){
		spinData1.clear();
		RewardSubject dto0 = new RewardSubject();
		dto0.setCHILDREN("0");
		dto0.setMOB_CLASS_CODE("00");
		dto0.setMOB_CLASS_NAME("请选择");
		
		RewardSubject dto1 = new RewardSubject();
		dto1.setCHILDREN("3");
		dto1.setMOB_CLASS_CODE("01");
		dto1.setMOB_CLASS_NAME("客户发展");
		
		RewardSubject dto2 = new RewardSubject();
		dto2.setCHILDREN("3");
		dto2.setMOB_CLASS_CODE("02");
		dto2.setMOB_CLASS_NAME("终端业务");
		
		RewardSubject dto3 = new RewardSubject();
		dto3.setCHILDREN("3");
		dto3.setMOB_CLASS_CODE("03");
		dto3.setMOB_CLASS_NAME("数信业务");
		
		RewardSubject dto4 = new RewardSubject();
		dto4.setCHILDREN("3");
		dto4.setMOB_CLASS_CODE("04");
		dto4.setMOB_CLASS_NAME("集团业务");
		
		RewardSubject dto5 = new RewardSubject();
		dto5.setCHILDREN("3");
		dto5.setMOB_CLASS_CODE("05");
		dto5.setMOB_CLASS_NAME("代办业务");
		
		RewardSubject dto6 = new RewardSubject();
		dto6.setCHILDREN("3");
		dto6.setMOB_CLASS_CODE("06");
		dto6.setMOB_CLASS_NAME("激励");
		
		spinData1.add(dto0);
		spinData1.add(dto1);
		spinData1.add(dto2);
		spinData1.add(dto3);
		spinData1.add(dto4);
		spinData1.add(dto5);
		spinData1.add(dto6);
		
		adapter1 = new RewardSubjectAdapter(mBaseActivity, spinData1);
		spin_1.setAdapter(adapter1);
		spin_1.setSelection(0, true);
	}

	/*
	 * 设置下拉框
	 */
	private void setSpinner(View view) {
		// TODO Auto-generated method stub
		spin_1 = (Spinner) view.findViewById(R.id.spin_1);
		spin_2 = (Spinner) view.findViewById(R.id.spin_2);
		spin_3 = (Spinner) view.findViewById(R.id.spin_3);

		// 绑定适配器和值
		adapter1 = new RewardSubjectAdapter(mBaseActivity, spinData1);
		spin_1.setAdapter(adapter1);
		spin_1.setSelection(0, true);

		adapter2 = new RewardSubjectAdapter(mBaseActivity, spinData2);
		spin_2.setAdapter(adapter2);
		spin_2.setSelection(0, true);

		adapter3 = new RewardSubjectAdapter(mBaseActivity, spinData3);
		spin_3.setAdapter(adapter3);
		spin_3.setSelection(0, true);

		// 第一个下拉框监听
		spin_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			// 表示选项被改变的时候触发此方法，主要实现办法：动态改变地级适配器的绑定值
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if(position!=0){
					index = 2;
					spin_2.setVisibility(View.GONE);
					spin_3.setVisibility(View.GONE);
					MOB_FIRST_CLASS_CODE = spinData1.get(position).getMOB_CLASS_CODE();
					if (!spinData1.get(position).getCHILDREN().equals("0")) { // 有下一级
						startTask(spinData1.get(position).getMOB_CLASS_CODE());
					} else {
						MOB_SECOND_CLASS_CODE = "";
						MOB_THIRD_CLASS_CODE ="";
					}
				}else{
					MOB_FIRST_CLASS_CODE ="";
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		// 二级下拉监听
		spin_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if(position!=0){
					index = 3;
					spin_3.setVisibility(View.GONE);
					MOB_SECOND_CLASS_CODE = spinData2.get(position).getMOB_CLASS_CODE();
					if (!spinData2.get(position).getCHILDREN().equals("0")) { // 下一级
						startTask(spinData2.get(position).getMOB_CLASS_CODE());
					} else {
						//MOB_CLASS_CODE = spinData2.get(position).getMOB_CLASS_CODE();
					}
				}else{
					MOB_SECOND_CLASS_CODE ="";
				}
				

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		// 三级下拉监听
		spin_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if(position==0){
					MOB_THIRD_CLASS_CODE ="";
				}else{
					MOB_THIRD_CLASS_CODE = spinData3.get(position).getMOB_CLASS_CODE();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private GenericTask mTask;

	private void startTask(String MOB_PARENT_CLASS_CODE) {
		String jsonStr = getData(MOB_PARENT_CLASS_CODE);
		if (mTask != null) {
			mTask.cancle();
		}
		mTask = new GetRewardSubjectTreeTask().execute(jsonStr, this);
	}

	private String getData(String MOB_PARENT_CLASS_CODE) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String curStr = formatter.format(curDate);
		String curStr2 = formatter2.format(curDate);

		//date_et.setText(curStr2.substring(0, 10));

		JSONObject json = new JSONObject();
		try {
			json.put("TOTAL_DATE", curStr.substring(0, 8));
			json.put("OPERATE_NO", getPreferences().getSubAccount());
			json.put("OPERATE_TIME", curStr2);
			json.put("MOB_PARENT_CLASS_CODE", MOB_PARENT_CLASS_CODE);
			json.put("authenticationID", getPreferences().getAuthentication());
			json.put("authentication", getPreferences().getAuthentication());
			json.put("businessID", "123");
			json.put("businessId", "123");
			json.put("appTag", "XDB");
			json.put("loadOvertTime", "undefined");
			json.put("loadStartTime", curStr);
			json.put("session_phone", getPreferences().getMobile()); // getPreferences().getMobile()
			json.put("deviceImei", Imei);
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
		case R.id.date_et:
			new DefaultDatePickerDialog(mBaseActivity).setAssociatedTextView((TextView) v).setDilaogTitle("设置日期信息")
					.show();
			break;
		case R.id.check_btn:
			String phone = phone_edt.getText().toString();
			String imei = imei_edt.getText().toString();
			if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(imei)) {
				Toast.makeText(mBaseActivity, "请输入手机号码或手机串号!", Toast.LENGTH_SHORT).show();
			} else {
				if(!TextUtils.isEmpty(MOB_FIRST_CLASS_CODE)){
					String SERVICE_NO = null;
					String type ="";
					if (TextUtils.isEmpty(phone)) {
						SERVICE_NO = imei;
						type ="M";
					} else {
						SERVICE_NO = phone;
						type ="P";
					}
					String  MOB_CLASS_CODE= "";
					if(TextUtils.isEmpty(MOB_THIRD_CLASS_CODE.trim())){
						MOB_CLASS_CODE = MOB_SECOND_CLASS_CODE;
					}else{
						MOB_CLASS_CODE = MOB_THIRD_CLASS_CODE;
					}
					startActivity(SingleFragmentActivity.createIntent(mBaseActivity, RewardInverseDetailFragment.class,
							date_et.getText().toString() + "&" + SERVICE_NO + "&"+MOB_CLASS_CODE+"&"+MOB_FIRST_CLASS_CODE+"&"+type, null, null, null));
				}else{
					Toast.makeText(mBaseActivity, "请选择一级菜单!", Toast.LENGTH_SHORT).show();
				}
				
			}
			break;
		}

	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param != null) {
			if (task instanceof GetRewardSubjectTreeTask) {
				try {
					JSONObject json = new JSONObject((String) param);
					List<RewardSubject> tempList = new ArrayList<RewardSubject>();
					RewardSubject dto0 = new RewardSubject();
					dto0.setCHILDREN("0");
					dto0.setMOB_CLASS_CODE("00");
					dto0.setMOB_CLASS_NAME("请选择");
					tempList.add(dto0);
					
					if (json.has("rows")) {
						JSONArray info = json.getJSONArray("rows");
						if (info.length() > 0) {
							for (int i = 0; i < info.length(); i++) {
								RewardSubject dto = (RewardSubject) JsonUtils
										.parseJsonStrToObject(info.getJSONObject(i).toString(), RewardSubject.class);
								tempList.add(dto);
							}
						}
					}
					switch (index) {
					case 1:
						spinData1.clear();
						spinData1.addAll(tempList);
						adapter1.notifyDataSetChanged();
						if (spinData1.size() > 1 && spinData1.get(0).getCHILDREN().equals("1")) {
							index = 2;
							startTask(spinData1.get(0).getMOB_CLASS_CODE());
						}
						break;
					case 2:
						spinData2.clear();
						spinData2.addAll(tempList);
						adapter2.notifyDataSetChanged();
						if (spinData2.size() > 1) {
							spin_2.setVisibility(View.VISIBLE);
							MOB_SECOND_CLASS_CODE = "";  //spinData2.get(0).getMOB_CLASS_CODE()
//							if (!spinData2.get(0).getCHILDREN().equals("0")) {
//								index = 3;
//								startTask(spinData2.get(0).getMOB_CLASS_CODE());
//							}
						} else {
							spin_2.setVisibility(View.GONE);
							spin_3.setVisibility(View.GONE);
						}
						break;
					case 3:
						spinData3.clear();
						spinData3.addAll(tempList);
						if (spinData3.size() > 1) {
							MOB_THIRD_CLASS_CODE = ""; //spinData3.get(0).getMOB_CLASS_CODE()
							spin_3.setVisibility(View.VISIBLE);
						} else {
							spin_3.setVisibility(View.GONE);
						}
						adapter3.notifyDataSetChanged();
						break;

					default:
						break;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
