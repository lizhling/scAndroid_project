package com.starcpt.cmuc.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.starcpt.cmuc.R;
import com.starcpt.cmuc.model.bean.VisitHistoryBean;

public class HistoryExpandableAdapter extends BaseExpandableListAdapter {
private String[] mGroupArray;
private  ArrayList<ArrayList<VisitHistoryBean>> mChildArray;  
private LayoutInflater mInflater;

	public HistoryExpandableAdapter(Context context,String[] mGroupArray,
		ArrayList<ArrayList<VisitHistoryBean>> mChildArray) {
	super();
	this.mGroupArray = mGroupArray;
	this.mChildArray = mChildArray;
	mInflater=LayoutInflater.from(context);
}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return mChildArray.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		VisitHistoryBean visitHistoryBean=mChildArray.get(groupPosition).get(childPosition);
		String title=visitHistoryBean.getTitle();
		String url=visitHistoryBean.getUrl();
		ChildViewHolder childViewHolder;
		if(convertView==null){
			convertView=mInflater.inflate(R.layout.visit_history_child_item, null);
			childViewHolder=new ChildViewHolder();
			childViewHolder.titleView=(TextView) convertView.findViewById(R.id.history_title);
			childViewHolder.urlView=(TextView) convertView.findViewById(R.id.history_url);
			convertView.setTag(childViewHolder);
		}else{
			childViewHolder=(ChildViewHolder) convertView.getTag();
		}
		childViewHolder.titleView.setText(title);
		childViewHolder.urlView.setText(url);
		childViewHolder.groupId=groupPosition;
		childViewHolder.childId=childPosition;
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mChildArray.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mGroupArray[groupPosition];
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mGroupArray.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String groupName=mGroupArray[groupPosition];
		GroupViewHolder groupViewHolder;
		if(convertView==null){
			convertView=mInflater.inflate(R.layout.vistit_history_group_item, null);
			groupViewHolder=new GroupViewHolder();
			groupViewHolder.groupNameView=(TextView) convertView.findViewById(R.id.history_group_name);
			groupViewHolder.expandView=(ImageView) convertView.findViewById(R.id.history_group_expand);
			convertView.setTag(groupViewHolder);
		}else{
			groupViewHolder=(GroupViewHolder) convertView.getTag();
		}	
		groupViewHolder.groupNameView.setText(groupName);
		if(isExpanded){
			groupViewHolder.expandView.setImageResource(R.drawable.expand1);
		}else{
			groupViewHolder.expandView.setImageResource(R.drawable.expand0);
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public class GroupViewHolder{
		TextView groupNameView;
		ImageView expandView;
	}
	
	public class ChildViewHolder{
		public int groupId;
		public int childId;
		TextView titleView;
		TextView urlView;
	}

}
