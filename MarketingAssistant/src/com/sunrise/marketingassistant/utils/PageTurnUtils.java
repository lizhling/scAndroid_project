package com.sunrise.marketingassistant.utils;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.sunrise.javascript.AddedJavascriptObject;
import com.sunrise.javascript.utils.CommonUtils;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.FriendsShareActivity;
import com.sunrise.marketingassistant.activity.HallDetailActivity;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.database.CmmaDBHelper;
import com.sunrise.marketingassistant.entity.CollectBranch;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.entity.TabContent;
import com.sunrise.marketingassistant.entity.TabContentManager;
import com.sunrise.marketingassistant.entity.UpdateInfo;
import com.sunrise.marketingassistant.fragment.MapLocationShowFragment;
import com.sunrise.marketingassistant.fragment.WebViewFragment2;

@SuppressLint("DefaultLocale")
public class PageTurnUtils extends AddedJavascriptObject implements ExtraKeyConstant {

	private Context context;
	private Handler mHandler;
	private String mInfoFromOutPage;
	private CmmaDBHelper cdbh;
	private String subAccount;

	public PageTurnUtils(WebView webview, String outPageInfo, Handler handler) {
		context = webview.getContext();
		mHandler = handler;
		mInfoFromOutPage = outPageInfo;
		subAccount = Preferences.getInstance(context).getSubAccount();
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
	@JavascriptInterface
	public void startShareFunction(String description, String content) {
		LogUtlis.i("启动分享页面", "startShareFunction(" + description + "," + content + ")");
		try {
			String Ddescription = description;// DesCrypUtil.DESDecrypt(description.trim());
			String Dcontent = content;// DesCrypUtil.DESDecrypt(content.trim());
			Intent intent = new Intent(context, FriendsShareActivity.class);
			intent.putExtra(Intent.EXTRA_TEMPLATE, Ddescription);
			intent.putExtra(Intent.EXTRA_TEXT, Dcontent);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtlis.e(Thread.currentThread().getStackTrace()[0].getMethodName(), e.getLocalizedMessage());
		}
	}

	@JavascriptInterface
	public void callBusinessWebPage(String url, String name, String lastModify, String outPageInfo) {
		LogUtlis.i("启动新的页面", "callBusinessWebPage(" + url + "," + name + "," + lastModify + "," + outPageInfo + ")");
		try {
			String Durl = url;// DesCrypUtil.DESDecrypt(url);
			String Dname = name;// DesCrypUtil.DESDecrypt(name);
			String DlastModify = lastModify;// DesCrypUtil.DESDecrypt(lastModify);

			Intent intent = SingleFragmentActivity.createIntent(context, WebViewFragment2.class, Durl, Dname, DlastModify, outPageInfo);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtlis.e(Thread.currentThread().getStackTrace()[0].getMethodName(), e.getLocalizedMessage());
		}
	}

	@JavascriptInterface
	public void callBusinessPage(String outPageInfo) {
		LogUtlis.i("查看渠道详情页面", "callBusinessWebPage(" + outPageInfo + ")");
		try {
			TabContent temp = TabContentManager.getInstance(context).getTabContent(ID_TAB_ITEM_CHANNEL_DETAIL);
			Intent intent = HallDetailActivity.createIntent(context, WebViewFragment2.class, temp.getZipContent(), temp.getTabName(), temp.getLastModify(),
					outPageInfo);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtlis.e(Thread.currentThread().getStackTrace()[0].getMethodName(), e.getLocalizedMessage());
		}
	}

	@JavascriptInterface
	public void callMapPage(String info) {
		LogUtlis.i("启动地图页面", "callMapPage(" + info + ")");

		if (TextUtils.isEmpty(info)) {
			LogUtlis.e("启动地图页面", "info = " + info + ", 终止启动callMapPage(" + info + ")方法");
			return;
			// info = FileUtils.getTextFromAssets(context, "info.txt", "utf-8");
		}

		MobileBusinessHall hall = null;
		try {
			hall = JsonUtils.parseJsonStrToObject(info, MobileBusinessHall.class);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtlis.e("启动地图页面", "终止启动,异常:" + e.getMessage());
			return;
		}

		if (hall == null) {
			LogUtlis.e("启动地图页面", "传入数据格式错误");
			return;
		}
		LogUtlis.i("启动地图页面", "hall: name= " + hall.getGROUP_NAME() + ",latitude=" + hall.getLATITUDE() + " ,longitude=" + hall.getLATITUDE() + ",gropid="
				+ hall.getGROUP_ID() + ")");
		if (hall == null || hall.getLATITUDE() == 0 || hall.getLATITUDE() == 0) {
			Toast.makeText(context, "调用js没有坐标", Toast.LENGTH_SHORT).show();
			return;
		} else if (hall.getGROUP_ID() == null) {
			Toast.makeText(context, "调用js没有groupID", Toast.LENGTH_SHORT).show();
			return;
		} else if (hall.getGROUP_NAME() == null) {
			Toast.makeText(context, "调用js没有渠道名称", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(context, SingleFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putBoolean(Intent.EXTRA_CC, true);
		ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
		array.add(hall);
		bundle.putParcelableArrayList(Intent.EXTRA_DATA_REMOVED, array);
		bundle.putInt(Intent.EXTRA_UID, 0);

		intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
		intent.putExtra(KEY_FRAGMENT, MapLocationShowFragment.class);
		context.startActivity(intent);
	}

	@JavascriptInterface
	public void getTabInfos(String key) {
		LogUtlis.i("获取tabInfos", "getTabInfos:" + key);
		String jsonStr = FileUtils.getTextFromAssets(context, "tabInfo.json", "utf-8");
		jsonStr = Preferences.getInstance(context).getString(UpdateInfo.KEY_TAB_INFOS, jsonStr);
		CommonUtils.sendResult(jsonStr, key, mHandler);
	}

	private CmmaDBHelper getDatabase() {
		if (cdbh == null)
			cdbh = new CmmaDBHelper(context);
		return cdbh;
	}

	/**
	 * 收藏网点
	 * 
	 * @param key
	 */
	@JavascriptInterface
	public void insertCollectBranch(String info, String key) {
		LogUtlis.i("收藏网点", "insertCollectBranch:" + info + "," + key);
		CollectBranch collect = null;
		try {
			collect = JsonUtils.parseJsonStrToObject(info, CollectBranch.class);
		} catch (Exception e) {
			e.printStackTrace();
			CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, -2, e.getMessage()), key, mHandler);
		}
		long result = getDatabase().insert(collect);
		if (result == -1)
			CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, result, "groupid has contained"), key, mHandler);
		else if (result == -2)
			CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, result, "no collect info"), key, mHandler);
		else
			CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, 0, "complete"), key, mHandler);
	}

	/**
	 * 查询收藏网
	 * 
	 * @param account
	 */
	@JavascriptInterface
	public void cancelCollectedBranchByGroupId(String groupId, String key) {
		int result = getDatabase().deleteByGroupId(subAccount, groupId);
		if (result >= 0)
			CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, 0, "delete complete"), key, mHandler);
		else
			CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, result, "no item groupId is " + groupId), key, mHandler);
	}

	/**
	 * 查询收藏网
	 * 
	 * @param account
	 */
	@JavascriptInterface
	public void getCollectedBranchByAccount(String key) {
		LogUtlis.i("查询收藏网", "getCollectedBranchByAccount:" + key);
		try {
			ArrayList<CollectBranch> array = getDatabase().queryCollectBranchByAccount(subAccount);
			if (array == null) {
				CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, -2, "no result"), key, mHandler);
				return;
			}
			String result = JsonUtils.writeObjectToJsonStr(array);
			CommonUtils.sendResult(createJsonResult(0, result), key, mHandler);
		} catch (Exception e) {
			e.printStackTrace();
			CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, -1, e.getMessage()), key, mHandler);
		}
	}

	/**
	 * 查询收藏网
	 * 
	 * @param account
	 */
	@JavascriptInterface
	public void getCollectedBranchByGroupId(String groupId, String key) {
		LogUtlis.i("查询收藏网", "getCollectedBranchByGroupId:" + groupId + "," + key);
		try {
			ArrayList<CollectBranch> array = getDatabase().queryCollectBranchByGroupId(subAccount, groupId);
			if (array == null) {
				CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, -2, "no result"), key, mHandler);
				return;
			}
			String result = JsonUtils.writeObjectToJsonStr(array);
			CommonUtils.sendResult(createJsonResult(0, result), key, mHandler);
		} catch (Exception e) {
			e.printStackTrace();
			CommonUtils.sendResult(String.format(FORMAT_JS_RETURN, -1, "异常" + e.getMessage()), key, mHandler);
		}
	}

	@Override
	public String getName() {
		return PageTurnUtils.class.getSimpleName();
	}

	@Override
	public String getHtmlRegist() {
		return context.getResources().getString(R.string.pageTurnHtmlRegist);
	}

	// function
	// startShareFunction(description,content){window.JavascriptObjcets.startShareFunction(description,content);}
	// function getTabInfos(key){window.JavascriptObjcets.getTabInfos(key);}
	// function
	// insertCollectBranch(info,key){window.JavascriptObjcets.insertCollectBranch(info,key);}
	// function
	// cancelCollectedBranchByGroupId(groupId,key){window.JavascriptObjcets.cancelCollectedBranchByGroupId(groupId,key);}
	// function
	// getCollectedBranchByAccount(key){window.JavascriptObjcets.getCollectedBranchByAccount(key);}
	// function
	// getCollectedBranchByGroupId(groupId,key){window.JavascriptObjcets.getCollectedBranchByGroupId(groupId,key);}
	// function
	// getInfoFromOutPage(key){window.JavascriptObjcets.getmInfoFromOutPage(key);}
	// function
	// callBusinessWebPage(url,name,lastModify,outPageInfo){window.JavascriptObjcets.callBusinessWebPage(url,name,lastModify,outPageInfo);
	@JavascriptInterface
	public void getInfoFromOutPage(String key) {
		LogUtlis.i("获取页面信息", "getInfoFromOutPage:" + key);
		String info = createJsonResult(mInfoFromOutPage == null ? -1 : 0, mInfoFromOutPage);
		CommonUtils.sendResult(info, key, mHandler);
	}

	private String createJsonResult(int resultCode, String resultMsg) {
		if (resultMsg == null)
			resultMsg = "";

		JSONObject json = new JSONObject();
		try {
			json.put("resultCode", resultCode);
			json.put("resultMsg", resultMsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	@JavascriptInterface
	public void getDistance(final double latitude, final double longitute, final String key) {
		LogUtlis.i("获取距离信息", "getInfoFromOutPage:(" + latitude + "," + longitute + "," + key + ")");

		final BaiduMapUtils baiduutils = new BaiduMapUtils(context);

		baiduutils.startLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation arg0) {
				baiduutils.stopLocationListener();
				LogUtlis.i("获取到当前坐标信息", "坐标:(" + arg0.getLatitude() + "," + arg0.getLongitude() + ")" + arg0.getCity());
				LatLng p1 = new LatLng(latitude, longitute);
				LatLng p2 = new LatLng(arg0.getLatitude(), arg0.getLongitude());
				String info = createJsonResult(0, String.valueOf(BaiduMapUtils.getDistance(p1, p2)));
				CommonUtils.sendResult(info, key, mHandler);
			}
		});

	}
}
