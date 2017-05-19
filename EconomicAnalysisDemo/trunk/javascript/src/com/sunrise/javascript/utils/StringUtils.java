/**
 *@(#)StringUtils.java        0.01 2012/01/12
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript.utils;

/**
 * 处理字符串工具
 * 
 * @version 0.01 January 12 2012
 * @author LIU WEI
 */
public class StringUtils {
	
	/**
	 * 检查字符串是否是数字字符串
	 * @param str 数字字符串
	 * @return 是数字返回true，反之返回false
	 */
	public static boolean isNumber(String str){
		return str.matches("[0-9]*");
	} 
	
}
