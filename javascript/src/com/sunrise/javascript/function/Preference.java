/**
 *@(#)Preference.java        0.01 2012/01/17
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

package com.sunrise.javascript.function;

import com.sunrise.javascript.JavascriptHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Message;
import android.text.TextUtils;

/**
 * 用SharedPreferences来存储数据，实例被绑定到javascript上。
 * @version 0.01 January 17 2012
 * @author HUANG CHAO FANG
 */
public class Preference {
	private Context mContext;	// 上下文
	private JavascriptHandler mHandler;
	
	/**
	 * 构造函数
	 * @param mContext 上下文
	 */
	public Preference(Context mContext,JavascriptHandler mHandler) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
	}

	/**
	 * 根据键key值，存储preference的值，到指定的文件
	 * @date 20120105
	 * @param key key的值
	 * @param preference preference的值
	 * @exception IllegalArgumentException fileName为空
	 * @exception IllegalArgumentException key为空
	 */
	public void setPreferenceForKey(String fileName,String key, String preference){
		/*对fileName和key进行空值判断，为空抛出异常*/
		if(TextUtils.isEmpty(key.trim()))
			throw new IllegalArgumentException("键值key为空");
		if(TextUtils.isEmpty(fileName.trim()))
			throw new IllegalArgumentException("文件名为空");
		// 获得SharePreferences引用对象
		SharedPreferences sharedPreferences=mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor edit=sharedPreferences.edit();
		if(preference == null)	//如果存入的值为空，则将其设置为空串
			preference = "";
		edit.putString(key, preference);	// storing value is base on Key-Value  根据键值关系保存值
		boolean result=edit.commit();
		String params[]={fileName,key,""+result};
		Message msg=mHandler.obtainMessage(JavascriptHandler.JAVASCRIPT_SET_PREFERENCE_FOR_KEY,params);
		mHandler.sendMessage(msg);
	}
	
	/**
	 * 根据文件名，取出key值对应的preference的值
	 * @param key 键key的值
	 * @exception IllegalArgumentException fileName为空
	 * @exception IllegalArgumentException key为空
	 */
	public void getPreferenceForKey(String fileName,String key){
		/* 对fileName和key进行空值判断，为空抛出异常*/
		if(TextUtils.isEmpty(key.trim()))
			throw new IllegalArgumentException("键值key为空");
		if(TextUtils.isEmpty(fileName.trim()))
			throw new IllegalArgumentException("文件名为空");
		// 获得SharePreferences引用对象
		SharedPreferences sharedPreferences=mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		// 根据key获取对应的preference值
		String value = sharedPreferences.getString(key, null); // get preference value by key value
		String params[]={fileName,key,""+value};
		Message msg=mHandler.obtainMessage(JavascriptHandler.JAVASCRIPT_GET_PREFERENCE_WITH_KEY,params);
		mHandler.sendMessage(msg);
	}
}
