package com.starcpt.cmuc.model;

import java.util.ArrayList;

public abstract class DataPackage{
	public final static String GRID_DISPALY_STYLE_1="SQUARES";
	public final static String VERTICAL_LIST_DISPLAY_STYLE="LIST";
	protected ArrayList<Item> datas=new ArrayList<Item>();
	abstract int getRowCount();
	abstract void setRowCount(int rowCount); 
	abstract int getPageSize(); 
	abstract void setPageSize(int pageSize); 
	abstract int getPageNo(); 
	abstract void setPageNo(int pageNo); 
	abstract String getAppListDisplayStyle(); 
	abstract void setAppListDisplayStyle(String appListDisplayStyle);
	
	
	public DataPackage(ArrayList<Item> datas) {
		super();
		this.datas = datas;
	}
	

	public DataPackage() {
	}
	
	public ArrayList<Item> getDatas() {
		// TODO Auto-generated method stub
		return datas;
	}
}
