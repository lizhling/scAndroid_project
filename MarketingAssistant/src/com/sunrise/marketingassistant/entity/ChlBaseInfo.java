package com.sunrise.marketingassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ChlBaseInfo implements Parcelable {
	private long chlId; // 渠道编号
	private String chlName; // 渠道名称
	private String chlLvl; // 渠道等级
	private String busareaId; // 归属商圈
	private String area; // 营业厅面积
	private String localNature; // 地理位置属性
	private String longitude; // 经度
	private String latitude; // 纬度
	private String radius; // 服务半径
	private String personNum; // 人口覆盖
	private String personDensity; // 人口密度
	private String rentFee; // 附近每100平米租金
	private String empNum; // 台席数量
	private String salerNum; // 营业员人数
	private String address; // 详细地址
	private String isNew; // 是否新增渠道
	private String isLost; // 是否移动流失渠道
	private String mktCenterType; // 归属营销中心类型
	private String mktCenter; // 归属营销中心
	private String LoginNo;
	private String chlType;
	private String chllvl;// 运营商名称
	private String groupId;
	private String outdoorPic;
	private String imgId;

	public long getChlId() {
		return chlId;
	}

	public void setChlId(long chlId) {
		this.chlId = chlId;
	}

	public String getChlName() {
		return chlName;
	}

	public void setChlName(String chlName) {
		this.chlName = chlName;
	}

	public String getChlLvl() {
		if (chllvl != null)
			return chllvl;
		return chlLvl;
	}

	public void setChlLvl(String chlLvl) {
		this.chlLvl = chlLvl;
	}

	public String getBusareaId() {
		return busareaId;
	}

	public void setBusareaId(String busareaId) {
		this.busareaId = busareaId;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLocalNature() {
		return localNature;
	}

	public void setLocalNature(String localNature) {
		this.localNature = localNature;
	}

	public String getLongitude() {
		return longitude;
	}

	public double getLongitudeDouble() {
		if (getLongitude() != null)
			return Double.parseDouble(getLongitude());
		return 0;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public double getLatitudeDouble() {
		if (getLatitude() != null)
			return Double.parseDouble(getLatitude());
		return 0;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getPersonNum() {
		return personNum;
	}

	public void setPersonNum(String personNum) {
		this.personNum = personNum;
	}

	public String getPersonDensity() {
		return personDensity;
	}

	public void setPersonDensity(String personDensity) {
		this.personDensity = personDensity;
	}

	public String getRentFee() {
		return rentFee;
	}

	public void setRentFee(String rentFee) {
		this.rentFee = rentFee;
	}

	public String getEmpNum() {
		return empNum;
	}

	public void setEmpNum(String empNum) {
		this.empNum = empNum;
	}

	public String getSalerNum() {
		return salerNum;
	}

	public void setSalerNum(String salerNum) {
		this.salerNum = salerNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getIsLost() {
		return isLost;
	}

	public void setIsLost(String isLost) {
		this.isLost = isLost;
	}

	public String getMktCenterType() {
		return mktCenterType;
	}

	public void setMktCenterType(String mktCenterType) {
		this.mktCenterType = mktCenterType;
	}

	public String getMktCenter() {
		return mktCenter;
	}

	public void setMktCenter(String mktCenter) {
		this.mktCenter = mktCenter;
	}

	public String getLoginNo() {
		return LoginNo;
	}

	public void setLoginNo(String loginNo) {
		LoginNo = loginNo;
	}

	public String getChlType() {
		return chlType;
	}

	public void setChlType(String chlType) {
		this.chlType = chlType;
	}

	public String getChllvl() {
		if (chlLvl != null)
			return chlLvl;
		return chllvl;
	}

	public void setChllvl(String chllvl) {
		this.chllvl = chllvl;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getOutdoorPic() {
		return outdoorPic;
	}

	public void setOutdoorPic(String outdoorPic) {
		this.outdoorPic = outdoorPic;
	}

	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeString(getAddress());
		p.writeString(getArea());
		p.writeString(getBusareaId());

		p.writeLong(getChlId());
		p.writeString(getChlLvl());
		p.writeString(getChllvl());
		p.writeString(getChlName());
		p.writeString(getChlType());

		p.writeString(getEmpNum());
		p.writeString(getGroupId());

		p.writeString(getIsLost());
		p.writeString(getIsNew());

		p.writeString(getLatitude());
		p.writeString(getLocalNature());
		p.writeString(getLoginNo());
		p.writeString(getLongitude());

		p.writeString(getMktCenter());
		p.writeString(getMktCenterType());
		p.writeString(getPersonDensity());
		p.writeString(getPersonNum());

		p.writeString(getRadius());
		p.writeString(getRentFee());
		p.writeString(getSalerNum());
	}

	public static final Parcelable.Creator<ChlBaseInfo> CREATOR = new Parcelable.Creator<ChlBaseInfo>() {

		@Override
		public ChlBaseInfo createFromParcel(Parcel p) {
			ChlBaseInfo c = new ChlBaseInfo();
			c.setAddress(p.readString());
			c.setArea(p.readString());
			c.setBusareaId(p.readString());

			c.setChlId(p.readLong());
			c.setChlLvl(p.readString());
			c.setChllvl(p.readString());
			c.setChlName(p.readString());
			c.setChlType(p.readString());

			c.setEmpNum(p.readString());
			c.setGroupId(p.readString());

			c.setIsLost(p.readString());
			c.setIsNew(p.readString());

			c.setLatitude(p.readString());
			c.setLocalNature(p.readString());
			c.setLoginNo(p.readString());
			c.setLongitude(p.readString());

			c.setMktCenter(p.readString());
			c.setMktCenterType(p.readString());
			c.setPersonDensity(p.readString());
			c.setPersonNum(p.readString());

			c.setRadius(p.readString());
			c.setRentFee(p.readString());
			c.setSalerNum(p.readString());
			return c;
		}

		@Override
		public ChlBaseInfo[] newArray(int size) {
			return new ChlBaseInfo[size];
		}

	};
}
