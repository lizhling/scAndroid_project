package com.sunrise.marketingassistant.task;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.entity.MobileBusinessHall;
import com.sunrise.marketingassistant.net.ServerClient;

/**
 * 渠道搜索-跟据用户当前经纬度，工号，名称等信息查看其周边2km的渠道信息,只能查询该工号下归属的渠道网点
 * 
 * @author 珩
 * 
 */
public class MobInfoByGroupIdTask extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param routePhoneNumber
	 *            手机号
	 * @param groupId
	 * @param boss
	 *            BOSS工号（boss） 可选 boss暂时无用
	 * @param listener
	 * @return
	 */
	public final MobInfoByGroupIdTask execute(String routePhoneNumber, String groupId, String boss, TaskListener listener) {

		TaskParams params = new TaskParams("boss", boss);
		params.put("routePhoneNumber", routePhoneNumber);
		params.put("groupId", groupId);
		setListener(listener);
		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String routePhoneNumber = params[0].getString("routePhoneNumber");
		String groupId = params[0].getString("groupId");
		String boss = params[0].getString("boss");

		try {
			String result = ServerClient.getInstance().getGroupMsgInfo(routePhoneNumber, groupId, boss);
			LogUtlis.w("获取业务详情", result);
			JSONArray jarr = new JSONObject(result).getJSONArray("resultStr");
			if (jarr.length() > 0)
				publishProgress(JsonUtils.parseJsonStrToObject(jarr.getString(0), MobileBusinessHall.class));
			// {
			// "RETCODE": "0",
			// "RETMSG": "操作成功！",
			// "resultStr": [
			// {
			// "GROUP_NAME": "锦江顺吉通讯",
			// "LATITUDE": 30.644,
			// "LONGITUDE": 104.109,
			// "ACTIVE_TIME": "2010-06-21",
			// "GROUP_ADDRESS": "水碾河路6号附20号",
			// "CONTACT_PERSON": "0",
			// "CONTACT_PHONE": "0",
			// "GROUP_AREA": 0,
			// "CLASS_CODE": "4605",
			// "CLASS_NAME": "共享渠道",
			// "GRADE_CODE": "5",
			// "GRADE_NAME": "一星级",
			// "IMG_INFO": null,
			// "RWD_TOTAL": "201510:32.34;201509:31.42;201508:17.56;"
			// }
			// ],
			// "serialNumber": "201512162056631206"
			// }
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;
	}
}
