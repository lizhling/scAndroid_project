package com.sunrise.scmbhc.ui.view;


import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.UpdateInfo;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class UpdateApkDialog extends Dialog {
	private Button mRightButton;
	private Context mContext;
	private TextView mSupportVersionAlertView;
	public UpdateApkDialog(Context context,UpdateInfo updateInfo,View.OnClickListener leftOnClickListener,View.OnClickListener rightOnClickListener) {
		super(context);
		this.mContext=context;
		initView(updateInfo,leftOnClickListener,rightOnClickListener);		
	}

	private void initView(UpdateInfo updateInfo,View.OnClickListener leftOnClickListener,View.OnClickListener rightOnClickListener){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_apk_dialog);
		((TextView)(findViewById(R.id.dialog_title))).setText(mContext.getString(R.string.find_version_apk));
		((Button)(findViewById(R.id.update_now))).setOnClickListener(leftOnClickListener);
		mRightButton=((Button)(findViewById(R.id.later_say)));
		mRightButton.setOnClickListener(rightOnClickListener);
		
		String  colon=":";
		String updateLatestVersionName=updateInfo.getNewVersionName();
		String latestVersionName=mContext.getString(R.string.latest_version_name);
		if(updateLatestVersionName!=null)
			latestVersionName=latestVersionName+colon+updateLatestVersionName;
		((TextView)(findViewById(R.id.latest_version_name))).setText(latestVersionName);
		
		String updateSupportVersionName=updateInfo.getSuportVersionName();
		String supportVersionName=mContext.getString(R.string.support_version_name);
		if(supportVersionName!=null)
			supportVersionName=supportVersionName+colon+updateSupportVersionName;
		((TextView)(findViewById(R.id.support_version_name))).setText(supportVersionName);
		
		String updateDes=updateInfo.getUpdateDescription();
		String des="";
		if(updateDes!=null){
			String[] desArray=updateDes.split(colon);
			if(desArray!=null){
				for(int i=0;i<desArray.length;i++){
					int index=i+1;
					String temp=index+"."+desArray[i];
					des+=temp+"\n";
				}
			}
			((TextView)(findViewById(R.id.update_des))).setText(des);
		}
		
		mSupportVersionAlertView=(TextView) findViewById(R.id.must_update);
	}
	
	public void setRightButton(int visibility){
		mRightButton.setVisibility(visibility);
	}
	
	public void setSupportVersionAlert(int visibility){
		mSupportVersionAlertView.setVisibility(visibility);
	}
	
}
