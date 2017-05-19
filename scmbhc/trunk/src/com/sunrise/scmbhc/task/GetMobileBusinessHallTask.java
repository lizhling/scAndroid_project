package com.sunrise.scmbhc.task;

import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.entity.MobileBusinessHall;
import com.sunrise.scmbhc.entity.UpdateInfo;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;

public class GetMobileBusinessHallTask extends GenericTask {
	private Context mContext;
	private UpdateInfo mUpdateInfo;
	
	public GetMobileBusinessHallTask(Context mContext,
			UpdateInfo mUpdateInfo) {
		super();
		this.mContext = mContext;
		this.mUpdateInfo = mUpdateInfo;
	}



	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String url = mUpdateInfo.getDownloadUrl();
		try {
			ArrayList<MobileBusinessHall> halls= App.sServerClient.getBusinesHallInfo(mContext, url);
			if(halls!=null){
				if(halls.size()>0){
					App.sSettingsPreferences.putResVersion(String.valueOf(UpdateInfo.TYPE_BUSINESS_HALL),mUpdateInfo.getNewVersionCode());
					App.sMobileBusinessHalls.clear();
					App.sMobileBusinessHalls.addAll(halls);
				}
			}
		
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
