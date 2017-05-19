package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.cache.preference.Preferences;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.net.ServerClient;
import com.sunrise.javascript.utils.LogUtlis;

public class GetChannelSearchTaskByLatLng extends GenericTask implements ExtraKeyConstant {

	public final GetChannelSearchTaskByLatLng execute(String mobile, String subAccount, String groupId, double longitude, double latitude, String keyword,
			TaskListener listener) {

		TaskParams params = new TaskParams("mobile", mobile);
		params.put("subAccount", subAccount);
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		params.put("groupId", groupId);
		params.put("keyword", keyword);
		setListener(listener);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String mobile = params[0].getString("mobile");
		String subAccount = params[0].getString("subAccount");
		double longitude = (Double) params[0].get("longitude");
		double latitude = (Double) params[0].get("latitude");
		String groupId = params[0].getString("groupId");
		String keyword = params[0].getString("keyword");

		String temp = Preferences.getInstance(null).getCacheString(KEY_CACHE_HALL_NEARBY_LATLNG);
		if (!TextUtils.isEmpty(temp) && hasRecordInRange200(latitude, longitude, 200)) {
			ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
			try {
				JSONArray jsonArray = new JSONArray(temp);
				for (int i = 0; i < jsonArray.length(); i++)
					array.add(JsonUtils.parseJsonStrToObject(jsonArray.getString(i), MobileBusinessHall.class));
				publishProgress(array);
				return TaskResult.OK;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			String result = ServerClient.getInstance().getChannelSearchByLatLng(mobile, groupId, subAccount, String.valueOf(longitude),
					String.valueOf(latitude), keyword);

			// {"retCode":"0","retMsg":"操作完成","detailMsg":"操作完成","rowNmu":"2151","rows":[],"serialNumber":"201512101044197051"}
			// {"retCode":"0","retMsg":"操作完成","detailMsg":"操作完成","rowNmu":"36985","rows":[],"serialNumber":"201512111659366719"}

			JSONArray jsona = new JSONObject(result).getJSONArray("resultStr");
			ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
			for (int i = 0; i < jsona.length(); ++i) {
				MobileBusinessHall mbh = JsonUtils.parseJsonStrToObject(jsona.getString(i), MobileBusinessHall.class);
				array.add(mbh);
			}

			LogUtlis.e("渠道查询", "结果:" + result);

			// if (App.isTest)
			// array.add(getTestMobileBusinessHall());

			publishProgress(array);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

	private MobileBusinessHall getTestMobileBusinessHall() {
		MobileBusinessHall mb = new MobileBusinessHall();
		mb.setGROUP_ID("1286164");
		mb.setLATITUDE(23.11189686786069);// "30.686"
		mb.setLONGITUDE(113.32669276382101);// 104.085

		mb.setCLASS_NAME("新型营业厅-自建他营厅");
		mb.setCLASS_CODE("4102");
		mb.setGRADE_NAME("无星级");
		mb.setGROUP_NAME("金牛分公司金仙桥营业厅速成通讯（自建他营）");
		mb.setGRADE_CODE("99");
		return mb;
	}

	private boolean hasRecordInRange200(double latitude, double longitude, int range) {
		String _latitude = Preferences.getInstance(null).getCacheString(KEY_MY_LOCATION_LATITUDE);
		String _longitude = Preferences.getInstance(null).getCacheString(KEY_MY_LOCATION_LONGITUDE);
		if (!TextUtils.isEmpty(_latitude)) {
			double distance = DistanceUtil.getDistance(new LatLng(latitude, longitude),
					new LatLng(Double.parseDouble(_latitude), Double.parseDouble(_longitude)));
			if (distance < range) {
				return true;
			}
		}

		return false;
	}
}
