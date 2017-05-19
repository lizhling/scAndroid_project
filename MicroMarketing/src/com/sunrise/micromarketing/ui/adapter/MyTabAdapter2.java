package com.sunrise.micromarketing.ui.adapter;

import java.util.ArrayList;

import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.entity.TabContent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
public class MyTabAdapter2 extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<TabContent> tabItems;

	public MyTabAdapter2(Context context, ArrayList<TabContent> tabItems) {
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
		TabContent item = tabItems.get(position);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.tab_item, null);

		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.tab_image);
		TextView textView = (TextView) convertView.findViewById(R.id.tab_text);
		item.setIconNormal2ImageView(imageView);

		textView.setText(item.getTabName());

		return convertView;
	}

}
