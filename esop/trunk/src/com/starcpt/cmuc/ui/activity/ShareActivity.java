package com.starcpt.cmuc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.ui.view.ShareDialog;

public class ShareActivity extends Activity implements OnClickListener {
	private TextView mTitleView;
	private Button mBackView;
	private Button mMsgShareButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CommonActions.setScreenOrientation(this);
		setContentView(R.layout.share);
		initView();
		
		mMsgShareButton = (Button) findViewById(R.id.msg_share_button);
		mMsgShareButton.setOnClickListener(this);
		
		findViewById(R.id.other_share_button).setOnClickListener(this);
		
		CommonActions.addActivity(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (CmucApplication.sNeedShowLock) {
			CommonActions.showLockScreen(this);
		}
	}

	private void sendSMS(String smsBody) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", smsBody);
		startActivity(intent);

	}

	private void initView() {
		mTitleView = (TextView) findViewById(R.id.top_title_text);
		mTitleView.setText(R.string.friend_share);
		mBackView = (Button) findViewById(R.id.top_back_btn);
		mBackView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.msg_share_button:

		{
			CmucApplication cmucApplication = ((CmucApplication) CmucApplication.sContext);
			String appTag = cmucApplication.getAppTag();
			String appName = cmucApplication.getAppName();
			if (appTag.equals(CmucApplication.ESOP_APP_TAG)) {
				appName = "ESOP";
			}
			sendSMS(getString(R.string.share_content).replaceFirst("APPNAME", appName));
		}

			break;
		case R.id.other_share_button: {
			CmucApplication cmucApplication = ((CmucApplication) CmucApplication.sContext);
			String appTag = cmucApplication.getAppTag();
			String appName = cmucApplication.getAppName();
			if (appTag.equals(CmucApplication.ESOP_APP_TAG)) {
				appName = "ESOP";
			}
			new ShareDialog(this, getString(R.string.chooseShareApp), getString(R.string.share_content).replaceFirst("APPNAME", appName),
					BitmapFactory.decodeResource(getResources(), R.drawable.qr_code)).show();
		}

			break;
		default:
			break;
		}
	}
}
