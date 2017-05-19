package com.starcpt.cmuc.model.bean;

public class VisitHistoryBean {
	private long time;
	private String title;
	private String url;
	private String userName;
	private int id;
	
	public VisitHistoryBean(long time, String title, String url) {
		super();
		this.time = time;
		this.title = title;
		this.url = url;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
}
