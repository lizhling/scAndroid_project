package com.sunrise.scmbhc.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.FeedBackInfo;
import com.sunrise.scmbhc.task.CommitUserFeedBackTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.ui.view.TwoButtonDialog;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

public class UserFeedbackFragment extends BaseFragment implements OnClickListener {

	private EditText mEditContackNumber;
	private EditText mEditFeedContent;
	private Button mFeedbackTypes;
	private CommitUserFeedBackTask mCommitTask;
	private final String TAG = "UserFeedbackFragment";
	private RadioGroup mSpinerDropDown;
	private TwoButtonDialog mExitAppDialog;

	private TaskListener mFeedBackListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			// mStatusView.setText((String) param);
		}

		@Override
		public void onPreExecute(GenericTask task) {

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// TODO Auto-generated method stub
			if (result == TaskResult.OK) {
				// 显示提交成功对话框
				createFeedBackResultDialog(getActivity(), "提交成功");
				LogUtlis.showLogI(TAG, "提交成功");
				// 关闭当前fragment
			} else if (result == TaskResult.CANCELLED) {
				// 提交失败对话框
				createFeedBackResultDialog(getActivity(), "提交失败，请检查输入信息");
				LogUtlis.showLogI(TAG, "提交失败");
			} else {
				// 提交失败对话框
				LogUtlis.showLogI(TAG, "提交失败");
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "FeedBack";
		}
	};

	public UserFeedbackFragment() {

	}
	public void createFeedBackResultDialog(final Context context, String msg ) {
		
		mExitAppDialog=new TwoButtonDialog(context, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mEditFeedContent != null) {
					mEditFeedContent.setText("");
					mEditFeedContent.requestFocus();
				}
				mExitAppDialog.dismiss();
			}
		}, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mExitAppDialog.dismiss();
			}
		});
		mExitAppDialog.setMessage(msg);
		mExitAppDialog.show();
		/*new AlertDialog.Builder(context)
		.setTitle("提醒")
		.setMessage(msg).setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 退出程序
				dialog.dismiss();
				
			}
		}).setNeutralButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 清空反馈的数据
				if (mEditFeedContent != null) {
					mEditFeedContent.setText("");
					mEditFeedContent.requestFocus();
				}
				dialog.dismiss();
			}
		}).create().show();		*/
		
	}
	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more_user_feedback, container, false);

		view.findViewById(R.id.submit).setOnClickListener(this);

		mEditContackNumber = (EditText) view.findViewById(R.id.editText_contack_number);
		{
			String phoneNum = UserInfoControler.getInstance().getUserName();
			if (!TextUtils.isEmpty(phoneNum))
				mEditContackNumber.setText(phoneNum);
		}

		mEditFeedContent = (EditText) view.findViewById(R.id.editText_feedback_content);

		mFeedbackTypes = (Button) view.findViewById(R.id.spinner_feedback_type);
		mFeedbackTypes.setOnClickListener(this);

		mSpinerDropDown = (RadioGroup) view.findViewById(R.id.spinner_dropDown);
		mSpinerDropDown.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				for (int i = 0; i < group.getChildCount(); ++i) {

					RadioButton radio = (RadioButton) group.getChildAt(i);

					if (radio.getId() == checkedId) {// 选中状态设置
						mFeedbackTypes.setText(radio.getText());
						radio.setBackgroundColor(0xff92ccf3);
					} else {
						radio.setBackgroundColor(Color.TRANSPARENT);
					}
				}

				mSpinerDropDown.setVisibility(View.GONE);
			}
		});

		return view;

	}

	public void onStart() {
		super.onStart();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setLeftButtonVisibility(View.VISIBLE);
		baseActivity.setTitle(getResources().getStringArray(R.array.moreFunctionOther)[3]);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.submit:
			// TODO
			FeedBackInfo mFeedBackInfo = new FeedBackInfo();
			long id = CommUtil.getCheckedPositionInRadioGroup(mSpinerDropDown);
			mFeedBackInfo.setRequestUrl("");
			if (mEditFeedContent == null) {
				Toast.makeText(getActivity(), "请填写正确的信息", Toast.LENGTH_LONG);
				return;
			}
			String inputmsg = mEditFeedContent.getText().toString().trim();
			if (inputmsg == null || inputmsg.length() == 0) {
				Toast.makeText(getActivity(), "内容不能为空", Toast.LENGTH_LONG);
				mEditFeedContent.requestFocus();
				return;
			}
			long id1 = mSpinerDropDown.getId();
			String phoneNum = mEditContackNumber.getText().toString();
			mFeedBackInfo.setContent(inputmsg);
			mFeedBackInfo.setFeekbackType(id);
			mCommitTask = new CommitUserFeedBackTask(getActivity(), mFeedBackInfo);
			mCommitTask.setListener(mFeedBackListener);
			TaskParams params = new TaskParams();
			params.put(CommitUserFeedBackTask.PHONE_NUM, phoneNum);
			
			mCommitTask.execute(params);
			break;
		case R.id.spinner_feedback_type:
			if (mSpinerDropDown.getVisibility() == View.VISIBLE)
				mSpinerDropDown.setVisibility(View.GONE);
			else
				mSpinerDropDown.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.UserFeedbackFragment;
	}
}
