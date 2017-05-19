/**
 *@(#)CallLog.java        0.01 2012/01/13
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

package com.sunrise.javascript.function;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.sunrise.javascript.utils.CallLogUtils;
import com.sunrise.javascript.utils.StringUtils;

/**
 * 处理通话记录，实例被绑定到javascript上
 * 
 * @version 0.01 13 January 2013
 * @author LIU WEI 
 */
public class CallLog {	
	private Context mContext;
	private Handler mHandler;
	
	public CallLog(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
	}



	/**
	 * 得到指定类型通话记录的个数
	 * @param callLogType INCOMING_TYPE=incoming,OUTGOING_TYP=outing MISSED_TYPE=missing通话记录类型 
	 * @throws IllegalArgumentException callLogType 为空，通话记录类型错误
	 */
	public void getCallLogCount(final String callLogType){
		if(TextUtils.isEmpty(callLogType)){
			throw new IllegalArgumentException("callLogType 为空");	
		}
		if(!(callLogType.equalsIgnoreCase(CallLogUtils.INCOMING_TYPE)
			||callLogType.equalsIgnoreCase(CallLogUtils.OUTGOING_TYPE)
			||callLogType.equalsIgnoreCase(CallLogUtils.MISSED_TYPE))){
			throw new IllegalArgumentException("callLogType 通话记录类型错误");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CallLogUtils.getCallLogCount(mContext.getContentResolver(),mHandler,callLogType);
			}
		}).start();
	}
	
	/**
	 * 
	 * 根据电话号码获取特定类型的最后一次通话记录
	 * @param callLogType INCOMING_TYPE=incoming,OUTGOING_TYP=outing 通话记录类型 
	 * @param phoneNumber 电话号码
	 * @throws IllegalArgumentException callLogType 为空，通话记录类型错误
	 * @throws IllegalArgumentException phoneNumber 为空，不是数字
	 */
	public void getCallLog(final String callLogType,final String phoneNumber){
		if(TextUtils.isEmpty(callLogType)){
			throw new IllegalArgumentException("callLogType 为空");	
		}
		if(!(callLogType.equalsIgnoreCase(CallLogUtils.INCOMING_TYPE)
			||callLogType.equalsIgnoreCase(CallLogUtils.OUTGOING_TYPE)
			||callLogType.equalsIgnoreCase(CallLogUtils.MISSED_TYPE))){
			throw new IllegalArgumentException("callLogType 通话记录类型错误");
		}
		if(TextUtils.isEmpty(phoneNumber)){
			throw new IllegalArgumentException("phoneNumber 为空");	
		}
		if(!StringUtils.isNumber(phoneNumber)){
			throw new IllegalArgumentException("phoneNumber 不为数字");	
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CallLogUtils.getCallLog(mContext.getContentResolver(),mHandler,callLogType,phoneNumber);
			}
		}).start();
	}
}
