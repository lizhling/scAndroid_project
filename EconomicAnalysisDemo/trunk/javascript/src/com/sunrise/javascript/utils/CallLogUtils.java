/**
 *@(#)CallLogUtils.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import com.sunrise.javascript.JavascriptHandler;

/**
 * 处理通话记录工具
 * 
 * @version 0.01 13 January 2012
 * @author LIU WEI 
 */
public class CallLogUtils {
	public final static String INCOMING_TYPE="incoming";
	public final static String OUTGOING_TYPE="outing";
	public final static String MISSED_TYPE="missing";
	private static String[] mCallLog=new String[]{
			CallLog.Calls._ID,
			CallLog.Calls.NUMBER,
			CallLog.Calls.DATE,
			CallLog.Calls.DURATION,
			CallLog.Calls.CACHED_NAME,
			CallLog.Calls.CACHED_NUMBER_TYPE,
			CallLog.Calls.CACHED_NUMBER_LABEL
		};
    /**
	 * 获取特定类型通话记录的个数
	 * @param callType 通话记录的种类 CallLog.Calls.OUTGOING_TYPE，
	 * CallLog.Calls.INCOMING_TYPE
	 */
	public static void getCallLogCount(ContentResolver resolver,Handler handler,String callLogType){
		 int callType=-1;
			if(callLogType.equalsIgnoreCase(INCOMING_TYPE))
				callType=CallLog.Calls.INCOMING_TYPE;
			else if(callLogType.equalsIgnoreCase(MISSED_TYPE))
				callType=CallLog.Calls.MISSED_TYPE;
			else
				callType=CallLog.Calls.OUTGOING_TYPE;
		int count=0;
		String where="type="+callType;
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{"_id"}, where,  null,  CallLog.Calls.DEFAULT_SORT_ORDER);
		if(cursor!=null){
			count=cursor.getCount();
			cursor.close();
		}	
		String[] params={callLogType,""+count};
		Message msg=handler.obtainMessage(JavascriptHandler.JAVASCRIPT_CALL_LOG_COUNT,params);
		handler.sendMessage(msg);
	}
	
	/**
	 * 根据电话号码获取特定类型的最后一次通话记录
	 * 
	 * @param callLogType INCOMING_TYPE=incoming,OUTGOING_TYP=outing 通话记录类型 
	 * @param phoneNumber 电话号码
	 * @throws IllegalArgumentException callLogType 为空，通话记录类型错误
	 * @throws IllegalArgumentException phoneNumber 为空，不是数字
	 */
	public static void getCallLog(ContentResolver resolver,Handler handler,final String callLogType,final String phoneNumber){
		int callType=-1;
		if(callLogType.equalsIgnoreCase(INCOMING_TYPE))
			callType=CallLog.Calls.INCOMING_TYPE;
		else if(callLogType.equalsIgnoreCase(MISSED_TYPE))
			callType=CallLog.Calls.MISSED_TYPE;
		else
			callType=CallLog.Calls.OUTGOING_TYPE;
		String where="type="+callType+" and "+"number="+phoneNumber;
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI,
				mCallLog,
				where,  null,  "_id desc limit 1");
		com.sunrise.javascript.mode.CallLog callLog=new com.sunrise.javascript.mode.CallLog();
		if(cursor.moveToFirst()){
			do{
				callLog.setId(cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID)));
				callLog.setNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
				callLog.setDate(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DATE)));
				callLog.setDuration(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION)));
				callLog.setCachedName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
				callLog.setCachedNumberType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE)));
				callLog.setCachedNumberLabel(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL)));
			}
			while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		String[] params={callLogType,callLog.toString()};
		Message msg=handler.obtainMessage(JavascriptHandler.JAVASCRIPT_LAST_CALL_LOG,params);
		handler.sendMessage(msg);		
	}
}
