package com.sunrise.scmbhc.utils.coding;

import android.text.TextUtils;
import com.sunrise.scmbhc.utils.DesCrypUtil;
public class DesCrypCoding implements CodingInterface {

	/**
	 * @param str
	 * @return 加密
	 */
	public String encode(String str) {
		if (TextUtils.isEmpty(str))
			return str;
		//LogUtlis.showLogI("DES", "加密前：" + str);
		//String ret = DesCryp.DESEncrypt(str, "1234adcb");
		String ret = "error des";
		try {
			ret = DesCrypUtil.DESEncrypt(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
		//LogUtlis.showLogI("DES", "加密后：" + ret);
		return ret;
	}

	/**
	 * @param str
	 * @return 解密
	 */
	public String decode(String str) {
		if (TextUtils.isEmpty(str))
			return str;
		//LogUtlis.showLogI("DES", "解密前：" + str);
		//String ret = DesCryp.DESDecrypt(str, "1234adcb");
		String ret =  null;
		try {
			ret = DesCrypUtil.DESDecrypt(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.gc();
		//LogUtlis.showLogI("DES", "解密后：" + ret);
		return ret;
	}

}
