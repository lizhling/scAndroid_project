package com.sunrise.micromarketing.net.http.utils;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

public class UrlUtils {
	public static String buildQueryString(Map<String,String> map){
		List<NameValuePair> pairs = buildNameValuePairs(map);
		String queryStr = URLEncodedUtils.format(pairs, "UTF-8");
		return queryStr;
	}
	
	public static String buildQueryString(List<NameValuePair> pairs){
		String queryStr="";
		int size=pairs.size();
		for(int i=0;i<size;i++){
			NameValuePair pair=pairs.get(i);
			String name=pair.getName();
			String value=pair.getValue();
			if(i<size-1)
			queryStr+=name+"="+value+"&";
			else
			queryStr+=name+"="+value;
		}
		return queryStr;
	}
	
	public static String buildUrlByQueryStringAndBaseUrl(String baseUrl,String queryString){
		return baseUrl + "?" + queryString;
	}
	public static String buildUrlByQueryStringMapAndBaseUrl(String baseUrl,Map<String,String> map){
		return buildUrlByQueryStringAndBaseUrl(baseUrl, buildQueryString(map));
	}
	
	public static List<NameValuePair> buildNameValuePairs(Map<String,String> map){
		Set<String> keySet = map.keySet();
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for(String key: keySet){
			NameValuePair pair = new BasicNameValuePair(key, map.get(key));
			pairs.add(pair);
		}
		return pairs;
	}
	
}
