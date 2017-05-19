package com.sunrise.javascript.utils;

/**
 *@(#)ActivityUtils.java        0.01 2012/11/17
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.activity.MipcaCaptureActivity;
import com.sunrise.javascript.activity.TakePictureActivity;

/**
 * Activity跳转和实名认证，捕捉图片
 * 
 * @version 0.01 September 21 2012
 * @author LIU WEI
 */

public class ActivityUtils {
	private final static String TAG = "ActivityUtils";
	private Context mContext;
	private JavascriptHandler mJavascriptHandler;
	public static final int GO_LOGIN_REQUEST_CODE = 1;
	public static final String CMUC_LOGIN_ACTION = "com.starcpt.yxzs.LOGIN";
	public static final String CHECK_DEVICE = "com.sunrise.javascript.CHECKDEVICE";
	public static final String TEST_LOGIN_ACTION = "com.sunrise.javascript.LOGIN";
	public static final String WEB_GO_BROADCAST = "com.sunrise.javascript.WEBGO";
	public static final String WEB_REFRESH_BROADCAST = "com.sunrise.javascript.WEBREFRESH";
	public static final String FIRST_PAGE_EXTRAL = "isFirstPage";
	public static final String CAPTURE_IMAGE_ACTION = "com.sunrise.javascript.CAPTURE";
	public static final String CAPTURE_TRANSFERIMAGE_ACTION = "com.sunrise.javascript.TRANSFER";
	public static final String IDCARD_INFO_ACTION = "com.sunrise.javascript.IDCARD";
	public static final String DIMENSIONAL_BAR_CODE_SCAN = "com.sunrise.javascript.SCAN_QR_CODE";
	private String mDirImageTake = "cmuc" + File.separator + "html";

	private static int CodeError;

	private final static String IMAGE_FILENAME = "channelImage.jpg";
	private final static String TXT_FILENAME = "channelImage.txt";

