package com.sunrise.marketingassistant.adapter;

import java.util.List;

import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.adapter.RemunerationAdapter.ViewHolder;
import com.sunrise.marketingassistant.entity.Remuneration;
import com.sunrise.marketingassistant.entity.RewardSubject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RewardSubjectAdapter extends BaseAdapter {

	private List<RewardSubject> data;
	private LayoutInflater mInflater;

	public RewardSubjectAdapter(Context context, List<RewardSubject> data) {
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
			convertView = mInflater.inflate(R.layout.item_reward_subject, null);
			viewHolder = new ViewHolder();
			initView(viewHolder,convertView);
			convertView.setTag(viewHolder);
			
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		RewardSubject dto = data.get(position);
		viewHolder.name.setText(dto.getMOB_CLASS_NAME());
		
		return convertView;
	}
	private void initView(ViewHolder viewHolder,View convertView){
		viewHolder.name = (TextView)convertView.findViewById(R.id.name);
	}
	class ViewHolder{
		TextView name;
	}

}
