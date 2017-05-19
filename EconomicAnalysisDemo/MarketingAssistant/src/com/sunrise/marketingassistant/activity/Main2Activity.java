package com.sunrise.marketingassistant.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.activity.MipcaCaptureActivity;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.entity.TabContent;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.fragment.BaseFragment;
import com.sunrise.marketingassistant.fragment.LocationOverlayFragment;
import com.sunrise.marketingassistant.fragment.MipcaCaptureFragment;
import com.sunrise.marketingassistant.fragment.WebViewFragment2;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.view.SignPanelDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Main2Activity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, OnClickListener {
	protected BaseFragment mCurrentFragment;
	private RadioGroup mTabWidget;
	private ArrayList<TabContent> mArrayTabContents = new ArrayList<TabContent>();
	private SlidingPaneLayout mSlidingPane;

	private final int REQUEST_CODE_MIPCACAPTURE = 104;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_main2);

		initContent();
		initView();

		setTabIndex(mTabWidget, 0);

		new SignPanelDialog(this, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();

			}
		}, null).show();
	}

	public void onDestroy() {
		super.onDestroy();
		CommUtil.exit(this);
	}

	private void initView() {
		mTabWidget = (RadioGroup) findViewById(R.id.radioGroupBottom);
		// mTabWidget.setOnCheckedChangeListener(this);

		((RadioButton) findViewById(R.id.radioButton_channelMap)).setOnCheckedChangeListener(this);
//		((RadioButton) findViewById(R.id.radioButton_scan)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radioButton_channelList)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radioButton_ChannelTree)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radioButton_favorite)).setOnCheckedChangeListener(this);

		mSlidingPane = (SlidingPaneLayout) findViewById(R.id.slidingPane1);

	}

	private void showFragmentWebview(int index) {

		TabContent item = mArrayTabContents.get(index);

		BaseFragment fragment = null;

		switch (item.getType()) {
		case 0: {
			fragment = new WebViewFragment2();
			Bundle args = new Bundle();
			args.putString(ExtraKeyConstant.KEY_CONTENT, null);
			args.putString(ExtraKeyConstant.KEY_LAST_MODIFY, item.getLastModify());
			args.putString(Intent.EXTRA_TEXT, item.getZipContent());
			fragment.setArguments(args);
			fragment.startFragment(this, R.id.fragmentContainer);
		}
			break;
		case 2: {
			fragment = new MipcaCaptureFragment();
			Bundle bundle = new Bundle();
			bundle.putString(ExtraKeyConstant.KEY_URL, JsonUtils.writeObjectToJsonStr(mArrayTabContents.get(4)));
			fragment.setArguments(bundle);
			// startFragment(basefragment, item.getZipContent());
			fragment.startFragment(this, R.id.fragmentContainer);
		}
			break;
		case 1:
			fragment = new LocationOverlayFragment();
			Bundle bundle = new Bundle();
			bundle.putString(ExtraKeyConstant.KEY_URL, JsonUtils.writeObjectToJsonStr(mArrayTabContents.get(0)));
			fragment.setArguments(bundle);
			// startFragment(basefragment, item.getZipContent());
			fragment.startFragment(this, R.id.fragmentContainer);
			break;
		default:
			break;
		}

		mCurrentFragment = fragment;

		// WebViewFragment2 fragment = new WebViewFragment2();
		// fragment.addBundleInfo(item.getZipContent(), null,
		// item.getLastModify());
		// fragment.startFragment(this, R.id.fragmentContainer);
		// mCurrentFragment = fragment;
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_MIPCACAPTURE) {

			return;
		}
		if (mCurrentFragment != null)
			mCurrentFragment.onActivityResult(requestCode, resultCode, data);
	}

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

	private void initContent() {
		final String FILE_NAME_TABINFO = "tabInfo.txt";
		String jsonStr = FileUtils.getTextFromAssets(getThis(), FILE_NAME_TABINFO, "utf-8");
		if (App.isTest) {
			try {
				if (FileUtils.fileIsExist(ExtraKeyConstant.APP_SD_PATH_NAME, FILE_NAME_TABINFO)) {
					jsonStr = FileUtils.readToStringFormFile(FileUtils.getAbsPath(ExtraKeyConstant.APP_SD_PATH_NAME, FILE_NAME_TABINFO));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			jsonStr = getPreferences().getString(UpdateInfo.KEY_TAB_INFOS, jsonStr);

		try {
			JSONArray jsarray = new JSONObject(jsonStr).getJSONArray("datas");

			for (int i = 0; i < jsarray.length(); ++i) {
				TabContent temp = JsonUtils.parseJsonStrToObject(jsarray.getString(i), TabContent.class);
				if (i == 1) {
					getPreferences().setPhoneTime(temp.getLastModify());
				}
				// .temp.setType(i);
				mArrayTabContents.add(temp);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean checked) {
		if (checked) {

			switch (arg0.getId()) {
			case R.id.radioButton_channelMap:
				showFragmentWebview(0);
				break;
			case R.id.radioButton_channelList:
				showFragmentWebview(1);
				break;
			case R.id.radioButton_ChannelTree:
				showFragmentWebview(2);
				break;
			case R.id.radioButton_favorite:
				showFragmentWebview(3);
				break;
			case R.id.radioButton_remuneration:
				showFragmentWebview(4);
				break;
			default:
				break;
			}
			mSlidingPane.closePane();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_operate:

			break;
		case R.id.btn_scan:
			startActivityForResult(new Intent(this, MipcaCaptureActivity.class), REQUEST_CODE_MIPCACAPTURE);
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
