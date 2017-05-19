package com.starcpt.cmuc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;

public class ShareActivity extends Activity {
	private TextView mTitleView;
	private Button mBackView;
	private Button mMsgShareButton;
  @Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	CommonActions.setScreenOrientation(this);
	setContentView(R.layout.share);
	initView();
	mMsgShareButton=(Button) findViewById(R.id.msg_share_button);
	mMsgShareButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			CmucApplication cmucApplication=((CmucApplication)CmucApplication.sContext);
			String appTag=cmucApplication.getAppTag();
			String appName=cmucApplication.getAppName();
			if(appTag.equals(CmucApplication.ESOP_APP_TAG)){
				appName="ESOP";
			}
			sendSMS(getString(R.string.share_content).replaceFirst("APPNAME", appName));
		}
	});
	CommonActions.addActivity(this);
}
  @Override
  protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	if(CmucApplication.sNeedShowLock){
		CommonActions.showLockScreen(this);
	}
}
	private void sendSMS(String smsBody) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", smsBody);
		startActivity(intent);

	}
  
  private void initView(){
		mTitleView=(TextView) findViewById(R.id.top_title_text);
		mTitleView.setText(R.string.friend_share);
		mBackView=(Button) findViewById(R.id.top_back_btn);
		mBackView.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				finish();
			}
		});
     }
}
