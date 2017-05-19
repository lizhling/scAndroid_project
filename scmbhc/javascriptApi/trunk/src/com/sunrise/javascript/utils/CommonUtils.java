package com.sunrise.javascript.utils;

import android.text.TextUtils;

public class CommonUtils {
	/**
	 * 将参javascript回调函数接收的参数和和它的方法名转化为字符串
	 * 
	 * @param params
	 *            参数数组
	 * @param methodName
	 *            javascript 函数名字
	 * @return 转化后的字符串
	 */
	public static String appendParamTOjavaScript(String[] params,
			String methodName) {
		if (TextUtils.isEmpty(methodName)) {
			throw new IllegalArgumentException("方法名不能为空");
		}
		String pas = "";
		String result = "";
		if (params != null) {
			int size = params.length;
			int lastIndex = size - 1;
			String temp = "", temp1;
			for (int i = 0; i < size; i++) {
				temp = params[i];
				if (temp.startsWith("{") && temp.endsWith("}")
						&& temp.contains(":")) {
					temp1 = temp;
				} else {
					temp1 = "'" + temp + "'";
				}
				pas += temp1 + (i == lastIndex ? "" : ",");

			}
		}
		result = methodName + "(" + pas + ")";
		return result;
	}
	
	/**
	 * 检查字符串是否是数字字符串
	 * @param str 数字字符串
	 * @return 是数字返回true，反之返回false
	 */
	public static boolean isNumber(String str){
		return str.matches("[0-9]*");
	}
}
