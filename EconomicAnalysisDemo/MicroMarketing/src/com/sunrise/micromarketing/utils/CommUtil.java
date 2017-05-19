package com.sunrise.micromarketing.utils;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.cache.download.DownLoadThread;
import com.sunrise.micromarketing.entity.UpdateInfo;
import com.sunrise.micromarketing.ui.activity.BaseActivity;
import com.swetake.util.Qrcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
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

public class CommUtil {
	public static int sNotificationId = -1;
	public static boolean isDownApk = false;
	private static NotificationManager sUpdateNotificationManager;
	private static String sApkFilePath;
	private static Notification sUpdateNotification;

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

	private static void showDownApkProgress(Context context) {
		// sDownApkProgressDialog = new MyProgressDialog(context);
		// sDownApkProgressDialog.setProgressStatus(context.getString(R.string.downloading_status));
		// sDownApkProgressDialog.show();
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
		// sUpdateNotification.contentView.setProgressBar(R.id.content_view_progress,
		// 100, msg.arg1, false);
		// String info = context.getString(R.string.latest_apk_downloading);
		// sUpdateNotification.contentView.setTextViewText(R.id.down_apk_status,
		// info + msg.arg1 + "%");
		// // modify by qhb want to be test
		// PendingIntent pendingintent = PendingIntent.getActivity(context, 0,
		// new Intent(), 0);
		// sUpdateNotification.contentIntent = pendingintent;
		// sUpdateNotificationManager.notify(sNotificationId,
		// sUpdateNotification);
	};

	private static void installApk(final Context context, boolean showProgressDialog) {
		// if (showProgressDialog)
		// sDownApkProgressDialog.dismiss();
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

	public static void exit(Context context) {
		BaseActivity.exit(context);
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
		btnreturn.setText(R.string.returntxt);

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

	public static int getApkVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getApkVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "0.0";
	}

	public static Bitmap createQRCode(String content, Bitmap icon) {
		final int CELL_SIZE = 8;
		final int bimtapSize = 360;
		Bitmap resizedBitmap = Bitmap.createBitmap(bimtapSize, bimtapSize, Bitmap.Config.ARGB_8888);
		try {
			Qrcode qrcodeHandler = new Qrcode();
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			qrcodeHandler.setQrcodeVersion(7);
			// System.out.println(content);
			byte[] contentBytes = content.getBytes("utf-8");

			Canvas canvas = new Canvas(resizedBitmap);
			Paint paint = new Paint();
			canvas.drawColor(0xffffffff);
			// 设定图像颜色 > BLACK
			paint.setColor(0xff000000);
			// 设置偏移量 不设置可能导致解析出错
			int pixoff = 0;
			// 输出内容 > 二维码
			System.out.println(contentBytes.length);
			if (contentBytes.length > 0 && contentBytes.length < 124) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				System.out.println(codeOut.length);
				RectF r = new RectF();
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							int x = j * CELL_SIZE + pixoff;
							int y = i * CELL_SIZE + pixoff;
							r.set(x, y, x + CELL_SIZE, y + CELL_SIZE);
							canvas.drawRect(r, paint);
						}
					}
				}
			} else {
				System.err.println("QRCode content bytes length = " + contentBytes.length + " not in [ 0,120 ]. ");
				return null;
			}

			// // 实例化一个Image对象。
			canvas.drawBitmap(icon, bimtapSize - icon.getWidth() >> 1, bimtapSize - icon.getHeight() >> 1, paint);
			// // 生成二维码QRCode图片
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resizedBitmap;
	}
}
