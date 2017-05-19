package com.sunrise.scmbhc.ui.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.http.client.ClientProtocolException;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;

import com.sunrise.scmbhc.ui.view.SlidingPaneLayout;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.starcpt.analytics.PhoneClickAgent;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppActionConstant;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.MyTabAdapter;
import com.sunrise.scmbhc.adapter.UseConditionAdapter;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.TabItem;
import com.sunrise.scmbhc.entity.UpdateInfo;
import com.sunrise.scmbhc.entity.UseCondition;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.service.BackgroundService;
import com.sunrise.scmbhc.task.ChannelOperTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetAllMenusTask;
import com.sunrise.scmbhc.task.GetCommonlyBusinessMenuTask;
import com.sunrise.scmbhc.task.GetCommonlySearchsTask;
import com.sunrise.scmbhc.task.GetContentInfoTask;
import com.sunrise.scmbhc.task.GetCreditsExchangeInfoTask;
import com.sunrise.scmbhc.task.GetMobileBusinessHallTask;
import com.sunrise.scmbhc.task.GetPreferentialInfosTask;
import com.sunrise.scmbhc.task.GetUpdateInfosTask;
import com.sunrise.scmbhc.task.LoginTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskManager;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.fragment.TopUpServeFragment;
import com.sunrise.scmbhc.ui.fragment.WebViewFragment;
import com.sunrise.scmbhc.ui.view.CustomTabHost;
import com.sunrise.scmbhc.ui.view.MyTab;
import com.sunrise.scmbhc.ui.view.MyTab.ItemSelectedListener;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.FileUtils;
import com.sunrise.scmbhc.utils.LogUtlis;
import com.sunrise.scmbhc.utils.UnitUtils;

public class MainActivity extends TabActivity implements OnTabChangeListener, OnClickListener, Observer {
	private final static String DISCOUNT_PHONE = "http://183.221.33.188:8092/scmbhi/wdtq/html/index.html";
	// "http://sc.10086.cn/m123/http://www.sc.10086.cn/service/phone/";//
	// "http://idcdemo.h3w.com.cn:8888/tailor/http://www.sc.10086.cn/service/phone/";//"file:///android_asset/index.html";//
	@SuppressWarnings("rawtypes")
	private final static Class[] sTabActivitys = { HomeActivity.class, BusinessActivity.class, SingleFragmentActivity.class, MoreActivity.class };
	private final static int[] sTabIconNoramlImageIds = { R.drawable.tap_item_icon_1, R.drawable.tap_item_icon_2, R.drawable.tap_item_icon_3,
			R.drawable.tap_item_icon_4 };
	private final static int[] sTabIconFocusImagesIds = { R.drawable.tap_item_icon_focus_1, R.drawable.tap_item_icon_focus_2, R.drawable.tap_item_icon_focus_3,
			R.drawable.tap_item_icon_focus_4 };
	private final static int[] sTabIconOnclickImagesIds = { R.drawable.tap_item_icon_click_1, R.drawable.tap_item_icon_click_2,
			R.drawable.tap_item_icon_click_3, R.drawable.tap_item_icon_click_4 };
	private final static int[] sStringIds = { R.string.home, R.string.oneself_handle_business, R.string.discount_phone, R.string.more };
	private final static int[] sResIds_starUser = { R.drawable.ic_star_1, R.drawable.ic_star_2, R.drawable.ic_star_3, R.drawable.ic_star_4,
			R.drawable.ic_star_5, R.drawable.ic_star_6, R.drawable.ic_star_7 };

	private ArrayList<TabItem> mTabItems = new ArrayList<TabItem>();
	private CustomTabHost tabHost;
	private MyTab mBottomTab;
	private SlidingPaneLayout mSlidingMenu;
	private boolean isScrolling;
	private MyTabAdapter mMyTabAdapter;
	private static MainActivity mainActiviry;
	private UserInfoControler mUserInfoControler;
	private ArrayList<UpdateInfo> updateInfos;
	public static int tabheight = 50;

	private TextView mBtSlidingMenuUserName;

	private ItemSelectedListener itemSelectedListener = new ItemSelectedListener() {
		@Override
		public void onItemSelected(int position, View currentItem, View lastItem) {
			updateTabSelected(position, currentItem, lastItem);
		}
	};

