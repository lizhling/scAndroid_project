package com.starcpt.cmuc.utils;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.cache.download.DownLoadThread;

@SuppressLint("HandlerLeak")
public class AsyncImageLoader {
//private final static String TAG="AsyncImageLoader";
private Context mContext;
private boolean IsAlwaysFormNet=false;
public AsyncImageLoader(Context mContext) {
	super();
	this.mContext = mContext;
}


public void loadDrawable(final String url,final ImageCallBack callBack){
	
	final Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case DownLoadThread.DWONLOAD_FLIE_COMPLETE:
			Bitmap bitmap = null;
			byte[] datas=(byte[]) (msg.obj);
			if(datas!=null){
				bitmap=BitmapFactory.decodeByteArray(datas, 0, datas.length);	
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
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void getImageRes(final String url, final Handler handler) throws IOException{
	final String fileName=FileUtils.getFileNameByUrl(url);
	boolean isExist=FileUtils.fileIsExist(CmucApplication.APP_FILE_IMAGE_DATA_DIR, fileName);
		if(isExist){
			new Thread(){
				public void run() {	
						try {
							byte[] datas = FileUtils.readFiletoBytes(CmucApplication.APP_FILE_IMAGE_DATA_DIR,fileName);
							Message msg=handler.obtainMessage(0, datas);
							msg.what=DownLoadThread.DWONLOAD_FLIE_COMPLETE;
							handler.sendMessage(msg);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}.start();
		}else{
			CmucApplication.sDownFileManager.downFile(mContext,url, CmucApplication.APP_FILE_IMAGE_DATA_DIR,null,handler,!IsAlwaysFormNet,true);
		}		
}

public interface ImageCallBack{
	void loadImage(Bitmap d);
}

public void setIsAlwaysFormNet(boolean isAlwaysFormNet) {
	IsAlwaysFormNet = isAlwaysFormNet;
}

}




