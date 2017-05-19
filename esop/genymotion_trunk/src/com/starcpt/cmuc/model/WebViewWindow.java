package com.starcpt.cmuc.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sunrise.javascript.JavaScriptWebView;
public class WebViewWindow {
private byte[] bitmap;
private String title;
private JavaScriptWebView webView;
private int businessId;
private String url;
private String appTag;
private String menuId;
private boolean isBusinessWeb;
private boolean isFirstPage;
private String mMenuVersionKey;
private long mChildVersion;

//private WebViewFragment mWebViewFragment;

public WebViewWindow(byte[] bitmap,String title, JavaScriptWebView webView) {
	super();
	this.bitmap = bitmap;
	this.title = title;
	this.webView = webView;
}

public Bitmap getBitmap() {
	  return Bytes2Bitmap(bitmap);
}

private Bitmap Bytes2Bitmap(byte[] b) {  
    if (b!=null&&b.length != 0) {  
        return BitmapFactory.decodeByteArray(b, 0, b.length);  
    }  
    return null;  
}  

public byte[] getBitmapBytes() {
	return bitmap;
}

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public JavaScriptWebView getWebView() {
	return webView;
}

public void setWebView(JavaScriptWebView webView) {
	this.webView = webView;
}

public int getBusinessId() {
	return businessId;
}

public void setBusinessId(int businessId) {
	this.businessId = businessId;
}

public String getUrl() {
	return url;
}

public void setUrl(String url) {
	this.url = url;
}

public String getAppTag() {
	return appTag;
}

public void setAppTag(String appTag) {
	this.appTag = appTag;
}

public String getMenuId() {
	return menuId;
}

public void setMenuId(String menuId) {
	this.menuId = menuId;
}

public boolean isBusinessWeb() {
	return isBusinessWeb;
}

public void setBusinessWeb(boolean isBusinessWeb) {
	this.isBusinessWeb = isBusinessWeb;
}

public boolean isFirstPage() {
	return isFirstPage;
}

public void setFirstPage(boolean isFirstPage) {
	this.isFirstPage = isFirstPage;
}

public String getMenuVersionKey() {
	return mMenuVersionKey;
}

public void setMenuVersionKey(String mMenuVersionKey) {
	this.mMenuVersionKey = mMenuVersionKey;
}

public long getChildVersion() {
	return mChildVersion;
}

public void setChildVersion(long mChildVersion) {
	this.mChildVersion = mChildVersion;
}





}
