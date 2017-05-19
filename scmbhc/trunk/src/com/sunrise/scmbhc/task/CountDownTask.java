package com.sunrise.scmbhc.task;

public class CountDownTask extends GenericTask {

	public static final String KEY_TIMES = "times";
	public static final String KEY_REFRESH_PERIOD = "refresh period";

	public static CountDownTask execute(int times, long refresh, TaskListener listener) {
		CountDownTask task = new CountDownTask();
		TaskParams params = new TaskParams(KEY_TIMES, times);
		params.put(KEY_REFRESH_PERIOD, refresh);
		task.setListener(listener);
		task.execute(params);
		return task;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length < 1)
			return TaskResult.AUTH_ERROR;

		int times = (Integer) params[0].get(KEY_TIMES);
		long refresh = (Long) params[0].get(KEY_REFRESH_PERIOD);

		while (times > 0) {
			publishProgress(times / refresh);
			try {
				Thread.sleep(refresh);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			times-=refresh;
		}
		publishProgress(times);
		return TaskResult.OK;
	}

}
