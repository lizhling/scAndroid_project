package com.starcpt.cmuc.ui.fragment;
import org.androidpn.client.Notifier;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.MessageAdapter;
import com.starcpt.cmuc.db.CmucDbManager;
import com.starcpt.cmuc.db.CmucStore;
import com.starcpt.cmuc.model.Message;
import com.starcpt.cmuc.ui.activity.MessageDetailActivity;
import com.starcpt.cmuc.ui.activity.SettingActivity;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;

public class MessageListFragment extends ListFragment{
	
	private static final int THREAD_LIST_QUERY_TOKEN       = 1701;
	private static final int DELETE_ONE_MESSAGE_TOKEN      = 1801;
	private static final int DELETE_ALL_MESSAGES_TOKEN     =1802;
	
	private TextView mTitleView;
	private Button mBackView;
	private ImageView mCommonLineView;
	private CmucDbManager mCmucDbManager;
	private MessageAdapter mMessageAdapter;
	private AsyncQueryHandler mQueryHandler;
	public static boolean sIsDisplayIng=false;
	private View mDialogTitleBar;
	private AlertDialog mDialog;
	private ProgressDialog mProgressDialog;
	private int mItemLongClickPosition;
	private int mMessageCount=0;
	private MessageDetailFragment mMessageDetailFragment;
	private FragmentManager mFragmentManager; 
	private Activity mActivity;
	private ListView mListView;
	private BroadcastReceiver mScreenDirectionReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(action.equals(SettingActivity.CHANGE_SCERRN_DIRECTION_ACTION)){
				int screenDirection=intent.getIntExtra(SettingActivity.SCREEN_DIRECTION_EXTRAL, -1);
				if(screenDirection==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
					RemoveMessageDetailFragment();
					mMessageAdapter.notifyDataSetChanged();
				}else{
					if(mMessageDetailFragment!=null){
						addMessageDetailFragment();
						mMessageAdapter.notifyDataSetChanged();
					}
					else{
						startAsyncQuery();
					}
				}

			}
		}
	};
	
	
	private final MessageAdapter.OnContentChangedListener mContentChangedListener =
			new MessageAdapter.OnContentChangedListener() {
			public void onContentChanged(MessageAdapter adapter) {
			    startAsyncQuery();
			}
			};

			private final OnItemLongClickListener mOnItemLongClickListener=new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View view, int position,
						long id) {
					mItemLongClickPosition=position;
					if(mDialog==null)
						mDialog=createDeleteDialog();
					mDialog.show();
					return true;
				}
			};
	private View mCurrView;
	private CmucApplication cmucApplication;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		ContentResolver contentResolver=mActivity.getContentResolver();
		mCmucDbManager=CmucDbManager.getInstance(contentResolver);
		mQueryHandler=new QueryHandler(contentResolver);
		mMessageAdapter=new MessageAdapter(mActivity, null);
		mMessageAdapter.setOnContentChangedListener(mContentChangedListener);
		setListAdapter(mMessageAdapter);
		mListView = getListView();
		mListView.setOnItemLongClickListener(mOnItemLongClickListener);
	    initDialogTitleBar();
		mCommonLineView=(ImageView) mActivity.findViewById(R.id.common_line);
		if(!cmucApplication.isIsPad())
			mCommonLineView.setVisibility(View.GONE);
		setSkin();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity=getActivity();
		cmucApplication=(CmucApplication) mActivity.getApplicationContext();
		mFragmentManager = ((FragmentActivity) mActivity).getSupportFragmentManager();
		mActivity.registerReceiver(mScreenDirectionReceiver, new IntentFilter(SettingActivity.CHANGE_SCERRN_DIRECTION_ACTION));
		mCurrView = inflater.inflate(R.layout.message_list_fragment, container, false);
		initTitleBar(mCurrView);
		registerSkinChangedReceiver();
		return mCurrView;
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
//		mListView.setBackgroundColor(getResources().getColor(R.color.listview_bg));
		SkinManager.setSkin(mActivity,mListView, ViewEnum.MessageListFragment);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unRegisterSkinChangedReceiver();
		mActivity.unregisterReceiver(mScreenDirectionReceiver);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor=mMessageAdapter.getCursor();
		cursor.moveToFirst();
		cursor.move(position);
		Message message=mCmucDbManager.cursorToMessage(cursor);
		visit(message);
	}

	public void visit(Message message){
		if(cmucApplication.isIsPad()&&cmucApplication.getScreenDirection()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			repaceMessageDetailFragment(message);
		}else{
			startMessageDetailActivity(message);
		}
		
	}

	private void startMessageDetailActivity(Message message) {
		String optionType=message.getOptionType();
		int operationComplete=message.getOperationComplete();
		String notificationId=message.getNotificationId();
		long feedbackTime=message.getFeedbackTime();
		String feedbackContent=message.getFeedbackContent();
		String optionContent=message.getOptionContent(); 
		int id=message.getId();
		String title=message.getTitle();
		long messageTime=message.getMessageTime();
		String textContent=message.getTextContent();
		int readed=message.getReaded();
		Intent 	intent=new Intent(mActivity, MessageDetailActivity.class);
		if(message.getOptionType().equalsIgnoreCase(Message.READED_OPTION_TYPE)
				||optionType.equalsIgnoreCase(Message.FEEDBACK_OPTION_TYPE)){	
			intent.putExtra(CmucApplication.MESSAGE_OPERATION_COMPLETE_EXTRAL, operationComplete==1?true:false);				
			intent.putExtra(CmucApplication.MESSAGE_NOTIFICATION_ID_EXTRAL, notificationId);		
			if(optionType.equalsIgnoreCase(Message.FEEDBACK_OPTION_TYPE)){
				intent.putExtra(CmucApplication.MESSAGE_FEEDBACK_TIME_EXTRAL, feedbackTime);
				intent.putExtra(CmucApplication.MESSAGE_FEEDBACK_CONTENT_EXTRAL, feedbackContent);
			}		
		}else{
			intent.putExtra(CmucApplication.MESSAGE_OPTION_CONTENT_EXTRAL, optionContent);
		}
		//common
		intent.putExtra(CmucApplication.MESSAGE_ID_EXTRAL, id);
		intent.putExtra(CmucApplication.MESSAGE_TITLE_EXTRAL, title);
		intent.putExtra(CmucApplication.MESSAGE_TIME_EXTRAL, messageTime);
		intent.putExtra(CmucApplication.MESSAGE_TEXT_CONTENT_EXTRAL, textContent);
		intent.putExtra(CmucApplication.MESSAGE_READED_EXTRAL, readed);
		intent.putExtra(CmucApplication.MESSAGE_OPTION_TYPE_EXTRAL, optionType);
		mActivity.startActivity(intent);
	}

	private void initTitleBar(View view) {
		mTitleView=(TextView) view.findViewById(R.id.top_title_text);
		mBackView=(Button) view.findViewById(R.id.top_back_btn);
		mTitleView.setText(R.string.message);
		mBackView.setVisibility(View.GONE);
	}
	
	private void initDialogTitleBar() {
		mDialogTitleBar=LayoutInflater.from(mActivity).inflate(R.layout.dialog_title_bar, null);
	    TextView dialogTitle=(TextView) mDialogTitleBar.findViewById(R.id.tvTitle);
	    dialogTitle.setText(R.string.manage_message);
	}
	
	private AlertDialog createDeleteDialog(){
		return new AlertDialog.Builder(mActivity)
		.setCustomTitle(mDialogTitleBar)
		.setItems(
	     new String[] { getString(R.string.delete_selected_message), getString(R.string.delete_all_messages)},
	     new DialogInterface.OnClickListener() {
	     public void onClick(DialogInterface dialog, int which) {
	    	 switch(which){
	    	 case 0:
	    		 	deleteMessage(mItemLongClickPosition);
	    		 break;
	    	 case 1:
	    			deleteAllMessage();
	    		 break;
	    		
	    	 }
	    	 dialog.dismiss();
	     }
		}).create();
		
	}
	
	private void cancleNotification(){
		NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Notifier.NOF_ID);
		readMissMessage();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		startAsyncQuery();
		sIsDisplayIng=true;
		cancleNotification();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		sIsDisplayIng=false;
	}
	
	private void readMissMessage() {
		new Thread(){@Override
		public void run() {
			mCmucDbManager.readMissMessage();
		}}.start();
	}

	private void deleteMessage(int position){
		Cursor cursor=mMessageAdapter.getCursor();
		cursor.moveToFirst();
		cursor.move(position);
		final int _id=cursor.getInt(cursor.getColumnIndex(CmucStore.Messages._ID));
		showProgressDialog();
		mCmucDbManager.startDeleteMessage(mQueryHandler, DELETE_ONE_MESSAGE_TOKEN, _id);
	}
	
	private void deleteAllMessage(){
		showProgressDialog();
		mCmucDbManager.startDeleteAllMessages(mQueryHandler, DELETE_ALL_MESSAGES_TOKEN);
	}
	
	private void showProgressDialog(){
		mProgressDialog=ProgressDialog.show(mActivity, "",getResources().getString(R.string.deleting_message), true, false);	
	}
	
	private void disMissProgressDialog(){
		mProgressDialog.dismiss();
	}
	
    private void startAsyncQuery() {
    	mCmucDbManager.startQueryMessage(mQueryHandler,THREAD_LIST_QUERY_TOKEN,cmucApplication.getSettingsPreferences().getUserName());
    }

    private void repaceMessageDetailFragment(Message message) {
		mMessageDetailFragment=new MessageDetailFragment();
		mMessageDetailFragment.setMessageId(message.getId());
		mMessageDetailFragment.setMessageNotificationId(message.getNotificationId());
		mMessageDetailFragment.setMessageOptionType(message.getOptionType());
		mMessageDetailFragment.setMessageOperationComplete(message.getOperationComplete()==1?true:false);
		mMessageDetailFragment.setMessageReaded(message.getReaded());
		mMessageDetailFragment.setMessageTitle(message.getTitle());
		mMessageDetailFragment.setMessageContent(message.getTextContent());
		mMessageDetailFragment.setMessageTime(message.getMessageTime());
		mMessageDetailFragment.setFeedBackContent(message.getFeedbackContent());
		mMessageDetailFragment.setFeedBackTime(message.getFeedbackTime());
		mMessageDetailFragment.setMessageOptionContent(message.getOptionContent());
		addMessageDetailFragment();
	}

	private void addMessageDetailFragment() {
		if(mMessageDetailFragment!=null){
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction(); 
			fragmentTransaction.replace(R.id.message_list_panel, mMessageDetailFragment);
			fragmentTransaction.commitAllowingStateLoss();
		}
	}
    
	private void RemoveMessageDetailFragment() {
		if(mMessageDetailFragment!=null){
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction(); 
			fragmentTransaction.remove(mMessageDetailFragment);
			fragmentTransaction.commitAllowingStateLoss();
		}
	}

	
	class QueryHandler extends AsyncQueryHandler{
	
		public QueryHandler(ContentResolver cr) {
			super(cr);
			// TODO Auto-generated constructor stub
		}
		 
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			switch(token){
			case THREAD_LIST_QUERY_TOKEN:
				if(cmucApplication.isIsPad()&&cmucApplication.getScreenDirection()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
					mMessageCount=cursor.getCount();
					if(mMessageCount==0){
						RemoveMessageDetailFragment();
					}else{
						if(mMessageDetailFragment==null){
							cursor.moveToFirst();
							Message message=mCmucDbManager.cursorToMessage(cursor);					  
							repaceMessageDetailFragment(message);
						}
					}
				}
				mMessageAdapter.changeCursor(cursor);
				mMessageAdapter.notifyDataSetInvalidated();
				break;
			}
		}
		
		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			switch(token){
			case DELETE_ONE_MESSAGE_TOKEN:
				disMissProgressDialog();
				Toast.makeText(mActivity, R.string.delete_selected_message_scuess, Toast.LENGTH_LONG).show();
				break;
			case DELETE_ALL_MESSAGES_TOKEN:
				disMissProgressDialog();
				Toast.makeText(mActivity, R.string.delete_all_messages_scuess, Toast.LENGTH_LONG).show();
				break;
			}
		}
	 }
	
}
