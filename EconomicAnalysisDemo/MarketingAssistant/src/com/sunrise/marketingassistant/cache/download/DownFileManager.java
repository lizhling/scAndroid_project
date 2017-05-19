package com.sunrise.marketingassistant.cache.download;

import java.io.IOException;
import com.sunrise.javascript.utils.FileUtils;

import android.content.Context;
import android.os.Handler;

public class DownFileManager {
private static FileDownLoader sFileDownLoader;
private static DownFileManager sInstance;

private DownFileManager(){
}

public static DownFileManager getInstance(){
	if(sInstance==null){
		sInstance=new DownFileManager();
		sFileDownLoader=FileDownLoader.getInstance();
	}
	return sInstance;
}

public void downFile(Context context,String downLoadUrl,String dir,String fileN,Handler handler,boolean isSaveFile,boolean isCach) throws IOException{
	String fileName;
	if(fileN==null)
		fileName=FileUtils.getFileNameByUrl(downLoadUrl);
	else
		fileName=fileN;
	if(sFileDownLoader.checkTheadIsFinished(fileName)){
		sFileDownLoader.startDownLoadThread(context,downLoadUrl, dir, fileName,handler,isSaveFile,isCach);
	}
	}



public void stopAllDownTask(){
	sFileDownLoader.clearAllDownTask();
}

}
