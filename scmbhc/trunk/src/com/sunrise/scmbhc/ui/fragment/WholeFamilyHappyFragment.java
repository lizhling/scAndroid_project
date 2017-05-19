package com.sunrise.scmbhc.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.HJH_AddOrDeleteMemberTask;
import com.sunrise.scmbhc.task.HJH_CloseTask;
import com.sunrise.scmbhc.task.HJH_Load_MemberTask;
import com.sunrise.scmbhc.task.HJH_Load_MemberTask.HJHResult;
import com.sunrise.scmbhc.task.HJH_OpenTask;
import com.sunrise.scmbhc.task.HandleBusinessTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.view.TwoButtonDialog;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

/**
 * 
 * @Project: scmbhc
 * @ClassName: WholeFamilyHappyFragment
 * @Description: 合家欢业务
 * @Author fuheng
 * @CreateFileTime: 2014年1月16日 下午3:08:38
 * @Modifier: qinhubao
 * @ModifyTime: 2014年4月16日 下午3:08:38
 * @ModifyNote:
 * @version
 * 
 */
@SuppressLint("ValidFragment")
public class WholeFamilyHappyFragment extends DefaultBusinessDetailFragment {

	private static final String KEY_MEMBER_INFO = "menber info";

	private static final String KEY_IS_CALLER = "is caller";
	private static boolean isFootShowing = false;
	private GenericTask mTask;
	private final int FAMILY_STATE_CALLER = 1;
	private final int FAMILY_STATE_MEMBER = 2;
	private final int FAMILY_STATE_CLOSE = 3;
	private final int FAMILY_STATE_UNKNOW = 4;
	private ListView mListview;
	private View mFooterView;
	private EditText mEditTextNewMember;

	/**
	 * user state
	 */
	private TextView mTextViewStateShow;

	private Button mBtJoinOrExit;
	// private EditText mMemberNum;
	// private LinearLayout memberpanel;

	private ArrayList<String> mMenberList;

