package com.sunrise.scmbhc.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.PaymentContainer.Payment;

public class PayWayAdapter extends BaseAdapter {

	private List<Payment> mList;
	private Context mContext;

	public PayWayAdapter(Context context, List<Payment> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mList != null)
			return mList.get(position).getId();
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, null);
		}
		Payment info = mList.get(position);
		TextView title = (TextView) view.findViewById(android.R.id.text1);
		title.setText(info.getName());
		return view;
	}
	// public View getView(int position, View view, ViewGroup arg2) {
	//
	// if (view == null) {
	// view = LayoutInflater.from(mContext).inflate(R.layout.item_topup_payway,
	// null);
	// }
	//
	// View checkBox = view.findViewById(android.R.id.checkbox);
	//
	// Payment info = mList.get(position);
	// TextView title = (TextView) view.findViewById(android.R.id.text1);
	// title.setText(info.getName());
	// return view;
	// }

}
