package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.ECoupon;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ECouponNoItemAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<ECoupon> mList;
	//private int colorslector = 1;  // 1 代表兑换话费    2 兑换电子券  3 兑换流量
	

	public ECouponNoItemAdapter(Context context, ArrayList<ECoupon> array) {
		mContext = context;
		mList = array;
	}

	@Override
	public int getCount() {
		if (mList != null) {
			return mList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}



	@Override
	public View getView(int position, View view, ViewGroup group) {
		
		TextView text = new TextView(mContext);
		text.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_4));
		text.setText(mList.get(position).getName());
		text.setTextColor(Color.BLACK);
		text.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		return text;
	}

}
