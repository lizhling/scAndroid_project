package com.starcpt.cmuc.ui.activity;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.adapter.FeedbacksAdapter;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.bean.FeedbackBean;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.ui.skin.SkinManager;
import com.starcpt.cmuc.ui.skin.ViewEnum;
import com.starcpt.cmuc.ui.view.BubbleEditText;

@SuppressLint("HandlerLeak")
public class FeedBacksActivity extends ListActivity {
private CmucApplication cmucApplication;
private ListView mListView;
private Button mRefreshFeedBackButton;
private Button mCommitFeedBackButton;
private BubbleEditText mCommitTextView;
private LinearLayout mCommitFeedbackPanel;
private FeedbacksAdapter mFeedbacksAdapter;
private static FreshFeedbacksThread sFreshFeedbacksThread;

private TextView mTitleView;
private Button mBackView;
private ProgressDialog pd;
private String mFeedBack;

private View mHeaderView;

private CommitFeedbackTask mCommitFeedbackTask;
private TaskListener mCommitFeedbackListener = new TaskListener() {

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreExecute(GenericTask task) {
		pd=ProgressDialog.show(FeedBacksActivity.this, null, getText(R.string.commiting_feedback),false,false);
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		// TODO Auto-generated method stub
		pd.dismiss();
		if (result == TaskResult.OK) {	
			mCommitTextView.setText(null);
			mFeedbacksAdapter.notifyDataSetChanged();
		} else {
			Toast.makeText(FeedBacksActivity.this, getText(R.string.commit_failed), Toast.LENGTH_LONG).show();
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


private final static int REFRESH_FEEDBACK=0;
private final static int REFRESH_FEEDBACK_ERROR=1;

private Handler mHandler=new Handler(){
	public void handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case REFRESH_FEEDBACK:
			mRefreshFeedBackButton.setText(R.string.refresh);
			mCommitFeedbackPanel.setVisibility(View.VISIBLE);
			break;
		case REFRESH_FEEDBACK_ERROR:
			Toast.makeText(FeedBacksActivity.this, getText(R.string.refresh_feedbacks_failed), Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		
	};
};

private void setSkin(){
	SkinManager.setSkin(this,mHeaderView, ViewEnum.FeedBacksActivity);
}


@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	cmucApplication=(CmucApplication) getApplicationContext();
	CommonActions.setScreenOrientation(this);
	setContentView(R.layout.feedbacks);
	registerSkinChangedReceiver();
	initTitleBar();
	mCommitTextView=(BubbleEditText) findViewById(R.id.commit_feedback_text);
	
	mCommitFeedBackButton=(Button) findViewById(R.id.btn_commit);
	mCommitFeedBackButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			mFeedBack=mCommitTextView.getText().toString().trim();
			if(TextUtils.isEmpty(mFeedBack)){
				mCommitTextView.showBubbleTxtInfo(getString(R.string.feedback_isempty));
			}else{
				doCommitFeedback();
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mCommitTextView.getWindowToken(), 0);
			}
		}
	});
	
	mListView=getListView();
	mCommitFeedbackPanel=(LinearLayout) findViewById(R.id.commit_feedback_panel);
	LayoutInflater layoutInflater=LayoutInflater.from(this);
	mHeaderView=(LinearLayout) layoutInflater.inflate(R.layout.feedback_list_head, null);
	mRefreshFeedBackButton=(Button) mHeaderView.findViewById(R.id.btn_refresh_feedback);
	mRefreshFeedBackButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(sFreshFeedbacksThread!=null){
				if(sFreshFeedbacksThread.isRefreshFinish()){
					refreshFeedbacks();
					}
			}
		}
	});
	
	mListView.addHeaderView(mHeaderView, null, false);
	mFeedbacksAdapter=new FeedbacksAdapter(this, cmucApplication.getFeedbackBeans());
	mListView.setAdapter(mFeedbacksAdapter);
	
	if(sFreshFeedbacksThread==null){
		refreshFeedbacks();
	}else{
		if(!sFreshFeedbacksThread.isRefreshFinish()){
			mRefreshFeedBackButton.setText(R.string.refreshing);
			mCommitFeedbackPanel.setVisibility(View.GONE);
		}else{
			if(cmucApplication.getFeedbackBeans().size()==0){
				refreshFeedbacks();
			}
		}
	}
	CommonActions.addActivity(this);
	setSkin();
}


@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(CmucApplication.sNeedShowLock){
			CommonActions.showLockScreen(this);
		}
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
	registerReceiver(mSkinChangedReceiver, filter);
}

private void unRegisterSkinChangedReceiver(){
	unregisterReceiver(mSkinChangedReceiver);
}


private void doCommitFeedback(){
	mCommitFeedbackTask = new CommitFeedbackTask();
	mCommitFeedbackTask.setListener(mCommitFeedbackListener);
	mCommitFeedbackTask.execute();
}

private void refreshFeedbacks() {
	mRefreshFeedBackButton.setText(R.string.refreshing);
	sFreshFeedbacksThread=new FreshFeedbacksThread();
	sFreshFeedbacksThread.start();
	mCommitFeedbackPanel.setVisibility(View.GONE);
}

private void initTitleBar(){
	mTitleView=(TextView) findViewById(R.id.top_title_text);
	mTitleView.setText(R.string.my_feedback_list);
	mBackView=(Button) findViewById(R.id.top_back_btn);
	mBackView.setOnClickListener(new OnClickListener() {		
		@Override
		public void onClick(View v) {
			finish();
		}
	});
}

@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mCommitTextView.dismissBubble();
	}

@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterSkinChangedReceiver();
	}
class FreshFeedbacksThread extends Thread{
	private boolean refreshFinish=false;	
	
	public boolean isRefreshFinish() {
		return refreshFinish;
	}


	public void setRefreshFinish(boolean refreshFinish) {
		this.refreshFinish = refreshFinish;
	}


	@Override
	public void run() {
		String authentication=cmucApplication.getSettingsPreferences().getAuthentication();
		ArrayList<FeedbackBean> feedbackBeans;
		try {
			feedbackBeans = CmucApplication.sServerClient.getFeedBackList(authentication, "1", "10000");
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(mHandler!=null){
				mHandler.sendEmptyMessage(REFRESH_FEEDBACK_ERROR);
			}
			return;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(mHandler!=null){
				mHandler.sendEmptyMessage(REFRESH_FEEDBACK_ERROR);
			}
			return ;
		}
		cmucApplication.getFeedbackBeans().clear();
		cmucApplication.getFeedbackBeans().addAll(feedbackBeans);
		refreshFinish=true;
		if(mHandler!=null){
			mHandler.sendEmptyMessage(REFRESH_FEEDBACK);
		}
	}
	
} 

class CommitFeedbackTask extends GenericTask {

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		// TODO Auto-generated method stub
		String account=cmucApplication.getSettingsPreferences().getUserName();
		String phoneNumber=cmucApplication.getSettingsPreferences().getMobile();
		FeedbackBean feedbackBean;
		try {
			feedbackBean = CmucApplication.sServerClient.submitFeedback(account,phoneNumber, mFeedBack);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  TaskResult.FAILED;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  TaskResult.FAILED;
		}
		cmucApplication.getFeedbackBeans().add(0, feedbackBean);
		return TaskResult.OK;
	}

}

}
