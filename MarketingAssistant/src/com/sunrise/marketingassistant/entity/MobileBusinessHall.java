package com.sunrise.marketingassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class MobileBusinessHall implements Parcelable {
	private String GROUP_ID;
	private String GROUP_NAME;
	private double LATITUDE;
	private double LONGITUDE;
	private String CLASS_CODE;
	private String CLASS_NAME;
	private String IMG_INFO;
	private String GRADE_CODE;
	private String GRADE_NAME;

	private String ACTIVE_TIME;
	private String GROUP_ADDRESS;
	private String CONTACT_PERSON;
	private String CONTACT_PHONE;
	private int GROUP_AREA;
	private String RWD_TOTAL;

	private String G4_TARIFF_ADD;
	private String G4_TERM_SALES;
	private String BROADBAND_NUMS;
	private String DATA_CYCLE;
	private String RANK;

	private String REGISTIME;

	private int RN;

	public MobileBusinessHall() {

	}

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

	public String getACTIVE_TIME() {
		return ACTIVE_TIME;
	}

	public String getGROUP_ADDRESS() {
		return GROUP_ADDRESS;
	}

	public String getCONTACT_PERSON() {
		return CONTACT_PERSON;
	}

	public String getCONTACT_PHONE() {
		return CONTACT_PHONE;
	}

	public int getGROUP_AREA() {
		return GROUP_AREA;
	}

	public String getRWD_TOTAL() {
		return RWD_TOTAL;
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

	public void setLATITUDE(double lATITUDE) {
		LATITUDE = lATITUDE;
	}

	public void setLONGITUDE(double lONGITUDE) {
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

	public void setACTIVE_TIME(String aCTIVE_TIME) {
		ACTIVE_TIME = aCTIVE_TIME;
	}

	public void setGROUP_ADDRESS(String gROUP_ADDRESS) {
		GROUP_ADDRESS = gROUP_ADDRESS;
	}

	public void setCONTACT_PERSON(String cONTACT_PERSON) {
		CONTACT_PERSON = cONTACT_PERSON;
	}

	public void setCONTACT_PHONE(String cONTACT_PHONE) {
		CONTACT_PHONE = cONTACT_PHONE;
	}

	public void setGROUP_AREA(int gROUP_AREA) {
		GROUP_AREA = gROUP_AREA;
	}

	public void setRWD_TOTAL(String rWD_TOTAL) {
		RWD_TOTAL = rWD_TOTAL;
	}

	public String getG4_TARIFF_ADD() {
		return G4_TARIFF_ADD;
	}

	public String getG4_TERM_SALES() {
		return G4_TERM_SALES;
	}

	public String getBROADBAND_NUMS() {
		return BROADBAND_NUMS;
	}

	public String getDATA_CYCLE() {
		return DATA_CYCLE;
	}

	public String getRANK() {
		return RANK;
	}

	public void setG4_TARIFF_ADD(String g4_TARIFF_ADD) {
		G4_TARIFF_ADD = g4_TARIFF_ADD;
	}

	public void setG4_TERM_SALES(String g4_TERM_SALES) {
		G4_TERM_SALES = g4_TERM_SALES;
	}

	public void setBROADBAND_NUMS(String bROADBAND_NUMS) {
		BROADBAND_NUMS = bROADBAND_NUMS;
	}

	public void setDATA_CYCLE(String dATA_CYCLE) {
		DATA_CYCLE = dATA_CYCLE;
	}

	public void setRANK(String rANK) {
		RANK = rANK;
	}

	public void setRN(int rN) {
		RN = rN;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeString(getACTIVE_TIME());
		p.writeString(getCLASS_CODE());
		p.writeString(getCLASS_NAME());
		p.writeString(getCONTACT_PERSON());
		p.writeString(getCONTACT_PHONE());

		p.writeString(getGRADE_CODE());
		p.writeString(getGRADE_NAME());
		p.writeString(getGROUP_ADDRESS());
		p.writeInt(getGROUP_AREA());
		p.writeString(getGROUP_ID());

		p.writeString(getGROUP_NAME());
		p.writeString(getIMG_INFO());
		p.writeDouble(getLATITUDE());
		p.writeDouble(getLONGITUDE());
		p.writeString(getRWD_TOTAL());

		p.writeString(getG4_TARIFF_ADD());
		p.writeString(getG4_TERM_SALES());
		p.writeString(getBROADBAND_NUMS());
		p.writeString(getDATA_CYCLE());
		p.writeString(getRANK());

		p.writeString(getREGISTIME());
		p.writeInt(getRN());
	}

	public String getREGISTIME() {
		return REGISTIME;
	}

	public void setREGISTIME(String rEGISTIME) {
		REGISTIME = rEGISTIME;
	}

	public void addREGISTIME(String registime) {
		if (REGISTIME == null)
			REGISTIME = registime;
		else
			REGISTIME = REGISTIME + "\n" + registime;
	}

	public static final Parcelable.Creator<MobileBusinessHall> CREATOR = new Parcelable.Creator<MobileBusinessHall>() {

		@Override
		public MobileBusinessHall createFromParcel(Parcel p) {
			MobileBusinessHall item = new MobileBusinessHall();
			item.setACTIVE_TIME(p.readString());
			item.setCLASS_CODE(p.readString());
			item.setCLASS_NAME(p.readString());
			item.setCONTACT_PERSON(p.readString());
			item.setCONTACT_PHONE(p.readString());

			item.setGRADE_CODE(p.readString());
			item.setGRADE_NAME(p.readString());
			item.setGROUP_ADDRESS(p.readString());
			item.setGROUP_AREA(p.readInt());
			item.setGROUP_ID(p.readString());

			item.setGROUP_NAME(p.readString());
			item.setIMG_INFO(p.readString());
			item.setLATITUDE(p.readDouble());
			item.setLONGITUDE(p.readDouble());
			item.setRWD_TOTAL(p.readString());

			item.setG4_TARIFF_ADD(p.readString());
			item.setG4_TERM_SALES(p.readString());
			item.setBROADBAND_NUMS(p.readString());
			item.setDATA_CYCLE(p.readString());
			item.setRANK(p.readString());

			item.setREGISTIME(p.readString());
			item.setRN(p.readInt());
			return item;
		}

		@Override
		public MobileBusinessHall[] newArray(int size) {
			return new MobileBusinessHall[size];
		}

	};

	public void merge(MobileBusinessHall h) {
		if (getACTIVE_TIME() == null)
			setACTIVE_TIME(h.getACTIVE_TIME());
		if (getCLASS_CODE() == null)
			setCLASS_CODE(h.getCLASS_CODE());
		if (getCLASS_NAME() == null)
			setCLASS_NAME(h.getCLASS_NAME());
		if (getCONTACT_PERSON() == null)
			setCONTACT_PERSON(h.getCONTACT_PERSON());
		if (getCONTACT_PHONE() == null)
			setCONTACT_PHONE(h.getCONTACT_PHONE());

		if (getGRADE_CODE() == null)
			setGRADE_CODE(h.getGRADE_CODE());
		if (getGRADE_NAME() == null)
			setGRADE_NAME(h.getGRADE_NAME());
		if (getGROUP_ADDRESS() == null)
			setGROUP_ADDRESS(h.getGROUP_ADDRESS());
		if (getGROUP_AREA() == 0)
			setGROUP_AREA(h.getGROUP_AREA());
		if (getGROUP_ID() == null)
			setGROUP_ID(h.getGROUP_ID());

		if (getGROUP_NAME() == null)
			setGROUP_NAME(h.getGROUP_NAME());
		if (getIMG_INFO() == null)
			setIMG_INFO(h.getIMG_INFO());
		if (getLATITUDE() == 0)
			setLATITUDE(h.getLATITUDE());
		if (getLONGITUDE() == 0)
			setLONGITUDE(h.getLONGITUDE());
		if (getRWD_TOTAL() == null)
			setRWD_TOTAL(h.getRWD_TOTAL());

		if (getG4_TARIFF_ADD() == null)
			setG4_TARIFF_ADD(h.getG4_TARIFF_ADD());
		if (getG4_TERM_SALES() == null)
			setG4_TERM_SALES(h.getG4_TERM_SALES());
		if (getBROADBAND_NUMS() == null)
			setBROADBAND_NUMS(h.getBROADBAND_NUMS());
		if (getDATA_CYCLE() == null)
			setDATA_CYCLE(h.getDATA_CYCLE());
		if (getRANK() == null)
			setRANK(h.getRANK());

		if (getREGISTIME() == null)
			setREGISTIME(h.getREGISTIME());

		if (getRN() == 0)
			setRN(h.getRN());
	}

	public String getIMG_INFO(int index) {
		String imageInfo = getIMG_INFO();// "A:319672;B:319620;C:319624";
		if (imageInfo != null && imageInfo.length() > 8) {
			String[] tempArr = imageInfo.split(";");
			for (int i = 0; i < tempArr.length; i++) {
				if (i == index) {
					String temp = tempArr[i].substring(2);
					if (temp != null && temp.length() > 0) {
						return temp;
					}
				}
			}
		}
		return null;
	}

	public String getIMG_INFO_FIRST() {
		String imageInfo = getIMG_INFO();// "A:319672;B:319620;C:319624";
		if (imageInfo != null && imageInfo.length() > 8) {
			String[] tempArr = imageInfo.split(";");
			for (int i = 0; i < tempArr.length; i++) {
				String temp = tempArr[i].substring(2);
				if (temp != null && temp.length() > 0) {
					return temp;
				}
			}
		}
		return null;
	}
}
