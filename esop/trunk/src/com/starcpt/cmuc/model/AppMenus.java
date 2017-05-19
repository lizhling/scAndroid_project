package com.starcpt.cmuc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.starcpt.cmuc.model.bean.AppMenuBean;
import com.starcpt.cmuc.model.bean.AppMenusBean;

public class AppMenus extends DataPackage{
	public static final int TIME_SORT=0;
	public static final int ORDER_SORT=1;
	public static final int SEARCH_SORT=2;
	private AppMenusBean appMenusBean;
	public AppMenus(AppMenusBean appMenusBean,int sortType) {
		super();
		this.appMenusBean = appMenusBean;
		ArrayList<AppMenuBean> appMenuBeans=appMenusBean.getDatas();
		for(AppMenuBean appMenuBean:appMenuBeans){
			if(sortType==TIME_SORT){
				appMenuBean.setItemStyleName(Item.DISPLAY_STYLE_1);
			    appMenuBean.setMenuType(Item.WEB_TYPE);
			    }
			else if(sortType==SEARCH_SORT){
				appMenuBean.setItemStyleName(Item.DISPLAY_STYLE_2);
			}
			AppMenu appMenu=new AppMenu(appMenuBean);
			datas.add(appMenu);
		}
			Collections.sort(datas, new OrederComparator());
		
	}

	public AppMenus(ArrayList<Item> datas) {
		super(datas);
	}

	
	@Override
	int getRowCount() {
		// TODO Auto-generated method stub
		return appMenusBean.getRowCount();
	}

	@Override
	void setRowCount(int rowCount) {
		// TODO Auto-generated method stub
		appMenusBean.setRowCount(rowCount);
	}

	@Override
	int getPageSize() {
		// TODO Auto-generated method stub
		return appMenusBean.getPageSize();
	}

	@Override
	void setPageSize(int pageSize) {
		// TODO Auto-generated method stub
		appMenusBean.setPageSize(pageSize);
	}

	@Override
	int getPageNo() {
		// TODO Auto-generated method stub
		return appMenusBean.getPageNo();
	}

	@Override
	void setPageNo(int pageNo) {
		// TODO Auto-generated method stub
		appMenusBean.setPageNo(pageNo);
	}

	@Override
	String getAppListDisplayStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void setAppListDisplayStyle(String appListDisplayStyle) {
		// TODO Auto-generated method stub
		
	}

	public AppMenusBean getAppMenusBean() {
		return appMenusBean;
	}

	public void setAppMenusBean(AppMenusBean appMenusBean) {
		this.appMenusBean = appMenusBean;
	}

class OrederComparator implements Comparator<Item>{

	@Override
	public int compare(Item object1, Item object2) {
			if(object1.getListOrder()>object2.getListOrder())
				return 1;
			else if(object1.getListOrder()==object2.getListOrder())
				return 0;
			else
				return -1;
	}
	
}

class TimeComparator implements Comparator<Item>{

	@Override
	public int compare(Item object1, Item object2) {
		if(object1.getCollectionTime()>object2.getCollectionTime())
			return -1;
		else if(object1.getCollectionTime()==object2.getCollectionTime())
			return 0;
		else
			return 1;
	}
	
}



	
}
