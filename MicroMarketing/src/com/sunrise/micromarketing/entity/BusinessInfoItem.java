package com.sunrise.micromarketing.entity;

public class BusinessInfoItem {

	public BusinessMenu turnBusinessInfoToBusinessMenu() {
		BusinessMenu menu = new BusinessMenu();
		menu.setName(getBusName());
		menu.setProdPrcid(getProdCode());
		menu.setBusinessData("PROD_PRCID=" + getProdCode() + "&OPERATE_TYPE=A&LOGIN_NO=ob0014");
		menu.setDescription(getBusDesc());
		menu.setId(3600);
		if ("2".equals(getProdType()))
			menu.setServiceUrl("sShortAddMode_wyx");
		else
			menu.setServiceUrl("sShortAddMode_wyx");

		return menu;

	}

	private String busDesc, busName, prodCode, prodType, reWardRule, spbizCode, spid, terminalDes;

	public String getBusDesc() {
		return busDesc;
	}

	public void setBusDesc(String busDesc) {
		this.busDesc = busDesc;
	}

	public String getBusName() {
		return busName;
	}

	public void setBusName(String busName) {
		this.busName = busName;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public String getReWardRule() {
		return reWardRule;
	}

	public void setReWardRule(String reWardRule) {
		this.reWardRule = reWardRule;
	}

	public String getSpbizCode() {
		return spbizCode;
	}

	public void setSpbizCode(String spbizCode) {
		this.spbizCode = spbizCode;
	}

	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	public String getTerminalDes() {
		return terminalDes;
	}

	public void setTerminalDes(String terminalDes) {
		this.terminalDes = terminalDes;
	}
}
