package com.starcpt.cmuc.adapter;

import java.util.List;

import com.starcpt.cmuc.model.Item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GridViewAdapter  extends BaseAdapter{
	private List<Item> mCollectionBusiness;
	private Context mContext;
	
	
	public GridViewAdapter(List<Item> mCollectionBusiness, Context mContext) {
		super();
		this.mCollectionBusiness = mCollectionBusiness;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCollectionBusiness.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCollectionBusiness.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item itemBean = mCollectionBusiness.get(position);
		convertView = itemBean.bindItemBeanToView(convertView, mContext);
		return convertView;
	}

}
