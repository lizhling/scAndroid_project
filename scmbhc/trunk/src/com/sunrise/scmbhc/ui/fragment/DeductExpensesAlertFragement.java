package com.sunrise.scmbhc.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.entity.BusinessMenu;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.HandleBusinessTask;
import com.sunrise.scmbhc.task.TaskListener;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.BaseActivity;
import com.sunrise.scmbhc.utils.CommUtil;

/**
 * 扣费提醒
 * 
 * @author fuheng
 * 
 */
public class DeductExpensesAlertFragement extends BaseFragment implements OnClickListener, TaskListener, App.ExtraKeyConstant {

	private GenericTask mTask;

	private BusinessMenu mBusiness;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null)
			mBusiness = savedInstanceState.getParcelable(KEY_BUSINESS_INFO);
	}

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_business_deduct_expenses_alert, container, false);

		if (mBusiness == null && getArguments() != null && getArguments().containsKey(KEY_BUSINESS_INFO))
			mBusiness = getArguments().getParcelable(KEY_BUSINESS_INFO);

		view.findViewById(R.id.clickManageCertain).setOnClickListener(this);

		TextView warmNotice = (TextView) view.findViewById(R.id.textView_warmNotice);
		warmNotice.setText(mBusiness.getWarmPrompt());

		// 业务介绍
		TextView businessIntroduce = (TextView) view.findViewById(R.id.businessIntroduce);
		businessIntroduce.setText(mBusiness.getDescription());

		// 生效时间
		TextView takeEffectTime = (TextView) view.findViewById(R.id.takeEffectTime);

		// 资费
		TextView businessTariff = (TextView) view.findViewById(R.id.businessTariff);
		businessTariff.setText(mBusiness.getCharges());
		return view;
	}

	public void onSaveInstanceState(Bundle outState) {
		if (mBusiness != null)
			outState.putParcelable(KEY_BUSINESS_INFO, mBusiness);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.setTitle(getString(R.string.deduct_expenses_alert));
	}

	public void onStop() {
		super.onStop();
		doCancelTask();
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.clickManageCertain) {
			// TODO 点击开通办理
		}
	}

	private void doCancelTask() {
		if (mTask != null && mTask.getStatus() != HandleBusinessTask.Status.FINISHED)
			mTask.cancle();
	}

	@Override
	public String getName() {
		return "业务办理";
	}

	@Override
	public void onPreExecute(GenericTask task) {
			initDialog(false,false,null);
		showDialog(getActivity().getResources().getString(R.string.onDealing));
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		dismissDialog();

		if (result != TaskResult.OK) {
			if(task.isBusinessAuthenticationTimeOut())
				mBaseActivity.showReLoginDialog();
			else 
			if (task.getException() != null && task.getException().getMessage() != null) {
				CommUtil.showAlert(getActivity(), getResources().getString(R.string.businessDealFaild), task.getException().getMessage(), null);
			}
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
	}

	@Override
	public void onCancelled(GenericTask task) {
		dismissDialog();
	}

	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.DeductExpensesAlertFragement;
	}

}
