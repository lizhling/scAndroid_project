package com.sunrise.businesstransaction.service.vo;

public class PrintVO {
	
	private String custName;//客户名称
	private String ticketName;//单据名称
	private String number;//编号
	private String date;//日期
	private String name;//姓名
	private String phoneNum;//手机号码
	private String identity;//身份证号
	private String brand;//手机品牌
	private String checkRightType;//鉴权方式
	private String busiType;//业务类型
	private String amount;//金额
	private String orderNum;//订单号
	private String planName;//营销方案名称
	private String protocol;//协议内容
	private String custDesc;//客户声明
	private String preUse1;//备用1
	private String preUse2;//备用2
	
	private byte[] sign;//个人签名
	
	private String template;//版本

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCheckRightType() {
		return checkRightType;
	}

	public void setCheckRightType(String checkRightType) {
		this.checkRightType = checkRightType;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getCustDesc() {
		return custDesc;
	}

	public void setCustDesc(String custDesc) {
		this.custDesc = custDesc;
	}

	public String getPreUse1() {
		return preUse1;
	}

	public void setPreUse1(String preUse1) {
		this.preUse1 = preUse1;
	}

	public String getPreUse2() {
		return preUse2;
	}

	public void setPreUse2(String preUse2) {
		this.preUse2 = preUse2;
	}

	public byte[] getSign() {
		return sign;
	}

	public void setSign(byte[] sign) {
		this.sign = sign;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	
}
