package com.starcpt.cmuc.ui.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.androidpn.client.ServiceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.cache.download.DownLoadThread;
import com.starcpt.cmuc.cache.preferences.Preferences;
import com.starcpt.cmuc.model.Item;
import com.starcpt.cmuc.model.WebViewWindow;
import com.starcpt.cmuc.service.LockScreenService;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.utils.FileUtils;

public class CommonActions {
	 private static List<Activity> sActivityList = new ArrayList<Activity>(); 
	// private static ProgressDialog mPd;
	 
/*	 private static Handler mHandler=new Handler(){
			public void handleMessage(android.os.Message msg) {
					exit();
					if(mPd!=null)
					mPd.dismiss();
			};
		};*/
		
		/** 
	     * 退出客户端。 
	     * 
	     * @param context 上下文 
	     */ 
	    public static void exit() 
	    { 
	    	try{
		        for (int i = 0; i < sActivityList.size(); i++) 
		        { 
		            if (null != sActivityList.get(i)) 
		            { 
		                sActivityList.get(i).finish(); 
		            } 
		        } 
	    	}catch(Exception e){
	    	}
	    	((CmucApplication)CmucApplication.sContext).setApplicationRunning(false);
			CmucApplication.sLockScreenShowing=false;
			CmucApplication.sNeedShowLock=false;
	    	CmucApplication.sContext.stopService(new Intent(CmucApplication.sContext,LockScreenService.class));
	    	deleteIDCardImages();
	    	clearCache(false);
	    	ArrayList<WebViewWindow> webViews=((CmucApplication)CmucApplication.sContext).getWebViewWindows();
	    	if(webViews!=null)
	    		webViews.clear();
	    }
	    
	    public static void clearUserInfo(){
	    	try{
		        for (int i = 0; i < sActivityList.size(); i++) 
		        { 
		            if (null != sActivityList.get(i)) 
		            { 
		                sActivityList.get(i).finish(); 
		            } 
		        } 
	    	}catch(Exception e){
	    	}
	    	deleteIDCardImages();
	    	clearCache(true);
	    }
	    
	    private static void deleteIDCardImages(){
			FileUtils.deleteFileByRelativePath("cmuc/html/idCardImg", "idcardImage.jpg");
			FileUtils.deleteFileByRelativePath("", "2x.jpg");
			FileUtils.deleteFileByRelativePath("", "2x1.jpg");
		}

	    public static void setApplicationScreenDirection() 
	    { 
	        for (int i = 0; i < sActivityList.size(); i++) 
	        { 
	            if (null != sActivityList.get(i)) 
	            { 
	               Activity activity=sActivityList.get(i);
	               setScreenOrientation(activity);                
	            } 
	        } 	    	
	    	
	    }
	    
	    public static void clearCache(boolean isCleanUser){
	    	CmucApplication cmucApplication=(CmucApplication) CmucApplication.sContext;
	    	cmucApplication.clearDataPackages();
	    	Preferences preferences=cmucApplication.getSettingsPreferences();
	    	if(!cmucApplication.getAppTag().equals(CmucApplication.YXZS_APP_TAG)||isCleanUser)
	    	preferences.clearUserInfo();
	    	cmucApplication.getWebViewWindows().clear();
	    	cmucApplication.clearSubAccounts();
	    	CmucApplication.sDownFileManager.stopAllDownTask();
	    }
	    
	    public static Spanned getMenuTracks(String poses) {
			String tracks = null;
			Spanned tracksSpanned = null;
			String head = "您当前的位置：";
			if (poses != null) {
				String[] positions=poses.split(">>");
				int length = positions.length;
				if (length != 0) {
					for (int i = 0; i < length; i++) {
						head += (i == length - 1) ? ("<font color=#000000>"
								+ positions[i] + "</font>")
								: "<font color=#CCCCCC>" + positions[i]
										+ ">>" + "</font>";
					}
					tracks = head;
				}
			}
			if (tracks != null)
				tracksSpanned = Html.fromHtml(tracks);
			return tracksSpanned;
		}
	    
