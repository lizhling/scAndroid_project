package com.sunrise.scmbhc.ui.fragment;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.App.ExtraKeyConstant;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.task.CallTransferQueryTask;
import com.sunrise.scmbhc.task.GenericTask;
import com.sunrise.scmbhc.task.TaskParams;
import com.sunrise.scmbhc.task.TaskResult;
import com.sunrise.scmbhc.ui.activity.GetContactsActivity;
import com.sunrise.scmbhc.utils.CommUtil;
import com.sunrise.scmbhc.utils.LogUtlis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class CallTransferFragment extends DefaultBusinessDetailFragment implements OnClickListener {

	/**
	 * arraylist for strings
	 */
	public static final String KEY_MENBER_LIST = "menbers";
	private Button mSpiner;
	private RadioGroup mSpinerDropDown;
	private TextView mPhoneNum;
	private GenericTask mTask;

	public View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_business_calltransfer, container, false);

		initView(view);

		view.findViewById(R.id.unsubscribe).setOnClickListener(this);
		view.findViewById(R.id.commitAlter).setOnClickListener(this);
		view.findViewById(R.id.details).setOnClickListener(this);
		view.findViewById(R.id.button_addressList).setOnClickListener(this);

		mPhoneNum = (TextView) view.findViewById(R.id.editText_phoneNumber);

		mSpiner = (Button) view.findViewById(R.id.spinner_conditions);
		mSpiner.setText(getResources().getStringArray(R.array.CallTransferType)[0]);
		mSpiner.setOnClickListener(this);

		mSpinerDropDown = (RadioGroup) view.findViewById(R.id.spinner_dropDown);
		mSpinerDropDown.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				for (int i = 0; i < group.getChildCount(); ++i) {

					RadioButton radio = (RadioButton) group.getChildAt(i);

					if (radio.getId() == checkedId) {// 选中状态设置
						mSpiner.setText(radio.getText());
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

	@Override
	public void onResume() {
		super.onResume();
		if (mBaseActivity.checkLoginIn(null))
			return;

		mTask = new CallTransferQueryTask();
		mTask.execute();
	}

	public void onStop() {
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			switch (requestCode) {
			case GetContactsActivity.REQUEST_CONTACT:
				String str = data.getStringExtra(ExtraKeyConstant.KEY_PHONE_NUMBER);
				if (!TextUtils.isEmpty(str))
					mPhoneNum.setText(str);
				break;

			default:
				mBaseActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null);
				break;
			}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		LogUtlis.showLogD(getClass().getName(), "onclick : view.tag = " + view.getTag() + ",id = " + view.getId());
		switch (view.getId()) {
		case R.id.unsubscribe:// 退订
			break;
		case R.id.details:// 详情
			if (mBusinessMenu != null)
				CommUtil.showAlert(getActivity(), getResources().getString(R.string.details), mBusinessMenu.getDescription(), null);
			break;
		case R.id.commitAlter:// 提交修改
			break;
		case R.id.spinner_conditions:// spinner
			if (mSpinerDropDown.getVisibility() == View.VISIBLE)
				mSpinerDropDown.setVisibility(View.GONE);
			else
				mSpinerDropDown.setVisibility(View.VISIBLE);
			break;
		case R.id.button_addressList:
			startActivityForResult(new Intent(mBaseActivity, GetContactsActivity.class), GetContactsActivity.REQUEST_CONTACT);
			break;
		default:
			break;
		}
	}

	class CallTransferHandleTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			String opType = null;
			String cf_phone = null;
			String phone_no = UserInfoControler.getInstance().getUserName();
			if (params.length > 0) {
			}
			try {
				String result = App.sServerClient.handleBusinessCallTransfer(phone_no, opType, cf_phone,UserInfoControler.getInstance().getAuthorKey());
				publishProgress(result);
			} catch (Exception e) {
				e.printStackTrace();
				setException(e);
				return TaskResult.FAILED;
			}

			return TaskResult.OK;
		}
	}
	/* 
	 * 功能: 为用户行为提供页面名称
	 * @see com.sunrise.scmbhc.ui.fragment.BaseFragment#getClassNameTitle()
	 * 
	 */
	@Override
	int getClassNameTitleId() {
		return R.string.CallTransferFragment;
	}

}
