package com.sunrise.scmbhc.task;

import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.entity.BusinessMenu;

public class GetRecommendBusinessTask extends GenericTask {
	public GetRecommendBusinessTask excute(String phoneNo, String token, TaskListener taskListener) {
		this.setListener(taskListener);
		TaskParams params = new TaskParams(ExtraKeyConstant.KEY_PHONE_NUMBER, phoneNo);
		params.put(ExtraKeyConstant.KEY_TOKEN, token);
		this.execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		String phone_no = params[0].getString(ExtraKeyConstant.KEY_PHONE_NUMBER);
		String token = params[0].getString(ExtraKeyConstant.KEY_TOKEN);

		try {
			String result = App.sServerClient.getRecommendBusiness(phone_no, token);
			if (result.contains("RETURN_INFO") && result.contains("PROD_PRIC_NAME")) {
				JSONObject jobj = new JSONObject(result).getJSONObject("RETURN").getJSONArray("RETURN_INFO").getJSONObject(0);

				BusinessMenu item = new BusinessMenu();
				item.setName(jobj.getString("PROD_PRIC_NAME"));
				item.setProdPrcid(jobj.getString("PROD_PRIC_ID"));

				// {"PRICE":"5元/月","COMMENT":"包30M国内流量","PROD_PRIC_ID":"ACAZ22362","PROD_PRIC_NAME":"4G流量月包5元"}
				if (result.contains("PRICE"))
					item.setCharges(jobj.getString("PRICE"));
				if (result.contains("COMMENT"))
					item.setDescription(jobj.getString("COMMENT"));
				if (result.contains("WARMPROMPT"))
					item.setWarmPrompt(jobj.getString("WARMPROMPT"));
				publishProgress(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
