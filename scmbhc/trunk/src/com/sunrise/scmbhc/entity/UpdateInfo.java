package com.sunrise.scmbhc.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateInfo implements Parcelable {
	public static final int TYPE_APK = 1;
	public static final int TYPE_FORCE_UPDATE = 1; // 强制更新 add by qinhubao
	public static final int TYPE_SELECT_UPDATE = 2; // 可选更新 add by qinhubao
	public static final int TYPE_MENUS = 2;
	public static final int TYPE_COMMONLY_BUSINESS = 3;
	public static final int TYPE_SYSTEM_NOTICE = 6;
	public static final int TYPE_COMMONLY_PROBLEM = 7;
	public static final int TYPE_USER_GUIDE = 8;
	public static final int TYPE_BUSINESS_HALL = 9;
	public static final int TYPE_CREDITS_EXCHANGE = 10;//积分兑换
	public static final int TYPE_TOPUP_RATES = 11;//充值利率
	public static final int TYPE_START_GUIDE = 13; // 启动时候的启动指引
	public static final long MENUS_DEFAULT_VERSION = 20140113;
	public static final long COMMONLY_BUSINESS_DEFAULT_VERSION = 20140113;

	private int type;
	private String downloadUrl;
	private long newVersionCode;
	private String suportVersion;
	private String newVersionName;
	private String updateDescription;
	private String suportVersionName;
	private int updateType; // 是否强制更新字段 add by qinhubao

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public long getNewVersionCode() {
		return newVersionCode;
	}

	public void setNewVersionCode(long newVersionCode) {
		this.newVersionCode = newVersionCode;
	}

	public String getSuportVersion() {
		return suportVersion;
	}

	public void setSuportVersion(String suportVersion) {
		this.suportVersion = suportVersion;
	}

	public String getNewVersionName() {
		return newVersionName;
	}

	public void setNewVersionName(String newVersionName) {
		this.newVersionName = newVersionName;
	}

	public String getUpdateDescription() {
		return updateDescription;
	}

	public void setUpdateDescription(String updateDescription) {
		this.updateDescription = updateDescription;
	}

	public String getSuportVersionName() {
		return suportVersionName;
	}

	public void setSuportVersionName(String suportVersionName) {
		this.suportVersionName = suportVersionName;
	}

	public int getUpdateType() {
		return updateType;
	}

	public void setUpdateType(int updateType) {
		this.updateType = updateType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeString(getDownloadUrl());
		p.writeLong(getNewVersionCode());
		p.writeString(getNewVersionName());
		p.writeString(getSuportVersion());
		p.writeString(getSuportVersionName());
		p.writeInt(getType());
		p.writeString(getUpdateDescription());
		p.writeInt(getUpdateType());
	}

	public static final Parcelable.Creator<UpdateInfo> CREATOR = new Parcelable.Creator<UpdateInfo>() {

		@Override
		public UpdateInfo createFromParcel(Parcel p) {
			UpdateInfo item = new UpdateInfo();
			item.setDownloadUrl(p.readString());
			item.setNewVersionCode(p.readLong());
			item.setNewVersionName(p.readString());
			item.setSuportVersion(p.readString());
			item.setSuportVersionName(p.readString());
			item.setType(p.readInt());
			item.setUpdateDescription(p.readString());
			item.setUpdateType(p.readInt());
			return item;
		}

		@Override
		public UpdateInfo[] newArray(int size) {
			return new UpdateInfo[size];
		}

	};

}
