package com.sunrise.marketingassistant.task;

import com.sunrise.marketingassistant.entity.UpdateInfo;

public class GetCommResTask extends DownloadZipPageTask {
	public UpdateInfo updateInfo;

	public final GetCommResTask execute(UpdateInfo updateInfo, TaskListener listener) {
		this.updateInfo = updateInfo;
		super.execute(updateInfo.getDownloadUrl(), listener);
		return this;
	}

}
