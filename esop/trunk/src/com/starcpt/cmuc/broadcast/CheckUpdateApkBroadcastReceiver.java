package com.starcpt.cmuc.broadcast;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.model.bean.UpdateInfoBean;
import com.starcpt.cmuc.service.DownApkService;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.task.UpdateApkTask;
import com.starcpt.cmuc.ui.activity.CommonActions;
import com.sunrise.javascript.utils.device.ThirdPartyDeviceUtils;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class CheckUpdateApkBroadcastReceiver extends BroadcastReceiver {
	public final static String CANCLE_CHECK="com.starcpt.cmuc.broadcast.CANCLECHECK";
	private Context mContext;
	private UpdateApkTask mCheckApkUpdateTask;
	private ProgressDialog mCheckApkUpdatePd;
	private int runningTaskIndex=-1;
	private String mCheckType=null;
	private TaskListener mUpdateApkListener = new TaskListener() {	
		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			
		}
		
		@Override
		public void onPreExecute(GenericTask task) {
			String info=mContext.getString(R.string.checking_application_update);
			if(mCheckType.equals(UpdateInfoBean.NO_DEVICE_TO_SUPPORT_DEVICE_CHECK_TYPE))
				info=mContext.getString(R.string.checking_support_device_update);
			mCheckApkUpdatePd=ProgressDialog.show(mContext, null, info, false, true);
		}
		
		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// TODO Auto-generated method stub
			mCheckApkUpdatePd.dismiss();
			if(result == TaskResult.OK){
				runningTaskIndex=-1;
				startDownloadApk();
			} else {
				handleTaskFailed(task);
			}
		}
		
		@Override
		public void onCancelled(GenericTask task) {
			
		}
		
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "Update";
		}
	};
	
	
	
	public CheckUpdateApkBroadcastReceiver(Context mContext) {
		super();
		this.mContext = mContext;
	}

	private void cancleAllTask() {
		CommonActions.cancleTask(mCheckApkUpdateTask);
	}
	
	private void doCheckApkUpdate(){
		if(mCheckApkUpdateTask!=null)
		mCheckApkUpdateTask.cancle();
		mCheckApkUpdateTask = new UpdateApkTask(mContext);
		mCheckApkUpdateTask.setListener(mUpdateApkListener);
		String curVersion =CmucApplication.getApkVersion();
		TaskParams params = new TaskParams();
		params.put(UpdateApkTask.CURRENT_VERSION_KEY, curVersion);
		params.put(UpdateApkTask.CHECK_TYPE_KEY, mCheckType);
		runningTaskIndex=CmucApplication.UPDATE_APK_TASK_INDEX;
		mCheckApkUpdateTask.execute(params);
	}

	private void startDownloadApk() {
		if(mCheckApkUpdateTask.getUpdateApkInfo().getUpdateType()==UpdateInfoBean.FORCE_UPDATE)
			doDownloadApk();
		else if(mCheckApkUpdateTask.getUpdateApkInfo().getUpdateType()==UpdateInfoBean.SELECT_UPDATE){
			createUpdateApkVersionDialog();
		}
		else{
			Toast.makeText(mContext, R.string.latest_version, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		mCheckType=intent.getStringExtra(CmucApplication.CHECK_TYPE_EXTRAL);
		if(DownApkService.sDownApkIng)
		{
			int resId=R.string.downloading_apk;
			if(DownApkService.sNotificationId==DownApkService.ADD_SUPPORT_DEVICE_APK_NOTIFICATION_ID)
				resId=R.string.downloading_support_device_apk;
			Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
			return;
		}
		if(action.equals(ThirdPartyDeviceUtils.CHECK_APK_UPDATE_ACTION)){
			doCheckApkUpdate();
		}else if(action.equals(CANCLE_CHECK)){
			cancleAllTask();
		}
	}

	private void createUpdateApkVersionDialog() {
		String title=mContext.getString(R.string.update_version_title);
		String message=mContext.getString(R.string.update_version_info);
		
		if(mCheckType.equals(UpdateInfoBean.NO_DEVICE_TO_SUPPORT_DEVICE_CHECK_TYPE)){
			title=mContext.getString(R.string.add_support_device);
			message=mContext.getString(R.string.add_support_device_info); 
		}
		
		CommonActions.createTwoBtnMsgDialog(mContext,
				title, 
				message, 
				null, null, 
				new CommonActions.OnTwoBtnDialogHandler() {
					
					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						doDownloadApk();
						dialog.dismiss();
					}
					
					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				},
				false
		);
	}
	
	private void doDownloadApk(){
		Intent intent = new Intent();   
		intent.setClass(mContext, DownApkService.class);   
		intent.putExtra(CmucApplication.DOWNLOAD_APK_URL_EXTRAL,mCheckApkUpdateTask.getUpdateApkInfo().getDownloadUrl());
		intent.putExtra(CmucApplication.NOTIFICATION_ID_EXTRAL, DownApkService.UPDATE_APK_NOTIFICATION_ID);
		int resId=R.string.down_apk_toast;
		if(mCheckType.equals(UpdateInfoBean.NO_DEVICE_TO_SUPPORT_DEVICE_CHECK_TYPE)){
			resId=R.string.down_support_device_apk_toast;
			intent.putExtra(CmucApplication.NOTIFICATION_ID_EXTRAL, DownApkService.ADD_SUPPORT_DEVICE_APK_NOTIFICATION_ID);
		}
		mContext.startService(intent); 
		Toast.makeText(mContext, resId, Toast.LENGTH_LONG).show();
	}

	private void handleTaskFailed(GenericTask task) {
		// TODO Auto-generated method stub
		CommonActions.createTwoBtnMsgDialog(mContext, null,
				task.getException().getMessage(), 
				mContext.getString(R.string.re_try), 
				mContext.getString(R.string.cancel), 
				new CommonActions.OnTwoBtnDialogHandler() {
					
					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						retryTask();
						dialog.dismiss();
					}
					
					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				},
				false
		);
	}

	private void retryTask() {
		if(runningTaskIndex!=-1){
			switch(runningTaskIndex){
			case CmucApplication.UPDATE_APK_TASK_INDEX:
				doCheckApkUpdate();
				break;
			}
		}
	}
}
