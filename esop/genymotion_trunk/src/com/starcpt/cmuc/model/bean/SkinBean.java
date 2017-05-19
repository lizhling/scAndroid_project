package com.starcpt.cmuc.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SkinBean implements Parcelable  {


	public static final int SKIN_STATE_DEFAULT = 0;
	public static final int SKIN_STATE_INSTALLED = 1;
	public static final int SKIN_STATE_DOWNLOADING = 2;
	public static final int SKIN_STATE_NEWSKIN = 3;
	public static final int SKIN_STATE_DOWNLOAD_OK = 4;
	public static final int SKIN_STATE_DOWNLOAD_FAILED = 5;
	
	private int skinId = 0;
	private String skinName = "默认皮肤";
	private String skinIcon;
	private String resolution;
	private String downUrl;
	private String createTime;
	private String operateSystem;
	private int skinState;
	private boolean currentSkin = false;
	
	public SkinBean() {}
	
	public SkinBean(int skinId, String skinName, String skinIcon,
			String resolution, String downUrl, String createTime,
			String operateSystem, int skinState) {
		super();
		this.skinId = skinId;
		this.skinName = skinName;
		this.skinIcon = skinIcon;
		this.resolution = resolution;
		this.downUrl = downUrl;
		this.createTime = createTime;
		this.operateSystem = operateSystem;
		this.skinState = skinState;
	}
	public SkinBean(int skinId, String skinName, String skinIcon,
			String resolution, String downUrl, String createTime,
			String operateSystem) {
		super();
		this.skinId = skinId;
		this.skinName = skinName;
		this.skinIcon = skinIcon;
		this.resolution = resolution;
		this.downUrl = downUrl;
		this.createTime = createTime;
		this.operateSystem = operateSystem;
	}
	
	
	 
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
//		result = prime * result + (currentSkin ? 1231 : 1237);
		result = prime * result + ((downUrl == null) ? 0 : downUrl.hashCode());
		result = prime * result
				+ ((operateSystem == null) ? 0 : operateSystem.hashCode());
		result = prime * result
				+ ((resolution == null) ? 0 : resolution.hashCode());
		result = prime * result
				+ ((skinIcon == null) ? 0 : skinIcon.hashCode());
		result = prime * result + skinId;
		result = prime * result
				+ ((skinName == null) ? 0 : skinName.hashCode());
//		result = prime * result + skinState;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkinBean other = (SkinBean) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
//		if (currentSkin != other.currentSkin)
//			return false;
		if (downUrl == null) {
			if (other.downUrl != null)
				return false;
		} else if (!downUrl.equals(other.downUrl))
			return false;
		if (operateSystem == null) {
			if (other.operateSystem != null)
				return false;
		} else if (!operateSystem.equals(other.operateSystem))
			return false;
		if (resolution == null) {
			if (other.resolution != null)
				return false;
		} else if (!resolution.equals(other.resolution))
			return false;
		if (skinIcon == null) {
			if (other.skinIcon != null)
				return false;
		} else if (!skinIcon.equals(other.skinIcon))
			return false;
		if (skinId != other.skinId)
			return false;
		if (skinName == null) {
			if (other.skinName != null)
				return false;
		} else if (!skinName.equals(other.skinName))
			return false;
//		if (skinState != other.skinState)
//			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SkinBean [skinId=" + skinId + ", skinName=" + skinName
				+ ", skinIcon=" + skinIcon + ", resolution=" + resolution
				+ ", downUrl=" + downUrl + ", createTime=" + createTime
				+ ", operateSystem=" + operateSystem + ", skinState="
				+ skinState + "]";
	}
	public int getSkinId() {
		return skinId;
	}
	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}
	public String getSkinName() {
		return skinName;
	}
	public void setSkinName(String skinName) {
		this.skinName = skinName;
	}
	public String getSkinIcon() {
		return skinIcon;
	}
	public void setSkinIcon(String skinIcon) {
		this.skinIcon = skinIcon;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getOperateSystem() {
		return operateSystem;
	}
	public void setOperateSystem(String operateSystem) {
		this.operateSystem = operateSystem;
	}
 
	public int getSkinState() {
		return skinState;
	}
	public void setSkinState(int skinState) {
		this.skinState = skinState;
	}
	/**
	 * @return the currentSkin
	 */
	public boolean isCurrentSkin() {
		return currentSkin;
	}
	/**
	 * @param currentSkin the currentSkin to set
	 */
	public void setCurrentSkin(boolean currentSkin) {
		this.currentSkin = currentSkin;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(skinId);
		dest.writeString(skinName);
		dest.writeString(skinIcon);
		dest.writeString(resolution);
		dest.writeString(downUrl);
		dest.writeString(createTime);
		dest.writeString(operateSystem);
		dest.writeInt(skinState);
		dest.writeString(String.valueOf(currentSkin));
	}
	
		public static final Parcelable.Creator<SkinBean> CREATOR = new Creator<SkinBean>(){
			 
	         @Override
	         public SkinBean createFromParcel(Parcel source) {
	             // TODO Auto-generated method stub
	             // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
	        	 SkinBean skinBean = new SkinBean();
	        	 skinBean.setSkinId(source.readInt());
	        	 skinBean.setSkinName(source.readString());
	        	 skinBean.setSkinIcon(source.readString());
	        	 skinBean.setResolution(source.readString());
	        	 skinBean.setDownUrl(source.readString());
	        	 skinBean.setCreateTime(source.readString());
	        	 skinBean.setOperateSystem(source.readString());
	        	 skinBean.setSkinState(source.readInt());
	        	 skinBean.setCurrentSkin(Boolean.valueOf(source.readString()));
	             return skinBean;
	         }
	 
	         @Override
	         public SkinBean[] newArray(int size) {
//	              TODO Auto-generated method stub
	             return new SkinBean[size];
	         }
	     };

}
