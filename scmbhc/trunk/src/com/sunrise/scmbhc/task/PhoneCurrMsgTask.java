package com.sunrise.scmbhc.task;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.entity.PhoneCurInfo;

/**
 * 
 * @author fuheng
 * 
 */
public class PhoneCurrMsgTask extends GenericTask implements ExtraKeyConstant, TaskListener {

	private boolean isOverForGetPublishAccount;

	private JSONObject publicAccount;

	public PhoneCurrMsgTask execute(String phoneNumber, TaskListener taskListener) {
		setListener(taskListener);
		execute(new TaskParams(KEY_PHONE_NUMBER, phoneNumber));
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		startLoadPublicAccount();

		PhoneCurInfo phoneCurrMsg = null;

		try {
			String phone_no = params[0].getString(KEY_PHONE_NUMBER);
			String result = App.sServerClient.getPhoneCurrMsg(phone_no, UserInfoControler.getInstance().getAuthorKey());

			String curPhoneInfo = null;
			JSONObject jobj = new JSONObject(result).getJSONObject("RETURN");
			boolean isNewInterface = result.contains("OUT_MSG") && result.contains("NEW_INTERFACE");
			if (isNewInterface)
				curPhoneInfo = jobj.getJSONArray("RETURN_INFO").getJSONObject(0).getString("OUT_MSG");
			else
				curPhoneInfo = jobj.getString("RETURN_MESSAGE");

			phoneCurrMsg = new PhoneCurInfo(curPhoneInfo);

			while (!isOverForGetPublishAccount) {
				Thread.sleep(100);
			}
		} catch (Exception e) {
			setException(e);
			e.printStackTrace();
			return TaskResult.FAILED;
		}

		if (publicAccount != null)
			try {
				String balanceOfPublishAccount = publicAccount.getString("direct_remain_fee");
				String payOfPublishAccount = publicAccount.getString("direct_pay_fee");
				phoneCurrMsg.setPublicBalance(balanceOfPublishAccount);
				phoneCurrMsg.setPublicCost(payOfPublishAccount);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		publishProgress(phoneCurrMsg);

		return TaskResult.OK;
	}

	protected void onProgressUpdate(Object... values) {
		if (values != null && values.length > 0) {
			UserInfoControler.getInstance().setCurrentPhoneInfo((PhoneCurInfo) values[0]);
			super.onProgressUpdate((PhoneCurInfo) values[0]);
		}
	}

	protected void onPreExecute(){
		TaskManager.getInstance().addTask(this);
		super.onPreExecute();
	}
	
	protected void onPostExecute(TaskResult result) {
		TaskManager.getInstance().deleteObserver(this);
		super.onPostExecute(result);
	}

	private void startLoadPublicAccount() {
		GetPublicAccountTask task = new GetPublicAccountTask();
		task.setListener(this);
		task.execute();
	}

	@Override
	public void onPreExecute(GenericTask task) {

	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		isOverForGetPublishAccount = true;
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		publicAccount = (JSONObject) param;
	}

	@Override
	public void onCancelled(GenericTask task) {
		isOverForGetPublishAccount = true;
	}

	class GetPublicAccountTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {

			try {
				String result = App.sServerClient.getPhoneFeeMsg(UserInfoControler.getInstance().getUserName(), UserInfoControler.getInstance().getAuthorKey());
				JSONObject jsObj = new JSONObject(result).getJSONObject("RETURN");
				publishProgress(jsObj);
			} catch (Exception e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			}

			return TaskResult.OK;
		}

	}
}
