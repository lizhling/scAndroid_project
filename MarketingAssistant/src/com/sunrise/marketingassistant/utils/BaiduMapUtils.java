package com.sunrise.marketingassistant.utils;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.favorite.FavoriteManager;
import com.baidu.mapapi.favorite.FavoritePoiInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.VehicleInfo;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.sunrise.marketingassistant.task.BaiduUpdateOfflineMap;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public class BaiduMapUtils {
	private LocationClient mLocClient;
	private Context mContext;
	private BDLocationListener mLocationListener;
	private MKOfflineMap mOfflineMap;

	public BaiduMapUtils(Context context) {
		mContext = context;
	}

	public LocationClient startLocationListener(BDLocationListener bdlocationListener) {
		mLocationListener = bdlocationListener;

		// 定位初始化
		if (mLocClient == null) {
			mLocClient = new LocationClient(mContext);
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);

			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(1000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
			// option.setIsNeedLocationDescribe(true);//
			// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
			// option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
			option.setIsNeedAddress(true);//
			mLocClient.registerLocationListener(bdlocationListener);
			mLocClient.setLocOption(option);
		}
		mLocClient.start();

		return mLocClient;
	}

	public void stopLocationListener() {
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(mLocationListener);
			mLocClient.registerNotifyLocationListener(null);
			mLocClient.stop();
		}
	}

	/**
	 * @param p1
	 * @param p2
	 * @return 计算p1、p2两点之间的直线距离，单位：米
	 */
	public static double getDistance(LatLng p1, LatLng p2) {
		return DistanceUtil.getDistance(p1, p2);
	}

	public static boolean isCircalContainsPoint(LatLng pCenter, int radius, LatLng pt) {
		// 判断点pt是否在，以pCenter为中心点，radius为半径的圆内。
		return SpatialRelationUtil.isCircleContainsPoint(pCenter, radius, pt);
	}

	/**
	 * 显示步行路线
	 * 
	 * @param p1
	 * @param p2
	 */
	public void openBaiduMapWalkingRoute(LatLng p1, LatLng p2) {
		RouteParaOption para = new RouteParaOption().startPoint(p1).endPoint(p2).busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.openBaiduMapWalkingRoute(para, mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 结束调启功能时调用finish方法以释放相关资源
		BaiduMapRoutePlan.finish(mContext);
	}

	public void testFavorate(LatLng p1) {
		// 在使用之前必须调用初始化方法
		FavoriteManager.getInstance().init();
		// 构造一个点信息，pt和poiName是必填项
		FavoritePoiInfo info = new FavoritePoiInfo().poiName("test").pt(p1);
		// 添加到收藏夹,info既是输入也是输出，输出时携带id和时间戳
		FavoriteManager.getInstance().add(info);

		// 删除
		FavoriteManager.getInstance().deleteFavPoi(info.getID());

		// 更新，修改info的属性
		FavoriteManager.getInstance().updateFavPoi(info.getID(), info);

		// 通过id获取某个点的信息
		FavoriteManager.getInstance().getFavPoi(info.getID());

		// 获取全部点
		FavoriteManager.getInstance().getAllFavPois();

		// 清空所有数据
		FavoriteManager.getInstance().clearAllFavPois();

		// 不用时销毁对象释放内存
		FavoriteManager.getInstance().destroy();
	}

	/**
	 * @param width
	 * @param height
	 * @param latlng
	 * @param fov
	 * @param apikey
	 * @return 获取百度全景图
	 */
	@SuppressLint("DefaultLocale")
	public String getPanoramaPictureUrl(int width, int height, LatLng latlng, int fov) {
		return String.format("http://api.map.baidu.com/panorama?width=%d&height=%d&location=%f,%f&fov=%d&ak=%s", width, height, latlng.longitude,
				latlng.latitude, fov, "sVZhSz2YMDPdHrkrDY7vdPey");
	}

	/**
	 * @param latlng
	 * @param width
	 * @param height
	 * @param zoom
	 * @return 获取百度静态图
	 */
	public String getStaticImage(LatLng latlng, int width, int height, int zoom) {
		return String.format("http://api.map.baidu.com/staticimage/v2?ak=sVZhSz2YMDPdHrkrDY7vdPey&mcode=666666&center=%f,%f&width=%d&height=%d&zoom=%d",
				latlng.longitude, latlng.latitude, width, height, zoom);
	}

	public void checkOfflineUpdate(String city, MKOfflineMapListener listener) {
		if (mOfflineMap == null)
			mOfflineMap = new MKOfflineMap();
		// 传入接口事件，离线地图更新会触发该回调
		mOfflineMap.init(listener);

		ArrayList<MKOLUpdateElement> allUpdateInfo = mOfflineMap.getAllUpdateInfo();
		if (allUpdateInfo != null && !allUpdateInfo.isEmpty())
			for (MKOLUpdateElement item : allUpdateInfo) {
				Log.e("升级信息", item.cityName + "," + item.cityID + "," + item.level + "," + item.size + "," + item.serversize);
			}

		ArrayList<MKOLSearchRecord> records = mOfflineMap.searchCity(city);
		if (records == null) {
			Log.e("升级离线地图城市列表", city + " 之下未查到信息！");
			return;
		}
		for (MKOLSearchRecord item : records) {
			showChildCities(mOfflineMap, city, item);
		}
	}

	private void showChildCities(MKOfflineMap offline, String parantName, MKOLSearchRecord item) {
		Log.e(parantName, item.cityName + "," + item.cityID + "," + item.cityType + "," + item.size + ",updateEnable：" + offline.update(item.cityID));
		ArrayList<MKOLSearchRecord> childs = item.childCities;
		if (childs != null)
			for (MKOLSearchRecord child : childs) {
				showChildCities(offline, item.cityName, child);
			}
	}

	/** 线路搜索 */
	private RoutePlanSearch mRoutePlanSearch;
	private BusLineSearch mBusLineSearch;

	// private OnGetRoutePlanResultListener listener;
	// /** 默认搜索监控 */
	// private final OnGetRoutePlanResultListener
	// DEFAULT_ON_GET_ROUTE_PLAN_RESULT_LISTENER = new
	// OnGetRoutePlanResultListener() {
	//
	// @Override
	// public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
	// if (listener != null)
	// listener.onGetWalkingRouteResult(arg0);
	// }
	//
	// @Override
	// public void onGetTransitRouteResult(TransitRouteResult arg0) {
	// if (listener != null)
	// listener.onGetTransitRouteResult(arg0);
	// }
	//
	// @Override
	// public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
	// if (listener != null)
	// listener.onGetDrivingRouteResult(arg0);
	// }
	// };
	//
	// private void initRoutePlanSearch() {
	// if (mRoutePlanSearch != null)
	// return;
	// mRoutePlanSearch = RoutePlanSearch.newInstance();
	// mRoutePlanSearch.setOnGetRoutePlanResultListener(DEFAULT_ON_GET_ROUTE_PLAN_RESULT_LISTENER);
	// }

	public void setRoutePlanSearchListener(OnGetRoutePlanResultListener listener) {
		if (mRoutePlanSearch == null)
			mRoutePlanSearch = RoutePlanSearch.newInstance();
		mRoutePlanSearch.setOnGetRoutePlanResultListener(listener);
	}

	/**
	 * 公交线路查询
	 * 
	 * @param city
	 * @param uid
	 * @param listener
	 */
	public void startBusPlanSearch(String city, String uid, OnGetBusLineSearchResultListener listener) {
		if (mBusLineSearch == null)
			mBusLineSearch = BusLineSearch.newInstance();

		mBusLineSearch.setOnGetBusLineSearchResultListener(listener);
		mBusLineSearch.searchBusLine(new BusLineSearchOption().city(city).uid(uid));
	}

	/***
	 * 换乘线路规划检索
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public void startTransitPlanSearch(String city, LatLng arg0, LatLng arg1) {
		if (mRoutePlanSearch == null)
			mRoutePlanSearch = RoutePlanSearch.newInstance();

		PlanNode stNode = PlanNode.withLocation(arg0);
		PlanNode enNode = PlanNode.withLocation(arg1);

		mRoutePlanSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).city(city).to(enNode));
	}

	private GeoCoder mGeoCoder;

	/**
	 * 反向查找地理编码
	 * 
	 * @param location
	 */
	public void startReverseGeoCode(LatLng location, OnGetGeoCoderResultListener listener) {
		if (mGeoCoder == null)
			mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(location));
		mGeoCoder.setOnGetGeoCodeResultListener(listener);
	}

	public void stopGeoCodeSearch() {
		if (mGeoCoder != null) {
			mGeoCoder.destroy();
			mGeoCoder = null;
		}
	}

	/**
	 * 驾车线路查询
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public void startDrivingPlanSearch(LatLng arg0, LatLng arg1) {
		if (mRoutePlanSearch == null)
			mRoutePlanSearch = RoutePlanSearch.newInstance();

		PlanNode stNode = PlanNode.withLocation(arg0);
		PlanNode enNode = PlanNode.withLocation(arg1);

		mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
	}

	public void startWalkingPlanSearch(LatLng arg0, LatLng arg1) {
		if (mRoutePlanSearch == null)
			mRoutePlanSearch = RoutePlanSearch.newInstance();

		PlanNode stNode = PlanNode.withLocation(arg0);
		PlanNode enNode = PlanNode.withLocation(arg1);

		mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
	}

	/** * 释放检索实例； */
	public void stopSearch() {
		if (mRoutePlanSearch != null) {
			mRoutePlanSearch.destroy();
			mRoutePlanSearch = null;
		}
		if (mBusLineSearch != null) {
			mBusLineSearch.destroy();
			mBusLineSearch = null;
		}
	}

	public void destory() {
		stopSearch();
		stopLocationListener();
		stopGeoCodeSearch();
		if (mOfflineMap != null)
			mOfflineMap.destroy();
	}

	/** 离线地图 */

	private MKOLUpdateElement updateInfo;

	public void startLoadOfflineMap(String city, MKOfflineMapListener listener) {
		MKOfflineMap mOfflineMap = new MKOfflineMap();
		// 传入接口事件，离线地图更新会触发该回调
		mOfflineMap.init(new MKOfflineMapListener() {

			@Override
			public void onGetOfflineMapState(int arg0, int arg1) {

			}
		});
		ArrayList<MKOLSearchRecord> records = mOfflineMap.searchCity(city);
		if (records == null || records.isEmpty())
			return;
		MKOLSearchRecord record = records.get(0);

		boolean isCanUpdate = true;
		ArrayList<MKOLUpdateElement> allUpdateInfo = mOfflineMap.getAllUpdateInfo();
		if (allUpdateInfo != null && !allUpdateInfo.isEmpty())
			for (MKOLUpdateElement info : allUpdateInfo) {
				if (info.cityID == record.cityID) {
					if (!info.update && info.status == MKOLUpdateElement.DOWNLOADING)
						isCanUpdate = false;
					else
						updateInfo = info;
				}
			}

		if (!isCanUpdate)
			return;

		new BaiduUpdateOfflineMap().execute(record.cityID, new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				Log.e("baidumap offline", "cityName:" + updateInfo.cityName + " ," + updateInfo.ratio + "/" + updateInfo.serversize);
			}

			@Override
			public void onPreExecute(GenericTask task) {

			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {

			}

			@Override
			public void onCancelled(GenericTask task) {

			}

			@Override
			public String getName() {
				return null;
			}
		});
	}

	/**
	 * 转化秒为习惯性文字时间
	 * 
	 * @param time
	 * @return
	 */
	public String turnDigist2Time(int time) {
		if (time < 60)
			return time + "秒";
		else if (time < 3600)
			return String.format("%d分%d秒", time / 60, time % 60);
		else
			return String.format("%d时%d分%d秒", time / 3600, time / 60 % 60, time % 60);
	}

	/**
	 * 将数字转化为具体距离文字
	 * 
	 * @param distance
	 * @return
	 */
	public String turnDigit2Distance(int distance) {
		if (distance < 1000)
			return distance + "米";
		else
			return String.format("%.1f公里", distance / 1000f);
	}

	/**
	 * 地图上增加marker点
	 * 
	 * @param baiduMap
	 * @param titleName
	 * @param latlng
	 * @param bitmapDes
	 * @param type
	 * @param arg0
	 */
	public void addMarker2Map(BaiduMap baiduMap, String titleName, LatLng latlng, BitmapDescriptor bitmapDes, MarkerAnimateType type, Bundle arg0) {
		addMarker2Map(baiduMap, titleName, latlng, bitmapDes, type, arg0, 0.5f, 1.0f, 1);
	}

	public void addMarker2Map(BaiduMap baiduMap, String titleName, LatLng latlng, BitmapDescriptor bitmapDes, MarkerAnimateType type, Bundle arg0, float offx,
			float offy, float alpha) {
		MarkerOptions overLayOptions = new MarkerOptions();
		overLayOptions.icon(bitmapDes);
		overLayOptions.position(latlng).title(titleName).zIndex(9).period(5).anchor(offx, offy);
		overLayOptions.animateType(type);
		overLayOptions.alpha(alpha);

		if (arg0 != null)
			overLayOptions.extraInfo(arg0);

		baiduMap.addOverlay(overLayOptions);
	}

	public void addText2Map(BaiduMap baiduMap, String titleName, LatLng latlng, int textSize, int bgcolor, int fontcolor, int radius) {
		TextOptions overLayOptions = new TextOptions();
		overLayOptions.bgColor(bgcolor);
		overLayOptions.fontSize(textSize);
		overLayOptions.position(latlng).text(titleName).align(TextOptions.ALIGN_LEFT, TextOptions.ALIGN_CENTER_VERTICAL);

		DotOptions dotOp = new DotOptions();
		dotOp.center(latlng).color(fontcolor).radius(radius);

		baiduMap.addOverlay(dotOp);
		baiduMap.addOverlay(overLayOptions);
	}

	public void addInfoWindow(BaiduMap baiduMap, View view, LatLng latlng) {
		InfoWindow mInfoWindow = new InfoWindow(view, latlng, 0);
		baiduMap.showInfoWindow(mInfoWindow);
	}

	public void setMapLocationMode(BaiduMap baidumap, com.baidu.mapapi.map.MyLocationConfiguration.LocationMode arg0) {
		baidumap.setMyLocationConfigeration(new MyLocationConfiguration(arg0, true, null));
	}

	public void setShowSatellite(BaiduMap baidumap, boolean arg1) {
		baidumap.setMapType(arg1 ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL);
	}

	public void showPolygon(BaiduMap baidumap, ArrayList<LatLng> pts) {
		// 构建用户绘制多边形的Option对象
		PolygonOptions polygonOption = new PolygonOptions().points(pts).stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
		// 在地图上添加多边形Option，用于显示
		baidumap.addOverlay(polygonOption);
	}

	/**
	 * 绘制路线
	 * 
	 * @param baidumap
	 * @param lineWidht
	 * @param color
	 * @param points
	 */
	public void drawLine(BaiduMap baidumap, int lineWidht, int color, List<LatLng> points) {
		// 构造对象
		PolylineOptions ooPolyline = new PolylineOptions().width(lineWidht).color(color).points(points).dottedLine(true);
		// 添加到地图
		baidumap.addOverlay(ooPolyline);
	}

	public ArrayList<String> getAllRoutesTitle(ArrayList<RouteLine<?>> aRL) {
		final String STR_LINE = "线路%i\n总路程:%s\n预计耗时:%s";
		ArrayList<String> array = new ArrayList<String>();
		for (int i = 0; i < aRL.size(); ++i) {
			RouteLine<?> item = aRL.get(i);

			StringBuilder sb = new StringBuilder();
			if (TextUtils.isEmpty(item.getTitle()))
				sb.append("线路").append(i + 1);
			else
				sb.append(item.getTitle());

			sb.append('\n');
			sb.append("总路程:");
			sb.append(turnDigit2Distance(item.getDistance()));
			sb.append('\n');

			sb.append("预计耗时:");
			sb.append(turnDigist2Time(item.getDuration()));

			array.add(sb.toString());
		}
		return array;
	}

	public String getStepInfo(RouteStep step) {
		if (step instanceof TransitStep) // 公交
			return getStepTitle((TransitStep) step);

		return null;
	}

	private String getStepTitle(TransitStep step) {
		TransitStep s = (TransitStep) step;
		VehicleInfo st = s.getVehicleInfo();
		if (st != null)
			return st.getTitle() + "\n所乘站数" + st.getPassStationNum();
		return null;
	}

	/**
	 * @param context
	 * @param startName
	 * @param startLatLng
	 * @param endName
	 * @param endLatLng
	 * @param type
	 *            0:开车；1:步行；2:公交
	 */
	public void viewMapTransitRoute(Context context, String startName, LatLng startLatLng, String endName, LatLng endLatLng, int type) {
		if (endLatLng == null || startLatLng == null)
			return;
		RouteParaOption para = new RouteParaOption().startName(startName).startPoint(startLatLng).endPoint(endLatLng).endName(endName);
		if (type == 0)
			BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, context);
		else if (type == 1)
			BaiduMapRoutePlan.openBaiduMapWalkingRoute(para, context);
		else
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, context);
		BaiduMapRoutePlan.finish(context);
	}
}
