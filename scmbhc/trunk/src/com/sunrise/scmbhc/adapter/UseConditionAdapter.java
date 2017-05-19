package com.sunrise.scmbhc.adapter;

import java.util.List;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.UseCondition;
import com.sunrise.scmbhc.utils.CommUtil;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UseConditionAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<UseCondition> mList;

	private int red;
	static final String FORMAT_BUSINESS = "<b>%s</b><br><font color=\"%d\">剩余：%s</font><font dir=\"ltr\" style=\"margin-left: 0px; margin-right: 10px\"><span style=\"font-size:12px;\">&nbsp;&nbsp;&nbsp;</span>总量：%s";
	static final String FORMAT_BUSINESS_RATE = "剩余：<i>%.1f<i>%%";

	public UseConditionAdapter(Context context, List<UseCondition> list) {
		inflater = LayoutInflater.from(context);
		this.mList = list;

		red = context.getResources().getColor(R.color.text_color_red);
	}

	@Override
	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.item_condition_free, null);

		UseCondition condition = mList.get(position);

		ProgressBar progeressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_1);

		progeressBar.setMax(100);
		progeressBar.setProgress((int) Math.round(condition.getSurplusRate() * 100));

		{// 业务名称
			Spanned spanned_business = Html.fromHtml(String.format(FORMAT_BUSINESS, CommUtil.deleteProdPrcidFromBusinessName(condition.getName()), red,
					condition.getSurplusString(), condition.getTotleString()));
			TextView tag = (TextView) convertView.findViewById(R.id.tag);
			tag.setText(spanned_business);

//			if (condition.isMemberUse()) {
//				tag.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
//			}
		}
		{// 余量百分比显示
			Spanned spanned_business = Html.fromHtml(String.format(FORMAT_BUSINESS_RATE, (condition.getSurplusRate() * 100)));
			TextView usecondition = (TextView) convertView.findViewById(R.id.useCondition);
			usecondition.setText(spanned_business);

//			if (condition.isMemberUse()) {
//				usecondition.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//			}
		}
		return convertView;
	}
}
