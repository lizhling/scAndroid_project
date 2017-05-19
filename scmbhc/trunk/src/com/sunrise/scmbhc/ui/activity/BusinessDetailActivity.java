package com.sunrise.scmbhc.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;

public class BusinessDetailActivity extends BaseActivity {
	private String mBusinessTag;
	private ScmbhcDbManager mScmbhcDbManager;
	private ContentResolver mContentResolver;
	private BusinessMenu mBusinessMenu;

	@Override
	public void init() {
		initDatabase();
		Intent intent=getIntent();
		mBusinessTag=intent.getStringExtra(App.ExtraKeyConstant.KEY_QR_CODE);
		mBusinessMenu=mScmbhcDbManager.queryBusinessMenuByBusTag(mBusinessTag);
		setFinishActivity(true);
	}

	@Override
	protected BaseFragment getFragment() {
		// TODO Auto-generated method stub
		if(mBusinessMenu!=null){
			return mBusinessMenu.createBaseFragment();
		}else{
			return null;
		}
	}
	
	private void initDatabase() {
		mContentResolver = getContentResolver();
		mScmbhcDbManager = ScmbhcDbManager.getInstance(mContentResolver);
	}


}
