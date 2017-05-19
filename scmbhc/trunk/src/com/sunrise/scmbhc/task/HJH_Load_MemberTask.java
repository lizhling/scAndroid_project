package com.sunrise.scmbhc.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;

/**
 * 获取合家欢成员信息
 * 
 * @author fuheng
 * 
 */
public class HJH_Load_MemberTask extends GenericTask {

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		if (!UserInfoControler.getInstance().checkUserLoginIn()) {
			setException(new NullPointerException("请先登录！！！"));
			return TaskResult.AUTH_ERROR;
		}

		TaskResult result = TaskResult.OK;

		HJHResult hjhresult = new HJHResult();

		String userPhoneNumber = UserInfoControler.getInstance().getUserName();

		try {
			String mMenber = App.sServerClient.loadWholeFamilyMembers(userPhoneNumber, UserInfoControler.getInstance().getAuthorKey());
			JSONObject json = new JSONObject(mMenber).getJSONObject("RETURN");
			JSONArray array = json.getJSONArray("RETURN_INFO");
			JSONObject json2 = array.getJSONObject(0);

			String member_no = json2.getString("MEB_PHONESTR");
			if (member_no == null || member_no.trim().length() == 0 || member_no.trim().equals("null")) {
				hjhresult.setMenber(null);
			} else {
				hjhresult.setMenber(member_no);

			}
			hjhresult.setCaller(true);// 有问题
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			hjhresult.setCaller(false);
			result = TaskResult.FAILED;
		}
		publishProgress(hjhresult);

		if (!hjhresult.isCaller())
			try {
				String type = App.sServerClient.getWholeFamilyType(UserInfoControler.getInstance().getUserName(), UserInfoControler.getInstance()
						.getAuthorKey());
				JSONObject json = new JSONObject(type).getJSONObject("RETURN");
				JSONArray array = json.getJSONArray("RETURN_INFO");
				JSONObject json2 = array.getJSONObject(0);

				hjhresult.setMenber(json2.getString("MEB_PHONESTR"));
				if (!hjhresult.getMenber().contains(userPhoneNumber))
					hjhresult.getMenber().add(userPhoneNumber);

				publishProgress(hjhresult);

				result = TaskResult.OK;
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof BusinessException || e instanceof ServerInterfaceException) {
					super.setException(e);
				}
				result = TaskResult.FAILED;
			}

		return result;
	}

	public static class HJHResult implements Parcelable {
		boolean isCaller;
		String returnMessage;

		ArrayList<String> menber;

		public boolean isCaller() {
			return isCaller;
		}

		public ArrayList<String> getMenber() {
			return menber;
		}

		public void setCaller(boolean isCaller) {
			this.isCaller = isCaller;
		}

		public void setMenber(String memberInfo) {

			ArrayList<String> array = new ArrayList<String>();

			if (!TextUtils.isEmpty(memberInfo))
				for (int start = 0, end = 0; end != -1; start = end + 1) {
					end = memberInfo.indexOf('|', start);
					String temp = null;
					if (end == -1) {
						temp = memberInfo.substring(start);
					} else {
						temp = memberInfo.substring(start, end);
					}
					if (!TextUtils.isEmpty(temp))
						array.add(temp);
				}
			menber = array;
		}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub

		}

		public String getReturnMessage() {
			return returnMessage;
		}

		public void setReturnMessage(String returnMessage) {
			this.returnMessage = returnMessage;
		}

	}
}
