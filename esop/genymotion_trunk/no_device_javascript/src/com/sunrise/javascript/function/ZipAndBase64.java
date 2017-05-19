/**
 *@(#)ZipAndBase64.java        0.01 2012/09/21
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */
package com.sunrise.javascript.function;
import android.text.TextUtils;

import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.utils.ZipAndBaseUtils;

/**
 * 字符串Gzip压缩，解压；字符串base64 解密，加密
 * @version 0.01 September 21 2012
 * @author LIU WEI
 */


public class ZipAndBase64 {
	private JavascriptHandler mJavascriptHandler;
	
	public ZipAndBase64(JavascriptHandler mJavascriptHandler) {
		super();
		this.mJavascriptHandler = mJavascriptHandler;
	}
	
	/**
	 * 对字符串进行GZIP压缩和Base64加密
	 * @param source 需要压缩和加密输入内容
	 * @param chartSetName 字符串编码
	 * @throws IllegalArgumentException source 加密内容为空
	 */
	public void compressAndEncodeBase64(final String source,final String chartSetName,final String key){
		if(TextUtils.isEmpty(source))
			throw new IllegalArgumentException("content 加密内容为空");		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ZipAndBaseUtils.compressAndEncodeBase64(source, chartSetName,key,mJavascriptHandler);
			}
		}).start();
	}
	
	/**
	 * 对字符串进行GZIP解压和Base64解密
	 * @param source 需要解压和解密的字符串
	 * @param chartSetName 字符串编码
	 * @throws IllegalArgumentException source 解密内容为空
	 */
	public void decompressAndDecodeBase64(final String source,
			final String chartSetName, final String key) {
		if (TextUtils.isEmpty(source)){
			throw new IllegalArgumentException("content 解密内容为空");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				ZipAndBaseUtils.decompressAndDecodeBase64(source, chartSetName,
						key, mJavascriptHandler);
			}
		}).start();
	}
}
