package com.sunrise.micromarketing.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.sunrise.javascript.utils.FileUtils;
import com.sunrise.micromarketing.ExtraKeyConstant;

public class DownloadZipPage2Task extends GenericTask implements ExtraKeyConstant {

	/**
	 * @param phoneNumber
	 *            登录名
	 * @param password
	 *            登录密码
	 * @param imsi
	 *            sim卡号
	 * @param imei
	 *            设备号
	 * @param listener
	 *            回调对象
	 * @return 异步对象
	 */
	public final DownloadZipPage2Task execute(String content,String time,TaskListener listener) {
		setListener(listener);
		TaskParams params = new TaskParams();
		params.put(KEY_CONTENT, content);
		params.put(KEY_TIME, time);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;
		String content = params[0].getString(KEY_CONTENT);
		String time = params[0].getString(KEY_TIME);

		InputStream is = null;
		try {
			
			String filename=null;
			if(time!=null){
				filename = time+"_"+content.substring(content.lastIndexOf('/') + 1);
			}else{
				filename = content.substring(content.lastIndexOf('/') + 1);
			}
			

			URL myURL = new URL(content);
			URLConnection conn = myURL.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.setReadTimeout(30 * 1000);
			is = conn.getInputStream();
			conn.connect();

			FileUtils.saveToFile(is, APP_FILE_HTML_DATA_DIR, filename);
			

			String zipFolder = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR + File.separator + filename.substring(0, filename.indexOf('.')), null);
			if (FileUtils.upZip(FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR, filename), zipFolder) == 0) {
				publishProgress("file://" + FileUtils.searchFileFormDir(zipFolder, HTML_FILE_NAME));
			}
		} catch (Exception e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return TaskResult.OK;
	}

}
