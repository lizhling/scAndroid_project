package com.sunrise.marketingassistant.task;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetChannellBitmapTask extends GenericTask implements ExtraKeyConstant {

	public GetChannellBitmapTask execute(String imginfo, TaskListener listener) {
		setListener(listener);
		TaskParams params = new TaskParams("imginfo", imginfo);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String imginfo = params[0].getString("imginfo");
		try {
			String result = null;

			result = ServerClient.getInstance().getBitmapOfChannel(imginfo);

			publishProgress(result);

		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
