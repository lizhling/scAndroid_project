package com.sunrise.marketingassistant.task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipException;

import com.sunrise.marketingassistant.ExtraKeyConstant;
import com.sunrise.marketingassistant.exception.http.HttpException;
import com.sunrise.marketingassistant.exception.logic.BusinessException;
import com.sunrise.marketingassistant.net.http.HttpClient;
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
	public DownloadZipPageTask execute(String content, TaskListener listener) {
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

		String filename = content.substring(content.lastIndexOf('/') + 1);

		InputStream is = null;
		try {

			is = new ByteArrayInputStream(HttpClient.getInstance().httpRequestForBytes(new URI(content)));
			FileUtils.saveToFile(is, APP_FILE_HTML_DATA_DIR, filename);
		} catch (HttpException e) {
			e.printStackTrace();
			try {
				URL myURL = new URL(content);
				URLConnection conn = myURL.openConnection();
				conn.setConnectTimeout(10 * 1000);
				conn.setReadTimeout(30 * 1000);
				is = conn.getInputStream();
				conn.connect();
				FileUtils.saveToFile(is, APP_FILE_HTML_DATA_DIR, filename);
			} catch (Exception e1) {
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			}
		} catch (IOException e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		} catch (BusinessException e) {
			e.printStackTrace();
			setException(e);
			return TaskResult.FAILED;
		} catch (URISyntaxException e) {
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

		String zipFolder = FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR + File.separator + filename.substring(0, filename.indexOf('.')), null);
		try {
			if (FileUtils.upZip(FileUtils.getAbsPath(APP_FILE_HTML_DATA_DIR, filename), zipFolder) == 0) {
				publishProgress("file://" + FileUtils.searchFileFormDir(zipFolder, HTML_FILE_NAME));
			}
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return TaskResult.OK;
	}

}
