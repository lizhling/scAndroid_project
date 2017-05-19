package com.listview;

import android.view.View;

public abstract class BaseHolder<T> {
	protected View mConvertView;

	public BaseHolder(int resId) {
		super();
		this.mConvertView = mConvertView;
		mConvertView.setTag(this);
	}
	
	public View getConvertView(){
		return mConvertView;
	}
	
	public void setData(T data){
		refreshView(data);
	}
	
	protected abstract void refreshView(T data);
}
