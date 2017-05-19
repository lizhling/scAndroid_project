package com.sunrise.scmbhc.entity;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.Spanned;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用于获取附加资费查询信息,这是单个附加资费的信息
 * 
 * @author fuheng
 * 
 */
public class AdditionalTariffInfo {
	private String EXP_DATE;
	private String run_name;
	private String PROD_PRC_NAME;
	private String PROD_PRCID;
	private String EFF_DATE;
	private String CANCEL_FLAG;
	private String EFF_TYPE; // 生效方式 (1 预约生效 2 预约失效 3 当前生效 4 当前失效) note add by
								// qhb
	private String PROD_TYPE;

	private static final String UNLIMIT_EXP_DATA = "20991231235959";// 无限时间

	private String ID_NO;// 11902030466649,
	private String PRODPRCINS_ID;// 10091705639,
	private String ORDER_NO;// 0,
	private String PROD_ID;// APAZ06735,
	private String PROD_NAME;// 入网纪念日送优惠,
	private String PROD_PKGID;// ,
	private String PRC_PKGID;// ,
	private String PROD_DESC;// 入网纪念日送优惠,
	private String PROD_PRC_DESC;// 入网纪念日送优惠,
	private String OP_TIME;// 20140804093707,
	private String STATE;// A,
	private String USER_RANGE;// 0000000,
	private String SUB_PACKGE;// N,
	private String BILL_NAME;// 包月,
	private String PROD_CLASS_TYPE;// 附加产品,
	private String CANCEL_RULE_ID;// 1002,
	private String LIMIT_FLAG;// N,

	public static String getUnlimitExpData() {
		return UNLIMIT_EXP_DATA;
	}

	public String getID_NO() {
		return ID_NO;
	}

	public String getPRODPRCINS_ID() {
		return PRODPRCINS_ID;
	}

	public String getORDER_NO() {
		return ORDER_NO;
	}

	public String getPROD_ID() {
		return PROD_ID;
	}

	public String getPROD_NAME() {
		return PROD_NAME;
	}

	public String getPROD_PKGID() {
		return PROD_PKGID;
	}

	public String getPRC_PKGID() {
		return PRC_PKGID;
	}

	public String getPROD_DESC() {
		return PROD_DESC;
	}

	public String getPROD_PRC_DESC() {
		return PROD_PRC_DESC;
	}

	public String getOP_TIME() {
		return OP_TIME;
	}

	public String getSTATE() {
		return STATE;
	}

	public String getUSER_RANGE() {
		return USER_RANGE;
	}

	public String getSUB_PACKGE() {
		return SUB_PACKGE;
	}

	public String getBILL_NAME() {
		return BILL_NAME;
	}

	public String getPROD_CLASS_TYPE() {
		return PROD_CLASS_TYPE;
	}

	public String getCANCEL_RULE_ID() {
		return CANCEL_RULE_ID;
	}

	public String getLIMIT_FLAG() {
		return LIMIT_FLAG;
	}

	public String getFEE_TYPE() {
		return FEE_TYPE;
	}

	public String getPRICE() {
		return PRICE;
	}

	public String getPRICE_UNIT() {
		return PRICE_UNIT;
	}

	public String getPRODUCT_TYPE() {
		return PRODUCT_TYPE;
	}

	public void setID_NO(String iD_NO) {
		ID_NO = iD_NO;
	}

	public void setPRODPRCINS_ID(String pRODPRCINS_ID) {
		PRODPRCINS_ID = pRODPRCINS_ID;
	}

