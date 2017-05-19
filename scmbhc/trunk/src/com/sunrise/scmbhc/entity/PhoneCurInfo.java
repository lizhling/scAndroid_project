package com.sunrise.scmbhc.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sunrise.scmbhc.utils.CommUtil;

public class PhoneCurInfo {

	private static final String KEY_USED = "已使用";

	private static final String KEY_TOTLE = "合计";

	/*
	 * 本月通信费83.00元,当前余额7.03元,套餐分钟数合计260分钟,已使用144分钟,剩余116分钟,短信合计150条,已使用21条,剩余129条
	 * ,
	 * GPRS国内流量合计60.00M,已使用42.02M,剩余17.98M,剩余V网包月套餐通话分钟数合计3000分钟,当前积分为:6714.00(
	 * 仅供参考 ,准确数据以每月账单为准)。
	 */
	private float coat = 0;

	private float balance = 0;

	private double gprs_totle = 0;
	private double gprs_cost = 0;
	private double gprs_remain = 0;
	/**
	 * 公共账户 消费
	 */
	private String publicCost;
	/**
	 * 公共账户剩余
	 */
	private String publicBalance;

	// private UseCondition conditionCall;
	// private UseCondition conditionSMS;
	// private ArrayList<UseCondition> conditionFlow = new
	// ArrayList<UseCondition>();

	private ArrayList<UseCondition> arrCondition = new ArrayList<UseCondition>();

	private int credits = -1;

	public PhoneCurInfo() {

	}

	public PhoneCurInfo(String str) {
		init(str);
	}

	private void init(String str) {
		if (TextUtils.isEmpty(str))
			return;

		if (str.contains("，"))
			str = str.replace('，', ',');

		String[] infos = str.split(",");

		UseCondition condition = null;
		for (int i = 0; i < infos.length; ++i) {
			String info = infos[i];
			if (info.contains("通信费"))
				coat = CommUtil.parse2Float(info);
			else if (info.contains("余额"))
				balance = CommUtil.parse2Float(info);
			else if (info.contains("积分"))
				credits = (int) CommUtil.parse2Float(info);

			else if (info.contains(KEY_TOTLE) && condition == null) {
				condition = new UseCondition();
				arrCondition.add(condition);

				int index = info.indexOf(KEY_TOTLE);
				condition.setName(info.substring(0, index));
				index += KEY_TOTLE.length();
				String temp = info.substring(index);

				condition.setType(condition.getName());

				double d = CommUtil.parse2Double(temp);
				if (temp.contains("M"))
					d *= 1024;
				else if (temp.contains("G"))
					d *= 1024 * 1024;
				condition.setTotle(d);
			}

			else if (info.contains(KEY_USED) && condition != null) {

				String temp = info.substring(info.indexOf(KEY_USED) + KEY_USED.length());

				double d = CommUtil.parse2Double(temp);
				if (temp.contains("M"))
					d *= 1024;
				else if (temp.contains("G"))
					d *= 1024 * 1024;

				condition.setUsed(d);
			}

			else if (info.contains("剩余") && condition != null) {
				double d = CommUtil.parse2Double(info);
				if (info.contains("M"))
					d *= 1024;
				else if (info.contains("G"))
					d *= 1024 * 1024;
				condition.setSurplus(d);
				
				condition = null;
			}
		}
	}

	public JSONObject toJsonObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("coat", coat);// 保存当前消费
			json.put("balance", balance);// 保存余额
			if (getPublicCost() != null)
				json.put("publicCost", getPublicCost());// 公共账户消费
			if (getPublicBalance() != null)
				json.put("publicBalance", getPublicBalance());// 公共账户余额
			// if (conditionSMS != null)
			// json.put("conditionSMS", conditionSMS.toJsonObject());
			// if (conditionCall != null)
			// json.put("conditionCall", conditionCall.toJsonObject());
			json.put("credits", credits);// 保存积分
			//
			// JSONArray jsarray = new JSONArray();
			// for (UseCondition flow : conditionFlow)
			// jsarray.put(flow.toJsonObject());
			// json.put("conditionFlow", jsarray);
			JSONArray jsarray = new JSONArray();
			for (UseCondition flow : arrCondition)
				jsarray.put(flow.toJsonObject());
			json.put("arrCondition", jsarray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public String toString() {
		return toJsonObject().toString();
	}

	public float getCoat() {
		return coat;
	}

	public float getBalance() {
		return balance;
	}
	public UseCondition getFlowAllCondition() {
		for (UseCondition condition : arrCondition) {
			if (condition.isFlow()) {
				return condition;
			}
		}
		return null;
	}
	public ArrayList<UseCondition> getConditionFlowArray() {
		ArrayList<UseCondition> array = new ArrayList<UseCondition>();
		for (UseCondition condition : arrCondition) {
			if (condition.isFlow()) {
				array.add(condition);
			}
		}
		return array;
	}

	public UseCondition getConditionFlow(ArrayList<String> array) {

		double usedSum = 0;
		double totleSum = 0;

		for (UseCondition flow : arrCondition) {
			if (flow.isFlow()) {
				usedSum += flow.getUsed();
				totleSum += flow.getTotle();

				if (array != null)
					array.add(flow.toConditionString());
			}
		}

		UseCondition condition = new UseCondition();
		if (totleSum > 0) {
			condition.setType(UseCondition.TYPE_FLOW);
			condition.setTotle(totleSum);
			condition.setUsed(usedSum);
		}
		return condition;
	}

	public UseCondition getConditionSMS() {
		double usedSum = 0;
		double totleSum = 0;

		for (UseCondition sms : arrCondition) {
			if (sms.isSMS()) {
				totleSum += sms.getTotle();
				usedSum += sms.getUsed();
			}
		}

		UseCondition condition = new UseCondition();
		if (totleSum > 0) {
			condition.setType(UseCondition.TYPE_SMS);
			condition.setTotle(totleSum);
			condition.setUsed(usedSum);
		}
		return condition;
	}

	public UseCondition getConditionCall() {
		double usedSum = 0;
		double totleSum = 0;

		for (UseCondition call : arrCondition) {
			if (call.isCall()) {
				totleSum += call.getTotle();
				usedSum += call.getUsed();
			}
		}

		UseCondition condition = new UseCondition();
		if (totleSum > 0) {
			// condition.setType(PhoneFreeQuery.KEYS_CALL[0]);
			condition.setType(UseCondition.TYPE_CALL);
			condition.setTotle(totleSum);
			condition.setUsed(usedSum);
		}

		return condition;
	}

	public int getCredits() {
		return credits;
	}

	public void setCoat(float coat) {
		this.coat = coat;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	// public void setConditionFlow(ArrayList<UseCondition> conditionFlow) {
	// this.conditionFlow = conditionFlow;
	// }
	// public void setConditionSMS(UseCondition conditionSMS) {
	// this.conditionSMS = conditionSMS;
	// }
	// public void setConditionCall(UseCondition conditionCall) {
	// this.conditionCall = conditionCall;
	// }

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public ArrayList<UseCondition> getArrCondition() {
		return arrCondition;
	}

	public void setArrCondition(ArrayList<UseCondition> arrCondition) {
		this.arrCondition = arrCondition;
	}

	public String getPublicBalance() {
		return publicBalance;
	}

	public void setPublicBalance(String publicBalance) {
		this.publicBalance = publicBalance;
	}

	public String getPublicCost() {
		return publicCost;
	}

	public void setPublicCost(String publicCost) {
		this.publicCost = publicCost;
	}
}
