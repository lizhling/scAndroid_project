package com.sunrise.marketingassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 渠道占比
 * 
 * @author 珩
 * 
 */
public class ShareOfChannels implements Parcelable {
	private String CITY_ID;
	private String CITY_NAME;
	private String COUNTRY_ID;
	private String COUNTRY_NAME;
	private float CMCC_QUOTA;
	private float UNICOM_QUOTA;
	private float TELECOM_QUOTA;
	private String DATA_CYCLE;

	public String getCITY_ID() {
		return CITY_ID;
	}

	public String getCITY_NAME() {
		return CITY_NAME;
	}

	public String getCOUNTRY_ID() {
		return COUNTRY_ID;
	}

	public String getCOUNTRY_NAME() {
		return COUNTRY_NAME;
	}

	public float getCMCC_QUOTA() {
		return CMCC_QUOTA;
	}

	public float getUNICOM_QUOTA() {
		return UNICOM_QUOTA;
	}

	public float getTELECOM_QUOTA() {
		return TELECOM_QUOTA;
	}

	public String getDATA_CYCLE() {
		return DATA_CYCLE;
	}

	public void setCITY_ID(String cITY_ID) {
		CITY_ID = cITY_ID;
	}

	public void setCITY_NAME(String cITY_NAME) {
		CITY_NAME = cITY_NAME;
	}

	public void setCOUNTRY_ID(String cOUNTRY_ID) {
		COUNTRY_ID = cOUNTRY_ID;
	}

	public void setCOUNTRY_NAME(String cOUNTRY_NAME) {
		COUNTRY_NAME = cOUNTRY_NAME;
	}

	public void setCMCC_QUOTA(float cMCC_QUOTA) {
		CMCC_QUOTA = cMCC_QUOTA;
	}

	public void setUNICOM_QUOTA(float uNICOM_QUOTA) {
		UNICOM_QUOTA = uNICOM_QUOTA;
	}

	public void setTELECOM_QUOTA(float tELECOM_QUOTA) {
		TELECOM_QUOTA = tELECOM_QUOTA;
	}

	public void setDATA_CYCLE(String dATA_CYCLE) {
		DATA_CYCLE = dATA_CYCLE;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeString(CITY_ID);
		p.writeString(CITY_NAME);
		p.writeString(COUNTRY_ID);
		p.writeString(COUNTRY_NAME);
		p.writeFloat(CMCC_QUOTA);
		p.writeFloat(UNICOM_QUOTA);
		p.writeFloat(TELECOM_QUOTA);
		p.writeString(DATA_CYCLE);
	}

	public static final Parcelable.Creator<ShareOfChannels> CREATOR = new Parcelable.Creator<ShareOfChannels>() {

		@Override
		public ShareOfChannels createFromParcel(Parcel p) {
			ShareOfChannels item = new ShareOfChannels();
			item.setCITY_ID(p.readString());
			item.setCITY_NAME(p.readString());
			item.setCOUNTRY_ID(p.readString());
			item.setCOUNTRY_NAME(p.readString());
			item.setCMCC_QUOTA(p.readFloat());
			item.setUNICOM_QUOTA(p.readFloat());
			item.setTELECOM_QUOTA(p.readFloat());
			item.setDATA_CYCLE(p.readString());
			return item;
		}

		@Override
		public ShareOfChannels[] newArray(int size) {
			return new ShareOfChannels[size];
		}

	};

}
