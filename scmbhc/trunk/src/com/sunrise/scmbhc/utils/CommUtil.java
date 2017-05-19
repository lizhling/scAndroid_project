package com.sunrise.scmbhc.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.androidpn.client.ServiceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.starcpt.analytics.PhoneClickAgent;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.broadcast.DownLoadApkReceiver;
import com.sunrise.scmbhc.broadcast.ServiceCheckReceiver;
import com.sunrise.scmbhc.cache.download.DownLoadThread;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.MobileBusinessHall;
import com.sunrise.scmbhc.entity.SearchTag;
import com.sunrise.scmbhc.entity.UpdateInfo;
import com.sunrise.scmbhc.service.TrafficNotificationService;
import com.sunrise.scmbhc.ui.activity.LocationOverlayActivity;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.ui.fragment.WebViewFragment;
import com.sunrise.scmbhc.ui.view.MyProgressDialog;

public class CommUtil {
	public static int sNotificationId = -1;
	public static boolean isDownApk = false;
	private static NotificationManager sUpdateNotificationManager;
	private static String sApkFilePath;
	private static boolean isForceUpdate = false;
	private static Notification sUpdateNotification;
	private static MyProgressDialog sDownApkProgressDialog;

	/**
	 * @param context
	 * @param updateInfo
	 * @return 升级信息描述
	 */
	public static CharSequence sGetDownloadDescriptionWords(Context context, UpdateInfo updateInfo) {
		String colon = ":";
		String updateLatestVersionName = updateInfo.getNewVersionName();
		String latestVersionName = context.getString(R.string.latest_version_name);
		if (updateLatestVersionName != null)
			latestVersionName = latestVersionName + colon + updateLatestVersionName;

		String updateSupportVersionName = updateInfo.getSuportVersionName();
		String supportVersionName = context.getString(R.string.support_version_name);
		if (supportVersionName != null)
			supportVersionName = supportVersionName + colon + updateSupportVersionName;

		String updateDes = updateInfo.getUpdateDescription();
		String des = "";
		if (updateDes != null) {
			String[] desArray = updateDes.split(colon);
			if (desArray != null) {
				for (int i = 0; i < desArray.length; i++) {
					int index = i + 1;
					String temp = index + "." + desArray[i];
					des += temp + "\n";
				}
			}
		}
		CharSequence message = Html.fromHtml(String.format("<b>发现新版本:v" + updateLatestVersionName + "<\b>")) + "\n\n" + "更新内容:\n" + des;
		return message;
	}

	public static void cleanSharedPreference(Context context) {
		FileUtils.deleteFileAbsolutePath("/data/data/" + context.getPackageName() + "/shared_prefs");
	}

