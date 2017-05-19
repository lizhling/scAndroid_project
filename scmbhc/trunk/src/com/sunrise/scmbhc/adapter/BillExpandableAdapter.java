package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BillApxPageSecoundLevelItem;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 账单详情列表
 * 
 * @author 珩
 * @since 2014年9月30日10:20:56
 */
public class BillExpandableAdapter extends BaseExpandableListAdapter {

	protected Context mContext;

	protected ArrayList<BillApxPageSecoundLevelItem> mData;

	protected int red;

	public BillExpandableAdapter(Context context, ArrayList<BillApxPageSecoundLevelItem> data) {
		mContext = context;
		mData = data;

		red = context.getResources().getColor(R.color.text_color_red);
	}

	public void setData(ArrayList<BillApxPageSecoundLevelItem> data) {
		mData = data;
		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mData.get(groupPosition).getArrayAttribuilt().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return mData.get(groupPosition).getArrayAttribuilt().get(childPosition).hashCode();
	}

	private static final String FORM = "%s<p align=\"right\">%s</p>";

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parentView) {

		if (convertView == null)// 显示普通文字
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_more_comm_factory, null);

		CharSequence charSequence = mData.get(groupPosition).getArrayAttribuilt().get(childPosition);
		TextView title = (TextView) convertView;// .findViewById(R.id.title);
		title.setText(charSequence);
		title.setSingleLine(false);
		// TextView discribe = (TextView)
		// convertView.findViewById(R.id.discribe);
		// discribe.setText();

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (mData.get(groupPosition) == null || mData.get(groupPosition).getArrayAttribuilt() == null)
			return 0;

		return mData.get(groupPosition).getArrayAttribuilt().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (mData == null)
			return null;

		return mData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		if (mData == null)
			return 0;

		return mData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parentView) {

		if (convertView == null)
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bill_query_list, null);

		ImageView icon = (ImageView) convertView.findViewById(R.id.arrow_down);

		TextView textview = (TextView) convertView.findViewById(R.id.text1);

		if (isExpanded) {
			// convertView.setBackgroundResource(R.drawable.grey_button_bg_click);
			icon.setVisibility(View.INVISIBLE);
		} else {
			// convertView.setBackgroundResource(R.drawable.grey_button_bg_normal);
			icon.setVisibility(View.VISIBLE);
		}
		textview.setTypeface(Typeface.DEFAULT_BOLD);

		textview.setText(mData.get(groupPosition).getAttribuiltName());

		if (mData.get(groupPosition).getFee() != null) {
			TextView textview2 = (TextView) convertView.findViewById(R.id.text2);
			textview2.setText(mData.get(groupPosition).getFee() + '元');
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
