package com.sunrise.javascript.mode;

public class UserInfo {
public static final int LOGIN_CODE=0;
public static final int UN_LOGIN_CODE=1;
public static final String LOGIN_STR="用户已经登录";
public static final String UN_LOGIN_STR="用户未登录";
private String loginPhoneNumber;
private int resultCode;
private String resultMessage;
private String token;
private String cridits_class_info;//星级信息

public String getLoginPhoneNumber() {
	return loginPhoneNumber;
}

public void setLoginPhoneNumber(String loginPhoneNumber) {
	this.loginPhoneNumber = loginPhoneNumber;
}

public int getResultCode() {
	return resultCode;
}

public void setResultCode(int resultCode) {
	this.resultCode = resultCode;
}

public String getResultMessage() {
	return resultMessage;
}

public void setResultMessage(String resultMessage) {
	this.resultMessage = resultMessage;
}

public String getToken() {
	return token;
}

public void setToken(String token) {
	this.token = token;
}

public String getCriditsClassInfo() {
	return cridits_class_info;
}

public void setCriditsClassInfo(String cridits_class_info) {
	this.cridits_class_info = cridits_class_info;
}

}
