/**
 *@(#)PhoneUtils.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 处理语音电话工具
 * 
 * @version 0.01 13 January 2012
 * @author LIU WEI
 */
public class PhoneUtils {
	/**
	 * 拨打语音电话
	 * @param phoneNumber 电话号码
	 * @throws IllegalArgumentException phoneNumber为空，或不为数字
	 */
	public static void callVoice(Context context,String phoneNumber){
		if(TextUtils.isEmpty(phoneNumber)){
			throw new IllegalArgumentException("phoneNumber 为空");
		}
		if(!StringUtils.isNumber(phoneNumber)){
			throw new IllegalArgumentException("phoneNumber 不是数字");
		}
		String phoneuri="tel:"+phoneNumber;
		Intent in = new Intent(Intent.ACTION_CALL, Uri.parse(phoneuri));
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(in);	
	}
}
