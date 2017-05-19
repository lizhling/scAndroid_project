package com.starcpt.cmuc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import com.sunrise.javascript.gson.Gson;
public class JsonUtils {
	//private final static String TAG="JsonUtils";
	public static <T> T  parseJsonStrToObject(String jsonData,Class<T> classOfT){
		Gson gson=new Gson();
		return gson.fromJson(jsonData, classOfT);
	}
	

	
	public static <T> T readJsonObjectFormFile(String filePath,Class<T> classOfT) throws JSONException, IOException{
		String jsonData=FileUtils.readToStringFormFile(filePath);
		Gson gson=new Gson();
		return gson.fromJson(jsonData, classOfT);
	}
	
	
	public static <T> T readJsonObjectFormNet(String filePath,Class<T> classOfT) throws IOException{
		String jsonStr=FileUtils.readToStringFormFile(filePath);
		Gson gson=new Gson();
		return gson.fromJson(jsonStr, classOfT);
	}
	
	public static JSONObject readJsonObjectFormCachedFile(Context context) throws FileNotFoundException, JSONException{
		JSONObject existJSON=null;
		File dir = context.getDir("libs", Context.MODE_PRIVATE);
		String fileName=context.getPackageName();
	    File cachedFile = new File(dir,fileName);
	    if(cachedFile.exists()){
	    	String jsonStr=FileUtils.readToStringFormInputStream(new FileInputStream(cachedFile));
	    	if(jsonStr.length()>0){
	    		 existJSON = new JSONObject(jsonStr);
	    	}
	    }
	    return existJSON;
	}
	
}
