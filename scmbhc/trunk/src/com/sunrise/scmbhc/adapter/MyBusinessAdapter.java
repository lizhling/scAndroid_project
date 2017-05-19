package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.AdditionalTariffInfo;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.utils.CommUtil;

/**
 * my business
 * 
 * @author fuheng
 * 
 */
public class MyBusinessAdapter extends BaseAdapter {
	private static final String FORMAT_TITLE = "%s：<font color=\"16761567\">%s%s</font>";
	protected ArrayList<AdditionalTariffInfo> mArray;
	protected Context mContext;
	protected OnClickListener mOnClickOpenUp;

	public MyBusinessAdapter(Context context, ArrayList<AdditionalTariffInfo> array) {
		mContext = context;
		mArray = array;
	}

	@Override
	public int getCount() {
		if (mArray != null)
			return mArray.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mArray != null)
			mArray.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_my_business, null);
			view.setFocusable(false);
		}

		AdditionalTariffInfo tariff = mArray.get(position);

		TextView menber = (TextView) view.findViewById(R.id.text);
		int price = 0;
		CharSequence span = CommUtil.deleteProdPrcidFromBusinessName(tariff.getPROD_PRC_NAME());
		if (tariff.getPRICE() != null)
			price = Integer.parseInt(tariff.getPRICE());
		if (price != 0)
			span = Html.fromHtml(String.format(FORMAT_TITLE, span, tariff.getPRICE(),tariff.getPRICE_UNIT()));
		menber.setText(span);

		BusinessMenu businessItem = tariff.getBusinessItem();

		Button button = (Button) view.findViewById(R.id.quit);
		if (mOnClickOpenUp != null && businessItem != null) {
			button.setTag(businessItem);
			button.setOnClickListener(mOnClickOpenUp);
			button.setVisibility(View.VISIBLE);
		} else {
			button.setVisibility(View.GONE);
		}

		TextView duration = (TextView) view.findViewById(R.id.duration);
		duration.setText(tariff.getDuration());

		return view;
	}

	public void setOnClickOpenUp(OnClickListener onClickListener) {
		this.mOnClickOpenUp = onClickListener;
	}

}
