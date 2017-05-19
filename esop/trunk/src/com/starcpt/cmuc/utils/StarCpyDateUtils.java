package com.starcpt.cmuc.utils;

import java.util.Calendar;

public class StarCpyDateUtils {
	
	public static long getTodayFrom(){
		Calendar start = Calendar.getInstance();
        start.set( Calendar.HOUR_OF_DAY,00);
        start.set( Calendar.MINUTE, 0);
        start.set( Calendar.SECOND,0);
        
        return start.getTimeInMillis();
	}
	
	public static long getTodayEnd(){
		Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY,23);           
        end.set(Calendar.MINUTE,59);           
        end.set(Calendar.SECOND,59);      
        return end.getTimeInMillis();
	}
	
	public static long getYesterdayFrom(){
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DAY_OF_MONTH, -1);
		start.set(Calendar.HOUR_OF_DAY,00);           
		start.set( Calendar.MINUTE,0);           
		start.set(Calendar.SECOND,0);
        
        return start.getTimeInMillis();
	}
	
	public static long getYesterdayEnd(){
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DAY_OF_MONTH, -1);
		start.set(Calendar.HOUR_OF_DAY,23);           
		start.set( Calendar.MINUTE,59);           
		start.set(Calendar.SECOND,59);
        
        return start.getTimeInMillis();
	}
}
