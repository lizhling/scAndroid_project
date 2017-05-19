package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.database.CmmaDBHelper;
import com.sunrise.marketingassistant.entity.CollectBranch;
import com.sunrise.javascript.utils.LogUtlis;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LocationFragment extends BaseFragment implements AMapLocationListener, AMapLocalWeatherListener, OnClickListener {

	private TextView text1, text2, text3, text4, text5, text6;
	private LocationManagerProxy aMapLocManager;
	private ListView mListView;
	private ArrayList<String> mArrayDB;
	private TextView mEditText;
	private CmmaDBHelper cdbh;
	private ArrayAdapter<String> mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cdbh = new CmmaDBHelper(mBaseActivity);
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_location, null);
		text1 = (TextView) view.findViewById(R.id.textView1);
		text2 = (TextView) view.findViewById(R.id.textView2);
		text3 = (TextView) view.findViewById(R.id.textView3);
		text4 = (TextView) view.findViewById(R.id.textView4);
		text5 = (TextView) view.findViewById(R.id.textView5);
		text6 = (TextView) view.findViewById(R.id.textView6);

		mListView = (ListView) view.findViewById(R.id.listView01);
		mArrayDB = new ArrayList<String>();
		mAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.simple_list_item, mArrayDB);
		mListView.setAdapter(mAdapter);
		view.findViewById(R.id.button1).setOnClickListener(this);
		view.findViewById(R.id.button2).setOnClickListener(this);
		view.findViewById(R.id.button3).setOnClickListener(this);
		view.findViewById(R.id.button4).setOnClickListener(this);
		view.findViewById(R.id.button5).setOnClickListener(this);

		mEditText = (TextView) view.findViewById(R.id.editText1);
		return view;
	}

	public void onStart() {
		super.onStart();
		initLocation();
	}

	public void onStop() {
		stopLocation();
		super.onStop();
	}

	@SuppressWarnings("deprecation")
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}

	private void initLocation() {
		aMapLocManager = LocationManagerProxy.getInstance(mBaseActivity);
		/*
		 * •provider：有三种定位Provider供用户选择，分别是:LocationManagerProxy.GPS_PROVIDER，
		 * 代表使用手机GPS定位
		 * ；LocationManagerProxy.NETWORK_PROVIDER，代表使用手机网络定位；LocationProviderProxy
		 * .AMapNetwork，代表高德网络定位服务，混合定位。
		 */
		/* •minTime：位置变化的通知时间，单位为毫秒。如果为-1，定位只定位一次 */
		/* •minDistance:位置变化通知距离，单位为米。 */
		/* •listener:定位监听者。 */
		aMapLocManager.requestLocationData(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		aMapLocManager.setGpsEnable(false);

		aMapLocManager.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);
		aMapLocManager.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_FORECAST, this);
	}

	@Override
	public void onLocationChanged(Location arg0) {
		text5.setText("onLocationChanged( " + arg0.getLongitude() + "," + arg0.getLatitude() + ")");
	}

	@Override
	public void onProviderDisabled(String arg0) {
		text4.setText("onProviderDisabled( " + arg0 + ")");
	}

	@Override
	public void onProviderEnabled(String arg0) {
		text3.setText("onProviderEnabled( " + arg0 + ")");
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		text2.setText("onStatusChanged( " + arg0 + "," + arg1 + ")");
	}

	@Override
	public void onLocationChanged(AMapLocation arg0) {
		if (TextUtils.isEmpty(arg0.getCity()))
			return;
		text1.setText("onLocationChanged( 城市:" + arg0.getCity() + "," + arg0.getAdCode() + "," + arg0.getAddress() + "," + arg0.getAltitude() + ","
				+ arg0.getBearing() + "," + arg0.getCityCode() + "," + arg0.getCountry() + "," + arg0.getDistrict() + "," + arg0.getFloor() + ","
				+ arg0.getPoiId() + "," + arg0.getPoiName() + "," + arg0.getProvider() + "," + arg0.getProvince() + "," + arg0.getRoad() + ","
				+ arg0.getSpeed() + "," + arg0.getStreet() + "," + arg0.getTime() + "," + arg0.getLongitude() + "," + arg0.getLatitude() + ")");
	}

	@Override
	public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {
		// if (arg0 != null && arg0.getAMapException().getErrorCode() == 0) {
		// StringBuilder sb = new StringBuilder();
		// for (AMapLocalDayWeatherForecast forecast :
		// arg0.getWeatherForecast()) {
		// sb.append(forecast.getProvince()).append(forecast.getCity()).append("[").append(forecast.getCityCode()).append("]:").append(forecast.getDate())
		// .append(' ').append(forecast.getWeek()).append("\n   ");
		// sb.append(forecast.getDayWeather()).append(',').append(forecast.getDayTemp()).append('℃').append(' ').append(forecast.getDayWindDir())
		// .append(forecast.getDayWindPower());
		// sb.append("\n   ").append(forecast.getNightWeather()).append(',').append(forecast.getNightTemp()).append('℃').append(' ')
		// .append(forecast.getNightWindDir()).append(forecast.getNightWindPower()).append('\n');
		// }
		//
		// text3.setText(sb.toString());
		// }
	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive arg0) {
		text2.setText("onWeatherLiveSearched( " + arg0.getCity() + ",\n天气：" + arg0.getWeather() + "℃ ,\n更新时间：" + arg0.getReportTime() + ",\n"
				+ arg0.getHumidity() + "%, \n风向：" + arg0.getWindDir() + ",\n" + arg0.getCityCode() + ",温度：" + arg0.getTemperature() + "℃ ,\n风力："
				+ arg0.getWindPower() + "级)");
	}

	@Override
	public void onClick(View arg0) {
		String text = mEditText.getText().toString();
		String account = getPreferences().getSubAccount();
		switch (arg0.getId()) {
		case R.id.button1:
			LogUtlis.e("数据库", "删除结果：" + cdbh.deleteByGroupId(account, text));
			break;
		case R.id.button2:
			CollectBranch c = new CollectBranch();
			c.setAccount(account);
			c.setGROUP_ID(text);
			c.setGROUP_ADDRESS("" + Math.round(100 * Math.random()));
			c.setCLASS_NAME("" + Math.round(100 * Math.random()));
			c.setGROUP_NAME("" + Math.round(100 * Math.random()));
			LogUtlis.e("数据库", "插入结果：" + cdbh.insert(c));
			break;
		case R.id.button3:
			show2List(cdbh.queryCollectBranchByGroupId(account, text));
			break;
		case R.id.button4:
			show2List(cdbh.queryCollectBranchByAccount(text));
			break;
		case R.id.button5:
			show2List(cdbh.queryCollectBranch());
			break;
		default:
			break;
		}
	}

	private void show2List(ArrayList<CollectBranch> arrayList) {
		mArrayDB.clear();
		if (arrayList != null)
			for (CollectBranch item : arrayList)
				mArrayDB.add(JsonUtils.writeObjectToJsonStr(item));
		mAdapter.notifyDataSetChanged();
	}
}
