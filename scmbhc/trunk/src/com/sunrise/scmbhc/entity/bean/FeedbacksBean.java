package com.sunrise.scmbhc.entity.bean;

import java.util.ArrayList;

public class FeedbacksBean extends ResultBean{
	private int rowCount;
	private int pageSize;
	private int pageNo;
	private ArrayList<FeedbackBean> datas=new ArrayList<FeedbackBean>();
	
	public int getRowCount() {
		return rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public ArrayList<FeedbackBean> getDatas() {
		return datas;
	}
	public void setDatas(ArrayList<FeedbackBean> datas) {
		this.datas = datas;
	}
	
	
	
}
