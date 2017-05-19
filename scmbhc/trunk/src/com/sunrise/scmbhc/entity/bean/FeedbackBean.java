package com.sunrise.scmbhc.entity.bean;

public class FeedbackBean{
private int id;
private String account4a;
private String mobile;
private String feedback;
private String createTime;
private String status;
private String reply;
private String replyPerson;
private String replyTime;
public final static String REPLYED="0";
public final static String UNREPLY="1";
public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getAccount4a() {
	return account4a;
}

public void setAccount4a(String account4a) {
	this.account4a = account4a;
}

public String getMobile() {
	return mobile;
}

public void setMobile(String mobile) {
	this.mobile = mobile;
}

public String getFeedback() {
	return feedback;
}

public void setFeedback(String feedback) {
	this.feedback = feedback;
}

public String getCreateTime() {
	return createTime;
}

public void setCreateTime(String createTime) {
	this.createTime = createTime;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public String getReply() {
	return reply;
}

public void setReply(String reply) {
	this.reply = reply;
}

public String getReplyPerson() {
	return replyPerson;
}

public void setReplyPerson(String replyPerson) {
	this.replyPerson = replyPerson;
}

public String getReplyTime() {
	return replyTime;
}

public void setReplyTime(String replyTime) {
	this.replyTime = replyTime;
}


}
