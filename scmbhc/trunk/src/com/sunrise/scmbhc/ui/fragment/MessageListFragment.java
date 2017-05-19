package com.sunrise.scmbhc.ui.fragment;

import org.androidpn.client.Notifier;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.adapter.MessageAdapter;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.database.ScmbhcStore;
import com.sunrise.scmbhc.entity.Message;
import com.sunrise.scmbhc.ui.activity.MessageDetailActivity;

/**  
 *   
 * @Project: scmbhc  
 * @ClassName: MessageListFragment  
 * @Description: 消息推送列表
 * @Author qinhubao  
 * @CreateFileTime: 2014年5月9日 下午2:48:48  
 * @Modifier: qinhubao  
 * @ModifyTime: 2014年5月9日 下午2:48:48  
 * @ModifyNote: 
 * @version   
 *   
 */
public class MessageListFragment extends BaseFragment {

	private static final int THREAD_LIST_QUERY_TOKEN = 1701;
	private static final int DELETE_ONE_MESSAGE_TOKEN = 1801;
	private static final int DELETE_ALL_MESSAGES_TOKEN = 1802;

	private TextView mTitleView;
	private Button mBackView;
	private ImageView mCommonLineView;
	private ScmbhcDbManager mCmucDbManager;
	private MessageAdapter mMessageAdapter;
	private AsyncQueryHandler mQueryHandler;
	public static boolean sIsDisplayIng = false;
	private View mDialogTitleBar;
	private AlertDialog mDialog;
	private int mItemLongClickPosition;
	private int mMessageCount = 0;
	private MessageDetailFragment mMessageDetailFragment;
	private FragmentManager mFragmentManager;
	private Activity mActivity;
	private ListView mListView;
	

	private final MessageAdapter.OnContentChangedListener mContentChangedListener = new MessageAdapter.OnContentChangedListener() {
		public void onContentChanged(MessageAdapter adapter) {
			startAsyncQuery();
		}
	};

	private final OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int position, long id) {
			mItemLongClickPosition = position;
			if (mDialog == null)
				mDialog = createDeleteDialog();
			mDialog.show();
			return true;
		}
	};
	private View mCurrView;
	private App cmucApplication;

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private OnItemClickListener ListItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Cursor cursor = mMessageAdapter.getCursor();
			cursor.moveToFirst();
			cursor.move(position);
			Message message = mCmucDbManager.cursorToMessage(cursor);
			visit(message);
		}
	};

	public void visit(Message message) {
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("message", message);
		Intent intent = new Intent(mActivity, MessageDetailActivity.class);
		intent.putExtras(bundle);
		mActivity.startActivity(intent);
		

	}

	private void initDialogTitleBar() {
		mDialogTitleBar = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_title_bar, null);
		TextView dialogTitle = (TextView) mDialogTitleBar
				.findViewById(R.id.dialog_title);
		dialogTitle.setText("消息管理管理");
	}

	private AlertDialog createDeleteDialog() {
		return new AlertDialog.Builder(mActivity)
				.setCustomTitle(mDialogTitleBar).setItems(new String[] { "删除选中", "删除全部" },
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							switch (which) {
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

	private void cancleNotification() {
		NotificationManager notificationManager = (NotificationManager) mActivity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Notifier.NOF_ID);
		readMissMessage();
	}

	@Override
	public void onResume() {
		super.onResume();
		startAsyncQuery();
		sIsDisplayIng = true;
		cancleNotification();
	}

	@Override
	public void onPause() {
		super.onPause();
		sIsDisplayIng = false;
	}

	private void readMissMessage() {
		new Thread() {
			@Override
			public void run() {
				mCmucDbManager.readMissMessage();
			}
		}.start();
	}

	private void deleteMessage(int position) {
		Cursor cursor = mMessageAdapter.getCursor();
		cursor.moveToFirst();
		cursor.move(position);
		final int _id = cursor.getInt(cursor
				.getColumnIndex(ScmbhcStore.Messages._ID));
		showProgressDialog();
		mCmucDbManager.startDeleteMessage(mQueryHandler,
				DELETE_ONE_MESSAGE_TOKEN, _id);
	}

	private void deleteAllMessage() {
		showProgressDialog();
		mCmucDbManager.startDeleteAllMessages(mQueryHandler,
				DELETE_ALL_MESSAGES_TOKEN);
	}

	private void showProgressDialog() {
		initDialog(false, false, null);
		showDialog("消息删除中..");
	}

	private void startAsyncQuery() {
		mCmucDbManager.startQueryMessage(mQueryHandler,
				THREAD_LIST_QUERY_TOKEN, UserInfoControler.getInstance()
						.getUserName());
	}

	class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(ContentResolver cr) {
			super(cr);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			switch (token) {
			case THREAD_LIST_QUERY_TOKEN:
				 /*if(cmucApplication.isIsPad()&&cmucApplication.getScreenDirection()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
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
				 }*/
				mMessageAdapter.changeCursor(cursor);
				mMessageAdapter.notifyDataSetInvalidated();
				break;
			}
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			switch (token) {
			case DELETE_ONE_MESSAGE_TOKEN:
				dismissDialog();
				Toast.makeText(mActivity, "删除消息成功", Toast.LENGTH_LONG).show();
				break;
			case DELETE_ALL_MESSAGES_TOKEN:
				dismissDialog();
				Toast.makeText(mActivity, "删除全部消息成功", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mBaseActivity.setTitle(getString(R.string.pushmessage_title));
		mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
	}

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = getActivity();
		/*cmucApplication = (App) mActivity.getApplicationContext();
		mFragmentManager = ((FragmentActivity) mActivity)
				.getSupportFragmentManager();*/
		mCurrView = inflater.inflate(R.layout.message_list_fragment, container,
				false);
		ContentResolver contentResolver = mActivity.getContentResolver();
		mCmucDbManager = ScmbhcDbManager.getInstance(contentResolver);
		mQueryHandler = new QueryHandler(contentResolver);
		mMessageAdapter = new MessageAdapter(mActivity, null);
		mMessageAdapter.setOnContentChangedListener(mContentChangedListener);

		mListView = (ListView) mCurrView.findViewById(R.id.lv_messagelist);// getListView();
		mListView.setAdapter(mMessageAdapter);
		mListView.setOnItemLongClickListener(mOnItemLongClickListener);
		mListView.setOnItemClickListener(ListItemClickListener);
		mListView.setEmptyView(mCurrView.findViewById(R.id.orderedBusinessesNoContent));
		initDialogTitleBar();
		// mCommonLineView=(ImageView) mActivity.findViewById(R.id.common_line);
		// if(!cmucApplication.isIsPad())
		// mCommonLineView.setVisibility(View.GONE);
		// registerSkinChangedReceiver();
		return mCurrView;
	}
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		// TODO Auto-generated method stub
		return R.string.MessageListFragment;
	}

}
