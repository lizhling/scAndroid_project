package com.sunrise.scmbhc.utils;

public class IsChineseOrNot {
	
    public static final boolean isChineseCharacter(String chineseStr) {  
        char[] charArray = chineseStr.toCharArray();  
        for (int i = 0; i < charArray.length; i++) {  	
        	//是否是Unicode编码,除了"�"这个字符.这个字符要另外处理
            if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD')||((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF'))) {  
                continue;
            }
            else{
            	return false;
            }
        }  
        return true;  
    }  
    
    public static final boolean isSpecialCharacter(String str){
    	//是"�"这个特殊字符的乱码情况
    	if(str.contains("ï¿½")){
    		return true;
    	}
    	return false;
    }
}