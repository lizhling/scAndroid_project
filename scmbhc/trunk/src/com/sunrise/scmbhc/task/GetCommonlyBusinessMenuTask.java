package com.sunrise.scmbhc.task;

import java.io.IOException;
import android.content.Context;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.entity.CommonlyUsedBusinessMenus;
import com.sunrise.scmbhc.entity.UpdateInfo;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;

public class GetCommonlyBusinessMenuTask extends GenericTask {
	private Context mContext;
	private UpdateInfo mUpdateInfo;
	
	public GetCommonlyBusinessMenuTask(Context mContext,
			UpdateInfo mUpdateInfo) {
		super();
		this.mContext = mContext;
		this.mUpdateInfo = mUpdateInfo;
	}



	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String url = mUpdateInfo.getDownloadUrl();
		try {
			CommonlyUsedBusinessMenus commonlyUsedBusinessMenus = App.sServerClient.getCommonlyUsedBusinessMenus(mContext, url);
			App.sSettingsPreferences.putResVersion(String.valueOf(UpdateInfo.TYPE_COMMONLY_BUSINESS),mUpdateInfo.getNewVersionCode());
			App.sCommonlyUsedBusiness.clear();
			App.sCommonlyUsedBusiness.addAll(commonlyUsedBusinessMenus.getDatas());
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (ServerInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (IOException e){
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