	public void setORDER_NO(String oRDER_NO) {
		ORDER_NO = oRDER_NO;
	}

	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}

	public void setPROD_NAME(String pROD_NAME) {
		PROD_NAME = pROD_NAME;
	}

	public void setPROD_PKGID(String pROD_PKGID) {
		PROD_PKGID = pROD_PKGID;
	}

	public void setPRC_PKGID(String pRC_PKGID) {
		PRC_PKGID = pRC_PKGID;
	}

	public void setPROD_DESC(String pROD_DESC) {
		PROD_DESC = pROD_DESC;
	}

	public void setPROD_PRC_DESC(String pROD_PRC_DESC) {
		PROD_PRC_DESC = pROD_PRC_DESC;
	}

	public void setOP_TIME(String oP_TIME) {
		OP_TIME = oP_TIME;
	}

	public void setSTATE(String sTATE) {
		STATE = sTATE;
	}

	public void setUSER_RANGE(String uSER_RANGE) {
		USER_RANGE = uSER_RANGE;
	}

	public void setSUB_PACKGE(String sUB_PACKGE) {
		SUB_PACKGE = sUB_PACKGE;
	}

	public void setBILL_NAME(String bILL_NAME) {
		BILL_NAME = bILL_NAME;
	}

	public void setPROD_CLASS_TYPE(String pROD_CLASS_TYPE) {
		PROD_CLASS_TYPE = pROD_CLASS_TYPE;
	}

	public void setCANCEL_RULE_ID(String cANCEL_RULE_ID) {
		CANCEL_RULE_ID = cANCEL_RULE_ID;
	}

	public void setLIMIT_FLAG(String lIMIT_FLAG) {
		LIMIT_FLAG = lIMIT_FLAG;
	}

	public void setFEE_TYPE(String fEE_TYPE) {
		FEE_TYPE = fEE_TYPE;
	}

	public void setPRICE(String pRICE) {
		PRICE = pRICE;
	}

	public void setPRICE_UNIT(String pRICE_UNIT) {
		PRICE_UNIT = pRICE_UNIT;
	}

	public void setPRODUCT_TYPE(String pRODUCT_TYPE) {
		PRODUCT_TYPE = pRODUCT_TYPE;
	}

	private String FEE_TYPE;// B,
	private String PRICE;// 0,
	private String PRICE_UNIT;// 元/月,
	private String PRODUCT_TYPE;// 01

	public String getEXP_DATE() {
		return parseDate(EXP_DATE);
	}

	public String getRun_name() {
		return run_name;
	}

	public String getPROD_PRC_NAME() {
		return PROD_PRC_NAME;
	}

	public String getPROD_PRCID() {
		return PROD_PRCID;
	}

	public String getEFF_DATE() {
		return parseDate(EFF_DATE);
	}

	public String getCANCEL_FLAG() {
		return CANCEL_FLAG;
	}

	public String getEFF_TYPE() {
		return EFF_TYPE;
	}

	public String getPROD_TYPE() {
		return PROD_TYPE;
	}

	public void setEXP_DATE(String eXP_DATE) {
		EXP_DATE = eXP_DATE;
	}

	public void setRun_name(String run_name) {
		this.run_name = run_name;
	}

	public void setPROD_PRC_NAME(String pROD_PRC_NAME) {
		PROD_PRC_NAME = pROD_PRC_NAME;
	}

	public void setPROD_PRCID(String pROD_PRCID) {
		PROD_PRCID = pROD_PRCID;
	}

	public void setEFF_DATE(String eFF_DATE) {
		EFF_DATE = eFF_DATE;
	}

	public void setCANCEL_FLAG(String cANCEL_FLAG) {
		CANCEL_FLAG = cANCEL_FLAG;
	}

	public void setEFF_TYPE(String eFF_TYPE) {
		EFF_TYPE = eFF_TYPE;
	}

	public void setPROD_TYPE(String pROD_TYPE) {
		PROD_TYPE = pROD_TYPE;
	}

	@SuppressLint("SimpleDateFormat")
	private String parseDate(String res) {
		SimpleDateFormat decode = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat encode = new SimpleDateFormat("yyyy年MM月dd日");

		try {
			return encode.format(decode.parse(res));
		} catch (ParseException e) {
			e.printStackTrace();
			return res;
		}
	}

	private BusinessMenu businessItem;

	public BusinessMenu getBusinessItem() {
		return businessItem;
	}

	public void setBusinessItem(BusinessMenu businessItem) {
		this.businessItem = businessItem;
	}

	public Spanned getDuration() {
		if (UNLIMIT_EXP_DATA.equals(EXP_DATE))
			return Html.fromHtml(String.format("生效日期：%s", getEFF_DATE()));

		return Html.fromHtml(String.format("生效日期：%s<br>结束日期：%s", getEFF_DATE(), getEXP_DATE()));
	}

	public String toString() {
		try {
			return toJson().toString();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return super.toString();
	}

	public JSONObject toJson() throws IllegalArgumentException, IllegalAccessException, JSONException {
		JSONObject json = new JSONObject();
		Field[] fields = getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			json.put(fields[i].getName(), fields[i].get(this));
		}
		return json;
	}

	public void eat(AdditionalTariffInfo a2) {
		Field[] fields = getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				Object o1 = f.get(this);
				Object o2 = f.get(a2);
				if (o1 == null && o2 != null) {
					f.set(this, o2);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
