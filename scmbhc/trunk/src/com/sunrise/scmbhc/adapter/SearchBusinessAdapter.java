package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import com.sunrise.scmbhc.entity.BusinessMenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SearchBusinessAdapter extends BaseAdapter {

	private ArrayList<BusinessMenu> mBusinessMenus;
	private Context mContext;
	
	public SearchBusinessAdapter(ArrayList<BusinessMenu> businessMenus,
			Context mContext) {
		super();
		this.mBusinessMenus = businessMenus;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBusinessMenus.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mBusinessMenus.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		BusinessMenu businessMenu=mBusinessMenus.get(position);	
		return businessMenu.bindDataToSearchList(convertView, mContext);
	}

}
