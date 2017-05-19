package com.sunrise.scmbhc.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.entity.OpenedBusinessList;
import com.sunrise.scmbhc.task.HandleBusinessTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.LoadUserTrafficTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.view.TwoButtonDialog;
import com.sunrise.scmbhc.utils.CommUtil;

/**
 * @author fuheng
 */
public class DefaultBusinessDetailFragment1 extends BaseFragment implements OnClickListener, TaskListener {
	protected BusinessMenu mBusinessMenu;
	private GenericTask mTask;
	private TwoButtonDialog mDialogCertain;
	private String prodPrcid = null;
	private boolean isCancleMode;// 取消模式

	private final String FORMAT_OPEN_UP = "是否办理：<font color=\"%d\">%s?</font>\n资费标准：%s";
	private final String FORMAT_EXIT = "是否退订：<font color=\"%d\">%s?</font>\n资费标准：%s";

	private OnClickListener mOnClickListenerForHanlleBusiness = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mDialogCertain.dismiss();
			doBusinessHandle(mBusinessMenu);
		}
	};
	private OnClickListener mOnClickListenerForCancle = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mDialogCertain.dismiss();
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null)
			savedInstanceState = getArguments();

		if (savedInstanceState != null) {
			this.mBusinessMenu = (BusinessMenu) savedInstanceState.get(App.ExtraKeyConstant.KEY_BUSINESS_INFO);
			System.out.println(this.mBusinessMenu.toString());
			this.isCancleMode = savedInstanceState.getBoolean(App.ExtraKeyConstant.KEY_HAVE_OPENED);
		}

	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(App.ExtraKeyConstant.KEY_BUSINESS_INFO, mBusinessMenu);
		outState.putBoolean(App.ExtraKeyConstant.KEY_HAVE_OPENED, isCancleMode);
		super.onSaveInstanceState(outState);
	}

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_business_default1, container, false);
		initView(view);
		return view;
	}

	protected void initView(View view) {
		if (mBusinessMenu == null)
			return;
		{
			TextView textview_showHtml = (TextView) view.findViewById(R.id.textView_showHtml);
			String description = mBusinessMenu.getDescription();
			if (description != null)
				textview_showHtml.setText(description);
		}
		{
			TextView textView_charge = (TextView) view.findViewById(R.id.textView_charge);
			String charge = mBusinessMenu.getCharges();
			if (charge != null)
				textView_charge.setText(charge);
		}
		{
			TextView textView_warmNotice = (TextView) view.findViewById(R.id.textView_warmNotice);
			String warm = mBusinessMenu.getWarmPrompt();
			if (warm != null)
				textView_warmNotice.setText(warm);
		}

		if (isCancleMode) {// 退订模式
			TextView submit = (TextView) view.findViewById(R.id.submit);
			submit.setText(R.string.clickQuitBusiness);
		}

		view.findViewById(R.id.submit).setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onStart() {
		super.onStart();
		if (mBusinessMenu != null) {
			mBaseActivity.setLeftButtonVisibility(View.VISIBLE);
			mBaseActivity.setTitle(mBusinessMenu.getName());
		}

		if (UserInfoControler.getInstance().checkUserLoginIn()) {
			mBaseActivity.setRightButtonVisibility(View.GONE);

			OpenedBusinessList mOpenedBusinessList = UserInfoControler.getInstance().getOpenedBusinessList();
			if (mOpenedBusinessList != null)
				mOpenedBusinessList.isContain(mBusinessMenu);

		} else {
			mBaseActivity.setRightButton(getResources().getString(R.string.login), new OnClickListener() {
				@Override
				public void onClick(View v) {
					mBaseActivity.checkLoginIn(null);
				}
			});
		}
	}

	public void onStop() {
		super.onStop();
		doCancelTask();
	}

	private void doCancelTask() {
		if (mTask != null && mTask.getStatus() != GenericTask.Status.FINISHED)
			mTask.cancle();
	}

	/**
	 * 处理业务
	 * 
	 * @param item
	 */
	private void doBusinessHandle(BusinessMenu item) {
		if (item == null)
			return;

		doCancelTask();
		prodPrcid = item.getProdPrcid();
		mTask = new HandleBusinessTask();
		mTask.setListener(this);
		mTask.execute(new TaskParams(ExtraKeyConstant.KEY_BUSINESS_INFO, item));
	}

	@Override
	public void onClick(View arg0) {
		if (!mBaseActivity.checkLoginIn(null))// 检测是否登录。未登录，启动登录页面
		{
			Toast.makeText(mBaseActivity, getResources().getString(R.string.unlogin_notice), Toast.LENGTH_LONG).show();
			return;
		}

		if (mDialogCertain == null) {
			mDialogCertain = new TwoButtonDialog(mBaseActivity, mOnClickListenerForHanlleBusiness, mOnClickListenerForCancle);

			mDialogCertain.setMessage(Html.fromHtml(String.format(isCancleMode ? FORMAT_EXIT : FORMAT_OPEN_UP,
					getResources().getColor(R.color.dialog_button_text), mBusinessMenu.getName(), mBusinessMenu.getCharges())));
		}
		mDialogCertain.show();
	}

	@Override
	public String getName() {
		return "业务办理";
	}

	@Override
	public void onPreExecute(GenericTask task) {
		initDialog(false, false, null);
		showDialog(getActivity().getResources().getString(R.string.onDealing));
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();

		if (result == TaskResult.OK) {
			onRefresh();
		} else {
			if (task.isBusinessAuthenticationTimeOut())
				mBaseActivity.showReLoginDialog();
			else if (task.getException() != null && task.getException().getMessage() != null) {
				String strMsg = task.getException().getMessage();
				strMsg = strMsg.replaceAll("\\[[a-z0-9A-Z]+\\$\\$[a-z0-9A-Z]+\\]", "");
				strMsg = strMsg.replaceAll("\\[[a-z0-9A-Z]+~[a-z0-9A-Z]+\\]", "");
				strMsg = strMsg.replaceAll("~", ",");
				if (prodPrcid != null) {
					prodPrcid = prodPrcid.replaceAll("\\$", "\\\\\\$");
					strMsg = strMsg.replaceAll("\\[" + prodPrcid + "\\]", "");
					strMsg = strMsg.replaceAll("【" + prodPrcid + "】", "");
					if (prodPrcid.indexOf(',') > -1) {
						String prcidArray[] = prodPrcid.split(",");
						if (prcidArray != null && prcidArray.length > 0) {
							for (int i = 0; i < prcidArray.length; i++) {
								strMsg = strMsg.replaceAll("\\[" + prcidArray[i] + "\\]", "");
								strMsg = strMsg.replaceAll("【" + prcidArray[i] + "】", "");
							}
						}
					}

				}
				CommUtil.showAlert(getActivity(), getResources().getString(R.string.businessDealFaild), strMsg, null);
				prodPrcid = null;
			}
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// 启动刷新用户已办业务的线程
		HandleBusinessTask tTask = (HandleBusinessTask) task;
		String title = tTask.getBusinessItem().getName() + getResources().getString(isCancleMode ? R.string.businessExitSuccess : R.string.businessDealSuccess);
		String message = title;
		if (CommUtil.isContainChinese((String) param)) {
			message = (String) param;
		} else {
			title = null;
		}
		String strMsg = message;
		strMsg = strMsg.replaceAll("\\[[a-z0-9A-Z]+\\$\\$[a-z0-9A-Z]+\\]", "");
		strMsg = strMsg.replaceAll("\\[[a-z0-9A-Z]+~[a-z0-9A-Z]+\\]", "");
		strMsg = strMsg.replaceAll("~", ",");
		if (prodPrcid != null && strMsg != null) {
			prodPrcid = prodPrcid.replaceAll("\\$", "\\\\\\$");
			strMsg = strMsg.replaceAll("\\[" + prodPrcid + "\\]", "");
			strMsg = strMsg.replaceAll("【" + prodPrcid + "】", "");
			if (prodPrcid.indexOf(',') > -1) {
				String prcidArray[] = prodPrcid.split(",");
				if (prcidArray != null && prcidArray.length > 0) {
					for (int i = 0; i < prcidArray.length; i++) {
						strMsg = strMsg.replaceAll("\\[" + prcidArray[i] + "\\]", "");
						strMsg = strMsg.replaceAll("【" + prcidArray[i] + "】", "");
					}
				}
			}

		}
		CommUtil.showAlert(getActivity(), title, strMsg, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				finish(getActivity());
			}
		});
		prodPrcid = null;
	}

	/**
	 * 刷新个人信息
	 */
	private void onRefresh() {
		new LoadUserTrafficTask().execute();
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	/*
	 * 功能: 为用户行为提供页面名称
	 * 
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.DefaultBusinessDetailFragment1;
	}

}
