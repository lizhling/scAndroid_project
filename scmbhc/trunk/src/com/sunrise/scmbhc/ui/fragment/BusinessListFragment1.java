package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.BusinessListAdapter;
import com.sunrise.scmbhc.adapter.SubBusinessListAdapter;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.database.ScmbhcStore;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.view.ScrollAdGallery;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

@SuppressLint({ "ValidFragment", "HandlerLeak" })
public class BusinessListFragment1 extends BaseFragment implements OnItemClickListener, Observer, App.ExtraKeyConstant, OnCheckedChangeListener {
	private static final int QUERY_MAIN_LIST_TOKEN = 1701;
	private static final int QUERY_SUB_LIST_TOKEN = 1702;

	private ListView mSubListView;
	private ListView mMainListview;
	private RadioGroup mRadioGroupMainList;
	private ArrayList<BusinessMenu> mMainBusiessMenuListInfo = new ArrayList<BusinessMenu>();
	private ArrayList<BusinessMenu> mSubBusiessMenuListInfo = new ArrayList<BusinessMenu>();
	private BusinessListAdapter mMainListAdapter;
	private SubBusinessListAdapter mSubListAdapter;
	private BusinessMenu mParentBusinessMenu;
	private ScmbhcDbManager mScmbhcDbManager;
	private BusinessMenusObserver mBusinessMainMenusObserver;
	private ContentResolver mContentResolver;
	private AsyncQueryHandler mQueryHandler;
	RelativeLayout galleryContainer;
	ScrollAdGallery mScrollGallery;

