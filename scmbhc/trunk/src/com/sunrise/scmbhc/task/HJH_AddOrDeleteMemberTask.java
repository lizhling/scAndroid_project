package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;

/**
 * 合家欢增删成员
 * 
 * @author fuheng
 * 
 */
public class HJH_AddOrDeleteMemberTask extends GenericTask {

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String memberNumber = params[0].getString(ExtraKeyConstant.KEY_MEMBER_NUMBER);
		boolean isDelete = Boolean.parseBoolean(params[0].getString(ExtraKeyConstant.KEY_IS_TRAFFIC_OVER));
		try {
			String mResult = App.sServerClient.deleteOrAddWholeFamilyMember(UserInfoControler.getInstance().getUserName(), memberNumber, isDelete,UserInfoControler.getInstance().getAuthorKey());
			publishProgress(mResult);
		} catch (Exception e) {
			setException(e);
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
