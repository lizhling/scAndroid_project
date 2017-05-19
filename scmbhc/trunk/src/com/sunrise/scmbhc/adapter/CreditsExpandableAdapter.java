package com.sunrise.scmbhc.adapter;

import java.io.IOException;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.bean.CreditsSmsList;
import com.sunrise.scmbhc.utils.FileUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CreditsExpandableAdapter extends BaseExpandableListAdapter {

	private Context mContext;

	private CreditsSmsList mData;

	private int mUserScore;

	public CreditsExpandableAdapter(Context context, String filename, int score) {
		mContext = context;
		setUserScore(score);

		String data = null;
		try {
			data = com.sunrise.scmbhc.utils.FileUtils.readDataFile(mContext, filename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (data == null)
			try {
				data = FileUtils.readToStringFormInputStreamUTF_8(context.getResources().getAssets().open(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}

		mData = new CreditsSmsList(data);
	}

	public CreditsExpandableAdapter(Context context, String assetFilename, String credits) {
		this(context, assetFilename, getScoreDigit(credits));
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mData.getArray().get(groupPosition).getDatas().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return mData.getArray().get(groupPosition).getDatas().get(childPosition).getCredits();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parentView) {
		if (convertView == null)
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mexchange_list, null);

		TextView title = (TextView) convertView.findViewById(R.id.title);
		title.setText(mData.getArray().get(groupPosition).getDatas().get(childPosition).getTitle());

		TextView discribe = (TextView) convertView.findViewById(R.id.discribe);
		discribe.setText(String.format(mContext.getResources().getString(R.string.needScore), mData.getArray().get(groupPosition).getDatas().get(childPosition)
				.getCredits()));

		int itemValue = mData.getArray().get(groupPosition).getDatas().get(childPosition).getCredits();
		discribe.setVisibility(itemValue == 0 ? View.GONE : View.VISIBLE);

		if (itemValue <= mUserScore) {
			title.setTextColor(Color.BLACK);
			discribe.setTextColor(Color.DKGRAY);
		} else {
			title.setTextColor(Color.LTGRAY);
			discribe.setTextColor(mContext.getResources().getColor(R.color.dark_red));
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (mData.getArray().get(groupPosition).getDatas() == null)
			return 0;

		return mData.getArray().get(groupPosition).getDatas().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (mData == null || mData.getArray() == null)
			return null;

		return mData.getArray().get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		if (mData == null || mData.getArray() == null)
			return 0;

		return mData.getArray().size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parentView) {

		if (convertView == null)
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_more_comm_factory, null);

		TextView textview = (TextView) convertView;

		if (isExpanded) {
			textview.setBackgroundResource(R.drawable.grey_button_bg_click);
			textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		} else {
			textview.setBackgroundResource(R.drawable.grey_button_bg_normal);
			textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
		}
		textview.setTypeface(Typeface.DEFAULT_BOLD);

		textview.setText(Html.fromHtml(mData.getArray().get(groupPosition).getOwenAttr().getTitle()));

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		if (mData.getArray().get(groupPosition).getDatas().get(childPosition).getCredits() > mUserScore)
			return false;
		return true;
	}

	public void setUserScore(int credits) {
		this.mUserScore = credits;
		notifyDataSetChanged();
	}

	public void setUserScore(String credits) {
		setUserScore(getScoreDigit(credits));
	}

	private static int getScoreDigit(String credits) {
		int score = 0;
		if (!TextUtils.isEmpty(credits))
			try {
				score = Integer.parseInt(credits);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		return score;
	}
}
