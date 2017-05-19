package com.starcpt.cmuc.ui.skin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.utils.ColorUtils;
import com.starcpt.cmuc.utils.FileUtils;
import com.starcpt.cmuc.utils.NinePatchTool;


/**
 * @author LUOYUN
 */
public class SkinManager {
	
	//private static String TAG = "SkinManager";
	//皮肤改变广播
	public static final String SKIN_CHANGED_RECEIVER = "com.starcpt.cmuc.SKIN_CHANGED";
	//皮肤下载完成广播
	public static final String SKIN_DOWNLOAD_COMPLETED_RECEIVER = "com.starcpt.cmuc.SKIN_DOWNLOAD_COMPLETED";
	//皮肤Bean key
	public static final String SKIN_BEAN = "skin_bean";
	//下载状态key
	public static final String SKIN_DOWNLOAD_STATE = "download_state";
	
	//默认皮肤
	public static final String DEFAULT_SKIN = "默认皮肤";
	//当前皮肤key
	private static final String CURRENT_SKIN = "CURRENT_SKIN";
	//当前皮肤
	private static String currentSkin;

	/**
	 * 皮肤包的路径
	 */
	private final static String sSkinAbsPath =FileUtils.getAbsPath(CmucApplication.APP_SKIN_INSTALLED_DIR,null);
    
    /**
     * 保存已读取的图片到缓存，加快重复读取图片的速度
     */
    private static Map<String, Drawable> drawableCache = new HashMap<String, Drawable>();
	
    /**
     * 获取当前皮肤
     * @param context
     */
    public static void initCurSkin(Context context){
    	if (currentSkin==null) {
    		currentSkin = getCurrentSkin(context);
		}
    	clearCache();
    	initSkinColor(context);
    	initSkinVisibilitys(context);
    }
	
    /**
     * 获取默认Drawable
     * @param context
     * @param resId
     * @return
     */
	private static Drawable getDrawable(Context context,int resId){
		return context.getResources().getDrawable(resId);
	}
	
	/**
	 * 根据皮肤包获取颜色资源
	 * @param context
	 * @param colorId
	 * @return
	 */
	public static int getSkinColor(Context context,int colorId){
		HashMap<Integer, Integer> colorMap =  SkinResource.sColorValueMap;
		Integer colorObj = colorMap.get(colorId);
		if (colorObj!=null) {
			return colorObj;
		}
		return context.getResources().getColor(colorId);
	}
	
	/**
	 * 
	 * @param downloadUrl
	 * @return
	 */
	public static String getSkinName(String downloadUrl){
		return downloadUrl.substring(downloadUrl.lastIndexOf("/")+1, downloadUrl.length());
	}

	static String getSkinFolderName(String downloadUrl){
		return downloadUrl.substring(downloadUrl.lastIndexOf("/")+1, downloadUrl.length()-4);
	}
	
