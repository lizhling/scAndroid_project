package com.starcpt.cmuc.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.util.Log;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.model.bean.LogBean;
import com.sunrise.javascript.utils.JsonUtils;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author user
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	public static final String TAG = "CrashHandler";
	
	//系统默认的UncaughtException处理类 
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	//程序的Context对象
	private Context mContext;

	//用于格式化日期,作为日志文件名的一部分
	@SuppressLint("SimpleDateFormat")
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ConnectivityManager mConnManager;
	private CmucApplication cmucApplication;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	
	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		cmucApplication=(CmucApplication) mContext.getApplicationContext();
		//获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		mConnManager=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			if(mDefaultHandler!=null)
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		
		try {
			saveCrashInfoFile(ex);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private String getNetworkStatus() {  
		mConnManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if(mConnManager.getActiveNetworkInfo() != null) {  
	    	if(mConnManager.getActiveNetworkInfo().isAvailable())
	    		return LogBean.NET_OPEN;
	    	else
	    		return LogBean.NET_COLSE;
	    }    
	     return LogBean.NET_COLSE;
	}  
	
	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return	返回文件名称,便于将文件传送到服务器
	 * @throws NameNotFoundException 
	 */
	private void saveCrashInfoFile(Throwable ex) throws NameNotFoundException {
		Preferences preferences=cmucApplication.getSettingsPreferences();
		String time = formatter.format(new Date());
		long timestamp = System.currentTimeMillis();
		String fileName = "noup-"+"crash-" + time + "-" + timestamp + ".log";
		
		LogBean logBean=new LogBean();
		logBean.setDeviceModel(cmucApplication.getModeNumber());
		logBean.setDeviceImei(cmucApplication.getPhoneIMEI());
		logBean.setDeviceOsVersion(cmucApplication.getReleaseVersion());
		logBean.setOccurTime(time);
		String userName=preferences.getUserName()==null?"null":preferences.getUserName();
		logBean.setAccount4a(userName);
		String netStatus=getNetworkStatus();
		logBean.setNetStatus(netStatus);
		logBean.setNetType(mConnManager.getActiveNetworkInfo().getTypeName());
		logBean.setApkVersion(CmucApplication.sContext.getPackageManager().getPackageInfo(CmucApplication.sContext.getPackageName(), 0).versionCode+"");
			
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		if (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		result=result.replace("{", "(");
		result=result.replace("}", ")");
		logBean.setErrorLog(result);		
		try {
			String jsonStr=JsonUtils.writeObjectToJsonStr(logBean);
			Log.d(TAG, "jsonStr:"+jsonStr);
			FileUtils.saveToFile(jsonStr, CmucApplication.APP_CRASH_LOG_DIR, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
