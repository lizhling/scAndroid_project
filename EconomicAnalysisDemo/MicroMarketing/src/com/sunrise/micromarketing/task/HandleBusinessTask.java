package com.sunrise.micromarketing.task;

import org.json.JSONObject;

import com.sunrise.micromarketing.entity.BusinessMenu;
import com.sunrise.micromarketing.net.ServerClient;

public class HandleBusinessTask extends GenericTask {

	private BusinessMenu mBusinessItem;

	public HandleBusinessTask execute(BusinessMenu business,String phoneNum,String authorKey,TaskListener listener){
		TaskParams params = new TaskParams("businessInfo", business);
		params.put("phone", phoneNum);
		params.put("author_key", authorKey);
		setListener(listener);
		execute(params );
		
		return this;
	}
	
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params.length == 0)
			return TaskResult.AUTH_ERROR;
		mBusinessItem = (BusinessMenu) params[0].get("businessInfo");
		String phoneNum = (String) params[0].get("phone");
		String authorKey = (String) params[0].get("author_key");
		try {
			String str = ServerClient.getInstance().handleBusiness(getBusinessItem().getServiceUrl(), getBusinessData(), phoneNum, getBusinessItem().getId(),
					authorKey);
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
