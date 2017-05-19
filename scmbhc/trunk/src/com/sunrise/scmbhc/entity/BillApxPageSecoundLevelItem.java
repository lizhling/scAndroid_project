package com.sunrise.scmbhc.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Html;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.entity.bean.ResultBean;
import com.sunrise.scmbhc.utils.FileUtils;

public class BillApxPageSecoundLevelItem {

	private String total;
	private String fee;
	private String totalName;
	private String feeName;

	private String attribuiltName;
	private ArrayList<CharSequence> arrayAttribuilt;

	public String getTotal() {
		return total;
	}

	public String getFee() {
		return fee;
	}

	public String getTotalName() {
		return totalName;
	}

	public String getFeeName() {
		return feeName;
	}

	public ArrayList<CharSequence> getArrayAttribuilt() {
		return arrayAttribuilt;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public void setTotalName(String totalName) {
		this.totalName = totalName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public void setArrayAttribuilt(ArrayList<CharSequence> arrayAttribuilt) {
		this.arrayAttribuilt = arrayAttribuilt;
	}

	public void addAttribuilt(CharSequence charsequence) {
		if (arrayAttribuilt == null)
			arrayAttribuilt = new ArrayList<CharSequence>();
		arrayAttribuilt.add(charsequence);
	}

	public String getAttribuiltName() {
		return attribuiltName;
	}

	public void setAttribuiltName(String attribuiltName) {
		this.attribuiltName = attribuiltName;
	}

	private static final ArrayList<ArrayList<BillApxPageSecoundLevelItem>> analysisCurMonthBillDetail(JSONObject jsonobj, ArrayList<String> arrayName,
			ArrayList<Byte> arrayType) {

		if (jsonobj == null)
			return null;

		arrayName.add("费用明细");
		arrayType.add((byte) 0);
		ArrayList<ArrayList<BillApxPageSecoundLevelItem>> result = new ArrayList<ArrayList<BillApxPageSecoundLevelItem>>();
		ArrayList<BillApxPageSecoundLevelItem> secoundLevel = new ArrayList<BillApxPageSecoundLevelItem>();
		result.add(secoundLevel);
		try {
			BillApxPageSecoundLevelItem item = new BillApxPageSecoundLevelItem();

			item.setFee(Float.toString(Float.parseFloat(jsonobj.getString("TOTAL_PAY")) / 100));
			item.setAttribuiltName("总计");

			JSONArray array = jsonobj.getJSONArray("DETAIL_INFO");
			for (int i = 0; i < array.length(); ++i) {
				JSONObject jsitem = array.getJSONObject(i);
				ResultBean been = new ResultBean();
				been.setResultCode(Float.toString(Float.parseFloat(jsitem.getString("PAY")) / 100));
				been.setResultMesage(jsitem.getString("SHOW_NAME"));
				item.addAttribuilt(JsonUtils.writeObjectToJsonStr(been));

			}
			// jsonobj.getString("BEGIN_DATE");
			// jsonobj.getString("TOTAL_PAY");
			secoundLevel.add(item);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	/*********************************** 解析 *****************************************/

	/**
	 * 从json对照表中获取对照名称
	 * 
	 * @param name
	 * @param jsobNameTable
	 * @return 表中名称，如果表中没有，返回原字符串
	 * @throws JSONException
	 */
	private static String getRealNameFromJsonTable(String name, JSONObject jsobNameTable) throws JSONException {
		if (jsobNameTable.isNull(name))
			return name;
		else
			return jsobNameTable.getString(name);
	}

	public static ArrayList<ArrayList<BillApxPageSecoundLevelItem>> analysisJson(Context context, JSONObject json, ArrayList<String> arrayName,
			ArrayList<Byte> arrayType) {
		if (json.toString().contains("DETAIL_INFO"))
			return analysisCurMonthBillDetail(json, arrayName, arrayType);
		try {
			return analysisBillApxPageQry(context, json, arrayName, arrayType);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ArrayList<ArrayList<BillApxPageSecoundLevelItem>> analysisBillApxPageQry(Context context, JSONObject json, ArrayList<String> arrayName,
			ArrayList<Byte> arrayType) throws JSONException, IOException {

		final JSONObject jsobNameTable = new JSONObject(FileUtils.readToStringFormInputStreamUTF_8(context.getResources().getAssets()
				.open("nameTableOfQueryBillHome.txt")));

		ArrayList<ArrayList<BillApxPageSecoundLevelItem>> result = new ArrayList<ArrayList<BillApxPageSecoundLevelItem>>();

		Iterator<?> keys = json.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String tempName = getRealNameFromJsonTable(key, jsobNameTable);

			if (arrayName != null)// 存储名称
				arrayName.add(tempName);

			Object obj = json.get(key);
			// Log.d(keys.getClass().getSimpleName(), "key = " + key.toString()
			// + " , value =  " + obj + " , instanceof " + obj.getClass());

			byte type = 0;
			if (tempName.contains("费用"))
				type = 0;
			else if (tempName.contains("账户"))
				type = 1;
			else if (tempName.contains("使用"))
				type = 2;

			if (arrayType != null)
				arrayType.add(type);

			if (obj instanceof JSONObject) {
				JSONObject jsonobj = (JSONObject) obj;
				result.add(analysisJsonSecound(jsonobj, type, jsobNameTable));
			}
		}

		return result;
	}

	private static ArrayList<BillApxPageSecoundLevelItem> analysisJsonSecound(JSONObject jsonobj, byte type, JSONObject jsobNameTable) throws JSONException {
		ArrayList<String> arrayName = new ArrayList<String>();
		ArrayList<BillApxPageSecoundLevelItem> arrayResult = new ArrayList<BillApxPageSecoundLevelItem>();

		Iterator<?> keys = jsonobj.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object obj = jsonobj.get(key);

			// Log.d(keys.getClass().getSimpleName(), "key = " + key.toString()
			// + " , value =  " + obj + " , instanceof " + obj.getClass());

			BillApxPageSecoundLevelItem tempItem = null;
			int index = checkTableType(key, arrayName);

			if (index == -1) {
				tempItem = new BillApxPageSecoundLevelItem();
				arrayResult.add(tempItem);
			} else {
				tempItem = arrayResult.get(index);
			}

			if (obj instanceof JSONArray) {
				JSONArray tempJArray = (JSONArray) obj;
				tempItem.setAttribuiltName(getRealNameFromJsonTable(key, jsobNameTable));

				for (int i = 0; i < tempJArray.length(); ++i)
					analysisJsonThird(tempJArray.getJSONObject(i), tempItem, type, jsobNameTable);
			}

			else if (obj instanceof String) {
				if (key.endsWith("TOTAL")) {
					tempItem.setTotal((String) obj);
					tempItem.setTotalName(getRealNameFromJsonTable(key, jsobNameTable));
				} else if (key.endsWith("FEE")) {
					tempItem.setFee((String) obj);
					tempItem.setFeeName(getRealNameFromJsonTable(key, jsobNameTable));
				}
			}
		}

		for (int i = 0; i < arrayResult.size();) {
			BillApxPageSecoundLevelItem obj = arrayResult.get(i);
			if (obj.getAttribuiltName() == null)
				arrayResult.remove(obj);
			else
				++i;
		}

		return arrayResult;
	}

	/**
	 * 填入列表信息
	 * 
	 * @param jsonobj
	 * @param item
	 * @return
	 * @throws JSONException
	 */
	private static HashMap<String, String> analysisJsonThird(JSONObject jsonobj, BillApxPageSecoundLevelItem item, byte t, JSONObject jsobNameTable)
			throws JSONException {
		HashMap<String, String> result = new HashMap<String, String>();

		Iterator<?> keys = jsonobj.keys();

		String name = null;
		String value = null;
		String type = null;
		String last = null;
		String cost = null;
		String unit = null;
		String effect_time = null;
		String failure_time = null;

		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object obj = jsonobj.get(key);
			String tempKey = getRealNameFromJsonTable(key, jsobNameTable);

			if (tempKey.contains("名称") || tempKey.equals("帐户"))
				name = (String) obj;
			else if (tempKey.contains("失效"))
				failure_time = (String) obj;
			else if (tempKey.contains("入帐时间"))
				effect_time = (String) obj;
			else if (tempKey.contains("类型") || tempKey.contains("方式"))
				type = (String) obj;
			else if (tempKey.equals("剩余"))
				last = (String) obj;
			else if (tempKey.equals("已送"))
				cost = (String) obj;
			else if (tempKey.equals("单位"))
				unit = (String) obj;
			else if (tempKey.equals("费用") || tempKey.contains("金额") || tempKey.equals("应送"))
				value = (String) obj;
		}

		if (t == 2) {// 使用状况
			UseCondition condition = new UseCondition();
			condition.setName(name);
			condition.setTotle(Double.parseDouble(value));
			condition.setUsed(Double.parseDouble(cost));
			condition.setType(type);
			item.addAttribuilt(condition.toJsonObject().toString());
		} else if (t == 1) {// 账户明细
			item.addAttribuilt(Html.fromHtml(String.format("<B>%s</B><br> 入账时间：%s<br> 金额：<i>%s<i>元<br> 预存类型：%s", name, effect_time, value, type)));
		} else {
			ResultBean been = new ResultBean();
			been.setResultCode(value);
			been.setResultMesage(name);
			item.addAttribuilt(JsonUtils.writeObjectToJsonStr(been));
		}

		return result;
	}

	private static int checkTableType(String str, ArrayList<String> arrayName) {

		int index = str.lastIndexOf('_');

		String tempName = null;
		if (index < 2)
			return -1;

		tempName = str.substring(0, index);

		for (int i = 0; i < arrayName.size(); ++i) {

			String oldName = arrayName.get(i);
			if (oldName.equals(tempName)) {
				return i;
			}
		}
		arrayName.add(tempName);

		return -1;
	}
}
