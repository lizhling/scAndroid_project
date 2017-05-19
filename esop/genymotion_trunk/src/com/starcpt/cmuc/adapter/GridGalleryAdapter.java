package com.starcpt.cmuc.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.starcpt.cmuc.model.Item;

public class GridGalleryAdapter extends BaseAdapter{
	private List<Item> subList;
	private Context mContext;
	private int mPageSize;
	public GridGalleryAdapter(Context context, List<Item> list, int pageSize,int pageIndex){
		this.mContext=context;
		this.mPageSize=pageSize;
		this.subList = new ArrayList<Item>();   
        int i = pageIndex * mPageSize;   
        int iEnd = i+mPageSize;   
        while ((i<list.size()) && (i<iEnd)) {   
        	subList.add(list.get(i));   
            i++;   
        }   
	}
	


	public void setPageSize(int mPageSize) {
		this.mPageSize = mPageSize;
	}


	@Override
	public int getCount() {
		  return subList.size();  
	}

	@Override
	public Object getItem(int position) {
		return subList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Item itemBean = subList.get(position);
		convertView = itemBean.bindItemBeanToView(convertView, mContext);
		return convertView;
	}
		
	public List<Item> getSubList() {
		return subList;
	}
	
	
}
