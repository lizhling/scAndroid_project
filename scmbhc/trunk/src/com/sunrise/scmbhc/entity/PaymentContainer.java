package com.sunrise.scmbhc.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PaymentContainer {
	private String name;
	private ArrayList<Payment> aArrPayment;

	public String getName() {
		return name;
	}

	public ArrayList<Payment> getPayment() {
		return aArrPayment;
	}

	public void addPayment(Payment payment) {
		if (aArrPayment == null)
			aArrPayment = new ArrayList<Payment>();
		aArrPayment.add(payment);
	}

	public PaymentContainer(String string) {
		try {
			init(string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void init(String string) throws JSONException {
		JSONArray jsonarr = new JSONArray(string);
		int length = jsonarr.length();
		for (int i = 0; i < length; ++i) {
			JSONObject json = jsonarr.getJSONObject(i);
			name = json.getString("@name");
			Object obj = json.get("payment");
			if (obj instanceof JSONObject) {
				Payment payment = new Payment();
				payment.init((JSONObject) obj);
				addPayment(payment);
			} else if (obj instanceof JSONArray) {
				analysisPayment((JSONArray) obj);
			}
		}
	}

	private void analysisPayment(JSONArray array) throws JSONException {
		int length = array.length();
		for (int i = 0; i < length; ++i) {
			Payment payment = new Payment();
			payment.init(array.getJSONObject(i));
			addPayment(payment);
		}
	}

	public static class Payment {
		private String account;
		private String icon;
		private String id;
		private String name;

		public String getAccount() {
			return account;
		}

		public String getIcon() {
			return icon;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Payment() {

		}

		public Payment(String string) {
			try {
				init(new JSONObject(string));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public void init(JSONObject json) throws JSONException {
			name = json.getString("@name");
			icon = json.getString("@icon");
			id = json.getString("@id");
			account = json.getString("@account");
		}

	}
}
