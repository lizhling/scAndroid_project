package com.sunrise.scmbhc.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.starcpt.analytics.PhoneClickAgent;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.AppActionConstant;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.view.TwoButtonDialog;
import com.sunrise.scmbhc.utils.CommUtil;

/**
 * @author fuheng
 * 
 */
public abstract class BaseActivity extends FragmentActivity {
	protected BaseFragment mCurrentFragment;
	private LinearLayout mTitleLayout;
	private TextView mTitle;
	private Button mLeftButton;
	private Button mRightButton;
	private Button mSearchButton;
	private Button mHomeLeftButton;
	private Button mHomeRightButton;
	private boolean isFinishActivity = false;
	private boolean isHomeActivity = false;
	private View maskLayout;

	// 再次点击退出处理 by guiban
	private long firstExitingTime = 0;

	public static final String OPEN_MASK_LAYOUT = "com.scmbhc.OPEN_MASK_LAYOUT";
	public static final String INVALID_TOKEN_ACTION = "com.scmbhc.INVALID_TOKEN_ACTION";
	public static final String CLOSE_MASK_LAYOUT = "com.scmbhc.CLOSE_MASK_LAYOUT";
	private BroadcastReceiver mOpenMaskLayoutReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(OPEN_MASK_LAYOUT)) {
				if (maskLayout != null) {
					maskLayout.setVisibility(View.VISIBLE);
				}
			} else if (intent.getAction().equals(CLOSE_MASK_LAYOUT)) {
				if (maskLayout != null) {
					maskLayout.setVisibility(View.GONE);
				}
			}
		}
	};
	TwoButtonDialog mDialogCertain = null;
	private BroadcastReceiver mDealInvalidToken = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(INVALID_TOKEN_ACTION)) {
				showReLoginDialog();
			}
		}
	};

	public void showReLoginDialog() {
		UserInfoControler.getInstance().loginOut();

		if (mDialogCertain == null) {
			mDialogCertain = new TwoButtonDialog(BaseActivity.this, new OnClickListener() {

				@Override
				public void onClick(View v) {
					mDialogCertain.dismiss();
					UserInfoControler.getInstance().loginOut();// 需要先登出
					checkLoginIn(null);// 此功能是先判是否登录，没登录，才启动登录界面
				}
			}, new OnClickListener() {

				@Override
				public void onClick(View v) {
					mDialogCertain.dismiss();
				}
			});

			mDialogCertain.setMessage("长时间未使用，请重新登录？");
		}
		mDialogCertain.show();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.base_layout);
		initHeaderView();
		init();
		CommUtil.addActivity(this);
		mCurrentFragment = getFragment();
		if (mCurrentFragment != null) {
			mCurrentFragment.startFragment(this, R.id.fragmentContainer);

			maskLayout = findViewById(R.id.maskLayout);
			maskLayout.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						MainActivity.getInstance().closeSlidingMenu();
					}
					return true;
				}
			});
			registerReceiver(mDealInvalidToken, new IntentFilter(INVALID_TOKEN_ACTION));
			// registerReceiver(mOpenMaskLayoutReceiver, new
			// IntentFilter(OPEN_MASK_LAYOUT));
			// registerReceiver(mOpenMaskLayoutReceiver, new
			// IntentFilter(CLOSE_MASK_LAYOUT));
		}

	}

	private void initHeaderView() {
		mTitle = (TextView) findViewById(R.id.headbar_title);
		mLeftButton = (Button) findViewById(R.id.headbar_leftbutton);
		mRightButton = (Button) findViewById(R.id.headbar_rightbutton);
		mSearchButton = (Button) findViewById(R.id.headbar_search);
		mTitleLayout = (LinearLayout) findViewById(R.id.titleLayout);
		mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BaseActivity.this, SearchActivity.class);
				startActivity(intent);
			}
		});
		mHomeLeftButton = (Button) findViewById(R.id.home_leftbutton);
		mHomeRightButton = (Button) findViewById(R.id.home_rightbutton);
		initLeftButton();
	}

	public void setHomeLeftButton(OnClickListener onClickListener) {
		mHomeLeftButton.setOnClickListener(onClickListener);
		mHomeLeftButton.setVisibility(View.VISIBLE);
	}

	public void setHomeLeftButtonBackgroundsResource(int resid) {
		mHomeLeftButton.setBackgroundResource(resid);
	}

	public void setHomeLRightButton(OnClickListener onClickListener) {
		mHomeRightButton.setOnClickListener(onClickListener);
		mHomeRightButton.setVisibility(View.VISIBLE);
	}

	private void initLeftButton() {
		mLeftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleBack();
			}
		});
	}

	/*
	 * public void setHeaderHomeIconVisibility(int Visibility) {
	 * mHomeIcon.setVisibility(Visibility); }
	 */

	public void setRightButton(String text, OnClickListener onClickListener) {
		// mRightButton.setText(text);
		mRightButton.setOnClickListener(onClickListener);
		mRightButton.setVisibility(View.VISIBLE);
	}

	public void setLeftButton(String text, OnClickListener onClickListener) {
		mLeftButton.setText(text);
		mLeftButton.setOnClickListener(onClickListener);
		mLeftButton.setVisibility(View.VISIBLE);
	}

	public void setRightButtonVisibility(int visible) {
		mRightButton.setVisibility(visible);
	}

	public void setLeftButtonVisibility(int visible) {
		mLeftButton.setVisibility(visible);
	}

	public void setSearchButtonVisibility(int visible) {
		mSearchButton.setVisibility(View.GONE);
	}

	protected String getTitle(Intent intent) {
		String title = null;
		if (intent != null)
			title = intent.getStringExtra(ExtraKeyConstant.KEY_TITLE);
		return title;
	}

	@Override
	protected void onResume() {
		super.onResume();
		PhoneClickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		PhoneClickAgent.onPause(this);
		try {
			unregisterReceiver(mDealInvalidToken);
			// unregisterReceiver(mOpenMaskLayoutReceiver);
		} catch (Exception e) {
		}
	}

	public void setTitle(CharSequence title) {
		if (!TextUtils.isEmpty(title)) {
			mTitle.setText(title);
			mTitle.setVisibility(View.VISIBLE);
		} else {
			mTitle.setVisibility(View.INVISIBLE);
		}
	}

	public void setTitleLayoutVisibility(boolean isvisiblity) {
		if (isvisiblity) {
			mTitleLayout.setVisibility(View.VISIBLE);
		} else {
			mTitleLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * head bar left return button
	 */
	protected int onBack() {
		int count = 1;

		if (MainActivity.getInstance() != null && MainActivity.getInstance().closeSlidingMenu()) { // 如果已打开侧边栏，先关闭
			count = 0;
		} else if (mCurrentFragment != null) {
			if (mCurrentFragment.onKeyDown(KeyEvent.KEYCODE_BACK, null))
				count = 0;
			else
				count = mCurrentFragment.finish(this);
		}

		return count;
	}

	public abstract void init();

	/**
	 * call in onCreate()
	 * 
	 * @return
	 */
	protected abstract BaseFragment getFragment();

	public void setCurrentFragment(BaseFragment mCurrentFragment) {
		this.mCurrentFragment = mCurrentFragment;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			handleBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void handleBack() {
		if (onBack() == 1) {
			if (isFinishActivity) {// 二三级页面，关闭当前activity
				finish();
			} else if (!isHomeActivity) {// 导航栏页面，如果不是首页，先返回首页
				MainActivity.getInstance().setCurrentTab(0);
			} else {
				eixtApp();
			}
		}
	}

	private void eixtApp() {
		// mExitAppDialog = new TwoButtonDialog(this, new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// CommUtil.exit(BaseActivity.this);
		// }
		// }, new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mExitAppDialog.dismiss();
		// }
		// });
		// mExitAppDialog.setMessage(getString(R.string.exit_app_message));
		// mExitAppDialog.show();
		// 再次点击退出处理 by guiban
		if ((System.currentTimeMillis() - firstExitingTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
			firstExitingTime = System.currentTimeMillis();
		} else {
			App.setPwd(null);
			App.sSettingsPreferences.setLoginOut(true);
			getApplicationContext().sendBroadcast(new Intent(AppActionConstant.ACTION_REFRESH));// 呼叫刷新
			// PhoneClickAgent.onAppClose(BaseActivity.this);
			CommUtil.exit(BaseActivity.this);

		}
	}

	public boolean isFinishActivity() {
		return isFinishActivity;
	}

	public void setFinishActivity(boolean isFinishActivity) {
		this.isFinishActivity = isFinishActivity;
	}

	/**
	 * 检测是否已登录并且密匙存在
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean checkLoginIn(Bundle bundle) {
		if (UserInfoControler.getInstance().checkUserLoginIn())
			return true;
		Intent intent = new Intent(this, LoginActivity.class);
		if (bundle != null)
			intent.putExtras(bundle);
		if (mCurrentFragment != null)
			mCurrentFragment.startActivityForResult(intent, 1);
		else
			startActivityForResult(intent, 1);

		return false;
	}

	/**
	 * @return 检测密钥是否被内存清掉，未清掉，返回true
	 */
	// private boolean checkToken() {
	// if (UserInfoControler.getInstance().getAuthorKey() != null)
	// return true;
	//
	// Toast.makeText(this, R.string.no_secret_key, Toast.LENGTH_LONG).show();
	// return false;
	// }

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

	}

	/**
	 * @param isHomeActivity
	 *            the isHomeActivity to set
	 */
	public void setHomeActivity(boolean isHomeActivity) {
		this.isHomeActivity = isHomeActivity;
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// sendBroadcast(new
			// Intent(MainActivity.TRIGGLE_SLIDINGMENU_ACTION));
			MainActivity.getInstance().toggleSlidingMenu();
		}
		return true;
	}

}
