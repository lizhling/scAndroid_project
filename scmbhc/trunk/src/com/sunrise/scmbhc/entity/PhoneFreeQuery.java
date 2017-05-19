package com.sunrise.scmbhc.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.javascript.utils.JsonUtils;

import android.text.TextUtils;

public class PhoneFreeQuery {
	public static final String UNKNOWN = "未知";

	String consumptionThisMonth = UNKNOWN;
	String curBalance = UNKNOWN;
	ArrayList<UseCondition> otherPackages;

	/**
	 * 状态
	 */
	private EmumState mState = EmumState.NORMAL;

	public static enum EmumState {
		NO_INIT(-1, UNKNOWN), CONNECT_ERROR(-2, "联网失败"), NORMAL(0, "normal"), ANALYSIS_FAILED(-3, "解析问题");
		private String name;
		private int code;

		private EmumState(int code, String name) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public int getCode() {
			return code;
		}
	}

	public PhoneFreeQuery() {

	}

	public PhoneFreeQuery(EmumState state) {
		mState = state;
	}

	private void init(String info) throws JSONException {
		if (TextUtils.isEmpty(info))
			return;

		JSONObject totleJson = new JSONObject(info).getJSONObject("RETURN");
		boolean isOld = totleJson.getString("RETURN_CHANNEL").toLowerCase().contains("old");
		boolean isArray = totleJson.getInt("IS_ARRAY") == 1;
		JSONArray array = totleJson.getJSONArray("RETURN_INFO");
		if (isArray)
			array = array.getJSONObject(0).getJSONArray("DETAIL_INFO");
		if (isOld)
			analysisOld(array);
		else
			analysisNew(array);
	}

	private void analysisNew(JSONArray jsonArray) throws JSONException {
		// TODO 解析新接口的返回数据
		final int length = jsonArray.length();
		for (int i = 0; i < length; ++i) {
			PackageFreeInfo otherPackage = JsonUtils.parseJsonStrToObject(jsonArray.getString(i), PackageFreeInfo.class);
			addOtherPackages(otherPackage);
		}
	}

	private void analysisOld(JSONArray jsonArray) throws JSONException {
		// TODO 解析旧接口的返回数据
		final int length = jsonArray.length();
		for (int i = 0; i < length; ++i) {
			PackageFreeInfo otherPackage = JsonUtils.parseJsonStrToObject(jsonArray.getString(i), PackageFreeInfo.class);
			addOtherPackages(otherPackage);
		}
	}

	/**
	 * @param key1
	 * @param key2
	 * @return 总量，使用量
	 */
	public UseCondition getConditionFlow(ArrayList<String> array) {
		return getCondition(array, UseCondition.TYPE_FLOW);
	}

	public UseCondition getConditionCall(ArrayList<String> array) {
		return getCondition(array, UseCondition.TYPE_CALL);
	}

	public UseCondition getConditionSms(ArrayList<String> array) {
		return getCondition(array, UseCondition.TYPE_SMS);
	}

	public ArrayList<UseCondition> getFlowPackages() {

		ArrayList<UseCondition> result = new ArrayList<UseCondition>();

		if (otherPackages != null)
			for (UseCondition condition : otherPackages) {

				if (condition.isMemberUse() || condition.isWlanGprs())
					continue;

				if (condition.isFlow())
					result.add(condition);
			}

		return result;
	}

	/**
	 * @param array
	 *            返回所有type类型数据
	 * @param type
	 *            类型
	 * @return 总量，使用量
	 */
	public UseCondition getCondition(ArrayList<String> array, byte type) {

		if (mState != EmumState.NORMAL) {
			UseCondition result = new UseCondition();
			result.setTotle(mState.getCode());
			result.setUsed(mState.getCode());
			result.setSurplus(mState.getCode());
			return result;
		}

		float usedSum = 0;
		float totleSum = 0;
		float remainSum = 0;

		final int length = getOtherPackages().size();

		for (int i = 0; i < length; ++i) {// 计算总和
			String packageName = getOtherPackages().get(i).getName();
			if (packageName != null) {
				if (getOtherPackages().get(i).isType(type)) {
					UseCondition info = getOtherPackages().get(i);

					// 不统计成员使用
					if (info.isMemberUse() || info.isWlanGprs())
						continue;

					double used = info.getUsed();
					double totle = info.getTotle();
					double remain = info.getSurplus();
					usedSum += used;
					totleSum += totle;
					remainSum += remain;

					if (array != null)
						array.add(packageName + "：" + info.getSurplusString() + " / " + info.getTotleString());
				}
			}
		}

		UseCondition condition = new UseCondition();
		condition.setType(type);
		if (totleSum != 0) {
			condition.setTotle(totleSum);
			condition.setUsed(usedSum);
			condition.setSurplus(remainSum);
		}

		return condition;
	}

