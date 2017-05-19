package com.sunrise.javascript.utils;

public class ServerZipAndBaseUtils { 

	public static String compressAndEncodeBase64(String content,String chartSetName){
		byte[] source=content.getBytes();
		String result=Base64.encodeBytes(source, Base64.GZIP,chartSetName);
		return result;
	}
	
	public static String decompressAndDecodeBase64(String content,String chartSetName){
		byte[] bytes=Base64.decode(content, Base64.GZIP,chartSetName);
		String result=new String(bytes);
		return result;
	}
}
