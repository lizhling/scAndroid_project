package com.sunrise.scmbhc.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.text.TextUtils;

/**
 * 充值历史记录单条
 * 
 * @author 珩
 * @version 2014年9月28日14:09:15
 */
public class TopupHistoryItem {
	private String OP_CODE;// "8000",
	private String TOWN_NAME;// "四川省",
	private String OP_TIME;// "20140916 12:18:35",
	private String PAY_NOTE;// "网厅充值：0.985折扣率充值缴费",
	private String PAY_NAME;// "现金",
	private String PAY_PATH;// "03",
	private String PAY_TYPE;// "0",
	private String PAY_SN;// "10000366630754",
	private String LOGIN_NO;// "ob0014",
	private String BACK_FLAG;// "0",
	private String CONTRACT_NO;// "11404056524134",
	private String PAY_MONEY;// "2955"

	public static final String[] FROM = { "PAY_NOTE", "PAY_MONEY", "OP_TIME" };

	public String getOP_CODE() {
		return OP_CODE;
	}

	/**
	 * @return 交费地点
	 */
	public String getTOWN_NAME() {
		return TOWN_NAME;
	}

	/**
	 * @return 充值时间
	 */
	public String getOP_TIME() {
		if (TextUtils.isEmpty(OP_TIME) || OP_TIME.length() < 9)
			return OP_TIME;
		else
			return parseTimeFormat(OP_TIME);
	}

	public long getOP_TIME_long() {
		if (!TextUtils.isEmpty(OP_TIME)) {
			SimpleDateFormat decode = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			try {
				Date date = decode.parse(OP_TIME);
				return date.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	/**
	 * @return 充值途径(备注)
	 */
	public String getPAY_NOTE() {

		if (PAY_NOTE == null)
			return PAY_NOTE;

		int index = PAY_NOTE.indexOf('：');
		if (index != -1) {
			return String.format(FORM_2_LINE, PAY_NOTE.substring(0, index), PAY_NOTE.substring(index + 1));
		} else if (PAY_NOTE.length() > 12) {
			index = PAY_NOTE.length() / 2;
			return String.format(FORM_2_LINE, PAY_NOTE.substring(0, index), PAY_NOTE.substring(index));
		} else
			return PAY_NOTE;

	}

	/**
	 * @return 帐本类型
	 */
	public String getPAY_NAME() {
		return PAY_NAME;
	}

	public String getPAY_PATH() {
		return PAY_PATH;
	}

	/**
	 * @return 帐本类型编码
	 */
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}

	/**
	 * @return 交费流水
	 */
	public String getPAY_SN() {
		return PAY_SN;
	}

	public String getLOGIN_NO() {
		return LOGIN_NO;
	}

	/**
	 * @return 是否冲正
	 */
	public String getBACK_FLAG() {
		return BACK_FLAG;
	}

	/**
	 * @return 账户标识
	 */
	public String getCONTRACT_NO() {
		return CONTRACT_NO;
	}

	/**
	 * @return 充值金额
	 */
	public String getPAY_MONEY() {
		if (PAY_MONEY == null)
			return PAY_MONEY;
		else
			return String.format("<b><i>%.2f</i></b>元", Integer.parseInt(PAY_MONEY) / 100f);
	}

	public void setOP_CODE(String oP_CODE) {
		OP_CODE = oP_CODE;
	}

	public void setTOWN_NAME(String tOWN_NAME) {
		TOWN_NAME = tOWN_NAME;
	}

	public void setOP_TIME(String oP_TIME) {
		OP_TIME = oP_TIME;
	}

	public void setPAY_NOTE(String pAY_NOTE) {
		PAY_NOTE = pAY_NOTE;
	}

	public void setPAY_NAME(String pAY_NAME) {
		PAY_NAME = pAY_NAME;
	}

	public void setPAY_PATH(String pAY_PATH) {
		PAY_PATH = pAY_PATH;
	}

	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}

	public void setPAY_SN(String pAY_SN) {
		PAY_SN = pAY_SN;
	}

	public void setLOGIN_NO(String lOGIN_NO) {
		LOGIN_NO = lOGIN_NO;
	}

	public void setBACK_FLAG(String bACK_FLAG) {
		BACK_FLAG = bACK_FLAG;
	}

	public void setCONTRACT_NO(String cONTRACT_NO) {
		CONTRACT_NO = cONTRACT_NO;
	}

	public void setPAY_MONEY(String pAY_MONEY) {
		PAY_MONEY = pAY_MONEY;
	}

	public HashMap<String, String> getHashMap() {

		HashMap<String, String> hash = new HashMap<String, String>();
		// for (String str : FROM)
		// try {
		// hash.put(str, (String) getClass().getDeclaredField(str).get(this));
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (NoSuchFieldException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// e.printStackTrace();
		// }
		hash.put(FROM[0], getPAY_NOTE());
		hash.put(FROM[1], getPAY_MONEY());
		hash.put(FROM[2], getOP_TIME());
		return hash;
	}

	private static final String FORM_2_LINE = "%s<br>%s";

	private static final String parseTimeFormat(String time) {
		SimpleDateFormat decode = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		SimpleDateFormat encode1 = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat encode2 = new SimpleDateFormat("hh:mm:ss");
		try {
			Date date = decode.parse(time);
			return String.format(FORM_2_LINE, encode1.format(date), encode2.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			return time.substring(0, 8);
		}
	}
}
