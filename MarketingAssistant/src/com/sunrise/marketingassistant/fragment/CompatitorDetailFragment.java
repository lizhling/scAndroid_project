package com.sunrise.marketingassistant.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.task.DelBaseInfoTask;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetBaseInfoTask;
import com.sunrise.marketingassistant.task.SaveOrUpdateBaseInfoTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.BaiduMapUtils;
import com.sunrise.marketingassistant.utils.CommUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 竞争对手信息详情
 * 
 * @author 珩
 * 
 */
public class CompatitorDetailFragment extends BaseFragment implements TaskListener, OnClickListener, OnItemSelectedListener, BDLocationListener,
		OnGetGeoCoderResultListener {

	/** 渠道编码 */
	private TextView code_text;
	/** 渠道名称 */
	private EditText name_et;
	/** 营业厅面积 */
	private EditText size_et;
	/** 地理位置属性 */
	private EditText location_et;
	/** 台席数量 */
	private EditText num_et;
	/** 营业厅人数 */
	private EditText people_num_et;
	/** 归属营销中心 */
	private EditText bussiness_et;
	/** 经度 */
	private EditText longitude_et;
	/** 纬度 */
	private EditText latitude_et;
	/** 详细地址 */
	private EditText address_et;
	private GenericTask mTask;
	private Spinner mSpinnerChannelType;

	/** 竞争对手详情 */
	private ChlBaseInfo mChlBaseInfo;

	/** 是否新增模式 */
	private boolean mIsAddState;

	private BaiduMapUtils baiduMapUtils;
	private LocationClient mLocationClient;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mChlBaseInfo = getArguments().getParcelable(Intent.EXTRA_DATA_REMOVED);
		mIsAddState = mChlBaseInfo == null;
	}

	public void onStart() {
		super.onStart();

		if (!mIsAddState)
			mTask = new GetBaseInfoTask().execute(getPreferences().getAuthentication(), String.valueOf(mChlBaseInfo.getChlId()), this);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		if (baiduMapUtils != null)
			baiduMapUtils.destory();

		if (mTask != null) {
			mTask.cancle();
		}
		super.onDestroy();
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_channels_info_collection, container, false);
		init(view);
		initData(mChlBaseInfo);
		return view;
	}

	private void init(View view) {
		code_text = (TextView) view.findViewById(R.id.code_text);
		name_et = (EditText) view.findViewById(R.id.name_et);
		mSpinnerChannelType = (Spinner) view.findViewById(R.id.spinner_channelType);
		mSpinnerChannelType.setOnItemSelectedListener(this);
		size_et = (EditText) view.findViewById(R.id.size_et);

		location_et = (EditText) view.findViewById(R.id.location_et);
		num_et = (EditText) view.findViewById(R.id.num_et);
		people_num_et = (EditText) view.findViewById(R.id.people_num_et);
		bussiness_et = (EditText) view.findViewById(R.id.bussiness_et);
		longitude_et = (EditText) view.findViewById(R.id.longitude_et);

		latitude_et = (EditText) view.findViewById(R.id.latitude_et);
		address_et = (EditText) view.findViewById(R.id.address_et);

		if (mIsAddState) {
			view.findViewById(R.id.delete_btn).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.location_btn).setOnClickListener(this);
			view.findViewById(R.id.delete_btn).setOnClickListener(this);
			view.findViewById(R.id.clear_btn).setOnClickListener(this);
			view.findViewById(R.id.save_btn).setOnClickListener(this);
		}
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
		if (task instanceof GetBaseInfoTask) {
			showDialog("获取基本信息，请稍候……");
		} else if (task instanceof SaveOrUpdateBaseInfoTask) {
			showDialog("正在保存信息，请稍候……");
		} else if (task instanceof DelBaseInfoTask) {
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

	private void saveData() {
		if (TextUtils.isEmpty(name_et.getText().toString())) {
			Toast.makeText(mBaseActivity, "渠道名称不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mSpinnerChannelType.getSelectedItemPosition() == -1) {
			Toast.makeText(mBaseActivity, "请选择渠道类型!", Toast.LENGTH_SHORT).show();
			return;
		}
		JSONObject json = new JSONObject();
		int empNum = 0;
		int salerNum = 0;
		if (!TextUtils.isEmpty(num_et.getText().toString())) {
			empNum = Integer.parseInt(num_et.getText().toString());
		}
		if (!TextUtils.isEmpty(people_num_et.getText().toString())) {
			salerNum = Integer.parseInt(people_num_et.getText().toString());
		}
		try {
			json.put("authenticationID", getPreferences().getAuthentication());
			if (mIsAddState) {
				json.put("chlId", "");
			} else {
				json.put("chlId", mChlBaseInfo.getChlId());
			}

			json.put("chlName", name_et.getText().toString());
			json.put("chlLvl", "0");
			json.put("busareaId", "0");
			json.put("arId", size_et.getText().toString());
			json.put("localNature", location_et.getText().toString());
			json.put("longitude", longitude_et.getText().toString());
			json.put("latitude", latitude_et.getText().toString());
			json.put("radius", "0");
			json.put("personNum", "0");
			json.put("personDensity", "0");
			json.put("rentFee", "0");
			json.put("empNum", empNum + "");
			json.put("salerNum", salerNum + "");
			json.put("address", address_et.getText().toString());
			json.put("isNew", "1");
			json.put("isLost", "0");
			json.put("mktCenterType", "0");
			json.put("mktCenterId", "1049199");
			json.put("login_no", getPreferences().getSubAccount());
			json.put("chlType", mSpinnerChannelType.getSelectedItemPosition());

			if (mTask != null) {
				mTask.cancle();
			}
			mTask = new SaveOrUpdateBaseInfoTask().execute(json.toString(), this);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void clearData() {
		name_et.setText("");
		size_et.setText("");
		location_et.setText("");
		num_et.setText("");
		people_num_et.setText("");
		bussiness_et.setText("");
		longitude_et.setText("");
		latitude_et.setText("");
		address_et.setText("");
	}

	private void initData(ChlBaseInfo info) {
		code_text.setText(String.valueOf(info.getChlId()));
		name_et.setText(info.getChlName());
		size_et.setText(info.getArea() + "");
		location_et.setText(info.getLocalNature());
		num_et.setText(info.getEmpNum() + "");
		people_num_et.setText(info.getSalerNum() + "");
		bussiness_et.setText(info.getMktCenter());
		longitude_et.setText(info.getLongitude() + "");
		latitude_et.setText(info.getLatitude() + "");
		address_et.setText(info.getAddress());

		if (info.getChllvl() != null)
			for (int i = 0; i < mSpinnerChannelType.getAdapter().getCount(); ++i) {

				if (info.getChllvl().equals(mSpinnerChannelType.getAdapter().getItem(i))) {
					mSpinnerChannelType.setSelection(i);
					return;
				}
			}
		mSpinnerChannelType.setSelection(mSpinnerChannelType.getAdapter().getCount() - 1);
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param == null)
			return;
		if (task instanceof GetBaseInfoTask) {
			initData((ChlBaseInfo) param);
		} else if (task instanceof SaveOrUpdateBaseInfoTask) {
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
		} else if (task instanceof DelBaseInfoTask) {
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

	}

	/** 请求获取定位数据 */
	private void getLocation() {
		initDialog();
		showDialog("获取定位信息，请稍候……");

		if (baiduMapUtils == null) {
			baiduMapUtils = new BaiduMapUtils(mBaseActivity);
			mLocationClient = baiduMapUtils.startLocationListener(this);
		}
		mLocationClient.requestLocation();
	}

	/** 实现实位回调监听 */
	@Override
	public void onReceiveLocation(BDLocation location) {
		dismissDialog();
		latitude_et.setText(String.valueOf(location.getLatitude()));
		longitude_et.setText(String.valueOf(location.getLongitude()));

		baiduMapUtils.startReverseGeoCode(new LatLng(location.getLatitude(), location.getLongitude()), this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.location_btn:
			getLocation();
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
		mTask = new DelBaseInfoTask().execute(getPreferences().getAuthentication(), getPreferences().getSubAccount(), String.valueOf(mChlBaseInfo.getChlId()),
				this);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		address_et.setText(arg0.getAddress());
	}

}
