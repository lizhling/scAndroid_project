package com.sunrise.econan;

import java.io.File;

import android.os.Environment;

public interface ExtraKeyConstant {

	public static final String APP_SD_PATH_NAME = "econan";

	public static final String APP_FILE_HTML_DATA_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_SD_PATH_NAME;

	public static final String HTML_FILE_NAME = "index.html";

	public static final String KEY_BUNDLE = "bundle";
	public static final String KEY_URL = "url";

	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_IMSI = "imsi";
	public static final String KEY_IMEI = "imei";

	public static final String APP_FILE_APK_DIR = "econan";

	public static final String APP_FILE_APK_NAME = "update.apk";

	public static final String APP_DOWNLOAD_INFO = "app download info";

}
