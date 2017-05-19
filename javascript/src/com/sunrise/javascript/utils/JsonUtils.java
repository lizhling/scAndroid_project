package com.sunrise.javascript.utils;
import com.sunrise.javascript.gson.Gson;

public class JsonUtils {
	
	public static <T> T  parseJsonStrToObject(String jsonData,Class<T> classOfT){
		Gson gson=new Gson();
		return gson.fromJson(jsonData, classOfT);
	}
	
	public static String writeObjectToJsonStr(Object object){
		Gson gson=new Gson();
		String obj=gson.toJson(object);
		return obj;
	}
}
