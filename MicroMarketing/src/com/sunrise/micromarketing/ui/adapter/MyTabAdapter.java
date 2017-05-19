package com.sunrise.micromarketing.ui.adapter;

import java.util.ArrayList;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.entity.TabItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MyTabAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<TabItem> tabItems;
	
	

	public MyTabAdapter(Context context, ArrayList<TabItem> tabItems) {
		this.inflater = LayoutInflater.from(context);
		this.tabItems = tabItems;
	}

	@Override
	public int getCount() {
		return tabItems.size();
	}

	@Override
	public Object getItem(int position) {
		return tabItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		TabItem item = tabItems.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.tab_item, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.tab_image);
			holder.textView = (TextView) convertView.findViewById(R.id.tab_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.imageView.setImageResource(item.getIconNormalResId());
		holder.textView.setText(item.getStringContent());
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

}
