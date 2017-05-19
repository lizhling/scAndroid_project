package com.starcpt.cmuc.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.starcpt.cmuc.R;

public class PopMenuAdapter extends BaseAdapter {
	private Menu menu;
	LayoutInflater inflater;
	
	public PopMenuAdapter(Context context,Menu menu){
		inflater=LayoutInflater.from(context);
		this.menu=menu;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return menu.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.function_menu_item, null);
			viewHolder=new ViewHolder();
			viewHolder.iconView=(ImageView) convertView.findViewById(R.id.menu_item_image);
			viewHolder.titleView=(TextView) convertView.findViewById(R.id.menu_item_text);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		MenuItem item=menu.getItem(position);
		viewHolder.iconView.setImageDrawable(item.getIcon());
		CharSequence text = item.getTitle();
		viewHolder.titleView.setText(text);
		return convertView;
	}
	
	class ViewHolder{
		ImageView iconView;
		TextView titleView;
	}

}
