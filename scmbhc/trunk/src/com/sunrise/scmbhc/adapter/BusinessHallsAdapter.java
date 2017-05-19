package com.sunrise.scmbhc.adapter;

import java.util.ArrayList;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.MobileBusinessHall;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusinessHallsAdapter extends BaseAdapter {
	private ArrayList<MobileBusinessHall> mMobileBusinessHalls;
	private Context mContext;
	private OnItemClickListener mViewMapListener;
	private OnItemClickListener mGetWaitPeopleListener;
	
	public BusinessHallsAdapter(ArrayList<MobileBusinessHall> mobileBusinessHalls,
			Context mContext) {
		super();
		this.mMobileBusinessHalls = mobileBusinessHalls;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMobileBusinessHalls.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMobileBusinessHalls.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		MobileBusinessHall mobileBusinessHall=mMobileBusinessHalls.get(position);
		String name=mobileBusinessHall.getName();
		String waitPeople=mobileBusinessHall.getWaitPeople();
		String address=mobileBusinessHall.getAddress();
		String phoneNumber=mobileBusinessHall.getPhoneNumber();
		float distance=(float) (mobileBusinessHall.getDistance()/1000);
		ViewHolder holder = null;
		if (convertView == null) {
			holder=new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.hall_info_item, null);
			holder.nameView=(TextView) convertView.findViewById(R.id.hall_name);
			holder.waitPeopleViewPanel=(LinearLayout) convertView.findViewById(R.id.wait_people_panel);
			holder.waitPeopleView=(TextView) convertView.findViewById(R.id.wait_people);
			holder.addressView=(TextView) convertView.findViewById(R.id.hall_address);			
			holder.phoneNumerView=(TextView) convertView.findViewById(R.id.phone_number);
			holder.noWaitPeoplePanel = (LinearLayout)convertView.findViewById(R.id.no_waitpeople_panel);
			holder.distcanceView=(TextView) convertView.findViewById(R.id.distance);
			holder.viewMapView=(LinearLayout) convertView.findViewById(R.id.view_map);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.nameView.setText(name);
		if (waitPeople.trim().length() == 0) {
			waitPeople = "0";
		}
		if(mobileBusinessHall.getCanbeappoint() == 0) {
			if (holder.noWaitPeoplePanel != null) {
				holder.waitPeopleViewPanel.setVisibility(View.GONE);
				holder.noWaitPeoplePanel.setVisibility(View.VISIBLE);
			}
		} else {
			holder.waitPeopleViewPanel.setVisibility(View.VISIBLE);
			holder.noWaitPeoplePanel.setVisibility(View.GONE);
		}
		holder.waitPeopleView.setText(waitPeople);
		holder.addressView.setText(mContext.getText(R.string.address)+address);
		if(!TextUtils.isEmpty(phoneNumber)){
			holder.phoneNumerView.setText(mContext.getText(R.string.tel)+phoneNumber);
		}
		if(distance!=-1){
			String distancStr=String.format("%.2f", distance)+mContext.getString(R.string.k_meter);
			holder.distcanceView.setText(distancStr);
		}
		
		holder.viewMapView.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(mViewMapListener!=null)
				mViewMapListener.onItemClick(null, null, position, 0);
			}
		});
		final TextView  waitPeopleView=holder.waitPeopleView;
		holder.waitPeopleViewPanel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mGetWaitPeopleListener.onItemClick(null, waitPeopleView, position, 0);
			}
		});
		return convertView;
	}

	
	
	public void setGetWaitPeopleListener(OnItemClickListener mGetWaitPeopleListener) {
		this.mGetWaitPeopleListener = mGetWaitPeopleListener;
	}

	public void setViewMapClickListener(OnItemClickListener viewMapClickListener) {
		this.mViewMapListener = viewMapClickListener;
	}

	class ViewHolder{
		TextView nameView;
		TextView waitPeopleView;
		TextView addressView;
		TextView phoneNumerView;
		LinearLayout noWaitPeoplePanel;
		TextView distcanceView;
		LinearLayout viewMapView;
		LinearLayout waitPeopleViewPanel;
	}

}
