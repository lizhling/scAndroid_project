/**
 *@(#)Phone.java        0.01 2012/01/13
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

package com.sunrise.javascript.function;

import com.sunrise.javascript.utils.PhoneUtils;
import com.sunrise.javascript.utils.StringUtils;

import android.content.Context;
import android.text.TextUtils;

/**
 * 处理语音电话,实例被绑定到javascript上。
 * @version 0.01 January 13 2012
 * @author LIU WEI
 */

public class Phone {
	private Context mContext;

	public Phone(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	/**
	 * 拨打语音电话
	 * @param phoneNumber 电话号码
	 * @throws IllegalArgumentException phoneNumber为空，或不为数字
	 */
	public void voiceCall(final String phoneNumber){
		if(TextUtils.isEmpty(phoneNumber)){
			throw new IllegalArgumentException("phoneNumber 为空");
		}
		if(!StringUtils.isNumber(phoneNumber)){
			throw new IllegalArgumentException("phoneNumber 不是数字");
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				PhoneUtils.callVoice(mContext, phoneNumber);
			}
		}).start();
	}
}
