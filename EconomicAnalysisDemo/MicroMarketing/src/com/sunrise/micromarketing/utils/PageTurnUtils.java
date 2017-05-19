package com.sunrise.micromarketing.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.webkit.WebView;

import com.sunrise.javascript.AddedJavascriptObject;
import com.sunrise.javascript.utils.CommonUtils;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.micromarketing.ExtraKeyConstant;
import com.sunrise.micromarketing.R;
import com.sunrise.micromarketing.cache.preferences.Preferences;
import com.sunrise.micromarketing.entity.BusinessInfoItem;
import com.sunrise.micromarketing.entity.UpdateInfo;
import com.sunrise.micromarketing.ui.activity.DefaultBusinessDetailActivity;
import com.sunrise.micromarketing.ui.activity.FriendsShareActivity;
import com.sunrise.micromarketing.ui.activity.WebViewActivity;

public class PageTurnUtils extends AddedJavascriptObject {

	private Context context;
	private Handler mHandler;
	private String mName;
	private String mRegistInfo;

	public PageTurnUtils(WebView webview, String name, Handler handler) {
		context = webview.getContext();
		mHandler = handler;
		this.mName = name;
		mRegistInfo = context.getResources().getString(R.string.pageTurnHtmlRegist);
	}

	// public void startShareFunction(String string) {
	// try {
	// String result = DesCrypUtil.DESDecrypt(string.trim());
	// Intent intent = new Intent(context, FriendsShareActivity.class);
	// intent.putExtra(Intent.EXTRA_TEXT, result);
	// context.startActivity(intent);
	// } catch (Exception e) {
	// e.printStackTrace();
	// LogUtlis.e(Thread.currentThread().getStackTrace()[0].getMethodName(),
	// e.getLocalizedMessage());
	// }
	// }

	public void startShareFunction(String description, String content) {
		try {
			String Ddescription = description;//DesCrypUtil.DESDecrypt(description.trim());
			String Dcontent = content;//DesCrypUtil.DESDecrypt(content.trim());
			Intent intent = new Intent(context, FriendsShareActivity.class);
			intent.putExtra(Intent.EXTRA_TEMPLATE, Ddescription);
			intent.putExtra(Intent.EXTRA_TEXT, Dcontent);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtlis.e(Thread.currentThread().getStackTrace()[0].getMethodName(), e.getLocalizedMessage());
		}
	}

	public void callBusinessPage(String phoneno, String jsonData) {
		// jsonData =
		// "{\"busDesc\":\"\",\"busName\":\"4G流量月包10元\",\"prodCode\":\"ACAZ22363\",\"prodType\":\"2\",\"reWardRule\":\"\",\"spbizCode\":\"\",\"spid\":\"\",\"terminalDes\":\"\"}";
		try {
			String Dphoneno = phoneno;//DesCrypUtil.DESDecrypt(phoneno);
			String DjsonData = jsonData;//DesCrypUtil.DESDecrypt(jsonData.trim());

			BusinessInfoItem bii = JsonUtils.parseJsonStrToObject(DjsonData, BusinessInfoItem.class);

			Intent intent = new Intent(context, DefaultBusinessDetailActivity.class);
			intent.putExtra(Intent.EXTRA_PHONE_NUMBER, Dphoneno);
			intent.putExtra(Intent.EXTRA_SUBJECT, bii.turnBusinessInfoToBusinessMenu());
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtlis.e(Thread.currentThread().getStackTrace()[0].getMethodName(), e.getLocalizedMessage());
		}
	}

	// public void callBusinessWebPage(String url, String name) {
	// Intent intent = new Intent(context, WebViewActivity.class);
	// intent.putExtra(Intent.EXTRA_TEXT, url);
	// intent.putExtra(Intent.EXTRA_TITLE, name);
	// context.startActivity(intent);
	// }

	public void callBusinessWebPage(String url, String name, String lastModify) {
		try {
			String Durl = url;//DesCrypUtil.DESDecrypt(url);
			String Dname = name;//DesCrypUtil.DESDecrypt(name);
			String DlastModify = lastModify;//DesCrypUtil.DESDecrypt(lastModify);

			Intent intent = new Intent(context, WebViewActivity.class);
			intent.putExtra(Intent.EXTRA_TEXT, Durl);
			intent.putExtra(Intent.EXTRA_TITLE, Dname);
			intent.putExtra(ExtraKeyConstant.KEY_LAST_MODIFY, DlastModify);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtlis.e(Thread.currentThread().getStackTrace()[0].getMethodName(), e.getLocalizedMessage());
		}
	}

	public void getTabInfos(String key) {
		String jsonStr = FileUtils.getTextFromAssets(context, "tabInfo.json", "utf-8");
		jsonStr = Preferences.getInstance(context).getString(UpdateInfo.KEY_TAB_INFOS, jsonStr);
		CommonUtils.sendResult(jsonStr, key, mHandler);
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getHtmlRegist() {
		return mRegistInfo;
	}
}
