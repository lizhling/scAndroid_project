package com.sunrise.scmbhc.entity;

import java.util.ArrayList;

import android.text.TextUtils;

import com.sunrise.javascript.utils.JsonUtils;

/**
 * 已办业务列表
 * 
 * @author fuheng
 * 
 */
public class OpenedBusinessList {

	private ArrayList<AdditionalTariffInfo> tariffInfos;
	private ArrayList<AdditionalTariffInfo> mainTraffic;

	// public static OpenedBusinessList analysis1(String param) throws
	// JSONException {
	// JSONArray array = new
	// JSONObject(param).getJSONArray("RETURN_INFO").getJSONObject(0).getJSONArray("DETAIL_INFO");
	//
	// OpenedBusinessList list = new OpenedBusinessList();
	//
	// if (array.length() > 0) {
	// ArrayList<AdditionalTariffInfo> result = new
	// ArrayList<AdditionalTariffInfo>();
	// final int lenth = array.length();
	// for (int i = 0; i < lenth; ++i) {
	// AdditionalTariffInfo info = (AdditionalTariffInfo) JsonUtils
	// .parseJsonStrToObject(array.getJSONObject(i).toString(),
	// AdditionalTariffInfo.class);
	// if (info.getPROD_TYPE().equals("0")) {
	// list.setMainTraffic(info);
	// } else
	// result.add(info);
	// }
	// list.setTariffInfos(result);
	// }
	//
	// return list;
	// }

	/**
	 * @return 获取主资费之外的其它资费信息
	 */
	public ArrayList<AdditionalTariffInfo> getTariffInfos() {
		return tariffInfos;
	}

	public void setTariffInfos(ArrayList<AdditionalTariffInfo> mTariffInfos) {
		this.tariffInfos = mTariffInfos;
	}

	/**
	 * @return 获取主资费信息
	 */
	public AdditionalTariffInfo getMainTraffic() {
		if (mainTraffic == null || mainTraffic.isEmpty())
			return null;
		return mainTraffic.get(0);
	}

	public void setMainTraffic(ArrayList<AdditionalTariffInfo> mMainTraffic) {
		this.mainTraffic = mMainTraffic;
	}

	@Override
	public String toString() {
		return JsonUtils.writeObjectToJsonStr(this);
	}

	/**
	 * 通过与已有业务对照，判断business是否已开通。
	 * 
	 * @param business
	 * @return 判断business 是否已开通
	 */
	public boolean isContain(BusinessMenu business) {
		String string = business.getProdPrcid();
		if (TextUtils.isEmpty(string))
			return false;

		// 判断是否主资费代码
		if (getMainTraffic() != null && getMainTraffic().getPROD_PRCID() != null && getMainTraffic().getPROD_PRCID().equals(string)) {
			// business.setLevel(i % (length / 2));
			return true;
		}

		// 判断附加资费里
		if (tariffInfos != null && !tariffInfos.isEmpty()) {
			for (AdditionalTariffInfo info : tariffInfos)
				if (info.getPROD_PRCID() != null && info.getPROD_PRCID().equals(string)) {
					// business.setLevel(i % (length / 2));
					return true;
				}
		}

		return false;
	}

	public AdditionalTariffInfo getMainTrafficNext() {
		if (mainTraffic == null || mainTraffic.size() < 2)
			return null;
		return mainTraffic.get(1);
	}

}
