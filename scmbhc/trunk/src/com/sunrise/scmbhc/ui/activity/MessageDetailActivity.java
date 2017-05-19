package com.sunrise.scmbhc.ui.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.Message;
import com.sunrise.scmbhc.ui.fragment.BaseFragment;
import com.sunrise.scmbhc.ui.fragment.MessageDetailFragment;


public class MessageDetailActivity extends BaseActivity{
	// private static final String TAG="MessageDetailActivity";
	
	 private TextView mTitleView;
	 private Button mBackView;	 
	 private Message message;
	 
	 @Override
	public void init() {
		setTitle(getTitle(getIntent()));
		Intent intent=getIntent();
		setFinishActivity(true);
		Bundle bundle = intent.getExtras();
		message = (Message)bundle.get("message");
	}
	
	@Override
	protected BaseFragment getFragment() {
		MessageDetailFragment fragment = new MessageDetailFragment();
		fragment.setArguments(getIntent().getExtras());
		return fragment;
	}

private void addMessageDetailFragment() {
//	Intent intent=getIntent();	
//	int messageId=intent.getIntExtra(CmucApplication.MESSAGE_ID_EXTRAL, -1);
//	String messageNotificationId=intent.getStringExtra(CmucApplication.MESSAGE_NOTIFICATION_ID_EXTRAL);
//	String messageOptionType=intent.getStringExtra(CmucApplication.MESSAGE_OPTION_TYPE_EXTRAL);
//	boolean messageOperationComplete=intent.getBooleanExtra(CmucApplication.MESSAGE_OPERATION_COMPLETE_EXTRAL, false);
//	int messageReaded=intent.getIntExtra(CmucApplication.MESSAGE_READED_EXTRAL, 0);
//	
//	String messageTitle=intent.getStringExtra(CmucApplication.MESSAGE_TITLE_EXTRAL);
//	String messageContent=intent.getStringExtra(CmucApplication.MESSAGE_TEXT_CONTENT_EXTRAL);
//	long messageTime=intent.getLongExtra(CmucApplication.MESSAGE_TIME_EXTRAL, 0);	
//	
//	String feedBackContent=intent.getStringExtra(CmucApplication.MESSAGE_FEEDBACK_CONTENT_EXTRAL);
//	long feedBackTime=intent.getLongExtra(CmucApplication.MESSAGE_FEEDBACK_TIME_EXTRAL, 0);
//	String messageOptionContent=intent.getStringExtra(CmucApplication.MESSAGE_OPTION_CONTENT_EXTRAL);
//	
//	MessageDetailFragment messageDetailFragment=new MessageDetailFragment();
//	messageDetailFragment.setMessageId(messageId);
//	messageDetailFragment.setMessageNotificationId(messageNotificationId);
//	messageDetailFragment.setMessageOptionType(messageOptionType);
//	messageDetailFragment.setMessageOperationComplete(messageOperationComplete);
//	messageDetailFragment.setMessageReaded(messageReaded);
//	messageDetailFragment.setMessageTitle(messageTitle);
//	messageDetailFragment.setMessageContent(messageContent);
//	messageDetailFragment.setMessageTime(messageTime);
//	messageDetailFragment.setFeedBackContent(feedBackContent);
//	messageDetailFragment.setFeedBackTime(feedBackTime);
//	messageDetailFragment.setMessageOptionContent(messageOptionContent);
//	
//	
//	FragmentManager fragmentManager = getSupportFragmentManager();
//	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 
//	fragmentTransaction.add(R.id.message_detail, messageDetailFragment);
//	fragmentTransaction.commit();
	
}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
	}

}
