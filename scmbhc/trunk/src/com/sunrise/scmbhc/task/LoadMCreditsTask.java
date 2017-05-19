package com.sunrise.scmbhc.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.PhoneFreeQuery;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;

/**
 * 读取积分/M值
 * 
 * @author fuheng
 * 
 */
public class LoadMCreditsTask extends GenericTask implements ExtraKeyConstant {

	/*
	 * @param listener 回调对象
	 * 
	 * @return 异步对象
	 */
	public void execute(TaskListener listener) {
		setListener(listener);
		execute();
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String phoneNumber = UserInfoControler.getInstance().getUserName();
		try {
			// 用户积分M值查询
			String score = App.sServerClient.getMScore(phoneNumber, UserInfoControler.getInstance().getAuthorKey());

			JSONObject json = new JSONObject(score);
			json = json.getJSONObject("RETURN");

			JSONArray jsonArray = json.getJSONArray("RETURN_INFO");
			score = json.getString("RETURN_MESSAGE");
			if (jsonArray != null && jsonArray.length() > 0) {
				json = jsonArray.getJSONObject(0);
				if (json.has("CUR_SCORE"))
					score = json.getString("CUR_SCORE");
			} else {
				score = score.substring(0, score.indexOf('。'));
				Pattern pattern = Pattern.compile("[^0-9]");
				Matcher matcher = pattern.matcher(score);
				score = matcher.replaceAll("");
			}

			publishProgress(score);
		} catch (JSONException e) {
			e.printStackTrace();
			setException(e);
			publishProgress(PhoneFreeQuery.EmumState.ANALYSIS_FAILED.getName());
			return TaskResult.FAILED;
		} catch (HttpException e) {
			e.printStackTrace();
			publishProgress(PhoneFreeQuery.EmumState.CONNECT_ERROR.getName());
			return TaskResult.FAILED;
		} catch (ServerInterfaceException e) {
			e.printStackTrace();
			publishProgress(e.getMessage());
			return TaskResult.FAILED;
		} catch (BusinessException e) {
			e.printStackTrace();
			publishProgress(e.getMessage());
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

	protected void onProgressUpdate(Object... values) {
		if (values != null && values.length > 0) {
			String score = values[0].toString();
			// if (TextUtils.isEmpty(score))
			// UserInfoControler.getInstance().setMCredits(PhoneFreeQuery.EmumState.CONNECT_ERROR.getCode());
			// else if (!score.startsWith("{"))
			// UserInfoControler.getInstance().setMCredits(PhoneFreeQuery.EmumState.CONNECT_ERROR.getCode());
			// else
			// try {
			// JSONObject json = new JSONObject(score);
			// json = json.getJSONObject("RETURN");
			// json = json.getJSONArray("RETURN_INFO").getJSONObject(0);
			// score = json.getString("CUR_SCORE");
			// if (!TextUtils.isEmpty(score))
			// UserInfoControler.getInstance().setMCredits(score);
			// } catch (Exception e) {
			// UserInfoControler.getInstance().setMCredits(PhoneFreeQuery.EmumState.ANALYSIS_FAILED.getName());
			// e.printStackTrace();
			// super.onProgressUpdate(e.getMessage());
			// return;
			// }
			if (!TextUtils.isEmpty(score))
				UserInfoControler.getInstance().setMCredits(score);
			super.onProgressUpdate(score);
		}
	}
	
	protected void onPreExecute(){
		TaskManager.getInstance().addTask(this);
		super.onPreExecute();
	}
	
	protected void onPostExecute(TaskResult result) {
		TaskManager.getInstance().deleteObserver(this);
		super.onPostExecute(result);
	}
}