	private boolean mIsCaller;

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			mMenberList = savedInstanceState.getStringArrayList(KEY_MEMBER_INFO);
			mIsCaller = savedInstanceState.getBoolean(KEY_IS_CALLER);
		}

		View view = inflater.inflate(R.layout.fragment_business_whole_family_happy_layout, container, false);
		initView(view);

		mTabWedget.getChildAt(0).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!UserInfoControler.getInstance().checkUserLoginIn()) {
					if (!mBaseActivity.checkLoginIn(null)) {
						showNextPage();
					}
					/*
					 * mTabWedget.check(1); mSwitcher.showNext();
					 */
				}
			}
		});

		// initSubBusinessList(view);
		mListview = (ListView) view.findViewById(R.id.listView_menber);
		mFooterView = getAddNewMemberFooterView(getActivity());
		mFooterView.setVisibility(View.GONE);
		mListview.addFooterView(mFooterView);
		// memberpanel = (LinearLayout) view.findViewById(R.id.member_no_panel);
		// mMemberNum = (EditText) view.findViewById(R.id.et_member_no);
		mTextViewStateShow = (TextView) view.findViewById(R.id.textView_stateShow);

		mBtJoinOrExit = (Button) view.findViewById(R.id.button_joinOrExit);
		mBtJoinOrExit.setTag(false);
		mBtJoinOrExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doOpenOrCloseHJH((Boolean) arg0.getTag());
			}

		});

		Button mBtnHandleHJH = (Button) view.findViewById(R.id.btn_handleHJH);
		mBtnHandleHJH.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// if (!UserInfoControler.getInstance().checkUserLoginIn()) {
				// CommUtil.showAlert(mBaseActivity,
				// getString(R.string.warmNotice),
				// getString(R.string.unlogin_notice),
				// new DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// mBaseActivity.checkLoginIn(null);
				// }
				// });
				// return;
				// }
				if (mBaseActivity.checkLoginIn(null)) {
					showNextPage();
				}
			}
		});

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mBaseActivity.setRightButtonVisibility(View.GONE);// 不显示登录按钮
		if (!UserInfoControler.getInstance().checkUserLoginIn()) {
			mTabWedget.check(mTabWedget.getChildAt(1).getId());
			return;
		}

		if (mMenberList == null) {
			loadMemberList();
		} else {
			initMemberList();
		}
	}

	public void onSave(Bundle outState) {
		outState.putStringArrayList(KEY_MEMBER_INFO, mMenberList);
		outState.putBoolean(KEY_IS_CALLER, mIsCaller);
		super.onSaveInstanceState(outState);
	}

	private void doCancelTask() {
		if (mTask != null && mTask.getStatus() != HandleBusinessTask.Status.FINISHED)
			mTask.cancle();
	}

	public View getAddNewMemberFooterView(Context context) {
		LinearLayout viewGroup = new LinearLayout(context);
		viewGroup.setOrientation(LinearLayout.HORIZONTAL);
		viewGroup.setGravity(Gravity.CENTER);
		mEditTextNewMember = new EditText(context);
		mEditTextNewMember.setInputType(InputType.TYPE_CLASS_PHONE);
		mEditTextNewMember.setSingleLine(true);
		mEditTextNewMember.setBackgroundResource(R.drawable.gray_stroke_gray_solid_shape_bg);
		mEditTextNewMember.setSingleLine();
		mEditTextNewMember.setPadding(7, 7, 7, 7);
		mEditTextNewMember.setHint(R.string.addNewMemberHint);
		mEditTextNewMember.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
		mEditTextNewMember.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
		layoutParams.setMargins(0, 10, 10, 10);
		viewGroup.addView(mEditTextNewMember, layoutParams);

		Button button = new Button(context);
		button.setText(R.string.addNewMember);
		button.setTextColor(getResources().getColor(R.color.white));
		button.setTextSize(15);
		button.setBackgroundResource(R.drawable.selector_normal_blue_click_red);
		button.setSingleLine();
		viewGroup.addView(button);
		button.setOnClickListener(mListener_addMember);
		button.setTag(-1);
		return viewGroup;
	}

	/**
	 * 功能： 控制不同状态的合家欢界面显示
	 * 
	 * @param int isFamily 1 表示 已开通合家欢 并是付费人 2 标识 已开通合家欢但是成员 3. 未开通合家欢 4 或其他 显示
	 *        查询失败，请稍后重试。
	 */
	private void setFamilyViewState(int isFamily) {
		switch (isFamily) {
		case FAMILY_STATE_CALLER:
			initMemberList();
			mTextViewStateShow.setText(R.string.isWholeFamilyHappyCaller);
			mBtJoinOrExit.setTag(true);
			// memberpanel.setVisibility(View.GONE);
			break;
		case FAMILY_STATE_MEMBER:
			initMemberList();
			mTextViewStateShow.setText(getString(R.string.notWholeFamilyHappyCaller));
			mBtJoinOrExit.setVisibility(View.GONE);
			mBtJoinOrExit.setTag(true);
			// memberpanel.setVisibility(View.GONE);
			break;
		case FAMILY_STATE_CLOSE:
			initMemberList();
			mTextViewStateShow.setText(getString(R.string.notJoinWholeFamilyHappy));
			mBtJoinOrExit.setText(R.string.openUp);
			mBtJoinOrExit.setVisibility(View.VISIBLE);
			mBtJoinOrExit.setBackgroundResource(R.drawable.selector_normal_blue_click_red);
			mBtJoinOrExit.setTag(false);
			// memberpanel.setVisibility(View.VISIBLE);
			break;
		default:
			mTextViewStateShow.setText(getString(R.string.wholeFamilyUnknowStatus));
			mBtJoinOrExit.setVisibility(View.GONE);
			mBtJoinOrExit.setTag(false);
			// memberpanel.setVisibility(View.GONE);
			break;
		}
	}

	private void initMemberList() {

		// 判断是否显示添加成员输入框
		refreshAddMember();

		// init the member list view
		if (mIsCaller) {
			mListview.setAdapter(new MemberAdapter(getActivity(), mMenberList, mListener_deleteMember));

			// memberpanel.setVisibility(View.GONE);
			mBtJoinOrExit.setBackgroundResource(R.drawable.selector_bg_business_manage);
			mBtJoinOrExit.setText(R.string.exitWholeFamilyHappy);
			mBtJoinOrExit.setTag(true);
		} else {
			mListview.setAdapter(new MemberAdapter(getActivity(), mMenberList, null));
			mBtJoinOrExit.setBackgroundResource(R.drawable.selector_normal_blue_click_red);
		}

	}

	private void refreshAddMember() {
		// if (!mIsCaller) {
		// if (isFootShowing) {
		// mListview.removeFooterView(mFooterView);
		// }
		// isFootShowing = false;
		//
		// return;
		// }
		// if (mMenberList.size() >= 8 && isFootShowing) {
		// mListview.removeFooterView(mFooterView);
		// isFootShowing = false;
		// } else if (mIsCaller) {
		// if (!isFootShowing) {
		// mListview.addFooterView(mFooterView);
		// isFootShowing = true;
		// }
		// }
		if (mIsCaller && mMenberList != null && mMenberList.size() < 8) {
			if (mListview.getFooterViewsCount() < 1) {
				mListview.addFooterView(mFooterView);
			}
			mFooterView.setVisibility(View.VISIBLE);
		} else {
			if (mMenberList != null) {
				mFooterView.setVisibility(View.GONE);
				if (mListview.getFooterViewsCount() > 0) {
					mListview.removeFooterView(mFooterView);
				}
			}
		}
		mEditTextNewMember.getText().clear();
	}

	class MemberAdapter extends BaseAdapter {
		private List<String> mList;
		private OnClickListener mListener;
		private Context mContext;

		public MemberAdapter(Context context, List<String> list, OnClickListener listener) {
			mContext = context;
			mList = list;
			mListener = listener;
		}

		@Override
		public int getCount() {

			int count = 0;

			if (mList != null) {
				count = mList.size();
			}

			return count;
		}

		@Override
		public Object getItem(int position) {
			if (mList != null)
				return mList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup container) {
			if (view == null)
				view = LayoutInflater.from(mContext).inflate(R.layout.item_menber_in_whole_family_happy, null);

			TextView menber = (TextView) view.findViewById(R.id.text);
			menber.setTextColor(0xff000000);
			menber.setText(mList.get(position));

			Button deletebutton = (Button) view.findViewById(R.id.button);
			deletebutton.setTag(mList.get(position));
			if (mListener != null) {
				deletebutton.setText(R.string.delete);
				deletebutton.setTag(mList.get(position));
				deletebutton.setFocusable(false);
				deletebutton.setOnClickListener(mListener);
			} else {
				deletebutton.setVisibility(View.INVISIBLE);
			}
			return view;
		}

	}

	private TaskListener mLoadMenberTaskListener = new TaskListener() {

		@Override
		public String getName() {
			return null;
		}

		@Override
		public void onPreExecute(GenericTask task) {
			initDialog(false, false, null);
			showDialog("加载合家欢成员信息！！");
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {

			dismissDialog();

			if (task.isBusinessAuthenticationTimeOut()) {
				mBaseActivity.showReLoginDialog();
				return;
			}

			if (result == TaskResult.OK && mIsCaller) {
				// 如果是号码召集人
				initMemberList();
				mTextViewStateShow.setText(R.string.isWholeFamilyHappyCaller);
				mBtJoinOrExit.setTag(true);
				// mMemberNum.setText(null);
				// memberpanel.setVisibility(View.GONE);
			} else if (result == TaskResult.OK && !mIsCaller) {// //已加入合家欢成员。
																// 不能维护成员，只能查看
				// 已加入合家欢。 不能维护成员，只能查看
				initMemberList();
				mTextViewStateShow.setText(getString(R.string.notWholeFamilyHappyCaller));
				mBtJoinOrExit.setVisibility(View.GONE);
				mBtJoinOrExit.setTag(true);
				// memberpanel.setVisibility(View.GONE);
			} else if (task.getException() != null && task.getException().getMessage() != null) {// 未加入合家欢。
																									// 显示开通处理界面
				// 未加入合家欢。 显示开通处理界面
				initMemberList();
				mTextViewStateShow.setText(getString(R.string.notJoinWholeFamilyHappy));
				mBtJoinOrExit.setText(R.string.openUp);
				mBtJoinOrExit.setVisibility(View.VISIBLE);
				mBtJoinOrExit.setBackgroundResource(R.drawable.selector_normal_blue_click_red);
				mBtJoinOrExit.setTag(false);
				// memberpanel.setVisibility(View.VISIBLE);
			} else {// other case , only show the error message, and hide the
					// handle button
				mTextViewStateShow.setText(getString(R.string.wholeFamilyUnknowStatus));
				mBtJoinOrExit.setVisibility(View.GONE);
				mBtJoinOrExit.setTag(false);
				// memberpanel.setVisibility(View.GONE);
			}
		}

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {
			HJHResult hjhResult = (HJHResult) param;
			mMenberList = hjhResult.getMenber();
			mIsCaller = hjhResult.isCaller();
		}

		@Override
		public void onCancelled(GenericTask task) {
			dismissDialog();
		}

	};

	private TaskListener mDeleteMemberTaskListener = new TaskListener() {

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {

		}

		@Override
		public void onPreExecute(GenericTask task) {
			initDialog(false, false, null);
			showDialog(getResources().getString(R.string.onDealing));
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			dismissDialog();

			if (task.isBusinessAuthenticationTimeOut()) {
				mBaseActivity.showReLoginDialog();
				return;
			}

			if (result == TaskResult.OK) {
				// 删除成功后刷新数据
				loadMemberList();
				return;
			}

			if (task.getException() != null && task.getException().getMessage() != null) {
				String exceptionMsg = task.getException().getMessage();
				CommUtil.showAlert(getActivity(), getResources().getString(R.string.wFH_faildGetMember), exceptionMsg, null);
			}
		}

		@Override
		public void onCancelled(GenericTask task) {
			dismissDialog();
		}

		@Override
		public String getName() {
			return null;
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			// mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		} else {
			showNextPage();
			loadMemberList();
		}

	}

	private OnClickListener mListener_addMember = new OnClickListener() {
		@Override
		public void onClick(View v) {

			String phoneNumber = mEditTextNewMember.getText().toString();

			if (TextUtils.isEmpty(phoneNumber) || !CommUtil.isMobilePhone(phoneNumber)) {
				CommUtil.showAlert(getActivity(), null, getString(R.string.inputCorrectPhoneNumber), null);
				mEditTextNewMember.setFocusable(true);
				return;
			}

			// mMenberList.add("111");
			// initMemberList();
			doAddMember(phoneNumber);
		}
	};
	private OnClickListener mListener_deleteMember = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// mMenberList.remove((String) arg0.getTag());
			// initMemberList();
			doDeleteMember((String) arg0.getTag());
		}
	};

	private TwoButtonDialog mDialogCertain;

	/**
	 * 开通合家欢业务
	 */
	private void doOpenedBusiness() {

		mTask = new HJH_OpenTask();
		mTask.setListener(new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {

			}

			@Override
			public void onPreExecute(GenericTask task) {
				initDialog(false, false, null);
				showDialog(getResources().getString(R.string.onDealing));
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				dismissDialog();

				if (task.isBusinessAuthenticationTimeOut()) {
					mBaseActivity.showReLoginDialog();
					return;
				}

				if (result == TaskResult.OK) {
					// 办理成功更新数据
					loadMemberList();
					return;
				}

				if (task.getException() != null && task.getException().getMessage() != null) {
					String exceptionMsg = task.getException().getMessage();
					CommUtil.showAlert(getActivity(), getResources().getString(R.string.wFH_faildOpenBusiness), exceptionMsg, null);
				}
			}

			@Override
			public void onCancelled(GenericTask task) {
				dismissDialog();
			}

			@Override
			public String getName() {
				return null;
			}
		});
		// String PhoneNumber = mMemberNum.getText().toString();
		// TaskParams params = new
		// TaskParams(ExtraKeyConstant.KEY_MEMBER_NUMBER, PhoneNumber);
		mTask.execute();
	}

	/**
	 * 关闭合家欢业务
	 */
	private void doCloseBusiness() {
		mTask = new HJH_CloseTask();
		mTask.setListener(new TaskListener() {

			@Override
			public void onProgressUpdate(GenericTask task, Object param) {

			}

			@Override
			public void onPreExecute(GenericTask task) {
				initDialog(false, false, null);
				showDialog(getResources().getString(R.string.onDealing));
			}

			@Override
			public void onPostExecute(GenericTask task, TaskResult result) {
				dismissDialog();

				if (task.isBusinessAuthenticationTimeOut()) {
					mBaseActivity.showReLoginDialog();
					return;
				}

				if (result == TaskResult.OK) {
					// 关闭合家欢成功更新数据， 界面切换到未开通状态 设置数据为空
					CommUtil.showAlert(mBaseActivity, null, "退出合家欢成功", null);
					mIsCaller = false;
					mMenberList = new ArrayList<String>();
					setFamilyViewState(FAMILY_STATE_CLOSE);
					isFootShowing = false;
					return;
				}

				if (task.getException() != null && task.getException().getMessage() != null) {
					String exceptionMsg = task.getException().getMessage();
					CommUtil.showAlert(getActivity(), getResources().getString(R.string.wFH_faildGetMember), exceptionMsg, null);
				}
			}

			@Override
			public void onCancelled(GenericTask task) {
				dismissDialog();
			}

			@Override
			public String getName() {
				return null;
			}
		});
		mTask.execute();
	}

	protected void onTabChangeed(int index) {

	}

	private void showAlert(CharSequence msg, OnClickListener onClickListener) {
		mDialogCertain = new TwoButtonDialog(mBaseActivity, onClickListener, null);
		mDialogCertain.setMessage(msg);
		mDialogCertain.show();
	}

	/**
	 * 
	 * 办理或退出合家欢
	 * 
	 * @param tag
	 */
	private void doOpenOrCloseHJH(boolean isOpened) {
		mBaseActivity.checkLoginIn(null);
		if (isOpened) {
			showAlert(Html.fromHtml(String.format("是否退出合家欢？ <i><font color=\"#ff0000\">%s</font></i>", mBusinessMenu.getCharges())), new OnClickListener() {
				@Override
				public void onClick(View v) {
					doCloseBusiness();
					mDialogCertain.dismiss();
				}
			});

		} else {
			// if (mMemberNum.getText().toString().length() != 11 ||
			// mMemberNum.getText().toString().equals(UserInfoControler.getInstance().getUserName()))
			// {
			// CommUtil.showAlert(mBaseActivity, null, "您输入的成员号码无效。", null);
			// return;
			// }
			showAlert(Html.fromHtml(String.format("是否开通合家欢？ <i><font color=\"#ff0000\">%s</font></i>", mBusinessMenu.getCharges())), new OnClickListener() {
				@Override
				public void onClick(View v) {

					doOpenedBusiness();
					mDialogCertain.dismiss();
				}
			});
		}
	}

	private void doDeleteMember(final String number) {

		showAlert(Html.fromHtml(String.format("是否将成员<i><font color=\"#ff0000\">%s</font></i>踢出合家欢？", number)), new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCloseBusiness();
				doCancelTask();
				mTask = new HJH_AddOrDeleteMemberTask();
				mTask.setListener(mDeleteMemberTaskListener);
				TaskParams params = new TaskParams(ExtraKeyConstant.KEY_IS_TRAFFIC_OVER, String.valueOf(true));
				params.put(ExtraKeyConstant.KEY_MEMBER_NUMBER, number);// arg0.getTag());
				mTask.execute(params);

				mDialogCertain.dismiss();
			}
		});
	}

	private void doAddMember(final String number) {

		showAlert(Html.fromHtml(String.format("是否添加新成员<i><font color=\"#ff0000\">%s</font></i>？", number)), new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCancelTask();
				mTask = new HJH_AddOrDeleteMemberTask();
				mTask.setListener(mDeleteMemberTaskListener);
				TaskParams params = new TaskParams(ExtraKeyConstant.KEY_IS_TRAFFIC_OVER, String.valueOf(false));
				params.put(ExtraKeyConstant.KEY_MEMBER_NUMBER, number);
				mTask.execute(params);

				mDialogCertain.dismiss();
			}
		});
	}

	/**
	 * 读取成员列表
	 */
	private void loadMemberList() {
		mTask = new HJH_Load_MemberTask();
		// if (mIsCaller) {
		// memberpanel.setVisibility(View.GONE);
		// }
		mTask.setListener(mLoadMenberTaskListener);
		mTask.execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtlis.showLogI("", "onResume()");
		if (UserInfoControler.getInstance().checkUserLoginIn()) {
			if (mTabWedget.getCheckedRadioButtonId() == 1) {
				mTabWedget.check(0);
				mSwitcher.showPrevious();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isFootShowing = false;
	}

	@Override
	public void onStop() {
		super.onStop();
		isFootShowing = false;
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.WholeFamilyHappyFragment;
	}
}
