package com.sunrise.javascript.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateFormat;

public class DateUtils {
	public final static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public final static String DATE_FORMAT_1 = "yyyyMMddHHmmss";

	/**
	 * 格式化日期为字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public final static String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * 分析并格式化字符串为日期类型
	 * 
	 * @param source
	 * @param pattern
	 *            　日期格式，如：yyyy-MM-dd HH:mm:ss
	 * @return 返回格式后的日期类型（如果source为null或空，则返回null ，如果日期格式分析错误也返回null）
	 */
	public final static Date parse(String source, String pattern) {
		if (source == null || "".equals(source))
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	// yyyy-MM-dd hh:mm:ss"
	public static String formatTime(long time, String pattern) {
		String s = DateFormat.format(pattern, time).toString();
		return s;
	}

	public static String formatlong2Time(long time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date(time));
	}

	public static long string2Long(String time, String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date dt = sdf.parse(time);
		return dt.getTime();
	}

	/**
	 * 获得当前时间，格式自定义
	 * 
	 * @param format
	 * @return 当前时间的字符串表示
	 */
	public static String getCurrentDate(String format) {
		Calendar day = Calendar.getInstance();
		day.add(Calendar.DATE, 0);
		SimpleDateFormat sdf = new SimpleDateFormat(format);// "yyyy-MM-dd"
		String date = sdf.format(day.getTime());
		return date;
	}

}
