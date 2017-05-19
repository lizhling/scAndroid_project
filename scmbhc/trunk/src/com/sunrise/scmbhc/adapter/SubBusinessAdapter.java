package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.BusinessMenu;

public class SubBusinessAdapter extends BaseAdapter implements OnClickListener {
	private ArrayList<BusinessMenu> mArray;
	private Context mContext;

	private PopupWindow popupwindow;
	private TextView popupview;

	private OnClickListener mOnClickOpenUp;

	public SubBusinessAdapter(Context context, ArrayList<BusinessMenu> array) {
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
			view = LayoutInflater.from(mContext).inflate(R.layout.item_business_deal, null);
			view.setFocusable(false);
		}

		BusinessMenu subBusinessinfos = mArray.get(position);

		TextView menber = (TextView) view.findViewById(R.id.text);
		menber.setText(subBusinessinfos.getName());

		Button openUp = (Button) view.findViewById(R.id.openUp);
		openUp.setTag(subBusinessinfos);
		openUp.setOnClickListener(mOnClickOpenUp);

		// if (UserInfoControler.getInstance().checkUserLoginIn())//
		// 如果用户登录过，才判断用户是否开通过此业务
		// if
		// (UserInfoControler.getInstance().getOpenedBusinessList().isContain(subBusinessinfos))
		// {
		// openUp.setText(R.string.unsubscribe);
		// } else {
		// openUp.setText(R.string.openUp);
		// }

		Button details = (Button) view.findViewById(R.id.details);
		if (!TextUtils.isEmpty(subBusinessinfos.getDescription())) {
			// have details
			details.setTag(subBusinessinfos.getDescription());
			details.setOnClickListener(this);
		} else {
			details.setVisibility(View.GONE);
		}

		return view;
	}

	private void showHelpInfo(View v) {
		if (popupwindow == null) {
			popupview = new TextView(v.getContext());
			popupview.setBackgroundResource(android.R.drawable.toast_frame);
			popupview.setTextColor(Color.WHITE);
			popupwindow = new PopupWindow(popupview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			popupwindow.setBackgroundDrawable(new ColorDrawable(0));
			popupwindow.setOutsideTouchable(true);
			popupwindow.setFocusable(true);
		}
		if (popupwindow != null) {
			popupview.setText(v.getTag().toString());
			popupwindow.showAsDropDown(v, 0, 0);
		}
	}

	@Override
	public void onClick(View view) {
		showHelpInfo(view);
	}

	public void setOnClickOpenUp(OnClickListener onClickListener) {
		this.mOnClickOpenUp = onClickListener;
	}
}
