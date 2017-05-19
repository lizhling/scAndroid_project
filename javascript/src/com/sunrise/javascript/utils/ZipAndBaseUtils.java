package com.sunrise.javascript.utils;
import com.sunrise.javascript.utils.aes.DesCrypUtil;

import android.os.Handler;
import android.util.Log;

public class ZipAndBaseUtils{ 
	private static final String TAG="ZipAndBaseUtils";
	public static void compressAndEncodeBase64(String content,String chartSetName,String key,Handler handler){
		content = content.replace("%2C", ",");
		Log.i(TAG, "加密前："+content);
		byte[] source=content.getBytes();
		String result=Base64.encodeBytes(source, Base64.GZIP,chartSetName);
		Log.i(TAG, "加密后："+result);
		CommonUtils.sendResult(result,key,handler);
	}
	
	public static String decompressAndDecodeBase64(String content,String chartSetName,String key,Handler handler){
		Log.i(TAG, "解密前："+content);
		byte[] bytes=Base64.decode(content, Base64.GZIP,chartSetName);
		String result="error";
		if(bytes!=null)
		 result=new String(bytes);
		result=result.replace("\\", "\\\\").replace("&", "\\&").replace("'", "\\'");
		Log.i(TAG, "解密后："+result);
		CommonUtils.sendResult(result,key, handler);
		return result;
	}
	public static void compressAndEncodeDes(String content,String key,Handler handler){
		content = content.replace("%2C", ",");
		Log.i(TAG, "加密前："+content);
		String result = "error: 加密失败";
		try {
			result = DesCrypUtil.DESEncrypt(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(TAG, "加密后："+result);
		CommonUtils.sendResult(result,key,handler);
	}
	
	public static String decompressAndDecodeDes(String content,String key,Handler handler){
		Log.i(TAG, "解密前："+content);
		String result = "error";
		try {
			result = DesCrypUtil.DESDecrypt(content.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		result=result.replace("\\", "\\\\").replace("&", "\\&").replace("'", "\\'");
		Log.i(TAG, "解密后："+result);
		CommonUtils.sendResult(result,key, handler);
		return result;
	}
}
