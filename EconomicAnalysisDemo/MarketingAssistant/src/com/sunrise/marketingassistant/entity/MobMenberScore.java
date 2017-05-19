package com.sunrise.marketingassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 店员积分
 * 
 * @author 珩
 * 
 */
public class MobMenberScore implements Parcelable {
	/** 渠道编号 */
	private String GROUP_ID;
	/** 店员编码 */
	private String PERSON_ID;
	/** 店员名称 */
	private String PERSON_NAME;
	/** 当前积分 */
	private String CUR_SCORE;
	/** 总积分 */
	private String TOL_SCORE;

	public String getGROUP_ID() {
		return GROUP_ID;
	}

	public String getPERSON_ID() {
		return PERSON_ID;
	}

	public String getPERSON_NAME() {
		return PERSON_NAME;
	}

	public String getCUR_SCORE() {
		return CUR_SCORE;
	}

	public String getTOL_SCORE() {
		return TOL_SCORE;
	}

	public void setGROUP_ID(String gROUP_ID) {
		GROUP_ID = gROUP_ID;
	}

	public void setPERSON_ID(String pERSON_ID) {
		PERSON_ID = pERSON_ID;
	}

	public void setPERSON_NAME(String pERSON_NAME) {
		PERSON_NAME = pERSON_NAME;
	}

	public void setCUR_SCORE(String cUR_SCORE) {
		CUR_SCORE = cUR_SCORE;
	}

	public void setTOL_SCORE(String tOL_SCORE) {
		TOL_SCORE = tOL_SCORE;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeString(getCUR_SCORE());
		p.writeString(getGROUP_ID());
		p.writeString(getPERSON_ID());
		p.writeString(getPERSON_NAME());
		p.writeString(getTOL_SCORE());
	}

	public static final Parcelable.Creator<MobMenberScore> CREATOR = new Parcelable.Creator<MobMenberScore>() {

		@Override
		public MobMenberScore createFromParcel(Parcel p) {
			MobMenberScore item = new MobMenberScore();
			item.setCUR_SCORE(p.readString());
			item.setGROUP_ID(p.readString());
			item.setPERSON_ID(p.readString());
			item.setPERSON_NAME(p.readString());
			item.setTOL_SCORE(p.readString());
			return item;
		}

		@Override
		public MobMenberScore[] newArray(int size) {
			return new MobMenberScore[size];
		}

	};
}
