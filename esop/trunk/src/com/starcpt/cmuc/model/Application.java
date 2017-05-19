package com.starcpt.cmuc.model;
import com.starcpt.cmuc.model.bean.ApplicationBean;


public class Application extends Item {	
	private ApplicationBean applicationBean;
	
	
	protected Application(ApplicationBean applicationBean) {
		super();
		this.applicationBean = applicationBean;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return applicationBean.getName();
	}

	@Override
	void setName(String name) {
		// TODO Auto-generated method stub
		applicationBean.setName(name);
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return applicationBean.getDescription();
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub
		applicationBean.setDescription(description);
	}

	@Override
	public String getIcon() {
		// TODO Auto-generated method stub
		return applicationBean.getIcon();
	}

	@Override
	void setIcon(String icon) {
		// TODO Auto-generated method stub
		applicationBean.setIcon(icon);
	}

	@Override
	String getIconClick() {
		// TODO Auto-generated method stub
		return applicationBean.getIconClick();
	}

	@Override
	void setIconClick(String iconClick) {
		// TODO Auto-generated method stub
		applicationBean.setIconClick(iconClick);
	}


	@Override
	public long getListOrder() {
		// TODO Auto-generated method stub
		return applicationBean.getListOrder();
	}

	@Override
	public void setListOrder(long listOrder) {
		// TODO Auto-generated method stub
		applicationBean.setListOrder(listOrder);
	}

	@Override
	public String getItemStyleName() {
		// TODO Auto-generated method stub
		return applicationBean.getItemStyleName();
	}

	@Override
	public void setItemStyleName(String itemStyleName) {
		// TODO Auto-generated method stub
		applicationBean.setItemStyleName(itemStyleName);
	}

	@Override
	public String getChildStyleName() {
		// TODO Auto-generated method stub
		return applicationBean.getChildStyleName();
	}

	@Override
	void setChildStyleName(String childStyleName) {
		// TODO Auto-generated method stub
		applicationBean.setChildStyleName(childStyleName);
	}

	@Override
	public long getChildVersion() {
		// TODO Auto-generated method stub
		return applicationBean.getChildVersion();
	}

	@Override
	public void setChildVersion(long childVersion) {
		// TODO Auto-generated method stub
		applicationBean.setChildVersion(childVersion);
	}
	
	//menu
	@Override
	public int getMenuId() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	void setMenuId(int menuId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBusinessId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void setBusinessId(int businessId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMenuType() {
		// TODO Auto-generated method stub
		return AppMenu.MENU_TYPE;
	}

	@Override
	void setMenuType(int menuType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void setContent(String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAppTag() {
		// TODO Auto-generated method stub
		return applicationBean.getAppTag();
	}

	@Override
	void setAppTag(String appTag) {
		applicationBean.setAppTag(appTag);
	}

	@Override
	public long getCollectionTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCollectionTime(long collectionTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserName(String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getDeleteFlag() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDeleteFlag(int deleteFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setApplicationName(String applicationName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getApplicationName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setAppDownLoadingStaus(boolean icon) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getAppDowningLoadStaus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getThreePackageNameString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setThreePackageNameString(String str) {
		// TODO Auto-generated method stub
		
	}

}
