package com.starcpt.cmuc.net;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.content.Context;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.AppMenu;
import com.starcpt.cmuc.model.AppMenus;
import com.starcpt.cmuc.model.Applications;
import com.starcpt.cmuc.model.Item;
import com.starcpt.cmuc.model.bean.AppMenusBean;
import com.starcpt.cmuc.model.bean.ApplicationsBean;
import com.starcpt.cmuc.model.bean.DynamicCodeBean;
import com.starcpt.cmuc.model.bean.FeedBackResponse;
import com.starcpt.cmuc.model.bean.FeedbackBean;
import com.starcpt.cmuc.model.bean.FeedbacksBean;
import com.starcpt.cmuc.model.bean.IdentityVerificationMenu;
import com.starcpt.cmuc.model.bean.LoginBean;
import com.starcpt.cmuc.model.bean.MessageFeedbackBean;
import com.starcpt.cmuc.model.bean.ReadedBean;
import com.starcpt.cmuc.model.bean.ResultBean;
import com.starcpt.cmuc.model.bean.SkinsBean;
import com.starcpt.cmuc.model.bean.SubAccountBeen;
import com.starcpt.cmuc.model.bean.UpdateInfoBean;
import com.starcpt.cmuc.net.http.HttpClient;
import com.starcpt.cmuc.utils.FileUtils;
import com.starcpt.cmuc.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;

public class ServerClient {
	private static final String TAG="ServerClient";
	private static ServerClient Instance;
	//private static boolean testBytestData=true;
	private static final String UPDATEINFOBYOSINFO_URL="scUnifiedAppManagePlatform/unifiedAppInterface/getUpdateInfoByOsInfo.action";
	private static final String GETSKINSINFO_URL="scUnifiedAppManagePlatform/unifiedAppInterface/getSkinInfo.action";
	private static final String LOGIN_URL="scUnifiedAppManagePlatform/unifiedAppInterface/login.action";
	private static final String LOGOUT_URL="scUnifiedAppManagePlatform/unifiedAppInterface/logout.action";
	private static final String GET_APPLICATIONS_URL="scUnifiedAppManagePlatform/unifiedAppInterface/getApplicationList.action";
	private static final String GET_APPMENUS_URL    ="scUnifiedAppManagePlatform/unifiedAppInterface/getMenuList.action";
	private static final String READED_URL="scUnifiedAppManagePlatform/unifiedAppInterface/readed.action";
	private static final String FEEDBACK_URL="scUnifiedAppManagePlatform/unifiedAppInterface/feedback.action";
	private static final String SENDDYNAMICCODE_URL="scUnifiedAppManagePlatform/unifiedAppInterface/sendDynamicCode.action";
	private static final String GETFEEDBACKLIST_URL="scUnifiedAppManagePlatform/unifiedAppInterface/getFeedBackList.action";
	private static final String SUBMITFEEDBACK_URL="scUnifiedAppManagePlatform/unifiedAppInterface/submitFeedback.action";
	private static final String SEARCHCOLLECTIONBUSINESSES_URL="scUnifiedAppManagePlatform/unifiedAppInterface/searchCollectionBusinesses.action";
	private static final String UPLOADCOLLECTIONBUSINESSES_URL="scUnifiedAppManagePlatform/unifiedAppInterface/uploadCollectionBusinesses.action";
	private static final String SEARCHBUSINESSOFKEYWORD_URL="scUnifiedAppManagePlatform/unifiedAppInterface/searchBusinessOfKeyWord.action";
	private static final String SEARCHBUSINESSOFKEYWORDNEW_URL="scUnifiedAppManagePlatform/unifiedAppInterface/searchBusinessOfKeyWordNew.action";
	private static final String SAVEDEVICEERRORLOG_URL="scUnifiedAppManagePlatform/unifiedAppInterface/saveDeviceErrorLog.action";
	private static final String GET_ACCOUNT_SUB_ACCOUNT_INFO_URL = "scUnifiedAppManagePlatform/businessHandlingService/getAccountSubAccountInfo.action";
	private static final String BINDING_IMEI_ACCOUNT_URL = "scUnifiedAppManagePlatform/unifiedAppInterface/bindAccountAndImei.action";
	private static final String GET_MENU_DETAIL_URL = "scUnifiedAppManagePlatform/unifiedAppInterface/getMenuDetail.action";
	//218.205.252.26:18098
	
/*	private static final String PORT=":8080";
	public static final String SERVER_IP="61.235.80.178";*/
	
