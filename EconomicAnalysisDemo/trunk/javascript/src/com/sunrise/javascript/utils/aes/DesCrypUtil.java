/*
 * FileName:     DesCrypUtil.java
 * @Description: 
 * Copyright (c) 2014 Sunrise Corporation.
 * #368, Guangzhou Avenue South, Haizhu District, Guangzhou
 * All right reserved.
 *
 * Modification  History:
 * Date           Author         Version         Discription
 * -----------------------------------------------------------------------------------
 * 2014-5-20   WangXiangBo        1.0              新建
 */

package com.sunrise.javascript.utils.aes;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.sunrise.des.DesCryp;
import com.sunrise.javascript.utils.Base64;


/** 
 * @ClassName DesCrypUtil.java
 * @Author WangXiangBo 
 * @Date 2014-5-20 下午03:56:02
 */
public class DesCrypUtil {
	
    /** 加密、解密key. */
    private static String PASSWORD_CRYPT_KEY = null;//  "1234abdc";
    /** 加密算法,可用 DES,DESede,Blowfish. */
    private final static String ALGORITHM = "DES";
    private final static String ALGORITHM_FORMATE = "DES/ECB/PKCS5Padding";
    
    /**
     * 对用DES加密过的数据进行解密.
     * @param data DES加密数据
     * @return 返回解密后的数据, 如果传入为null返回null
     */
    public final static String DESDecrypt(String data) throws Exception {
    	if (PASSWORD_CRYPT_KEY == null) {
    		String key = DesCryp.GetEncodeKey();
    		PASSWORD_CRYPT_KEY = DesCryp.DecodeKey(key);
//    		PASSWORD_CRYPT_KEY = "ZdCX*@k^";
    	}
    	
    	String result = "error";
        if (data == null || data.length() == 0) {
        	return null;
        } else {
        	byte[] bytes=Base64.decode(data, Base64.GZIP,Base64.PREFERRED_ENCODING);
        	if (bytes != null) {
        		result = new String(decrypt(bytes,
	        		PASSWORD_CRYPT_KEY.getBytes(Base64.PREFERRED_ENCODING)),Base64.PREFERRED_ENCODING);
        	}
			return result;
        }
    }
    
    /**
     * 对数据进行DES加密.
     * @param data 待进行DES加密的数据
     * @return 返回经过DES加密后的数据
     */
    public final static String DESEncrypt(String data) throws Exception  {
    	if (PASSWORD_CRYPT_KEY == null) {
    		String key = DesCryp.GetEncodeKey();
    		PASSWORD_CRYPT_KEY = DesCryp.DecodeKey(key);
    	}
    	byte[] source = encrypt(data.getBytes(Base64.PREFERRED_ENCODING), PASSWORD_CRYPT_KEY
                .getBytes(Base64.PREFERRED_ENCODING));
    	String result = Base64.encodeBytes(source, Base64.GZIP,Base64.PREFERRED_ENCODING);
        return result;
    }
    
    public final static String GetDynamicKey(String data) throws Exception  {
    	return DesCryp.GetDynamicKey(data);
    }
    
    /**
     * 对用DES加密过的数据进行解密.
     * @param data DES加密数据
     * @param key DES加密的key
     * @return 返回解密后的数据, 如果传入为null返回null
     */
    public final static String DESDecrypt(String data, String key) throws Exception {
    	
    	String result = "error";
        if (data == null || data.length() == 0) {
        	return null;
        } else {
        	byte[] bytes=Base64.decode(data, Base64.GZIP,Base64.PREFERRED_ENCODING);
        	if (bytes != null) {
        		result = new String(decrypt(bytes,
        				key.getBytes(Base64.PREFERRED_ENCODING)),Base64.PREFERRED_ENCODING);
        	}
			return result;
        }
    }
    
    /**
     * 对数据进行DES加密.
     * @param data 待进行DES加密的数据
     * @param key DES加密的key
     * @return 返回经过DES加密后的数据
     */
    public final static String DESEncrypt(String data, String key) throws Exception  {
    	byte[] source = encrypt(data.getBytes(Base64.PREFERRED_ENCODING), key.getBytes(Base64.PREFERRED_ENCODING));
    	String result = Base64.encodeBytes(source, Base64.GZIP,Base64.PREFERRED_ENCODING);
        return result;
    }
    /**
     * 用指定的key对数据进行DES加密.
     * @param data 待加密的数据
     * @param key DES加密的key
     * @return 返回DES加密后的数据
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
    	//System.out.println("encrypt:"+data.length);
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(ALGORITHM_FORMATE);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(data);
    }
    
    /**
     * 用指定的key对数据进行DES解密.
     * @param data 待解密的数据
     * @param key DES解密的key
     * @return 返回DES解密后的数据
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(ALGORITHM_FORMATE);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(data);
    }
    
    /*public static byte[] hex2byte(byte[] b) {
    	//System.out.println(b.length);
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        System.out.println("hex2byte:"+b2.length);
        return b2;
    }
    
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }*/
	
}
