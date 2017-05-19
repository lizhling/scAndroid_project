package com.sunrise.businesstransaction.utils;

public class StringConverter {

	public static long binToLong(byte[] from, int offset, int len) {
		long to;
		int min = offset;
		to = 0;
		for (int i_move = len - 1, i_from = min; i_move >= 0; i_move--, i_from++) {
			to = to << 8 | (from[i_from] & 0xff);
		}
		return to;
	}


	public static byte[] readByte(int startByte, int endByte, byte[] b){
		byte[] result = new byte[(endByte -startByte)];
		
		System.arraycopy(b, startByte, result, 0, result.length);
		
		return result;
	}
	
	public static long readLong(int startByte, int endByte, byte[] b){
		byte[] result = readByte(startByte, endByte, b);
		
		return binToLong(result, 0, result.length);
	}
	
	public static String readString(int startByte, int endByte, byte[] b){
		byte[] result = readByte(startByte, endByte, b);
		return new String(result);
	}
	
	public static String readByteString(int startByte, int endByte, byte[] b){
		StringBuffer sb = new StringBuffer();
		byte[] result = readByte(startByte, endByte, b);
		for(byte bit : result){
			System.out.println();
			sb.append(bit);
		}
		return sb.toString();
	}
	
	/****************************************************************************
	 * 将报文以字符串数值的方式进行解析
	 * @param hex
	 * @param bit
	 * @return
	 */
	public static String[] converterString2Array(String hex, int bit){
		String[] result = new String[hex.length()/bit];
		for(int i=0; i<result.length; i++){
			result[i]=hex.substring(i*bit,(i+1)*bit);
		}
		return result;
	}
	
	/**
	 * 报文长度类型，直接返回整形
	 * @param startByte
	 * @param endByte
	 * @param s
	 * @return
	 */
	public static int getIntegerFromArr(int startByte, int endByte, String[] s){
		//高位在前
		StringBuffer sb = new StringBuffer();
		for(int i=endByte-1; i>=startByte; i--){
			sb.append(s[i]);
		}
		return Integer.parseInt(sb.toString(), 16);
	}
	
	/**
	 * 返回原始的16进制字符串
	 * @param startByte
	 * @param endByte
	 * @param s
	 * @return
	 */
	public static String getHexStringFromArr(int startByte, int endByte, String[] s){
		StringBuffer sb = new StringBuffer();
		for(int i=startByte; i<endByte; i++){
			sb.append(s[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 返回解析后的字符串
	 * @param startByte
	 * @param endByte
	 * @param s
	 * @return
	 */
	public static String getStringFromArr(int startByte, int endByte, String[] s){
		StringBuffer sb = new StringBuffer();
		for(int i=startByte; i<endByte; i++){
			sb.append(s[i]);
		}
		byte[] b = HexConverter.hexStringDecode(sb.toString());
		
		return new String(b);
	}

	/**
	 * 返回中文字符串
	 * @param startByte
	 * @param endByte
	 * @param s
	 * @return
	 */
	public static String getStringZWFromArr(int startByte, int endByte, String[] s){
		StringBuffer sb = new StringBuffer();
		for(int i=startByte; i<endByte; i++){
			sb.append(s[i]);
		}
//		System.out.println(sb.toString());
		
		StringBuffer result = new StringBuffer("");
		String[] hexArr = converterString2Array(sb.toString(), 4);
		for(int i=0; i<hexArr.length; i++){
			result.append(StringConvertUtils.codeToChinese(hexArr[i]));
		}
		return result.toString();
	}
	
	/**
	 * 替换身份证指定位置显示为*
	 * @param idcard 身份证号码
	 * @param begin 开始位置
	 * @param end　结束位置
	 * @return
	 */
	public static String replaceIDCardstar(String idcard,int begin,int end) {
		if(idcard!=null && idcard.length() > 0){
			String tempStr = idcard.substring(begin, end);
			idcard = idcard.replace(tempStr, "******");
		} else {
			return "";
		}
		return idcard;
	}
	
}
