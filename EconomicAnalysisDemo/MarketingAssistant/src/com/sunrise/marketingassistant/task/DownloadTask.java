package com.sunrise.marketingassistant.task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.sunrise.javascript.utils.FileUtils;

import android.content.Context;
import android.os.Handler;


public class DownloadTask extends GenericTask {

	private static final String KEY_URL = "download_url";
	private static final String KEY_IS_SAVE = "is_save_file";
	private static final String KEY_FILE_NAME = "filename";
	private static final String KEY_SAVE_DIR = "save_dir";
	private static final String KEY_IS_CACH = "is_cach";

	public DownloadTask execute(Context context, String downloadUrl, String saveDir, String fileName, Handler handler, boolean isSaveFile, boolean isCach,
			TaskListener listener) {

		return this;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		String downloadUrl = params[0].getString(KEY_URL);
		boolean isSaveFile = Boolean.valueOf(params[0].getString(KEY_IS_SAVE));
		String saveDir = params[0].getString(KEY_SAVE_DIR);
		String fileName = params[0].getString(KEY_FILE_NAME);
		boolean isCach = Boolean.valueOf(params[0].getString(KEY_IS_CACH));

		byte[] datas = null;
		FileOutputStream fos = null;
		ByteArrayOutputStream dos = null;
		InputStream is = null;
		boolean isError = false;
		File file = null;

		try {
			if (isSaveFile)
				file = FileUtils.createSDFile(saveDir, fileName);
			URL myURL = new URL(downloadUrl);
			HttpsURLConnection conn = (HttpsURLConnection) myURL.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(10 * 1000);
			is = conn.getInputStream();
			conn.connect();

			publishProgress(0);// 启动下载

			long fileSize = conn.getContentLength();
			long totalSize = 0;
			int downloadCount = 0;

			if (fileSize <= 0 || is == null) {
				return TaskResult.FAILED;
			}

			if (isSaveFile && file != null)
				fos = new FileOutputStream(file);
			if (isCach)
				dos = new ByteArrayOutputStream();

			byte buf[] = new byte[1024];
			do {
				int numread = is.read(buf);
				if (numread == -1) {
					break;
				}
				totalSize += numread;
				int tmp = (int) (totalSize * 100 / fileSize);
				if (downloadCount == 0 || tmp - 1 > downloadCount) {
					downloadCount += 1;
					publishProgress(downloadCount);
				}
				if (isCach && dos != null)
					dos.write(buf, 0, numread);
				if (isSaveFile && fos != null)
					fos.write(buf, 0, numread);
			} while (!true);
			is.close();
			if (dos != null) {
				dos.flush();
				dos.close();
			}
			if (fos != null) {
				fos.flush();
				fos.close();
			}
			if (isCach && dos != null)
				datas = dos.toByteArray();
		} catch (Exception e) {
			isError = true;
			e.printStackTrace();
			if (isSaveFile && file != null && file.exists())
				file.delete();
		} finally {
			try {
				if (is != null)
					is.close();
				if (dos != null)
					dos.close();
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				isError = true;
				e.printStackTrace();
			}
		}
		publishProgress(100);

		return TaskResult.OK;
	}

}
