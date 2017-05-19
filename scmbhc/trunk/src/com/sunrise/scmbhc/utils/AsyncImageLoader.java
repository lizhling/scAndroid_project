package com.sunrise.scmbhc.utils;

import java.io.IOException;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.cache.download.DownLoadThread;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class AsyncImageLoader {
	// private final static String TAG="AsyncImageLoader";
	private Context mContext;
	private boolean IsAlwaysFormNet = false;

	public AsyncImageLoader(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public void loadDrawable(final String url, final ImageCallBack callBack) {

		final Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case DownLoadThread.DWONLOAD_FLIE_COMPLETE:
					Bitmap bitmap = null;
					byte[] datas = (byte[]) (msg.obj);
					if (datas != null) {
						bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
					}
					callBack.loadImage(bitmap);
					break;
				default:
					break;
				}
			};
		};
		try {
			getImageRes(url, handler);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getImageRes(final String url, final Handler handler) throws IOException {
		String fileName = FileUtils.getFileNameByUrl(url);
		final String filename = fileName;
		boolean isExist = FileUtils.fileIsExist(App.AppDirConstant.APP_IMAGE_DATA_DIR, fileName);
		if (isExist) {
			new Thread() {
				public void run() {
					try {
						byte[] datas = FileUtils.readFiletoBytes(App.AppDirConstant.APP_IMAGE_DATA_DIR, filename);
						boolean isBitmapComplete = false;// 判断图片文件是否全，不全则重新下载
						if (datas.length > 0) {
							for (int i = datas.length - 1; i > datas.length - 64; --i) {
								if (datas[i] != 0) {
									isBitmapComplete = true;
									break;
								}
							}
						}

						if (isBitmapComplete) {
							Message msg = handler.obtainMessage(DownLoadThread.DWONLOAD_FLIE_COMPLETE, datas);
							handler.sendMessage(msg);
						} else {// 如果图片数据不全，删除文件，发送文件读取失败信息
							FileUtils.deleteFileByRelativePath(App.AppDirConstant.APP_IMAGE_DATA_DIR, filename);
							App.sDownFileManager.downFile(mContext, url, App.AppDirConstant.APP_IMAGE_DATA_DIR, filename, handler, !IsAlwaysFormNet, true);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			App.sDownFileManager.downFile(mContext, url, App.AppDirConstant.APP_IMAGE_DATA_DIR, filename, handler, !IsAlwaysFormNet, true);
		}
	}

	public interface ImageCallBack {
		void loadImage(Bitmap d);
	}

	public void setIsAlwaysFormNet(boolean isAlwaysFormNet) {
		IsAlwaysFormNet = isAlwaysFormNet;
	}

}