	private TaskListener mGetUpdateInfosListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			DealUpateInfo((ArrayList<UpdateInfo>) param);
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
	};

	private void DealUpateInfo(ArrayList<UpdateInfo> datas) {
		for (final UpdateInfo updateInfo : datas) {
			int type = updateInfo.getType();
			long newVersion = updateInfo.getNewVersionCode();
			App.sSettingsPreferences.putResNewVersion(String.valueOf(type), newVersion);
			switch (type) {
			case UpdateInfo.TYPE_MENUS:
				updateAllBusinessMenu(updateInfo);
				break;
			case UpdateInfo.TYPE_COMMONLY_BUSINESS:
				// updateCommonlyBusinessMenu(updateInfo);
				break;

			case UpdateInfo.TYPE_COMMONLY_PROBLEM:
				updateContentInfoMsg(updateInfo);
				break;

			case UpdateInfo.TYPE_USER_GUIDE:
				updateContentInfoMsg(updateInfo);
				break;

			case UpdateInfo.TYPE_SYSTEM_NOTICE:
				updateContentInfoMsg(updateInfo);
				break;
			case UpdateInfo.TYPE_BUSINESS_HALL:
				updateMobileBusinessHallInfo(updateInfo);
				break;
			case UpdateInfo.TYPE_TOPUP_RATES:
				updateTopupRates(updateInfo);
				break;
			case UpdateInfo.TYPE_CREDITS_EXCHANGE:
				updateCreditsExchange(updateInfo);
				break;
			}
		}
	}

	private TaskListener mPreferentialInfosListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			if (result == TaskResult.OK) {
				if (App.sScrollsPreferentialInfosObservable != null) {
					App.sScrollsPreferentialInfosObservable.refresh();
				}
			} else {

			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private TaskListener mGetCommonlySearchsListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			if (result == TaskResult.OK) {
				App.sCommonlySearchsObservable.refresh();
			} else {

			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private TaskListener mContentInfoMsgListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			if (result == TaskResult.OK) {
				if (App.sContenInfoObservable != null) {
					App.sContenInfoObservable.refresh();
				}
			} else {

			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	private TaskListener mGetCommonlyBusinessListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			if (result == TaskResult.OK) {
				App.sCommonlyBusinessObservable.refresh();
			} else {

			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	private TaskListener mAutoLoginListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// Toast.makeText(MainActivity.this, param + " welcome back!",
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			if (result != TaskResult.OK) {
				Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserInfoControler = UserInfoControler.getInstance();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		setContentView(R.layout.main);
		initIntentParam(getIntent().getParcelableArrayListExtra("updateinfos"));
		init();
		mainActiviry = this;
	}

	public void onStart() {
		super.onStart();
		mUserInfoControler.addObserver(this);
		registerUpdateReciever();
	}

	@Override
	protected void onResume() {
		updateSlideView();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSlidingMenu.setLocked(true);
	}

	public void onStop() {
		super.onStop();
		UserInfoControler.getInstance().deleteObserver(this);
		unregisterReceiver(updateReceiver);
	}

	public void onDestroy() {
		if (UserInfoControler.getInstance().checkUserLoginIn())
			doAsiaInfoChannelLogout();
		super.onDestroy();
		App.sContext.onDestroy();
	}

	private void initIntentParam(ArrayList<Parcelable> arrayList) {
		if (arrayList == null)
			return;

		updateInfos = new ArrayList<UpdateInfo>();
		for (Parcelable p : arrayList)
			updateInfos.add((UpdateInfo) p);
	}

	private void updateTabSelected(int position, View currentView, View lastView) {
		tabHost.setCurrentTab(position);
	}

	// 开放给其他界面设置 tab
	public void setCurrentTab(int position) {
		if (tabHost != null) {
			tabHost.setCurrentTab(position);
		}
		if (mBottomTab != null) {
			mBottomTab.setItemClick(position);
		}
	}

	private void init() {
		if (!App.sAppRunning) {
			App.sAppRunning = true;
			doCommitSearchTags();
		}
		CommUtil.addActivity(this);
		initTabs();
		doGetUpdateInfos();
		doGetPreferentialInfos();
		doGetComnonlySearchs();
		// doAutoLogin();

		mSlidingMenu = (SlidingPaneLayout) findViewById(R.id.sliding_menu);
		mSlidingMenu.setParallaxDistance(UnitUtils.dip2px(this, 150));

		CommUtil.startTrafficNotificationService(this);

		PhoneClickAgent.setDebugMode(true);
		PhoneClickAgent.setAutoLocation(true);
		PhoneClickAgent.setSessionContinueMillis(1000 * 5);
		PhoneClickAgent.updateOnlineConfig(this);

		mBtSlidingMenuUserName = (TextView) findViewById(R.id.home_sliding_menu_user_name_bt);
		mBtSlidingMenuUserName.setOnClickListener(this);

		findViewById(R.id.home_sliding_menu_account_balance_bt).setOnClickListener(this);
		findViewById(R.id.home_sliding_menu_main_tariff_packages_bt).setOnClickListener(this);
		findViewById(R.id.home_sliding_menu_monthly_consumption_bt).setOnClickListener(this);
		findViewById(R.id.home_sliding_menu_credits_level_help).setOnClickListener(this);
		/*
		 * mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		 * mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		 * mDrawerList = (ListView) findViewById(R.id.left_drawer);
		 * 
		 * // Set the adapter for the list view mDrawerList.setAdapter(new
		 * ArrayAdapter<String>(this, R.layout.drawer_list_item,
		 * mPlanetTitles)); // Set the list's click listener
		 * mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		 */
	}

	/**
	 * 更新配置信息
	 */
	private void doGetUpdateInfos() {
		if (updateInfos != null) {
			DealUpateInfo(updateInfos);
		} else {
			GenericTask getUpdateInfos = new GetUpdateInfosTask();
			getUpdateInfos.setListener(mGetUpdateInfosListener);
			getUpdateInfos.execute();
			App.sTaskManager.addObserver(getUpdateInfos);
			// DealUpateInfo(updateInfos);
		}
	}

	private void doGetPreferentialInfos() {
		GenericTask getScrollsPreferentialInfosTask = new GetPreferentialInfosTask(MainActivity.this);
		getScrollsPreferentialInfosTask.setListener(mPreferentialInfosListener);
		getScrollsPreferentialInfosTask.execute();
		App.sTaskManager.addObserver(getScrollsPreferentialInfosTask);
	}

	private void doGetComnonlySearchs() {
		GenericTask getCommonlySearchsTask = new GetCommonlySearchsTask(MainActivity.this);
		getCommonlySearchsTask.setListener(mGetCommonlySearchsListener);
		getCommonlySearchsTask.execute();
		App.sTaskManager.addObserver(getCommonlySearchsTask);
	}

	private void updateCommonlyBusinessMenu(final UpdateInfo updateInfo) {
		int type = updateInfo.getType();
		long newVersion = updateInfo.getNewVersionCode();
		long oldVersion = App.sSettingsPreferences.getResOldVersion(String.valueOf(type), UpdateInfo.COMMONLY_BUSINESS_DEFAULT_VERSION);
		if (newVersion > oldVersion) {
			doGetCommonlyBusinessMenu(updateInfo);
		}
	}

	private void updateMobileBusinessHallInfo(final UpdateInfo updateInfo) {
		int type = updateInfo.getType();
		long newVersion = updateInfo.getNewVersionCode();
		long oldVersion = App.sSettingsPreferences.getResOldVersion(String.valueOf(type), UpdateInfo.TYPE_BUSINESS_HALL);
		if (newVersion > oldVersion) {
			doGetMobileBusinessHallInfo(updateInfo);
		}
	}

	private void doGetMobileBusinessHallInfo(UpdateInfo updateInfo) {
		GenericTask getMobileBusinessHallTask = new GetMobileBusinessHallTask(MainActivity.this, updateInfo);
		getMobileBusinessHallTask.execute();
	}

	private void doGetCommonlyBusinessMenu(UpdateInfo updateInfo) {
		GenericTask getCommonlyUsedBusinessMenuTask = new GetCommonlyBusinessMenuTask(MainActivity.this, updateInfo);
		getCommonlyUsedBusinessMenuTask.setListener(mGetCommonlyBusinessListener);
		getCommonlyUsedBusinessMenuTask.execute();
		App.sTaskManager.addObserver(getCommonlyUsedBusinessMenuTask);
	}

	private void updateContentInfoMsg(final UpdateInfo updateInfo) {
		int type = updateInfo.getType();
		long newVersion = updateInfo.getNewVersionCode();
		long oldVersion = App.sSettingsPreferences.getResOldVersion(String.valueOf(type), UpdateInfo.COMMONLY_BUSINESS_DEFAULT_VERSION);
		if (newVersion > oldVersion) {
			doGetContentInfoMsg(updateInfo, type);
		}
	}

	private void updateTopupRates(UpdateInfo updateInfo) {
		int type = updateInfo.getType();
		long newVersion = updateInfo.getNewVersionCode();
		long oldVersion = App.sSettingsPreferences.getResOldVersion(String.valueOf(type), UpdateInfo.COMMONLY_BUSINESS_DEFAULT_VERSION);
		if (newVersion > oldVersion) {
			App.sSettingsPreferences.saveTopupRates(updateInfo.getUpdateDescription());
			App.sSettingsPreferences.putResVersion(String.valueOf(type), newVersion);
		}
	}

	private void updateCreditsExchange(UpdateInfo updateInfo) {
		int type = updateInfo.getType();
		long newVersion = updateInfo.getNewVersionCode();
		long oldVersion = App.sSettingsPreferences.getResOldVersion(String.valueOf(type), UpdateInfo.COMMONLY_BUSINESS_DEFAULT_VERSION);
		if (newVersion > oldVersion || App.test) {
			doGetCreditsExchangeMsg(updateInfo);
		}
	}

	private void doGetCreditsExchangeMsg(UpdateInfo updateInfo) {
		GetCreditsExchangeInfoTask getCreditsExchangeInfoTask = new GetCreditsExchangeInfoTask();
		App.sTaskManager.addObserver(getCreditsExchangeInfoTask.execute(this, updateInfo, null));
	}

	private void doGetContentInfoMsg(UpdateInfo updateInfo, int type) {
		GenericTask getContentInfoTaskNotice = new GetContentInfoTask(MainActivity.this, updateInfo);
		TaskParams params = new TaskParams();
		params.put("type", type);
		getContentInfoTaskNotice.setListener(mContentInfoMsgListener);
		getContentInfoTaskNotice.execute(params);
		App.sTaskManager.addObserver(getContentInfoTaskNotice);
		/*
		 * // 使用帮助 GenericTask getContentInfoTaskGuide =new
		 * GetContentInfoTask(MainActivity.this, updateInfo); TaskParams params1
		 * = new TaskParams(); params1.put("type", UpdateInfo.USER_GUIDE_TYPE);
		 * getContentInfoTaskGuide.setListener(mContentInfoMsgListener);
		 * getContentInfoTaskGuide.execute(params1);
		 * App.sTaskManager.addObserver(getContentInfoTaskGuide); // 常见问题
		 * GenericTask getContentInfoTaskProblem =new
		 * GetContentInfoTask(MainActivity.this, updateInfo); TaskParams params2
		 * = new TaskParams(); params2.put("type",
		 * UpdateInfo.COMMONLY_PROBLEM_TYPE);
		 * getContentInfoTaskProblem.setListener(mContentInfoMsgListener);
		 * getContentInfoTaskProblem.execute(params2);
		 * App.sTaskManager.addObserver(getContentInfoTaskProblem);
		 */
	}

	private void updateAllBusinessMenu(UpdateInfo updateInfo) {
		int type = updateInfo.getType();
		long newVersion = updateInfo.getNewVersionCode();
		long oldVersion = App.sSettingsPreferences.getResOldVersion(String.valueOf(type), UpdateInfo.MENUS_DEFAULT_VERSION);
		if (newVersion > oldVersion) {
			doGetAllMenus(updateInfo);
		}
	}

	private void doGetAllMenus(UpdateInfo updateInfo) {
		String url = updateInfo.getDownloadUrl();
		if (url != null) {
			GenericTask getAllMenusTask = new GetAllMenusTask(MainActivity.this, updateInfo);
			getAllMenusTask.execute();
			App.sTaskManager.addObserver(getAllMenusTask);
		}
	}

	/*
	 * private void getUpdateInfo(){ App.sServerClient.getUpdateInfos(); }
	 */

	private void initTabs() {
		tabHost = (CustomTabHost) findViewById(android.R.id.tabhost);
		tabHost.setOnTabChangedListener(this);

		for (int i = 0; i < sTabActivitys.length; i++) {
			setIndicator(i);
			TabItem tabItem = new TabItem(sTabIconNoramlImageIds[i], sTabIconFocusImagesIds[i], sStringIds[i], sTabIconOnclickImagesIds[i]);
			mTabItems.add(tabItem);
		}
		mMyTabAdapter = new MyTabAdapter(this, mTabItems);
		mBottomTab = (MyTab) findViewById(R.id.bottom_tab);
		setCurrentTab(0);
		mBottomTab.setAdapter(mMyTabAdapter);
		mBottomTab.setItemSelectedListener(itemSelectedListener);

	}

	private void setIndicator(int tabId) {
		Intent intent = new Intent(this, sTabActivitys[tabId]);
		int strId = sStringIds[tabId];
		String IndicatorStr = getString(strId);
		if (strId == R.string.home) {
			IndicatorStr = getString(R.string.app_name);
		}
		intent.putExtra(ExtraKeyConstant.KEY_TITLE, IndicatorStr);
		if (strId == R.string.discount_phone) {
			PhoneClickAgent.onPageStart("PreferentialPhoneFragment");
			PhoneClickAgent.onPageEnd(MainActivity.this, "PreferentialPhoneFragment");
			intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT, WebViewFragment.class);
			BusinessMenu businessMenu = new BusinessMenu();
			businessMenu.setServiceUrl(DISCOUNT_PHONE + "?app_version=" + App.sAPKVersionCode);
			businessMenu.setName(IndicatorStr);
			Bundle bundle = new Bundle();
			bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
			intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
			intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, false);
		}
		String str = String.valueOf(tabId);
		TabHost.TabSpec localTabSpec = tabHost.newTabSpec(str).setIndicator(str).setContent(intent);
		tabHost.addTab(localTabSpec);
	}

	@Override
	public void onTabChanged(String tabId) {
		// tabId值为要切换到的tab页的索引位置
		int tabID = Integer.valueOf(tabId);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP && isScrolling) {
			// mSlidingMenu.slidingMenu();
			// if (!mSlidingMenu.isOpened())
			// mSlidingMenu.setScrolling(false);
		}
		return super.dispatchTouchEvent(ev);
	}

	/*
	 * public boolean onTouchEvent(MotionEvent event) { return
	 * gestureDetector.onTouchEvent(event); }
	 */

	public void setTabHostVisibility(boolean isVisible) {
		if (mBottomTab == null) {
			initTabs();
		}
		if (mBottomTab != null && (!isVisible)) {
			mBottomTab.setVisibility(View.GONE);
		} else {
			mBottomTab.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 自动登录
	 */
	private void doAutoLogin() {
		if (App.sSettingsPreferences.isAutoLogin()) {
			LoginTask.execute(UserInfoControler.getInstance().getUserName(), UserInfoControler.getInstance().getPassword(), true, true, true,
					mAutoLoginListener);
		} else {
			App.sSettingsPreferences.savePhoneFreeQuery(null);
		}
	}

	private void doCommitSearchTags() {
		SubmitSearchTagsTask submitSearchTagsTask = new SubmitSearchTagsTask();
		submitSearchTagsTask.execute();
	}

	class SubmitSearchTagsTask extends GenericTask {
		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				String jsonstr = FileUtils.readDataFile(MainActivity.this, App.AppDirConstant.APP_SEARCH_FILE_NAME);
				if (jsonstr != null) {
					App.sServerClient.commitSearchTags(jsonstr);
					FileUtils.deleteDataFile(MainActivity.this, App.AppDirConstant.APP_SEARCH_FILE_NAME);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			} catch (IOException e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			} catch (HttpException e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	/**
	 * @return the mainActiviry
	 */
	public static MainActivity getInstance() {
		return mainActiviry;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_sliding_menu_user_name_bt:// 注销
			if (UserInfoControler.getInstance().checkUserLoginIn()) {
				closeSlidingMenu();
				TaskManager.getInstance().cancelAll();
				UserInfoControler.getInstance().loginOut();
				UserInfoControler.getInstance().setAutoLogin(false);// 取消自动登录
				App.setPwd(null);
				doAsiaInfoChannelLogout();
				updateSlideView();
			} else {
				startActivityForResult(new Intent(this, LoginActivity.class), 1);
			}
			break;
		case R.id.home_sliding_menu_account_balance_bt: {// 充值
			Intent intent = new Intent(getCurrentActivity(), SingleFragmentActivity.class);
			intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, TopUpServeFragment.class);
			getCurrentActivity().startActivity(intent);
		}
			break;
		case R.id.home_sliding_menu_main_tariff_packages_bt: // 实时帐单
			break;
		case R.id.home_sliding_menu_monthly_consumption_bt: // 资费查询
			break;
		case R.id.home_sliding_menu_credits_level_help:// 星级详情
			Intent newIntent = new Intent(MainActivity.this, SingleFragmentActivity.class);
			Bundle bundle = new Bundle();
			BusinessMenu businessMenu = new BusinessMenu();
			// businessMenu.setServiceUrl("file:///android_asset/starServiceHtml/index.html");
			businessMenu.setServiceUrl("file:///android_asset/starIntroduce/html/xinjifuwu.html");
			businessMenu.setName(getString(R.string.creditRating));
			bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
			newIntent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, WebViewFragment.class);
			newIntent.putExtra(App.ExtraKeyConstant.KEY_BUNDLE, bundle);
			startActivity(newIntent);
			break;
		default:
			break;
		}

	}

	/**
	 * 更新侧边栏数据
	 */
	private void updateSlideView() {
		boolean isLogined = UserInfoControler.getInstance().checkUserLoginIn();

		mSlidingMenu.setLocked(!isLogined);// 锁侧边栏

		updateLoginState(isLogined);
		updateBalanceInfo(isLogined);
		updateFreeCondition(isLogined);
		updateUserBaseInfo(isLogined);

		// if (isLogined) {
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_user_name)).setText(mUserInfoControler.getUserPersonName());
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_phone)).setText(mUserInfoControler.getUserName());
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_account_balance)).setText(mUserInfoControler.getCurBalance());
		// {// 公共余额展示
		// TextView view = ((TextView)
		// findViewById(R.id.home_sliding_menu_public_account_balance));
		// String pBalance =
		// mUserInfoControler.getPhoneCurMsg().getPublicBalance();
		// if (!TextUtils.isEmpty(pBalance)) {
		// view.setText(pBalance + "元");
		// ((View) view.getParent()).setVisibility(View.VISIBLE);
		// } else
		// ((View) view.getParent()).setVisibility(View.GONE);
		// }
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_brand)).setText(mUserInfoControler.getBrandName());
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_main_tariff_packages)).setText(mUserInfoControler.getMainMode());
		//
		// {// 星级展示
		// String str = mUserInfoControler.getCreditClass();
		//
		// if (TextUtils.isEmpty(str))
		// ((ViewGroup)
		// findViewById(R.id.home_sliding_menu_credits_level).getParent()).setVisibility(View.GONE);
		// else {
		// int starLevel = Integer.parseInt(str);
		// starLevel = Math.min(starLevel, sResIds_starUser.length);
		// starLevel = Math.max(starLevel, 1);
		// ((ViewGroup)
		// findViewById(R.id.home_sliding_menu_credits_level).getParent()).setVisibility(View.VISIBLE);
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_credits_level)).setCompoundDrawablesWithIntrinsicBounds(sResIds_starUser[starLevel
		// - 1], 0,
		// 0, 0);
		// }
		// }
		//
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_monthly_consumption)).setText(mUserInfoControler.getConsumptionThisMonth());
		// {// 公共账单消费
		// TextView view = ((TextView)
		// findViewById(R.id.home_sliding_menu_public_monthly_consumption));
		// String pCost = mUserInfoControler.getPhoneCurMsg().getPublicCost();
		// if (!TextUtils.isEmpty(pCost)) {
		// view.setText(pCost + "元");
		// ((View) view.getParent()).setVisibility(View.VISIBLE);
		// } else
		// ((View) view.getParent()).setVisibility(View.GONE);
		// }
		// ListView mListViewFreeCondition = (ListView)
		// findViewById(R.id.listview_usedConditon);
		//
		// List<UseCondition> list =
		// mUserInfoControler.getPhoneFreeQuery().getOtherPackages();
		// LogUtlis.showLogI("绑定套餐余量", list.toString());
		// if (list == null || list.isEmpty())
		// list = mUserInfoControler.getPhoneCurMsg().getArrCondition();
		// if (list != null && !list.isEmpty()) {
		// mListViewFreeCondition.setVisibility(View.VISIBLE);
		// UseConditionAdapter adapter = new UseConditionAdapter(this, list);
		// mListViewFreeCondition.setAdapter(adapter);
		// } else {
		// mListViewFreeCondition.setVisibility(View.GONE);
		// }
		// mBtSlidingMenuUserName.setText(R.string.logout);
		// mBtSlidingMenuUserName.setBackgroundResource(R.drawable.selector_normal_red_click_blue);
		// mBtSlidingMenuUserName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sliding_menu_logout,
		// 0, 0, 0);
		// } else {
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_user_name)).setText(null);
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_phone)).setText(null);
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_account_balance)).setText(null);
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_brand)).setText(null);
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_main_tariff_packages)).setText(null);
		// ((TextView)
		// findViewById(R.id.home_sliding_menu_monthly_consumption)).setText(null);
		// ListView mListViewFreeCondition = (ListView)
		// findViewById(R.id.listview_usedConditon);
		// mListViewFreeCondition.setVisibility(View.GONE);
		// UseConditionAdapter adapter = new UseConditionAdapter(this,
		// mUserInfoControler.getPhoneCurMsg().getArrCondition());
		// mListViewFreeCondition.setAdapter(adapter);
		//
		// ((View)
		// findViewById(R.id.home_sliding_menu_public_account_balance).getParent()).setVisibility(View.GONE);
		//
		// mBtSlidingMenuUserName.setText(R.string.login);
		// mBtSlidingMenuUserName.setBackgroundResource(R.drawable.selector_bg_azure);
		// mBtSlidingMenuUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0,
		// 0, 0);
		//
		// mSlidingMenu.closePane();
		// }
	}

	/**
	 * 更新登录状态
	 * 
	 * @param isLogined
	 */
	private void updateLoginState(boolean isLogined) {
		if (isLogined) {
			((TextView) findViewById(R.id.home_sliding_menu_phone)).setText(mUserInfoControler.getUserName());
			mBtSlidingMenuUserName.setText(R.string.logout);
			mBtSlidingMenuUserName.setBackgroundResource(R.drawable.selector_normal_red_click_blue);
			mBtSlidingMenuUserName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sliding_menu_logout, 0, 0, 0);
		} else {
			((TextView) findViewById(R.id.home_sliding_menu_phone)).setText(null);
			mBtSlidingMenuUserName.setText(R.string.login);
			mBtSlidingMenuUserName.setBackgroundResource(R.drawable.selector_bg_azure);
			mBtSlidingMenuUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mSlidingMenu.closePane();
		}
	}

	/**
	 * 更新套餐余量信息
	 * 
	 * @param isLogined
	 */
	private void updateFreeCondition(boolean isLogined) {

		ListView mListViewFreeCondition = (ListView) findViewById(R.id.listview_usedConditon);
		if (isLogined) {
			List<UseCondition> list = mUserInfoControler.getPhoneFreeQuery().getOtherPackages();
			LogUtlis.showLogI("绑定套餐余量", list.toString());
			if (list == null || list.isEmpty())
				list = mUserInfoControler.getPhoneCurMsg().getArrCondition();
			if (list != null && !list.isEmpty()) {
				UseConditionAdapter adapter = new UseConditionAdapter(this, list);
				mListViewFreeCondition.setAdapter(adapter);
				mListViewFreeCondition.setVisibility(View.VISIBLE);
			} else {
				mListViewFreeCondition.setVisibility(View.GONE);
			}
		} else {
			UseConditionAdapter adapter = new UseConditionAdapter(this, null);
			mListViewFreeCondition.setAdapter(adapter);
			mListViewFreeCondition.setVisibility(View.GONE);
		}

	}

	/**
	 * 更新账户信息
	 * 
	 * @param isLogined
	 */
	private void updateBalanceInfo(boolean isLogined) {

		if (isLogined) {
			((TextView) findViewById(R.id.home_sliding_menu_account_balance)).setText(mUserInfoControler.getCurBalance());
			{// 公共余额展示
				TextView view = ((TextView) findViewById(R.id.home_sliding_menu_public_account_balance));
				String pBalance = mUserInfoControler.getPhoneCurMsg().getPublicBalance();
				if (!TextUtils.isEmpty(pBalance)) {
					view.setText(pBalance + "元");
					((View) view.getParent()).setVisibility(View.VISIBLE);
				} else
					((View) view.getParent()).setVisibility(View.GONE);
			}

			((TextView) findViewById(R.id.home_sliding_menu_monthly_consumption)).setText(mUserInfoControler.getConsumptionThisMonth());
			{// 公共账单消费
				TextView view = ((TextView) findViewById(R.id.home_sliding_menu_public_monthly_consumption));
				String pCost = mUserInfoControler.getPhoneCurMsg().getPublicCost();
				if (!TextUtils.isEmpty(pCost)) {
					view.setText(pCost + "元");
					((View) view.getParent()).setVisibility(View.VISIBLE);
				} else
					((View) view.getParent()).setVisibility(View.GONE);
			}
		} else {
			((TextView) findViewById(R.id.home_sliding_menu_account_balance)).setText(null);
			((TextView) findViewById(R.id.home_sliding_menu_monthly_consumption)).setText(null);
			((View) findViewById(R.id.home_sliding_menu_public_account_balance).getParent()).setVisibility(View.GONE);
		}

	}

	/**
	 * 用户基本信息展示
	 * 
	 * @param isLogined
	 */
	private void updateUserBaseInfo(boolean isLogined) {

		if (isLogined) {
			((TextView) findViewById(R.id.home_sliding_menu_user_name)).setText(mUserInfoControler.getUserPersonName());
			((TextView) findViewById(R.id.home_sliding_menu_brand)).setText(mUserInfoControler.getBrandName());
			((TextView) findViewById(R.id.home_sliding_menu_main_tariff_packages)).setText(mUserInfoControler.getMainMode());
		} else {
			((TextView) findViewById(R.id.home_sliding_menu_user_name)).setText(null);
			((TextView) findViewById(R.id.home_sliding_menu_brand)).setText(null);
			((TextView) findViewById(R.id.home_sliding_menu_main_tariff_packages)).setText(null);
		}
		setStarUserShow();
	}

	/**
	 * 星级展示
	 */
	private void setStarUserShow() {
		String str = mUserInfoControler.getCreditClass();
		if (TextUtils.isEmpty(str))
			((ViewGroup) findViewById(R.id.home_sliding_menu_credits_level).getParent()).setVisibility(View.GONE);
		else {
			int starLevel = Integer.parseInt(str);
			starLevel = Math.min(starLevel, sResIds_starUser.length);
			starLevel = Math.max(starLevel, 1);
			((ViewGroup) findViewById(R.id.home_sliding_menu_credits_level).getParent()).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.home_sliding_menu_credits_level)).setCompoundDrawablesWithIntrinsicBounds(sResIds_starUser[starLevel - 1], 0, 0, 0);
		}
	}

	public boolean isMenuOpen() {
		return mSlidingMenu.isOpen();
	}

	public void toggleSlidingMenu() {
		if (!mSlidingMenu.closePane())
			openSlidingMenu();
	}

	public boolean closeSlidingMenu() {
		return mSlidingMenu.closePane();
	}

	public boolean openSlidingMenu() {
		return mSlidingMenu.openPane();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// updateSlideView();
	}

	/**
	 * 激活亚信后台，获取asiaInfoToken
	 */
	private void doAsiaInfoChannelLogout() {
		Intent intent = new Intent(this, BackgroundService.class);
		intent.putExtra(ExtraKeyConstant.KEY_CASE, ChannelOperTask.METHOD_NAMES[ChannelOperTask.METHOD_LOGOUT]);
		Bundle bundle = new Bundle();
		bundle.putString(ExtraKeyConstant.KEY_PHONE_NUMBER, UserInfoControler.getInstance().getUserName());
		bundle.putString(ExtraKeyConstant.KEY_TOKEN, App.sSettingsPreferences.getAsiaInfoToken());
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		startService(intent);
	}

	private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(AppActionConstant.ACTION_REFRESH_REQUEST)) {
				updateSlideView();
				return;
			}

			if (!action.equals(AppActionConstant.ACTION_REFRESH))
				return;

			byte Case = intent.getByteExtra(ExtraKeyConstant.KEY_CASE, (byte) 0);
			switch (Case) {
			case AppActionConstant.STATE_COMPLETE_PHONE_FREE_QUERY:
				updateFreeCondition(true);
				break;
			case AppActionConstant.STATE_COMPLETE_PHONE_CURRENT_MSG:
				updateBalanceInfo(true);
				break;
			case AppActionConstant.STATE_COMPLETE_ADDITIONAL_TRAFFIC_INFO:
				updateFreeCondition(true);
				break;
			case AppActionConstant.STATE_COMPLETE_GET_CREDITS:
				break;
			case AppActionConstant.STATE_COMPLETE_GET_USER_BASE_INFO:
				updateUserBaseInfo(true);
				break;
			default:
				updateSlideView();
				break;
			}

		}
	};

	private void registerUpdateReciever() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppActionConstant.ACTION_REFRESH);
		registerReceiver(updateReceiver, filter);
	}
}