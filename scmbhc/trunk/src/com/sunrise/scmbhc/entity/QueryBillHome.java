package com.sunrise.scmbhc.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.scmbhc.utils.FileUtils;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

/**
 * 帐单查询
 * 
 * @author fuheng
 * 
 */
public class QueryBillHome {

	public static final String KEY_NAME = "name";
	public static final String KEY_VALUE = "value";
	
	static final String FORM_VALUE = "%s<font color=\"3146\">元</font>";

	private String PCAS_07_01;// 账户项目
	private String PCAS_07_02;// 上期结余
	private String PCAS_07_03;// 本期收入
	private String PCAS_07_04;// 本期返还
	private String PCAS_07_05;// 本期退费
	private String PCAS_07_06;// 本期可用
	private String PCAS_07_07;// 消费支出
	private String PCAS_07_08;// 其他支出
	private String PCAS_07_09;// 余额

	private String PCAS_03_01;// 套餐固定费用
	private String PCAS_03_03;// 上网费
	private String PCAS_03_02;// 语音通信费
	private String PCAS_03_05;// 增值业务费
	private String PCAS_03_04;// 短彩信费
	private String PCAS_03_07;// 其他费
	private String PCAS_03_06;// 代收业务费
	private String PCAS_03_09;// 合计
	private JSONObject mJsobNameTable;

	/**
	 * @return 账户项目
	 */
	public String getPCAS_07_01() {
		return PCAS_07_01;
	}

	/**
	 * @return 上期结余
	 */
	public String getPCAS_07_02() {
		return PCAS_07_02;
	}

	/**
	 * @return 本期收入
	 */
	public String getPCAS_07_03() {
		return PCAS_07_03;
	}

	/**
	 * @return 本期返还
	 */
	public String getPCAS_07_04() {
		return PCAS_07_04;
	}

	/**
	 * @return 本期退费
	 */
	public String getPCAS_07_05() {
		return PCAS_07_05;
	}

	/**
	 * @return 本期可用
	 */
	public String getPCAS_07_06() {
		return PCAS_07_06;
	}

	/**
	 * @return 消费支出
	 */
	public String getPCAS_07_07() {
		return PCAS_07_07;
	}

	/**
	 * @return 其他支出
	 */
	public String getPCAS_07_08() {
		return PCAS_07_08;
	}

	/**
	 * @return 余额
	 */
	public String getPCAS_07_09() {
		return PCAS_07_09;
	}

	/**
	 * @return 套餐固定费用
	 */
	public String getPCAS_03_01() {
		return getNoNullFloatValue(PCAS_03_01);
	}

	/**
	 * @return 上网费
	 */
	public String getPCAS_03_03() {
		return getNoNullFloatValue(PCAS_03_03);
	}

	/**
	 * @return 语音通信费
	 */
	public String getPCAS_03_02() {
		return getNoNullFloatValue(PCAS_03_02);
	}

	/**
	 * @return 增值业务费
	 */
	public String getPCAS_03_05() {
		return getNoNullFloatValue(PCAS_03_05);
	}

	/**
	 * @return 短彩信费
	 */
	public String getPCAS_03_04() {
		return getNoNullFloatValue(PCAS_03_04);
	}

	/**
	 * @return 代收业务费
	 */
	public String getPCAS_03_06() {
		return getNoNullFloatValue(PCAS_03_06);
	}

	/**
	 * @return 其他费
	 */
	public String getPCAS_03_07() {
		return getNoNullFloatValue(PCAS_03_07);
	}

	/**
	 * @return 合计
	 */
	public String getPCAS_03_09() {
		return getNoNullFloatValue(PCAS_03_09);
	}

	/**
	 * @return 合计
	 */
	public String getTotleCost() {
		float sum = 0;
		sum += Float.parseFloat(getPCAS_03_01());// 套餐固定费用
		sum += Float.parseFloat(getPCAS_03_03());// 上网费
		sum += Float.parseFloat(getPCAS_03_02());// 语音通信费
		sum += Float.parseFloat(getPCAS_03_05());// 增值业务费
		sum += Float.parseFloat(getPCAS_03_04());// 短彩信费
		sum += Float.parseFloat(getPCAS_03_07());// 其他费
		sum += Float.parseFloat(getPCAS_03_06());// 代收业务费

		return String.format("%.2f", sum);
	}

	public void setPCAS_07_01(String pCAS_07_01) {
		PCAS_07_01 = pCAS_07_01;
	}

	public void setPCAS_07_02(String pCAS_07_02) {
		PCAS_07_02 = pCAS_07_02;
	}

	public void setPCAS_07_03(String pCAS_07_03) {
		PCAS_07_03 = pCAS_07_03;
	}

