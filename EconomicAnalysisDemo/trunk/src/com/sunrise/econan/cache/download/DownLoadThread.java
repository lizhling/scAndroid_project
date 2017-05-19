package com.sunrise.econan.cache.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sunrise.javascript.utils.FileUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class DownLoadThread extends Thread {
	// private static final String TAG="DownLoadThread";
	public final static int DWONLOADING_FILE = 0;
	public final static int DWONLOAD_FLIE_COMPLETE = 1;
	public final static int DWONLOAD_FLIE_ERROR = 2;
	public final static int START_DOWN_FILE = 3;
	private boolean finish = false;
	private String downloadUrl;
	private String saveDir;
	private String fileName;
	private Context mContext;
	private Handler mHandler;
	private boolean isSaveFile;
	private boolean isCach;
	private File file;

	public DownLoadThread(Context context, String downloadUrl, String saveDir, String fileName, Handler handler, boolean isSaveFile, boolean isCach) {
		super();
		this.downloadUrl = downloadUrl;
		this.saveDir = saveDir;
		this.fileName = fileName;
		this.mContext = context;
		this.mHandler = handler;
		this.isSaveFile = isSaveFile;
		this.isCach = isCach;
	}

	@Override
	public void run() {
		down();
	}

	protected void down() {
		byte[] datas = null;
		FileOutputStream fos = null;
		ByteArrayOutputStream dos = null;
		InputStream is = null;
		boolean isError = false;
		Message msg = mHandler.obtainMessage(START_DOWN_FILE);
		mHandler.sendMessage(msg);
		if (downloadUrl.contains("http")) {
			try {
				if (isSaveFile)
					file = FileUtils.createSDFile(saveDir, fileName);
				URL myURL = new URL(downloadUrl);
				HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
				conn.setConnectTimeout(10 * 1000);
				is = conn.getInputStream();
				conn.connect();

				long fileSize = conn.getContentLength();
				long totalSize = 0;
				int downloadCount = 0;

				if (fileSize <= 0 || is == null) {
					finish = true;
					return;
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
						msg = mHandler.obtainMessage();
						msg.what = DWONLOADING_FILE;
						msg.arg1 = downloadCount;
						mHandler.sendMessage(msg);
					}
					if (isCach && dos != null)
						dos.write(buf, 0, numread);
					if (isSaveFile && fos != null)
						fos.write(buf, 0, numread);
				} while (!finish);
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
		} else {
			try {
				InputStream inputStream = mContext.getAssets().open(downloadUrl);
				datas = FileUtils.readFiletoByte(inputStream);
				FileUtils.saveToFile(mContext.getAssets().open(downloadUrl), saveDir, fileName);
			} catch (IOException e) {
				e.printStackTrace();
				isError = true;
			}
		}
		finish = true;
		msg = mHandler.obtainMessage(DWONLOAD_FLIE_ERROR);
		if (!isError) {
			msg = mHandler.obtainMessage(DWONLOAD_FLIE_COMPLETE);
			if (isCach) {
				msg = mHandler.obtainMessage(DWONLOAD_FLIE_COMPLETE, datas);
			}
		}
		mHandler.sendMessage(msg);
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public void deleteFile() {
		if (isSaveFile && file != null && file.exists())
			file.delete();
	}

}
