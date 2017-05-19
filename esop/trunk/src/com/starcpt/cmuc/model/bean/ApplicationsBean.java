package com.starcpt.cmuc.model.bean;

import java.util.ArrayList;

public class ApplicationsBean extends ResultBean{
	private String appListDisplayStyle;
	private int rowCount;
	private int pageSize;
	private int pageNo;
	private ArrayList<ApplicationBean> datas;
	
	public String getAppListDisplayStyle() {
		return appListDisplayStyle;
	}
	public void setAppListDisplayStyle(String appListDisplayStyle) {
		this.appListDisplayStyle = appListDisplayStyle;
	}
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
	public ArrayList<ApplicationBean> getDatas() {
		return datas;
	}
	public void setDatas(ArrayList<ApplicationBean> datas) {
		this.datas = datas;
	}
	}
