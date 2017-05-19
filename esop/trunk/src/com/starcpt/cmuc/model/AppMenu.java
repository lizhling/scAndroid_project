package com.starcpt.cmuc.model;
import com.starcpt.cmuc.model.bean.AppMenuBean;

public class AppMenu extends Item{
	private AppMenuBean appMenuBean;
	private boolean downLoading=false;
	
	
	public AppMenu(AppMenuBean appMenuBean) {
		super();
		this.appMenuBean = appMenuBean;
	}

	
	public AppMenu(String name, String content, int businessId,
			String icon, String appTag) {
		super();
		appMenuBean=new AppMenuBean(name, content, businessId, icon, appTag);
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name=appMenuBean.getName();
/*		if(name!=null)
		name= StringUtil.toSBC(name);*/
		return name;
	}

	@Override
	void setName(String name) {
		// TODO Auto-generated method stub
		appMenuBean.setName(name);
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return appMenuBean.getDescription();
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub
		appMenuBean.setDescription(description);
	}

	@Override
	public String getIcon() {
		// TODO Auto-generated method stub
		return appMenuBean.getIcon();
	}

	@Override
	void setIcon(String icon) {
		// TODO Auto-generated method stub
		appMenuBean.setIcon(icon);
	}

	@Override
	String getIconClick() {
		// TODO Auto-generated method stub
		return appMenuBean.getIconClick();
	}

	@Override
	void setIconClick(String iconClick) {
		// TODO Auto-generated method stub
		appMenuBean.setIconClick(iconClick);
	}

	@Override
	public long getListOrder() {
		// TODO Auto-generated method stub
		return appMenuBean.getListOrder();
	}

	@Override
	public  void setListOrder(long listOrder) {
		// TODO Auto-generated method stub
		appMenuBean.setListOrder(listOrder);
	}

	@Override
	public String getItemStyleName() {
		// TODO Auto-generated method stub
		return appMenuBean.getItemStyleName();
	}

	@Override
	public void setItemStyleName(String itemStyleName) {
		// TODO Auto-generated method stub
		appMenuBean.setItemStyleName(itemStyleName);
	}

	@Override
	public String getChildStyleName() {
		// TODO Auto-generated method stub
		return appMenuBean.getChildStyleName();
	}

	@Override
	void setChildStyleName(String childStyleName) {
		// TODO Auto-generated method stub
		appMenuBean.setChildStyleName(childStyleName);
	}

	@Override
	public int getMenuId() {
		// TODO Auto-generated method stub
		return appMenuBean.getMenuId();
	}

	@Override
	public void setMenuId(int menuId) {
		// TODO Auto-generated method stub
		appMenuBean.setMenuId(menuId);
	}

	@Override
	public int getBusinessId() {
		// TODO Auto-generated method stub
		return appMenuBean.getBusinessId();
	}

	@Override
	void setBusinessId(int businessId) {
		// TODO Auto-generated method stub
		appMenuBean.setBusinessId(businessId);
	}

	@Override
	public int getMenuType() {
		// TODO Auto-generated method stub
		return appMenuBean.getMenuType();
	}

	@Override
	void setMenuType(int menuType) {
		// TODO Auto-generated method stub
		appMenuBean.setMenuType(menuType);
	}

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return appMenuBean.getContent();
	}

	@Override
	void setContent(String content) {
		// TODO Auto-generated method stub
		appMenuBean.setContent(content);
	}

	@Override
	public long getChildVersion() {
		// TODO Auto-generated method stub
		return appMenuBean.getChildVersion();
	}

	@Override
	public void setChildVersion(long childVersion) {
		// TODO Auto-generated method stub
		appMenuBean.setChildVersion(childVersion);
	}

	@Override
	public String getAppTag() {
		// TODO Auto-generated method stub
		return appMenuBean.getAppTag();
	}

	@Override
	void setAppTag(String appTag) {
		appMenuBean.setAppTag(appTag);
	}


	@Override
	public long getCollectionTime() {
		// TODO Auto-generated method stub
		return appMenuBean.getCollectionTime();
	}


	@Override
	public void setCollectionTime(long collectionTime) {
		appMenuBean.setCollectionTime(collectionTime);
	}


	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return appMenuBean.getUserName();
	}


	@Override
	public void setUserName(String userName) {
		appMenuBean.setUserName(userName);

	}


	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return appMenuBean.getId();
	}


	@Override
	public void setId(int id) {
		appMenuBean.setId(id);
	}


	@Override
	public int getDeleteFlag() {
		// TODO Auto-generated method stub
		return appMenuBean.getDeleteFlag();
	}


	@Override
	public void setDeleteFlag(int deleteFlag) {
		appMenuBean.setDeleteFlag(deleteFlag);
	}


	@Override
	public void setApplicationName(String applicationName) {
		appMenuBean.setApplicationName(applicationName);
	}


	@Override
	public String getApplicationName() {
		// TODO Auto-generated method stub
		return appMenuBean.getApplicationName();
	}


	@Override
	public boolean getAppDowningLoadStaus() {
		// TODO Auto-generated method stub
		return downLoading;
	}


	@Override
	public void setAppDownLoadingStaus(boolean downLoading) {
		this.downLoading=downLoading;
	}


	@Override
	public String getThreePackageNameString() {
		// TODO Auto-generated method stub
		return appMenuBean.getThreePackageName();
	}


	@Override
	public void setThreePackageNameString(String str) {
		// TODO Auto-generated method stub
		appMenuBean.setThreePackageName(str);
	}

}
