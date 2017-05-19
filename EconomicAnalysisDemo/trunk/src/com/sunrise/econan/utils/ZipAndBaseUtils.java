package com.sunrise.econan.utils;

import com.sunrise.javascript.utils.Base64;

import android.util.Log;

public class ZipAndBaseUtils {
	private static final String TAG = "ZipAndBaseUtils";

	public static String encodeBase64(String content) {
		Log.i(TAG, "加密前：" + content);
		byte[] source = content.getBytes();
		String result = Base64.encodeBytes(source, Base64.GZIP, Base64.PREFERRED_ENCODING);
		Log.i(TAG, "加密后：" + content);
		return result;
	}

	public static String decodeBase64(String content) {
		Log.i(TAG, "解密前：" + content);
		byte[] bytes = Base64.decode(content, Base64.GZIP, Base64.PREFERRED_ENCODING);
		String result = "error";
		if (bytes != null)
			result = new String(bytes);
		result = result.replace("\\", "\\\\").replace("&", "\\&").replace("'", "\\'");
		Log.i(TAG, "解密后：" + result);
		return result;
	}
}
