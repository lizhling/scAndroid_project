package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.HorizontalListAdapter;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.PhoneFreeQuery;
import com.sunrise.scmbhc.entity.PhoneFreeQuery.EmumState;
import com.sunrise.scmbhc.entity.UseCondition;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.GetPreferentialInfosTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.AppRecommendedActivity;
import com.sunrise.scmbhc.ui.activity.HomePageGuideActivity;
import com.sunrise.scmbhc.ui.activity.LocationOverlayActivity;
import com.sunrise.scmbhc.ui.activity.LoginActivity;
import com.sunrise.scmbhc.ui.activity.MainActivity;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.ui.view.GridGallery.ChildGridViewItemClickListener;
import com.sunrise.scmbhc.ui.view.HorizontalListView;
import com.sunrise.scmbhc.ui.view.HorizontalListView.OnListItemClickListener;
import com.sunrise.scmbhc.ui.view.RoundProgressBar;
import com.sunrise.scmbhc.ui.view.ScrollAdGallery;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.UnitUtils;

public class HomeFragment extends BaseFragment implements Observer {
	private static final int REQUEST_CODE_GO_APPOINT_PAGE = 0x331;// 前往某个功能点的请求
	private static final int REQUEST_CODE_BUTTON_LOGIN = 0x332;// 按钮登录请求

	private static ArrayList<BusinessMenu> sHomeBusiness;
	private ViewGroup galleryContainer;
	private ScrollAdGallery mGalleryHelper;
	private RoundProgressBar mRemainderTrafficRoundProgress;
	// private TextView mUnloginUserBalanceView;
	private TextView mUnloginRemainderTrafficView;
	private LinearLayout mUnloginScorePanel;
	// private ViewGroup mLoginUserBalanceInfoPanel;
	private ViewGroup mLoginRemainderTrafficInfoPanel;
	private ViewGroup mLoginScorePanel;
	// private TextView mUserBalanceValueView;
	private TextView mRemainderTrafficValueView;
	private TextView mScoreValueView;

	/**
	 * 用户余额，充值按钮
	 */
	private TextView mUserBalanceText;

	/**
	 * 充值利率显示
	 */
	private TextView mDiscountRates;

