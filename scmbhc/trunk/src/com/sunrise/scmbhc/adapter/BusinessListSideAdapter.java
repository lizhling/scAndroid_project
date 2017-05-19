package com.sunrise.scmbhc.adapter;

import java.util.List;

import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 
 * @author fuheng
 * 
 */
public class BusinessListSideAdapter extends BusinessListAdapter {
	private List<BusinessMenu> list;
	private int Currentposition = -1;
	private Context mContext;

	public void setCurrentposition(int currentposition) {
		Currentposition = currentposition;
	}

	public BusinessListSideAdapter(Context context, List<BusinessMenu> list) {
		super(context, list);
		this.list = list;
		this.mContext = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup container) {
		View viewgroup = LayoutInflater.from(getContext()).inflate(R.layout.item_business_list2, null);

		BusinessMenu item = (BusinessMenu) getItem(position);

		TextView title = (TextView) viewgroup.findViewById(R.id.title);
		title.setText(item.getName());
		if (position == Currentposition) {
			title.setTextColor(0xff0085D2);
		} else {
			title.setTextColor(mContext.getResources().getColor(android.R.color.black));
		}
		ImageView icon = (ImageView) viewgroup.findViewById(R.id.icon1);
		loadBitmapForImageView(getContext(), icon, item);
		ImageView iconselect = (ImageView) viewgroup.findViewById(R.id.icon_selector);
		iconselect.setImageResource(R.drawable.icon_selector);
		return viewgroup;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

}
