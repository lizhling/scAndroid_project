package com.starcpt.cmuc.model;

import java.util.ArrayList;

import com.starcpt.cmuc.model.bean.ApplicationBean;
import com.starcpt.cmuc.model.bean.ApplicationsBean;



public class Applications extends DataPackage{
private ApplicationsBean applicationsBean;


public Applications(ApplicationsBean applicationsBean) {
	super();
	this.applicationsBean = applicationsBean;
	ArrayList<ApplicationBean> applicationBeans=applicationsBean.getDatas();
	for(ApplicationBean applicationBean:applicationBeans){
		Application application=new Application(applicationBean);
		datas.add(application);
	}
}

@Override
public int getRowCount() {
	// TODO Auto-generated method stub
	return applicationsBean.getRowCount();
}

@Override
public void setRowCount(int rowCount) {
	// TODO Auto-generated method stub
	applicationsBean.setRowCount(rowCount);
}

@Override
public int getPageSize() {
	// TODO Auto-generated method stub
	return applicationsBean.getPageSize();
}

@Override
public void setPageSize(int pageSize) {
	// TODO Auto-generated method stub
	applicationsBean.setPageSize(pageSize);
}

@Override
public int getPageNo() {
	// TODO Auto-generated method stub
	return applicationsBean.getPageNo();
}

@Override
public void setPageNo(int pageNo) {
	// TODO Auto-generated method stub
	applicationsBean.setPageNo(pageNo);
}

@Override
public String getAppListDisplayStyle() {
	// TODO Auto-generated method stub
	return applicationsBean.getAppListDisplayStyle();
}

@Override
public void setAppListDisplayStyle(String appListDisplayStyle) {
	// TODO Auto-generated method stub
	applicationsBean.setAppListDisplayStyle(appListDisplayStyle);
}
 
}
