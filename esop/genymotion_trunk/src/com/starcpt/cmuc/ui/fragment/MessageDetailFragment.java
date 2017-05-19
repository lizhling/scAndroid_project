package com.starcpt.cmuc.ui.fragment;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.db.CmucDbManager;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.Message;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.ui.activity.CommonActions;
import com.starcpt.cmuc.ui.activity.WebViewActivity;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.sunrise.javascript.utils.DateUtils;

public class MessageDetailFragment extends Fragment {
	//private final static String TAG="MessageDetailFragment";
	private Activity mActivity;
	private int messageId;
	private String messageNotificationId;
	private String messageOptionType;
	private boolean messageOperationComplete;
	private int messageReaded;
	
	private String messageTitle;
	private String messageContent;
	private long messageTime;
	
	private String feedBackContent;
	private long feedBackTime;
	private String messageOptionContent;
	 
	 private TextView mMessageTitleView;
	 private TextView mMessageTimeView;	 
	 private TextView mMessageContentView;	  
	 
	 private EditText mFeedBackEditText;
	 private LinearLayout mFeededBackPanel;	
	 
	 private TextView mFeededBackContentView;
	 private TextView mFeededBackTimeView;
	 
	 private Button mCommitButton;
	 private LinearLayout mCommitPanel;	 
	 private ProgressDialog mPd;
	 
