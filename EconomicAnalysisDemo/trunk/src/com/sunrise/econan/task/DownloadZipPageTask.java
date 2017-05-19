package com.sunrise.econan.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.sunrise.econan.ExtraKeyConstant;
import com.sunrise.javascript.utils.FileUtils;

public class DownloadZipPageTask extends GenericTask implements ExtraKeyConstant {

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
	public final DownloadZipPageTask execute(String content, TaskListener listener) {
		setListener(listener);
		TaskParams params = new TaskParams("content", content);
		execute(params);
		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		if (params == null || params.length == 0)
			return TaskResult.AUTH_ERROR;
		String content = params[0].getString("content");

		InputStream is = null;
		try {
			String filename = content.substring(content.lastIndexOf('/') + 1);

			URL myURL = new URL(content);
			URLConnection conn = myURL.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.setReadTimeout(30 * 1000);
			is = conn.getInputStream();
			conn.connect();

			FileUtils.saveToFile(is, APP_SD_PATH_NAME, filename);

			String zipFolder = FileUtils.getAbsPath(APP_SD_PATH_NAME + File.separator + filename.substring(0, filename.indexOf('.')), null);
			if (FileUtils.upZip(FileUtils.getAbsPath(APP_SD_PATH_NAME, filename), zipFolder) == 0) {
				publishProgress("file://" + getFileFormDir(zipFolder, HTML_FILE_NAME));
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

	private String getFileFormDir(String dir, String fileLastName) {
		File file = new File(dir);
		if (file.isFile()) {
			return null;
		}

		File[] list = file.listFiles();
		for (File f : list) {
			if (f.isDirectory()) {
				String result = getFileFormDir(f.getAbsolutePath(), fileLastName);
				if (result != null) {
					return result;
				}
			} else if (f.getAbsolutePath().endsWith(fileLastName)) {
				return f.getAbsolutePath();
			}
		}

		return null;
	}
}
