package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine.WalkingStep;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.sunrise.javascript.activity.MipcaCaptureActivity;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.javascript.utils.UnitUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.CompatitorDetailActivity;
import com.sunrise.marketingassistant.activity.HallDetailActivity;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.entity.ShareOfChannels;
import com.sunrise.marketingassistant.entity.TabContent;
import com.sunrise.marketingassistant.entity.TabContentManager;
import com.sunrise.marketingassistant.task.DoMobRegisteTask;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetAppQuotaAllCity;
import com.sunrise.marketingassistant.task.GetChannelSearchTaskByLatLng;
import com.sunrise.marketingassistant.task.GetCompetitorInfoTask;
import com.sunrise.marketingassistant.task.GetGroupIndexsortTask;
import com.sunrise.marketingassistant.task.GetMobPictureTask;
import com.sunrise.marketingassistant.task.MobInfoByGroupIdTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.BaiduMapUtils;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.utils.HardwareUtils;
import com.sunrise.marketingassistant.view.PopupWindowPieChartViewForRateOfChannels;

@SuppressWarnings("rawtypes")
@SuppressLint({ "HandlerLeak", "InflateParams" })
public class LocationOverlayFragment extends BaseFragment implements OnClickListener, BDLocationListener, TaskListener, ExtraKeyConstant,
		OnGetRoutePlanResultListener, OnItemSelectedListener, OnGetGeoCoderResultListener, OnCheckedChangeListener {

	private SDKReceiver mReceiver;

	private final String SEARCH_KEY = "特约代理点";
	private static final int MAX_SEARCH_RANGE = 5000;

	private static final String TextView = null;

	/** 用户坐标位置 */
	private LatLng mLocData = null;

	// private GenericTask mTask;
	// private GetCompetitorInfoTask mGetCompetitorInfoTask;
	// private GetChannelSearchTaskByLatLng mSearchPoiTask;
	// private GetMobPictureTask mGetMobPicture;

	private ArrayList<GenericTask> mArrayTask = new ArrayList<GenericTask>();

	private BaiduMap mBaiduMap;// 百度地图

	/** 弹出泡泡图层 */
	private View mViewCache = null;

	/**
	 * 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理,如果不处理touch事件，则无需继承，
	 * 直接使用MapView即可
	 */
	private MapView mMapView = null; // 地图View

	private final int LATLON_ACTION_NORMAL = 0;// 平时获取到坐标
	private final int LATLON_ACTION_FIRST = 1;// 首次定位
	private final int LATLON_ACTION_USERCALL = 2;// 用户定位
	private int mLatlonAction = LATLON_ACTION_FIRST;

	/** 显示路线状态 */
	private boolean mIsShowLine;

	/** 可预约图标 */
	private BitmapDescriptor mEnableMarker;
	/** 不可预约图标 */
	private BitmapDescriptor mUnableMarker;

	private BitmapDescriptor mDBRouteLineStep;

	private BitmapDescriptor mBDLiantong;
	private BitmapDescriptor mBDDianxin;

	private BitmapDescriptor[] mBD_TOP10;

	private BitmapDescriptor[] mBD_INDEX;

	/** 起点 **/
	private BitmapDescriptor mStartMarker;
	/** 终点 **/
	private BitmapDescriptor mEndMarker;

	/** 百度地图动画图标 */
	private ArrayList<BitmapDescriptor> mGiflistOfMarker;

	/** 当前选中marker对象的序列 */
	private int mIndexTemp = -1;
	/**
	 * 查找结果
	 */
	// private ArrayList<PoiInfo> mSearchMkPoiInfos = new ArrayList<PoiInfo>();
	/** 营业厅信息 */
	private ArrayList<MobileBusinessHall> mArrMobileBusinessHalls = new ArrayList<MobileBusinessHall>();
	/** 指标信息 */
	private ArrayList<MobileBusinessHall> mArrIndexsort;

	/** marker点的地址信息 */
	private TextView mPointLatLngText;
	/** marker点的名称 */
	private TextView mPointLatLngName;
	/** 地图marker对应的地址详情按钮 */
	private TextView mBtnDetail;

	private ExecutorService mPool;
	private ImageView mBtnFindMe;

	private BaiduMapUtils baiduMapUtils;

	/** 图标 */
	private ImageView mIVHall;

	/** 线路选择 */
	private Spinner mSpinnerLines;

	/** 搜索栏 */
	private View mSearcherBar;

	/*** 所在城市 */
	private String mCity;

	/** 指标切换漂浮窗 */
	private PopupWindow mPopupWindowDrawer;
	/** 渠道份额占比悬浮窗 */
	private PopupWindowPieChartViewForRateOfChannels mPopupWindowRate;

	/** KPI指标选择器 */
	private Spinner mSpinner_Trigger;
	/** KPI排序后内容选择器 */
	private Spinner mSpinnerIndexSort;
	/** 竞争对手列表 */
	private ArrayList<ChlBaseInfo> mArrayCometitorInfo;

	private CheckBox mCheckboxDianxin;

	private CheckBox mCheckboxLiantong;

	private CheckBox mCheckboxChinaMobile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mEnableMarker = BitmapDescriptorFactory.fromResource(R.drawable.pop_map);
		mUnableMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		mDBRouteLineStep = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_place);

		mBD_TOP10 = new BitmapDescriptor[RES_ID_MARKER_TOP_10.length];
		for (int i = 0; i < RES_ID_MARKER_TOP_10.length; ++i)
			mBD_TOP10[i] = BitmapDescriptorFactory.fromResource(RES_ID_MARKER_TOP_10[i]);

		mBD_INDEX = new BitmapDescriptor[RES_ID_MARKER_INDEX.length];
		for (int i = 0; i < RES_ID_MARKER_INDEX.length; ++i)
			mBD_INDEX[i] = BitmapDescriptorFactory.fromResource(RES_ID_MARKER_INDEX[i]);

		mGiflistOfMarker = new ArrayList<BitmapDescriptor>();
		mGiflistOfMarker.add(mEnableMarker);
		mGiflistOfMarker.add(mUnableMarker);

		mPool = Executors.newFixedThreadPool(4);
		baiduMapUtils = new BaiduMapUtils(mBaseActivity);

		{
			String _latitude = getPreferences().getCacheString(KEY_MY_LOCATION_LATITUDE);
			String _longitude = getPreferences().getCacheString(KEY_MY_LOCATION_LONGITUDE);
			if (!TextUtils.isEmpty(_latitude))
				mLocData = new LatLng(Double.parseDouble(_latitude), Double.parseDouble(_longitude));
		}

		if (TextUtils.isEmpty(getPreferences().getCacheString(KEY_SHARE_OF_CHANNEL_ALL_CITYS)))
			doGetAppQuotaAllCity();
	}

	public void onStart() {
		super.onStart();
		registBaiduSDKReceiver();
		initLocation();
	}

	public void onStop() {

		if (baiduMapUtils != null) {
			baiduMapUtils.stopLocationListener();
			baiduMapUtils.stopSearch();
		}

		// 取消监听 SDK 广播
		mBaseActivity.unregisterReceiver(mReceiver);

		super.onStop();
	}

	public boolean onBackPressed() {
		if (mIsShowLine) {
			mBaiduMap.clear();
			setWhenShowLineState(!mIsShowLine, false);
			return true;
		}

		return false;
	}

	private void cancleTask() {
		// if (mTask != null)
		// mTask.cancle();
		// if (mGetCompetitorInfoTask != null)
		// mGetCompetitorInfoTask.cancle();
		// if (mSearchPoiTask != null)
		// mSearchPoiTask.cancle();
		//
		// if (mGetMobPicture != null)
		// mGetMobPicture.cancle();
		for (int i = 0; i < mArrayTask.size(); i++) {
			mArrayTask.get(i).cancle();
		}
	}

	private void registBaiduSDKReceiver() {
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		mBaseActivity.registerReceiver(mReceiver, iFilter);
	}

	/**
	 * 创建弹出泡泡图层 在地图上弹出的预约取号小对话框
	 */
	private void initInfoWindowContent(LayoutInflater inflater) {
		mViewCache = inflater.inflate(R.layout.layout_pop_window, null);

		mViewCache.findViewById(R.id.btn_bus).setOnClickListener(this);
		mViewCache.findViewById(R.id.btn_bike).setOnClickListener(this);
		mViewCache.findViewById(R.id.btn_car).setOnClickListener(this);

		mPointLatLngText = (TextView) mViewCache.findViewById(R.id.address_location);
		mPointLatLngName = (TextView) mViewCache.findViewById(R.id.address_name);
		mBtnDetail = (TextView) mViewCache.findViewById(R.id.btn_info);
		mPointLatLngName.setOnClickListener(this);
		mPointLatLngText.setOnClickListener(this);
		mBtnDetail.setOnClickListener(this);

		mViewCache.findViewById(R.id.btn_sign).setOnClickListener(this);

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				if (mIsShowLine) {
					TextView textView = (TextView) LayoutInflater.from(mBaseActivity).inflate(R.layout.simple_list_item, null);
					textView.setMaxEms(6);
					textView.setText(marker.getTitle());
					textView.setBackgroundResource(R.drawable.bg_map_info);
					baiduMapUtils.addInfoWindow(mBaiduMap, textView, marker.getPosition());
					mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(marker.getPosition()));
					return true;
				}

				if (marker.getIcon() == mBDDianxin || marker.getIcon() == mBDLiantong) {
					for (int i = 0; i < mArrMobileBusinessHalls.size(); ++i)
						if (mArrayCometitorInfo.get(i).getChlName() == marker.getTitle()) {
							showCompetitorDetail(i);
							mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(marker.getPosition()));
							break;
						}
					return true;
				}

				for (int i = 0; i < mArrMobileBusinessHalls.size(); ++i)
					if (mArrMobileBusinessHalls.get(i).getGROUP_NAME() == marker.getTitle()) {
						mIndexTemp = i;
						showMobileBusinessHallDetail(mArrMobileBusinessHalls.get(i));
						mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(marker.getPosition()));
						break;
					}
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				hideInfoWindow();
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				hideInfoWindow();
			}
		});

		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {
				LogUtlis.i("获取坐标", "坐标：" + arg0.latitude + " , " + arg0.longitude);
				baiduMapUtils.startReverseGeoCode(arg0, LocationOverlayFragment.this);
			}
		});

		mIVHall = (ImageView) mViewCache.findViewById(R.id.image_hall);

	}

	/**
	 * 功能： 初始化定位
	 */
	private void initLocation() {
		if (mLocData == null) {
			mLocData = new LatLng(30.65642060414003, 104.06552229267588);
			// 设置定位数据
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(mLocData));
		}
		// 定位初始化
		baiduMapUtils.startLocationListener(this);
	}

	private void initMapView(View view) {
		// 地图初始化
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		if (mLocData != null) {
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));
			mBaiduMap.setMyLocationData(new MyLocationData.Builder().direction(100).latitude(mLocData.latitude).longitude(mLocData.longitude).build());
			doGetChannelByLatLng(SEARCH_KEY, mLocData, MAX_SEARCH_RANGE);
		}
		mBaiduMap.showMapPoi(true);// 将底图标注设置为隐藏
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void initPopwindowOperation() {
		View operateView = LayoutInflater.from(mBaseActivity).inflate(R.layout.part_locationoverlay_drawer_content, null, false);
		CheckBox mCheckMapPoi = (CheckBox) operateView.findViewById(R.id.check_showMapPoi);
		mCheckMapPoi.setOnCheckedChangeListener(this);
		CheckBox mCheckSatellite = (CheckBox) operateView.findViewById(R.id.check_mapState_SATELLITE);
		mCheckSatellite.setOnCheckedChangeListener(this);

		mCheckboxDianxin = (CheckBox) operateView.findViewById(R.id.check_dianxin);
		mCheckboxDianxin.setOnCheckedChangeListener(this);
		mCheckboxLiantong = (CheckBox) operateView.findViewById(R.id.check_liantong);
		mCheckboxLiantong.setOnCheckedChangeListener(this);
		mCheckboxChinaMobile = (CheckBox) operateView.findViewById(R.id.check_cnmobile);
		mCheckboxChinaMobile.setOnCheckedChangeListener(this);

		operateView.findViewById(R.id.btn_drawer_hide).setOnClickListener(this);

		mSpinner_Trigger = (Spinner) operateView.findViewById(R.id.spinner_trigger);
		mPopupWindowDrawer = new PopupWindow(operateView, -2, -2, false);
		mPopupWindowDrawer.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindowDrawer.setFocusable(true);
		mPopupWindowDrawer.setOutsideTouchable(false);
		mPopupWindowDrawer.setAnimationStyle(R.style.MorePopupAnimation);
	}

	// private void initShowHallListData() {
	// mShowMobileBusinessHalls.clear();
	// mShowMobileBusinessHalls.addAll(mArrMobileBusinessHalls);
	// }

	/**
	 * 根据经纬度搜索周边渠道
	 * 
	 * @param key
	 * @param pt
	 * @param radius
	 */
	private void doGetChannelByLatLng(String key, final LatLng pt, final int radius) {
		// mSearchPoiTask =
		new GetChannelSearchTaskByLatLng().execute(getPreferences().getMobile(), getPreferences().getSubAccount(), getPreferences().getGroupId(), pt.longitude,
				pt.latitude, null, this);
	}

	private void prepareCompetitorData() {
		// if (mGetCompetitorInfoTask != null)
		// mGetCompetitorInfoTask.cancle();
		for (int i = 0; i < mArrayTask.size(); i++) {
			if (mArrayTask.get(i) instanceof GetCompetitorInfoTask)
				mArrayTask.get(i).cancle();
		}
		// mGetCompetitorInfoTask =
		new GetCompetitorInfoTask().execute(mLocData.latitude, mLocData.longitude, null, this);
	}

	private void refreshOverlayLocal() {
		if (mIsShowLine || mMapView == null)
			return;
		mBaiduMap.clear();

		if (mCheckboxChinaMobile == null || mCheckboxChinaMobile.isChecked())
			refreshAllBusinessHall();
		refreshCompetitorInfo();
	}

	/** 展示营业厅 */
	private void refreshAllBusinessHallWithOutIndexsort(ArrayList<MobileBusinessHall> result) {

		for (int i = 0; i < result.size(); i++) {
			MobileBusinessHall hall = result.get(i);
			if (hall.getLATITUDE() == 0 || hall.getLONGITUDE() == 0)
				continue;
			LatLng coordinate = new LatLng(hall.getLATITUDE(), hall.getLONGITUDE());

			baiduMapUtils.addMarker2Map(mBaiduMap, hall.getGROUP_NAME(), coordinate, mEnableMarker, MarkerAnimateType.none, null);
		}

	}

	private void refreshAllBusinessHall() {

		if (mArrIndexsort == null) {
			refreshAllBusinessHallWithOutIndexsort(mArrMobileBusinessHalls);
			mSpinnerIndexSort.setVisibility(View.GONE);
			return;
		}

		ArrayList<String> arrayList = new ArrayList<String>();// 用于显示指标

		int perLength = (mArrIndexsort.size() - mBD_TOP10.length) / mBD_INDEX.length;

		for (int i = 0; i < mArrIndexsort.size(); i++) {

			MobileBusinessHall item = mArrIndexsort.get(i);
			for (MobileBusinessHall hall : mArrMobileBusinessHalls) {
				if (hall.getGROUP_ID().equals(item.getGROUP_ID())) {
					if (hall.getLATITUDE() == 0 || hall.getLONGITUDE() == 0)
						break;
					LatLng coordinate = new LatLng(hall.getLATITUDE(), hall.getLONGITUDE());

					BitmapDescriptor bitmapDes;
					if (i < mBD_TOP10.length) {
						bitmapDes = mBD_TOP10[i];
					} else if ((i - mBD_TOP10.length) / perLength < mBD_INDEX.length)
						bitmapDes = mBD_INDEX[(i - mBD_TOP10.length) / perLength];
					else
						bitmapDes = mUnableMarker;

					baiduMapUtils.addMarker2Map(mBaiduMap, hall.getGROUP_NAME(), coordinate, bitmapDes, MarkerAnimateType.none, null);
					// 增加指标排序的选择item
					switch (mSpinner_Trigger.getSelectedItemPosition()) {
					case 1:
						arrayList.add(String.format(FORMAT_INDEXSORT_SHOW_G4_TARIFF_ADD, hall.getGROUP_NAME(), item.getG4_TARIFF_ADD(), item.getDATA_CYCLE()));
						break;
					case 2:
						arrayList.add(String.format(FORMAT_INDEXSORT_SHOW_G4_TEAM_SALE, hall.getGROUP_NAME(), item.getG4_TERM_SALES(), item.getDATA_CYCLE()));
						break;
					case 3:
					default:
						arrayList
								.add(String.format(FORMAT_INDEXSORT_SHOW_BROADBAND_NUMS, hall.getGROUP_NAME(), item.getBROADBAND_NUMS(), item.getDATA_CYCLE()));
						break;
					}
					break;
				}
			}
		}

		mSpinnerIndexSort.setAdapter(new ArrayAdapter<String>(mBaseActivity, R.layout.simple_list_item, arrayList));
		mSpinnerIndexSort.setVisibility(View.VISIBLE);
	}

	/** 展示竞争对手 */
	private void refreshCompetitorInfo() {
		if (mArrayCometitorInfo == null || mArrayCometitorInfo.isEmpty())
			return;

		if (mBDLiantong == null)
			mBDLiantong = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_liantong);

		if (mBDDianxin == null)
			mBDDianxin = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_dianxin);
		String strDianxin = getString(R.string.dianxin);
		String strLiantong = getString(R.string.liantong);

		for (ChlBaseInfo item : mArrayCometitorInfo) {
			LatLng latlng = new LatLng(item.getLatitudeDouble(), item.getLongitudeDouble());
			if (strDianxin.equals(item.getChllvl()) && mCheckboxDianxin.isChecked())
				baiduMapUtils.addMarker2Map(mBaiduMap, item.getChlName(), latlng, mBDDianxin, MarkerAnimateType.grow, null);
			else if (strLiantong.equals(item.getChllvl()) && mCheckboxLiantong.isChecked())
				baiduMapUtils.addMarker2Map(mBaiduMap, item.getChlName(), latlng, mBDLiantong, MarkerAnimateType.grow, null);
		}

	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (mBaiduMap != null) {
			mBaiduMap.setMyLocationEnabled(false);
			mMapView.onDestroy();
			mMapView = null;
		}

		if (mPool != null && !mPool.isTerminated() && !mPool.isShutdown())
			mPool.shutdown();

		doCatheState();

		cancleTask();

		super.onDestroy();
	}

	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// mMapView.ononSaveInstanceState(outState);
	//
	// }

	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// super.onRestoreInstanceState(savedInstanceState);
	// mMapView.onRestoreInstanceState(savedInstanceState);
	// }

	private void doGetGroupInfo(String groupId) {

		// if (mTask != null)
		// mTask.cancle();

		for (int i = 0; i < mArrayTask.size(); i++) {
			if (mArrayTask.get(i) instanceof MobInfoByGroupIdTask)
				mArrayTask.get(i).cancle();
		}
		// mTask =
		new MobInfoByGroupIdTask().execute(getPreferences().getMobile(), groupId, getPreferences().getSubAccount(), this);
	}

	private void showMobileBusinessHallDetail(MobileBusinessHall hall) {

		LatLng pt = new LatLng(hall.getLATITUDE(), hall.getLONGITUDE());

		mPointLatLngName.setText(hall.getGROUP_NAME());
		mViewCache.findViewById(R.id.btn_bus).setTag(pt);
		mViewCache.findViewById(R.id.btn_bike).setTag(pt);
		mViewCache.findViewById(R.id.btn_car).setTag(pt);
		mViewCache.findViewById(R.id.btn_sign).setVisibility(View.VISIBLE);

		mPointLatLngName.setTag(hall);
		mPointLatLngText.setTag(hall);
		mBtnDetail.setTag(hall);

		{// 显示距离和签到
			double distance = BaiduMapUtils.getDistance(mLocData, pt);

			// 如果没有获取到地址，仅显示距离。并且启动详情查找
			if (TextUtils.isEmpty(hall.getGROUP_ADDRESS())) {
				mPointLatLngText.setText(String.format("(%.0f米)", distance));
				doGetGroupInfo(hall.getGROUP_ID());
			} else
				mPointLatLngText.setText(String.format("%s(%.0f米)", hall.getGROUP_ADDRESS(), distance));

		}
		{
			mIVHall.setVisibility(View.GONE);
			String iconurl = hall.getIMG_INFO_FIRST();

			for (int i = 0; i < mArrayTask.size(); i++) {
				if (mArrayTask.get(i) instanceof GetMobPictureTask)
					mArrayTask.get(i).cancle();
			}
			if (!TextUtils.isEmpty(iconurl))
				new GetMobPictureTask().execute(getPreferences().getSubAccount(), iconurl, this);
		}

		InfoWindow mInfoWindow = new InfoWindow(mViewCache, pt, 0);

		mBaiduMap.showInfoWindow(mInfoWindow);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(pt));

	}

	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_locationoverlay, null);

		mSearcherBar = view.findViewById(R.id.linearLayout01);

		mBtnFindMe = (ImageView) view.findViewById(R.id.btn_myLocation);
		view.findViewById(R.id.btn_search).setOnClickListener(this);

		view.findViewById(R.id.btn_drawer_show).setOnClickListener(this);

		initMapView(view);

		// 二维码扫描按钮
		view.findViewById(R.id.btn_scan).setOnClickListener(this);

		view.findViewById(R.id.btn_rate).setOnClickListener(this);

		// 线路选择按钮
		mSpinnerLines = (Spinner) view.findViewById(R.id.spinner_lines);
		mSpinnerLines.setOnItemSelectedListener(this);

		mSpinnerIndexSort = (Spinner) view.findViewById(R.id.spinner_indexsort);
		mSpinnerIndexSort.setOnItemSelectedListener(this);

		// 初始化网点信息详情界面
		initInfoWindowContent(inflater);

		return view;
	}

	@Override
	public void onClick(View v) {
		// 顶部选择搜索距离刻度条
		switch (v.getId()) {
		case R.id.btn_myLocation:
			mLatlonAction = LATLON_ACTION_USERCALL;
			((AnimationDrawable) mBtnFindMe.getDrawable()).start();
			hideInfoWindow();
			break;
		case R.id.btn_bus:
			getMapTransitRoute(2, (LatLng) v.getTag());
			break;
		case R.id.btn_bike:
			getMapTransitRoute(1, (LatLng) v.getTag());
			break;
		case R.id.btn_car:
			getMapTransitRoute(0, (LatLng) v.getTag());
			break;
		case R.id.btn_scan:
			startActivityForResult(new Intent(mBaseActivity, MipcaCaptureActivity.class), REQUEST_CODE_MUPCA_CAPTURE);
			break;
		case R.id.btn_rate:
			showRateOfChannel(v);
			break;
		case R.id.btn_drawer_show:// 显示功能
			hideInfoWindow();
			showOperationPanel(v);
			break;
		case R.id.btn_drawer_hide:
			hideOperationPanel();
			break;
		case R.id.btn_search:
			gotoSearchPage();
			break;
		case R.id.btn_sign:// 签到
			doSignIn();
			break;
		case R.id.btn_info:// 签到
		case R.id.address_name:// 详情界面
		case R.id.address_location:// 详情界面
			gotoHallDetail(v);
			break;
		default:
			break;
		}
	}

	/** 前往网点详情 */
	private void gotoHallDetail(View v) {

		if (v.getTag() == null)
			return;

		if (v.getTag() instanceof MobileBusinessHall) {
			gotoCMHallDetailPage((MobileBusinessHall) v.getTag());
		}

		else if (v.getTag() instanceof ChlBaseInfo) {
			ChlBaseInfo info = (ChlBaseInfo) v.getTag();
			// intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// Bundle bundle = new Bundle();
			// bundle.putParcelable(Intent.EXTRA_DATA_REMOVED, info);
			// intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
			// intent.putExtra(KEY_FRAGMENT, CompatitorDetailFragment.class);
			Intent intent = new Intent(mBaseActivity, CompatitorDetailActivity.class);
			intent.putExtra(Intent.EXTRA_DATA_REMOVED, info);
			startActivity(intent);
		}
	}

	private void gotoCMHallDetailPage(MobileBusinessHall info) {
		Intent intent = null;
		TabContent temp = TabContentManager.getInstance(mBaseActivity).getTabContent(ID_TAB_ITEM_CHANNEL_DETAIL);
		// if (temp == null || temp.getType() != 0) {// 原生详情
		// intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// Bundle bundle = new Bundle();
		// bundle.putParcelable(Intent.EXTRA_DATA_REMOVED, info);
		// intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		// intent.putExtra(KEY_FRAGMENT, HallDetailFragment2.class);
		// } else {// html展现详情
		intent = HallDetailActivity.createIntent(mBaseActivity, WebViewFragment2.class, temp.getZipContent(), temp.getTabName(), temp.getLastModify(),
				info.getGROUP_ID());
		// }
		startActivity(intent);
	}

	private void gotoSearchPage() {
		Intent intent = null;
		intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putBoolean(Intent.EXTRA_CC, true);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		intent.putExtra(KEY_FRAGMENT, ChannelListFragment.class);
		startActivity(intent);
	}

	/** 签到的动作 */
	private void doSignIn() {

		MobileBusinessHall tempHall = mArrMobileBusinessHalls.get(mIndexTemp);
		LatLng pt = new LatLng(tempHall.getLATITUDE(), tempHall.getLONGITUDE());
		double distance = BaiduMapUtils.getDistance(mLocData, pt);
		long maxRange = getPreferences().getMaxSignRange();
		if (maxRange < distance) {
			CommUtil.showAlert(mBaseActivity, null, String.format(FORMAT_NOTICE_SIGN_UNABLE, distance, maxRange), null, null);
			return;
		}

		new DoMobRegisteTask().execute(tempHall.getGROUP_ID(), getPreferences().getSubAccount(), getPreferences().getMobile(), tempHall.getLATITUDE(),
				tempHall.getLONGITUDE(), HardwareUtils.getPhoneIMSI(mBaseActivity), this);

	}

	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(mBaseActivity, "地图错误请重新启动", Toast.LENGTH_SHORT).show();
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(mBaseActivity, "网络异常……", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 功能： 隐藏显示的信息框 note by qhb
	 */
	private void hideInfoWindow() {
		mBaiduMap.hideInfoWindow();

		mIndexTemp = -1;
		// if (mTask != null)
		// mTask.cancle();
		for (int i = 0; i < mArrayTask.size(); i++) {
			if (mArrayTask.get(i) instanceof MobInfoByGroupIdTask)
				mArrayTask.get(i).cancle();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_MUPCA_CAPTURE) {

			if (resultCode == Activity.RESULT_OK) {
				String result = data.getStringExtra(Intent.EXTRA_TEXT);
				LogUtlis.e("二维码扫描返回", result);
				TabContent temp = TabContentManager.getInstance(mBaseActivity).getTabContent(ID_TAB_ITEM_CHANNEL_SCAN);
				startActivity(SingleFragmentActivity.createIntent(mBaseActivity, WebViewFragment2.class, temp.getZipContent(), temp.getTabName(),
						temp.getLastModify(), result));
			} else
				LogUtlis.e("二维码扫描返回", "取消扫描");
		}
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		mLocData = new LatLng(location.getLatitude(), location.getLongitude());

		// map view 销毁后不在处理新接收的位置
		if (baiduMapUtils == null || mMapView == null)
			return;
		mCity = location.getCity();

		MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100).latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);

		if (mLatlonAction == LATLON_ACTION_NORMAL)
			return;

		// 移动地图到定位点

		if (mLatlonAction == LATLON_ACTION_FIRST) {// 初始化定位和搜索周边
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));
			mBtnFindMe.setOnClickListener(this);
			if (mArrMobileBusinessHalls.isEmpty())// 添加为空条件才启动搜索
				doGetChannelByLatLng(SEARCH_KEY, mLocData, MAX_SEARCH_RANGE);
			// if (TextUtils.isEmpty(mCity)) {
			// baiduMapUtils.checkOfflineUpdate(mCity, null);
			// baiduMapUtils.startLoadOfflineMap(mCity, null);
			// }
		} else {
			((AnimationDrawable) mBtnFindMe.getDrawable()).stop();
		}
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(mLocData));

		// 定位完成
		mLatlonAction = LATLON_ACTION_NORMAL;

	}

	/**
	 * @param type
	 *            0:开车；1:步行；2:换乘
	 */
	private void getMapTransitRoute(int type, LatLng latlng) {
		if (latlng == null)
			return;

		initDialog();

		baiduMapUtils.setRoutePlanSearchListener(this);
		if (type == 0) {
			showDialog(getString(R.string.searchingDriveLine));
			baiduMapUtils.startDrivingPlanSearch(mLocData, latlng);
		} else if (type == 1) {
			showDialog(getString(R.string.searchingWalkLine));
			baiduMapUtils.startWalkingPlanSearch(mLocData, latlng);
		} else {
			showDialog(getString(R.string.searchingBusLine));
			baiduMapUtils.startTransitPlanSearch(mCity, mLocData, latlng);
		}
		BaiduMapRoutePlan.finish(mBaseActivity);
	}

	private void showOperationPanel(View v) {
		if (mPopupWindowDrawer == null) {
			initPopwindowOperation();
		}

		int vh = v.getHeight();
		mPopupWindowDrawer.getContentView().measure(0, 0);
		int wh = mPopupWindowDrawer.getContentView().getMeasuredHeight();
		int yoff = wh + vh >> 1;

		mPopupWindowDrawer.showAsDropDown(v, 0, -yoff);

		mSpinner_Trigger.setOnItemSelectedListener(this);
	}

	private void hideOperationPanel() {
		if (mPopupWindowDrawer != null && mPopupWindowDrawer.isShowing())
			mPopupWindowDrawer.dismiss();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {

		mArrayTask.add(task);

		if (task instanceof DoMobRegisteTask) {
			initDialog();
			showDialog(mArrMobileBusinessHalls.get(mIndexTemp).getGROUP_NAME() + " 签到过程中……");
		}

	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		mArrayTask.remove(task);
		if (result != TaskResult.OK)
			Toast.makeText(mBaseActivity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param == null) {
			Toast.makeText(mBaseActivity, "未找到", Toast.LENGTH_LONG).show();
			return;
		}

		if (task instanceof GetChannelSearchTaskByLatLng) {
			ArrayList<MobileBusinessHall> result = (ArrayList<MobileBusinessHall>) param;
			// 错误号可参考MKEvent中的定义
			if (result.size() == 0) {
				Toast.makeText(mBaseActivity, "周边没有渠道网点……", Toast.LENGTH_LONG).show();
			} else {
				mArrMobileBusinessHalls.clear();
				mArrMobileBusinessHalls.addAll(result);

				// 移动到第一个点
				// mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(new
				// LatLng(result.get(0).getLATITUDE(),
				// result.get(0).getLONGITUDE())));
				refreshOverlayLocal();
				doGetGroupIndexsort(result);
			}
		}

		else if (task instanceof MobInfoByGroupIdTask) {
			if (mIndexTemp >= 0) {
				MobileBusinessHall tempHall = mArrMobileBusinessHalls.get(mIndexTemp);
				MobileBusinessHall hall = (MobileBusinessHall) param;
				tempHall.merge(hall);

				final LatLng pt = new LatLng(tempHall.getLATITUDE(), tempHall.getLONGITUDE());
				double distance = BaiduMapUtils.getDistance(mLocData, pt);
				mPointLatLngText.setText(String.format("%s(%.0f米)", tempHall.getGROUP_ADDRESS(), distance));
			}

		}

		else if (task instanceof DoMobRegisteTask)
			CommUtil.showAlert(mBaseActivity, null, mArrMobileBusinessHalls.get(mIndexTemp).getGROUP_NAME() + "\n" + param.toString(), null, false, null);

		else if (task instanceof GetGroupIndexsortTask) {
			mArrIndexsort = (ArrayList<MobileBusinessHall>) param;

			if (!mIsShowLine)
				refreshOverlayLocal();
		}

		else if (task instanceof GetCompetitorInfoTask) {
			mArrayCometitorInfo = (ArrayList<ChlBaseInfo>) param;
			refreshOverlayLocal();
		} else if (task instanceof GetMobPictureTask) {
			Bitmap bitmap = (Bitmap) param;// BitmapFactory.decodeFile(iconurl);
			if (bitmap != null) {
				mIVHall.setImageBitmap(bitmap);
				mIVHall.setVisibility(View.VISIBLE);
			}
		}  else if (task instanceof GetAppQuotaAllCity) {
			getPreferences().putCacheString(KEY_SHARE_OF_CHANNEL_ALL_CITYS, param.toString());
		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
		mArrayTask.remove(task);
	}

	/** 百度地图线路搜索 */

	/** 线路信息 */

	private ArrayList<RouteLine<?>> mRouteLines;

	/**
	 * @param isShowLine
	 *            true显示线路
	 * @param isShowSpinner
	 *            是否显示选择器
	 */
	private void setWhenShowLineState(boolean isShowLine, boolean isShowSpinner) {
		if (isShowLine == mIsShowLine)
			return;

		mIsShowLine = isShowLine;
		if (isShowLine) {// 显示路线
			if (isShowSpinner)
				mSpinnerLines.setVisibility(View.VISIBLE);
			mSearcherBar.setVisibility(View.GONE);
			hideInfoWindow();
			mBaiduMap.clear();
		} else {// 还原
			refreshOverlayLocal();
			mSpinnerLines.setVisibility(View.GONE);
			mSearcherBar.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 绘制路径
	 * 
	 * @param i
	 */
	@SuppressWarnings("unchecked")
	private void drawRouteLine(RouteLine line) {

		mBaiduMap.clear();
		int radius = UnitUtils.dip2px(mBaseActivity, 4);

		if (mStartMarker == null)
			mStartMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_start);
		baiduMapUtils.addMarker2Map(mBaiduMap, line.getStarting().getTitle(), line.getStarting().getLocation(), mStartMarker, MarkerAnimateType.grow, null);
		if (!TextUtils.isEmpty(line.getStarting().getTitle()))
			baiduMapUtils.addText2Map(mBaiduMap, line.getStarting().getTitle(), line.getStarting().getLocation(), UnitUtils.sp2px(mBaseActivity, 10),
					0x77eeaa44, Color.BLACK, radius);

		if (mEndMarker == null)
			mEndMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_end);
		baiduMapUtils.addMarker2Map(mBaiduMap, line.getTerminal().getTitle(), line.getTerminal().getLocation(), mEndMarker, MarkerAnimateType.grow, null);
		if (!TextUtils.isEmpty(line.getTerminal().getTitle()))
			baiduMapUtils.addText2Map(mBaiduMap, line.getTerminal().getTitle(), line.getTerminal().getLocation(), UnitUtils.sp2px(mBaseActivity, 10),
					0x7744aaee, Color.BLACK, radius);

		ArrayList<LatLng> points = new ArrayList<LatLng>();
		points.add(line.getStarting().getLocation());
		List<RouteStep> allStep = line.getAllStep();
		LayoutInflater inflater = LayoutInflater.from(mBaseActivity);
		for (RouteStep step : allStep) {

			TextView textview = (TextView) inflater.inflate(R.layout.simple_list_item, null, false);
			textview.setBackgroundResource(R.drawable.shape_white_bg);
			RouteNode startNote = null;
			String instruction = null;
			if (step instanceof TransitStep) {// 公交
				TransitStep s = (TransitStep) step;
				instruction = s.getInstructions();
				startNote = s.getEntrance();
			} else if (step instanceof DrivingStep) {
				DrivingStep s = (DrivingStep) step;
				startNote = s.getEntrance();
				instruction = s.getInstructions();
			} else if (step instanceof WalkingStep) {
				WalkingStep s = (WalkingStep) step;
				startNote = s.getEntrance();
				instruction = s.getInstructions();
			}
			baiduMapUtils.addMarker2Map(mBaiduMap, instruction, startNote.getLocation(), mDBRouteLineStep, MarkerAnimateType.grow, null, 0.5f, 0.5f, 0.5f);
			points.addAll(step.getWayPoints());// 点元素
		}

		points.add(line.getTerminal().getLocation());

		float[] hsv = { (float) Math.random() * 255, 1, 1 };

		baiduMapUtils.drawLine(mBaiduMap, 15, Color.HSVToColor(hsv), points);
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		LogUtlis.e("步行建议", arg0.toString());
		dismissDialog();
		if (arg0.getRouteLines() == null || arg0.getRouteLines().isEmpty())
			return;

		getRouteLines().clear();
		getRouteLines().addAll(arg0.getRouteLines());

		gotoLineShowState();
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		dismissDialog();
		LogUtlis.e("公交建议", arg0.toString());
		if (arg0 == null || arg0.getRouteLines() == null || arg0.getRouteLines().isEmpty()) {
			Toast.makeText(mBaseActivity, "没有查询到公交线路……", Toast.LENGTH_SHORT).show();
			return;
		}

		getRouteLines().clear();
		getRouteLines().addAll(arg0.getRouteLines());

		gotoLineShowState();

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		dismissDialog();
		LogUtlis.e("驾驶建议", arg0.toString());
		if (arg0.getRouteLines() == null || arg0.getRouteLines().isEmpty())
			return;

		getRouteLines().clear();
		getRouteLines().addAll(arg0.getRouteLines());

		gotoLineShowState();
	}

	private void gotoLineShowState() {
		boolean isNeedShowSpinner = getRouteLines().size() > 1;// 线路是否大于一条
		setWhenShowLineState(true, isNeedShowSpinner);
		if (isNeedShowSpinner) {// 如果大于一条，显示spinner
			refreshSpinnerSwitchLine();
			mSpinnerLines.setSelection(0, true);
		} else
			drawRouteLine(getRouteLines().get(0));
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		switch (arg0.getId()) {
		case R.id.spinner_lines:
			drawRouteLine(getRouteLines().get(position));
			break;
		case R.id.spinner_trigger:
			if (mArrMobileBusinessHalls.isEmpty())
				break;
			if (position == 0) {
				mArrIndexsort = null;
				refreshOverlayLocal();
			} else {
				doGetGroupIndexsort(mArrMobileBusinessHalls);
			}
			break;
		case R.id.spinner_indexsort:
			doMoveToPointMarker(position);
			break;
		default:
			break;
		}
	}

	/**
	 * 前往指定Marker坐标点
	 * 
	 * @param position
	 */
	private void doMoveToPointMarker(int position) {

		MobileBusinessHall item = mArrIndexsort.get(position);
		for (MobileBusinessHall hall : mArrMobileBusinessHalls) {
			if (hall.getGROUP_ID().equals(item.getGROUP_ID())) {
				if (hall.getLATITUDE() == 0 || hall.getLONGITUDE() == 0)
					break;
				showMobileBusinessHallDetail(hall);
				break;
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	/*** 刷新线路选择下拉菜单 */
	private void refreshSpinnerSwitchLine() {
		mSpinnerLines.setAdapter(new ArrayAdapter<String>(mBaseActivity, R.layout.simple_list_item, baiduMapUtils.getAllRoutesTitle(getRouteLines())));
	}

	private ArrayList<RouteLine<?>> getRouteLines() {
		if (mRouteLines == null)
			mRouteLines = new ArrayList<RouteLine<?>>();
		return mRouteLines;
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		String detail = arg0.getAddress() + ": {" + arg0.getLocation().latitude + "," + arg0.getLocation().longitude;
		Log.i("地图GEO查询", detail);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		String detail = arg0.getAddressDetail().city + " , " + arg0.getAddressDetail().province + "," + arg0.getAddressDetail().district + ","
				+ arg0.getAddressDetail().street;
		Log.i("地图反向查询", arg0.getAddress() + "\n" + arg0.getBusinessCircle() + "\n" + detail);
	}

	/**
	 * 启动查询kpi指标接口
	 * 
	 * @param result
	 */
	private void doGetGroupIndexsort(ArrayList<MobileBusinessHall> result) {
		if (mSpinner_Trigger == null)
			return;

		int index = mSpinner_Trigger.getSelectedItemPosition();
		new GetGroupIndexsortTask().execute("1", getResources().getStringArray(R.array.indexsortValue)[index], result, this);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.check_mapState_SATELLITE:
			if (mBaiduMap != null) {
				baiduMapUtils.setShowSatellite(mBaiduMap, arg1);
			}
			break;
		case R.id.check_showMapPoi:
			if (mBaiduMap != null)
				mBaiduMap.showMapPoi(arg1);
			break;
		case R.id.check_liantong:
		case R.id.check_dianxin:
			if (mArrayCometitorInfo == null || mArrayCometitorInfo.isEmpty())
				prepareCompetitorData();
			else
				refreshOverlayLocal();
			break;
		case R.id.check_cnmobile:
			refreshOverlayLocal();
			break;
		default:
			break;
		}
	}

	private void showCompetitorDetail(int index) {
		mMapView.setVisibility(View.VISIBLE);

		final ChlBaseInfo tempHall = mArrayCometitorInfo.get(index);
		final LatLng pt = new LatLng(tempHall.getLatitudeDouble(), tempHall.getLongitudeDouble());
		mViewCache.findViewById(R.id.btn_bus).setTag(pt);
		mViewCache.findViewById(R.id.btn_bike).setTag(pt);
		mViewCache.findViewById(R.id.btn_car).setTag(pt);
		mViewCache.findViewById(R.id.btn_sign).setVisibility(View.GONE);
		mPointLatLngName.setTag(tempHall);
		mPointLatLngText.setTag(tempHall);
		mBtnDetail.setTag(tempHall);
		mPointLatLngName.setText(tempHall.getChlName());

		{// 显示距离和签到
			double distance = BaiduMapUtils.getDistance(mLocData, pt);

			// 如果没有获取到地址，仅显示距离。并且启动详情查找
			if (TextUtils.isEmpty(tempHall.getAddress())) {
				mPointLatLngText.setText(String.format("(%.0f米)", distance));
			} else
				mPointLatLngText.setText(String.format("%s(%.0f米)", tempHall.getAddress(), distance));
		}
		{
			String url = baiduMapUtils.getPanoramaPictureUrl(mIVHall.getWidth(), mIVHall.getHeight(), pt, 0);
			mIVHall.setImageBitmap(BitmapFactory.decodeFile(url));
		}
		{
			mIVHall.setVisibility(View.GONE);
		}

		InfoWindow mInfoWindow = new InfoWindow(mViewCache, pt, 0);

		mBaiduMap.showInfoWindow(mInfoWindow);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(pt));

	}

	/** 缓存数据和坐标 */
	private void doCatheState() {
		if (mArrMobileBusinessHalls == null)
			return;

		if (!mArrMobileBusinessHalls.isEmpty())
			getPreferences().putCacheString(KEY_CACHE_HALL_NEARBY_LATLNG, JsonUtils.writeObjectToJsonStr(mArrMobileBusinessHalls));
		if (mLocData != null) {
			getPreferences().putCacheString(KEY_MY_LOCATION_LATITUDE, String.valueOf(mLocData.latitude));
			getPreferences().putCacheString(KEY_MY_LOCATION_LONGITUDE, String.valueOf(mLocData.longitude));
		}
		if (mCity != null)
			getPreferences().putCacheString(KEY_CITY, mCity);
	}

	/** 获取渠道份额 */
	@SuppressLint("DefaultLocale")
	private void doGetAppQuotaAllCity() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) - 1;
		if (month < 0) {
			month += 12;
			year--;
		}
		String endMonth = String.format("%04d%02d", year, month + 1);
		month -= 2;
		if (month < 0) {
			month += 12;
			year--;
		}
		String startMonth = String.format("%04d%02d", year, month + 1);
		new GetAppQuotaAllCity().execute(startMonth, endMonth, this);
	}

	/**
	 * 
	 * @param string
	 */
	private ArrayList<ShareOfChannels> getRateChannelsOfThisCity(String content, String city) {
		if (city == null || content == null) {
			return null;
		}

		ArrayList<ShareOfChannels> result = null;

		try {
			JSONArray jsonArray = new JSONArray(content);

			for (int i = 0; i < jsonArray.length(); i++) {
				ShareOfChannels item = JsonUtils.parseJsonStrToObject(jsonArray.getString(i), ShareOfChannels.class);
				if (city.startsWith(item.getCITY_NAME())) {
					if (result == null)
						result = new ArrayList<ShareOfChannels>();

					result.add(item);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void showRateOfChannel(View v) {
		if (mPopupWindowRate == null) {
			mPopupWindowRate = new PopupWindowPieChartViewForRateOfChannels(mBaseActivity);
		}
		mPopupWindowRate.setContent2(getRateChannelsOfThisCity(getPreferences().getCacheString(KEY_SHARE_OF_CHANNEL_ALL_CITYS), mCity));
		mPopupWindowRate.showAsDropDown(v);
	}
}
