package com.sunrise.marketingassistant.adapter;

import java.util.List;

import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.adapter.PickInfoAdapter.ViewHolder;
import com.sunrise.marketingassistant.entity.ActInfo;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.fragment.PickMessageListFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityInfoAdapter extends BaseAdapter {

	private List<ActInfo> data;
	private LayoutInflater mInflater;

	public ActivityInfoAdapter(Context context, List<ActInfo> data) {
		super();
		this.data = data;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder =null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_act_info, null);
			viewHolder = new ViewHolder();
			initView(viewHolder,convertView);
			convertView.setTag(viewHolder);
			
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ActInfo dto = data.get(position);
		viewHolder.name.setText("活动名称:"+dto.getActionName());
		viewHolder.type.setText("活动类型:"+dto.getActionType());
		viewHolder.time.setText("活动时间:"+dto.getEffDate()+"至"+dto.getExpDate());
		
		return convertView;
	}
	private void initView(ViewHolder viewHolder,View convertView){
		viewHolder.name = (TextView)convertView.findViewById(R.id.name);
		viewHolder.type = (TextView)convertView.findViewById(R.id.type);
		viewHolder.time = (TextView)convertView.findViewById(R.id.time);
	}
	class ViewHolder{
		TextView name;
		TextView type;
		TextView time;
	}

}

