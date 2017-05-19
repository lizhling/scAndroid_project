package com.starcpt.cmuc.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.starcpt.cmuc.model.Item;

public class ListViewAdapter extends BaseAdapter {
//	private String TAG="PageListAdapter";
    private ArrayList<Item> mListItems;
    private Context mContext;
	public ListViewAdapter(Context mContext,ArrayList<Item> mListItemBeans) {
		this.mListItems = mListItemBeans;
		this.mContext=mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListItems.size();
	}

	@Override
	public Object getItem(int positon) {
		// TODO Auto-generated method stub
		return mListItems.get(positon);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Item item=mListItems.get(position);
		convertView = item.bindItemBeanToView(convertView, mContext);
		return convertView;
	}
	
}
