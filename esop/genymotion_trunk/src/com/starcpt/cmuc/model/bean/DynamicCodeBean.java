package com.starcpt.cmuc.model.bean;

public class DynamicCodeBean extends ResultBean {
	private String dynamicType;
	
	public static String DYNAMIC_MESSAGE_TYPE="0";

	public String getDynamicType() {
		return dynamicType;
	}

	public void setDynamicType(String dynamicType) {
		this.dynamicType = dynamicType;
	}
	
}
