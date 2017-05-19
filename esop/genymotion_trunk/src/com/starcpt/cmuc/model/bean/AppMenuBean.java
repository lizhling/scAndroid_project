package com.starcpt.cmuc.model.bean;

import android.graphics.Bitmap;

public class AppMenuBean {
	    //common
		private String appTag;
		private String name;
		private String description;
		private String icon;
		private String iconClick;	
		private Bitmap bitmap;
		private String itemStyleName;
		private String childStyleName;
		private long listOrder;
		private long childVersion;
		
		//menu
		protected int menuId;
		protected int businessId;
		protected int menuType;
		protected String content;
		protected String applicationName;
		
		//collection
		private String userName;
		private long  collectionTime;
		private int id;
		private int deleteFlag;
		
		public AppMenuBean(){
			super();
		}
		
		public AppMenuBean(String name, String content, int businessId,
				String icon, String appTag) {
			super();
			this.name = name;
			this.content = content;
			this.businessId = businessId;
			this.icon = icon;
			this.appTag = appTag;
		}
		
		public String getAppTag() {
			return appTag;
		}
		public void setAppTag(String appTag) {
			this.appTag = appTag;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		public String getIconClick() {
			return iconClick;
		}
		public void setIconClick(String iconClick) {
			this.iconClick = iconClick;
		}
		public Bitmap getBitmap() {
			return bitmap;
		}
		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}
		public String getItemStyleName() {
			return itemStyleName;
		}
		public void setItemStyleName(String itemStyleName) {
			this.itemStyleName = itemStyleName;
		}
		public String getChildStyleName() {
			return childStyleName;
		}
		public void setChildStyleName(String childStyleName) {
			this.childStyleName = childStyleName;
		}
		public long getListOrder() {
			return listOrder;
		}
		public void setListOrder(long listOrder) {
			this.listOrder = listOrder;
		}
		public long getChildVersion() {
			return childVersion;
		}
		public void setChildVersion(long childVersion) {
			this.childVersion = childVersion;
		}
		public int getMenuId() {
			return menuId;
		}
		public void setMenuId(int menuId) {
			this.menuId = menuId;
		}
		public int getBusinessId() {
			return businessId;
		}
		public void setBusinessId(int businessId) {
			this.businessId = businessId;
		}
		public int getMenuType() {
			return menuType;
		}
		public void setMenuType(int menuType) {
			this.menuType = menuType;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public long getCollectionTime() {
			return collectionTime;
		}
		public void setCollectionTime(long collectionTime) {
			this.collectionTime = collectionTime;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}

		public int getDeleteFlag() {
			return deleteFlag;
		}

		public void setDeleteFlag(int deleteFlag) {
			this.deleteFlag = deleteFlag;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}
		
		
		
		
}
