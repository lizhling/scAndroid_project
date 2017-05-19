package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;

/**
 * 呼叫转移查询接口
 * 
 * @author fuheng
 * 
 */
public class CallTransferQueryTask extends GenericTask {

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String phone_no = UserInfoControler.getInstance().getUserName();
		try {
			String result = App.sServerClient.queryCallTransfer(phone_no,UserInfoControler.getInstance().getAuthorKey());
			publishProgress(result);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
