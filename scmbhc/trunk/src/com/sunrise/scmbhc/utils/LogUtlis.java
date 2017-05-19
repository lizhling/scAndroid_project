package com.sunrise.scmbhc.utils;

import com.sunrise.scmbhc.App;

import android.util.Log;

public class LogUtlis {


	public static final void showLogE(String tag, String msg) {
		if (App.test)
			Log.e(tag, msg);
	}

	public static final void showLogE(String tag, String msg, Throwable e) {
		if (App.test)
			Log.e(tag, msg, e);
	}

	public static final void showLogD(String tag, String msg) {
		if (App.test)
			Log.d(tag, msg);
	}

	public static final void showLogI(String tag, String msg) {
		if (App.test)
			Log.i(tag, msg);
	}

	public static final void w(String tag, String msg) {
		if (App.test)
			Log.w(tag, msg);
	}

	public static final void w(String tag, String msg, Throwable e) {
		if (App.test)
			Log.w(tag, msg, e);
	}

	public static final void v(String tag, String msg) {
		if (App.test)
			Log.v(tag, msg);
	}
}
