package com.starcpt.cmuc.utils;

import com.sunrise.javascript.utils.aes.AES;

public class ZipUtils {

	public static String encodeBase64(String content, String chartSetName, int option) {
		byte[] source = content.getBytes();
//		 String result=Base64.encodeBytes(source, option,chartSetName);
		String result = new AES().encrypt(source);
		return result;
	}

	public static String decodeBase64(String content, String chartSetName, int option) {
//		 byte[] bytes=Base64.decode(content, option,chartSetName);
//		 String result;
//		 try {
//		 result=new String(bytes, chartSetName);
//		 } catch (UnsupportedEncodingException e) {
//		 e.printStackTrace();
//		 result=new String(bytes);
//		 }
//		 return result;
		return new AES().decrypt(content);
	}
}
