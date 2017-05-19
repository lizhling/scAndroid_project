package com.sunrise.marketingassistant.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.entity.MobileBusinessHallFromSearch;
import com.sunrise.marketingassistant.net.ServerClient;

/**
 * 渠道搜索-跟据用户当前经纬度，工号，名称等信息查看其周边2km的渠道信息,只能查询该工号下归属的渠道网点
 * 
 * @author 珩
 * 
 */
public class GetChannelSearchTaskByGroupId extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param isTude
	 * @param routePhoneNumber
	 *            手机号
	 * @param groupId
	 * @param loginNo
	 *            工号
	 * @param longiTude
	 * @param latiTude
	 * @param mobGrade
	 *            星级
	 * @param mobClass
	 *            网点类型
	 * @param startNum
	 * @param endNum
	 * @param listener
	 * @return
	 */
	public final GetChannelSearchTaskByGroupId execute(String routePhoneNumber, String searchStr, String loginNo, String mobGrade, String mobClass,
			int startNum, int endNum, TaskListener listener) {

		TaskParams params = new TaskParams("loginNo", loginNo);
		params.put("routePhoneNumber", routePhoneNumber);
		params.put("groupName", searchStr);
		params.put("mobGrade", mobGrade);
		params.put("mobClass", mobClass);
		params.put("startNum", String.valueOf(startNum));
		params.put("endNum", String.valueOf(endNum));
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String routePhoneNumber = params[0].getString("routePhoneNumber");
		String searchStr = params[0].getString("groupName");
		String loginNo = params[0].getString("loginNo");
		String mobGrade = params[0].getString("mobGrade");
		String mobClass = params[0].getString("mobClass");
		String startNum = params[0].getString("startNum");
		String endNum = params[0].getString("endNum");

		try {
			String result = ServerClient.getInstance().getChannelSearchByGroupId(routePhoneNumber, searchStr, loginNo, mobGrade, mobClass, startNum, endNum);

			JSONArray jsona = new JSONObject(result).getJSONArray("rows");

			ArrayList<MobileBusinessHall> array = new ArrayList<MobileBusinessHall>();
			for (int i = 0; i < jsona.length(); ++i) {
				MobileBusinessHallFromSearch mbh = JsonUtils.parseJsonStrToObject(jsona.getString(i), MobileBusinessHallFromSearch.class);
				array.add(mbh.toMobileBusinessHall());
			}
			publishProgress(array);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}
}
