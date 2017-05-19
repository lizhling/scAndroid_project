package com.starcpt.cmuc.ui.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.ui.fragment.MessageDetailFragment;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;


public class MessageDetailActivity extends FragmentActivity{
	// private static final String TAG="MessageDetailActivity";
	 
	 private TextView mTitleView;
	 private Button mBackView;	 
	
	 
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.message_detail);
	registerSkinChangedReceiver();
	CommonActions.setScreenOrientation(this);
	addMessageDetailFragment();	
	initTitleBar();
	CommonActions.addActivity(this);
	setSkin();
}

private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		setSkin();
	}
};

private void setSkin(){
	SkinManager.setSkin(this,null,ViewEnum.MessageDetailActivity);
}

private void registerSkinChangedReceiver(){
	IntentFilter filter = new IntentFilter();
	filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
	registerReceiver(mSkinChangedReceiver, filter);
}

private void unRegisterSkinChangedReceiver(){
	unregisterReceiver(mSkinChangedReceiver);
}

private void initTitleBar() {
	mTitleView=(TextView) findViewById(R.id.top_title_text);
	mBackView=(Button) findViewById(R.id.top_back_btn);
	mTitleView.setText(R.string.message_detail);
	mBackView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	});
}

private void addMessageDetailFragment() {
	Intent intent=getIntent();	
	int messageId=intent.getIntExtra(CmucApplication.MESSAGE_ID_EXTRAL, -1);
	String messageNotificationId=intent.getStringExtra(CmucApplication.MESSAGE_NOTIFICATION_ID_EXTRAL);
	String messageOptionType=intent.getStringExtra(CmucApplication.MESSAGE_OPTION_TYPE_EXTRAL);
	boolean messageOperationComplete=intent.getBooleanExtra(CmucApplication.MESSAGE_OPERATION_COMPLETE_EXTRAL, false);
	int messageReaded=intent.getIntExtra(CmucApplication.MESSAGE_READED_EXTRAL, 0);
	
	String messageTitle=intent.getStringExtra(CmucApplication.MESSAGE_TITLE_EXTRAL);
	String messageContent=intent.getStringExtra(CmucApplication.MESSAGE_TEXT_CONTENT_EXTRAL);
	long messageTime=intent.getLongExtra(CmucApplication.MESSAGE_TIME_EXTRAL, 0);	
	
	String feedBackContent=intent.getStringExtra(CmucApplication.MESSAGE_FEEDBACK_CONTENT_EXTRAL);
	long feedBackTime=intent.getLongExtra(CmucApplication.MESSAGE_FEEDBACK_TIME_EXTRAL, 0);
	String messageOptionContent=intent.getStringExtra(CmucApplication.MESSAGE_OPTION_CONTENT_EXTRAL);
	
	MessageDetailFragment messageDetailFragment=new MessageDetailFragment();
	messageDetailFragment.setMessageId(messageId);
	messageDetailFragment.setMessageNotificationId(messageNotificationId);
	messageDetailFragment.setMessageOptionType(messageOptionType);
	messageDetailFragment.setMessageOperationComplete(messageOperationComplete);
	messageDetailFragment.setMessageReaded(messageReaded);
	messageDetailFragment.setMessageTitle(messageTitle);
	messageDetailFragment.setMessageContent(messageContent);
	messageDetailFragment.setMessageTime(messageTime);
	messageDetailFragment.setFeedBackContent(feedBackContent);
	messageDetailFragment.setFeedBackTime(feedBackTime);
	messageDetailFragment.setMessageOptionContent(messageOptionContent);
	
	
	FragmentManager fragmentManager = getSupportFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 
	fragmentTransaction.add(R.id.message_detail, messageDetailFragment);
	fragmentTransaction.commit();
	
}

@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	if(CmucApplication.sNeedShowLock){
		CommonActions.showLockScreen(this);
	}
}


@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	   if(keyCode==KeyEvent.KEYCODE_BACK){
		   finish();
		   return true;
	   }
	return super.onKeyDown(keyCode, event);
}



protected void onStart() {
	super.onStart();
};

@Override
protected void onStop() {
	// TODO Auto-generated method stub
	super.onStop();
}

@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	unRegisterSkinChangedReceiver();
}

}