	public void setPCAS_07_04(String pCAS_07_04) {
		PCAS_07_04 = pCAS_07_04;
	}

	public void setPCAS_07_05(String pCAS_07_05) {
		PCAS_07_05 = pCAS_07_05;
	}

	public void setPCAS_07_06(String pCAS_07_06) {
		PCAS_07_06 = pCAS_07_06;
	}

	public void setPCAS_07_07(String pCAS_07_07) {
		PCAS_07_07 = pCAS_07_07;
	}

	public void setPCAS_07_08(String pCAS_07_08) {
		PCAS_07_08 = pCAS_07_08;
	}

	public void setPCAS_07_09(String pCAS_07_09) {
		PCAS_07_09 = pCAS_07_09;
	}

	public void setPCAS_03_01(String pCAS_03_01) {
		PCAS_03_01 = pCAS_03_01;
	}

	public void setPCAS_03_03(String pCAS_03_03) {
		PCAS_03_03 = pCAS_03_03;
	}

	public void setPCAS_03_02(String pCAS_03_02) {
		PCAS_03_02 = pCAS_03_02;
	}

	public void setPCAS_03_05(String pCAS_03_05) {
		PCAS_03_05 = pCAS_03_05;
	}

	public void setPCAS_03_04(String pCAS_03_04) {
		PCAS_03_04 = pCAS_03_04;
	}

	public void setPCAS_03_06(String pCAS_03_06) {
		PCAS_03_06 = pCAS_03_06;
	}

	public void setPCAS_03_07(String pCAS_03_07) {
		PCAS_03_07 = pCAS_03_07;
	}

	public void setPCAS_03_09(String pCAS_03_09) {
		PCAS_03_09 = pCAS_03_09;
	}

	public ArrayList<HashMap<String, CharSequence>> getAccountingBill(Context context) {
		if (mJsobNameTable == null)
			initNameTable(context);

		ArrayList<HashMap<String, CharSequence>> result = new ArrayList<HashMap<String, CharSequence>>();
		result.add(getHash("PCAS_07_01", PCAS_07_01));// 账户项目
		result.add(getHash("PCAS_07_02", PCAS_07_02));// 上期结余
		result.add(getHash("PCAS_07_03", PCAS_07_03));// 本期收入
		result.add(getHash("PCAS_07_04", PCAS_07_04));// 本期返还
		result.add(getHash("PCAS_07_05", PCAS_07_05));// 本期退费
		result.add(getHash("PCAS_07_06", PCAS_07_06));// 本期可用
		result.add(getHash("PCAS_07_07", PCAS_07_07));// 消费支出
		result.add(getHash("PCAS_07_08", PCAS_07_08));// 其他支出
		result.add(getHash("PCAS_07_09", PCAS_07_09));// 余额

		return result;
	}

	public ArrayList<HashMap<String, CharSequence>> getDetailOfBill(Context context) {

		if (mJsobNameTable == null)
			initNameTable(context);

		ArrayList<HashMap<String, CharSequence>> result = new ArrayList<HashMap<String, CharSequence>>();

		result.add(getHash("PCAS_03_01", getPCAS_03_01()));// 套餐固定费用
		result.add(getHash("PCAS_03_03", getPCAS_03_03()));// 上网费
		result.add(getHash("PCAS_03_02", getPCAS_03_02()));// 语音通信费
		result.add(getHash("PCAS_03_05", getPCAS_03_05()));// 增值业务费
		result.add(getHash("PCAS_03_04", getPCAS_03_04()));// 短彩信费
		result.add(getHash("PCAS_03_07", getPCAS_03_07()));// 其他费
		result.add(getHash("PCAS_03_06", getPCAS_03_06()));// 代收业务费
		return result;
	}

	private HashMap<String, CharSequence> getHash(String name, String value) {

		try {
			String temp = mJsobNameTable.getString(name);
			if (!TextUtils.isEmpty(temp))
				name = temp;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HashMap<String, CharSequence> result = new HashMap<String, CharSequence>();
		result.put(KEY_NAME, name);
		result.put(KEY_VALUE, Html.fromHtml(String.format(FORM_VALUE, value)));
		return result;
	}

	private void initNameTable(Context context) {
		try {
			String temp = FileUtils.readToStringFormInputStreamUTF_8(context.getResources().getAssets().open("nameTableOfQueryBillHome.txt"));
			mJsobNameTable = new JSONObject(temp);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void checkInfoNull() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			Field field = fields[i];
			if (field.getType() == String.class)
				field.set(this, "0");
		}
	}

	private String getNoNullFloatValue(String str) {
		if (TextUtils.isEmpty(str))
			return "0";

		return str;
	}

}
