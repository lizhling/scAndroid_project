package com.sunrise.scmbhc.task;

import java.io.IOException;
import android.content.Context;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.entity.CommonlySearchs;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;

public class GetCommonlySearchsTask extends GenericTask {
	private Context mContext;
	
	public GetCommonlySearchsTask(Context mContext) {
		this.mContext = mContext;
	}
	
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		try {
			CommonlySearchs commonlySearchs = App.sServerClient.getCommonlySearch(mContext);
			App.sCommonlySearchs.clear();
			App.sCommonlySearchs.addAll(commonlySearchs.getDatas());
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
		}catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}

}
