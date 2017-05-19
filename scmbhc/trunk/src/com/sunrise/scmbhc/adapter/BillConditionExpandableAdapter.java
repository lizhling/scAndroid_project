package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BillApxPageSecoundLevelItem;
import com.sunrise.scmbhc.entity.UseCondition;
import com.sunrise.scmbhc.utils.CommUtil;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 账单详情列表
 * 
 * @author 珩
 * @since 2014年9月30日10:20:56
 */
public class BillConditionExpandableAdapter extends BillExpandableAdapter {

	public BillConditionExpandableAdapter(Context context, ArrayList<BillApxPageSecoundLevelItem> data) {
		super(context, data);
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parentView) {
		String string = mData.get(groupPosition).getArrayAttribuilt().get(childPosition).toString();
		UseCondition condition = JsonUtils.parseJsonStrToObject(string, UseCondition.class);
		if (condition == null)
			return null;

		if (convertView == null)
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_condition_free, null);

		ProgressBar progeressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_1);

		progeressBar.setMax(100);
		progeressBar.setProgress((int) Math.round(condition.getSurplusRate() * 100));

		{// 业务名称
			Spanned spanned_business = Html.fromHtml(String.format(UseConditionAdapter.FORMAT_BUSINESS,
					CommUtil.deleteProdPrcidFromBusinessName(condition.getName()), red, condition.getSurplusString(), condition.getTotleString()));
			TextView tag = (TextView) convertView.findViewById(R.id.tag);
			tag.setText(spanned_business);
		}
		{// 余量百分比显示
			Spanned spanned_business = Html.fromHtml(String.format(UseConditionAdapter.FORMAT_BUSINESS_RATE, (condition.getSurplusRate() * 100)));
			TextView usecondition = (TextView) convertView.findViewById(R.id.useCondition);
			usecondition.setText(spanned_business);
		}
		return convertView;
	}

}
