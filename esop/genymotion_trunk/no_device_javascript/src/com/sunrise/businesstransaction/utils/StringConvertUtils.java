package com.sunrise.businesstransaction.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringConvertUtils {
	
	/**  
     * 字符串转换成十六进制字符串 
     * @param String str 待转换的ASCII字符串 
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B] 
     */    
    public static String str2HexStr(String str)  
    {    
  
        char[] chars = "0123456789ABCDEF".toCharArray();    
        StringBuilder sb = new StringBuilder("");  
        byte[] bs = str.getBytes();    
        int bit;    
          
        for (int i = 0; i < bs.length; i++)  
        {    
            bit = (bs[i] & 0x0f0) >> 4;    
            sb.append(chars[bit]);    
            bit = bs[i] & 0x0f;    
            sb.append(chars[bit]);  
            //sb.append(' ');  
        }    
        return sb.toString().trim();    
    }  
      
    /**  
     * 十六进制转换字符串 
     * @param String str Byte字符串(Byte之间无分隔符 如:[616C6B]) 
     * @return String 对应的字符串 
     */    
    public static String hexStr2Str(String hexStr)  
    {    
        String str = "0123456789ABCDEF";    
        char[] hexs = hexStr.toCharArray();    
        byte[] bytes = new byte[hexStr.length() / 2];    
        int n;    
  
        for (int i = 0; i < bytes.length; i++)  
        {    
            n = str.indexOf(hexs[2 * i]) * 16;    
            n += str.indexOf(hexs[2 * i + 1]);    
            bytes[i] = (byte) (n & 0xff);    
        }    
        return new String(bytes);    
    }  
      
    /** 
     * bytes转换成十六进制字符串 
     * @param byte[] b byte数组 
     * @return String 每个Byte值之间空格分隔 
     */  
    public static String byte2HexStr(byte[] b)  
    {  
        String stmp="";  
        StringBuilder sb = new StringBuilder("");  
        for (int n=0;n<b.length;n++)  
        {  
            stmp = Integer.toHexString(b[n] & 0xFF);  
            sb.append((stmp.length()==1)? "0"+stmp : stmp);  
            //sb.append(" ");  
        }  
        return sb.toString().toUpperCase().trim();  
    }
      
    /** 
     * bytes字符串转换为Byte值 
     * @param String src Byte字符串，每个Byte之间没有分隔符 
     * @return byte[] 
     */  
    public static byte[] hexStr2Bytes(String src)  
    {  
        int m=0,n=0;  
        int l=src.length()/2;  
        System.out.println(l);  
        byte[] ret = new byte[l];  
        for (int i = 0; i < l; i++)  
        {  
            m=i*2+1;  
            n=m+1;  
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));  
        }  
        return ret;  
    }  
  
    /** 
     * String的字符串转换成unicode的String 
     * @param String strText 全角字符串 
     * @return String 每个unicode之间无分隔符 
     * @throws Exception 
     */  
    public static String strToUnicode(String strText)  
        throws Exception  
    {  
        char c;  
        StringBuilder str = new StringBuilder();  
        int intAsc;  
        String strHex;  
        for (int i = 0; i < strText.length(); i++)  
        {  
            c = strText.charAt(i);  
            intAsc = (int) c;  
            strHex = Integer.toHexString(intAsc);  
            if (intAsc > 128)  
                str.append("\\u" + strHex);  
            else // 低位在前面补00   
                str.append("\\u00" + strHex);  
        }  
        return str.toString();  
    }  
      
    /** 
     * unicode的String转换成String的字符串 
     * @param String hex 16进制值字符串 （一个unicode为2byte） 
     * @return String 全角字符串 
     */  
    public static String unicodeToString(String hex)  
    {  
        int t = hex.length() / 6;  
        StringBuilder str = new StringBuilder();  
        for (int i = 0; i < t; i++)  
        {  
            String s = hex.substring(i * 6, (i + 1) * 6);  
            // 高位需要补上00再转   
            String s1 = s.substring(2, 4) + "00";  
            // 低位直接转   
            String s2 = s.substring(4);  
            // 将16进制的string转为int   
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);  
            // 将int转换为字符   
            char[] chars = Character.toChars(n);  
            str.append(new String(chars));  
        }  
        return str.toString();  
    }  

    
	public static String stringToHexString(String strPart) {
		String hexString = "&&";
		for (int i = 0; i < strPart.length(); i++) {
			int ch = (int) strPart.charAt(i);
			String strHex = Integer.toHexString(ch);
			hexString = hexString + strHex;
		}
		return hexString;
	}

	private static String hexString="0123456789ABCDEF";

	/*
	* 将字符串编码成16进制数字,适用于所有字符（包括中文）
	*/
	public static String encode(String str){
		// 根据默认编码获取字节数组
		byte[] bytes=str.getBytes();
		StringBuilder sb=new StringBuilder(bytes.length*2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for(int i=0;i<bytes.length;i++){
			sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
			sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
		}
		return sb.toString();
	}

	/*
	* 将16进制数字解码成字符串,适用于所有字符（包括中文）
	*/
	public static String decode(String bytes){
		ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2);
		// 将每2位16进制整数组装成一个字节
		for(int i=0;i<bytes.length();i+=2)
		baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
		return new String(baos.toByteArray());
	}
	
	private static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] {src0})).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] {src1})).byteValue();
		byte ret = (byte) (_b0 | _b1);
		return ret;
	}
	
	/**
	 * １６进制字符串转成２进制
	 * @param src
	 * @return
	 */
	public static byte[] HexString2Bytes(String src){
		byte[] ret = new byte[6];
		byte[] tmp = src.getBytes();
		for(int i=0; i<6; ++i ){
		ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
		}
		return ret;
	}
	

	/**
	* Convert char to byte
	* @param c char
	* @return byte
	*/
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 去字符串左中右的空格
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	/**
	 * 把１０进制转成１６进制字符串     10->0A
	 * @param data
	 * @return
	 */
	public static final String toHexString(int data) {
		StringBuffer sb = new StringBuffer("");
		String sTemp;
		
		sTemp = Integer.toHexString(data);
		if (sTemp.length() < 2){
			sb.append(0);
		}
		sb.append(sTemp.toUpperCase());
		
		return sb.toString();
	}
	
    
    public static String byteToHexString(byte b) {
        byte[] bt = {b};                                        // 字节数组
        return byte2HexStr(bt);
    }
    
    
    public static List<Byte> chineseToCodeMulti(String chinese) {
    	List<Byte> list = new ArrayList<Byte>();
    	char[] charArr = chinese.toCharArray();
    	for(Character c : charArr) {
    		String quwei = chineseToCode(c.toString());
    		if(quwei.length() == 4){
    			list.add((byte)Integer.parseInt(quwei.substring(0, 2), 16));
    			list.add((byte)Integer.parseInt(quwei.substring(2, 4), 16));
    		}else {
    			byte b = (byte)Integer.parseInt(quwei.substring(0, 2), 16);
    			list.add(b);
    		}
    	}
    	return list;
    }
    
    
	public static String chineseToCode(String chinese) {
	byte[] bt;
	String code = "";
	try {
		bt = chinese.getBytes("GB2312"); // 用GB2312编码为字节数组
			//ascii转换
		if(bt.length == 1){
			int ascii = Integer.parseInt(byteToHexString(bt[0]), 16);
			String hex = Integer.toHexString(ascii);
			if(hex.length()==1){
				code += "0" + hex;
			}else {
				code += hex;
			}
		}else {
			for (int i = 0; i < bt.length; i++) {
				int a = Integer.parseInt(byteToHexString(bt[i]), 16);
				// 获得区位码
				String aa = (a - 0x80 - 0x20) + "";
				if(aa.length()==1){
					code += Integer.toHexString(Integer.valueOf(aa)+0x20+0x80);
				}else {
					code += Integer.toHexString(Integer.valueOf(aa)+0x20+0x80);
				}
			}
		}
		
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	return code;
}    
    
    
    
	/**
	 * 将区位码转换为汉字的方法
	 * @param code
	 * @return
	 */
	public static String codeToChinese(String code) {
		String Chinese = "";
		for (int i = 0; i < code.length(); i += 4) {
			byte[] bytes = new byte[2]; // 存储区位码的字节数组
			String highCode = code.substring(i, i + 2); // 获得高位
			int tempHigh = Integer.parseInt(highCode); // 将高位转换为整数
			tempHigh += 160; // 计算出区号
			bytes[0] = (byte) tempHigh; // 将区号存储到字节数组
			String lowCode = code.substring(i + 2, code.length()); // 获得低位
			int tempLow = Integer.parseInt(lowCode); // 将低位转换为整数
			tempLow += 160; // 计算出位号
			bytes[1] = (byte) tempLow; // 将位号存储到字节数组
			String singleChar = ""; // 存储转换的单个字符
			try {
				singleChar = new String(bytes, "GB2312"); // 通过GB2312编码进行转换
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Chinese += singleChar; // 存储转换后的结果
		}
		return Chinese;
	}

}