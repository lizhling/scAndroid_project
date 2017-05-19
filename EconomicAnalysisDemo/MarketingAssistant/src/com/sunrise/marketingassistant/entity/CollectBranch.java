package com.sunrise.marketingassistant.entity;

public class CollectBranch {

	private String account;
	private String GROUP_ID;
	private String CLASS_NAME;
	private String GROUP_NAME;
	private String GROUP_ADDRESS;

	public String getAccount() {
		return account;
	}

	public String getGROUP_ID() {
		return GROUP_ID;
	}

	public String getCLASS_NAME() {
		return CLASS_NAME;
	}

	public String getGROUP_NAME() {
		return GROUP_NAME;
	}

	public String getGROUP_ADDRESS() {
		return GROUP_ADDRESS;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setGROUP_ID(String gROUP_ID) {
		GROUP_ID = gROUP_ID;
	}

	public void setCLASS_NAME(String cLASS_NAME) {
		CLASS_NAME = cLASS_NAME;
	}

	public void setGROUP_NAME(String gROUP_NAME) {
		GROUP_NAME = gROUP_NAME;
	}

	public void setGROUP_ADDRESS(String gROUP_ADDRESS) {
		GROUP_ADDRESS = gROUP_ADDRESS;
	}

	public MobileBusinessHall toMobileBusinessHall() {
		MobileBusinessHall hall = new MobileBusinessHall();

		hall.setGROUP_ADDRESS(GROUP_ADDRESS);
		hall.setGROUP_ID(GROUP_ID);
		hall.setGROUP_NAME(GROUP_NAME);
		hall.setCLASS_NAME(CLASS_NAME);

		return hall;
	}
}
