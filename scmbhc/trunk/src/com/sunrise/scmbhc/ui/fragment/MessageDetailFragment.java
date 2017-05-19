package com.sunrise.scmbhc.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.Message;
import com.sunrise.scmbhc.exception.http.HttpException;
import com.sunrise.scmbhc.exception.logic.BusinessException;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.ui.view.TwoButtonDialog;

/**
 * 
 * @Project: scmbhc
 * @ClassName: MessageDetailFragment
 * @Description: 消息推送详情
 * @Author qinhubao
 * @CreateFileTime: 2014年5月9日 下午2:49:26
 * @Modifier: qinhubao
 * @ModifyTime: 2014年5月9日 下午2:49:26
 * @ModifyNote:
 * @version
 * 
 */
public class MessageDetailFragment extends BaseFragment {
	// private final static String TAG="MessageDetailFragment";
	private Activity mActivity;
	private int messageId;
	private String messageNotificationId;
	private String messageOptionType;
	private boolean messageOperationComplete;
	private int messageReaded;
	TwoButtonDialog mDialogCertain;
	private Message message;
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

	private FeedBackTask mFeedBackTask;
	private TaskListener mFeedBackTaskListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {

		}

		@Override
		public void onPreExecute(GenericTask task) {
			initDialog();
			showDialog("消息反馈中");
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			dismissDialog();
			if (result == TaskResult.OK) {
				dismissFeedbackView();
				SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				mFeededBackTimeView.setText(dateformat2.format(messageTime));
				Toast.makeText(mActivity, "消息反馈成功", Toast.LENGTH_LONG).show();
			} else {
				handleFeedBackTaskFailed();
			}
		}

		@Override
		public void onCancelled(GenericTask task) {

		}

