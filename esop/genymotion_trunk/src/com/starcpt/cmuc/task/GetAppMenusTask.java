package com.starcpt.cmuc.task;

import java.io.IOException;
import org.json.JSONException;
import android.content.Context;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.AppMenu;
import com.starcpt.cmuc.model.AppMenus;
import com.starcpt.cmuc.model.Item;
import com.starcpt.cmuc.model.bean.AppMenuBean;
import com.starcpt.cmuc.model.bean.AppMenusBean;
import com.starcpt.cmuc.utils.FileUtils;
import com.starcpt.cmuc.utils.JsonUtils;

public class GetAppMenusTask extends GenericTask {
private Context mContext;
//private static final String TAG="GetAppMenusTask";
private CmucApplication cmucApplication;
private static final String PAGE_NUMBER="50";
private static final String START_INDEX="1";
private String mMenuVersionKey;
private boolean mIsChildUpdated;
private long mChildVersion;
	public GetAppMenusTask(Context mContext) {
	super();
	this.mContext = mContext;
	cmucApplication=(CmucApplication) mContext.getApplicationContext();
}
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		TaskParams param = params[0];
		String authentication=cmucApplication.getSettingsPreferences().getAuthentication();
		String menuParentId = param.getString(CmucApplication.MENU_ID);
		String appTag=param.getString(CmucApplication.APP_TAG_EXTRAL);
		mChildVersion=(Long) param.get(CmucApplication.CHILD_VERSION_EXTRAL);
		mIsChildUpdated=isChildUpdated(menuParentId, appTag);
		String pageId=appTag+menuParentId;
		return getAppMensFromNet(authentication, menuParentId, appTag, pageId);
	}

	private boolean isChildUpdated(String menuID,String appTag){
		CmucApplication cmucApplication=(CmucApplication) CmucApplication.sContext;
		Preferences preferences=cmucApplication.getSettingsPreferences();
		mMenuVersionKey=preferences.getUserName()+appTag+menuID;
		if(Integer.valueOf(menuID)==CmucApplication.IDENTITY_VERIFICATION_MENU_ID){
			mMenuVersionKey=menuID;
		}
	    long oldChildVersion=preferences.getMenuVersion(mMenuVersionKey);
	    return oldChildVersion<mChildVersion;
    }
	
	private TaskResult getAppMensFromNet(String authentication,
			String menuParentId, String appTag,String pageId) {
		String userJsonDir=cmucApplication.getUserJsonDir();
		int sw=cmucApplication.getScreenWidth();
		int sh=cmucApplication.getScreenHeight();
		try {
		    AppMenus appMenus = null;
			boolean fileIsExist=FileUtils.fileIsExist(userJsonDir, pageId+".json");
			if(!fileIsExist){
				appMenus=CmucApplication.sServerClient.getMenuList(mContext, authentication, appTag, menuParentId, sw+"", sh+"", START_INDEX, PAGE_NUMBER);
			}else{
				if(mIsChildUpdated){
					FileUtils.deleteFileByRelativePath(userJsonDir, pageId+".json");
					appMenus=CmucApplication.sServerClient.getMenuList(mContext, authentication, appTag, menuParentId, sw+"", sh+"", START_INDEX, PAGE_NUMBER);
				}
				else{
					String filePath=FileUtils.getAbsPath(userJsonDir, pageId+".json");
					AppMenusBean appMenusBean=JsonUtils.readJsonObjectFormFile(filePath,  AppMenusBean.class);
					appMenus=new AppMenus(appMenusBean,AppMenus.ORDER_SORT);
				}
			}
			//for test
			if(pageId.equals("YXZS1")&&CmucApplication.sTestData){
				AppMenuBean testAppmenuBean=new AppMenuBean();
				testAppmenuBean.setMenuType(Item.APP_TYPE);
				testAppmenuBean.setMenuId(453785);
				testAppmenuBean.setName("4G助销系统");
				testAppmenuBean.setContent("http://gdown.baidu.com/data/wisegame/474b9c9cdbfc16ec/weibo_735.apk");
				testAppmenuBean.setChildVersion(20131224);
				testAppmenuBean.setIcon("http://61.235.80.178:8080/scUnifiedAppManagePlatform/resources/icons/480X800/APP_MENU_ICON_CLICK_APP_MENU_ICON_CLICK_monternetCompanyQuery10.png");
				AppMenu appMenuTest=new AppMenu(testAppmenuBean);
				appMenus.getDatas().add(appMenuTest);
				
				AppMenuBean testAppmenuBean1=new AppMenuBean();
				testAppmenuBean1.setMenuType(Item.APP_TYPE);
				testAppmenuBean1.setMenuId(4537833);
				testAppmenuBean1.setName("快乐孕期");
				testAppmenuBean1.setContent("http://gdown.baidu.com/data/wisegame/c944ab93c431ef7d/kuaileyunqi_101.apk");
				testAppmenuBean1.setChildVersion(20131222);
				testAppmenuBean1.setIcon("http://61.235.80.178:8080/scUnifiedAppManagePlatform/resources/icons/480X800/APP_MENU_ICON_CLICK_APP_MENU_ICON_CLICK_monternetCompanyQuery10.png");
				AppMenu appMenuTest1=new AppMenu(testAppmenuBean1);
				appMenus.getDatas().add(appMenuTest1);
			}			
			cmucApplication.insertDataPakage(pageId, appMenus);
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
		} catch (JSONException e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		} catch (IOException e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		if(mIsChildUpdated)
		updateChildVersion();
		return TaskResult.OK;
	}
	
	private void updateChildVersion() {
		CmucApplication cmucApplication=(CmucApplication) CmucApplication.sContext;
		Preferences preferences=cmucApplication.getSettingsPreferences();
		preferences.saveMenuVersion(mMenuVersionKey, mChildVersion);
	}

}
