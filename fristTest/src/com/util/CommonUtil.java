package com.util;

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

import com.entity.UpdateInfo;
import com.example.fristtest.R;
import com.swetake.util.Qrcode;
import com.view.OneButtonDialog;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;

public class CommonUtil {
	
	private static AlertDialog sAlartDialog;

	private static int sNotificationId = -1;
	private static String sApkFilePath;
	private static NotificationManager sUpdateNotificationManager;

	private static void installApk(final Context context,boolean showProgressDialog){
		startInstallApkActivity(context);
		sUpdateNotificationManager.cancel(sNotificationId);
		sNotificationId = -1;
	}
	
	private static void startInstallApkActivity(final Context context){
		Uri uri = Uri.fromFile(new File(sApkFilePath));
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
		installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(installIntent);
	}
	/**
	 * ���ת��Ϊȫ��
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
	/**
	 * �ж��Ƿ��������ַ��ʽ
	 * 
	 * @param address
	 * @return
	 */
	public static boolean isEmail(String address) {
		return address.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
	}
	
	/**
	 * ȡ����ʾALERT�Ի���
	 * 
	 */
	public static void dismissAlert() {
		if (sAlartDialog != null && sAlartDialog.isShowing())
			sAlartDialog.dismiss();
	}
	
	/**
	 * չ���б�,����scrollview��livtview�ĳ�ͻ
	 * @param adapter
	 * @param listview
	 */
	public static void expandListView(BaseAdapter adapter ,ListView listView){
		// ͳ�Ƹ߶�
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++){// listAdapter.getCount()�������������Ŀ
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);// ��������View �Ŀ��
			totalHeight += listItem.getMeasuredHeight(); // ͳ������������ܸ߶�
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		// listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�
		// params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�
		params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
		listView.setLayoutParams(params);
	}
	
	
	/**
	 * �ƶ����ݿ����͹ر�
	 * 
	 * @param context
	 * @param enabled
	 */
	public static void setMobileDataStatus(Context context, boolean enabled) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
				// ConnectivityManager��
				Class<?> conMgrClass = null;
				// ConnectivityManager���е��ֶ�
				Field iConMgrField = null;
				// IConnectivityManager�������
				Object iConMgr = null;
				// IConnectivityManager��
				Class<?> iConMgrClass = null;
				// setMobileDataEnabled����
				Method setMobileDataEnabledMethod = null;
				try {
					// ȡ��ConnectivityManager��
					conMgrClass = Class.forName(conMgr.getClass().getName());
					// ȡ��ConnectivityManager���еĶ���Mservice
					iConMgrField = conMgrClass.getDeclaredField("mService");
					// ����mService�ɷ���
					iConMgrField.setAccessible(true);
					// ȡ��mService��ʵ������IConnectivityManager
					iConMgr = iConMgrField.get(conMgr);
					// ȡ��IConnectivityManager��
					iConMgrClass = Class.forName(iConMgr.getClass().getName());

					// ȡ��IConnectivityManager���е�setMobileDataEnabled(boolean)����
					setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

					// ����setMobileDataEnabled�����Ƿ�ɷ���
					setMobileDataEnabledMethod.setAccessible(true);
					// ����setMobileDataEnabled����
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
	 * ��ȡ�ƶ����ݿ���״̬
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
	 * @return ��õ�ǰѡ�е���ţ�ûѡ�У�����-1
	 */
	public static int getCheckedPositionInRadioGroup(RadioGroup radiogroup) {
		int id = radiogroup.getCheckedRadioButtonId();
		if (id != -1)
			return radiogroup.indexOfChild(radiogroup.findViewById(id));
		return -1;
	}
	
	/**
	 * ȥ���ַ����з����ֵ��ַ�
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
	
	/**
	 * ����תΪ������
	 * @param str
	 * @return
	 */
	public static float parse2Float(String str) {
		float result = 0;
		String temp = wipeOffUnDigitChar(str);

		if (!TextUtils.isEmpty(temp))
			temp = wipeOffUnDigitChar(temp);
		if (!TextUtils.isEmpty(temp))
			result = Float.parseFloat(temp);

		return result;
	}

	/**
	 * ����תΪ˫������
	 * @param str
	 * @return
	 */
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
	 * ת��Ϊ���֣�ת��ʧ�ܣ�ΪĬ����
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
	 * ת��Ϊ���֣�ת��ʧ�ܣ�ΪĬ����
	 * 
	 * @param str
	 * @param defaultInt
	 * @return
	 */
	public static Long parse2Long(String str, long defaultInt) {
		if (str != null)
			try {
				defaultInt = Long.parseLong(str);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		return defaultInt;
	}

	/**
	 * ��ȡǰN+1���µ���ʾ��ʽ�ַ���
	 * 
	 * @param pastNum
	 *            ǰ�����¡�0Ϊǰһ����
	 * @return ��ʾ��ʽΪyyyyMM
	 */
	public static String getMonthPast(int pastNum) {
		return getMonthPast("%04d%02d", pastNum);
	}

	/**
	 * @param format
	 *            ����������������
	 * @param pastNum
	 * @return ��ȡǰN+1���µ���ʾ��ʽ�ַ���
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
	 * @return �ж��Ƿ���������ַ�
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
	 * �ж��Ƿ�������
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
	 * ȥ���绰�����в���Ҫ���ַ�
	 * 
	 * @param number
	 * @return
	 */
	public static final String removeNoNecessaryWordsFromPhoneNumber(String number) {
		// �绰���룬ȥ������Ҫ���ַ�
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

	public static boolean isOpenNetwork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
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
	 * ���ܣ� ��ֹ����˫�ε��
	 * 
	 * @return �����Ƿ�ɽ��ж��β���
	 * 
	 *         ʹ�÷�����public void onClick(View v) { if (Utils.isFastDoubleClick())
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
			// ��ȡCPU��Ϣ
			Process pp = Runtime.getRuntime().exec("cat /procuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			// ����CPU���к�
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					// ���ҵ����к�������
					if (str.indexOf("Serial") > -1) {
						// ��ȡ���к�
						strCPU = str.substring(str.indexOf(":") + 1, str.length());
						// ȥ�ո�
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					// �ļ���β
					break;
				}
			}
		} catch (IOException ex) {
			// ����Ĭ��ֵ
			ex.printStackTrace();
		}
		return cpuAddress;
	}

	public static String getAppsign(Context context) {
		return getSingInfo3(context);
	}
	
	/**
	 * ��ȡAPPǩ��2
	 * @param context
	 * @return
	 */
	private static String getAppSign1(Context context) {
		StringBuilder builder = new StringBuilder();
		try {
			/** ͨ�������������ָ����������ǩ���İ���Ϣ **/
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			/******* ͨ�����صİ���Ϣ���ǩ������ *******/
			Signature[] signatures = packageInfo.signatures;
			/******* ѭ������ǩ������ƴ��Ӧ��ǩ�� *******/
			for (Signature signature : signatures) {
				builder.append(signature.toCharsString());
			}
			/************** �õ�Ӧ��ǩ�� **************/
			return builder.toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "δ��ȡ��MD5";
	}

	/**
	 * ��ȡAPPǩ��2
	 * @param context
	 * @return
	 */
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

	/**
	 * ʮ������ת��
	 * @param b
	 * @return
	 */
	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	
	/**
	 * ��ȡǩ��3
	 * @param context
	 * @return
	 */
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
	
	/**
	 * ��ȡǩ��
	 * @param signature
	 * @return
	 */
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
	 * ��ȡAPK�汾��
	 * @param context
	 * @return
	 */
	public static int getApkVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * ��ȡAPK��������
	 * @param context
	 * @return
	 */
	public static String getApkVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "0.0";
	}
	
	/**
	 * �Ƿ�װĳӦ��
	 * @param context
	 * @param string Ӧ������
	 * @return
	 */
	public static boolean isApkInstalled(Context context, String string) {
		PackageManager pm = context.getPackageManager();// ͨ�����application�õ�PackageManager
		// ����launch��ע���˵�Ӧ�� pm.getInstalledApplications ���Է������е��б�
		Intent intent = pm.getLaunchIntentForPackage(string);
		if (intent == null) {// ���intent���Ϊ�գ��Ǿ��Ǵ���û���ҵ�ָ�������ĳ���
			return false;
		}
		return true;
	}
	
	/**
	 * �Ƿ��ƶ�����
	 * @param number
	 * @return
	 */
	public static boolean isMobilePhone(String number) {
		return number.matches("^((13[4-9])|(147)|(15[^4,^5,\\D])|(178)|(18[2-4,7-8]))\\d{8}$");
	}

	/**
	 * �Ƿ��ֻ�����
	 * @param number
	 * @return
	 */
	public static boolean isPhoneNumber(String number) {
		return number.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
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
			// �趨ͼ����ɫ > BLACK
			paint.setColor(0xff000000);
			// ����ƫ���� �����ÿ��ܵ��½�������
			int pixoff = 0;
			// ������� > ��ά��
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

			// // ʵ����һ��Image����
			canvas.drawBitmap(icon, bimtapSize - icon.getWidth() >> 1, bimtapSize - icon.getHeight() >> 1, paint);
			// // ���ɶ�ά��QRCodeͼƬ
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resizedBitmap;
	}
	
	public static void showAlert(Context context, String title, String string, String clickText, OnClickListener onClickListener) {
		showAlert(context, title, string, clickText, true, onClickListener);
	}

	public static void showAlert(Context context, String title, String string, String clickText, boolean isCancelable, OnClickListener onClickListener) {
		// new
		// AlertDialog.Builder(context).setTitle(title).setCancelable(isCancelable).setMessage(string).setPositiveButton(clickText,
		// onClickListener).create()
		// .show();
		OneButtonDialog dialog = new OneButtonDialog(context, string, onClickListener, clickText);
		dialog.setTitle(title);
		dialog.setCancelable(isCancelable);
		dialog.show();

	}

	/**
	 * �˳���ǰANDROID�ͻ���
	 * @param context
	 */
	public static void exit(Context context) {
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * @param context
	 * @param updateInfo
	 * @return ������Ϣ����
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

		CharSequence message = Html.fromHtml(String.format("<b>�����°汾:v" + updateLatestVersionName + "<\b>")) + "\n\n" + "��������:\n" + des;
		return message;
	}

	public static String getWeekName(String week) {
		Pattern p = Pattern.compile("[^0-7]");
		int num = Integer.parseInt(p.matcher(week).replaceAll(""));
		final String[] weeknames = { "һ", "��", "��", "��", "��", "��", "��" };
		return "��" + weeknames[num - 1];
	}
	
}
