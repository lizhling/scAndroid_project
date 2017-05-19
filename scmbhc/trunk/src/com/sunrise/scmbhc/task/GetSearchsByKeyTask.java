package com.sunrise.scmbhc.task;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.PreferentialInfo;
import com.sunrise.scmbhc.entity.SearchKeyWord;
import com.sunrise.scmbhc.utils.CommUtil;

public class GetSearchsByKeyTask extends GenericTask {
	private Context mContext;
	private SearchKeyWord mSearchKeyWord;
	private ContentResolver mContentResolver;
	private ScmbhcDbManager mScmbhcDbManager;
	
	public GetSearchsByKeyTask(Context mContext, SearchKeyWord mSearchKeyWord) {
		super();
		this.mContext = mContext;
		this.mSearchKeyWord = mSearchKeyWord;
		initDatabase();
	}
	
	private void initDatabase() {
		mContentResolver = mContext.getContentResolver();
		mScmbhcDbManager = ScmbhcDbManager.getInstance(mContentResolver);
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		int type=mSearchKeyWord.getSearchType();
		try {
			CommUtil.saveSearchKeyToFile(mContext, mSearchKeyWord.getKeyWord(), mSearchKeyWord.getTagType());
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch(type){
		case SearchKeyWord.ALL_TYPE:
			searchBusiness();
			searchPrefernitialInfos();
			break;
		case SearchKeyWord.BUSINESS_TYPE:
			App.sSearchPreferentialInfos.clear();
			searchBusiness();
			break;
		case SearchKeyWord.DISCOUNT_TYPE:	
			App.sSearchBusinesses.clear();
			searchPrefernitialInfos();
			break;
		}
		return TaskResult.OK;
	}


	private void searchBusiness() {
		ArrayList<BusinessMenu> datas=mScmbhcDbManager.searchBusinessMenuByKey(mSearchKeyWord.getKeyWord());
		if(datas!=null){
			App.sSearchBusinesses.clear();
			App.sSearchBusinesses.addAll(datas);
		}
	}

	private void searchPrefernitialInfos(){
		ArrayList<PreferentialInfo> preferentialInfos = getSearchByKey(mSearchKeyWord.getKeyWord());		
		if(preferentialInfos!=null){
			App.sSearchPreferentialInfos.clear();
			App.sSearchPreferentialInfos.addAll(preferentialInfos);
		}
	}
	
	private ArrayList<PreferentialInfo> getSearchByKey(String key){
		ArrayList<PreferentialInfo> datas=new ArrayList<PreferentialInfo>();
		for(PreferentialInfo preferentialInfo:App.sAllPreferentialInfos){
			String title=preferentialInfo.getTitle();
			if(!TextUtils.isEmpty(title)){
				if(title.contains(key)){
					datas.add(preferentialInfo);
				}
			}
		}
		return datas;
	}

}
