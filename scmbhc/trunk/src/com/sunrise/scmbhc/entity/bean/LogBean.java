package com.sunrise.scmbhc.entity.bean;

public class LogBean extends ResultBean{
private String account4a;
private String deviceImei;
private String deviceOsVersion;
private String deviceModel;
private String occurTime;
private String netStatus;
private String netType;
private String errorLog;
private String apkVersion;
public static final String NET_OPEN="online";
public static final String NET_COLSE="offline";

public String getAccount4a() {
	return account4a;
}
public void setAccount4a(String account4a) {
	this.account4a = account4a;
}
public String getDeviceImei() {
	return deviceImei;
}
public void setDeviceImei(String deviceImei) {
	this.deviceImei = deviceImei;
}
public String getDeviceOsVersion() {
	return deviceOsVersion;
}
public void setDeviceOsVersion(String deviceOsVersion) {
	this.deviceOsVersion = deviceOsVersion;
}
public String getDeviceModel() {
	return deviceModel;
}
public void setDeviceModel(String deviceModel) {
	this.deviceModel = deviceModel;
}
public String getOccurTime() {
	return occurTime;
}
public void setOccurTime(String occurTime) {
	this.occurTime = occurTime;
}
public String getNetStatus() {
	return netStatus;
}
public void setNetStatus(String netStatus) {
	this.netStatus = netStatus;
}
public String getNetType() {
	return netType;
}
public void setNetType(String netType) {
	this.netType = netType;
}
public String getErrorLog() {
	return errorLog;
}
public void setErrorLog(String errorLog) {
	this.errorLog = errorLog;
}
public String getApkVersion() {
	return apkVersion;
}
public void setApkVersion(String apkVersion) {
	this.apkVersion = apkVersion;
}




}
