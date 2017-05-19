package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;

public class LoadUserTrafficTask implements ExtraKeyConstant {

	public final LoadUserTrafficTask execute() {

		startLoadPhoneFreeQuery();
		startPhoneCurrentMsg();
		startAdditionalTrafficQuery();
		startLoadCredits();
		startLoadBaseUserInfos();

		return this;
	}

	/**
	 * 启动加载用户余量查询
	 */
	private void startLoadPhoneFreeQuery() {
		new PhoneFreeQueryTask().execute(UserInfoControler.getInstance().getUserName(), null);
	}

	private void startLoadBaseUserInfos() {
		new LoadUserBaseInfoTask().execute(UserInfoControler.getInstance().getUserName(), null);

	}

	private void startLoadCredits() {
		new LoadMCreditsTask().execute();
	}

	private void startAdditionalTrafficQuery() {
		GetOpenedUpBusinnessListTask task = new GetOpenedUpBusinnessListTask();
		task.execute();

	}

	private void startPhoneCurrentMsg() {
		new PhoneCurrMsgTask().execute(UserInfoControler.getInstance().getUserName(), null);
	}


}
