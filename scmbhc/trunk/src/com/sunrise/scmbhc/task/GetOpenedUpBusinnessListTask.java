package com.sunrise.scmbhc.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.AdditionalTariffInfo;
import com.sunrise.scmbhc.entity.OpenedBusinessList;

/**
 * 获得已开通业务列表
 * 
 * @author fuheng
 * 
 */
public class GetOpenedUpBusinnessListTask extends GenericTask implements TaskListener {

	private boolean isOverloadTask2;
	private GetOpenedUpBusinnessList2Task mTask;
	private ArrayList<AdditionalTariffInfo> mArrayTariffInof;

	public GetOpenedUpBusinnessListTask execute(TaskListener taskListener) {
		setListener(taskListener);
		execute();
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String phoneNumber = UserInfoControler.getInstance().getUserName();

		if (TextUtils.isEmpty(phoneNumber)) {
			setException(new NullPointerException("您尚未登录，请先登录。"));
			return TaskResult.NOT_FOLLOWED_ERROR;
		}

		startOpenedUpBusinessList2();

		ArrayList<AdditionalTariffInfo> arrayTariffInof = null;
		try {
			String result = App.sServerClient.loadAdditionalTariffInfo(phoneNumber, UserInfoControler.getInstance().getAuthorKey());
			JSONObject json = new JSONObject(result).getJSONObject("RETURN");
			boolean isArray = json.getInt("IS_ARRAY") == 1;
			JSONArray array = json.getJSONArray("RETURN_INFO");
			if (isArray)
				array = array.getJSONObject(0).getJSONArray("DETAIL_INFO");

			arrayTariffInof = analysisAdditionalTariffInfo(array.toString());

			// 等待第二个线程结束
			while (!isOverloadTask2)
				Thread.sleep(100);

			arrayTariffInof = compressTwoArray(arrayTariffInof, mArrayTariffInof);
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}

		publishProgress(createOpenedBusinessList(arrayTariffInof));

		return TaskResult.OK;
	}

	private ArrayList<AdditionalTariffInfo> compressTwoArray(ArrayList<AdditionalTariffInfo> array1, ArrayList<AdditionalTariffInfo> array2) {

		for (AdditionalTariffInfo a1 : array1) {
			for (AdditionalTariffInfo a2 : array2) {
				if (a1.getPROD_PRCID().equals(a2.getPROD_PRCID())) {
					a1.eat(a2);
					break;
				}
			}
		}

		return array1;
	}

	private void startOpenedUpBusinessList2() {
		GetOpenedUpBusinnessList2Task task = new GetOpenedUpBusinnessList2Task();
		task.setListener(this);
		task.execute();
		mTask = task;
	}

	protected void onProgressUpdate(Object... values) {
		if (values != null && values.length > 0)
			UserInfoControler.getInstance().setAdditionalTariffInfo((OpenedBusinessList) values[0]);

		super.onProgressUpdate(values);
	}
	
	protected void onPreExecute(){
		TaskManager.getInstance().addTask(this);
		super.onPreExecute();
	}
	
	protected void onPostExecute(TaskResult result) {
		TaskManager.getInstance().deleteObserver(this);
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled() {
		mTask.cancle();
		super.onCancelled();
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		mArrayTariffInof = analysisAdditionalTariffInfo((String) param);
	}

	@Override
	public void onPreExecute(GenericTask task) {
		isOverloadTask2 = false;
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		isOverloadTask2 = true;
		if (result == TaskResult.OK) {
		} else {
		}
	}

	@Override
	public void onCancelled(GenericTask task) {
		isOverloadTask2 = true;
	}

	@Override
	public String getName() {
		return null;
	}

	private OpenedBusinessList createOpenedBusinessList(ArrayList<AdditionalTariffInfo> array) {
		if (array.isEmpty())
			return null;

		OpenedBusinessList list = new OpenedBusinessList();

		ArrayList<AdditionalTariffInfo> arrayTarifInfos = new ArrayList<AdditionalTariffInfo>();
		ArrayList<AdditionalTariffInfo> mainTariffInfos = new ArrayList<AdditionalTariffInfo>();
		for (AdditionalTariffInfo info : array)
			if (info.getPROD_TYPE().equals("0"))
				if (!mainTariffInfos.isEmpty() && checkTariffIsEndInThisWeek(info))
					mainTariffInfos.add(0, info);
				else
					mainTariffInfos.add(info);
			else
				arrayTarifInfos.add(info);
		list.setMainTraffic(mainTariffInfos);
		list.setTariffInfos(arrayTarifInfos);
		return list;
	}

	private ArrayList<AdditionalTariffInfo> analysisAdditionalTariffInfo(String str) {
		ArrayList<AdditionalTariffInfo> arrayAdditional = new ArrayList<AdditionalTariffInfo>();
		try {
			JSONArray jarray = new JSONArray(str);
			for (int i = 0; i < jarray.length(); ++i) {
				AdditionalTariffInfo item = JsonUtils.parseJsonStrToObject(jarray.get(i).toString(), AdditionalTariffInfo.class);
				arrayAdditional.add(item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrayAdditional;
	}

	/**
	 * @param info
	 * @return 业务是否本月结束
	 */
	private static final boolean checkTariffIsEndInThisWeek(AdditionalTariffInfo info) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String curMonth = sdf.format(new Date(System.currentTimeMillis()));
		return info.getEXP_DATE().startsWith(curMonth);
	}

	/**
	 * 获取第二张表格
	 * 
	 * @author 珩
	 * @version 2014年11月26日 10:48:03
	 */
	class GetOpenedUpBusinnessList2Task extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			String phoneNumber = UserInfoControler.getInstance().getUserName();

			try {// 获取分类的其他业务信息。
				String result = App.sServerClient.getMyBusinessInfo(phoneNumber, UserInfoControler.getInstance().getAuthorKey());

				JSONObject json = new JSONObject(result).getJSONObject("RETURN");
				boolean isArray = json.getInt("IS_ARRAY") == 1;
				JSONArray array = json.getJSONArray("RETURN_INFO");
				if (isArray)
					array = array.getJSONObject(0).getJSONArray("DETAIL_INFO");

				publishProgress(array.toString());
			} catch (Exception e) {
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			}

			return TaskResult.OK;
		}
	}
}
