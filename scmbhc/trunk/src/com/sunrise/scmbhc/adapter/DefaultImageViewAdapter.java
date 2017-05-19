package com.sunrise.scmbhc.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BusinessMenu;

public class DefaultImageViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<RecommodPackage> mList;

	public DefaultImageViewAdapter(Context context, List<RecommodPackage> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mList != null)
			count += mList.size();
		return count;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {

		ImageView imageview = (ImageView) view;
		if (view == null) {
			imageview = new ImageView(mContext);
			/*imageview.setPadding(2, 2, 2, 2);
			imageview.setBackgroundResource(R.drawable.shape_img_bg_traficserver);*/
		}

		imageview.setImageResource(mList.get(position).getIconRes());

		return imageview;
	}

	public static class RecommodPackage {
		private BusinessMenu business;
		private int iconRes;

		public BusinessMenu getBusiness() {
			return business;
		}

		public int getIconRes() {
			return iconRes;
		}

		public void setIconRes(int iconRes) {
			this.iconRes = iconRes;
		}

		public void setBusiness(BusinessMenu business) {
			this.business = business;
		}

	}

}
