package com.sunrise.scmbhc.entity;

import com.baidu.mapapi.model.LatLng;


public class MobileBusinessHall implements Comparable<MobileBusinessHall>{
	private String id="0";
	private String name;
	private String address;
	private String phoneNumber;
	private String iconUrl;
	private String workingDay;
	private String holiDay;
	private int canbeappoint;
	private LatLng coordinate;
	private double distance;
	private String waitPeople="0";
	public MobileBusinessHall(String name, String address, String phoneNumber,
			String iconUrl, String workingDay, String holiDay) {
		super();
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.iconUrl = iconUrl;
		this.workingDay = workingDay;
		this.holiDay = holiDay;
	}
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNumber() {
		return "136xxxxxxxx";
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getWorkingDay() {
		return "09:00-17";
	}
	public void setWorkingDay(String workingDay) {
		this.workingDay = workingDay;
	}
	public String getHoliDay() {
		return "09:00-12:00;14:00-18:00";
	}
	public void setHoliDay(String holiDay) {
		this.holiDay = holiDay;
	}
	public LatLng getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(LatLng coordinate) {
		this.coordinate = coordinate;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	@Override
	public int compareTo(MobileBusinessHall another) {
		if(distance<another.getDistance()){
			return -1;
		}
		if(distance>another.getDistance()){
			return 1;
		}
		return 0;
	}


	public String getWaitPeople() {
		return waitPeople;
	}


	public void setWaitPeople(String waitPeople) {
		this.waitPeople = waitPeople;
	}


	public int getCanbeappoint() {
		return canbeappoint;
	}


	public void setCanbeappoint(int canbeappoint) {
		this.canbeappoint = canbeappoint;
	}
	
	
}
