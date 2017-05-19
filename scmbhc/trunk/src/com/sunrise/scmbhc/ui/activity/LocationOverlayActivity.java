package com.sunrise.scmbhc.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.utils.DistanceUtil;
import com.starcpt.analytics.PhoneClickAgent;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.BusinessHallsAdapter;
import com.sunrise.scmbhc.cache.preferences.Preferences;
import com.sunrise.scmbhc.entity.MobileBusinessHall;
import com.sunrise.scmbhc.entity.ReservationNumberReslut;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.ReservationNumberTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

@SuppressLint("HandlerLeak")
public class LocationOverlayActivity extends BaseActivity implements OnClickListener {
	public final static String SPLITSTR = "$";

	private SDKReceiver mReceiver;

	private Stack<ViewBean> mViewSack = new Stack<ViewBean>();
	private String SEARCH_KEY;
	private static final int MAX_SEARCH_RANGE = 5000;
	private static final int MIN_SEARCH_RANGE = 500;
	private static final String M = "m";
	private boolean isinit = false;
	// 定位相关
	private LocationClient mLocClient;
	private LatLng mLocData = null;
	private MyLocationListenner myListener = new MyLocationListenner();

	private GenericTask mTask;

	private BaiduMap mBaiduMap;// 百度地图

	// 弹出泡泡图层
	private View mViewCache = null;
	/**
	 * 营业厅信息
	 */
	private TextView mNameTextView = null;
	/**
	 * // 等候人数
	 */
	private TextView mWaitPeopleTextView = null;

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	private MapView mMapView = null; // 地图View
	private Button mLeftButton;
	private Button mRightButton;
	private TextView mTitleTextView;
	private boolean isFirstLoc = true;// 是否首次定位

	private BitmapDescriptor mEnableMarker;		// 可预约图标
	private BitmapDescriptor mUnableMarker;		// 不可预约图标
	
	private int mCurrentShowHallInfoIndex = -1;
	private MobileBusinessHall mCurrentMobileBusinessHall;
	/**
	 * 查找结果
	 */
	// private ArrayList<PoiInfo> mSearchMkPoiInfos = new ArrayList<PoiInfo>();
	/**
	 * 营业厅信息
	 */
	private ArrayList<MobileBusinessHall> mSearchMobileBusinessHalls = new ArrayList<MobileBusinessHall>();
	private ArrayList<MobileBusinessHall> mShowMobileBusinessHalls = new ArrayList<MobileBusinessHall>();
	private ListView mSearchBusinessHallReslut;
	private LinearLayout mSearchBusinessHallReslutPanel;
	private BusinessHallsAdapter mBusinessHallsAdapter;
	private TextView mBusinessHallNumberView;
	private LatLng mCenterPoint;
	private int mSearchRange = MAX_SEARCH_RANGE;
	private SeekBar mSearchRangeSeekBar;
	private TextView mSearchRangeTextView;
	private RelativeLayout mBusinessHallDetailPanel;
	private LinearLayout mWaitpeoplePanel;
	private TextView mNoWaitPeopleView;			// 地图标注无预约panel显示的信息
	private LinearLayout mNoWaitpeoplePanel;	// 地图标注无预约panel
	/**
	 * 营业厅详情--排队人数
	 */
	private TextView mHallDetailwaitPeoTextView;
	private LinearLayout mHaveReservationPanel;
	private TextView mHaveReservationHallName;
	private TextView mHaveReservationHallWaitPeople;

	private ExecutorService mPool;

	private boolean isStarted;

	private HashMap<String, String> mHashPointNum = new HashMap<String, String>();

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			if (!isStarted)
				return;

