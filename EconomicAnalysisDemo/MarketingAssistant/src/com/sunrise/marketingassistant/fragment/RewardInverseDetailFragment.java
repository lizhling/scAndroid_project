package com.sunrise.marketingassistant.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.adapter.RewardInverseDetailAdapter;
import com.sunrise.marketingassistant.entity.RewardInverseDetail;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.RewardInverseDetailTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RewardInverseDetailFragment extends BaseFragment implements OnClickListener, TaskListener,OnRefreshListener<ListView> {
	private PullToRefreshListView detail_list;
	private TextView empty_text;
	private RewardInverseDetailAdapter adapter;
	private List<RewardInverseDetail> data = new ArrayList<RewardInverseDetail>();
	private GenericTask mTask;

	
	private String Imei;
	private String parm;
	private int page =1;
	private int pageSize =10;

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_reward_inverse_detail, container, false);
		TelephonyManager tm = (TelephonyManager) mBaseActivity.getSystemService(mBaseActivity.TELEPHONY_SERVICE);
		Imei = tm.getDeviceId();
		
		if(this.getArguments()!=null){
			parm = this.getArguments().getString(Intent.EXTRA_TEXT);
			//Toast.makeText(mBaseActivity, parm, Toast.LENGTH_SHORT).show();
		}

		data.clear();
		detail_list = (PullToRefreshListView) view.findViewById(R.id.detail_list);
		empty_text =(TextView)view.findViewById(R.id.empty_text);
		detail_list.setEmptyView(empty_text);
		detail_list.setMode(Mode.BOTH);
		detail_list.setOnRefreshListener(this);
		adapter = new RewardInverseDetailAdapter(mBaseActivity, data);
		detail_list.setAdapter(adapter);
		startTask();
		return view;
	}

	private void startTask() {
		int start = isRefresh ? 0 : data.size();
		String jsonStr = getData(start,start+pageSize);
		if (jsonStr != null) {
			if (mTask != null) {
				mTask.cancle();
			}
			mTask = new RewardInverseDetailTask().execute(jsonStr, this);
		}
	}

	private String getData(int start,int end) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String curStr = formatter.format(curDate);
		String curStr2 = formatter2.format(curDate);
		
		String YEAR_MONTH = "";
		String MOB_FIRST_CLASS_CODE ="";
		String SERVICE_NO ="";
		String MOB_CLASS_CODE ="";
		String type ="";
		if(parm!=null){
			YEAR_MONTH = parm.split("&")[0].replaceAll("-","").substring(0,6);
			SERVICE_NO = parm.split("&")[1];
			MOB_CLASS_CODE = parm.split("&")[2];
			MOB_FIRST_CLASS_CODE = parm.split("&")[3];
			type = parm.split("&")[4];
			
		}

		JSONObject json = new JSONObject();
		try {
			json.put("TOTAL_DATE", curStr.substring(0, 8));
			json.put("FUNCTION_CODE", "Sa09");
			json.put("SYS_FLAG", "20");
			json.put("OPERATE_NO", getPreferences().getSubAccount());
			json.put("OPERATE_TIME", curStr2);
			json.put("OPERATE_NOTE", "酬金明细");
			json.put("rewardQryType", "66");
			json.put("MOB_FIRST_CLASS_CODE", MOB_FIRST_CLASS_CODE);
			json.put("MOB_CLASS_CODE", MOB_CLASS_CODE);
			json.put("GROUP_ID", getPreferences().getGroupId());
			json.put("YEAR_MONTH", YEAR_MONTH);
			json.put("LOGIN_NO", getPreferences().getSubAccount());
			
			json.put("SERVICE_NO", SERVICE_NO);
			json.put("SERVICE_TYPE", type);
			json.put("START_NUM", start+"");
			json.put("END_NUM", end+"");
			json.put("authenticationID", getPreferences().getAuthentication());
			json.put("authentication", getPreferences().getAuthentication());
			json.put("businessID", "123");
			json.put("businessId", "123");
			json.put("appTag", "XDB");
			json.put("loadOvertTime", "undefined");
			json.put("loadStartTime", curStr);
			json.put("session_phone", getPreferences().getMobile()); 
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
		}

	}
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (detail_list.isRefreshing()) {
			detail_list.onRefreshComplete();
		}
		if (param != null) {
//			    param = "{\"retCode\":\"0\",\"retMsg\":\"操作完成\",\"detailMsg\":\"操作完成\",\"rowNmu\":\"2\",\"rows\":[{\"REGION_CODE\":\"11\",\"MOB_RWD_CODE\":\"0105\",\"MOB_CLASS_NAME\":\"客户发展>按质支付-2G号段\",\"GROUP_ID\":\"127998\",\"GROUP_NAME\":\"金堂超力淮白路手机连锁店\",\"SERVICE_NO\":\"13678064761\",\"SERVICE_TYPE\":\"P\",\"BLNC_ID\":\"1000007459\",\"BLNC_NAME\":\"用户发展_非全球通_2013_按质支付酬金\",\"BLNC_BASE_FEE\":10.85,\"RWD_FLAG\":\"Y\",\"RWD_SUC_FEE\":1,\"RWD_FAIL_ID\":null,\"RWD_FAIL_CONTENT\":null,\"RN\":1}"
//			    		+ "],\"serialNumber\":\"201512261645689165\"}";
				try {
					JSONObject json = new JSONObject((String) param);
					if(json.has("retCode")){
							if (json.has("rows")) {
								JSONArray info = json.getJSONArray("rows");
								List<RewardInverseDetail> tempList =new ArrayList<RewardInverseDetail>();
								if (info.length() > 0) {
									for (int i = 0; i < info.length(); i++) {
										RewardInverseDetail dto = (RewardInverseDetail) JsonUtils
												.parseJsonStrToObject(info.getJSONObject(i).toString(), RewardInverseDetail.class);
										tempList.add(dto);
									}
								}
								if (isRefresh){
									data.clear();
								}
								data.addAll(tempList);
								adapter.notifyDataSetChanged();
						   }
						
					}else{
						Toast.makeText(mBaseActivity, json.getString("RETMSG"), Toast.LENGTH_SHORT).show();
					}
						
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
	/** 是否刷新 */
	private boolean isRefresh;
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.isHeaderShown()) {
			isRefresh = true;
		} else if (refreshView.isFooterShown()) {
			isRefresh = false;
		}
		startTask();
	}
}

