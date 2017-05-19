package com.sunrise.marketingassistant.entity;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 签到轨迹
 * 
 * @author 珩
 */
@SuppressLint("SimpleDateFormat")
public class RegisteTrajectoryBeen {
	private String GROUP_ID;
	private String GROUP_NAME;
	private double LATITUDE;
	private double LONGITUDE;
	private long REGISTIME;

	public String getGROUP_ID() {
		return GROUP_ID;
	}

	public String getGROUP_NAME() {
		return GROUP_NAME;
	}

	public double getLATITUDE() {
		return LATITUDE;
	}

	public double getLONGITUDE() {
		return LONGITUDE;
	}

	public long getREGISTIME() {
		return REGISTIME;
	}

	public void setGROUP_ID(String gROUP_ID) {
		GROUP_ID = gROUP_ID;
	}

	public void setGROUP_NAME(String gROUP_NAME) {
		GROUP_NAME = gROUP_NAME;
	}

	public void setLATITUDE(double lATITUDE) {
		LATITUDE = lATITUDE;
	}

	public void setLONGITUDE(double lONGITUDE) {
		LONGITUDE = lONGITUDE;
	}

	public void setREGISTIME(long rEGISTIMEl) {
		REGISTIME = rEGISTIMEl;
	}

	public MobileBusinessHall toMobileBusinessHall(String pattern) {
		MobileBusinessHall mbh = new MobileBusinessHall();
		mbh.setGROUP_ID(GROUP_ID);
		mbh.setGROUP_NAME(GROUP_NAME);
		mbh.setLATITUDE(LATITUDE);
		mbh.setLONGITUDE(LONGITUDE);
		mbh.setREGISTIME(new SimpleDateFormat(pattern).format(new Date(getREGISTIME())));
		return mbh;
	}
}
