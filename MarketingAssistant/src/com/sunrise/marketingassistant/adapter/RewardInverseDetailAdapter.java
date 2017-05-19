package com.sunrise.marketingassistant.adapter;

import java.util.List;

import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.adapter.RemunerationAdapter.ViewHolder;
import com.sunrise.marketingassistant.entity.Remuneration;
import com.sunrise.marketingassistant.entity.RewardInverseDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RewardInverseDetailAdapter extends BaseAdapter {

	private List<RewardInverseDetail> data;
	private LayoutInflater mInflater;

	public RewardInverseDetailAdapter(Context context, List<RewardInverseDetail> data) {
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
			convertView = mInflater.inflate(R.layout.item_reward_inver_detail, null);
			viewHolder = new ViewHolder();
			initView(viewHolder,convertView);
			convertView.setTag(viewHolder);
			
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		RewardInverseDetail dto = data.get(position);
		viewHolder.node_name.setText("节点名称:"+dto.getMOB_CLASS_NAME());
		viewHolder.group_name.setText("集团名称:"+dto.getGROUP_NAME());
		viewHolder.phone_no.setText("服务号码:"+dto.getSERVICE_NO());
		viewHolder.blnc_name.setText("政策名称"+dto.getBLNC_NAME());
		if(dto.getRWD_FLAG().equals("Y") || dto.getRWD_FLAG().equals("y")){
			viewHolder.flag.setText("结算标示:成功结算");
		}else{
			viewHolder.flag.setText("结算标示:未成功结算");
		}
		viewHolder.amount.setText("结算金额:"+dto.getRWD_SUC_FEE());
		
		return convertView;
	}
	private void initView(ViewHolder viewHolder,View convertView){
		viewHolder.node_name = (TextView)convertView.findViewById(R.id.node_name);
		viewHolder.group_name = (TextView)convertView.findViewById(R.id.group_name);
		viewHolder.phone_no = (TextView)convertView.findViewById(R.id.phone_no);
		viewHolder.blnc_name = (TextView)convertView.findViewById(R.id.blnc_name);
		viewHolder.flag = (TextView)convertView.findViewById(R.id.flag);
		viewHolder.amount = (TextView)convertView.findViewById(R.id.amount);
	}
	class ViewHolder{
		TextView node_name;
		TextView group_name;
		TextView phone_no;
		TextView blnc_name;
		TextView flag;
		TextView amount;
	}

}

