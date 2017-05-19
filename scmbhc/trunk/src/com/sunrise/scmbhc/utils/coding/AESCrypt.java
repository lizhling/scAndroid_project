package com.sunrise.scmbhc.utils.coding;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.sunrise.des.DesCryp;

import android.util.Log;

public class AESCrypt implements CodingInterface {

	/**
	 * 加密
	 * 
	 * @param seed
	 * @param cleartext
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String seed, String cleartext) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] result = encrypt(rawKey, cleartext.getBytes());
		return toHex(result);
	}

	/**
	 * 解密
	 * 
	 * @param seed
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String seed, String encrypted) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	private byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
		SecureRandom sr = null;
		if (android.os.Build.VERSION.SDK_INT >= 17) {
			sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		} else {
			sr = SecureRandom.getInstance("SHA1PRNG");
		}
		sr.setSeed(seed);
		kgen.init(256, sr); // 256 bits or 128 bits,192bits
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	private byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	private String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	private String fromHex(String hex) {
		return new String(toByte(hex));
	}

	private byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
		return result;
	}

	private String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private final static String HEX = "0123456789ABCDEF";
	private static final String SEED = "abcdefghijklmn";

	private void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

	/** Called when the activity is first created. */
	public void test() {
		// super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		String masterPassword = "abcdefghijklmn";
		String originalText = "i see qinhubao eat milk!";
		byte[] text = new byte[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		byte[] password = new byte[] { 'a' };
		try {
			String encryptingCode = encrypt(masterPassword, originalText);
			// System.out.println("加密结果为 " + encryptingCode);
			Log.i("加密结果为 ", encryptingCode);
			String decryptingCode = decrypt(masterPassword, encryptingCode);
			System.out.println("解密结果为 " + decryptingCode);
			Log.i("解密结果", decryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String encode(String str) {
		try {
			return encrypt(SEED, str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String decode(String str) {
		try {
			return decrypt(SEED, str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