	 private FeedBackTask mFeedBackTask;		 	 
	 private TaskListener mFeedBackTaskListener=new TaskListener() {
		
		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub
			mPd=ProgressDialog.show(mActivity, "", getString(R.string.commiting_feedback));
		}
		
		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			mPd.dismiss();
			if(result == TaskResult.OK){
				dismissFeedbackView();
				mFeededBackTimeView.setText(DateUtils.formatTime(feedBackTime, DateUtils.DATE_FORMAT));	
				Toast.makeText(mActivity, getString(R.string.commit_feedback_scuess),Toast.LENGTH_LONG).show();
			} else {
				handleFeedBackTaskFailed();
			}			
		}
		
		@Override
		public void onCancelled(GenericTask task) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	 
	 private static HashMap<String, ReadThread> readThreads=new HashMap<String, ReadThread>();	 
	 private CmucDbManager mHistoryMessageDbManager;
	private View mView;
	 
	 @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mHistoryMessageDbManager=CmucDbManager.getInstance(mActivity.getContentResolver());
		if(messageReaded==0)
		mHistoryMessageDbManager.readed(messageId);
	}
	 
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity=getActivity();
		mView =initMainView(inflater,container);	
		registerSkinChangedReceiver();
		setSkin();
		return mView;
	}
	 
	 private BroadcastReceiver mSkinChangedReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				setSkin();
			}
		};
		
		private void registerSkinChangedReceiver(){
			IntentFilter filter = new IntentFilter();
			filter.addAction(SkinManager.SKIN_CHANGED_RECEIVER);
			mActivity.registerReceiver(mSkinChangedReceiver, filter);
		}
		
		private void unRegisterSkinChangedReceiver(){
			mActivity.unregisterReceiver(mSkinChangedReceiver);
		}
		
		private void setSkin(){
			SkinManager.setSkin(mActivity,null, ViewEnum.MessageDetailFragment);
		}
		
	 @Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		unRegisterSkinChangedReceiver();
	 }
		
	 private View initMainView(LayoutInflater inflater, ViewGroup container) {
		    View view=inflater.inflate(R.layout.message_detail_fragment, container, false);
			mFeededBackPanel=(LinearLayout) view.findViewById(R.id.feeded_back_panel);
			mCommitPanel=(LinearLayout) view.findViewById(R.id.commit_panel);
			mMessageContentView=(TextView) view.findViewById(R.id.message_text_view);
			mMessageTitleView=(TextView) view.findViewById(R.id.message_title_view);
			mMessageTimeView=(TextView) view.findViewById(R.id.message_date_view);	
			mFeedBackEditText=(EditText) view.findViewById(R.id.feedback_text);	
			mFeededBackContentView=(TextView) view.findViewById(R.id.feedback_message_text_view);
			mFeededBackTimeView=(TextView) view.findViewById(R.id.feed_back_message_date_view);
			mCommitButton=(Button) view.findViewById(R.id.commit);	
			
			if(messageTitle!=null)
				mMessageTitleView.setText(messageTitle);
			if(messageTime!=0)
				mMessageTimeView.setText(DateUtils.formatTime(messageTime, DateUtils.DATE_FORMAT));
			if(messageContent!=null)
			mMessageContentView.setText(messageContent);
			
			if(feedBackTime!=0)
				mFeededBackTimeView.setText(DateUtils.formatTime(feedBackTime, DateUtils.DATE_FORMAT));	
			if(feedBackContent!=null)
				mFeededBackContentView.setText(feedBackContent);	
			
			if(messageOptionType.equals(Message.FEEDBACK_OPTION_TYPE)){
				if(!messageOperationComplete){
					mFeedBackEditText.setVisibility(View.VISIBLE);
					mFeededBackPanel.setVisibility(View.GONE);
				}else{
					mCommitPanel.setVisibility(View.GONE);
					mFeedBackEditText.setVisibility(View.GONE);
					mFeededBackPanel.setVisibility(View.VISIBLE);
				}
				mCommitButton.setText(R.string.commit);
			}else if(messageOptionType.equals(Message.WEB_OPTION_TYPE)){
				dimissFeedbackPanel();
				mCommitButton.setText(R.string.open_web);
			}else if(messageOptionType.equals(Message.FUNCTION_OPTION_TYPE)){
				mCommitButton.setText(R.string.do_business);
				dimissFeedbackPanel();
			}else if(messageOptionType.equals(Message.READED_OPTION_TYPE)){
				mCommitPanel.setVisibility(View.GONE);
				dimissFeedbackPanel();
				if(!messageOperationComplete)
					doRead();
			}
			
			mCommitButton.setOnClickListener(new OnClickListener() {		
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					visitMessage();
				}
			});
			return view;
		}
	 
	 private void visitMessage() {
			if(messageOptionType.equals(Message.FEEDBACK_OPTION_TYPE)){
				doFeedback();
			}
			else if(messageOptionType.equals(Message.WEB_OPTION_TYPE)||messageOptionType.equals(Message.FUNCTION_OPTION_TYPE)){
				Intent intent=new Intent(mActivity, WebViewActivity.class);
				intent.putExtra(CmucApplication.TITLE_EXTRAL, messageTitle);
				if(messageOptionType.equals(Message.WEB_OPTION_TYPE)){
					intent.putExtra(CmucApplication.CONTENT_EXTRAL, messageOptionContent);
				}else{
					if(messageOptionContent!=null){
						String[] optionContents=messageOptionContent.split("~");
						if(optionContents.length>=3){
							String businessId=optionContents[0];
							String applicationId=optionContents[1];
							String url=optionContents[2];							
							intent.putExtra(CmucApplication.CONTENT_EXTRAL, url);
							intent.putExtra(CmucApplication.APP_TAG_EXTRAL, applicationId);
							intent.putExtra(CmucApplication.BUSINESS_ID_EXTRAL, Integer.valueOf(businessId));
							intent.putExtra(CmucApplication.IS_BUSINESS_WEB_EXTRAL,true);
						}
						}
				}
				startActivity(intent);
			}
		}

	private void dimissFeedbackPanel() {
		mFeedBackEditText.setVisibility(View.GONE);
		mFeededBackPanel.setVisibility(View.GONE);
	}
	
	private void dismissFeedbackView() {
		mCommitPanel.setVisibility(View.GONE);
		mFeedBackEditText.setVisibility(View.GONE);
	    if(messageOptionType.equals(Message.FEEDBACK_OPTION_TYPE))
		mFeededBackPanel.setVisibility(View.VISIBLE);
	}

	private void handleFeedBackTaskFailed() {
		CommonActions.createTwoBtnMsgDialog(mActivity, null,
				getString(R.string.server_error), 
				getString(R.string.re_try), 
				getString(R.string.exit), 
				new CommonActions.OnTwoBtnDialogHandler() {
					
					@Override
					public void onPositiveHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						doFeedback();
						dialog.dismiss();
					}
					
					@Override
					public void onNegativeHandle(Dialog dialog, View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				},
				false);
	}


	private void doRead(){
		ReadThread readThread= readThreads.get(messageNotificationId);
		if(readThread==null){
			readThread=new ReadThread();
			readThread.start();
			readThreads.put(messageNotificationId, readThread);
		}else{
			if(readThread.isFinish()){
				readThread.setFinish(false);
				readThread=new ReadThread();
				readThread.start();
				readThreads.put(messageNotificationId, readThread);
			}
		}
	}
	
	private void doFeedback(){
		feedBackContent=mFeedBackEditText.getText().toString();
		if(TextUtils.isEmpty(feedBackContent.trim())){
			Toast.makeText(mActivity, R.string.no_input_feedback_text, Toast.LENGTH_LONG).show();
			return ;
		}else{
			mFeedBackTask=new FeedBackTask();
			mFeedBackTask.setListener(mFeedBackTaskListener);
			mFeedBackTask.execute();
		}
	}

	class ReadThread extends Thread{
		boolean finish=false;
		@Override
		public void run() {
			try {
				CmucApplication.sServerClient.readed(messageNotificationId);
				mHistoryMessageDbManager.updateReadedMessage(messageId);
				readThreads.remove(messageNotificationId);
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish=true;
		}
		public boolean isFinish() {
			return finish;
		}
		public void setFinish(boolean finish) {
			this.finish = finish;
		}	
	}

	class FeedBackTask extends GenericTask{

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				CmucApplication.sServerClient.feedback(messageNotificationId, feedBackContent);
				messageTime=System.currentTimeMillis();
				mHistoryMessageDbManager.updateFeedbackMessage(messageId,feedBackContent,messageTime);
				return TaskResult.OK;
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return TaskResult.FAILED;
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return TaskResult.FAILED;
			}
		}
		
	}
		
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	public void setMessageNotificationId(String messageNotificationId) {
		this.messageNotificationId = messageNotificationId;
	}
	public void setMessageOptionType(String messageOptionType) {
		this.messageOptionType = messageOptionType;
	}

	public void setMessageOperationComplete(boolean messageOperationComplete) {
		this.messageOperationComplete = messageOperationComplete;
	}

	public void setMessageReaded(int messageReaded) {
		this.messageReaded = messageReaded;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public void setMessageTime(long messageTime) {
		this.messageTime = messageTime;
	}

	public void setFeedBackContent(String feedBackContent) {
		this.feedBackContent = feedBackContent;
	}

	public void setFeedBackTime(long feedBackTime) {
		this.feedBackTime = feedBackTime;
	}

	public void setMessageOptionContent(String messageOptionContent) {
		this.messageOptionContent = messageOptionContent;
	}
	
	

}
