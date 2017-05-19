package com.sunrise.marketingassistant.entity;

public class MobileBusinessHallFromSearch {
	private String GROUP_ID;
	private String GROUP_NAME;
	private String LATITUDE;
	private String LONGITUDE;
	private String CLASS_CODE;
	private String CLASS_NAME;
	private String IMG_INFO;
	private String GRADE_CODE;
	private String GRADE_NAME;
	private int RN;

	public String getGROUP_ID() {
		return GROUP_ID;
	}

	public String getGROUP_NAME() {
		return GROUP_NAME;
	}

	public String getLATITUDE() {
		return LATITUDE;
	}

	public String getLONGITUDE() {
		return LONGITUDE;
	}

	public String getCLASS_CODE() {
		return CLASS_CODE;
	}

	public String getCLASS_NAME() {
		return CLASS_NAME;
	}

	public String getIMG_INFO() {
		return IMG_INFO;
	}

	public String getGRADE_CODE() {
		return GRADE_CODE;
	}

	public String getGRADE_NAME() {
		return GRADE_NAME;
	}

	public int getRN() {
		return RN;
	}

	public void setGROUP_ID(String gROUP_ID) {
		GROUP_ID = gROUP_ID;
	}

	public void setGROUP_NAME(String gROUP_NAME) {
		GROUP_NAME = gROUP_NAME;
	}

	public void setLATITUDE(String lATITUDE) {
		LATITUDE = lATITUDE;
	}

	public void setLONGITUDE(String lONGITUDE) {
		LONGITUDE = lONGITUDE;
	}

	public void setCLASS_CODE(String cLASS_CODE) {
		CLASS_CODE = cLASS_CODE;
	}

	public void setCLASS_NAME(String cLASS_NAME) {
		CLASS_NAME = cLASS_NAME;
	}

	public void setIMG_INFO(String iMG_INFO) {
		IMG_INFO = iMG_INFO;
	}

	public void setGRADE_CODE(String gRADE_CODE) {
		GRADE_CODE = gRADE_CODE;
	}

	public void setGRADE_NAME(String gRADE_NAME) {
		GRADE_NAME = gRADE_NAME;
	}

	public void setRN(int rN) {
		RN = rN;
	}

	public MobileBusinessHall toMobileBusinessHall() {

		MobileBusinessHall hall = new MobileBusinessHall();
		hall.setGROUP_ID(GROUP_ID);
		hall.setGROUP_NAME(GROUP_NAME);
		if (LATITUDE != null)
			hall.setLATITUDE(Double.parseDouble(LATITUDE));
		if (LONGITUDE != null)
			hall.setLONGITUDE(Double.parseDouble(LONGITUDE));
		hall.setCLASS_CODE(CLASS_CODE);
		hall.setCLASS_NAME(CLASS_NAME);
		hall.setIMG_INFO(IMG_INFO);
		hall.setGRADE_CODE(GRADE_CODE);
		hall.setGRADE_NAME(GRADE_NAME);
		hall.setRN(RN);

		return hall;
	}
}
