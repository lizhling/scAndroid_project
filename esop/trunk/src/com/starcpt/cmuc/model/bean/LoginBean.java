package com.starcpt.cmuc.model.bean;

public class LoginBean extends ResultBean{
private String authentication;
private String notificationPassword;
private String mobile;

public String getAuthentication() {
	return authentication;
}

public void setAuthentication(String authentication) {
	this.authentication = authentication;
}


public String getNotificationPassword() {
	return notificationPassword;
}

public void setNotificationPassword(String notificationPassword) {
	this.notificationPassword = notificationPassword;
}


public String getMobile() {
	return mobile;
}

public void setMobile(String mobile) {
	this.mobile = mobile;
}

@Override
public String toString() {
	return "LoginBean [authentication=" + authentication + "]";
}


}
