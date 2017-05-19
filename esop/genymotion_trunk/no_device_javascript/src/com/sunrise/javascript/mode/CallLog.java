package com.sunrise.javascript.mode;

import java.io.Serializable;

public class CallLog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _id;
	private String number;
	private int date;
	private int duration;
	private String cachedName;
	private int cachedNumberType;
	private String cachedNumberLabel;
	public int get_id() {
		return _id;
	}
	public void setId(int i) {
		this._id = i;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getCachedName() {
		return cachedName;
	}
	public void setCachedName(String cachedName) {
		this.cachedName = cachedName;
	}
	public int getCachedNumberType() {
		return cachedNumberType;
	}
	public void setCachedNumberType(int cachedNumberType) {
		this.cachedNumberType = cachedNumberType;
	}
	public String getCachedNumberLabel() {
		return cachedNumberLabel;
	}
	public void setCachedNumberLabel(String cachedNumberLabel) {
		this.cachedNumberLabel = cachedNumberLabel;
	}
	@Override
	public String toString() {
		return "_id=" + _id + ", number=" + number + ", date=" + date
				+ ", duration=" + duration + ", cachedName=" + cachedName
				+ ", cachedNumberType=" + cachedNumberType
				+ ", cachedNumberLabel=" + cachedNumberLabel;
	}
	
	
	
}
