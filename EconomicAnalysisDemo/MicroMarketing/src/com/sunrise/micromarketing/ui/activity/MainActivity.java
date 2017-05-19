package com.sunrise.micromarketing.ui.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.micromarketing.cache.preferences.Preferences;
import com.sunrise.micromarketing.entity.TabContent;
import com.sunrise.micromarketing.entity.UpdateInfo;
import com.sunrise.micromarketing.ui.adapter.MyTabAdapter2;
import com.sunrise.micromarketing.ui.view.CustomTabHost;
import com.sunrise.micromarketing.ui.view.MyTab;
import com.sunrise.micromarketing.ui.view.MyTab.ItemSelectedListener;
import com.sunrise.micromarketing.App;
import com.sunrise.micromarketing.R;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class MainActivity extends TabActivity implements OnTabChangeListener {

	private static MainActivity mainActiviry;

	private CustomTabHost tabHost;
	private MyTab mBottomTab;
	private MyTabAdapter2 mMyTabAdapter;
	

	private ArrayList<TabContent> mArrayTabContents = new ArrayList<TabContent>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		
		mainActiviry = this;

		setContentView(R.layout.main);

		initContent();

		try {
			initTabs();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getThis(), "初始化失败:" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private MainActivity getThis() {
		return this;
	}

	/**
	 * @return the mainActiviry
	 */
	public static MainActivity getInstance() {
		return mainActiviry;
	}

	public void onStart() {
		super.onStart();
	}

	public void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		mainActiviry = null;
		super.onDestroy();
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	private void initTabs() throws ClassNotFoundException {
		tabHost = (CustomTabHost) findViewById(android.R.id.tabhost);
		tabHost.setOnTabChangedListener(this);

		for (int i = 0; i < mArrayTabContents.size(); i++) {
			TabContent tabContent = mArrayTabContents.get(i);

			TabHost.TabSpec localTabSpec = tabHost.newTabSpec(String.valueOf(i))
					.setIndicator(tabContent.getTabName(), getResources().getDrawable(R.drawable.ic_launcher)).setContent(tabContent.getIntent(this));
			tabHost.addTab(localTabSpec);
		}
		mMyTabAdapter = new MyTabAdapter2(this, mArrayTabContents);
		mBottomTab = (MyTab) findViewById(R.id.bottom_tab);
		setCurrentTab(0);
		mBottomTab.setAdapter(mMyTabAdapter);
		mBottomTab.setItemSelectedListener(itemSelectedListener);

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
	
	

	private ItemSelectedListener itemSelectedListener = new ItemSelectedListener() {
		@Override
		public void onItemSelected(int position, View currentItem, View lastItem) {
			updateTabSelected(position, currentItem, lastItem);
		}
	};

	private void updateTabSelected(int position, View currentView, View lastView) {
		// if(position==1){
		// TabContent temp =mArrayTabContents.get(position);
		// Preferences.getInstance(this).setPhoneTime(temp.getLastModify());
		// }
		tabHost.setCurrentTab(position);
	}

	@Override
	public void onTabChanged(String tabId) {
		// tabId值为要切换到的tab页的索引位置
		int position = Integer.valueOf(tabId);
	}

	private void initContent() {

		String jsonStr = FileUtils.getTextFromAssets(getThis(), "tabInfo.json", "utf-8");
		jsonStr = Preferences.getInstance(this).getString(UpdateInfo.KEY_TAB_INFOS, jsonStr);

		try {
			JSONArray jsarray = new JSONObject(jsonStr).getJSONArray("datas");

			for (int i = 0; i < jsarray.length(); ++i) {
				TabContent temp = JsonUtils.parseJsonStrToObject(jsarray.getString(i), TabContent.class);
				if (i == 1) {
					Preferences.getInstance(this).setPhoneTime(temp.getLastModify());
				}
				// .temp.setType(i);
				mArrayTabContents.add(temp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
