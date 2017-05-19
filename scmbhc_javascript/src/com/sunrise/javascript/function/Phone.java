/**
 *@(#)Phone.java        0.01 2012/01/13
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

package com.sunrise.javascript.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavaScriptInterface;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.utils.CommonUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 处理语音电话,实例被绑定到javascript上。
 * 
 * @version 0.01 January 13 2012
 * @author LIU WEI
 */

public class Phone implements JavaScriptInterface, Serializable {
	private static final long serialVersionUID = 4969491162712398126L;

	public static final int REQUEST_CONTACT = (int) serialVersionUID;

	private Context mContext;
	private JavascriptHandler mJavascriptHandler;

	private String mKey;

	public Phone(Context mContext, JavascriptHandler handler) {
		super();
		this.mContext = mContext;
		mJavascriptHandler = handler;
	}

	/**
	 * 拨打语音电话
	 * 
	 * @param phoneNumber
	 *            电话号码
	 * @throws IllegalArgumentException
	 *             phoneNumber为空，或不为数字
	 */
	public void voiceCall(final String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber 为空");
		}
		if (!CommonUtils.isNumber(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber 不是数字");
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				callVoice(mContext, phoneNumber);
			}
		}).start();
	}

	/**
	 * 发送短信
	 * 
	 * @param phoneNumber
	 *            电话号码
	 * @param content
	 *            短信内容
	 * @throws IllegalArgumentException
	 *             phoneNumber电话号码为空，不是数字
	 */
	public void sendMessage(final String phoneNumber, final String content) {
		if (TextUtils.isEmpty(phoneNumber))
			throw new IllegalArgumentException("phoneNumber 为空");
		if (!CommonUtils.isNumber(phoneNumber))
			throw new IllegalArgumentException("phoneNumber 不是数字");
		if (TextUtils.isEmpty(content))
			throw new IllegalArgumentException("content 短信内容为空");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (JavaScriptConfig.sDebug) {
					Log.d(JavaScriptConfig.TAG, "发送短信");
					Log.d(JavaScriptConfig.TAG, "phoneNumber:+" + phoneNumber);
					Log.d(JavaScriptConfig.TAG, "content:+" + content);
				}
				sendSMS(mContext, mJavascriptHandler, phoneNumber, content);
			}
		}).start();
	}

	/**
	 * 调用系统界面发送短信
	 * 
	 * @param phoneNumber
	 *            电话号码
	 * @throws IllegalArgumentException
	 *             phoneNumber电话号码为空，不是数字
	 */
	public void sendMessage(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber))
			throw new IllegalArgumentException("phoneNumber 为空");
		if (!CommonUtils.isNumber(phoneNumber))
			throw new IllegalArgumentException("phoneNumber 不是数字");
		Uri smsToUri = Uri.parse("smsto:" + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		mContext.startActivity(intent);
	}

	public void getPhoneFromContacts(String key) {
		this.mKey = key;
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setData(ContactsContract.Contacts.CONTENT_URI);
		((Activity) mContext).startActivityForResult(intent, REQUEST_CONTACT);
	}

	/**
	 * 拨打语音电话
	 * 
	 * @param phoneNumber
	 *            电话号码
	 * @throws IllegalArgumentException
	 *             phoneNumber为空，或不为数字
	 */
	public static void callVoice(Context context, String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber 为空");
		}
		if (!CommonUtils.isNumber(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber 不是数字");
		}
		String phoneuri = "tel:" + phoneNumber;
		Intent in = new Intent(Intent.ACTION_CALL, Uri.parse(phoneuri));
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(in);
	}

	/* 用于监听短信发送结果的广播 */
	static String SEND_SMS_ACTION = "SENT_SMS_ACTION";

	/**
	 * 发送短信
	 * 
	 * @param context
	 *            上下文
	 * @param phoneNumber
	 *            电话号码
	 * @param content
	 *            短信内容
	 * @param webView
	 *            显示当前HTML页面的view
	 */
	public static void sendSMS(Context context, final Handler handler, String phoneNumber, String content) {
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<PendingIntent> sendIntents = new ArrayList<PendingIntent>();
		PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(SEND_SMS_ACTION), 0);
		sendIntents.add(sentIntent);
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean result = false;
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					result = true;
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					break;
				}

				ArrayList<Object> params = new ArrayList<Object>();
				params.add(JavaScriptConfig.GET_USER_INFO_FUNCTION_CALL_BACK_NAME);
				params.add(new String[] { String.valueOf(result) });

				Message msg = handler.obtainMessage(0, params);
				handler.sendMessage(msg);
				context.unregisterReceiver(this);
			}

		}, new IntentFilter(SEND_SMS_ACTION));
		ArrayList<String> msgs = smsManager.divideMessage(content);
		smsManager.sendMultipartTextMessage(phoneNumber, null, msgs, sendIntents, null);
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_CONTACT)
			return false;

		if (resultCode != Activity.RESULT_OK || data == null) {
			callback(false, null, null);
			return true;
		}

		Uri result = data.getData();
		String contactId = result.getLastPathSegment();

		// 显示
		ContentResolver resolver = mContext.getContentResolver();
		String[] PHONES_PROJECTION = new String[] { android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER };
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION,
				android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);

		if (phoneCursor != null && phoneCursor.moveToFirst()) {
			// 得到手机号码
			String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
			String name = phoneCursor.getString(phoneCursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			Pattern pattern = Pattern.compile("[^0-9]");
			Matcher matcher = pattern.matcher(phoneNumber.replace("+86", ""));
			phoneNumber = matcher.replaceAll("");
			callback(true, phoneNumber, name);
			phoneCursor.close();
		}

		return true;
	}

	private void callback(boolean result, String phoneNumber, String name) {
		String jsonStr = phoneNumber;
		try {
			JSONObject jsobj = new JSONObject();
			jsobj.put("result", result);
			if (!TextUtils.isEmpty(phoneNumber))
				jsobj.put("phoneNo", phoneNumber);
			if (!TextUtils.isEmpty(name))
				jsobj.put("name", name);
			jsonStr = jsobj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ArrayList<Object> objs = new ArrayList<Object>();
		objs.add(JavaScriptConfig.JAVASCRIPT_API_CALLBACK_NAME);

		if (TextUtils.isEmpty(mKey)) {
			objs.add(new String[] { jsonStr, "getPhoneFromContacts" });
		} else {
			objs.add(new String[] { jsonStr, mKey });
		}
		mJavascriptHandler.obtainMessage(0, objs).sendToTarget();
	}

}
