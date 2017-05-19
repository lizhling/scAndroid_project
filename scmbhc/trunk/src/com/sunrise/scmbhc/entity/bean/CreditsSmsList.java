package com.sunrise.scmbhc.entity.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;

public class CreditsSmsList {
	private ArrayList<CreditsSmsObject> SMS_EXCHANGE_INFOS;

	public CreditsSmsList() {

	}

	public CreditsSmsList(String data) {
		try {
			JSONArray jsonarray = new JSONObject(data).getJSONArray("datas");
			ArrayList<CreditsExchangeItem> array = new ArrayList<CreditsExchangeItem>();
			for (int i = 0; i < jsonarray.length(); ++i)
				array.add(JsonUtils.parseJsonStrToObject(jsonarray.getString(i), CreditsExchangeItem.class));

			final int ID_START = 1;
			SMS_EXCHANGE_INFOS = new ArrayList<CreditsSmsObject>();
			// 获取第一级菜单
			for (CreditsExchangeItem item : array)
				if (item.getParentId() == ID_START) {
					CreditsSmsObject obj = new CreditsSmsObject();
					obj.setOwenAttr(item);
					SMS_EXCHANGE_INFOS.add(obj);
				}
			// 清除第一级菜单和第二级菜单
			for (CreditsSmsObject sitem : SMS_EXCHANGE_INFOS) {
				int parantId = sitem.getOwenAttr().getId();
				array.remove(sitem.getOwenAttr());
				for (CreditsExchangeItem item : array) {
					if (item.getParentId() == parantId)
						sitem.addECoupon(item);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<CreditsSmsObject> getArray() {
		return getSMS_EXCHANGE_INFOS();
	}

	public ArrayList<CreditsSmsObject> getSMS_EXCHANGE_INFOS() {
		return SMS_EXCHANGE_INFOS;
	}

	public void setSMS_EXCHANGE_INFOS(ArrayList<CreditsSmsObject> sMS_EXCHANGE_INFOS) {
		SMS_EXCHANGE_INFOS = sMS_EXCHANGE_INFOS;
	}

	public static class CreditsSmsObject {

		private CreditsExchangeItem mOwenAttr;

		private ArrayList<CreditsExchangeItem> datas;

		public void addECoupon(CreditsExchangeItem e) {
			if (datas == null)
				datas = new ArrayList<CreditsExchangeItem>();
			datas.add(e);
		}

		public ArrayList<CreditsExchangeItem> getDatas() {
			return datas;
		}

		public void setDatas(ArrayList<CreditsExchangeItem> datas) {
			this.datas = datas;
		}

		public CreditsExchangeItem getOwenAttr() {
			return mOwenAttr;
		}

		public void setOwenAttr(CreditsExchangeItem mOwenAttr) {
			this.mOwenAttr = mOwenAttr;
		}
	}

	public static class CreditsExchangeItem {
		private int id;
		private String title;// : 10元 70M数据流量包,
		private int credits;// : 517,
		private int exchangeType;
		private String equalScor;
		private int parentId;// : 41,
		private int displayOrder;// : 2,
		private int status;// : 1

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getCredits() {
			return credits;
		}

		public void setCredits(int credits) {
			this.credits = credits;
		}

		public int getExchangeType() {
			return exchangeType;
		}

		public void setExchangeType(int exchangeType) {
			this.exchangeType = exchangeType;
		}

		public String getEqualScor() {
			return equalScor;
		}

		public void setEqualScor(String equalScor) {
			this.equalScor = equalScor;
		}

		public int getParentId() {
			return parentId;
		}

		public void setParentId(int parentId) {
			this.parentId = parentId;
		}

		public int getDisplayOrder() {
			return displayOrder;
		}

		public void setDisplayOrder(int displayOrder) {
			this.displayOrder = displayOrder;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

	}
}
