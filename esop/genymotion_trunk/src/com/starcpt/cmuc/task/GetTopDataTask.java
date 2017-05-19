package com.starcpt.cmuc.task;
import android.content.Context;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.db.CmucDbManager;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.Applications;
import com.starcpt.cmuc.model.Item;
import com.starcpt.cmuc.model.bean.SubAccountBeen;

public class GetTopDataTask extends GenericTask {
/*	private static final String PAGE_NUMBER="50";
	private static final String START_INDEX="1";*/
	private Context mContext;
	private CmucApplication cmucApplication;
	public GetTopDataTask(Context mContext) {
		super();
		this.mContext = mContext;
		cmucApplication=(CmucApplication) mContext.getApplicationContext();
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		Preferences preferences=cmucApplication.getSettingsPreferences();
		String authentication = preferences.getAuthentication();
		String account =preferences.getUserName();
		Applications applications;
		try {
			    publishProgress(mContext.getString(R.string.getting_data));
				applications = CmucApplication.sServerClient
						.getApplicationList(mContext, authentication,
								cmucApplication.getScreenWidth() + "",
								cmucApplication.getScreenHeight() + "", "1", "15",
								cmucApplication.getAppTag());
				cmucApplication.insertDataPakage(CmucApplication.APP_MENU_FIRST_PAGE_ID, applications);
				
				for(Item application:applications.getDatas()){
					String appTag=application.getAppTag();
					SubAccountBeen subAccountBeen=CmucApplication.sServerClient.getSubAccount(account,appTag);
					cmucApplication.getSubAccountMap().put(appTag, subAccountBeen);
				}
				
			/*	if(cmucApplication.isSingleApp()){
					ArrayList<Item> datas=applications.getDatas();
					if(datas!=null&&datas.size()>=1){
						getAppMenus(authentication, datas);					
					}else{
						return TaskResult.FAILED;
					}
				}*/
				
				getAppMenusFromDatabase(mContext);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}
	
	private TaskResult getAppMenusFromDatabase(Context context) {
		CmucDbManager cmucDbManager=CmucDbManager.getInstance(context.getContentResolver());
		String userName=cmucApplication.getSettingsPreferences().getUserName();
		cmucApplication.setCollectionAppmenus(cmucDbManager.queryCollectionBusiness(userName,false));
		return TaskResult.OK;
	}
	
}
