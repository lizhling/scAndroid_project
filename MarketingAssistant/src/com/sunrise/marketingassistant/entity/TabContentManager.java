package com.sunrise.marketingassistant.entity;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.App;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.cache.preference.Preferences;

/**
 * 页面管理
 * 
 * @author 珩
 * 
 */
public class TabContentManager implements ExtraKeyConstant {

	private static TabContentManager instance;
	private Context mContext;

	// private ArrayList<TabContent> mArrayTabContents = new
	// ArrayList<TabContent>();
	private HashMap<Integer, TabContent> mHashTabContents = new HashMap<Integer, TabContent>();

	public static final TabContentManager getInstance(Context context) {
		if (instance == null) {
			if (context == null)
				return null;
			instance = new TabContentManager(context);
		}
		return instance;
	}

	private TabContentManager(Context context) {
		mContext = context;
		initContent(context);
	}

	// private TabContent getTabContent1(int id) {
	//
	// for (TabContent temp : mArrayTabContents) {
	// if (temp.getId() == id)
	// return temp;
	// }
	// return null;
	// }

	public TabContent getTabContent(int id) {
		if (mHashTabContents.isEmpty())
			initContent(mContext);
		return mHashTabContents.get(id);
	}

	// public TabContent getTabContent(int id) {
	//
	// String jsonStr =
	// Preferences.getInstance(mContext).getString(UpdateInfo.KEY_TAB_INFOS,
	// null);
	// if (jsonStr == null)
	// jsonStr = FileUtils.getTextFromAssets(mContext, "tabInfo.txt", "utf-8");
	// try {
	// JSONArray jsarray = new JSONObject(jsonStr).getJSONArray("datas");
	//
	// for (int i = 0; i < jsarray.length(); ++i) {
	// TabContent temp = JsonUtils.parseJsonStrToObject(jsarray.getString(i),
	// TabContent.class);
	// if (temp.getId() == id)
	// return temp;
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	private void initContent(Context context) {
		final String FILE_NAME_TABINFO = "tabInfo.txt";
		String jsonStr = null;
		if (App.isTest) {
			try {
				if (FileUtils.fileIsExist(APP_SD_PATH_NAME, FILE_NAME_TABINFO)) {
					jsonStr = FileUtils.readToStringFormFile(FileUtils.getAbsPath(APP_SD_PATH_NAME, FILE_NAME_TABINFO));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			jsonStr = Preferences.getInstance(context).getString(UpdateInfo.KEY_TAB_INFOS, null);

		if (jsonStr == null)
			jsonStr = FileUtils.getTextFromAssets(context, FILE_NAME_TABINFO, "utf-8");

		try {
			JSONArray jsarray = new JSONObject(jsonStr).getJSONArray("datas");

			for (int i = 0; i < jsarray.length(); ++i) {
				TabContent temp = JsonUtils.parseJsonStrToObject(jsarray.getString(i), TabContent.class);
				if (i == 1) {
					Preferences.getInstance(context).setPhoneTime(temp.getLastModify());
				}

				// if (temp.getId() ==
				// ExtraKeyConstant.ID_TAB_ITEM_CHANNEL_TREE) {
				// // int index = temp.getZipContent().lastIndexOf('/');
				// temp.setZipContent("local://html/xdb-qds.zip");
				// }else if (temp.getId() ==
				// ExtraKeyConstant.ID_TAB_ITEM_CHANNEL_SCAN) {
				// int index = temp.getZipContent().lastIndexOf('/');
				// temp.setZipContent("local://html/xdb-xq.zip");
				// }

				mHashTabContents.put(temp.getId(), temp);
				// mArrayTabContents.add(temp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
