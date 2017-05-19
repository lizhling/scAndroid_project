package com.sunrise.scmbhc.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 电子券兑换
 * 
 * @author fuheng
 * 
 */
public class ECoupon implements Parcelable {
	private String USERULE_NAME;
	private String EQUAL_SCORE;
	private int credits;

	public String getName() {
		return USERULE_NAME;
	}

	public void setUSERULE_NAME(String uSERULE_NAME) {
		USERULE_NAME = uSERULE_NAME;
	}

	public String getScore() {
		return EQUAL_SCORE;
	}

	public void setEQUAL_SCORE(String eQUAL_SCORE) {
		EQUAL_SCORE = eQUAL_SCORE;
	}

	public static final Parcelable.Creator<ECoupon> CREATOR = new Parcelable.Creator<ECoupon>() {

		@Override
		public ECoupon createFromParcel(Parcel p) {
			ECoupon item = new ECoupon();
			item.setEQUAL_SCORE(p.readString());
			item.setUSERULE_NAME(p.readString());
			item.setCredits(p.readInt());
			return item;
		}

		@Override
		public ECoupon[] newArray(int size) {
			return new ECoupon[size];
		}

	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(EQUAL_SCORE);
		dest.writeString(USERULE_NAME);
		dest.writeInt(credits);
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}
}