	private HorizontalListView mHomeBusinessListView;
	private String mUnknowStr;
	protected final OnClickListener mLoginClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (UserInfoControler.getInstance().checkUserLoginIn()) {
				MainActivity.getInstance().toggleSlidingMenu();
			} else {
				startActivityForResult(new Intent(mBaseActivity, LoginActivity.class), REQUEST_CODE_BUTTON_LOGIN);
			}
		}
	};
	// private GridGallery mGridGallery;
	private ChildGridViewItemClickListener mChildGridViewItemClickListener = new ChildGridViewItemClickListener() {
		@Override
		public void onItemClik(AdapterView<?> arg0, View arg1, int position, long id, List<BusinessMenu> list) {
			if (goAppointedPage(position))
				return;
			BusinessMenu businessMenu = list.get(position);
			businessMenu.visitByNewActivity(mBaseActivity);
		}

	};

	public void onStart() {
		super.onStart();
		mBaseActivity.setLeftButtonVisibility(View.GONE);
		mBaseActivity.setHomeLeftButton(mLoginClickListener);
		mBaseActivity.setHomeLRightButton(mQRCodeClickListener);
		mBaseActivity.setTitle(R.string.app_name);

		{// 刷新充值利率
			String rates = App.sSettingsPreferences.getTopupRates();
			if (TextUtils.isEmpty(rates))
				mDiscountRates.setVisibility(View.GONE);
			else {
				float mRateOfDiscount = Float.parseFloat(rates);
				if (mRateOfDiscount == 1)// 无折扣不显示
					mDiscountRates.setVisibility(View.GONE);
				else
					mDiscountRates.setVisibility(View.VISIBLE);

				mDiscountRates.setText(CommUtil.subZeroAndDot(String.format("%.2f", mRateOfDiscount * 10)) + "折");
			}
		}

		if (App.sSettingsPreferences.getGuideVersion() < App.sAPKVersionCode) {// 新增首页导航。通过版本号来判断，每次升级版本，都显示一次
			startActivity(new Intent(mBaseActivity, HomePageGuideActivity.class));
			App.sSettingsPreferences.setGuideVersion(App.sAPKVersionCode);
		}
		refreshUserInfo();
		registerUpdateReciever();
	};

	public void onStop() {
		super.onStop();
		unregisterUpdateReciever();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUnknowStr = getString(R.string.unknow);
		initHomeBusinessMenu();

		App.sScrollsPreferentialInfosObservable.addObserver(new Observer() {

			@Override
			public void update(Observable arg0, Object arg1) {
				if (mGalleryHelper != null)
					mGalleryHelper.refreshGaller();
			}
		});
		CommonlyBusinessObserver commonlyBusinessObserver = new CommonlyBusinessObserver();
		App.sCommonlyBusinessObservable.addObserver(commonlyBusinessObserver);
	}

	private void initHomeBusinessMenu() {
		if (sHomeBusiness == null) {
			sHomeBusiness = new ArrayList<BusinessMenu>();
			BusinessMenu homeBusinessMenu1 = new BusinessMenu();
			homeBusinessMenu1.setName(getString(R.string.myBusiness));
			homeBusinessMenu1.setIconRes(R.drawable.home_mybusiness);
			// homeBusinessMenu1.setIconBitmap(getBitmap(R.drawable.home_business_1));
			BusinessMenu homeBusinessMenu2 = new BusinessMenu();
			homeBusinessMenu2.setName(getString(R.string.historyBill));
			homeBusinessMenu2.setIconRes(R.drawable.home_historybill);
			BusinessMenu homeBusinessMenu3 = new BusinessMenu();
			homeBusinessMenu3.setName(getString(R.string.reservation_number));
			homeBusinessMenu3.setIconRes(R.drawable.home_prepare);
			BusinessMenu homeBusinessMenu4 = new BusinessMenu();
			homeBusinessMenu4.setName(getString(R.string.application_recommendation));
			homeBusinessMenu4.setIconRes(R.drawable.home_recommended);
			sHomeBusiness.add(homeBusinessMenu1);
			sHomeBusiness.add(homeBusinessMenu2);
			sHomeBusiness.add(homeBusinessMenu3);
			sHomeBusiness.add(homeBusinessMenu4);
		}
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home, container, false);
		initScrollGalleryView(view);
		// 初始化用户余额，余量，积分信息
		initHomeUserInfoView(view);
		// 初始化横向列表
		initBusinessListView(view);
		// mGridGallery = (GridGallery) view.findViewById(R.id.grid_gallery);
		// mGridGallery.setNumbersOfPage(8);
		// mGridGallery.setChildGridViewItemClickListener(mChildGridViewItemClickListener);
		// mGridGallery.initGallery(App.sCommonlyUsedBusiness, 4);
		/*
		 * ((ImageView)
		 * view.findViewById(R.id.hot_sell_phone)).setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Uri uri =
		 * Uri.parse("http://shop.10086.cn/iphone/5s/311.html"); Intent intent =
		 * new Intent(Intent.ACTION_VIEW, uri);
		 * mBaseActivity.startActivity(intent); } });
		 */
		return view;
	}

	private void initBusinessListView(View view) {
		mHomeBusinessListView = (HorizontalListView) view.findViewById(R.id.home_business_list);
		HorizontalListAdapter horizontalListAdapter = new HorizontalListAdapter(mBaseActivity, sHomeBusiness);
		mHomeBusinessListView.setAdapter(horizontalListAdapter);
		mHomeBusinessListView.registerListItemClickListener(new OnListItemClickListener() {

			@Override
			public void onClick(View v, int position) {
				// 防快速点击双击判断
				if (CommUtil.isFastDoubleClick()) {
					return;
				}
				goAppointedPage(position);
			}
		});
	}

	private void initHomeUserInfoView(View view) {
		mRemainderTrafficRoundProgress = (RoundProgressBar) view.findViewById(R.id.remainder_traffic_roundProgressBar);
		// mUnloginUserBalanceView = (TextView)
		// view.findViewById(R.id.unlogin_user_balance_text);
		mUnloginRemainderTrafficView = (TextView) view.findViewById(R.id.unlongin_remainder_traffic_text);
		mUnloginScorePanel = (LinearLayout) view.findViewById(R.id.unlogin_score_panel);
		// mLoginUserBalanceInfoPanel = (ViewGroup)
		// view.findViewById(R.id.login_user_balance_info_panel);
		mLoginRemainderTrafficInfoPanel = (ViewGroup) view.findViewById(R.id.login_remainder_traffic_info_panel);
		mLoginScorePanel = (ViewGroup) view.findViewById(R.id.login_score_panel);
		// mUserBalanceValueView = (TextView)
		// view.findViewById(R.id.user_balance_value);
		mRemainderTrafficValueView = (TextView) view.findViewById(R.id.remainder_traffic_value);
		mScoreValueView = (TextView) view.findViewById(R.id.current_score_value);

		mUserBalanceText = (TextView) view.findViewById(R.id.user_balance_text);
		view.findViewById(R.id.user_balance).setOnClickListener(mClickListenerForTopUp);

		mDiscountRates = (TextView) view.findViewById(R.id.textView_discount_rates);
		// view.findViewById(R.id.unlogin_user_balance_text).setOnClickListener(mClickListenerForTopUp);
		// view.findViewById(R.id.login_user_balance_info_panel).setOnClickListener(mClickListenerForTopUp);
		// view.findViewById(R.id.user_balance_small_text).setOnClickListener(mClickListenerForTopUp);
		// view.findViewById(R.id.user_balance_value).setOnClickListener(mClickListenerForTopUp);

		view.findViewById(R.id.remainder_traffic).setOnClickListener(mClickListenerForTraffic);
		view.findViewById(R.id.unlongin_remainder_traffic_text).setOnClickListener(mClickListenerForTraffic);
		view.findViewById(R.id.login_remainder_traffic_info_panel).setOnClickListener(mClickListenerForTraffic);
		view.findViewById(R.id.remainder_traffic_small_text).setOnClickListener(mClickListenerForTraffic);
		view.findViewById(R.id.remainder_traffic_value).setOnClickListener(mClickListenerForTraffic);
		view.findViewById(R.id.remainder_traffic_roundProgressBar).setOnClickListener(mClickListenerForTraffic);

		view.findViewById(R.id.score_panel).setOnClickListener(mClickListenerForCredits);
		view.findViewById(R.id.unlogin_score_panel).setOnClickListener(mClickListenerForCredits);
		view.findViewById(R.id.login_score_panel).setOnClickListener(mClickListenerForCredits);
		view.findViewById(R.id.current_score_label).setOnClickListener(mClickListenerForCredits);
		view.findViewById(R.id.current_score_value).setOnClickListener(mClickListenerForCredits);
	}

	private void initScrollGalleryView(View view) {
		galleryContainer = (ViewGroup) view.findViewById(R.id.scroll_gallery);
		galleryContainer.setBackgroundResource(R.drawable.scroll_11);
		mGalleryHelper = new ScrollAdGallery(mBaseActivity, App.sScrollPreferentialInfos);
		galleryContainer.addView(mGalleryHelper.getLayout());
		mGalleryHelper.startAutoSwitch();

	}

	@Override
	public void onResume() {
		super.onResume();
		// UserInfoControler.getInstance().addObserver(mRefreshCallBack);
	}

	public void onPause() {
		super.onPause();
		// UserInfoControler.getInstance().deleteObserver(mRefreshCallBack);
	}

	protected void refreshUserInfo() {
		UserInfoControler userInfoControler = UserInfoControler.getInstance();
		if (!userInfoControler.checkUserLoginIn()) {
			// mUnloginUserBalanceView.setVisibility(View.VISIBLE);
			mUnloginRemainderTrafficView.setVisibility(View.VISIBLE);
			mUnloginScorePanel.setVisibility(View.VISIBLE);

			mRemainderTrafficRoundProgress.setProgress(0);
			// mLoginUserBalanceInfoPanel.setVisibility(View.GONE);
			mLoginRemainderTrafficInfoPanel.setVisibility(View.GONE);
			mLoginScorePanel.setVisibility(View.GONE);

			// 设置充值选项
			mUserBalanceText.setTextSize(UnitUtils.px2dip(mBaseActivity, getResources().getDimension(R.dimen.text_size_1)));
			mUserBalanceText.setTypeface(Typeface.DEFAULT_BOLD);
			mUserBalanceText.setText(R.string.user_balance_warp);

			mBaseActivity.setHomeLeftButtonBackgroundsResource(R.drawable.selector_btn_user_login);
		} else {
			// mUnloginUserBalanceView.setVisibility(View.GONE);
			mUnloginRemainderTrafficView.setVisibility(View.GONE);
			mUnloginScorePanel.setVisibility(View.GONE);

			// mLoginUserBalanceInfoPanel.setVisibility(View.VISIBLE);
			mLoginRemainderTrafficInfoPanel.setVisibility(View.VISIBLE);
			mLoginScorePanel.setVisibility(View.VISIBLE);
			userInfoControler.getPhoneFreeQuery();
			// 显示余额
			String curBalance = userInfoControler.getCurBalance();
			mUserBalanceText.setTextSize(UnitUtils.px2dip(mBaseActivity, getResources().getDimension(R.dimen.text_size_2)));
			if (checkUserInfoValid(curBalance)) {
				String pBalance = userInfoControler.getPhoneCurMsg().getPublicBalance();
				mUserBalanceText.setTypeface(Typeface.DEFAULT);
				if (TextUtils.isEmpty(pBalance)) {
					mUserBalanceText.setText(Html.fromHtml(String.format("当前<br>余额<br><b>%s</b>", curBalance)));
				} else {
					String source = String.format("当前余额<br><b>%s</b><br>公共账户<br><b>%s元</b>", curBalance, pBalance);
					CharSequence charsquene = Html.fromHtml(source);
					mUserBalanceText.setText(charsquene);
				}
			} else {
				mUserBalanceText.setText("获取\n失败");
			}
			// int curScore = userInfoControler.getMCredits();
			// if (curScore == -1) {
			// mScoreValueView.setText(getString(R.string.connect_failed));
			// } else if (curScore == -2) {
			// mScoreValueView.setText(getString(R.string.not_opened));
			// } else {
			mScoreValueView.setText(userInfoControler.getMCredits());
			// }
			UseCondition useCondition = userInfoControler.getConditionFlow(null);
			double usedCondition = useCondition.getUsed();
			double totalCondition = useCondition.getTotle();
			double remainderCondition = useCondition.getSurplus();//totalCondition - usedCondition;
			if (usedCondition < 0) {
				mRemainderTrafficValueView.setText(mUnknowStr);
			} else {
				int pr = (int) (remainderCondition * 100 / totalCondition);
				mRemainderTrafficRoundProgress.setProgress(pr);
				mRemainderTrafficValueView.setText(useCondition.getSurplusString());
			}

			mRemainderTrafficValueView.setText(useCondition.getSurplusString());

			mBaseActivity.setHomeLeftButtonBackgroundsResource(R.drawable.user_info_bg);
		}
	}

	protected void refreshScoreInfo(boolean isLogined) {
		if (!isLogined) {
			mLoginScorePanel.setVisibility(View.GONE);
			mUnloginScorePanel.setVisibility(View.VISIBLE);
			return;
		}
		mUnloginScorePanel.setVisibility(View.GONE);
		mLoginScorePanel.setVisibility(View.VISIBLE);
		mScoreValueView.setText(UserInfoControler.getInstance().getMCredits());
	}

	protected void refreshRemanderView(boolean isLogined) {
		if (!isLogined) {
			mLoginRemainderTrafficInfoPanel.setVisibility(View.GONE);
			// 设置充值选项
			mUserBalanceText.setTextSize(UnitUtils.px2dip(mBaseActivity, getResources().getDimension(R.dimen.text_size_1)));
			mUserBalanceText.setTypeface(Typeface.DEFAULT_BOLD);
			mUserBalanceText.setText(R.string.user_balance_warp);
			return;
		}

		UserInfoControler userInfoControler = UserInfoControler.getInstance();

		userInfoControler.getPhoneFreeQuery();
		// 显示余额
		String curBalance = userInfoControler.getCurBalance();
		mUserBalanceText.setTextSize(UnitUtils.px2dip(mBaseActivity, getResources().getDimension(R.dimen.text_size_2)));
		if (checkUserInfoValid(curBalance)) {
			String pBalance = userInfoControler.getPhoneCurMsg().getPublicBalance();
			mUserBalanceText.setTypeface(Typeface.DEFAULT);
			if (TextUtils.isEmpty(pBalance)) {
				mUserBalanceText.setText(Html.fromHtml(String.format("当前<br>余额<br><b>%s</b>", curBalance)));
			} else {
				String source = String.format("当前余额<br><b>%s</b><br>公共账户<br><b>%s元</b>", curBalance, pBalance);
				CharSequence charsquene = Html.fromHtml(source);
				mUserBalanceText.setText(charsquene);
			}
		} else {
			mUserBalanceText.setText("获取\n失败");
		}
	}

	/**
	 * 刷新用户余量信息
	 * 
	 * @param isLogined
	 */
	protected void refreshFreeCondition(boolean isLogined) {

		if (!isLogined) {
			mUnloginRemainderTrafficView.setVisibility(View.VISIBLE);
			mRemainderTrafficRoundProgress.setProgress(0);
			mLoginRemainderTrafficInfoPanel.setVisibility(View.GONE);
			return;
		}

		UseCondition useCondition = UserInfoControler.getInstance().getConditionFlow(null);
		double usedCondition = useCondition.getUsed();
		double totalCondition = useCondition.getTotle();
		double remainderCondition = useCondition.getSurplus();
		if (usedCondition < 0) {
			mRemainderTrafficValueView.setText(mUnknowStr);
		} else {
			int pr = (int) (remainderCondition * 100 / totalCondition);
			mRemainderTrafficRoundProgress.setProgress(pr);
			mRemainderTrafficValueView.setText(useCondition.getSurplusString());
		}

		mRemainderTrafficValueView.setText(useCondition.getSurplusString());
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
				App.sScrollsPreferentialInfosObservable.refresh();
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

	private void doGetPreferentialInfos() {
		GenericTask getScrollsPreferentialInfosTask = new GetPreferentialInfosTask(mBaseActivity);
		getScrollsPreferentialInfosTask.setListener(mPreferentialInfosListener);
		getScrollsPreferentialInfosTask.execute();
		App.sTaskManager.addObserver(getScrollsPreferentialInfosTask);
	}

	private boolean checkUserInfoValid(String info) {
		if (info.equals(EmumState.ANALYSIS_FAILED.getName()) || info.equals(EmumState.CONNECT_ERROR.getName()) || info.equals(EmumState.NO_INIT.getName())
				|| info.equals(PhoneFreeQuery.UNKNOWN)) {
			return false;
		}
		return true;
	}

	@Override
	public void update(Observable observable, Object data) {
		if (mGalleryHelper != null) {
			mGalleryHelper.refreshGaller();
		}

	}

	public class CommonlyBusinessObserver implements Observer {

		@Override
		public void update(Observable observable, Object data) {
			// mGridGallery.initGallery(App.sCommonlyUsedBusiness, 4);
		}

	}

	private boolean goAppointedPage(int position) {

		if (MainActivity.getInstance().isMenuOpen()) {
			return false;
		}

		boolean result = true;

		Class<? extends BaseFragment> fragment = null;
		switch (position) {
		case 0:
			fragment = MyBusinessFragment2.class;// 我的业务
			break;
		case 1:
			fragment = BillApxPageQueryFragment.class;// 我的帐单
			break;
		case 2:
			mBaseActivity.startActivity(new Intent(mBaseActivity, LocationOverlayActivity.class));// 预约取号
			return true;
		case 3:
			mBaseActivity.startActivity(new Intent(mBaseActivity, AppRecommendedActivity.class));// 应用推荐
			break;
		case 4:
			fragment = TrafficServeFragement.class;// 流量服务
			break;
		case 5:
			fragment = CreditsExchangeFragment.class;// 积分兑换
			break;
		case 6:
			fragment = TopUpServeFragment.class;// 充值//TestFragment.class;
			break;
		default:
			result = false;
			break;
		}
		if (result && fragment != null) {
			Intent intent = new Intent(mBaseActivity, SingleFragmentActivity.class);
			intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, fragment);
			mBaseActivity.startActivity(intent);
		}

		return result;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (App.sPreferentialListInfos != null && App.sPreferentialListInfos.size() == 0) {
				doGetPreferentialInfos();
			}
			if (requestCode == REQUEST_CODE_GO_APPOINT_PAGE && data != null) {
				goAppointedPage(data.getIntExtra("position", -1));
			}

			else if (requestCode == REQUEST_CODE_BUTTON_LOGIN)
				MainActivity.getInstance().openSlidingMenu();
		}
	}

	private OnClickListener mClickListenerForTopUp = new OnClickListener() {// 充值
		@Override
		public void onClick(View arg0) {
			goAppointedPage(6);
		}
	};
	private OnClickListener mClickListenerForTraffic = new OnClickListener() {// 流量

		@Override
		public void onClick(View arg0) {
			goAppointedPage(4);
		}
	};
	private OnClickListener mClickListenerForCredits = new OnClickListener() {// 积分

		@Override
		public void onClick(View arg0) {
			goAppointedPage(5);
		}
	};

	private Observer mRefreshCallBack = new Observer() {

		@Override
		public void update(Observable observable, Object data) {
			refreshUserInfo();
		}
	};

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.HomeFragment;
	}
}
