package com.util;

import android.util.Log;

import com.sunrise.javascript.utils.Base64;
import com.sunrise.javascript.utils.aes.AES;
import com.sunrise.javascript.utils.aes.DesCrypUtil;

public class ZipUtils {

	private static final String TAG = "ZipUtils";

	public static String encodeBase64(String content) {
		Log.i(TAG, "加密前：" + content);
		byte[] source = content.getBytes();
		String result = Base64.encodeBytes(source, Base64.GZIP, Base64.PREFERRED_ENCODING);
		Log.i(TAG, "加密后：" + result);
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

	public static String encodeAES(String content) {
		byte[] source = content.getBytes();
		String result = new AES().encrypt(source);
		return result;
	}

	public static String decodeAES(String content) {
		return new AES().decrypt(content);
	}

	public static String encodeDes(String content) {
		Log.i(TAG, "加密前：" + content);
		String result = "error: 加密失败";
		try {
			result = DesCrypUtil.DESEncrypt(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(TAG, "加密后：" + result);
		return result;
	}

	public static String decodeDes(String content) {
		Log.i(TAG, "解密前：" + content);
		String result = "error";
		try {
			result = DesCrypUtil.DESDecrypt(content.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(TAG, "解密后：" + result);
		return result;
	}
}
