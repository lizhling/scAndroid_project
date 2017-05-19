package com.sunrise.micromarketing.ui.activity;

import java.util.ArrayList;

import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.adapter.BusinessListAdapter;
import com.sunrise.micromarketing.database.ScmbhcDbManager;
import com.sunrise.micromarketing.database.ScmbhcStore;
import com.sunrise.micromarketing.entity.BusinessMenu;
import com.sunrise.micromarketing.ui.adapter.SubBusinessListAdapter;
import com.sunrise.micromarketing.utils.LogUtlis;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 业务办理列表界面
 * 
 * @author 珩
 * 
 */
public class BusinessActivity extends BaseActivity implements
		OnItemClickListener, OnCheckedChangeListener {
	private ListView mMainListview;
	private ListView mSubListView;
	private RadioGroup mRadioGroupMainList;

	private ArrayList<BusinessMenu> mMainBusiessMenuListInfo = new ArrayList<BusinessMenu>();
	private ArrayList<BusinessMenu> mSubBusiessMenuListInfo = new ArrayList<BusinessMenu>();
	private BusinessListAdapter mMainListAdapter;
	private SubBusinessListAdapter mSubListAdapter;

	private View m2Page; // 第二页
	private View mSubListContainer;
	private View mPage2radioGroupContainer;
	/**
	 * 二级列表动作中
	 */
	private boolean mIsAmimimg;

	/**
	 * 数据库管理
	 */
	private ScmbhcDbManager mScmbhcDbManager;
	private AsyncQueryHandler mQueryHandler;
	private BusinessMenusObserver mBusinessMainMenusObserver;

	private static final int QUERY_MAIN_LIST_TOKEN = 1701;
	private static final int QUERY_SUB_LIST_TOKEN = 1702;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_businesslist);

		mMainListview = (ListView) findViewById(R.id.main_list);
		mMainListview.setOnItemClickListener(this);
		mMainListAdapter = new BusinessListAdapter(this,
				mMainBusiessMenuListInfo);
		mMainListview.setAdapter(mMainListAdapter);

		mSubListView = (ListView) findViewById(R.id.sub_list);
		mSubListView.setOnItemClickListener(this);
		mSubListAdapter = new SubBusinessListAdapter(this,
				mSubBusiessMenuListInfo);
		mSubListView.setAdapter(mSubListAdapter);

		mRadioGroupMainList = (RadioGroup) findViewById(R.id.radioGroup_mainList);
		mRadioGroupMainList.setOnCheckedChangeListener(this);

		m2Page = findViewById(R.id.page2);
		mSubListContainer = findViewById(R.id.sub_list_container);
		mPage2radioGroupContainer = findViewById(R.id.page2radioGroupContainer);

		setTitle(getIntent().getStringExtra(Intent.EXTRA_TITLE));
		setTitleBarLeftClick(new OnClickListener() {
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		setTitleBarRightClick(null);

		initData(savedInstanceState);
	}

	protected void onStop() {
		super.onStop();
	}

	public void onSaveInstanceState(Bundle outState) {
		if (mMainBusiessMenuListInfo != null)
			outState.putParcelableArrayList(Intent.EXTRA_TEMPLATE,
					mMainBusiessMenuListInfo);
		super.onSaveInstanceState(outState);
	}

	private void initData(Bundle savedInstanceState) {
		mScmbhcDbManager = ScmbhcDbManager.getInstance(getContentResolver());
		mQueryHandler = new QueryHandler(getContentResolver());
		mBusinessMainMenusObserver = new BusinessMenusObserver(mHandler);
		getContentResolver().registerContentObserver(
				ScmbhcStore.BusinessMenu.CONTENT_URI, true,
				mBusinessMainMenusObserver);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(Intent.EXTRA_TEMPLATE)) {
			mMainBusiessMenuListInfo = savedInstanceState
					.getParcelableArrayList(Intent.EXTRA_TEMPLATE);
		} else {
			mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler,
					QUERY_MAIN_LIST_TOKEN, BusinessMenu.ROOT_BUSINESSMEUN_ID);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {

		if (adapter.getId() == R.id.main_list) {// 首页列表
			mRadioGroupMainList.setOnCheckedChangeListener(null);
			mRadioGroupMainList.removeAllViews();
			LayoutInflater inflater = LayoutInflater.from(mRadioGroupMainList
					.getContext());

			for (BusinessMenu item : mMainBusiessMenuListInfo) {
				RadioButton child = (RadioButton) inflater.inflate(
						R.layout.radiobutton_for_business_menu_list, null);
				child.setText(item.getName());
				child.setId((int) item.getId());
				if (item.getIconBitmap() != null) {
					Drawable top = new BitmapDrawable(getResources() ,item.getIconBitmap());

					Drawable[] drawables = child.getCompoundDrawables();
					LogUtlis.d(getClass().getSimpleName(), "drawables = "
							+ drawables);
					if (drawables != null && drawables.length > 2
							&& drawables[1] != null) {
						top.setBounds(drawables[1].getBounds());
					}
					child.setCompoundDrawables(null, top, null, null);
				}
				mRadioGroupMainList.addView(child, new RadioGroup.LayoutParams(
						RadioGroup.LayoutParams.MATCH_PARENT,
						RadioGroup.LayoutParams.WRAP_CONTENT));
			}

			mRadioGroupMainList.check(-1);

			mRadioGroupMainList.setOnCheckedChangeListener(this);

			open2rdPage(position);
		}

		else if (adapter.getId() == R.id.sub_list) {
			gotoBusinessPage((BusinessMenu) adapter.getItemAtPosition(position));
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup radiogroup, int id) {
		int index = radiogroup.indexOfChild(radiogroup.findViewById(id));
		if (index != -1)
			mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler,
					QUERY_SUB_LIST_TOKEN, mMainBusiessMenuListInfo.get(index)
							.getId());
	}

	/**
	 * 打开第二页
	 */
	private void open2rdPage(final int position) {

		m2Page.setVisibility(View.VISIBLE);
		mRadioGroupMainList.check(mRadioGroupMainList.getChildAt(position)
				.getId());
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_right_in);
		mPage2radioGroupContainer.startAnimation(AnimationUtils.loadAnimation(
				this, R.anim.slide_left_in));
		mSubListContainer.startAnimation(animation);
	}

	/**
	 * 关闭二级界面
	 */
	private void close2rdPage() {
		if (mIsAmimimg)
			return;

		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_right_out);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mIsAmimimg = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

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

		mPage2radioGroupContainer.startAnimation(AnimationUtils.loadAnimation(
				this, R.anim.slide_left_out));
		mSubListContainer.startAnimation(animation);
	}

	public void onBackPressed() {
		if (m2Page.getVisibility() == View.VISIBLE) {
			close2rdPage();
			return;
		}

		MainActivity.getInstance().setCurrentTab(0);
	}

	class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			switch (token) {
			case QUERY_MAIN_LIST_TOKEN:
				ArrayList<BusinessMenu> businessMenus = mScmbhcDbManager
						.cursorToBusinessBusinessMenus(cursor);
				mMainBusiessMenuListInfo.clear();
				mMainBusiessMenuListInfo.addAll(businessMenus);
				mMainListAdapter.notifyDataSetChanged();
				break;
			case QUERY_SUB_LIST_TOKEN:
				ArrayList<BusinessMenu> subBusinessMenus = mScmbhcDbManager
						.cursorToBusinessBusinessMenus(cursor);
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

	class BusinessMenusObserver extends ContentObserver {

		public BusinessMenusObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler,
					QUERY_MAIN_LIST_TOKEN, BusinessMenu.ROOT_BUSINESSMEUN_ID);
		}

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		}
	};

	/**
	 * 前往业务办理页面
	 */
	private void gotoBusinessPage(BusinessMenu business) {
		switch (business.getMenuType()) {
		case BusinessMenu.MENU_TYPE:
			mRadioGroupMainList.check(-1);
			mScmbhcDbManager.startQueryBusinessMenu(mQueryHandler,
					QUERY_SUB_LIST_TOKEN, business.getId());
			break;
		case BusinessMenu.WEB_BUSINESS_TYPE: {
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(Intent.EXTRA_TEXT, business.getServiceUrl());
			intent.putExtra(Intent.EXTRA_TITLE, business.getName());
			startActivity(intent);
		}
			break;
		case BusinessMenu.LOCAL_BUSINESS_TYPE: {
			Intent intent = new Intent(this,
					DefaultBusinessDetailActivity.class);
			intent.putExtra(Intent.EXTRA_SUBJECT, business);
			startActivity(intent);
		}
			break;
		default:
			break;
		}
	}
}
