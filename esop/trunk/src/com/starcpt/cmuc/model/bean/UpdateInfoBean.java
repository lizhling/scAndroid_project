package com.starcpt.cmuc.model.bean;

public class UpdateInfoBean extends ResultBean{
	private int updateType;
	private String downloadUrl;
	private String updatedVersion;
	public final static int NO_NEED_UPDATE=0;	
	public final static int FORCE_UPDATE=1;
	public final static int SELECT_UPDATE=2;
	public static final String HAVE_DEVICE_CHECK_TYPE="0";
	public static final String COMMON_RES_CHECK_TYPE="1";
	public static final String NO_DEVICE_CHECK_TYPE="2";
	public static final String NO_DEVICE_TO_SUPPORT_DEVICE_CHECK_TYPE="3";
	public static final String BUSINESSGUIDE_CHECK_TYPE="4";
	
	public int getUpdateType() {
		return updateType;
	}
	
	public void setUpdateType(int updateStatus) {
		this.updateType = updateStatus;
	}
	
	public String getDownloadUrl() {
		return downloadUrl;
	}
	
	public void setDownloadUrl(String updateApkUrl) {
		this.downloadUrl = updateApkUrl;
	}

	
	public String getUpdatedVersion() {
		return updatedVersion;
	}

	public void setUpdatedVersion(String updatedVersion) {
		this.updatedVersion = updatedVersion;
	}

	@Override
	public String toString() {
		return "UpdateInfo [updateType=" + updateType + ", downloadUrl="
				+ downloadUrl + "]";
	}
	
	
	
	
}