			if (msg.obj != null) {
				ArrayList<MobileBusinessHall> result = (ArrayList<MobileBusinessHall>) msg.obj;
				// 错误号可参考MKEvent中的定义
				if (result.size() == 0) {
					Toast.makeText(LocationOverlayActivity.this, R.string.not_found_result, Toast.LENGTH_LONG).show();
				} else {
					refreshOverlayLocal(result);
				}
			} else {
				Toast.makeText(LocationOverlayActivity.this, R.string.not_found_result, Toast.LENGTH_LONG).show();
			}
		}

	};

	/**
	 * 预约按钮
	 */
	private Button mReservationNumberButton;

	/**
	 * 定位按钮
	 */
	private ImageView mDetailArressFlag;

	/**
	 * 同步按钮
	 */
	private ImageView mBtnSync;

	private ReservationNumberTask mReservationNumberTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPool = Executors.newFixedThreadPool(4);
		SEARCH_KEY = getResources().getString(R.string.searchKey);

		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	public void onStart() {
		super.onStart();
		isStarted = true;
		if (App.sSettingsPreferences.isValidReservation()) {
			// 发送请求更新等待人数，并且读取保存的营业厅信息显示出来
			String hallnum = App.sSettingsPreferences.getString(Preferences.HALL_NUMBER, "");
			getWaitPeople(mHaveReservationHallWaitPeople, hallnum, true);
			mHaveReservationHallName.setText(App.sSettingsPreferences.getString(Preferences.HALL_NAME_POP));
			mHaveReservationPanel.setVisibility(View.VISIBLE);

		} else {
			// 如果已有预约营业厅信息预约时间小于今天 或当前时间大于20点 隐藏预约信息清除保存的预约信息
			App.sSettingsPreferences.clearReservationInfo();
			mHaveReservationPanel.setVisibility(View.GONE);
		}

	}

	public void onStop() {
		super.onStop();
		isStarted = false;
		mLocClient.unRegisterLocationListener(myListener);
		mLocClient.registerNotifyLocationListener(null);// fh add

		cancleTask();
	}

	private void cancleTask() {
		if (mTask != null)
			mTask.cancle();

		if (mReservationNumberTask != null)
			mReservationNumberTask.cancle();
	}

	/**
	 * 功能： 初始化定位
	 */
	private void initLocation() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocData = new LatLng(30.664785, 104.071085);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		// 设置定位数据
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(mLocData));
	}

	/**
	 * 显示新界面
	 * 
	 * @param listener
	 * */
	private void visibleNewView(View view, int titleId, ViewBeanStateChangeListener listener) {
		try {
			ViewBean lastViewBean = mViewSack.peek();
			if (lastViewBean != null) {
				lastViewBean.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ViewBean viewBean = new ViewBean(view, titleId, listener);
		view.setVisibility(View.VISIBLE);
		viewBean.getListener().onShow();
		mViewSack.push(viewBean);
		mTitleTextView.setText(titleId);
	}

	/**
	 * 弹出当前界面，返回前一个界面
	 * */
	private void invisibleView() {
		if (mViewSack.isEmpty()) {
			finish();
			return;
		}

		ViewBean viewBean = mViewSack.pop();
		if (mViewSack.isEmpty()) {
			finish();
			return;
		} else {
			if (viewBean != null) {
				viewBean.setVisibility(View.GONE);

				cancleTask();
			}
		}
		ViewBean lastViewBean = mViewSack.peek();
		if (lastViewBean != null) {
			int titleId = lastViewBean.titleId;
			lastViewBean.setVisibility(View.VISIBLE);
			mTitleTextView.setText(titleId);
		}
	}

	/**
	 * 功能： 显示营业厅详细信息并提示是否可预约                modify add by qhb
	 * @param position  位置
	 */
	private void visitHallInfo(int position) {
		mCurrentMobileBusinessHall = mSearchMobileBusinessHalls.get(position);
		visibleNewView(mBusinessHallDetailPanel, R.string.business_hall_detail, new ViewBeanStateChangeListener() {

			@Override
			public void onShow() {
				setBtnSynsEnable(true);
				setReservationNumberButton(true);
			}

			@Override
			public void onHide() {
				cancleTask();
			}
		});
		((TextView) findViewById(R.id.business_hall_name)).setText(mCurrentMobileBusinessHall.getName());
		((TextView) findViewById(R.id.business_hall_address)).setText(mCurrentMobileBusinessHall.getAddress());
		((TextView) findViewById(R.id.tel)).setText(mCurrentMobileBusinessHall.getPhoneNumber());
		((TextView) findViewById(R.id.working_day_business_hours)).setText(mCurrentMobileBusinessHall.getHoliDay());
		LinearLayout waitpanel = (LinearLayout)findViewById(R.id.ll_waitpeople_panel);
		LinearLayout nowaitpanel = (LinearLayout)findViewById(R.id.ll_nowaitpeople_panel);
		mHallDetailwaitPeoTextView.setText("0");
		((TextView) findViewById(R.id.update_time)).setText(new SimpleDateFormat("HH:mm").format(System.currentTimeMillis()));
		doRefreshWaitPeopleNum(mCurrentMobileBusinessHall.getId(), false, new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				String number = (String) param;

				if (TextUtils.isEmpty(number))
					return;

				mHallDetailwaitPeoTextView.setText(number);

				if (mCurrentMobileBusinessHall != null)
					mCurrentMobileBusinessHall.setWaitPeople(number);

				syncHallPeopleNum(mCurrentMobileBusinessHall.getId(), number);
			}

			@Override
			public void onPreExecute(GenericTask task) {
				setBtnSynsEnable(false);
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				setBtnSynsEnable(true);

				if (result != TaskResult.OK && task.getException() != null)
					Toast.makeText(LocationOverlayActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onCancelled(GenericTask task) {
				setBtnSynsEnable(true);
			}

			@Override
			public String getName() {
				return null;
			}
		});
		if (mCurrentMobileBusinessHall.getCanbeappoint() == 0) {
			mReservationNumberButton.setTextColor(0xffd9d9d9);
			mReservationNumberButton.setEnabled(false);
			waitpanel.setVisibility(View.GONE);
			nowaitpanel.setVisibility(View.VISIBLE);
			
		} else {
			mReservationNumberButton.setEnabled(true);
			mReservationNumberButton.setTextColor(0xffffffff);
			waitpanel.setVisibility(View.VISIBLE);
			nowaitpanel.setVisibility(View.GONE);
		}
		mDetailArressFlag.setTag(position);
	}

	private void getWaitPeople(final TextView textView, final String id, boolean reload) {

		if (!reload && mHashPointNum.containsKey(id)) {
			String number = mHashPointNum.get(id);
			if (mCurrentMobileBusinessHall != null) {
				mCurrentMobileBusinessHall.setWaitPeople(number);
			}
			if (textView != null) {
				textView.setText(number);
			}
			return;
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {

				if (!isStarted)
					return;

				String number = (String) msg.obj;
				if (TextUtils.isEmpty(number)) {
					number = "0";
				}
				if (mCurrentMobileBusinessHall != null) {
					mCurrentMobileBusinessHall.setWaitPeople(number);
				}
				if (textView != null) {
					textView.setText(number);
				}

				if (mCurrentShowHallInfoIndex != -1) {// 同步已预约的等候人数
					String hallnum = App.sSettingsPreferences.getString(Preferences.HALL_NUMBER, "");
					if (!TextUtils.isEmpty(hallnum) && id.equals(hallnum))
						if (mHaveReservationHallWaitPeople != null)
							mHaveReservationHallWaitPeople.setText(number);
				}

				mHashPointNum.put(id, number);
			};
		};

		mPool.submit(new GetWaitNumberTask(handler, id));
	}

	private void doReservationNumber() {
		if (!UserInfoControler.getInstance().checkUserLoginIn()) {
			startActivityForResult(new Intent(LocationOverlayActivity.this, LoginActivity.class), 0);
		} else {
			reservationNumber();
		}
	}
	private void initShowHallListData(){
		mShowMobileBusinessHalls.clear();
		for (int i = 0 ; i < mSearchMobileBusinessHalls.size(); i++) {
			MobileBusinessHall hall = mSearchMobileBusinessHalls.get(i);
			if (0  !=  hall.getCanbeappoint()) {
				mShowMobileBusinessHalls.add(hall);
			}

		}
	}
	private void initBusinessHallList() {
		
		CheckBox box = (CheckBox)findViewById(R.id.isShowAll);
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mBusinessHallsAdapter != null ) {
					if (isChecked) {
						mShowMobileBusinessHalls.clear();
						mShowMobileBusinessHalls.addAll(mSearchMobileBusinessHalls);
					} else {
						initShowHallListData();
					}
					mBusinessHallsAdapter.notifyDataSetChanged();
				}
			}
		});
		mSearchBusinessHallReslutPanel = (LinearLayout) findViewById(R.id.search_business_hall_panel);
		mSearchBusinessHallReslut = (ListView) findViewById(R.id.business_hall_search_result);
		
		mSearchBusinessHallReslut.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				visitHallInfo(position);
			}
		});
		mBusinessHallNumberView = (TextView) findViewById(R.id.business_hall_info);
		initShowHallListData();
		mBusinessHallsAdapter = new BusinessHallsAdapter(mShowMobileBusinessHalls, this);
		mBusinessHallsAdapter.setViewMapClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				viewMap(position);
			}
		});

		mBusinessHallsAdapter.setGetWaitPeopleListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				refreshBusinessHallListWaitPeopleNumber((TextView) arg1, position);
			}
		});

		mSearchBusinessHallReslut.setAdapter(mBusinessHallsAdapter);
		mSearchBusinessHallReslutPanel.setVisibility(View.GONE);

		mSearchRangeTextView = (TextView) findViewById(R.id.search_range_text);
		mSearchRangeTextView.setText(mSearchRange + M);
		((TextView) findViewById(R.id.search_min)).setText(MIN_SEARCH_RANGE + M);
		((TextView) findViewById(R.id.search_min)).setOnClickListener(this);
		((TextView) findViewById(R.id.search_1k_text)).setOnClickListener(this);
		((TextView) findViewById(R.id.search_2k_text)).setOnClickListener(this);
		((TextView) findViewById(R.id.search_3k_text)).setOnClickListener(this);
		((TextView) findViewById(R.id.search_max)).setOnClickListener(this);
		((TextView) findViewById(R.id.search_max)).setText(R.string.fiveKilometers);// MAX_SEARCH_RANGE
		// + M

		mSearchRangeSeekBar = (SeekBar) findViewById(R.id.search_range);
		mSearchRangeSeekBar.setMax(MAX_SEARCH_RANGE - MIN_SEARCH_RANGE);
		mSearchRangeSeekBar.setProgress(mSearchRange);
		mSearchRangeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
				mSearchRangeSeekBar.setProgress(progress);
				mSearchRange = progress + MIN_SEARCH_RANGE;
				// LogUtlis.showLogI("预约取号", "当前距离:" + mSearchRange);
				mSearchRangeTextView.setText(mSearchRange + M);
				poiSearchNearBy(SEARCH_KEY, mCenterPoint, mSearchRange);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	private void viewMap(int position) {
		visibleNewView(mMapView, R.string.reservation_number, null);
		showMobileBusinessHallDetail(position);
	}

	private void poiSearchNearBy(String key, final LatLng pt, final int radius) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				mCurrentShowHallInfoIndex = -1;
				ArrayList<MobileBusinessHall> result = new ArrayList<MobileBusinessHall>();
				for (MobileBusinessHall mobileBusinessHall : App.sMobileBusinessHalls) {
					LatLng coordinate = mobileBusinessHall.getCoordinate();
					double distance = DistanceUtil.getDistance(pt, coordinate);
					mobileBusinessHall.setDistance(distance);
					if (distance < radius) {
						result.add(mobileBusinessHall);
					}
				}
				Collections.sort(result);
				Message message = mHandler.obtainMessage(0, result);
				mHandler.sendMessage(message);
			}
		};
		mPool.execute(runnable);
	}

	private void initBusinessHallDetail() {
		mBusinessHallDetailPanel = (RelativeLayout) findViewById(R.id.businesss_hall_detail);
		mReservationNumberButton = ((Button) findViewById(R.id.reservation_number_bt));
		mDetailArressFlag = ((ImageView) findViewById(R.id.detail_address_flag));
		mHallDetailwaitPeoTextView = ((TextView) findViewById(R.id.customer_queue));

		mDetailArressFlag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getTag() != null)
					viewMap((Integer) v.getTag());
			}
		});
		// 预约取号
		mReservationNumberButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doReservationNumber();
				App.sSettingsPreferences.putLong(Preferences.ORDER_TIME, System.currentTimeMillis());
			}
		});

		// 同步按钮
		mBtnSync = (ImageView) findViewById(R.id.synchronous);
		mBtnSync.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doRefreshWaitPeopleNum(mCurrentMobileBusinessHall.getId(), true, new TaskListener() {

					@Override
					public void onProgressUpdate(GenericTask task, Object param) {
						((TextView) findViewById(R.id.update_time)).setText(new SimpleDateFormat("HH:mm").format(System.currentTimeMillis()));
						String number = (String) param;
						if (TextUtils.isEmpty(number))
							return;

						mHallDetailwaitPeoTextView.setText(number);
						syncHallPeopleNum(mCurrentMobileBusinessHall.getId(), number);
						
					}

					@Override
					public void onPreExecute(GenericTask task) {
						setBtnSynsEnable(false);
					}

					@Override
					public void onPostExecute(GenericTask task, TaskResult result) {
						setBtnSynsEnable(true);
						
						if (result != TaskResult.OK && task.getException() != null)
							Toast.makeText(LocationOverlayActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onCancelled(GenericTask task) {
						setBtnSynsEnable(true);
					}

					@Override
					public String getName() {
						return null;
					}
				});
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (resultCode == Activity.RESULT_OK)
			reservationNumber();
	}

	private void reservationNumber() {
		// 从保存的预约信息中判断是否已预约营业厅 如果是 提示对话框先取消再预约 如果否 进行预约动作
		if (App.sSettingsPreferences.isValidReservation()) {
			CommUtil.showAlert(LocationOverlayActivity.this, null,
					String.format(getResources().getString(R.string.noticeOfHasReservation), App.sSettingsPreferences.getString(Preferences.HALL_NAME_POP)),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
		} else {

			cancleTask();

			mReservationNumberTask = ReservationNumberTask.execute(mCurrentMobileBusinessHall.getId(), new TaskListener() {

				public void onProgressUpdate(GenericTask task, Object param) {
					ReservationNumberReslut reservationNumberReslut = (ReservationNumberReslut) param;
					if (mHallDetailwaitPeoTextView != null)
						mHallDetailwaitPeoTextView.setText(reservationNumberReslut.getNumber());
					String result = reservationNumberReslut.getResult();
					if (isinit) {
						isinit = false;
					} else {
						if (reservationNumberReslut.getResultCode() == 0) {
							// 提示预约成功结果
							LogUtlis.showLogD("test", reservationNumberReslut.getResult());
							CommUtil.showAlert(LocationOverlayActivity.this, reservationNumberReslut.getResult(),
									getResources().getString(R.string.toHallGetNumber), null);
							// 保存营业厅信息，并写入更新时间 ： 保存内容包括： 营业厅ID，营业厅名称，地址，当前时间
							App.sSettingsPreferences.saveReservationInfo(mCurrentMobileBusinessHall.getId(), mCurrentMobileBusinessHall.getName(),
									mCurrentMobileBusinessHall.getAddress());

							UserInfoControler.getInstance().setReservationHallId(UserInfoControler.getInstance().getUserName(),
									mCurrentMobileBusinessHall.getId());
							// 展示预约信息
							refreshHaveReservationPanel(reservationNumberReslut, mCurrentMobileBusinessHall.getId());

						} else {
							CommUtil.showAlert(LocationOverlayActivity.this, null, reservationNumberReslut.getResult(), null);
						}
					}
				}

				@Override
				public void onPreExecute(GenericTask task) {

					setReservationNumberButton(false);
				}

				@Override
				public void onPostExecute(GenericTask task, TaskResult result) {
					setReservationNumberButton(true);
					((TextView) findViewById(R.id.update_time)).setText(new SimpleDateFormat("HH:mm").format(System.currentTimeMillis()));
					if (TaskResult.OK != result) {
						if (isinit) {
							isinit = false;
						} else {
							Toast.makeText(LocationOverlayActivity.this, R.string.reservation_failed, Toast.LENGTH_SHORT).show();
						}
					}
				}

				@Override
				public void onCancelled(GenericTask task) {
					setReservationNumberButton(true);
				}

				@Override
				public String getName() {
					return null;
				}
			});
		}

	}

	private void initMapView() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
		visibleNewView(mMapView, R.string.reservation_number, new ViewBeanStateChangeListener() {

			@Override
			public void onShow() {
				mRightButton.setVisibility(View.VISIBLE);
			}

			@Override
			public void onHide() {
				mRightButton.setVisibility(View.GONE);
				hideInfoWindow();
			}
		});
	}

	private void initHeader() {
		mLeftButton = (Button) findViewById(R.id.headbar_leftbutton);
		mLeftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doBack();
			}
		});
		mRightButton = (Button) findViewById(R.id.headbar_rightbutton);
		// mRightButton.setEnabled(false);
		mRightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSearchMobileBusinessHalls != null && !mSearchMobileBusinessHalls.isEmpty()) {
					visibleNewView(mSearchBusinessHallReslutPanel, R.string.business_hall_list, null);
				} else {
					Toast.makeText(LocationOverlayActivity.this, "您附近没有搜到营业厅信息", Toast.LENGTH_LONG).show();
				}
			}
		});
		mTitleTextView = (TextView) findViewById(R.id.headbar_title);
	}

	private void doBack() {
		invisibleView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	// private void refreshOverlay(List<PoiInfo> res) {
	// mBaiduMap.clear();
	//
	// mSearchMkPoiInfos.clear();
	// mSearchMkPoiInfos.addAll(res);
	// String hallNumberStr = getString(R.string.hall_number).replace("number",
	// mSearchMkPoiInfos.size() + "");
	// mBusinessHallNumberView.setText(hallNumberStr);
	// mBusinessHallsAdapter.notifyDataSetChanged();
	// for (int i = 0; i < mSearchMkPoiInfos.size(); i++) {
	// PoiInfo mkPoiInfo = mSearchMkPoiInfos.get(i);
	// LatLng pt = mkPoiInfo.location;
	// OverlayOptions overLayOptions = new
	// MarkerOptions().position(pt).icon(mCurrentMarker).zIndex(9);
	// mBaiduMap.addOverlay(overLayOptions);
	// }
	// }

	private void refreshOverlayLocal(ArrayList<MobileBusinessHall> result) {
		if (result.size() > 0) {
			mRightButton.setEnabled(true);
			mBaiduMap.clear();

			mSearchMobileBusinessHalls.clear();
			mSearchMobileBusinessHalls.addAll(result);
			String hallNumberStr = getString(R.string.hall_number).replace("number", mSearchMobileBusinessHalls.size() + "");
			mBusinessHallNumberView.setText(hallNumberStr);
			mBusinessHallsAdapter.notifyDataSetChanged();
			ArrayList<PoiInfo> mkPoiInfos = new ArrayList<PoiInfo>();
			for (int i = 0; i < mSearchMobileBusinessHalls.size(); i++) {
				MobileBusinessHall hall = mSearchMobileBusinessHalls.get(i);
				LatLng coordinate = hall.getCoordinate();
				PoiInfo mkPoiInfo = new PoiInfo();
				mkPoiInfo.location = coordinate;
				mkPoiInfos.add(mkPoiInfo);
				OverlayOptions overLayOptions = null;
				if (hall.getCanbeappoint() == 0) {
					overLayOptions = new MarkerOptions().position(coordinate).title(hall.getName()).title(hall.getName()).icon(mUnableMarker)
							.zIndex(9);
				} else {
					overLayOptions = new MarkerOptions().position(coordinate).title(hall.getName()).title(hall.getName()).icon(mEnableMarker)
							.zIndex(9);
				}
				
				mBaiduMap.addOverlay(overLayOptions);

			}
			initShowHallListData();
			for (MobileBusinessHall info : result) {
				if (info.getCoordinate() != null) {
					mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(info.getCoordinate()));
					// mMapView.getController().animateTo(info.getCoordinate());
					break;
				}
			}
		}

	}

	@SuppressWarnings("unused")
	private BitmapDrawable drawTextAtBitmap(Bitmap bitmap, String text) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		// 创建一个和原图同样大小的位图
		Bitmap newbit = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newbit);
		Paint paint = new Paint();
		// 在原始位置0，0插入原图
		canvas.drawBitmap(bitmap, 0, 0, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(20);
		int iWordWidth = (int) paint.measureText(text);
		// 在原图指定位置写上字
		canvas.drawText(text, (bitmapWidth - iWordWidth) / 2, (bitmapHeight - 20) / 2 + 5, paint);
		return new BitmapDrawable(newbit);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);

			if (isFirstLoc) {
				mCenterPoint = new LatLng(locData.latitude, locData.longitude);
				// 移动地图到定位点
				//mCenterPoint = new LatLng( (30.665 ),  (104.080));  // 指定位置为联想移动成都服务支持中心为中心点
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(mCenterPoint, 14);
				mBaiduMap.animateMapStatus(u);
				
				poiSearchNearBy(SEARCH_KEY, mCenterPoint, mSearchRange);

				// 首次定位完成
				isFirstLoc = false;
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
		PhoneClickAgent.onPageEnd(LocationOverlayActivity.this, getString(getClassNameTitleId()));
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		PhoneClickAgent.onPageStart(getString(getClassNameTitleId()));
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();

		// mPoiSearch.destroy();

		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;

		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);

		if (mPool != null && !mPool.isTerminated() && !mPool.isShutdown())
			mPool.shutdown();

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		menu.add(0, 0, 0, "普通");
		menu.add(1, 1, 1, "罗盘");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0:
			Toast.makeText(LocationOverlayActivity.this, "地图：普通模式", Toast.LENGTH_SHORT).show();
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(LocationMode.NORMAL, true, null));
			break;
		case 1:
			Toast.makeText(LocationOverlayActivity.this, "地图：罗盘模式", Toast.LENGTH_SHORT).show();
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(LocationMode.COMPASS, true, null));
			break;
		default:
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 创建弹出泡泡图层 在地图上弹出的预约取号小对话框
	 */
	private void initInfoWindowContent() {
		mViewCache = getLayoutInflater().inflate(R.layout.info_window_layout, null);
		mNameTextView = (TextView) mViewCache.findViewById(R.id.address_name);
		mWaitPeopleTextView = (TextView) mViewCache.findViewById(R.id.wait_people);
		mWaitpeoplePanel = (LinearLayout)mViewCache.findViewById(R.id.ll_wait_panel);	// 可预约对应panel
		mNoWaitpeoplePanel = (LinearLayout)mViewCache.findViewById(R.id.ll_nowait_panel);	// 不可预约对应panel
		mNoWaitPeopleView = (TextView)mViewCache.findViewById(R.id.nowaitinfo);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {

				for (int i = 0; i < mSearchMobileBusinessHalls.size(); ++i)
					if (mSearchMobileBusinessHalls.get(i).getCoordinate() == marker.getPosition()) {
						showMobileBusinessHallDetail(i);
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

		((TextView) mViewCache.findViewById(R.id.click_diteil)).getPaint().setUnderlineText(true);
	}

	private void showMobileBusinessHallDetail(int index) {
		mMapView.setVisibility(View.VISIBLE);
		mRightButton.setVisibility(View.VISIBLE);

		mCurrentMobileBusinessHall = mSearchMobileBusinessHalls.get(index);
		LatLng pt = mCurrentMobileBusinessHall.getCoordinate();
		mNameTextView.setText(mCurrentMobileBusinessHall.getName());
		
	
		mWaitPeopleTextView.setText("0");
		mCurrentShowHallInfoIndex = index;
		doRefreshWaitPeopleNum(mCurrentMobileBusinessHall.getId(), false, new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				String number = (String) param;
				if (TextUtils.isEmpty(number))
					return;

				mWaitPeopleTextView.setText((String) param);
				InfoWindow mInfoWindow = new InfoWindow(mViewCache, mSearchMobileBusinessHalls.get(mCurrentShowHallInfoIndex).getCoordinate(),
						new MyInfoWindowClickListener(mCurrentShowHallInfoIndex));
				mBaiduMap.showInfoWindow(mInfoWindow);

				syncHallPeopleNum(mCurrentMobileBusinessHall.getId(), number);
				
				((TextView) findViewById(R.id.update_time)).setText(new SimpleDateFormat("HH:mm").format(System.currentTimeMillis()));
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

		// TODO: 当前营业厅是否可预约判断   by qhb 
		LogUtlis.showLogI("预约取号	", "canbeappoint:" + mCurrentMobileBusinessHall.getCanbeappoint() + "mWaitpeoplePanel: "  + (mWaitpeoplePanel == null? "null":"not null"));
		if(mCurrentMobileBusinessHall.getCanbeappoint() == 0) {
			if (mWaitpeoplePanel != null) {
				mWaitpeoplePanel.setVisibility(View.GONE);
				mNoWaitpeoplePanel.setVisibility(View.VISIBLE);
				mNoWaitPeopleView.setText(mCurrentMobileBusinessHall.getAddress());
			}
			InfoWindow mInfoWindow = new InfoWindow(mViewCache, pt, new MyInfoWindowClickListener(index));
			mBaiduMap.showInfoWindow(mInfoWindow);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(pt));
			return ;
		} else {
			if (mWaitpeoplePanel != null) {
				mWaitpeoplePanel.setVisibility(View.VISIBLE);
				mNoWaitpeoplePanel.setVisibility(View.GONE);
			}
		}
		InfoWindow mInfoWindow = new InfoWindow(mViewCache, pt, new MyInfoWindowClickListener(index));
		mBaiduMap.showInfoWindow(mInfoWindow);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(pt));
	}

	@Override
	public void init() {
		// 修改为自定义marker
		mEnableMarker = BitmapDescriptorFactory.fromResource(R.drawable.pop_map);
		mUnableMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		setContentView(R.layout.locationoverlay);
		initHeader();
		initHaveReservationView();
		initMapView();
		initInfoWindowContent();
		initBusinessHallList();
		initBusinessHallDetail();

		initLocation();
	}

	// 预约取号信息条
	private void initHaveReservationView() {
		mHaveReservationPanel = (LinearLayout) findViewById(R.id.have_reservation_panel);
		mHaveReservationHallName = (TextView) findViewById(R.id.have_reservation_hall_name);
		mHaveReservationHallWaitPeople = (TextView) findViewById(R.id.have_reservation_wait_people);
		findViewById(R.id.have_number).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取消预约时间
				App.sSettingsPreferences.clearReservationInfo();
				mHaveReservationPanel.setVisibility(View.GONE);

			}
		});
	}

	/**
	 * 功能： 刷新预约取号信息        note add by qhb
	 * @param reservationNumberReslut  预约结果信息 
	 * @param id	营业厅id
	 */
	private void refreshHaveReservationPanel(ReservationNumberReslut reservationNumberReslut, String id) {
		MobileBusinessHall mobileBusinessHall = null;
		for (MobileBusinessHall hall : App.sMobileBusinessHalls) {
			if (hall.getId().trim().equals(id.trim())) {
				mobileBusinessHall = hall;
				break;
			}
		}
		if (mobileBusinessHall != null) {
			mHaveReservationHallName.setText(mobileBusinessHall.getName());
		}
		mHaveReservationHallWaitPeople.setText(reservationNumberReslut.getNumber());
		Date date = new Date();
		Date today = new Date(date.getYear(), date.getMonth(), date.getDate(), 0, 1, 0);
		if (App.sSettingsPreferences.getLong(Preferences.ORDER_TIME, 0) < today.getTime()) {
			mHaveReservationPanel.setVisibility(View.GONE);
		} else {
			mHaveReservationPanel.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected BaseFragment getFragment() {
		return null;
	}

	public class ComparatorHall implements Comparator<MobileBusinessHall> {

		@Override
		public int compare(MobileBusinessHall lhs, MobileBusinessHall rhs) {
			return lhs.compareTo(rhs);
		}
	}

	final ViewBeanStateChangeListener DEFAULT_LISTENER = new ViewBeanStateChangeListener() {

		@Override
		public void onShow() {

		}

		@Override
		public void onHide() {

		}
	};

	class ViewBean {
		View view;
		int titleId;

		ViewBeanStateChangeListener listener;

		public ViewBean(View view, int titleId, ViewBeanStateChangeListener listener) {
			super();
			this.view = view;
			this.titleId = titleId;
			this.listener = listener;
		}

		void setVisibility(int visibility) {

			int old = view.getVisibility();

			if (old == visibility)
				return;

			if (visibility == View.VISIBLE)
				getListener().onShow();
			else
				getListener().onHide();
			view.setVisibility(visibility);
		}

		ViewBeanStateChangeListener getListener() {
			if (listener == null)
				return DEFAULT_LISTENER;
			return listener;
		}
	}

	interface ViewBeanStateChangeListener {
		void onShow();

		void onHide();
	}

	@Override
	public void onClick(View v) {
		//  顶部选择搜索距离刻度条
		if (mSearchRangeSeekBar == null) {
			mSearchRangeSeekBar = (SeekBar) findViewById(R.id.search_range);
		}
		switch (v.getId()) {
		case R.id.search_min:
			mSearchRangeSeekBar.setProgress(0);
			break;
		case R.id.search_1k_text:
			mSearchRangeSeekBar.setProgress(500);
			break;
		case R.id.search_2k_text:
			mSearchRangeSeekBar.setProgress(1500);
			break;
		case R.id.search_3k_text:
			mSearchRangeSeekBar.setProgress(2500);
			break;
		case R.id.search_max:
			mSearchRangeSeekBar.setProgress(4500);
			break;

		default:
			break;
		}
	}

	/**
	 * 获取等待人数任务
	 */
	class GetWaitNumberTask implements Runnable {

		private Handler mHandler;
		private String mId;

		GetWaitNumberTask(Handler handler, String id) {
			mHandler = handler;
			mId = id;
		}

		@Override
		public void run() {
			try {
				String number = App.sServerClient.getWaitPeople(mId);
				Message message = mHandler.obtainMessage(0, number);
				mHandler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(-1);
			}
		}
	}

	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(LocationOverlayActivity.this, R.string.mapErrorRequireRestartSurface, Toast.LENGTH_SHORT).show();
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(LocationOverlayActivity.this, R.string.netHasProblem, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class MyInfoWindowClickListener implements OnInfoWindowClickListener {
		private int position;

		MyInfoWindowClickListener(int index) {
			position = index;
		}

		@Override
		public void onInfoWindowClick() {
			visitHallInfo(position);
			hideInfoWindow();
		}

	}

	/**
	 * 点击刷新指定等候用户人数
	 * 
	 * @param reload
	 * @param listener
	 * 
	 * @param clickButton
	 *            点击按钮
	 */
	private void doRefreshWaitPeopleNum(String id, boolean reload, TaskListener listener) {
		if (!reload && mHashPointNum.containsKey(id)) {
			String number = mHashPointNum.get(id);
			if (mCurrentMobileBusinessHall != null) {
				mCurrentMobileBusinessHall.setWaitPeople(number);
			}

			listener.onProgressUpdate(null, number);
			return;
		}

		cancleTask();

		mTask = com.sunrise.scmbhc.task.GetWaitNumberTask.execute(id, listener);
	}

	/**
	 * 设置刷新按钮
	 * 
	 * @param enable
	 */
	private void setBtnSynsEnable(boolean enable) {
		if (enable) {

			mBtnSync.setImageResource(android.R.drawable.ic_popup_sync);
			mBtnSync.setEnabled(true);

		} else {
			AnimationDrawable animD = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_loadingbar);
			mBtnSync.setImageDrawable(animD);
			animD.start();
			mBtnSync.setEnabled(false);
		}
	}

	/**
	 * 设置预约按钮
	 * 
	 * @param enable
	 */
	private void setReservationNumberButton(boolean enable) {
		if (enable) {
			mReservationNumberButton.setEnabled(true);
			mReservationNumberButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		} else {
			mReservationNumberButton.setEnabled(false);
			AnimationDrawable animD = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list_loadingbar);
			mReservationNumberButton.setCompoundDrawablesWithIntrinsicBounds(animD, null, null, null);
			animD.start();
		}
	}

	
	/**
	 * 功能： 隐藏显示的信息框   note by qhb
	 */
	private void hideInfoWindow() {
		mBaiduMap.hideInfoWindow();
		if (mTask != null)
			mTask.cancle();
	}

	/**
	 * 同步已预约的等候人数
	 * 
	 * @param id
	 * @param number
	 */
	private void syncHallPeopleNum(String id, String number) {
		if (mCurrentShowHallInfoIndex != -1) {
			String hallnum = App.sSettingsPreferences.getString(Preferences.HALL_NUMBER, "");
			if (!TextUtils.isEmpty(hallnum) && id.equals(hallnum))
				if (mHaveReservationHallWaitPeople != null)
					mHaveReservationHallWaitPeople.setText(number);
		}

		mHashPointNum.put(id, number);
	}

	/**
	 * 功能： 刷新营业厅列表等待人数   note by qhb
	 * @param textView 	等待人数textView人数
	 * @param position	营业厅list的位置
	 */
	private void refreshBusinessHallListWaitPeopleNumber(final TextView textView, int position) {
		doRefreshWaitPeopleNum(mSearchMobileBusinessHalls.get(position).getId(), true, new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {
				String number = (String) param;
				if (TextUtils.isEmpty(number))
					return;

				textView.setText((String) param);
				syncHallPeopleNum(mCurrentMobileBusinessHall.getId(), number);
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
	public int getClassNameTitleId() {
		return R.string.LocationOverlayActivity;
	}
}
