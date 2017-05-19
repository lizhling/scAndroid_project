package com.sunrise.scmbhc.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class UseCondition {
	private double totle;
	private double used;
	private double remain;

	private String name;

	private byte type;

	private String stmp;

	private static final String FORMAT_KB = "%.0fK";
	private static final String FORMAT_MB = "%.1fM";
	private static final String FORMAT_GB = "%.1fG";

	private static final String FORMAT_MIN = "%.0f分";
	private static final String FORMAT_TIAO = "%.0f条";
	private static final String FORMAT_NORMAL = "%.0f";

	static final byte TYPE_FLOW = 4;
	static final byte TYPE_CALL = 3;
	static final byte TYPE_MMS = 2;
	static final byte TYPE_WLAN = 5;
	static final byte TYPE_SMS = 1;
	static final byte TYPE_NORMAL = 0;

	private static final CharSequence STR_MEMBER_USE = "成员使用";
	private static final String NO_THIS_ACCOUNT_USE = "(非本号使用)";

	private static final String STR_WLAN = "WLAN";

	public UseCondition() {

	}

	public UseCondition(UseCondition condition) {
		if (condition != null) {
			setName(condition.getName());
			setTotle(condition.getTotle());
			setUsed(condition.getUsed());
		}
	}

	public double getTotle() {
		return totle;
	}

	public void setTotle(double totle) {
		this.totle = totle;
	}

	public double getUsed() {
		return used;
	}

	public void setUsed(double used) {
		this.used = used;
	}

	public void setSurplus(double remain) {
		this.remain = remain;
	}

	public double getSurplus() {
//		return getTotle() - getUsed();
		return remain;
	}

	public double getSurplusRate() {
		if (this.totle == 0)
			return 0;
		return getSurplus() / this.totle;
	}

	public void addCondition(UseCondition condition) {
		if (condition == null || condition.totle < 0)
			return;

		if (totle < 0 || used < 0) {
			totle = condition.totle;
			used = condition.used;
			remain = condition.remain;
		} else {
			totle += condition.totle;
			used += condition.used;
			remain += condition.remain;
		}
	}

	public JSONObject toJsonObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("totle", totle);
			json.put("used", used);
			json.put("name", name);
			json.put("type", type);
			json.put("stmp", stmp);
			json.put("remain", remain);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	// /**
	// * @return 使用量 流量格式
	// */
	// public String getFlowUsedString() {
	// if (used < 0)
	// return PhoneFreeQuery.getCodeMeans(used);
	// return getFlowString(used);
	// }
	//
	// /**
	// * @return 总量 流量格式
	// */
	// public String getFlowTotleString() {
	// if (totle < 0)
	// return PhoneFreeQuery.getCodeMeans(totle);
	// return getFlowString(totle);
	// }
	//
	// /**
	// * @return 余量 流量格式
	// */
	// public String getFlowSurplusString() {
	// if (totle - used < 0)
	// return PhoneFreeQuery.getCodeMeans(totle - used);
	// return getFlowString(totle - used);
	// }

	public static String getFlowString(double f) {
		if (f / 1024 > 999)
			return String.format(FORMAT_GB, f / (1 << 20));
		else if (f > 999)
			return String.format(FORMAT_MB, f / 1024);
		else if (f == 0)
			return String.valueOf(Math.round(f));
		else
			return String.format(FORMAT_KB, f);
	}

	/**
	 * @param f
	 * @return
	 */
	public static String getPercentString(double f, double total) {
		if (f > 0 && total > 0) {
			return String.format(FORMAT_NORMAL, f * 100 / total) + "%";
		} else if (total > 0) {
			return "0.0%";
		} else {
			return "NODATA";
		}
	}

	public String getName() {
		if (isMemberUse())
			return name + NO_THIS_ACCOUNT_USE;
		if (isWlanGprs())
			return name + "(" + stmp + ")";

		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toConditionString() {
		return name + ":" + getSurplusString() + " / " + getTotleString();
	}

	public String getSurplusString() {
		return getString(getSurplus());
	}

	public String getUsedString() {
		return getString(used);
	}

	public String getTotleString() {
		return getString(totle);
	}

	public String getString(double d) {
		String result = null;
		switch (type) {
		case TYPE_CALL:
			result = String.format(FORMAT_MIN, d);
			break;
		case TYPE_MMS:
			result = String.format(FORMAT_TIAO, d);
			break;
		case TYPE_SMS:
			result = String.format(FORMAT_TIAO, d);
			break;
		case TYPE_FLOW:
			result = getFlowString(d);
			break;
		case TYPE_WLAN:
			result = String.format(FORMAT_MIN, d);
			break;
		default:
			result = String.format(FORMAT_NORMAL, d);
			break;
		}

		return result;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public void setType(String typeName) {
		if (checkStrBeContainedInArray(typeName, KEYS_FLOW) && !checkStrBeContainedInArray(typeName, KEYS_WLAN)) {
			this.type = TYPE_FLOW;
		}

		else if (checkStrBeContainedInArray(typeName, KEYS_WLAN)) {
			this.type = TYPE_WLAN;
		}

		else if (checkStrBeContainedInArray(typeName, KEYS_MMS)) {
			this.type = TYPE_MMS;
		}

		else if (checkStrBeContainedInArray(typeName, KEYS_SMS)) {
			this.type = TYPE_SMS;
		}

		else if (checkStrBeContainedInArray(typeName, KEYS_CALL)) {
			this.type = TYPE_CALL;
		}
	}

	public boolean isFlow() {
		return type == TYPE_FLOW;
	}

	public boolean isCall() {
		return type == TYPE_CALL;
	}

	public boolean isSMS() {
		return type == TYPE_SMS;
	}

	public boolean isMMS() {
		return type == TYPE_MMS;
	}

	public boolean isWlan() {
		return type == TYPE_WLAN;
	}

	static final String[] KEYS_CALL = { "合家欢", "套餐语音", "套餐分钟", "通话", "分钟", "分" };
	static final String[] KEYS_SMS = { "短信", "条" };
	static final String[] KEYS_MMS = { "彩信" };
	static final String[] KEYS_WLAN = { "WLAN" };
	static final String[] KEYS_FLOW = { "流量", "GPRS", "GRPS", "4G", "2G", "3G", "KB" };

	boolean checkStrBeContainedInArray(String name, String[] array) {
		if (name == null)
			return false;

		for (int j = 0; j < array.length; j++) {
			if (name.contains(array[j]))
				return true;
		}
		return false;
	}

	public boolean isType(byte type2) {
		return type2 == type;
	}

	public String getStmp() {
		return stmp;
	}

	public void setStmp(String stmp) {
		this.stmp = stmp;
	}

	/**
	 * @return 是否是成员使用的
	 */
	public boolean isMemberUse() {
		boolean result = !TextUtils.isEmpty(name) && name.contains(STR_MEMBER_USE);
		return result;
	}

	/**
	 * @return 判断是否是wlan类别的gprs
	 */
	public boolean isWlanGprs() {
		if (type != TYPE_FLOW)
			return false;

		if (stmp == null)
			return false;

		if (stmp.toUpperCase().contains(STR_WLAN))
			return true;

		return false;

	}
}
