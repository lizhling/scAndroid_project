package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;

/**
 * 合家欢开通或关闭
 * 
 * @author fuheng
 * 
 */
public class HJH_OpenTask extends GenericTask {

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		
		if (!UserInfoControler.getInstance().checkUserLoginIn()) {
			setException(new NullPointerException("请先登录！！！"));
			return TaskResult.AUTH_ERROR;
		}
		
		try {
			String result = App.sServerClient.openWholeFamily(UserInfoControler.getInstance().getUserName(),UserInfoControler.getInstance().getAuthorKey());
			publishProgress(result);
		} catch (Exception e) {
			setException(e);
			e.printStackTrace();
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
