package com.starcpt.cmuc.utils;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;

import com.starcpt.cmuc.exception.http.HttpException;
import com.sunrise.javascript.utils.LogUtlis;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class HttpDownLoader {
	static String TAG="HttpDownLoader";
	static DefaultHttpClient httpClient=new DefaultHttpClient();
	/**
	 * 
	 * @param url
	 * @return
	 */
public static String downLoadToString(String url){
	String temp=null;
	HttpResponse response;
	HttpEntity httpEntity=null;
	HttpGet httpGet=null;
	try {
		 httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler());
		 httpGet=new HttpGet(url);
	     httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);		 
		 response = httpClient.execute(httpGet);
		 if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
		 LogUtlis.d(TAG, response.getStatusLine().getReasonPhrase());
		 }
		else{
		httpEntity=response.getEntity();
		}
		 temp=FileUtils.readToStringFormInputStream(httpEntity.getContent());
	} catch (Exception e) {
		httpGet.abort();
		e.printStackTrace();
	}
	httpGet.abort();
	return temp;
	
}

public static File downLoadToFile(String Url,String destDir,String destFileName) throws ClientProtocolException, IOException, HttpException{
	 HttpResponse response;
	 HttpEntity httpEntity=null;
	 HttpGet httpGet=null;
	 File resultFile=null;
	 httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler());
	 httpGet=new HttpGet(Url);
	 response = httpClient.execute(httpGet);
	 if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
		 throw new HttpException("服务器繁忙，请稍后再试");
	 }
	else{
	httpEntity=response.getEntity();
	resultFile = FileUtils.writeToFileFormInputStream(destDir, destFileName,httpEntity.getContent());
	}
	 if(resultFile==null){
		 httpGet.abort();
		 return resultFile;
	}
	httpGet.abort();
	return resultFile;
}

/**
 * 
 * @param fileUrl
 * @param saveDir
 * @return
 * 		  the byte number of down load file
 * @throws IOException 
 */
public static byte[] downLoadFileFromNet(String fileUrl) throws IOException{
	ByteArrayOutputStream dos=new ByteArrayOutputStream();
	URL url = new URL(fileUrl);
	URLConnection conn = url.openConnection();
	conn.connect();		
	InputStream is = conn.getInputStream();
	byte buf[] = new byte[1024];
	int numread;
	while ((numread = is.read(buf)) != -1) {
		dos.write(buf,0,numread);	
	}
	is.close();
	dos.flush();
	dos.close();
	byte[] result=dos.toByteArray();
    return result;
}

public static Bitmap loadImageFromNet(String url) throws MalformedURLException{
	byte[] bytes;
	Bitmap bitmap = null;
	try {
		bytes = downLoadFileFromNet(url);
		bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return bitmap;
}

public static Bitmap loadImageFromLocal(String filePath) throws IOException{
    FileInputStream inputStream=new FileInputStream(filePath);
    Bitmap bitmap = BitmapFactory.decodeStream(inputStream); 
	return bitmap;
}

public static Bitmap loadImageFromAssertDir(Context context,String fileName) throws IOException{  
    Bitmap image = null;  
    AssetManager am = context.getAssets();  
    InputStream is = am.open(fileName);  
    image = BitmapFactory.decodeStream(is);  
    is.close();  
    return image;  
} 

public static Bitmap downLoadImageFromNet(String url,String dir) throws IOException{
	String fileName=FileUtils.getFileNameByUrl(url);
	byte bytes[]=HttpDownLoader.downLoadFileFromNet(url);
	FileUtils.writeToFileFormInputStream(bytes, dir, fileName);
	Bitmap bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    return bitmap;
}


}