		@Override
		public String getName() {
			return null;
		}
	};

	private static HashMap<String, ReadThread> readThreads = new HashMap<String, ReadThread>();
	private ScmbhcDbManager mHistoryMessageDbManager;
	private View mView;

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void visitMessage() {
		if (messageOptionType.equals(Message.FEEDBACK_OPTION_TYPE)) {
			doFeedback();
		} else if (messageOptionType.equals(Message.WEB_OPTION_TYPE) || messageOptionType.equals(Message.FUNCTION_OPTION_TYPE)) {
			Intent intent = new Intent(mActivity, SingleFragmentActivity.class);
			intent.putExtra(ExtraKeyConstant.KEY_TITLE, messageTitle);
			if (messageOptionType.equals(Message.WEB_OPTION_TYPE)) {
				/*
				 * BusinessMenu businessMenu = new BusinessMenu();
				 * businessMenu.setName(messageTitle);
				 * businessMenu.setDescription(messageOptionContent); Bundle
				 * bundle = new Bundle();
				 * bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO,
				 * businessMenu); intent.putExtra(ExtraKeyConstant.KEY_BUNDLE,
				 * bundle);
				 * intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY, true);
				 */
			} else {
				if (messageOptionContent != null) {
					String[] optionContents = messageOptionContent.split("~");
					if (optionContents.length >= 3) {
						/*
						 * String businessId=optionContents[0]; String
						 * applicationId=optionContents[1]; String
						 * url=optionContents[2];
						 * intent.putExtra(ExtraKeyConstant.KEY_TITLE,
						 * messageTitle);
						 * intent.putExtra(ExtraKeyConstant.KEY_FRAGMENT,
						 * WebViewFragment.class); BusinessMenu businessMenu =
						 * new BusinessMenu(); businessMenu.setServiceUrl(url);
						 * businessMenu.setName(messageTitle); Bundle bundle =
						 * new Bundle();
						 * bundle.putParcelable(ExtraKeyConstant.KEY_BUSINESS_INFO
						 * , businessMenu);
						 * intent.putExtra(ExtraKeyConstant.KEY_BUNDLE, bundle);
						 * intent.putExtra(ExtraKeyConstant.KEY_FINISH_ACTIVITY,
						 * true);
						 */
						/*
						 * intent.putExtra(App.CONTENT_EXTRAL, url);
						 * intent.putExtra(App.APP_TAG_EXTRAL, applicationId);
						 * intent.putExtra(App.BUSINESS_ID_EXTRAL,
						 * Integer.valueOf(businessId));
						 * intent.putExtra(App.IS_BUSINESS_WEB_EXTRAL,true);
						 */
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
		if (messageOptionType.equals(Message.FEEDBACK_OPTION_TYPE)) {
			mFeededBackPanel.setVisibility(View.VISIBLE);
		}

	}

	private OnClickListener mOnClickListenerForHanlleBusiness = new OnClickListener() {
		@Override
		public void onClick(View v) {
			doFeedback();
			mDialogCertain.dismiss();
		}
	};
	private OnClickListener mOnClickListenerForCancle = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mDialogCertain.dismiss();
		}
	};

	private void handleFeedBackTaskFailed() {

		if (mDialogCertain == null) {
			mDialogCertain = new TwoButtonDialog(mBaseActivity, mOnClickListenerForHanlleBusiness, mOnClickListenerForCancle);

			mDialogCertain.setMessage("返馈失败，是否重试？");
		}
		mDialogCertain.show();

	}

	private void doRead() {
		ReadThread readThread = readThreads.get(messageNotificationId);
		if (readThread == null) {
			readThread = new ReadThread();
			readThread.start();
			readThreads.put(messageNotificationId, readThread);
		} else {
			if (readThread.isFinish()) {
				readThread.setFinish(false);
				readThread = new ReadThread();
				readThread.start();
				readThreads.put(messageNotificationId, readThread);
			}
		}
	}

	private void doFeedback() {
		feedBackContent = mFeedBackEditText.getText().toString();
		if (TextUtils.isEmpty(feedBackContent.trim())) {
			Toast.makeText(mActivity, "输入反馈信息不能为空", Toast.LENGTH_LONG).show();
			return;
		} else {
			mFeedBackTask = new FeedBackTask();
			mFeedBackTask.setListener(mFeedBackTaskListener);
			mFeedBackTask.execute();
		}
	}

	class ReadThread extends Thread {
		boolean finish = false;

		@Override
		public void run() {
			try {
				App.sServerClient.readed(messageNotificationId);
				mHistoryMessageDbManager.updateReadedMessage(messageId);
				readThreads.remove(messageNotificationId);
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (BusinessException e) {
				e.printStackTrace();
			}
			finish = true;
		}

		public boolean isFinish() {
			return finish;
		}

		public void setFinish(boolean finish) {
			this.finish = finish;
		}
	}

	class FeedBackTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				App.sServerClient.feedback(messageNotificationId, feedBackContent);
				messageTime = System.currentTimeMillis();
				mHistoryMessageDbManager.updateFeedbackMessage(messageId, feedBackContent, messageTime);
				return TaskResult.OK;
			} catch (HttpException e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			} catch (BusinessException e) {
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

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		message = (Message) bundle.get("message");
		mActivity = getActivity();
		View view = inflater.inflate(R.layout.message_detail_fragment, container, false);
		mFeededBackPanel = (LinearLayout) view.findViewById(R.id.feeded_back_panel);
		mCommitPanel = (LinearLayout) view.findViewById(R.id.commit_panel);
		mMessageContentView = (TextView) view.findViewById(R.id.message_text_view);
		mMessageTitleView = (TextView) view.findViewById(R.id.message_title_view);
		mMessageTimeView = (TextView) view.findViewById(R.id.message_date_view);
		mFeedBackEditText = (EditText) view.findViewById(R.id.feedback_text);
		mFeededBackContentView = (TextView) view.findViewById(R.id.feedback_message_text_view);
		mFeededBackTimeView = (TextView) view.findViewById(R.id.feed_back_message_date_view);
		mCommitButton = (Button) view.findViewById(R.id.commit);

		int messageId = message.getId();
		String messageNotificationId = message.getNotificationId();
		String messageOptionType = message.getOptionType();
		boolean messageOperationComplete;
		if (messageOptionType.equalsIgnoreCase(Message.READED_OPTION_TYPE) || messageOptionType.equalsIgnoreCase(Message.FEEDBACK_OPTION_TYPE)) {
			messageOperationComplete = (message.getOperationComplete() == 1 ? true : false);
		} else {
			messageOperationComplete = false;
		}
		int messageReaded = message.getReaded();

		String messageTitle = message.getTitle();
		String messageContent = message.getTextContent();
		long messageTime = message.getMessageTime();

		String feedBackContent = message.getFeedbackContent();
		long feedBackTime = message.getFeedbackTime();
		String messageOptionContent = message.getOptionContent();

		// MessageDetailFragment messageDetailFragment=new
		// MessageDetailFragment();
		setMessageId(messageId);
		setMessageNotificationId(messageNotificationId);
		setMessageOptionType(messageOptionType);
		setMessageOperationComplete(messageOperationComplete);
		setMessageReaded(messageReaded);
		setMessageTitle(messageTitle);
		setMessageContent(messageContent);
		setMessageTime(messageTime);
		setFeedBackContent(feedBackContent);
		setFeedBackTime(feedBackTime);
		setMessageOptionContent(messageOptionContent);

		if (messageTitle != null)
			mMessageTitleView.setText(messageTitle);
		if (messageTime != 0) {
			SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			mMessageTimeView.setText(dateformat2.format(messageTime));
		}
		if (messageContent != null) {
			mMessageContentView.setText(messageContent);
		}
		if (feedBackTime != 0) {
			SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			mFeededBackTimeView.setText(dateformat2.format(messageTime));
		}
		if (feedBackContent != null) {
			mFeededBackContentView.setText(feedBackContent);
		}

		if (messageOptionType.equals(Message.FEEDBACK_OPTION_TYPE)) {
			if (!messageOperationComplete) {
				mFeedBackEditText.setVisibility(View.VISIBLE);
				mFeededBackPanel.setVisibility(View.GONE);
			} else {
				mCommitPanel.setVisibility(View.GONE);
				mFeedBackEditText.setVisibility(View.GONE);
				mFeededBackPanel.setVisibility(View.VISIBLE);
			}
			mCommitButton.setText("提交");
		} else if (messageOptionType.equals(Message.WEB_OPTION_TYPE)) {
			dimissFeedbackPanel();
			mCommitButton.setText("打开网页");
		} else if (messageOptionType.equals(Message.FUNCTION_OPTION_TYPE)) {
			mCommitButton.setText("处理业务");
			dimissFeedbackPanel();
		} else if (messageOptionType.equals(Message.READED_OPTION_TYPE)) {
			mCommitPanel.setVisibility(View.GONE);
			dimissFeedbackPanel();
			if (!messageOperationComplete)
				doRead();
		}

		mCommitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				visitMessage();
			}
		});
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		baseActivity.setTitle(getResources().getString(R.string.pushmessage_title));
		mHistoryMessageDbManager = ScmbhcDbManager.getInstance(mActivity.getContentResolver());
		if (messageReaded == 0)
			mHistoryMessageDbManager.readed(messageId);
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.MessageDetailFragment;
	}

}
