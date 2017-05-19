package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BillApxPageSecoundLevelItem;
import com.sunrise.scmbhc.entity.bean.ResultBean;
import com.sunrise.scmbhc.utils.UnitUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 账单详情列表
 * 
 * @author 珩
 * @since 2014年9月30日10:20:56
 */
public class BillStringExpandableAdapter extends BillExpandableAdapter {

	private int density;
	private int textsize;

	public BillStringExpandableAdapter(Context context, ArrayList<BillApxPageSecoundLevelItem> data) {
		super(context, data);

		density = Math.round(4 * mContext.getResources().getDisplayMetrics().density);
		textsize = UnitUtils.px2dip(context, context.getResources().getDimension(R.dimen.text_size_3));
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parentView) {
		if (convertView == null)
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bill_query_list, null);

		convertView.findViewById(R.id.arrow_down).setVisibility(View.INVISIBLE);

		ResultBean been = JsonUtils.parseJsonStrToObject(mData.get(groupPosition).getArrayAttribuilt().get(childPosition).toString(), ResultBean.class);

		TextView textview = (TextView) convertView.findViewById(R.id.text1);
		textview.setText(been.getResultMesage());
		textview.setPadding(density, 0, 0, 0);

		TextView textview2 = (TextView) convertView.findViewById(R.id.text2);
		textview2.setText(been.getResultCode() + '元');
		textview2.setPadding(0, 0, density, 0);
		textview2.setTextSize(textsize);
		textview2.setTextColor(textview.getTextColors());

		return convertView;
	}

}
