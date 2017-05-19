package com.sunrise.javascript.utils;

/**
 *@(#)ActivityUtils.java        0.01 2012/11/17
 *Copyright (c) 2012-3000 Sunrise, Inc.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.JavascriptHandler;
import com.sunrise.javascript.activity.CaptureImageActivity;
import com.sunrise.javascript.activity.IDCardImageActivity;
import com.sunrise.javascript.activity.TakePictureActivity;
import com.sunrise.javascript.utils.CommonUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

/**
 * Activity跳转和实名认证，捕捉图片
 * @version 0.01 September 21 2012
 * @author LIU WEI
 */

public class ActivityUtils {
	private final static String TAG="ActivityUtils";
	private Context mContext;
	private JavascriptHandler mJavascriptHandler;
	public static final int GO_LOGIN_REQUEST_CODE=1;
	public static final String CMUC_LOGIN_ACTION="com.starcpt.yxzs.LOGIN";
	public static final String TEST_LOGIN_ACTION="com.sunrise.javascript.LOGIN";
	public static final String WEB_GO_BROADCAST="com.sunrise.javascript.WEBGO";
	public static final String FIRST_PAGE_EXTRAL="isFirstPage";
	public static final String CAPTURE_IMAGE_ACTION="com.sunrise.javascript.CAPTURE";
	public static final String CAPTURE_TRANSFERIMAGE_ACTION="com.sunrise.javascript.TRANSFER";
	public static final String IDCARD_INFO_ACTION="com.sunrise.javascript.IDCARD";
	
	
	private final static String IMAGE_FILENAME = "channelImage.jpg";
	private final static String TXT_FILENAME="channelImage.txt";
	private final static String TAKE_IAMGEPATH_DIR = "cmuc"+File.separator+"html";
	
