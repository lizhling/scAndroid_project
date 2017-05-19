package com.sunrise.scmbhc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sunrise.scmbhc.R;

public class SpinnerAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private TextView textView;
	private String[] values;
	private int CurrentPosition = -1;
	public SpinnerAdapter(Context context,String[] values) {
		inflater = LayoutInflater.from(context);
		this.values=values;
	}
	@Override
	public int getCount() {
		return values.length;
	}

	@Override
	public Object getItem(int position) {
		return values[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setCurrentPosition(int currentPosition) {
		CurrentPosition = currentPosition;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.myspinner_item, null);
		textView = (TextView) convertView.findViewById(R.id.textView);
		ImageView imgView = (ImageView)convertView.findViewById(R.id.img_selector);
		textView.setText(values[position]);
		if (CurrentPosition == position && imgView != null) {
			imgView.setVisibility(View.VISIBLE);
			RelativeLayout rl = (RelativeLayout)convertView.findViewById(R.id.rl_listpanel);
			if (rl != null){
				rl.setBackgroundColor(0xff94CEF3);
			}
		} else {
			imgView.setVisibility(View.GONE);
			RelativeLayout rl = (RelativeLayout)convertView.findViewById(R.id.rl_listpanel);
			if (rl != null){
				rl.setBackgroundColor(0xffffffff);
			}
		}
		return convertView;
	}

}