	public static void downloadApk(final Context context, final String downLoadUrl, final boolean showProgressDialog) throws IOException {
		isDownApk = true;
		sApkFilePath = FileUtils.getAbsPath(App.AppDirConstant.APP_APK_DIR, App.AppDirConstant.APP_APK_FILE_NAME);
		final Handler dowloadApkHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case DownLoadThread.START_DOWN_FILE:
					initDownLoadApk(context, showProgressDialog);
					break;
				case DownLoadThread.DWONLOAD_FLIE_COMPLETE:
					isDownApk = false;
					isForceUpdate = false;
					installApk(context, showProgressDialog);
					break;
				case DownLoadThread.DWONLOADING_FILE:
					showDownloadApkProgress(context, msg, showProgressDialog);
					break;
				case DownLoadThread.DWONLOAD_FLIE_ERROR:
					isDownApk = false;
					handleDownloadApkError(context, downLoadUrl, showProgressDialog);
					if (isForceUpdate) {
						sUpdateNotificationManager.cancel(sNotificationId);
						sNotificationId = -1;
						Toast.makeText(context, "下载失败,重新启动app进行更新", Toast.LENGTH_LONG).show();
						if (showProgressDialog) {
							sDownApkProgressDialog.dismiss();
						}
						isForceUpdate = false;
						CommUtil.exit(context);
						System.gc();
						System.exit(0);
					}
					break;
				default:
					break;
				}

			}

		};
		if (downLoadUrl != null)
			App.sDownFileManager.downFile(context, downLoadUrl, App.AppDirConstant.APP_APK_DIR, App.AppDirConstant.APP_APK_FILE_NAME, dowloadApkHandler, true,
					false);
	}

	private static void showDownApkProgress(Context context) {
		sDownApkProgressDialog = new MyProgressDialog(context);
		sDownApkProgressDialog.setProgressStatus(context.getString(R.string.downloading_status));
		/*
		 * sDownApkProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL
		 * ); sDownApkProgressDialog.setMessage(context.getString(R.string.
		 * down_apk_progress)); sDownApkProgressDialog.setMax(100);
		 * sDownApkProgressDialog.setIndeterminate(false);
		 * sDownApkProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
		 * context.getString(R.string.background_donwn_apk), new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * sDownApkProgressDialog.dismiss(); }
		 * 
		 * });
		 */
		sDownApkProgressDialog.show();
	}

	private static void initDownLoadApk(final Context context, boolean showProgressDialog) {
		if (showProgressDialog)
			showDownApkProgress(context);
		initNotifaction(context);
	}

	private static void initNotifaction(final Context context) {
		sNotificationId = DownLoadThread.START_DOWN_FILE;
		// 初始化通知管理器
		sUpdateNotificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		sUpdateNotification = new Notification();
		sUpdateNotification.icon = R.drawable.ic_launcher;

		// 通知自定义视图
		sUpdateNotification.contentView = new RemoteViews(context.getPackageName(), R.layout.down_apk_notification_view);
		sUpdateNotification.contentView.setProgressBar(R.id.content_view_progress, 100, 0, false);
	}

	private static void showDownloadApkProgress(final Context context, android.os.Message msg, boolean showProgressDialog) {
		/*
		 * if (showProgressDialog) sDownApkProgressDialog.setProgress(msg.arg1);
		 */
		showNotifactionProgress(context, msg);
	}

	private static void showNotifactionProgress(final Context context, android.os.Message msg) {
		sUpdateNotification.contentView.setProgressBar(R.id.content_view_progress, 100, msg.arg1, false);
		String info = context.getString(R.string.latest_apk_downloading);
		sUpdateNotification.contentView.setTextViewText(R.id.down_apk_status, info + msg.arg1 + "%");
		// modify by qhb want to be test
		PendingIntent pendingintent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		sUpdateNotification.contentIntent = pendingintent;
		/*
		 * PendingIntent pendingintent = PendingIntent.getActivity(context, 0,
		 * new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
		 * sUpdateNotification.setLatestEventInfo(context,info, msg.arg1 + "%",
		 * pendingintent);
		 */
		sUpdateNotificationManager.notify(sNotificationId, sUpdateNotification);
	};

	private static void installApk(final Context context, boolean showProgressDialog) {
		if (showProgressDialog)
			sDownApkProgressDialog.dismiss();
		startInstallApkActivity(context);
		CommUtil.exit(context);
		sUpdateNotificationManager.cancel(sNotificationId);
		sNotificationId = -1;
	}

	private static void startInstallApkActivity(final Context context) {
		Uri uri = Uri.fromFile(new File(sApkFilePath));
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
		installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(installIntent);
	}

	private static void handleDownloadApkError(final Context context, final String downloadUrl, boolean showProgressDialog) {
		notifactionShowError(context, downloadUrl);
		if (showProgressDialog) {
			progressDialogShowError(context, downloadUrl);
		}
	}

	private static JSONArray readSearchObjectFromFile(Context context) throws JSONException, IOException {
		JSONArray existJSON = null;
		String contentStr = FileUtils.readDataFile(context, App.AppDirConstant.APP_SEARCH_FILE_NAME);
		if (contentStr != null) {
			if (contentStr.length() > 0) {
				existJSON = new JSONArray(contentStr);
			}
		}
		return existJSON;
	}

	public static void addActivity(Activity activity) {
		App.sActivityList.add(activity);
	}

	public static ArrayList<MobileBusinessHall> getMobileBusinessHalls(String mobileBusinessHallsStr) {
		LogUtlis.showLogD("CommUtil", "mobileBusinessHallsStr:" + mobileBusinessHallsStr);
		String[] mobileBusinessHallStrArray = mobileBusinessHallsStr.split("\n");
		ArrayList<MobileBusinessHall> halls = new ArrayList<MobileBusinessHall>();
		for (String mobileBusinessHallStr : mobileBusinessHallStrArray) {
			if (!TextUtils.isEmpty(mobileBusinessHallStr)) {
				String mobileBusinessHall[] = mobileBusinessHallStr.split("\\" + LocationOverlayActivity.SPLITSTR);
				String id = mobileBusinessHall[0];
				String name = mobileBusinessHall[1];
				String address = mobileBusinessHall[2];
				// String phoneNumber=mobileBusinessHall[4];
				// String iconUrl=mobileBusinessHall[3];
				// String workingDay=mobileBusinessHall[4];
				// String holiDay=mobileBusinessHall[5];
				double lat = Double.valueOf(mobileBusinessHall[3]);
				double lon = Double.valueOf(mobileBusinessHall[4]);
				String point = "0";
				if (mobileBusinessHall.length > 5 && !TextUtils.isEmpty(mobileBusinessHall[5])) {
					point = mobileBusinessHall[5];
				}
				int canbepoint = Integer.valueOf(point);
				MobileBusinessHall mobileHall = new MobileBusinessHall(name, address, null, null, null, null);
				LatLng coordinate = new LatLng(lat, lon);
				mobileHall.setCoordinate(coordinate);
				mobileHall.setId(id);
				mobileHall.setCanbeappoint(canbepoint);
				halls.add(mobileHall);
			}
		}
		return halls;
	}

	/**
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
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

	public static void visitWebView(Context context, String url) {
		Intent intent = new Intent(context, SingleFragmentActivity.class);
		intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT, WebViewFragment.class);
		BusinessMenu businessMenu = new BusinessMenu();
		businessMenu.setServiceUrl(url);
		businessMenu.setName("二维码扫描结果");
		Bundle bundle = new Bundle();
		bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, true);
		context.startActivity(intent);
	}

	public static void visitWebView(Context context, String url, String titleName) {
		Intent intent = new Intent(context, SingleFragmentActivity.class);
		intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT, WebViewFragment.class);
		BusinessMenu businessMenu = new BusinessMenu();
		businessMenu.setServiceUrl(url);
		if (!TextUtils.isEmpty(titleName)) {
			businessMenu.setName(titleName);
		} else {
			businessMenu.setName("二维码扫描结果");
		}
		Bundle bundle = new Bundle();
		bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO, businessMenu);
		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, true);
		context.startActivity(intent);
	}

	public static void exit(Context context) {
		try {
			for (int i = 0; i < App.sActivityList.size(); i++) {
				if (null != App.sActivityList.get(i)) {
					App.sActivityList.get(i).finish();
				}
			}
		} catch (Exception e) {
		}
		PhoneClickAgent.onKillProcess(context);
		App.sAppRunning = false;
	}

	public static void saveSearchKeyToFile(Context context, String key, int tagType) throws JSONException, NameNotFoundException, IOException {
		JSONArray datas = null;
		datas = readSearchObjectFromFile(context);
		if (datas == null) {
			datas = new JSONArray();
		}
		int length = datas.length();
		boolean isContain = false;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject searchTagJson = (JSONObject) datas.get(i);
				String tagName = searchTagJson.getString(SearchTag.TAGNAME);
				int type = searchTagJson.getInt(SearchTag.TAGTYPE);
				int scount = searchTagJson.getInt(SearchTag.SCOUNT);
				if (tagName.equals(key) && type == tagType) {
					isContain = true;
					scount += 1;
					searchTagJson.put(SearchTag.SCOUNT, scount);
					break;
				}
			}
		}
		if (!isContain) {
			JSONObject searchJson = new JSONObject();
			searchJson.put(SearchTag.SCOUNT, 1);
			searchJson.put(SearchTag.TAGNAME, key);
			searchJson.put(SearchTag.TAGTYPE, tagType);
			datas.put(searchJson);
		}
		FileUtils.saveToDataFile(context, datas.toString(), App.AppDirConstant.APP_SEARCH_FILE_NAME);
	}

	private static void notifactionShowError(final Context context, final String downloadUrl) {
		sUpdateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent reDownloadApkIntent = new Intent(context, DownLoadApkReceiver.class);
		reDownloadApkIntent.putExtra(App.ExtraKeyConstant.KEY_DOWNLOAD_APK_URL, downloadUrl);
		PendingIntent pendingintent = PendingIntent.getBroadcast(context, 0, reDownloadApkIntent, 0);
		sUpdateNotification.contentView.setTextViewText(R.id.down_apk_status, context.getString(R.string.apk_download_fail));
		sUpdateNotification.setLatestEventInfo(context, context.getString(R.string.apk_download_fail), context.getString(R.string.apk_download_fail),
				pendingintent); // add
		sUpdateNotificationManager.notify(sNotificationId, sUpdateNotification);
	}

	private static void progressDialogShowError(final Context context, final String downloadUrl) {
		sDownApkProgressDialog.dismiss();
		/*
		 * sDownApkProgressDialog.setMessage(context.getString(R.string.
		 * down_apk_failed));
		 * sDownApkProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE
		 * ).setText(R.string.re_try);
		 * sDownApkProgressDialog.getButton(DialogInterface
		 * .BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * sDownApkProgressDialog.dismiss();
		 * sUpdateNotificationManager.cancel(sNotificationId); try {
		 * downloadApk(context, downloadUrl, true); } catch (IOException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } } });
		 * sDownApkProgressDialog.setProgress(0);
		 */
	}

	/**
	 * 判断是否是手机号码格式
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isMobilePhone(String number) {
		return number.matches("^((13[4-9])|(147)|(15[^4,^5,\\D])|(178)|(18[2-4,7-8]))\\d{8}$");
	}

	/**
	 * 判断是否是邮箱地址格式
	 * 
	 * @param address
	 * @return
	 */
	public static boolean isEmail(String address) {
		return address.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
	}

	private static AlertDialog sAlartDialog;

	/**
	 * @param context
	 * @param title
	 * @param message
	 * @param listener
	 * @param cancelAble
	 */
	public static void showAlert(Context context, CharSequence title, CharSequence message, boolean cancelAble, final DialogInterface.OnClickListener listener) {

		dismissAlert();

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		sAlartDialog = builder.create();

		sAlartDialog.show();
		sAlartDialog.setCancelable(cancelAble);
		Window view = sAlartDialog.getWindow();
		view.setContentView(R.layout.return_alert_button_dialog);
		LayoutInflater flater = LayoutInflater.from(context);
		// View view = flater.inflate(R.layout.return_alert_button_dialog,
		// null);
		Button btnreturn = (Button) view.findViewById(R.id.cancle);// 注：“View.”别忘了，不然则找不到资源
		btnreturn.setText(R.string.Return);

		TextView tv_title = (TextView) view.findViewById(R.id.dialog_title);
		View tv_title_view = (View) view.findViewById(R.id.dialog_titl_view);
		if (title != null && !TextUtils.isEmpty(title)) {
			tv_title.setText(title);
			tv_title.setVisibility(View.VISIBLE);
			tv_title_view.setVisibility(View.VISIBLE);
		} else {
			tv_title.setVisibility(View.GONE);
			tv_title_view.setVisibility(View.GONE);
		}
		TextView tv_msg = (TextView) view.findViewById(R.id.dialog_message);
		tv_msg.setText(message);
		// builder.setView(view);
		btnreturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sAlartDialog.dismiss();
				if (listener != null)
					listener.onClick(sAlartDialog, 0);
			}
		});

		// new
		// AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton(R.string.Return,
		// listener).show();
	}

	public static void showAlert(Context context, CharSequence title, CharSequence message, final DialogInterface.OnClickListener listener) {
		showAlert(context, title, message, true, listener);
	}

	public static void dismissAlert() {
		if (sAlartDialog != null && sAlartDialog.isShowing())
			sAlartDialog.dismiss();
	}

	/**
	 * 展开列表,避免scrollview和livtview的冲突
	 * 
	 * @param adapter
	 * @param listView
	 */
	public static void expandListView(BaseAdapter adapter, ListView listView) {
		// 统计高度
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	public static void startTrafficNotificationService(Context context) {
		boolean isTurnOnService = App.sSettingsPreferences.isTrafficNotificationFunction(true);
		if (isTurnOnService) {
			context.startService(new Intent(context, TrafficNotificationService.class));
		} else {
			context.stopService(new Intent(context, TrafficNotificationService.class));
		}
	}

	/**
	 * 移动数据开启和关闭
	 * 
	 * @param context
	 * @param enabled
	 */
	public static void setMobileDataStatus(Context context, boolean enabled) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// ConnectivityManager类
		Class<?> conMgrClass = null;
		// ConnectivityManager类中的字段
		Field iConMgrField = null;
		// IConnectivityManager类的引用
		Object iConMgr = null;
		// IConnectivityManager类
		Class<?> iConMgrClass = null;
		// setMobileDataEnabled方法
		Method setMobileDataEnabledMethod = null;
		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// 取得ConnectivityManager类中的对象Mservice
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());

			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

			// 设置setMobileDataEnabled方法是否可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
			setMobileDataEnabledMethod.invoke(iConMgr, enabled);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取移动数据开关状态
	 * 
	 * @param context
	 * @param getMobileDataEnabled
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean getMobileDataStatus(Context context, String getMobileDataEnabled) {
		ConnectivityManager cm;
		cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		Class cmClass = cm.getClass();
		Class[] argClasses = null;
		Object[] argObject = null;
		Boolean isOpen = false;
		try {
			Method method = cmClass.getMethod(getMobileDataEnabled, argClasses);
			isOpen = (Boolean) method.invoke(cm, argObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOpen;
	}

	/**
	 * @param radiogroup
	 * @return 获得当前选中的序号，没选中，返回-1
	 */
	public static int getCheckedPositionInRadioGroup(RadioGroup radiogroup) {
		int id = radiogroup.getCheckedRadioButtonId();
		if (id != -1)
			return radiogroup.indexOfChild(radiogroup.findViewById(id));
		return -1;
	}

	/**
	 * 去除字符串中非数字的字符
	 * 
	 * @param str
	 * @return
	 */
	public static String wipeOffUnDigitChar(String str) {
		if (TextUtils.isEmpty(str))
			return null;

		StringBuffer sb = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			if ((c >= '0' && c <= '9') || c == '-' || c == '.')
				sb.append(c);
		}
		return sb.toString();
	}

	public static float parse2Float(String str) {
		float result = 0;
		String temp = wipeOffUnDigitChar(str);

		if (!TextUtils.isEmpty(temp))
			temp = wipeOffUnDigitChar(temp);
		if (!TextUtils.isEmpty(temp))
			result = Float.parseFloat(temp);

		return result;
	}

	public static double parse2Double(String str) {
		double result = 0;
		String temp = wipeOffUnDigitChar(str);

		if (!TextUtils.isEmpty(temp))
			temp = wipeOffUnDigitChar(temp);
		if (!TextUtils.isEmpty(temp))
			result = Double.parseDouble(temp);

		return result;
	}

	/**
	 * 转换为数字，转换失败，为默认数
	 * 
	 * @param str
	 * @param defaultInt
	 * @return
	 */
	public static int parse2Integer(String str, int defaultInt) {
		try {
			defaultInt = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return defaultInt;
	}

	/**
	 * 获取前N+1个月的显示格式字符串
	 * 
	 * @param pastNum
	 *            前几个月。0为前一个月
	 * @return 显示格式为yyyyMM
	 */
	public static String getMonthPast(int pastNum) {
		return getMonthPast("%04d%02d", pastNum);
	}

	/**
	 * @param format
	 *            仅限年月两个属性
	 * @param pastNum
	 * @return 获取前N+1个月的显示格式字符串
	 */
	public static String getMonthPast(String format, int pastNum) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);

		month -= pastNum;
		if (month < 0) {
			year--;
			month += 12;
		}

		return String.format(format, year, month + 1);
	}

	/**
	 * @param str
	 * @return 判断是否包含中文字符
	 */
	public static boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		Pattern p = Pattern.compile("^[0-9]*$");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 去除电话号码中不必要的字符
	 * 
	 * @param number
	 * @return
	 */
	public static final String removeNoNecessaryWordsFromPhoneNumber(String number) {
		// 电话号码，去除不必要的字符
		if (number.startsWith("+86"))
			number = number.substring(3);

		if (!TextUtils.isDigitsOnly(number)) {
			StringBuilder sb = new StringBuilder();
			char[] chars = number.toCharArray();
			for (char c : chars)
				if (Character.isDigit(c)) {
					sb.append(c);
				}

			number = sb.toString();
		}
		return number;
	}

	/**
	 * 功能： 启动消息推送服务
	 * 
	 * @param context
	 * @return
	 */
	public static ServiceManager startPushMessageService(Context context) {
		// Start the service
		LogUtlis.showLogI("PushMessage", "启动消息服务");
		ServiceManager serviceManager = new ServiceManager(context);
		serviceManager.setNotificationIcon(R.drawable.ic_launcher);
		serviceManager.startService();
		// 一直检查服务运行状态
	    IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK); 
	    ServiceCheckReceiver receiver = new ServiceCheckReceiver(); 
	    context.registerReceiver(receiver, filter);
		return serviceManager;
	}

	public static HashMap<String, String> stringToMap(String... params) {
		HashMap<String, String> map = new HashMap<String, String>();

		if (params != null) {
			for (String paramStr : params) {
				String[] paramArray = paramStr.split("&");
				if (paramArray.length > 1) {
					map.put(paramArray[0], paramArray[1]);
				}
			}
		}

		return map;
	}

	public static boolean isOpenNetwork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	/**
	 * @param name
	 * @return 从业务名称中删除资费代码
	 */
	public static String deleteProdPrcidFromBusinessName(String name) {
		if (TextUtils.isEmpty(name))
			return name;

		int start = name.indexOf('[');
		int end = name.indexOf(']');

		if (start == -1 || end == -1 || start > end)
			return name;
		String temp = name.substring(start, end + 1);
		return name.replace(temp, "");
	}

	public static boolean isChineseMobile(String phoneNum) {
		if (TextUtils.isEmpty(phoneNum.trim()) || phoneNum.trim().length() != 11) {
			return false;
		} else {
			String[] chinaMobile = { "188", "147", "152", "138", "151", "137", "158", "134", "135", "136", "178", "184", "187", "183", "139", "150", "182",
					"157", "159", "156", "153" };
			String temp = phoneNum.trim().substring(0, 3);
			for (int i = 0; i < chinaMobile.length; i++) {
				if (chinaMobile[i].equals(temp)) {
					return true;
				}
			}
			return false;
		}
	}

	private static long lastClickTime;

	/**
	 * 功能： 防止快速双次点击
	 * 
	 * @return 返回是否可进行二次操作
	 * 
	 *         使用方法：public void onClick(View v) { if (Utils.isFastDoubleClick())
	 *         {return;}}
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {

			return true;

		}
		lastClickTime = time;
		return false;

	}

	public static String getUniqueID(Context context) {
		String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
				+ Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length()
				% 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10;
		String uID = m_szDevIDShort + getAppUUID(context);

		// 13 digits
		return m_szDevIDShort;
	}

	public synchronized static String getAppUUID(Context context) {
		String sID = null;
		final String INSTALLATION = "INSTALLATION";
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation);
				sID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sID;
	}

	private static String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}

	public static String getCpuID() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			// 读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /procuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			// 查找CPU序列号
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					// 查找到序列号所在行
					if (str.indexOf("Serial") > -1) {
						// 提取序列号
						strCPU = str.substring(str.indexOf(":") + 1, str.length());
						// 去空格
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					// 文件结尾
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return cpuAddress;

	}

	public static String getAppsign(Context context) {
		return getSingInfo3(context);
	}

	private static String getAppSign1(Context context) {
		StringBuilder builder = new StringBuilder();
		try {
			/** 通过包管理器获得指定包名包含签名的包信息 **/
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			/******* 通过返回的包信息获得签名数组 *******/
			Signature[] signatures = packageInfo.signatures;
			/******* 循环遍历签名数组拼接应用签名 *******/
			for (Signature signature : signatures) {
				builder.append(signature.toCharsString());
			}
			/************** 得到应用签名 **************/
			return builder.toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "未获取到MD5";
	}

	private static String getAppSign2(Context context) {

		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		String test = "";
		try {
			fis = new FileInputStream(context.getPackageResourcePath());
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			test = toHexString(md5.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		test.toString();
		return test.toString();
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	private static String getSingInfo3(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature[] signs = packageInfo.signatures;
			Signature sign = signs[0];
			return parseSignature(sign.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String parseSignature(byte[] signature) {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
			String pubKey = cert.getPublicKey().toString();
			String signNumber = cert.getSerialNumber().toString();
			// System.out.println("pubKey:" + pubKey);
			// System.out.println("signNumber:" + signNumber);
			return pubKey;
		} catch (CertificateException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/** 
     * 使用java正则表达式去掉多余的.与0 
     * @param s 
     * @return  
     */  
    public static String subZeroAndDot(String s){  
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }  
        return s;  
    }  
}
