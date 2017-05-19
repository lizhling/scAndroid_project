package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.App;

/**
 * 获取预约人数
 * 
 * @author 珩
 * 
 */
public class GetWaitNumberTask extends GenericTask {
	private static final String ID = "id";

	public static GetWaitNumberTask execute(String id, TaskListener taskListener) {

		GetWaitNumberTask task = new GetWaitNumberTask();
		task.setListener(taskListener);
		task.execute(new TaskParams(ID, id));
		return task;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String id = (String) params[0].get(ID);
		try {
			String number = App.sServerClient.getWaitPeople(id);
			publishProgress(number);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}
}
