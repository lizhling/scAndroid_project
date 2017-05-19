package com.sunrise.javascript.utils;

import java.util.Calendar;

import com.sunrise.javascript.JavascriptHandler;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class ViewUtils extends CommonUtils{
private final static String TAG="ViewUtils";
private Context mContext;
private JavascriptHandler mJavascriptHandler;
private int mYear;
private int mMonth ;
private int mDay ;
private int mHour;
private int mMinute;
private String mKey;
private DatePickerDialog mDatePickerDialog;
private TimePickerDialog mTimePickerDialog;
private boolean datePickShow = false;
private boolean timePickShow = false;
class Time{
	private int year;
	private int month ;
	private int day ;
	private int hour;
	private int minute;
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
}

/**   
 * 日期控件的事件   

 */ 
private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {    
 public void onDateSet(DatePicker view, int year, int monthOfYear,    
          int dayOfMonth) {
	  if(datePickShow){
	       datePickShow=false;
		   Time time=new Time();  
	       time.setYear(year);
	       time.setMonth(monthOfYear+1);
	       time.setDay(dayOfMonth);
	       String jsonStr=JsonUtils.writeObjectToJsonStr(time);
	       sendResult(jsonStr,mKey, mJavascriptHandler);
	       Log.d(TAG, "jsonStr:"+jsonStr);
	       mDatePickerDialog.dismiss();
	  }
   }    
};   
/**  

 * 时间控件事件  

 */ 
private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {  
    @Override 
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {  
    	if(timePickShow){
    	timePickShow=false;
        mHour = hourOfDay;  
        mMinute = minute;  
        Time time=new Time();  
        time.setHour(hourOfDay);
        time.setMinute(minute);
        String jsonStr=JsonUtils.writeObjectToJsonStr(time);
        Log.d(TAG, "jsonStr:"+jsonStr);      
        sendResult(jsonStr,mKey, mJavascriptHandler);
        }
    }  

};  

public ViewUtils(Context mContext, JavascriptHandler mJavascriptHandler) {
	super();
	this.mContext = mContext;
	this.mJavascriptHandler = mJavascriptHandler;
	Calendar c = Calendar.getInstance(); 
	mYear = c.get(Calendar.YEAR);  
	mMonth = c.get(Calendar.MONTH);  
	mDay = c.get(Calendar.DAY_OF_MONTH);   
	mHour = c.get(Calendar.HOUR_OF_DAY);
	mMinute = c.get(Calendar.MINUTE); 
}

public void showDatePickerDialog(String key){
	mKey=key;
	mDatePickerDialog=new DatePickerDialog(((Activity)mContext).getParent()!=null?((Activity)mContext).getParent():mContext, mDateSetListener, mYear, mMonth, mDay);
	mDatePickerDialog.show();  
	datePickShow=true;
}

public void showTimePickerDialog(String key){
	mKey=key;
	mTimePickerDialog=new TimePickerDialog(((Activity)mContext).getParent()!=null?((Activity)mContext).getParent():mContext, mTimeSetListener, mHour, mMinute, true);
	mTimePickerDialog.show();
	timePickShow=true;
}
}
