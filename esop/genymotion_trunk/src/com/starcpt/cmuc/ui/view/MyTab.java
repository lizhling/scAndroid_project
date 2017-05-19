package com.starcpt.cmuc.ui.view;

import com.starcpt.cmuc.ui.activity.BusinessActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

public class MyTab extends LinearLayout {
private Adapter mBaseAdapter;
private ItemSelectedListener mItemSelectedListener;

private int mLastSelectedPosition = 0;
private Context mContext;

private OnClickListener menuItemSelectedListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = v.getId(); 
		if (position!=mLastSelectedPosition) {
			View lastView = getChildAt(mLastSelectedPosition);
			if (mItemSelectedListener!=null) {
				mItemSelectedListener.onItemSelected(position,v, lastView);
				mLastSelectedPosition = position;
			}
		}else{
			if(position==mLastSelectedPosition){
				if(position==0){
					mContext.sendBroadcast(new Intent(BusinessActivity.MENU_ROOT_ACTION));
				}			
			}
		}
	}
};
	
	public MyTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
	}
	
	public Adapter getAdapter() {
		return mBaseAdapter;
	}
	
	public void setAdapter(Adapter mBaseAdapter) {
		this.mBaseAdapter = mBaseAdapter;
		initView();
	}
	
	public void updateLastPosition(int position){
		mLastSelectedPosition=position;
	}
	
	public void initView(){
		 int count = mBaseAdapter.getCount();
		 removeAllViews();
		 for (int i = 0; i < count; i++) {
			View view = mBaseAdapter.getView(i, null, this);
			view.setOnClickListener(menuItemSelectedListener);
			view.setId(i);
			LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			layoutParams.weight=1.0f;
			view.setLayoutParams(layoutParams);
			addView(view);		
		 }
		 invalidate();
	}
	

	public void setmItemSelectedListener(ItemSelectedListener mItemSelectedListener) {
		this.mItemSelectedListener = mItemSelectedListener;
	}


	public interface ItemSelectedListener {
	public void onItemSelected(int position ,View currentItem, View lastItem);
	}
}
