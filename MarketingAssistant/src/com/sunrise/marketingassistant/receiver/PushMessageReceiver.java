//package com.sunrise.marketingassistant.receiver;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import cn.jpush.android.api.JPushInterface;
//
///**
// * 自定义接收器
// * 
// * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
// */
//public class PushMessageReceiver extends BroadcastReceiver {
//	private static final String TAG = "JPush";
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		Bundle bundle = intent.getExtras();
//		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
//		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
//			// send the Registration Id to your server...
//
//		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//			Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//			processCustomMessage(context, bundle);
//
//		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
//			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//
//		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
//
//			// 打开自定义的Activity
//			Intent i = new Intent();
//			intent.setComponent(new ComponentName("com.asiainfo.cm10085", "com.asiainfo.cm10085.IdentityAuthenticationActivity"));
//			Bundle b = new Bundle();
//			bundle.putString("transactionID", "28020141202122623579450");// 全网唯一操作流水号
//			bundle.putString("billId", "18408230888");// 开户号码
//			bundle.putString("account", "ob0014");// 操作员工号
//			bundle.putString("channelCode", "05");// 渠道编号
//			bundle.putString("provinceCode", "280");// 省编号
//			bundle.putString(
//					"signature",
//					"Sl6ZTRkffpNpw+3KuVA5auMJisJKx0mNohP5fr/ZYuMCr1u6UtGAY4162St/CwAR7c3lGy1NqMEEI2FkVZuuTaaDEpo3eTJWeSOGa/6tXN1iVQo9ULW3E9Ka65/XpAz44mud5zOPmkL4LOah0M56Bk+BJer2sFZhXvRPbDGosc7Zc8B56V0SuRq4YhlFzTE3twDozqyWhRca4kmX9KuNEU4SoINHL+5s5nQoopoGP+j5pZXBC9q28qsth+e1UYNUdHqLmaBz/gdOcfNJoAY7t30LWcwp6IMgA+Qxm7clyGvhyFPJWzwxALexA0mf1MRRUUp/j4E/wXRrrPepg6rRPQ==");// 签名
//			intent.putExtras(b);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			context.startActivity(i);
//
//		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//			Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
//			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
//			// 打开一个网页等..
//
//		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//			Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
//		} else {
//			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
//		}
//	}
//
//	// 打印所有的 intent extra 数据
//	private static String printBundle(Bundle bundle) {
//		StringBuilder sb = new StringBuilder();
//		for (String key : bundle.keySet()) {
//			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
//			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
//				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
//			} else {
//				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
//			}
//		}
//		return sb.toString();
//	}
//
//	// send msg to MainActivity
//	private void processCustomMessage(Context context, Bundle bundle) {
//		// String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//		Intent msgIntent = new Intent(Intent.ACTION_VIEW);
//		try {
//			JSONObject extraJson = new JSONObject(extras);
//			msgIntent.putExtra(Intent.EXTRA_TEXT, extraJson.toString());
//		} catch (JSONException e) {
//
//		}
//		context.sendBroadcast(msgIntent);
//	}
//}
