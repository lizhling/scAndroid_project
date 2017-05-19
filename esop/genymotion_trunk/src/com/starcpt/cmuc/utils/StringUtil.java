package com.starcpt.cmuc.utils;

public class StringUtil {
private static final String TRUE="true";
public static boolean convertStringToBool(String str){
	if(str.equals(TRUE)){
		return true;
	}
	else
		return false;
}
public static int convertStringToNumber(String str){
	int number;
	try {
	number=Integer.valueOf(str);
	} catch (NumberFormatException e) {
		// TODO: handle exception
		return 0;
	}
	return number;
}

public static String toSBC(String input) { 
	// 半角转全角： 
	char[] c = input.toCharArray(); 
	for (int i = 0; i< c.length; i++) { 
	if (c[i] == 32) { 
	c[i] = (char) 12288; 
	continue; 
	} 
	if (c[i]< 127) 
	c[i] = (char) (c[i] + 65248); 
	} 
	return new String(c); 
	}

/**   
 * 全角转换为半角   
 *    
 * @param input   
 * @return   
 */   
public static String toDBC(String input) {    
    char[] c = input.toCharArray();    
    for (int i = 0; i < c.length; i++) {    
        if (c[i] == 12288) {    
            c[i] = (char) 32;    
            continue;    
        }    
        if (c[i] > 65280 && c[i] < 65375)    
            c[i] = (char) (c[i] - 65248);    
    }    
    return new String(c);    
}  

}
