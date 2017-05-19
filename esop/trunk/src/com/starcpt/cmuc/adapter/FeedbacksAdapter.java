package com.starcpt.cmuc.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.starcpt.cmuc.R;
import com.starcpt.cmuc.model.bean.FeedbackBean;

public class FeedbacksAdapter extends BaseAdapter {
private ArrayList<FeedbackBean> list;
private LayoutInflater inflater;
private Context mContext;
private static String REPLYED;
private static String UNREPLY;


	public FeedbacksAdapter(Context context,ArrayList<FeedbackBean> list) {
	super();
	this.list = list;
	this.mContext=context;
	if(REPLYED==null)
		REPLYED=context.getString(R.string.replyed);
	if(UNREPLY==null)
		UNREPLY=context.getString(R.string.unreply);
	inflater=LayoutInflater.from(context);
}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		FeedbackBean feedbackBean=list.get(position);
		
		String status=feedbackBean.getStatus().equals(FeedbackBean.REPLYED)?REPLYED:UNREPLY;
		String feedBack=feedbackBean.getFeedback();
		String feedbackDate=feedbackBean.getCreateTime();
		String reply=feedbackBean.getReply();
		String replyDate=feedbackBean.getReplyTime();
		String replyPerson=feedbackBean.getReplyPerson();
		
		if(convertView==null){
			convertView=inflater.inflate(R.layout.feedback_item, null);
			viewHolder=new ViewHolder();
			viewHolder.feedback=(TextView) convertView.findViewById(R.id.feedback);
			viewHolder.feedbackDate=(TextView) convertView.findViewById(R.id.feedback_date_view);
			viewHolder.feedbackStatus=(TextView) convertView.findViewById(R.id.feedback_status);
			viewHolder.reply=(TextView) convertView.findViewById(R.id.reply);
			viewHolder.replyDate=(TextView) convertView.findViewById(R.id.reply_date_view);
			viewHolder.replyPerson=(TextView) convertView.findViewById(R.id.reply_person);
			viewHolder.replyPanel=(LinearLayout) convertView.findViewById(R.id.reply_panel);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		if(feedbackBean.getStatus().equals(FeedbackBean.REPLYED)){
			viewHolder.replyPanel.setVisibility(View.VISIBLE);
		}else{
			viewHolder.replyPanel.setVisibility(View.GONE);
		}
		
		if(feedBack!=null){
			viewHolder.feedback.setText(mContext.getString(R.string.feedback)+feedBack);
		}
		
		if(feedbackDate!=null){
			viewHolder.feedbackDate.setText(feedbackDate);
		}
		
		if(status!=null){
			viewHolder.feedbackStatus.setText(status);
		}
		
		if(reply!=null){
			viewHolder.reply.setText(mContext.getString(R.string.reply)+reply);
		}
		
		if(replyDate!=null){
			viewHolder.replyDate.setText(replyDate);
		}
		
		if(replyPerson!=null){
			viewHolder.replyPerson.setText(replyPerson);
		}
		
		return convertView;
	}

	class ViewHolder{
		TextView feedback;
		TextView feedbackDate;
		TextView feedbackStatus;
		TextView reply;
		TextView replyDate;
		TextView replyPerson;
		LinearLayout replyPanel;
	}
}