		/** 
	     * 退出客户端。 
	     * 
	     * @param context 上下文 
	     */ 
	    public static void exitClient(Activity activity) 
	    { 
	    	CmucApplication cmucApplication=(CmucApplication) CmucApplication.sContext;
	    	String authentication=cmucApplication.getSettingsPreferences().getAuthentication();
	    	if(authentication!=null&&authentication.length()>1){
	    		if(!cmucApplication.getAppTag().equals(CmucApplication.YXZS_APP_TAG)){
	    		logout(null,activity);
	    		}
	    	}
	    	    exit();
	    	
	    }
	    
	    
	    public static void logout(final Handler handler,final Activity activity){
   	    	  new Thread(){
   		        	public void run() {
 			        	if(handler!=null)
 	   			     	    handler.sendEmptyMessage(0);
   		        		CmucApplication cmucApplication=(CmucApplication) CmucApplication.sContext;
   		                String authentication=cmucApplication.getSettingsPreferences().getAuthentication();
   			        	CmucApplication.sServerClient.logout(authentication);
   		        	};
   		        }.start();
	    }
	    
	   public static void addActivity(Activity activity){
		   sActivityList.add(activity);
	   }
		
	   public static void reMoveActivity(Activity activity){
		   sActivityList.remove(activity);
	   }
	   
