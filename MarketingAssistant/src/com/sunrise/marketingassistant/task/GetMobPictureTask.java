package com.sunrise.marketingassistant.task;

import java.io.ByteArrayOutputStream;

import org.jivesoftware.smack.util.Base64;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.sunrise.javascript.utils.DateUtils;
import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.net.ServerClient;

public class GetMobPictureTask extends GenericTask implements ExtraKeyConstant {
	public View mView;
	public String mIconInfo;

	public final GetMobPictureTask execute(String subContent, String filsaccaptId, TaskListener listener) {
		TaskParams params = new TaskParams("filsaccaptId", filsaccaptId);
		params.put("subContent", String.valueOf(subContent));
		setListener(listener);

		mIconInfo = filsaccaptId;

		execute(params);

		return this;
	}

	public final GetMobPictureTask execute(View view, String subContent, String filsaccaptId, TaskListener listener) {
		TaskParams params = new TaskParams("filsaccaptId", filsaccaptId);
		params.put("subContent", String.valueOf(subContent));
		setListener(listener);

		mView = view;
		mIconInfo = filsaccaptId;

		execute(params);

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;

		String subContent = params[0].getString("subContent");
		String filsaccaptId = params[0].getString("filsaccaptId");

		Bitmap bt = readBitmapFromSD(filsaccaptId + ".png");
		if (bt != null) {
			publishProgress(bt);
			return TaskResult.OK;
		}
		try {
			String result = ServerClient.getInstance().getMobPicture(DateUtils.formatlong2Time(System.currentTimeMillis(), "yyyyMMdd"),
					DateUtils.formatlong2Time(System.currentTimeMillis(), "yyyyMMdd HH:mm:ss"), subContent, filsaccaptId);

			JSONObject jsobj = new JSONObject(result).getJSONObject("resultStr");
			if (jsobj.has("fileAcceptId") && jsobj.has("fileContent")) {
				String fileName = jsobj.getString("fileAcceptId");
				bt = turnBase64Str2Bitmap(jsobj.getString("fileContent"));
				publishProgress(bt);
				saveBitmap2File(bt, fileName + ".png");
			} else
				throw new Exception("服务器没有数据返回……");

		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		}
		return TaskResult.OK;

	}

	private Bitmap turnBase64Str2Bitmap(String base64str) {
		byte[] data = Base64.decode(base64str);
		Bitmap bt = BitmapFactory.decodeByteArray(data, 0, data.length);
		data = null;

		return bt;
	}

	private String saveBitmap2File(Bitmap bt, String fileName) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bt.compress(Bitmap.CompressFormat.JPEG, 100, baos)) {
			FileUtils.deleteFileByRelativePath(ExtraKeyConstant.APP_CHENNEL_IMAGE_DIR, fileName);
			return FileUtils.saveToFile(baos.toByteArray(), ExtraKeyConstant.APP_CHENNEL_IMAGE_DIR, fileName);
		}
		return "error";
	}

	private Bitmap readBitmapFromSD(String fileName) {// /storage/emulated/0/marketing_assistant/html/channelImage/319672
		return FileUtils.readBitmapFormFile(FileUtils.getAbsPath(ExtraKeyConstant.APP_CHENNEL_IMAGE_DIR, fileName));
	}
}
