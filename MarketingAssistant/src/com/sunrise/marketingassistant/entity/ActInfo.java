package com.sunrise.marketingassistant.entity;

public class ActInfo {
	private int actId;
	private String opTime; //日期
	private String actionName;  //活动名称
	private String actionDesc;  //活动详情
	private String actionLvl;  //活动等级
	private String chlId;  //归属竞争对手渠道
	private String busarea;  //归属商圈
	private String effDate;  //开始时间
	private String expDate;  //结束时间
	private String actionType;  //活动类型
	private String subsidyFee;  //活动补贴金额
	
	public int getActId() {
		return actId;
	}
	public void setActId(int actId) {
		this.actId = actId;
	}
	public String getOpTime() {
		return opTime;
	}
	public void setOpTime(String opTime) {
		this.opTime = opTime;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getActionDesc() {
		return actionDesc;
	}
	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}
	public String getActionLvl() {
		return actionLvl;
	}
	public void setActionLvl(String actionLvl) {
		this.actionLvl = actionLvl;
	}
	public String getChlId() {
		return chlId;
	}
	public void setChlId(String chlId) {
		this.chlId = chlId;
	}
	
	public String getBusarea() {
		return busarea;
	}
	public void setBusarea(String busarea) {
		this.busarea = busarea;
	}
	public String getEffDate() {
		return effDate;
	}
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}
	public String getExpDate() {
		return expDate;
	}
	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getSubsidyFee() {
		return subsidyFee;
	}
	public void setSubsidyFee(String subsidyFee) {
		this.subsidyFee = subsidyFee;
	}
	
	
	

}
