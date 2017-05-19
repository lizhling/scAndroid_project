package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.entity.PaymentContainer;

/**
 * 获取支付类型
 * 
 * @author fuheng
 * 
 */
public class LoadPayTypeTask extends GenericTask {
	/**
	 * @param taskListener
	 * @return
	 */
	public static LoadPayTypeTask execute(TaskListener taskListener) {
		LoadPayTypeTask result = new LoadPayTypeTask();
		result.setListener(taskListener);
		result.execute();
		return result;
	}

	private LoadPayTypeTask() {
		super();
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		try {
			String result = App.sServerClient.getPayTypeAction();
			publishProgress(new PaymentContainer(result));
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