	private String key;
	private BroadcastReceiver mCaptureImageReciever = new BroadcastReceiver() {	
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(CAPTURE_IMAGE_ACTION)) {
				    String result=intent.getStringExtra(JavaScriptConfig.EXTRAL_BITMAP);
					CommonUtils.sendResult(result,key, mJavascriptHandler);
					context.unregisterReceiver(this);
			} else if (action.equals(CAPTURE_TRANSFERIMAGE_ACTION)){
				Log.i("excute:", "action");
				String result=intent.getStringExtra(JavaScriptConfig.EXTRAL_BITMAP_RESULT);
				Log.i("write action before:", "result:"+result);
				CommonUtils.sendResult(result,key, mJavascriptHandler);
				Log.i("write action after:", "result:"+result);
				context.unregisterReceiver(this);
			}
		}
			
	};
	
	private BroadcastReceiver mIdCardImageReciever = new BroadcastReceiver() {	
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(IDCARD_INFO_ACTION)) {
				    String result=intent.getStringExtra(JavaScriptConfig.EXTRAL_ID_CARD_INFO);
					CommonUtils.sendResult(result,key, mJavascriptHandler);
					context.unregisterReceiver(this);
			} 
		}
			
	};


	
	public ActivityUtils(Context mContext, JavascriptHandler mJavascriptHandler) {
		super();
		this.mContext = mContext;
		this.mJavascriptHandler = mJavascriptHandler;
	}


	private void registerCaptureCameraReciever(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(CAPTURE_IMAGE_ACTION);
		mContext.registerReceiver(mCaptureImageReciever, filter);
	}
	
	private void registerIdCardImageReciever(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(IDCARD_INFO_ACTION);
		mContext.registerReceiver(mIdCardImageReciever, filter);
	}
	
	/**
	 * 通过包民和Activity名字启动指定的activity
	 * @param pkg activity所在包的名字，如：com.starcpt.yxzs
	 * @param cls activity 全名 如：com.starcpt.yxzs.ui.activity.LoginActivity
	 * @throws IllegalArgumentException source 加密内容为空
	 */
	
	public void startActivity(String pkg,String cls){
		Intent intent=new Intent();
	    ComponentName componentName=new ComponentName(pkg, cls);
	    intent.setComponent(componentName);
	    ((Activity)mContext).startActivityForResult(intent, GO_LOGIN_REQUEST_CODE);
	}
	

	/**
	 * 通过action启动指定的activity
	 * @param action 值：com.starcpt.yxzs.LOGIN 智能统一终端登录；com.sunrise.javascript.LOGIN javascript api测试登录
	 * @throws IllegalArgumentException source 没在找到能执行此动作的activity
	 */
	
	public void startActivity(String action){
		try {
			Intent intent=new Intent();
		    intent.setAction(action);
			((Activity)mContext).startActivityForResult(intent, GO_LOGIN_REQUEST_CODE);			
		} catch (ActivityNotFoundException e) {
			throw new IllegalArgumentException("指定的操作不存在");
		}
	}
	
	/**
	 *
	 * 结束当前页
     * @param isFirstPage 是否返回的最后一页
	 * 
	 * */
	
	public void finish(boolean isFirstPage){
		Intent intent = new Intent(WEB_GO_BROADCAST);
        intent.putExtra(FIRST_PAGE_EXTRAL, isFirstPage);
        mContext.sendBroadcast(intent);
	}
	
	/**
	 * 实名认证
	 * @param key 本次调用标志，多次调用时候判断。
	 * */
	public void getIdCardInfo(String key){
		this.key=key;
		registerIdCardImageReciever();
		Intent ti = new Intent(mContext,IDCardImageActivity.class);
		ti.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		ti.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(ti);
	}
	
	/**
	 * 拍照获取图片
	 * @param key 本次调用标志，多次调用时候判断。
	 * */
	public void takePicture(String watermark,String key){
		registerCaptureCameraReciever();
		this.key=key;
		Intent ti = new Intent(mContext,TakePictureActivity.class);
		ti.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		ti.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ti.putExtra(TakePictureActivity.TAKE_PICTURE_WATERMARK_EXTRAL, watermark);
		mContext.startActivity(ti);
	}
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		return sdDir.toString();
	}
	/**
	 * 拍照获取图片
	 * @param key 本次调用标志，多次调用时候判断。
	 * */
	public void takePicture(String watermark, String type ,String key){
		registerCaptureCameraReciever();
		this.key=key;
		Intent ti = new Intent(mContext,TakePictureActivity.class);
		ti.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		ti.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ti.putExtra(TakePictureActivity.TAKE_PICTURE_PREFIX_EXTRAL, type);
		ti.putExtra(TakePictureActivity.TAKE_PICTURE_WATERMARK_EXTRAL, watermark);
		mContext.startActivity(ti);
	}
	
	/**
	 * @param fileStream
	 * @param tag  inter 室内 outer 室外 shop 店面
	 * @return
	 */
	public String strTransferImg (String fileStream, String tag){
		String reusltStr = "";
		// 读取字符串并 
		// 进行解码
		// 保存成图片
		// 构建返回字符串并返回
		return null;
	}
	
	
	/**
	 * 将base64加密后的图片数据解密再放到文件目录
	 * @param FileStream  加密后的图片文件字符串
	 * @param tag  标识 inter 室内 outer 室外 shop 店面
	 * @return json 字符串
	 */
	public void strTransferImg(final String FileStream, final String tag,final String key) {
		new Thread() {
			public void run() {
				String result=null;
				String baseStr=null;
				String FileStreamtemp = FileStream;
				byte image_encodebyte[] = null;
				String fileNamelocal = IMAGE_FILENAME;
				if (tag != null && tag.trim().length() > 0) {
					fileNamelocal = tag+IMAGE_FILENAME;
				}
				Log.i("filemsg", "file content:"+FileStreamtemp);
				String path = TAKE_IAMGEPATH_DIR+File.separator+"strTransferImage"+File.separator;
				Log.i("Activity", "path: "+path +"  name:"+fileNamelocal);
				if(FileStream == null){
					FileStreamtemp = FileUtils.getImageStr(TAKE_IAMGEPATH_DIR+File.separator+tag + TXT_FILENAME);
					
				}
				try 
		        {
		            //Base64解码
					// FileStream = FileStrem.trim();
					Pattern p = Pattern.compile("\\\\n|\t|\r|\n");
					Matcher m = p.matcher(FileStreamtemp);
					FileStreamtemp = m.replaceAll("");;
					int len = FileStreamtemp.getBytes().length;
					Log.i("Activity ：" ,"FileStream:"+FileStreamtemp.getBytes().toString() + ",lenth:"+FileStreamtemp.getBytes());
					//image_encodebyte=Base64.decode(FileStream.getBytes(), 0, len, Base64.NO_OPTIONS);
					image_encodebyte=Base64.decode(FileStreamtemp);
		            //生成jpeg图片
					//Log.i("Activity", "picturebyte:" + new String(image_encodebyte));
		            if(image_encodebyte!=null){
						FileUtils.deleteFileByRelativePath(path ,fileNamelocal);
						File file = new File(getSDPath()+File.separator+path+fileNamelocal);
						Log.i("ActivityUtils", "file is exits ?" +file.exists()+"," + getSDPath()+path+fileNamelocal);
						FileUtils.createSDDir(path);
						Log.i("ActivityUtils", "path:"+path);
						String txt = "";
						txt = new String(image_encodebyte);
						OutputStream out = new FileOutputStream(getSDPath()+File.separator+path+fileNamelocal);  
				        out.write(image_encodebyte);
				        out.flush();
				        out.close();
					}			
		        } 
		        catch (Exception e) 
		        {
		        	e.printStackTrace();
		        	image_encodebyte = null;
		        } finally {
		        	if (image_encodebyte != null ) {
		        		result = "{\"ImageFileName\":\""+fileNamelocal+"\",\"filePath\":\""+getSDPath()+ File.separator + path+"\",\"ImageIdentify\":\""+tag+"\"}";
		        	} else {
		        		result= "{}";
		        	}
		        	Log.i("ACtivity", "result:"+result);
		        	CommonUtils.sendResult(result,key, mJavascriptHandler);
		        }
			};
		}.start();
		
	}
	
	
	/**
	 * 将身份整图片转化成文本文件
	 * @param path 图片路径
	 * @param key 本次调用标志，多次调用时候判断。
	 * */
	public void getStringFromImage(final String path, final String key) {
		new Thread() {
			public void run() {
				String result=null;
				String baseStr=null;
				if(IDCardImageActivity.sImageByte!=null){
					baseStr=Base64.encodeBytes(IDCardImageActivity.sImageByte);
				}else{
					baseStr = FileUtils.getImageStr(IDCardImageActivity.IDCARDIAMGEPATHDIR+File.separator+IDCardImageActivity.IMAGE_FILENAME);
				}
				try {
					if(baseStr!=null){
					FileUtils.deleteFileByRelativePath(IDCardImageActivity.IDCARDIAMGEPATHDIR,IDCardImageActivity.TXT_FILENAME);
					FileUtils.saveToFile(baseStr,IDCardImageActivity.IDCARDIAMGEPATHDIR,IDCardImageActivity.TXT_FILENAME);
					}
				} catch (IOException e) {
					e.printStackTrace();
					baseStr=null;
				}				
				if(baseStr==null){
					result="decodeIdCardImageError";
				}else{
					result=IDCardImageActivity.TXT_FILENAME;
				}
				CommonUtils.sendResult(result, key,mJavascriptHandler);
			};
		}.start();
	}
	
	/**
	 * 
	 * 删除实名认证时候的身份证图片
	 * 
	 * */
	public void deleteIDCardImages(){
		FileUtils.deleteFileByRelativePath("cmuc/html/idCardImg", "idcardImage.jpg");
		FileUtils.deleteFileByRelativePath(IDCardImageActivity.IDCARDIAMGEPATHDIR,IDCardImageActivity.TXT_FILENAME);
		FileUtils.deleteFileByRelativePath("", "2x.jpg");
		FileUtils.deleteFileByRelativePath("", "2x1.jpg");
		IDCardImageActivity.sImageByte=null;
	}
	 //public String toString() { return "ActivityUtils"; } 
	 
 }
