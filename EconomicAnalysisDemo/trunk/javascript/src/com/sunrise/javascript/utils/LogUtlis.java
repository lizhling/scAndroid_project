package com.sunrise.javascript.utils;

import android.util.Log;

public class LogUtlis {

	public static boolean showLog = true;// 是否显示log

	public static final void e(String tag, String msg) {
		if (showLog)
			Log.e(tag, msg);
	}

	public static final void e(String tag, String msg, Throwable e) {
		if (showLog)
			Log.e(tag, msg, e);
	}

	public static final void d(String tag, String msg) {
		if (showLog)
			Log.d(tag, msg);
	}

	public static final void i(String tag, String msg) {
		if (showLog)
			Log.i(tag, msg);
	}

	public static final void w(String tag, String msg) {
		if (showLog)
			Log.w(tag, msg);
	}

	public static final void v(String tag, String msg) {
		if (showLog)
			Log.v(tag, msg);
	}
}
