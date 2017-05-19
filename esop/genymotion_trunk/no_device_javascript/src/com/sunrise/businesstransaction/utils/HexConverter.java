/**
 * 
 */
package com.sunrise.businesstransaction.utils;

public class HexConverter {

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static final String toHexString(byte[] data) {
		if(data == null){
			return null;
		}
		StringBuffer sb = new StringBuffer(data.length);
		String sTemp;
		for (int i = 0; i < data.length; i++) {
			sTemp = Integer.toHexString(0xFF & data[i]);
			if (sTemp.length() < 2){
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	
	public static final String toHexString(String data) {
		if(data == null){
			return null;
		}
		return toHexString(data.getBytes());
	}
	
	/**
	 * 把十进制的字节转换为十六进制的字节
	 * @param bArray
	 * @return
	 */
	public static final byte[] toHex(byte[] data) {
		if(data == null){
			return null;
		}
		return toHexString(data).getBytes();
	}


	/**
	 * 
	 * @param octalStr
	 * @return
	 */
	public static final byte[] toHex(String octalStr) {
		if(octalStr == null){
			return null;
		}
		return toHex(octalStr.getBytes());
	}
	
	
	/**
	 * 将16进制转换成byte[]
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringDecode(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (hexToByte(achar[pos]) << 4 | hexToByte(achar[pos + 1]));
		}
		return result;
	}

	public static String hexStringDecodeToString(String hex) {
		return new String(hexStringDecode(hex));
	}
	
	private static byte hexToByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}	
	
}
