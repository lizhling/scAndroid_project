package com.sunrise.scmbhc.utils.coding;

import java.io.UnsupportedEncodingException;

import com.sunrise.scmbhc.utils.LogUtlis;

import android.text.TextUtils;
import android.util.Base64;

public class Base64Coding implements CodingInterface {
	private static final String TAG = "Base64Coding";

	/**
	 * @param str
	 * @return 加密
	 */
	public String encode(String str) {
		if (TextUtils.isEmpty(str))
			return str;

		try {
			return compressAndEncodeBase64(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * @param str
	 * @return 解密
	 */
	public String decode(String str) {
		if (TextUtils.isEmpty(str))
			return str;
		try {
			return decompressAndDecodeBase64(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String compressAndEncodeBase64(String content, String chartSetName) throws UnsupportedEncodingException {
		LogUtlis.showLogI(TAG, "加密前：" + content);
		byte[] source = content.getBytes(chartSetName);
		byte[] bytes = Base64.encode(source, Base64.DEFAULT);
		String result = new String(bytes, chartSetName);
		LogUtlis.showLogI(TAG, "加密后：" + result);
		return result;
	}

	public static String decompressAndDecodeBase64(String content, String chartSetName) throws UnsupportedEncodingException {
		LogUtlis.showLogI(TAG, "解密前：" + content);
		byte[] source = content.getBytes(chartSetName);
		byte[] bytes = Base64.decode(source, Base64.DEFAULT);
		String result = "error";
		if (bytes != null) {
			result = new String(bytes, chartSetName);
		}
		LogUtlis.showLogI(TAG, "解密后：" + result);
		return result;
	}
}
