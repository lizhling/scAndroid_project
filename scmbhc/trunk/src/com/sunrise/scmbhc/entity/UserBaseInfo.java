package com.sunrise.scmbhc.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class UserBaseInfo {

	private String REGION_NAME;
	private String CONTACT_NAME;
	private String MAIN_MODE;
	private String BRAND_NAME;
	private String CREDIT_CLASS;
	private String SMS_CONTENT;

	public static class UserBaseAdapter extends UserBaseInfo {
		private ArrayList<String> MAIN_MODE;

		public void setMAIN_MODE(ArrayList<String> mAIN_MODE) {
			MAIN_MODE = mAIN_MODE;

			if (mAIN_MODE != null && !mAIN_MODE.isEmpty())
				setMAIN_MODE(mAIN_MODE.get(0));
		}
	}

	/**
	 * @return 归属地
	 */
	public String getREGION_NAME() {
		if (REGION_NAME == null)
			return "";
		return REGION_NAME;
	}

	/**
	 * @return 用户名字
	 */
	public String getCONTACT_NAME() {
		if (CONTACT_NAME == null)
			return "";
		return CONTACT_NAME;
	}

	/**
	 * @return 主套餐名称
	 */
	public String getMAIN_MODE() {
		return MAIN_MODE;
	}

	/**
	 * @return 此号码品牌
	 */
	public String getBRAND_NAME() {
		if (BRAND_NAME == null)
			return "";
		return BRAND_NAME;
	}

	public void setREGION_NAME(String rEGION_NAME) {
		REGION_NAME = rEGION_NAME;
	}

	public void setCONTACT_NAME(String cONTACT_NAME) {
		CONTACT_NAME = cONTACT_NAME;
	}

	public void setMAIN_MODE(String mAIN_MODE) {
		MAIN_MODE = mAIN_MODE;
	}

	public void setBRAND_NAME(String bRAND_NAME) {
		BRAND_NAME = bRAND_NAME;
	}

	public String toString() {
		return toJSONObject().toString();
	}

	private JSONObject toJSONObject() {
		JSONObject result = new JSONObject();
		try {
			result.put("BRAND_NAME", BRAND_NAME);
			result.put("REGION_NAME", REGION_NAME);
			result.put("CONTACT_NAME", this.CONTACT_NAME);
			result.put("MAIN_MODE", this.MAIN_MODE);
			result.put("CREDIT_CLASS", this.CREDIT_CLASS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return 用户信用等级
	 */
	public String getCREDIT_CLASS() {
		return CREDIT_CLASS;
	}

	/**
	 * @param cREDIT_CLASS
	 *            用户信用等级
	 */
	public void setCREDIT_CLASS(String cREDIT_CLASS) {
		CREDIT_CLASS = cREDIT_CLASS;
	}

	public String getSMS_CONTENT() {
		return SMS_CONTENT;
	}

	public void setSMS_CONTENT(String sMS_CONTENT) {
		SMS_CONTENT = sMS_CONTENT;
	}

}