	private volatile int usbflag = 0;
	private String key;
	private BroadcastReceiver mCaptureImageReciever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(CAPTURE_IMAGE_ACTION)) {
				String result = intent.getStringExtra(JavaScriptConfig.EXTRAL_BITMAP);
				String key = intent.getStringExtra(Intent.EXTRA_KEY_EVENT);
				CommonUtils.sendResult(result, key, mJavascriptHandler);
				context.unregisterReceiver(this);
			} else if (action.equals(CAPTURE_TRANSFERIMAGE_ACTION)) {
				LogUtlis.i("excute:", "action");
				String result = intent.getStringExtra(JavaScriptConfig.EXTRAL_BITMAP_RESULT);
				LogUtlis.i("write action before:", "result:" + result);
				CommonUtils.sendResult(result, key, mJavascriptHandler);
				LogUtlis.i("write action after:", "result:" + result);
				context.unregisterReceiver(this);
			}
		}

	};

	public ActivityUtils(Context mContext, JavascriptHandler mJavascriptHandler) {
		super();
		this.mContext = mContext;
		this.mJavascriptHandler = mJavascriptHandler;
		CodeError = 1;
	}

	/**
	 * 通过包民和Activity名字启动指定的activity
	 * 
	 * @param pkg
	 *            activity所在包的名字，如：com.starcpt.yxzs
	 * @param cls
	 *            activity 全名 如：com.starcpt.yxzs.ui.activity.LoginActivity
	 * @throws IllegalArgumentException
	 *             source 加密内容为空
	 */

	public void startActivity(String pkg, String cls) {
		Intent intent = new Intent();
		ComponentName componentName = new ComponentName(pkg, cls);
		intent.setComponent(componentName);
		((Activity) mContext).startActivityForResult(intent, GO_LOGIN_REQUEST_CODE);
	}

	/**
	 * 通过action启动指定的activity
	 * 
	 * @param action
	 *            值：com.starcpt.yxzs.LOGIN 智能统一终端登录；com.sunrise.javascript.LOGIN
	 *            javascript api测试登录
	 * @throws IllegalArgumentException
	 *             source 没在找到能执行此动作的activity
	 */

	public void startActivity(String action) {
		try {
			Intent intent = new Intent();
			intent.setAction(action);
			((Activity) mContext).startActivityForResult(intent, GO_LOGIN_REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			throw new IllegalArgumentException("指定的操作不存在");
		}
	}

	/**
	 * 
	 * 结束当前页
	 * 
	 * @param isFirstPage
	 *            是否返回的最后一页
	 * 
	 * */
	public void finish(boolean isFirstPage) {
		Intent intent = new Intent(WEB_GO_BROADCAST);
		intent.putExtra(FIRST_PAGE_EXTRAL, isFirstPage);
		mContext.sendBroadcast(intent);
	}

	public void refresh(boolean showBt) {
		Intent intent = new Intent(WEB_REFRESH_BROADCAST);
		intent.putExtra(Intent.EXTRA_TEXT, showBt);
		mContext.sendBroadcast(intent);
	}

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		return sdDir.toString();
	}

	private void registerCaptureCameraReciever() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(CAPTURE_IMAGE_ACTION);
		mContext.registerReceiver(mCaptureImageReciever, filter);
	}

	/**
	 * 拍照获取图片
	 * 
	 * @param key
	 *            本次调用标志，多次调用时候判断。
	 * */
	public void takePicture(String watermark, String key) {
		takePicture(watermark, null, key);
	}

	/**
	 * 拍照获取图片
	 * 
	 * @param key
	 *            本次调用标志，多次调用时候判断。
	 * @param watermark
	 *            水印参数
	 * @param type
	 *            图片前缀
	 * */
	public void takePicture(String watermark, String type, String key) {
		registerCaptureCameraReciever();
		this.key = key;
		Intent ti = new Intent(mContext, TakePictureActivity.class);
		ti.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		ti.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (type != null)
			ti.putExtra(TakePictureActivity.TAKE_PICTURE_PREFIX_EXTRAL, type);
		ti.putExtra(TakePictureActivity.TAKE_PICTURE_WATERMARK_EXTRAL, watermark);
		ti.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, JavaScriptConfig.sCaptureImageDir);
		ti.putExtra(Intent.EXTRA_KEY_EVENT, key);
		mContext.startActivity(ti);
	}

	/**
	 * 
	 * 删除路径路径下的文件
	 * 
	 * @param path
	 *            文件路径
	 * @param fileName
	 *            文件名
	 * @param key
	 *            本次调用标志，多次调用时候判断。
	 * 
	 * */
	public void deleteFile(String path, String fileName, String key) { // add by
																		// qhb
		String filePath = null;
		this.key = key;
		if (path == null || fileName == null || path.length() == 0 || fileName.length() == 0) {
			CommonUtils.sendResult("{\"RETURN_CODE\":\"1\", \"RETURN_MESSAGE\"\"目录或文件不存在\"}", key, mJavascriptHandler);
			return;
		}
		if (!path.startsWith("/")) {
			if (path.endsWith(File.separator)) {
				filePath = getSDPath() + path + fileName;
			} else {
				filePath = getSDPath() + path + File.separator + fileName;
			}
			FileUtils.deleteFileByRelativePath(path, fileName);
		} else {
			if (path.endsWith(File.separator)) {
				filePath = path + fileName;
			} else {
				filePath = path + File.separator + fileName;
			}
			FileUtils.deleteFileAbsolutePath(filePath);
		}
		File file = new File(filePath);
		if (!file.exists()) {
			CommonUtils.sendResult("{\"RETURN_CODE\":\"0\", \"RETURN_MESSAGE\":\"删除成功\"}", key, mJavascriptHandler);
		} else {
			CommonUtils.sendResult("{\"RETURN_CODE\":\"2\", \"RETURN_MESSAGE\":\"删除失败\"}", key, mJavascriptHandler);
		}

	}

	/**
	 * 将base64加密后的图片数据解密再放到文件目录
	 * 
	 * @param FileStream
	 *            加密后的图片文件字符串
	 * @param tag
	 *            标识 inter 室内 outer 室外 shop 店面
	 * @return json 字符串
	 */
	public void strTransferImg(final String FileStream, final String tag, final String key) {
		new Thread() {
			public void run() {
				String result = null;
				String baseStr = null;
				String FileStreamtemp = FileStream;
				byte image_encodebyte[] = null;
				String fileNamelocal = IMAGE_FILENAME;
				if (tag != null && tag.trim().length() > 0) {
					fileNamelocal = tag + IMAGE_FILENAME;
				}
				LogUtlis.i("filemsg", "file content:" + FileStreamtemp);
				String path = JavaScriptConfig.sCaptureImageDir + File.separator + "strTransferImage" + File.separator;
				LogUtlis.i("Activity", "path: " + path + "  name:" + fileNamelocal);
				if (FileStream == null) {
					FileStreamtemp = FileUtils.getImageStr(JavaScriptConfig.sCaptureImageDir + File.separator + tag + TXT_FILENAME);

				}
				try {
					// Base64解码
					// FileStream = FileStrem.trim();
					Pattern p = Pattern.compile("\\\\n|\t|\r|\n");
					Matcher m = p.matcher(FileStreamtemp);
					FileStreamtemp = m.replaceAll("");
					int len = FileStreamtemp.getBytes().length;
					LogUtlis.i("Activity ：", "FileStream:" + FileStreamtemp.getBytes().toString() + ",lenth:" + FileStreamtemp.getBytes());
					// image_encodebyte=Base64.decode(FileStream.getBytes(), 0,
					// len, Base64.NO_OPTIONS);
					image_encodebyte = Base64.decode(FileStreamtemp);
					// 生成jpeg图片
					// Log.i("Activity", "picturebyte:" + new
					// String(image_encodebyte));
					if (image_encodebyte != null) {
						FileUtils.deleteFileByRelativePath(path, fileNamelocal);
						File file = new File(getSDPath() + File.separator + path + fileNamelocal);
						LogUtlis.i("ActivityUtils", "file is exits ?" + file.exists() + "," + getSDPath() + path + fileNamelocal);
						FileUtils.createSDDir(path);
						LogUtlis.i("ActivityUtils", "path:" + path);
						String txt = "";
						txt = new String(image_encodebyte);
						OutputStream out = new FileOutputStream(getSDPath() + File.separator + path + fileNamelocal);
						out.write(image_encodebyte);
						out.flush();
						out.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					image_encodebyte = null;
				} finally {
					if (image_encodebyte != null) {
						result = "{\"ImageFileName\":\"" + fileNamelocal + "\",\"filePath\":\"" + getSDPath() + File.separator + path
								+ "\",\"ImageIdentify\":\"" + tag + "\"}";
					} else {
						result = "{}";
					}
					LogUtlis.i("ACtivity", "result:" + result);
					CommonUtils.sendResult(result, key, mJavascriptHandler);
				}
			};
		}.start();

	}

	/**
	 * 启动二维码扫描
	 * 
	 * @param key
	 */
	public void scanQrCode(String key) {
		this.key = key;

		IntentFilter filter = new IntentFilter();
		filter.addAction(DIMENSIONAL_BAR_CODE_SCAN);
		mContext.registerReceiver(mScanQrCodeReciever, filter);

		Intent intent = new Intent(mContext, MipcaCaptureActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	/**
	 * 二维码扫描结果监听
	 */
	private BroadcastReceiver mScanQrCodeReciever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(DIMENSIONAL_BAR_CODE_SCAN)) {
				String result = intent.getStringExtra(Intent.EXTRA_TEXT);
				CommonUtils.sendResult(result, key, mJavascriptHandler);
				context.unregisterReceiver(this);
			}
		}

	};

	// 取本机通讯录
	public void getPhoneContracts(String key) {
		new Thread(key) {
			public void run() {
				ContentResolver resolver = mContext.getContentResolver();
				// 获取手机联系人
				Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, null, null, null, null); // 传入正确的uri

				if (phoneCursor != null) {
					JSONArray jsarr = new JSONArray();

					while (phoneCursor.moveToNext()) {
						String name = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.DISPLAY_NAME));// 获取联系人name
						String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER)); // 获取联系人number
						if (TextUtils.isEmpty(phoneNumber)) {
							continue;
						}

						phoneNumber = CommonUtils.removeNoNecessaryWordsFromPhoneNumber(phoneNumber);
						if (!CommonUtils.isPhoneNumber(phoneNumber))
							continue;

						try {
							JSONObject arg0 = new JSONObject();
							arg0.put(Phone.NUMBER, phoneNumber);
							arg0.put(Phone.DISPLAY_NAME, name);
							jsarr.put(arg0);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					phoneCursor.close();
					CommonUtils.sendResult(jsarr.toString(), getName(), mJavascriptHandler);
				} else
					CommonUtils.sendResult("通讯录获取失败", getName(), mJavascriptHandler);
			}

		}.start();
	}

}
