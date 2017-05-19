package com.sunrise.javascript.utils;
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
}
