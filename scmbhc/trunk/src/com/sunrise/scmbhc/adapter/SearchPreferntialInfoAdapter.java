package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import com.sunrise.scmbhc.entity.PreferentialInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SearchPreferntialInfoAdapter extends BaseAdapter {
	
	private ArrayList<PreferentialInfo> mPreferentialInfos;
	private Context mContext;
	
	
	public SearchPreferntialInfoAdapter(
			ArrayList<PreferentialInfo> mPreferentialInfos, Context mContext) {
		super();
		this.mPreferentialInfos = mPreferentialInfos;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPreferentialInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPreferentialInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		PreferentialInfo preferentialInfo=mPreferentialInfos.get(position);
		return preferentialInfo.bindDataToSearchList(convertView, mContext);
	}

}
