package com.starcpt.cmuc.model.bean;

import java.util.ArrayList;

public class AppMenusBean extends ResultBean{
	private int rowCount;
	private int pageSize;
	private int pageNo;
	private ArrayList<AppMenuBean> datas;
	
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
	public ArrayList<AppMenuBean> getDatas() {
		return datas;
	}
	public void setDatas(ArrayList<AppMenuBean> datas) {
		this.datas = datas;
	}
	
}