	private static final String PORT=":18098";
	public static final String SERVER_IP="218.205.252.26";
	
	private static final String HTTP="http://";
	private static final String HTTP_SPLIT="/";
	private HttpClient mHttpClient;
	
	private ServerClient(){
		mHttpClient=HttpClient.getInstance();
	}
	
	public static ServerClient getInstance(){
		if(Instance == null){
			Instance = new ServerClient();
		}
		return Instance;
	}
	
	private  String getUrl(String relUrl){
		//String severIp=CmucApplication.sSettingsPreferences.getServerIp();
		String absUrl=HTTP+SERVER_IP+PORT+HTTP_SPLIT+relUrl;
		/*if(severIp!=null)
			absUrl=HTTP+severIp+PORT+HTTP_SPLIT+relUrl;*/
		return absUrl;
	}
	
	/**
	 * 获取皮肤列表
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 */
//	public ArrayList<SkinBean> getSkinList() throws HttpException, BusinessException{ 
//		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
//		String jsonStr=mHttpClient.httpRequest(getUrl(LOGIN_URL), pairs);
//		SkinBean skinBean = JsonUtils.parseJsonStrToObject(jsonStr, SkinBean.class);
//		return skinBean.getDatas();
//	}
	
	public LoginBean login(String userName, String dynamicCode) throws HttpException, BusinessException{
		LoginBean loginBean=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("userName", userName));
		pairs.add(new BasicNameValuePair("dynamicCode", dynamicCode));
		String jsonStr=mHttpClient.httpRequest(getUrl(LOGIN_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		loginBean=JsonUtils.parseJsonStrToObject(jsonStr, LoginBean.class);
		return loginBean;
	}
	
	public void logout(String authentication){
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authentication", authentication));
		try {
			mHttpClient.httpRequest(getUrl(LOGOUT_URL), pairs);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param currentVersion
	 * @return 
	 * 		the new apk url , if there has a new apk and the client need update
	 * @throws BusinessException 
	 * @throws HttpException 
	 */
	public UpdateInfoBean getUpdateInfoByOsInfo(String currentVersion, String osInfo, String checkType,String appTag) throws HttpException, BusinessException{
		UpdateInfoBean updateInfo = null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("currentVersion",currentVersion));
		pairs.add(new BasicNameValuePair("osInfo",osInfo));
		pairs.add(new BasicNameValuePair("checkType",checkType));
		pairs.add(new BasicNameValuePair("appTag", appTag));
		String jsonStr=mHttpClient.httpRequest(getUrl(UPDATEINFOBYOSINFO_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		updateInfo=JsonUtils.parseJsonStrToObject(jsonStr, UpdateInfoBean.class);						
		 return updateInfo;
	}

	/**
	 * 获取皮肤列表
	 * @param currentVersion   当前版本号
	 * @param osInfo	系统
	 * @param checkType	
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 */
	public SkinsBean getSkinList(String resolution, String operateSystem) throws HttpException, BusinessException{
		SkinsBean skinsBean = null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("resolution",resolution));
		pairs.add(new BasicNameValuePair("operateSystem",operateSystem));
		String jsonStr=mHttpClient.httpRequest(getUrl(GETSKINSINFO_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		skinsBean=JsonUtils.parseJsonStrToObject(jsonStr, SkinsBean.class);		
		return skinsBean;
		
	}

	
	public Applications getApplicationList (Context context,String authentication, String screenw, String screenh, String pageNumber, String pageSize, String appTag) throws HttpException, BusinessException{
		Applications applications=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authentication",authentication));
		pairs.add(new BasicNameValuePair("screenw",screenw));
		pairs.add(new BasicNameValuePair("screenh",screenh));
		pairs.add(new BasicNameValuePair("pageNumber",pageNumber));
		pairs.add(new BasicNameValuePair("pageSize",pageSize));
		if(appTag!=null)
		pairs.add(new BasicNameValuePair("appTag",appTag));
		String jsonStr=mHttpClient.httpRequest(getUrl(GET_APPLICATIONS_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		ApplicationsBean applicationsBean=JsonUtils.parseJsonStrToObject(jsonStr, ApplicationsBean.class);
			applications= new Applications(applicationsBean);
		return applications;
	}
	
	public AppMenus getMenuList (Context context,String authentication, String appTag, String menuParentId, String screenw, String screenh, String pageNumber, String pageSize) throws HttpException, BusinessException, IOException{
		AppMenus appMenus=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		String pageId=appTag+menuParentId+"";
		pairs.add(new BasicNameValuePair("authentication",authentication));
		pairs.add(new BasicNameValuePair("appTag",appTag));
		pairs.add(new BasicNameValuePair("menuParentId",menuParentId));
		pairs.add(new BasicNameValuePair("screenw",screenw));
		pairs.add(new BasicNameValuePair("screenh",screenh));
		pairs.add(new BasicNameValuePair("pageNumber",pageNumber));
		pairs.add(new BasicNameValuePair("pageSize",pageSize));	
		String jsonStr=mHttpClient.httpRequest(getUrl(GET_APPMENUS_URL), pairs);
		LogUtlis.d(TAG, "getMenuList jsonStr:"+jsonStr);
		CmucApplication cmucApplication=(CmucApplication) context.getApplicationContext();
		FileUtils.saveToFile(jsonStr, cmucApplication.getUserJsonDir(), pageId+".json");
		AppMenusBean appMenusBean=JsonUtils.parseJsonStrToObject(jsonStr, AppMenusBean.class);
		appMenus=new AppMenus(appMenusBean,AppMenus.ORDER_SORT);
		return appMenus;
	}
	
	public void saveDeviceErrorLog(String errorLogJson) throws HttpException, BusinessException{
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("errorLogJson",errorLogJson));
		String jsonStr=mHttpClient.httpRequest(getUrl(SAVEDEVICEERRORLOG_URL), pairs);
		LogUtlis.d(TAG, jsonStr);
	}
	
	public  ReadedBean readed (String notificationId) throws HttpException, BusinessException{
		ReadedBean readedBean=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("notificationId",notificationId));
		String jsonStr=mHttpClient.httpRequest(getUrl(READED_URL), pairs);
		readedBean=JsonUtils.parseJsonStrToObject(jsonStr, ReadedBean.class);
		return readedBean;
	}
	
	public MessageFeedbackBean feedback (String notificationId, String feedback) throws HttpException, BusinessException{
		MessageFeedbackBean feedbackBean=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("notificationId",notificationId));
		pairs.add(new BasicNameValuePair("feedback",feedback));
		String jsonStr=mHttpClient.httpRequest(getUrl(FEEDBACK_URL), pairs);
		feedbackBean=JsonUtils.parseJsonStrToObject(jsonStr, MessageFeedbackBean.class);
		return feedbackBean;
	}
	
	public DynamicCodeBean sendDynamicCode(String account, String password, String imei, String imsi) throws HttpException, BusinessException{		
		DynamicCodeBean dynamicCodeBean=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account",account));
		pairs.add(new BasicNameValuePair("password",password));
		pairs.add(new BasicNameValuePair("imei",imei));
		pairs.add(new BasicNameValuePair("imsi",imsi));
		String jsonStr=mHttpClient.httpRequest(getUrl(SENDDYNAMICCODE_URL), pairs);
		LogUtlis.d(TAG, "jsonStr"+jsonStr);
		dynamicCodeBean=JsonUtils.parseJsonStrToObject(jsonStr, DynamicCodeBean.class);
		return dynamicCodeBean;	
	}
	
	public ArrayList<FeedbackBean> getFeedBackList(String authentication, String pageNumber, String pageSize) throws HttpException, BusinessException{
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authentication",authentication));
		pairs.add(new BasicNameValuePair("pageNumber",pageNumber));
		pairs.add(new BasicNameValuePair("pageSize",pageSize));
		String jsonStr=mHttpClient.httpRequest(getUrl(GETFEEDBACKLIST_URL), pairs);
		LogUtlis.d(TAG, "jsonStr"+jsonStr);
		FeedbacksBean feedbacksBean=JsonUtils.parseJsonStrToObject(jsonStr, FeedbacksBean.class);
		return feedbacksBean.getDatas();
	}
	
	public FeedbackBean submitFeedback(String account,String mobile,String feedback) throws HttpException, BusinessException{
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account",account));
		pairs.add(new BasicNameValuePair("mobile",mobile));
		pairs.add(new BasicNameValuePair("feedback",feedback));
		String jsonStr=mHttpClient.httpRequest(getUrl(SUBMITFEEDBACK_URL), pairs);
		LogUtlis.d(TAG, "jsonStr"+jsonStr);
		FeedBackResponse feedBackResponse=JsonUtils.parseJsonStrToObject(jsonStr, FeedBackResponse.class);
		FeedbackBean feedbackBean=new FeedbackBean();
		feedbackBean.setFeedback(feedback);
		feedbackBean.setStatus(FeedbackBean.UNREPLY);
		feedbackBean.setCreateTime(feedBackResponse.getCreateTime());
		return feedbackBean;
	}
	
	public AppMenus searchCollectionBusinesses (String authentication,String pageNumber, String pageSize) throws HttpException, BusinessException{
		AppMenus appMenus=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authentication",authentication));
		pairs.add(new BasicNameValuePair("pageNumber",pageNumber));
		pairs.add(new BasicNameValuePair("pageSize",pageSize));
		String jsonStr=mHttpClient.httpRequest(getUrl(SEARCHCOLLECTIONBUSINESSES_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		AppMenusBean appMenusBean=JsonUtils.parseJsonStrToObject(jsonStr, AppMenusBean.class);
		appMenus=new AppMenus(appMenusBean,AppMenus.TIME_SORT);
		return appMenus;
	}
	
	public void uploadCollectionBusinesses(String authentication,String listJson) throws HttpException, BusinessException{
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authentication",authentication));
		pairs.add(new BasicNameValuePair("listJson",listJson));
		String jsonStr=mHttpClient.httpRequest(getUrl(UPLOADCOLLECTIONBUSINESSES_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
	}
	
	public ArrayList<Item> searchBusinessOfKeyWord (String authentication, String businessSearchWord,String appTag) throws HttpException, BusinessException{
		AppMenus appMenus=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authentication",authentication));
		pairs.add(new BasicNameValuePair("businessSearchWord",businessSearchWord));
		pairs.add(new BasicNameValuePair("appTag", appTag));
		String jsonStr=mHttpClient.httpRequest(getUrl(SEARCHBUSINESSOFKEYWORD_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		AppMenusBean appMenusBean=JsonUtils.parseJsonStrToObject(jsonStr, AppMenusBean.class);
		appMenus=new AppMenus(appMenusBean,AppMenus.SEARCH_SORT);
		return appMenus.getDatas();
	}
	
	public ArrayList<Item> searchBusinessOfKeyWordNew(String authentication, String businessSearchWord,String appTag) throws HttpException, BusinessException{
		AppMenus appMenus=null;
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authentication",authentication));
		pairs.add(new BasicNameValuePair("businessSearchWord",businessSearchWord));
		pairs.add(new BasicNameValuePair("appTag", appTag));
		String jsonStr=mHttpClient.httpRequest(getUrl(SEARCHBUSINESSOFKEYWORDNEW_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		AppMenusBean appMenusBean=JsonUtils.parseJsonStrToObject(jsonStr, AppMenusBean.class);
		appMenus=new AppMenus(appMenusBean,AppMenus.SEARCH_SORT);
		return appMenus.getDatas();
	}
	

	public SubAccountBeen getSubAccount(String account,String appTag) throws HttpException, BusinessException{
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account",account));
		pairs.add(new BasicNameValuePair("appTag",appTag));
		String jsonStr=mHttpClient.httpRequest(getUrl(GET_ACCOUNT_SUB_ACCOUNT_INFO_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		SubAccountBeen subAccountBeen=JsonUtils.parseJsonStrToObject(jsonStr, SubAccountBeen.class);
		return subAccountBeen;
	}
	
	public ResultBean bindingIMEI(String imei,String account) throws HttpException, BusinessException{
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("imei",imei));
		pairs.add(new BasicNameValuePair("account4A",account.replaceAll(" ", "")));
		String jsonStr=mHttpClient.httpRequest(getUrl(BINDING_IMEI_ACCOUNT_URL), pairs);
		ResultBean result=JsonUtils.parseJsonStrToObject(jsonStr, ResultBean.class);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		return result;
	}
	
	public AppMenu getMenuDetail(String appTag, String menuId, String screenw, String screenh) throws HttpException, BusinessException{
		List<NameValuePair>  pairs=new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("appTag",appTag));
		pairs.add(new BasicNameValuePair("menuId",menuId));
		pairs.add(new BasicNameValuePair("screenw",screenw));
		pairs.add(new BasicNameValuePair("screenh",screenh));
		String jsonStr=mHttpClient.httpRequest(getUrl(GET_MENU_DETAIL_URL), pairs);
		LogUtlis.d(TAG, "jsonStr:"+jsonStr);
		IdentityVerificationMenu appMenuBean=JsonUtils.parseJsonStrToObject(jsonStr, IdentityVerificationMenu.class);
		AppMenu appMenu=new AppMenu(appMenuBean.getAppMenuBean());
		return appMenu;
	}
}