	/**
	 * 初始化颜色值
	 * @param context
	 */
	private static void initSkinColor(Context context){
			try {
				String skin = getCurrentSkin(context);
				String colorPath = null;
				SkinResource.sColorValueMap.clear();
				if (!SkinManager.DEFAULT_SKIN.equals(skin)) {
					colorPath = sSkinAbsPath+skin+File.separator+"color.xml";
					FileInputStream fileInputStream = new FileInputStream(colorPath);
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser parser = factory.newSAXParser();
					XMLReader reader = parser.getXMLReader();
					XMLSkinHandler handler = new XMLSkinHandler();
					handler.setElementName("color");
					reader.setContentHandler(handler);
					reader.parse(new InputSource(fileInputStream));
					HashMap<String, String> map = handler.getMap();
					Set<Entry<String, String>> mapSet = map.entrySet();
					HashMap<String, Integer> nameMap = SkinResource.sColorNameMap;
					for (Entry<String, String> entry : mapSet) {
						String colorName = entry.getKey();
						if(colorName.equals("login_get_auth_code_text_color")){
							System.out.print("ff");
						}
						String colorValue = entry.getValue();
						Integer integerId = nameMap.get(colorName);
						if (integerId!=null) {
							SkinResource.sColorValueMap.put(integerId, ColorUtils.getIntColor(colorValue));
						}
					}
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 * 初始化控件是否显示
	 * @param context
	 */
	private static void initSkinVisibilitys(Context context){
			try {
				String skin = getCurrentSkin(context);
				String colorPath = null;
				SkinResource.sVisibilityValueMap.clear();
				if (!SkinManager.DEFAULT_SKIN.equals(skin)) {
					colorPath = sSkinAbsPath+skin+File.separator+"visibility.xml";
					FileInputStream fileInputStream = new FileInputStream(colorPath);
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser parser = factory.newSAXParser();
					XMLReader reader = parser.getXMLReader();
					XMLSkinHandler handler = new XMLSkinHandler();
					handler.setElementName("visibility");
					reader.setContentHandler(handler);
					reader.parse(new InputSource(fileInputStream));
					HashMap<String, String> map = handler.getMap();
					Set<Entry<String, String>> mapSet = map.entrySet();
					HashMap<String, Integer> nameMap = SkinResource.sVisibilityNameMap;
					for (Entry<String, String> entry : mapSet) {
						String visibilityName = entry.getKey();
						String visibilityValue = entry.getValue();
						Integer integerId = nameMap.get(visibilityName);
						if (integerId!=null) {
							SkinResource.sVisibilityValueMap.put(integerId, Integer.valueOf(visibilityValue));
						}
					}
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 * 根据皮肤包获取控件是否显示
	 * @param context
	 * @param colorId
	 * @return
	 */
/*	private static int getSkinVisibility(Context context,int visibilityId,int defaultVisibility){
		HashMap<Integer, Integer> visibilityMap =  SkinResource.sVisibilityValueMap;
		Integer visibility = visibilityMap.get(visibilityId);
		if (visibility!=null) {
			return visibility;
		}
		return defaultVisibility;
	}*/
	
	
	/**
	 * 根据设置获取皮肤资源
	 * @param context
	 * @param fileName
	 * @param defaultDrawableId
	 * @return
	 */
	public static Drawable getSkinDrawable(Context context,int defaultDrawableId) {
		//读取保存的皮肤方案
		HashMap<Integer, String> resMap = SkinResource.sResMap;
		String skinFolder = getCurrentSkin(context);
		if (SkinManager.DEFAULT_SKIN.equals(sSkinAbsPath)) {
			return getDrawable(context, defaultDrawableId);
		}
		//先从缓存在查找图片
		String fileName = resMap.get(defaultDrawableId);
		String filePath = sSkinAbsPath+skinFolder+File.separator;
		Drawable drawable = drawableCache.get(fileName);
		if (drawable == null) {
			//从数据路径查找图片
			if (sSkinAbsPath!=null) {
				drawable = getSkinDrawableFromFile(context,filePath,fileName);
			}  
			
			if (drawable==null) {
				drawable = getDrawable(context, defaultDrawableId);
			}
			
			//保存图片到缓存
			if (drawable != null) {
				drawableCache.put(fileName, drawable);
			}
		}
		return drawable;
	}
	
	/**
	 * 根据ID值，获取bitmap
	 * @param context
	 * @param defaultDrawableId   图片id
	 * @return
	 */
	/*private static Bitmap getSkinBitmap(Context context,int defaultDrawableId){
		BitmapDrawable bitmapDrawable = (BitmapDrawable) getSkinDrawable(context, defaultDrawableId);
		return bitmapDrawable.getBitmap();
	}*/
	
	/**
	 * 获取StateListDrawable
	 * @param context
	 * @param idNormal      Normal背景图片 id
	 * @param idPressed		Pressed背景图片 id
	 * @param idFocused		Focused背景图片 id
	 * @return
	 */
	private static StateListDrawable addStateDrawable(Context context,  int idNormal, int idPressed, int idFocused) {   
	     StateListDrawable sd = new StateListDrawable();   
	     Drawable normal = idNormal == -1 ? null : getSkinDrawable(context, idNormal);  
	     Drawable pressed = idPressed == -1 ? null : getSkinDrawable(context, idPressed);   
	     Drawable focus = idFocused == -1 ? null : getSkinDrawable(context, idFocused);   
	     //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉   
	     //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了    
	     sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);   
	     sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);   
	     sd.addState(new int[]{android.R.attr.state_focused}, focus);   
	     sd.addState(new int[]{android.R.attr.state_pressed}, pressed);   
	     sd.addState(new int[]{android.R.attr.state_enabled}, normal);   
	     sd.addState(new int[]{}, normal);   
	     return sd;   
	 }
	
	/**
	 * 清空图片缓存
	 */
	private static void clearCache(){
		drawableCache.clear();
	}
	
	/**
	 * 从数据路径查找图片
	 * @param context
	 * @param fileName
	 * @param path
	 * @return
	 */
	private static Drawable getSkinDrawableFromFile(Context context,String skinPath,String fileName) {
		//转化成二进制
		Drawable drawable = null;
		File file = getFile(skinPath, fileName);
		if (file != null) {
			try {
				String filePath=file.getCanonicalPath();
				if(filePath.contains(".9.png")){
					try {
						drawable=NinePatchTool.decodeDrawableFromFile(context, filePath);
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					drawable = BitmapDrawable.createFromPath(file.getCanonicalPath());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return drawable;
	}
	
	/**
	 * 检查图片格式
	 * @param skinPath
	 * @param fileName
	 * @return  返回正确的图片文件
	 */
	private  static File getFile(String skinPath,String fileName){
		String filePath = skinPath+fileName+".png";
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		filePath = filePath.replace(".png", ".jpg");
		file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		filePath = skinPath+fileName+".9.png";
		file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		return null;
	}
	
	/**
	 * 设置当前皮肤
	 * @param context
	 * @param currentSkin
	 */
	static boolean setCurrentSkin(Context context,String curSkin) {
		if (!currentSkin.equals(curSkin)) {
			currentSkin = curSkin;
			CmucApplication cmucApplication=(CmucApplication) context.getApplicationContext();
			cmucApplication.getSettingsPreferences().putString(CURRENT_SKIN, currentSkin);
			return true;
		}
		return false;
	}
	
	/**
	 * 获取当前皮肤，如果没有设置，则返回默认皮肤
	 * @param context
	 */
	static String getCurrentSkin(Context context) {
		if (currentSkin==null) {
			CmucApplication cmucApplication=(CmucApplication) context.getApplicationContext();
			currentSkin = cmucApplication.getSettingsPreferences().getString(CURRENT_SKIN,DEFAULT_SKIN);
		}
		return currentSkin;
	}
	
	/*
	 * 看看是否要换皮肤
	 * @param context
	 * @return
	 */
	/*private static boolean isCustomizeSkin(Context context) {
		return !getCurrentSkin().equalsIgnoreCase(currentSkin);
	}*/

	/*
	 * 查找所有已安装的皮肤，返回文件夹路径
	 * @return
	 */
	/*private static File[] getInstalledSkinFolder() {
		String skinPath = Environment.getExternalStorageDirectory() + File.separator+CmucApplication.APP_SKIN_INSTALLED_DIR;
		File file = new File(skinPath);
		return file.listFiles();
	}*/
	
	/*
	 * 返回blue  StateListDrawable
	 * @param context
	 * @return
	 */
	/*private static StateListDrawable getBlueBtnDrawable(Context context){
		return addStateDrawable(context, R.drawable.blue_button_normal, R.drawable.blue_button_click, R.drawable.blue_button_click);
	}*/

	/**
	 * 返回green  StateListDrawable
	 * @param context
	 * @return
	 */
	public static StateListDrawable getGreenBtnDrawable(Context context){
		return addStateDrawable(context, R.drawable.green_button_bg_normal, R.drawable.green_button_bg_click, R.drawable.green_button_bg_click);
	}
	 
	/**
	 * 返回grey  StateListDrawable
	 * @param context
	 * @return
	 */
	public static StateListDrawable getGreyBtnDrawable(Context context){
		return addStateDrawable(context, R.drawable.grey_button_bg_normal, R.drawable.grey_button_bg_click, R.drawable.grey_button_bg_click);
	}

	
	/**
	 * 返回LightGrey  StateListDrawable
	 * @param context
	 * @return
	 */
	public static StateListDrawable getLightGreyDrawable(Context context){
		return addStateDrawable(context, R.drawable.light_grey_button_normal, R.drawable.light_grey_button_click, R.drawable.light_grey_button_click);
	}
	
	/**
	 * 设置TopBar皮肤
	 * @param activity
	 */
	private static void setTopTileSkin(Activity activity){
		activity.findViewById(R.id.top_root_view).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.tob_title_bg));
		activity.findViewById(R.id.title_view).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.title_text_bg));
		((TextView)activity.findViewById(R.id.top_title_text)).setTextColor(getSkinColor(activity, R.color.top_title_text_color));
		((Button)activity.findViewById(R.id.sync_colleciton)).setBackgroundDrawable(addStateDrawable(activity, R.drawable.light_grey_button_normal,
				R.drawable.light_grey_button_click, R.drawable.light_grey_button_click));
		((Button)activity.findViewById(R.id.top_back_btn)).setBackgroundDrawable(addStateDrawable(activity, R.drawable.tob_title_back_normal,
				R.drawable.tob_title_back_focus, R.drawable.tob_title_back_focus));
	}
	
	/**
	 * 设置皮肤
	 * @param activity   activity中控件皮肤
	 * @param view		view控件皮肤
	 * @param skinView   设置分类
	 */
	public static void setSkin(Activity activity,View view,ViewEnum skinView){
		/*switch (skinView) {
		case StartActivity:
			((ImageView)activity.findViewById(R.id.splash_app_logo)).setImageBitmap(getSkinBitmap(activity, R.drawable.splash_logo));
			((LinearLayout)activity.findViewById(R.id.start_bg)).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.splash));
			break;
		case BusinessActivity:
			setTopTileSkin(activity);
			activity.findViewById(R.id.business_boot_view).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.common_bg));
			break;
		case FeedBacksActivity:
			setTopTileSkin(activity);
			activity.findViewById(R.id.feedbacks_boot_view).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.common_bg));
			activity.findViewById(R.id.btn_refresh_feedback).setBackgroundDrawable(getGreenBtnDrawable(activity));
			activity.findViewById(R.id.btn_commit).setBackgroundDrawable(getGreenBtnDrawable(activity)); 
			break;
		case LoginActivity:
			View tobPanel=activity.findViewById(R.id.login_tob_panel);
			tobPanel.setVisibility(getSkinVisibility(activity, R.id.login_tob_panel,tobPanel.getVisibility()));
			View logoPanel=activity.findViewById(R.id.login_logo_panel);
			activity.findViewById(R.id.login_logo_panel).setVisibility(getSkinVisibility(activity, R.id.login_logo_panel,logoPanel.getVisibility()));			
			activity.findViewById(R.id.rootView).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.login_bg));
			
			Button get_auth_code = (Button) activity.findViewById(R.id.get_auth_code);
			get_auth_code.setBackgroundDrawable(getGreyBtnDrawable(activity));
			get_auth_code.setTextColor(getSkinColor(activity, R.color.login_get_auth_code_text_color));
			
			Button btn_login =(Button)activity.findViewById(R.id.btn_login);
			btn_login.setBackgroundDrawable(getGreenBtnDrawable(activity));
			btn_login.setTextColor(getSkinColor(activity, R.color.login_btn_text_color));
			break;
		case MainTabActivity:
			activity.findViewById(R.id.bottomTab).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.bottom_tab_bg));
			break;
		case MessageDetailActivity:
			setTopTileSkin(activity);
			break;
		case MessageListActivity:
			activity.findViewById(R.id.message_list_panel).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.common_bg));
			break;
		case PushMessageSettingActivity:
			activity.findViewById(R.id.two_button_dialog).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.dialog_bg));
			activity.findViewById(R.id.confirm).setBackgroundDrawable(getGreenBtnDrawable(activity));
			activity.findViewById(R.id.cancel).setBackgroundDrawable(getGreenBtnDrawable(activity));
			break;
		case SearchBusinessActivity:
			activity.findViewById(R.id.search_list_panel).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.common_bg));
			break;
		case SecureOptionlActivity:
			setTopTileSkin(activity);
			activity.findViewById(R.id.rootView).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.dialog_bg));
			activity.findViewById(R.id.btnOK).setBackgroundDrawable(getGreenBtnDrawable(activity));
			activity.findViewById(R.id.btnCancel).setBackgroundDrawable(getGreyBtnDrawable(activity));
			break;
		case SecurePasswordActivity:
			activity.findViewById(R.id.top_title).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.title_text_bg));
			activity.findViewById(R.id.rootView).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.common_bg));
			activity.findViewById(R.id.btnCancel).setBackgroundDrawable(getGreyBtnDrawable(activity));
			activity.findViewById(R.id.btnOK).setBackgroundDrawable(getGreenBtnDrawable(activity));
			break;
		case SettingActivity:
			setTopTileSkin(activity);
			activity.findViewById(R.id.EsopSettingItem).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.setting_item_bg));
			activity.findViewById(R.id.rootView).setBackgroundDrawable(getSkinDrawable(activity, R.drawable.common_bg));
			break;
		case WebViewActivity:
			setTopTileSkin(activity);
			break;
		case LockActivity:
			activity.findViewById(R.id.two_button_dialog).setBackgroundDrawable(getDrawable(activity, R.drawable.dialog_bg));
			activity.findViewById(R.id.confirm).setBackgroundDrawable(SkinManager.getGreenBtnDrawable(activity));
			break;
		case MessageDetailFragment:
			setTopTileSkin(activity);
			break;
		case MessageListFragment:
			setTopTileSkin(activity);
			break;
		case SearchBusinessListFragment:
			setTopTileSkin(activity);
			view.setBackgroundDrawable(getSkinDrawable(activity, R.drawable.common_bg));
			view.findViewById(R.id.serach_business_button).setBackgroundDrawable(addStateDrawable(activity, R.drawable.search_normal, 
			R.drawable.search_press, R.drawable.search_press));
			break;
			
		case WebViewFragment:
			activity.findViewById(R.id.re_load_web).setBackgroundDrawable(getGreenBtnDrawable(activity));
			break;
			
		default:
			break;
		}*/
	}
	
	
}
