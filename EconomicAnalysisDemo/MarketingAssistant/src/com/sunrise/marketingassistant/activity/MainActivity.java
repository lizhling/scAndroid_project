package com.sunrise.marketingassistant.activity;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.TabContent;
import com.sunrise.marketingassistant.entity.TabContentManager;
import com.sunrise.marketingassistant.fragment.AboutFragment;
import com.sunrise.marketingassistant.fragment.BaseFragment;
import com.sunrise.marketingassistant.fragment.ChannelListFragment;
import com.sunrise.marketingassistant.fragment.ChannelTreeFragment;
import com.sunrise.marketingassistant.fragment.FavoriteChannelFragment;
import com.sunrise.marketingassistant.fragment.LocationOverlayFragment;
import com.sunrise.marketingassistant.fragment.MipcaCaptureFragment;
import com.sunrise.marketingassistant.fragment.OfflineMapFragment;
import com.sunrise.marketingassistant.fragment.PickMessageListFragment;
import com.sunrise.marketingassistant.fragment.RemunerationFragment;
import com.sunrise.marketingassistant.fragment.RegisteTrajectoryFragment;
import com.sunrise.marketingassistant.fragment.WebViewFragment2;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.javascript.utils.LogUtlis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, OnClickListener, ExtraKeyConstant {
	protected BaseFragment mCurrentFragment;
	private RadioGroup mTabWidget;
	private RadioGroup mTabWidgetTop;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_main);

		initView();

		setTabIndex(mTabWidget, 0);
	}

	public void onDestroy() {
		super.onDestroy();
		CommUtil.exit(this);
	}

	private void initView() {
		mTabWidget = (RadioGroup) findViewById(R.id.radioGroupBottom);
		// mTabWidget.setOnCheckedChangeListener(this);

		mTabWidgetTop = (RadioGroup) findViewById(R.id.radioGroupTop);
		// mTabWidgetTop.setOnCheckedChangeListener(this);

		((RadioButton) findViewById(R.id.radioButton_channelMap)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radioButton_remuneration)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radioButton_channelList)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radioButton_myChannel)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radioButton_ChannelTree)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radioButton_favorite)).setOnCheckedChangeListener(this);

		findViewById(R.id.btn_more).setOnClickListener(this);
	}

	private PopupWindow mMorePopupWindow;

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	private void initMorePopupWin() {
		View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_window_more, null);

		view.findViewById(R.id.btn_about).setOnClickListener(this);
		view.findViewById(R.id.btn_exit).setOnClickListener(this);
		view.findViewById(R.id.btn_history).setOnClickListener(this);
		view.findViewById(R.id.btn_share).setOnClickListener(this);
		view.findViewById(R.id.btn_pick_message).setOnClickListener(this);
		view.findViewById(R.id.btn_offlineMap).setOnClickListener(this);

		mMorePopupWindow = new PopupWindow(view, -2, -2, false);
		mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mMorePopupWindow.setFocusable(true);
		mMorePopupWindow.setOutsideTouchable(false);
		mMorePopupWindow.setAnimationStyle(R.style.MorePopupAnimation);
	}

	private void hideMorePopWindow() {
		if (mMorePopupWindow != null && mMorePopupWindow.isShowing())
			mMorePopupWindow.dismiss();
	}

	private void showMorePopWindow(View v) {
		if (mMorePopupWindow == null) {
			initMorePopupWin();
		}
		int height = ((View) v.getParent()).getHeight();
		mMorePopupWindow.showAsDropDown(v, 0, -mMorePopupWindow.getHeight() - height - height);
	}

	// @Override
	// public void onCheckedChanged(RadioGroup group, int id) {
	// switch (id) {
	// case R.id.radioButton_channelMap:
	// showFragmentWebview(0);
	// mTabWidgetTop.setVisibility(View.GONE);
	// break;
	// case R.id.radioButton_myChannel:
	// mTabWidgetTop.setVisibility(View.VISIBLE);
	// showContentWithTopWidget();
	// break;
	// case R.id.radioButton_remuneration:
	// showFragmentWebview(4);
	// mTabWidgetTop.setVisibility(View.GONE);
	// break;
	// case R.id.radioButton_channelList:
	// showFragmentWebview(1);
	// break;
	// case R.id.radioButton_ChannelTree:
	// showFragmentWebview(2);
	// break;
	// case R.id.radioButton_favorite:
	// showFragmentWebview(3);
	// break;
	// default:
	// break;
	// }
	//
	// }

	private void showContentWithTopWidget() {

		int index = getTabIndex(mTabWidgetTop);
		if (index == -1) {
			setTabIndex(mTabWidgetTop, 0);
		} else {
			final int[] idsOfPage = { ID_TAB_ITEM_CHANNEL_LIST, ID_TAB_ITEM_CHANNEL_TREE, ID_TAB_ITEM_CHANNEL_COLLECT };
			showFragmentWebview(idsOfPage[index]);
		}

	}

	private void showFragmentWebview(int id) {

		BaseFragment fragment = null;

		TabContent item = TabContentManager.getInstance(this).getTabContent(id);
		if (item != null && item.getType() == 0) {
			fragment = new WebViewFragment2();
			Bundle args = new Bundle();
			args.putString(KEY_CONTENT, null);
			args.putString(KEY_LAST_MODIFY, item.getLastModify());
			args.putString(Intent.EXTRA_TEXT, item.getZipContent());
			fragment.setArguments(args);
			// fragment.startFragment(this, R.id.fragmentContainer);
		} else
			// 原生的切换
			switch (id) {
			case ID_TAB_ITEM_CHANNEL_MAP: {
				fragment = new // LocationFragment();
				LocationOverlayFragment();
				Bundle bundle = new Bundle();
				bundle.putString(KEY_URL, JsonUtils.writeObjectToJsonStr(TabContentManager.getInstance(this).getTabContent(ID_TAB_ITEM_CHANNEL_MAP)));
				fragment.setArguments(bundle);
			}
				break;
			case ID_TAB_ITEM_CHANNEL_LIST:
				fragment = new ChannelListFragment();
				break;
			case ID_TAB_ITEM_CHANNEL_TREE:
				fragment = new ChannelTreeFragment();
				break;
			case ID_TAB_ITEM_CHANNEL_REMUNERATION:
				fragment = new RemunerationFragment();
				break;
			case ID_TAB_ITEM_CHANNEL_SCAN: {
				fragment = new MipcaCaptureFragment();
				Bundle bundle = new Bundle();
				bundle.putString(KEY_URL, JsonUtils.writeObjectToJsonStr(TabContentManager.getInstance(this).getTabContent(ID_TAB_ITEM_CHANNEL_SCAN)));
				fragment.setArguments(bundle);
			}
				break;
			case ID_TAB_ITEM_CHANNEL_COLLECT:
				fragment = new FavoriteChannelFragment();
				break;
			default:
				return;
			}

		fragment.startFragment(this, R.id.fragmentContainer);
		mCurrentFragment = fragment;

	}

	private long firstExitingTime;

	private void setTabIndex(RadioGroup group, int index) {
		int id = index;
		if (index != -1) {
			id = group.getChildAt(index).getId();
		}
		group.check(id);
	}

	private int getTabIndex(RadioGroup group) {
		int id = group.getCheckedRadioButtonId();
		if (id != -1) {
			return group.indexOfChild(group.findViewById(id));
		}
		return -1;
	}

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if (onBack() == 1) {
	// finish();// eixtApp();
	// }
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public void onBackPressed() {
		if (mCurrentFragment == null || !mCurrentFragment.onBackPressed())
			onExit(mCurrentFragment);

		/*
		 * if (mCurrentFragment != null) { if
		 * (!mCurrentFragment.onBackPressed()) exit(); } else exit();
		 */
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if (mCurrentFragment != null)
	// mCurrentFragment.onActivityResult(requestCode, resultCode, data);
	// else
	// super.onActivityResult(requestCode, resultCode, data);
	// }

	// public void eixtApp() {
	// // 再次点击退出处理 by guiban
	// if ((System.currentTimeMillis() - firstExitingTime) > 2000) {
	// Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
	// Toast.LENGTH_SHORT).show();
	// firstExitingTime = System.currentTimeMillis();
	// } else {
	// CommUtil.exit(this);
	// }
	// }

	public void onExit(Fragment fragment) {
		if (fragment == mCurrentFragment) {
			if (getTabIndex(mTabWidget) != 0) {
				setTabIndex(mTabWidget, 0);
			} else
				exit();
		}
	}

	private void exit() {
		if ((System.currentTimeMillis() - firstExitingTime) > 2000) {
			for (StackTraceElement array : new Throwable().getStackTrace()) {
				LogUtlis.e("exit", "^^^^^^^^" + array.getClassName() + '.' + array.getMethodName());
			}
			Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
			firstExitingTime = System.currentTimeMillis();
		} else {
			super.finish();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_pick_message:
			hideMorePopWindow();
			startActivity(SingleFragmentActivity.createIntent(getThis(), PickMessageListFragment.class, null, null, null, null));
			break;
		case R.id.btn_share:
			hideMorePopWindow();
			startActivity(new Intent(this, FriendsShareActivity.class));
			break;
		case R.id.btn_about:
			hideMorePopWindow();
			startActivity(SingleFragmentActivity.createIntent(getThis(), AboutFragment.class, null, null, null, null));
			break;
		case R.id.btn_exit:
			CommUtil.exit(this);
			break;
		case R.id.btn_history:
			hideMorePopWindow();
			startActivity(SingleFragmentActivity.createIntent(getThis(), RegisteTrajectoryFragment.class, null, null, null, null));
			break;
		case R.id.btn_more:
			showMorePopWindow(arg0);
			break;
		case R.id.btn_offlineMap:
			hideMorePopWindow();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			startActivity(SingleFragmentActivity.createIntent(getThis(), OfflineMapFragment.class, null, null, null, null));
			break;
		default:
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (!arg1)
			return;
		switch (arg0.getId()) {
		case R.id.radioButton_channelMap:
			showFragmentWebview(ID_TAB_ITEM_CHANNEL_MAP);
			mTabWidgetTop.setVisibility(View.GONE);
			break;
		case R.id.radioButton_myChannel:
			mTabWidgetTop.setVisibility(View.VISIBLE);
			showContentWithTopWidget();
			break;
		case R.id.radioButton_remuneration:
			showFragmentWebview(ID_TAB_ITEM_CHANNEL_REMUNERATION);
			mTabWidgetTop.setVisibility(View.GONE);
			break;
		case R.id.radioButton_channelList:
			showFragmentWebview(ID_TAB_ITEM_CHANNEL_LIST);
			break;
		case R.id.radioButton_ChannelTree:
			showFragmentWebview(ID_TAB_ITEM_CHANNEL_TREE);
			break;
		case R.id.radioButton_favorite:
			showFragmentWebview(ID_TAB_ITEM_CHANNEL_COLLECT);
			break;
		default:
			break;
		}
	}

	// private void startFragment(BaseFragment fragment, String tag) {
	// FragmentManager fragmentManager = getSupportFragmentManager();
	// FragmentTransaction ft = fragmentManager.beginTransaction();
	// if (fragmentManager.getBackStackEntryCount() == 0 ||
	// fragmentManager.findFragmentByTag(tag) == null) {
	// ft.replace(R.id.fragmentContainer, fragment, tag);
	//
	// } else {
	// Fragment oldfragment = fragmentManager.findFragmentByTag(tag);
	// oldfragment.setArguments(fragment.getArguments());
	// ft.replace(R.id.fragmentContainer, fragment, tag);
	// Toast.makeText(this, "替换", Toast.LENGTH_SHORT).show();
	// }
	//
	// mCurrentFragment = fragment;
	// ft.commit();
	// }
}
