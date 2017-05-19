package com.sunrise.marketingassistant.entity;

public class ChannelInfo {
	private String GROUP_ID;  //ID
	private String PARENT_GROUP_ID; //parentID
	private String CLASS_CODE; //编号
	private String CLASS_NAME;
	private String GROUP_NAME; //名称
	private String ROOT_DIS;
	private String HAS_CHILD; //是否有子节点
	
	private String groupId;
	private String groupName;
	private String classCode;
	private String className;
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getClassCode() {
		return classCode;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getGROUP_ID() {
		return GROUP_ID;
	}
	public void setGROUP_ID(String gROUP_ID) {
		GROUP_ID = gROUP_ID;
	}
	public String getPARENT_GROUP_ID() {
		return PARENT_GROUP_ID;
	}
	public void setPARENT_GROUP_ID(String pARENT_GROUP_ID) {
		PARENT_GROUP_ID = pARENT_GROUP_ID;
	}
	public String getCLASS_CODE() {
		return CLASS_CODE;
	}
	public void setCLASS_CODE(String cLASS_CODE) {
		CLASS_CODE = cLASS_CODE;
	}
	public String getCLASS_NAME() {
		return CLASS_NAME;
	}
	public void setCLASS_NAME(String cLASS_NAME) {
		CLASS_NAME = cLASS_NAME;
	}
	public String getGROUP_NAME() {
		return GROUP_NAME;
	}
	public void setGROUP_NAME(String gROUP_NAME) {
		GROUP_NAME = gROUP_NAME;
	}
	public String getROOT_DIS() {
		return ROOT_DIS;
	}
	public void setROOT_DIS(String rOOT_DIS) {
		ROOT_DIS = rOOT_DIS;
	}
	public String getHAS_CHILD() {
		return HAS_CHILD;
	}
	public void setHAS_CHILD(String hAS_CHILD) {
		HAS_CHILD = hAS_CHILD;
	}
	
	

}
