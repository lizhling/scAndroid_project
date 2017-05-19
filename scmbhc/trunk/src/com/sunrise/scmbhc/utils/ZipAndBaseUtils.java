package com.sunrise.scmbhc.utils;

public class ZipAndBaseUtils {
	// private static final String TAG="ZipAndBaseUtils";
	public static String compressAndEncodeBase64(String content, String chartSetName) {
		// LogUtlis.showLogI(TAG, "加密前："+content);
		// byte[] source=content.getBytes();
		// String result=Base64.encodeBytes(source, Base64.GZIP,chartSetName);
		String result = "error: 加密失败";
		try {
			result = DesCrypUtil.DESEncrypt(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// LogUtlis.showLogI(TAG, "加密后："+result);
		return result;
	}

	/**
	 * 使用动态key加密
	 * 
	 * @param content
	 * @param key
	 * @return
	 */
	public static String compressAndEncodeDesCryp(String content, String key) {
		String result = "error: 加密失败";
		try {
			result = DesCrypUtil.DESEncrypt(content, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String decompressAndDecodeBase64(String content, String chartSetName) {
		// LogUtlis.showLogI(TAG, "解密前："+content);
		// byte[] bytes=Base64.decode(content, Base64.GZIP,chartSetName);
		String result = "error";
		/*
		 * if(bytes!=null) { result=new String(bytes); }
		 */
		try {
			result = DesCrypUtil.DESDecrypt(content.trim());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// LogUtlis.showLogI(TAG, "解密后："+result);
		return result;
	}
}
