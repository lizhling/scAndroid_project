package com.sunrise.javascript.mode;

public class BusinessInformation {
private String oauthInforamtion;
private String businessId;
private String deviceImei;
private String deviceImsi;
private String phoneNumber;
private String appTag;
private String loadStartTime;
private String loadOvertTime;
private String subAccount;
private String phoneSimCardNumber;
private String helpId;
private String appVersion;
private String os;
public BusinessInformation(String oauthInforamtion, String businessId,
		String deviceImei, String deviceImsi, String appTag) {
	super();
	this.oauthInforamtion = oauthInforamtion;
	this.businessId = businessId;
	this.deviceImei = deviceImei;
	this.deviceImsi = deviceImsi;
	this.appTag = appTag;
}

public String getBusinessId() {
	return businessId;
}

public void setBusinessId(String businessId) {
	this.businessId = businessId;
}

public String getDeviceIme() {
	return deviceImei;
}

public void setDeviceIme(String deviceIme) {
	this.deviceImei = deviceIme;
}

public String getOauthInforamtion() {
	return oauthInforamtion;
}

public void setOauthInforamtion(String oauthInforamtion) {
	this.oauthInforamtion = oauthInforamtion;
}

public String getDeviceImei() {
	return deviceImei;
}

public void setDeviceImei(String deviceImei) {
	this.deviceImei = deviceImei;
}

public String getDeviceImsi() {
	return deviceImsi;
}

public void setDeviceImsi(String deviceImsi) {
	this.deviceImsi = deviceImsi;
}

public String getAppTag() {
	return appTag;
}

public void setAppTag(String appTag) {
	this.appTag = appTag;
}

public String getLoadStartTime() {
	return loadStartTime;
}

public void setLoadStartTime(String loadStartTime) {
	this.loadStartTime = loadStartTime;
}

public String getLoadOvertTime() {
	return loadOvertTime;
}

public void setLoadOvertTime(String loadOvertTime) {
	this.loadOvertTime = loadOvertTime;
}

public String getPhoneNumber() {
	return phoneNumber;
}

public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
}



public String getSubAccount() {
	return subAccount;
}

public void setSubAccount(String subAccount) {
	this.subAccount = subAccount;
}


public String getPhoneSimCardNumber() {
	return phoneSimCardNumber;
}

public void setPhoneSimCardNumber(String phoneSimCardNumber) {
	this.phoneSimCardNumber = phoneSimCardNumber;
}

@Override
public String toString() {
	return "BusinessInformation [oauthInforamtion=" + oauthInforamtion
			+ ", businessId=" + businessId + ", deviceImei=" + deviceImei
			+ ", deviceImsi=" + deviceImsi + ", phoneNumber=" + phoneNumber
			+ ", appTag=" + appTag + ", loadStartTime=" + loadStartTime
			+ ", loadOvertTime=" + loadOvertTime + "]";
}

public String getHelpId() {
	return helpId;
}

public void setHelpId(String helpId) {
	this.helpId = helpId;
}

public String getAppVersion() {
	return appVersion;
}

public void setAppVersion(String appVersion) {
	this.appVersion = appVersion;
}

public String getOs() {
	return os;
}

public void setOs(String os) {
	this.os = os;
}

}