		public static AlertDialog createTwoBtnMsgDialog(final Context context,String title,String msg,String okBtnTxt,String cancelBtnTxt,final OnTwoBtnDialogHandler handle,boolean isFeedback){
			final AlertDialog dialog = new AlertDialog.Builder(context).create();
			dialog.show();
			dialog.setContentView(R.layout.two_button_dialog);
			TextView ttl = (TextView) dialog.findViewById(R.id.tvTitle);
			TextView message = (TextView) dialog.findViewById(R.id.msg);
			Button confirm = (Button) dialog.findViewById(R.id.confirm);
		    Button cancel = (Button) dialog.findViewById(R.id.cancel);
		    Button feedBack=(Button) dialog.findViewById(R.id.dialog_feedback_btn);
		    confirm.setBackgroundDrawable(SkinManager.getGreenBtnDrawable(context));
		    cancel.setBackgroundDrawable(SkinManager.getGreyBtnDrawable(context));
		    feedBack.setBackgroundDrawable(SkinManager.getLightGreyDrawable(context));
		    ttl.setTextColor(SkinManager.getSkinColor(context, R.color.dilog_title_text));
		    dialog.findViewById(R.id.two_button_dialog).setBackgroundDrawable(SkinManager.getSkinDrawable(context, R.drawable.dialog_bg));
		    dialog.findViewById(R.id.dialog_title_bar).setBackgroundDrawable(SkinManager.getSkinDrawable(context, R.drawable.dialog_title_bar));
		    if(isFeedback){
		    	feedBack.setVisibility(View.VISIBLE);
		    }
		    
		    feedBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(context, FeedBacksActivity.class);
					context.startActivity(intent);	
				}
			});
		    
		    if(!TextUtils.isEmpty(title))
		    	ttl.setText(title);
		    if(!TextUtils.isEmpty(msg))
		    	message.setText(msg);
		    if(!TextUtils.isEmpty(okBtnTxt))
		    	confirm.setText(okBtnTxt);
		    if(!TextUtils.isEmpty(cancelBtnTxt))
		    	cancel.setText(cancelBtnTxt);
		    
		    confirm.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(handle != null)
						handle.onPositiveHandle(dialog,v);
				}
			});
		    
		    cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(handle != null)
						handle.onNegativeHandle(dialog,v);
				}
			});
		    dialog.setCanceledOnTouchOutside(false);
		    dialog.setCancelable(false);
			return dialog;
		}
		
		public static AlertDialog createSingleBtnMsgDialog(Context context,String title,String msg,String okBtnTxt,final OnTwoBtnDialogHandler handle){
			final AlertDialog dialog = new AlertDialog.Builder(context).create();
			dialog.show();
			dialog.setContentView(R.layout.single_button_dialog);
			TextView ttl = (TextView) dialog.findViewById(R.id.tvTitle);
			TextView message = (TextView) dialog.findViewById(R.id.msg);
			Button confirm = (Button) dialog.findViewById(R.id.confirm);
		    if(!TextUtils.isEmpty(title))
		    	ttl.setText(title);
		    if(!TextUtils.isEmpty(msg))
		    	message.setText(msg);
		    if(!TextUtils.isEmpty(okBtnTxt))
		    	confirm.setText(okBtnTxt);
		    
		    confirm.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(handle != null)
						handle.onPositiveHandle(dialog,v);
				}
			});
		    dialog.setCanceledOnTouchOutside(false);
		    dialog.setCancelable(false);
			return dialog;
		}
		
		public interface OnTwoBtnDialogHandler{
			public void onPositiveHandle(Dialog dialog,View v);
			public void onNegativeHandle(Dialog dialog,View v);
		}
		
		public static void createNetSettingDialog(final Activity activity) {
			 CommonActions.createTwoBtnMsgDialog(activity,
					activity.getString(R.string.set_network), 
					activity.getString(R.string.network_error), 
					activity.getString(R.string.set_network), 
					activity.getString(R.string.exit), 
					new CommonActions.OnTwoBtnDialogHandler() {
						
						@Override
						public void onPositiveHandle(Dialog dialog,View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent("/");
							ComponentName cm = new ComponentName("com.android.settings","com.android.settings.Settings");
						    intent.setComponent(cm);
							intent.setAction("android.intent.action.VIEW");
							try {
								((Activity) activity).startActivityForResult(intent , 0);
							} catch (SecurityException e) {
								 intent = new Intent(
										"android.settings.WIRELESS_SETTINGS");
								 ((Activity) activity).startActivityForResult(intent,0);
							}
							
							dialog.dismiss();
						}
						
						@Override
						public void onNegativeHandle(Dialog dialog,View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							activity.finish();
						}
					},
					false
			 );
			 
		}
		
		public static void openAndInstallFile(Context context,File file){
			String fileType = FileUtils.getFileMimeType(file);
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), fileType);
			context.startActivity(intent);
		}
		
		/**
		 * 退出应用程序提示框
		 * @param activity
		 */
		public static void exitAppDialog(final Activity activity){
			CommonActions.createTwoBtnMsgDialog(activity, 
					activity.getString(R.string.userexit), 
					activity.getString(R.string.confirmexit), 
					activity.getString(R.string.user_exit_yes), 
					activity.getString(R.string.user_exit_no), 
					new CommonActions.OnTwoBtnDialogHandler() {
						
						@Override
						public void onPositiveHandle(Dialog dialog, View v) {
							dialog.dismiss();
							exitClient(activity);
						}
						
						@Override
						public void onNegativeHandle(Dialog dialog, View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					},
			true
			);
		}
		
		public static ServiceManager startPushMessageService(Context context){
	        // Start the service
	        ServiceManager serviceManager = new ServiceManager(context);
	        serviceManager.setNotificationIcon(R.drawable.icon);
	        serviceManager.startService();
	        return serviceManager;
		}
		
		public static void setScreenOrientation(Activity activity){
			CmucApplication cmucApplication=(CmucApplication) CmucApplication.sContext;
			int screenDirection=cmucApplication.getSettingsPreferences().getScreenDirection();   
			activity.setRequestedOrientation(screenDirection);
		}
		
		
		public static void cancleTask(GenericTask task) {
			if(task!=null&&task.getStatus()==GenericTask.Status.RUNNING){
				task.cancle();
			}
		}
		
		public static  void startLockActivity(Context context){
			Intent in=new Intent(context, LockActivity.class);
			in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(in);
	   }
		
		public static void showLockScreen(Context context) {
			CmucApplication cmucApplication=(CmucApplication) CmucApplication.sContext;
			if (cmucApplication.isApplicationRunning()) {
				boolean noIsLock = cmucApplication.getSettingsPreferences()
						.getNoLock();
				if (!noIsLock) {
					boolean screenOffLock = cmucApplication.getSettingsPreferences().getScreenOffLock();
					if (screenOffLock) {
						if(!CmucApplication.sLockScreenShowing){
							startLockActivity(context);
							CmucApplication.sLockScreenShowing=true;
							CmucApplication.sNeedShowLock=false;
						}
					}
				}
			}
		}
		
		
		public static void startExApplication(Context context,String pkg,Bundle bundle){
			final PackageManager pm = context.getPackageManager();
			Intent i = pm.getLaunchIntentForPackage(pkg);
			if(bundle!=null)
				i.putExtras(bundle);
			context.startActivity(i);
		}
		
		public static void installApp(final Item item,final Context context,String packageName,final CommonActions.OpenThirdAppCallBack callBack) {
			boolean reInstallApp=false;
			String apkFileName=null;
		    String content=item.getDescription();//item.getContent();
			CmucApplication cmucApplication=(CmucApplication) CmucApplication.sContext;
			final Preferences preferences=cmucApplication.getSettingsPreferences();
		    final String menuVersionKey=preferences.getUserName()+item.getAppTag()+""+item.getMenuId();
		    final long childVersion=item.getChildVersion();
		    long oldChildVersion=preferences.getMenuVersion(menuVersionKey);
		 	boolean isChildUpdate=oldChildVersion<childVersion;
			try {
				if(!isChildUpdate){
					Bundle extra = new Bundle();
					extra.putString("package", context.getPackageName());
					extra.putString("account", cmucApplication.getSettingsPreferences().getUserName());
					extra.putString("sub_account",cmucApplication.getCurrentSubAccount(item.getAppTag()));
					extra.putString("mobile_phone",cmucApplication.getSettingsPreferences().getMobile());
					CommonActions.startExApplication(context, packageName,extra);
				}
				else{
					reInstallApp=true;
				}
			} catch (NullPointerException e) {
				reInstallApp=true;
			}
			if(reInstallApp){
				if (content!= null) {
					try {
						apkFileName = FileUtils.getFileNameByUrl(content);
						if (apkFileName == null) {
							apkFileName = System.currentTimeMillis() + "";
						}
						final String fileName=apkFileName;
						Handler handler=new Handler(){
							public void handleMessage(android.os.Message msg) {
								switch (msg.what) {
								case DownLoadThread.DWONLOAD_FLIE_COMPLETE:
									item.setAppDownLoadingStaus(false);
									callBack.refreshUi();
									File file=	FileUtils.createSDFile(CmucApplication.APP_FILE_APK_DIR,fileName);
									CommonActions.openAndInstallFile(context, file);
									preferences.saveMenuVersion(menuVersionKey, childVersion);
									break;
								case DownLoadThread.DWONLOADING_FILE:
									item.setAppDownLoadingStaus(true);
									item.setDownPercentage(msg.arg1);
									callBack.refreshUi();
									break;
								case DownLoadThread.DWONLOAD_FLIE_ERROR:
									item.setAppDownLoadingStaus(false);
									callBack.refreshUi();
									break;
								default:
									break;
								}						
							}
						};
						if (isChildUpdate) {
							CmucApplication.sDownFileManager.downFile(context,content, CmucApplication.APP_FILE_APK_DIR,null,handler,true,false);
						} else {
							boolean apkIsExist = FileUtils.fileIsExist(CmucApplication.APP_FILE_APK_DIR, apkFileName);
							if (!apkIsExist) {
								CmucApplication.sDownFileManager.downFile(context,content, CmucApplication.APP_FILE_APK_DIR,null,handler,true,false);
							}else{
								File file=	FileUtils.createSDFile(CmucApplication.APP_FILE_APK_DIR,apkFileName);
								CommonActions.openAndInstallFile(context, file);
							}
						}
					} catch (IOException ioEx) {
						ioEx.printStackTrace();
					}
				}
			}
		}
		
		public interface OpenThirdAppCallBack{
			public void refreshUi();
		}
}
