package com.sunrise.scmbhc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.gson.JsonParseException;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.App.AppActionConstant;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.entity.OpenedBusinessList;
import com.sunrise.scmbhc.entity.PhoneCurInfo;
import com.sunrise.scmbhc.entity.PhoneFreeQuery;
import com.sunrise.scmbhc.entity.UserBaseInfo;
import com.sunrise.scmbhc.entity.PhoneFreeQuery.EmumState;
import com.sunrise.scmbhc.entity.UseCondition;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class UserInfoControler extends Observable {

	private static volatile UserInfoControler sInstance;

	private PhoneFreeQuery mPhoneFreeQuery;

	private UserBaseInfo mUserBaseInfo;

	private OpenedBusinessList mOpenedBusinessList;

	private PhoneCurInfo mPhoneCurrMsg;

	public static UserInfoControler getInstance() {
		if (sInstance == null)
			synchronized (UserInfoControler.class) {
				if (sInstance == null)
					sInstance = new UserInfoControler();
			}
		return sInstance;
	}

	private UserInfoControler() {
		sInstance = this;
	}

	/**
	 * 用在登录界面，判断密码是否保存
	 * 
	 * @return 是否存储了密码
	 */
	public boolean isSavePassword() {
		String password = App.sSettingsPreferences.getPassword();
		return !TextUtils.isEmpty(password);
	}

	/**
	 * @param phoneNumber
	 *            存储帐号
	 * @param password
	 *            密码
	 */
	public void loginIn(String phoneNumber, String password, String token) {
		addLoginUserName(phoneNumber, password);
		App.sSettingsPreferences.saveUserInfo(phoneNumber, password);
		App.sSettingsPreferences.setToken(token);
		// App.sSettingsPreferences.setLoginIn(true);
	}

	/**
	 * @return 是否自动登录
	 */
	public boolean isAutoLogin() {
		return App.sSettingsPreferences.isAutoLogin();
	}

	/**
	 * @return 判断是否显示指引页面
	 */
	public boolean isNeedGuide() {
		return App.sSettingsPreferences.isNeedGuide();
	}

	/**
	 * @return 判断是否显示指引页面
	 */
	public void setNeedGuide(boolean is) {
		App.sSettingsPreferences.setNeedGuide(is);
	}

	/**
	 * @param is
	 * @return
	 */
	public void setAutoLogin(boolean is) {
		App.sSettingsPreferences.saveAutoLogin(is);
	}

	public void setReservationHallId(String userName, String id) {
		App.sSettingsPreferences.putString(userName, id);
	}

	public String getReservationHallId(String userName) {
		return App.sSettingsPreferences.getString(userName, null);
	}

	/**
	 * @return 获取套餐余量
	 */
	public PhoneFreeQuery getPhoneFreeQuery() {

		if (mPhoneFreeQuery == null) {
			String query = App.sSettingsPreferences.getPhoneFreeQuery();
			if (!TextUtils.isEmpty(query))
				mPhoneFreeQuery = (PhoneFreeQuery) JsonUtils.parseJsonStrToObject(query, PhoneFreeQuery.class);
			notifyObservers();
		}
		return mPhoneFreeQuery == null ? new PhoneFreeQuery(EmumState.NO_INIT) : mPhoneFreeQuery;
	}

	/**
	 * @return 本月通讯费
	 */
	public String getConsumptionThisMonth() {
		return getPhoneCurMsg().getCoat() + "元";
	}

	/**
	 * @return 当前余额
	 */
	public String getCurBalance() {
		return getPhoneCurMsg().getBalance() + "元";
	}

	/**
	 * @param info
	 *            服务器获取的原始字符串
	 */
	public void setPhoneFreeQuery(String info) {
		setPhoneFreeQuery(PhoneFreeQuery.craeteByAnalysisMessage(info));
	}

	/**
	 * @param query
	 *            设置新的套餐余量信息并存储到本地。
	 */
	public void setPhoneFreeQuery(PhoneFreeQuery query) {

		if (query == null)
			App.sSettingsPreferences.savePhoneFreeQuery(null);
		else
			App.sSettingsPreferences.savePhoneFreeQuery(query.toString());

		mPhoneFreeQuery = query;

		if (App.sContext != null) {
			Intent intent = new Intent(AppActionConstant.ACTION_REFRESH);
			intent.putExtra(ExtraKeyConstant.KEY_CASE, AppActionConstant.STATE_COMPLETE_PHONE_FREE_QUERY);
			App.sContext.sendBroadcast(intent);
		}
		
		notifyChanged();
	}

	/**
	 * 设置新的用户已办业务列表信息
	 * 
	 * @param list
	 */
	public void setAdditionalTariffInfo(OpenedBusinessList list) {
		App.sSettingsPreferences.saveAdditionalTariffInfo(list == null ? null : list.toString());
		mOpenedBusinessList = list;
		
		if (App.sContext != null) {
			Intent intent = new Intent(AppActionConstant.ACTION_REFRESH);
			intent.putExtra(ExtraKeyConstant.KEY_CASE, AppActionConstant.STATE_COMPLETE_ADDITIONAL_TRAFFIC_INFO);
			App.sContext.sendBroadcast(intent);
		}
		
		notifyChanged();
	}

	/**
	 * @return 获取用户账单信息
	 */
	public PhoneCurInfo getPhoneCurMsg() {
		if (mPhoneCurrMsg == null) {
			String query = App.sSettingsPreferences.getPhoneCurrMsg();
			if (!TextUtils.isEmpty(query))
				mPhoneCurrMsg = JsonUtils.parseJsonStrToObject(query, PhoneCurInfo.class);
			notifyObservers();
		}
		return mPhoneCurrMsg == null ? new PhoneCurInfo() : mPhoneCurrMsg;
	}

	/**
	 * @return 获取用户已开通业务信息
	 */
	public OpenedBusinessList getOpenedBusinessList() {
		if (mOpenedBusinessList == null) {
			String query = App.sSettingsPreferences.getAdditionalTariffInfo();
			if (TextUtils.isEmpty(query))
				return null;

			mOpenedBusinessList = JsonUtils.parseJsonStrToObject(query, OpenedBusinessList.class);

			setChanged();
			notifyObservers();
		}
		return mOpenedBusinessList;
	}

	// public void setCurrentPhoneInfo(String query) {
	// mPhoneCurrMsg = new PhoneCurInfo(query);
	// App.sSettingsPreferences.savePhoneCurrMsg(mPhoneCurrMsg.toString());
	// setChanged();
	// notifyObservers();
	// }

	public void setCurrentPhoneInfo(PhoneCurInfo query) {
		mPhoneCurrMsg = query;
		App.sSettingsPreferences.savePhoneCurrMsg(mPhoneCurrMsg.toString());
		
		if (App.sContext != null) {
			Intent intent = new Intent(AppActionConstant.ACTION_REFRESH);
			intent.putExtra(ExtraKeyConstant.KEY_CASE, AppActionConstant.STATE_COMPLETE_PHONE_CURRENT_MSG);
			App.sContext.sendBroadcast(intent);
		}
		
	}

	public String getUserName() {
		return App.sSettingsPreferences.getUserName();
	}

	/**
	 * @return 获取用户真实姓名
	 */
	public String getUserPersonName() {
		return getUserBaseInfo().getCONTACT_NAME();
	}

	/**
	 * @return 获取品牌号码
	 */
	public String getBrandName() {
		return getUserBaseInfo().getBRAND_NAME();
	}

	/**
	 * @return 主套餐名称
	 */
	public String getMainMode() {
		return getUserBaseInfo().getMAIN_MODE();
	}

	/**
	 * @return 归属地
	 */
	public String getCity() {
		return getUserBaseInfo().getREGION_NAME();
	}

	/**
	 * @return 用户信用等级
	 */
	public String getCreditClass() {
		return getUserBaseInfo().getCREDIT_CLASS();
	}

	/**
	 * @return 用户基本信息对象
	 */
	public UserBaseInfo getUserBaseInfo() {

		if (mUserBaseInfo == null) {

			String str = App.sSettingsPreferences.getUserBaseInfo();
			if (!TextUtils.isEmpty(str)) {
				mUserBaseInfo = JsonUtils.parseJsonStrToObject(str, UserBaseInfo.class);
			}
		}

		return mUserBaseInfo == null ? new UserBaseInfo() : mUserBaseInfo;
	}

	public String getPassword() {
		return App.sSettingsPreferences.getPassword();
	}

	/**
	 * @return 判断用户是否已经登录
	 */
	public boolean checkUserLoginIn() {
		return App.sSettingsPreferences.getToken() != null;
		// return App.sSettingsPreferences.isLoginIn();
	}

	/**
	 * 清除用户信息
	 */
	public void clean() {
		mPhoneFreeQuery = null;

		mPhoneCurrMsg = null;

		mOpenedBusinessList = null;

		App.sSettingsPreferences.clearUserInfo();

		clearChanged();
	}

	/**
	 * @param m_credits
	 *            积分/M值
	 */
	public void setMCredits(String m_credits) {
		App.sSettingsPreferences.saveMCredits(m_credits);

		if (App.sContext != null) {
			Intent intent = new Intent(AppActionConstant.ACTION_REFRESH);
			intent.putExtra(ExtraKeyConstant.KEY_CASE, AppActionConstant.STATE_COMPLETE_GET_CREDITS);
			App.sContext.sendBroadcast(intent);
		}

		notifyChanged();
	}

	/**
	 * @return 积分/M值 -1读取失败；-2未开通
	 */
	public String getMCredits() {
		return App.sSettingsPreferences.getMCredits();
	}

	/**
	 * 登出
	 */
	public void loginOut() {
		mPhoneFreeQuery = null;

		mUserBaseInfo = null;

		mOpenedBusinessList = null;

		mPhoneCurrMsg = null;

		App.sSettingsPreferences.cleanUserOtherData();
		if (!isSavePassword()) {
			String name = App.sSettingsPreferences.getUserName();
			App.sSettingsPreferences.saveUserInfo(name, null);
		}

		if (App.sContext != null)
			App.sContext.sendBroadcast(new Intent(AppActionConstant.ACTION_REFRESH));

		notifyChanged();
	}

	/**
	 * @param array
	 * @return 获取流量使用状况[总量，使用量]
	 */
	public UseCondition getConditionFlow(ArrayList<String> array) {
		// UseCondition newCondition1 =
		// getPhoneCurMsg().getConditionFlow(array);
		// UseCondition newCondition2 =
		// getPhoneFreeQuery().getConditionFlow(array);
		//
		// newCondition1.addCondition(newCondition2);
		// return newCondition1;
//		if(getPhoneCurMsg()!=null&&getPhoneCurMsg().getFlowAllCondition()!=null)
//			return getPhoneCurMsg().getFlowAllCondition();
		return getPhoneFreeQuery().getConditionFlow(array);
	}

	/**
	 * @return 获取通话使用状况[总量，使用量]
	 */
	public UseCondition getConditionCall() {
		UseCondition newCondition1 = getPhoneCurMsg().getConditionCall();
		// UseCondition newCondition2 =
		// getPhoneFreeQuery().getConditionCall(null);
		// newCondition1.addCondition(newCondition2);
		return newCondition1;
	}

	/**
	 * @return 获取短信使用状况[总量，使用量]
	 */
	public UseCondition getConditionSms() {
		UseCondition newCondition1 = getPhoneCurMsg().getConditionSMS();
		// UseCondition newCondition2 =
		// getPhoneFreeQuery().getConditionSMS(null);
		// newCondition1.addCondition(newCondition2);
		return newCondition1;
	}

	public void setWidgetPhoneNumber(String number) {
		App.sSettingsPreferences.setWidgetPhoneNumber(number);
	}

	public String getWidgetPhoneNumber() {
		return App.sSettingsPreferences.getWidgetPhoneNumber();
	}

	public String getAuthorKey() {
		return App.sSettingsPreferences.getToken();
	}

	public void setUserBaseInfo(String info) {
		try {
			mUserBaseInfo = JsonUtils.parseJsonStrToObject(info, UserBaseInfo.class);
		} catch (JsonParseException e) {
			mUserBaseInfo = JsonUtils.parseJsonStrToObject(info, UserBaseInfo.UserBaseAdapter.class);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(getClass().getName(), "info = " + info);
		}

		if (mUserBaseInfo == null)
			return;

		App.sSettingsPreferences.saveUserBaseInfo(mUserBaseInfo.toString());

		if (App.sContext != null) {
			Intent intent = new Intent(AppActionConstant.ACTION_REFRESH);
			intent.putExtra(ExtraKeyConstant.KEY_CASE, AppActionConstant.STATE_COMPLETE_GET_USER_BASE_INFO);
			App.sContext.sendBroadcast(intent);
		}
		
		setChanged();
		notifyObservers();
	}

	/**
	 * @return 获取登陆过的用户信息
	 */
	public ArrayList<HashMap<String, String>> getUserNames() {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		String tempNames = App.sSettingsPreferences.getLoginNames();
		if (!TextUtils.isEmpty(tempNames)) {
			try {
				JSONArray array = new JSONArray(tempNames);

				for (int i = 0; i < array.length(); ++i) {
					JSONObject jsobj = array.getJSONObject(i);
					HashMap<String, String> hash = new HashMap<String, String>();

					hash.put(ExtraKeyConstant.KEY_PHONE_NUMBER, jsobj.getString(ExtraKeyConstant.KEY_PHONE_NUMBER));
					if (jsobj.has(ExtraKeyConstant.KEY_PASSWORD))
						hash.put(ExtraKeyConstant.KEY_PASSWORD, jsobj.getString(ExtraKeyConstant.KEY_PASSWORD));
					result.add(hash);
				}
			} catch (JSONException e) {
				App.sSettingsPreferences.setLoginNames(null);// 有问题就清空
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * 添加新的登录用户记录
	 * 
	 * @param name
	 * @param password
	 */
	public void addLoginUserName(String name, String password) {

		String tempNames = App.sSettingsPreferences.getLoginNames();

		JSONArray array = null;

		try {
			if (TextUtils.isEmpty(tempNames)) {// 没有记录，就直接存入
				array = new JSONArray();
				JSONObject jsobj = new JSONObject();
				jsobj.put(ExtraKeyConstant.KEY_PHONE_NUMBER, name);// 存入号码

				if (!TextUtils.isEmpty(password))// 如果有密码，存入密码
					jsobj.put(ExtraKeyConstant.KEY_PASSWORD, password);

				array.put(jsobj);
			}

			else {
				array = new JSONArray(tempNames);
				boolean isFindSameRecorde = false;
				for (int i = 0; i < array.length(); ++i) {// 遍历已有数据
					JSONObject jsobj = array.getJSONObject(i);
					if (jsobj.getString(ExtraKeyConstant.KEY_PHONE_NUMBER).equals(name)) { // 如果号码相同
						isFindSameRecorde = true;
						if (jsobj.has(ExtraKeyConstant.KEY_PASSWORD)) {// 如果有密码
							String oldpsw = jsobj.getString(ExtraKeyConstant.KEY_PASSWORD);
							if (!oldpsw.equals(password)) {
								jsobj.remove(ExtraKeyConstant.KEY_PASSWORD);
								jsobj.put(ExtraKeyConstant.KEY_PASSWORD, password);
							}
						} else {// 没有密码
							if (!TextUtils.isEmpty(password))// 如果有密码，存入密码
								jsobj.put(ExtraKeyConstant.KEY_PASSWORD, password);
						}
					}
				}

				if (!isFindSameRecorde) {// 不存在相同号码的情况下，填充如新的号码信息
					JSONObject jsobj = new JSONObject();
					jsobj.put(ExtraKeyConstant.KEY_PHONE_NUMBER, name);

					if (!TextUtils.isEmpty(password))
						jsobj.put(ExtraKeyConstant.KEY_PASSWORD, password);

					array.put(jsobj);
				}
			}
			Log.d("preferences", array.toString());
			App.sSettingsPreferences.setLoginNames(array.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void notifyChanged() {
		setChanged();
		notifyObservers();
	}

}