	/**
	 * 二级列表动作中
	 */
	private boolean mIsAmimimg;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		}
	};
	private View m2Page; // 第二页
	private View m1stPage;
	private View mSubListContainer;
	private View mPage2radioGroupContainer;

	public BusinessListFragment1(BusinessMenu mParentBusinessMenu) {
		super();
		this.mParentBusinessMenu = mParentBusinessMenu;
	}

	public BusinessListFragment1() {
		super();
		mParentBusinessMenu = new BusinessMenu();
		mParentBusinessMenu.setId(BusinessMenu.ROOT_BUSINESSMEUN_ID);
	}

	public void onStart() {
		super.onStart();

		if (!UserInfoControler.getInstance().checkUserLoginIn())
			mBaseActivity.setRightButton(getString(R.string.login), mLoginClickListener);
		else
			mBaseActivity.setRightButtonVisibility(View.GONE);

		mBaseActivity.setSearchButtonVisibility(View.VISIBLE);
		App.sScrollsPreferentialInfosObservable.addObserver(this);
	}

	public void onStop() {
		super.onStop();
		App.sScrollsPreferentialInfosObservable.deleteObserver(this);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDatabase();
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_BUSINESS_INFO))
				mMainBusiessMenuListInfo = savedInstanceState.getParcelableArrayList(KEY_BUSINESS_INFO);
		} else {
			mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler, QUERY_MAIN_LIST_TOKEN, mParentBusinessMenu.getId());
		}
	}

	private void initDatabase() {
		mContentResolver = mBaseActivity.getContentResolver();
		// 尝试修复 mContentResolver 空指针异常
		if (mContentResolver == null) {
			mContentResolver = getActivity().getContentResolver();
		}
		mScmbhcDbManager = ScmbhcDbManager.getInstance(mContentResolver);
		mQueryHandler = new QueryHandler(mContentResolver);
		mBusinessMainMenusObserver = new BusinessMenusObserver(mHandler);
		mContentResolver.registerContentObserver(ScmbhcStore.BusinessMenu.CONTENT_URI, true, mBusinessMainMenusObserver);
	}

	public void onSaveInstanceState(Bundle outState) {
		if (mMainBusiessMenuListInfo != null)
			outState.putParcelableArrayList(KEY_BUSINESS_INFO, mMainBusiessMenuListInfo);
		super.onSaveInstanceState(outState);
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_businesslist1, container, false);

		m1stPage = view.findViewById(R.id.page1);
		mSubListContainer = view.findViewById(R.id.sub_list_container);

		mMainListview = (ListView) view.findViewById(R.id.main_list);
		mMainListview.setOnItemClickListener(this);
		mMainListAdapter = new BusinessListAdapter(mBaseActivity, mMainBusiessMenuListInfo);
		mMainListview.setAdapter(mMainListAdapter);
		CommUtil.expandListView(mMainListAdapter, mMainListview);

		mRadioGroupMainList = (RadioGroup) view.findViewById(R.id.radioGroup_mainList);
		mRadioGroupMainList.setOnCheckedChangeListener(this);

		initScrollGallery(view);

		m2Page = view.findViewById(R.id.page2);
		mPage2radioGroupContainer = view.findViewById(R.id.page2radioGroupContainer);

		mSubListView = (ListView) view.findViewById(R.id.sub_list);
		mSubListView.setOnItemClickListener(new CommListItemClickListener());
		mSubListAdapter = new SubBusinessListAdapter(mBaseActivity, mSubBusiessMenuListInfo);
		mSubListView.setAdapter(mSubListAdapter);

		return view;
	}

	private void initScrollGallery(View view) {
		galleryContainer = (RelativeLayout) view.findViewById(R.id.scroll_gallery);
		if (App.sScrollPreferentialInfos == null || App.sScrollPreferentialInfos.size() == 0) {
			galleryContainer.setBackgroundResource(R.drawable.scroll_11);
		} else {
			mScrollGallery = new ScrollAdGallery(mBaseActivity, App.sScrollPreferentialInfos);
			galleryContainer.addView(mScrollGallery.getLayout());
			mScrollGallery.startAutoSwitch();
		}
	}

	public void onResume() {
		super.onResume();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setTitle(getString(R.string.oneself_handle_business));
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
		// baseActivity.setLeftButtonVisibility(View.GONE);

		if (!UserInfoControler.getInstance().checkUserLoginIn())
			mBaseActivity.setRightButton(getString(R.string.login), mLoginClickListener);
		else
			mBaseActivity.setRightButtonVisibility(View.GONE);
	}

	private class CommListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			BusinessMenu businessInfo = (BusinessMenu) adapter.getItemAtPosition(position);
			BaseActivity baseActivity = (BaseActivity) getActivity();
			int menuType = businessInfo.getMenuType();
			if (menuType == BusinessMenu.MENU_TYPE) {
				mRadioGroupMainList.check(-1);
				mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler, QUERY_SUB_LIST_TOKEN, businessInfo.getId());
			} else {
				// 测试web办亚信EDU全年套餐页面
				// businessInfo.setMenuType(businessInfo.WEB_BUSINESS_TYPE);
				// businessInfo.setServiceUrl("file:///android_asset/test.html");
				businessInfo.visitByNewActivity(baseActivity);
			}
		}
	}

	int precolor = 0xffffff;
	AdapterView<?> preadapter = null;
	int prepostion = -1;

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if (m2Page.getVisibility() != View.VISIBLE) {
			mRadioGroupMainList.setOnCheckedChangeListener(null);
			mRadioGroupMainList.removeAllViews();
			LayoutInflater inflater = LayoutInflater.from(mRadioGroupMainList.getContext());

			for (BusinessMenu item : mMainBusiessMenuListInfo) {
				RadioButton child = (RadioButton) inflater.inflate(R.layout.radiobutton_for_business_menu_list, null);
				child.setText(item.getName());
				child.setId((int) item.getId());
				if (item.getIconBitmap() != null) {
					Drawable top = new BitmapDrawable(item.getIconBitmap());

					Drawable[] drawables = child.getCompoundDrawables();
					LogUtlis.showLogD(getClass().getSimpleName(), "drawables = " + drawables);
					if (drawables != null && drawables.length > 2 && drawables[1] != null) {
						top.setBounds(drawables[1].getBounds());
					}
					child.setCompoundDrawables(null, top, null, null);
				}
				mRadioGroupMainList.addView(child, new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
			}

			mRadioGroupMainList.check(-1);

			mRadioGroupMainList.setOnCheckedChangeListener(BusinessListFragment1.this);

			open2rdPage(position);
		}

		// listCommentFun(adapter, view, position);
	}

	private int getTextSize(TextView view, String str) {
		Rect bounds = new Rect();
		view.getPaint().getTextBounds(str, 0, str.length(), bounds);
		return bounds.width();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (m2Page.getVisibility() == View.VISIBLE) {
				close2rdPage();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	class BusinessMenusObserver extends ContentObserver {

		public BusinessMenusObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler, QUERY_MAIN_LIST_TOKEN, mParentBusinessMenu.getId());
		}

	}

	class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			switch (token) {
			case QUERY_MAIN_LIST_TOKEN:
				ArrayList<BusinessMenu> businessMenus = mScmbhcDbManager.cursorToBusinessBusinessMenus(cursor);
				mMainBusiessMenuListInfo.clear();
				mMainBusiessMenuListInfo.addAll(businessMenus);
				mMainListAdapter.notifyDataSetChanged();
				CommUtil.expandListView(mMainListAdapter, mMainListview);
				break;
			case QUERY_SUB_LIST_TOKEN:
				ArrayList<BusinessMenu> subBusinessMenus = mScmbhcDbManager.cursorToBusinessBusinessMenus(cursor);
				mSubBusiessMenuListInfo.clear();
				mSubBusiessMenuListInfo.addAll(subBusinessMenus);
				mSubListAdapter.notifyDataSetChanged();
				break;

			}
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		mScrollGallery.refreshGaller();
	}

	@Override
	public void onCheckedChanged(RadioGroup radiogrouop, int id) {

		int index = radiogrouop.indexOfChild(radiogrouop.findViewById(id));
		if (index != -1)
			mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler, QUERY_SUB_LIST_TOKEN, mMainBusiessMenuListInfo.get(index).getId());
	}

	/**
	 * 打开第二页
	 */
	private void open2rdPage(final int position) {

		m2Page.setVisibility(View.VISIBLE);
		mRadioGroupMainList.check(mRadioGroupMainList.getChildAt(position).getId());
		Animation animation = AnimationUtils.loadAnimation(mBaseActivity, R.anim.slide_right_in);
		mPage2radioGroupContainer.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.slide_left_in));
		mSubListContainer.startAnimation(animation);
	}

	/**
	 * 关闭二级界面
	 */
	private void close2rdPage() {
		if (mIsAmimimg)
			return;

		Animation animation = AnimationUtils.loadAnimation(mBaseActivity, R.anim.slide_right_out);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mIsAmimimg = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				m2Page.setVisibility(View.GONE);
				mIsAmimimg = false;
			}
		});
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setFillBefore(true);

		mPage2radioGroupContainer.startAnimation(AnimationUtils.loadAnimation(mBaseActivity, R.anim.slide_left_out));
		mSubListContainer.startAnimation(animation);
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.BusinessListFragment1;
	}
}
