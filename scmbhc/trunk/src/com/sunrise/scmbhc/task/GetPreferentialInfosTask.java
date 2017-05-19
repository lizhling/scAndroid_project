package com.sunrise.scmbhc.task;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.entity.PreferentialInfo;
import com.sunrise.scmbhc.entity.PreferentialInfos;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;

public class GetPreferentialInfosTask extends GenericTask {
	private Context mContext;

	public GetPreferentialInfosTask(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		try {
			PreferentialInfos preferentialInfos = App.sServerClient
					.getPreferentialInfos(mContext);
		
			ArrayList<PreferentialInfo> datas=preferentialInfos.getDatas();
			if(datas!=null){
				App.sScrollPreferentialInfos.clear();
				App.sPreferentialListInfos.clear();
				for(PreferentialInfo preferentialInfo:datas){
					if(preferentialInfo.getType()==PreferentialInfo.SCROLL_TYPE){
						App.sScrollPreferentialInfos.add(preferentialInfo);
					}else{
						App.sPreferentialListInfos.add(preferentialInfo);
					}
					App.sAllPreferentialInfos.add(preferentialInfo);
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
