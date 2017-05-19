package com.adapter;

import java.util.List;

import com.listview.BaseHolder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BasicAdapter<T> extends BaseAdapter {
	
	protected List<T> mDatas;
	
	public BasicAdapter(List<T> datas){
		this.mDatas = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BaseHolder<T> holder;
		
		if(convertView == null){
			holder = createHolder();
		}else{
			holder = (BaseHolder<T>)convertView.getTag();
		}
		T string = mDatas.get(position);
		holder.setData(string);
		
		return holder.getConvertView();
	}
	
	protected abstract BaseHolder createHolder();

}
