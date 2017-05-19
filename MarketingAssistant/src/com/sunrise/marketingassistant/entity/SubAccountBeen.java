package com.sunrise.marketingassistant.entity;

public class SubAccountBeen extends ResultBean {

	// {"belongSystemName":"","orgCode":"2","regionCode":"01","resultCode":0,"resultMessage":"success","roleId":"2","subAccount":"aaaX74"}
	private String subAccount;
	private String[] subAccounts;
	private String belongSystemName;
	private String orgCode;
	private String regionCode;
	private String roleId;

	public String getBelongSystemName() {
		return belongSystemName;
	}

	public void setBelongSystemName(String belongSystemName) {
		this.belongSystemName = belongSystemName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getSubAccount() {
		return subAccount;
	}

	public void setSubAccount(String subAccount) {
		this.subAccount = subAccount;
	}

	public String[] getSubAccounts() {
		if (subAccounts == null) {
			if (subAccount != null) {
				String[] temp = subAccount.split(",");
				if (temp.length >= 1) {
					subAccounts = temp;
				}
			}
		}
		return subAccounts;
	}

}
