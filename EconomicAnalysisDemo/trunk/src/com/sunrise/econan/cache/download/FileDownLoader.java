package com.sunrise.econan.cache.download;

import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.os.Handler;

public class FileDownLoader {
//private static final String TAG="FileDownLoader";
private HashMap<String, DownLoadThread> mDownThreads;
private static FileDownLoader instance;

private FileDownLoader() {
	mDownThreads=new HashMap<String, DownLoadThread>();
}

public static FileDownLoader getInstance(){
	if(instance==null)
		instance=new FileDownLoader();
	return instance;
}

public void startDownLoadThread(Context context, 
		String downloadUrl, String saveDir,String fileName,Handler handler,boolean isSave,boolean isCach){
	DownLoadThread downLoadThread=new DownLoadThread(context, downloadUrl, saveDir, fileName,handler,isSave,isCach);
	mDownThreads.put(fileName, downLoadThread);
	downLoadThread.start();
}

public boolean checkTheadIsFinished(String threadId){
	DownLoadThread downLoadThread=mDownThreads.get(threadId);
	if(downLoadThread!=null){
		return downLoadThread.isFinish();
	}else
		return true;
}

public void stopDownloadThread(String threadId){
	DownLoadThread downLoadThread=mDownThreads.get(threadId);
	if(downLoadThread!=null&&!downLoadThread.isFinish()){
		downLoadThread.setFinish(true);
		downLoadThread.deleteFile();
	}
};

public void clearAllDownTask(){
	 Set<String> keys = mDownThreads.keySet(); 
	 for(String key:keys){
		 stopDownloadThread(key);
	 }
	 mDownThreads.clear();
}

}
