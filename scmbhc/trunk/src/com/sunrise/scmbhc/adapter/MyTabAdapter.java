package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.TabItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyTabAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private ArrayList<TabItem> tabItems;
	
	public MyTabAdapter(Context context,ArrayList<TabItem> tabItems) {
		this.inflater=LayoutInflater.from(context);
		this.tabItems=tabItems;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tabItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return tabItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		TabItem item=tabItems.get(position);
		if (convertView==null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.tab_item,null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.tab_image);
			holder.textView=(TextView) convertView.findViewById(R.id.tab_text);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.imageView.setImageResource(item.getIconNormalResId());
		holder.textView.setText(item.getStringResId());
		return convertView;
	}
	
	class ViewHolder{
		ImageView imageView;
		TextView textView;
	}
	
}
