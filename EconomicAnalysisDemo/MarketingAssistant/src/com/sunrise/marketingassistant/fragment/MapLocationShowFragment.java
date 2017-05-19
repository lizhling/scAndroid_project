package com.sunrise.marketingassistant.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CheckBox;
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
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
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
import com.sunrise.javascript.utils.DateUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.javascript.utils.UnitUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.task.DoMobRegisteTask;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.MobInfoByGroupIdTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.BaiduMapUtils;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.utils.HardwareUtils;

@SuppressWarnings("rawtypes")
@SuppressLint({ "HandlerLeak", "InflateParams" })
public class MapLocationShowFragment extends BaseFragment implements OnClickListener, BDLocationListener, TaskListener, ExtraKeyConstant,
		OnGetRoutePlanResultListener, OnItemSelectedListener, OnGetGeoCoderResultListener, OnCheckedChangeListener, OnMapStatusChangeListener,
		OnMarkerClickListener {

	private SDKReceiver mReceiver;

	/** 定位相关 */
	private LatLng mLocData = null;

	private GenericTask mTask;

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
	/** 较远网点 **/
	private BitmapDescriptor mFarAwayMarker;

	private BitmapDescriptor[] mBD_TOP10;

	/** 起点 **/
	private BitmapDescriptor mStartMarker;
	/** 终点 **/
	private BitmapDescriptor mEndMarker;

	private BitmapDescriptor mDBRouteLineStep;

	/** 百度地图动画图标 */
	private ArrayList<BitmapDescriptor> mGiflistOfMarker;

	/** 当前选中marker对象的序列 */
	private int mIndexTemp = -1;
	/** 营业厅信息 */
	private ArrayList<MobileBusinessHall> mArrMobileBusinessHalls = new ArrayList<MobileBusinessHall>();

	/** marker点的地址信息 */
	private TextView mPointLatLngText;
	/** marker点的签到记录 */
	private TextView mTVRegisterRecode;
	/**
	 * marker点的名称
	 */
	private TextView mPointLatLngName;
	private ExecutorService mPool;
	private ImageView mBtnFindMe;

	private BaiduMapUtils baiduMapUtils;

	/** 图标 */
	private ImageView mViewHall;

	/** 线路选择 */
	private Spinner mSpinnerLines;

	/*** 所在城市 */
	private String mCity;

	/**
	 * 指标切换漂浮窗
	 */
	private PopupWindow mPopupWindowDrawer;

	private ArrayList<RegisteTrajectoryBeen> mArrayTrajectory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mEnableMarker = BitmapDescriptorFactory.fromResource(R.drawable.pop_map);
		mFarAwayMarker = BitmapDescriptorFactory.fromResource(R.drawable.address_icon);
		mUnableMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		mDBRouteLineStep = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_place);
		mBD_TOP10 = new BitmapDescriptor[RES_ID_MARKER_TOP_10.length];
		for (int i = 0; i < RES_ID_MARKER_TOP_10.length; ++i)
			mBD_TOP10[i] = BitmapDescriptorFactory.fromResource(RES_ID_MARKER_TOP_10[i]);

		mGiflistOfMarker = new ArrayList<BitmapDescriptor>();
		mGiflistOfMarker.add(mEnableMarker);
		mGiflistOfMarker.add(mUnableMarker);

		mPool = Executors.newFixedThreadPool(4);
		baiduMapUtils = new BaiduMapUtils(mBaseActivity);

		mArrMobileBusinessHalls = getArguments().getParcelableArrayList(Intent.EXTRA_DATA_REMOVED);
		if (mArrMobileBusinessHalls == null) {
			Toast.makeText(mBaseActivity, "没有数据传入", Toast.LENGTH_SHORT).show();
			mBaseActivity.finish();
		}

		mIndexTemp = getArguments().getInt(Intent.EXTRA_UID, mIndexTemp);
	}

	public void onStart() {
		super.onStart();
		registBaiduSDKReceiver();
		initLocation();

		refreshOverlayLocal();
	}

	public void onStop() {

		if (baiduMapUtils != null) {
			baiduMapUtils.stopLocationListener();
			baiduMapUtils.stopSearch();
		}

		// 取消监听 SDK 广播
		mBaseActivity.unregisterReceiver(mReceiver);

		cancleTask();

		super.onStop();
	}

	public boolean onBackPressed() {
		if (mIsShowLine) {
			mBaiduMap.clear();
			setWhenShowLineState(!mIsShowLine);
			return true;
		}

		return false;
	}

	private void cancleTask() {
		if (mTask != null)
			mTask.cancle();
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
		mPointLatLngName.setOnClickListener(this);
		mPointLatLngText.setOnClickListener(this);

		mTVRegisterRecode = (TextView) mViewCache.findViewById(R.id.registerRecordes);

		mViewCache.findViewById(R.id.btn_sign).setOnClickListener(this);

		mBaiduMap.setOnMarkerClickListener(this);
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
				baiduMapUtils.startReverseGeoCode(arg0, MapLocationShowFragment.this);
			}
		});

		mViewHall = (ImageView) mViewCache.findViewById(R.id.image_hall);

	}

	/**
	 * 功能： 初始化定位
	 */
	private void initLocation() {

		// 设置定位数据
		if (mLocData == null) {
			mLocData = new LatLng(30.65642060414003, 104.06552229267588);
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(mLocData));
		}
		// 定位初始化
		baiduMapUtils.startLocationListener(this);
	}

	private void initMapView(View view) {
		// 地图初始化
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.showMapPoi(true);// 将底图标注设置为隐藏
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
	}

	private void refreshOverlayLocal() {

		mBaiduMap.clear();

		refreshAllBusinessHall(mArrMobileBusinessHalls);
	}

	/** 展示营业厅 */
	private void refreshAllBusinessHall(ArrayList<MobileBusinessHall> result) {

		for (int i = 0; i < result.size(); i++) {
			MobileBusinessHall hall = result.get(i);
			if (hall.getLATITUDE() == 0 || hall.getLONGITUDE() == 0)
				continue;

			LatLng coordinate = new LatLng(hall.getLATITUDE(), hall.getLONGITUDE());

			baiduMapUtils.addMarker2Map(mBaiduMap, hall.getGROUP_NAME(), coordinate, i < mBD_TOP10.length ? mBD_TOP10[i] : mUnableMarker,
					MarkerAnimateType.none, null);
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
		// mPoiSearch.destroy();
		if (mBaiduMap != null) {
			mBaiduMap.setMyLocationEnabled(false);
			mMapView.onDestroy();
			mMapView = null;
		}

		if (mPool != null && !mPool.isTerminated() && !mPool.isShutdown())
			mPool.shutdown();

		super.onDestroy();
	}

	private void doGetGroupInfo(String groupId) {

		if (mTask != null)
			mTask.cancle();
		mTask = new MobInfoByGroupIdTask().execute(getPreferences().getMobile(), groupId, getPreferences().getSubAccount(), this);
	}

	private void showMobileBusinessHallDetail(MobileBusinessHall hall) {

		LatLng pt = new LatLng(hall.getLATITUDE(), hall.getLONGITUDE());

		mPointLatLngName.setText(hall.getGROUP_NAME());
		mViewCache.findViewById(R.id.btn_bus).setTag(pt);
		mViewCache.findViewById(R.id.btn_bike).setTag(pt);
		mViewCache.findViewById(R.id.btn_car).setTag(pt);

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
			String url = baiduMapUtils.getPanoramaPictureUrl(mViewHall.getWidth(), mViewHall.getHeight(), pt, 0);
			mViewHall.setImageBitmap(BitmapFactory.decodeFile(url));
		}
		{
			mViewHall.setVisibility(View.GONE);
			String iconurl = hall.getIMG_INFO();
			if (!TextUtils.isEmpty(iconurl)) {
				new BitmapFactory();
				Bitmap bitmap = BitmapFactory.decodeFile(iconurl);
				if (bitmap != null) {
					mViewHall.setImageBitmap(bitmap);
					mViewHall.setVisibility(View.VISIBLE);
				}
			}
		}
		// 签到记录显示
		if (hall.getREGISTIME() != null) {
			((View) mTVRegisterRecode.getParent()).setVisibility(View.VISIBLE);
			mTVRegisterRecode.setText(hall.getREGISTIME());
		} else {
			mTVRegisterRecode.setText(null);
			((View) mTVRegisterRecode.getParent()).setVisibility(View.GONE);
		}

		InfoWindow mInfoWindow = new InfoWindow(mViewCache, pt, 0);

		mBaiduMap.showInfoWindow(mInfoWindow);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(pt));

	}

	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_map_location, null, false);

		CheckBox mCheckMapPoi = (CheckBox) view.findViewById(R.id.check_showMapPoi);
		mCheckMapPoi.setOnCheckedChangeListener(this);
		CheckBox mCheckSatellite = (CheckBox) view.findViewById(R.id.check_mapState_SATELLITE);
		mCheckSatellite.setOnCheckedChangeListener(this);

		view.findViewById(R.id.imageButton1).setOnClickListener(this);

		mBtnFindMe = (ImageView) view.findViewById(R.id.btn_myLocation);

		initMapView(view);

		mSpinnerLines = (Spinner) view.findViewById(R.id.spinner_lines);
		mSpinnerLines.setOnItemSelectedListener(this);

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
		case R.id.btn_drawer_hide:
			hideOperationPanel();
			break;
		case R.id.btn_sign:// 签到
			doSignIn();
			break;
		case R.id.address_name:// 详情界面
		case R.id.address_location:// 详情界面
			gotoHallDetail();
			break;
		case R.id.imageButton1:
			doReviewHistory();
			break;
		default:
			break;
		}
	}

	/** 前往网点详情 */
	private void gotoHallDetail() {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_DATA_REMOVED, mArrMobileBusinessHalls.get(mIndexTemp));
		mBaseActivity.setResult(Activity.RESULT_OK, intent);
		mBaseActivity.finish();
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

	private void doReviewHistory() {
		if (mArrayTrajectory == null)
			mArrayTrajectory = doCreateTrajectoryArray();

		mBaiduMap.setOnMapStatusChangeListener(this);
		mBaiduMap.setOnMarkerClickListener(null);

		mIndexTemp = 0;
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(mArrayTrajectory.get(mIndexTemp).latLng, 17));
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
		if (mTask != null)
			mTask.cancle();
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
		if (mLatlonAction == LATLON_ACTION_FIRST) {
			mBtnFindMe.setOnClickListener(this);
//			if (TextUtils.isEmpty(mCity)) {
//				baiduMapUtils.checkOfflineUpdate(mCity, null);
//				baiduMapUtils.startLoadOfflineMap(mCity, null);
//			}
			// TODO
			if (mIndexTemp == -1)
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));
			else {
				MobileBusinessHall temp = mArrMobileBusinessHalls.get(mIndexTemp);
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17)
						.target(new LatLng(temp.getLATITUDE(), temp.getLONGITUDE())).build()));
			}
		} else {
			((AnimationDrawable) mBtnFindMe.getDrawable()).stop();
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(mLocData));
		}

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
		if (task instanceof DoMobRegisteTask) {
			initDialog();
			showDialog(mArrMobileBusinessHalls.get(mIndexTemp).getGROUP_NAME() + " 签到过程中……");
		}

	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();
		if (result != TaskResult.OK)
			Toast.makeText(mBaseActivity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		if (param == null) {
			Toast.makeText(mBaseActivity, "未找到", Toast.LENGTH_LONG).show();
			return;
		}

		if (task instanceof MobInfoByGroupIdTask) {
			// TODO
			if (mIndexTemp >= 0) {
				MobileBusinessHall tempHall = mArrMobileBusinessHalls.get(mIndexTemp);
				MobileBusinessHall hall = (MobileBusinessHall) param;
				tempHall.setACTIVE_TIME(hall.getACTIVE_TIME());
				tempHall.setCLASS_CODE(hall.getCLASS_CODE());
				tempHall.setCLASS_NAME(hall.getCLASS_NAME());
				tempHall.setCONTACT_PERSON(hall.getCONTACT_PERSON());
				tempHall.setCONTACT_PHONE(hall.getCONTACT_PHONE());
				tempHall.setGRADE_CODE(hall.getGRADE_CODE());
				tempHall.setGROUP_ADDRESS(hall.getGROUP_ADDRESS());
				tempHall.setGROUP_AREA(hall.getGROUP_AREA());
				tempHall.setIMG_INFO(hall.getIMG_INFO());
				tempHall.setRWD_TOTAL(hall.getRWD_TOTAL());

				final LatLng pt = new LatLng(tempHall.getLATITUDE(), tempHall.getLONGITUDE());
				double distance = BaiduMapUtils.getDistance(mLocData, pt);
				mPointLatLngText.setText(String.format("%s(%.0f米)", tempHall.getGROUP_ADDRESS(), distance));
			}

		}

		else if (task instanceof DoMobRegisteTask)
			CommUtil.showAlert(mBaseActivity, null, mArrMobileBusinessHalls.get(mIndexTemp).getGROUP_NAME() + "\n" + param.toString(), null, false, null);

	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	/** 百度地图线路搜索 */

	/** 线路信息 */

	private ArrayList<RouteLine<?>> mRouteLines;

	/**
	 * @param isShowLine
	 *            true显示线路
	 */
	private void setWhenShowLineState(boolean isShowLine) {
		if (isShowLine == mIsShowLine)
			return;

		if (isShowLine) {// 显示路线
			mSpinnerLines.setVisibility(View.VISIBLE);
			hideInfoWindow();
			mBaiduMap.clear();
		} else {// 还原
			refreshOverlayLocal();
			mSpinnerLines.setVisibility(View.GONE);
		}
		mIsShowLine = isShowLine;
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
		dismissDialog();
		LogUtlis.e("步行建议", arg0.toString());
		if (arg0.getRouteLines() == null || arg0.getRouteLines().isEmpty())
			return;

		getRouteLines().clear();
		getRouteLines().addAll(arg0.getRouteLines());

		refreshSpinnerSwitchLine();
		setWhenShowLineState(true);

		mSpinnerLines.setSelection(0, true);
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
		refreshSpinnerSwitchLine();
		setWhenShowLineState(true);
		mSpinnerLines.setSelection(0, true);

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		dismissDialog();
		LogUtlis.e("驾驶建议", arg0.toString());
		if (arg0.getRouteLines() == null || arg0.getRouteLines().isEmpty())
			return;

		getRouteLines().clear();
		getRouteLines().addAll(arg0.getRouteLines());
		refreshSpinnerSwitchLine();
		setWhenShowLineState(true);
		mSpinnerLines.setSelection(0, true);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		switch (arg0.getId()) {
		case R.id.spinner_lines:
			drawRouteLine(getRouteLines().get(position));
			break;
		default:
			break;
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
		default:
			break;
		}
	}

	/** @return 创建轨迹对象列表，并排序 */
	private ArrayList<RegisteTrajectoryBeen> doCreateTrajectoryArray() {
		ArrayList<RegisteTrajectoryBeen> array = new ArrayList<RegisteTrajectoryBeen>();
		// 创建
		for (MobileBusinessHall hall : mArrMobileBusinessHalls) {
			String temp = hall.getREGISTIME();
			if (TextUtils.isEmpty(temp))
				continue;

			LatLng latlng = new LatLng(hall.getLATITUDE(), hall.getLONGITUDE());

			String[] tempArray = temp.split("\n");
			for (int i = 0; i < tempArray.length; i++) {
				RegisteTrajectoryBeen been = new RegisteTrajectoryBeen();
				been.latLng = latlng;
				try {
					been.time = DateUtils.string2Long(tempArray[i], FORMAT_PARAM_TIME);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				been.name = hall.getGROUP_NAME();

				array.add(been);
			}
		}

		// 排序
		for (int i = 0, min = 0; i < array.size() - 1; i++) {
			min = i;
			for (int j = i + 1; j < array.size(); j++) {
				RegisteTrajectoryBeen a = array.get(min);
				RegisteTrajectoryBeen b = array.get(j);
				if (a.time > b.time)
					min = j;
			}
			if (min != i)
				array.set(i, array.remove(min));
		}
		return array;
	}

	private class RegisteTrajectoryBeen {
		LatLng latLng;
		long time;
		String name;
	}

	@Override
	public void onMapStatusChangeStart(MapStatus arg0) {

	}

	@Override
	public void onMapStatusChangeFinish(MapStatus arg0) {

		// if (!arg0.target.equals(mArrayTrajectory.get(mIndexTemp).latLng)) {
		// mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(mLocData,
		// 17));
		// return;
		// }

		if (++mIndexTemp < mArrayTrajectory.size()) {
			TextView textView = (TextView) LayoutInflater.from(mBaseActivity).inflate(R.layout.simple_list_item, null);
			textView.setMaxEms(6);
			textView.setText(mArrayTrajectory.get(mIndexTemp).name + "\n" + DateUtils.formatlong2Time(mArrayTrajectory.get(mIndexTemp).time, FORMAT_PARAM_TIME));
			textView.setBackgroundResource(R.drawable.bg_map_info);
			baiduMapUtils.addInfoWindow(mBaiduMap, textView, mArrayTrajectory.get(mIndexTemp).latLng);

			ArrayList<LatLng> points = new ArrayList<LatLng>();
			points.add(mArrayTrajectory.get(mIndexTemp - 1).latLng);
			points.add(mArrayTrajectory.get(mIndexTemp).latLng);
			float[] hsv = { (float) Math.random() * 255, 1, 1 };
			baiduMapUtils.drawLine(mBaiduMap, 15, Color.HSVToColor(hsv), points);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(mArrayTrajectory.get(mIndexTemp).latLng, 17), 4000);

		} else {// 结束
			mBaiduMap.setOnMarkerClickListener(this);
			mBaiduMap.setOnMapStatusChangeListener(null);
			refreshOverlayLocal();
			Toast.makeText(mBaseActivity, "轨迹播放结束", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onMapStatusChange(MapStatus arg0) {

	}

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

		for (int i = 0; i < mArrMobileBusinessHalls.size(); ++i)
			if (mArrMobileBusinessHalls.get(i).getGROUP_NAME() == marker.getTitle()) {
				mIndexTemp = i;
				showMobileBusinessHallDetail(mArrMobileBusinessHalls.get(i));
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(marker.getPosition()));
				break;
			}
		return true;
	}
}