	public static PhoneFreeQuery craeteByAnalysisMessage(String info) {

		if (TextUtils.isEmpty(info))
			return new PhoneFreeQuery(EmumState.CONNECT_ERROR);

		PhoneFreeQuery phoneFreeQuery = new PhoneFreeQuery();
		try {
			phoneFreeQuery.init(info);
		} catch (JSONException e) {
			e.printStackTrace();
			return new PhoneFreeQuery(EmumState.ANALYSIS_FAILED);
		}
		return phoneFreeQuery;
	}

	public String toString() {
		JSONObject jsob = new JSONObject();

		try {
			jsob.put("consumptionThisMonth", consumptionThisMonth);
			jsob.put("curBalance", curBalance);

			JSONArray jsarray = new JSONArray();
			for (UseCondition item : getOtherPackages())
				jsarray.put(item.toJsonObject());
			jsob.put("otherPackages", jsarray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsob.toString();
	}

	/**
	 * @return 本月通讯费
	 */
	public String getConsumptionThisMonth() {
		if (mState != EmumState.NORMAL) {
			return mState.getName();
		}
		return consumptionThisMonth;
	}

	/**
	 * @return 当前余额
	 */
	public String getCurBalance() {
		if (mState != EmumState.NORMAL) {
			return mState.getName();
		}
		return curBalance;
	}

	public ArrayList<UseCondition> getOtherPackages() {
		if (otherPackages == null)
			otherPackages = new ArrayList<UseCondition>();
		return otherPackages;
	}

	public void setConsumptionThisMonth(String consumptionThisMonth) {
		this.consumptionThisMonth = consumptionThisMonth;
	}

	public void setCurBalance(String curBalance) {
		this.curBalance = curBalance;
	}

	public void setOtherPackages(ArrayList<UseCondition> otherPackages) {
		this.otherPackages = otherPackages;
	}

	private void addOtherPackages(PackageFreeInfo otherPackage) {
		if (otherPackages == null)
			otherPackages = new ArrayList<UseCondition>();
		otherPackages.add(otherPackage.parseUseConditon());
	}

	class BaseItem {
		String key;
		String value;

		public BaseItem(String string) {

			if (TextUtils.isEmpty(string))
				return;

			int end = 0;
			int length = string.length();
			while (end != -1 && end < length) {
				char c = string.charAt(end);
				if (Character.isDigit(c))
					break;
				end++;
			}

			key = string.substring(0, end);
			value = string.substring(end);
		}
	}

	public static class PackageFreeInfo {
		private String ID_NO;
		private String PROD_PRC_NAME;
		private String REMAIN;
		private String TOTAL;
		private String USED;
		private String STMP;
		private byte TYPE;
		private String TYPE_NAME;

		public UseCondition parseUseConditon() {
			UseCondition condition = new UseCondition();
			condition.setName(PROD_PRC_NAME);
			condition.setUsed(CommUtil.parse2Float(USED));
			condition.setTotle(CommUtil.parse2Float(TOTAL));
			condition.setSurplus(CommUtil.parse2Float(REMAIN));
			condition.setType(TYPE);
			condition.setStmp(STMP);
			return condition;
		}

		public void setID_NO(String iD_NO) {
			ID_NO = iD_NO;
		}

		public void setPROD_PRC_NAME(String pROD_PRC_NAME) {
			PROD_PRC_NAME = pROD_PRC_NAME;
		}

		public void setTOTAL(String tOTAL) {
			TOTAL = tOTAL;
		}

		public void setUSED(String uSED) {
			USED = uSED;
		}
		
		public void setREMAIN(String rEMAIN) {
			REMAIN = rEMAIN;
		}

		public void setSTMP(String sTMP) {
			STMP = sTMP;
		}

		public int getTYPE() {
			return TYPE;
		}

		public void setTYPE(byte tYPE) {
			TYPE = tYPE;
		}

		public String getTYPE_NAME() {
			return TYPE_NAME;
		}

		public void setTYPE_NAME(String tYPE_NAME) {
			TYPE_NAME = tYPE_NAME;
		}

	}

	public static String getCodeMeans(double a) {
		for (EmumState e : EmumState.values()) {
			if (a == e.getCode())
				return e.getName();
		}

		return String.valueOf(a);
	}

	/**
	 * @return 是否正常状态
	 */
	public boolean isNormalState() {
		return mState == EmumState.NORMAL;
	}
}
