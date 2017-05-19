package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.BusinessMenu;

public class HandleBusinessTask extends GenericTask {

	private BusinessMenu mBusinessItem;

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params.length == 0)
			return TaskResult.AUTH_ERROR;
		mBusinessItem = (BusinessMenu) params[0].get(ExtraKeyConstant.KEY_BUSINESS_INFO);
		String phoneNum = UserInfoControler.getInstance().getUserName();
		try {
			String str = App.sServerClient.handleBusiness(getBusinessItem().getServiceUrl(), getBusinessData(), phoneNum, getBusinessItem().getId(),UserInfoControler.getInstance().getAuthorKey());
			publishProgress(new JSONObject(str).getJSONObject("RETURN").getString("RETURN_MESSAGE"));
		} catch (Exception e) {
			setException(e);
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

	protected String getBusinessData() {
		return getBusinessItem().getBusinessData();
	}

	public BusinessMenu getBusinessItem() {
		return mBusinessItem;
	}
}
