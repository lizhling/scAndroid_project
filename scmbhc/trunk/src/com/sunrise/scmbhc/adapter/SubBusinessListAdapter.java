package com.sunrise.scmbhc.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BusinessMenu;

/**
 * 
 * 业务列表 - 侧边栏2级子业务列表
 * 
 * @author fuheng
 * 
 */
public class SubBusinessListAdapter extends BaseAdapter {

	private Context mContext;
	private List<BusinessMenu> mList;

	private boolean isSimpleMode;

	public SubBusinessListAdapter(Context context, List<BusinessMenu> list) {
		mContext = context;
		mList = list;
		setSimpleMode(false);
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
			return mList.get(position);

		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mList != null)
			return mList.get(position).getId();
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup container) {
		if (view == null)
			view = LayoutInflater.from(mContext).inflate(R.layout.item_business_sublist, null);

		TextView title = (TextView) view.findViewById(R.id.title);
		title.setGravity(Gravity.LEFT);
		title.setTextColor(Color.BLACK);
		title.setText(mList.get(position).getName());
		TextView title2 = (TextView) view.findViewById(R.id.subtitle);
		
		String description = mList.get(position).getDescription();
		if (description == null || description.trim().length() == 0) {
			description = mList.get(position).getName();
		}
		if (title2 != null) {
			title2.setText(description);
		}

		return view;
	}

	public void setSimpleMode(boolean isSimpleMode) {
		if (this.isSimpleMode != isSimpleMode)
			notifyDataSetChanged();
		this.isSimpleMode = isSimpleMode;
	}

}
