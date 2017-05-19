package com.starcpt.cmuc.ui.skin;

import java.util.HashMap;

import com.starcpt.cmuc.R;

public class SkinResource {

	public static final HashMap<Integer, String> sResMap = new HashMap<Integer, String>();
	
	public static final HashMap<Integer, Integer> sColorValueMap = new HashMap<Integer, Integer>();
	public static final HashMap<String, Integer> sColorNameMap = new HashMap<String, Integer>();
	
	public static final HashMap<Integer, Integer> sVisibilityValueMap = new HashMap<Integer, Integer>();
	public static final HashMap<String, Integer> sVisibilityNameMap = new HashMap<String, Integer>();
	
	static{
		
		/*common*/
		sResMap.put(R.drawable.common_bg, "common_bg");
		
		//blue_button
		sResMap.put(R.drawable.blue_button_click, "blue_button_click");
		sResMap.put(R.drawable.blue_button_normal, "blue_button_normal");
		//green_button
		sResMap.put(R.drawable.green_button_bg_normal, "green_button_bg_normal");
		sResMap.put(R.drawable.green_button_bg_click, "green_button_bg_click");
		//light_grey_button
		sResMap.put(R.drawable.light_grey_button_normal, "light_grey_button_normal");
		sResMap.put(R.drawable.light_grey_button_click, "light_grey_button_click");
		//grey_button
		sResMap.put(R.drawable.grey_button_bg_normal, "grey_button_bg_normal");
		sResMap.put(R.drawable.grey_button_bg_click, "grey_button_bg_click");
		//TopTitleView
		sResMap.put(R.drawable.cmuc_start_name, "cmuc_start_name");
		sResMap.put(R.drawable.tob_title_back_focus, "tob_title_back_focus");
		sResMap.put(R.drawable.tob_title_back_normal, "tob_title_back_normal");
		sResMap.put(R.drawable.tob_title_bg, "tob_title_bg");
		sResMap.put(R.drawable.title_text_bg, "title_text_bg");
		
		//StartActivity
		sResMap.put(R.drawable.splash_logo, "splash_logo");
		sResMap.put(R.drawable.splash, "splash");
		//LoginActivity
		sResMap.put(R.drawable.login_bg, "login_bg");
		//MainTabActivity
		sResMap.put(R.drawable.bottom_tab_bg, "bottom_tab_bg");
		
		//SearchBusinessListFragment
		sResMap.put(R.drawable.search_normal, "search_normal");
		sResMap.put(R.drawable.search_press, "search_press");
		//Dialog
		sResMap.put(R.drawable.dialog_bg, "dialog_bg");
		sResMap.put(R.drawable.dialog_title_bar, "dialog_title_bar");
		
		
     /*<!-- 标题栏(TopTitle)-->
		<!-- 标题栏字体颜色 -->
		<color name="top_title_text_color">#017bc7</color>*/
		sColorNameMap.put("top_title_text_color",R.color.top_title_text_color);
		
	/*<!-- 登录界面(LoginActivity) -->
		<!-- 验证码获取按钮字体颜色 -->
    	<color name="login_get_auth_code_text_color">#B0E0E6</color>*/
		
		sColorNameMap.put("login_get_auth_code_text_color",R.color.login_get_auth_code_text_color);
		
	/*	<!-- 登录按钮字体颜色 -->
    	<color name="login_btn_text_color">#999999</color>*/		
		sColorNameMap.put("login_btn_text_color",R.color.login_btn_text_color);
		
	/*<!-- 主界面颜色(SecurePasswordActivity) -->	
		 <color name="top_text_color">#017bc7</color>*/
		sColorNameMap.put("top_text_color",R.color.top_text_color);
		
    /*设置控件显示属性	
		<!-- 登录界面(LoginActivity) -->
  		<!-- 登录界面logo显示 -->*/
		sVisibilityNameMap.put("login_logo_panel", R.id.login_logo_panel);
		
  		/*<!-- 登录界面标题显示 -->*/
		sVisibilityNameMap.put("login_tob_panel", R.id.login_tob_panel);
	}
	
}
